package restaurant.test.mock;

import java.util.ArrayList;
import java.util.List;

import restaurant.interfaces.Customer;
import restaurant.interfaces.Host;
import restaurant.interfaces.Waiter;

public class MockHost extends MockAgent implements Host{
	
	public MockHost(String name){
		super(name);
	}
	
	public List<Customer> blackList = new ArrayList<Customer>();
	
	@Override
	public void msgIWantToEat(Customer customer) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTableIsFree(int tableNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIWantToGoOnBreak(Waiter waiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIAmOffBreak(Waiter waiter) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgImNotWaiting(Customer c) {
		// TODO Auto-generated method stub
		
	}
	
	public void addCustomerBlacklist(Customer customer){
		blackList.add(customer);
	}

}
