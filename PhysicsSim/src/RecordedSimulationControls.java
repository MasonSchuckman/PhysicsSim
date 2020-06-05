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


public class RecordedSimulationControls extends Parent {
	
	
	
    public RecordedSimulationControls() throws IOException {
       
    	
	    try {
			Thread.sleep(1000);
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
                												//when the objects are released, re apply their previous velocities...basically like a time stop mechanic
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