package commun;

import java.io.Serializable;
import java.util.List;

public class Penalite implements Serializable {
    public String joueur;
    public Double chronometreLiberation;
    
	public Penalite(String joueur, Double chronometreLiberation) {
		this.joueur = joueur;
		this.chronometreLiberation = chronometreLiberation;
	}
    
    
}
