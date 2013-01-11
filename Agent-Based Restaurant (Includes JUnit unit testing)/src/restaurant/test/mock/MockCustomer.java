package restaurant.test.mock;

import restaurant.Bill;
import restaurant.Menu;
import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;

public class MockCustomer extends MockAgent implements Customer{

	public MockCustomer (String name){
		super(name);
	}
	
	public EventLog log = new EventLog();
	
	public void setHungry() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFollowMeToTable(Waiter waiter, Menu menu) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDecided() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgWhatWouldYouLike() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsYourFood(String choice, Bill b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEating() {
		// TODO Auto-generated method stub
		
	}

	public void msgHereIsYourChange(double change) {
		log.add(new LoggedEvent("Received message msgHereIsYourChange from cashier for the amount of "+change+"."));
	}

	@Override
	public void msgOutOfOrder(String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNoSeatAvailable() {
		// TODO Auto-generated method stub
		
	}

}
