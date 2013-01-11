package restaurant.interfaces;

import java.util.List;
import restaurant.Bill;
import restaurant.CookAgent.FoodData;

public interface Cook {
	
	public abstract void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice);
	public abstract void msgPleaseCancelOrder(Waiter waiter,int tableNum);	
	public abstract void msgOrderFulfilled(List<FoodData> foodList,Market m,Bill b);	
	public abstract void msgStockIsLow();
	public abstract void msgCannotFulfillOrder(Market m);
	
}
