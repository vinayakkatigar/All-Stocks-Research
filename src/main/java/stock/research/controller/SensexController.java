package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;

@RestController
public class SensexController {

    private static final Logger LOGGER = LoggerFactory.getLogger(SensexController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SensexStockResearchAlertMechanismService stockResearchAlertMechanismService;

    @CrossOrigin
    @GetMapping("/sensex/pnl")
    public String sensexPnL(){
        return stockResearchAlertMechanismService.getSensexPnlData();
    }

    @CrossOrigin
    @GetMapping("/sensex/daily")
    public String sensexDaily(){
        return stockResearchAlertMechanismService.getSensexDailyData();
    }

}