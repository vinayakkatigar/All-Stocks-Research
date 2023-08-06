package stock.research.gfinance.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.email.alerts.FtseEmailAlertMechanismService;
import stock.research.gfinance.email.alerts.GFinanceNYSEEmailAlertService;
import stock.research.service.FTSEStockResearchService;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Order(5)
@SpringBootApplication
public class GFinanceNYSEStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceNYSEStocksCmdRunner.class);

    @Autowired
    private GFinanceNYSEEmailAlertService gFinanceNYSEEmailAlertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        gFinanceNYSEEmailAlertService.kickOffGFinanceRefresh();
        gFinanceNYSEEmailAlertService.kickOffGoogleFinanceNYSEEmailAlerts();
        gFinanceNYSEEmailAlertService.kickOffGFPortfolioEmailAlerts();
        gFinanceNYSEEmailAlertService.kickOffGFNSEEmailAlerts();

    }

}
