package restaurant.test.mock;

import restaurant.interfaces.Customer;
import restaurant.interfaces.Waiter;
import restaurant.layoutGUI.Food;

public class MockWaiter extends MockAgent implements Waiter{
	
	public MockWaiter(String name){
		super(name);
	}

	@Override
	public void msgSitCustomerAtTable(Customer customer, int tableNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImReadyToOrder(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsMyChoice(Customer customer, String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderIsReady(int tableNum, Food f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneEatingAndLeaving(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setBreakStatus(boolean state) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIWantSomethingElse(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCancelledOrder(int tableNum, Food f) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOKToBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNOTOKToBreak() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOutOfOrder(String choice, int tableNum) {
		// TODO Auto-generated method stub
		
	}

}
