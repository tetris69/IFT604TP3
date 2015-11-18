package server;

public class Bet {
	Integer montant;
	Boolean equipeA;
	Connection connection;
	
	public Bet(Integer montant, Boolean equipeA, Connection connection) {
		this.montant = montant;
		this.equipeA = equipeA;
		this.connection = connection;
	}
	
	
	
}
