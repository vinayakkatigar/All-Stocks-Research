package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.email.alerts.AllStocksEmailAlertMechanismService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
@Order(5)
@SpringBootApplication
public class AllStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(AllStocksCmdRunner.class);

    @Autowired
    private AllStocksEmailAlertMechanismService allStocksEmailAlertMechanismService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> c = () -> {   // Lambda Expression

            long start = System.currentTimeMillis();
            LOGGER.info("##AllStocksCmdRunner.run::started##" );
            allStocksEmailAlertMechanismService.kickOffNyseTop1000();
            allStocksEmailAlertMechanismService.kickOffIndiaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffItalyEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSpainEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffJapanEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffBelgiumEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffFranceEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSouthKoreaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSingaporeEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffHongKongEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
            allStocksEmailAlertMechanismService.kickOffAustriaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSwissEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSwedenEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffEUROEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffCanadaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffDenmarkEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffFinlandEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffGermanyEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffNetherlandsEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffNorwayEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
            allStocksEmailAlertMechanismService.kickOffAustraliaEmailAlerts();
            LOGGER.info("##AllStocksCmdRunner.run::end##" + (System.currentTimeMillis() - start));
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
