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
import stock.research.service.InteractiveInvestorsResearchService;
import stock.research.service.SensexStockResearchService;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Order(1)
@SpringBootApplication
public class Sensex500StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksCmdRunner.class);

    @Autowired
    private SensexStockResearchService sensexStockResearchService;

    @Autowired
    private InteractiveInvestorsResearchService interactiveInvestorsResearchService;
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
        ExecutorService executor = Executors.newSingleThreadExecutor();
        Callable<Integer> c = () -> {   // Lambda Expression
            LOGGER.info("Started Sensex500StocksCmdRunner::run" );
            sensexStockResearchAlertMechanismService.kickOffEmailAlerts();
            LOGGER.info("End Sensex500StocksCmdRunner::run" );
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
