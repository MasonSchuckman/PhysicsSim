import com.amd.aparapi.Kernel;

public class fieldGPU extends Kernel{
	final private float[] mass;
	final private float grav;
	final public float G;
	final private float[] bX;
	final private float[] bY;
	final private float[] bZ;
	final private float[] X;
	final private float[] Y;
	final private float[] Z;
	final private float[] mag;
	final private float[] pitch;
	final private float[] yaw;
	final private float [] vec;
	final private float [] v1;
	final private float [] v2;
	final private float [] v3;
	
	fieldGPU(float []x,float []y,float []z,float []_mag,float []_pitch,
			float []_yaw,float[]_mass,float gra,float g,
			float[] bigX,float[] bigY,float[] bigZ){
		X=x;
		Y=y;
		Z=z;
		mag=_mag;
		pitch=_pitch;
		yaw=_yaw;
		mass=_mass;
		grav=gra;
		G=g;
		bX=bigX;
		bY=bigY;
		bZ=bigZ;
		vec=new float[3];
		v1=new float[x.length];
		v2=new float[x.length];
		v3=new float[x.length];
	}
	
	@Override
	public void run() {		
		int gid = getGlobalId(0);
		
		float x1 = X[gid];
		float y1 = Y[gid];
		float z1 = Z[gid];
		float mass1;
		float fx=0,fy=0,fz=0;
		float scale = 1;
		float elapSec = 1;
		float netX=0,netY=0,netZ=0;
		//declaring variables used in the for loop. (might make it faster if variables aren't declared each loop, idk
		float r,fmag,x2,y2,z2,fX,fY,fZ,dX,dY,dZ,mass2;
		
		for (int i = 0; i < mass.length; i++) {		
			
				 mass1=mass[i];
				 mass2 = 10;
				 x2 = bX[i];
				 y2 = bY[i];
				 z2 = bZ[i];

				 dX = x1 - x2;
				 dY = y1 - y2;
				 dZ = z1 - z2;

				 r = sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
				 fmag = (G * mass1 * mass2) / (r * r *.3f* r);
				 
				 fX = fmag * dX;
				 fY =-fmag * dY;
				 fZ = fmag * dZ;
				 
				 fx+=fX;
				 fy+=fY;
				 fz+=fZ;
			
			float particleGravX = fx / mass2;
			float particleGravY = fy / mass2;
			float particleGravZ = fz / mass2;

			float gravchange1 = scale*(particleGravX * elapSec); // this is for x1
			float gravchange2 = scale*(particleGravY * elapSec); //- grav * elapSec; // this is for y1
			float gravchange3 = scale*(particleGravZ * elapSec); // this is for z1
			netX+=gravchange1;
			netY+=gravchange2;
			netZ+=gravchange3;
		}
		
		float len=sqrt(netX*netX+netY*netY+netZ*netZ);
					
		vec[0]= netX/len;
		vec[1]=	netY/len;
		vec[2]=	netZ/len;
		float vv0=netX/len;
		float vv1=netY/len;
		float vv2=netZ/len;
		
		yaw[gid]=atan2(vv0, vv2);
		pitch[gid]=acos(vv1);
		mag[gid]=len;
		v1[gid]=vv0;
		v2[gid]=vv1;
		v3[gid]=vv2;
			
	}
	
	public void updatePos(float[][]pos) {			
		for(int i=0; i<pos[0].length; i++) {
			bX[i] =pos [0][i];			
			bY[i] =pos [1][i];			
			bZ[i] =pos [2][i];	
		}
	}
	public float[][]getVecs(){
		return new float[][] {v1,v2,v3};
	}

	public float[][] getInfo() { //returns pitch,yaw,and magnitude
		int size = X.length;
		float[][] angles = new float[size][3];
		
		for (int i = 0; i < size; i++) {
			angles[i][0] = (float) Math.toDegrees(pitch[i]);
			angles[i][1] = (float) Math.toDegrees(yaw[i]);			
			angles[i][2] = mag[i];
		}	
		
	return angles;
	}
}
