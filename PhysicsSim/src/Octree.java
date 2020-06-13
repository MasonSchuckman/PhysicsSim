import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Stack;
import java.util.concurrent.ThreadLocalRandom;

import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.DrawMode;

public class Octree {
	Octree parent;
	Octree [] children=new Octree[8];
	int [][] bounds = new int[3][2]; //specifies the bounding spatial corners of the node's box in space. format is: 
	//{ {root x, root x+size(of the tree) },{root y, root y+size(of the tree)}, ...	
	final int MIN_SIZE=10;
	final int minObs=2; // the minimum number of objects in an octant to justify making a new octree node
	final int minSizeToShow=50; //if an octant is smaller than 100, then don't draw it.
	int size;
	int [][][]octantBounds=new int[8][3][2];//this will store the bounding boxes for all of the octants.
	//octants go in this order(if looking at a cube face on):
	/*
	 * far away:
	 * 	 north west, north east		|0,1|
	 *   south west, south east		|2,3|
	 * close side:
	 * 								|4,5|
	 * 								|6,7|   	
	 */
	int[]origin;


	ArrayList <Freebody> [] octList=new ArrayList [8];//contains all the objects that fit within each respective octant.
	ArrayList <Freebody> obs;

	Box vis=null;
	@SuppressWarnings("rawtypes")
	public Octree(Octree _parent, int [][] bounds, ArrayList<Freebody>objectsInOctant) {
		this(_parent,new int[]{bounds[0][0],bounds[1][0],bounds[2][0]}, bounds[0][1]-bounds[0][0],objectsInOctant);
	}

	public Octree(Octree _parent, int [] roots, int size,ArrayList<Freebody>objectsInOctant) {

		TestingOctrees.totalOctrees++;		
		//origin is the center of the octant/octree
		origin=new int[3];
		origin[0]=roots[0]+size/2;
		origin[1]=roots[1]+size/2;
		origin[2]=roots[2]+size/2;


		this.size=size;
		obs=objectsInOctant;
		parent=_parent;

		for(int i=0; i<3; i++) {
			bounds[i][0]=roots[i];
			bounds[i][1]=roots[i]+size;			
		}

		for(int i=0; i<8; i++) {
			octList[i]=new ArrayList<Freebody>();
		}

		setupOctantBounds();
		setupVisualizationBox();

		Stack<Integer>delist=new Stack<Integer>();
		int objIndex=0;
		for(Freebody ob : obs) {
			float [] pos=ob.getPos().asArray();
			int i=0; //i is which octant we're checking
			for(int [][] octantBound : octantBounds) {				
				if(insideOfBounds(pos,octantBound,(int)(ob.getRadius()*1.5))) { //the 1.5 is to ensure that if an object is in (or close to in) 2 octants, it is included in both. 
					//This only make the algorithm run 5% slower. This was checked with statistical anaylysis.

					//adding object indicies to a stack to be removed from the current object's ArrayList
					if(delist.isEmpty())
						delist.push(objIndex);

					//one object might be in two octants, but the object can only be removed the parent's ArrayList, so we peek()
					//to ensure we don't try to remove the object twice. This would cause problems!
					else if(delist.peek()!=objIndex) 
						delist.push(objIndex);

					octList[i].add(ob);	
				}
				i++;
			}
			objIndex++;
		}
		//creating the children octrees once objects have been determined to lie within them
		for(int i=0; i<8; i++) {
			if(octList[i].size()>2&&size/2>MIN_SIZE) {				
				children[i]=new Octree(this,octantBounds[i],octList[i]);				
			}
		}
		//removing all objects from the parent's ArrayList once the children have been given them.
		while(!delist.isEmpty()) {
			int removing=delist.pop().intValue();
			obs.remove(removing);
		}


	}
	public boolean insideOfBounds(float [] objectPosition,int[][]bounds,int rad) {
		if(
				bounds[0][0]+rad<objectPosition[0]&&bounds[0][1]>objectPosition[0]+rad&& //checked the x position
				bounds[1][0]+rad<objectPosition[1]&&bounds[1][1]>objectPosition[1]+rad&& //checked the y position
				bounds[2][0]+rad<objectPosition[2]&&bounds[2][1]>objectPosition[2]+rad   //checked the z position

				/*
				 * New optimization: 
				 * I was checking all edge conditions, where one side of an object would be inside of an edge of another octant. This however, was causing the tree construction to take
				 * 3 times as long. As an alternative solution to edge conditions, I simply make the bounds of each octant larger by the radius of the object, 
				 * so that if an object is slightly inside of an octant, it will be counted.
				 * 
				 * old code to check edge conditions (idk why I did it this way to begin with):
				 * 
				 * //checking edge conditions

				//checking  when an ob is mostly in a sphere but the right side pokes into another
				bounds[0][0]<objectPosition[0]+rad&&rad+bounds[0][1]>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]&&bounds[1][1]>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]&&bounds[2][1]>objectPosition[2]   //checked the z position
				||
				//checking  when an ob is mostly in a sphere but the left side pokes into another
				bounds[0][0]<objectPosition[0]-rad&&bounds[0][1]-rad>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]&&bounds[1][1]>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]&&bounds[2][1]>objectPosition[2]   //checked the z position
				||

				//checking  when an ob is mostly in a sphere but the TOP side pokes into another
				bounds[0][0]<objectPosition[0]&&bounds[0][1]>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]+rad&&bounds[1][1]+rad>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]&&bounds[2][1]>objectPosition[2]   //checked the z position
				||
				//checking  when an ob is mostly in a sphere but the BOTTOM side pokes into another
				bounds[0][0]<objectPosition[0]&&bounds[0][1]>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]-rad&&bounds[1][1]-rad>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]&&bounds[2][1]>objectPosition[2]   //checked the z position
				||	
				//checking  when an ob is mostly in a sphere but the FRONT side pokes into another
				bounds[0][0]<objectPosition[0]&&bounds[0][1]>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]&&bounds[1][1]>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]+rad&&bounds[2][1]+rad>objectPosition[2]   //checked the z position
				||
				//checking  when an ob is mostly in a sphere but the BACK side pokes into another
				bounds[0][0]<objectPosition[0]&&bounds[0][1]>objectPosition[0]&& //checked the x position
				bounds[1][0]<objectPosition[1]&&bounds[1][1]>objectPosition[1]&& //checked the y position
				bounds[2][0]<objectPosition[2]-rad&&bounds[2][1]-rad>objectPosition[2]   //checked the z position
				 */
				)
			return true;
		else
			return false;
	}

	public void setupVisualizationBox() {
		if(size>minSizeToShow) {
			vis=new Box(0,0,0);		
			vis.setWidth(size);
			vis.setHeight(size);
			vis.setDepth(size);
			PhongMaterial mat=new PhongMaterial();
			int r1 = obs.size()/20;
			int r2 = obs.size();
			int r3 = obs.size()*3;
			r1=Math.min(r1, 255);
			r2=Math.min(r2, 255);
			r3=Math.min(r3, 255);
			int min=100;
			r3-=r1*r1;
			r2-=r1*r1/4;
			r1=Math.max(r1, min);
			r2=Math.max(r2, min);
			r3=Math.max(r3, min);

			mat.setDiffuseColor(javafx.scene.paint.Color.rgb(r1, r2, r3, .1));			

			vis.setMaterial(mat);

			vis.setDrawMode(DrawMode.LINE);
			vis.setTranslateX(origin[0]);
			vis.setTranslateY(origin[1]);
			vis.setTranslateZ(origin[2]);	
		}
	}

	public void setupOctantBounds() {
		int x=origin[0];
		int y=origin[1];
		int z=origin[2];

		//far octants first
		octantBounds[0]=makeBounds(x-size/2,y-size/2,z-size/2,size/2); //NW
		octantBounds[1]=makeBounds(x,y-size/2,z-size/2,size/2); //NE

		octantBounds[2]=makeBounds(x-size/2,y,z-size/2,size/2);//SW
		octantBounds[3]=makeBounds(x,y,z-size/2,size/2);		//SE

		//close octants now (changing z)
		octantBounds[4]=makeBounds(x-size/2,y-size/2,z,size/2); //NW
		octantBounds[5]=makeBounds(x,y-size/2,z,size/2); //NE

		octantBounds[6]=makeBounds(x-size/2,y,z,size/2);//SW
		octantBounds[7]=makeBounds(x,y,z,size/2);		//SE

	}

	public int[][]makeBounds(int x,int y, int z,int size){//makes the bounds based on origin x,y and the size
		int[][]b=new int[3][2];

		b[0][0]=x;
		b[0][1]=x+size;


		b[1][0]=y;
		b[1][1]=y+size;

		b[2][0]=z;
		b[2][1]=z+size;

		return b;
	}


	public String toString(int spacer) {
		String data="";
		String space="";				
		for(int i=0; i<spacer*4; i++) {
			space+=" ";
		}
		data+=space+"Level "+spacer+" with "+obs.size()+" objects\n"+space+"\n";
		for(int i=0; i<8; i++) {
			if(children[i]!=null) {
				data+=children[i].toString(spacer+1);
			}
		}		
		return data;
	}
}
