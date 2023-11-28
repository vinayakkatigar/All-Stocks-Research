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
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceNYSEEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFPortfolioEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFNSEEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFFTSEEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFASXEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFGermanyEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGFWatchListEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceNYSEDailyWinnersLosersEmailAlerts();
        Thread.sleep(70 * 1000);
        gFinanceEmailAlertService.kickOffGoogleFinanceHongKongEmailAlerts();

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.SECONDS)+ " <- Total time in mins, Ended GFinanceStocksCmdRunner::run" + Instant.now() );
    }

}
