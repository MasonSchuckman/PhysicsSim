import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;

import javafx.scene.PerspectiveCamera;
import javafx.scene.transform.*;

public class camControls {
	public double mouseOldX, mouseOldY, mousePosY, mousePosX;
	private boolean w=false,a=false,s=false,d=false,alt=false,rot=false,up=false,down=false,left=false,right=false,q=false,e=false; //these are for keyboard control of the camera
	private PerspectiveCamera c;
	private double totalRot;
	public camControls(PerspectiveCamera cam)
	{
		c=cam;
	}

	public void mControl(MouseEvent me) {

		mouseOldX = mousePosX;
		mouseOldY = mousePosY;
		mousePosX = me.getX();
		mousePosY = me.getY();

		double mouseDeltaX, mouseDeltaY;
		mouseDeltaX = mousePosX - mouseOldX;
		mouseDeltaY = mousePosY - mouseOldY;
		double rotAngle,ra;
		if (me.isAltDown() && me.isShiftDown() && me.isPrimaryButtonDown()) {
			c.setRotationAxis(Rotate.Z_AXIS);
			rotAngle = (c.getRotate() - mouseDeltaX / 20);
			//System.out.println(c.getRotate());
			c.setRotate(rotAngle);

		} else if (me.isAltDown() && me.isPrimaryButtonDown()) {

			c.setRotationAxis(Rotate.X_AXIS);
			ra = (c.getRotate() + mouseDeltaY / 100);
			c.setRotate(ra);
			rotAngle=0;
			//c.setRotationAxis(Rotate.Y_AXIS);			
			//rotAngle = (c.getRotate() - mouseDeltaX / 100);
			//c.setRotate(rotAngle);
		}

        else if (me.isAltDown() && me.isSecondaryButtonDown()) {
            double fov = c.getFieldOfView();            
            c.setFieldOfView(fov+(mouseDeltaY/100));
        }
//        else if (me.isAltDown() && me.isMiddleButtonDown()) {
//            c.t.setX(c.t.getX() + mouseDeltaX);
//            c.t.setY(c.t.getY() + mouseDeltaY);
//        }
	}
	
	public void camControl() {
    	double dx = 0, dy = 0,dz=0;
    	double rx=0,rz=0,ry=0, fov = c.getFieldOfView();            
        
    	double theta=totalRot;
    	
        theta=Math.toRadians(theta);
        //System.out.println(theta);
    	double swivelSpeed=.006;
        if (w) {
        	dz += swivelSpeed*2*Math.cos(theta);
        	dx += swivelSpeed*2*Math.sin(theta);
        }
        if (s) {
        	dz -= swivelSpeed*2*Math.cos(theta);
        	dx -= swivelSpeed*2*Math.sin(theta);
        }
        if (d) {
        	dx += swivelSpeed*2*Math.cos(theta);
        	dz -= swivelSpeed*2*Math.sin(theta);
        }
        if (a) {
        	dx -= swivelSpeed*2*Math.cos(theta);
        	dz += swivelSpeed*2*Math.sin(theta);
        }
        
        if(alt&&up) fov-=swivelSpeed*.5;
        if(alt&&down) fov+=swivelSpeed*.5;
        if(up&!alt)   ry+=swivelSpeed;
        if(down&!alt) ry-=swivelSpeed;
        if(left) rx-=swivelSpeed;
        if(right)rx+=swivelSpeed;        
        
        if (q)  dy -= swivelSpeed*5;
        if (e)  dy += swivelSpeed*5;
		
        totalRot+=rx;
        
        double currentX=c.getTranslateX();
        double currentY=c.getTranslateY();
        double currentZ=c.getTranslateZ();
        
        c.setTranslateX(dx+currentX);
        c.setTranslateY(dy+currentY);
        c.setTranslateZ(dz+currentZ);
        
        c.setFieldOfView(fov);
        
        Rotate r = new Rotate();
		r.setPivotX(0);
		r.setPivotY(0);
		r.setPivotZ(0);
		r.setAxis(Rotate.Y_AXIS);
		r.setAngle(rx);
		c.getTransforms().add(r);
		r.setAxis(Rotate.X_AXIS);
		r.setAngle(ry);
		c.getTransforms().add(r);		
    }
	
	public void keyPressed(KeyEvent event){
		switch (event.getCode()) {
        case UP:    w = true; break;
        case DOWN:  s = true; break;
        case LEFT:  a  = true; break;
        case RIGHT: d  = true; break;
        case SHIFT: alt = true; break;
        case W: up=true; break;
        case Q: q=true; break;
        case E: e=true; break;
        case S: down=true; break; 
        case A: left=true; break; 
        case D: right=true; break; 
        case ALT: rot=true;break;
	default:
		break;
		}
	}
	public void keyReleased(KeyEvent event){
		switch (event.getCode()) {
        case UP:    w = false; break;
        case DOWN:  s = false; break;
        case LEFT:  a  = false; break;
        case RIGHT: d  = false; break;
        case SHIFT: alt = false; break;
        case W: up=false; break;
        case S: down=false; break; 
        case A: left=false; break; 
        case Q: q=false; break;
        case E: e=false; break;
        case D: right=false; break;
        case ALT: rot=false;break;
	default:
		break;
    }
	}
}
