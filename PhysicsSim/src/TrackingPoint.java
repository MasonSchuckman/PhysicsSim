


import javax.vecmath.*;

import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;

import java.util.ArrayList;


public class TrackingPoint extends GameObject{
	
    private  int w=10;  
    private ArrayList<float[]>rawPoses=new ArrayList<float[]>(50);
    private ArrayList<float[]>poses=new ArrayList<float[]>(50);
    private ArrayList<float[]>vels=new ArrayList<float[]>(10);
    public   javafx.scene.paint.Color fxColor; //color for 3d stuff
    public Box box;
       
   
    public double radOfInfluence;
    private boolean full=false;//full for positions
    private boolean full2=false;//full for vels
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
            rotateY = new Rotate(0, Rotate.Y_AXIS),
            rotateZ = new Rotate(0, Rotate.Z_AXIS);
    
    public TrackingPoint(int currentX,int currentY,int currentZ, int rad,int number)
    {
    	super(currentX,currentY,currentZ);
    	if(number==0)fxColor= javafx.scene.paint.Color.rgb(255, 100, 100, 1);//controller 1
		if(number==1)fxColor= javafx.scene.paint.Color.rgb(100, 255, 255, 1);//controller 2
    	w=(int) rad;
    	radOfInfluence=rad;
		
    }
    
    private int framesSaved=40;//save 40 frames of motion data before deletion.
    public void setPos(float x,float y,float z)
    {
        poses.add(new float[] {x,y,z});//adds the change of position to the list.
        position.setX(x);
        position.setY(y);
        position.setZ(z);
        
        if(!full) {
        	if(poses.size()>framesSaved) {
        		full=true;
        		poses.remove(0);
        	}
        }else poses.remove(0);        
    }
    public void makeLightsaber() {
    	box=new Box(10,100,10);
    	box.getTransforms().addAll(rotateX,rotateY,rotateZ);
    	box.setCache(true);
    }
    
    public void rotateBox(int x,int y,int z) {
    	rotateX.setAngle(x);
    	rotateZ.setAngle(z);
    }
    
    public void setVel()
    {
    	if(poses.size()>1) {
    	int size=poses.size();
    	float x=(float) getX();
    	float y=(float) getY();
    	float z=(float) getZ();
    	
    	float []pastPos=poses.get(size-2);
    	float x2=(float) pastPos[0];
    	float y2=(float) pastPos[1];
    	float z2=(float) pastPos[2];
    	
    	float dx=(x-x2);
    	float dy=(y-y2);
    	float dz=(z-z2);
    	vels.add(new float[] {dx,dy,dz});//adds the change of position to the list.
        
        if(!full2) {
        	if(vels.size()>10) {
        		full2=true;
        		vels.remove(0);
        	}
        }else vels.remove(0);     
    	}
    }
    
    private boolean full3=false;
    public void addRaw(float []pos) {
    	rawPoses.add(pos);
    	float [] poss=rawPoses.get(rawPoses.size()-1);
    	//System.out.println(poss[0]+ "  "+poss[1]+ "  "+poss[2]+ "  ");
    	
    	if(!full3) {
        	if(rawPoses.size()>framesSaved) {
        		full3=true;
        		rawPoses.remove(0);
        	}
        }else rawPoses.remove(0);   
    }    
    
    public float[][]getXFramesOfData(int count){ //this returns raw data
    	float [][]data=new float[count][3];
    	
    	for(int i=0; i<count; i++) {    		
    		float []pos=rawPoses.get(rawPoses.size()-1-i);    		
    		//System.out.println(pos[0]+ "  "+pos[1]+ "  "+pos[2]+ "  ");
    		data[i]=(pos);    		    		
    	}    	
    	return data;
    }    
    
	
    public double getRecentMovement() {
    	float total=0;
    	total=sumOfMovement(poses.size());
    	return total;
    }
    public boolean checkConsistency(int count) {
    	if(poses.size()>count&&vels.size()>count) {
    		double leniency=.5;
    		Vector3d pos=new Vector3d(this.poses.get(poses.size()-1)); //current position    	
    		Vector3d posOld=new Vector3d(this.poses.get(poses.size()-1-count));//position count frames ago
    		Vector3d difference=pos.subtract(posOld); //difference between 2 positions
    		difference=difference.getUnitVector();    	
    		Vector3d unitVel=new Vector3d(this.vels.get(vels.size()-1-count));//gets velocity from count frames ago
    		unitVel=unitVel.getUnitVector();
    		
    		double variable=(difference.subtract(unitVel)).getLength();
    		//if((variable<leniency))System.out.println("position unit vec: "+difference+"   vel unit vec: "+unitVel);
    		return (variable<leniency);
    	}
    	return false;
    }
    public double getXFramesOfMovement(int count) {
    	return sumOfMovement(count);
    }
    private float[]normalizeArray(float[] data){
    	return new float[]{data[0]/640,data[1]/480,data[2]/640};
    }
    public double calcDisplacement(int count) {
    	if(poses.size()>count) {
    	Vector3d p1=new Vector3d(poses.get(poses.size()-1)); //gets the most recent position 
    	Vector3d p2=new Vector3d(poses.get(poses.size()-count-1)); //gets the position [count] frames ago. 
    	return p1.subtract(p2).getLength(); 		//returns the magnitude of the change of position.
    	}
		return 0;
    }
    
    public double calcVel(int count) {
    	return sumOfMovement(count)/30;//30 is cuz 30 frames of data/sec
    }
    
    private float sumOfMovement(int index) { //starts from most recent and works backwards
    	float total=0;
    	for(int i=poses.size()-1; i>=index; i--) {
    		float []pos=poses.get(i);
    		float dX=pos[0];
    		float dY=pos[1];
    		float dZ=pos[2];
    		float dist = (float) Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
    		total+=dist;
    	}
    	return total;
    }

	public ArrayList<Freebody> getAffected(ArrayList<Freebody> list2) { //returns an arrayList of all the objects inside the sphere of influence
		ArrayList<Freebody> list = new ArrayList<Freebody>();
		double X, Y, Z, rad, dx, dy, dz, distance;
		double rad2 = w/2;
		for (int i = 0; i < list2.size(); i++) {
			
			Freebody c = list2.get(i);			
			X=c.getX();
			Y=c.getY();
			Z=c.getZ();
			dx = X - this.getX();
			dy = Y - this.getY();
			dz = Z - this.getZ();
			rad = c.getRadius();

			distance = Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
			if (distance < rad/2 + rad2) {
				// is inside
				list.add(c);
			}			
		}
		return list;
	}

	public double getRadOfInfluence() {
		return radOfInfluence;
	}

	public void makeSphere() {
	   ball=new Sphere(w);
	   ball.setDrawMode(DrawMode.LINE);
   }
   
   public void draw() {
	   
	   if(box!=null) {
		   box.setTranslateX(position.getX());
		   box.setTranslateY(position.getY());
		   box.setTranslateZ(position.getZ());
	   }else {
		   ball.setTranslateX(position.getX());
		   ball.setTranslateY(position.getY());
		   ball.setTranslateZ(position.getZ());
	   }
   }
   
    
    
    
}
