import javafx.application.Application;
import javafx.geometry.Point3D;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
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

	public Long lastNanoTime; //Long value for updating the simulation time
	public SimpleFPSCamera camera; //camera in the simulation	
	public  ArrayList<Freebody>obs;	//particles in the simulation
	public  ArrayList<TrackingPoint>trackers; //motion tracking points
	public assets assets; //all walls and lights in the simulation 	
	public Group root;
	public playerControls controls;
	public FileWriter myWriter;
	public int frame = 0;
	private fieldDrawer visualize;
	private float timescale=start.TIMESCALE;
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
		boolean visualizing=false;
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
		myWriter= new FileWriter("C:\\Users\\suprm\\Desktop\\data.txt");
		// Add the Camera to the Scene
		scene.setCamera(camera.getCamera());
		
		
		camera.getCamera().setFieldOfView(camera.getCamera().getFieldOfView()+10);
		// Add the Scene to the Stage
		stage.setScene(scene);
		scene.setFill(Color.BLACK);
		camera.loadControlsForScene(scene);
		controls.loadControlsForScene(scene);
		
		// Set the Title of the Stage
		stage.setTitle("3D physics simulator!");
		// Displays the Stage, turn on/off fullscreen
		stage.show();
		
		
		int limit=1000;
		stage.setFullScreen(true);
		stage.setMaximized(true);
		lastNanoTime = new Long(System.nanoTime());		
		myWriter.write(obs.size()+" ");
		myWriter.write(start.camPos[0]+" "+start.camPos[1]+" "+start.camPos[2]+"\n");
		System.out.println(start.camPos[0]+" "+start.camPos[1]+" "+start.camPos[2]+"\n");
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// calculate time since last update.
				double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;				
				if(elapsedTime>.000150) //normally .0150
				{					
					root.setCache(true);
					root.setCacheHint(CacheHint.SPEED);
					lastNanoTime = currentNanoTime;
					//60 fps is .0166666667
					start.game.s.update(.045f*timescale);
					frame++;
					for (int i = 0; i < obs.size(); i++) {
						Freebody o = obs.get(i); // gets each particle from the ArrayList
						o.draw(); 				// draws the particle
						try {
							myWriter.write((int)o.getX()+","+(int)o.getY()+","+(int)o.getZ()+" ");
						} catch (IOException e) {}
					}
					for (int i = 0; i < trackers.size(); i++) {
						TrackingPoint t = trackers.get(i); // gets each tracker from the ArrayList
						t.draw(); 				   		 // draws the tracker
					}
					try {
						myWriter.write("\n");
					} catch (IOException e) {}
					
					if(frame>limit) {
						try {
							myWriter.close();
						} catch (IOException e) {}
						//System.out.println("DONE!");
						//this.stop();						
					}else
						System.out.println("Progess: "+frame*100/(float)limit+"%");
					//visualize.setAngles();
					//visualize.refresh();
				}				
			}
		}.start();
		
		// Adding scene to the stage
		stage.setScene(scene);
		// Displaying the contents of the stage
		stage.show();		
	}	
}
