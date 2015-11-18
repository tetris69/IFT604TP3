package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import commun.Marshallizer;
import commun.Match;
import commun.Message;

/**
 *  Florian Bonniec
 *  Eric Bergeron 13 073 363
 *  Marie Chidaine 15 134 418
 */

public class Serveur {

	public static void main(String[] zero) {
		// LNH creation
		LNH lnh = new LNH();
		// Create matchs
		Match m1 = new Match("Canadiens", "Bruins", 1);
		Match m2 = new Match("Rangers", "Red wings", 2);
		lnh.startMatch(m1);
		lnh.startMatch(m2);
		
		m1.scoreEquipeA("P.K Subban");
		m1.scoreEquipeA("Paccioretty");
		m1.scoreEquipeB("Bergeron");
		m1.penaliteEquipeA("Paccioretty", 2);
		
		m2.scoreEquipeA("JoueurX");
		
		
		//
		ServerSocket listenSocket;
		ExecutorService executor = Executors.newFixedThreadPool(20000);// Thread pool

		try {
			listenSocket = new ServerSocket(2015);
			System.out.println("Server Online");
			while (true) {
				Socket clientSocket = listenSocket.accept();
				Connection c = new Connection(clientSocket, lnh);
				executor.execute(c);
			}

		} catch (IOException e) {

			e.printStackTrace();
		}
	}
}

