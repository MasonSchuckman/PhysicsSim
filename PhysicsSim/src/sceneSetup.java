import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import javafx.animation.Animation;
import javafx.animation.AnimationTimer;
import javafx.animation.RotateTransition;

public class sceneSetup extends Application {
	private final boolean debugging=false;
	
	public Long lastNanoTime; //Long value for updating the simulation time
	public Long startTime; //Long value for calculating time taken to finish sim
	public SimpleFPSCamera camera; //camera in the simulation	
	public ArrayList<Freebody>obs;	//particles in the simulation
	public ArrayList<TrackingPoint>trackers; //motion tracking points
	public assets assets; //all walls and lights in the simulation 	
	public Group root;
	public playerControls controls;
	public FileWriter myWriter;
	public int frame = 0;

	private final boolean recordingData=false;
	private final boolean visualizing;
	
	private Text tet;
	private int cx,cy,cz,cx2,cy2,cz2; // camera current and past positions (for debugging only)
	
	private fieldDrawer visualize;
	private float timescale=start.TIMESCALE;
	public static Octree rootNode=null;
	public void startLoop(String [] args) {
		Application.launch();		
	}
	
	public sceneSetup() {
		// creates all assets required for program to run(lights, camera..not action
		// though) Put additional shapes in here too, including images and boxes, ect..
		assets = new assets(false);

		root = assets.Assets();
		camera = assets.cam;// Gets the Camera for the scene
		controls = start.Pcontrols;
		obs = start.list;
		trackers = start.trackers;
		customUtils.addStuff(root, obs, trackers);
		//if(root.getChildren().addAll(visualize.visualizeField(start.uni, obs,15000)))System.out.println("DONE");

		//true if the magnetic/gravity field visualizer is on
		visualizing=false;
		if(visualizing) {
			int numIndicators=6000;
			visualize=new fieldDrawer(start.uni,obs,numIndicators,root); 
			visualize.useGPU();
			visualize.start();
		}
	}

	@Override
	public void start(Stage stage) throws Exception  {		
		// Create a Scene with depth buffer enabled
		Scene scene = new Scene(root, 600, 600, true);

		// Add the Camera to the Scene
		scene.setCamera(camera.getCamera());


		camera.getCamera().setFieldOfView(camera.getCamera().getFieldOfView()+10);
		// Add the Scene to the Stage
		stage.setScene(scene);
		scene.setFill(Color.BLACK);
		camera.loadControlsForScene(scene);
		controls.loadControlsForScene(scene);
		
		if(debugging)
			setupTextViewer(root);
		
		// Set the Title of the Stage
		stage.setTitle("3D physics simulator!");
		// Displays the Stage, turn on/off fullscreen
		stage.show();



		stage.setFullScreen(false);
		stage.setMaximized(false);

		int limit=0;
		if(recordingData) {
			limit=200;
			myWriter= new FileWriter("C:\\Users\\suprm\\Desktop\\data.txt");
			myWriter.write(obs.size()+" ");
			myWriter.write(start.camPos[0]+" "+start.camPos[1]+" "+start.camPos[2]+"\n");
		}

		lastNanoTime = new Long(System.nanoTime());	
		startTime=new Long(System.nanoTime());	
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// calculate time since last update.
				double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;				
				if(elapsedTime>.000150) //normally .0150
				{					
					root.setCache(true);
					root.setCacheHint(CacheHint.SPEED);

					lastNanoTime = currentNanoTime;

					if(start.uni.checking) { //if ball collisions are turned on, setup otrees.
						
						if(rootNode!=null)
							removeOctreesFromScene(rootNode,root);
						Long ttt = new Long(System.currentTimeMillis());	
						rootNode= new Octree(null,new int[] {-50000,-50000,-50000},100000,(ArrayList<Freebody>)obs.clone());
						addOctreesToScene(rootNode,root);
						if(debugging)
							System.out.println("Tree construction time: "+(System.currentTimeMillis()-ttt));
					}

					start.game.s.update(.045f*timescale);

					frame++;

					for (int i = 0; i < obs.size(); i++) {
						Freebody o = obs.get(i); // gets each particle from the ArrayList
						o.draw();// draws the particle
						if(recordingData) {
							try {
								myWriter.write((int)o.getX()+","+(int)o.getY()+","+(int)o.getZ()+" ");
							} catch (IOException e) {}
						}
					}
					for (int i = 0; i < trackers.size(); i++) {
						TrackingPoint t = trackers.get(i); // gets each tracker from the ArrayList
						t.draw(); 				   		   // draws the tracker
					}
					
					if(recordingData) {
						try {
							myWriter.write("\n");
						} catch (IOException e) {}


						if(frame>limit) {
							try {
								myWriter.close();
							} catch (IOException e) {}
							System.out.println("DONE!");							
						}else
							System.out.println("Progess: "+frame*100/(float)limit+"%");
					}
					if(visualizing) {
						visualize.setAngles();
						visualize.refresh();
					}
				}				
			}
		}.start();

		// Adding scene to the stage
		stage.setScene(scene);
		// Displaying the contents of the stage
		stage.show();		
	}	
	public void setupTextViewer(Group root) {
		tet = new Text("This is a text sample");
		root.getChildren().add(tet);

		tet.setFont(Font.font ("Verdana", 200));
		tet.setFill(Color.RED);
		tet.setTranslateX(1000);
		tet.setTranslateY(1000);
		tet.setTranslateZ(1000);

		tet.setRotationAxis(Rotate.Y_AXIS);
		tet.setRotate(180);
	}
	/*all this can be used to move the text box and objects along with the camera.
	 *  cx=(int)camera.getPosition().getX();
		cy=(int)camera.getPosition().getY();
		cz=(int)camera.getPosition().getZ();

		tet.setTranslateX((int)(tet.getTranslateX()+cx-cx2));
		tet.setTranslateY((int)(tet.getTranslateY()+cy-cy2));
		tet.setTranslateZ((int)(tet.getTranslateZ()+cz-cz2));


	  	o.setX(o.getX()-cx2+cx);
		o.setY(o.getY()-cy2+cy);
		o.setZ(o.getZ()-cz2+cz);

	  	cx2=cx;
		cy2=cy;
		cz2=cz;
	 */
	public void addOctreesToScene(Octree node, Group root) {		
		if(node.vis!=null) {
			root.getChildren().add(node.vis);
			for(Octree n:node.children) {
				if(n!=null)addOctreesToScene(n,root);
			}
		}

	}
	public void removeOctreesFromScene(Octree node, Group root) {		
		if(node.vis!=null) {
			root.getChildren().remove(node.vis);
			for(Octree n:node.children) {
				if(n!=null)removeOctreesFromScene(n,root);
			}
		}	
	}

}
