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
import stock.research.gfinance.domain.GFinanceStockInfo;
import stock.research.gfinance.repo.GFinanceStockInfoRepositary;
import stock.research.gfinance.service.GFinanceStockService;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.time.temporal.ChronoUnit.SECONDS;
import static java.util.Arrays.*;
import static java.util.stream.Collectors.toList;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_END;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_START;
import static stock.research.utility.FtseStockResearchUtility.END_BRACKET;
import static stock.research.utility.FtseStockResearchUtility.START_BRACKET;

@Service
public class GFinanceEmailAlertService {
//    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};

    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceEmailAlertService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GFinanceStockService gFinanceStockService;
    @Autowired
    private GFinanceStockInfoRepositary gFinanceStockInfoRepositary;

    private Map<String, String> nyseUrlInfo = new HashMap<>();
    private Map<String, String> nseUrlInfo = new HashMap<>();
    private Map<String, String> portfolioUrl = new HashMap<>();
    private Map<String, String> ftseUrl = new HashMap<>();
    private Map<String, String> asxUrl = new HashMap<>();
    private Map<String, String> germanUrl = new HashMap<>();
    private Map<String, String> watchListUrl = new HashMap<>();

    @PostConstruct
    public void setUp(){
        nyseUrlInfo.put("Vin-Nyse-1", "1r0ZqMeOPIfkoakhcW3dGHE2YsKgJJO4M7InwgcP2-Ao");
        nyseUrlInfo.put("Vin-Nyse-2", "1DdkJYnXIR0UCLeB7cjB8G4LGzZMXHKQ_dGpXGO8CU8I");
        nyseUrlInfo.put("Vin-Nyse-3", "1YmgSZuLMPJqgPLUuaQCTq2TXjts4aPD0w19zYpV0WpE");
        nyseUrlInfo.put("Vin-Nyse-4", "1TGdtwdz_6O9wTO3xFzUwo4wlbsyGsQWeHolLVJzxLyQ");
        portfolioUrl.put("Vin-portfolio", "1M7swFwopiNGRZn052yCc1YM3cYVQvLiBytJNehncSPI");
        nseUrlInfo.put("Vin-Nse-1", "1wjfxknOyQ5TghGwoFOt-LTz2E-APPXFAfsYqaooIyBQ");
        nseUrlInfo.put("Vin-Nse-2", "1j6QRSY9A9b8hYWBEidkl5B4X6FaiVByS2tUc4aU4b6M");
        nseUrlInfo.put("Vin-Nse-3", "1Hvw1BJH1eJWbtJwReD3n96DGeHarFVXeR93B3-0tKPE");
        nseUrlInfo.put("Vin-Nse-4", "1WSVRVhqU6vUdatoic49kwdAUSoBUaOlqZscyTVzX8CE");
        nseUrlInfo.put("Vin-Nse-5", "1YlVMw15EIi2-Z62TIJ3Bt6-zb0UlR_4FZrjc1LoPnHU");
        ftseUrl.put("Vin-FTSE-1", "1iF_6oxXe2bvyQCNadAUlaLB_3GSkhNrzik9HZyK2iUI");
        ftseUrl.put("Vin-FTSE-2", "1gqq8oNpG35WwhSARwIoFhTQOs2yqVa6UPzTTzLgAdew");
        asxUrl.put("Vin-Australia", "1qijUJ91J-Qzuj2wI9RdWULOoq1XaZQMrAZOpWwbyt3Y");
        germanUrl.put("Vin-Germany", "1a9tmx2Qx1dx1hH4J4Hr8yLFSaV-i_X4VoNp1VrThMek");
        watchListUrl.put("Vin-Watchlist", "1V89w-xI5urpoBIcCAHeIoho0J1cJxkFzSQXmG04U85w");

    }

    @Scheduled(cron = "0 */15 * ? * *", zone = "GMT")
    public void kickOffGFinanceRefresh() {
        Instant instantBefore = now();
        LOGGER.info(now() + " <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGFinanceRefresh" );
        gFinanceStockService.getGFStockInfoList(nyseUrlInfo);
        gFinanceStockService.getGFStockInfoList(portfolioUrl);
        gFinanceStockService.getGFStockInfoList(nseUrlInfo);
        gFinanceStockService.getGFStockInfoList(ftseUrl);
        gFinanceStockService.getGFStockInfoList(asxUrl);
        gFinanceStockService.getGFStockInfoList(germanUrl);
        gFinanceStockService.getGFStockInfoList(watchListUrl);
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceNYSEEmailAlertService::kickOffGFinanceRefresh" + now() );
    }

    @Scheduled(cron = "0 40 0,4,9,18,22 ? * MON-SAT", zone = "GMT")
    public void kickOffGFWatchListEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFWatchListEmailAlerts" );

            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(watchListUrl);
            List<GFinanceStockInfo> stockInfoPctList = sortByDailyPCTChange(stockInfoList);

            generateAlertEmails(stockInfoPctList, SIDE.BUY, new StringBuilder("*** GF WatchList " + SIDE.BUY + " Alerts ***"));
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF WatchList Daily Data *** "));

            writeToDB(stockInfoList);
            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFWatchListEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFWatchListEmailAlerts" + now() );

        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 30 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFASXEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFASXEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(asxUrl);
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF ASX " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF ASX Daily Data *** "));
            writeToDB(stockInfoList);
            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFASXEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFASXEmailAlerts" + now() );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 45 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFGermanyEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFGermanyEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(germanUrl);
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF Germany " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF Germany Daily Data *** "));
            writeToDB(stockInfoList);
            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFGermanyEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), SECONDS)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFGermanyEmailAlerts" + now() );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 30 3,9,12,15,21 ? * MON-SAT", zone = "GMT")
    public void kickOffGFFTSEEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFFTSEEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(ftseUrl);
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF FTSE " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF FTSE Daily Data *** "));
            writeToDB(stockInfoList);
            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFFTSEEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFFTSEEmailAlerts" + now() );
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 0 4,10,16,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNSEEmailAlerts() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFNSEEmailAlerts" );
            final List<GFinanceStockInfo> gfPortfolioList = gFinanceStockService.getGFStockInfoList(nseUrlInfo);
            gfPortfolioList.sort(Comparator.comparing(GFinanceStockInfo::getMktCapRealValue));
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gfPortfolioList, SIDE.BUY, new StringBuilder("*** GF NSE " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(gfPortfolioList, new StringBuilder("*** GF NSE Daily Data *** "));
            writeToDB(gfPortfolioList);
            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFNSEEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFNSEEmailAlerts" + now() );
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 0 5,11,17,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFPortfolioEmailAlerts() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFPortfolioEmailAlerts" );
            final List<GFinanceStockInfo> gfPortfolioList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(portfolioUrl));

            stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(gfPortfolioList, x, new StringBuilder("*** GF Portfolio " + x + " Alerts ***"));
            });

            generateDailyEmail(gfPortfolioList, new StringBuilder("*** GF Portfolio Daily Data ** "));
            writeToDB(gfPortfolioList);

            LOGGER.info(now()+ " <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFPortfolioEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGFPortfolioEmailAlerts" + now() );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 30 0,14,17,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceNYSEEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = gFinanceStockService.getGFStockInfoList(nyseUrlInfo);
/*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
            final StringBuilder subjectBuffer = new StringBuilder("");
            generateAlertEmails(gFinanceStockInfoList,SIDE.BUY, subjectBuffer);
            LOGGER.info(now()+ " <-  Ended kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** GF NYSE Daily Data *** ");
            generateDailyEmail(gFinanceStockInfoList, subject);
            writeToDB(gFinanceStockInfoList);
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGoogleFinanceNYSEEmailAlerts" + now() );
        });
        executorService.shutdown();


    }

    @Scheduled(cron = "0 45 7,17,21,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(now() + " <-  Started kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(nyseUrlInfo).stream().filter(x -> x.getMktCapRealValue() > 9900000000d).collect(toList())).stream().filter(x -> Math.abs(x.getChangePct()) >= 5d).collect(toList());

            generateDailyEmail(gFinanceStockInfoList, new StringBuilder("*** GF NYSE Daily Winners & Losers *** "));
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, Ended GFinanceEmailAlertService::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts" + now() );
        });
        executorService.shutdown();

    }

    private void generateAlertEmails(List<GFinanceStockInfo> populatedFtseList, SIDE side, StringBuilder subjectBuffer) {
        try {
            System.out.println("Size -> " + populatedFtseList.size());
            StringBuilder dataBuffer = new StringBuilder("");

            generateHTMLContent(populatedFtseList, side, dataBuffer, subjectBuffer);
            int retry = 3;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
        } catch (Exception e) {
            ERROR_LOGGER.error(now() + "<- , Error ->", e);
        }
    }

    private boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            Thread.sleep(100 * 1000);
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

    public void createTableContents(StringBuilder dataBuffer, GFinanceStockInfo x) {
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
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        if (Double.compare(x.getChangePct().doubleValue() , 5d) >= 0){
            dataBuffer.append("<td style=\"background-color:#A7D971\">" + x.getChangePct()  + "</td>");
        } else if (Double.compare(x.getChangePct().doubleValue() , -5d) <= 0){
            dataBuffer.append("<td style=\"background-color:#cc0000\">" + x.getChangePct()  + "</td>");
        }else {
            dataBuffer.append("<td>" + x.getChangePct() + "</td>");
        }
        dataBuffer.append("<td>" + x.getP2e() + "</td>");
        dataBuffer.append("</tr>");
    }

    private void writeToDB(List<GFinanceStockInfo> gFinanceStockInfoList) {
        try {
            gFinanceStockInfoList.forEach(x -> gFinanceStockInfoRepositary.save(x));
        }catch (Exception e){
            ERROR_LOGGER.error("GF DB inserts", e);
        }
    }

    private void generateDailyEmail(List<GFinanceStockInfo> gFinanceStockInfoList, StringBuilder subject) {
        try {
            final StringBuilder dataBuffer = new StringBuilder("");
            gFinanceStockInfoList.stream().filter((x -> (x.get_52WeekLowPrice() != null
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

    private List<GFinanceStockInfo> sortByDailyPCTChange(List<GFinanceStockInfo> stockInfoList) {
        List<GFinanceStockInfo> stockInfoPctList = new ArrayList<>(stockInfoList);

        stockInfoPctList.sort(Comparator.comparing(x -> {
            return Math.abs(x.getChangePct());
        }));

        Collections.reverse(stockInfoPctList);
        return stockInfoPctList;
    }


    private void generateHTMLContent(List<GFinanceStockInfo> populatedFtseList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (populatedFtseList != null && populatedFtseList.size() >0){
            populatedFtseList = populatedFtseList.stream().distinct().collect(toList());

            populatedFtseList.stream().distinct().forEach(x -> {
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
                            subjectBuffer.append("*** GF NYSE Buy Large Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(50)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Buy Mid Cap Alert ***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() <= 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Sell Large Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0))){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Sell Mid Cap Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                }
            });
        }
    }
}
