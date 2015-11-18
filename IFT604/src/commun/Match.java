package commun;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Match implements Serializable {
	private int id;
	private String equipeA;
    private String equipeB;
    private int periode;
    private Double chronometre;
    private List<String> compteursA;
    private List<String> compteursB;
    private List<Penalite> penalitesA;
    private List<Penalite> penalitesB;
	
    public Match(String equipeA, String equipeB, int id) {
    	this.id = id;
		this.equipeA = equipeA;
		this.equipeB = equipeB;
		this.periode = 1;
		this.chronometre = 20.0;
		this.compteursA = new ArrayList<String>();
		this.compteursB = new ArrayList<String>();
		this.penalitesA = new ArrayList<Penalite>();
		this.penalitesB = new ArrayList<Penalite>();
	}
    
    public void scoreEquipeA(String compteur){
    	compteursA.add(compteur);
    }
    
    public void scoreEquipeB(String compteur){
    	compteursB.add(compteur);
    }
    
    public void penaliteEquipeA(String joueur, int minutes){
    	double liberation = Double.max(0.0, chronometre - minutes);
    	penalitesA.add(new Penalite(joueur, liberation));
    }
    
    public void penaliteEquipeB(String joueur, int minutes){
    	double liberation = Double.max(0.0, chronometre - minutes);
    	penalitesB.add(new Penalite(joueur, liberation));
    }

	public int getId() {
		return id;
	}
	
	public String toString(){
		return equipeA + " VS " + equipeB;
	}

	public String getEquipeA() {
		return equipeA;
	}

	public void setEquipeA(String equipeA) {
		this.equipeA = equipeA;
	}

	public String getEquipeB() {
		return equipeB;
	}

	public void setEquipeB(String equipeB) {
		this.equipeB = equipeB;
	}

	public int getPeriode() {
		return periode;
	}

	public void setPeriode(int periode) {
		this.periode = periode;
	}

	public Double getChronometre() {
		return chronometre;
	}

	public void setChronometre(Double chronometre) {
		this.chronometre = chronometre;
	}

	public List<String> getCompteursA() {
		return compteursA;
	}

	public void setCompteursA(List<String> compteursA) {
		this.compteursA = compteursA;
	}

	public List<String> getCompteursB() {
		return compteursB;
	}

	public void setCompteursB(List<String> compteursB) {
		this.compteursB = compteursB;
	}

	public List<Penalite> getPenalitesA() {
		return penalitesA;
	}

	public void setPenalitesA(List<Penalite> penalitesA) {
		this.penalitesA = penalitesA;
	}

	public List<Penalite> getPenalitesB() {
		return penalitesB;
	}

	public void setPenalitesB(List<Penalite> penalitesB) {
		this.penalitesB = penalitesB;
	}

	public void setId(int id) {
		this.id = id;
	}

	
	
	
}
