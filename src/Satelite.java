import javax.vecmath.Vector3d;

public class Satelite extends GameObject{
	private Freebody parent;
	public Satelite(float Vi,float angle,int currentX,int currentY,int currentZ,Freebody Parent)
    {
		super(Vi,angle,currentX,currentY,currentZ);    	
		parent=Parent;
    }
}
