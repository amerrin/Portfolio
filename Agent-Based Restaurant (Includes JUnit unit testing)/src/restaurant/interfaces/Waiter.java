package restaurant.interfaces;

import restaurant.layoutGUI.Food;

public interface Waiter {
	
	String name = null;
	public abstract void msgSitCustomerAtTable(Customer customer, int tableNum);
	public abstract void msgImReadyToOrder(Customer customer);
	public abstract void msgHereIsMyChoice(Customer customer, String choice);
	public abstract void msgOrderIsReady(int tableNum, Food f);
    public abstract void msgDoneEatingAndLeaving(Customer customer);
    public abstract void setBreakStatus(boolean state);
    public abstract void msgIWantSomethingElse(Customer customer);
    public abstract void msgCancelledOrder(int tableNum, Food f);
    public abstract void msgOKToBreak();
    public abstract void msgNOTOKToBreak();
    public abstract void msgOutOfOrder(String choice,int tableNum);
    
}
