package ServerClient;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

//use this class to act as a server and pass messages to your manager
public class UTManager {

	public static void main(String[] args) {
		try {
			//test in and out
			ServerSocket ss = new ServerSocket(Manager.PORT);
			Socket socket = ss.accept();
			System.out.println("Socket connection accepted in TestManager");
			ObjectOutputStream testOutputStream  = new ObjectOutputStream(socket.getOutputStream());
			ObjectInputStream testInputStream = new ObjectInputStream(socket.getInputStream());
			
			//receive
			MessageToServer msg = (MessageToServer)testInputStream.readObject();
			msg.doIt(null);
			//send
			testOutputStream.reset();
			testOutputStream.flush();
			
			testOutputStream.writeObject(new PrintsStringMessageToClient("Second Message, from UTManager"));
		} catch(Exception ex) {
			ex.printStackTrace();
		}
	}
}


//written by Alex Jones