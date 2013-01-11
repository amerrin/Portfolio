package restaurant.interfaces;

import restaurant.Bill;
import restaurant.Menu;


public interface Customer {

	public abstract void setHungry();
	public abstract void msgFollowMeToTable(Waiter waiter, Menu menu);
	public abstract void msgDecided();
	public abstract void msgWhatWouldYouLike();
	public abstract void msgHereIsYourFood(String choice,Bill b);
	public abstract void msgDoneEating();
	public abstract void msgHereIsYourChange(double change);
	public abstract void msgOutOfOrder(String choice);
	public abstract void msgNoSeatAvailable();
	
}
