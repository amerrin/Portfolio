package ServerClient;

import java.net.*;
import java.io.*;
import gfx.*;

//This class will be a thread which runs on the 
//server and handles the connection to one Manager client.
public class HandleAClient implements Runnable {
	Socket mySocket; //socket to connect to client
	Server myServer; //reference to the server
	ObjectInputStream inputStream;
	ObjectOutputStream outputStream;
	boolean exitThreadFlag = false;
	
	/* Initialize variables and open streams. */
	public HandleAClient(Server server, Socket socket) {
	 	exitThreadFlag = false;
		myServer = server;
		mySocket = socket;
		try {
			inputStream = new ObjectInputStream(mySocket.getInputStream());
			outputStream = new ObjectOutputStream(mySocket.getOutputStream());
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/* Receive messages until it is time to exit. */
	public void run() {
		System.out.println("HAC thread running");
		
		do {
			exitThreadFlag = false;
			receiveMessage();
			//a message must change exitThreadFlag to false for thread to exit.
			
		} while (!exitThreadFlag);
		System.out.println("Thread is done.");
	}
	
	/* Receive a message from the client and call the doIt method. */
	public void receiveMessage() {
		try {
			Object msg = (Object)inputStream.readObject();
			//System.out.println("Debug: message recieved.");
			if (msg instanceof MessageToServer) {
				//do message actions upon this manager
				((MessageToServer)msg).doIt(this);
			} else {
				System.out.println("Error: Wrong message recieved, not of MessageToServer type.");
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	/* Send a message to the client. */
	public void sendMessage(MessageToClient msg) {
		try {
			outputStream.reset();
			outputStream.flush();
			outputStream.writeObject(msg);
			outputStream.flush();
			outputStream.reset();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(0);
		}
	}
	
	public void setExitThreadFlag(boolean b) {exitThreadFlag = b;}	
	
	//getters
	public Server getServer() {
		return myServer;
	}
}
