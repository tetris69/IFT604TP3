package webServer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import commun.Match;
import commun.Message;
import commun.Penalite;
import commun.Message.Method;
import server.Bet;
import server.LNH;

public class WebServerConnection {
	Socket socket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Integer requestNumber = 0;
	Thread threadListen;
	HashMap<Integer, Thread> waitingThreads;
	HashMap<Integer, Message> waitingMessages;

	public WebServerConnection(String server, Integer port) throws IOException {
		socket = new Socket(server, port);
		oos = new ObjectOutputStream(socket.getOutputStream());
		ois = new ObjectInputStream(socket.getInputStream());
		
		waitingThreads = new HashMap<Integer, Thread>();
		waitingMessages = new HashMap<Integer, Message>();
		
		threadListen = new Thread(new ConnectionListenner(ois));
		threadListen.start();
	}

	protected void finalize() throws IOException {
		socket.close();
	}

	private synchronized Integer getNextRequestNumber() {
		Integer nextNumber = requestNumber;
		requestNumber++;
		return nextNumber;
	}

	public Message executeRequest(Message m) {
		Integer numero = sendMessage(m);
		return getResponse(numero);
	}

	private synchronized Integer sendMessage(Message m) {
		m.setNumero(getNextRequestNumber());
		try {
			oos.writeObject(m);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return m.getNumero();
	}

	private Message getResponse(Integer numero) {
		Message response;
		if (!waitingMessages.containsKey(numero)) {
			synchronized (Thread.currentThread()) {
				waitingThreads.put(numero, Thread.currentThread());
				// Wait for response
				try {
					System.out.println("wait"+numero);
					Thread.currentThread().wait();
					System.out.println("after wait :)"+numero);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				waitingThreads.remove(numero);
			}
		}
		response = waitingMessages.get(numero);
		waitingMessages.remove(numero);
		return response;
	}

	public class ConnectionListenner implements Runnable {
		ObjectInputStream ois;

		public ConnectionListenner(ObjectInputStream ois) {
			this.ois = ois;
		}

		public void run() {
			Message m;
			while (true) {
				try {
					while ((m = (Message) ois.readObject()) != null) {
						waitingMessages.put(m.getNumero(), m);
						if (waitingThreads.containsKey(m.getNumero())) {
							synchronized (waitingThreads.get(m.getNumero())) {
								System.out.println("Before notify"+m.getNumero());
								waitingThreads.get(m.getNumero()).notify();
								System.out.println("after notify"+m.getNumero());
							}
						}
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
