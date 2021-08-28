package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.EuroNextStockInfo;
import stock.research.service.EuroNextStockResearchService;
import stock.research.utility.EuroNextStockResearchUtility;

import java.time.Instant;
import java.util.List;

//http://localhost:8084/euroNextStocks

@RestController
public class EuroNextStocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(EuroNextStocksController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EuroNextStockResearchService stockResearchService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/euroNextStocks")
    public String euroNextStocks(){
        try {
            LOGGER.info("EuroNextStocksController::euroNextStocks");

            List<EuroNextStockInfo> EuroNextStockInfoList = stockResearchService.getCacheEuroNextStockDetailedInfoList();
            StringBuilder dataBuffer = new StringBuilder("");
            EuroNextStockInfoList.stream().forEach(x-> {
                    EuroNextStockResearchUtility.createTableContents(dataBuffer, x);
            });
            String data = EuroNextStockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += EuroNextStockResearchUtility.HTML_END;
            data += "<title>EURO NEXT </title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", EURO NEXT - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }
}