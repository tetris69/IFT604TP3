package client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import commun.Match;
import commun.Message;
import commun.Penalite;
import commun.Message.Method;
import server.Bet;
import server.LNH;

public class ClientConnection {
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Integer requestNumber = 0;
	Thread threadListen;
	
	public ClientConnection(String server, Integer port) throws IOException{
		socket = new Socket(server, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		threadListen = new Thread(new ConnectionListenner(ois));
		threadListen.start();
	}

	protected void finalize() throws IOException {
		socket.close();
	}
	
	private synchronized Integer getNextRequestNumber(){
		Integer nextNumber = requestNumber;
		requestNumber++;
		return nextNumber;
	}
	
	public synchronized void sendMessage(Message m) throws IOException, ClassNotFoundException{
		m.setNumero(getNextRequestNumber());
		oos.writeObject(m);
	}
	
	public class ConnectionListenner implements Runnable {
		ObjectInputStream ois;
		public ConnectionListenner(ObjectInputStream ois) {
			this.ois = ois;
		}
		public void run() {
			Message m;
			while(true){
				try {
					while ((m = (Message) ois.readObject()) != null) {
						switch (m.getMethod()) {
						case ReplyListeMatchs:
							HashMap<Integer, String> matchs = (HashMap<Integer, String>) m.getArgument().get(0);
							for (Map.Entry<Integer, String> entry : matchs.entrySet()) {
							    System.out.println("Match no " + entry.getKey() + " : " + entry.getValue());
							}
							break;

						case ReplyDetailsMatch:
							Match match = (Match) m.getArgument().get(0);
							if(match.getPeriode() == 4)
								System.out.println("Match terminé");
							else{
								System.out.println("Periode : " + match.getPeriode());
								System.out.println("Chrono : " + match.getChronometre());
								for (Penalite p : match.getPenalitesA())
									System.out.println("Penalite equipe A : " + p.joueur + " | "
											+ (match.getChronometre() - p.chronometreLiberation));
								for (Penalite p : match.getPenalitesB())
									System.out.println("Penalite equipe B : " + p.joueur + " | "
											+ (match.getChronometre() - p.chronometreLiberation));
							}
							System.out.println("Score : " + match.getCompteursA().size() + "-" + match.getCompteursB().size());
							for (String s : match.getCompteursA())
								System.out.println("Scoreur equipe A : " + s);
							for (String s : match.getCompteursB())
								System.out.println("Scoreur equipe B : " + s);
							break;

						case RefusPari:
							System.out.println("Pari refusé!");
							break;

						case ConfirmationPari:
							System.out.println("Pari accepté!");
							break;

						case ResultatPari:
							Double gain = (Double)m.getArgument().get(0);
							if(gain == 0)
								System.out.println("Vous avez perdu");
							else
								System.out.println("Vous avez gagné : " + gain);
							Client.endMatch();
							return;

						default:
							break;
						}
						System.out.println("");
					}
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

	}
}
