package stock.research.gfinance.email.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import stock.research.gfinance.domain.GoogleFinanceStockDetails;
import stock.research.gfinance.repo.GFinanceStockInfoRepositary;
import stock.research.gfinance.repo.GoogleFinanceStockDetailsRepositary;
import stock.research.gfinance.service.GFinanceStockService;

import javax.annotation.PostConstruct;
import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Double.compare;
import static java.lang.Math.abs;
import static java.lang.Thread.currentThread;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.math.RoundingMode.HALF_UP;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.stream;
import static java.util.Collections.reverse;
import static java.util.Comparator.comparing;
import static java.util.Comparator.reverseOrder;
import static java.util.concurrent.Executors.newSingleThreadExecutor;
import static java.util.stream.Collectors.toList;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_END;
import static stock.research.gfinance.utility.GFinanceNyseStockUtility.HTML_START;
import static stock.research.utility.FtseStockResearchUtility.END_BRACKET;
import static stock.research.utility.FtseStockResearchUtility.START_BRACKET;
import static stock.research.utility.StockResearchUtility.checkIfWeekend;
import static stock.research.utility.StockUtility.goSleep;
import static stock.research.utility.StockUtility.writeToFile;

@Service
public class GFinanceEmailAlertService {

    public static final String GF_NYSE = "GF-NYSE";
    public static final String GF_WATCHLIST = "GF-WATCHLIST";
    public static final String GF_PORTFOLIO = "GF-PORTFOLIO";

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
    @Autowired
    private GoogleFinanceStockDetailsRepositary googleFinanceStockDetailsRepositary;

    @Autowired
    private GFAlertService gfAlertService;

    private Map<String, String> nyseUrlInfo = new HashMap<>();
    private Map<String, String> nseUrlInfo = new HashMap<>();
    private Map<String, String> portfolioUrl = new HashMap<>();
    private Map<String, String> ftseUrl = new HashMap<>();
    private Map<String, String> asxUrl = new HashMap<>();
    private Map<String, String> germanUrl = new HashMap<>();
    private Map<String, String> watchListUrl = new HashMap<>();
    private Map<String, String> hongKongUrl = new HashMap<>();
    private Map<String, String> swissUrl = new HashMap<>();
    private Map<String, String> euroUrl = new HashMap<>();
    private Map<String, String> nsePortfolioUrl = new HashMap<>();
    private Map<String, String> skwUrl = new HashMap<>();
    private Map<String, String> indonesiaUrl = new HashMap<>();
    private Map<String, String> spainUrl = new HashMap<>();
    private Map<String, String> italyUrl = new HashMap<>();

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
        hongKongUrl.put("Vin-HongKong", "1cOJOjVE49DCjFFMq7JFdmDeOFcf3MzwV2hl6K7gyX9g");
        swissUrl.put("Vin-Switzerland", "1FybDb-TiZ1T10HUDxwVoWvQj2WHAQZG5RWqzQUX2MTI");
        euroUrl.put("Vin-Euro", "1q4PG03AHihCXg1wGHO6bKYaczyOxqW-T0BgUjJ4axJo");
        nsePortfolioUrl.put("Vin-NSE-portfolio", "1uZAxfSwuGJONmcB7DsKMJTC5c_MHQDzofKqI4lsYR0w");
        skwUrl.put("Vin-Southkorea", "14uokCiL9lYv4eRYbJbi8QRsgkGi4d2nUwqQP-bTZ3fI");
        indonesiaUrl.put("Vin-Indsia", "1FD1CJ6rJhmTfIgAzK8pE-_JPfdUn9PpI0Y3fIUEDj6c");
        spainUrl.put("Vin-Spain", "1n29f0K-GSclaF8asxBLZEdw-3_QwAP6B3l7noi6qAdE");
        italyUrl.put("Vin-Italy", "119vPfJwhY584ooSbf_3viOYYEPaTNLLW0UW60cSog8w");
    }
    @Scheduled(cron = "0 */15 * ? * *", zone = "GMT")
    public void kickOffGFinanceRefresh() {
        Instant instantBefore = now();
        LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGFinanceRefresh" );
        gFinanceStockService.getGFStockInfoList(nyseUrlInfo);
        gFinanceStockService.getGFStockInfoList(portfolioUrl);
        gFinanceStockService.getGFStockInfoList(nseUrlInfo);
        gFinanceStockService.getGFStockInfoList(ftseUrl);
        gFinanceStockService.getGFStockInfoList(asxUrl);
        gFinanceStockService.getGFStockInfoList(germanUrl);
        gFinanceStockService.getGFStockInfoList(watchListUrl);
        gFinanceStockService.getGFStockInfoList(hongKongUrl);
        gFinanceStockService.getGFStockInfoList(swissUrl);
        gFinanceStockService.getGFStockInfoList(euroUrl);
        gFinanceStockService.getGFStockInfoList(nsePortfolioUrl);
        gFinanceStockService.getGFStockInfoList(skwUrl);
        gFinanceStockService.getGFStockInfoList(indonesiaUrl);
        gFinanceStockService.getGFStockInfoList(spainUrl);
        gFinanceStockService.getGFStockInfoList(italyUrl);
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceNYSEEmailAlertService::kickOffGFinanceRefresh"  );
    }

    @Scheduled(cron = "0 10 0,4,9,18,22 ? * MON-SAT", zone = "GMT")
    public void kickOffGFWatchListEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF(GF_WATCHLIST, "WatchList ", watchListUrl, false);
    }

    @Scheduled(cron = "0 20 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFASXEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-AUSTRALIA", "ASX ", asxUrl, false);
    }

    @Scheduled(cron = "0 35 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFGermanyEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-GERMANY", "Germany ", germanUrl, false);
    }

    @Scheduled(cron = "0 55 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFSouthKoreaEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-SKW", "SouthKorea ", skwUrl, false);
    }

    @Scheduled(cron = "0 40 3,9,12,15,21 ? * MON-SAT", zone = "GMT")
    public void kickOffGFFTSEEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-FTSE", "FTSE ", ftseUrl, true);
    }


    @Scheduled(cron = "0 50 4,10,16,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNSEEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-NSE", "NSE ", nseUrlInfo, false);
    }


    @Scheduled(cron = "0 5 5,11,17,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFPortfolioEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF(GF_PORTFOLIO, "Portfolio ", portfolioUrl, false);
    }

    @Scheduled(cron = "0 15 15,19,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNYSEEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF(GF_NYSE, "NYSE ", nyseUrlInfo, true);
    }

    @Scheduled(cron = "0 35 15,19,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFIndonesiaEmailAlerts() {
        currentThread().setPriority(Thread.MIN_PRIORITY);
        kickOffGF("GF-INDONESIA", "Indonesia ", indonesiaUrl, false);
    }

    @Scheduled(cron = "0 15 6,12,18,21,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNSEPortfolioEmailAlerts() {

        currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            currentThread().setPriority(Thread.MAX_PRIORITY);
            currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFNSEPortfolioEmailAlerts" );
            final List<GFinanceStockInfo> gfPortfolioList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(nsePortfolioUrl));
            gfPortfolioList.stream().forEach(x -> x.setCountry("GF-NSE-PORTFOLIO"));
            stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(gfPortfolioList, x, new StringBuilder("*** GF NSE Portfolio " + x + " Alerts ***"));
            });

            generateDailyEmail(gfPortfolioList, new StringBuilder("*** GF NSE Portfolio Daily Data ** "));
            try {
                writeGFPayloadToDB(gfPortfolioList, "GF-NSE-PORTFOLIO");
                writeToDB(gfPortfolioList);
                writeToFile("GF-NSE-PORTFOLIO", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gfPortfolioList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFNSEPortfolioEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFNSEPortfolioEmailAlerts"  );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 25 7,17,21,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNYSEDailyPnLEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            exeWinnerAndLosers();
        });
        executorService.shutdown();

    }

    @Scheduled(cron = "0 11 3,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGFHongKongEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-HONGKONG", "HongKong ", hongKongUrl, false);
    }

    @Scheduled(cron = "0 31 10,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGFSwitzerlandEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-SWITZERLAND", "Switzerland ", swissUrl, false);
    }

    @Scheduled(cron = "0 31 10,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGFEUROEmailAlerts() {
        currentThread().setPriority(Thread.MAX_PRIORITY);
        kickOffGF("GF-EURO", "EURO ", euroUrl, false);
    }

    @Scheduled(cron = "0 07 0,21 ? * *", zone = "GMT")
    public void kickOffGFWatchListWeeklyPnLEmailAlerts() {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(7, 20d, GF_WATCHLIST + "_PNL_WEEKLY" ,
                    "** GF WATCHLIST Weekly PNL Data ** ", GF_WATCHLIST);
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 47 2,22 ? * *", zone = "GMT")
    public void kickOffGFPortfolioWeeklyPnLEmailAlerts() {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(7, 20d, GF_PORTFOLIO + "_PNL_WEEKLY" ,
                    "** GF PORTFOLIO Weekly PNL Data ** ", GF_PORTFOLIO);
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 17 1,20 ? * *", zone = "GMT")
    public void kickOffScreenerWeeklyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(7, 20d, "GF-NYSE_PNL_WEEKLY" , "** GF NYSE Weekly PNL Data ** ", GF_NYSE);
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 47 2,20 ? * *", zone = "GMT")
    public void kickOffScreenerMonthlyPnLEmailAlerts() {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(30, 30d, "GF-NYSE_PNL_MONTHLY" , "** GF NYSE Monthly PNL Data ** ", GF_NYSE);
        });
        executorService.shutdown();
    }

    private void runPnlLogicForSpecifiedDays(int noOfDays, double cutOffPct, String fileName, String emailSubject, String country) {
        List<GFinanceStockInfo> sortedGFNYSEStockAlertList = new CopyOnWriteArrayList<>();

        try {
                sortedGFNYSEStockAlertList = gfAlertService.runPnlLogicForSpecifiedDays(noOfDays, cutOffPct, fileName, emailSubject, country)
                        .stream().sorted(comparing(GFinanceStockInfo::getStockRankIndex)).collect(toList());
                writeToFile( fileName, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sortedGFNYSEStockAlertList));
            } catch (Exception e) {
                LOGGER.error("Error - ",e);
            }

        try {
            StringBuilder dataBuffer = new StringBuilder("");
            sortedGFNYSEStockAlertList.forEach(gfNyse ->  createTableContents(dataBuffer, gfNyse));
            int retry = 3;
            while (!sendEmail(dataBuffer, new StringBuilder(emailSubject)) && --retry >= 0);
        }catch (Exception e){
            LOGGER.error("Error - ",e);
        }
    }

    public StringBuilder exeWinnerAndLosers() {

        currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant instantBefore = now();
        LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts" );
        final List<GFinanceStockInfo> gFinanceStockInfoList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(nyseUrlInfo).stream()
                .filter(x -> x.getMktCapRealValue() > 9900000000d).collect(toList())).stream().filter(x -> abs(x.getDailyPctChange().doubleValue()) >= 5d).collect(toList());
        gFinanceStockInfoList.stream().forEach(x -> x.setCountry(GF_NYSE));
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts"  );
        return generateDailyEmail(gFinanceStockInfoList, new StringBuilder("*** GF NYSE PnL Daily *** "));
    }

    private void generateAlertEmails(List<GFinanceStockInfo> populatedFtseList, SIDE side, StringBuilder subjectBuffer) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");

            generateHTMLContent(populatedFtseList, side, dataBuffer, subjectBuffer);
            int retry = 5;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
            if (--retry <= 0 && !sendEmail(dataBuffer, subjectBuffer)){
                ERROR_LOGGER.error("Failed to send email, GF Email error -> ");
            }
        } catch (Exception e) {
            ERROR_LOGGER.error( "<- , Error ->", e);
        }
    }

    private synchronized boolean sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        try {
            goSleep(90);
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
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genHtml\\" + fileName  + ".html"), data.getBytes());
                FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\genHtml\\" + fileName + ".html");
                helper.addAttachment(file.getFilename(), file);
                javaMailSender.send(message);
            }
        }catch (Exception e){
            goSleep(60);
            return false;
        }
        return true;
    }

    public void createTableContents(StringBuilder dataBuffer, GFinanceStockInfo x) {
        if ((x.get_52WeekLowPriceDiff().doubleValue() <= 1d)
                || (x.get_52WeekLowPrice().compareTo(x.getCurrentMarketPrice()) >= 0 )){
            dataBuffer.append("<tr style=\"background-color:#00ffff\">");
        }else if ((x.get_52WeekHighPriceDiff().doubleValue() <= 1d)
                || (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0 )){
            dataBuffer.append("<tr style=\"background-color:#A7D971\">");
        }else {
            dataBuffer.append("<tr>");
        }
        dataBuffer.append("<td>" + x.getStockRankIndex() + "</td>");
        dataBuffer.append("<td>" + x.getStockName() +
                START_BRACKET + x.getMktCapFriendyValue() + END_BRACKET + "</a></td>");
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, HALF_UP) + "</td>");
        if (compare(x.getDailyPctChange().doubleValue() , 5d) >= 0){
            dataBuffer.append("<td style=\"background-color:#A7D971\">" + x.getDailyPctChange()  + "</td>");
        } else if (compare(x.getDailyPctChange().doubleValue() , -5d) <= 0){
            dataBuffer.append("<td style=\"background-color:#cc0000\">" + x.getDailyPctChange()  + "</td>");
        }else {
            dataBuffer.append("<td>" + x.getDailyPctChange() + "</td>");
        }
        dataBuffer.append("<td>" + x.getP2e() + "</td>");
        dataBuffer.append("</tr>");
    }

    private void writeToDB(List<GFinanceStockInfo> gFinanceStockInfoList) {
        if (checkIfWeekend()){
            return;
        }
        try {
            gFinanceStockInfoList.forEach(x -> gFinanceStockInfoRepositary.save(x));
        }catch (Exception e){
            ERROR_LOGGER.error("GF DB inserts", e);
        }
    }
    private void writeGFPayloadToDB(List<GFinanceStockInfo> gFinanceStockInfoList, String country) {
        if (checkIfWeekend()){
            return;
        }
        try {
            googleFinanceStockDetailsRepositary.save(new GoogleFinanceStockDetails(Timestamp.from(Instant.now()), objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gFinanceStockInfoList), ""+Instant.now(), country));
        }catch (Exception e){
            ERROR_LOGGER.error("GF DB Payload inserts", e);
        }
    }

    private StringBuilder generateDailyEmail(List<GFinanceStockInfo> gFinanceStockInfoList, StringBuilder subject) {
        final StringBuilder dataBuffer = new StringBuilder("");
        try {
            gFinanceStockInfoList.stream().filter((x -> (x.get_52WeekLowPrice() != null
                            && x.get_52WeekLowPrice().compareTo(ZERO) != 0 &&
                            x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) != 0 &&
                            x.get_52WeekHighLowPriceDiff() != null &&
                            x.get_52WeekHighLowPriceDiff().compareTo(ZERO) != 0) ))
                    .forEach(x ->  createTableContents(dataBuffer, x));
            int retry = 5;
            while (!sendEmail(dataBuffer, subject) && --retry >= 0);
            if (--retry <= 0 && !sendEmail(dataBuffer, subject)){
                ERROR_LOGGER.error("Failed to send email, GF NYSE Email error -> ");
            }
        }catch (Exception e){
            ERROR_LOGGER.error("GF NYSE Email error -> ", e);
        }
        return dataBuffer;
    }

    private List<GFinanceStockInfo> sortByDailyPCTChange(List<GFinanceStockInfo> stockInfoList) {
        List<GFinanceStockInfo> stockInfoPctList = new ArrayList<>(stockInfoList);

        stockInfoPctList.sort(comparing(x -> {
            return abs(x.getDailyPctChange().doubleValue());
        },reverseOrder()));

        return stockInfoPctList;
    }

    private void generateHTMLContent(List<GFinanceStockInfo> populatedFtseList, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (populatedFtseList != null && populatedFtseList.size() >0){
            populatedFtseList = populatedFtseList.stream().distinct().collect(toList());

            populatedFtseList.stream().distinct().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighLowPriceDiff() != null) {
                    if (x.getStockRankIndex() != null && x.getStockRankIndex() <= 150
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(44)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Buy Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150
                            && x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(50)) > 0
                            && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                                    || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(5.0)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Buy Alert ***");
                        }
                        createTableContents(dataBuffer, x);
                    }

                    if (x.getStockRankIndex() != null && x.getStockRankIndex() <= 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Sell Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                    if (x.getStockRankIndex() != null && x.getStockRankIndex() > 150 &&
                            x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(45)) > 0
                            && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null &&
                            ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice()) >= 0
                                    || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(5)) <= 0))){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("*** GF NYSE Sell Alert***");
                        }
                        createTableContents(dataBuffer, x);
                    }
                }
            });
        }
    }

    private void generateDailyPnLEmail(List<GFinanceStockInfo> stockInfoList, String emailSubject) {
        stockInfoList.stream().forEach(gfStock -> {
            BigDecimal pnlDailyPct = BigDecimal.ONE;
            if (gfStock.getDailyPctChange().compareTo(ZERO) < 0
                    && gfStock.getDailyHighPrice().compareTo(ZERO) > 0){
                pnlDailyPct = ((gfStock.getDailyHighPrice().subtract(gfStock.getDailyLowPrice()))
                        .divide(gfStock.getDailyHighPrice(),2, HALF_UP).multiply(valueOf(100d)));
            }else if (gfStock.getDailyLowPrice().compareTo(ZERO) > 0){
                pnlDailyPct = ((gfStock.getDailyHighPrice().subtract(gfStock.getDailyLowPrice()))
                        .divide(gfStock.getDailyLowPrice(),2, HALF_UP).multiply(valueOf(100d)));
            }

            if (abs(pnlDailyPct.doubleValue()) >= 5d){
                if (gfStock.getDailyPctChange().compareTo(ZERO) < 0){
                    gfStock.setDailyPctPnLChange(valueOf(-1 * abs(pnlDailyPct.doubleValue())));
                }else {
                    gfStock.setDailyPctPnLChange(valueOf(abs(pnlDailyPct.doubleValue())));
                }
            }
        });
        stockInfoList.stream().forEach(stock -> {
            if (stock.getDailyPctPnLChange().compareTo(ZERO) > 0){
                stock.setDailyPctChange(stock.getDailyPctPnLChange());
            }
        });

        generateDailyEmail(sortByDailyPCTChange(stockInfoList.stream().filter(x -> {
            if (abs(x.getDailyPctChange().doubleValue()) >= 5d){
                return true;
            }
            return false;
        }).collect(toList())),
        new StringBuilder(emailSubject));
    }

    private void kickOffGF(String country, String emailSubject, Map<String, String> gfUrl, boolean pnlGenerate) {
        ExecutorService executorService = newSingleThreadExecutor();
        executorService.submit(() -> {
            currentThread().setPriority(Thread.MAX_PRIORITY);

            Instant instantBefore = now();
            LOGGER.info(" <-  Started " + this.getClass().getName() + "::" + new Object(){}.getClass().getEnclosingMethod().getName());
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(gfUrl);
            stockInfoList.stream().forEach(x -> x.setCountry(country));
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF " + emailSubject + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF "+ emailSubject + " Daily Data *** "));
            try {
                writeGFPayloadToDB(stockInfoList, country);
                writeToDB(stockInfoList);
                writeToFile(country, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }
            if (pnlGenerate){
                generateDailyPnLEmail(stockInfoList, "*** GF " + emailSubject + " PNL Daily Data *** ");
            }
            LOGGER.info(" <-  Ended " + this.getClass().getName() + "::" + new Object(){}.getClass().getEnclosingMethod().getName());
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded "+
                    this.getClass().getName() + "::" +   new Object(){}.getClass().getEnclosingMethod().getName());
        });
        executorService.shutdown();
    }

}
