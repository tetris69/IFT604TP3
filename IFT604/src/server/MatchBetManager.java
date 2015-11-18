package server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import commun.Match;
import commun.Message;
import commun.Message.Method;

public class MatchBetManager implements Runnable {
	Match match;
	
	BlockingQueue<Bet> requetes;
	List<Bet> parisEnregistres;
	Double montantTotal = 0.0;
	
	
	public MatchBetManager(Match match) {
		this.match = match;
		this.requetes = new ArrayBlockingQueue<Bet>(1024) ;
		this.parisEnregistres = new ArrayList<Bet>();
	}


	public void run() {
		while(true){
			try {
				Bet pari = requetes.take();
				if(match.getPeriode() == 1 || match.getPeriode() == 2){
					montantTotal += pari.montant;
					parisEnregistres.add(pari);
					pari.connection.sendMessage(new Message(Method.ConfirmationPari));
					System.out.println("Pari accepté de " + pari.montant + " pour l'equipe " + (pari.equipeA ? "A" : "B"));
				}
				else{
					pari.connection.sendMessage(new Message(Method.RefusPari));
					System.out.println("Pari refusé");
				}
				
			} catch (InterruptedException e) {
				return;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void parier(Bet bet){
		requetes.add(bet);
	}
	
	public void finalizeGame(){
		Double redistribution = montantTotal * 0.75;
		Double miseTotaleGagnants = 0.0;
		Boolean victoireEquipeA = match.getCompteursA().size() > match.getCompteursB().size();
		Boolean egalite = match.getCompteursA().size() == match.getCompteursB().size();
		for(Bet b : parisEnregistres)
			if(b.equipeA == victoireEquipeA || egalite)
				miseTotaleGagnants += b.montant;
		
		Message m;
		for(Bet b : parisEnregistres){
			m = new Message(Method.ResultatPari);
			Double gain;
			if(b.equipeA == victoireEquipeA || egalite)
				gain = redistribution * b.montant / miseTotaleGagnants;
			else
				gain = 0.0;
			m.addArgument(gain);
			try {
				b.connection.sendMessage(m);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}