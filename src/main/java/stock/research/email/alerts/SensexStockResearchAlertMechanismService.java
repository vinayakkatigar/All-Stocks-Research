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
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.domain.PortfolioInfo;
import stock.research.domain.SensexStockInfo;
import stock.research.entity.dto.SensexStockDetails;
import stock.research.entity.repo.SensexStockDetailsRepositary;
import stock.research.entity.repo.SensexStockInfoRepositary;
import stock.research.service.ScreenerSensexStockResearchService;
import stock.research.utility.SensexStockResearchUtility;
import stock.research.utility.StockUtility;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static java.util.Comparator.*;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.*;
import static stock.research.utility.StockUtility.goSleep;
import static stock.research.utility.StockUtility.writeToFile;

@Service
public class SensexStockResearchAlertMechanismService {

    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};
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

    @Scheduled(cron = "0 35 1,9,16,23 ? * *", zone = "GMT")
    public void kickOffEmailAlerts_Cron() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEmailAlerts();
            kickOffScreenerEmailAlerts();
        });
        executorService.shutdown();
    }

    private void kickOffScreenerEmailAlerts() {
        Instant instantBefore = Instant.now();
        LOGGER.info( " <- Started ScreenerSensexStockResearchAlertMechanismService::kickOffScreenerEmailAlerts");
        List<SensexStockInfo> resultSensexList = new ArrayList<>();
        try{
            List<SensexStockInfo> populatedSensexList = screenerSensexStockResearchService.populateStocksAttributes();
            Arrays.stream(StockCategory.values()).forEach(x -> {
                generateAlertEmails(populatedSensexList,x, SIDE.SELL);
                generateAlertEmails(populatedSensexList, x, SIDE.BUY);
            });
            resultSensexList.addAll(populatedSensexList);
        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        try {
            StringBuilder dataBuffer = new StringBuilder("");
            resultSensexList.forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder("** Screener Sensex Daily Data ** "), false) && --retry >= 0);
        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        try {
            LocalDateTime ldt = LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault());
            if (ldt.getHour() <= 4 && ldt.getHour() > 11){
                writeSensexPayload();
            }
            writeSensexInfoToDB();
            writeToFile( "SCREENER_SENSEX_DAILY", objectMapper.writeValueAsString(resultSensexList));
        } catch (Exception e) {
            LOGGER.error("Error - ",e);
        }

        try {
            goSleep(90);
            resultSensexList.stream().filter(x -> x.getDailyPCTChange() == null).forEach(x -> x.setDailyPCTChange(BigDecimal.ZERO));
            resultSensexList.sort(comparing(x -> {
                return Math.abs(x.getDailyPCTChange().doubleValue());
            }, nullsLast(naturalOrder())));

            Collections.reverse(resultSensexList);

            StringBuilder dataBuffer = new StringBuilder("");
            resultSensexList.stream().filter(x -> Math.abs(x.getDailyPCTChange().doubleValue()) > 7.5d)
                    .forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder("** Screener Daily PnL Daily Data ** "), false) && --retry >= 0);
            try {
                writeToFile( "SCREENER_PNL_DAILY", objectMapper.writeValueAsString(resultSensexList.stream().filter(x -> Math.abs(x.getDailyPCTChange().doubleValue()) > 7.5d)));
            }catch (Exception e){ }

        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.MINUTES)+ " <- Total time in mins , \nEnded ScreenerSensexStockResearchAlertMechanismService::kickOffScreenerEmailAlerts" );
    }


    @Scheduled(cron = "0 35 1,20 ? * *", zone = "GMT")
    public void kickOffScreenerWeeklyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {

            List<SensexStockDetails> sensexStockInfoList = new CopyOnWriteArrayList<>();
            sensexStockDetailsRepositary.findAll().forEach(sensexStockInfoList::add);

            List<SensexStockDetails> sensexStockInfoWeeklyList = sensexStockInfoList.stream().filter(x -> {
                long difInMS = from(now()).getTime() - x.getStockTS().getTime();
                Long diffDays = difInMS / (1000  * 60 * 60 * 24);
                if (diffDays  <= 7){
                    return true;
                }else {
                    return false;
                }
            }).collect(toList());

            sensexStockInfoWeeklyList.stream().sorted(comparing(SensexStockDetails::getStockTS).reversed());

            final List<SensexStockInfo> sensexStockList = new CopyOnWriteArrayList<>();
            final List<SensexStockInfo> weeklyStockAlertList = new CopyOnWriteArrayList<>();
            List<SensexStockInfo> sortedWeeklyStockAlertList = new ArrayList<>();
            sensexStockInfoWeeklyList.stream().forEach(x ->{
                try {
                    x.setSensexStocksPayload(x.getSensexStocksPayload().replaceAll("timestamp", "quoteInstant"));
                }catch (Exception e){
                    e.printStackTrace();
                }
                try {
                    sensexStockList.addAll( objectMapper.readValue(x.getSensexStocksPayload(), new TypeReference<List<SensexStockInfo>>(){}));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            List<SensexStockInfo> resultSensexStockList = new CopyOnWriteArrayList<>(sensexStockList);
            resultSensexStockList.forEach(x -> {
                x.setStockInstant(Instant.parse(x.getQuoteInstant()));
            });

            resultSensexStockList = resultSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed()).collect(toList());

            if (resultSensexStockList != null && resultSensexStockList.size() > 0) {
                Map<String, List<SensexStockInfo>> sensexStockInfoWeeklyMap = resultSensexStockList.stream().filter(Objects::nonNull)
                        .filter(x -> x.getStockName() != null).collect(groupingBy(SensexStockInfo::getStockName));

                sensexStockInfoWeeklyMap.forEach((key, weeklyPnlSensexStockList) -> {
                    weeklyPnlSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed());
                    weeklyPnlSensexStockList = weeklyPnlSensexStockList.stream().sorted(comparing(SensexStockInfo::getStockInstant).reversed()).collect(toList());

                    List<SensexStockInfo> weeklyPnl = new CopyOnWriteArrayList<>();
                    Instant instant = Instant.now();

                    for (int i = 1; i < 8; i++) {
                        weeklyPnl.add(  addAndRemoveSpecifiedDates(weeklyPnlSensexStockList, instant, i));
                    }

                    final BigDecimal[] pct = {BigDecimal.ZERO};
                    weeklyPnl.stream().filter(Objects::nonNull).forEach(x -> {
                        pct[0] = pct[0].add(x.getDailyPCTChange());
                    });

                    weeklyPnl = weeklyPnl.stream().filter(Objects::nonNull).sorted(comparing(SensexStockInfo::getStockInstant).reversed()).collect(toList());

                    if ((Math.abs(pct[0].doubleValue()) >= 20 )){
                        weeklyPnl.get(0).setDailyPCTChange(pct[0]);
                        weeklyStockAlertList.add(weeklyPnl.get(0));
                    }

                });

                try {
                    sortedWeeklyStockAlertList = weeklyStockAlertList.stream().sorted(comparing(SensexStockInfo::getStockRankIndex)).collect(toList());
                    StockUtility.writeToFile( "SCREENER_PNL_WEEKLY", objectMapper.writeValueAsString(weeklyStockAlertList));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            try {
                StringBuilder dataBuffer = new StringBuilder("");
                sortedWeeklyStockAlertList.forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
                int retry = 3;
                while (!sendEmail(dataBuffer, new StringBuilder("** Screener Sensex Weekly PNL Data ** "), false) && --retry >= 0);
            }catch (Exception e){
                LOGGER.error("Error - ",e);
            }

        });
        executorService.shutdown();
    }

    private void generateAlertEmails(List<SensexStockInfo> populatedSensexList, StockCategory stockCategory, SIDE side) {
        try {
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::generateAlertEmails");
            LOGGER.info("stockCategory = " + stockCategory + ", side = " + side);
            List<SensexStockInfo> populatedLargeCapSensexList = null;
            List<SensexStockInfo> populatedMidCapSensexList = null;
            List<SensexStockInfo> populatedSmallCapSensexList = null;

            if (stockCategory == StockCategory.LARGE_CAP){
                populatedLargeCapSensexList = populatedSensexList.parallelStream()
                                        .filter(x -> x.getStockRankIndex() <= LARGE_CAP).collect(toList());
                //Filter large Cap with Y/L diff of 75 or more
                populatedLargeCapSensexList = populatedLargeCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() <= LARGE_CAP
                                &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedLargeCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                int retry = 3;
                while (!sendEmail(dataBuffer, subjectBuffer, false) && --retry >= 0);
            } if (stockCategory == StockCategory.MID_CAP){
                populatedMidCapSensexList = populatedSensexList.parallelStream()
                        .filter(x -> x.getStockRankIndex() > 150 && x.getStockRankIndex() <= 300).collect(toList());

                //Filter Mid Cap with Y/L diff of 100 or more
                populatedMidCapSensexList = populatedMidCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() > LARGE_CAP &&  x.getStockRankIndex() <= 300
                                &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedMidCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                int retry = 3;
                while (!sendEmail(dataBuffer, subjectBuffer, false) && --retry >= 0);
            }
            if (stockCategory == StockCategory.SMALL_CAP){
                populatedSmallCapSensexList = populatedSensexList.parallelStream()
                        .filter(x -> x.getStockRankIndex() > 300).collect(toList());

                //Filter Small Cap with Y/L diff of 125 or more
                populatedSmallCapSensexList = populatedSmallCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() > 300 &&
                                x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedSmallCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                int retry = 3;
                while (!sendEmail(dataBuffer, subjectBuffer, false) && --retry >= 0);
            }
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::generateAlertEmails");
        } catch (Exception e) {
            ERROR_LOGGER.error( "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<SensexStockInfo> populatedSensexList, StockCategory stockCategory, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (populatedSensexList != null && populatedSensexList.size() >0){
            populatedSensexList.stream().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ) {
                    if (stockCategory == StockCategory.LARGE_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Large Cap Alert**");
                        }
                        if (!(checkPortfolioSizeAndQty(x.getStockName()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.MID_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(4)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Mid Cap Alert**");
                        }

                        if (!(checkPortfolioSizeAndQty(x.getStockName()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.SMALL_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(3)) <= 0)){

                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Small Cap Alert**");
                        }
                        if (!(checkPortfolioSizeAndQty(x.getStockName()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.LARGE_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if (isStockInPortfolio(x)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Large Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getStockName()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                    if (stockCategory == StockCategory.MID_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(4)) <= 0)){
                        if (isStockInPortfolio(x)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Mid Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getStockName()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                    if (stockCategory == StockCategory.SMALL_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(3)) <= 0)){
                        if (isStockInPortfolio(x)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Small Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getStockName()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                }
            });
        }
    }

    private boolean isStockInPortfolio(SensexStockInfo x) {
        return pfStockName.stream().anyMatch(s -> ((x.getStockName().split(" ")[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0]))
                || s.toLowerCase().split(" ")[0].equalsIgnoreCase(x.getStockName().split(" ")[0].toLowerCase())));
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
        try {
            goSleep(90);
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::sendEmail");
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = "";
            if (isPortfolio){
                data = SensexStockResearchUtility.HTML_PORTFOLIO_START;
            }else {
                data = SensexStockResearchUtility.HTML_START;
            }
            data += dataBuffer.toString();
            if (isPortfolio){
                data += HTML_PORTFOLIO_END;
            }else {
                data += SensexStockResearchUtility.HTML_END;
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
                javaMailSender.send(message);
            }
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::sendEmail");
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @PostConstruct
    public void setUpPortfolioData(){
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.reader(PortfolioInfo.class).with(schema);
            try ( InputStream inputStream  =  new ClassPathResource("SensexPortFolioEqtSummary.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }
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
        try {
            screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList().stream().forEach(x -> {
                x.setStockTS(Timestamp.from(Instant.now()));
                x.setQuoteInstant("" + Instant.now());
                x.setId(null);
            });
            SensexStockDetails sensexStockDetails = new SensexStockDetails();
            sensexStockDetails.setStockTS(Timestamp.from(Instant.now()));
            sensexStockDetails.setSensexStocksPayload(objectMapper.writeValueAsString(screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList()));
            sensexStockDetailsRepositary.save(sensexStockDetails);
        }catch (Exception e){
            LOGGER.error("Failed to write Sensex Stock Details", e);
        }
    }


    private void writeSensexInfoToDB() {
        screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList().forEach( sensexStockInfo -> {
            try {
                sensexStockInfo.setStockTS(Timestamp.from(Instant.now()));
                sensexStockInfo.setQuoteInstant("" + (Instant.now()));
                sensexStockInfo.setId(null);
                sensexStockInfoRepositary.save(sensexStockInfo);
            }catch (Exception e){
                LOGGER.error("Failed to write Sensex Stock Info", e);
            }
        });

    }

    private void writeSensexInfoListToDB(List<SensexStockInfo> sensexStockInfoList) {
        sensexStockInfoList.forEach(sensexStockInfo -> {
            try {
                sensexStockInfo.setId(null);
                sensexStockInfo.setStockTS(Timestamp.from(Instant.now()));
                sensexStockInfoRepositary.save(sensexStockInfo);
            } catch (Exception e) {
                LOGGER.error("Failed to write Sensex Stock Info", e);
            }
        });
    }


    private SensexStockInfo addAndRemoveSpecifiedDates(List<SensexStockInfo> weeklyPnlSensexStockList,  Instant instant, int i) {

        Map<String, List<SensexStockInfo>> stockDates = new LinkedHashMap<>();
        List<SensexStockInfo> dates = new CopyOnWriteArrayList<>();
        List<SensexStockInfo> dups = new CopyOnWriteArrayList<>();
        aa:
        for (SensexStockInfo x : weeklyPnlSensexStockList) {
//            System.out.println("checking for ->" +x.getStockInstant());
            if ((Duration.between(x.getStockInstant(), instant).toDays() == i)) {
                if (dates.size() ==0){
//                    System.out.println("ADDED for ->" +x.getStockInstant());
                    dates.add(x);
                    dups.add(x);
                    return x;
                }else {
                    dups.add(x);
                }
            }
        }

        weeklyPnlSensexStockList.removeAll(dups);
        if (dates.size() > 1){
            weeklyPnlSensexStockList.add(dates.get(1));
        }
        if (dates.size() > 0)
            return dates.get(0);

        return null;
    }

}
