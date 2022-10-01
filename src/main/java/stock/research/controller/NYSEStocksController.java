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
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.service.NYSEStockResearchService;
import stock.research.service.NyseTop1000StockResearchService;
import stock.research.utility.NyseStockResearchUtility;

import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.math.BigDecimal.ZERO;
import static stock.research.utility.NyseStockResearchUtility.HTML_END;
import static stock.research.utility.NyseStockResearchUtility.HTML_START;
import static stock.research.utility.StockResearchUtility.createTableContents;

@RestController
public class NYSEStocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(NYSEStocksController.class);
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

    @RequestMapping("/kickNyse")
    public String kickNyse() throws Exception {

       ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            nyseEmailAlertMechanismService.kickOffEmailAlerts();
        });
        Thread.sleep(1000 * 60 * 1);
        return "kickNyse";
    }
    @RequestMapping("/nyse1000")
    public String nyse1000(){
        try {
            LOGGER.info("NYSEStocksController::nyse1000");

            List<StockInfo> stockInfoList = nyseTop1000StockResearchService.getCacheNyseStockInfoList();
            StringBuilder dataBuffer = new StringBuilder("");
            stockInfoList.stream()
                    .forEach( x-> {
                        if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) > 0 ){
                            createTableContents(dataBuffer, x);
                }
            });
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;
            data += "<title>NYSE Top 100</title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", nyse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }


    @RequestMapping("/nyse")
    public String nyse(){
        try {
            LOGGER.info("NYSEStocksController::nyseTop100");

            List<NyseStockInfo> nyseStockInfoList = stockResearchService.getCacheNYSEStockDetailedInfoList();
            StringBuilder dataBuffer = new StringBuilder("");
            nyseStockInfoList.stream()
                    .forEach( x-> {
                        if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) > 0 ){
                            NyseStockResearchUtility.createTableContents(dataBuffer, x);
                }
            });
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;
            data += "<title>NYSE Top 100</title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", nyse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

    @RequestMapping("/nyseTop100")
    public String nyseTop100(){
        try {
            LOGGER.info("NYSEStocksController::nyseTop100");

            List<NyseStockInfo> nyseStockInfoList = stockResearchService.getCacheNYSEStockDetailedInfoList();
            StringBuilder dataBuffer = new StringBuilder("");
            nyseStockInfoList.stream().filter(i -> i.getStockRankIndex() <= 125).forEach( x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) > 0 ){
//                    LOGGER.info(x.toString());
                    NyseStockResearchUtility.createTableContents(dataBuffer, x);
//                    LOGGER.info("$After$" + x);
                }
            });
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;
            data += "<title>NYSE Top 100</title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", nyse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }
    @RequestMapping("/nyseMid250")
    public String nyseMid250(){
        try {
            LOGGER.info("NYSEStocksController::nyseMid250");
            List<NyseStockInfo> nyseStockInfoList = stockResearchService.getCacheNYSEStockDetailedInfoList();
            StringBuilder dataBuffer = new StringBuilder("");
            nyseStockInfoList.stream().filter(i -> i.getStockRankIndex() > 125).forEach( x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(ZERO) > 0 ){
//                    LOGGER.info(x.toString());
                    NyseStockResearchUtility.createTableContents(dataBuffer, x);
//                    LOGGER.info("$After$" + x);
                }
            });
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;
            data += "<title>NYSE Mid 250</title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", nyse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }
}