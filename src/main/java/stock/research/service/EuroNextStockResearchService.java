package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.EuroNextStockInfo;
import stock.research.utility.EuroNextStockResearchUtility;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static org.openqa.selenium.support.ui.ExpectedConditions.elementToBeClickable;
import static stock.research.utility.EuroNextStockResearchUtility.*;

@Service
public class EuroNextStockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(EuroNextStockResearchService.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RetryTemplate retryTemplate;

    @Autowired
    private RestTemplate restTemplate;
//    @Autowired
//    private EuroNextStockUrlExtractor euroNextStockUrlExtractor;

    public static List<EuroNextStockInfo> getCacheEuroNextStockDetailedInfoList() {
        return cacheEuroNextStockDetailedInfoList;
    }

    private static List<EuroNextStockInfo> cacheEuroNextStockDetailedInfoList  = new ArrayList<>();

    private WebDriver webDriver;

    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }

    public List<EuroNextStockInfo> getEuroNextStockInfo() {
        try {
            List<EuroNextStockInfo> euroNextUrlMap = objectMapper.readValue(new ClassPathResource("EuroNextALLDetailedInfo.json").getInputStream(), new TypeReference<List<EuroNextStockInfo>>(){});;
//            Map<String , String> euroNextUrlMap = objectMapper.readValue(new ClassPathResource("euroNextTop250Url.json").getInputStream(), new TypeReference<Map<String , String>>(){});;
            return euroNextUrlMap.stream().limit(200).map(x -> new EuroNextStockInfo(x.getStockName(), x.getStockURL())).collect(Collectors.toList());
//            return objectMapper.readValue(new ClassPathResource("failed-EuroNext-detailedInfo.json").getInputStream(), new TypeReference<List<EuroNextStockInfo>>(){});
        } catch (Exception ex) {
            ERROR_LOGGER.error(now() + ", Error ->", ex);
            ex.printStackTrace();
            return null;
        }
    }

    public List<EuroNextStockInfo> populateEuroNextStockDetailedInfo() {
        LOGGER.info("<- Started EuroNextStockResearchService.populateEuroNextStockDetailedInfo");
//        final  List<EuroNextStockInfo> populateEuroNextStockDetailedInfoList  = getFileJson();
        final  List<EuroNextStockInfo> populateEuroNextStockDetailedInfoList  = getEuroNextStockInfo();

        try {
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();
        }catch (Exception e){
            webDriver = launchBrowser();
        }

        try {

//            populateEuroNextStockDetailedInfoList.stream().forEach(euroNextStockInfo -> {
            populateEuroNextStockDetailedInfoList.stream().limit(200).forEach(euroNextStockInfo -> {

                boolean success = false;
                int retry =5;
                while (!success && --retry >0 ){
                    if (euroNextStockInfo.getStockMktCap() == null ||
                            euroNextStockInfo.get_52WeekHighPrice() == null ||
                            euroNextStockInfo.get_52WeekLowPrice() == null ||
                            euroNextStockInfo.getCurrentMarketPrice() == null ||
                            (euroNextStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) == 0 ) ||
                            (euroNextStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0
                                    || euroNextStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) == 0)){
                        try {
                            success = launchAndExtract(euroNextStockInfo);
                        }catch (Exception e) {
                            ERROR_LOGGER.error("$"+euroNextStockInfo.getStockURL() + "$ <- Error Url" +now() + ",launchAndExtract::Error ->", e);
                            e.printStackTrace();
                            if (webDriver != null) webDriver.close();
                            webDriver = launchBrowser();
                            refreshDriver();
                        }
                    }
                }
            });

            populateEuroNextStockDetailedInfoList.sort(Comparator.comparing(EuroNextStockInfo::getMktCapRealValue,
                    nullsFirst(naturalOrder())).reversed());

            int i =1;
            for (EuroNextStockInfo x : populateEuroNextStockDetailedInfoList){
                x.setStockRankIndex(i++);
            }

            System.out.println(populateEuroNextStockDetailedInfoList.size() + "<= size, AFTER .populateEuroNextStockDetailedInfo");
            LOGGER.info(populateEuroNextStockDetailedInfoList.size() + "<= size, AFTER .populateEuroNextStockDetailedInfo");

            String fileName =  LocalDateTime.now() + HYPHEN  ;
            fileName = fileName.replace(":","-");

            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName + HYPHEN
                            + "EuroNext" + HYPHEN  + "ALLDetailedInfo.json"),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(populateEuroNextStockDetailedInfoList).getBytes());
            cacheEuroNextStockDetailedInfoList = populateEuroNextStockDetailedInfoList;
            try {
                if (webDriver != null) webDriver.close();webDriver = null;
            }catch (Exception e){ webDriver = null;}
            return (populateEuroNextStockDetailedInfoList);
        }catch (StaleElementReferenceException e){
            if (webDriver != null) webDriver.close();webDriver = null;
            webDriver = launchBrowser();
            e.printStackTrace();
            refreshDriver();
        }
        catch (Exception e){
            if (webDriver != null) webDriver.close();webDriver = null;
            webDriver = launchBrowser();
            refreshDriver();
            ERROR_LOGGER.error(now() + ", Error ->", e);
            e.printStackTrace();
        }
        cacheEuroNextStockDetailedInfoList = populateEuroNextStockDetailedInfoList;
        try {
            if (webDriver != null) webDriver.close();webDriver = null;
        }catch (Exception e){ webDriver = null;}
        return (populateEuroNextStockDetailedInfoList);
    }

    private List<EuroNextStockInfo> getFileJson() {
        try {
            return objectMapper.readValue(new ClassPathResource("EuroNext-detailedInfo.json").getInputStream(), new TypeReference<List<EuroNextStockInfo>>(){});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private boolean launchAndExtract(EuroNextStockInfo euroNextStockInfo) {
        try{
            WebDriverWait wait = new WebDriverWait(webDriver, (10));

            if (webDriver == null) launchBrowser();
//            webDriver.get("https://live.euronext.com/en/product/equities/FR0011053636-ALXP/the-blockchain-gp/altbg/quotes");
            webDriver.get(euroNextStockInfo.getStockURL());
            scrollToolbar();
            Thread.sleep(1000 * 1);

            hoverAndScollBrowser();

            LOGGER.info("EuroNextStockResearchService::populateEuroNextStockDetailedInfo::StockURL ->  " + euroNextStockInfo.getStockURL());
            if (!updateDomDriver(webDriver,wait, UUID.randomUUID().toString())){
                updateDomDriver(webDriver,wait, UUID.randomUUID().toString());
            }

            Thread.sleep(10 * 5);

            try {
                WebElement stkNameEle = webDriver.findElement(By.id("header-instrument-name"));
                if (stkNameEle != null){
                    euroNextStockInfo.setStockName(stkNameEle.getText());
                }
            }catch (Exception e){}

            try {
                WebElement currencyEle = webDriver.findElement(By.id("header-instrument-currency"));
                if (currencyEle != null && currencyEle.getText() != null){
                    wait.until(elementToBeClickable(currencyEle));
                    euroNextStockInfo.setStockCurrency((currencyEle.getText().trim()));
                }
            }catch (Exception e){}

            /*if (!updateDomDriver(webDriver,wait, UUID.randomUUID().toString())){
                updateDomDriver(webDriver,wait, UUID.randomUUID().toString());
            }*/
            Thread.sleep(10);
            try{
                WebElement curentMktPrice = webDriver.findElement(By.id("header-instrument-price"));
                if (curentMktPrice != null && curentMktPrice.getText() != null){
                    wait.until(elementToBeClickable(curentMktPrice));
                    euroNextStockInfo.setCurrentMarketPrice(EuroNextStockResearchUtility.getBigDecimalFromString(curentMktPrice.getText().trim()));
                }
            }catch (Exception e){}

            Thread.sleep(10);
            WebElement quoteDiv = webDriver.findElement(By.id("detailed-quote"));
            List<WebElement> hlPriceTr = quoteDiv.findElements(By.tagName("tr"));
            if (hlPriceTr != null && hlPriceTr.size() > 0
                    && ((euroNextStockInfo.getStockMktCap() == null) ||
                    (euroNextStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) == 0
                    && euroNextStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) == 0 ))){
                for (WebElement tr : hlPriceTr){
                    if ((euroNextStockInfo.getStockMktCap() == null)){
/*
                        if (!updateDomDriver(webDriver,wait, UUID.randomUUID().toString())){
                            updateDomDriver(webDriver,wait, UUID.randomUUID().toString());
                        }
*/
                        List<WebElement> hlPriceTds = tr.findElements(By.tagName("td"));
                        if (hlPriceTds != null && hlPriceTds.size() > 0){
                            for (int i = 0; i < hlPriceTds.size(); i++) {

                                if (hlPriceTds.get(i) != null && "52 Week".equalsIgnoreCase(hlPriceTds.get(i).getText())){
                                    if ((i +1) <= hlPriceTds.size()){
                                        WebElement td = hlPriceTds.get(i + 1);
                                        List<WebElement> hlPriceSpans = td.findElements(By.tagName("span"));
                                        if (hlPriceSpans != null && hlPriceSpans.size() > 1){
                                            String low52Price = hlPriceSpans.get(0).getText();
                                            if (low52Price != null && low52Price.trim() != null){
                                                low52Price = low52Price.trim();
                                                low52Price = low52Price.replace(euroNextStockInfo.getStockCurrency(), "");
                                                euroNextStockInfo.set_52WeekLowPrice(getBigDecimalFromString(low52Price));
                                            }
                                            String high52Price = hlPriceSpans.get(1).getText();
                                            if (high52Price != null && high52Price.trim() != null){
                                                high52Price = high52Price.trim();
                                                high52Price = high52Price.replace(euroNextStockInfo.getStockCurrency(), "");
                                                euroNextStockInfo.set_52WeekHighPrice(getBigDecimalFromString(high52Price));
                                            }
                                        }
                                    }
                                }

                                Thread.sleep(10);
                                if (hlPriceTr != null && hlPriceTr.size() >= 10){
                                    if (hlPriceTds.get(i) != null &&
                                            hlPriceTds.get(i).getText().contains("Market") &&
                                            hlPriceTds.get(i).getText().contains("Cap")){
                                        if ((i +1) <= hlPriceTds.size()){
                                            euroNextStockInfo.setStockMktCap(hlPriceTds.get(i + 1).getText());
                                            if (euroNextStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0
                                                    && euroNextStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0){
//                                                break;
                                            }
                                        }
                                    }
                                }
                                Thread.sleep(10);
                                if (hlPriceTr != null && hlPriceTr.size() >= 10){
                                    if (hlPriceTds.get(i) != null &&
                                            hlPriceTds.get(i).getText().contains("Currency")){
                                        if ((i +1) <= hlPriceTds.size()){
                                            euroNextStockInfo.setStockCurrency(hlPriceTds.get(i + 1).getText());
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            String mktCap = euroNextStockInfo.getStockMktCap();
            if (mktCap != null && mktCap.trim()!= null){
                mktCap = mktCap.trim();
                mktCap = mktCap.replace(euroNextStockInfo.getStockCurrency(),"");
                if (mktCap != null){
                    mktCap = mktCap.trim();
                    if (mktCap.contains("T")){
                        euroNextStockInfo.setMktCapRealValue(TRILLION * getDoubleFromString(mktCap.split("T")[0]));
                    }
                    if (mktCap.contains("B")){
                        euroNextStockInfo.setMktCapRealValue(BILLION * getDoubleFromString(mktCap.split("B")[0]));
                    }
                    if (mktCap.contains("M")){
                        euroNextStockInfo.setMktCapRealValue(MILLION * getDoubleFromString(mktCap.split("M")[0]));
                    }
                    if (mktCap.contains("K")){
                        euroNextStockInfo.setMktCapRealValue(1000 * getDoubleFromString(mktCap.split("K")[0]));
                    }
                    if (mktCap.contains("k")){
                        euroNextStockInfo.setMktCapRealValue(1000 * getDoubleFromString(mktCap.split("k")[0]));
                    }
                }
            }
            if (euroNextStockInfo.get_52WeekLowPrice() != null && euroNextStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                    euroNextStockInfo.get_52WeekHighPrice() != null && euroNextStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0){
                euroNextStockInfo.set_52WeekHighLowPriceDiff(((euroNextStockInfo.get_52WeekHighPrice().subtract(euroNextStockInfo.get_52WeekLowPrice()).abs())
                        .divide(euroNextStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            if (euroNextStockInfo.get_52WeekHighPrice() != null && euroNextStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0
            && euroNextStockInfo.get_52WeekHighPrice().subtract(euroNextStockInfo.getCurrentMarketPrice()).abs().compareTo(BigDecimal.ZERO) >0){
                euroNextStockInfo.set_52WeekHighPriceDiff(((euroNextStockInfo.get_52WeekHighPrice().subtract(euroNextStockInfo.getCurrentMarketPrice()).abs())
                        .divide(euroNextStockInfo.getCurrentMarketPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            if (euroNextStockInfo.get_52WeekLowPrice() != null && euroNextStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0
            && euroNextStockInfo.getCurrentMarketPrice().subtract(euroNextStockInfo.get_52WeekLowPrice()).abs().compareTo(BigDecimal.ZERO) > 0){
                euroNextStockInfo.set_52WeekLowPriceDiff(((euroNextStockInfo.getCurrentMarketPrice().subtract(euroNextStockInfo.get_52WeekLowPrice()).abs())
                        .divide(euroNextStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
            }
            if (euroNextStockInfo.getStockCurrency() != null && euroNextStockInfo.getMktCapRealValue() != null){
                if (euroNextStockInfo.getStockCurrency().equalsIgnoreCase("NOK")){
                    euroNextStockInfo.setMktCapRealValue(euroNextStockInfo.getMktCapRealValue()/10);
                }
                if (euroNextStockInfo.getStockCurrency().equalsIgnoreCase("GBP")){
                    euroNextStockInfo.setMktCapRealValue(euroNextStockInfo.getMktCapRealValue() * 1.1);
                }if (euroNextStockInfo.getStockCurrency().equalsIgnoreCase("USD") || euroNextStockInfo.getStockCurrency().equalsIgnoreCase("$")){
                    euroNextStockInfo.setMktCapRealValue(euroNextStockInfo.getMktCapRealValue() * 0.85);
                }
            }
            euroNextStockInfo.setTimestamp(now());
            LOGGER.info(String.valueOf(euroNextStockInfo));
        }catch(StaleElementReferenceException e){
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();
            ERROR_LOGGER.error("$"+euroNextStockInfo.getStockURL() + "$ <- Error Url" +now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            refreshDriver();
        }catch (Exception e){
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();

            refreshDriver();
            ERROR_LOGGER.error("$"+euroNextStockInfo.getStockURL() + "$ <- Error Url" +now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
        }
        return true;
    }

    private void refreshDriver() {
        try {
            webDriver.navigate().refresh();
        }catch (Exception e){}
    }


    private boolean updateDomDriver(WebDriver driver, WebDriverWait wait, String searchText) {
        try {
            driver.getTitle();
            Thread.sleep(10);
            wait.until(elementToBeClickable(driver.findElement(By.id("edit-search-input-quote--3"))));
            driver.findElement(By.id("edit-search-input-quote--3")).clear();
            driver.findElement(By.id("edit-search-input-quote--3")).sendKeys(searchText + UUID.randomUUID());
            Thread.sleep(10);
            driver.findElement(By.id("edit-search-input-quote--3")).clear();
            Thread.sleep(10);
        }catch (Exception e){
            refreshDriver();
            return false;
        }
        return true;
    }


    private WebDriver launchBrowser() {
        try{
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            sleep(1000 * 2);

            for (int i = 0; i < 3; i++) {
                try {
                    refreshDriver();
                    webDriver.get("https://live.euronext.com/en/product/equities/FR0000120321-XPAR/l%27oreal/or/quotes");

                    sleep(1000 * 3);
                    try { webDriver.findElement(By.className("eu-cookie-compliance-save-preferences-button")).click(); } catch (Exception e) { }
                    sleep(1000 * 2);
                    try {
                        webDriver.switchTo().alert().accept();
                        sleep(1000 * 2);
                    } catch (Exception e) { }
                }catch (Exception e){ }
            }

        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            return null;
        }
        return webDriver;
    }


    private void hoverAndScollBrowser() {
        try{
            //to perform Scroll on application using Selenium
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeScript("window.scrollBy(0,200)", "");
            Thread.sleep(100 * 1);
            js.executeScript("window.scrollBy(0,200)", "");
            Thread.sleep(100 * 1);
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(100 * 1);
            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
            Thread.sleep(100 * 1);
//            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.HOME);
        }catch (Exception e){}
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
            Thread.sleep(500 * 2);

        }catch (Exception e){}
    }


}