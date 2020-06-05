import javax.vecmath.Vector3d;

public class Freebody extends GameObject{
	public Freebody(float Vi,float angle,float mass,int currentX,int currentY,int currentZ, int w,float bounciness,float drag)
    {
		super(Vi,angle,currentX,currentY,currentZ);
    	fxColor= javafx.scene.paint.Color.rgb(230, 230, 100, 1);
    	fxColor= javafx.scene.paint.Color.rgb(200, 60, 60, 1);
    	if(drag==1||mass>1000)fxColor= javafx.scene.paint.Color.rgb(60, 60, 200, 1);
    	if(mass<0)fxColor= javafx.scene.paint.Color.rgb(200, 60, 60, 1);
    	
        this.mass=mass;
        this.w=w;
        this.drag=drag;
        this.bounciness=bounciness;       
        float xv =  (float) (Vi*Math.cos(Math.toRadians(angle)));       
        float yv=   (float) (Vi*Math.sin(Math.toRadians(angle)));        
        this.velocity = new Vector3d(xv, yv,0);
		this.position = new Vector3d(currentX, currentY,currentZ);
		this.position.setX(currentX);		
		this.position.setY(currentY);
		this.position.setZ(currentZ);
    }
	
	 public void addHit(int obj, int iteration)
	    {
	        recentlyHit.add(obj);
	        this.lastHit=iteration;
	    }
	 public boolean checkRecent(int obj,Freebody Obj)
	    {    	       
	        for(int i=0; i<recentlyHit.size(); i++)
	        {
	            if(obj==recentlyHit.get(i))         
	            {
	                //System.out.println("recently");
	                return true;
	            }
	        } 
	        return false;
	    }
	    
	    public void clearRecent(int checkingNum)
	    {
	        if(lastHit+5<=checkingNum)
	        {
	            recentlyHit.clear();            
	        }
	    }
}
