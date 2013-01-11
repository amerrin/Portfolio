package restaurant.interfaces;

public interface Host{
	
	public abstract void msgIWantToEat(Customer customer);
	public abstract void msgTableIsFree(int tableNum);
	public abstract void msgIWantToGoOnBreak(Waiter waiter);
	public abstract void msgIAmOffBreak(Waiter waiter);
	public abstract void msgImNotWaiting(Customer c);
	public abstract void addCustomerBlacklist(Customer c);
}
