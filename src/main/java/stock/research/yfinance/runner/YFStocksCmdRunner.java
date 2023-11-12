package stock.research.yfinance.runner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.utility.StockUtility;
import stock.research.yfinance.email.alerts.YFEmailAlertService;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.Thread.currentThread;
import static stock.research.utility.StockUtility.goSleep;

@Order(5)
@SpringBootApplication
public class YFStocksCmdRunner  implements CommandLineRunner {
    private static final Logger LOGGER = LoggerFactory.getLogger(YFStocksCmdRunner.class);

    @Autowired
    private YFEmailAlertService yfEmailAlertService;
    @Override
    public void run(String... args) throws Exception {
        Instant instantBefore = Instant.now();
        LOGGER.info(Instant.now() + " <-  Started YFStocksCmdRunner::run" );

        currentThread().setPriority(Thread.MAX_PRIORITY);

        goSleep(360);
        yfEmailAlertService.kickOffYFNYSEEmailAlerts();

        goSleep(120);
        yfEmailAlertService.kickOffYFROWEmailAlerts();
//        yfEmailAlertService.kickOffYFChinaEmailAlerts();

        LOGGER.info(instantBefore.until(Instant.now(), ChronoUnit.SECONDS)+ " <- Total time in mins, Ended YFStocksCmdRunner::run" + Instant.now() );
    }
}
