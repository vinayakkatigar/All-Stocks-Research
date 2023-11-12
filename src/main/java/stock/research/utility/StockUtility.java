package stock.research.utility;

import java.time.Instant;

public class StockUtility {

    public static void goSleep(int x){
        try {
            System.out.println(Instant.now() + ", starting goSleep for: " + x);
            Thread.sleep(1000 * x);
        } catch (InterruptedException e) { }
    }
}