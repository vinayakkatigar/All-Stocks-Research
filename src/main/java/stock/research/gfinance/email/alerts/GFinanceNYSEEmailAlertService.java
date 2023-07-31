package stock.research.gfinance.email.alerts;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.gfinance.domain.GFinanceNYSEStockInfo;
import stock.research.gfinance.repo.GFinanceNyseStockInfoRepositary;
import stock.research.gfinance.service.GFinanceNYSEStockService;

import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_END;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_START;
import static stock.research.utility.FtseStockResearchUtility.END_BRACKET;
import static stock.research.utility.FtseStockResearchUtility.START_BRACKET;

@Service
public class GFinanceNYSEEmailAlertService {
    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceNYSEEmailAlertService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GFinanceNYSEStockService gFinanceNYSEStockService;
    @Autowired
    private GFinanceNyseStockInfoRepositary gFinanceNyseStockInfoRepositary;

    private List<GFinanceNYSEStockInfo> gFinanceNYSEStockList = new ArrayList<>();

    @Scheduled(cron = "0 */15 * ? * *", zone = "GMT")
    public void kickOffGFinanceRefresh() {
        Instant instantBefore = now();
        LOGGER.info(now() + " <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGFinanceRefresh" );
        gFinanceNYSEStockService.getGFinanceNYSEStockInfoList();
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceNYSEEmailAlertService::kickOffGFinanceRefresh" + now() );
    }

    @Scheduled(cron = "0 0 0,6,12,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceNYSEEmailAlerts() {
        Instant instantBefore = now();
        LOGGER.info(now() + " <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );
        final List<GFinanceNYSEStockInfo> gFinanceNYSEStockInfoList = gFinanceNYSEStockService.getGFinanceNYSEStockInfoList();
/*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
        generateAlertEmails(gFinanceNYSEStockInfoList,SIDE.BUY, StockCategory.LARGE_CAP);
        LOGGER.info(now()+ " <-  Ended kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );

        try {
            final StringBuilder dataBuffer = new StringBuilder("");
            gFinanceNYSEStockInfoList.stream().filter((x -> (x.get_52WeekLowPrice() != null
                    && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) != 0 &&
                    x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) != 0 &&
                    x.get_52WeekHighLowPriceDiff() != null &&
                    x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO) != 0) ))
                    .forEach(x ->  createTableContents(dataBuffer, x));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder("** Google Finance NYSE Daily Data ** ")) && --retry >= 0);
        }catch (Exception e){
            ERROR_LOGGER.error("Google Finance NYSE -> ", e);
        }
        try {
            gFinanceNYSEStockInfoList.forEach(x -> gFinanceNyseStockInfoRepositary.save(x));
        }catch (Exception e){
            ERROR_LOGGER.error("GFinance NYSE DB inserts", e);
        }
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceNYSEEmailAlertService::kickOffGoogleFinanceNYSEEmailAlerts" + now() );
    }

    private void generateAlertEmails(List<GFinanceNYSEStockInfo> populatedFtseList, SIDE side, StockCategory stockCategory) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");

            generateHTMLContent(populatedFtseList, side, dataBuffer, subjectBuffer, stockCategory);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<GFinanceNYSEStockInfo> populatedFtseList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer, StockCategory stockCategory) {
        if (populatedFtseList != null && populatedFtseList.size() >0){
            populatedFtseList = populatedFtseList.stream().distinct().collect(Collectors.toList());

            populatedFtseList.stream().distinct().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() <=150 && stockCategory == StockCategory.LARGE_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(35)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                                || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Google Finance NYSE Buy Large Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() > 150 && stockCategory == StockCategory.MID_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(50)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Google Finance NYSE Buy Mid Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                    }

                    if (stockCategory == StockCategory.LARGE_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                                if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                    subjectBuffer.append("** Google Finance NYSE Sell Large Cap Alert**");
                                }
                                createTableContents(dataBuffer, x);
                    }
                    if (stockCategory == StockCategory.MID_CAP
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Google Finance NYSE Sell Mid Cap Alert**");
                            }
                            createTableContents(dataBuffer, x);
                    }
                }
            });
        }
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
            ERROR_LOGGER.error("Error::GoogleFinance generating Email", e);
            return false;
        }
        return true;
    }


    public void createTableContents(StringBuilder dataBuffer, GFinanceNYSEStockInfo x) {
        if (x.get_52WeekLowPrice().compareTo(x.getCurrentMarketPrice()) >= 0){
            dataBuffer.append("<tr style=\"background-color:#FFBA75\">");
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
        dataBuffer.append("<td>" + x.getP2e() + "</td>");
        dataBuffer.append("</tr>");
    }

}
