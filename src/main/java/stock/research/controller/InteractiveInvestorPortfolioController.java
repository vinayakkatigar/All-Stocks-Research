package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.domain.StockInfo;
import stock.research.service.InteractiveInvestorsResearchService;
import stock.research.service.NYSEStockResearchService;
import stock.research.service.NyseTop1000StockResearchService;
import stock.research.utility.NyseStockResearchUtility;

import java.time.Instant;
import java.util.List;

import static java.math.BigDecimal.ZERO;
import static stock.research.utility.NyseStockResearchUtility.HTML_END;
import static stock.research.utility.NyseStockResearchUtility.HTML_START;
import static stock.research.utility.StockResearchUtility.createTableContents;

@RestController
public class InteractiveInvestorPortfolioController {

    private static final Logger LOGGER = LoggerFactory.getLogger(InteractiveInvestorPortfolioController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private InteractiveInvestorsResearchService interactiveInvestorsResearchService;

    @RequestMapping("/portfolio")
    public String portfolio(){
        try {
            LOGGER.info("InteractiveInvestorPortfolioController::monitorPortfolioStock");

            return interactiveInvestorsResearchService.monitorPortfolioStock();
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", nyse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

}