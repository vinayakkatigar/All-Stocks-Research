package stock.research.email.alerts;

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
import stock.research.domain.StockInfo;
import stock.research.service.ScreenerSensexStockResearchService;
import stock.research.service.SensexStockResearchService;
import stock.research.utility.SensexStockResearchUtility;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.*;

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
    private SensexStockResearchService sensexStockResearchService;

    @Autowired
    private ScreenerSensexStockResearchService screenerSensexStockResearchService;

    private List<String> pfStockName = new ArrayList<>();

    @Scheduled(cron = "0 35 0 ? * MON-SAT")
    public void kickOffNightlyEmailAlerts_Cron() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEmailAlerts();
            kickOffScreenerEmailAlerts();
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 35 6,11 ? * MON-FRI")
    public void kickOffEmailAlerts_Cron() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEmailAlerts();
            kickOffScreenerEmailAlerts();
        });
        executorService.shutdown();
    }

    public void kickOffScreenerEmailAlerts() {

        Instant instantBefore = Instant.now();
        LOGGER.info(Instant.now() + " <- Started ScreenerSensexStockResearchAlertMechanismService::kickOffEmailAlerts");
        try{
            List<SensexStockInfo> populatedSensexList = screenerSensexStockResearchService.populateStocksAttributes();
            Arrays.stream(StockCategory.values()).forEach(x -> {
                generateAlertEmails(populatedSensexList,x, SIDE.SELL);
                generateAlertEmails(populatedSensexList, x, SIDE.BUY);
            });

        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

        try {
            StringBuilder dataBuffer = new StringBuilder("");
            screenerSensexStockResearchService.getCacheScreenerSensexStockInfosList().forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder("** Screener Sensex Daily Data ** "), false) && --retry >= 0);
        }catch (Exception e){ }

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.MINUTES)+ " <- Total time in mins , Ended ScreenerSensexStockResearchAlertMechanismService::kickOffEmailAlerts" + Instant.now());
    }

    public void kickOffEmailAlerts() {

            Instant instantBefore = Instant.now();
            LOGGER.info(Instant.now()+ " <- Started SensexStockResearchAlertMechanismService::kickOffEmailAlerts");
//                List<SensexStockInfo> populatedSensexList = objectMapper.readValue(new ClassPathResource("SensexStock-1000-MktCap-detailedInfo.json").getInputStream(), new TypeReference<List<SensexStockInfo>>(){});
        try{
                List<SensexStockInfo> populatedSensexList = get500StocksAttributes();
                Arrays.stream(StockCategory.values()).forEach(x -> {
                    generateAlertEmails(populatedSensexList,x, SIDE.SELL);
                    generateAlertEmails(populatedSensexList, x, SIDE.BUY);
                });
                Map<PortfolioInfo, SensexStockInfo> portfolioInfoMap = new LinkedHashMap<>();
                try {
                    StringBuilder dataBuffer = new StringBuilder("");
                    sensexStockResearchService.getPortfolioInfoList().forEach(portfolioInfo -> {
                        try {
                            Optional<SensexStockInfo>sensexStockInfo = populatedSensexList.stream()
                                    .filter(x -> (portfolioInfo.getiSIN() != null && x.getIsin() != null) && (x.getIsin().equalsIgnoreCase(portfolioInfo.getiSIN()))).findAny();
                            if (sensexStockInfo != null && sensexStockInfo.isPresent()){
                                portfolioInfoMap.put(portfolioInfo, sensexStockInfo.get());
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    });

                    LOGGER.info("SensexStockResearchAlertMechanismService.portfolioInfoMap");
                    LOGGER.info(portfolioInfoMap.toString());
                    if (portfolioInfoMap != null && portfolioInfoMap.size() > 0){
                        portfolioInfoMap.forEach((k,v) -> generateTableContents(dataBuffer, k ,v));
                    }

                    int retry = 3;
//                    while (!sendEmail(dataBuffer, new StringBuilder("** Portfolio Sensex Daily Data ** "), true) && --retry >= 0);
                }catch (Exception e){
                    LOGGER.error("Error - ",e);
                }

            }catch (Exception e){
            LOGGER.error("Error - ",e);
        }

            try {
                StringBuilder dataBuffer = new StringBuilder("");
                sensexStockResearchService.getCacheSensexStockInfosList().forEach(sensexStockInfo ->  generateTableContents(dataBuffer, sensexStockInfo));
                int retry = 3;
                while (!sendEmail(dataBuffer, new StringBuilder("** Sensex Daily Data ** "), false) && --retry >= 0);
            }catch (Exception e){ }

            LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.MINUTES)+ " <- Total time in mins , Ended SensexStockResearchAlertMechanismService::kickOffEmailAlerts" + Instant.now());
    }

    private List<SensexStockInfo>  get500StocksAttributes() {
        List<SensexStockInfo> populatedSensexList = new ArrayList<>();
        try {
            LOGGER.info("SensexStockResearchAlertMechanismService::get500StocksAttributes");
            populatedSensexList = sensexStockResearchService.populateStocksAttributes();
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
        return populatedSensexList;
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
                        .filter(x -> x.getStockRankIndex() <= LARGE_CAP &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(toList());

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
                        .filter(x -> x.getStockRankIndex() > LARGE_CAP &&  x.getStockRankIndex() <= 300 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0).collect(toList());

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
                        .filter(x -> x.getStockRankIndex() > 300 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(125)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedSmallCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                int retry = 3;
                while (!sendEmail(dataBuffer, subjectBuffer, false) && --retry >= 0);
            }
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::generateAlertEmails");
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
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
        return portfolioInfoList.stream().map(PortfolioInfo::getiSIN).anyMatch(y -> y.equalsIgnoreCase(x.getIsin()))
                && pfStockName.stream().anyMatch(s -> ((x.getStockName().split(" ")[0].toLowerCase().equalsIgnoreCase(s.toLowerCase().split(" ")[0]))
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
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir")  + "\\genFiles\\" + fileName + ".html");
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
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }
    }
}
