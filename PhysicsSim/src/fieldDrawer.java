import java.util.ArrayList;

import javax.vecmath.Vector3d;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;

public class fieldDrawer extends Thread{
	private ArrayList<Cylinder> arrows;
	private WorldCreator uni;
	private ArrayList<Freebody>massives;
	private float scale=1;
	private float[] mass;
	private float grav;
	public float G;
	private float[] bX;
	private float[] bY;
	private float[] bZ;
	private float[] X;
	private float[] Y;
	private float[] Z;
	private float[] mag;
	private float[] pitch;
	private float[] yaw;	
	private int counter=0;
	private boolean GPU=false;
	private boolean slice=false;
	private fieldGPU vis;
	public fieldDrawer(WorldCreator uni,ArrayList<Freebody> obs,int count,Group root) {
		this.uni=uni;
		arrows=new ArrayList<Cylinder>(count);
		
		massives=new ArrayList<Freebody>();
		
		for(int i=0; i<obs.size(); i++) //only cares about massive objects when drawing the field.
			if(Math.abs(obs.get(i).mass)>1000) massives.add(obs.get(i));
			else break;
		int x=uni.getLocX();
		int y=uni.getLocY();
		int z=uni.getLocZ();
		if(slice)count=100000;		
		int co=(int)Math.cbrt(count);
		//spread across x units
		double d=15000/(2*co);
		
		
		int yStart=(int) (y-((co/2)*d));		
		int start=(int) (x-((co/2)*d));
		if(slice)co=1;
		int zOffset=(int)(z-((co/2)*d));
		//setting the max sizes of i,j,k in the for loops.
		int I=(int) Math.cbrt(count);//how many tall
		int J=(int) Math.cbrt(count);//how many wide
		int K=(int) Math.cbrt(count);//how many deep	
		int xpos,ypos,zpos;
		float [] field=new float[3];
		if(slice)K=1;
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
						arrows.add(c);
						c.setTranslateX(xpos);
						c.setTranslateY(ypos);
						c.setTranslateZ(zpos);
						counter++;
						root.getChildren().add(c);
					}					
				}
			}
		}
		//below is initializing stuff for GPU acceleration
		mass=new float[massives.size()];
		bX=new float[massives.size()];
		bY=new float[massives.size()];
		bZ=new float[massives.size()];
		mag=new float[counter];
		X=new float[counter];
		Y=new float[counter];
		Z=new float[counter];
		pitch=new float[counter];
		yaw=new float[counter];
		
		for(int i=0; i<massives.size(); i++) {
			Freebody o=massives.get(i);
			float pX=o.getX();
			float pY=o.getY();
			float pZ=o.getZ();
			bX[i]=pX;
			bY[i]=pY;
			bZ[i]=pZ;
			mass[i]=o.getMass();
		}
		
		for(int i=0; i<counter; i++) {
			Cylinder c=arrows.get(i);
			mag[i]=(float) c.getHeight();
			X[i]=(float) c.getTranslateX();
			Y[i]=(float) c.getTranslateY();
			Z[i]=(float) c.getTranslateZ();
			pitch[i]=0;
			yaw[i]=0;			
		}
		G=uni.getGForce();
		grav=uni.getGrav();		
	}
	
	private Range range;
	public void useGPU() {		
		vis=new fieldGPU(X,Y,Z,mag,pitch,yaw,mass,grav,G,bX,bY,bZ);		
		GPU=true;
		//vis.setExecutionMode(Kernel.EXECUTION_MODE.CPU);
		range=Range.create(counter);
		//System.out.println("Device = " + vis.getTargetDevice().toString()+"     "+vis.getKernelState());
	}
	
	public void run() {
		while(true) {
			for(int i=0; i<massives.size(); i++) {
				Freebody o=massives.get(i);
				float pX=o.getX();
				float pY=o.getY();
				float pZ=o.getZ();
				bX[i]=pX;
				bY[i]=pY;
				bZ[i]=pZ;
			}		
			float[][]pos=new float[][] {bX,bY,bZ};
			vis.updatePos(pos);
			
			vis.execute(range);
		}
	}
	
	public void setAngles() {
		float [][]info=vis.getInfo();
		float [][]vecs=vis.getVecs();
		
		for(int i=0; i<arrows.size(); i++) {
			
			float pitch=info[i][0];
			float yaw=info[i][1];
			float magg=info[i][2];
			
			Cylinder c=arrows.get(i);
			float len=magg/500;
			if(len>.000003) {
				
				int xpos=(int)X[i];
				int ypos=(int)Y[i];
				int zpos=(int)Z[i];

				float vX=vecs[0][i];
				float vY=vecs[1][i];
				float vZ=vecs[2][i];
				Point3D p=new Point3D(vX,vY,vZ);
				magg*=6;
				
				if(magg>255)magg=255;
				if(magg<10)magg=10;
				c.getTransforms().clear();
				
				int r=(int)Math.abs((int)(1.48*(Math.log(magg))*(Math.log(magg))*(Math.log(magg))));
				int g=(int)(magg/1.3+25)-(int)(r/1.5);
				int b=(int)Math.abs(50*(Math.log(magg))-(int)(8.22*(Math.log(magg))*(Math.log(magg))));
				Color fxColor= javafx.scene.paint.Color.rgb(r,g,b, 1);
				PhongMaterial boxcolor = new PhongMaterial();		    
				boxcolor.setSpecularColor(fxColor);
				boxcolor.setDiffuseColor(fxColor);		    
				c.setMaterial(boxcolor);

				c.getTransforms().addAll(new Rotate(pitch, p.crossProduct(Rotate.Y_AXIS)),new Rotate(yaw, Rotate.Y_AXIS));	
				c.setTranslateX(xpos);
				c.setTranslateY(ypos);
				c.setTranslateZ(zpos);				
			}
		}
	}
	
	public void refresh() {
		//if(GPU)refreshGPU();
		
		//else {
			for(int i=0; i<arrows.size(); i++) {
				Cylinder c=arrows.get(i);
				int xpos=(int)c.getTranslateX();
				int ypos=(int)c.getTranslateY();
				int zpos=(int)c.getTranslateZ();
				c.getTransforms().clear();
				System.out.println("here");
				float [] field=calculateField(massives,new int [] {xpos,ypos,zpos},uni,scale);
				Vector3d v=new Vector3d(field);
				alignArrow(v,c);
				c.setTranslateX(xpos);
				c.setTranslateY(ypos);
				c.setTranslateZ(zpos);
			}
		//}
	}
	
	private void alignArrow(Vector3d v,Cylinder c) {
		
			Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
					rotateY = new Rotate(0, Rotate.Y_AXIS);
			
			align(rotateX,rotateY,v);
			
			c.getTransforms().addAll(rotateX,rotateY);	
		
	}
	
	private void align(Rotate x,Rotate y, Vector3d v) {
		float[]vec=v.getUnitVector().asArray();
		float X=-vec[0],Y=vec[1],Z=vec[2];
		
		float j[]=v.getUnitVector().asArray();
		Point3D p=new Point3D(j[0],j[1],j[2]);
		
		float pitch = (float) Math.acos(j[1]);
		float yaw = (float) Math.atan2(j[0], j[2]);
		x.setAxis(p.crossProduct(Rotate.Y_AXIS));
		
		x.setAngle(Math.toDegrees(pitch));	
		y.setAngle(Math.toDegrees(yaw));
			
	}	
	
	private Cylinder getCylinder(Vector3d v,float x,float y,float z) {
		
		
			Rotate rotateX = new Rotate(0, Rotate.X_AXIS),
					rotateY = new Rotate(0, Rotate.Y_AXIS);					
					
			
			float mag=500*(float) v.getLength();
			if(mag>255)mag=255;
			Cylinder c=new Cylinder(2.7,50);
			c.setCache(true);
			c.setCacheHint(CacheHint.SPEED);
			align(rotateX,rotateY,v);
			Color fxColor= javafx.scene.paint.Color.rgb((int)mag, (int)mag, (int)mag/2, 1);
			PhongMaterial boxcolor = new PhongMaterial();		    
			boxcolor.setSpecularColor(fxColor);
			boxcolor.setDiffuseColor(fxColor);		    
			c.setMaterial(boxcolor);
			c.getTransforms().addAll(rotateX,rotateY);		
			
			return c;
		
	}
	
	private float[] calculateField(ArrayList<Freebody> obs,int [] pos,WorldCreator uni,float scale) {
		float netX=0,netY=0,netZ=0;
		for (int gid = 0; gid < obs.size(); gid++) {
			Freebody c=obs.get(gid);
			float mass1=(float) c.getMass();
			float x1 = (float)c.position.getX();
			float y1 = (float)c.position.getY();
			float z1 = (float)c.position.getZ();

			float fx = 0, fy = 0, fz = 0;						
			float elapSec = 1;
			
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
					//if (mass1 < 0)
					//	fmag = -fmag;
					fX = fmag * dX;
					fY = -fmag * dY;
					fZ = fmag * dZ;

					fx += fX;
					fy += fY;
					fz += fZ;

			float particleGravX = fx / mass2;
			float particleGravY = fy / mass2;
			float particleGravZ = fz / mass2;

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
