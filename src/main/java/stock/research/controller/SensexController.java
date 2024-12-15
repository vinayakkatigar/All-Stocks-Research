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

    @Autowired
    private SensexStockResearchAlertMechanismService stockResearchAlertMechanismService;

    @CrossOrigin
    @GetMapping("/screener/sensex/alerts")
    public String sensexPnL(){
        return stockResearchAlertMechanismService.getSensexAlertsData();
    }

    @CrossOrigin
    @GetMapping("/screener/sensex/daily")
    public String sensexDaily(){
        return stockResearchAlertMechanismService.getSensexDailyData();
    }

    @CrossOrigin
    @GetMapping("/screener/sensex/yearlow")
    public String sensexYearLow(){
        return stockResearchAlertMechanismService.getYearLow();
    }

}