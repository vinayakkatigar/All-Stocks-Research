package stock.research.utility;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public class StockUtility {

    public static void goSleep(int x){
        try {
            System.out.println(Instant.now() + ", starting goSleep for: " + x);
            Thread.sleep(1000 * x);
        } catch (InterruptedException e) { }
    }


    public static <T> String stringifyJson(ObjectMapper objectMapper, List<T> fileContent){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(fileContent);
        } catch (Exception e) {
            return null;
        }
    }

    public static <K,V> String stringifyMapJson(ObjectMapper objectMapper, Map<K,V> mapContent){
        try {
            return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(mapContent);
        } catch (Exception e) {
            return null;
        }
    }

    public static void writeToFile(String fileName, String fileContent){
        try {
            Files.write(Paths.get(System.getProperty("user.dir") + "\\genFiles\\" + fileName  + ".json"), fileContent.getBytes());
        } catch (Exception e) { }
    }

}