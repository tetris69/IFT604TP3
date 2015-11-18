package webServer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import client.ClientConnection;
import commun.Match;
import commun.Message;
import commun.Message.Method;

import com.sun.net.httpserver.Headers;

public class WebServer {
	static WebServerConnection cc;
	public static void main(String[] args) throws Exception {
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/list", new HTMLHandler("list.html"));
		server.createContext("/match/", new HTMLHandler("match.html"));
		server.createContext("/api/list", new APIListHandler());
		server.createContext("/api/match/", new APIMatchHandler());		

		cc = new WebServerConnection("localhost", 2015);

		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class HTMLHandler implements HttpHandler {
		String filename;
		public HTMLHandler(String filename){
			this.filename = filename;
		}
		public void handle(HttpExchange t) throws IOException {
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", "text/html");
			File file = new File(filename);
			if (!file.isFile())
				return;
			byte[] bytearray = new byte[(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);

			// ok, we are ready to send the response.
			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray, 0, bytearray.length);
			os.close();
		}
	}

	static class APIListHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {			
			Message m = new Message(Method.ListeMatchs);
			// Results
			Message r = cc.executeRequest(m);
			HashMap<Integer, String> matchs = (HashMap<Integer, String>) r.getArgument().get(0);
			String response = new JSONObject(matchs).toString();
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class APIMatchHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {			
			Message m = new Message(Method.DetailsMatch);
			String uri = t.getRequestURI().toString();
			String[] uriParts = uri.split("/");
			Integer matchId = Integer.parseInt(uriParts[uriParts.length - 1]);
			m.addArgument(matchId);
			// Results
			Message r = cc.executeRequest(m);
			Match match = (Match) r.getArgument().get(0);
			
			//TODO : Trouver un façon d'envoyer également les objets pénalités qui sont dans l'objet match			
			JSONObject lol = new JSONObject(match);
			String response = lol.toString();
			t.sendResponseHeaders(200, response.length());
			OutputStream os = t.getResponseBody();
			os.write(response.getBytes());
			os.close();
		}
	}

	static class MatchHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			
		}
	}
}