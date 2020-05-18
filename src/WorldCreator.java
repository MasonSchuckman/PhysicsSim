
//the class that holds the parameters for the simulation, such as universe size, gravity, and (G)ravity constant
public class WorldCreator 
{
    
	private int width=300,height=300,length=300,locX=0,locY=0,locZ=0;
    private float gravity=9.81f,iter=.01f,maxTime=10,gForce=0;
    private boolean noBounds=false;
    private int top=50,bot=600,left=50,right=400;
    private boolean locationOn=false;
    private int[]bounds={top,bot,left,right};
    public boolean checking;
    public boolean motionControls=false;
    public boolean hasController=false;
    //private Line[]lines=new Line[4];
    public WorldCreator(int w,int h,int l)//l is length
    {
        width=w;
        height=h;
        length=l;
    }
    //allows for gravity change
    public WorldCreator(int w,int h,int l, float grav,float GFORCE, int locx,int locy,int locz)
    {
    	this(w,h,l);
    	checking=false;
    	
        //if no borders, then turn on location definition
        if(w==0){
        	locX=locx;
        	locY=locy;
        	locZ=locz;
        	locationOn=true;
        }else {
        	locX=w/2;
        	locY=h/2;
        	locZ=l/2;
        }
        
        gravity=grav;
        //if entered width is 0, then the universe is unlimited.
        if(w==0)
        {
        	noBounds=true;
        }
        
        if(GFORCE!=0)
        {
            gForce=GFORCE;
        }
        
    } 
    public void turnOnMotionControls() {
    	motionControls=true;
    }
    public void isController() {
    	hasController=true;
    }
    public void collisions(boolean a){
    	checking=a;
    }
    public float getGrav()
    {
        return gravity;
    }
    
    public boolean locationIsDefined() {
    	return locationOn;
    }
    public int getWidth()
    {
        return width;
    }

    public int getHeight()
    {
        return height;
    }
    
    public int getLength()
    {
        return length;
    }
    public int getLocX()
    {
        return locX;
    }

    public int getLocY()
    {
        return locY;
    }
    
    public int getLocZ()
    {
        return locZ;
    }
    public float getIter()
    {
        return iter;
    }
    
    public boolean isBounds()
    {
    	return !noBounds;
    }
    
    public float getMaxTime()
    {
        return maxTime;
    }

    public float getGForce()
    {
        return gForce;
    }
    
    public int[] getBounds()
    {
        return bounds;
    }

}

