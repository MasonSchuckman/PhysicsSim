
import java.util.*;
import javax.vecmath.Vector3d;
import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class PhysicsSim {

	public boolean detecting = true, noBounds = false;
	private ArrayList<Freebody> objects;
	private ArrayList<TrackingPoint> tracks;
	private int counting = 0;
	boolean firstHitEver = true;
	float width, tall, depth;// dimensions of the universe.
	private float G;
	private float grav;
	private Range range;
	public gpuAccel GP;
	public Octree root;
	private float totalTime=0;
	private boolean checkCollisions=false;
	@SuppressWarnings("deprecation")
	public PhysicsSim(ArrayList<Freebody> objects, WorldCreator properties,ArrayList<TrackingPoint> trackers) {
		grav = properties.getGrav();
		width = properties.getWidth();
		tall = properties.getHeight();
		this.objects = objects;
		tracks=trackers;
		G = properties.getGForce();
		width = properties.getWidth();
		tall = properties.getHeight();
		depth = properties.getLength();
		checkCollisions=properties.checking;
		
		int size=objects.size();
		range = Range.create(size);

		//range=Range.create2D(size, 16);
		final float[] x=new float[size];
		final float[] y=new float[size];
		final float[] z=new float[size];
		final float[] xV=new float[size];
		final float[] yV=new float[size];
		final float[] zV=new float[size];
		final float[] mass=new float[size];
		final float[] drag=new float[size];

		for(int i=0; i<size;i++) {
			x[i]= objects.get(i).getX();	
			y[i]= objects.get(i).getY();	
			z[i]= objects.get(i).getZ();	

			xV[i]= objects.get(i).velocity.getX();	
			yV[i]= objects.get(i).velocity.getY();	
			zV[i]= objects.get(i).velocity.getZ();	

			mass[i]= objects.get(i).getMass();
			drag[i]= objects.get(i).getDrag();

		}
		G= (float) (6.674*Math.pow(11,G));
		GP=new gpuAccel(size,x,y,z,xV,yV,zV,mass,drag,(float)G,(float) grav);

		// if there aren't any bounds set(universe unlimited), then don't check for
		// collisions with walls
		if (properties.isBounds() == false) {
			noBounds = true;
		}
	}

	@SuppressWarnings("deprecation")
	public void update(float elapSec) {
		totalTime+=elapSec;
		//GPU computing of *all* motion calculations...Super fast!
		GP.timeUpdate((float)elapSec);				
		GP.execute(range);

		//Report target execution mode: GPU or JTP (Java Thread Pool).
		//System.out.println("Device = " + GP.getTargetDevice().toString()+"     "+GP.getKernelState());	

		//gets the Freebodys' Positions and Velocities.
		float [][]vels=GP.getVels();		
		float [][]positions=GP.getPositions();
		if(GP.getExecutionMode()==Kernel.EXECUTION_MODE.CPU) {
			//System.out.println("Device = " + GP.getTargetDevice().toString());	
			GP.setExecutionMode(Kernel.EXECUTION_MODE.GPU);
			//GP.getKernelState();
		}
		//updates each Freebodys' Positions and Velocities.
		for (int i = 0; i < objects.size(); i++) {

			float x=positions[i][0];
			float y=positions[i][1];
			float z=positions[i][2];

			float Vx=vels[i][0];
			float Vy=vels[i][1];
			float Vz=vels[i][2];

			Freebody c=objects.get(i);
			c.setPos(x, y, z);
			c.setVel(Vx, Vy, Vz);
			c.clearRecent(counting);
		}		

		counting++;
//		Long t = new Long(System.currentTimeMillis());	
//		
//		if(noBounds==false)checkWallCollisions();     //check object collision with walls if borders are on.
//		checks=0;
		if(checkCollisions) 
			checkBallCollisionsOctree(sceneSetup.rootNode,false);    //check object collision with other objects
		
//		System.out.println("Time: "+(System.currentTimeMillis()-t));
		
		//System.out.println(checks);
		for (int i = 0; i < objects.size(); i++) {
			objects.get(i).clearRecent(counting);
		}
	}
	public void interacting(ArrayList<Freebody>obs) {

	}

	public void interactionOccured(Freebody [] obs) { //method to call when 1 or more objects have been interacted with by the user
		for(int i=0; i<obs.length;i++) {
			Freebody a=obs[i];
			int index=objects.indexOf(a);
			float[]pos=new float[3];
			pos[0]=(float) a.getX();
			pos[1]=(float) a.getY();
			pos[2]=(float) a.getZ();
			float[]vels=new float[3];
			vels[0]=(float) a.velocity.getX();
			vels[1]=(float) a.velocity.getY();
			vels[2]=(float) a.velocity.getZ();
			GP.Collision(pos, vels,index);		
		}
	}

	public void trackersMoved(TrackingPoint [] trackers) { //method to call when 1 or more objects have been interacted with by the user
		for(int i=0; i<trackers.length;i++) {
			TrackingPoint t=trackers[i];
			int index=tracks.indexOf(t);
			float[]pos=new float[3];
			pos[0]=(float) t.getX();
			pos[1]=(float) t.getY();
			pos[2]=(float) t.getZ();
			float[]vels=new float[3];
			vels[0]=0;
			vels[1]=0;
			vels[2]=0;
			//GP.Collision(pos, vels,index); work on telling the GPU the tracker has moved..must implement		
		}
	}

	public void checkWallCollisions() {
		// Check for collision with walls
		for (int i = 0; i < objects.size(); i++) {
			boolean hit=false;
			Freebody c = objects.get(i);

			float bounce = c.bounciness;
			float b = bounce;
			float x=c.velocity.getX()*b;
			float y=c.velocity.getY()*b;
			float z=c.velocity.getZ()*b;
			if (c.position.getX() - c.getRadius() < 0) 
			{	
				c.position.setX(c.getRadius()); // Place ball against edge
				c.setVel(-x, y, z);
				hit=true;
			} else if (c.position.getX() + c.getRadius() > width) // Right Wall
			{				
				c.position.setX(width - c.getRadius()); // Place ball against edge
				c.setVel(-x, y, z);
				hit=true;

			}

			if (c.position.getY() - c.getRadius() < 0) // Top Wall
			{				
				c.position.setY(c.getRadius()); // Place ball against edge
				c.setVel(x, -y, z);
				hit=true;
			}

			else if (c.position.getY() + c.getRadius() > tall) // Bottom Wall
			{				
				c.position.setY(tall - c.getRadius()); // Place ball against edge
				c.setVel(x, -y, z);
				hit=true;
			}

			if (c.position.getZ() - c.getRadius() < 0) // front Wall
			{				
				c.position.setZ(c.getRadius()); // Place ball against edge
				c.setVel(x, y, -z);
				hit=true;
			}

			else if (c.position.getZ() + c.getRadius() > depth) // back Wall
			{				
				c.position.setZ(depth - c.getRadius()); // Place ball against edge
				c.setVel(x, y, -z);
				hit=true;
			}

			if(hit) { //inform the GPU that an object hit a wall, and adjust information based on that.
				float[]pos=new float[3];
				pos[0]=(float) c.getX();
				pos[1]=(float) c.getY();
				pos[2]=(float) c.getZ();
				float[]vels=new float[3];
				vels[0]=(float) c.velocity.getX();
				vels[1]=(float) c.velocity.getY();
				vels[2]=(float) c.velocity.getZ();
				GP.Collision(pos, vels,i);
			}
		}
	}

	int checks=0;
	public void checkBallCollisionsOctree(Octree node,boolean first) {

		if(!first) {
			ArrayList<Freebody>objects=node.obs;

			//System.out.println("Has: "+objects.size());

			for (int a = 0; a < objects.size(); a++) {
				for (int b = 0; b < objects.size(); b++) {
					if (a != b) {
						checks++;
						Freebody o = objects.get(a);
						Freebody p = objects.get(b);

						//						if(sceneSetup.rootNode.obs.contains(o)||sceneSetup.rootNode.obs.contains(p))
						//							continue;

						if (o.checkRecent(b, p) == false) {
							if (checkImpact(o, p)) {
								o.addHit(p.id, counting);
								p.addHit(o.id, counting);
								//System.out.println("Hit!");

								resolveCollision(o, p, a, b);
							}
						}
					}
				}
			}
		}
		for(Octree n:node.children) {
			if(n!=null)checkBallCollisionsOctree(n,false);
		}
	}

	public void checkBallCollisions() {
		for (int a = 0; a < objects.size(); a++) {
			for (int b = 0; b < objects.size(); b++) {
				if (a != b) {					
					Freebody o = objects.get(a);
					Freebody p = objects.get(b);
					//if(o.isController)continue;
					checks++;
					//if(p.isController)continue;
					if (o.checkRecent(b, p) == false) {
						if (checkImpact(o, p)) {
							o.addHit(p.id, counting);
							p.addHit(o.id, counting);
							//System.out.println("Hit!");
							resolveCollision(o, p, a, b);
						}

					}
				}
			}
		}
	}	

	public boolean checkImpact(Freebody a, Freebody b) {
		float dx = a.getX() - b.getX();
		float dy = a.getY() - b.getY();
		float dz = a.getZ() - b.getZ();
		float rad1 = a.getRadius();
		float rad2 = b.getRadius();
		//float distance = (float) Math.sqrt((dx * dx) + (dy * dy) + (dz * dz));
		float distance = (float) (dx * dx) + (dy * dy) + (dz * dz); //testing the squared method, versus sqrt method
		if (distance < (rad1 + rad2)*(rad1 + rad2)) {			
			//System.out.println(Math.sqrt(distance));
			return true;
		}
		return false;
	}

	public void Collision(Freebody a, Freebody b,int i1,int i2) {

		float m1 = a.getMass();
		float m2 = b.getMass();
		float xv1, xv2, yv1, yv2, zv1, zv2;

		xv1 = a.velocity.getX();
		xv2 = b.velocity.getX();
		yv1 = a.velocity.getY();
		yv2 = b.velocity.getY();
		zv1 = a.velocity.getZ();
		zv2 = b.velocity.getZ();

		float newv2 = ((2 * m1 * xv1 - m1 * xv2 + m2 * xv2) / (m1 + m2));
		float newv1 = xv2 - xv1 + newv2;
		float newvy2 = ((2 * m1 * yv1 - m1 * yv2 + m2 * yv2) / (m1 + m2));
		float newvy1 = yv2 - yv1 + newvy2;
		float newvz2 = ((2 * m1 * zv1 - m1 * zv2 + m2 * zv2) / (m1 + m2));
		float newvz1 = zv2 - zv1 + newvz2;

		a.velocity.set(newv1, newvy1, newvz1);
		b.velocity.set(newv2, newvy2, newvz2);

		//below lets the GPU know a collision occurred, and updates its information accordingly.
		//i1 and i2 are the indexes of each object.
		float[]pos=new float[3];
		pos[0]=(float) a.getX();
		pos[1]=(float) a.getY();
		pos[2]=(float) a.getZ();
		float[]vels=new float[3];
		vels[0]=(float) a.velocity.getX();
		vels[1]=(float) a.velocity.getY();
		vels[2]=(float) a.velocity.getZ();
		GP.Collision(pos, vels,i1);		
		pos=new float[3];
		pos[0]=(float) b.getX();
		pos[1]=(float) b.getY();
		pos[2]=(float) b.getZ();
		vels=new float[3];
		vels[0]=(float) b.velocity.getX();
		vels[1]=(float) b.velocity.getY();
		vels[2]=(float) b.velocity.getZ();
		GP.Collision(pos, vels,i2);
	}

	public void resolveCollision(Freebody a, Freebody b,int i1,int i2) {

		// b is ball, a is norm
		Vector3d delta = (a.position.subtract(b.position));
		float d = delta.getLength();
		// minimum translation distance to push objects apart after intersecting
		Vector3d mtd = delta.multiply((float) (((a.getRadius() + b.getRadius()+1) - d) / d));

		// resolve intersection --
		// inverse mass quantities
		float im1 = (float) (1 / a.getMass());
		float im2 = (float) (1 / b.getMass());

		// push-pull them apart based off their mass
		a.position = a.position.add(mtd.multiply(im1 / (im1 + im2)));
		b.position = b.position.subtract(mtd.multiply(im2 / (im1 + im2)));

		// impact speed
		Vector3d v = (a.velocity.subtract(b.velocity));
		float vn = v.dot(mtd.normalize());
		// System.out.println(vn);
		// sphere intersecting but moving away from each other already
		if (vn > 0)
			return;

		// collision impulse
		float i = (float) ((-(1.0 + 1) * vn) / (im1 + im2));

		Vector3d impulse = mtd.normalize().multiply(i);

		// change in momentum
		a.velocity = a.velocity.add(impulse.multiply(im1));
		b.velocity = b.velocity.subtract(impulse.multiply(im2));
		a.velocity.setY(-a.velocity.getY());
		b.velocity.setY(-b.velocity.getY());

		//below lets the GPU know a collision occurred, and updates its information accordingly.
		//i1 and i2 are the indexes of each object.
		float[]pos=new float[3];
		pos[0]=(float) a.getX();
		pos[1]=(float) a.getY();
		pos[2]=(float) a.getZ();
		float[]vels=new float[3];
		vels[0]=(float) a.velocity.getX();
		vels[1]=(float) a.velocity.getY();
		vels[2]=(float) a.velocity.getZ();			
		GP.Collision(pos, vels,a.id);		

		pos=new float[3];
		pos[0]=(float) b.getX();
		pos[1]=(float) b.getY();
		pos[2]=(float) b.getZ();
		vels=new float[3];
		vels[0]=(float) b.velocity.getX();
		vels[1]=(float) b.velocity.getY();
		vels[2]=(float) b.velocity.getZ();			
		GP.Collision(pos, vels,b.id);
	}

	public void testcol(Freebody a, Freebody b,int i1,int i2) {
		Freebody sphere1=a;
		Freebody sphere2=b;

		Vector3d collisionNormal = sphere1.position.subtract(sphere2.position);
		collisionNormal = collisionNormal.normalize();


		//Decompose v1 in parallel and orthogonal part
		float v1Dot = collisionNormal.dot(sphere1.velocity);
		Vector3d v1Collide = collisionNormal.multiply(v1Dot);
		Vector3d v1Remainder = sphere1.velocity.subtract(v1Collide);

		//Decompose v2 in parallel and orthogonal part
		float v2Dot =collisionNormal.dot(sphere2.velocity);
		Vector3d v2Collide = collisionNormal.multiply(v2Dot);
		Vector3d v2Remainder = sphere2.velocity.subtract(v2Collide);

		//Calculate the collision
		float v1Length = Math.copySign(v1Collide.getLength(),v1Dot);
		float v2Length = Math.copySign(v2Collide.getLength(),v2Dot);
		float commonVelocity = 2 * (sphere1.mass * v1Length + sphere2.mass * v2Length) / (sphere1.mass + sphere2.mass);
		float v1LengthAfterCollision = commonVelocity - v1Length;
		float v2LengthAfterCollision = commonVelocity - v2Length;
		v1Collide = v1Collide.multiply((v1LengthAfterCollision / v1Length));
		v2Collide = v2Collide.multiply((v2LengthAfterCollision / v2Length));

		sphere1.velocity = v1Collide.add(v1Remainder);
		sphere2.velocity = v2Collide.add(v2Remainder);
		//System.out.println(sphere1.velocity.getLength());
		//System.out.println(sphere2.velocity.getX()+" "+sphere2.velocity.getY()+" "+sphere2.velocity.getZ()+" ");
		float[]pos=new float[3];
		pos[0]=(float) a.getX();
		pos[1]=(float) a.getY();
		pos[2]=(float) a.getZ();
		float[]vels=new float[3];
		vels[0]=(float) a.velocity.getX();
		vels[1]=(float) a.velocity.getY();
		vels[2]=(float) a.velocity.getZ();			
		GP.Collision(pos, vels,i1);		

		pos=new float[3];
		pos[0]=(float) b.getX();
		pos[1]=(float) b.getY();
		pos[2]=(float) b.getZ();
		vels=new float[3];
		vels[0]=(float) b.velocity.getX();
		vels[1]=(float) b.velocity.getY();
		vels[2]=(float) b.velocity.getZ();			
		GP.Collision(pos, vels,i2);
	}

}
