package stock.research.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.SensexStockInfo;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static java.lang.Thread.sleep;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.getBigDecimalFromString;
import static stock.research.utility.StockResearchUtility.getDoubleFromString;

@Service
public class ScreenerSensexStockResearchService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(ScreenerSensexStockResearchService.class);
    @Autowired
    private ObjectMapper objectMapper;

    public static List<SensexStockInfo> getCacheScreenerSensexStockInfosList() {
        return cacheScreenerSensexStockInfosList;
    }

    private static List<SensexStockInfo> cacheScreenerSensexStockInfosList = new ArrayList<>();

    @Autowired
    RestTemplate restTemplate;

    public List<SensexStockInfo> populateStocksAttributes() {
        Thread.currentThread().setPriority(Thread.MIN_PRIORITY);
        List<SensexStockInfo> populatedSensexStockInfosList = new ArrayList<>();
        List<SensexStockInfo> resultSensexStockInfosList = new ArrayList<>();

        try {
            List<String> stockUrlsMap = objectMapper.readValue(new ClassPathResource("ScreenerSensexStockURLInfo.json").getInputStream(), new TypeReference<List<String>>(){});

            stockUrlsMap.stream().forEach(x -> {
                goSleep(3);
                ResponseEntity<String> response = null;

                LOGGER.info("SensexStockResearchService::StockURL ->  " + x);
                 response = makeRestCall(x);

                for (int i = 0; i < 5; i++) {
                    if (response == null){
                        response = makeRestCall(x);
                    }else {
                        break;
                    }
                }
                boolean isException = false;
                try {
                    if (response != null && response.getBody() != null){
                        SensexStockInfo sensexStockInfo = new SensexStockInfo();
                        sensexStockInfo.setStockURL(x);
                        Document doc = Jsoup.parse(response.getBody());
                        try{
                            Element topDiv = doc.getElementById("top");
                            sensexStockInfo.setStockName(topDiv.getElementsByTag("h1").get(0).text());
                            String dailyPct = null;
                            if (topDiv.getElementsByClass("font-size-12 down margin-left-4") != null
                                    && topDiv.getElementsByClass("font-size-12 down margin-left-4").size() > 0){
                                dailyPct = topDiv.getElementsByClass("font-size-12 down margin-left-4").get(0).text();
                                dailyPct = dailyPct.replaceAll("%", "");
                            }
                            if (dailyPct == null && topDiv.getElementsByClass("font-size-12 up margin-left-4") != null
                                    && topDiv.getElementsByClass("font-size-12 up margin-left-4").size() > 0){
                                dailyPct = topDiv.getElementsByClass("font-size-12 up margin-left-4").get(0).text();
                                dailyPct = dailyPct.replaceAll("%", "");
                            }
                            sensexStockInfo.setDailyPCTChange(getBigDecimalFromString(dailyPct));
                        }catch (Exception e){
                            printError(e);
                        }try{
                            Element divElement = doc.getElementById("quarterly-shp");
                            if (divElement != null && divElement.getElementsByClass("data-table") != null
                                    && divElement.getElementsByClass("data-table").size() > 0){
                                Element tabElement = divElement.getElementsByClass("data-table").get(0);
                                Element trElement = null;
                                for (Element ele : tabElement.getElementsByTag("tr")){
                                    if (ele.text().contains("FIIs")){
                                        trElement = ele;
                                    }
                                }
                                Elements tdElementList = trElement != null ? trElement.getElementsByTag("td") : null;
                                if (tdElementList != null && tdElementList.size() > 0){
                                    sensexStockInfo.setFiiPct(getDoubleFromString(tdElementList.get(tdElementList.size() - 1).text()));
                                }
                            }
                        }catch (Exception e){
                            printError(e);
                        }
                        try {
                            Elements companyInfoElements = doc.getElementsByClass("company-ratios");
                            if (companyInfoElements != null && companyInfoElements.size() > 0){
                                Element companyInfoElement = companyInfoElements.get(0);
                                if (companyInfoElement != null){
                                    Element ulElement = companyInfoElement.getElementById("top-ratios");
                                    if (ulElement != null){
                                        Elements  lielements = ulElement.getElementsByTag("li");
                                        if (lielements != null && lielements.size() > 0){
                                            lielements.stream().forEach(li -> {
                                                Elements  spanelements = li.getElementsByTag("span");
                                                if (spanelements != null && spanelements.size() > 1){
                                                    if (spanelements.get(0).text().contains("Market")){
                                                        sensexStockInfo.setStockMktCap(getDoubleFromString(getKeyValue(spanelements)));
                                                    }
                                                    if (spanelements.get(0).text().contains("Current")){
                                                        sensexStockInfo.setCurrentMarketPrice(getBigDecimalFromString(replaceRupee(getKeyValue(spanelements))));
                                                    }
                                                    if (spanelements.get(0).text().contains("Stock") && spanelements.get(0).text().contains("P/E") ){
                                                        sensexStockInfo.setP2eps(getDoubleFromString(getKeyValue(spanelements)));
                                                    }
                                                    if (spanelements.get(0).text().contains("Book") && spanelements.get(0).text().contains("Value") ){
                                                        sensexStockInfo.setBv(getDoubleFromString(getKeyValue(spanelements)));
                                                    }
                                                    if (spanelements.get(0).text().contains("High") && spanelements.get(0).text().contains("Low")){
                                                        String[] highLow = getKeyValue(spanelements).split("/");
                                                        if (highLow != null && highLow.length > 1){
                                                            sensexStockInfo.set_52WeekHighPrice(getBigDecimalFromString(replaceRupee(highLow[0])));
                                                            sensexStockInfo.set_52WeekLowPrice(getBigDecimalFromString(replaceRupee(highLow[1])));
                                                        }
                                                    }
                                                }
                                            });
                                        }
                                    }
                                }
                            }

                        }catch (Exception e){
                            printError(e);
                        }
                        try {
                            if (sensexStockInfo.getBv()!= null && (sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)) > 0
                                    && Double.compare(sensexStockInfo.getBv(), 0.0) > 0){
                                sensexStockInfo.setP2bv(BigDecimal.valueOf(sensexStockInfo.getCurrentMarketPrice().doubleValue()/sensexStockInfo.getBv())
                                        .setScale(2, RoundingMode.HALF_UP)
                                        .doubleValue());
                            }
                            if (sensexStockInfo.getP2eps() != null && (sensexStockInfo.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)) > 0
                                    && Double.compare(sensexStockInfo.getP2eps(), 0.0) > 0){
                                sensexStockInfo.setEps(BigDecimal.valueOf(sensexStockInfo.getCurrentMarketPrice().doubleValue()/sensexStockInfo.getP2eps())
                                        .setScale(2, RoundingMode.HALF_UP)
                                        .doubleValue());
                            }
                        }catch (Exception e){
                            ERROR_LOGGER.error(sensexStockInfo + " <- Error ->", e);
                            e.printStackTrace();
                        }

                        set52HighLowPriceDiff(sensexStockInfo);
                        if (sensexStockInfo.getFiiPct() == null){
                            sensexStockInfo.setFiiPct(0.0);
                        }

                        sensexStockInfo.setQuoteInstant(Instant.now().toString());
                        sensexStockInfo.setStockTS(Timestamp.from(Instant.now()));

                        if (isException == false) populatedSensexStockInfosList.add(sensexStockInfo);
                        LOGGER.info(populatedSensexStockInfosList.size() + " <- Size::" + sensexStockInfo.toString());
                    }
                }catch (Exception e) {
                    printError(e);
                }
            });

            resultSensexStockInfosList = populatedSensexStockInfosList.stream()
                    .filter(x -> x.getStockMktCap() != null
                    && x.getCurrentMarketPrice() != null
                    && x.get_52WeekHighLowPriceDiff() != null
                    && x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.ZERO)  > 0
                    && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO)  > 0
                    &&  x.getStockMktCap() > 1999).distinct().collect(toList());

            resultSensexStockInfosList.sort(comparing(SensexStockInfo::getStockMktCap,
                                                                        nullsLast(Comparator.naturalOrder())).reversed());

            int i =1;
            for (SensexStockInfo x : resultSensexStockInfosList){
                x.setStockTS(Timestamp.from(Instant.now()));
                x.setQuoteInstant("" + Instant.now());
                x.setStockRankIndex(i++);
            }

            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\ScreenerSensexStockDetailedInfo.json"),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultSensexStockInfosList).getBytes());
            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\ScreenerSensexStock-1000-MktCap-detailedInfo.json"),
                    objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(resultSensexStockInfosList.stream().filter(x -> x.getStockMktCap() >= 1000).collect(toList())).getBytes());
            cacheScreenerSensexStockInfosList = resultSensexStockInfosList;
            return (resultSensexStockInfosList);
        }catch (Exception e) {
            printError(e);
        }
        cacheScreenerSensexStockInfosList = resultSensexStockInfosList;
        return (resultSensexStockInfosList);
    }

    private void printError(Exception e, String ... info) {
        e.printStackTrace();
        ERROR_LOGGER.error(info != null && info.length > 0 ? info[0] : "" + "Error -", e);
    }

    private void goSleep(int x) {
        try { sleep(1000 * x);} catch (Exception e) { }
    }

    private String replaceRupee(String input) {
        try {
            String rupee = "\u20B9";
            byte[] utf8 = rupee.getBytes("UTF-8");
            rupee = new String(utf8, "UTF-8");
            return input.replace(rupee,"").trim();
        }catch (Exception e){
            printError(e);
        }
        return null;
    }

    private String getKeyValue(Elements spanelements) {
        Elements  spanVlvelements = spanelements.get(1).getElementsByTag("span");
        if (spanVlvelements != null && spanVlvelements.size() > 0){
            return (spanVlvelements.get(0).text());
        }
        return null;
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

    public ResponseEntity<String> makeRestCall(String url) {
        ResponseEntity<String> response = null;
        try {
            goSleep(3);
            response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response == null || response.getStatusCode() != HttpStatus.OK){
                return null;
            }
        }catch (Exception e){
            printError(e, url);
            goSleep(10);
            return null;
        }finally {
            goSleep(2);
        }

        return response;
    }

}