package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.service.NYSEStockResearchService;

import java.util.List;

@Order(1)
@SpringBootApplication
public class NyseStocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(NyseStocksCmdRunner.class);

    @Autowired
    private NyseEmailAlertMechanismService nyseEmailAlertMechanismService;

    @Autowired
    private NYSEStockResearchService nyseStockResearchService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RestTemplate restTemplate;

    @Override
    public void run(String... args) throws Exception {
        LOGGER.info("NyseStocksCmdRunner.run" );
        List<NyseStockInfo> stocksUrlMap = nyseStockResearchService.populateNYSEStockDetailedInfo();
    }

}
