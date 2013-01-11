package ServerClient;

public class PrintsStringMessageToClient implements MessageToClient {
	String myString;
	
	public  PrintsStringMessageToClient (String s) {
		if (s!= null)
			myString = s;
		else
			myString = new String("No message set");
	}
	
	public void doIt(HandleAServerConnection hasc) {
		System.out.println(myString);
		
	}

}
//written by Alex Jones
