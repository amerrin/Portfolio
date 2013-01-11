package ServerClient;

/* Message to request possible parts from server. */
public class GetPossiblePartsMessage implements MessageToServer {
	public void doIt(HandleAClient hac) {
		hac.sendMessage(new SendPossiblePartsMessage(hac.getServer().getPossibleParts()));
	}
}
