package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.utility.NyseStockResearchUtility;
import stock.research.utility.StockResearchUtility;

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

    private boolean isRunningFlag = false;

    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }
    public List<NyseStockInfo> populateNYSEStockDetailedInfo() {
        int maxRetries = 50;
        while (maxRetries-- > 0 && isRunningFlag) {
            try {
                Thread.sleep(1000 * 60 * 1);
            } catch (Exception e) { }
        }
        isRunningFlag = true;
        Map<String, String> nyseStockDetailedInfoMap = new LinkedHashMap<>();
         List<NyseStockInfo> populateNYSEStockDetailedInfoList = new ArrayList<>();

        try {
            nyseStockDetailedInfoMap = getNyseStockInfo();

            runNyse(nyseStockDetailedInfoMap, populateNYSEStockDetailedInfoList);

            try{
                String fileName =  LocalDateTime.now() + NyseStockResearchUtility.HYPHEN  ;
                fileName = fileName.replace(":","-");
                Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\NYSE-ALL-STOCKS-" + fileName + "detailedInfo.json"),
                        objectMapper.writeValueAsString(populateNYSEStockDetailedInfoList).getBytes());
            }catch (Exception e){}

            populateNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList.stream().filter(q -> (
                            (q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                                    && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                                    && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                                    && (q.getStockMktCap() != null || q.getMktCapRealValue() != null)))
                    .collect(toList());

            populateNYSEStockDetailedInfoList.sort(Comparator.comparing(NyseStockInfo::getMktCapRealValue,
                    nullsFirst(naturalOrder())).reversed());

            int i =1;
            for (NyseStockInfo x : populateNYSEStockDetailedInfoList){
                x.setStockRankIndex(i++);
                if(x.getMktCapRealValue() != null)x.setStockMktCap(truncateNumber(x.getMktCapRealValue()));
            }

            String fileName =  LocalDateTime.now() + NyseStockResearchUtility.HYPHEN  ;
            fileName = fileName.replace(":","-");
            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\NYSE-" + fileName + "detailedInfo.json"),
                    objectMapper.writeValueAsString(populateNYSEStockDetailedInfoList).getBytes());
            cacheNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList;
            isRunningFlag = false;

            try {
                if (webDriver != null) webDriver.close();
            }catch (Exception e) {
                ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
                e.printStackTrace();
            }

            return (populateNYSEStockDetailedInfoList);
        }catch (WebDriverException e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        cacheNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList;
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){ webDriver = null;}
        isRunningFlag = false;
        return (populateNYSEStockDetailedInfoList);
    }

    private void runNyse(Map<String, String> nyseStockDetailedInfoMap, List<NyseStockInfo> populateNYSEStockDetailedInfoList) {
        nyseStockDetailedInfoMap.entrySet().stream().forEach(x -> {
            if (populateNYSEStockDetailedInfoList != null && populateNYSEStockDetailedInfoList.size() >= 850){
                return;
            }
            int retry = 2;
            boolean sucess = false;
            sucess = extractAttributes(populateNYSEStockDetailedInfoList, x);
            while (!sucess && --retry > 0){
                sucess = extractAttributes(populateNYSEStockDetailedInfoList, x);
            }
        });
    }

    private boolean extractAttributes(List<NyseStockInfo> populateNYSEStockDetailedInfoList, Map.Entry<String, String> x) {
        //            nyseStockDetailedInfoMap.entrySet().stream().limit(25).forEach(x -> {
        try {
            LOGGER.info("NYSEStockResearchService::StockURL ->  " + x.getValue());
//                    response = restTemplate.exchange("https://ih.advfn.com/stock-market/NASDAQ/amazon-com-AMZN/stock-price", HttpMethod.GET, null, String.class);
            try {
                webDriver = setUpDriver();
                browseUrl(webDriver, x.getValue());
                Thread.sleep(1500 * 3);
            }catch (Exception e){
//                webDriver = launchBrowser();
                webDriver = setUpDriver();
                if (webDriver == null){
                    webDriver = setUpDriver();
                }
                browseUrl(webDriver, x.getValue());
                sleep(1000 * 3);
            }
            scrollToolbar();

        }catch (WebDriverException e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        try {

            NyseStockInfo nyseStockInfo = new NyseStockInfo(x.getKey(), x.getValue());

            try {
                nyseStockInfo.setStockName(webDriver.findElement(By.className("symbol-page-header__name")).getText());
                String[] urls = nyseStockInfo.getStockURL().split("stocks");
                nyseStockInfo.setStockCode(urls[urls.length - 1].replace("/", ""));
            }catch (Exception e){ }

            String crtPrice = "";

            int retry = 3;

            while (retry-- > 0 && ( "".equalsIgnoreCase(crtPrice))){
                crtPrice = setNYSECmp(x.getValue(), nyseStockInfo, crtPrice);
            }

            crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
            if (!StringUtils.isEmpty(crtPrice))nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));

            List<WebElement> webElementTdBodyList = null;
            retry = 2;

            try {
                webElementTdBodyList = getWebElements(retry, webElementTdBodyList, nyseStockInfo.getStockURL());
            }catch (Exception e){
                StockResearchUtility.killProcess("chrome" ,webDriver);
                webDriver = null;
                webDriver = setUpDriver();
                webDriver.get(nyseStockInfo.getStockURL());
            }

            if (((webElementTdBodyList ==null || webElementTdBodyList.size() ==0))){
                webElementTdBodyList = getWebElements(retry, webElementTdBodyList, nyseStockInfo.getStockURL());
            }
            if (webElementTdBodyList != null && webElementTdBodyList.size() > 0){
                for (int i = 0; i < webElementTdBodyList.size(); i++) {
                    try{
                        webElementTdBodyList.get(i).getText();
                    }catch (Exception exp){
                        StockResearchUtility.killProcess("chrome" ,webDriver);
                        webDriver = null;
                        webDriver = setUpDriver();
                    }
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
                    if (nyseStockInfo.getCurrentMarketPrice() != null
                             && nyseStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0 &&
                            "".equalsIgnoreCase(crtPrice) && StringUtils.isEmpty(crtPrice)
                            && key != null && key.contains("Previous Close")){
                        int mktIndex = i;
                        crtPrice = webElementTdBodyList.get(++mktIndex).getText();
                        crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
                        nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));
                    }
                    if (nyseStockInfo.getCurrentMarketPrice() != null
                            && nyseStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0 && "".equalsIgnoreCase(crtPrice) && StringUtils.isEmpty(crtPrice)
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
                LOGGER.info(populateNYSEStockDetailedInfoList.size() + "<- size::nyseStockInfo ->" + nyseStockInfo);
                populateNYSEStockDetailedInfoList.add(nyseStockInfo);
            }
            populateNYSEStockDetailedInfoList = populateNYSEStockDetailedInfoList.stream()
                    .filter(q -> ((q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                            && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                            && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                            && (q.getStockMktCap() != null || q.getMktCapRealValue() != null))).collect(toList());

        }catch (WebDriverException e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<WebElement> getWebElements(int retry, List<WebElement> webElementTdBodyList, String stockURL) {
        while (retry > 0 && ( webElementTdBodyList ==null || webElementTdBodyList.size() ==0)){
            --retry;
            try{
                try {
                    webElementTdBodyList = webDriver.findElement(By.className("summary-data__table")).findElements(tagName("td"));
                }catch (Exception e){
                    webDriver.findElement(xpath("//div[contains(@class, 'summary-data__table')]")).findElements(tagName("td"));
                }
            }catch (Exception exp){
                StockResearchUtility.killProcess("chrome" ,webDriver);
                webDriver = null;
                webDriver = setUpDriver();
                webDriver.get(stockURL);
            }
        }
        return webElementTdBodyList;
    }

    private void browseUrl(WebDriver webDriver, String x) {
        try{
            webDriver.get(x);
        }catch (Exception e){
            webDriver = null;
            webDriver = setUpDriver();
            webDriver.get(x);
        }
    }

    private WebDriver setUpDriver() {
        int retry = 3;
        while (webDriver == null && retry-- >= 0){
            webDriver = launchBrowser();
        }
        return webDriver;
    }

    private String setNYSECmp(String x, NyseStockInfo nyseStockInfo, String crtPrice) {
        try{
            crtPrice = webDriver.findElement(By.cssSelector(".symbol-page-header__pricing-details.symbol-page-header__pricing-details--current.symbol-page-header__pricing-details--decrease"))
                    .findElement(By.className("symbol-page-header__pricing-price")).getText();
        }catch (Exception ex){ }
        try{
            if (StringUtils.isEmpty(crtPrice)){
                crtPrice = webDriver.findElement(By.cssSelector(".symbol-page-header__pricing-details.symbol-page-header__pricing-details--current.symbol-page-header__pricing-details--increase"))
                        .findElement(By.className("symbol-page-header__pricing-price")).getText();
            }
        }catch (Exception exception){ }
        try{
            if (StringUtils.isEmpty(crtPrice)){
                crtPrice = webDriver.findElement(By.cssSelector(".symbol-page-header__pricing-details.symbol-page-header__pricing-details--current.symbol-page-header__pricing-details--unchanged"))
                        .findElement(By.className("symbol-page-header__pricing-price")).getText();
            }
        }catch (Exception exception){
            LOGGER.error(x + "<- URL, CMP Block ->", exception.getMessage());
        }

        crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
        if (!StringUtils.isEmpty(crtPrice)) nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));

        return crtPrice;
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
            Thread.sleep(2500 * 1);
            /*
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(500 * 2);
            */

//            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
//            Thread.sleep(500 * 2);

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

    private WebDriver launchBrowser() {

        System.out.println("StockResearchService.launchBrowser" + System.getProperty("user.dir"));
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){}

        try {
            if (webDriver != null) webDriver.quit();
        }catch (Exception e){}

        try{
//            killChrome("chrome");

            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
            sleep(2000 );
            int x = 2;
            while (--x > 0 && !acceptCookies()){
                sleep(500 * 5);
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
            browseUrl(webDriver, "https://www.nasdaq.com/market-activity/stocks/aapl");
            sleep(1000 * 2);
            try {
                webDriver.findElement(By.id("onetrust-button-group")).findElement(By.id("onetrust-accept-btn-handler")).click();
            } catch (Exception e) {
                return false;
            }
            webDriver.navigate().refresh();
            sleep(1000 * 3);
        }catch (Exception exception){
            return false;
        }

        return true;
    }


    public Map<String , String> getNyseStockInfo() {
        Map<String , String> stocksUrlMap = new LinkedHashMap<>();
        try {
            LinkedHashMap<String , String> stocksUrlInfo =  objectMapper.readValue(new ClassPathResource("NYSE/nyse-Top-750-StockUrlInfo.json").getInputStream(), new TypeReference<LinkedHashMap<String , String>>(){});
            return stocksUrlInfo;
        } catch (Exception e) {
            e.printStackTrace();
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

    public boolean isRunningFlag() {
        return isRunningFlag;
    }

    public void setRunningFlag(boolean runningFlag) {
        isRunningFlag = runningFlag;
    }


}