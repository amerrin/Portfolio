package restaurant;

import agent.Agent;
import java.util.*;
import restaurant.layoutGUI.*;
import java.awt.Color;

import restaurant.gui.ProducerConsumerMonitor;
import restaurant.interfaces.*;


/** Cook agent for restaurant.
 *  Keeps a list of orders for waiters
 *  and simulates cooking them.
 *  Interacts with waiters only.
 */
public class CookAgent extends Agent implements Cook{

    //List of all the orders
    private List<Order> orders = new ArrayList<Order>();
    private Map<String,FoodData> inventory = Collections.synchronizedMap(new HashMap<String,FoodData>());
    private List<String> foodNames = Collections.synchronizedList(new ArrayList<String>(Arrays.asList("Steak","Chicken","Pizza","Salad")));
    public enum Status {pending, cooking, cancel, done}; // order status
    public enum CookEvent {needStock, nothing};
    private List<CookEvent> events = Collections.synchronizedList(new ArrayList<CookEvent>());
    private List<MarketAgent> markets = Collections.synchronizedList(new ArrayList<MarketAgent>());
    private CashierAgent cashier = null;
    private ProducerConsumerMonitor turnTable = new ProducerConsumerMonitor();
    //Name of the cook
    private String name;

    private boolean orderIsOut = false;
	Map<String,Integer> cookTimes =  Collections.synchronizedMap(new HashMap<String,Integer>());
	
    //Timer for simulation
    Timer timer = new Timer();
    Restaurant restaurant; //Gui layout

    /** Constructor for CookAgent class
     * @param name name of the cook
     */
    public CookAgent(String name, Restaurant restaurant) {
	super();
	
	timer.schedule(new TimerTask() {
		public void run(){
		stateChanged();
		}
	}, 5000,5000);
	
	this.name = name;
	this.restaurant = restaurant;
	//Create the restaurant's inventory.
	inventory.put("Steak",new FoodData("Steak", 5,10 + (int)(Math.random()*30)));
	inventory.put("Chicken",new FoodData("Chicken", 4,10 + (int)(Math.random()*30)));
	inventory.put("Pizza",new FoodData("Pizza", 3,10 + (int)(Math.random()*30)));
	inventory.put("Salad",new FoodData("Salad", 2,10 + (int)(Math.random()*30)));
	cookTimes.put("Steak", 5); cookTimes.put("Chicken",4);cookTimes.put("Pizza",3);cookTimes.put("Salad",2);
	
	printStock();
    }
    /** Private class to store information about food.
     *  Contains the food type, its cooking time, and ...
     */
    public class FoodData {
	String type; //kind of food
	double cookTime;
	int count;
	// other things ...
	
	public FoodData(String type, double cookTime,int count){
	    this.type = type;
	    this.cookTime = cookTime;
	    this.count = count;
	}
	
	public FoodData(String type,int count){
		this.type = type;
		this.cookTime = 0;
		this.count = count;
	}
    }
    /** Private class to store order information.
     *  Contains the waiter, table number, food item,
     *  cooktime and status.
     */
    public class Order {
	public WaiterAgent waiter;
	public int tableNum;
	public String choice;
	public Status status;
	public Food food; //a gui variable

	/** Constructor for Order class 
	 * @param waiter waiter that this order belongs to
	 * @param tableNum identification number for the table
	 * @param choice type of food to be cooked 
	 */
	public Order(Waiter waiter, int tableNum, String choice){
	    this.waiter = (WaiterAgent)waiter;
	    this.choice = choice;
	    this.tableNum = tableNum;
	    this.status = Status.pending;
	}

	/** Represents the object as a string */
	public String toString(){
	    return choice + " for " + waiter ;
	}
    }


    


    // *** MESSAGES ***

    /** Message from a waiter giving the cook a new order.
     * @param waiter waiter that the order belongs to
     * @param tableNum identification number for the table
     * @param choice type of food to be cooked
     */
    public void msgHereIsAnOrder(Waiter waiter, int tableNum, String choice){
	orders.add(new Order(waiter, tableNum, choice));
	stateChanged();
    }
    
    public void msgPleaseCancelOrder(Waiter waiter,int tableNum){
    checkStatusOfOrder(waiter,tableNum);
    stateChanged();
    }
    
    public void msgOrderFulfilled(List<FoodData> foodList,Market m,Bill b){
    synchronized(this){orderIsOut = false;}
	for(FoodData food:foodList){
		synchronized(this){inventory.get(food.type).count += food.count;}
	}
	System.out.println(name+": Kitchen has been restocked from market order.");
	printStock();
	cashier.msgPayMarket(m,b);
    }
    
    public void msgStockIsLow(){
    events.add(CookEvent.needStock);
    synchronized(this){orderIsOut = true;}
    stateChanged();
    }
    
    public void msgCannotFulfillOrder(Market m){
    synchronized(this){orderIsOut = false;}
    events.add(CookEvent.needStock);
    markets.remove(0);markets.add((MarketAgent)m);
    stateChanged();
    }


    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {	
    synchronized(orders){	
	//If there exists an order o whose status is done, place o.
	for(Order o:orders){
	    if(o.status == Status.done){
		placeOrder(o);
		return true;
	    }
	}
	
	for(Order o:orders){
		if(o.status == Status.cancel){
		cancelOrder(o);
		return true;
		}
	}
	
	//If there exists an order o whose status is pending, cook o.
	for(Order o:orders){
	    if(o.status == Status.pending){
		cookOrder(o);
		return true;
	    }
	}}
	synchronized(events){
	for(CookEvent e:events){
		orderFood(e);
		return true;
	}}
	
	Order o = turnTable.remove();
	if(o!=null){
		print("Got order for table "+o.tableNum+" from turntable");
		msgHereIsAnOrder(o.waiter,o.tableNum,o.choice);
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    

    // *** ACTIONS ***
    
    /** Starts a timer for the order that needs to be cooked. 
     * @param order
     */
    
    public void orderFood(CookEvent event){
    	events.remove(event);
    	List<FoodData> needFood = new ArrayList<FoodData>();
		System.out.println(name+": ordering food from market");
    	for(String foodName:foodNames){
    		synchronized(markets){if(inventory.get(foodName).count<10){
    			needFood.add(new FoodData(foodName,cookTimes.get(foodName),(int)(Math.random()*30)));
    			}
    	}
    	markets.get(0).msgHereIsAnOrder(needFood);
    }}
    
    private void cookOrder(Order order){
    synchronized(inventory){
    if(inventory.get(order.choice).count == 0){
    	order.waiter.msgOutOfOrder(order.choice,order.tableNum);
    	orders.remove(order);
    	System.out.println(name+": Out of order for table "+order.tableNum +". Notifying waiter.");
    	if(!orderIsOut){
    		msgStockIsLow();}
    	return;}
	DoCooking(order);
	order.status = Status.cooking;
	inventory.get(order.choice).count -= 1;
	for(String food:foodNames){
		if(inventory.get(food).count < 10 && !orderIsOut){
			msgStockIsLow();
		}}
    }}

    private void placeOrder(Order order){
	DoPlacement(order);
	order.waiter.msgOrderIsReady(order.tableNum, order.food);
	orders.remove(order);
    }

    private void checkStatusOfOrder(Waiter waiter,int tableNum){
    synchronized(orders){
	for(Order o:orders){
    	if(o.waiter == waiter && o.tableNum == tableNum){
    	    print("Checking on status of order for " + tableNum);
    		if(o.status != Status.cooking && o.status != Status.done)
    			o.status = Status.cancel;
    		else{
    			print("too late to cancel order");
    			o.waiter.msgCannotCancelOrder(o.tableNum, o.food);
    		}
    	}
    	}
	}}
    
    private void cancelOrder(Order order){
    //add gui animation
    print("cancelling order for table "+order.tableNum);
    order.waiter.msgCancelledOrder(order.tableNum, order.food);
    orders.remove(order);
    }

    // *** EXTRA -- all the simulation routines***

    /** Returns the name of the cook */
    public String getName(){
        return name;
    }
    
    public void setMarkets(List<MarketAgent> m){
    	this.markets=m;
    }
    
    public void setCashier(CashierAgent c){
    	this.cashier = c;
    }

    private void DoCooking(final Order order){
	print("Cooking:" + order + " for table:" + (order.tableNum+1));
	//put it on the grill. gui stuff
	order.food = new Food(order.choice.substring(0,2),new Color(0,255,255), restaurant);
	order.food.cookFood();
	synchronized(inventory){
	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception    
		order.status = Status.done;
		stateChanged();
	    }
	}, (int)(inventory.get(order.choice).cookTime*1000));
    }}
    public void DoPlacement(Order order){
	print("Order finished: " + order + " for table:" + (order.tableNum+1));
	order.food.placeOnCounter();
    }
    
    private void printStock(){
	System.out.println("Inventory of food in restaurant");
	System.out.println("Steak: " + inventory.get("Steak").count);
	System.out.println("Chicken: " + inventory.get("Chicken").count);
	System.out.println("Pizza: " + inventory.get("Pizza").count);
	System.out.println("Salad: " + inventory.get("Salad").count);
    }
    
    public void setStock(String choice, int amount){
    synchronized(inventory){inventory.get(Character.toUpperCase(choice.charAt(0))+choice.substring(1)).count = amount;}
    System.out.println("Successfully changed supply of "+choice+" to "+amount);
    }
	public void setTurnTable(ProducerConsumerMonitor tt){
		this.turnTable = tt;
	}
}


    
