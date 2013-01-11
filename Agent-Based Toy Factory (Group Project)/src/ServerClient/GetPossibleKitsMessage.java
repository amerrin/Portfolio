package ServerClient;

/* Message to request possible kits from server. */
public class GetPossibleKitsMessage implements MessageToServer {
	public void doIt(HandleAClient hac) {
		hac.sendMessage(new SendPossibleKitsMessage(hac.getServer().getPossibleKits()));
	}
}
