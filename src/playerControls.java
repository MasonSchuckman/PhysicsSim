import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JLabel;


import javafx.animation.AnimationTimer;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.PerspectiveCamera;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.transform.Affine;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javafx.util.Callback;


public class playerControls extends Parent {
	private  ArrayList<Freebody> list;
	private  ArrayList<TrackingPoint> trackers;
	private TrackingPoint controller1, controller2;
		
	public FileWriter fileWriter;
	public PrintWriter printWriter; 
	private WorldCreator uni;
	private motionCapture tracker;
	public boolean motionControls=false;
    public playerControls(ArrayList<Freebody> l,boolean mC, WorldCreator uni,ArrayList<TrackingPoint> TrackingPoints) throws IOException {
        list=l;
        this.uni=uni;
    	motionControls=mC;
    	if(motionControls) { //sets up motion controls if that's enabled.
    		trackers=TrackingPoints;    	   	
        	controller1=trackers.get(0);
        	if(trackers.size()==2)controller2=trackers.get(1);
        	
    		tracker=new motionCapture(this);
    		tracker.start(); 
    	}
    	//String outputPath="C:\\Users\\suprm\\Desktop\\newBadData.txt";
    	//fileWriter = new FileWriter(outputPath);
	    //printWriter = new PrintWriter(fileWriter);
	    try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {			
			e.printStackTrace();
		}
	    initialize(); 
    }
    //CURRENT CONTROLS:
    //ALT + LMOUSE IS MOVE SPHERE OF CONTROL
    //ALT + RMOUSE IS INFLUENCE INSIDE SPHERE OF CONTROL
    //CAN DO BOTH AT THE SAME TIME!
    private void initialize() {
        getChildren().add(root);
        getTransforms().add(affine);        
        startUpdateThread();
    }
    
    private void update() {
        //updateControls();
        if(motionControls)lightControls(); 
        updateRot();
    }
    
   
    private void startUpdateThread() {
        new AnimationTimer() {
            @Override
            public void handle(long now) {            	            	
                update();
            }
        }.start();
    }    
   
    //light controls
    private float[][]rawXY1=new float[12][2];
	private float[][]rawXY2=new float[12][2];
	int counterr=0;
	public void addRawXY(float r1[],float r2[]) {
		rawXY1[counting]=r1;
		rawXY2[counting]=r2;
		setPos(r1,r2);
		//counting++;
		if(counting==12) 		
			counting=0;
//		if(recording) {
//			counting++;
//			if(counting<12) {
//				System.out.println("Camera one:  X:"+ rawXY1[counting][0]+"   Y:"+rawXY1[counting][1]);
//				System.out.println("Camera two:  X:"+ rawXY2[counting][0]+"   Y:"+rawXY2[counting][1]);
//			}
//			else if(counting==12) {
//				//System.out.println("Camera one:  X:"+ rawXY1[counting][0]+"   Y:"+rawXY1[counting][1]);
//				//System.out.println("Camera two:  X:"+ rawXY2[counting][0]+"   Y:"+rawXY2[counting][1]);
//				counting=0;
//				recording=false;
//				//System.out.print("]\n");
//			} 		
	}
    private float [][]raw=new float[2][3];
    private float [][]pos=new float[2][3];  //change to trackers.size()  
    
    private ArrayList<float[]>poses=new ArrayList<float[]>(50);
    private ArrayList<float[]>poses2=new ArrayList<float[]>(50);
    boolean full=false;
    
    public void setPos(float r1[],float r2[])    {
        poses.add(r1);
        poses2.add(r2);
        if(!full) {
        	if(poses.size()>50) {
        		full=true;
        		poses.remove(0);
        		poses2.remove(0);
        	}
        }else {
        	poses.remove(0); 
        	poses2.remove(0);
        }
    }
    public float[][]getXFramesOfData(int count){ //this returns raw data
    	float [][]data=new float[count][3];    	
    	for(int i=0; i<count; i++) {    		
    		float []pos=poses.get(poses.size()-1-i);    		
    		//System.out.println(pos[0]+ "  "+pos[1]+ "  "+pos[2]+ "  ");
    		data[i]=(pos);    		    		
    	}    	
    	return data;
    }
    public float[][]getXFramesOfData2(int count){ //this returns raw data
    	float [][]data=new float[count][3];    	
    	for(int i=0; i<count; i++) {    		
    		float []pos=poses2.get(poses2.size()-1-i);    		
    		//System.out.println(pos[0]+ "  "+pos[1]+ "  "+pos[2]+ "  ");
    		data[i]=(pos);    		    		
    	}    	
    	return data;
    }
    int lines=0;
    boolean stop=false;
    public void addData(float positions[][],float _raw[][]) {
    	pos=positions;
    	raw=_raw;
    	if(recording) {
			counting++;
			if(counting<12) {
			//System.out.print("["+raw[0][0]+","+raw[0][1]+","+raw[0][2]+"],\n");
			printWriter.print(raw[0][0]+","+raw[0][1]+","+raw[0][2]+"\n");
			lines++;
			}
			else if(counting==12) {
				//System.out.print("["+raw[0][0]+","+raw[0][1]+","+raw[0][2]+"]],\n");
				printWriter.print(raw[0][0]+","+raw[0][1]+","+raw[0][2]+"\n");
				counting=0;
				lines++;
				recording=false;
				//System.out.print("]\n");
				printWriter.print("\n");
				if(stop) {
					printWriter.close();
					//System.out.print("CLOSED");
				}
				if(lines>5000) {
					printWriter.close();
					//System.out.print("CLOSED");
				}
			}    			
		}
    }
    
    public boolean isPush(float[][] webcam1, float[][] webcam2, float[][] input) {
        float minMagnitude = 17;
        int totalFrames = input[0].length;
        float[] startPos = input[0];
        float[] endPos = input[totalFrames-1];
        float xDisp = endPos[0] - startPos[0];
        float yDisp = endPos[1] - startPos[1];
        float zDisp = endPos[2] - startPos[2];
        float magnitude = (float)Math.sqrt((xDisp * xDisp) + (yDisp * yDisp) + (zDisp * zDisp));
       
        if   (magnitude >= minMagnitude){
        	 boolean webcam1IsLine = isLinear(webcam1);
             boolean webcam2IsLine = isLinear(webcam2);            
            if  (webcam1IsLine && webcam2IsLine){
                //System.out.println(magnitude);
                //System.out.println(high);
            	return true;
            }
        }
        return false;
    }
    private float high=0;
    public boolean isLinear(float[][] webcam) {
        float threshold = .4f;
        float[] startPos = webcam[0];
        float[] endPos = webcam[webcam.length - 1];
        float rise = endPos[1] - startPos[1];
        float run = endPos[0] - startPos[0];
        float slope = rise / run;
        high=0;
        boolean isLinear = true;
        for (int i = 1; i < webcam.length; i++) {
            float[] currentPos = webcam[i];
            float xDistance = currentPos[0] - startPos[0];
            float lineYDistance = slope * xDistance;
            float trueYDistance = currentPos[1] - startPos[1];
            if(Math.abs(lineYDistance - trueYDistance)>high)high=Math.abs(lineYDistance - trueYDistance);
            if (Math.abs(lineYDistance - trueYDistance) > threshold) {
                isLinear = false;
            }
            
        }
       
        if (isLinear) {
        	 //
            int[][] visual = new int[640][480];
            for (int i = 0 ; i < webcam.length; i++) {
                float[] currentPos = webcam[i];
                int currentX = (int)currentPos[0];
                int currentY = (int)currentPos[1];
                //System.out.println(currentX+ " "+currentY);
                visual[currentX][currentY] = 1;
            }
            for (int x = 0; x < 640; x++) {
                for (int y = 0; y < 480; y++) {
                    //System.out.print(visual[x][y] + " ");
                }               
            }
        }
        return isLinear;
    }
//    public void addData(float positions[][],float _raw[][]) { //for camera 1
//    	pos=positions;
//    	raw=_raw;
//    	if(recording) {
//			counting++;
//			if(counting<12)
//			System.out.print(raw[0][0]+","+raw[0][1]+","+raw[0][2]+"\n");
//			else if(counting==12) {
//				System.out.print(raw[0][0]+","+raw[0][1]+","+raw[0][2]+"\n");
//				counting=0;
//				recording=false;
//				//System.out.print("]\n");
//			}    			
//		}
//    }
    
    private void updateControls() {//replaced currently by gesture controls
        if(shift) {        	
        }
        if(num1) { 
        }
        if(num6) {        	
        }
    }
    
    private final Group root = new Group();
    private final Affine affine = new Affine();
   
            
    //numpad 1 is "grab" objects, numpad6 is move the sphere of influence.
    //prob use Lmouse and numpad1 to move objects with sphere
    private boolean fwd, strafeL, strafeR, back, up, down, shift,num1,num6,cond,cond2;

    private float mouseSpeed = 1.0f, mouseModifier = 0.1f;
    private float moveSpeed = 10.0f;
    private float mousePosX;
    private float mousePosY;
    private float mouseOldX;
    private float mouseOldY;
    private float mouseDeltaX;
    private float mouseDeltaY;
    private float sw=0;
    private float wheelSpeed=1;
    private boolean recording=false;
    public void loadControlsForScene(Scene scene) {
    	
        scene.addEventHandler(KeyEvent.ANY, ke -> {
            if (ke.getEventType() == KeyEvent.KEY_PRESSED) {
                switch (ke.getCode()) {
                    case Q:
                        up = true;
                        break;
                    case E:
                        down = true;
                        break;
                    case W:
                        fwd = true;
                        break;
                    case S:
                        back = true;
                        break;
                    case A:
                        strafeL = true;
                        break;
                    case D:
                        strafeR = true;
                        break;
                    case SHIFT:
                        shift = true;
                        moveSpeed = 20;
                        wheelSpeed=.3f;
                        break;
                    case NUMPAD1:
                    	num1=true;
                    	break;
                    case NUMPAD6:
                    	num6=true;
                    	break;
				default:
					break;
                }
            } else if (ke.getEventType() == KeyEvent.KEY_RELEASED) {
                switch (ke.getCode()) {
                    case Q:
                        up = false;
                        break;
                    case E:
                        down = false;
                        break;
                    case W:
                        fwd = false;
                        break;
                    case S:
                        back = false;
                        break;
                    case A:
                        strafeL = false;
                        break;
                    case D:
                        strafeR = false;
                        break;
                    case SHIFT:
                        moveSpeed = 10;
                        wheelSpeed=1;
                        shift = false;
                        break;
                    case NUMPAD1:
                    	num1=false;
                    	break;
                    case NUMPAD6:
                    	num6=false;
                    	break;
				default:
					break;
                }
            }
            ke.consume();
        });
		scene.addEventHandler(MouseEvent.ANY, me -> {
			// the use of "cond" and "cond2" act as mouse.clicked for the numpad controls.
			// Without these booleans, the mousePos's dont work.
			// there's probably a more elegant solution but that's for another time

			if (num1 && me.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
				if (cond) {
					mousePosX = (float) me.getSceneX();
					mousePosY = (float) me.getSceneY();
					mouseOldX = (float) me.getSceneX();
					mouseOldY = (float) me.getSceneY();
					cond = false;
				}
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX = (float) me.getSceneX();
				mousePosY = (float) me.getSceneY();

				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);

				//moveController();
			} else if (me.getEventType().equals(MouseEvent.MOUSE_MOVED))
				cond = true;
			
			 if(num6 && me.getEventType().equals(MouseEvent.MOUSE_MOVED)) {
				if (cond2) {
					mousePosX = (float) me.getSceneX();
					mousePosY = (float)me.getSceneY();
					mouseOldX = (float)me.getSceneX();
					mouseOldY = (float)me.getSceneY();
					cond2 = false;
				}
				mouseOldX = mousePosX;
				mouseOldY = mousePosY;
				mousePosX =(float) me.getSceneX();
				mousePosY =(float)me.getSceneY();

				mouseDeltaX = (mousePosX - mouseOldX);
				mouseDeltaY = (mousePosY - mouseOldY);
				
				grab();
			} else if (me.getEventType().equals(MouseEvent.MOUSE_MOVED))
				cond2 = true;
        	
        	if (me.getEventType().equals(MouseEvent.MOUSE_PRESSED)) {
                mousePosX = (float)me.getSceneX();
                mousePosY = (float)me.getSceneY();
                mouseOldX = (float)me.getSceneX();
                mouseOldY = (float)me.getSceneY();
                System.out.print("[");                
                
                recording=true;
               
            } else if (me.getEventType().equals(MouseEvent.MOUSE_DRAGGED)) {
                mouseOldX = mousePosX;
                mouseOldY = mousePosY;
                mousePosX = (float)me.getSceneX();
                mousePosY = (float)me.getSceneY();
                mouseDeltaX = (mousePosX - mouseOldX);
                mouseDeltaY = (mousePosY - mouseOldY);

                mouseSpeed = 1.0f;
                mouseModifier = 0.1f;
                //if(num1)moveController();
                if (me.isPrimaryButtonDown()) {
                    if (me.isAltDown()) {
                        //moveController();  //moves the controller sphere.                      
                    }                    
                    if (me.isShiftDown()) {
                       //does something
                    }                    
                }
                if (me.isSecondaryButtonDown()) {//&&me.isAltDown()) { //IDEA: grab a lot of objects, saving their current velocities at the time of the grab,
                	stop=true;												//when the objects are released, re apply their previous velocities...basically like a time stop mechanic
                	//influence(); //influences objects inside sphere of influence.
                } else if (me.isControlDown()&&me.isPrimaryButtonDown()) {
                    //does something to test
                	
                }
            }
        });

        scene.addEventHandler(ScrollEvent.ANY, se -> {

            if (se.getEventType().equals(ScrollEvent.SCROLL_STARTED)) {
            	
            } else if (se.getEventType().equals(ScrollEvent.SCROLL)) {
            	wheelSpeed=1;
            	if(se.isControlDown())wheelSpeed=.3f;
            	sw=(float)se.getDeltaY();
            	//controller.setRadius((int) (controller.getRadius()+sw*wheelSpeed/10));
            	
            } else if (se.getEventType().equals(ScrollEvent.SCROLL_FINISHED)) {

            }
        });
    }

    
    /*==========================================================================
     Control methods
     */
    
    //lightControls() uses data from the 3D motion tracking system to control the 2 controllers in the simulation, given that
    //motion controls are enabled for the simulation.
    private int counting=0;
    public void lightControls() {
    		float locx=(float) uni.getLocX();
    		float locy=(float) uni.getLocY();
    		float locz=(float) uni.getLocZ();
    		float wid =tracker.getDimensions()[0];
    		float high=tracker.getDimensions()[1];
    		//System.out.println(tracker.getDimensions()[0]+"   "+tracker.getDimensions()[1]); //prints dimensions of webcam data

    		float ratio=locx/(wid/2);
    		float ratio2=locy/(high/2);    		
    		TrackingPoint[] tracks = new TrackingPoint[trackers.size()];
    		
    		for ( int i=0; i<trackers.size(); i++) {    		
    			TrackingPoint t = trackers.get(i);
    			
    			float x1=(float) (locx-ratio*pos[i][0]),y1=(float) (locy+ratio2*pos[i][1]),z1=locz+ratio2*pos[i][2],x2=(float) 
    					t.getPos().getX(),y2=(float) t.getPos().getY(),z2=(float) t.getPos().getZ();
    			float smooth=5;
    			float dx=x2-x1, dy=y2-y1,dz=z2-z1;    			
    			t.setPos(x2 - dx / smooth, y2 - dy / smooth, z2 - dz / smooth);
    			tracks[i] = t;	    			
    		}
		start.game.s.trackersMoved(tracks);
	}
    private int rots=0;
    private int [] rot=new int[3];
    public void setAngle(int angle) {
    	rot[0]=angle+90;
    	
    }
    public void setAngle2(int angle) {
    	rot[1]=-angle+90;
    	
    }
    private void updateRot() {
    	//trackers.get(0).rotateBox(rot[0], 0, rot[1]);
    }

    //"grabs" all particles inside a controller's radius
	private void grab() { 
		// reads all objects affected by the controller
		ArrayList<Freebody> affectedObs = controller1.getAffected(list);

		int affected = affectedObs.size();
		float dX,dY,dZ,x,y,z;
		Freebody c;
		float mult=1;
		for (int i = 0; i < affected; i++) {
			c = affectedObs.get(i);
			
			dX = list.get(0).velocity.getX();
			dY = list.get(0).velocity.getY();
			dZ = list.get(0).velocity.getZ();
			//System.out.println(x);
			c.setVel(dX,dY,dZ);
		}
		Freebody[] obs = new Freebody[affected];
		for (int i = 0; i < affected; i++) {
			obs[i] = affectedObs.get(i);
		}
		start.game.s.interactionOccured(obs);
	}
	
	int counter=0;
    
    
    
    //implement this
    private void forcePush(TrackingPoint t) {
    	//System.out.println(t.calcDisplacement(3));
    	
    }
    private float[][][] getMotionData(TrackingPoint t,String type){ //returns data specific to what gesture is being tested for (ex: push is 12 arrays of data)
    	if(type=="PUSH") {
    		float [][][]data= {t.getXFramesOfData(12)}; //gets 12 raw frames of position data from the motion tracker
    		data=setToDisplacement(data); //converts the 12 frames of position data to displacement data instead(normalized at 70)
    		return data;
    	}
    	return new float[][][] {{{}}};
    }
    private float max=140;
	
	private float[]normalizeArray(float[] data){
    	return new float[]{data[0]/max,data[1]/max,data[2]/max};
    }
   
	private float offset=70;	
	private float[][][] setToDisplacement(float[][][] data) {		
		float[][][] fixedData = new float[data.length][12][3];      
		for(int i = 0; i < data.length; i++) {
           
			float[] newOrigin = {70, 70, 70};
            fixedData[i][0] = normalizeArray(newOrigin);
            
            for(int j = 1; j < 12; j++) { // data[i].length should always be 12
                float[] previous = data[i][j-1]; // the first coordinates points in the array of coordinates
                float originX = previous[0];
                float originY = previous[1];
                float originZ = previous[2];
                
                float[] current = data[i][j];
                float currentX = current[0];                
                float currentY = current[1];
                float currentZ = current[2];
                
                float xDisp = currentX - originX+offset;
                float yDisp = currentY - originY+offset;
                float zDisp = currentZ - originZ+offset;  
                
                float[] newPoint = {xDisp, yDisp, zDisp};
                fixedData[i][j] = normalizeArray(newPoint);
            }
        }
        return fixedData;
    }
    
    private void influence() { //perform some action on all particles inside the controller.
    	//reads all objects affected by the controller    	
		ArrayList<Freebody> affectedObs = controller1.getAffected(list);
		
		int affected = affectedObs.size();
		float vX, vY;
		Freebody c;
		for (int i = 0; i < affected; i++) {
			c = affectedObs.get(i);
			vX = c.velocity.getX();
			vY = c.velocity.getY();
			c.setVel(vX + mouseDeltaX, vY - mouseDeltaY, 0);
		}
		Freebody[] obs = new Freebody[affected];
		for (int i = 0; i < affected; i++) {
			obs[i] = affectedObs.get(i);
		}
		start.game.s.interactionOccured(obs);		
    }
    
    private void getAffected() {
    	 start.game.s.interacting(controller1.getAffected(list)); //tells physics sim to do something with the information that objects are being interacted with.    	
    }

    /*==========================================================================
     Callbacks    
     | R | Up| F |  | P|
     U |mxx|mxy|mxz|  |tx|
     V |myx|myy|myz|  |ty|
     N |mzx|mzy|mzz|  |tz|
    
     */
    //Forward / look direction    
    private final Callback<Transform, Point3D> F = (a) -> {
        return new Point3D(a.getMzx(), a.getMzy(), a.getMzz());
    };
    private final Callback<Transform, Point3D> N = (a) -> {
        return new Point3D(a.getMxz(), a.getMyz(), a.getMzz());
    };
    // up direction
    private final Callback<Transform, Point3D> UP = (a) -> {
        return new Point3D(a.getMyx(), a.getMyy(), a.getMyz());
    };
    private final Callback<Transform, Point3D> V = (a) -> {
        return new Point3D(a.getMxy(), a.getMyy(), a.getMzy());
    };
    // right direction
    private final Callback<Transform, Point3D> R = (a) -> {
        return new Point3D(a.getMxx(), a.getMxy(), a.getMxz());
    };
    private final Callback<Transform, Point3D> U = (a) -> {
        return new Point3D(a.getMxx(), a.getMyx(), a.getMzx());
    };
    //position
    private final Callback<Transform, Point3D> P = (a) -> {
        return new Point3D(a.getTx(), a.getTy(), a.getTz());
    };

    private Point3D getF() {
        return F.call(getLocalToSceneTransform());
    }

    public Point3D getLookDirection() {
        return getF();
    }

    private Point3D getN() {
        return N.call(getLocalToSceneTransform());
    }

    public Point3D getLookNormal() {
        return getN();
    }

    private Point3D getR() {
        return R.call(getLocalToSceneTransform());
    }

    private Point3D getU() {
        return U.call(getLocalToSceneTransform());
    }

    private Point3D getUp() {
        return UP.call(getLocalToSceneTransform());
    }

    private Point3D getV() {
        return V.call(getLocalToSceneTransform());
    }

    public final Point3D getPosition() {
        return P.call(getLocalToSceneTransform());
    }

}