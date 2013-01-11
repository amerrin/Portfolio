package ServerClient;

/* Message to stop factory production on server. */
public class StopProductionMessage implements MessageToServer {
	
	public void doIt(HandleAClient hac) {
		hac.getServer().stopProduction();
	}
}
