
import javax.vecmath.*;
import javafx.scene.shape.Box;
import javafx.scene.shape.Cylinder;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Shape;
import javafx.scene.shape.Sphere;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.awt.*;
//class for all particles in the simulation
//all particles are given a color, velocity, radius, elasticity, mass, and drag coefficient
public class Object implements Cloneable
{
	public   Vector3d velocity;
	public   Vector3d position;	
	private  Vector3d velocity2;
	private  Vector3d position2;
    private  int r1 = ThreadLocalRandom.current().nextInt(0, 253),r2 = ThreadLocalRandom.current().nextInt(0, 253),r3 = ThreadLocalRandom.current().nextInt(0, 253);    
    public   int identifier = ThreadLocalRandom.current().nextInt(0, 100);
    public   float mass=1,bounciness=1,drag=1;
    private  int w=10,h=10,z=10;
    private  int lastHit=0;
    public   Color color=new Color(r1,r2,r3); //color for 2d stuff
    public   javafx.scene.paint.Color fxColor = javafx.scene.paint.Color.rgb(r1, r2, r3, 1); //color for 3d stuff
    public   float mass2=1,bounciness2=1,drag2=1;
    private  int w2=5,h2=5;    
    public Sphere ball;
    public String type;    
    public ArrayList<Integer> recentlyHit = new ArrayList<Integer>();
    
    
    public Object(float Vi,float angle,float mass,int currentX,int currentY,int currentZ, int w,float bounciness,float drag)
    {
    	fxColor= javafx.scene.paint.Color.rgb(230, 230, 100, 1);
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
	

    public void makeSphere() {
	   ball=new Sphere(w);
   }
   public Sphere getSphere() {
	   return ball;
   }
   public void draw() {
	   ball.setTranslateX(position.getX());
	   ball.setTranslateY(position.getY());
	   ball.setTranslateZ(position.getZ());
   }
   
    public void addHit(int obj, int iteration)
    {
        recentlyHit.add(obj);
        this.lastHit=iteration;
    }
    
    public boolean checkRecent(int obj,Object Obj)
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
    public void checkColor() {
    	if(drag==1||mass>1000)fxColor= javafx.scene.paint.Color.rgb(60, 60, 200, 1);
    	if(mass<0)fxColor= javafx.scene.paint.Color.rgb(200, 60, 60, 1);
    }
    
    public float getRadius() {
	return w;
    }
    public void setRadius(int r) {
    	 w=r;
    	 ball.setRadius(w);
        }
    public void setDrag(float drag)
    {
        this.drag=drag;
    }

    public void setMass(float mass)
    {
        this.mass=mass;
    }

    public void setBounciness(float bounciness)
    {
        this.bounciness=bounciness;
    }

    public void setDimensions(int w,int h)
    {
        this.h=h;
        this.w=w;
    }    

    public void setX(float x)
    {
    	position.setX(x);
    }

    public void setY(float y)
    {
    	position.setY(y);
    }
    
    public void setZ(float z)
    {
    	position.setY(z);
    }

    public void setPos(float x,float y,float z)
    {
        position.setX(x);
        position.setY(y);
        position.setZ(z);
    }
    
    public void setVel(float x,float y,float z)
    {
        velocity.setX(x);
        velocity.setY(y);
        velocity.setZ(z);
    }

    public Vector3d getPos()
    {        
        return  position;
    }  

    public float getDrag()

    {
        return drag;
    }
    
    public Color getColor()
    {
        return color;
    }

    public Vector3d getVel()
    {
        return velocity;
    }

    public float getY()
    {
    	return position.getY();
    }
    public float getZ()
    {
    	return position.getZ();
    }
    
    public float getX()
    {
        return position.getX();
    }

    public int getWidth()
    {
        return w;
    }

    public int getHeight()
    {
        return h;
    }
    
   
    public float getMass()
    {
        return mass;
        }

    public void save()
    {
        velocity2=velocity;
        position2=position;
        mass2=mass;
        bounciness2=bounciness;
        drag2=drag;        
        w2=w;
        h2=h;
    }

    public void reLoad()
    {
    	velocity=velocity2;
        position=position2;
        mass=mass2;
        bounciness=bounciness2;
        drag=drag2;        
        w=w2;
        h=h2;
    }
    
    public void refresh(float vx,float vy,float x,float y,float z)
    {
    	velocity.set(vx, vy,0);
    	position.set(x, y,z);    	
    }
    
    public Object clone() throws
    CloneNotSupportedException 
    	{ 
    	return (Object) super.clone(); 
    	} 
	}



