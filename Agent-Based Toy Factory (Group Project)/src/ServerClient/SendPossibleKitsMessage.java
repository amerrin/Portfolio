package ServerClient;

import java.util.ArrayList;

/* Message to send possible kits to client. */
public class SendPossibleKitsMessage implements MessageToClient {
	ArrayList<BlueprintKit> kits;
	
	public SendPossibleKitsMessage(ArrayList<BlueprintKit> k) {
		kits = k;
	}
	
	/* Set possible kits for managers with possible kits. */
	public void doIt(HandleAServerConnection hasc) {
		Manager m = hasc.getManager();
		if (m instanceof KitManager) {
			((KitManager)m).setPossibleKits(kits);
		} else if (m instanceof FactoryProductionManager) {
			((FactoryProductionManager)m).setPossibleKits(kits);
		} else if (m instanceof KitAssemblyManager) {
			((KitAssemblyManager)m).setPossibleKits(kits);
		}
	}
}
