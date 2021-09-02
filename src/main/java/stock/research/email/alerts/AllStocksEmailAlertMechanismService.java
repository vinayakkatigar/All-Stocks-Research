package stock.research.email.alerts;

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
import stock.research.domain.StockInfo;
import stock.research.service.AllStockResearchService;
import stock.research.utility.StockResearchUtility;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static stock.research.email.alerts.AllStocksEmailAlertMechanismService.StockCategory.LARGE_CAP;
import static stock.research.email.alerts.AllStocksEmailAlertMechanismService.StockCategory.MID_CAP;
import static stock.research.utility.StockResearchUtility.*;

@Service
public class AllStocksEmailAlertMechanismService {
    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(AllStocksEmailAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AllStockResearchService allStockResearchService;

    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();

    @Scheduled(cron = "0 45 23 ? * MON-FRI")
    public void kickOffEmailAlerts_4() {
        kickOffSwissEmailAlerts();
    }

    @Scheduled(cron = "0 0 8 ? * MON-FRI")
    public void kickOffSwissEmailAlerts() {

        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSwissEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Swiss",SWZLD_200_URL,SWZLD_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Swiss",stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Swiss",stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSwissEmailAlerts" );

    }

    @Scheduled(cron = "0 15 8 ? * MON-FRI")
    public void kickOffAustriaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffAustriaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Austria", AUSTRIA_URL, AUSTRIA_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Austria", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Austria", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffAustriaEmailAlerts" );
    }

    @Scheduled(cron = "0 0 2 ? * MON-FRI")
    public void kickOffAustraliaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffAustraliaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Australia", AUSTRALIA_URL, AUSTRALIA_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Australia", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Australia", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffAustraliaEmailAlerts" );
    }

    @Scheduled(cron = "0 0 10 ? * MON-FRI")
    public void kickOffEUROEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffEUROEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Euro", EURO_URL, EURO_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Euro", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Euro", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffEUROEmailAlerts" );
    }

    @Scheduled(cron = "0 30 19 ? * MON-FRI")
    public void kickOffCanadaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffCanadaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Canada", CANADA_URL, CANADA_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Canada", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Canada", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffCanadaEmailAlerts" );
    }

    @Scheduled(cron = "0 0 11 ? * MON-FRI")
    public void kickOffDenmarkEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffDenmarkEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Denmark", DENMARK_URL, DENMARK_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Denmark", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Denmark", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffDenmarkEmailAlerts" );
    }

    @Scheduled(cron = "0 15 11 ? * MON-FRI")
    public void kickOffFinlandEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffFinlandEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Finland", FINLAND_URL, FINLAND_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Finland", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Finland", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffFinlandEmailAlerts" );
    }

    @Scheduled(cron = "0 30 11 ? * MON-FRI")
    public void kickOffGermanyEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffGermanyEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Germany", GERMANY_URL, GERMANY_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Germany", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Germany", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffGermanyEmailAlerts" );
    }

    @Scheduled(cron = "0 15 12 ? * MON-FRI")
    public void kickOffNetherlandsEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffNetherlandsEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Netherlands", NETHERLANDS_URL, NETHERLANDS_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Netherlands", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Netherlands", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffNetherlandsEmailAlerts" );
    }

    @Scheduled(cron = "0 30 12 ? * MON-FRI")
    public void kickOffNorwayEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffNorwayEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Norway", NORWAY_URL, NORWAY_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Norway", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Norway", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffNorwayEmailAlerts" );
    }

    @Scheduled(cron = "0 0 18 ? * MON-FRI")
    public void kickOffWorld1000EmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffWorld1000EmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("World1000", WORLD_1000_URL, WORLD_1000_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("World1000", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("World1000", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffWorld1000EmailAlerts" );
    }

    @Scheduled(cron = "0 45 12 ? * MON-FRI")
    public void kickOffSwedenEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSwedenEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Sweden", SWEDEN_URL, SWEDEN_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Sweden", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Sweden", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSwedenEmailAlerts" );
    }

    private void generateAlertEmails(String component, List<StockInfo> populatedFtseList, SIDE side, StockCategory stockCategory) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");

            generateHTMLContent(component, populatedFtseList, side, dataBuffer, subjectBuffer, stockCategory);
            sendEmail(dataBuffer, subjectBuffer);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(String component, List<StockInfo> populatedFtseList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer, StockCategory stockCategory) {
        if (populatedFtseList != null && populatedFtseList.size() >0){
            populatedFtseList = populatedFtseList.stream().distinct().collect(Collectors.toList());
            populatedFtseList.stream().distinct().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() <=250 && stockCategory == LARGE_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(3.5)) <= 0)){
                        if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** " + component +" Buy Large Cap Alert**");
                            }
                            StockResearchUtility.createTableContents(dataBuffer, x);
                        }
                    }

                    if (x.getStockRankIndex() > 250 && stockCategory == StockCategory.MID_CAP && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(2.0)) <= 0)){
                        if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** " + component +" Buy Mid Cap Alert**");
                            }
                            StockResearchUtility.createTableContents(dataBuffer, x);
                        }
                    }

                    if (x.getStockRankIndex() <=250 && stockCategory == LARGE_CAP
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(2)) <= 0)){
                            if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                                if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                    subjectBuffer.append("** " + component +" Sell Large Cap Alert**");
                                }
                                StockResearchUtility.createTableContents(dataBuffer, x);
                            }
                    }
                    if (x.getStockRankIndex() > 250 && stockCategory == StockCategory.MID_CAP
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(3.5)) <= 0))){
                        if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** " + component +" Sell Mid Cap Alert**");
                            }
                            StockResearchUtility.createTableContents(dataBuffer, x);
                        }
                    }
                }
            });
        }
    }

    private boolean checkPortfolioSizeAndQtyExists(String code) {
        return portfolioInfoList.stream().filter(x -> x.getSymbol().contains(code))
                .anyMatch(y -> ((y.getMarketValue() != null && StockResearchUtility.getDoubleFromString(y.getMarketValue().replace("£", "").replace(",", "")) > 500.0)));
    }

    private void sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String data = StockResearchUtility.HTML_START;
        data += dataBuffer.toString();
        data += StockResearchUtility.HTML_END;

        if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                "".equalsIgnoreCase(subjectBuffer.toString()) == false){
            helper.setFrom("stockalert@stockalert.com");
            helper.setTo(new String[]{"raghukati1950@gmail.com"});
//            helper.setTo(new String[]{"raghukati1950@gmail.com","raghu.kat@outlook.com"});
            helper.setText(data, true);
            helper.setSubject(subjectBuffer.toString());
            String fileName = subjectBuffer.toString();
            fileName = fileName.replace("*", "");
            fileName = fileName.replace(" ", "");
            fileName =  fileName + "-" + LocalDateTime.now()  ;
            fileName = fileName.replace(":","-");
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\" + fileName  + ".html"), data.getBytes());
            FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\logs\\" + fileName + ".html");
            helper.addAttachment(file.getFilename(), file);
            javaMailSender.send(message);
        }
    }

    @PostConstruct
    public void setUpPortfolioData(){
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.reader(PortfolioInfo.class).with(schema);
            try ( InputStream inputStream  =  new ClassPathResource("portfolio.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }

        } catch(Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }
    }

}
