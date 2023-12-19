package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.SensexStockInfo;
import stock.research.service.ScreenerSensexStockResearchService;
import stock.research.utility.SensexStockResearchUtility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static stock.research.utility.SensexStockResearchUtility.generateTableContents;
//http://localhost:8090/sensexAllStocks
//http://localhost:8090/sensexLargeCap
//http://localhost:8090/sensexMidCap
//http://localhost:8090/sensexSmallCap

@RestController
public class Sensex500StocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ScreenerSensexStockResearchService sensexStockResearchService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/sensexAllStocks")
    public String sensexAllStocks(){
        try {
            LOGGER.info("Sensex500StocksController::sensexAllStocks");
            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.populateStocksAttributes();
            StringBuilder dataBuffer = new StringBuilder("");
            populatedSensexList.stream().forEach( x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ){
                        generateTableContents(dataBuffer, x);
                }
            });
            String data = SensexStockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += SensexStockResearchUtility.HTML_END;
            data = data + "<title>AllStocks Sensex </title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", sensexAllCap - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

}