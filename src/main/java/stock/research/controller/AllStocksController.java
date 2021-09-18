package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.StockInfo;
import stock.research.service.AllStockResearchService;
import stock.research.utility.StockResearchUtility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@RestController
public class AllStocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllStocksController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AllStockResearchService allStockResearchService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/SouthKorea")
    public String SouthKorea(){
        List<StockInfo> stockInfoList = getStockInfoListFor("SouthKorea");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/HongKong")
    public String HongKong(){
        List<StockInfo> stockInfoList = getStockInfoListFor("HongKong");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Singapore")
    public String Singapore(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Singapore");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Sweden")
    public String Sweden(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Sweden");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/World1000")
    public String World1000(){
        List<StockInfo> stockInfoList = getStockInfoListFor("World1000");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Norway")
    public String Norway(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Norway");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/germany")
    public String germany(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Germany");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Netherlands")
    public String Netherlands(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Netherlands");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/swiss")
    public String swiss(){
        List<StockInfo> stockInfoList = getStockInfoListFor("swiss");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/finland")
    public String finland(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Finland");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/austria")
    public String austria(){
        List<StockInfo> stockInfoList = getStockInfoListFor("austria");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/canada")
    public String canada(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Canada");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/denmark")
    public String denmark(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Denmark");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/australia")
    public String australia(){
        List<StockInfo> stockInfoList = getStockInfoListFor("australia");
        return generateHtml(stockInfoList);    }

    @RequestMapping("/euro")
    public String euro(){
        List<StockInfo> stockInfoList = getStockInfoListFor("euro");
        return generateHtml(stockInfoList);    }

    private List<StockInfo>  getStockInfoListFor(String mkt){
        if (mkt != null && "SouthKorea".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheSouthKoreaStockInfoList();
        }
        if (mkt != null && "HongKong".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheHongKongStockInfoList();
        }
        if (mkt != null && "Singapore".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheSingaporeStockInfoList();
        }
        if (mkt != null && "Sweden".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheSwedenStockInfoList();
        }
        if (mkt != null && "World1000".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheWorld1000StockInfoList();
        }
        if (mkt != null && "Norway".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheNorwayStockInfoList();
        }
        if (mkt != null && "Netherlands".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheNetherlandsStockInfoList();
        }
        if (mkt != null && "Germany".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheGermanyStockInfoList();
        }
        if (mkt != null && "Finland".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheFinlandStockInfoList();
        }
        if (mkt != null && "Denmark".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheDenmarkStockInfoList();
        }
        if (mkt != null && "Canada".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheCanadaStockInfoList();
        }
        if (mkt != null && "australia".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheAustraliaStockInfoList();
        }
        if (mkt != null && "austria".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheAustriaStockInfoList();
        }
        if (mkt != null && "swiss".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheSwissStockInfoList();
        }
        if (mkt != null && "euro".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheEuroStockInfoList();
        }
        return null;
    }


    private String generateHtml(List<StockInfo> stockInfoList) {
        try {
            LOGGER.info("AllStocksController::top200");
            StringBuilder dataBuffer = new StringBuilder("");
            stockInfoList.stream().forEach(x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ){
//                    LOGGER.info(x.toString());
                    StockResearchUtility.createTableContents(dataBuffer, x);
//                    LOGGER.info("$After$" + x);
                }
            });
            String data = StockResearchUtility.HTML_START;
            data += dataBuffer.toString();
            data += StockResearchUtility.HTML_END;
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", top200 - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

}