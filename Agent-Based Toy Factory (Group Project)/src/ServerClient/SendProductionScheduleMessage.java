package ServerClient;

import java.util.ArrayList;

/* Send production schedule to Factory Production Manager. */
public class SendProductionScheduleMessage implements MessageToClient {
	ArrayList<Order> ps;
	
	/* Initialize ps. */
	public SendProductionScheduleMessage(ArrayList<Order> orders) {
		ps = orders;
	}
	
	/* Set production schedule on Factory Production Manager. 
	 * Or send kit currently being made to Kit Assembly Manager 
	 */
	public void doIt(HandleAServerConnection hasc) {
		Manager m = hasc.getManager();
		if (m instanceof FactoryProductionManager) {
			FactoryProductionManager fpm = (FactoryProductionManager)m;
			fpm.setProductionSchedule(ps);
			fpm.updateProductionSchedule();
		} else if (m instanceof KitAssemblyManager) {
			KitAssemblyManager kam = (KitAssemblyManager)m;
			//sends the kit ID of the kit with the 0th index in Prod. Schedule
			if (!ps.isEmpty()) {
				kam.setCurrentKitConfig(ps.get(0).kID);
			}
		}
	}
}

//implemeted Evan Brown, Yinog Dai, Michael Bock
