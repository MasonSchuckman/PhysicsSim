import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;

import javafx.scene.shape.Sphere;

public class SimulationReader {
	public static ArrayList<ModelSphere>models;
	public static camControls controls; //controls for the camera
	public static RecordedSimulationControls Pcontrols;	
	public static int [][][]poses;
	public static int [] cameraPos;
	public static void main(String [] args) throws IOException {
		int lines=0;
		int obs=0;
		models=new ArrayList<ModelSphere>();
		Pcontrols=new RecordedSimulationControls();//bool is for motion controls.
		try {
			String file="C:\\Users\\suprm\\Desktop\\data.txt";
			File myObj = new File(file);
			Scanner myReader = new Scanner(myObj);
			String setupData=myReader.nextLine();
			String [] preData=setupData.split(" ");
			
			obs=Integer.parseInt(preData[0]);
			int camx=Integer.parseInt(preData[1]);
			int camy=Integer.parseInt(preData[2]);
			int camz=Integer.parseInt(preData[3]);
			cameraPos= new int[]{camx,camy,camz};
			System.out.format("%d  %d  %d \n",camx,camy,camz);
			for(int i=0; i<obs; i++)
				models.add(new ModelSphere(3,3));
			int liness=countLines(file);
			System.out.println("there are "+liness);
			poses=new int[liness][obs][3];
			System.out.println(myReader.next());
			while (myReader.hasNextLine()) {
				String data = myReader.nextLine();
				String [] posData=data.split(" ");
				
				int counter=0;
				if(lines>1)
				for(String ob:posData) {
					String [] components=ob.split(",");
					
					poses[lines][counter][0]=Integer.parseInt(components[0]);
					poses[lines][counter][1]=Integer.parseInt(components[1]);
					poses[lines][counter][2]=Integer.parseInt(components[2]);
					counter++;
				}
				
				lines++;
				//if(lines>50)break;
				System.out.println("Loading simulation progress: "+100*lines/liness+"%");
			}
			myReader.close();
		} catch (FileNotFoundException e) {
			System.out.println("An error occurred.");
			e.printStackTrace();
		}finally {
			
			System.out.println("There were: "+lines+" frames of data!");
			System.out.format("There are %d objects\n",obs);
			RecordedSimulationPlayer simulationRunner=new RecordedSimulationPlayer(); //setup the application
			simulationRunner.startLoop(args); //start the application loop
		}

	}
	public static int countLines(String filename) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(filename));
	    try {
	        byte[] c = new byte[1024];

	        int readChars = is.read(c);
	        if (readChars == -1) {
	            // bail out if nothing to read
	            return 0;
	        }

	        // make it easy for the optimizer to tune this loop
	        int count = 0;
	        while (readChars == 1024) {
	            for (int i=0; i<1024;) {
	                if (c[i++] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        // count remaining characters
	        while (readChars != -1) {
	            //System.out.println(readChars);
	            for (int i=0; i<readChars; ++i) {
	                if (c[i] == '\n') {
	                    ++count;
	                }
	            }
	            readChars = is.read(c);
	        }

	        return count == 0 ? 1 : count;
	    } finally {
	        is.close();
	    }
	}
}
