package stock.research.gfinance.email.alerts;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
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
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.lang.Double.compare;
import static java.lang.Math.abs;
import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static java.time.temporal.ChronoUnit.MINUTES;
import static java.util.Arrays.stream;
import static java.util.Collections.reverse;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
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
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone( ZoneId.systemDefault() );

    public static final String GF_NYSE = "GF-NYSE";
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
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceNYSEEmailAlertService::kickOffGFinanceRefresh"  );
    }

    @Scheduled(cron = "0 10 0,4,9,18,22 ? * MON-SAT", zone = "GMT")
    public void kickOffGFWatchListEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFWatchListEmailAlerts" );

            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(watchListUrl);
            stockInfoList.stream().forEach(x -> x.setCountry("GF-WATCHLIST"));
            List<GFinanceStockInfo> stockInfoPctList = sortByDailyPCTChange(stockInfoList);

            generateAlertEmails(stockInfoPctList, SIDE.BUY, new StringBuilder("*** GF WatchList " + SIDE.BUY + " Alerts ***"));
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF WatchList Daily Data *** "));


            try {
                writeGFPayloadToDB(stockInfoList, "GF-WATCHLIST");
                writeToDB(stockInfoList);
                writeToFile("GF-WatchList", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFWatchListEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFWatchListEmailAlerts"  );

        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 20 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFASXEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFASXEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(asxUrl);
            stockInfoList.stream().forEach(x -> x.setCountry("GF-AUSTRALIA"));
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF ASX " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF ASX Daily Data *** "));

            try {
                writeGFPayloadToDB(stockInfoList, "GF-AUSTRALIA");
                writeToDB(stockInfoList);
                writeToFile("GF-ASX", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFASXEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFASXEmailAlerts"  );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 35 0,4,9,18 ? * MON-SAT", zone = "GMT")
    public void kickOffGFGermanyEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFGermanyEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(germanUrl);
            stockInfoList.stream().forEach(x -> x.setCountry("GF-GERMANY"));
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF Germany " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF Germany Daily Data *** "));
            try {
                writeGFPayloadToDB(stockInfoList, "GF-GERMANY");
                writeToDB(stockInfoList);
                writeToFile("GF-Germany", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFGermanyEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFGermanyEmailAlerts"  );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 40 3,9,12,15,21 ? * MON-SAT", zone = "GMT")
    public void kickOffGFFTSEEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFFTSEEmailAlerts" );
            final List<GFinanceStockInfo> stockInfoList = gFinanceStockService.getGFStockInfoList(ftseUrl);
            stockInfoList.stream().forEach(x -> x.setCountry("GF-FTSE"));
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(stockInfoList, SIDE.BUY, new StringBuilder("*** GF FTSE " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(stockInfoList, new StringBuilder("*** GF FTSE Daily Data *** "));
            try {
                writeGFPayloadToDB(stockInfoList, "GF-FTSE");
                writeToDB(stockInfoList);
                writeToFile("GF-FTSE", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(stockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFFTSEEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFFTSEEmailAlerts"  );
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 50 4,10,16,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNSEEmailAlerts() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFNSEEmailAlerts" );
            final List<GFinanceStockInfo> gfPortfolioList = gFinanceStockService.getGFStockInfoList(nseUrlInfo);
            gfPortfolioList.stream().forEach(x -> x.setCountry("GF-NSE"));
            gfPortfolioList.sort(Comparator.comparing(GFinanceStockInfo::getMktCapRealValue).reversed());
//        stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gfPortfolioList, SIDE.BUY, new StringBuilder("*** GF NSE " + SIDE.BUY + " Alerts ***"));
//        });
            generateDailyEmail(gfPortfolioList, new StringBuilder("*** GF NSE Daily Data *** "));
            try {
                writeGFPayloadToDB(gfPortfolioList, "GF-NSE");
                writeToDB(gfPortfolioList);
                writeToFile("GF-NSE", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gfPortfolioList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFNSEEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFNSEEmailAlerts"  );
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 5 5,11,17,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFPortfolioEmailAlerts() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGFPortfolioEmailAlerts::kickOffGFPortfolioEmailAlerts" );
            final List<GFinanceStockInfo> gfPortfolioList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(portfolioUrl));
            gfPortfolioList.stream().forEach(x -> x.setCountry("GF-PORTFOLIO"));
            stream(SIDE.values()).forEach(x -> {
                generateAlertEmails(gfPortfolioList, x, new StringBuilder("*** GF Portfolio " + x + " Alerts ***"));
            });

            generateDailyEmail(gfPortfolioList, new StringBuilder("*** GF Portfolio Daily Data ** "));
            try {
                writeGFPayloadToDB(gfPortfolioList, "GF-PORTFOLIO");
                writeToDB(gfPortfolioList);
                writeToFile("GF-Portfolio", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gfPortfolioList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(" <-  Ended kickOffGFPortfolioEmailAlerts::kickOffGFPortfolioEmailAlerts" );
            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGFPortfolioEmailAlerts"  );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 15 0,14,17,22,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceNYSEEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = gFinanceStockService.getGFStockInfoList(nyseUrlInfo);
            gFinanceStockInfoList.stream().forEach(x -> x.setCountry(GF_NYSE));
            /*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
            final StringBuilder subjectBuffer = new StringBuilder("");
            generateAlertEmails(gFinanceStockInfoList,SIDE.BUY, subjectBuffer);
            LOGGER.info(" <-  Ended kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceNYSEEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** GF NYSE Daily Data *** ");
            generateDailyEmail(gFinanceStockInfoList, subject);
            try {
                writeGFPayloadToDB(gFinanceStockInfoList, "GF-NYSE");
                writeToDB(gFinanceStockInfoList);
                writeToFile("GF-NYSE", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gFinanceStockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceNYSEEmailAlerts"  );
        });
        executorService.shutdown();
    }



    @Scheduled(cron = "0 15 6,12,18,21,23 ? * MON-SAT", zone = "GMT")
    public void kickOffGFNSEPortfolioEmailAlerts() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
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
    public void kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            exeWinnerAndLosers();
        });
        executorService.shutdown();

    }

    @Scheduled(cron = "0 11 3,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceHongKongEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceHongKongEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = gFinanceStockService.getGFStockInfoList(hongKongUrl);
            gFinanceStockInfoList.stream().forEach(x -> x.setCountry("GF-HONGKONG"));
            /*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/

            generateAlertEmails(gFinanceStockInfoList, SIDE.BUY, new StringBuilder("*** GF HongKong " + SIDE.BUY + " Alerts ***"));
            LOGGER.info(" <-  Ended kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceHongKongEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** GF HongKong Daily Data *** ");
            generateDailyEmail(gFinanceStockInfoList, subject);
            try {
                writeGFPayloadToDB(gFinanceStockInfoList, "GF-HONGKONG");
                writeToDB(gFinanceStockInfoList);
                writeToFile("GF-HongKong", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gFinanceStockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceHongKongEmailAlerts"  );
        });
        executorService.shutdown();

    }

    @Scheduled(cron = "0 31 10,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceSwitzerlandEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceSwitzerlandEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = gFinanceStockService.getGFStockInfoList(swissUrl);
            gFinanceStockInfoList.stream().forEach(x -> x.setCountry("GF-SWITZERLAND"));
            /*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
            generateAlertEmails(gFinanceStockInfoList, SIDE.BUY, new StringBuilder("*** GF Switzerland " + SIDE.BUY + " Alerts ***"));
            LOGGER.info(" <-  Ended kickOffGoogleFinanceNYSEEmailAlerts::kickOffGoogleFinanceSwitzerlandEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** GF Switzerland Daily Data *** ");
            generateDailyEmail(gFinanceStockInfoList, subject);
            try {
                writeGFPayloadToDB(gFinanceStockInfoList, "GF-SWITZERLAND");
                writeToDB(gFinanceStockInfoList);
                writeToFile("GF-Switzerland", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gFinanceStockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceSwitzerlandEmailAlerts"  );
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 31 10,16 ? * MON-SAT", zone = "GMT")
    public void kickOffGoogleFinanceEUROEmailAlerts() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
            Instant instantBefore = now();
            LOGGER.info(" <-  Started kickOffGoogleFinanceEUROEmailAlerts::kickOffGoogleFinanceEUROEmailAlerts" );
            final List<GFinanceStockInfo> gFinanceStockInfoList = gFinanceStockService.getGFStockInfoList(euroUrl);
            gFinanceStockInfoList.stream().forEach(x -> x.setCountry("GF-EURO"));
            /*
        Arrays.stream(SIDE.values()).forEach(x -> {
            generateAlertEmails(gFinanceNYSEStockInfoList,x, StockCategory.LARGE_CAP);
        });
*/
            generateAlertEmails(gFinanceStockInfoList, SIDE.BUY, new StringBuilder("*** GF EURO " + SIDE.BUY + " Alerts ***"));
            LOGGER.info(" <-  Ended kickOffGoogleFinanceEUROEmailAlerts::kickOffGoogleFinanceEUROEmailAlerts" );

            StringBuilder subject = new StringBuilder("*** GF EURO Daily Data *** ");
            generateDailyEmail(gFinanceStockInfoList, subject);
            try {
                writeGFPayloadToDB(gFinanceStockInfoList, "GF-EURO");
                writeToDB(gFinanceStockInfoList);
                writeToFile("GF-EURO", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gFinanceStockInfoList));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error -", e);
            }

            LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceEUROEmailAlerts"  );
        });
        executorService.shutdown();
    }


    @Scheduled(cron = "0 17 1,20 ? * *", zone = "GMT")
    public void kickOffScreenerWeeklyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(7, 20d, "GF-NYSE_PNL_WEEKLY" , "** GF NYSE Weekly PNL Data ** ");
        });
        executorService.shutdown();
    }

    @Scheduled(cron = "0 47 2,20 ? * *", zone = "GMT")
    public void kickOffScreenerMonthlyPnLEmailAlerts() throws JsonProcessingException {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            runPnlLogicForSpecifiedDays(30, 30d, "GF-NYSE_PNL_MONTHLY" , "** GF NYSE Monthly PNL Data ** ");
        });
        executorService.shutdown();
    }



    private void runPnlLogicForSpecifiedDays(int noOfDays, double cutOffPct, String fileName, String emailSubject) {
        List<GoogleFinanceStockDetails> gfNYSEStockInfoList = new CopyOnWriteArrayList<>();
        googleFinanceStockDetailsRepositary.findByCountryIgnoreCase(GF_NYSE).forEach(gfNYSEStockInfoList::add);

        List<GoogleFinanceStockDetails> gfNYSEStockInfoWeeklyList = gfNYSEStockInfoList.stream().filter(x -> {
            long difInMS = from(now()).getTime() - x.getStockTS().getTime();
            Long diffDays = difInMS / (1000  * 60 * 60 * 24);
            if (diffDays  <= noOfDays && LocalDateTime.ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                    && LocalDateTime.ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SUNDAY){
                return true;
            }else {
                return false;
            }
        }).collect(toList());

        gfNYSEStockInfoWeeklyList.stream().sorted(comparing(GoogleFinanceStockDetails::getStockTS).reversed());

        final List<GFinanceStockInfo> gfNYSEStockList = new CopyOnWriteArrayList<>();
        final List<GFinanceStockInfo> gfNYSEAlertList = new CopyOnWriteArrayList<>();
        List<GFinanceStockInfo> sortedGFNYSEStockAlertList = new ArrayList<>();
        gfNYSEStockInfoWeeklyList.stream().forEach(x ->{
            try {
                x.setGoogleFinanceStocksPayload(x.getGoogleFinanceStocksPayload().replaceAll("timestamp", "quoteInstant"));
            }catch (Exception e){
                LOGGER.error("Error - ",e);
            }
            try {
                gfNYSEStockList.addAll( objectMapper.readValue(x.getGoogleFinanceStocksPayload(), new TypeReference<List<GFinanceStockInfo>>(){}) );
            } catch (Exception e) {
                LOGGER.error("Error - ",e);
            }
        });

        List<GFinanceStockInfo> resultGFNYSEStockList = new CopyOnWriteArrayList<>(gfNYSEStockList);
        resultGFNYSEStockList.forEach(x -> {
            x.setStockInstant(Instant.parse(x.getQuoteInstant()));
        });

        resultGFNYSEStockList = resultGFNYSEStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed()).collect(toList());
        final List<GFinanceStockInfo> pnlForDaysData = new CopyOnWriteArrayList<>();

        if (resultGFNYSEStockList != null && resultGFNYSEStockList.size() > 0) {
            Map<String, List<GFinanceStockInfo>> gfNyseStockInfoWeeklyMap = resultGFNYSEStockList.stream().filter(Objects::nonNull)
                    .filter(x -> x.getStockName() != null)
                    .collect(groupingBy(GFinanceStockInfo::getStockName));

            gfNyseStockInfoWeeklyMap.forEach((key, weeklyPnlGFNyseStockList) -> {
                weeklyPnlGFNyseStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());
                weeklyPnlGFNyseStockList = weeklyPnlGFNyseStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed()).collect(toList());

                final List<GFinanceStockInfo> pnlForDays = new CopyOnWriteArrayList<>();
                Instant instant = Instant.now();

                Map<String, GFinanceStockInfo> gfNyseStockInfoMap = new LinkedHashMap<>();

                for (int i = 0; i < noOfDays; i++) {
                    addAndRemoveSpecifiedDates(weeklyPnlGFNyseStockList, instant, i, gfNyseStockInfoMap);
                }
                gfNyseStockInfoMap.forEach((k,v) -> {
                    pnlForDays.add(v);
                    pnlForDaysData.add(v);
                });

                StringBuffer changePct = new StringBuffer("");
                final BigDecimal[] pct = {BigDecimal.ZERO};
                pnlForDays.stream().filter(Objects::nonNull).forEach(x -> {
                    changePct.append(" , " + x.getDailyPctChange() );
                    pct[0] = pct[0].add(x.getDailyPctChange());
                });

                pnlForDays.stream().filter(Objects::nonNull).sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());
                pnlForDaysData.stream().filter(Objects::nonNull).sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());

                if ((abs(pct[0].doubleValue()) >= cutOffPct )){
                    if (pct[0].doubleValue() < 0d){
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#990033;display:inline;\">" + pct[0] +"</h4> )");
                    }else {
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#00e6e6;display:inline;\">" + pct[0] +"</h4> )");
                    }
                    gfNYSEAlertList.add(pnlForDays.get(0));
                }
            });

            try {
                sortedGFNYSEStockAlertList = gfNYSEAlertList.stream().sorted(comparing(GFinanceStockInfo::getStockRankIndex)).collect(toList());
                writeToFile( fileName, objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(gfNYSEAlertList));
                writeToFile( fileName + "_ALL_STOCKS_DATA", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pnlForDaysData));
            } catch (Exception e) {
                LOGGER.error("Error - ",e);
            }
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


    private void addAndRemoveSpecifiedDates(List<GFinanceStockInfo> weeklyPnlGFNyseStockList,
                                            Instant instant, int i, Map<String, GFinanceStockInfo> gfNyseStockInfoMap) {
        for (GFinanceStockInfo x : weeklyPnlGFNyseStockList) {
            if ((Duration.between(x.getStockInstant(), instant).toDays() >= i)
                    && (Duration.between(x.getStockInstant(), instant).toDays() < (i + 1))) {
                if(LocalDateTime.ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                        && LocalDateTime.ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek()!= DayOfWeek.SUNDAY){
                    String key = dateTimeFormatter.format(x.getStockInstant());
                    if (!gfNyseStockInfoMap.containsKey(key)){
                        gfNyseStockInfoMap.put(key, x);
                    }else {
                        GFinanceStockInfo current = gfNyseStockInfoMap.get(key);
                        if (current != null && x.getStockInstant().isAfter(current.getStockInstant())){
                            gfNyseStockInfoMap.put(key, x);
                        }
                    }
                }
            }
        }
    }

    public StringBuilder exeWinnerAndLosers() {

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        Instant instantBefore = now();
        LOGGER.info(" <-  Started kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts" );
        final List<GFinanceStockInfo> gFinanceStockInfoList = sortByDailyPCTChange(gFinanceStockService.getGFStockInfoList(nyseUrlInfo).stream().filter(x -> x.getMktCapRealValue() > 9900000000d).collect(toList())).stream().filter(x -> Math.abs(x.getDailyPctChange().doubleValue()) >= 5d).collect(toList());
        gFinanceStockInfoList.stream().forEach(x -> x.setCountry("GF-NYSE"));
        LOGGER.info(instantBefore.until(now(), MINUTES)+ " <- Total time in mins, \nEnded GFinanceEmailAlertService::kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts"  );
        return generateDailyEmail(gFinanceStockInfoList, new StringBuilder("*** GF NYSE Daily Winners & Losers *** "));
    }

    private void generateAlertEmails(List<GFinanceStockInfo> populatedFtseList, SIDE side, StringBuilder subjectBuffer) {
        try {
            StringBuilder dataBuffer = new StringBuilder("");

            generateHTMLContent(populatedFtseList, side, dataBuffer, subjectBuffer);
            int retry = 5;
            while (!sendEmail(dataBuffer, subjectBuffer) && --retry >= 0);
            if (--retry <= 0 && !sendEmail(dataBuffer, subjectBuffer)){
                ERROR_LOGGER.error("Failed to send email, GF NYSE Email error -> ");
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
        dataBuffer.append("<td>" + (x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekLowPrice() + "</td>");
        dataBuffer.append("<td>" + x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
        dataBuffer.append("<td>" + x.getCurrentMarketPrice() + "</td>");
        dataBuffer.append("<td>" + (x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
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
                            && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) != 0 &&
                            x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) != 0 &&
                            x.get_52WeekHighLowPriceDiff() != null &&
                            x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO) != 0) ))
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

        stockInfoPctList.sort(Comparator.comparing(x -> {
            return Math.abs(x.getDailyPctChange().doubleValue());
        }));

        reverse(stockInfoPctList);
        return stockInfoPctList;
    }

    public Map<String, String> getNsePortfolioUrl() {
        return nsePortfolioUrl;
    }

    public void setNsePortfolioUrl(Map<String, String> nsePortfolioUrl) {
        this.nsePortfolioUrl = nsePortfolioUrl;
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
}
