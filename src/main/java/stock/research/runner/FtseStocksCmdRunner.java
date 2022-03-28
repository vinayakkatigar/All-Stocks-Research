package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import stock.research.email.alerts.FtseEmailAlertMechanismService;
@Order(3)
@SpringBootApplication
public class FtseStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(FtseStocksCmdRunner.class);

    @Autowired
    private FtseEmailAlertMechanismService ftseEmailAlertMechanismService;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {

/*
        LOGGER.info("FtseStocksCmdRunner.run" );
        ftseEmailAlertMechanismService.kickOffEmailAlerts();
*/

    }

}
