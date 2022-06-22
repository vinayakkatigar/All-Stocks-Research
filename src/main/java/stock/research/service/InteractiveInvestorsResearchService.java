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
import org.springframework.web.client.RestTemplate;
import stock.research.domain.NyseStockInfo;
import stock.research.email.alerts.EmailGenerationService;
import stock.research.utility.PortfolioResearchUtility;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;
import static stock.research.utility.NyseStockResearchUtility.*;


@Service
public class InteractiveInvestorsResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(InteractiveInvestorsResearchService.class);

    @Autowired
    private EmailGenerationService emailGenerationService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    private WebDriver webDriver;

    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }
    public String monitorPortfolioStock() {
        StringBuilder dataBuffer = new StringBuilder("");
        LOGGER.info("<- Started InteractiveInvestorsResearchService.populateNYSEStockDetailedInfo");
        Map<String, String> nyseStockDetailedInfoMap = new LinkedHashMap<>();
        final  List<NyseStockInfo> populateNYSEStockDetailedInfoList = new ArrayList<>();
        try {
            restartWebDriver();
        }catch (Exception e){}

        try {

            webDriver.get("https://secure.ii.co.uk/webbroker2/login.jsp");
            Thread.sleep(1000 * 5);

            WebElement userNameEle = webDriver.findElement(By.id("username"));
            userNameEle.sendKeys("3602099");
            Thread.sleep(1000);
            WebElement logonBtnEle = webDriver.findElement(By.id("logon-btn-login"));
            logonBtnEle.click();
            Thread.sleep(1000);

            WebElement userPwdEle = webDriver.findElement(By.id("1-password"));
            userPwdEle.sendKeys("Vinu$1983");
            Thread.sleep(1000);

            WebElement submitBtnEle = webDriver.findElement(By.id("1-submit"));
            submitBtnEle.click();
            Thread.sleep(1000);
            WebElement codeBtnEle = webDriver.findElement(By.className("auth0-lock-input"));
            codeBtnEle.click();
//            codeBtnEle.sendKeys("COde");
            Thread.sleep(1000 * 180);

            webDriver.findElement(By.className("auth0-mfa-checkbox")).click();
            webDriver.findElement(By.className("auth0-lock-submit")).click();

            webDriver.findElements(By.id("instrumentName")).get(7).getText();

            webDriver.findElement(By.id("percentageGainLoss")).getText();

            Thread.sleep(1000 * 20);
            webDriver.get("https://secure.ii.co.uk/webbroker2/app.sp#/portfolio-new/all/view");
            Thread.sleep(1000 * 20);


            for (int i = 0; i < webDriver.findElements(By.id("instrumentName")).size(); i++) {
                dataBuffer.append("<tr>");
                dataBuffer.append("<td>" + webDriver.findElements(By.id("instrumentName")).get(i).getText() + "</td>");
                dataBuffer.append("<td>" + webDriver.findElements(By.id("marketValue")).get(i).getText() + "</td>");
                dataBuffer.append("<td>" + webDriver.findElements(By.id("gainLoss")).get(i).getText() + "</td>");
                dataBuffer.append("<td>" + webDriver.findElements(By.id("percentageGainLoss")).get(i).getText() + "</td>");
                dataBuffer.append("</tr>");
            }
            int retry = 3;
            System.out.println("InteractiveInvestorsResearchService.monitorPortfolioStock");
            System.out.println(dataBuffer);
            while (!emailGenerationService.sendEmail(dataBuffer, new StringBuilder("** Portfolio Stocks Interactive Investors Data ** ")) && --retry >= 0);

            System.out.println("dataBuffer" + dataBuffer);
        }catch (Exception e){
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        String data = PortfolioResearchUtility.HTML_START;
        data += dataBuffer.toString();
        data += PortfolioResearchUtility.HTML_END;

        return data;
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
            LOGGER.info("InteractiveInvestorsResearchService::populateNYSEStockDetailedInfo::StockURL ->  " + x.getValue());
//                    response = restTemplate.exchange("https://ih.advfn.com/stock-market/NASDAQ/amazon-com-AMZN/stock-price", HttpMethod.GET, null, String.class);
            try {
                if (webDriver == null){
                    webDriver = launchBrowser();
                }
                webDriver.get(x.getValue());
            }catch (Exception e){
                webDriver = launchBrowser();
                webDriver.get(x.getValue());
            }

            sleep(1000 * 3);
            //webDriver.navigate().refresh();
            //sleep(1000 * 5);
//                    webDriver.navigate().refresh();
//                    sleep(1000 * 2);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();

        }
        try {
//                    webDriver.navigate().refresh();
//                    sleep(1000 * 15);
            NyseStockInfo nyseStockInfo = new NyseStockInfo(x.getKey(), x.getValue());

            try{
                String crtPrice = webDriver.findElement(By.className("symbol-page-header__pricing-last-price")).findElement(By.className("symbol-page-header__pricing-price")).getText();
                crtPrice = crtPrice.replace('$', ' ').replaceAll(" ", "");
                nyseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(crtPrice));
            }catch (Exception e){}

            List<WebElement> webElementTdBodyList = null;
            int wait =0;
            int retry =10;
            while (retry > 0 && ( webElementTdBodyList ==null || webElementTdBodyList.size() ==0)){
//                        webDriver.navigate().refresh();
                if (retry < 9)sleep(1000 * (2 + (++wait)));
                --retry;

                try{
                    //to perform Scroll on application using Selenium
                    JavascriptExecutor js = (JavascriptExecutor) webDriver;
                    js.executeScript("window.scrollBy(0,1200)", "");
                    Thread.sleep(500 * 1);
                    js.executeScript("window.scrollBy(0,250)", "");
                    Thread.sleep(500 * 1);
                    js.executeScript("window.scrollBy(0,250)", "");
                    Thread.sleep(250 * 1);
                    //js.executeScript("window.scrollTo(0, document.body.scrollHeight)");

                    webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
                    webElementTdBodyList =webDriver.findElement(By.xpath("//div[contains(@class, 'summary-data')]")).findElement(By.xpath("//div[contains(@class, 'summary-data--loaded')]")).findElements(By.tagName("td"));
                }catch (Exception e){}
            }
            wait=0;

//                    List<WebElement> webElementTdBodyList = webDriver.findElement(By.cssSelector(".summary-data.summary-data--loaded")).findElement(By.className("summary-data__table")).findElements(By.tagName("td"));
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
                    if (nyseStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0){

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

            populateNYSEStockDetailedInfoList.add(nyseStockInfo);
            System.out.println("b4 -> nyseStockInfo -> " + nyseStockInfo);
            LOGGER.info("b4 -> nyseStockInfo -> " + nyseStockInfo);

        }catch (Exception e) {
            restartWebDriver();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        return true;
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
        LOGGER.info("<- Started InteractiveInvestorsResearchService::getNyseStockInfo:: -> ");
        for(char i = 'A'; i <= 'Z'; ++i) {
            String stockUrl = url + i;
            makeRestCall(stockCodeUrlMap, stockUrl);
        }
    }


    @Retryable(maxAttempts=10, value = RuntimeException.class,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    private void makeRestCall(Map<String, String> stockCodeUrlMap, String stockUrl) {
        ResponseEntity<String> response;
        LOGGER.info("InteractiveInvestorsResearchService::getNyseStockInfo::stockUrl: -> " + stockUrl);
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
        try{
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
            sleep(200 );
            webDriver.get("https://secure.ii.co.uk/webbroker2/login.jsp");
            sleep(1000 * 5);

        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            return null;
        }
        return webDriver;
    }



}