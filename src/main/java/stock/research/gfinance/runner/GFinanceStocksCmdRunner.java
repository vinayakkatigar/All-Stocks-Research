package stock.research.gfinance.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.gfinance.email.alerts.GFinanceEmailAlertService;

import java.time.Instant;

import static java.lang.Thread.sleep;
import static java.time.temporal.ChronoUnit.SECONDS;

@Order(1)
@SpringBootApplication
public class GFinanceStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceStocksCmdRunner.class);

    @Autowired
    private GFinanceEmailAlertService gFinanceEmailAlertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        /*{
            Instant instantBefore = Instant.now();
            LOGGER.info(" <-  Started GFinanceStocksCmdRunner::run");

            Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

            gFinanceEmailAlertService.kickOffGFinanceRefresh();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFNYSEEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFWatchListEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFPortfolioEmailAlerts();
            sleep(70 * 1000);
            gFinanceEmailAlertService.kickOffGFNSEEmailAlerts();
            sleep(70 * 1000);
            gFinanceEmailAlertService.kickOffGFFTSEEUROEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFNYSEDailyPnLEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFHongKongEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFASXEmailAlerts();
            sleep(70 * 1000);
            gFinanceEmailAlertService.kickOffGFNSEPortfolioEmailAlerts();

            gFinanceEmailAlertService.kickOffGFPortfolioWeeklyPnLEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFWatchListWeeklyPnLEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffScreenerWeeklyPnLEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffScreenerMonthlyPnLEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFSouthKoreaEmailAlerts();
            sleep(70 * 1000);

            gFinanceEmailAlertService.kickOffGFIndonesiaEmailAlerts();
            sleep(70 * 1000);

            LOGGER.info(instantBefore.until(Instant.now(), SECONDS) + " <- Total time in mins, " +
                    "\nEnded GFinanceStocksCmdRunner::run" + Instant.now());
        }*/
    }

}
