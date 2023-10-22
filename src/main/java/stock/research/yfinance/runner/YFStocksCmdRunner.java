package stock.research.yfinance.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.yfinance.email.alerts.YFEmailAlertService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Order(1)
@SpringBootApplication
public class YFStocksCmdRunner  implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(YFStocksCmdRunner.class);

    @Autowired
    private YFEmailAlertService yfEmailAlertService;
    @Override
    public void run(String... args) throws Exception {
        Instant instantBefore = Instant.now();
        LOGGER.info(Instant.now() + " <-  Started YFStocksCmdRunner::run" );

        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        yfEmailAlertService.kickOffYFNYSEEmailAlerts();
//        yfEmailAlertService.kickOffYFChinaEmailAlerts();
        yfEmailAlertService.kickOffYFROWEmailAlerts();

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.SECONDS)+ " <- Total time in mins, Ended YFStocksCmdRunner::run" + Instant.now() );
    }
}
