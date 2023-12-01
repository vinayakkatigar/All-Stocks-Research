package stock.research.yfinance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stock.research.gfinance.service.GFinanceStockService;
import stock.research.yfinance.domain.Result;
import stock.research.yfinance.domain.YFinance;
import stock.research.yfinance.domain.YFinanceStockInfo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static java.lang.Thread.sleep;
import static java.math.BigDecimal.ZERO;
import static java.math.BigDecimal.valueOf;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.NyseStockResearchUtility.*;

@Service
public class YFStockService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(YFStockService.class);
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private GFinanceStockService gFinanceStockService;

    public List<YFinanceStockInfo> getYFStockInfoList(List<String> stockCodes) {
        List<YFinanceStockInfo> yFinanceStockInfoList = new ArrayList<>();
        List<List<String>> partitionedCodes = Lists.partition(stockCodes, 10);

        partitionedCodes.forEach(stocksCode -> {
            try {
                String yfFinance = null;
                int retry =5;
                while (retry-- > 0 && yfFinance == null){
                    yfFinance = queryYF(String.join(",", stocksCode).replaceAll("\\s+",""));
                }
                YFinance yFinance = objectMapper.readValue(yfFinance, YFinance.class) ;

                yFinanceStockInfoList.addAll(transformToGF(yFinance));
            } catch (Exception e) {
                ERROR_LOGGER.error("Error in getYFStockInfoList", e);
                e.printStackTrace();
            }
        });
        List<YFinanceStockInfo> yFinanceStockFilteredList = yFinanceStockInfoList.stream().filter(x -> x.getStockName() != null).collect(Collectors.toList());
        yFinanceStockFilteredList = yFinanceStockFilteredList.stream()
                .filter(q -> ((q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                        && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                        && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                        && (q.getMktCapFriendyValue() != null || q.getMktCapRealValue() != null))).distinct().collect(toList());


        yFinanceStockFilteredList.sort(Comparator.comparing(YFinanceStockInfo::getMktCapRealValue,
                nullsFirst(naturalOrder())).reversed());

        int i =1;
        for (YFinanceStockInfo x : yFinanceStockFilteredList){
            x.setStockRankIndex(i++);
        }

        return yFinanceStockFilteredList;
    }

    private List<YFinanceStockInfo> transformToGF(YFinance yfFinance) {
        List<YFinanceStockInfo> yFinanceStockInfoList = new ArrayList<>();

        if (yfFinance != null && yfFinance.getQuoteResponse() != null){
            yfFinance.getQuoteResponse().getResult().forEach(x -> {
                YFinanceStockInfo yFinanceStockInfo = new YFinanceStockInfo();
                try {
                    yFinanceStockInfo.setCurrentMarketPrice(x.getRegularMarketPrice() != null ? valueOf(x.getRegularMarketPrice()): x.getRegularMarketPrice() != null ? valueOf(x.getRegularMarketPrice()): ZERO);
                    yFinanceStockInfo.setStockName(x.getLongName());
                    if (x.getCurrency() != null && gFinanceStockService.getCcyValues() != null
                            && gFinanceStockService.getCcyValues().get(x.getCurrency()) != null){
                        yFinanceStockInfo.setMktCapRealValue((x.getMarketCap()) != null ? (gFinanceStockService.getCcyValues().get(x.getCurrency()).multiply(valueOf(x.getMarketCap()))).doubleValue() : 0d);
                        yFinanceStockInfo.setMktCapFriendyValue(x.getMarketCap() != null ? friendlyMktCap(((gFinanceStockService.getCcyValues().get(x.getCurrency()).multiply(valueOf(x.getMarketCap())))).doubleValue()) : "");
                    }else {
                        yFinanceStockInfo.setMktCapRealValue((x.getMarketCap()) != null ? Double.valueOf((x.getMarketCap())) : 0d);
                        yFinanceStockInfo.setMktCapFriendyValue(x.getMarketCap() != null ? friendlyMktCap(Double.valueOf((x.getMarketCap()))) : "");
                    }

                    yFinanceStockInfo.set_52WeekLowPrice(x.getFiftyTwoWeekLow() != null ? valueOf(x.getFiftyTwoWeekLow()) : ZERO);
                    yFinanceStockInfo.set_52WeekHighPrice(x.getFiftyTwoWeekHigh() != null ? valueOf(x.getFiftyTwoWeekHigh()) : ZERO);
                    yFinanceStockInfo.setP2e(x.getForwardPE() != null ? valueOf(x.getForwardPE()).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0d);
                    yFinanceStockInfo.setEps(x.getEpsForward() != null ? valueOf(x.getEpsForward()).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0d);
                    yFinanceStockInfo.setChangePct(x.getRegularMarketChangePercent() != null ? valueOf(x.getRegularMarketChangePercent()).setScale(2, RoundingMode.HALF_UP).doubleValue() : 0d);

                    if (yFinanceStockInfo.get_52WeekLowPrice() != null && yFinanceStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            yFinanceStockInfo.get_52WeekHighPrice() != null && yFinanceStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            ((yFinanceStockInfo.get_52WeekLowPrice())).compareTo(BigDecimal.ZERO) > 0){
                        yFinanceStockInfo.set_52WeekHighLowPriceDiff(((yFinanceStockInfo.get_52WeekHighPrice().subtract(yFinanceStockInfo.get_52WeekLowPrice()).abs())
                                .divide(yFinanceStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }

                    if (yFinanceStockInfo.get_52WeekHighPrice() != null && yFinanceStockInfo.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            ((yFinanceStockInfo.getCurrentMarketPrice())).compareTo(BigDecimal.ZERO) > 0 ){
                        yFinanceStockInfo.set_52WeekHighPriceDiff(((yFinanceStockInfo.get_52WeekHighPrice().subtract(yFinanceStockInfo.getCurrentMarketPrice()).abs())
                                .divide(yFinanceStockInfo.getCurrentMarketPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }

                    if (yFinanceStockInfo.get_52WeekLowPrice() != null && yFinanceStockInfo.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            ((yFinanceStockInfo.get_52WeekLowPrice())).compareTo(BigDecimal.ZERO) > 0){
                        yFinanceStockInfo.set_52WeekLowPriceDiff(((yFinanceStockInfo.getCurrentMarketPrice().subtract(yFinanceStockInfo.get_52WeekLowPrice()).abs())
                                .divide(yFinanceStockInfo.get_52WeekLowPrice(), 2, RoundingMode.HALF_UP)).multiply(new BigDecimal(100)));
                    }
                    yFinanceStockInfo.setStockTS(Timestamp.from(Instant.now()));
                    yFinanceStockInfo.setTimestamp(Instant.now());

                }catch (Exception e){
                    ERROR_LOGGER.error(stringfy(x) + "Error", e );
                }
                yFinanceStockInfoList.add(yFinanceStockInfo);
            });
        }
        return yFinanceStockInfoList;

    }

    private String stringfy(Result result) {
        try {
            return objectMapper.writeValueAsString(result);
        } catch (JsonProcessingException e) {
            ERROR_LOGGER.error("stringfy", e);
            return null;
        }
    }


    private String queryYF(String stockCode) {
        String output = null;

        try {
//            stockCode = "MSFT,GOOG,TSLA,IFF,SCHW";
            String command = "powershell.exe  " + System.getProperty("user.dir") + "\\src\\main\\resources\\YF\\yfiance.ps1 " + "'"+  stockCode + "'" ;
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
            try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()))) {
                while ((output = stdout.readLine()) != null) {
                    return output;
                }
            }
            try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getErrorStream()))) {
                while ((output = stderr.readLine()) != null) {
                    return null;
                }
            }

        }catch (Exception exception){
            ERROR_LOGGER.error("YF Query Error ->", exception);
            return null;
        }

        return output;
    }

    private String friendlyMktCap(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }
    private void goSleep(int x) {
        try { sleep(1000 * x);} catch (Exception e) { }
    }

}