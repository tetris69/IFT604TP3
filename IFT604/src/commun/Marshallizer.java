package commun;

import java.io.*;

public class Marshallizer {

	static public byte[] marshallize(Message message) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		byte[] serialized = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(message);
			serialized = bos.toByteArray();
		}  catch (IOException ex) {
			// ignore close exception
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return serialized;
	}

	public static Message unmarshall(byte[] data) {
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInput in = null;
		Message m = null;
		try {
			in = new ObjectInputStream(bis);
			m = (Message)in.readObject();
		} catch (IOException ex) {
			// ignore close exception
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return m;
	}
}
