package ServerClient;

import java.util.ArrayList;

/* Message to send possible parts to client. */
public class SendPossiblePartsMessage implements MessageToClient {
	ArrayList<BlueprintPart> parts;
	
	public SendPossiblePartsMessage(ArrayList<BlueprintPart> p) {
		parts = p;
	}
	
	/* Set possible parts for managers with possible parts. */
	public void doIt(HandleAServerConnection hasc) {
		Manager m = hasc.getManager();
		if (m instanceof PartsManager) {
			((PartsManager)m).setPossibleParts(parts);
		} else if (m instanceof KitManager) {
			((KitManager)m).setPossibleParts(parts);
		}
	}
}
