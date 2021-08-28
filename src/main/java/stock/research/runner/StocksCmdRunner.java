package stock.research.runner;

import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.core.io.ClassPathResource;
import stock.research.domain.StockInfo;
import stock.research.email.alerts.EmailAlertMechanismService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
public class StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(StocksCmdRunner.class);

    @Autowired
    private EmailAlertMechanismService emailAlertMechanismService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        long start = System.currentTimeMillis();
        LOGGER.info("##StocksCmdRunner.run::started##" );
        emailAlertMechanismService.kickOffWorld1000EmailAlerts();

        /*
        emailAlertMechanismService.kickOffAustriaEmailAlerts();
        emailAlertMechanismService.kickOffSwissEmailAlerts();

        emailAlertMechanismService.kickOffEUROEmailAlerts();
        emailAlertMechanismService.kickOffCanadaEmailAlerts();
        emailAlertMechanismService.kickOffDenmarkEmailAlerts();
        emailAlertMechanismService.kickOffFinlandEmailAlerts();
        emailAlertMechanismService.kickOffGermanyEmailAlerts();
        emailAlertMechanismService.kickOffNetherlandsEmailAlerts();
        emailAlertMechanismService.kickOffNorwayEmailAlerts();
        emailAlertMechanismService.kickOffWorld1000EmailAlerts();
        emailAlertMechanismService.kickOffSwedenEmailAlerts();
        emailAlertMechanismService.kickOffAustraliaEmailAlerts();
        LOGGER.info("##StocksCmdRunner.run::end##" + (System.currentTimeMillis() - start));
*/
        List<StockInfo> stockInfoList = objectMapper.readValue(new ClassPathResource("World1000detailedInfo.json").getInputStream(), new TypeReference<List<StockInfo>>(){});
        System.out.println(stockInfoList.stream().collect(Collectors.groupingBy(StockInfo::getCurrency)).keySet());
                //
    }

}
