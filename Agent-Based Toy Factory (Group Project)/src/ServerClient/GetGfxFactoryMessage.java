package ServerClient;

import gfx.*;

/* Message to request a Gfx Factory update from the server. */
public class GetGfxFactoryMessage implements MessageToServer {

	/* Send factory state back to the Manager that sent this request. */
	public void doIt(HandleAClient hac) {
		SendGfxFactoryMessage sgfm = new SendGfxFactoryMessage(hac.getServer().getGfxFactory());
		
		try {
			hac.getServer().gfSem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		hac.sendMessage(sgfm);
		hac.getServer().gfSem.release();
	}
}
