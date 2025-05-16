import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.Instant;

public class Hello {
    public static void main(String[] args) throws Exception{

          Robot robot = new Robot();
          while (true){
System.out.println(Instant.now());
              robot.keyPress(KeyEvent.VK_WINDOWS);
              Thread.sleep(100);
              robot.keyRelease(KeyEvent.VK_WINDOWS);
              Thread.sleep(100);
              robot.keyPress(KeyEvent.VK_WINDOWS);
              Thread.sleep(100);
              robot.keyRelease(KeyEvent.VK_WINDOWS);

               Thread.sleep(300);
              Point pObj = MouseInfo.getPointerInfo().getLocation();
              robot.mouseMove(pObj.x + 100, pObj.y + 100);
              Thread.sleep(3000);
              robot.mouseMove(pObj.x - 1, pObj.y - 1);

              Thread.sleep(1000 * 60 * 5);
          }
      }
}
