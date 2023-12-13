package stock.research.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.research.gfinance.service.GFinanceStockService;

import java.math.BigDecimal;
import java.util.Map;


@RestController
public class GFStocksController {

    @Autowired
    private GFinanceStockService gFinanceStockService;

    @RequestMapping("/gf/ccy")
    public Map<String, BigDecimal> getCcyValues(){
        return gFinanceStockService.getCcyValues();
    }
}