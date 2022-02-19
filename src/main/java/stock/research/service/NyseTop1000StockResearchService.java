package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.domain.StockInfo;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.StockResearchUtility.*;


@Service
public class NyseTop1000StockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(NyseTop1000StockResearchService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AllStockResearchService allStockResearchService;

    @Autowired
    private RetryTemplate retryTemplate;

    private Map<String, String> stockMap = new HashMap<>();

    @Autowired
    RestTemplate restTemplate;

    private static List<StockInfo> cacheNyseStockInfoList = new ArrayList<>();

    private WebDriver webDriver;

    @PostConstruct
    public void setUp(){
    }

    public List<StockInfo> populateStockDetailedInfo(String component,String uri,Integer cnt) {
        LOGGER.info("<- Started NyseTop1000StockResearchService.populateStockDetailedInfo");
        Map<String, String> stockMap = getStockMap(component,uri, cnt);
        List<StockInfo> stockInfoList = stockMap.entrySet().stream().map(x-> new StockInfo(x.getKey(), x.getValue())).collect(toList());
        stockInfoList = allStockResearchService.getDetailedInfo("NYSE_1000", stockInfoList);
        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\"+ component + ".json"),
                    objectMapper.writeValueAsString(stockInfoList).getBytes());
        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Outer Error ->", e);
            e.printStackTrace();
        }
        cacheNyseStockInfoList = stockInfoList;
        return (stockInfoList);
    }


    @Retryable(maxAttempts=10, value = RuntimeException.class,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public ResponseEntity<String> makeRestCall(String stockURL) {
        try {
            return restTemplate.exchange(stockURL, HttpMethod.GET, null, String.class);
        }catch (Exception e){ERROR_LOGGER.error(stockURL , e); }

        return null;
    }

    private Map<String, String> getStockMap(String component, String uri, Integer cnt) {
        Map<String, String> stockMap = new LinkedHashMap<>();
        try {
            return objectMapper.readValue(new ClassPathResource((component) + ".json").getInputStream(), new TypeReference<LinkedHashMap<String,String>>(){});
        }catch (Exception e){
            e.printStackTrace();
        }

        ResponseEntity<String> infoResponse = null;
        try {
            for (int i = 1; i <= cnt; i++) {
                String url = uri + i;
                LOGGER.info("NyseTop1000StockResearchService::url: -> " + url);
                infoResponse = makeRestCall(url);
                //restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                Document doc = Jsoup.parse(infoResponse.getBody());
                if (doc.getElementsByClass("table marketcap-table dataTable") != null && doc.getElementsByClass("table marketcap-table dataTable").size() > 0
                        && doc.getElementsByClass("table marketcap-table dataTable").get(0).getElementsByClass("name-div") != null){
                    for (Element x : doc.getElementsByClass("table marketcap-table dataTable").get(0).getElementsByClass("name-div")){
                        stockMap.put(x.getElementsByClass("company-code").get(0).text(), "https://uk.finance.yahoo.com/quote/" + x.getElementsByClass("company-code").get(0).text());
                    }
                }
                System.out.println(stockMap.size());
            }
        }catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        return stockMap;
    }


    private WebDriver launchBrowser() {
        System.out.println("StockResearchService.launchBrowser" + System.getProperty("user.dir"));
        try{
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            sleep(200 );
            webDriver.get("https://www.google.com/finance/");
            sleep(1000 * 5);

            try { webDriver.findElement(By.id("onetrust-accept-btn-handler")).click();webDriver.get("https://www.nasdaq.com/market-activity/stocks/aapl"); } catch (Exception e) { }

        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            return null;
        }
        return webDriver;
    }

    public static List<StockInfo> getCacheNyseStockInfoList() {
        return cacheNyseStockInfoList;
    }

}