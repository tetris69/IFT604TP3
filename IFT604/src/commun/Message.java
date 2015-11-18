package commun;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
	private int numero;
	private ArrayList<Object> argument;
	public  Method method;  				// nom de la méthode à invoquer, il n'y a qu'une seule valeur possible getIPFor
	
	public Message(int numero, Method method){
		this.numero = numero;
		this.method = method;
		this.argument = new ArrayList<Object>();
	}

	public Message(Method method){
		this.method = method;
		this.argument = new ArrayList<Object>();
	}


	public enum Method {
		ReplyListeMatchs, ReplyDetailsMatch, ListeMatchs, DetailsMatch, Parier, ConfirmationPari, RefusPari, ResultatPari
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}


	public int getNumero() {
		return numero;
	}

	public void setNumero(int numero) {
		this.numero = numero;
	}

	public ArrayList<Object> getArgument() {
		return argument;
	}

	public void addArgument(Object arg) {
		this.argument.add(arg);
	}
}


