package server;

import java.util.Hashtable;

public class Cache {
	
	Hashtable<String, String> table = new Hashtable<String, String>();
	
	synchronized public void put(String key, String value) {
		table.put(key, value);
	}

	synchronized public String get(String key) {
		return table.get(key);
	}

	synchronized public boolean containsKey(String key) {
		return table.containsKey(key);
	}
}
