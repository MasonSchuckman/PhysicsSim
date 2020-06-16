

import com.amd.aparapi.Kernel;
/*
 * this class is used to utilize the GPU in the computer to achieve much faster processing of simulation frames.
 * all motion calculation are GPU accelerated in this class in the run() method.
 */
public class gpuAccel extends Kernel {
	
	// locations of each object
	final private float[] X;
	final private float[] Y;
	final private float[] Z;
	final private float[] vX;
	final private float[] vY;
	final private float[] vZ;
	final private float[] mass;
	final private float[] drag;
	final private float[] time;
	final private float grav;
	final public float G;
	

	// g is G, gra is earth type gravity
	gpuAccel(int size, float[] X, float[] Y, float[] Z, float[] vX, float[] vY, float[] vZ, float[] mass, float[] _drag,
			float g, float gra) {

		this.X = X;
		this.Y = Y;
		this.Z = Z;
		this.vX = vX;
		this.vY = vY;
		this.vZ = vZ;
		this.mass = mass;
		G = g;		
		drag = _drag;
		grav = gra;
		time = new float[2];		
	}

	@Override
	public void run() {		
		int gid = getGlobalId(0);
		
		float mass1 = mass[gid];
		float x1 = X[gid];
		float y1 = Y[gid];
		float z1 = Z[gid];

		float fx=0,fy=0,fz=0;
		float scale = 1;
		float dragForce = drag[gid];
		float elapSec = time[0];
		
		//declaring variables used in the for loop. (might make it faster if variables aren't declared each loop, idk
		float r,fmag,x2,y2,z2,fX,fY,fZ,dX,dY,dZ,mass2;
		
		for (int i = 0; i < mass.length; i++) {
			
			if (gid != i) { // this "if" is the replacement for the continue statement to skip an Object
							// calcing grav on itself
				
				 mass2 = mass[i];
				 if(mass2<10&&mass1<10) continue;//small objects dont act on each other
				 x2 = X[i];
				 y2 = Y[i];
				 z2 = Z[i];

				 dX = x1 - x2;
				 dY = y1 - y2;
				 dZ = z1 - z2;

				 r = sqrt((dX * dX) + (dY * dY) + (dZ * dZ));
				 fmag = abs((G * mass1 * mass2) / (r * r * r));
				 if(mass2<0)fmag=-fmag;
				 fX = fmag * dX;
				 fY =-fmag * dY;
				 fZ = fmag * dZ;
				 
				 fx+=fX;
				 fy+=fY;
				 fz+=fZ;
			}			
		}
		
			float particleGravX = fx / mass1;
			float particleGravY = fy / mass1;
			float particleGravZ = fz / mass1;
			
			float gravchange1 = -(particleGravX * elapSec); 					// this is for x1
			float gravchange2 = -(particleGravY * elapSec) -grav * elapSec ;  	// this is for y1
			float gravchange3 = -(particleGravZ * elapSec); 					// this is for z1

			float xv1 = vX[gid];
			float yv1 = vY[gid];
			float zv1 = vZ[gid];

			xv1 += gravchange1;
			yv1 += gravchange2;
			zv1 += gravchange3;
			
			xv1 = xv1 * (1 - dragForce);
			yv1 = yv1 * (1 - dragForce);
			zv1 = zv1 * (1 - dragForce);

			x1 = scale * ((xv1) * (elapSec)) + x1;
			y1 = -(scale * (yv1) * elapSec) + y1;
			z1 = (scale * (zv1) * elapSec) + z1;

			// updating the position and velocity information in this class
			X[gid] = x1;
			Y[gid] = y1;
			Z[gid] = z1;

			vX[gid] = xv1;
			vY[gid] = yv1;
			vZ[gid] = zv1;
		
	}
	
	public void Collision(float[]pos,float[]vels,int i) {			
			X[i] =pos [0];
			vX[i]=vels[0];
			
			Y[i] =pos [1];
			vY[i]=vels[1];
			
			Z[i] =pos [2];
			vZ[i]=vels[2];	
	}
	

	public float[][] getPositions() {
		int size = X.length;
		float[][] positions = new float[size][3];
		for (int i = 0; i < size; i++) {
			positions[i][0] = X[i];
			positions[i][1] = Y[i];
			positions[i][2] = Z[i];
		}
		return positions;
	}

	public float[][] getVels() {
		int size = vX.length;
		float[][] Vels = new float[size][3];
		for (int i = 0; i < size; i++) {
			Vels[i][0] = vX[i];
			Vels[i][1] = vY[i];
			Vels[i][2] = vZ[i];
		}
		return Vels;
	}

	public void timeUpdate(float t) {
		time[0] = t;
		time[1]+=t;
		;
	}

	public float getTime() {
		return time[1];
	}

}