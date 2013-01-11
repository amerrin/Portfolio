package restaurant.test.mock;

import restaurant.Bill;
import restaurant.interfaces.Cashier;
import restaurant.interfaces.Market;

public class MockCashier extends MockAgent implements Cashier{
	
	public MockCashier(String name){
		super(name);
	}
	
	public EventLog log = new EventLog();
	
	public void msgHereIsMyMoney(Bill b, double dollars) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPayMarket(Market m, Bill b) {
		// TODO Auto-generated method stub
		
	}

}
