package stock.research.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.research.service.InteractiveInvestorsResearchService;

import java.time.Instant;

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