package stock.research.yfinance.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.yfinance.email.alerts.YFEmailAlertService;

@Order(6)
@SpringBootApplication
public class YFStocksCmdRunner  implements CommandLineRunner {
    @Autowired
    private YFEmailAlertService yfEmailAlertService;
    @Override
    public void run(String... args) throws Exception {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        yfEmailAlertService.kickOffYFNYSEEmailAlerts();
    }
}
