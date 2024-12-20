package stock.research.email.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.domain.PortfolioInfo;
import stock.research.domain.SensexStockInfo;
import stock.research.entity.dto.SensexStockDetails;
import stock.research.entity.repo.SensexStockDetailsRepositary;
import stock.research.entity.repo.SensexStockInfoRepositary;
import stock.research.service.ScreenerSensexStockResearchService;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Math.abs;
import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static java.util.Collections.reverse;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.*;
import static stock.research.utility.StockResearchUtility.checkIfWeekend;
import static stock.research.utility.StockResearchUtility.getDoubleFromString;
import static stock.research.utility.StockUtility.*;

@Service
public class SensexStockResearchAlertMechanismService {

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(SensexStockResearchAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ScreenerSensexStockResearchService screenerSensexStockResearchService;

    @Autowired
    private SensexStockDetailsRepositary sensexStockDetailsRepositary;

    @Autowired
    private SensexStockInfoRepositary sensexStockInfoRepositary;

    private List<String> pfStockName = new ArrayList<>();

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone( ZoneId.systemDefault() );

    private List<SensexStockInfo> rawSensexList = new ArrayList<>();

    @Scheduled(cron = "0 35 9,12,14,17 ? * *", zone = "IST")
    public void kickOffEmailAlerts_Cron() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEmailAlerts();
            kickOffScreenerEmailAlerts();
        });
        executorService.shutdown();
    }

    public String getSensexAlertsData() {
        String data = "";
        StringBuilder alertsBuffer = new StringBuilder("");
        try {
            rawSensexList.stream()
                    .filter(x -> x.get_52WeekLowPriceDiff() != null &&
                            x.get_52WeekLowPriceDiff().doubleValue() <= 5d)
                    .distinct()
                    .forEach(sensexStockInfo ->  generateTableContents(alertsBuffer, sensexStockInfo));
            data = HTML_START;
            data += alertsBuffer.toString();
            data += HTML_END;
        }catch (Exception e){
            LOGGER.error("getSensexAlertsData::Error - ",e);
        }
        return data;
    }

    public String getSensexDailyData() {
        String data = "";
        StringBuilder dailyBuffer = new StringBuilder("");
        try {
            List<SensexStockInfo> dailySensexList = new ArrayList<>(rawSensexList);

            dailySensexList.sort(comparing(x -> {
                return abs(x.getDailyPCTChange().doubleValue());
            }, nullsLast(naturalOrder())));
            reverse(dailySensexList);

            dailySensexList.forEach(sensexStockInfo ->  generateTableContents(dailyBuffer, sensexStockInfo));
            data = HTML_START;
            data += dailyBuffer.toString();
            data += HTML_END;
        }catch (Exception e){
            LOGGER.error("getSensexDailyData::Error - ",e);
        }
        return data;
    }

    public String getYearLow() {
        String yearLowData = "";
        StringBuilder yearLowBuffer = new StringBuilder("");
        try {
            rawSensexList.stream()
                    .filter(x -> x.get_52WeekLowPrice() != null
                                && x.getCurrentMarketPrice() != null
                                    && x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice()) <= 0)
                    .distinct()
                    .forEach(sensexStockInfo ->  generateTableContents(yearLowBuffer, sensexStockInfo));
            yearLowData = HTML_START;
            yearLowData += yearLowBuffer.toString();
            yearLowData += HTML_END;
        }catch (Exception e){
            LOGGER.error("getYearLow::Error - ",e);
        }
        return yearLowData;
    }

    public void kickOffScreenerEmailAlerts() {
        Instant instantBefore = now();
        LOGGER.info( " <- Started ScreenerSensexStockResearchAlertMechanismService::kickOffScreenerEmailAlerts");
        List<SensexStockInfo> resultSensexList = new ArrayList<>();
        try{
            rawSensexList = screenerSensexStockResearchService.populateStocksAttributes();
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(rawSensexList,x);
            });
            resultSensexList.addAll(rawSensexList);
            resultSensexList.sort(comparing(x -> {
                return abs(x.get_52WeekLowPriceDiff().doubleValue());
            }, nullsLast(naturalOrder())));

        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        try {
            StringBuilder pnlBuffer = new StringBuilder("");
            resultSensexList.forEach(sensexStockInfo ->  generateTableContents(pnlBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(pnlBuffer, new StringBuilder("** Screener Sensex Daily Data ** "), false) && --retry >= 0);
        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        try {
            writeSensexPayload();
            writeSensexInfoToDB();
            writeToFile( "SCREENER_SENSEX_DAILY", stringifyJson(objectMapper, resultSensexList));
        } catch (Exception e) {
            LOGGER.error("Error - ",e);
        }

        try {
            goSleep(90);
            resultSensexList.stream().filter(x -> x.getDailyPCTChange() == null).forEach(x -> x.setDailyPCTChange(BigDecimal.ZERO));
            resultSensexList.sort(comparing(x -> {
                return abs(x.getDailyPCTChange().doubleValue());
            }, nullsLast(naturalOrder())));

            reverse(resultSensexList);

            StringBuilder dailyBuffer = new StringBuilder("");
            resultSensexList.stream().filter(x -> abs(x.getDailyPCTChange().doubleValue()) > 7.5d)
                    .forEach(sensexStockInfo ->  generateTableContents(dailyBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(dailyBuffer, new StringBuilder("** Screener Daily PnL Daily Data ** "), false) && --retry >= 0);
            writeToFile( "SCREENER_PNL_DAILY", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultSensexList.stream().filter(x -> abs(x.getDailyPCTChange().doubleValue()) > 7.5d)));

        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        LOGGER.info(instantBefore.until(now(), ChronoUnit.MINUTES)+ " <- Total time in mins, \nEnded ScreenerSensexStockResearchAlertMechanismService::kickOffScreenerEmailAlerts" );
    }

    @Scheduled(cron = "0 40 9,15 ? * *", zone = "IST")
    public void kickOffScreenerWeeklyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // runPnlLogicForSpecifiedDays(7, 20d, "SCREENER_PNL_WEEKLY" , "** Screener Sensex Weekly PNL Data ** ");
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 45 9,15 ? * *", zone = "IST")
    public void kickOffScreenerBiWeeklyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // runPnlLogicForSpecifiedDays(14, 25, "SCREENER_PNL_BI_WEEKLY" , "** Screener Sensex BI-Weekly PNL Data ** ");
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 30 9,15 ? * *", zone = "IST")
    public void kickOffScreenerMONTHLYWeeklyPnLEmailAlerts() throws Exception {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            // runPnlLogicForSpecifiedDays(31, 35d, "SCREENER_PNL_MONTHLY" , "** Screener Sensex Monthly PNL Data ** ");
        });
        executorService.shutdown();
    }

    private void runPnlLogicForSpecifiedDays(int noOfDays, double cutOffPct, String fileName, String emailSubject) {
        List<stock.research.entity.dto.SensexStockDetails> sensexStockInfoList = new CopyOnWriteArrayList<>();
        sensexStockDetailsRepositary.findAll().forEach(sensexStockInfoList::add);

        List<stock.research.entity.dto.SensexStockDetails> sensexStockInfoWeeklyList = sensexStockInfoList.stream().filter(x -> {
            long difInMS = from(now()).getTime() - x.getStockTS().getTime();
            Long diffDays = difInMS / (1000  * 60 * 60 * 24);
            if (diffDays  <= noOfDays && LocalDateTime.ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                        && LocalDateTime.ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SUNDAY){
                    return true;
            }else {
                return false;
            }
        }).collect(toList());

        sensexStockInfoWeeklyList.stream().sorted(comparing(SensexStockDetails::getStockTS).reversed());

        final List<SensexStockInfo> sensexStockList = new CopyOnWriteArrayList<>();
        final List<SensexStockInfo> stockAlertList = new CopyOnWriteArrayList<>();
        List<SensexStockInfo> sortedStockAlertList = new ArrayList<>();
        sensexStockInfoWeeklyList.stream().forEach(x ->{
            try {
                x.setSensexStocksPayload(x.getSensexStocksPayload().replaceAll("timestamp", "quoteInstant"));
            }catch (Exception e){
                LOGGER.error("Error - ",e);
            }
            try {
                sensexStockList.addAll( objectMapper.readValue(x.getSensexStocksPayload(), new TypeReference<List<SensexStockInfo>>(){}) );
            } catch (Exception e) {
                LOGGER.error("Error - ",e);
            }
        });

        List<SensexStockInfo> resultSensexStockList = new CopyOnWriteArrayList<>(sensexStockList);
        resultSensexStockList.forEach(x -> {
            x.setStockInstant(Instant.parse(x.getQuoteInstant()));
        });

        resultSensexStockList = resultSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed()).collect(toList());
        final List<SensexStockInfo> pnlForDaysData = new CopyOnWriteArrayList<>();

        if (resultSensexStockList != null && resultSensexStockList.size() > 0) {
            Map<String, List<SensexStockInfo>> sensexStockInfoWeeklyMap = resultSensexStockList.stream().filter(Objects::nonNull)
                    .filter(x -> x.getStockName() != null)
                    .collect(groupingBy(SensexStockInfo::getStockName));

            sensexStockInfoWeeklyMap.forEach((key, weeklyPnlSensexStockList) -> {
                weeklyPnlSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed());
                weeklyPnlSensexStockList = weeklyPnlSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed()).collect(toList());

                final List<SensexStockInfo> pnlForDays = new CopyOnWriteArrayList<>();
                Instant instant = now();

                Map<String, SensexStockInfo> sensexStockInfoMap = new LinkedHashMap<>();

                for (int i = 0; i < noOfDays; i++) {
                    addAndRemoveSpecifiedDates(weeklyPnlSensexStockList, instant, i, sensexStockInfoMap);
                }
                sensexStockInfoMap.forEach((k,v) -> {
                    pnlForDays.add(v);
                    pnlForDaysData.add(v);
                });

                StringBuffer changePct = new StringBuffer("");
                final BigDecimal[] pct = {BigDecimal.ZERO};
                pnlForDays.stream().filter(Objects::nonNull).forEach(x -> {
                    changePct.append(" , " + x.getDailyPCTChange() );
                    pct[0] = pct[0].add(x.getDailyPCTChange());
                });

                pnlForDays.stream().filter(Objects::nonNull).sorted(comparing(SensexStockInfo::getStockInstant).reversed());
                pnlForDaysData.stream().filter(Objects::nonNull).sorted(comparing(SensexStockInfo::getStockInstant).reversed());

                if ((abs(pct[0].doubleValue()) >= cutOffPct )){
                    if (pct[0].doubleValue() < 0d){
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#990033;display:inline;\">" + pct[0] +"</h4> )");
                    }else {
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#00e6e6;display:inline;\">" + pct[0] +"</h4> )");
                    }
                    stockAlertList.add(pnlForDays.get(0));
                }
            });

            try {
                sortedStockAlertList = stockAlertList.stream().sorted(comparing(SensexStockInfo::getStockRankIndex)).collect(toList());
                writeToFile( fileName, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockAlertList));
                writeToFile( fileName + "_ALL_STOCKS_DATA", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pnlForDaysData));
            } catch (Exception e) {
                LOGGER.error("Error - ",e);
            }
        }

        try {
            StringBuilder dataBuffer = new StringBuilder("");
            sortedStockAlertList.forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder(emailSubject), false) && --retry >= 0);
        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }
    }

    private void generateAlertEmails(List<SensexStockInfo> populatedSensexList, SIDE side) {
        try {
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::generateAlertEmails");
            List<SensexStockInfo> originalSensexList = new ArrayList<>(populatedSensexList);
            List<SensexStockInfo> populatedLargeCapSensexList = null;
            List<SensexStockInfo> populatedMidCapSensexList = null;
            List<SensexStockInfo> populatedSmallCapSensexList = null;

            populatedLargeCapSensexList = populatedSensexList.parallelStream()
                    .filter(x -> x.getStockRankIndex() <= LARGE_CAP).collect(toList());
            //Filter large Cap with Y/L diff of 75 or more
            populatedLargeCapSensexList = populatedLargeCapSensexList.stream()
                    .filter(x -> x.getStockRankIndex() <= LARGE_CAP
                            &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(toList());

            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");

            if (side == SIDE.SELL){
                generateHTMLContent(originalSensexList, side, dataBuffer, subjectBuffer);
            }else {
                generateHTMLContent(populatedLargeCapSensexList, side, dataBuffer, subjectBuffer);
            }
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer, false) && --retry >= 0);
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::generateAlertEmails");
        } catch (Exception e) {
            ERROR_LOGGER.error( "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<SensexStockInfo> populatedSensexList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (populatedSensexList != null && populatedSensexList.size() >0){
            populatedSensexList.stream().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ) {
                    if (side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Screener Sensex Buy Alert**");
                        }
                        if (!(checkPortfolioSizeAndQty(x.getStockName()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if (isStockInPortfolio(x)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Screener Sensex Sell Alert**");
                            }
                            generateTableContents(dataBuffer, x);
                        }
                    }

                }
            });
        }
    }

    private boolean isStockInPortfolio(SensexStockInfo x) {
        return pfStockName.stream().anyMatch(s -> {
            String stockName = x.getStockName();
            stockName = stockName.toLowerCase();
            stockName = stockName.replaceAll(" Ltd.", "");
            stockName = stockName.replaceAll("Ltd.", "");
            stockName = stockName.replaceAll("Ltd", "");
            String[] stockNameArr = stockName.split(" ");
            if (stockNameArr != null && stockNameArr.length <= 2){
                return (s.contains(stockName) || (
                        ((stockName.split(" ") != null && stockName.split(" ").length >= 0) &&
                                ((stockName.split(" ")[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0].toLowerCase())) ||
                        (s.toLowerCase().contains(stockName.split(" ")[0].toLowerCase())))) &&
                                ((stockName.split(" ") != null && stockName.split(" ").length >= 1)
                                        && ((stockName.split(" ")[1].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[1].toLowerCase())) ||
                                            (s.toLowerCase().contains(stockName.split(" ")[1].toLowerCase()))))
                ));
            } else if(stockNameArr != null && stockNameArr.length >= 3) {
                for (int i = 0; i < 3; i++) {
                    try {
                        if (!s.toLowerCase().contains(stockNameArr[i].toLowerCase())){
                            return false;
                        }
                        String[] strings = stockName.split(" ");
                        return
                                ((s.contains(stockName))
                                        || (
                                        ((strings != null && strings.length >= 0)
                                                && (strings[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0].toLowerCase())) ||
                                                (s.toLowerCase().contains(strings[0].toLowerCase()))) &&
                                                ((strings != null && strings.length >= 2) &&
                                                        ((strings[1].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[1].toLowerCase())) ||
                                                                (s.toLowerCase().contains(strings[1].toLowerCase())))) &&
                                                ((strings != null && strings.length >= 3) &&
                                                        ((strings[2].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[2].toLowerCase())) ||
                                                                (s.toLowerCase().contains(strings[2].toLowerCase()))))
                                ));
                    }catch (Exception e){
                        ERROR_LOGGER.error("Exception isStockInPortfolio() ->", e);
                        return false;
                    }

                }
            }
            return ((stockName.split(" ") != null && stockName.split(" ").length >= 0) &&
                    ((stockName.split(" ")[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0]))
                    || s.toLowerCase().split(" ")[0].equalsIgnoreCase(stockName.split(" ")[0].toLowerCase())));
        });
    }

    private boolean checkPortfolioSizeAndQty(String stockName) {
        boolean exists = (portfolioInfoList.stream().map(PortfolioInfo::getCompany).anyMatch(s -> ((stockName.split(" ")[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0])) || s.toLowerCase().split(" ")[0].equalsIgnoreCase(stockName.split(" ")[0].toLowerCase()))));
        if (exists){
           Optional<PortfolioInfo> portfolioInfo = (portfolioInfoList.stream().filter(s -> ((stockName.split(" ")[0].toLowerCase().equalsIgnoreCase(s.getCompany().toLowerCase().split(" ")[0])) || s.getCompany().toLowerCase().split(" ")[0].equalsIgnoreCase(stockName.split(" ")[0].toLowerCase()))).findAny());
           if (portfolioInfo != null && portfolioInfo.isPresent()){
              if (portfolioInfo.get().getValueAtMarketPrice() != null && getDoubleFromString(portfolioInfo.get().getValueAtMarketPrice()) > 45000.0){
                   return true;
               }
           }
        }
        return false;
    }

    public boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer, boolean isPortfolio) {

        return true;
/*

        try {
            goSleep(10);
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::sendEmail");
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = "";
            if (isPortfolio){
                data = SensexStockResearchUtility.HTML_PORTFOLIO_START;
            }else {
                data = HTML_START;
            }
            data += dataBuffer.toString();
            if (isPortfolio){
                data += HTML_PORTFOLIO_END;
            }else {
                data += HTML_END;
            }

            if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                    "".equalsIgnoreCase(subjectBuffer.toString()) == false){
                helper.setFrom("raghu_kat_stocks@outlook.com");
                helper.setTo(new String[]{"raghu_kat_stocks@outlook.com"});
//            helper.setTo(new String[]{"raghu_kat_stocks@outlook.com","raghu.kat@outlook.com"});
                helper.setText(data, true);
                helper.setSubject(subjectBuffer.toString());
                String fileName = subjectBuffer.toString();
                fileName = fileName.replace("*", "");
                fileName = fileName.replace(" ", "");
                fileName =  fileName + "-" + LocalDateTime.now()  ;
                fileName = fileName.replace(":","-");
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genHtml\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir")  + "\\genHtml\\" + fileName + ".html");
                helper.addAttachment(file.getFilename(), file);
//                javaMailSender.send(message);
            }
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::sendEmail");
        }catch (Exception e){
            LOGGER.error("Error - ",e);
            return false;
        }
        return true;
*/
    }

    @PostConstruct
    public void setUpPortfolioData(){
        List<PortfolioInfo> lowValuePortfolioInfoList = new ArrayList<>();

        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.reader(PortfolioInfo.class).with(schema);
            try ( InputStream inputStream  =  new ClassPathResource("SensexPortFolioEqtSummary.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }
            portfolioInfoList.stream().forEach(x -> {
               if (x.getValueAtMarketPrice() != null && getDoubleFromString(x.getValueAtMarketPrice()) <= 300000){
                   lowValuePortfolioInfoList.add(x);
               }
            });

            portfolioInfoList.removeAll(lowValuePortfolioInfoList);

            if (portfolioInfoList != null && portfolioInfoList.size() > 0){
                portfolioInfoList.parallelStream().forEach(x -> {
                    pfStockName.add(x.getCompany());
                });
            }
        } catch(Exception e) {
            ERROR_LOGGER.error( ", Error -> ", e);
            e.printStackTrace();
        }
    }

    private void writeSensexPayload() {
        if (checkIfWeekend()){
            return;
        }
        try {
            screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList().stream().forEach(x -> {
                x.setStockTS(Timestamp.from(now()));
                x.setQuoteInstant("" + now());
                x.setId(null);
            });
            SensexStockDetails sensexStockDetails = new SensexStockDetails();
            sensexStockDetails.setStockTS(Timestamp.from(now()));
            sensexStockDetails.setSensexStocksPayload(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList()));
            sensexStockDetailsRepositary.save(sensexStockDetails);
        }catch (Exception e){
            LOGGER.error("Failed to write Sensex Stock Details", e);
        }
    }

    private void writeSensexInfoToDB() {
        if (checkIfWeekend()){
            return;
        }
        screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList().forEach( sensexStockInfo -> {
            try {
                sensexStockInfo.setStockTS(Timestamp.from(now()));
                sensexStockInfo.setQuoteInstant("" + (now()));
                sensexStockInfo.setId(null);
                sensexStockInfoRepositary.save(sensexStockInfo);
            }catch (Exception e){
                LOGGER.error("Failed to write Sensex Stock Info", e);
            }
        });

    }

    private void addAndRemoveSpecifiedDates(List<SensexStockInfo> weeklyPnlSensexStockList,
                                            Instant instant, int i, Map<String, SensexStockInfo> sensexStockInfoMap) {
        for (SensexStockInfo x : weeklyPnlSensexStockList) {
            if ((Duration.between(x.getStockInstant(), instant).toDays() >= i)
                    && (Duration.between(x.getStockInstant(), instant).toDays() < (i + 1))) {
                if(LocalDateTime.ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                        && LocalDateTime.ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek()!= DayOfWeek.SUNDAY){
                    String key = dateTimeFormatter.format(x.getStockInstant());
                    if (!sensexStockInfoMap.containsKey(key)){
                        sensexStockInfoMap.put(key, x);
                    }else {
                        SensexStockInfo current = sensexStockInfoMap.get(key);
                        if (current != null && x.getStockInstant().isAfter(current.getStockInstant())){
                            sensexStockInfoMap.put(key, x);
                        }
                    }
                }
            }
        }
    }


}