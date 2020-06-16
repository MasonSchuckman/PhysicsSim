
import java.io.IOException;
import java.util.ArrayList;

public class presets { 
	//this class is to store presets of start.java
	//use manEdit to make any new start.java's.
	//if you want to save a preset, make a new public static method, copy all of manEdit to that, and done!
	private static ArrayList<Freebody> list;
	public static WorldCreator uni;
	public static ArrayList<TrackingPoint>trackers;
	public static int [] camPos;
	
	//each preset calls this method so that with future additions, no null pointer exceptions
	//are thrown when attempting to access a feature that was not yet present when the preset was made.
	public static void initializePreqs() { 
		list = new ArrayList<Freebody>();
		trackers = new ArrayList<TrackingPoint>();
		camPos=new int[3];
	}
	
	public static ArrayList<Freebody> testing(){
		
		initializePreqs();
		int size       = 5;      
		float bounce  = 1; 
		float drag    = 0;		
		int locX,locY,locZ;		
		int dis        = 20;			
		float mainMass= 1000000;	
		
		int worldSize  = 0;		
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } 
		else { locX=1000;locY=1000;locZ=1000; }		
//		list.add(new Freebody(0, 1,1,locX,locY,locZ,     (int)(size)*1, bounce, 0,list.size())); 
//		
//		list.add(new Freebody(0, 1,1,locX+dis,locY,locZ,     (int)(size)*1, bounce, 0,list.size())); 
//		list.add(new Freebody(0, 1,1,locX+dis/2,locY+dis/2,locZ,     (int)(size)*1, bounce, 0,list.size()));
//		list.add(new Freebody(0, 1,1,locX+dis/2,locY-dis/2,locZ,     (int)(size)*1, bounce, 0,list.size()));
		//trackers.add(new TrackingPoint(locX,locY,locZ,    (int)(size),0));//controller 1
		//trackers.add(new TrackingPoint(locX+dis,locY,locZ,(int)(size),1));//controller 2	
		//trackers.get(0).makeLightsaber();
		
		boolean usingShapeCreator=true;
		if(usingShapeCreator) {
			ShapeCreator s=new ShapeCreator();
			int spacer=20;
			int[][]p=s.getPoints(spacer);
			int multi=2;
			int x=0;
			int y=0;
			int num=0;
			int xmax=0;
			int ymax=0;
			for(int i[]:p) {
				
				list.add(new Freebody(0, 0,1,locX+(i[0]*multi),locY+(i[1]*multi),locZ,(int)(size)*1, bounce, 0,list.size())); 
				list.get(num).setVel(0,0,300);
				num++;
				x+=i[0]*multi;
				y+=i[1]*multi;
				xmax=Math.max(xmax, i[0]*multi);
				ymax=Math.max(ymax, i[1]*multi);
			}
			System.out.println(xmax);
			int xAvg=x/num;
			int yAvg=y/num;
			camPos[0]=locX+xAvg;
			camPos[1]=locY+yAvg;
			camPos[2]=locZ+(int)(xmax/1.3);
			uni = new WorldCreator(worldSize, worldSize, worldSize, 0,1f,locX+xAvg,locY+yAvg,locZ+(int)(xmax/1.3));
			System.out.println(camPos[0]+"  "+camPos[1]+ "  "+camPos[2]);
		}else
			uni = new WorldCreator(worldSize, worldSize, worldSize, 0,4,locX,locY,locZ);
		
		
		uni.collisions(true);		//turns on object/object collisions
		
		return list;
	}
	
	
	public static ArrayList<Freebody> manEdit(){
			initializePreqs();
			int size = 10;      //radius of balls
			float bounce = 1; //elasticity of balls
			float drag=0;
			float secDrag=0;
			// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
			// away all velocity.
			// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
			int locX,locY,locZ;
			
			int dis=800;			    	//distance between the main mass and secondary masses
			
			float invertMass= 1400000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
			float mainMass  = 1200000;		//mass of the main Freebody.
			
			float ratio=(invertMass/mainMass);
			int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
			int worldSize=0;
			
			if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
			            else { locX=2500;locY=2500;locZ=2500; }
			int larges=0; 
			//Black hole
			invertMass=-invertMass;
			//list.add(new Freebody(0, 1,mainMass,locX,locY,locZ,     (int)(size)*10, bounce, drag)); larges++;
			//list.add(new Freebody(0, 1, mainMass, locX-dis,locY,locZ,(int)(size)*10, bounce, drag)); larges++;
			//below is secondary masses creation
			//z axis offset
			//list.add(new Freebody(0,  0, invertMass, locX,locY,locZ+dis, secSize, bounce, secDrag)); larges++;
			//list.add(new Freebody(-0, 0, invertMass, locX,locY,locZ-dis, secSize, bounce, secDrag)); larges++;
			//x axis offset
			//list.add(new Freebody(0,  0, invertMass, locX+dis,locY,locZ, secSize, bounce, secDrag)); larges++;
			//list.add(new Freebody(-0, 0, invertMass, locX-dis,locY,locZ, secSize, bounce, secDrag)); larges++;
			//y axis offset
			list.add(new Freebody(350,  180, -invertMass, locX,locY+dis,locZ, secSize, bounce, secDrag)); larges++;
			list.add(new Freebody(350, 0, 3*invertMass, locX,locY-dis,locZ, secSize, bounce, secDrag)); larges++;
			//end of large masses creation
			float spedd=700;
			
//			float vX2=-spedd*2,vY2=spedd*2,vZ2=-spedd*2;			
//				list.get(1).setVel(-vX2, 0, 0);
//				list.get(2).setVel(vX2, -0, -0);
//				list.get(3).setVel(-0, vY2, 0);
//				list.get(4).setVel(0, -vY2, -0);
//				list.get(5).setVel(100, 0, vZ2);
//				list.get(6).setVel(-100, -0, -vZ2);
				
			//list.get(0).makeController(50);
			//variable to make it easier to change the # of particles
			int c;
		    /**
			*below is explaining how to setup the particles around the center mass.
			*make symmetry with 1000 open space on both sides
			*worldsize-2000=usable space
			*c*2*d is space used by objects
			*worldsize-2000=c*2*d
			*d=(worldsize-2000)/(2c)
			*starting x is 1000.
			*
			*y starting is so that c/2 is at locY.
			*worldSize/2=y+(c/2*d)
			*y starting = (worldSize/2)-(c/2*d)
			*END of explanation
			*/
			c=0;
			if(c!=0) {
			float d=(locX*2-1000)/(2*c);
			float yStart=(locY)+((c/4)*d);
			
			//setting the max sizes of i,j,k in the for loops.
			int I=c;//how many tall
			int J=c;//how many wide
			int K=4;//how many deep
			
			//speed and angle of particles, defaulted inverted for symmetry.
			int sped=100;
			int ang=45;
			float Drag=0;//drag of the particles
			int zOffset=1000; //how far away from locZ the particles start
			int start=(int) (locX-((c/2)*d));
			for(int i=0; i<I; i++) {
				for(int j=0; j<J;j++) {
					for(int k=0; k<K;k++) { 
						list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -zOffset), size, bounce, Drag));
					}
				}
			}
			for(int i=0; i<I; i++) {
				for(int j=0; j<J;j++) {
					for(int k=0; k<K;k++) {
						list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +zOffset), size, bounce, Drag));
					}
				}
			}
			
			int obsInOnHalf=I*J*K; //how many particles(Freebodys) are in one "half" (presets are normally made with 2 distinct halves)
									 //below is to set velocities of every particle easily.
			           //set this value equal to how many large masses were made.
			
			
			//vels of particles
			float vX=sped*4,vY=sped*0,vZ=sped*0;
			for(int i=5; i<(obsInOnHalf)+larges;i++) {
				list.get(i).setVel(vX, vY, vZ);
			}
			for(int i=(obsInOnHalf)+larges; i<list.size();i++) {
				list.get(i).setVel(-vX, -vY, -vZ);
			}
			}
			//set worldSize to 0 for no boundaries (unlimited universe)			
			uni = new WorldCreator(worldSize, worldSize, worldSize, 0,1.5f,locX,locY,locZ);
			
			//trackers.add(new TrackingPoint(locX,locY,locZ,    (int)(size),0));//controller 1
			//trackers.add(new TrackingPoint(locX+dis,locY,locZ,(int)(size),1));//controller 2	
			//turns on(or off) object/object collisions, off by default.
			uni.collisions(false);			
			return list;
		}
	
	public static ArrayList<Freebody> preset6(){//electron orbits kinda
		
		initializePreqs();
		int size = 15;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=0;
		float secDrag=0;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=2000;			    	//distance between the main mass and secondary masses
		
		float invertMass= 220000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 600000;		//mass of the main Freebody.
		
		float ratio=(invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		int larges=0; 
		//Black hole
		list.add(new Freebody(0, 1, mainMass,   locX,locY,locZ, (int)(size)*10, bounce, drag)); larges++;
		
		//below is secondary masses creation
		//z axis offset
		list.add(new Freebody(0,  0, invertMass, locX,locY,locZ+dis, secSize, bounce, secDrag)); larges++;
		list.add(new Freebody(-0, 0, invertMass, locX,locY,locZ-dis, secSize, bounce, secDrag)); larges++;
		//x axis offset
		list.add(new Freebody(0,  0, invertMass, locX+dis,locY,locZ, secSize, bounce, secDrag)); larges++;
		list.add(new Freebody(-0, 0, invertMass, locX-dis,locY,locZ, secSize, bounce, secDrag)); larges++;
		//y axis offset
		list.add(new Freebody(0,  0, invertMass, locX,locY+dis,locZ, secSize, bounce, secDrag)); larges++;
		list.add(new Freebody(-0, 0, invertMass, locX,locY-dis,locZ, secSize, bounce, secDrag)); larges++;
		//end of large masses creation
		float spedd=700;
		float vX2=-spedd*2,vY2=spedd*2,vZ2=-spedd*2;			
			list.get(1).setVel(-vX2, 0, 0);
			list.get(2).setVel(vX2, -0, -0);
			list.get(3).setVel(-0, vY2, 0);
			list.get(4).setVel(0, -vY2, -0);
			list.get(5).setVel(0, 0, vZ2);
			list.get(6).setVel(-0, -0, -vZ2);
			
		
		//variable to make it easier to change the # of particles
		int c=00;
	    /*===================================================
		below is explaining how to setup the particles around the center mass.
		make symmetry with 1000 open space on both sides
		worldsize-2000=usable space
		c*2*d is space used by objects
		worldsize-2000=c*2*d
		d=(worldsize-2000)/(2c)
		starting x is 1000.
		
		y starting is so that c/2 is at locY.
		worldSize/2=y+(c/2*d)
		y starting = (worldSize/2)-(c/2*d)
		END of explanation
		*/
		if(c!=0) {
		float d=(locX*2-1000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		//setting the max sizes of i,j,k in the for loops.
		int I=c;
		int J=c;
		int K=c;
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=600;
		int ang=30;
		
		int start=(int) (locX-((c/2)*d));
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -1000), size, bounce, 0.001f));
				}
			}
		}
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +1000), size, bounce, 0.001f));
				}
			}
		}
		
		int obsInOnHalf=I*J*K; //how many particles(Freebodys) are in one "half" (presets are normally made with 2 distinct halves)
								 //below is to set velocities of every particle easily.
		           //set this value equal to how many large masses were made.
		
		
		//vels of particles
		float vX=sped*2,vY=sped*2,vZ=sped/2;
		for(int i=5; i<(obsInOnHalf)+larges;i++) {
			list.get(i).setVel(vX, vY, vZ);
		}
		for(int i=(obsInOnHalf)+larges; i<list.size();i++) {
			list.get(i).setVel(-vX, -vY, -vZ);
		}
		}
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,3,locX,locY,locZ);
		
		//turns on(or off) object/object collisions, off by default.
		uni.collisions(false);			
		return list;
	}
	public static ArrayList<Freebody> preset5(){
		
		initializePreqs();
		int size = 8;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=1;
		float secDrag=.5f;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=1500;			    	//distance between the main mass and secondary masses
		
		float invertMass=-250000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 450000;		//mass of the main Freebody.
		
		float ratio=(-invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		int larges=0; 
		//Black hole
		list.add(new Freebody(0, 0, mainMass,  locX,locY,locZ, (int)(size)*10, bounce, drag)); larges++;
		
		//below is secondary masses creation
		//z axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ+dis, secSize,  bounce, 1));larges++;
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ-dis, secSize,  bounce, 1));larges++;
		//x axis offset
		list.add(new Freebody(0, 0, -invertMass, locX+dis,locY,locZ, secSize, bounce, secDrag));larges++;
		list.add(new Freebody(0, 0, -invertMass, locX-dis,locY,locZ, secSize, bounce, secDrag));larges++;
		//y axis offset
		list.add(new Freebody(0, 0, invertMass, locX,locY+dis,locZ, secSize, bounce, secDrag));larges++;
		list.add(new Freebody(0, 0, invertMass, locX,locY-dis,locZ, secSize, bounce, secDrag));larges++;
		//end of large masses creation
		
		
		
		//variable to make it easier to change the # of particles
		int c=30;
		
		//below is explaining how to setup the particles around the center mass.
		//make symmetry with 1000 open space on both sides
		//worldsize-2000=usable space
		//c*2*d is space used by objects
		//worldsize-2000=c*2*d
		//d=(worldsize-2000)/(2c)
		//starting x is 1000.
		
		//y starting is so that c/2 is at locY.
		//worldSize/2=y+(c/2*d)
		//y starting = (worldSize/2)-(c/2*d)
		//END of explanation
		
		float d=(locX*2-2000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		//setting the max sizes of i,j,k in the for loops.
		int I=1*c;
		int J=2*c;
		int K=2;
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=400;
		int ang=30;
		
		int start=1000;
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -1000), size, bounce, (float) 0.001));
				}
			}
		}
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +1000), size, bounce, (float) 0.001));
				}
			}
		}
		
		int obsInOnHalf=I*J*K; //how many particles(Freebodys) are in one "half" (presets are normally made with 2 distinct halves)
								 //below is to set velocities of every particle easily.
		           //set this value equal to how many large masses were made.
		
		//vels of particles
		float vX=(float) (sped*1.5),vY=(float) (sped*1.5),vZ=0;
		for(int i=5; i<(obsInOnHalf)+larges;i++) {
			list.get(i).setVel(vX, vY, vZ);
		}
		for(int i=(obsInOnHalf)+larges; i<list.size();i++) {
			list.get(i).setVel(-vX, -vY, -vZ);
		}
		
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,(float) 2.1,locX,locY,locZ);
		
		//turns on(or off) object/object collisions, off by default.
		uni.collisions(false);			
		return list;
	}
	
	public static ArrayList<Freebody> preset4(){//reminds me of a supernova?
		
		initializePreqs();
		int size = 8;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=1;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=1500;			    	//distance between the main mass and secondary masses
		
		float invertMass=-200000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 850000;		//mass of the main Freebody.
		
		float ratio=(-invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		
		//Black hole
		list.add(new Freebody(0, 0, mainMass,  locX,locY,locZ, (int)(size)*10, bounce, drag));
		
		//below is secondary masses creation
		//z axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ+dis, secSize, size, size, bounce, 1, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ-dis, secSize, size, size, bounce, 1, Color.blue));
		//x axis offset
		//list.add(new Freebody(0, 0, invertMass, locX+dis,locY,locZ, secSize, size, size, bounce, drag, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX-dis,locY,locZ, secSize, size, size, bounce, drag, Color.blue));
		//y axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY+dis,locZ, secSize, size, size, bounce, drag, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY-dis,locZ, secSize, size, size, bounce, drag, Color.blue));
		//end of large masses creation
		
		
		
		//variable to make it easier to change the # of particles
		int c=25;
		
		//below is explaining how to setup the particles around the center mass.
		//make symmetry with 1000 open space on both sides
		//worldsize-2000=usable space
		//c*2*d is space used by objects
		//worldsize-2000=c*2*d
		//d=(worldsize-2000)/(2c)
		//starting x is 1000.
		
		//y starting is so that c/2 is at locY.
		//worldSize/2=y+(c/2*d)
		//y starting = (worldSize/2)-(c/2*d)
		//END of explanation
		
		float d=(locX*2-2000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		//setting the max sizes of i,j,k in the for loops.
		int I=1*c;
		int J=2*c;
		int K=2;
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=500;
		int ang=30;
		
		int start=1000;
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -1000), size, bounce, 0.001f));
				}
			}
		}
		for(int i=0; i<I; i++) {
			for(int j=0; j<J;j++) {
				for(int k=0; k<K;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +1000), size, bounce, 0.001f));
				}
			}
		}
		
		int obsInOnHalf=I*J*K; //how many particles(Freebodys) are in one "half" (presets are normally made with 2 distinct halves)
								 //below is to set velocities of every particle easily.
		int larges=1;            //set this value equal to how many large masses were made.
		
		//vels of particles
		float vX=sped,vY=sped,vZ=0;
		for(int i=5; i<(obsInOnHalf)+larges;i++) {
			list.get(i).setVel(vX, vY, vZ);
		}
		for(int i=(obsInOnHalf)+larges; i<list.size();i++) {
			list.get(i).setVel(-vX, -vY, 0);
		}
		
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,2,locX,locY,locZ);
		
		//turns on(or off) object/object collisions, off by default.
		uni.collisions(false);			
		return list;
	}
	
	public static ArrayList<Freebody> preset3(){//really cool "egg" kinda thing..idrk
		
		initializePreqs();
		int size = 8;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=1;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=1500;			    	//distance between the main mass and secondary masses
		
		float invertMass=-200000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 650000;		//mass of the main Freebody.
		
		float ratio=(-invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		
		//Black hole
		//list.add(new Freebody(0, 0, mainMass,  locX,locY,locZ, (int)(size)*10, bounce, drag));
		
		//below is secondary masses creation
		//z axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ+dis, secSize, size, size, bounce, 1, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ-dis, secSize, size, size, bounce, 1, Color.blue));
		//x axis offset
		//list.add(new Freebody(0, 0, invertMass, locX+dis,locY,locZ, secSize, size, size, bounce, drag, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX-dis,locY,locZ, secSize, size, size, bounce, drag, Color.blue));
		//y axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY+dis,locZ, secSize, size, size, bounce, drag, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY-dis,locZ, secSize, size, size, bounce, drag, Color.blue));
		//end of large masses creation
		
		
		
		//variable to make it easier to change the # of particles
		int c=30;
		
		//below is explaining how to setup the particles around the center mass.
		//make symmetry with 1000 open space on both sides
		//worldsize-2000=usable space
		//c*2*d is space used by objects
		//worldsize-2000=c*2*d
		//d=(worldsize-2000)/(2c)
		//starting x is 1000.
		
		//y starting is so that c/2 is at locY.
		//worldSize/2=y+(c/2*d)
		//y starting = (worldSize/2)-(c/2*d)
		//END of explanation
		
		float d=(locX*2-2000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=1600;
		int ang=30;
		
		int start=1000;
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -1000), size, bounce, 0.001f));
				}
			}
		}
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +1000), size, bounce, 0.001f));
				}
			}
		}
		
		int obsInOnHalf=c*c*2*2; //how many particles(Freebodys) are in one "half" (presets are normally made with 2 distinct halves)
								 //below is to set velocities of every particle easily.
		int larges=0;            //set this value equal to how many large masses were made.
		for(int i=5; i<(obsInOnHalf)+larges;i++) {
			list.get(i).setVel(-0, -sped/3, sped/2);
		}
		for(int i=(obsInOnHalf)+larges; i<list.size();i++) {
			list.get(i).setVel(0, sped/3, -sped/2);
		}
		
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,2,locX,locY,locZ);
		
		//turns on(or off) object/object collisions, off by default.
		uni.collisions(false);			
		return list;
	}
	
	public static ArrayList<Freebody> preset2(){ //trapped particles
		
		initializePreqs();
		int size = 8;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=1;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=2000;			    	//distance between the main mass and secondary masses
		
		float invertMass=-200000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 900000;		//mass of the main Freebody.
		
		float ratio=(-invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		
		//Black hole
		list.add(new Freebody(0, 0, mainMass,  locX,locY,locZ,     (int)(size)*10,bounce, drag));
		
		//below is secondary masses creation
		//z axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ+dis, secSize, size, size, bounce, 1, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ-dis, secSize, size, size, bounce, 1, Color.blue));
		//x axis offset
		list.add(new Freebody(0, 0, invertMass, locX+dis,locY,locZ, secSize,  bounce, drag));
		list.add(new Freebody(0, 0, invertMass, locX-dis,locY,locZ, secSize,  bounce, drag));
		//y axis offset
		list.add(new Freebody(0, 0, invertMass, locX,locY+dis,locZ, secSize,  bounce, drag));
		list.add(new Freebody(0, 0, invertMass, locX,locY-dis,locZ, secSize,  bounce, drag));
		//end of large masses creation
		
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=600;
		int ang=30;
		
		//variable to make it easier to change the # of particles
		int c=20; 
		
		//make symmetry with 1000 open space on both sides
		//worldsize-2000=usable space
		//c*2*d is space used by objects
		//worldsize-2000=c*2*d
		//d=(worldsize-2000)/(2c)
		//starting x is 1000.
		
		//y starting is so that c/2 is at locY.
		//worldSize/2=y+(c/2*d)
		//y starting = (worldSize/2)-(c/2*d)
		
		float d=(locX*2-2000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		
		int start=1000;
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -2500),  size, bounce, 0.001f));
				}
			}
		}
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +2500),  size, bounce, 0.001f));
				}
			}
		}
		for(int i=5; i<(c*c*2*2)+5;i++) {
			list.get(i).setVel(-sped, -sped/3, sped/2);
		}
		for(int i=(c*c*2*2)+5; i<list.size();i++) {
			list.get(i).setVel(sped, sped/3, -sped/2);
		}
		
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,2,locX,locY,locZ);
		return list;
	}
	
	public static ArrayList<Freebody> preset1(){ //idk
		initializePreqs();
		int size = 8;      //radius of balls
		float bounce = 1; //elasticity of balls
		float drag=.5f;
		// DRAG IS A VALUE BETWEEN 0 AND 1!! 1 would make it completely stop, by sapping
		// away all velocity.
		// 0.5 would reduce the speed by 1/2 each tick, and 0 is no friction
		int locX,locY,locZ;
		
		int dis=1200;			    	//distance between the main mass and secondary masses
		
		float invertMass=-45000;		//mass of the secondary masses, negative mass makes them repel the smaller particles, not attract
		float mainMass  = 350000;		//mass of the main Freebody.
		
		float ratio=(-invertMass/mainMass);
		int secSize=(int) ((Math.cbrt(ratio)*10*size)); 	    //size of secondary masses; size is proportional to mass.
		int worldSize=0;
		
		if(worldSize!=0) { locX=worldSize/2;locY=worldSize/2;locZ=worldSize/2; } //location of the action, typically the middle of the scene, or where the main mass is located.
		            else { locX=2500;locY=2500;locZ=2500; }
		
		//Black hole
		list.add(new Freebody(0, 0, mainMass,  locX,locY,locZ,     (int)(size)*10,  bounce, drag));
		
		//below is secondary masses creation
		//z axis offset
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ+dis, secSize, size, size, bounce, 1, Color.blue));
		//list.add(new Freebody(0, 0, invertMass, locX,locY,locZ-dis, secSize, size, size, bounce, 1, Color.blue));
		//x axis offset
		list.add(new Freebody(0, 0, invertMass, locX+dis,locY,locZ, secSize,  bounce, drag));
		list.add(new Freebody(0, 0, invertMass, locX-dis,locY,locZ, secSize,  bounce, drag));
		//y axis offset
		list.add(new Freebody(0, 0, invertMass, locX,locY+dis,locZ, secSize, bounce, drag));
		list.add(new Freebody(0, 0, invertMass, locX,locY-dis,locZ, secSize,  bounce, drag));
		//end of large masses creation
		
		//speed and angle of particles, defaulted inverted for symmetry.
		int sped=600;
		int ang=30;
		
		//variable to make it easier to change the # of particles
		int c=25; 
		
		//make symmetry with 1000 open space on both sides
		//worldsize-2000=usable space
		//c*2*d is space used by objects
		//worldsize-2000=c*2*d
		//d=(worldsize-2000)/(2c)
		//starting x is 1000.
		
		//y starting is so that c/2 is at locY.
		//worldSize/2=y+(c/2*d)
		//y starting = (worldSize/2)-(c/2*d)
		
		float d=(locX*2-2000)/(2*c);
		float yStart=(locY)-((c/2)*d);
		
		int start=1000;
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) { 
					list.add(new Freebody(-sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(-k*d)+(locZ -1000), size,  bounce, 0.1f));
				}
			}
		}
		for(int i=0; i<c; i++) {
			for(int j=0; j<c*2;j++) {
				for(int k=0; k<2;k++) {
					list.add(new Freebody( sped, ang, 1, start+(int)(d*j), (int)(i*d+yStart), (int)(k*d)+(locZ +1000), size, bounce, 0.1f));
				}
			}
		}
		
		//set worldSize to 0 for no boundaries (unlimited universe)			
		uni = new WorldCreator(worldSize, worldSize, worldSize, 0,3,locX,locY,locZ);
		return list;
	}
}
