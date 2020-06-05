import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.Sphere;

/*this class contains convenience methods for assorted tasks, 
 * such as printing arrays, converting a data file to an array,
 *and formatting data to be used by a machine learning model.
 */
public class customUtils {
	//convenience methods for printing arrays.
    public void printArray(float [] dat) {
    	for(int i=0; i<dat.length; i++){    		
			System.out.print(dat[i]+" ");    		    		
    	}
    	System.out.println("");
    }
    
    public void printArray(float [][] dat) {
    	for(int i=0; i<dat.length; i++){
    		for(int j=0; j<dat[0].length; j++) {
    			System.out.println(dat[i][j]);
    		}    		
    	}
    }
	//converts a data file to an array, data must be in sets of 12, separated by 1 blank space after each set, commas between values.
	public static float[][][]fileToArray(String path) throws IOException{
		 File file = new File(path); 
		 int lines=getLines(path)/13;//13 lines per 1 set of data
		  float [][][]dat=new float[lines][12][3];
		  BufferedReader br = new BufferedReader(new FileReader(file)); 		  
		  String st; 
		  for(int i=0; i<lines; i++) {
			  for(int j=0; j<12; j++) {
				  if((st = br.readLine())!=null) {
				  
				// System.out.println(st);
				  String [] vals=st.split(",");
				  float x=Float.parseFloat(vals[0]);
				  float y=Float.parseFloat(vals[1]);
				  float z=Float.parseFloat(vals[2]);
				  dat[i][j]=new float[] {x,y,z};
				  }
				  
			  }br.readLine();
		  }
		  return dat;
	}	

	/* given an array of values, it reverses the displacement data around the
	 * center. ex: if data started as : 30,30,30 reserved data would be: 30,30,30
	 *								    20,25,30                         40,35,30
	 */
	public static float[][][]reverseData(float [][][]data){
		float[][][]revD=new float[data.length][data[0].length][data[0][0].length];
		int counter=0;
		System.out.println(data.length);
		for(int k=0; k<data.length; k++) {
			//printWriter.print("0.0,0.0,0.0\n"); 
			
			for(int i=1; i<12; i++) {	
				revD[k][i][0]=0;
				revD[k][i][1]=0;
				revD[k][i][2]=0;
				for(int j=0; j<3; j++) {					
					float dif=data[k][0][j]-data[k][i][j];					
					revD[k][i][j]=data[k][i][j]+dif*2;					
				}
				counter++;
				//System.out.println("Normal:   "+data[k][i][0]+","+data[k][i][1]+","+data[k][i][2]+"    "+counter);
				//System.out.println("Reversed: "+revD[k][i][0]+","+revD[k][i][1]+","+revD[k][i][2]);
				//printWriter.print((revD[k][i][0]-70)+","+(revD[k][i][1]-70)+","+(revD[k][i][2]-70)+"\n"); 
			}
			//printWriter.print("\n"); 
		}
		return revD;
	}	
	//returns the number of lines in a file
	public static int getLines(String path) throws IOException {
		  File f1=new File(path); //Creation of File Descriptor for input file
	      int linecount=0;            //Intializing linecount as zero
	      FileReader fr=new FileReader(f1);  //Creation of File Reader object
	      BufferedReader br = new BufferedReader(fr);    //Creation of File Reader object
	      String s;              
	      while((s=br.readLine())!=null)    //Reading Content from the file line by line
	      {
	         linecount++;               //For each line increment linecount by one 	            
	      }
	      fr.close();
	      return linecount;
	}
	//normalize input array
	public static float[]normalizeArray(float[] data,int max){
    	return new float[]{data[0]/max,data[1]/max,data[2]/max};
    }
	
	/*
	 * changes an array from position data to displacement data, with all values
	 * being centered around the "offset" value. offset is useful for matching
	 * normalization for machine learning.
	 */
	public static float[][][] setToDisplacement(float[][][] data,int offset) {        
		float[][][] fixedData = new float[data.length][data[0].length][data[0][0].length]; 
		
		for(int i = 0; i < data.length; i++) {
           
			float[] newOrigin = {0, 0, 0};
            fixedData[i][0] = newOrigin;
            
            for(int j = 1; j < data[i].length; j++) { // data[i].length should always be 12
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
                fixedData[i][j] = newPoint;
            }
        }
        return fixedData;
    }
	
	/* this method is for neatness. Makes things cleaner by just calling this
	* method, instead of having all this junk in the start method.
	* Purpose of this method: convert each object in the arrayList into
	* spheres(read below), and then add those spheres to the Group(which is then
	* adding into the scene)
	*/
	public static void addStuff(Group root,ArrayList<Freebody> obs,ArrayList<TrackingPoint> trackers) {

		// Takes each Object in the arrayList, and creates Spheres inside the object's
		// instance data. This is used for cleaner drawing methods; less clutter.
		for (int i = 0; i < obs.size(); i++) {
			Freebody o = obs.get(i);
			o.makeSphere();
			Sphere s = o.ball;
			
			//sets the color of all the balls to the randomized values at each object's declaration
			PhongMaterial ballColor = new PhongMaterial();		    
		    ballColor.setSpecularColor(o.fxColor);
		    ballColor.setDiffuseColor(o.fxColor);		    
			s.setMaterial(ballColor);
			
			root.getChildren().add(s);
		}
		
		//adds the tracking points to the group
		for (int i = 0; i < trackers.size(); i++) {
			TrackingPoint t = trackers.get(i);
			t.makeSphere();	
			Sphere s = t.ball;
			
			//sets the color of all the balls to the randomized values at each object's declaration
			PhongMaterial ballColor = new PhongMaterial();		    
		    ballColor.setSpecularColor(t.fxColor);
		    ballColor.setDiffuseColor(t.fxColor);		    
			s.setMaterial(ballColor);
			
			root.getChildren().add(s);
		}
		//testing the lightsaber concept code
//		TrackingPoint t = trackers.get(0);
//		Box b=t.box;
//		PhongMaterial boxcolor = new PhongMaterial();		    
//		boxcolor.setSpecularColor(t.fxColor);
//		boxcolor.setDiffuseColor(t.fxColor);		    
//		b.setMaterial(boxcolor);
//		root.getChildren().add(b);	
		
		
	}
	/* this method is for adding the spheres to the group ( pre calculated trajectories only )
	*/
	public static void addStuff(Group root,ArrayList<ModelSphere> obs) {

		// Takes each Object in the arrayList, and creates Spheres inside the object's
		// instance data. This is used for cleaner drawing methods; less clutter.
		for (int i = 0; i < obs.size(); i++) {
			
			Sphere s = obs.get(i);
			
			//sets the color of all the balls to the randomized values at each object's declaration
			PhongMaterial ballColor = new PhongMaterial();		    
		    ballColor.setSpecularColor(javafx.scene.paint.Color.rgb(200, 60, 60, 1));
		    ballColor.setDiffuseColor(javafx.scene.paint.Color.rgb(200, 60, 60, 1));		    
			s.setMaterial(ballColor);
			
			root.getChildren().add(s);
		}
	}
}
