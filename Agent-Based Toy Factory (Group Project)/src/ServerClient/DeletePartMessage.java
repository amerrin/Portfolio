package ServerClient;

/* Delete part from possible parts on server. */
public class DeletePartMessage implements MessageToServer {
	BlueprintPart myPart;
	
	public DeletePartMessage(BlueprintPart p) {
		myPart = p;
	}
	
	public void doIt(HandleAClient hac) {
		hac.getServer().removePart(myPart);
	}
}
