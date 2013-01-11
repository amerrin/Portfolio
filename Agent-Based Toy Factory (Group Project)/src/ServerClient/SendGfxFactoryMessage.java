package ServerClient;

import gfx.GfxFactory;

/* This is a message for sending the Gfx Factory state to a Gfx manager. */
public class SendGfxFactoryMessage implements MessageToClient {
	private GfxFactory myGfxFactory;
	
	public SendGfxFactoryMessage(GfxFactory gf) {
		myGfxFactory = gf;
	}
	
	/* Set local gfx factory then call paint factory method. */
	public void doIt(HandleAServerConnection hasc) {
		Manager m = hasc.getManager();
		if (m instanceof GfxManager) {
			m.setLocalGfxFactory(myGfxFactory);
			m.repaint();
			//System.out.println("Message Debug: Factory received from server and set. Repaint called.");
		} else {
			System.out.println("Message Debug: Error: Server sent GfxFactory to non-GfxManager.");
		}
	}
}
