package stock.research.gfinance.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.gfinance.email.alerts.GFinanceEmailAlertService;

@Order(5)
@SpringBootApplication
public class GFinanceStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(GFinanceStocksCmdRunner.class);

    @Autowired
    private GFinanceEmailAlertService gFinanceEmailAlertService;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        gFinanceEmailAlertService.kickOffGFinanceRefresh();
        gFinanceEmailAlertService.kickOffGoogleFinanceNYSEEmailAlerts();
        gFinanceEmailAlertService.kickOffGFPortfolioEmailAlerts();
        gFinanceEmailAlertService.kickOffGFNSEEmailAlerts();
        gFinanceEmailAlertService.kickOffGFFTSEEmailAlerts();
        gFinanceEmailAlertService.kickOffGFASXEmailAlerts();

    }

}
