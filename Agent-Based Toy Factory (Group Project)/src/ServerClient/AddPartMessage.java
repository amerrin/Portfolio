package ServerClient;

/* Add a part to possible parts on Server. */
public class AddPartMessage implements MessageToServer {
	private BlueprintPart myPart;
	
	public AddPartMessage(BlueprintPart p) {
		myPart = p;
	}
	
	public void doIt(HandleAClient hac) {
		hac.getServer().addPart(myPart);
	}
}

//created by Even Brown
