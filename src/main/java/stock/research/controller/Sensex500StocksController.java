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
import stock.research.service.SensexStockResearchService;
import stock.research.utility.SensexStockResearchUtility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.LARGE_CAP;
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
    private SensexStockResearchService sensexStockResearchService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/sensexAllStocks")
    public String sensexAllStocks(){
        try {
            LOGGER.info("Sensex500StocksController::sensexAllStocks");
            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.getCacheSensexStockInfosList();
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

    @RequestMapping("/sensexLargeCap")
    public String sensexLargeCap(){
        try {
            LOGGER.info("Sensex500StocksController::sensexLargeCap");

            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.getCacheSensexStockInfosList()
                    .parallelStream()
                    .filter(x -> x.getStockRankIndex() <= LARGE_CAP).collect(toList());
            //Filter large Cap with Y/L diff of 75 or more
            populatedSensexList = populatedSensexList.stream()
                    .filter(x -> x.getStockRankIndex() <= LARGE_CAP &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(Collectors.toList());

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
            data = data + "<title>LargeCap Sensex </title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", sensexLargeCap - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

    @RequestMapping("/sensexMidCap")
    public String sensexMidCap(){
        try {
            LOGGER.info("Sensex500StocksController::sensexMidCap");

            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.getCacheSensexStockInfosList()
                    .parallelStream()
                    .filter(x -> x.getStockRankIndex() > 150 && x.getStockRankIndex() <= 300).collect(toList());

            //Filter Mid Cap with Y/L diff of 100 or more
            populatedSensexList = populatedSensexList.stream()
                    .filter(x -> x.getStockRankIndex() > LARGE_CAP &&  x.getStockRankIndex() <= 300 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0).collect(Collectors.toList());

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
            data = data + "<title>MidCap Sensex </title>";
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", sensexMidCap - , Error ->", e);
            e.printStackTrace();
        }
        return "Failed!!";
    }

    @RequestMapping("/sensexSmallCap")
    public String sensexSmallCap(){
        try {
            LOGGER.info("Sensex500StocksController::sensexSmallCap");
            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.getCacheSensexStockInfosList()
                    .parallelStream()
                    .filter(x -> x.getStockRankIndex() > 300 && x.getStockRankIndex() <= 950).collect(toList());

            //Filter Small Cap with Y/L diff of 125 or more
            populatedSensexList = populatedSensexList.stream()
                    .filter(x -> x.getStockRankIndex() > 300 &&  x.getStockRankIndex() <= 500 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(125)) > 0).collect(Collectors.toList());

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
            data = data + "<title>SmallCap Sensex </title>";
            return data;

        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", sensexSmallCap - , Error ->", e);
            e.printStackTrace();
        }
        return "Failed!!";
    }

}