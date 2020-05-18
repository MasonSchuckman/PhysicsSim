import java.awt.Color;
import java.util.ArrayList;

import javafx.scene.shape.Sphere;

public class gameRunner {
	public ArrayList<Object> obs;
	
	public static void update(double time, Sphere s) {
    	double lastpos=s.getTranslateZ();
    	s.setTranslateZ(.5+lastpos);
    }
}
