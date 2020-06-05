import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.CacheHint;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Sphere;
import javafx.stage.Stage;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import javafx.animation.AnimationTimer;

public class RecordedSimulationPlayer extends Application {

	public Long lastNanoTime; //Long value for updating the simulation time
	public SimpleFPSCamera camera; //camera in the simulation	
	public  ArrayList<ModelSphere>obs;	//particles in the simulation
	
	public assets assets; //all walls and lights in the simulation 	
	public Group root;
	public RecordedSimulationControls controls;
	private int [][][]poses;
	public int frame = 0;
	
	public void startLoop(String [] args) {
		Application.launch();		
	}
	public RecordedSimulationPlayer() {
		// creates all assets required for program to run(lights, camera..not action
		// though) Put additional shapes in here too, including images and boxes, ect..
		assets = new assets(true);

		root = assets.Assets();
		camera = assets.cam;// Gets the Camera for the scene
		controls = SimulationReader.Pcontrols;
		obs = SimulationReader.models;
		customUtils.addStuff(root, obs);
		poses=SimulationReader.poses;
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

		stage.setFullScreen(false);
		//stage.setMaximized(true);
		lastNanoTime = new Long(System.nanoTime());		

		new AnimationTimer() {
			public void handle(long currentNanoTime) {
				// calculate time since last update.
				double elapsedTime = (currentNanoTime - lastNanoTime) / 1000000000.0;				
				if(elapsedTime>.0150) //normally .0150
				{					
					root.setCache(true);
					root.setCacheHint(CacheHint.SPEED);
					lastNanoTime = currentNanoTime;
					//60 fps is .0166666667					
					frame++;
					int counter=0;
					int x;
					int y;
					int z;
					System.out.println("frame:" +frame);
					for(ModelSphere o : obs) { //update all the objects for this frame
						x=poses[frame][counter][0];
						y=poses[frame][counter][1];
						z=poses[frame][counter][2];
						o.refresh(x, y, z);
						//System.out.format("%d %d %d",x,y,z);
						counter++;						
					}
					
					if(frame>poses.length) {//the program is supposed to stop here
						Platform.exit();
						stop();
						System.exit(0);
						double xx= 1/0;
					}
				}				
			}
		}.start();

		// Adding scene to the stage
		stage.setScene(scene);
		// Displaying the contents of the stage
		stage.show();		
		
	}	
}
