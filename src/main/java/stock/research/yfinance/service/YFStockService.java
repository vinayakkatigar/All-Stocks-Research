package stock.research.yfinance.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import stock.research.gfinance.domain.GFinanceStockInfo;
import stock.research.yfinance.domain.YFinance;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Thread.sleep;
import static stock.research.utility.NyseStockResearchUtility.*;

@Service
public class YFStockService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(YFStockService.class);
    @Autowired
    private ObjectMapper objectMapper;

    public List<GFinanceStockInfo> getYFStockInfoList(List<String> stockCodes) {
        List<GFinanceStockInfo> gFinanceStockInfos = new ArrayList<>();

        System.out.println();
        stockCodes.forEach(stock -> {
            try {
                String yfFinance = null;
                int retry =5;
                while (retry-- > 0 && yfFinance == null){
                    yfFinance = queryYF(stock);
                }
                YFinance yFinance = objectMapper.readValue(yfFinance, YFinance.class) ;
                System.out.println(yFinance);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        });
        System.out.println("YFStockService.getYFStockInfoList");

        return null;
    }



    private String queryYF(String stockCode) {
        String output = null;

        try {
            String command = "powershell.exe  " + System.getProperty("user.dir") + "\\src\\main\\resources\\YF\\yfiance.ps1 " + stockCode ;
            Process powerShellProcess = Runtime.getRuntime().exec(command);
            powerShellProcess.getOutputStream().close();
            try (BufferedReader stdout = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getInputStream()))) {
                while ((output = stdout.readLine()) != null) {
                    System.out.println("Output -> " + output);
                    return output;
                }
            }
            try (BufferedReader stderr = new BufferedReader(new InputStreamReader(
                    powerShellProcess.getErrorStream()))) {
                while ((output = stderr.readLine()) != null) {
                    return null;
                }
            }
            System.out.println("Done");


        }catch (Exception exception){
            ERROR_LOGGER.error("YF Query Error ->", exception);
            return null;
        }

        return output;
    }

    private String truncateNumber(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }
    private void goSleep(int x) {
        try { sleep(1000 * x);} catch (Exception e) { }
    }

}