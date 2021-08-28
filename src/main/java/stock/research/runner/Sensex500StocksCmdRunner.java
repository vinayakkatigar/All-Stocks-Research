package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.SensexStockInfo;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;
import stock.research.service.SensexStockResearchService;
import stock.research.utility.SensexStockResearchUtility;

import javax.mail.internet.MimeMessage;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@SpringBootApplication
public class Sensex500StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksCmdRunner.class);

    @Autowired
    private SensexStockResearchService sensexStockResearchService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SensexStockResearchAlertMechanismService sensexStockResearchAlertMechanismService;

    @Override
    public void run(String... args) throws Exception {
//        getReport();
//        while (true)

//        sensexStockResearchService.populateStocksAttributesConcurrently();
        sensexStockResearchAlertMechanismService.kickOffEmailAlerts();

//        generateStockPriceAlertWatchList();
    }

//    @Scheduled(cron = "0 55 4,5,9,10 ? * MON-FRI")
    public void getReport() {
        try {
            LOGGER.info(Instant.now() + " <- Kicked off -> getReport");

            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.populateStocksAttributes();
            StringBuilder dataBuffer = new StringBuilder("");
            populatedSensexList.stream().forEach( x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ){
                    dataBuffer.append("<tr><td>" +x.getStockName() + SensexStockResearchUtility.HYPHEN + x.getStockCode()
                            + SensexStockResearchUtility.START_BRACKET + x.getStockMktCap() + SensexStockResearchUtility.END_BRACKET + "</td>");
                    dataBuffer.append("<td>" +x.getStockRankIndex() + "</td>");
                    dataBuffer.append("<td>" +x.getCurrentMarketPrice() + "</td>");
                    dataBuffer.append("<td>" +x.get_52WeekHighPrice() + "</td>");
                    dataBuffer.append("<td>" +x.get_52WeekLowPrice() + "</td>");
                    dataBuffer.append("<td>" +x.get_52WeekHighLowPriceDiff().setScale(2, RoundingMode.HALF_UP) + "</td>");
                    dataBuffer.append("<td>" +x.getFiiPct() + "</td>");
                    dataBuffer.append( "<td>" +(x.get_52WeekLowPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
                    dataBuffer.append("<td>" +(x.get_52WeekHighPriceDiff()).setScale(2, RoundingMode.HALF_UP) + "</td>");
                    dataBuffer.append("<td>" +  x.getP2eps() + SensexStockResearchUtility.START_BRACKET + x.getEps() + SensexStockResearchUtility.END_BRACKET + "</td>");
                    dataBuffer.append("<td>" + x.getBv() + SensexStockResearchUtility.START_BRACKET + x.getP2bv() + SensexStockResearchUtility.END_BRACKET + "</td>");
                    dataBuffer.append("</tr>");
                }
            });
            String htmlContent = SensexStockResearchUtility.HTML_START;
            htmlContent += dataBuffer.toString();
            htmlContent += SensexStockResearchUtility.HTML_END;

        if (htmlContent != null){
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setFrom("stockalert@stockalert.com");
            helper.setTo("raghukati1950@gmail.com");
            helper.setSubject("Top 500 Stock details!");
            helper.setText(htmlContent, true);

            Files.write(Paths.get(System.getProperty("user.dir") + "\\target" + "\\top500EmailContents.html"),htmlContent.getBytes());

            FileSystemResource file = new FileSystemResource(System.getProperty("user.dir") + "\\target" + "\\top500EmailContents.html");
            helper.addAttachment(file.getFilename(), file);

            javaMailSender.send(message);
        }

        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }
    }

    //Generate Map of Isin's against thier price to alert
    private void generateStockPriceAlertWatchList() {
        try {
            Map<String, Double> stockPriceData = new HashMap<>();
            LOGGER.info(Instant.now() + " <- Kicked off -> generateStockPriceAlertWatchList");

            List<SensexStockInfo> populatedSensexList = sensexStockResearchService.populateStocksAttributes();
//
//            Resource resource = new ClassPathResource("top-1-487-detailedInfo.json");
//            List<Sensex500StockInfo> populatedSensexList = objectMapper.readValue( resource.getInputStream(), new TypeReference<List<Sensex500StockInfo>>(){});

            List<SensexStockInfo> filteredTop150SensexList = populatedSensexList.stream().filter(x -> x.getFiiPct() != null)
                    .sorted(Comparator.comparing(SensexStockInfo::get_52WeekHighLowPriceDiff).reversed())
                    .limit(150).collect(Collectors.toList()).stream()
                    .filter(x -> (x.getFiiPct() >= 14 || x.getStockRankIndex() <=300)).collect(Collectors.toList());

            Files.write(Paths.get(System.getProperty("user.dir") + "\\target" + "\\filteredTop150SensexList.json"),
                    objectMapper.writeValueAsString(filteredTop150SensexList).getBytes());

            filteredTop150SensexList.forEach( x -> {
                stockPriceData.put(x.getIsin(), (x.get_52WeekLowPrice().multiply(BigDecimal.valueOf(1.01))).doubleValue());
            });
            populatedSensexList.removeAll(filteredTop150SensexList);
            filteredTop150SensexList.clear();
            filteredTop150SensexList = populatedSensexList.stream().filter(x -> x.getFiiPct() != null)
                    .sorted(Comparator.comparing(SensexStockInfo::get_52WeekHighLowPriceDiff).reversed())
                    .limit(250).collect(Collectors.toList()).stream()
                    .filter(x -> ( (x.getStockRankIndex() > 300  && x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.valueOf(240.0)) >= 0) || (x.getFiiPct() >= 4 && x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.valueOf(140.0)) >= 0))).collect(Collectors.toList());
            filteredTop150SensexList.forEach( x -> {
                stockPriceData.put(x.getIsin(), (x.get_52WeekLowPrice().multiply(BigDecimal.valueOf(1.05))).doubleValue());
            });
            populatedSensexList.removeAll(filteredTop150SensexList);
            filteredTop150SensexList.clear();
            filteredTop150SensexList = populatedSensexList.stream().filter(x -> x.getFiiPct() != null)
                    .sorted(Comparator.comparing(SensexStockInfo::get_52WeekHighLowPriceDiff).reversed())
                    .collect(Collectors.toList()).stream()
                    .filter(x -> (x.getStockRankIndex() <= 200 &&
                            (x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.valueOf(90.0)) >= 0)
                    ) || (x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.valueOf(140.0)) >= 0)).collect(Collectors.toList());
            filteredTop150SensexList.forEach( x -> {
                stockPriceData.put(x.getIsin(), (x.get_52WeekLowPrice().multiply(BigDecimal.valueOf(1.02))).doubleValue());
            });

            populatedSensexList.removeAll(filteredTop150SensexList);
            filteredTop150SensexList.clear();
            filteredTop150SensexList = populatedSensexList.stream().filter(x -> x.getFiiPct() != null)
                    .sorted(Comparator.comparing(SensexStockInfo::get_52WeekHighLowPriceDiff).reversed())
                    .collect(Collectors.toList()).stream()
                    .filter(x -> (x.getStockRankIndex() <= 150 &&
                            (x.get_52WeekHighLowPriceDiff().compareTo(BigDecimal.valueOf(100.0)) >= 0)
                    )).collect(Collectors.toList());
            filteredTop150SensexList.forEach( x -> {
                stockPriceData.put(x.getIsin(), (x.get_52WeekLowPrice().multiply(BigDecimal.valueOf(1.02))).doubleValue());
            });
            Files.write(Paths.get(System.getProperty("user.dir") + "\\target" + "\\stockPriceWatchListData.json"),
                    objectMapper.writeValueAsString(stockPriceData).getBytes());

        }catch (Exception e){
            ERROR_LOGGER.error(Instant.now() + ", Error ->", e);
            e.printStackTrace();
        }

    }


}
