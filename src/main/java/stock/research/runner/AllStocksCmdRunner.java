package stock.research.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import stock.research.domain.StockInfo;
import stock.research.email.alerts.AllStocksEmailAlertMechanismService;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class AllStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(AllStocksCmdRunner.class);

    @Autowired
    private AllStocksEmailAlertMechanismService allStocksEmailAlertMechanismService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {/*
        long start = System.currentTimeMillis();
        LOGGER.info("##AllStocksCmdRunner.run::started##" );
        allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
        allStocksEmailAlertMechanismService.kickOffAustriaEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffSwissEmailAlerts();

        allStocksEmailAlertMechanismService.kickOffEUROEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffCanadaEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffDenmarkEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffFinlandEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffGermanyEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffNetherlandsEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffNorwayEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
        allStocksEmailAlertMechanismService.kickOffSwedenEmailAlerts();
        allStocksEmailAlertMechanismService.kickOffAustraliaEmailAlerts();
        LOGGER.info("##AllStocksCmdRunner.run::end##" + (System.currentTimeMillis() - start));

        List<StockInfo> stockInfoList = objectMapper.readValue(new ClassPathResource("World1000detailedInfo.json").getInputStream(), new TypeReference<List<StockInfo>>(){});
        System.out.println(stockInfoList.stream().collect(Collectors.groupingBy(StockInfo::getCurrency)).keySet());

*/
    }

}
