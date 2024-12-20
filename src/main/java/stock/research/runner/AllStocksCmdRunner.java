package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.email.alerts.AllStocksEmailAlertMechanismService;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;

@Order(5)
@SpringBootApplication
public class AllStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(AllStocksCmdRunner.class);

    @Autowired
    private AllStocksEmailAlertMechanismService allStocksEmailAlertMechanismService;

    @Autowired
    private SensexStockResearchAlertMechanismService SensexStockResearchAlertMechanismService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {


/*
        SensexStockResearchAlertMechanismService.kickOffScreenerWeeklyPnLEmailAlerts();
        SensexStockResearchAlertMechanismService.kickOffScreenerBiWeeklyPnLEmailAlerts();
        SensexStockResearchAlertMechanismService.kickOffScreenerMONTHLYWeeklyPnLEmailAlerts();

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
*/
    }

}
