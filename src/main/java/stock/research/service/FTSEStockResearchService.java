package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.FtseStockInfo;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static stock.research.utility.FtseStockResearchUtility.*;


@Service
public class FTSEStockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(FTSEStockResearchService.class);
    
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    RestTemplate restTemplate;

    @Autowired
    private RetryTemplate retryTemplate;

    private WebDriver webDriver;

    public static List<FtseStockInfo> getLargeCapCacheftseStockDetailedInfoList() {
        return largeCapCacheftseStockDetailedInfoList;
    }
    public static List<FtseStockInfo> getMidCapCapCacheftseStockDetailedInfoList() {
        return midCapCapCacheftseStockDetailedInfoList;
    }

    private static List<FtseStockInfo> largeCapCacheftseStockDetailedInfoList = new ArrayList<>();

    private static List<FtseStockInfo> midCapCapCacheftseStockDetailedInfoList = new ArrayList<>();


    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }


    public List<FtseStockInfo> populateYearlyGainersLosersFtseStockDetailedInfo(String url, Integer trCnt) {
        LOGGER.info("<- Started FTSEStockResearchService.populateYearlyGainersLosersFtseStockDetailedInfo");
        List<FtseStockInfo> ftseStockDetailedInfoList = new ArrayList<>();
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){}

        ResponseEntity<String> response = null;

        LOGGER.info("FTSEStockResearchService::populateYearlyGainersLosersFtseStockDetailedInfo::url: -> " + url);
        response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        Document doc = Jsoup.parse(response.getBody());
        Elements tableElements = doc.getElementsByClass("stockTable");
        if (tableElements != null && tableElements.size() > 0){
            Element tableElement = tableElements.get(0);
            Elements trElements = tableElement.getElementsByTag("tr");
            if (trElements != null && trElements.size() > 0){
                for (int i = 0; i < trElements.size(); i++) {
                    Element trElement = trElements.get(i);
                    Elements tdElements = trElement.getElementsByTag("td");
                        if (tdElements != null && tdElements.size() > 0 && trCnt  > 0){
                            try {
                                Double.valueOf(tdElements.get(6).text());
                            }catch (Exception e){
                                continue;
                            }
                            trCnt--;
                            ftseStockDetailedInfoList.add(new FtseStockInfo(tdElements.get(1).toString(), new BigDecimal(tdElements.get(6).text())));
                        }
                    }
                }
            }
        return  ftseStockDetailedInfoList;
    }

    public List<FtseStockInfo> getFtseStockInfo(String urlInfo, int cnt) {
        ResponseEntity<String> response = null;
        List<FtseStockInfo> ftseStockInfoList = new ArrayList<>();

        try {
            LOGGER.info("<- Started FTSEStockResearchService::getFtseStockInfo:: -> ");
            for (int i = 1; i < cnt; i++) {

                try {
                    webDriver.get(urlInfo + i);
                    Thread.sleep(1000 * 2);
                }catch (Exception e){
                    webDriver = launchBrowser();
                    webDriver.get(urlInfo + i);
                    Thread.sleep(1000 * 2);
                }
                WebElement  tabElements = webDriver.findElement(By.cssSelector(".full-width.ftse-index-table-table"));
                if (tabElements != null){
                    List<WebElement>   trElements = webDriver.findElements(By.cssSelector(".medium-font-weight.slide-panel"));
                    if (trElements != null && trElements.size() >= 20){
                        trElements.stream().forEach(tr -> {
                            FtseStockInfo ftseStockInfo = new FtseStockInfo();
                            List<WebElement> tdElements = tr.findElements(By.tagName("td"));
                            if (tdElements != null && tdElements.size() >= 7){
                                for (int j = 0; j < 7; j++) {
                                    WebElement tdElement = tdElements.get(j);
                                    if (tdElement != null){
                                        if (j == 0 && tdElement.findElement(By.tagName("a")) != null) {
                                            ftseStockInfo.setStockURL(tdElement.findElement(By.tagName("a")).getAttribute("href"));
                                            ftseStockInfo.setStockCode(tdElement.findElement(By.tagName("a")).getText());
                                            continue;
                                        }if (j == 1 && tdElement.findElement(By.tagName("a")) != null) {
                                            ftseStockInfo.setStockName((tdElement.findElement(By.tagName("a")).getText()));
                                            continue;
                                        }if (j == 3) {
                                            ftseStockInfo.setStockMktCap(getDoubleFromString(tdElement.getText()));
                                            continue;
                                        }if (j == 4) {
                                            ftseStockInfo.setCurrentMarketPrice(getBigDecimalFromString(tdElement.getText()));
                                            continue;
                                        }
                                    }

                                }

                            }
                        ftseStockInfoList.add(ftseStockInfo);
                        });

                    }

                }

            }
            ftseStockInfoList.sort(Comparator.comparing(FtseStockInfo::getStockMktCap).reversed());
            int i = 1;
            for (FtseStockInfo x : ftseStockInfoList) {
                x.setStockRankIndex(i++);
            }

            if (cnt == FTSE_250_CNT){
                Files.write(Paths.get(System.getProperty("user.dir") + "\\logs" + "\\ftse250Info.json"),
                        objectMapper.writeValueAsString(ftseStockInfoList).getBytes());
            }
            if (cnt == FTSE_100_CNT){
                Files.write(Paths.get(System.getProperty("user.dir") + "\\logs" + "\\ftse100Info.json"),
                        objectMapper.writeValueAsString(ftseStockInfoList).getBytes());
            }
            }catch (Exception e){
                ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
                e.printStackTrace();
                return getFtseStockInfosFile("ftse250Info.json");
            }

        return ftseStockInfoList;
    }

    public List<FtseStockInfo> populateFtseStockDetailedInfo(String urlInfo, Integer cnt) {
        LOGGER.info("<- Started FTSEStockResearchService.populateFtseStockDetailedInfo");
        List<FtseStockInfo> ftseStockDetailedInfoList = new ArrayList<>();
        try {
            if (webDriver != null) {
                webDriver.get("https://www.londonstockexchange.com/indices/ftse-100/constituents/table?lang=en&page=1");
            }
        }catch (Exception e){
            webDriver = launchBrowser();
        }

        try {

            //            File ftse250StockInfosFile = new File(System.getProperty("user.dir") + "" + "\\top500DetailedInformation.json");
//            ftse250StockInfoList = objectMapper.readValue(ftse250StockInfosFile, new TypeReference<List<Ftse250StockInfo>>(){});

            ftseStockDetailedInfoList = getFtseStockInfo(urlInfo, cnt);

            try {
                if (webDriver == null) webDriver = launchBrowser();
            }catch (Exception e){}

            ftseStockDetailedInfoList.stream().forEach(x -> {
                try {
                    int retry =5;
                    boolean sucess = false;
                    sucess = extractedChromeDriverAttr(x);
                    while (!sucess && --retry > 0){
                        sucess = extractedChromeDriverAttr(x);
                    }
                }catch (Exception e){
                    webDriver = launchBrowser();
                }
            });

            ftseStockDetailedInfoList.forEach(x -> {
                try {
                    int retry =5;
                    boolean sucess = false;
                    sucess = extractRestJSoupAttributes(x);
                    while (!sucess && --retry > 0){
                        sucess = extractRestJSoupAttributes(x);
                    }
                }catch (Exception e){ webDriver = launchBrowser(); }

            });

            ftseStockDetailedInfoList = ftseStockDetailedInfoList.stream().distinct().collect(Collectors.toList());
            ftseStockDetailedInfoList.sort(Comparator.comparing(FtseStockInfo::getStockMktCap).reversed());
            if (cnt == FTSE_100_CNT){
                largeCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
            }
            if (cnt == FTSE_250_CNT){
                midCapCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
            }

            try {
                if (ftseStockDetailedInfoList != null && ftseStockDetailedInfoList.size() > 0){
                    String fileName =  LocalDateTime.now() + HYPHEN  ;
                    fileName = fileName.replace(":","-");
                    fileName = fileName + "top"+  HYPHEN + ftseStockDetailedInfoList.get(0).getStockRankIndex()+  HYPHEN + ftseStockDetailedInfoList.get(ftseStockDetailedInfoList.size() - 1).getStockRankIndex() +  HYPHEN;
                    Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\"+ fileName + "detailedInfo.json"),
                            objectMapper.writeValueAsString(ftseStockDetailedInfoList).getBytes());
                }
                if (webDriver != null) webDriver.close();
            }catch (Exception e){
                webDriver = null;
            }
            return (ftseStockDetailedInfoList);
        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }

        if (cnt == FTSE_100_CNT){
            largeCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
        }
        if (cnt == FTSE_250_CNT){
            midCapCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
        }
        try {
            if (webDriver != null) webDriver.close();
        }catch (Exception e){ webDriver = null;}
        return (ftseStockDetailedInfoList);
    }

    private boolean extractedChromeDriverAttr(FtseStockInfo x) {
        //            ftseStockDetailedInfoList.stream().limit(15).forEach(x -> {
        if (x.get_52WeekLowPrice() == null || x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) == 0 ||
                x.get_52WeekHighPrice() == null || x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) == 0 ||
                x.get_52WeekHighLowPriceDiff() == null || x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO) == 0) {
            if (webDriver != null){

                try { webDriver.get(x.getStockURL()); Thread.sleep(1000 * 3);} catch (Exception e) {webDriver = launchBrowser(); }
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
                }catch (Exception e){}

                try {
                    if (webDriver.findElement(By.className("chart-table-instrument-information")) != null &&
                            webDriver.findElement(By.className("chart-table-instrument-information")).findElements(By.tagName("app-index-item")) != null
                    && webDriver.findElement(By.className("chart-table-instrument-information")).findElements(By.tagName("app-index-item")).size() > 2 &&
                    webDriver.findElement(By.className("chart-table-instrument-information")).findElements(By.tagName("app-index-item")).get(2) != null) {
                        String eps = webDriver.findElement(By.className("chart-table-instrument-information")).findElements(By.tagName("app-index-item")).get(2).getText();
                        if (eps != null && eps.split("\n").length > 1
                                && eps.split("\n")[0] != null && eps.split("\n")[0].contains("Earnings")) {
                            x.setEps(getDoubleFromString(eps.split("\n")[1]));
                            if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                                    x.getEps() != null && BigDecimal.valueOf(x.getEps()).compareTo(BigDecimal.ZERO) > 0) {
                                x.setP2e(x.getCurrentMarketPrice().divide(BigDecimal.valueOf(x.getEps()), 2, RoundingMode.HALF_EVEN));
                            }
                        }
                    }

                }catch (Exception e){
                    e.printStackTrace();
                }
                try{
                    if((x.get_52WeekLowPrice() == null || x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) == 0) && (webDriver.findElements(By.className("widget-results")).size() > 1)){
                        x.set_52WeekLowPrice(getBigDecimalFromString(webDriver.findElements(By.className("widget-results")).get(0).getText()));
                    }
                }catch(Exception e1){}
                try{
                    if((x.get_52WeekHighPrice() == null || x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) == 0) && (webDriver.findElements(By.className("widget-results")).size() > 1)){
                        x.set_52WeekHighPrice(getBigDecimalFromString(webDriver.findElements(By.className("widget-results")).get(1).getText()));
                    }
                }catch(Exception e1){}

                set52HighLowDifference(x);
                LOGGER.info("ftseStockDetails ->" + x);
            }
        }
        return true;
    }

    private boolean extractRestJSoupAttributes(FtseStockInfo x) {
        //            ftseStockDetailedInfoList.stream().limit(15).forEach(x -> {
        ResponseEntity<String> response = null;
        try {
            LOGGER.info("FTSEStockResearchService::populateFtseStockDetailedInfo::StockURL ->  " + x.getStockURL());
//                    response = restTemplate.exchange("https://www.londonstockexchange.com/stock/AZN/astrazeneca-plc", HttpMethod.GET, null, String.class);
            response = restTemplate.exchange(x.getStockURL(), HttpMethod.GET, null, String.class);
//                    sleep(1000 * 1);
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }
        try {
            String hlYearRange=null, eps=null;
            if (response != null && response.getBody() != null){
                Document doc = Jsoup.parse(response.getBody());
                Elements hlItems =  doc.getElementsByClass("index-item");
                for (Element item: hlItems){
                    Elements spanItems =  item.getElementsByTag("span");
                    if (spanItems != null && spanItems.size() > 0){
                        String spanText = spanItems.get(0).text();
                        if (spanText.contains("52") && spanText.contains("week")
                                && spanText.contains("range")){
                            Elements hlDiv = item.getElementsByTag("div");
                            if (hlDiv != null && hlDiv.size() > 0){
                                hlYearRange = hlDiv.get(0).text().trim();
                            }
                        }
                        if (spanText.contains("Earnings") && spanText.contains("per")
                                && spanText.contains("share")){
                            Elements hlDiv = item.getElementsByTag("div");
                            if (hlDiv != null && hlDiv.size() > 0){
                                eps = hlDiv.get(0).text();
                            }
                        }
                    }
                    x.setEps(getDoubleFromString(eps));
                }
                if (hlYearRange != null){
                    hlYearRange = hlYearRange.replace(",","");
                    String[] hlvalues = hlYearRange.split("/");
                    if (hlvalues != null && hlvalues.length > 1){
                        x.set_52WeekLowPrice(BigDecimal.valueOf(getDoubleFromString(hlvalues[0])));
                        x.set_52WeekHighPrice(BigDecimal.valueOf(getDoubleFromString(hlvalues[1]))  );
                    }
                }
                set52HighLowDifference(x);
                x.setTimestamp(Instant.now());
            }

        }catch (Exception e) {
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();

            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private List<FtseStockInfo> getFtseStockInfosFile(String fileName) {
        try {
            return objectMapper.readValue(new ClassPathResource(fileName).getInputStream(), new TypeReference<List<FtseStockInfo>>(){});
        } catch (Exception ex) {
            ERROR_LOGGER.error(Instant.now() + ", Error ->", ex);
            ex.printStackTrace();
            return null;
        }
    }


    private WebDriver launchBrowser() {
        System.out.println("AllStockResearchService.launchBrowser" + System.getProperty("user.dir"));
        try{
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
//            System.setProperty("webdriver.chrome.webDriver","D:\\Software\\chromedriver_win32\\chromedriver.exe");
            webDriver = new ChromeDriver();
//            webDriver = new ChromeDriver(options);
            webDriver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
//            webDriver.manage().window().
//            webDriver.get(url);
//            webDriver.navigate().refresh();
            Thread.sleep(200 );
//            webDriver.navigate().refresh();
//            Thread.sleep(200 * 5);
            for (int i = 0; i < 3; i++) {
                try {
                    webDriver.navigate().refresh();
                    webDriver.get("https://www.londonstockexchange.com/stock/AZN/astrazeneca-plc");
                    try { webDriver.findElement(By.id("ccc-notify-accept")).click(); } catch (Exception e) { }
                    Thread.sleep(200 );
                }catch (Exception e){}
            }


        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            //if (webDriver != null) webDriver.close();
            return null;
        }
        return webDriver;
    }

    private void set52HighLowDifference(FtseStockInfo x) {
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
    }

}