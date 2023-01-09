package stock.research.runner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.annotation.Order;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.client.RestTemplate;
import stock.research.email.alerts.NyseEmailAlertMechanismService;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;
import stock.research.service.InteractiveInvestorsResearchService;
import stock.research.service.ScreenerSensexStockResearchService;
import stock.research.service.SensexStockResearchService;

@Order(1)
@SpringBootApplication
public class Sensex500StocksCmdRunner implements CommandLineRunner {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(Sensex500StocksCmdRunner.class);

    @Autowired
    private SensexStockResearchService sensexStockResearchService;

    @Autowired
    private InteractiveInvestorsResearchService interactiveInvestorsResearchService;
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    RestTemplate restTemplate;
    @Autowired
    SensexStockResearchAlertMechanismService sensexStockResearchAlertMechanismService;
    @Autowired
    private NyseEmailAlertMechanismService startUpNYSEStockResearchService;

    @Autowired
    private ScreenerSensexStockResearchService screenerSensexStockResearchService;


    @Override
    public void run(String... args) throws Exception {
        sensexStockResearchAlertMechanismService.kickOffScreenerEmailAlerts();
/*
        List<NyseStockInfo> stockInfoList = objectMapper.readValue(new ClassPathResource("NYSE--detailedInfo.json").getInputStream(), new TypeReference<List<NyseStockInfo>>(){});

        stockInfoList = stockInfoList.stream()
                .filter(q -> ((q.getCurrentMarketPrice() != null && q.getCurrentMarketPrice().intValue() > 0)
                        && (q.get_52WeekLowPrice() != null && q.get_52WeekLowPrice().intValue() > 0)
                        && (q.get_52WeekHighPrice() != null && q.get_52WeekHighPrice().intValue() > 0)
                        && (q.getStockMktCap() != null || q.getMktCapRealValue() != null))).collect(toList());
        Map<String, String> stringMap = new LinkedHashMap<>();
        stockInfoList.stream().forEach(x -> stringMap.put(x.getStockCode(), x.getStockURL()));
        System.out.println(objectMapper.writeValueAsString(stringMap));
*/
        LOGGER.info("Started Sensex500StocksCmdRunner::run" );
        sensexStockResearchAlertMechanismService.kickOffEmailAlerts();
        LOGGER.info("End Sensex500StocksCmdRunner::run" );

//        startUpNYSEStockResearchService.kickOffEmailAlerts();
    }

}
