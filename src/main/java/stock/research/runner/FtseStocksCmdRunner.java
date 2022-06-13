package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.email.alerts.FtseEmailAlertMechanismService;
import stock.research.service.FTSEStockResearchService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Order(3)
@SpringBootApplication
public class FtseStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(FtseStocksCmdRunner.class);

    @Autowired
    private FtseEmailAlertMechanismService ftseEmailAlertMechanismService;

    @Autowired
    private FTSEStockResearchService ftseStockResearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> c = () -> {   // Lambda Expression

            LOGGER.info("FtseStocksCmdRunner.run" );
            ftseEmailAlertMechanismService.kickOffFTSEEmailAlerts();

            ftseEmailAlertMechanismService.kickOffFTSE250YearlyGainerLoserEmailAlerts();
            ftseEmailAlertMechanismService.kickOffFTSE100YearlyGainerLoserEmailAlerts();
            return 0;
        };
        Future<Integer> future = executor.submit(c);
        try {
            future.get(); //wait for a thread to complete
        } catch(Exception e) {
            e.printStackTrace();
        }
        executor.shutdown();

    }

}
