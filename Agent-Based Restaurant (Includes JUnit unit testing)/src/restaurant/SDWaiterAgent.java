package restaurant;

import java.awt.Color;
import java.util.TimerTask;

import restaurant.CookAgent.Order;
import restaurant.WaiterAgent.CustomerState;
import restaurant.gui.ProducerConsumerMonitor;

import restaurant.interfaces.Waiter;
import restaurant.layoutGUI.GuiWaiter;
import restaurant.layoutGUI.Restaurant;
import restaurant.layoutGUI.Table;
import astar.AStarTraversal;
import astar.Position;

public class SDWaiterAgent extends WaiterAgent implements Waiter{
	private ProducerConsumerMonitor turnTable = new ProducerConsumerMonitor();
	
	public SDWaiterAgent(String name, AStarTraversal aStar,
		       Restaurant restaurant, Table[] tables) {
	super(name,aStar,restaurant,tables);
	} 
	
	protected void giveOrderToCook(MyCustomer customer){
		print("Placing "+customer.cmr +"'s choice of "+customer.choice+" on turntable.");
		customer.orderPassed = true;
		customer.state = CustomerState.NO_ACTION;
		turnTable.insert(cook.new Order(this,customer.tableNum,customer.choice));
		stateChanged();
		
		tables[customer.tableNum].takeOrder(customer.choice.substring(0,2)+"?");
		restaurant.placeFood(tables[customer.tableNum].foodX(),
				     tables[customer.tableNum].foodY(),
				     new Color(255, 255, 255), customer.choice.substring(0,2)+"?");
	}
	
	public void setTurnTable(ProducerConsumerMonitor tt){
		this.turnTable = tt;
	}
	
	public void requestOrderChange(final MyCustomer customer){
		print("Attempting to cancel " + customer.cmr.getName()+"'s order");
	    customer.state = CustomerState.NO_ACTION;
	    Order data = turnTable.remove(cook.new Order(this,customer.tableNum,customer.choice));
	    if (customer.orderDone){
	    	customer.cmr.msgCannotChangeOrder();
	    	return;
	    }
	    else if(data.tableNum == -1){
	    	cook.msgPleaseCancelOrder(this, customer.tableNum);
	    }
	    else
	    	timer.schedule(new TimerTask() {
	    	    public void run() {
	    	    print("Allowing "+customer.cmr.getName()+" to re-order");
	    		reTakeOrder(customer);   
	    	    }},
	    	    500);
	    stateChanged();
	}
}
