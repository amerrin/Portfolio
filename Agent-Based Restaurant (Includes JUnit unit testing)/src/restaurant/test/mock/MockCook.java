package restaurant.test.mock;

import java.util.List;

import restaurant.Bill;
import restaurant.CookAgent.FoodData;
import restaurant.interfaces.Cook;
import restaurant.interfaces.Market;
import restaurant.interfaces.Waiter;

public class MockCook extends MockAgent implements Cook{

	public MockCook (String name){
		super(name);
	}
	
	@Override
	public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgPleaseCancelOrder(Waiter waiter, int tableNum) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgOrderFulfilled(List<FoodData> foodList, Market m, Bill b) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgStockIsLow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCannotFulfillOrder(Market m) {
		// TODO Auto-generated method stub
		
	}

}
