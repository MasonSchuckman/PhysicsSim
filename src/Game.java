
import java.util.ArrayList;

public class Game {

	public PhysicsSim s;	
	public ArrayList<Freebody> objects;	
	public long totalElapsedTime;
	public long frame;
	public WorldCreator w;
	public  ArrayList<TrackingPoint> trackers;
	//this class needs to be phased out
	public Game(WorldCreator world, ArrayList<Freebody> obs,ArrayList<TrackingPoint> trackers) {
		this.trackers=trackers;
		objects = obs;
		w = world;		
		s = new PhysicsSim(objects, world,trackers);		
	}
}
