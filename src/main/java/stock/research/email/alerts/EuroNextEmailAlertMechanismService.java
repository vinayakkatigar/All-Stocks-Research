package stock.research.email.alerts;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.domain.EuroNextStockInfo;
import stock.research.domain.PortfolioInfo;
import stock.research.service.EuroNextStockResearchService;
import stock.research.utility.EuroNextStockResearchUtility;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
public class EuroNextEmailAlertMechanismService {

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(EuroNextEmailAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EuroNextStockResearchService stockResearchService;

    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();


    @Scheduled(cron = "0 15 1 ? * MON-SAT")
    public void kickOffNightlyEmailAlerts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEuro();
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 35 9,15 ? * MON-FRI")
    public void kickOffEmailAlerts() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
//            kickOffEuro();
        });
        executorService.shutdown();
    }

    private void kickOffEuro() {
        try {
            Instant instantBefore = Instant.now();
            LOGGER.info(Instant.now()+ " <-  Started  EuroNextEmailAlertMechanismService::kickOffEmailAlerts" );
            final List<EuroNextStockInfo> euroNextStockInfoList = stockResearchService.populateEuroNextStockDetailedInfo();
            Arrays.stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(euroNextStockInfoList,x);
            });
            LOGGER.info(Instant.now()+ " <-  Ended  EuroNextEmailAlertMechanismService::kickOffEmailAlerts" );

            StringBuilder dataBuffer = new StringBuilder("");
            euroNextStockInfoList.stream().forEach(x -> EuroNextStockResearchUtility.createTableContents(dataBuffer, x));
            String data = EuroNextStockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += EuroNextStockResearchUtility.HTML_END;
            String fileName = "EuroNextTop250".toString();
            fileName = fileName.replace("*", "");
            fileName = fileName.replace(" ", "");
            fileName =  fileName + "-" + LocalDateTime.now()  ;
            fileName = fileName.replace(":","-");

            try {
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genHtml\\EURO-" + fileName  + ".html"), data.getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                StringBuilder euroDataBuffer = new StringBuilder("");
                euroNextStockInfoList.forEach(x ->  EuroNextStockResearchUtility.createTableContents(euroDataBuffer, x));
                int retry = 3;
                while (!sendEmail(euroDataBuffer, new StringBuilder("** EURO Daily Data ** ")) && --retry >= 0);
            }catch (Exception e){
                e.printStackTrace();
                LOGGER.error("NASDAQ Daily Data, Error ->",e);
            }

            LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.MINUTES)+ " <- Total time in mins, Ended EuroNextEmailAlertMechanismService::kickOffEmailAlerts" + Instant.now() );
        }catch (Exception e){

        }
    }

    private void generateAlertEmails(List<EuroNextStockInfo> EuroNextStockInfoList, SIDE side) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");
            generateHTMLContent(EuroNextStockInfoList, side, dataBuffer, subjectBuffer);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<EuroNextStockInfo> EuroNextStockInfoList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (EuroNextStockInfoList != null && EuroNextStockInfoList.size() > 0){
            EuroNextStockInfoList.stream().forEach(x -> {
                if (x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(2.0)) <= 0){
                        if (!(checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** EURO NEXT Buy Large Cap Alert**");
                            }
                            EuroNextStockResearchUtility.createTableContents(dataBuffer, x);
                        }
                    }
                    if (side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(2.0)) <= 0){
                        if ((checkPortfolioSizeAndQtyExists(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** EURO NEXT Sell Large Cap Alert**");
                            }
                            EuroNextStockResearchUtility.createTableContents(dataBuffer, x);
                        }
                    }

                }
            });
        }
    }

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = EuroNextStockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += EuroNextStockResearchUtility.HTML_END;

            if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                    "".equalsIgnoreCase(subjectBuffer.toString()) == false){
                helper.setFrom("raghu_kat_stocks@outlook.com");
//            helper.setTo(new String[]{"raghu_kat_stocks@outlook.com","raghu.kat@outlook.com"});
                helper.setTo(new String[]{"raghu_kat_stocks@outlook.com"});
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

    private boolean checkPortfolioSizeAndQtyExists(String code) {
        return portfolioInfoList.stream().filter(x -> x.getSymbol().contains(code))
                .anyMatch(y -> ((y.getMarketValue() != null && EuroNextStockResearchUtility.getDoubleFromString(y.getMarketValue().replace("£", "").replace(",", "")) > 500.0)));
    }

    @PostConstruct
    public void setUpPortfolioData(){
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.reader(PortfolioInfo.class).with(schema);
            try ( InputStream inputStream  =  new ClassPathResource("euro_next_portfolio.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }

        } catch(Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }
    }

}
