package ServerClient;

/* Delete kit from possible kits on server. */
public class DeleteKitMessage implements MessageToServer {
	private BlueprintKit myKit;
	
	public DeleteKitMessage(BlueprintKit k) {
		myKit = k;
	}
	
	public void doIt(HandleAClient hac) {
		hac.getServer().removeKit(myKit);
	}
}
