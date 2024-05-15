package stock.research.gfinance.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.domain.StockInfo;
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.gfinance.email.alerts.GFinanceEmailAlertService;
import stock.research.gfinance.utility.GFinanceNyseStockUtility;
import stock.research.service.NYSEStockResearchService;
import stock.research.service.NyseTop1000StockResearchService;
import stock.research.utility.NyseStockResearchUtility;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.math.BigDecimal.ZERO;
import static stock.research.utility.StockResearchUtility.createTableContents;

@RestController
public class GFController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GFController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private NYSEStockResearchService stockResearchService;

    @Autowired
    private NyseTop1000StockResearchService nyseTop1000StockResearchService;
    @Autowired
    private NyseEmailAlertMechanismService nyseEmailAlertMechanismService;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    GFinanceEmailAlertService gFinanceEmailAlertService;

    @GetMapping("/gf/nyse/wnl")
    public String WnL() throws Exception {
        return GFinanceNyseStockUtility.HTML_START + gFinanceEmailAlertService.exeWinnerAndLosers(false).toString() + GFinanceNyseStockUtility.HTML_END;
    }

    @GetMapping("/gf/nyse/daily")
    public String nyseDaily() throws Exception {
        return GFinanceNyseStockUtility.HTML_START + gFinanceEmailAlertService.kickOffNYSEGFDaily() + GFinanceNyseStockUtility.HTML_END;
    }
    @GetMapping("/gf/nyse/alerts")
    public String nyseAlerts() throws Exception {
        return GFinanceNyseStockUtility.HTML_START + gFinanceEmailAlertService.kickOffNYSEGFDailyAlerts() + GFinanceNyseStockUtility.HTML_END;
    }

}