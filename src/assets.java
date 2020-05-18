import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.PointLight;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;


public class assets {
	public SimpleFPSCamera cam;
	public ArrayList<ImageView>  walls;			  //Walls if there are borders, these are made from images put into the src folder
	public ArrayList<PointLight> wallLights;	 //lights if there are borders, these are typically to be placed on the walls of the scene
	public ArrayList<PointLight> locationLights; //lights if there are no universe borders, these are centered around the "action" of the scene
	private boolean locDef;
	public assets() {
		locDef=start.uni.locationIsDefined();
	}

	public Group Assets() {
		
		//Sets up the walls and lights of the scene.		
		//if the universe is border-less, don't add walls, then place lights around the "location" of the scene, typically around the center mass.		
		//locDef is true if the universe is border-less.		
		//makeCamera is also called in these methods, method call in addLights and locLights makes code neater, and ensures camera is looking at
		//the middle of the scene regardless of if the uni has borders or not
		
		if(locDef) {
			addLocationLights();
		}else {
			addWalls();
			addWallLights();
		}
		
		
		//directly returns all the elements of the scene without making a new Group Object.
		return addStuff(new Group());
	}
	
	private Group addStuff(Group G) {	//adds all elements of the scene to the group.
		Group g=G;
		
		//checks if locationDefinition is on, otherwise nullPointerException is thrown.
		if(!locDef) {
			for(ImageView i:walls) {
				g.getChildren().add(i); //adding each wall to the group
			}
		
			for(PointLight i:wallLights) {
				g.getChildren().add(i); //adding each wallLight to the group
			}
		}
		else {
			for(PointLight i:locationLights) {
				g.getChildren().add(i); //adding each locationLight to the group
			}
		}
		return g;
	}
	private void makeCamera(double x,double y,double z) { //makes camera for simulation. Method called from make lights. Method called once per sim.
		cam=new SimpleFPSCamera(x,y,-z);//x,y,z are the starting coords for the camera
		
		cam.getCamera().setNearClip(0.1);
		cam.getCamera().setFarClip(12500.0);
		cam.getCamera().setFieldOfView(70);
	}
	
	private void addWalls() { //adds the walls of the scene if borders are on.
		
		//wall1 is back,wall2 is left, wall3 is right, wall4 is bottom, wall5 is top, wall 6 is close
		walls=new ArrayList<ImageView>();
		if(start.uni.isBounds()) {
			double w=start.uni.getWidth();
			double h=start.uni.getHeight();
			double d=start.uni.getLength();
			//WALL 1
			InputStream inputstream = null;
			try {
				inputstream = new FileInputStream("C:\\Users\\suprm\\eclipse-workspace\\Java3DTestomg\\src\\wall1.jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Image image = new Image(inputstream);
			ImageView wall1 = new ImageView(image);
			wall1.setImage(image);
			wall1.setPreserveRatio(false);
			wall1.setSmooth(true);
			wall1.setCache(true);
			wall1.setTranslateZ(d);
			wall1.setFitHeight(h);
			wall1.setFitWidth(w);
			Rotate r = new Rotate();
			r.setPivotX(0);
			r.setPivotY(0);
			r.setPivotZ(0);
			r.setAxis(Rotate.Y_AXIS);
			r.setAngle(0);
			wall1.getTransforms().add(r);
			
			//WALL 2
			try {
				inputstream = new FileInputStream("C:\\Users\\suprm\\eclipse-workspace\\Java3DTestomg\\src\\wall2.jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image = new Image(inputstream);
			ImageView wall2 = new ImageView(image);
			wall2.setImage(image);
			wall2.setPreserveRatio(false);
			wall2.setSmooth(true);
			wall2.setCache(true);
			wall2.setTranslateZ(d);
			wall2.setFitHeight(h);
			wall2.setFitWidth(d);
			r = new Rotate();
			r.setPivotX(0);
			r.setPivotY(0);
			r.setPivotZ(0);
			r.setAxis(Rotate.Y_AXIS);
			r.setAngle(90);
			wall2.getTransforms().add(r);
			
			//WALL 3
			try {
				inputstream = new FileInputStream("C:\\Users\\suprm\\eclipse-workspace\\Java3DTestomg\\src\\wall3.jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image = new Image(inputstream);
			ImageView wall3 = new ImageView(image);
			wall3.setImage(image);
			wall3.setPreserveRatio(false);
			wall3.setSmooth(true);
			wall3.setCache(true);
			wall3.setTranslateZ(d);
			wall3.setTranslateX(w);
			wall3.setFitHeight(h);
			wall3.setFitWidth(d);
			r = new Rotate();
			r.setPivotX(0);
			r.setPivotY(0);
			r.setPivotZ(0);
			r.setAxis(Rotate.Y_AXIS);
			r.setAngle(90);
			wall3.getTransforms().add(r);
			
			//WALL 4
			try {
				inputstream = new FileInputStream("C:\\Users\\suprm\\eclipse-workspace\\Java3DTestomg\\src\\wall3.jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image = new Image(inputstream);
			ImageView wall4 = new ImageView(image);
			wall4.setImage(image);
			wall4.setPreserveRatio(false);
			wall4.setSmooth(true);
			wall4.setCache(true);
			wall4.setTranslateZ(0);
			wall4.setTranslateX(0);
			wall4.setFitHeight(d);
			wall4.setFitWidth(w);
			r = new Rotate();
			r.setPivotX(0);
			r.setPivotY(0);
			r.setPivotZ(0);
			r.setAxis(Rotate.X_AXIS);
			r.setAngle(90);
			wall4.getTransforms().add(r);

			//WALL 5
			try {
				inputstream = new FileInputStream("C:\\Users\\suprm\\eclipse-workspace\\Java3DTestomg\\src\\wall3.jpg");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			image = new Image(inputstream);
			ImageView wall5 = new ImageView(image);
			wall5.setImage(image);
			wall5.setPreserveRatio(false);
			wall5.setSmooth(true);
			wall5.setCache(true);
			wall5.setTranslateZ(0);
			wall5.setTranslateX(0);
			wall5.setTranslateY(d);
			System.out.println(wall5.getTranslateY());
			wall5.setFitHeight(d);
			wall5.setFitWidth(w);
			
			r = new Rotate();
			r.setPivotX(0);
			r.setPivotY(0);
			r.setPivotZ(0);
			r.setAxis(Rotate.X_AXIS);
			r.setAngle(90);
			wall5.getTransforms().add(r);
			
			
			walls.add(wall1);
			walls.add(wall2);
			walls.add(wall3);
			walls.add(wall4);
			walls.add(wall5);
			
		}
		
	}
	
	private void addWallLights() { //adds the walls Lights of the scene if borders are on.
		wallLights=new ArrayList<PointLight>();
		double w=start.uni.getWidth();
		double h=start.uni.getHeight();
		double d=start.uni.getLength();
		
		int away=1500;	              //how far away(z direction) the camera starts from the action
		makeCamera(w/2,h/2,d/2-away); //makes the camera looking at the scene
		
		PointLight light1 = new PointLight(); //middle of back wall
		light1.setColor(Color.WHITE);
		light1.setTranslateX(w/2);
		light1.setTranslateY(h/2);
		light1.setTranslateZ(0);
		
		PointLight light2 = new PointLight(); //middle scene
		light2.setColor(Color.WHITE);
		light2.setTranslateX(w/2);
		light2.setTranslateY(h/2);
		light2.setTranslateZ(d/2);
		
		PointLight light3 = new PointLight(); //middle of right wall
		light3.setColor(Color.WHITE);
		light3.setTranslateX(w);
		light3.setTranslateY(h/2);
		light3.setTranslateZ(d/2);
		
		wallLights.add(light1);
		wallLights.add(light2);
		wallLights.add(light3);
	}

	private void addLocationLights() { //adds the Lights of the scene if borders are off.
		locationLights=new ArrayList<PointLight>();
		double w=start.uni.getLocX();
		double h=start.uni.getLocY();
		double d=start.uni.getLocZ();
		
		int away=1500;          //how far away(z direction) the camera starts from the action
		makeCamera(w,h,d-away); //makes the camera looking at the scene
		
		int dis=10000; //distance offset from middle light
		
		PointLight light1 = new PointLight(); //exact middle of the scene, typically inside the main mass
		light1.setColor(Color.WHITE);
		light1.setTranslateX(w);
		light1.setTranslateY(h);
		light1.setTranslateZ(d);
		
		//Y offset from middle(above)
		PointLight light2 = new PointLight();
		light2.setColor(Color.WHITE);
		light2.setTranslateX(w);
		light2.setTranslateY(h-dis);
		light2.setTranslateZ(d);		
				
		//Z offset from middle(in front)
		PointLight light3 = new PointLight(); 
		light3.setColor(Color.WHITE);
		light3.setTranslateX(w);
		light3.setTranslateY(h);
		light3.setTranslateZ(d-dis);
		
		//X offset from middle(to left)
		PointLight light4 = new PointLight(); 
		light4.setColor(Color.WHITE);
		light4.setTranslateX(w-dis);
		light4.setTranslateY(h);
		light4.setTranslateZ(d);
		
		//Y offset from middle(below)
		PointLight light5 = new PointLight();
		light5.setColor(Color.WHITE);
		light5.setTranslateX(w);
		light5.setTranslateY(h+dis);
		light5.setTranslateZ(d);
		
		//locationLights.add(light1);
		locationLights.add(light2);
		locationLights.add(light3);
		locationLights.add(light4);
		locationLights.add(light5);
	}
	
	
}

