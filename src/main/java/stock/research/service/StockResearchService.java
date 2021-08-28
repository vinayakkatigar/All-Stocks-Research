package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;
import stock.research.domain.StockInfo;
import stock.research.utility.StockResearchUtility;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static stock.research.utility.StockResearchUtility.*;


@Service
public class StockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(StockResearchService.class);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RetryTemplate retryTemplate;

    private Map<String, String> urlMap = new HashMap<>();

    @Autowired
    RestTemplate restTemplate;
    private static List<StockInfo> cacheSwissStockInfoList = new ArrayList<>();

    private static List<StockInfo> cacheCanadaStockInfoList = new ArrayList<>();

    public static List<StockInfo> getCacheAustriaStockInfoList() {
        return cacheAustriaStockInfoList;
    }

    private static List<StockInfo> cacheAustriaStockInfoList = new ArrayList<>();

    private static List<StockInfo> cacheAustraliaStockInfoList = new ArrayList<>();

    private static List<StockInfo> cacheEuroStockInfoList = new ArrayList<>();

    private static List<StockInfo> cacheDenmarkStockInfoList = new ArrayList<>();

    private static List<StockInfo> cacheFinlandStockInfoList = new ArrayList<>();
    private static List<StockInfo> cacheGermanyStockInfoList = new ArrayList<>();
    private static List<StockInfo> cacheNetherlandsStockInfoList = new ArrayList<>();
    private static List<StockInfo> cacheNorwayStockInfoList = new ArrayList<>();
    private static List<StockInfo> cacheWorld1000StockInfoList = new ArrayList<>();
    private static List<StockInfo> cacheSwedenStockInfoList = new ArrayList<>();

    @PostConstruct
    public void setUp(){
        urlMap.put("Australia", "AustraliadetailedInfo.json");
        urlMap.put("Swiss", "SwissdetailedInfo.json");
        urlMap.put("Austria", "AustriadetailedInfo.json");
        urlMap.put("Canada", "CanadadetailedInfo.json");
        urlMap.put("Denmark", "DenmarkdetailedInfo.json");
        urlMap.put("Euro", "EurodetailedInfo.json");
        urlMap.put("Finland", "FinlanddetailedInfo.json");
        urlMap.put("Germany", "GermanydetailedInfo.json");
        urlMap.put("Netherlands", "NetherlandsdetailedInfo.json");
        urlMap.put("Norway", "NorwaydetailedInfo.json");
        urlMap.put("World1000", "World1000detailedInfo.json");
    }
    public List<StockInfo> populateStockDetailedInfo(String component,String uri,Integer cnt) {
        LOGGER.info("<- Started StockResearchService.populateStockDetailedInfo");
        List<StockInfo> stockInfoList = getStockUrlInfos(component,uri, cnt);

        try {
            stockInfoList.stream().forEach(x -> {
//            stockInfoList.stream().limit(15).forEach(x -> {
                ResponseEntity<String> response = null;
                try {
                    Thread.sleep(100 * 5);
                    LOGGER.info("StockResearchService::populateStockDetailedInfo::StockURL ->  " +   x.getStockURL());
//                    response = restTemplate.exchange("https://finance.yahoo.com/quote/AAL.L", HttpMethod.GET, null, String.class);
                    response = makeRestCall(x.getStockURL());

                    int retry =5;
                    while (response == null && --retry > 0){
                        response = makeRestCall(x.getStockURL());
                    }

                    Document doc = Jsoup.parse(response.getBody());
                    Elements spanEle = doc.getElementsByTag("span");
                    if (spanEle != null && spanEle.size() > 0){
                        for (Element z : spanEle){
                            z.getElementsByAttributeValueContaining("data-reactid","14");
                            try{
                                if (x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) <= 0 ){
                                    String mktPrice = z.getElementsByAttributeValueContaining("data-reactid","14").text();
                                    if (mktPrice != null && !StringUtils.isEmpty(mktPrice)){
                                        mktPrice = mktPrice.trim();
                                        mktPrice = mktPrice.replace(",", "");
                                        x.setCurrentMarketPrice(new BigDecimal(mktPrice));
                                    }
                                }
                            }catch (Exception e){}
                        }
                    }
                    if (spanEle != null && spanEle.size() >12){
                       String[] currencyArr = spanEle.get(12).text().split(" ");
                       if (currencyArr[currencyArr.length - 1] != null && !StringUtils.isEmpty(currencyArr[currencyArr.length - 1])){
                           x.setCurrency(currencyArr[currencyArr.length - 1]);
                       }
                    }
                    Elements h1Ele = doc.getElementsByTag("h1");
                    if (h1Ele != null && h1Ele.size() > 0){
                        for (Element h1 : h1Ele){
                            String stockDetails = h1.getElementsByAttributeValueContaining("data-reactid","7").text();
                            String[] stockDetailsArr  = stockDetails.split("-");
                            if (stockDetailsArr != null && stockDetailsArr.length >= 2){
                                x.setStockCode(stockDetailsArr[0]);
                                x.setStockName(stockDetailsArr[1]);
                            }
                        }
                    }
                    String _52Range = extractFieldValue(doc, "52 Week Range");
                    if (_52Range != null){
                        String[]_52RangeArr = _52Range.split(" - ");
                        if (_52RangeArr != null && _52RangeArr.length >=2){
                            x.set_52WeekLowPrice(getBigDecimalFromString(_52RangeArr[0]));
                            x.set_52WeekHighPrice(getBigDecimalFromString(_52RangeArr[1]));
                        }
                    }
                    x.setP2e(getDoubleFromString(extractFieldValue(doc, "PE Ratio")));
                    x.setEps(getDoubleFromString(extractFieldValue(doc, "EPS")));
                    String mktCap = extractFieldValue(doc, "Market Cap");
                    if (mktCap != null){
                        mktCap = mktCap.replace("$","");
                        mktCap = mktCap.replace("£","");
                        mktCap = mktCap.replace("€","");
                        if (mktCap != null){
                            mktCap = mktCap.trim();
                            x.setStockMktCapStr(mktCap);
                            if (mktCap.contains("T")){
                                x.setStockMktCapRealValue(TRILLION * getDoubleFromString(mktCap.split("T")[0]));
                            }
                            if (mktCap.contains("B")){
                                x.setStockMktCapRealValue(BILLION * getDoubleFromString(mktCap.split("B")[0]));
                            }
                            if (mktCap.contains("M")){
                                x.setStockMktCapRealValue(MILLION * getDoubleFromString(mktCap.split("M")[0]));
                            }
                            if (mktCap.contains("K")){
                                x.setStockMktCapRealValue(1000 * getDoubleFromString(mktCap.split("K")[0]));
                            }
                            if (mktCap.contains("k")){
                                x.setStockMktCapRealValue(1000 * getDoubleFromString(mktCap.split("k")[0]));
                            }
                        }
                    }

                } catch (Exception e) {
                    ERROR_LOGGER.error(x.getStockURL() + Instant.now() + ", Error ->", e);
                    e.printStackTrace();
                }
                try {
                    if (x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0){
                        x.set_52WeekHighLowPriceDiff(((x.get_52WeekHighPrice().subtract(x.get_52WeekLowPrice()).abs())
                                .divide(x.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }
                    if (x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0){
                        x.set_52WeekHighPriceDiff(((x.get_52WeekHighPrice().subtract(x.getCurrentMarketPrice()).abs())
                                .divide(x.getCurrentMarketPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }
                    if (x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0){
                        x.set_52WeekLowPriceDiff(((x.getCurrentMarketPrice().subtract(x.get_52WeekLowPrice()).abs())
                                .divide(x.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }
                    setMktCapInUsd(x);
                    x.setTimestamp(Instant.now());
                    LOGGER.info(x.toString());
                }catch (Exception e) {
                    ERROR_LOGGER.error(x.getStockURL() + "<->" +Instant.now() + ", Error ->", e);
                    e.printStackTrace();
                }
            });

            stockInfoList.sort(Comparator.comparing(StockInfo::getStockMktCapRealValue,
                    nullsFirst(naturalOrder())).reversed());

            int i =1;
            for (StockInfo x : stockInfoList){
                x.setStockRankIndex(i++);
            }

            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\"+ component + "detailedInfo.json"),
                    objectMapper.writeValueAsString(stockInfoList).getBytes());
            setCacheForComponent(component, stockInfoList);
            return (stockInfoList);
        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        setCacheForComponent(component, stockInfoList);
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

    private List<StockInfo> getStockUrlInfos(String component, String uri, Integer cnt) {
        List<StockInfo> stockInfoList = new ArrayList<>();
        try {
            stockInfoList = objectMapper.readValue(new ClassPathResource(urlMap.get(component)).getInputStream(), new TypeReference<List<StockInfo>>(){});
            return stockInfoList.stream().map(x -> new StockInfo(x.getStockCode(), x.getStockURL())).collect(Collectors.toList());
        }catch (Exception e){
            e.printStackTrace();
        }

        ResponseEntity<String> infoResponse = null;
        try {
            for (int i = 0; i <= cnt; i++) {
                String url = uri + i;
                LOGGER.info("StockResearchService::url: -> " + url);
                infoResponse = restTemplate.exchange(url, HttpMethod.GET, null, String.class);

                Document doc = Jsoup.parse(infoResponse.getBody());
                Elements yahooElements = doc.getElementsMatchingOwnText("Yahoo @ ");
                if (yahooElements != null && yahooElements.size() > 0){
                    for (Element element : yahooElements){
                        StockInfo stockInfo = new StockInfo();
                        stockInfo.setStockURL(element.attr("href"));
                        stockInfoList.add(stockInfo);
                    }
                }
            }
        }catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        return stockInfoList;
    }

    private void setMktCapInUsd(StockInfo stockInfo) {
        if (stockInfo != null && stockInfo.getStockMktCapRealValue() != null
                && stockInfo.getCurrency() != null ) {
            if (stockInfo.getCurrency().equalsIgnoreCase("CHF")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 1.1); return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("EUR")
                    || stockInfo.getCurrency().equalsIgnoreCase("EURO")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 1.2);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("AUD")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.7);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("GBP")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 1.35);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("DKK")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.15);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("SEK")
                    || stockInfo.getCurrency().equalsIgnoreCase("NOK")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.11);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("CAD")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.8);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("ZAC")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() / 1500);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("MXN")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.050 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("INR")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.014 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("THB")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.031 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("CNY")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.15 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("KRW")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.00086 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("JPY")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.0091 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("IDR")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.000070 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("SGD")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.74 );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("SEK")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.12);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("BRL")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.19);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("TRY")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.12  );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("RUB")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.014  );return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("TWD")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.036);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("HKD")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.13);return;
            }
            if (stockInfo.getCurrency().equalsIgnoreCase("QAR") ||
                    stockInfo.getCurrency().equalsIgnoreCase("SAR")){
                stockInfo.setStockMktCapRealValue(stockInfo.getStockMktCapRealValue() * 0.27);return;
            }
        }
    }

    private void setCacheForComponent(String component, List<StockInfo> stockInfoList) {
        if ("Sweden".equalsIgnoreCase(component)){
            cacheSwedenStockInfoList = stockInfoList; return;
        }
        if ("World1000".equalsIgnoreCase(component)){
            cacheWorld1000StockInfoList = stockInfoList; return;
        }
        if ("Norway".equalsIgnoreCase(component)){
            cacheNorwayStockInfoList = stockInfoList; return;
        }
        if ("Netherlands".equalsIgnoreCase(component)){
            cacheNetherlandsStockInfoList = stockInfoList; return;
        }
        if ("Germany".equalsIgnoreCase(component)){
            cacheGermanyStockInfoList = stockInfoList; return;
        }
        if ("Finland".equalsIgnoreCase(component)){
            cacheFinlandStockInfoList = stockInfoList; return;
        }
        if ("Canada".equalsIgnoreCase(component)){
            cacheCanadaStockInfoList = stockInfoList; return;
        }
        if ("Denmark".equalsIgnoreCase(component)){
            cacheDenmarkStockInfoList = stockInfoList; return;
        }
        if ("Swiss".equalsIgnoreCase(component)){
            cacheSwissStockInfoList = stockInfoList; return;
        }
        if ("Austria".equalsIgnoreCase(component)){
            cacheAustriaStockInfoList = stockInfoList; return;
        }
        if ("Australia".equalsIgnoreCase(component)){
            cacheAustraliaStockInfoList = stockInfoList; return;
        }
        if ("Euro".equalsIgnoreCase(component)){
            cacheEuroStockInfoList = stockInfoList; return;
        }
        if ("Norway".equalsIgnoreCase(component)){
            cacheNorwayStockInfoList = stockInfoList; return;
        }
    }

    private String extractFieldValue(Document doc, String field) {
        String _52Range = null;
        Elements _52WeekRange = doc.getElementsMatchingOwnText(field);
        if (_52WeekRange != null && _52WeekRange.size() > 0){
            Element element = _52WeekRange.get(0);
            if (element != null && element.parent() != null){
                element = element.parent();
                if (element != null && element.parent() != null){
                    element = element.parent();
                    if (element != null){
                        _52Range = element.getElementsMatchingOwnText(" - ").text();
                        if (_52Range != null && !StringUtils.isEmpty(_52Range)) return _52Range;
                        if (element.getElementsByTag("td").get(element.getElementsByTag("td").size() - 1).text() != null)
                            return element.getElementsByTag("td").get(element.getElementsByTag("td").size() - 1).text();

                    }
                }
            }
        }
        return null;
    }

    public static List<StockInfo> getCacheFinlandStockInfoList() {
        return cacheFinlandStockInfoList;
    }

    public static List<StockInfo> getCacheSwissStockInfoList() {
        return cacheSwissStockInfoList;
    }

    public static List<StockInfo> getCacheCanadaStockInfoList() {
        return cacheCanadaStockInfoList;
    }

    public static List<StockInfo> getCacheAustraliaStockInfoList() {
        return cacheAustraliaStockInfoList;
    }

    public static List<StockInfo> getCacheEuroStockInfoList() {
        return cacheEuroStockInfoList;
    }

    public static List<StockInfo> getCacheDenmarkStockInfoList() {
        return cacheDenmarkStockInfoList;
    }

    public static List<StockInfo> getCacheGermanyStockInfoList() {
        return cacheGermanyStockInfoList;
    }

    public static List<StockInfo> getCacheNetherlandsStockInfoList() {
        return cacheNetherlandsStockInfoList;
    }

    public static List<StockInfo> getCacheNorwayStockInfoList() {
        return cacheNorwayStockInfoList;
    }

    public static List<StockInfo> getCacheWorld1000StockInfoList() {
        return cacheWorld1000StockInfoList;
    }

    public static List<StockInfo> getCacheSwedenStockInfoList() {
        return cacheSwedenStockInfoList;
    }

}