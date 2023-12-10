package stock.research.utility;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;

public class StockUtility {

    public static void goSleep(int x){
        try {
            System.out.println(Instant.now() + ", starting goSleep for: " + x);
            Thread.sleep(1000 * x);
        } catch (InterruptedException e) { }
    }

    public static void writeToFile(String fileName, String fileContent){
        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName  + ".json"), fileContent.getBytes());
        } catch (Exception e) { }
    }
}