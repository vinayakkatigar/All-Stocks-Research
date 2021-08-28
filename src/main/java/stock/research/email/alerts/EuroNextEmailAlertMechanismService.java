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
import stock.research.domain.EuroNextStockInfo;
import stock.research.domain.PortfolioInfo;
import stock.research.service.EuroNextStockResearchService;
import stock.research.utility.EuroNextStockResearchUtility;

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

    //@Scheduled(cron = "0 5 4 ? * MON-FRI")
    public void kickOffEmailAlerts_1() {
        kickOffEmailAlerts();
    }
    //@Scheduled(cron = "0 5 5 ? * MON-FRI")
    public void kickOffEmailAlerts_2() {
        kickOffEmailAlerts();
    }
    //@Scheduled(cron = "0 25 9 ? * MON-FRI")
    public void kickOffEmailAlerts_3() {
        kickOffEmailAlerts();
    }
    @Scheduled(cron = "0 35 9 ? * MON-FRI")
    public void kickOffEmailAlerts_4() {
        kickOffEmailAlerts();
    }

    //@Scheduled(cron = "0 35 4,5,9,10 ? * MON-FRI")
    public void kickOffEmailAlerts() {
        LOGGER.info(Instant.now()+ " <-  Started  EuroNextEmailAlertMechanismService::kickOffEmailAlerts" );
        final List<EuroNextStockInfo> EuroNextStockInfoList = stockResearchService.populateEuroNextStockDetailedInfo();
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(EuroNextStockInfoList,x);
        });
        LOGGER.info(Instant.now()+ " <-  Ended  EuroNextEmailAlertMechanismService::kickOffEmailAlerts" );

        StringBuilder dataBuffer = new StringBuilder("");
        EuroNextStockInfoList.stream().forEach(x -> EuroNextStockResearchUtility.createTableContents(dataBuffer, x));
        String data = EuroNextStockResearchUtility.HTML_START;
        data += dataBuffer.toString();
        data += EuroNextStockResearchUtility.HTML_END;
        String fileName = "EuroNextTop250".toString();
        fileName = fileName.replace("*", "");
        fileName = fileName.replace(" ", "");
        fileName =  fileName + "-" + LocalDateTime.now()  ;
        fileName = fileName.replace(":","-");

        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\" + fileName  + ".html"), data.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void generateAlertEmails(List<EuroNextStockInfo> EuroNextStockInfoList, SIDE side) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");
            generateHTMLContent(EuroNextStockInfoList, side, dataBuffer, subjectBuffer);
            sendEmail(dataBuffer, subjectBuffer);
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

    private void sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) throws MessagingException, IOException {
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String data = EuroNextStockResearchUtility.HTML_START;
        data += dataBuffer.toString();
        data += EuroNextStockResearchUtility.HTML_END;

        if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                "".equalsIgnoreCase(subjectBuffer.toString()) == false){
            helper.setFrom("stockalert@stockalert.com");
            helper.setTo(new String[]{"raghukati1950@gmail.com","raghu.kat@outlook.com"});
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
        LOGGER.info("EuroNextEmailAlertMechanismService::portfolioInfoList" + portfolioInfoList);
    }

}
