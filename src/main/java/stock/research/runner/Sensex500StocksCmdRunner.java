package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;
import stock.research.service.SensexStockResearchService;

@Order(1)
@SpringBootApplication
public class Sensex500StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksCmdRunner.class);

    @Autowired
    private SensexStockResearchService sensexStockResearchService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SensexStockResearchAlertMechanismService sensexStockResearchAlertMechanismService;

    @Override
    public void run(String... args) throws Exception {
        sensexStockResearchAlertMechanismService.kickOffEmailAlerts();
    }

}
