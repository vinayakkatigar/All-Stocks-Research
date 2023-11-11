package stock.research.utility;

public class StockUtility {

    public static void goSleep(int x){
        try {
            Thread.sleep(1000 * x);
        } catch (InterruptedException e) { }
    }
}