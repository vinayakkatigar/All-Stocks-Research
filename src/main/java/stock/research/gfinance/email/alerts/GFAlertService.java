package stock.research.gfinance.email.alerts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stock.research.gfinance.domain.GFinanceStockInfo;
import stock.research.gfinance.domain.GoogleFinanceStockDetails;
import stock.research.gfinance.repo.GoogleFinanceStockDetailsRepositary;

import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import static java.lang.Math.abs;
import static java.sql.Timestamp.from;
import static java.time.Instant.now;
import static java.time.LocalDateTime.ofInstant;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.StockUtility.writeToFile;

@Service
public class GFAlertService {
    private static final Logger LOGGER = LoggerFactory.getLogger(GFAlertService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd").withZone( ZoneId.systemDefault() );
    @Autowired
    private GoogleFinanceStockDetailsRepositary googleFinanceStockDetailsRepositary;

    @Autowired
    private ObjectMapper objectMapper;

    public List<GFinanceStockInfo> runPnlLogicForSpecifiedDays(int noOfDays, double cutOffPct,
                                                               String fileName, String emailSubject, String country) {
        final List<GFinanceStockInfo> gfNYSEStockList = new CopyOnWriteArrayList<>();
        final List<GFinanceStockInfo> gfNYSEAlertList = new CopyOnWriteArrayList<>();
        List<GFinanceStockInfo> sortedGFNYSEStockAlertList = new ArrayList<>();

        List<GoogleFinanceStockDetails> gfNYSEStockInfoList = new CopyOnWriteArrayList<>();
        googleFinanceStockDetailsRepositary.findByCountryIgnoreCase(country).forEach(gfNYSEStockInfoList::add);

        List<GoogleFinanceStockDetails> gfNYSEStockInfoWeeklyList = gfNYSEStockInfoList.stream().filter(x -> {
            long difInMS = from(now()).getTime() - x.getStockTS().getTime();
            Long diffDays = difInMS / (1000 * 60 * 60 * 24);
            if (diffDays <= noOfDays && ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                    && ofInstant(x.getStockTS().toInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SUNDAY) {
                return true;
            } else {
                return false;
            }
        }).collect(toList());

        gfNYSEStockInfoWeeklyList.stream().sorted(comparing(GoogleFinanceStockDetails::getStockTS).reversed());

        gfNYSEStockInfoWeeklyList.stream().forEach(x -> {
            try {
                x.setGoogleFinanceStocksPayload(x.getGoogleFinanceStocksPayload().replaceAll("timestamp", "quoteInstant"));
            } catch (Exception e) {
                LOGGER.error("Error - ", e);
            }
            try {
                gfNYSEStockList.addAll(objectMapper.readValue(x.getGoogleFinanceStocksPayload(), new TypeReference<List<GFinanceStockInfo>>() {
                }));
            } catch (Exception e) {
                LOGGER.error("Error - ", e);
            }
        });

        List<GFinanceStockInfo> resultGFNYSEStockList = new CopyOnWriteArrayList<>(gfNYSEStockList);
        resultGFNYSEStockList.forEach(x -> {
            x.setStockInstant(Instant.parse(x.getQuoteInstant()));
        });

        resultGFNYSEStockList = resultGFNYSEStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed()).collect(toList());
        final List<GFinanceStockInfo> pnlForDaysData = new CopyOnWriteArrayList<>();

        if (resultGFNYSEStockList != null && resultGFNYSEStockList.size() > 0) {
            Map<String, List<GFinanceStockInfo>> gfNyseStockInfoWeeklyMap = resultGFNYSEStockList.stream().filter(Objects::nonNull)
                    .filter(x -> x.getStockName() != null)
                    .collect(groupingBy(GFinanceStockInfo::getStockName));

            gfNyseStockInfoWeeklyMap.forEach((key, weeklyPnlGFNyseStockList) -> {
                weeklyPnlGFNyseStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());
                weeklyPnlGFNyseStockList = weeklyPnlGFNyseStockList.stream().sorted(comparing(GFinanceStockInfo::getStockInstant).reversed()).collect(toList());

                final List<GFinanceStockInfo> pnlForDays = new CopyOnWriteArrayList<>();
                Instant instant = Instant.now();

                Map<String, GFinanceStockInfo> gfNyseStockInfoMap = new LinkedHashMap<>();

                for (int i = 0; i < noOfDays; i++) {
                    addAndRemoveSpecifiedDates(weeklyPnlGFNyseStockList, instant, i, gfNyseStockInfoMap);
                }
                gfNyseStockInfoMap.forEach((k, v) -> {
                    pnlForDays.add(v);
                    pnlForDaysData.add(v);
                });

                StringBuffer changePct = new StringBuffer("");
                final BigDecimal[] pct = {BigDecimal.ZERO};
                pnlForDays.stream().filter(Objects::nonNull).forEach(x -> {
                    changePct.append(" , " + x.getDailyPctChange());
                    pct[0] = pct[0].add(x.getDailyPctChange());
                });

                pnlForDays.stream().filter(Objects::nonNull).sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());
                pnlForDaysData.stream().filter(Objects::nonNull).sorted(comparing(GFinanceStockInfo::getStockInstant).reversed());

                if ((abs(pct[0].doubleValue()) >= cutOffPct)) {
                    if (pct[0].doubleValue() < 0d) {
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#990033;display:inline;\">" + pct[0] + "</h4> )");
                    } else {
                        pnlForDays.get(0).setStockName(pnlForDays.get(0).getStockName() + changePct.toString() +
                                "( <h4 style=\"background-color:#00e6e6;display:inline;\">" + pct[0] + "</h4> )");
                    }
                    gfNYSEAlertList.add(pnlForDays.get(0));
                }
            });
        }
        try {
            writeToFile( fileName + "_ALL_STOCKS_DATA", objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(pnlForDaysData));
        } catch (Exception e) {
            LOGGER.error("Error - ",e);
        }

        return gfNYSEAlertList;
    }



    private void addAndRemoveSpecifiedDates(List<GFinanceStockInfo> weeklyPnlGFNyseStockList,
                                            Instant instant, int i, Map<String, GFinanceStockInfo> gfNyseStockInfoMap) {
        for (GFinanceStockInfo x : weeklyPnlGFNyseStockList) {
            if ((Duration.between(x.getStockInstant(), instant).toDays() >= i)
                    && (Duration.between(x.getStockInstant(), instant).toDays() < (i + 1))) {
                if(ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek() != DayOfWeek.SATURDAY
                        && ofInstant(x.getStockInstant(), ZoneId.systemDefault()).getDayOfWeek()!= DayOfWeek.SUNDAY){
                    String key = dateTimeFormatter.format(x.getStockInstant());
                    if (!gfNyseStockInfoMap.containsKey(key)){
                        gfNyseStockInfoMap.put(key, x);
                    }else {
                        GFinanceStockInfo current = gfNyseStockInfoMap.get(key);
                        if (current != null && x.getStockInstant().isAfter(current.getStockInstant())){
                            gfNyseStockInfoMap.put(key, x);
                        }
                    }
                }
            }
        }
    }



}

