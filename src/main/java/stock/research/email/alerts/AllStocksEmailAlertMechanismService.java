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
import org.springframework.stereotype.Service;
import stock.research.domain.PortfolioInfo;
import stock.research.domain.StockInfo;
import stock.research.service.AllStockResearchService;
import stock.research.service.NyseTop1000StockResearchService;
import stock.research.utility.NyseStockResearchUtility;
import stock.research.utility.StockResearchUtility;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
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

    @Autowired
    private NyseTop1000StockResearchService nyseTop1000StockResearchService;

    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();

    //@Scheduled(cron = "0 0 02 ? * *")
    public void kickOffSingaporeEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSingaporeEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Singapore", SNGR_URL, SNGR_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Singapore", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Singapore", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSingaporeEmailAlerts" );
    }

//    //@Scheduled(cron = "0 0 01 ? * *")
    public void kickOffIndiaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffIndiaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("India", INDIA_URL, INDIA_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("India", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("India", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffIndiaEmailAlerts" );
    }

    //@Scheduled(cron = "0 45 01 ? * *")
    public void kickOffHongKongEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffHongKongEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("HongKong", HK_URL, HK_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("HongKong", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("HongKong", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffHongKongEmailAlerts" );
    }

    //@Scheduled(cron = "0 15 01 ? * *")
    public void kickOffSouthKoreaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSouthKoreaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("SouthKorea", SWTHKRW_URL, SWTHKRW_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("SouthKorea", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("SouthKorea", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSouthKoreaEmailAlerts" );
    }

    //@Scheduled(cron = "0 15 0 ? * *")
    public void kickOffAustraliaEmailAlerts() {
        try{
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffAustraliaEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Australia", AUSTRALIA_URL, AUSTRALIA_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Australia", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Australia", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffAustraliaEmailAlerts" );
        }catch (Exception e){ }

    }


    //@Scheduled(cron = "0 10 0 ? * *")
    public void kickOffJapanEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffJapanEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Japan", JAPAN_URL, JAPAN_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Japan", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Japan", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffJapanEmailAlerts" );
    }


    //@Scheduled(cron = "0 20 2 ? * *")
    public void kickOffSpainEmailAlerts() {

        try{
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSpainEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Spain",SPAIN_URL,SPAIN_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Spain",stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Spain",stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSpainEmailAlerts" );

        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 40 2 ? * *")
    public void kickOffItalyEmailAlerts() {

        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffItalyEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Italy",ITALY_URL,ITALY_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Italy",stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Italy",stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffItalyEmailAlerts" );

        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 0 3 ? * *")
    public void kickOffSwissEmailAlerts() {

        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSwissEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Swiss",SWZLD_200_URL,SWZLD_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Swiss",stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Swiss",stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSwissEmailAlerts" );

        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 15 3 ? * *")
    public void kickOffAustriaEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffAustriaEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Austria", AUSTRIA_URL, AUSTRIA_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Austria", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Austria", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffAustriaEmailAlerts" );
    }

    //@Scheduled(cron = "0 30 3 ? * *")
    public void kickOffEUROEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffEUROEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Euro", EURO_URL, EURO_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Euro", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Euro", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffEUROEmailAlerts" );
    }

    //@Scheduled(cron = "0 45 3 ? * *")
    public void kickOffDenmarkEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffDenmarkEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Denmark", DENMARK_URL, DENMARK_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Denmark", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Denmark", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffDenmarkEmailAlerts" );
    }

    //@Scheduled(cron = "0 0 4 ? * *")
    public void kickOffFinlandEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffFinlandEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Finland", FINLAND_URL, FINLAND_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Finland", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Finland", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffFinlandEmailAlerts" );
    }

    //@Scheduled(cron = "0 15 4 ? * *")
    public void kickOffGermanyEmailAlerts() {
        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffGermanyEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Germany", GERMANY_URL, GERMANY_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Germany", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Germany", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffGermanyEmailAlerts" );
        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 30 4 ? * *")
    public void kickOffNetherlandsEmailAlerts() {
        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffNetherlandsEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Netherlands", NETHERLANDS_URL, NETHERLANDS_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Netherlands", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Netherlands", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffNetherlandsEmailAlerts" );
        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 45 4 ? * *")
    public void kickOffNorwayEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffNorwayEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Norway", NORWAY_URL, NORWAY_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Norway", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Norway", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffNorwayEmailAlerts" );
    }

    //@Scheduled(cron = "0 05 5 ? * *")
    public void kickOffSwedenEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffSwedenEmailAlerts" );
        final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Sweden", SWEDEN_URL, SWEDEN_CNT);
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails("Sweden", stockInfoList,x, LARGE_CAP);
            generateAlertEmails("Sweden", stockInfoList,x, MID_CAP);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffSwedenEmailAlerts" );
    }

    //@Scheduled(cron = "0 15 5 ? * *")
    public void kickOffFranceEmailAlerts() {
        try{
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffFranceEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("France", FRANCE_URL, FRANCE_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("France", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("France", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffFranceEmailAlerts" );
        }catch (Exception e){

        }

    }


    //@Scheduled(cron = "0 35 5 ? * *")
    public void kickOffBelgiumEmailAlerts() {
        try{
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffBelgiumEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Belgium", BELGIUM_URL, BELGIUM_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Belgium", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Belgium", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffBelgiumEmailAlerts" );
        }catch (Exception e){

        }

    }


    //@Scheduled(cron = "0 0 6 ? * *")
    public void kickOffNyseTop1000() {
        try {
            LOGGER.info(Instant.now()+ " <-  Started NYSE::kickOffNyseTop1000" );
            final List<StockInfo> nyseStockInfoList = nyseTop1000StockResearchService.populateStockDetailedInfo("NYSE_1000", NyseStockResearchUtility.NYSE_1000_URL, NyseStockResearchUtility.NYSE_1000_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("NYSE_1000", nyseStockInfoList,x, StockCategory.LARGE_CAP);
            });

            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("NYSE_1000", nyseStockInfoList,x, StockCategory.MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended NYSE::kickOffNyseTop1000::kickOffEmailAlerts" );
        }catch (Exception e){

        }

    }


    //@Scheduled(cron = "0 45 5 ? * *")
    public void kickOffWorld1000EmailAlerts() {
        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffWorld1000EmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("World1000", WORLD_1000_URL, WORLD_1000_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("World1000", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("World1000", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffWorld1000EmailAlerts" );
        }catch (Exception e){

        }

    }

    //@Scheduled(cron = "0 0 7 ? * *")
    public void kickOffCanadaEmailAlerts() {
        try {
            LOGGER.info(Instant.now()+ " <-  Started  AllStocksEmailAlertMechanismService::kickOffCanadaEmailAlerts" );
            final List<StockInfo> stockInfoList = allStockResearchService.populateStockDetailedInfo("Canada", CANADA_URL, CANADA_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails("Canada", stockInfoList,x, LARGE_CAP);
                generateAlertEmails("Canada", stockInfoList,x, MID_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  AllStocksEmailAlertMechanismService::kickOffCanadaEmailAlerts" );
        }catch (Exception e){

        }

    }

    private void generateAlertEmails(String component, List<StockInfo> stockInfoList, SIDE side, StockCategory stockCategory) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");

            generateHTMLContent(component, stockInfoList, side, dataBuffer, subjectBuffer, stockCategory);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
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

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try{
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = StockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += StockResearchUtility.HTML_END;

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
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\genHtml\\" + fileName + ".html");
                helper.addAttachment(file.getFilename(), file);
                javaMailSender.send(message);
            }
            }catch (Exception e){
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
