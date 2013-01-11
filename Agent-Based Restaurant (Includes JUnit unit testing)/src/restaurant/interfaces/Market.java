package restaurant.interfaces;

import java.util.List;

import restaurant.Bill;
import restaurant.CookAgent.FoodData;

public interface Market {

	public abstract void msgHereIsAnOrder(List<FoodData> needFood);
	public abstract void msgHereIsPayment(Bill b,double dollars);
	public abstract String getName();

}
