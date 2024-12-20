package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.service.NYSEStockResearchService;
import stock.research.service.NyseTop1000StockResearchService;

@Order(7)
@SpringBootApplication
public class NyseStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(NyseStocksCmdRunner.class);

    @Autowired
    private NyseEmailAlertMechanismService nyseEmailAlertMechanismService;

    @Autowired
    private NyseTop1000StockResearchService nyseTop1000StockResearchService;

    @Autowired
    private NYSEStockResearchService nyseStockResearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
/*
        startUpNYSEStockResearchService.startUpKickOffEmailAlerts();
        nyseTop1000StockResearchService.populateStockDetailedInfo("NYSE_1000", NyseStockResearchUtility.NYSE_1000_URL, NyseStockResearchUtility.NYSE_1000_CNT);
        LOGGER.info("NyseStocksCmdRunner.run" );
*/
    }

}
