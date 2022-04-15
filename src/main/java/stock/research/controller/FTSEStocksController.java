package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import stock.research.domain.FtseStockInfo;
import stock.research.service.FTSEStockResearchService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static stock.research.utility.FtseStockResearchUtility.*;

@RestController
public class FTSEStocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FTSEStocksController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private JavaMailSender javaMailSender;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private FTSEStockResearchService stockResearchService;

    @Autowired
    RestTemplate restTemplate;

    @RequestMapping("/ftse")
    public String ftse(){
        List<FtseStockInfo> ftseStockDetailedInfoList = stockResearchService.getLargeCapCacheftseStockDetailedInfoList();
        ftseStockDetailedInfoList.addAll(stockResearchService.getMidCapCapCacheftseStockDetailedInfoList());
        return generateHtml(ftseStockDetailedInfoList);
    }

    @RequestMapping("/ftse250")
    public String ftse250(){
        List<FtseStockInfo> ftseStockDetailedInfoList = stockResearchService.getMidCapCapCacheftseStockDetailedInfoList();
        return generateHtml(ftseStockDetailedInfoList);
    }

    @RequestMapping("/ftse100")
    public String ftse100(){
        List<FtseStockInfo> ftseStockDetailedInfoList = stockResearchService.getLargeCapCacheftseStockDetailedInfoList();
        return generateHtml(ftseStockDetailedInfoList);
    }

    private String generateHtml(List<FtseStockInfo> ftseStockDetailedInfoList) {
        try {
            LOGGER.info("FTSEStocksController::generateHtml");

            StringBuilder dataBuffer = new StringBuilder("");
            ftseStockDetailedInfoList.stream().forEach(x-> {
                if (x.getCurrentMarketPrice() != null && x.getCurrentMarketPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekLowPrice() != null && x.get_52WeekLowPrice().compareTo(BigDecimal.ZERO) > 0 &&
                        x.get_52WeekHighPrice() != null && x.get_52WeekHighPrice().compareTo(BigDecimal.ZERO) > 0 ){
                    LOGGER.info("ftseStockDetailedInfo -> "+x.toString());
                    createTableContents(dataBuffer, x);
//                    LOGGER.info("$After$" + x);
                }
            });
            String data = HTML_START;
            data += dataBuffer.toString();
            data += HTML_END;
            return data;
        } catch (Exception e) {
            ERROR_LOGGER.error(Instant.now() + ", ftse - , Error ->", e);
            e.printStackTrace();
            return e.toString();
        }
    }

}