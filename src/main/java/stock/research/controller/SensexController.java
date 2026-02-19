package stock.research.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import stock.research.email.alerts.SensexStockResearchAlertMechanismService;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.ZonedDateTime.now;

@RestController
public class SensexController {
    public final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    @Autowired
    private SensexStockResearchAlertMechanismService stockResearchAlertMechanismService;

    @CrossOrigin
    @GetMapping("/screener/sensex/alerts")
    public String sensexPnL(){
        String data = stockResearchAlertMechanismService.getSensexAlertsData();
        writeHTMLFile(getDateFileFormat(now().format(formatter))  + "SCREENER-SENSEX-ALERTS.html", data);
        return data;
    }

    @CrossOrigin
    @GetMapping("/screener/sensex/daily")
    public String sensexDaily(){
        String data = stockResearchAlertMechanismService.getSensexDailyData();
        writeHTMLFile(getDateFileFormat(now().format(formatter))  + "SCREENER-SENSEX-DAILY.html", data);
        return data;
    }

    @CrossOrigin
    @GetMapping("/screener/sensex/yearlow")
    public String sensexYearLow(){
        String data = stockResearchAlertMechanismService.getYearLow();
        writeHTMLFile(getDateFileFormat(now().format(formatter))  + "SCREENER-SENSEX-YEAR-LOW.html", data);
        return data;
    }

    public String getDateFileFormat(String input){
        int sum = 0;
        for(int i = 0; i < input.length(); i++) {
            if(Character.isDigit(input.charAt(i))) {
                sum = sum + Integer.parseInt(input.charAt(i) + "");
            }
        }
        ZonedDateTime dateTime = now();
        sum = sum + (dateTime.getYear() + dateTime.getMonthValue() + dateTime.getDayOfMonth());
        return (sum * (dateTime.getMonthValue() +
                dateTime.getDayOfMonth())) + "--" + input;
    }

    public static void writeHTMLFile(String fileName, String fileContent, String... subDir){
        try {
            String fileDir = "\\genHtml\\";
            if (subDir != null && subDir.length > 0) {
                fileDir = fileDir + "\\" + subDir[0]  + "\\"  ;
            }
            Files.write(Paths.get(System.getProperty("user.dir") + fileDir + fileName), fileContent.getBytes());
        } catch (Exception | Error e) { }
    }

}