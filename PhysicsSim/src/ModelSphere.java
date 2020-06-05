import javafx.scene.shape.Sphere;

public class ModelSphere extends Sphere{
	public ModelSphere(int radius, int divisions) {
		super(radius,divisions);
	}
	public void refresh(int x,int y, int z) {
		   setTranslateX(x);
		   setTranslateY(y);
		   setTranslateZ(z);
	   }
}
