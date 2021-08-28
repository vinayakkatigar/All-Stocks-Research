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
        this.webDriver = launchBrowser();
    }

    public List<FtseStockInfo> getFtseStockInfo(String urlInfo, int cnt) {
        ResponseEntity<String> response = null;
        List<FtseStockInfo> ftseStockInfoList = new ArrayList<>();

        try {
            LOGGER.info("<- Started FTSEStockResearchService::getFtseStockInfo:: -> ");
            for (int i = 1; i < cnt; i++) {
                String url = urlInfo + i;

                LOGGER.info("FTSEStockResearchService::getFtseStockInfo::url: -> " + url);
                response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
                Document doc = Jsoup.parse(response.getBody());
                Elements tableElements = doc.getElementsByTag("tbody");
                if (tableElements != null && tableElements.size() >0){
                    Elements trElements = tableElements.get(0).getElementsByClass("medium-font-weight slide-panel");
                    String stockName = null,stockURL = null,stockCode = null;
                    Double stockMktCap= 0d;
                    BigDecimal currentMarketPrice= BigDecimal.ZERO;
                    for (Element tr: trElements){
                        Elements tdCodeElements = tr.getElementsByClass("clickable bold-font-weight instrument-tidm gtm-trackable td-with-link");
                        if (tdCodeElements != null && tdCodeElements.size() > 0){
                            Elements aElements = tdCodeElements.get(0).getElementsByClass("dash-link blue-text");
                            if (aElements != null && aElements.size() > 0){
                                stockURL = LSE_URL + aElements.get(0).attr("href");
                                stockCode = aElements.get(0).text();
                            }
                        }

                        Elements tdNameElements = tr.getElementsByClass("clickable instrument-name gtm-trackable td-with-link");
                        if (tdNameElements != null && tdNameElements.size() > 0){
                            Elements aElements = tdNameElements.get(0).getElementsByClass("dash-link black-link ellipsed");
                            if (aElements != null && aElements.size() > 0){
                                stockName = aElements.get(0).text();
                            }
                        }

                        Elements tdMktCapElements = tr.getElementsByClass("instrument-marketcapitalization hide-on-landscape");
                        if (tdMktCapElements != null && tdMktCapElements.size() > 0){
                            String mktCap = tdMktCapElements.get(0).text();
                            if (mktCap != null){
                                mktCap = mktCap.replace(",", "");
                                stockMktCap = getDoubleFromString(mktCap);
                            }
                        }

                        Elements tdMktPriceElements = tr.getElementsByClass("instrument-lastprice");
                        if (tdMktPriceElements != null && tdMktPriceElements.size() > 0){
                            String mktPrice = tdMktPriceElements.get(0).text();
                            if (mktPrice != null){
                                mktPrice = mktPrice.replace(",", "");
                                currentMarketPrice = getBigDecimalFromString(mktPrice);
                            }
                        }

                        ftseStockInfoList.add(new FtseStockInfo(stockName, stockURL, stockCode, stockMktCap, currentMarketPrice));
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

            //            File ftse250StockInfosFile = new File(System.getProperty("user.dir") + "" + "\\top500DetailedInformation.json");
//            ftse250StockInfoList = objectMapper.readValue(ftse250StockInfosFile, new TypeReference<List<Ftse250StockInfo>>(){});

            ftseStockDetailedInfoList = getFtseStockInfo(urlInfo, cnt);

            ftseStockDetailedInfoList.stream().forEach(x -> {
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
                    ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
                    e.printStackTrace();
                }
            });
            ftseStockDetailedInfoList.stream().forEach(x -> {
//            ftseStockDetailedInfoList.stream().limit(15).forEach(x -> {
                if (x.get_52WeekLowPrice() == null || x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) == 0 ||
                        x.get_52WeekHighPrice() == null || x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) == 0 ||
                        x.get_52WeekHighLowPriceDiff() == null || x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO) == 0
                ) {
                    if (webDriver != null){
                        webDriver.get(x.getStockURL());
                        try { Thread.sleep(1000 * 10);} catch (Exception e) { }

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
            });

            ftseStockDetailedInfoList = ftseStockDetailedInfoList.stream().distinct().collect(Collectors.toList());
            if (cnt == FTSE_100_CNT){
                largeCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
            }
            if (cnt == FTSE_250_CNT){
                midCapCapCacheftseStockDetailedInfoList = ftseStockDetailedInfoList;
            }

            String fileName =  LocalDateTime.now() + HYPHEN  ;
            fileName = fileName.replace(":","-");
            fileName = fileName + "top"+  HYPHEN + ftseStockDetailedInfoList.get(0).getStockRankIndex()+  HYPHEN + ftseStockDetailedInfoList.get(ftseStockDetailedInfoList.size() - 1).getStockRankIndex() +  HYPHEN;
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\"+ fileName + "detailedInfo.json"),
                    objectMapper.writeValueAsString(ftseStockDetailedInfoList).getBytes());
            if (webDriver != null) webDriver.close();
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
        if (webDriver != null) webDriver.close();
        return (ftseStockDetailedInfoList);
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
        System.out.println("StockResearchService.launchBrowser" + System.getProperty("user.dir"));
        try{
//            ChromeOptions options = new ChromeOptions();
//            options.addArguments("--headless");
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
//            System.setProperty("webdriver.chrome.webDriver","D:\\Software\\chromedriver_win32\\chromedriver.exe");
            webDriver = new ChromeDriver();
//            webDriver = new ChromeDriver(options);
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);
//            webDriver.manage().window().
//            webDriver.get(url);
//            webDriver.navigate().refresh();
            Thread.sleep(200 );
//            webDriver.navigate().refresh();
//            Thread.sleep(200 * 5);
            webDriver.get("https://www.londonstockexchange.com/stock/AZN/astrazeneca-plc");
            try { webDriver.findElement(By.id("ccc-notify-accept")).click(); } catch (Exception e) { }

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