package stock.research.utility;

import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Value;
import stock.research.domain.StockInfo;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Stream;

public class StockUtility {

    public static void goSleep(int x){
        try {
            Thread.sleep(1000 * x);
        } catch (InterruptedException e) { }
    }
}