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
import stock.research.domain.FtseStockInfo;
import stock.research.domain.PortfolioInfo;
import stock.research.service.FTSEStockResearchService;

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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static stock.research.utility.FtseStockResearchUtility.*;

@Service
public class FtseEmailAlertMechanismService {
    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(FtseEmailAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FTSEStockResearchService stockResearchService;

    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();


    @Scheduled(cron = "0 0 17 ? * MON-FRI")
    public void kickOffFTSE250YearlyGainerLoserEmailAlerts() {


            LOGGER.info(Instant.now()+ " <-  Started FTSE100 FtseEmailAlertMechanismService::kickOffFTSE250YearlyGainerLoserEmailAlerts" );
            final List<FtseStockInfo> populatedftse250List = stockResearchService.populateYearlyGainersLosersFtseStockDetailedInfo("https://www.hl.co.uk/shares/stock-market-summary/ftse-250/performance?column=date_1y&order=asc", 20);

            LOGGER.info(Instant.now()+ " <-  Ended FTSE250 FtseEmailAlertMechanismService::kickOffFTSE100YearlyGainerLoserEmailAlerts" );

            /*
            StringBuilder dataBuffer = new StringBuilder("");

            final StringBuilder subjectBuffer = new StringBuilder("");
            subjectBuffer.append("** FTSE 250 Yearly Gainer Buy Mid Cap Alert**");
            populatedftse250List.stream().forEach(x -> dataBuffer.append("<tr><td>" + x.getStockName() + "</td><td>" + x.getCurrentMarketPrice() + "</td></tr>" ));
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
             */

    }


    @Scheduled(cron = "0 0 16 ? * MON-FRI")
    public void kickOffFTSE100YearlyGainerLoserEmailAlerts() {

        LOGGER.info(Instant.now()+ " <-  Started FTSE100 FtseEmailAlertMechanismService::kickOffFTSE100YearlyGainerLoserEmailAlerts" );
        final List<FtseStockInfo> populatedftse100List = stockResearchService.populateYearlyGainersLosersFtseStockDetailedInfo("https://www.hl.co.uk/shares/stock-market-summary/ftse-100/performance?column=date_1y&order=asc", 20);

        LOGGER.info(Instant.now()+ " <-  Ended FTSE250 FtseEmailAlertMechanismService::kickOffFTSE100YearlyGainerLoserEmailAlerts" );

        /*
        StringBuilder dataBuffer = new StringBuilder("");
        final StringBuilder subjectBuffer = new StringBuilder("");
        subjectBuffer.append("** FTSE 100 Yearly Gainer Buy Large Cap Alert**");
        populatedftse100List.stream().forEach(x -> dataBuffer.append("<tr><td>" + x.getStockName() + "</td><td>" + x.getCurrentMarketPrice() + "</td></tr>" ));
        int retry = 3;
        while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
         */
    }


    @Scheduled(cron = "0 15 10,16 ? * MON-FRI")
    public void kickOffFTSEEmailAlerts() {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            LOGGER.info(Instant.now()+ " <-  Started FTSE100 FtseEmailAlertMechanismService::kickOffEmailAlerts" );
            final List<FtseStockInfo> populatedftse100List = stockResearchService.
                    populateFtseStockDetailedInfo(FTSE_100_URL, FTSE_100_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(populatedftse100List,x, StockCategory.LARGE_CAP);
            });
            LOGGER.info(Instant.now()+ " <-  Ended FTSE100 FtseEmailAlertMechanismService::kickOffEmailAlerts" );

            LOGGER.info(Instant.now()+ " <- Started FTSE250 FtseEmailAlertMechanismService::kickOffEmailAlerts");
            final List<FtseStockInfo> populatedftse250List = stockResearchService.
                    populateFtseStockDetailedInfo(FTSE_250_URL, FTSE_250_CNT);
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(populatedftse250List,x, StockCategory.MID_CAP);
            });


            try {
                final StringBuilder dataBuffer = new StringBuilder("");
                stockResearchService.getLargeCapCacheftseStockDetailedInfoList().forEach(x ->  createTableContents(dataBuffer, x));
                int retry = 3;
                while (!sendEmail(dataBuffer, new StringBuilder("** FTSE LARGE CAP Daily Data ** ")) && --retry >= 0);
                final StringBuilder dataMidCapBuffer = new StringBuilder("");
                stockResearchService.getMidCapCapCacheftseStockDetailedInfoList().forEach(x ->  createTableContents(dataMidCapBuffer, x));
                retry = 3;
                while (!sendEmail(dataMidCapBuffer, new StringBuilder("** FTSE MID CAP Daily Data ** ")) && --retry >= 0);
            }catch (Exception e){

            }
            LOGGER.info(Instant.now()+ " <-  Ended FTSE250 FtseEmailAlertMechanismService::kickOffEmailAlerts" );

        });
        executorService.shutdown();

    }

    private void generateAlertEmails(List<FtseStockInfo> populatedFtseList, SIDE side, StockCategory stockCategory) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");

            generateHTMLContent(populatedFtseList, side, dataBuffer, subjectBuffer, stockCategory);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<FtseStockInfo> populatedFtseList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer, StockCategory stockCategory) {
        if (populatedFtseList != null && populatedFtseList.size() >0){
            populatedFtseList = populatedFtseList.stream().distinct().collect(Collectors.toList());
            populatedFtseList.stream().distinct().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() <=150 && stockCategory == StockCategory.LARGE_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(3.5)) <= 0)){
                        if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** FTSE Buy Large Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                        }
                    }

                    if (x.getStockRankIndex() > 150 && stockCategory == StockCategory.MID_CAP && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(2.0)) <= 0)){
                        if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** FTSE Buy Mid Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                        }
                    }

                    if (stockCategory == StockCategory.LARGE_CAP  && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(2)) <= 0)){
                            if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                                if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                    subjectBuffer.append("** FTSE Sell Large Cap Alert**");
                                }
                                createTableContents(dataBuffer, x);
                            }
                    }
                    if (stockCategory == StockCategory.MID_CAP  && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(3.5)) <= 0))){
                        if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** FTSE Sell Mid Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                        }
                    }
                }
            });
        }
    }


    private boolean checkPortfolioSizeAndQtyExists(String code) {
        return portfolioInfoList.stream().filter(x -> x.getSymbol().contains(code))
                .anyMatch(y -> ((y.getMarketValue() != null && getDoubleFromString(y.getMarketValue().replace("£", "").replace(",", "")) > 500.0)));
    }

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;

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
                Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\logs\\" + fileName + ".html");
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
            try ( InputStream inputStream  =  new ClassPathResource("ftse_portfolio.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }

        } catch(Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }
    }

}
