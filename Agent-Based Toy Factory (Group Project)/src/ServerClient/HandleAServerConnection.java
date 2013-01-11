package ServerClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;

/* This class is a thread that runs on each Manager and handles the connection to the Server. */
public class HandleAServerConnection implements Runnable {
	Socket mySocket;
	ObjectOutputStream oos;
	ObjectInputStream ois;
	Manager myManager;
	
	/* Initialize variables and open streams. */
	public HandleAServerConnection(Socket s, Manager m) {
		mySocket = s;
		myManager = m;
		
		try {
			//Create the 2 streams for talking to the player client
			oos = new ObjectOutputStream( mySocket.getOutputStream() );
			ois = new ObjectInputStream( mySocket.getInputStream() );
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/* Receive and process requests. */
	public void run() {
		while( true ) {
			// Wait for the client to send a request and then process it
			receiveMessage();
		}
	}
	
	/* This method sends a MessageToServer. */
	public void sendMessage(MessageToServer mts) {
		try {
			oos.writeObject(mts);
			oos.reset();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/* This method receives a MessageToClient and processes it. */
	public void receiveMessage() {
		try {
			MessageToClient mtc = (MessageToClient)ois.readObject();
			mtc.doIt(this);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	/* Getter for Manager. */
	public Manager getManager() {
		return myManager;
	}
}
