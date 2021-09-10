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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.SensexStockInfo;
import stock.research.utility.SensexStockResearchUtility;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.time.Instant.now;
import static stock.research.utility.SensexStockResearchUtility.getBigDecimalFromString;
import static stock.research.utility.SensexStockResearchUtility.getDoubleFromString;

@Service
public class SensexStockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(SensexStockResearchService.class);
    List<String> excludedIsinCodeList = Arrays.asList(new String[]{"INE501A01019", "INE220G01021", "INE785M01013", "INE355A01028","INE107A01015","INE049A01027",
            "INE883N01014","INE508G01029","INE421C01016","INE725A01022","INE852F01015","INE199G01027",
            "INE836F01026","INE224A01026","INE999A01015","INE974H01013","INE839M01018",
            "INE164A01016","INE310A01015","INE493A01027","INE979B01015","INE399C01030","INE739E01017",
            "INE040H01021", "INE665A01038","INE659A01023","INE348B01021",
            "INE786A01032","INE150G01020", "INE896L01010", "INE490G01020","INE269B01029",
            "INE482A01020","INE383A01012","INE634I01029","INE180C01026","INE672A01018","INE260B01028",
            "INE710A01016","INE668F01031","INE258B01022","INE873D01024",
            "INE342J01019","INE016A01026", "INE002L01015","INE752E01010" , "INE320J01015",
            "INE768C01010","INE733E01010","INE325A01013","INE589A01014",});
    @Autowired
    private ObjectMapper objectMapper;

    public static List<SensexStockInfo> getCacheSensexStockInfosList() {
        return cacheSensexStockInfosList;
    }

    private static List<SensexStockInfo> cacheSensexStockInfosList = new ArrayList<>();

    @Autowired
    RestTemplate restTemplate;

    private WebDriver webDriver;

    @PostConstruct
    public void setUp(){
//        this.webDriver = launchBrowser();
    }

    public List<SensexStockInfo> getSensex500StockInfo() {
        ResponseEntity<String> response = null;
        List<SensexStockInfo> sensexStockInfos = new ArrayList<>();
        try {

            String url = "https://www.moneycontrol.com/india/stockpricequote/";

//            response = makeRestCall(url + "others");
//            extractedAndPopulate(response, sensexStockInfos);


            char c;
//            for(c = 'A'; c <= 'A'; ++c){
            for(c = 'Q'; c <= 'Z'; ++c){
                LOGGER.info(c + " ");
                response = makeRestCall(url + c);
                for (int i = 0; i < 2; i++) {
                    if (response == null){
                        response = makeRestCall(url + c);
                    }
                }
                extractedAndPopulate(response, sensexStockInfos);
            }


        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        return sensexStockInfos;
    }

    private void extractedAndPopulate(ResponseEntity<String> response, List<SensexStockInfo> sensexStockInfos) {
        Document doc = Jsoup.parse(response.getBody());
        Elements tableElements =  doc.getElementsByClass("pcq_tbl MT10");
        if (tableElements != null && tableElements.size() > 0){
            for (Element e : tableElements){
                Elements anchorElements =  e.getElementsByTag("a");
                for (Element ahref : anchorElements){
                    sensexStockInfos.add(new SensexStockInfo(ahref.text(), ahref.attr("href")));
                }
            }
        }
    }

    public List<SensexStockInfo> populateStocksAttributes() {
        List<SensexStockInfo> populatedSensexStockInfosList = new ArrayList<>();
        List<SensexStockInfo> resultSensexStockInfosList = new ArrayList<>();
        try {
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();
        }catch (Exception e){
            webDriver = null;
            webDriver = launchBrowser();
        }

        try {

//            sensexStockInfosList = objectMapper.readValue(new ClassPathResource("top500.json").getInputStream(), new TypeReference<List<SensexStockInfo>>(){});
            LinkedHashMap<String,String> stockUrlsMap = objectMapper.readValue(new ClassPathResource("mktCap1K/top5KMktCapUrlInfo.json").getInputStream(), new TypeReference<LinkedHashMap<String,String>>(){});

//            sensexStockInfosList = getSensex500StockInfo();

//            sensexStockInfosList.stream().map(SensexStockInfo::getStockURL).limit(125).forEach(x -> {
            stockUrlsMap.entrySet().stream().map(x -> x.getValue()).limit(600).forEach(x -> {
                ResponseEntity<String> response = null;

                LOGGER.info("SensexStockResearchService::StockURL ->  " + x);
//                 response = makeRestCall("https://www.moneycontrol.com/india/stockpricequote/personal-care/jlmorisonindia/JLM");
                 response = makeRestCall(x);

                for (int i = 0; i < 2; i++) {
                    if (response == null){
                        response = makeRestCall(x);
                    }else {
                        break;
                    }
                }
//                    sleep(1000 * 1);
                boolean isException = false;
                try {
                    if (response != null && response.getBody() != null){
                        SensexStockInfo sensexStockInfo = new SensexStockInfo();
                        sensexStockInfo.setStockURL(x);
                        Document doc = Jsoup.parse(response.getBody());
                        if (doc.getElementById("nsecp") != null){
                            sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(doc.getElementById("nsecp").text()));
                        }
                        if (doc.getElementById("bsecp") != null && sensexStockInfo.getCurrentMarketPrice() == null){
                            sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(doc.getElementById("bsecp").text()));
                        }
                        if ((sensexStockInfo.getCurrentMarketPrice() == null || sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) <= 0)
                                && doc.getElementById("JLM_lastPrice") != null){
                            sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(doc.getElementById("JLM_lastPrice").text()));
                        }
                        if (doc.getElementById("sp_yearlylow") != null){
                            sensexStockInfo.set_52WeekLowPrice(getBigDecimalFromString(doc.getElementById("sp_yearlylow").text()));
                        }
                        if (doc.getElementById("sp_yearlyhigh") != null){
                            sensexStockInfo.set_52WeekHighPrice(getBigDecimalFromString(doc.getElementById("sp_yearlyhigh").text()));
                        }
                        if (doc.getElementById("stockName") != null
                                && doc.getElementById("stockName").getElementsByTag("h1") != null
                                && doc.getElementById("stockName").getElementsByTag("h1").size() > 0){
                            sensexStockInfo.setStockName(doc.getElementById("stockName").getElementsByTag("h1").get(0).text());
                        }
                        if (doc.getElementsByClass("nsemktcap bsemktcap") != null
                                && doc.getElementsByClass("nsemktcap bsemktcap").size() > 0){
                            sensexStockInfo.setStockMktCap(getDoubleFromString(doc.getElementsByClass("nsemktcap bsemktcap").get(0).text()));
                        }
                        if ((sensexStockInfo.getStockMktCap() == null || sensexStockInfo.getStockMktCap() <= 0)
                                && doc.getElementById("JLM_marketCap") != null){
                            sensexStockInfo.setStockMktCap(getDoubleFromString(doc.getElementById("JLM_marketCap").text()));
                        }
                        if (doc.getElementsByClass("nseceps bseceps") != null
                                && doc.getElementsByClass("nseceps bseceps").size() > 0){
                            sensexStockInfo.setEps(getDoubleFromString(doc.getElementsByClass("nseceps bseceps").get(0).text()));
                        }
                        if (doc.getElementsByClass("nsepe bsepe") != null
                                && doc.getElementsByClass("nsepe bsepe").size() > 0){
                            sensexStockInfo.setP2eps(getDoubleFromString(doc.getElementsByClass("nsepe bsepe").get(0).text()));
                        }
                        if (doc.getElementsByClass("nsebv bsebv") != null
                                && doc.getElementsByClass("nsebv bsebv").size() > 0){
                            sensexStockInfo.setBv(getDoubleFromString(doc.getElementsByClass("nsebv bsebv").get(0).text()));
                        }
                        if (sensexStockInfo.getBv()!= null){
                            sensexStockInfo.setP2bv(sensexStockInfo.getCurrentMarketPrice().doubleValue()/sensexStockInfo.getBv());
                        }
                        if (doc.getElementsByClass("sharhold_insight") != null
                                && doc.getElementsByClass("sharhold_insight").size() > 0
                                && doc.getElementsByClass("sharhold_insight").get(0) != null
                                && doc.getElementsByClass("sharhold_insight").get(0).getElementsByTag("td") != null
                                && doc.getElementsByClass("sharhold_insight").get(0).getElementsByTag("td").size() > 8){

                                    sensexStockInfo.setFiiPct(getDoubleFromString(doc.getElementsByClass("sharhold_insight")
                                        .get(0).getElementsByTag("td").get(8).text()));
                        }

                        System.out.println("SensexStockResearchService.populateStocksAttributes" + sensexStockInfo);

                        set52HighLowPriceDiff(sensexStockInfo);
                        if (sensexStockInfo.getFiiPct() == null){
                            sensexStockInfo.setFiiPct(0.0);
                        }
                        sensexStockInfo.setTimestamp(Instant.now());
                        try {
                            if (sensexStockInfo.getCurrentMarketPrice() == null ||
                                    sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) == 0
                                    || sensexStockInfo.getStockMktCap() == null || sensexStockInfo.getStockMktCap() == 0){


                                for (int i = 0; i < 2; i++) {
                                    if (webDriver == null){
                                        webDriver = launchBrowser();
                                    }
                                    if (webDriver != null){
                                        webDriver.get(sensexStockInfo.getStockURL());
										try{
											if((sensexStockInfo.getStockMktCap() == null ||
													sensexStockInfo.getStockMktCap()  == 0) && (webDriver.findElement(By.cssSelector(".nsemktcap.bsemktcap")) != null)){
												sensexStockInfo.setStockMktCap(getDoubleFromString(webDriver.findElement(By.cssSelector(".nsemktcap.bsemktcap")).getText()));
											}
										}catch(Exception e1){}

                                        if((sensexStockInfo.getCurrentMarketPrice() == null ||
                                                sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  == 0) &&
                                                ( webDriver.findElement(By.cssSelector(".pcstkspr.nsestkcp.bsestkcp.futstkcp.optstkcp")) != null )){
                                            sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(webDriver.findElement(By.cssSelector(".pcstkspr.nsestkcp.bsestkcp.futstkcp.optstkcp")).getText()));
                                        }

                                        if((sensexStockInfo.getCurrentMarketPrice() == null ||
                                                sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  == 0) &&
                                                ( webDriver.findElement(By.cssSelector(".nseopn.bseopn")) != null )){
                                            sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(webDriver.findElement(By.cssSelector(".nseopn.bseopn")).getText()));
                                        }
                                        try{
                                            if((sensexStockInfo.getCurrentMarketPrice() == null ||
                                                    sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  == 0) &&
                                                    (webDriver.findElement(By.id("sp_high")) != null)){
                                                sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(webDriver.findElement(By.id("sp_high")).getText()));
                                            }
                                            if((sensexStockInfo.get_52WeekHighPrice() == null ||
                                                    sensexStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO)  == 0)
                                                    && ( webDriver.findElement(By.id("sp_yearlyhigh")) != null )){
                                                sensexStockInfo.set_52WeekHighPrice(getBigDecimalFromString(webDriver.findElement(By.id("sp_yearlyhigh")).getText()));
                                            }
                                            if((sensexStockInfo.get_52WeekLowPrice() == null ||
                                                    sensexStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO)  == 0)
                                                    && ( webDriver.findElement(By.id("sp_yearlylow")) != null )){
                                                sensexStockInfo.set_52WeekLowPrice(getBigDecimalFromString(webDriver.findElement(By.id("sp_yearlylow")).getText()));
                                            }
                                        }catch (Exception e){
                                            try{
                                                if((sensexStockInfo.getCurrentMarketPrice() == null ||
                                                        sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  == 0) &&
                                                        (webDriver.findElement(By.cssSelector(".nseprvclose.bseprvclose")) != null)){
                                                    sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(webDriver.findElement(By.cssSelector(".nseprvclose.bseprvclose")).getText()));
                                                }
                                                if((sensexStockInfo.get_52WeekHighPrice() == null ||
                                                        sensexStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO)  == 0)
                                                        && ( webDriver.findElement(By.cssSelector(".nseH52.bseH52")) != null )){
                                                    sensexStockInfo.set_52WeekHighPrice(getBigDecimalFromString(webDriver.findElement(By.cssSelector(".nseH52.bseH52")).getText()));
                                                }
                                                if((sensexStockInfo.get_52WeekLowPrice() == null ||
                                                        sensexStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO)  == 0)
                                                        && ( webDriver.findElement(By.cssSelector(".nseL52.bseL52")) != null )){
                                                    sensexStockInfo.set_52WeekLowPrice(getBigDecimalFromString(webDriver.findElement(By.cssSelector(".nseL52.bseL52")).getText()));
                                                }
                                            }catch (Exception ex){}

                                        }

                                        set52HighLowPriceDiff(sensexStockInfo);
                                        System.out.println("SensexStockResearchService.populateStocksAttributes" + sensexStockInfo);
                                    }
//                                    webDriver.findElement(By.cssSelector(".nsemktcap.bsemktcap")).getText()
//                                    webDriver.findElement(By.id("sp_high")).getText()
                                }
                            }
                        }catch (Exception e){
                            if (webDriver != null) webDriver.close();
                            webDriver = launchBrowser();

                            isException = true;
                            e.printStackTrace();
                        }
                        finally {
//                            if (webDriver != null) webDriver.close();
                        }

                        LOGGER.info(sensexStockInfo.toString());
                        if (isException == false) populatedSensexStockInfosList.add(sensexStockInfo);
                    }
                }catch (Exception e) {
                    if (webDriver != null) webDriver.close();
                    webDriver = launchBrowser();

                    ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
                    e.printStackTrace();
                }
            });

            resultSensexStockInfosList = populatedSensexStockInfosList.stream().filter(x -> x.getStockMktCap() != null && x.getCurrentMarketPrice() != null
                                                            && x.get_52WeekHighLowPriceDiff() != null
                                                            && x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO)  > 0
                                                            && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  > 0
                                                                &&  x.getStockMktCap() > 0).collect(Collectors.toList());
            resultSensexStockInfosList.sort(Comparator.comparing(SensexStockInfo::getStockMktCap,
                                                                        Comparator.nullsLast(Comparator.naturalOrder())).reversed());

            int i =1;
            for (SensexStockInfo x : resultSensexStockInfosList){
                x.setStockRankIndex(i++);
            }

            String fileName =  LocalDateTime.now() + SensexStockResearchUtility.HYPHEN;
            fileName = fileName.replace(":","-");
            fileName = fileName + "top"+  SensexStockResearchUtility.HYPHEN + resultSensexStockInfosList.get(0).getStockRankIndex()+  SensexStockResearchUtility.HYPHEN + resultSensexStockInfosList.get(resultSensexStockInfosList.size() - 1).getStockRankIndex() +  SensexStockResearchUtility.HYPHEN;
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\SensexStockDetailedInfo.json"),
                    objectMapper.writeValueAsString(resultSensexStockInfosList).getBytes());
            Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\SensexStock-1000-MktCap-detailedInfo.json"),
                    objectMapper.writeValueAsString(resultSensexStockInfosList.stream().filter(x -> x.getStockMktCap() >= 1000).collect(Collectors.toList())).getBytes());
            if (webDriver == null) webDriver.close();
            if (webDriver != null) webDriver.close();
            cacheSensexStockInfosList = resultSensexStockInfosList;
            return removeExcludedIsins(resultSensexStockInfosList);
        }catch (Exception e){
            if (webDriver != null) webDriver.close();
            webDriver = launchBrowser();
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
        if (webDriver == null) webDriver.close();
        if (webDriver != null) webDriver.close();
        cacheSensexStockInfosList = resultSensexStockInfosList;
        return removeExcludedIsins(resultSensexStockInfosList);
    }

    private void set52HighLowPriceDiff(SensexStockInfo sensexStockInfo) {
        if (sensexStockInfo.get_52WeekLowPrice() != null && sensexStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                sensexStockInfo.get_52WeekHighPrice() != null && sensexStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                sensexStockInfo.get_52WeekHighPrice().compareTo(sensexStockInfo.get_52WeekLowPrice()) > 0){
            sensexStockInfo.set_52WeekHighLowPriceDiff(((sensexStockInfo.get_52WeekHighPrice().subtract(sensexStockInfo.get_52WeekLowPrice()))
                    .divide(sensexStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
        }

        if (sensexStockInfo.get_52WeekHighPrice() != null && sensexStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                sensexStockInfo.getCurrentMarketPrice() != null && sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                sensexStockInfo.get_52WeekHighPrice().compareTo(sensexStockInfo.getCurrentMarketPrice()) > 0 &&
                (sensexStockInfo.get_52WeekHighPrice().subtract(sensexStockInfo.getCurrentMarketPrice())).compareTo(BigDecimal.ZERO) > 0 &&
                (sensexStockInfo.get_52WeekHighPrice().subtract(sensexStockInfo.getCurrentMarketPrice())).compareTo(BigDecimal.ZERO) > 0){
            sensexStockInfo.set_52WeekHighPriceDiff(((sensexStockInfo.get_52WeekHighPrice().subtract(sensexStockInfo.getCurrentMarketPrice()))
                    .divide(sensexStockInfo.getCurrentMarketPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
        }

        if (sensexStockInfo.get_52WeekLowPrice() != null && sensexStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                sensexStockInfo.getCurrentMarketPrice().compareTo(sensexStockInfo.get_52WeekLowPrice()) > 0){
            sensexStockInfo.set_52WeekLowPriceDiff(((sensexStockInfo.getCurrentMarketPrice().subtract(sensexStockInfo.get_52WeekLowPrice()))
                    .divide(sensexStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
        }
    }

    private WebDriver launchBrowser() {
        System.out.println("SensexStockResearchService.launchBrowser" + System.getProperty("user.dir"));
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
        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            //if (webDriver != null) webDriver.close();
            return null;
        }
        return webDriver;
    }


    private List<SensexStockInfo> removeExcludedIsins(List<SensexStockInfo> sensexStockInfosList) {
        return sensexStockInfosList
                .stream()
                .filter(c ->
                        ((excludedIsinCodeList.stream().map(x -> x).collect(Collectors.toList())).contains(c.getIsin())) == false)
                .collect(Collectors.toList());
    }


    @Retryable(maxAttempts=10, value = RuntimeException.class,
            backoff = @Backoff(delay = 5000, multiplier = 2))
    public ResponseEntity<String> makeRestCall(String url) {
        ResponseEntity<String> response = null;
        try {
            Thread.sleep(10);
            LOGGER.info("URL -> " + url);
            response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
        }catch (Exception e){
            return null;
        }
        if (response == null || response.getStatusCode() != HttpStatus.OK){
            return null;
        }
        return response;
    }

}