package stock.research.utility;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.lang.Thread.sleep;
import static java.time.Instant.now;

@Service
public class EuroNextStockUrlExtractor {
    private static final Logger ERROR_LOGGER = LoggerFactory.getLogger("ERRORS-FILE");
    private static final Logger LOGGER = LoggerFactory.getLogger(EuroNextStockUrlExtractor.class);

    private WebDriver webDriver;

    @Autowired
    private ObjectMapper objectMapper;

    @PostConstruct
    public void setUp(){
/*
        this.webDriver = launchBrowser();
        hoverAndScollBrowser();
*/
    }

    public Map<String , String> extractEuroNext(){
        if (webDriver == null){
            webDriver = launchBrowser();
        }
        Map<String , String> stocksUrlMap = new HashMap<>();

            for (int j = 0; j < 19; j++) {
                for (WebElement we: webDriver.findElement(By.id("stocks-data-table")).findElements(By.cssSelector(".stocks-name.sorting_1"))){
                    boolean sucess = false;
                    int retry =10;
                    while (!sucess && retry-- > 0 ){
                        sucess =extractUrl(stocksUrlMap, we);
                    }
                }
                boolean sucess = false;
                int retry =10;
                while (!sucess && retry-- > 0 ){
                    sucess = clickNext(stocksUrlMap.size());
                    hoverAndScollBrowser();
                }

                try {
                    Files.write(Paths.get(System.getProperty("user.dir") + "\\logs\\"+ stocksUrlMap.size() +"allEuroUriInfo"+".json"),
                            objectMapper.writeValueAsString(stocksUrlMap).getBytes());
                } catch (IOException e) { }
            }

        LOGGER.info("stocksUrlMap -" + stocksUrlMap);
        return stocksUrlMap;
    }

    private boolean clickNext( int cur) {
        int next = cur/100;
        ++next;
        System.out.println("Next ->" + next);
        try {
            webDriver.findElement(By.id("stocks-data-table_next")).click();
            Thread.sleep(1000 * 5);
        } catch (Exception e) {
//            webDriver.findElement(By.id("stocks-data-table_previous")).click();
            try{
                for (WebElement webElement : webDriver.findElement(By.id("stocks-data-table_paginate")).findElements(By.tagName("a"))){
                    if (++cur == Integer.valueOf(webElement.getText())){
                        webElement.click();
                        return true;
                    }
                }
              webDriver.findElement(By.id("stocks-data-table_paginate")).findElements(By.tagName("a")).get(4).click();
              return true;
          }catch (Exception ex){}
            System.out.println("Failed clickNext" + e.getMessage());
            return false;
        }
        return true;
    }

    private boolean extractUrl(Map<String, String> stocksUrlMap, WebElement we) {
        try {

            WebElement aWE = we.findElement(By.tagName("a"));
            stocksUrlMap.put(aWE.getText(), aWE.getAttribute("href"));
            LOGGER.info(stocksUrlMap.size() + " <- stocksUrlMap -");
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private void hoverAndScollBrowser() {
        try{
            //to perform Scroll on application using Selenium
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeScript("window.scrollBy(0,200)", "");
            Thread.sleep(1000 * 1);
            js.executeScript("window.scrollBy(0,200)", "");
            Thread.sleep(1000 * 1);
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(1000 * 1);
            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
            Thread.sleep(1000 * 1);
//            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.HOME);
        }catch (Exception e){}
    }

    private boolean saveCookies(){
        try {
            webDriver.findElement(By.cssSelector("agree-button.eu-cookie-compliance-default-button")).click();
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    private WebDriver launchBrowser() {
        try{
            System.setProperty("webdriver.chrome.driver", System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");
            System.setProperty("webdriver.chrome.webDriver",System.getProperty("user.dir") + "\\src\\main\\resources\\chromedriver.exe");

            webDriver = new ChromeDriver();
            webDriver.manage().timeouts().implicitlyWait(20, TimeUnit.SECONDS);

            sleep(1000 * 1);

            webDriver.get("https://live.euronext.com/en/products/equities/list");

            int retry = 10;
            boolean sucess = false;
            while (!sucess && --retry > 10){
                sucess = saveCookies();
            }
            try { webDriver.findElement(By.className("eu-cookie-compliance-save-preferences-button")).click(); } catch (Exception e) { }
            try { webDriver.findElement(By.cssSelector("eu-cookie-compliance-save-preferences-button")).click(); } catch (Exception e) { }
            try { webDriver.findElement(By.className("eu-cookie-compliance-save-preferences-button")).click(); } catch (Exception e) { }
            try { webDriver.switchTo().alert().accept();} catch (Exception e) { }


        }catch (Exception e){
            ERROR_LOGGER.error(now() + ",launchAndExtract::Error ->", e);
            e.printStackTrace();
            return null;
        }

        try{
            JavascriptExecutor js = (JavascriptExecutor) webDriver;
            js.executeScript("window.scrollBy(0,300)", "");
            Thread.sleep(1000 * 2);
            js.executeScript("window.scrollBy(0,250)", "");
            Thread.sleep(1000 * 2);
            webDriver.findElement(By.cssSelector("body")).sendKeys(Keys.CONTROL, Keys.END);
        }catch (Exception e){}
        return webDriver;
    }
}
