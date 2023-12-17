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
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;
import stock.research.entity.repo.SensexStockDetailsRepositary;
import stock.research.service.InteractiveInvestorsResearchService;
import stock.research.service.ScreenerSensexStockResearchService;
import stock.research.service.SensexStockResearchService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Order(6)
@SpringBootApplication
public class Sensex500StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksCmdRunner.class);

    @Autowired
    private NyseEmailAlertMechanismService nyseEmailAlertMechanismService;

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
    @Autowired
    private NyseEmailAlertMechanismService startUpNYSEStockResearchService;

    @Autowired
    private ScreenerSensexStockResearchService screenerSensexStockResearchService;

    @Autowired
    private SensexStockDetailsRepositary sensexStockRepositary;


    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
            sensexStockResearchAlertMechanismService.kickOffEmailAlerts_Cron();
        });
        executorService.shutdown();
    }

}
