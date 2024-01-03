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
import java.time.temporal.ChronoUnit;

import static java.lang.Thread.sleep;

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
        Instant instantBefore = Instant.now();
        LOGGER.info(Instant.now() + " <-  Started GFinanceStocksCmdRunner::run" );

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        gFinanceEmailAlertService.kickOffGFinanceRefresh();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceNYSEEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFPortfolioEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFNSEEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFFTSEEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFASXEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFGermanyEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFWatchListEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceHongKongEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceSwitzerlandEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceEUROEmailAlerts();
        sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFNSEPortfolioEmailAlerts();

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.SECONDS)+ " <- Total time in mins, \nEnded GFinanceStocksCmdRunner::run" + Instant.now() );
    }

}
