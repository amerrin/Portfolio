package ServerClient;

/* Test message class to print string on server. */
public class PrintsStringMessageToServer implements MessageToServer {
	String myString;
	
	public  PrintsStringMessageToServer (String s) {
		if (s!= null) myString = s;
		else myString = new String("No message set");
	}
	
	public void doIt(HandleAClient hac) {
		System.out.println(myString);
	}
}

//written by Alex Jones
