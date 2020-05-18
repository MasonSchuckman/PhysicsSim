import javafx.application.Application;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;

public class sceneSetup extends Application {

	public Long lastNanoTime; //Long value for updating the simulation time
	public SimpleFPSCamera camera; //camera in the simulation	
	public  ArrayList<Freebody>obs;	//particles in the simulation
	public  ArrayList<TrackingPoint>trackers; //motion tracking points
	public assets assets; //all walls and lights in the simulation 	
	public Group root;
	public playerControls controls;
	private fieldDrawer visualize;
	public void startLoop(String [] args) {
		Application.launch();		
	}
	public sceneSetup() {
		// creates all assets required for program to run(lights, camera..not action
		// though) Put additional shapes in here too, including images and boxes, ect..
		assets = new assets();

		root = assets.Assets();
		camera = assets.cam;// Gets the Camera for the scene
		controls = start.Pcontrols;
		obs = start.list;
		trackers = start.trackers;
		customUtils.addStuff(root, obs, trackers);
		//if(root.getChildren().addAll(visualize.visualizeField(start.uni, obs,15000)))System.out.println("DONE");
		
		//visualize=new fieldDrawer(start.uni,obs,6000,root);
		//visualize.useGPU();
		//visualize.start();
	}
	
	
	@Override
	public void start(Stage stage) throws Exception  {		
		// Create a Scene with depth buffer enabled
		Scene scene = new Scene(root, 600, 600, true);
		
		// Add the Camera to the Scene
		scene.setCamera(camera.getCamera());
		
		// Add the Scene to the Stage
		stage.setScene(scene);
		scene.setFill(Color.BLACK);
		camera.loadControlsForScene(scene);
		controls.loadControlsForScene(scene);
		
		// Set the Title of the Stage
		stage.setTitle("3D physics simulator!");
		// Displays the Stage, turn on/off fullscreen
		stage.show();
		
		stage.setFullScreen(true);
		//stage.setMaximized(true);
		lastNanoTime = new Long(System.nanoTime());		
		//visualize.setAngles();
		//visualize.st
		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// calculate time since last update.
				double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;				
				if(elapsedTime>.0150) 
				{					
					root.setCache(true);
					root.setCacheHint(CacheHint.SPEED);
					lastNanoTime = currentNanoTime;
					//60 fps is .0166666667
					start.game.s.update(.045f);
					
					for (int i = 0; i < obs.size(); i++) {
						Freebody o = obs.get(i); // gets each particle from the ArrayList
						o.draw(); 				// draws the particle
					}
					for (int i = 0; i < trackers.size(); i++) {
						TrackingPoint t = trackers.get(i); // gets each tracker from the ArrayList
						t.draw(); 				   		 // draws the tracker
					}
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
