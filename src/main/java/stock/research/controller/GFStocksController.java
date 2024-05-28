package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import stock.research.gfinance.service.GFinanceStockService;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.Map;


@RestController
public class GFStocksController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GFStocksController.class);
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GFinanceStockService gFinanceStockService;

    @GetMapping(value = "/image")
    public @ResponseBody byte[] getImage() throws IOException {
        InputStream in = new ClassPathResource("connection.png").getInputStream();
        return in.readAllBytes();
    }

    @RequestMapping("/gf/ccy")
    public Map<String, BigDecimal> getCcyValues(){
        return gFinanceStockService.getCcyValues();
    }
}