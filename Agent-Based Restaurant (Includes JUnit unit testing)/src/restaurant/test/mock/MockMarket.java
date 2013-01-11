package restaurant.test.mock;

import java.util.List;

import restaurant.Bill;
import restaurant.CookAgent.FoodData;
import restaurant.interfaces.Market;

public class MockMarket extends MockAgent implements Market{

	public MockMarket (String name){
		super(name);
	}
	
	public EventLog log = new EventLog();
	
	@Override
	public void msgHereIsAnOrder(List<FoodData> needFood) {
		// TODO Auto-generated method stub
		
	}

	public void msgHereIsPayment(Bill b, double dollars) {
		log.add(new LoggedEvent("Received message msgHereIsPayment from cashier for amount "+dollars+"."));
	}

}
