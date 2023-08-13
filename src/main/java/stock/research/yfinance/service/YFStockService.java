package stock.research.yfinance.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.sheets.v4.Sheets;
import com.google.api.services.sheets.v4.SheetsScopes;
import com.google.api.services.sheets.v4.model.ValueRange;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import stock.research.gfinance.domain.GFinanceStockInfo;
import stock.research.gfinance.utility.GFinanceNyseStockUtility;
import stock.research.utility.ChromeDriverService;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;

import static java.lang.Thread.sleep;
import static java.util.Comparator.naturalOrder;
import static java.util.Comparator.nullsFirst;
import static java.util.stream.Collectors.toList;
import static stock.research.utility.NyseStockResearchUtility.*;

@Service
public class YFStockService {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(YFStockService.class);

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ChromeDriverService chromeDriverService;

    public List<GFinanceStockInfo> getYFStockInfoList(List<String> stockCodes) {


        System.out.println("YFStockService.getYFStockInfoList");
        String cookie = getCookie("https://fc.yahoo.com");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Set-Cookie", cookie);
        httpHeaders.add("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36");
        HttpEntity requestEntity = new HttpEntity<>(httpHeaders);


        ResponseEntity<String> responseEntity = restGETCall("https://query2.finance.yahoo.com/v1/test/getcrumb", requestEntity);
        System.out.println(responseEntity);
        return null;
    }


    public String getCookie(String url) {
        ResponseEntity<String> response = null;
        try {
            response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            if (response == null || response.getStatusCode() != HttpStatus.OK){
                return null;
            }
        }catch (HttpClientErrorException e){
            System.out.println(e);
            return e.getResponseHeaders().get("Set-Cookie").get(0);
        }finally {
            goSleep(2);
        }

        return null;
    }
    public ResponseEntity<String> restGETCall(String url, HttpEntity... httpEntity) {
        ResponseEntity<String> response = null;
        try {
            sleep(10);
            if (httpEntity != null){
                response = restTemplate.exchange(url, HttpMethod.GET, httpEntity[0], String.class);
            }else {
                response = restTemplate.exchange(url, HttpMethod.GET, null, String.class);
            }
            if (response == null || response.getStatusCode() != HttpStatus.OK){
                return null;
            }
        }catch (HttpClientErrorException e){
            System.out.println(e);
            return null;
        }catch (Exception e){
            return null;
        }finally {
            goSleep(2);
        }

        return response;
    }

    private void goSleep(int x) {
        try { sleep(1000 * x);} catch (Exception e) { }
    }



    private String truncateNumber(double x) {
        return x < MILLION ?  String.valueOf(x) :
                x < BILLION ?  String.format("%.2f", x / MILLION) + "M" :
                        x < TRILLION ? String.format("%.2f", x / BILLION) + "B" :
                                String.format("%.2f", x / TRILLION) + "T";
    }

}