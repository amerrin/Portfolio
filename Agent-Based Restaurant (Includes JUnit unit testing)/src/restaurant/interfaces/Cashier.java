package restaurant.interfaces;

import restaurant.Bill;

public interface Cashier {

	public abstract void msgHereIsMyMoney(Bill b,double dollars);
	public abstract void msgPayMarket(Market m,Bill b);
	
}
