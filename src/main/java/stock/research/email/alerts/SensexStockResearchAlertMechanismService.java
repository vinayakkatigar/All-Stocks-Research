package stock.research.email.alerts;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import stock.research.domain.PortfolioInfo;
import stock.research.domain.SensexStockInfo;
import stock.research.service.SensexStockResearchService;
import stock.research.utility.StockResearchUtility;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;
import static stock.research.utility.SensexStockResearchUtility.*;

@Service
public class SensexStockResearchAlertMechanismService {

    enum StockCategory{LARGE_CAP, MID_CAP, SMALL_CAP};
    enum SIDE{BUY, SELL};
    private static final Logger LOGGER = LoggerFactory.getLogger(SensexStockResearchAlertMechanismService.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private List<PortfolioInfo> portfolioInfoList = new ArrayList<>();
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private SensexStockResearchService sensexStockResearchService;

    private List<String> pfStockCode = new ArrayList<>();
//    private List<String> isinCodeList = Arrays.asList(new String[]{"INE117A01022","INE423A01024","INE438A01022","INE406A01037","INE238A01034","INE296A01024","INE545U01014","INE028A01039","INE258A01016","INE257A01026","INE118H01025","INE476A01014","INE752H01013","INE371A01025","INE121A01024","INE522F01014","INE335K01011","INE298A01020","INE499A01024","INE202B01012","INE917M01012","INE532F01054","INE510A01028","INE452O01016","INE752P01024","INE935A01035","INE047A01021","INE040A01034","INE001A01036","INE795G01014","INE545A01016","INE066F01012","INE267A01025","INE274G01010","INE090A01021","INE726G01019","INE763G01038","INE092T01019","INE095A01012","INE148I01020","INE562A01011","INE919H01018","INE242A01010","INE335Y01012","INE069I01010","INE646L01027","INE858B01029","INE749A01030","INE019A01038","INE237A01028","INE018A01030","INE115A01026","INE774D01024","INE101A01026","INE122R01018","INE775A01035","INE868B01028","INE093I01010","INE274J01014","INE213A01029","INE140A01024","INE572E01012","INE811K01011","INE615P01015","INE976G01028","INE013A01015","INE542F01012","INE217K01011","INE036A01016","INE020B01018","INE123W01016","INE498B01024","INE722A01011","INE721A01013","INE671H01015","INE062A01020","INE155A01022","INE081A01012","INE691A01018","INE692A01016","INE628A01036","INE694A01020","INE956G01038","INE205A01025","INE398A01010","INE528G01035"});
    private Set<String> isinCodeSet = new HashSet<>();
    private Map<String, Double> stockAlertPrice = new HashMap<>();

    @Scheduled(cron = "0 35 5 ? * MON-FRI")
    public void kickOffEmailAlerts() {
        long start = System.currentTimeMillis();
        LOGGER.info(Instant.now()+ " <- Started SensexStockResearchAlertMechanismService::kickOffEmailAlerts");
        List<SensexStockInfo> populatedSensexList = get500StocksAttributes();
        Arrays.stream(StockCategory.values()).forEach(x -> {
            generateAlertEmails(populatedSensexList,x, SIDE.SELL);
            generateAlertEmails(populatedSensexList, x, SIDE.BUY);
        });
        generateStockPriceAlerts(populatedSensexList);
        LOGGER.info(Instant.now()+ " <- Ended SensexStockResearchAlertMechanismService::kickOffEmailAlerts" + (System.currentTimeMillis() - start));
    }

    private List<SensexStockInfo>  get500StocksAttributes() {
        List<SensexStockInfo> populatedSensexList = new ArrayList<>();
        try {
            LOGGER.info("SensexStockResearchAlertMechanismService::get500StocksAttributes");
            populatedSensexList = sensexStockResearchService.populateStocksAttributes();
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
        return populatedSensexList;
    }
    private void generateAlertEmails(List<SensexStockInfo> populatedSensexList, StockCategory stockCategory, SIDE side) {
        try {
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::generateAlertEmails");
            LOGGER.info("stockCategory = " + stockCategory + ", side = " + side);
            List<SensexStockInfo> populatedLargeCapSensexList = null;
            List<SensexStockInfo> populatedMidCapSensexList = null;
            List<SensexStockInfo> populatedSmallCapSensexList = null;

            if (stockCategory == StockCategory.LARGE_CAP){
                populatedLargeCapSensexList = populatedSensexList.parallelStream()
                                        .filter(x -> x.getStockRankIndex() <= LARGE_CAP).collect(toList());
                //Filter large Cap with Y/L diff of 75 or more
                populatedLargeCapSensexList = populatedLargeCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() <= LARGE_CAP &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(75)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedLargeCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                sendEmail(dataBuffer, subjectBuffer);
            }
            if (stockCategory == StockCategory.MID_CAP){
                populatedMidCapSensexList = populatedSensexList.parallelStream()
                        .filter(x -> x.getStockRankIndex() > 150 && x.getStockRankIndex() <= 300).collect(toList());

                //Filter Mid Cap with Y/L diff of 100 or more
                populatedMidCapSensexList = populatedMidCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() > LARGE_CAP &&  x.getStockRankIndex() <= 300 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(100)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedMidCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                sendEmail(dataBuffer, subjectBuffer);
            }
            if (stockCategory == StockCategory.SMALL_CAP){
                populatedSmallCapSensexList = populatedSensexList.parallelStream()
                        .filter(x -> x.getStockRankIndex() > 300).collect(toList());

                //Filter Small Cap with Y/L diff of 125 or more
                populatedSmallCapSensexList = populatedSmallCapSensexList.stream()
                        .filter(x -> x.getStockRankIndex() > 300 &&  x.get_52WeekHighLowPriceDiff().compareTo(new BigDecimal(125)) > 0).collect(toList());

                StringBuilder dataBuffer = new StringBuilder("");
                final StringBuilder subjectBuffer = new StringBuilder("");

                generateHTMLContent(populatedSmallCapSensexList, stockCategory, side, dataBuffer, subjectBuffer);
                sendEmail(dataBuffer, subjectBuffer);
            }
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::generateAlertEmails");
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void generateHTMLContent(List<SensexStockInfo> populatedSensexList, StockCategory stockCategory, SIDE side, StringBuilder dataBuffer, StringBuilder subjectBuffer) {
        if (populatedSensexList != null && populatedSensexList.size() >0){
            populatedSensexList.stream().forEach(x -> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ) {
                    if (stockCategory == StockCategory.LARGE_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && (x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(3.5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Large Cap Alert**");
                        }
                        if (!(checkPortfolioSizeAndQty(x.getIsin()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.MID_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(2.0)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Mid Cap Alert**");
                        }

                        if (!(checkPortfolioSizeAndQty(x.getIsin()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.SMALL_CAP && side == SIDE.BUY && x.get_52WeekLowPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekLowPrice())  <= 0 )
                            || x.get_52WeekLowPriceDiff().compareTo(new BigDecimal(1.5)) <= 0)){
                        if ("".equalsIgnoreCase(subjectBuffer.toString())){
                            subjectBuffer.append("** Sensex Buy Small Cap Alert**");
                        }
                        if (!(checkPortfolioSizeAndQty(x.getIsin()))){
                            generateTableContents(dataBuffer, x);
                        }
                    }
                    if (stockCategory == StockCategory.LARGE_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(3.0)) <= 0)){
                        if ((isinCodeSet.contains(x.getIsin()) || pfStockCode.contains(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Large Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getIsin()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                    if (stockCategory == StockCategory.MID_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(2.0)) <= 0)){
                        if ((isinCodeSet.contains(x.getIsin()) || pfStockCode.contains(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Mid Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getIsin()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                    if (stockCategory == StockCategory.SMALL_CAP && side == SIDE.SELL && x.get_52WeekHighPriceDiff() != null
                            && ((x.getCurrentMarketPrice().compareTo(x.get_52WeekHighPrice())  >= 0 )
                            || x.get_52WeekHighPriceDiff().compareTo(new BigDecimal(1.0)) <= 0)){
                        if ((isinCodeSet.contains(x.getIsin()) || pfStockCode.contains(x.getStockCode()))){
                            if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                subjectBuffer.append("** Sensex Sell Small Cap Alert**");
                            }
                            if ((checkPortfolioSizeAndQty(x.getIsin()))){
                                generateTableContents(dataBuffer, x);
                            }
                        }
                    }
                }
            });
        }
    }

    private boolean checkPortfolioSizeAndQty(String isin) {
        return portfolioInfoList.stream().filter(x -> x.getiSIN().equalsIgnoreCase(isin))
                .anyMatch(y -> ((y.getValueAtMarketPrice() != null && getDoubleFromString(y.getValueAtMarketPrice()) > 45000.0)
                        && (y.getQty() != null && Double.parseDouble(y.getQty()) > 10.0)));
    }


    private void generateStockPriceAlerts(List<SensexStockInfo> populatedSensexList) {
        try {
            LOGGER.info("<- Started SensexStockResearchAlertMechanismService::generateStockPriceAlerts");

            StringBuilder dataBuffer = new StringBuilder("");
            final StringBuilder subjectBuffer = new StringBuilder("");
            if (populatedSensexList != null && populatedSensexList.size() >0){
                populatedSensexList.stream().forEach(x -> {
                    if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                            x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ) {
                                if (stockAlertPrice.containsKey(x.getIsin()) && stockAlertPrice.get(x.getIsin()) != null
                                        && stockAlertPrice.get(x.getIsin()) > x.getCurrentMarketPrice().doubleValue() ){
                                    if ("".equalsIgnoreCase(subjectBuffer.toString())){
                                        subjectBuffer.append("** Sensex Buy Price Stock Alert**");
                                    }
                                    generateTableContents(dataBuffer, x);
                                }
                    }
                });
            }
            sendEmail(dataBuffer, subjectBuffer);
            LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::generateStockPriceAlerts");
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + "<- , Error ->", e);
        }
    }

    private void sendEmail(StringBuilder dataBuffer, StringBuilder subjectBuffer) throws MessagingException, IOException {
        LOGGER.info("<- Started SensexStockResearchAlertMechanismService::sendEmail");
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);
        String data = StockResearchUtility.HTML_START;
        data += dataBuffer.toString();
        data += StockResearchUtility.HTML_END;

        if ("".equalsIgnoreCase(dataBuffer.toString()) == false &&
                "".equalsIgnoreCase(subjectBuffer.toString()) == false){
            helper.setFrom("stockalert@stockalert.com");
            helper.setTo(new String[]{"raghukati1950@gmail.com"});
//            helper.setTo(new String[]{"raghukati1950@gmail.com","raghu.kat@outlook.com"});
            helper.setText(data, true);
            helper.setSubject(subjectBuffer.toString());
            String fileName = subjectBuffer.toString();
            fileName = fileName.replace("*", "");
            fileName = fileName.replace(" ", "");
            fileName =  fileName + "-" + LocalDateTime.now()  ;
            fileName = fileName.replace(":","-");
            Files.write(Paths.get(System.getProperty("user.dir") + "\\target\\" + fileName  + ".html"), data.getBytes());
            FileSystemResource file = new FileSystemResource(System.getProperty("user.dir")  + "\\target\\" + fileName + ".html");
            helper.addAttachment(file.getFilename(), file);
            javaMailSender.send(message);
        }
        LOGGER.info("<- Ended SensexStockResearchAlertMechanismService::sendEmail");
    }

    @PostConstruct
    public void setUpPortfolioData(){
        try {
            CsvMapper csvMapper = new CsvMapper();
            CsvSchema schema = CsvSchema.emptySchema().withHeader();
            ObjectReader oReader = csvMapper.reader(PortfolioInfo.class).with(schema);
            try ( InputStream inputStream  =  new ClassPathResource("ICICIDEMAT_PortFolioEqtSummary.csv").getInputStream()) {
                MappingIterator<PortfolioInfo> mi = oReader.readValues(inputStream);
                portfolioInfoList = mi.readAll();
            }

            if (portfolioInfoList != null && portfolioInfoList.size() > 0){
                portfolioInfoList.parallelStream().forEach(x -> {
                    pfStockCode.add(x.getSymbol());
                    isinCodeSet.add(x.getiSIN());
                });
            }

            Map<String, Double> stockAlertPriceData  = objectMapper.readValue(new ClassPathResource("stockPriceWatchListData.json").getInputStream(), new TypeReference<Map<String, Double>>(){});

            stockAlertPrice.put("INE939A01011", 350.0);
            stockAlertPrice.putAll(stockAlertPriceData);
            LOGGER.info("stockAlertPrice -> ");
//            LOGGER.info(stockAlertPrice.toString());
            LOGGER.info("portfolioList -> ");
//            LOGGER.info(portfolioInfoList.toString());
            LOGGER.info("pfStockCode -> ");
//            LOGGER.info(pfStockCode.toString());
            LOGGER.info("isinCode -> ");
//            LOGGER.info(isinCodeSet.toString());
        } catch(Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", Error -> ", e);
            e.printStackTrace();
        }

    }

}
