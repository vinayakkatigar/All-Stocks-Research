package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;
import stock.research.email.alerts.EuroNextEmailAlertMechanismService;
import stock.research.utility.EuroNextStockUrlExtractor;
@Order(4)
@SpringBootApplication
public class EuroNextStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(EuroNextStocksCmdRunner.class);

    @Autowired
    private EuroNextEmailAlertMechanismService euroNextEmailAlertMechanismService;

    @Autowired
    private stock.research.service.EuroNextStockResearchService EuroNextStockResearchService;
    @Autowired
    private EuroNextStockUrlExtractor euroNextStockUrlExtractor;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {

        LOGGER.info("EuroNextStocksCmdRunner.run");
        euroNextEmailAlertMechanismService.kickOffEmailAlerts();

    }
}
