package ServerClient;

public class ClientDoneMessage implements MessageToServer {

		public void doIt(HandleAClient hac) {
			System.out.println("Received exit message from client, ending thread.");
			hac.setExitThreadFlag(true);
		}
}
//written by Alex Jones
