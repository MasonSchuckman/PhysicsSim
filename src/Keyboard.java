import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
public class Keyboard {

    private static boolean[] pressed = new boolean[128];

    public static boolean isPressed(int key) {
        return pressed[key];
    }
 
   public  static KeyListener listener = new KeyAdapter() {

        @Override
        public void keyPressed(KeyEvent e) {
            int code = e.getKeyCode();
            if (code < pressed.length) {
                pressed[code] = true;
                //System.out.println(e);
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
            int code = e.getKeyCode();
            if (code < pressed.length) {
                pressed[code] = false;
            }
        }
    };
}
