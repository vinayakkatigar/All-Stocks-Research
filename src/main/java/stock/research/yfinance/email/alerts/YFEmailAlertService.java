package stock.research.yfinance.email.alerts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.domain.StockInfo;
import stock.research.yfinance.domain.YFinanceStockInfo;
import stock.research.yfinance.repo.YFinanceStockInfoRepositary;
import stock.research.yfinance.service.YFStockService;
import stock.research.yfinance.utility.YFinanceNyseStockUtility;

import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static stock.research.utility.FtseStockResearchUtility.END_BRACKET;
import static stock.research.utility.FtseStockResearchUtility.START_BRACKET;

@Service
public class YFEmailAlertService {
//    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(YFEmailAlertService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private YFStockService yfStockService;
    @Autowired
    private YFinanceStockInfoRepositary yFinanceStockInfoRepositary;


    @Scheduled(cron = "0 30 1,8,14,20 ? * MON-SAT", zone = "GMT")
    public void kickOffYFROWEmailAlerts() throws Exception {
        List<String> rowList = objectMapper.readValue("[\"Australia.json\",\"Austria.json\",\"Belgium.json\",\"Brazil.json\",\"Canada.json\",\"Denmark.json\",\"Euro.json\",\"Finland.json\",\"France.json\",\"Germany.json\",\"India.json\",\"Italy.json\",\"Japan.json\",\"Netherlands.json\",\"Norway.json\",\"Singapore.json\",\"SouthKorea.json\",\"Spain.json\",\"Sweden.json\",\"Swiss.json\"]", new TypeReference<List<String>>() { });
        
//        List<String> rowList = objectMapper.readValue(new ClassPathResource("Sweden.json").getInputStream(), new TypeReference<List<String>>() { });
        rowList.forEach( country -> {
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffYFNYSEEmailAlerts::kickOffYFROWEmailAlerts:: country->" + country);

            final List<YFinanceStockInfo> yfStockInfoList = yfStockService.getYFStockInfoList(getStockCode("YF/" + country));
/*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
            final StringBuilder subjectBuffer = new StringBuilder("*** YF ROW "+ country.replaceAll(".json", "") + " Buy Alert *** ");
            generateAlertEmails(yfStockInfoList, SIDE.BUY, subjectBuffer);
            LOGGER.info(now()+ " <-  Ended kickOffYFNYSEEmailAlerts::kickOffYFROWEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** YF ROW "+ country.replaceAll(".json", "") + " Daily Data *** ");
            generateDailyEmail(yfStockInfoList, subject);
            writeToDB(yfStockInfoList);
            LOGGER.info(instantBefore.until(now(), SECONDS)+ " <- Total time in mins, Ended YFinanceEmailAlertService::kickOffYFROWEmailAlerts" + now() );

        });


    }


    @Scheduled(cron = "0 30 1,15,18,21 ? * MON-SAT", zone = "GMT")
    public void kickOffYFNYSEEmailAlerts() {
        Instant instantBefore = now();
        LOGGER.info(now() + " <-  Started kickOffYFNYSEEmailAlerts::kickOffYFNYSEEmailAlerts" );



        final List<YFinanceStockInfo> yfStockInfoList = yfStockService.getYFStockInfoList(getStockCode("YF/NYSE.json"));
/*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
        final StringBuilder subjectBuffer = new StringBuilder("");
        generateAlertEmails(yfStockInfoList, SIDE.BUY, subjectBuffer);
        LOGGER.info(now()+ " <-  Ended kickOffYFNYSEEmailAlerts::kickOffYFNYSEEmailAlerts" );

        StringBuilder subject = new StringBuilder("*** YF NYSE Daily Data *** ");
        generateDailyEmail(yfStockInfoList, subject);
        writeToDB(yfStockInfoList);
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended YFinanceEmailAlertService::kickOffYFNYSEEmailAlerts" + now() );
    }

    private List<String> getStockCode(String file) {
        try {
            return objectMapper.readValue(new ClassPathResource(file).getInputStream(), new TypeReference<List<String>>(){});
        } catch (IOException e) {
            return null;
        }
    }

    private void generateAlertEmails(List<YFinanceStockInfo> yFinanceStockInfoList, SIDE side, StringBuilder subjectBuffer) {
        try {
            System.out.println("Size -> " + yFinanceStockInfoList.size());
            StringBuilder dataBuffer = new StringBuilder("");

            generateHTMLContent(yFinanceStockInfoList, side, dataBuffer, subjectBuffer);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(now() + "<- , Error ->", e);
        }
    }

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            String data = YFinanceNyseStockUtility.HTML_START;
            data += dataBuffer.toString();
            data += YFinanceNyseStockUtility.HTML_END;

            if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                    "".equalsIgnoreCase(subjectBuffer.toString()) == false){
                helper.setFrom("raghu_kat_stocks@outlook.com");
                helper.setTo(new String[]{"raghu_kat_stocks@outlook.com", "pabari_sush_stocks@outlook.com"});
//            helper.setTo(new String[]{"raghu_kat_stocks@outlook.com","raghu.kat@outlook.com"});
                helper.setText(data, true);
                helper.setSubject(subjectBuffer.toString());
                String fileName = subjectBuffer.toString();
                fileName = fileName.replace("*", "");
                fileName = fileName.replace(" ", "");
                fileName =  fileName + "-" + LocalDateTime.now()  ;
                fileName = fileName.replace(":","-");
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\genFiles\\" + fileName + ".html");
                helper.addAttachment(file.getFilename(), file);
                javaMailSender.send(message);
            }
        }catch (Exception e){
            ERROR_LOGGER.error("Error::YahooFinance generating Email", e);
            return false;
        }
        return true;
    }

    public void createTableContents(StringBuilder dataBuffer, YFinanceStockInfo x) {
        if (x.get_52WeekLowPrice().compareTo(x.getCurrentMarketPrice()) >= 0){
            dataBuffer.append("<tr style=\"background-color:#00ffff\">");
        }else if (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0 ){
            dataBuffer.append("<tr style=\"background-color:#A7D971\">");
        }else {
            dataBuffer.append("<tr>");
        }
        dataBuffer.append("<td>" + x.getStockRankIndex() + "</td>");
        dataBuffer.append("<td>" + x.getStockName() +
                START_BRACKET + x.getMktCapFriendyValue() + END_BRACKET + "</a></td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        if (Double.compare(x.getChangePct().doubleValue() , 5d) >= 0){
            dataBuffer.append("<td style=\"background-color:#A7D971\">" + x.getChangePct()  + "</td>");
        } else if (Double.compare(x.getChangePct().doubleValue() , -5d) <= 0){
            dataBuffer.append("<td style=\"background-color:#cc0000\">" + x.getChangePct()  + "</td>");
        }else {
            dataBuffer.append("<td>" + x.getChangePct() + "</td>");
        }
        dataBuffer.append("<td>" + x.getP2e() + "</td>");
        dataBuffer.append("<td>" + x.getEps() + "</td>");
        dataBuffer.append("</tr>");
    }

    private void writeToDB(List<YFinanceStockInfo> yFinanceStockInfoList) {
        try {
            yFinanceStockInfoList.forEach(x -> yFinanceStockInfoRepositary.save(x));
        }catch (Exception e){
            ERROR_LOGGER.error("GF DB inserts", e);
        }
    }

    private void generateDailyEmail(List<YFinanceStockInfo> yFinanceStockInfoList, StringBuilder subject) {
        try {
            final StringBuilder dataBuffer = new StringBuilder("");
            yFinanceStockInfoList.stream().filter((x -> (x.get_52WeekLowPrice() != null
                            && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) != 0 &&
                            x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) != 0 &&
                            x.get_52WeekHighLowPriceDiff() != null &&
                            x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO) != 0) ))
                    .forEach(x ->  createTableContents(dataBuffer, x));
            int retry = 3;
            while (!sendEmail(dataBuffer, subject) && --retry >= 0);
        }catch (Exception e){
            ERROR_LOGGER.error("GF NYSE Email error -> ", e);
        }
    }

    private void generateHTMLContent(List<YFinanceStockInfo> yFinanceStockInfoList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (yFinanceStockInfoList != null && yFinanceStockInfoList.size() >0){
            yFinanceStockInfoList = yFinanceStockInfoList.stream().distinct().collect(Collectors.toList());

            yFinanceStockInfoList.stream().distinct().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() != null && x.getStockRankIndex() <= 150
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(44)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** YF NYSE Buy Large Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(50)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** YF NYSE Buy Mid Cap Alert ***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() <= 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** YF NYSE Sell Large Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0))){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** YF NYSE Sell Mid Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                }
            });
        }
    }
}