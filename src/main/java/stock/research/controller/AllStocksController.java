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
import stock.research.email.alerts.AllStocksEmailAlertMechanismService;
import stock.research.service.AllStockResearchService;
import stock.research.utility.StockResearchUtility;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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
    private AllStocksEmailAlertMechanismService allStocksEmailAlertMechanismService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/AllStocks")
    public String AllStocks() throws InterruptedException {

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            long start = System.currentTimeMillis();
            LOGGER.info("##AllStocksController.AllStocks::started##" );
            allStocksEmailAlertMechanismService.kickOffNyseTop1000();
            allStocksEmailAlertMechanismService.kickOffIndiaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffItalyEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSpainEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffJapanEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffBelgiumEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffFranceEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSouthKoreaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSingaporeEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffHongKongEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
            allStocksEmailAlertMechanismService.kickOffAustriaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSwissEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffSwedenEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffEUROEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffCanadaEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffDenmarkEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffFinlandEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffGermanyEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffNetherlandsEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffNorwayEmailAlerts();
            allStocksEmailAlertMechanismService.kickOffWorld1000EmailAlerts();
            allStocksEmailAlertMechanismService.kickOffAustraliaEmailAlerts();
            LOGGER.info("##AllStocksCmdRunner.run::end##" + (System.currentTimeMillis() - start));
        });
        Thread.sleep(1000 * 60 * 1);
        return "AllStocks";
    }

    @RequestMapping("/India")
    public String India(){
        List<StockInfo> stockInfoList = getStockInfoListFor("India");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Italy")
    public String Italy(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Italy");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Spain")
    public String Spain(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Spain");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Japan")
    public String Japan(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Japan");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Belgium")
    public String Belgium(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Belgium");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/France")
    public String France(){
        List<StockInfo> stockInfoList = getStockInfoListFor("France");
        return generateHtml(stockInfoList);
    }

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

    @RequestMapping("/Germany")
    public String Germany(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Germany");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Netherlands")
    public String Netherlands(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Netherlands");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Swiss")
    public String Swiss(){
        List<StockInfo> stockInfoList = getStockInfoListFor("swiss");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Finland")
    public String Finland(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Finland");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Austria")
    public String Austria(){
        List<StockInfo> stockInfoList = getStockInfoListFor("austria");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Canada")
    public String Canada(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Canada");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Denmark")
    public String Denmark(){
        List<StockInfo> stockInfoList = getStockInfoListFor("Denmark");
        return generateHtml(stockInfoList);
    }

    @RequestMapping("/Australia")
    public String Australia(){
        List<StockInfo> stockInfoList = getStockInfoListFor("australia");
        return generateHtml(stockInfoList);    }

    @RequestMapping("/Euro")
    public String Euro(){
        List<StockInfo> stockInfoList = getStockInfoListFor("euro");
        return generateHtml(stockInfoList);    }

    private List<StockInfo>  getStockInfoListFor(String mkt){
        if (mkt != null && "India".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheIndiaStockInfoList();
        }
        if (mkt != null && "Italy".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheItalyStockInfoList();
        }
        if (mkt != null && "Spain".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheSpainStockInfoList();
        }
        if (mkt != null && "Japan".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheJapanStockInfoList();
        }
        if (mkt != null && "Belgium".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheBelgiumStockInfoList();
        }
        if (mkt != null && "France".equalsIgnoreCase(mkt)){
            return allStockResearchService.getCacheFranceStockInfoList();
        }
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