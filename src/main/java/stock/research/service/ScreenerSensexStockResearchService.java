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
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static stock.research.utility.SensexStockResearchUtility.getBigDecimalFromString;
import static stock.research.utility.SensexStockResearchUtility.getDoubleFromString;

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
            List<String> stockUrlsMap = objectMapper.readValue(new ClassPathResource("ScreenerSensexStockURLInfo.json").getInputStream(), new TypeReference<List<String>>(){});

            stockUrlsMap.stream().forEach(x -> {
                try { sleep(1000 * 3);} catch (Exception e) { }
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
                        }catch (Exception e){
                            e.printStackTrace();
                        }try{
                            Elements tablelements = doc.getElementsByClass("data-table");
                            if (tablelements != null && tablelements.size() > 5 && tablelements.get(tablelements.size() - 1) != null){
                                Elements trelements = tablelements.get(tablelements.size() - 1).getElementsByTag("tr");
                                if (trelements != null && trelements.size() > 2 && trelements.get(2) != null){
                                    Elements tdelements = trelements.get(2).getElementsByTag("td");
                                        if (tdelements != null && tdelements.size() > 11 && tdelements.get(tdelements.size() - 1 ) != null){
                                            sensexStockInfo.setFiiPct(getDoubleFromString(tdelements.get(tdelements.size() - 1 ).text()));
                                        }
                                    }
                                }
                        }catch (Exception e){
                            e.printStackTrace();
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
                        System.out.println(" @@@@@@ "+sensexStockInfo);

                        sensexStockInfo.setTimestamp(Instant.now().toString());

                        LOGGER.info(sensexStockInfo.toString());
                        if (isException == false) populatedSensexStockInfosList.add(sensexStockInfo);
                    }
                }catch (Exception e) {
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

            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\ScreenerSensexStockDetailedInfo.json"),
                    objectMapper.writeValueAsString(resultSensexStockInfosList).getBytes());
            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\ScreenerSensexStock-1000-MktCap-detailedInfo.json"),
                    objectMapper.writeValueAsString(resultSensexStockInfosList.stream().filter(x -> x.getStockMktCap() >= 1000).collect(Collectors.toList())).getBytes());
            cacheScreenerSensexStockInfosList = resultSensexStockInfosList;
            return (resultSensexStockInfosList);
        }catch (Exception e) {
            e.printStackTrace();
        }
        cacheScreenerSensexStockInfosList = resultSensexStockInfosList;
        return (resultSensexStockInfosList);
    }

    private String replaceRupee(String input) {
        try {
            String rupee = "\u20B9";
            byte[] utf8 = rupee.getBytes("UTF-8");
            rupee = new String(utf8, "UTF-8");
            return input.replace(rupee,"").trim();
        }catch (Exception e){}
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


    public ResponseEntity<String> makeRestCall(String url) {
        ResponseEntity<String> response = null;
        try {
            sleep(10);
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