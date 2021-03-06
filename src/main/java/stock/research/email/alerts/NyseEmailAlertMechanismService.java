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
import stock.research.domain.NyseStockInfo;
import stock.research.domain.PortfolioInfo;
import stock.research.service.NYSEStockResearchService;
import stock.research.service.StartUpNYSEStockResearchService;
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

import static stock.research.utility.NyseStockResearchUtility.*;

@Service
public class NyseEmailAlertMechanismService {
    enum StockCategory{LARGE_CAP, MID_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(NyseEmailAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NYSEStockResearchService stockResearchService;
    @Autowired
    private StartUpNYSEStockResearchService startUpNYSEStockResearchService;

    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();
    @Scheduled(cron = "0 45 9,15 ? * MON-THU")
    public void kickOffEmailAlerts_Cron() {
        kickOffEmailAlerts();
    }

    @Scheduled(cron = "0 5 15 ? * FRI")
    public void kickOffEmailAlerts_Fri() {
        kickOffEmailAlerts();
    }

    @Scheduled(cron = "0 5 0 ? * MON-FRI")
    public void kickOffKillZombs() {
        StockResearchUtility.killZombie("haha");
    }

    public void kickOffEmailAlerts() {

            LOGGER.info(Instant.now()+ " <-  Started NYSE NyseEmailAlertMechanismService::kickOffEmailAlerts" );
            final List<NyseStockInfo> nyseStockInfoList = stockResearchService.populateNYSEStockDetailedInfo();
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(nyseStockInfoList,x, StockCategory.LARGE_CAP);
            });

            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(nyseStockInfoList,x, StockCategory.MID_CAP);
            });
            try {
                StringBuilder dataBuffer = new StringBuilder("");
                stockResearchService.getCacheNYSEStockDetailedInfoList().forEach(x ->  createTableContents(dataBuffer, x));
                int retry = 3;
                while (!sendEmail(dataBuffer, new StringBuilder("** NASDAQ Daily Data ** ")) && --retry >= 0);
            }catch (Exception e){

            }
            LOGGER.info(Instant.now()+ " <-  Ended NYSE NyseEmailAlertMechanismService::kickOffEmailAlerts" );

    }

    public void startUpKickOffEmailAlerts() {

            LOGGER.info(Instant.now()+ " <-  Started NYSE NyseEmailAlertMechanismService::kickOffEmailAlerts" );
            final List<NyseStockInfo> nyseStockInfoList = startUpNYSEStockResearchService.startPopulateNYSEStockDetailedInfo();
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(nyseStockInfoList,x, StockCategory.LARGE_CAP);
            });

            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(nyseStockInfoList,x, StockCategory.MID_CAP);
            });
            try {
                StringBuilder dataBuffer = new StringBuilder("");
                startUpNYSEStockResearchService.getCacheNYSEStockDetailedInfoList().forEach(x ->  createTableContents(dataBuffer, x));
                int retry = 3;
                while (!sendEmail(dataBuffer, new StringBuilder("** NASDAQ Daily Data ** ")) && --retry >= 0);
            }catch (Exception e){

            }
            LOGGER.info(Instant.now()+ " <-  Ended NYSE NyseEmailAlertMechanismService::kickOffEmailAlerts" );

    }

    private void generateAlertEmails(List<NyseStockInfo> nyseStockInfoList, SIDE side, StockCategory stockCategory) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");
            generateHTMLContent(nyseStockInfoList, side, dataBuffer, subjectBuffer, stockCategory);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<NyseStockInfo> nyseStockInfoList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer, StockCategory stockCategory) {
        if (nyseStockInfoList != null && nyseStockInfoList.size() >0){
            nyseStockInfoList = nyseStockInfoList.stream().distinct().collect(Collectors.toList());
            nyseStockInfoList.stream().distinct().forEach(x -> {
//            nyseStockInfoList.stream().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() <= 250  && stockCategory == StockCategory.LARGE_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                            if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                                if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                    subjectBuffer.append("** NASDAQ Buy Large Cap Alert**");
                                }
                                createTableContents(dataBuffer, x);
                            }
                    }

                    if ( x.getStockRankIndex() > 250 && stockCategory == StockCategory.MID_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                                ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 ||
                                        x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5.0)) <= 0))){
                                if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                                    if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                        subjectBuffer.append("** NASDAQ Buy Mid Cap Alert**");
                                    }
                                    createTableContents(dataBuffer, x);
                                }
                    }

                    if (x.getStockRankIndex() <= 250 && stockCategory == StockCategory.LARGE_CAP
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** NASDAQ Sell Large Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                        }
                    }

                    if (x.getStockRankIndex() > 250 && stockCategory == StockCategory.MID_CAP
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0))){
                        if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** NASDAQ Sell Mid Cap Alert**");
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
                .anyMatch(y -> ((y.getMarketValue() != null && getDoubleFromString(y.getMarketValue().replace("??", "").replace(",", "")) > 500.0)));
    }

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) throws MessagingException, IOException {
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
            try ( InputStream inputStream  =  new ClassPathResource("nyse_portfolio.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }

        } catch(Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }
    }

}
