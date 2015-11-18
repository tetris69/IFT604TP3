package client;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.util.HashMap;
import java.util.Map;

import commun.Match;
import commun.Message;
import commun.Message.Method;
import commun.Penalite;

public class Client {
		static Thread tmf;
		static ClientConnection cc;

	public static void main(String[] zero) throws ClassNotFoundException, IOException, InterruptedException{
		 cc = new ClientConnection("localhost",2015);
		try {			
			// Liste des matchs
			Message m = new Message(Method.ListeMatchs);
			// Results
			cc.sendMessage(m);
			
			
			// Follow match 2
			MatchFollower matchFollower = new MatchFollower(2, cc);
			tmf = new Thread(matchFollower);
			tmf.start();
			// Pari en ligne
			m = new Message(Method.Parier);
			m.addArgument(2);// numero de match
			m.addArgument(60);//60$
			m.addArgument(true);//Equipe A : 1  | Equipe B : 0
			cc.sendMessage(m);
			
			tmf.join();
			
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	public static void endMatch(){
		tmf.interrupt();
	}
}
