import java.util.ArrayList;

public class OrbitManager {
	private ArrayList<Satelite>satelites;
	public OrbitManager() {
		satelites=new ArrayList<Satelite>();
	}
	public void addAnchor(Satelite satelite) {
		satelites.add(satelite);
	}
	public void update() {
		for(Satelite ob:satelites) {
			
		}
	}
	
	
}
