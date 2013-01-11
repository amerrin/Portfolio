package ServerClient;

/* Message to request a production schedule update from server. */
public class GetProductionScheduleMessage implements MessageToServer {
	
	/* Send production schedule back to the Manager that sent this request. */
	public void doIt(HandleAClient hac) {
		SendProductionScheduleMessage spsm = new SendProductionScheduleMessage(hac.getServer().getOrders());
		
		try {
			hac.getServer().psSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hac.sendMessage(spsm);
		hac.getServer().psSem.release();
	}
}
