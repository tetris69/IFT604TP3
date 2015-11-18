package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

import commun.Match;
import commun.Message;
import commun.Penalite;
import commun.Message.Method;

public class MatchFollower implements Runnable {
	Integer matchId;
	Match match;
	ClientConnection cc;

	public MatchFollower(Integer matchId, ClientConnection cc) {
		this.matchId = matchId;
		this.cc = cc;
	}

	public void run() {
		while (true) {
			Message m = new Message(Method.DetailsMatch);
			m.addArgument(matchId);
			try {
				cc.sendMessage(m);
			} catch (ClassNotFoundException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (IOException e1) {
				return;
			}

			// Sleep
			try {
				Thread.sleep(5000);//2 * 60000);
			} catch (InterruptedException e) {
				return;
			}
		}
	}

}
