package ServerClient;

import java.io.*;

public interface MessageToClient extends Serializable {
	public void doIt(HandleAServerConnection hasc);
}

//written by Alex Jones