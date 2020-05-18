import java.awt.geom.AffineTransform;
import java.util.ArrayList;

import javax.vecmath.Vector3d;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;

public class visualize {
	public static Group visualizeField(WorldCreator uni,ArrayList<Freebody> obs,int count) { //count is how many arrows total to draw
		float scale=100;
		Group group=new Group();
		ArrayList<Freebody>massives=new ArrayList();
		
		for(int i=0; i<obs.size(); i++) //only cares about massive objects when drawing the field.
			if(obs.get(i).mass>1000) massives.add(obs.get(i));
			else break;
		int x=uni.getLocX();
		int y=uni.getLocY();
		int z=uni.getLocZ();
		/*below is explaining how to setup the particles around the center mass.
		*make symmetry with 1000 open space on both sides
		*worldsize-2000=usable space
		*c*2*d is space used by objects
		*worldsize-2000=c*2*d
		*d=(worldsize-2000)/(2c)
		*starting x is 1000.
		*
		*2000=count*d*2
		*d=2000/(count*2)
		*
		*y starting is so that c/2 is at locY.
		*locY/2=ystart+(c/2*d)
		*y starting = (locY/2)-(c/2*d)
		*END of explanation
		*/		
		int co=(int)Math.cbrt(count);
		//spread across 2000 units
		double d=4000/(2*co);
		
		
		int yStart=(int) (y-((co/2)*d));
		int zOffset=(int)(z-((co/2)*d)); //how far away from locZ the particles start
		int start=(int) (x-((co/2)*d));
		
		//setting the max sizes of i,j,k in the for loops.
		int I=(int) Math.cbrt(count);//how many tall
		int J=(int) Math.cbrt(count);//how many wide
		int K=(int) Math.cbrt(count);//how many deep	
		int xpos,ypos,zpos;
		float [] field=new float[3];
		int counter=0;
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) { 
					
					xpos=start+(int)(d*j);
					ypos=(int) (yStart+(d*i));
					zpos=(int) (zOffset+(d*k));
					
					
					field=calculateField(massives,new int [] {xpos,ypos,zpos},uni,scale);
					
					Vector3d v=new Vector3d(field);
					
					Cylinder c=getCylinder(v,xpos,ypos,zpos);
					if(c!=null) {
						c.setTranslateX(xpos);
						c.setTranslateY(ypos);
						c.setTranslateZ(zpos);
						counter++;
						if(group.getChildren().add(c));
					}
					//System.out.println(xpos+" "+ypos+" "+zpos);
				}
			}
		}
		
		System.out.println(counter);
		//make a method to create a cylinder aligned with the unit vector, length of magnitude, set at position used to get the vector
		return group;
	}
	private static Cylinder getCylinder(Vector3d v,float x,float y,float z) {
		float len=v.getLength();
		//1646
		if(len<.8f&&len>.003) {
			Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
					rotateY = new Rotate(0, Rotate.Y_AXIS),					
					rotateZ = new Rotate(0, Rotate.Z_AXIS);
			
			float mag=500*len;
			if(mag>150)mag=150;
			//System.out.println(mag);
			Cylinder c=new Cylinder(mag/14,mag);
			align(rotateX,rotateY,rotateZ,v,x,y,z,c);
			
			c.getTransforms().addAll(rotateX,rotateY,rotateZ);		
			
			return c;
		}
		return null;
	}
	
	private static void align(Rotate x,Rotate y,Rotate z, Vector3d v,float xx, float yy, float zz,Cylinder shape) {
		float[]vec=v.getUnitVector().asArray();
		float X=-vec[0],Y=vec[1],Z=vec[2];
		
		float j[]=v.getUnitVector().asArray();
		Point3D p=new Point3D(j[0],j[1],j[2]);
		
		float pitch = (float) Math.acos(j[1]);
		float yaw = (float) Math.atan2(j[0], j[2]);
		x.setAxis(p.crossProduct(Rotate.Y_AXIS));
		//y.setAxis(p.crossProduct(Rotate.Z_AXIS));
		//z.setAxis(p);
		
		float a=(float) Math.atan2(Z,Y);
		float b=(float) Math.atan2(Z,X);
		float c=(float) Math.atan2(X,Y);
		//x controls look up and down
		//z controls side to side
		x.setAngle(Math.toDegrees(pitch));	
		y.setAngle(Math.toDegrees(yaw));
		//z.setAngle(Math.toDegrees(c));	
		//x.setAngle(45);
		//y.setAngle(90);
		//z.setAngle(45);
		//-c
		if(Y==0) {
			//x.setAngle(Math.toDegrees(a));
			//z.setAngle(Math.toDegrees(c));
		}
		//roll pitch yaw
		//matrixRotateNode(shape,yaw,pitch,0);
		
		//z.setAngle(23);	
	}
	
	private static void matrixRotateNode(Node n, double alf, double bet, double gam){
	    double A11=Math.cos(alf)*Math.cos(gam);
	    double A12=Math.cos(bet)*Math.sin(alf)+Math.cos(alf)*Math.sin(bet)*Math.sin(gam);
	    double A13=Math.sin(alf)*Math.sin(bet)-Math.cos(alf)*Math.cos(bet)*Math.sin(gam);
	    double A21=-Math.cos(gam)*Math.sin(alf);
	    double A22=Math.cos(alf)*Math.cos(bet)-Math.sin(alf)*Math.sin(bet)*Math.sin(gam);
	    double A23=Math.cos(alf)*Math.sin(bet)+Math.cos(bet)*Math.sin(alf)*Math.sin(gam);
	    double A31=Math.sin(gam);
	    double A32=-Math.cos(gam)*Math.sin(bet);
	    double A33=Math.cos(bet)*Math.cos(gam);

	    double d = Math.acos((A11+A22+A33-1d)/2d);
	    if(d!=0d){
	        double den=2d*Math.sin(d);
	        Point3D p= new Point3D((A32-A23)/den,(A13-A31)/den,(A21-A12)/den);
	        n.setRotationAxis(p);
	        n.setRotate(Math.toDegrees(d));                    
	    }
	}
	
	private static float[] calculateField(ArrayList<Freebody> obs,int [] pos,WorldCreator uni,float scale) {
		float netX=0,netY=0,netZ=0;
		for (int gid = 0; gid < obs.size(); gid++) {
			Freebody c=obs.get(gid);
			float mass1=(float) c.getMass();
			float x1 = (float)c.position.getX();
			float y1 = (float)c.position.getY();
			float z1 = (float)c.position.getZ();

			float fx = 0, fy = 0, fz = 0;						
			float elapSec = 1;
			// declaring variables used in the for loop. (might make it faster if variables
			// aren't declared each loop, idk
			float r, fmag, x2, y2, z2, fX, fY, fZ, dX, dY, dZ, mass2;

					mass2 = 10;
					x2 = pos[0];
					y2 = pos[1];
					z2 = pos[2];

					dX = x1 - x2;
					dY = y1 - y2;
					dZ = z1 - z2;
					float G=(float) uni.getGForce();
					float grav=(float) uni.getGrav();
					r = (float) Math.sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
					fmag = (G * mass1 * mass2) / (r * r * .3f*r);
					if (mass1 < 0)
						fmag = -fmag;
					fX = fmag * dX;
					fY = -fmag * dY;
					fZ = fmag * dZ;

					fx += fX;
					fy += fY;
					fz += fZ;

			float particleGravX = fx / mass1;
			float particleGravY = fy / mass1;
			float particleGravZ = fz / mass1;

			float gravchange1 = scale*(particleGravX * elapSec); // this is for x1
			float gravchange2 = scale*(particleGravY * elapSec) - grav * elapSec; // this is for y1
			float gravchange3 = scale*(particleGravZ * elapSec); // this is for z1
			netX+=gravchange1;
			netY+=gravchange2;
			netZ+=gravchange3;
		}
		return new float[] {netX,netY,netZ};
	}
}

