package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.utility.NyseStockResearchUtility;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;
import static org.openqa.selenium.By.tagName;
import static org.openqa.selenium.By.xpath;
import static stock.research.utility.NyseStockResearchUtility.*;


@Service
public class NYSEStockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(NYSEStockResearchService.class);

    public static List<NyseStockInfo> getCacheNYSEStockDetailedInfoList() {
        return cacheNYSEStockDetailedInfoList;
    }

    private static List<NyseStockInfo> cacheNYSEStockDetailedInfoList = new ArrayList<>();

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    private WebDriver webDriver;

    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }
    public List<NyseStockInfo> populateNYSEStockDetailedInfo() {
        LOGGER.info("<- Started NYSEStockResearchService.populateNYSEStockDetailedInfo");
        Map<String, String> nyseStockDetailedInfoMap = new LinkedHashMap<>();
        final  List<NyseStockInfo> populateNYSEStockDetailedInfoList = new ArrayList<>();
        try {
            restartWebDriver();
        }catch (WebDriverException e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }catch (Exception e){}

        try {
            nyseStockDetailedInfoMap = getNyseStockInfo();

            runNyse(nyseStockDetailedInfoMap, populateNYSEStockDetailedInfoList);

            try{
                String fileName =  LocalDateTime.now() + NyseStockResearchUtility.HYPHEN  ;
                fileName = fileName.replace(":","-");
                Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\NYSE-ALL-STOCKS-" + fileName + "detailedInfo.json"),
                        objectMapper.writeValueAsString(populateNYSEStockDetailedInfoList).getBytes());
            }catch (Exception e){}

            populateNYSEStockDetailedInfoList.stream().filter(q -> (
                    (q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                    && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                    && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                    && (q.getStockMktCap() != null || q.getMktCapRealValue() != null)))
                    .collect(toList()).sort(Comparator.comparing(NyseStockInfo::getMktCapRealValue,
                    nullsFirst(naturalOrder())).reversed());

            int i =1;
            for (NyseStockInfo x : populateNYSEStockDetailedInfoList){
                x.setStockRankIndex(i++);
                if(x.getMktCapRealValue() != null)x.setStockMktCap(truncateNumber(x.getMktCapRealValue()));
            }

            String fileName =  LocalDateTime.now() + NyseStockResearchUtility.HYPHEN  ;
            fileName = fileName.replace(":","-");
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\NYSE-" + fileName + "detailedInfo.json"),
                    objectMapper.writeValueAsString(populateNYSEStockDetailedInfoList).getBytes());
            cacheNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList;
            try {
                if (webDriver != null) webDriver.close();
            }catch (WebDriverException e) {
                restartWebDriver();
                ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
                e.printStackTrace();
            }catch (Exception e){ webDriver = null;}
            return (populateNYSEStockDetailedInfoList);
        }catch (WebDriverException e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }catch (Exception e){
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        cacheNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList;
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){ webDriver = null;}
        return (populateNYSEStockDetailedInfoList);
    }

    private void runNyse(Map<String, String> nyseStockDetailedInfoMap, List<NyseStockInfo> populateNYSEStockDetailedInfoList) {
        nyseStockDetailedInfoMap.entrySet().stream().forEach(x -> {
            if (populateNYSEStockDetailedInfoList != null && populateNYSEStockDetailedInfoList.size() >= 500){
                return;
            }
            int retry = 3;
            boolean sucess = false;
            sucess = extractAttributes(populateNYSEStockDetailedInfoList, x);
            while (!sucess && --retry > 0){
                sucess = extractAttributes(populateNYSEStockDetailedInfoList, x);
            }
        });
    }

    private void restartWebDriver() {
        /*try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){ }
        try {
            Runtime.getRuntime().exec("TASKKILL /IM  chromedriver.exe /F");
        }catch (Exception e){ }
        try {
            Runtime.getRuntime().exec("TASKKILL /IM  chrome.exe /F");
        }catch (Exception e){ }
        try {
            webDriver = launchBrowser();
        }catch (Exception e){ }*/
    }

    private boolean extractAttributes(List<NyseStockInfo> populateNYSEStockDetailedInfoList, Map.Entry<String, String> x) {
        //            nyseStockDetailedInfoMap.entrySet().stream().limit(25).forEach(x -> {
        try {
            LOGGER.info("NYSEStockResearchService::StockURL ->  " + x.getValue());
//                    response = restTemplate.exchange("https://ih.advfn.com/stock-market/NASDAQ/amazon-com-AMZN/stock-price", HttpMethod.GET, null, String.class);
            try {
                if (webDriver == null){
                    webDriver = launchBrowser();
                }
                webDriver.get(x.getValue());
                Thread.sleep(1000 * 3);
            }catch (Exception e){
//                webDriver = launchBrowser();
                webDriver = launchBrowser();
                webDriver.get(x.getValue());
                sleep(1000 * 2);
            }
            scrollToolbar();

        }catch (WebDriverException e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        try {

            NyseStockInfo nyseStockInfo = new NyseStockInfo(x.getKey(), x.getValue());

            String crtPrice = "";
            try{
                crtPrice = webDriver.findElement(xpath("//div[contains(@class, 'symbol-page-header__pricing-details symbol-page-header__pricing-details--current symbol-page-header__pricing-details--increase')]")).findElement(By.className("symbol-page-header__pricing-price")).getText();

                crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
                if (!StringUtils.isEmpty(crtPrice))nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));
            }catch (Exception e) {
                webDriver.navigate().refresh();
                sleep(1000 * 2);
                try {

                    webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.HOME);
                    scrollToolbar();

                    crtPrice = webDriver.findElement(xpath("//div[contains(@class, 'symbol-page-header__pricing-details symbol-page-header__pricing-details--current symbol-page-header__pricing-details--increase')]")).findElement(By.className("symbol-page-header__pricing-price")).getText();
                    crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
                    if (!StringUtils.isEmpty(crtPrice))nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));
                }catch (Exception ex){
                    restartWebDriver();
                }
            }

            crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
            nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));

            List<WebElement> webElementTdBodyList = null;
            int retry = 3;

            while (retry > 0 && ( webElementTdBodyList ==null || webElementTdBodyList.size() ==0)){
                --retry;
                try {
                    webElementTdBodyList = webDriver.findElement(By.className("summary-data__table")).findElements(tagName("td"));
                }catch (Exception e){
                    webDriver.findElement(xpath("//div[contains(@class, 'summary-data__table')]")).findElements(tagName("td"));
                }
            }
            if (webElementTdBodyList != null && webElementTdBodyList.size() > 0){
                for (int i = 0; i < webElementTdBodyList.size(); i++) {
                    String key = webElementTdBodyList.get(i).getText();
                    if (key != null && key.contains("Market Cap")){
                        int mktIndex = i;
                        nyseStockInfo.setMktCapRealValue(getDoubleFromString(webElementTdBodyList.get(++mktIndex).getText()));
                    }
                    if (key != null && key.contains("Sector")){
                        int mktIndex = i;
                        nyseStockInfo.setSectorIndustry((webElementTdBodyList.get(++mktIndex).getText()));
                    }
                    if (key != null && key.contains("Industry")){
                        int mktIndex = i;
                        nyseStockInfo.setSectorIndustry(nyseStockInfo.getSectorIndustry() + "-" + (webElementTdBodyList.get(++mktIndex).getText()));
                    }
                    if (key != null && key.contains("Earnings Per Share")){
                        int mktIndex = i;
                        String data = webElementTdBodyList.get(++mktIndex).getText();
                        data = data.replace('$', ' ').replaceAll(" ", "");

                        nyseStockInfo.setEps(getDoubleFromString(data));
                    }
                    if (key != null && key.contains("P/E Ratio")){
                        int mktIndex = i;
                        nyseStockInfo.setP2e(getDoubleFromString(webElementTdBodyList.get(++mktIndex).getText()));
                    }
                    if ("".equalsIgnoreCase(crtPrice) && StringUtils.isEmpty(crtPrice)
                            && key != null && key.contains("Previous Close")){
                        int mktIndex = i;
                        crtPrice = webElementTdBodyList.get(++mktIndex).getText();
                        crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
                        nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));
                    }
                    if ("".equalsIgnoreCase(crtPrice) && StringUtils.isEmpty(crtPrice)
                            && nyseStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0){

                        if (key != null && key.contains("Today's High/Low")){
                            int mktIndex = i;
                            String data = (webElementTdBodyList.get(++mktIndex).getText());
                            data = data.replace('$', ' ').replaceAll(" ", "");
                            String [] highlow = data.split("/");
                            nyseStockInfo.setCurrentMarketPriceStr(highlow[0]);
                            nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(highlow[0]));
                        }
                    }

                    if (key != null && key.contains("52 Week High/Low")){
                        int mktIndex = i;
                        String data = (webElementTdBodyList.get(++mktIndex).getText());
                        data = data.replace('$', ' ').replaceAll(" ", "");
                        String [] highlow = data.split("/");
                        nyseStockInfo.set_52WeekHighPrice(getBigDecimalFromString(highlow[0]));
                        nyseStockInfo.set_52WeekLowPrice(getBigDecimalFromString(highlow[1]));
                    }
                }
            }

            nyseStockInfo.set_52WeekHighLowPriceDiff(BigDecimal.ZERO);
            if (nyseStockInfo.get_52WeekLowPrice() != null && nyseStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    nyseStockInfo.get_52WeekHighPrice() != null && nyseStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    ((nyseStockInfo.get_52WeekLowPrice())).compareTo(BigDecimal.ZERO) > 0){
                nyseStockInfo.set_52WeekHighLowPriceDiff(((nyseStockInfo.get_52WeekHighPrice().subtract(nyseStockInfo.get_52WeekLowPrice()).abs())
                        .divide(nyseStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            nyseStockInfo.set_52WeekHighPriceDiff(BigDecimal.ZERO);
            if (nyseStockInfo.get_52WeekHighPrice() != null && nyseStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    ((nyseStockInfo.getCurrentMarketPrice())).compareTo(BigDecimal.ZERO) > 0 ){
                nyseStockInfo.set_52WeekHighPriceDiff(((nyseStockInfo.get_52WeekHighPrice().subtract(nyseStockInfo.getCurrentMarketPrice()).abs())
                        .divide(nyseStockInfo.getCurrentMarketPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            nyseStockInfo.set_52WeekLowPriceDiff(BigDecimal.ZERO);
            if (nyseStockInfo.get_52WeekLowPrice() != null && nyseStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    ((nyseStockInfo.get_52WeekLowPrice())).compareTo(BigDecimal.ZERO) > 0){
                nyseStockInfo.set_52WeekLowPriceDiff(((nyseStockInfo.getCurrentMarketPrice().subtract(nyseStockInfo.get_52WeekLowPrice()).abs())
                        .divide(nyseStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            nyseStockInfo.setTimestamp(Instant.now());

            if (((nyseStockInfo.getCurrentMarketPrice() != null && nyseStockInfo.getCurrentMarketPrice().intValue() > 0)
                    && (nyseStockInfo.get_52WeekLowPrice() != null && nyseStockInfo.get_52WeekLowPrice().intValue() > 0)
                    && (nyseStockInfo.get_52WeekHighPrice() != null && nyseStockInfo.get_52WeekHighPrice().intValue() > 0)
                    && (nyseStockInfo.getStockMktCap() != null || nyseStockInfo.getMktCapRealValue() != null))){
                LOGGER.info("nyseStockInfo ->" + nyseStockInfo);
                populateNYSEStockDetailedInfoList.add(nyseStockInfo);
            }
            populateNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList.stream()
                    .filter(q -> ((q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                            && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                            && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                            && (q.getStockMktCap() != null || q.getMktCapRealValue() != null))).collect(toList());

        }catch (WebDriverException e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }catch (Exception e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private void scrollToolbar() {
        try{
            //to perform Scroll on application using Selenium
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeScript("window.scrollBy(0,1200)", "");
            Thread.sleep(500 * 2);
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(500 * 2);
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(500 * 2);
            /*
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(500 * 2);
            */

//            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
//            Thread.sleep(500 * 2);

        }catch (WebDriverException e) {
            restartWebDriver();
        }catch (Exception e){}
    }

    private  Map<String , String> getNyseStockInfosFile(String fileName) {
        try {
            return objectMapper.readValue(new ClassPathResource(fileName).getInputStream(), new TypeReference<Map<String , String>>(){});
        } catch (Exception ex) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", ex);
            ex.printStackTrace();
            return null;
        }
    }

    private String truncateNumber(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }
    private void populateStockCodeUrlMap(Map<String, String> stockCodeUrlMap, String url) {
        LOGGER.info("<- Started NYSEStockResearchService::getNyseStockInfo:: -> ");
        for(char i = 'A'; i <= 'Z'; ++i) {
            String stockUrl = url + i;
            makeRestCall(stockCodeUrlMap, stockUrl);
        }
    }


    @Retryable(maxAttempts=10, value = RuntimeException.class,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    private void makeRestCall(Map<String, String> stockCodeUrlMap, String stockUrl) {
        ResponseEntity<String> response;
        LOGGER.info("NYSEStockResearchService::getNyseStockInfo::stockUrl: -> " + stockUrl);
        response = restTemplate.exchange(stockUrl, HttpMethod.GET, null, String.class);
        Document doc = Jsoup.parse(response.getBody());
        Elements tableElements = doc.getElementsByClass("market tab1");
        if (tableElements != null && tableElements.size() >0){
            Elements anchorElements = tableElements.get(0).getElementsByTag("a");
            if (anchorElements != null && anchorElements.size() > 0){
                anchorElements.stream().forEach(x -> {
                    if(x != null && x.text() != null && x.text().length() <= 4 && !"".equalsIgnoreCase(x.text()))
                        stockCodeUrlMap.put(x.text(), x.attr("href"));
                });
            }
        }
    }


    private WebDriver launchBrowser() {

        System.out.println("StockResearchService.launchBrowser" + System.getProperty("user.dir"));
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){}
        try {
            if (webDriver != null) webDriver.quit();
        }catch (Exception e){}

        try{
            killChrome("chrome");

            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            sleep(200 );
            int x = 5;
            while (--x > 0 && !acceptCookies()){
                sleep(1000 );
            }
            acceptCookies();
        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            return null;
        }
        return webDriver;
    }

    private boolean acceptCookies() {
        try {
            webDriver.get("https://www.nasdaq.com/market-activity/stocks/aapl");
            sleep(1000 * 5);
            try {
                webDriver.findElement(By.id("onetrust-button-group")).findElement(By.id("onetrust-accept-btn-handler")).click();
            } catch (Exception e) {
                return false;
            }
            webDriver.navigate().refresh();
            sleep(1000 );
        }catch (Exception exception){
            return false;
        }

        return true;
    }


    public Map<String , String> getNyseStockInfo() {
        Map<String , String> stocksUrlMap = new LinkedHashMap<>();
        try {
            LinkedHashMap<String , String> stocksUrlInfo =  objectMapper.readValue(new ClassPathResource("nyseAllStockUrlInfo.json").getInputStream(), new TypeReference<LinkedHashMap<String , String>>(){});
            return stocksUrlInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            System.out.println("NYSEStockResearchService.getNyseStockInfo");
            if (webDriver == null) {
                launchBrowser();
            }
            for (int i = 40; i < 320; i++) {

                System.out.println(stocksUrlMap.entrySet());
                int retry = 3;
                Map<String, String> newStocksUrlMap = null;
                System.out.println(i + " <-  stocksUrlMap::size -> " + stocksUrlMap.size());
                sleep(1000 * 3);

                int prev = i;
                for (int j = 1; j < 40; j++) {

                    if (j >= 8) {
                        try {
                            webDriver.findElement(By.className("pagination__pages")).findElements(tagName("button")).get(4).click();
                            sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (j < 7) {
                        try {
                            webDriver.findElement(By.className("pagination__pages")).findElements(tagName("button")).get(j).click();
                            sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                for (int x = 40; x < 320; x++) {

                    selectPageIndex(x);


                    sleep(1000 * 3);

                    newStocksUrlMap = collectStockUrl();

                    System.out.println(x +" <- collectStockUrl returned ->" + newStocksUrlMap);
                    if (newStocksUrlMap != null && newStocksUrlMap.size() > 0) {
                        stocksUrlMap.putAll(newStocksUrlMap);
                    } else {
                        while (retry > 0 && (newStocksUrlMap == null || newStocksUrlMap.size() == 0)) {
                            webDriver.close();
                            webDriver = launchBrowser();
                            sleep(1000 * 3);
                            System.out.println(retry + " <- $$ Retrying $$ NYSEStockResearchService.getNyseStockInfo");
                            selectPageIndex(i);
                            sleep(1000 * 3);
                            newStocksUrlMap = collectStockUrl();
                            if (newStocksUrlMap != null && newStocksUrlMap.size() > 0) {
                                stocksUrlMap.putAll(newStocksUrlMap);
                            }
                            retry--;
                        }
                    }

                    Files.write(Paths.get(System.getProperty("user.dir") + "\\logs" + "\\NYSEALLstocksUrlMap_22.json"),
                            objectMapper.writeValueAsString(stocksUrlMap).getBytes());

                }
            }

            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs" + "\\NYSEALLstocksUrlMap.json"),
                    objectMapper.writeValueAsString(stocksUrlMap).getBytes());
            return stocksUrlMap;
        }catch (WebDriverException e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return getNyseStockInfosFile("nyseTop250StockUrlInfo.json");
        }
        return stocksUrlMap;
    }

    private void selectPageIndex(int i) {
        if (i >= 7){
            try{webDriver.findElement(By.className("pagination__pages")).findElements(tagName("button")).get(4).click();
                sleep(1000);}catch (Exception e){e.printStackTrace();}
        }else if (i < 7) {
            try { webDriver.findElement(By.className("pagination__pages")).findElements(tagName("button")).get(i).click(); sleep(1000);}catch (Exception e){e.printStackTrace();}
        }
    }

    private Map<String , String> collectStockUrl() {
        Map<String , String> stocksUrlMap = new LinkedHashMap<>();

        try {
            for (int i = 0; i <webDriver.findElement(By.className("nasdaq-screener__table")).findElements(tagName("a")).size(); i++) {
                if (i%2 ==0){
                    continue;
                }else {
                    stocksUrlMap.put(webDriver.findElement(By.className("nasdaq-screener__table")).findElements(tagName("a")).get(i).getText(), webDriver.findElement(By.className("nasdaq-screener__table")).findElements(tagName("a")).get(i).getAttribute("href"));
                }

            }

        }catch (Exception e){
            e.printStackTrace();
            LOGGER.error(e.getMessage());
            System.out.println(e.getMessage());
            return null;
        }
        return stocksUrlMap;
    }



    public static void killChrome(String process) {
        try {
            try {
//                Runtime.getRuntime().exec("TASKKILL /IM  "+ process + ".exe /F");
            }catch (Exception e){
                e.printStackTrace();
            }
            try {
                Files.walk(Paths.get("C:\\Users\\vinka\\AppData\\Local\\Temp"))
                        .sorted(Comparator.reverseOrder())
                        .map(Path::toFile)
                        .forEach(File::delete);        }catch (Exception e){ }
            try {
                Path path = Paths.get("C:\\Users\\vinka\\AppData\\Local\\Temp");
                try (Stream<Path> walk = Files.walk(path)) {
                    walk.sorted(Comparator.reverseOrder())
                            .forEach(x -> {
                                try {
                                    Files.deleteIfExists(x);
                                } catch (IOException e) {
                                }
                            });
                }

                Runtime.getRuntime().exec("RD %temp%");
            }catch (Exception e){
            }
            try {
                Runtime.getRuntime().exec("RMDIR /Q/S %temp%");
            }catch (Exception e){
            }
        }catch (Exception e){ }

    }


}