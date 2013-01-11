package ServerClient;

import java.io.*;

/* Interface for sending a message to from a Manager to the Server. */
public interface MessageToServer extends Serializable {
	public void doIt(HandleAClient hac);
}

//written by Alex Jones
