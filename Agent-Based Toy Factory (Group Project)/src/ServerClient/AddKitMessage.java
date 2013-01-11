package ServerClient;

/* Add a kit to possible kits on Server. */
public class AddKitMessage implements MessageToServer {
	private BlueprintKit myKit;
	
	public AddKitMessage(BlueprintKit k) {
		myKit = k;
	}
	
	public void doIt(HandleAClient hac) {
		hac.getServer().addKit(myKit);
	}
}

//created by Evan Brown
