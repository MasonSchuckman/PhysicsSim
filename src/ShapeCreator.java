import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
class MyCanvas extends JComponent { 
	private  int[] x=new int[30];
	private  int[] y=new int[30];
	int points=0;
	private int[][]ps;
    public void paint(Graphics g) 
    {     	
    	
        // draw and display the line 
    	  
    	int [] xx= {100,150,200};
    	int [] yy= {100, 150, 200};
	    g.drawPolyline(x, y, points);
	    for(int i[]:ps) {
	    	g.drawRect(i[0], i[1], 1, 1);
	    }
	    
    } 
    public void setPoints(int[][]p) {
    	ps=p;
    	repaint();
    }
}
public class ShapeCreator {
	public ShapeCreator() {
		
	}
	public  int[][]getPoints(int spacer){
		File imageFile = new File("C:\\Users\\suprm\\test.png");
		BufferedImage image = null;
		try {
			image = ImageIO.read(imageFile);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		MyCanvas can=new MyCanvas();
		
		
        int cooldown=spacer;
        int remaining=1;
        int points=0;
        ArrayList<Integer>x=new ArrayList<Integer>();
        ArrayList<Integer>y=new ArrayList<Integer>();
		for(int i=0; i<image.getWidth(); i++) {
			for(int j=0; j<image.getHeight(); j++) {
				int color=image.getRGB(i, j);
				if(color!=-1)
					if(remaining==0) {
						x.add(i);
						y.add(j);
						points++;
						remaining+=cooldown;
					}else
						remaining--;						
			}			
		}
		//System.out.println(points);
		int [][]p=new int[x.size()][2];
		for(int i=0; i<x.size(); i++) {
			p[i][0]= x.get(i);
			p[i][1]= y.get(i);
		}
		return p;
	}
//	public static void main(String [] args) throws IOException {
//		File imageFile = new File("C:\\Users\\suprm\\test.png");
//		BufferedImage image = ImageIO.read(imageFile);
//		MyCanvas can=new MyCanvas();
//		
//		
//        int cooldown=0;
//        int remaining=1;
//        int points=0;
//        ArrayList<Integer>x=new ArrayList<Integer>();
//        ArrayList<Integer>y=new ArrayList<Integer>();
//		for(int i=0; i<image.getWidth(); i++) {
//			for(int j=0; j<image.getHeight(); j++) {
//				int color=image.getRGB(i, j);
//				if(color!=-1)
//					if(remaining==0) {
//						x.add(i);
//						y.add(j);
//						points++;
//						remaining+=cooldown;
//					}else
//						remaining--;						
//			}			
//		}
//		//System.out.println(points);
//		int [][]p=new int[x.size()][2];
//		for(int i=0; i<x.size(); i++) {
//			p[i][0]= x.get(i);
//			p[i][1]= y.get(i);
//		}
//		
////		can.setPoints(p);
////		JFrame window = new JFrame(); 
////        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); 
////        window.setBounds(30, 30, 600, 600); 
////        
////        window.getContentPane().add(can); 
////        window.setVisible(true); 
//	}
	
}
	
