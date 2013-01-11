package ServerClient;

public class SendOrderMessage implements MessageToServer {
	Order o;
	
	public SendOrderMessage(Order order) {
		o = order;
	}
	
	public void doIt(HandleAClient hac) {
		hac.getServer().addOrder(o);
	}
}
