
import java.awt.GridLayout;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;

import org.opencv.imgproc.Imgproc;
import org.opencv.video.Video;
import org.opencv.videoio.VideoCapture;
import org.opencv.videoio.Videoio;
 
 /*this class is responsible for all optical motion tracking.
 *includes features to tune the color filters with UI
 *the heart of this system is to read a frame from each camera, only look at the pixels that
 *fit inside the color filters specified by the UI(also called masks or limiters), then
 *find contours of those pixels, determine which contour is largest, and make a box around that contour
 *finally, send the x,y,z data of that box to the simulation, the x,y,z is gathered from the 2 cameras,
 *1 camera gets the x,y data of the contour, and the second camera gets the z position
 *the 2 cameras must be set at 90 degree angles to each other to achieve 3 dimensional motion tracking
 */
public class motionCapture extends Thread{
	private playerControls p;
	private JFrame jframe,jframe2;
	private JLabel vidpanel,vidpanel2;
	private final AtomicBoolean running = new AtomicBoolean(false);
	private String path;
	public motionCapture(playerControls play) {
		p=play;
		System.out.println("Enter the path of opencv_java411.dll");
		//Scanner sc=new Scanner(System.in);
		// path=sc.nextLine();
	}
	private float[][] pos=new float[2][3];//work on setting the first dimension automatically. Right now its hardcoded for 2 controllers.
	private float[][] raw=new float[2][3];
	
	private float []rawXY1=new float[2];
	private float []rawXY2=new float[2];
	//set brightness to 160 in camera app!!!!!
//	private int[] bounds={255,230,155,200,0,0};// [finding Red] upper RGB, lower RGB. (changeable by UI)
//	private int[] bounds2={200,255,255,0,220,220};// [finding Green] upper RGB, lower RGB. (changeable by UI)
	private int[] bounds={255,50,50,100,0,0};// [finding Red] upper RGB, lower RGB. (changeable by UI)
	private int[] bounds2={90,255,255,0,125,125};// [finding Green] upper RGB, lower RGB. (changeable by UI)
	private Scalar lowerb,upperb,lowerb2,upperb2;;
	private Mat frame,firstFrame,gray,frameDelta,thresh,frame2,firstFrame2,gray2,frameDelta2,thresh2,outr,outr2,outg,outg2;
	private  List<MatOfPoint> cnts,cnts2;
	private boolean contourViewing=false;
	private ArrayList<List<MatOfPoint>>contours;
	private VideoCapture camera,camera2;
	public void run()  {//playerControls play
		
		//load library
		 path="./dependencies/x64\\opencv_java411.dll";
		
		System.load(path);
		setupStartingFrames();
		
		
		camera = new VideoCapture();//open camera1 (x,y) axis
		camera.open(0);
		camera2 = new VideoCapture();//open camera2 (z axis)
		camera2.open(1);
		setupCameraSettings();
		camera.read(frame);
		camera2.read(frame2);
		
		//convert to grayscale and set the first frame
		Imgproc.cvtColor(frame, firstFrame, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(firstFrame, firstFrame, new Size(21, 21), 0);
		//System.out.println(frame.height());
		Imgproc.cvtColor(frame2, firstFrame2, Imgproc.COLOR_BGR2GRAY);
		Imgproc.GaussianBlur(firstFrame2, firstFrame2, new Size(21, 21), 0);
		//turns on webcam view
		displayViews();
		//setting up the bounds for filters
		updateBounds();
	  	addUI();
	  	dimensions=getDimensions();
		while(true) {			
			//reads the frames
			camera.read(frame);
			camera2.read(frame2);		
			//sets up the images to display on screen
			setImages(frame,frame2);
			//sends motion data to the simulation			
			p.addData(pos,raw);	
			p.addRawXY(rawXY1.clone(), rawXY2.clone());			
		}
	}
	private void getAngles(Point []p1,Point[]p2){
		if(p1!=null)
		for(int i=0; i<4; i++) {
			//System.out.println(p1[i]);
		}
		//return ;		
	}
	
	public int[]getDimensions(){
		return new int[] {frame.cols(),frame.rows()};
	}
	private int[]dimensions; 
	private void setupCameraSettings() {
		camera.set(Videoio.CAP_V4L2, 1);
		camera2.set(Videoio.CAP_V4L2, 1);
		camera.set(Videoio.CAP_PROP_AUTO_EXPOSURE, .25);//.25 enables manual exposure control, .75 re-enables auto control
		camera2.set(Videoio.CAP_PROP_AUTO_EXPOSURE, .25);
		camera.set(Videoio.CAP_PROP_FPS, 60);
		camera2.set(Videoio.CAP_PROP_FPS, 60);
		
		
		//camera.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);
		//camera2.set(Videoio.CAP_PROP_FRAME_HEIGHT, 720);
		
		//camera.set(Videoio.CAP_PROP_FRAME_WIDTH, 200);
		//camera2.set(Videoio.CAP_PROP_FRAME_WIDTH, 1280);
		
		//exposure of -3 works best in my room
		camera.set(Videoio.CAP_PROP_EXPOSURE, -3);
		camera2.set(Videoio.CAP_PROP_EXPOSURE, -3);
		
	}
	private void setupStartingFrames() {

		//stuff for first camera
		 frame = new Mat();
		 firstFrame = new Mat();
		 gray = new Mat();
		 frameDelta = new Mat();
		 thresh = new Mat();
		
		//stuff for second camera
		 frame2 = new Mat();
		 firstFrame2 = new Mat();
		 gray2 = new Mat();
		 frameDelta2 = new Mat();
		 thresh2 = new Mat();		
	}
	
	
	private void displayViews() {		
		//displaying first camera
		jframe= new JFrame("Camera1");
	    jframe.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    vidpanel = new JLabel();
	    jframe.setContentPane(vidpanel);
	    jframe.setVisible(true);
	    jframe.setSize(656, 518); //TODO: set these based on camera dimensions.
		//displaying second camera
		jframe2 = new JFrame("Camera2");
		jframe2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		vidpanel2 = new JLabel();
		jframe2.setContentPane(vidpanel2);
		jframe2.setVisible(true);
		jframe2.setSize(656, 518); //TODO: set these based on camera dimensions.			
	}
	
	private ArrayList<List<MatOfPoint>> findRedContours(Mat f1, Mat f2){
		List<MatOfPoint> cnts,cnts2;
		cnts=new ArrayList<MatOfPoint>();
		cnts2=new ArrayList<MatOfPoint>();
		
		Mat mask= new Mat();
		outr= new Mat();
		Core.inRange(f1, lowerb, upperb, mask);
		Core.bitwise_and(f1, f1,outr,mask);
		
		Mat mask2= new Mat();
		outr2= new Mat();
		Core.inRange(f2, lowerb, upperb, mask2);
		Core.bitwise_and(f2, f2,outr2,mask2);
		
		//convert to grayscale
		Imgproc.cvtColor(f1, gray, Imgproc.COLOR_BGR2GRAY);		
		Imgproc.cvtColor(f2, gray2, Imgproc.COLOR_BGR2GRAY);		
		
		//find the contours in the image.
		Imgproc.threshold(mask,thresh,40,255,Imgproc.THRESH_BINARY);
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.threshold(mask2,thresh2,40,255,Imgproc.THRESH_BINARY);
		Imgproc.findContours(thresh2, cnts2, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		ArrayList<List<MatOfPoint>> contours=new ArrayList<List<MatOfPoint>>(2);
		contours.add(cnts);
		contours.add(cnts2);
		//f1.assignTo(output);

		return contours;
	}
	
	private ArrayList<List<MatOfPoint>> findGreenContours(Mat f1, Mat f2){
		List<MatOfPoint> cnts,cnts2;
		cnts=new ArrayList<MatOfPoint>();
		cnts2=new ArrayList<MatOfPoint>();
		
		Mat mask= new Mat();
		outg= new Mat();
		Core.inRange(f1, lowerb2, upperb2, mask);
		Core.bitwise_and(f1, f1,outg,mask);
		
		Mat mask2= new Mat();
		outg2= new Mat();
		Core.inRange(f2, lowerb2, upperb2, mask2);
		Core.bitwise_and(f2, f2,outg2,mask2);
		
		//convert to grayscale
		Imgproc.cvtColor(f1, gray, Imgproc.COLOR_BGR2GRAY);		
		Imgproc.cvtColor(f2, gray2, Imgproc.COLOR_BGR2GRAY);
		
		//find the contours in the image.
		Imgproc.threshold(mask,thresh,40,255,Imgproc.THRESH_BINARY);
		Imgproc.findContours(thresh, cnts, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		Imgproc.threshold(mask2,thresh2,40,255,Imgproc.THRESH_BINARY);
		Imgproc.findContours(thresh2, cnts2, new Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
		
		ArrayList<List<MatOfPoint>> contours=new ArrayList<List<MatOfPoint>>(2);
		contours.add(cnts);
		contours.add(cnts2);
		return contours;
	}
	
	
	private int thick=0; //thickness of outlining boxes
	private int MAX=250; //max area of a contour to be detected. (in pixels)
	private int MIN=4;
	private double points[]=new double[4];
	/*
	 * finds given a list of contours for each color and the 2 webcam frames, find and draw a box
	 * around the largest contour found by each webcam onto the frame.
	 */
	private Mat[] addRectangles(Mat f1,Mat f2, List<MatOfPoint> cnts, List<MatOfPoint> cnts2,String col) {
		Point points[] = null;
		Point points2[] =null;
		Scalar color=new Scalar(0,0,0);
		int num=0;
		if(col=="RED") {
			color.set(new double[] {0,255,0});
			num=0;
		}
		if(col=="GREEN") {
			color.set(new double[] {100,120,255});
			num=1;
		}
		Mat matrix=f1;
		Mat matrix2=f2;
		double largest=0;
		int big=-1;
		// find largest contour area in first image
		for (int i=0; i<cnts.size(); i++) {
			
			MatOfPoint contour=cnts.get(i);
			double area=contour.size().area();
			if (area>largest&&area<MAX&&area>MIN) {
				largest=area;
				//System.out.println("got here, area: " +largest);
				big=i;
			}
		}
		
		
		if (big!=-1) {
			Rect r = Imgproc.boundingRect(cnts.get(big));
			MatOfPoint2f dst = new MatOfPoint2f();
			cnts.get(big).convertTo(dst, CvType.CV_32F);
			RotatedRect re= Imgproc.minAreaRect(dst);
			int ang=-(int)re.angle;
			//p.setAngle2(ang);
			//if vert and <45, add 90.
			//if hori and >45, subtract 90
			points = new Point[4];
		    re.points(points);
		    
		    if(isVertical(points,re)&&ang<45&&(ang+90-last1)<75){
		    	//
		    	p.setAngle2(ang+90);
		    	last1=ang+90;
		    	//System.out.println(ang+90);
	    	}else if(!isVertical(points,re)&&ang>45&&(ang-90-last1)<75) {
	    		p.setAngle2(ang-90);
	    		//System.out.println(ang-90);
	    		last1=ang-90;
		    }else {
		    	 //System.out.println(ang);
		    	 p.setAngle2(ang);
		    	 last1=ang;
		    }
		    for(int i=0; i<4; ++i){
		       // Imgproc.line(matrix, points[i], points[(i+1)%4], new Scalar(255,255,255),2);
		    }
			int[] p = new int[4];
			//System.out.println(big);
			p[0]=r.x;
			p[1]=r.y;
			p[2]=r.width;
			p[3]=r.height;

			Imgproc.rectangle(matrix, // Matrix obj of the image
					new Point(p[0],p[1]), // p1
					new Point(p[0]+p[2],p[1]+p[3]), // p2
					color, // Scalar object for color
					thick // Thickness of the line
			);
			
			//gets the center of mass of the rectangle
			p=getCOM(p);
			//data to be returned
			pos[num][0]=p[0]-dimensions[0]/2; //x coordinate
			raw[num][0]=p[0];
			rawXY1[0]=p[0];
			rawXY1[1]=p[1];
		}
		if (cnts2.size() > 0) {
			largest = 0;
			big = -1;
			// find largest contour area in second image
			for (int i=0; i<cnts2.size(); i++) {
				MatOfPoint contour = cnts2.get(i);
				double area=contour.size().area();
				if (area>largest&&area<MAX&&area>MIN) {  // finds the largest area, with a max area of MAX																						
					largest=area;
					//System.out.println("got here2, area: " +largest);
					big=i;
				}
			}
			
			if (big!=-1) {
				//System.out.println(big);
				Rect r = Imgproc.boundingRect(cnts2.get(big));
				
				MatOfPoint2f dst = new MatOfPoint2f();
				cnts2.get(big).convertTo(dst, CvType.CV_32F);
				RotatedRect re= Imgproc.minAreaRect(dst);
				//System.out.println(re.angle);
				int ang=-(int)re.angle;
				//p.setAngle(ang);
				
				points2 = new Point[4];
			    re.points(points2);
			    //System.out.println(ang);
			    if(isVertical(points2,re)&&ang<45&&(ang+90-last2)<75){
			    	//
			    	p.setAngle(ang+90);
			    	System.out.println(ang+90);
			    	last2=ang+90;
		    	}else if(!isVertical(points2,re)&&ang>45&&(ang-90-last2)<75) {
		    		p.setAngle(ang-90);
		    		System.out.println(ang-90);
		    		last2=ang-90;
			    }else {
			    	 System.out.println(ang);
			    	 p.setAngle(ang);
			    	 last2=ang;
			    }
			   
			    
		    	//System.out.println(ang);
			   // System.out.println(isVertical(points2,re));
			    for(int i=0; i<4; ++i){
			        //Imgproc.line(matrix2, points2[i], points2[(i+1)%4], new Scalar(255,255,255),2);
			    }
				int[] p = new int[4];
				p[0]=r.x;
				p[1]=r.y;
				p[2]=r.width;
				p[3]=r.height;
				
				Imgproc.rectangle(matrix2, // Matrix obj of the image
						new Point(p[0],p[1]), // p1
						new Point(p[0]+p[2], p[1]+p[3]), // p2
						color, // Scalar object for color
						thick // Thickness of the line
				);
				//gets the center of mass of the rectangle
				p=getCOM(p);
				//data to be returned
				pos[num][1]=p[1]-dimensions[1]/2; //y coordinate
				pos[num][2]=p[0]-dimensions[0]/2; //z coordinate
				raw[num][1]=p[1];
				raw[num][2]=p[0];
				rawXY2[0]=p[0];
				rawXY2[1]=p[1];
			}
			getAngles(points,points2);
		}
        return new Mat[]{f1,f2};        	
	}
	private int last1=0;
	private int last2=0;
	private boolean isVertical(Point [] p,RotatedRect r) {
		double dx=Math.abs(p[0].x-r.center.x);
		double dy=Math.abs(p[0].y-r.center.y);
		if(dx>dy) {
			return false; 
		}else
		return true;
	}
	
	//System.out.println(new Color(Mat2BufferedImage(frame).getRGB(a, b)));	//gets the RGB value of a pixel at a,b
	private void setImages(Mat f1,Mat f2) {
		cnts=new ArrayList<MatOfPoint>();
		cnts2=new ArrayList<MatOfPoint>();		
	
		//finding and boxing red object
		contours=findRedContours(f1,f2);
		cnts=contours.get(0);			
		cnts2=contours.get(1);	
		if(contourViewing)
		addRectangles(outr,outr2,cnts,cnts2,"RED");else
		addRectangles(f1,f2,cnts,cnts2,"RED");
		
		//finding and boxing green object
		contours=findGreenContours(f1,f2);
		cnts=contours.get(0);			
		cnts2=contours.get(1);			
		if(contourViewing)
			addRectangles(outg,outg2,cnts,cnts2,"GREEN");else
			addRectangles(f1,f2,cnts,cnts2,"GREEN");
		
		
		if(contourViewing) {
			//this code combines the two different filtered images.
			//(puts the red and blue layers together into 1 image, including the rectangles)
			Core.addWeighted(outr, 1, outg, 1, 0, f1);
			Core.addWeighted(outr2, 1, outg2, 1, 0, f2);			
		}
		//paints the next frame
		vidpanel.setIcon(new  ImageIcon(Mat2BufferedImage(f1)));
        vidpanel2.setIcon(new ImageIcon(Mat2BufferedImage(f2)));
        vidpanel.repaint(); //first camera image            
        vidpanel2.repaint(); //second camera image
	}
	private int selected=0;
	private int[]getCOM(int[]p) {
		int x=p[0]+(p[2]/2);
		int y=p[1]+(p[3]/2);
		
		return new int[]{x,y};		
	}
	private String[] texts= {"Upper Red: ","Upper Green: ","Upper Blue: ","Lower Red: ","Lower Green: ","Lower Blue: "};
	private ArrayList<JLabel>statuses; //shows the limiting variables for color detection
	private double[]exposure= {-3,-3};
	private ArrayList<JLabel>exposures;//shows the exposure of each camera
	private void updateBounds() {
		//updates red filter
		lowerb=new Scalar(bounds[5], bounds[4], bounds[3]); 
		upperb=new Scalar(bounds[2], bounds[1], bounds[0]);	
		//updates green filter
		lowerb2=new Scalar(bounds2[5], bounds2[4], bounds2[3]); 
		upperb2=new Scalar(bounds2[2], bounds2[1], bounds2[0]);	
	}
	private ArrayList<JSlider> sliders; //sliders for color limiting
	private  ArrayList<JSlider> exposureSliders; //TODO: finish implementing slider to control camera exposure
	private void addUI() {
		statuses=new ArrayList<>();
		// Create and set up a frame window
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("Slider with change listener");
        frame.setSize(500, 500);
        frame.setLayout(new GridLayout(4, 2));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        sliders=new ArrayList<JSlider>();
        for(int i=0; i<6; i++) {
        	//add sliders and values to frame        	
        	frame.add(addSlider(i));
        	frame.add(statuses.get(i));
        }
        //exposure slider adding to the frame
//        for(int i=0; i<1; i++) {
//        	frame.add(exposureSliders.get(i));
//        	frame.add(exposures.get(i));
//        }
        
        //add the drop down box for selecting which filter to edit
        addSelectionBox(frame);
        frame.pack();
        frame.setVisible(true);
	}
	   
	private void addSelectionBox(JFrame f){    //creates the selection box to choose which color filter to edit
	       
	    final JLabel label = new JLabel();          
	    label.setHorizontalAlignment(JLabel.CENTER);  
	    label.setSize(100,50);  
	    int s=10;
	    JButton b=new JButton("Select");  
	    b.setBounds(0,0,s,s);  
	    String languages[]={"Red","Green"};        
	     @SuppressWarnings({ "rawtypes", "unchecked" })
		JComboBox cb=new JComboBox(languages);    
	    cb.setBounds(50, 100,s,s);    
	    f.add(cb);
	    f.add(label);
	    f.add(b); 
	    label.setHorizontalAlignment(JLabel.CENTER); 
	          
	    b.addActionListener(new ActionListener() {  
	        public void actionPerformed(ActionEvent e) {       
	        	String data = "Filter Selected: "+ cb.getItemAt(cb.getSelectedIndex());  
	        	label.setText(data);
	        	selected=cb.getSelectedIndex();	        	
	        	refreshSliders();
			}
		});
	    
	    JToggleButton toggleButton = new JToggleButton("Click Me!");     
	      toggleButton.addActionListener(new ActionListener() {
	         @Override
	         public void actionPerformed(ActionEvent e) {
	        	 boolean status=((JToggleButton)e.getSource()).isSelected();
	        	 if(status)toggleButton.setText("true");else toggleButton.setText("false");	        	 
	        	 contourViewing=status;
	         }
	      });
	      f.add(toggleButton);
	}
  
	//TODO: finish implementing exposure control via slider.
	private JPanel addExposureSlider(int num) {
		// Set the panel to add buttons
        JPanel panel = new JPanel();
        
        // Add status label to show the status of the slider
        JLabel status = new JLabel("Exposure of Camera "+num+": "+exposure[num]);//, JLabel.CENTER);
         
        // Set the slider
        JSlider slider = new JSlider(); 
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        // Set the labels to be painted on the slider
        slider.setPaintLabels(true);
        slider.setMaximum(3);
        slider.setMinimum(-8);
        slider.setValue((int) exposure[num]);
        // Add positions label in the slider
        Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
        position.put(-8, new JLabel("-8"));
        position.put(-3, new JLabel("-3"));
        position.put(0, new JLabel("0"));
        position.put(3, new JLabel("3"));
        
        // Set the label to be drawn
        slider.setLabelTable(position);        
        // Add change listener to the slider
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {                
            	int val=((JSlider)e.getSource()).getValue();                
            	
                status.setText("Exposure of Camera "+num +": "+ val);
                exposure[num]=val;
            	
                updateBounds();
            }
        });
        statuses.add(status);
        // Add the slider to the panel
        sliders.add(slider);
        panel.add(slider);
        return panel;
	}
	
	private JPanel addSlider(int num) { //returns a slider corresponding to a color limiter
		// Set the panel to add buttons
        JPanel panel = new JPanel();
        
        // Add status label to show the status of the slider
        JLabel status = new JLabel(texts[num]+bounds[num]);//, JLabel.CENTER);
         
        // Set the slider
        JSlider slider = new JSlider(); 
        slider.setMinorTickSpacing(10);
        slider.setPaintTicks(true);
        // Set the labels to be painted on the slider
        slider.setPaintLabels(true);
        slider.setMaximum(255);
        slider.setValue(bounds[num]);
        // Add positions label in the slider
        Hashtable<Integer, JLabel> position = new Hashtable<Integer, JLabel>();
        position.put(0, new JLabel("0"));
        position.put(100, new JLabel("100"));
        position.put(200, new JLabel("200"));
        position.put(255, new JLabel("255"));
        
        // Set the label to be drawn
        slider.setLabelTable(position);        
        // Add change listener to the slider
        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent e) {                
            	int val=((JSlider)e.getSource()).getValue();                
            	if(selected==0) {
                status.setText(texts[num] + val);
                bounds[num]=val;
            	}else if(selected==1) {
            		status.setText(texts[num] + val);
                    bounds2[num]=val;                    
            	}
                updateBounds();
            }
        });
        statuses.add(status);
        // Add the slider to the panel
        sliders.add(slider);
        panel.add(slider);
        return panel;
	}
	private void refreshSliders() {
		if(selected==0) {
			for(int i=0; i<sliders.size(); i++) {
				JSlider s=sliders.get(i);
				int val=bounds[i];
				s.setValue(val);
				statuses.get(i).setText(texts[i] + val);
			}
		}else if(selected==1) {
			for(int i=0; i<sliders.size(); i++) {
				JSlider s=sliders.get(i);
				int val=bounds2[i];
				s.setValue(val);
				statuses.get(i).setText(texts[i] + val);
			}
		}
	}
	private BufferedImage image;	
	public BufferedImage Mat2BufferedImage(Mat m) { //converts a matrix object to a bufferedImage to be displayed.
	    // Fastest code
	    // output can be assigned either to a BufferedImage or to an Image

	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = m.channels()*m.cols()*m.rows();
	    byte [] b = new byte[bufferSize];
	    m.get(0,0,b); // get all the pixels
	    image= new BufferedImage(m.cols(),m.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    System.arraycopy(b, 0, targetPixels, 0, b.length);  
	    return image;
	}
	
}
