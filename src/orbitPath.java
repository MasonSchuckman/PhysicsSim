import javax.vecmath.Vector3d;;
public class orbitPath {
	public static int r=10;
	public static int maxSpeed=1;
	private GameObject pivot, satelite;
	private int rad;
	public orbitPath(GameObject pivot, GameObject satelite) {
		//rad=
	}
	
	public static void simpleOrbit(GameObject pivot, GameObject satelite,float t) {
		t=t*(float) (maxSpeed/Math.sqrt(r));
		float x = (float) (r*Math.cos(t));
		float y = (float) (r*Math.sin(t));
		//satelite.setPos(x+pivot.getX(), y+pivot.getY(),pivot.getZ()); 
	}
	
	public static void simpleOrbit2(GameObject pivot, GameObject satelite,float t) {
		float R=distance(pivot,satelite);
		t=t*(float) (maxSpeed*1.9/Math.sqrt(R));
		float x = (float) (R*Math.cos(t));
		float y = (float) (R*Math.sin(t));
		//satelite.setPos(x+(pivot.getX()), y+(pivot.getY()),(pivot.getZ()));
		//satelite.setPos(x+(pivot.getX()+satelite.getX())/2, y+(pivot.getY()+satelite.getY())/2,(pivot.getZ()+satelite.getZ())/2);
	}
	
	private static float distance(GameObject a, GameObject b) {
		float x=a.getX()-b.getX();
		float y=a.getY()-b.getY();
		float z=a.getZ()-b.getZ();
		Vector3d vec=new Vector3d(x,y,z);
		return vec.getLength();				
	}	
}

