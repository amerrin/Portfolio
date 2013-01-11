package restaurant;
import java.awt.Color;
import restaurant.interfaces.*;
import restaurant.gui.RestaurantGui;
import restaurant.layoutGUI.*;
import agent.Agent;

import java.util.Collections;
import java.util.Timer;
import java.util.TimerTask;
import astar.*;
import java.util.*;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/** Restaurant Waiter Agent.
 * Sits customers at assigned tables and takes their orders.
 * Takes the orders to the cook and then returns them 
 * when the food is done.  Cleans up the tables after the customers leave.
 * Interacts with customers, host, and cook */
public class WaiterAgent extends Agent implements Waiter{

   //State variables for Waiter
    protected boolean onBreak = false;

    //State constants for Customers

    public enum CustomerState 
	{NEED_SEATED,READY_TO_ORDER,ORDER_PENDING,ORDER_READY,IS_DONE,ORDER_CHANGE,NO_ACTION};
	public enum BreakStatus {YES,NO,PENDING};
	public enum WaiterEvent {BREAK, NOTHING};
	public BreakStatus breakState = BreakStatus.PENDING;

    Timer timer = new Timer();

    /** protected class to hold information for each customer.
     * Contains a reference to the customer, his choice, 
     * table number, and state */
    protected class MyCustomer {
	public CustomerState state;
	public CustomerAgent cmr;
	public String choice;
	public int tableNum;
	public boolean orderPassed;
	public boolean orderDone;
	public Food food; //gui thing

	/** Constructor for MyCustomer class.
	 * @param cmr reference to customer
	 * @param num assigned table number */
	public MyCustomer(CustomerAgent cmr, int num){
	    this.cmr = cmr;
	    tableNum = num;
	    state = CustomerState.NO_ACTION;
	    orderPassed = false;
	    orderDone = false;
	}
    }

    //Name of waiter
    public String name;

    //All the customers that this waiter is serving
    protected List<MyCustomer> customers = Collections.synchronizedList(new ArrayList<MyCustomer>());
    protected List<WaiterEvent> events = Collections.synchronizedList(new ArrayList<WaiterEvent>());
    protected HostAgent host;
    protected CookAgent cook;
    protected CashierAgent cashier;
    protected Semaphore multiStepSema = new Semaphore(0);
    
    

    //Animation Variables
    AStarTraversal aStar;
    Restaurant restaurant; //the gui layout
    GuiWaiter guiWaiter; 
    Position currentPosition; 
    Position originalPosition;
    Table[] tables; //the gui tables
    

    /** Constructor for WaiterAgent class
     * @param name name of waiter
     * @param gui reference to the gui */
    public WaiterAgent(String name, AStarTraversal aStar,
		       Restaurant restaurant, Table[] tables) {
	super();

	this.name = name;

	//initialize all the animation objects
	this.aStar = aStar;
	this.restaurant = restaurant;//the layout for astar
	guiWaiter = new GuiWaiter(name.substring(0,2), new Color(255, 0, 0), restaurant);
	currentPosition = new Position(guiWaiter.getX(), guiWaiter.getY());
        currentPosition.moveInto(aStar.getGrid());
	originalPosition = currentPosition;//save this for moving into
	this.tables = tables;
    } 

    // *** MESSAGES ***

    /** Host sends this to give the waiter a new customer.
     * @param customer customer who needs seated.
     * @param tableNum identification number for table */
    public void msgSitCustomerAtTable(Customer customer, int tableNum){
	MyCustomer c = new MyCustomer((CustomerAgent)customer, tableNum);
	c.state = CustomerState.NEED_SEATED;
	customers.add(c);
	stateChanged();
    }

    /** Customer sends this when they are ready.
     * @param customer customer who is ready to order.
     */
    public void msgImReadyToOrder(Customer customer){
	//print("received msgImReadyToOrder from:"+customer);
	/*for(int i=0; i < customers.size(); i++){
	    //if(customers.get(i).cmr.equals(customer)){
	    if (customers.get(i).cmr == customer){
		customers.get(i).state = CustomerState.READY_TO_ORDER;
		stateChanged();
		return;
	    }
	}
	System.out.println("msgImReadyToOrder in WaiterAgent, didn't find him?");*/
    multiStepSema.release();
    }

    /** Customer sends this when they have decided what they want to eat 
     * @param customer customer who has decided their choice
     * @param choice the food item that the customer chose */
    public void msgHereIsMyChoice(Customer customer, String choice){
	for(MyCustomer c:customers){
	    if(c.cmr.equals(customer)){
		c.choice = choice;
		//c.state = CustomerState.ORDER_PENDING;
		//stateChanged();
		//return;
	    multiStepSema.release();
	    }
	}
    }

    /** Cook sends this when the order is ready.
     * @param Num identification number of table whose food is ready
     * @param f is the guiFood object */
    public void msgOrderIsReady(int tableNum, Food f){
	for(MyCustomer c:customers){
	    if(c.tableNum == tableNum){
	    c.orderDone = true;
	    c.cmr.forceDisable = true;c.cmr.gui.toggleReorderEnabled(c.cmr); //this line is a hack for gui option
		c.state = CustomerState.ORDER_READY;
		c.food = f; //so that later we can remove it from the table.
		stateChanged();
		return;
	    }
	}
    }

    /** Customer sends this when they are done eating.
     * @param customer customer who is leaving the restaurant. */
    public void msgDoneEatingAndLeaving(Customer customer){
	for(MyCustomer c:customers){
	    if(c.cmr.equals(customer)){
		c.state = CustomerState.IS_DONE;
		stateChanged();
		return;
	    }
	}
    }

    /** Sent from GUI to control breaks 
     * @param state true when the waiter should go on break and 
     *              false when the waiter should go off break
     *              Is the name onBreak right? What should it be?*/
    public void setBreakStatus(boolean state){
	onBreak = state;
	events.add(WaiterEvent.BREAK);
	stateChanged();
    }

    public void msgIWantSomethingElse(Customer customer) {
    for(MyCustomer c:customers){
    	if(c.cmr == customer){
        c.state = CustomerState.ORDER_CHANGE;
        stateChanged();
    	}}
    }
    
    public void msgCancelledOrder(int tableNum, Food f){
	for(MyCustomer c:customers){
	    if(c.tableNum == tableNum){
		c.state = CustomerState.READY_TO_ORDER;
		c.food = f; //so that later we can remove it from the table.
		stateChanged();
		return;
	    }
	}}
    
    public void msgCannotCancelOrder(int tableNum,Food f){
    for(MyCustomer c:customers){
    	if(c.tableNum == tableNum){
    	print("Informing "+c.cmr.getName()+" he may not reorder");
    	c.state = CustomerState.NO_ACTION;
    	c.cmr.msgCannotChangeOrder();
    	c.food = f;
    	stateChanged();
    	return;
    	}
    }
    }
    
    public void msgOKToBreak(){
    breakState = BreakStatus.YES;
    stateChanged();
    }
    
    public void msgNOTOKToBreak(){
    breakState = BreakStatus.NO;
    stateChanged();
    }
    
    public void msgOutOfOrder(String choice,int tableNum){
    for(MyCustomer c:customers){
    	if(c.tableNum == tableNum){
    	System.out.println(name+": notifying customer to re-order");
    	c.cmr.msgOutOfOrder(choice);
    	stateChanged();
    	}
    }
    }

    /** Scheduler.  Determine what action is called for, and do it. */
    public boolean pickAndExecuteAnAction() {
	//print("in waiter scheduler");

    synchronized(events){	
	if(!events.isEmpty()){
	AskHostForBreak();
	return true;
	}}
    	
    synchronized(customers){
	//Runs through the customers for each rule, so 
	//the waiter doesn't serve only one customer at a time
	if(!customers.isEmpty()){
	    //System.out.println("in scheduler, customers not empty:");
	    //Gives food to customer if the order is ready
	    for(MyCustomer c:customers){
	    if(c.state == CustomerState.ORDER_CHANGE){
	    	requestOrderChange(c);
	    	return true;
	    }
	    }
		
		for(MyCustomer c:customers){
		if(c.state == CustomerState.ORDER_READY) {
		    giveFoodToCustomer(c);
		    return true;
		}
	    }
	    //Clears the table if the customer has left
	    for(MyCustomer c:customers){
		if(c.state == CustomerState.IS_DONE) {
		    clearTable(c);
		    return true;
		}
	    }

	    //Seats the customer if they need it
	    for(MyCustomer c:customers){
		if(c.state == CustomerState.NEED_SEATED){
		    seatCustomer(c);
		    return true;
		}
	    }

	    //Gives all pending orders to the cook
	    for(MyCustomer c:customers){
		if(c.state == CustomerState.ORDER_PENDING){
		    giveOrderToCook(c);
		    return true;
		}
	    }

	    //Takes new orders for customers that are ready
	    for(MyCustomer c:customers){
		//print("testing for ready to order"+c.state);
		if(c.state == CustomerState.READY_TO_ORDER) {
		    takeOrder(c);
		    return true;
		}
	    }
	    
	}}
    
	if(isOnBreak() && customers.isEmpty()){
	    if(breakState == BreakStatus.YES){
	    	TakeBreak();
	    	return true;}
	    if(breakState == BreakStatus.NO){
	    	CancelBreak();
	    	return true;}
	}
	    
	if (!currentPosition.equals(originalPosition)) {
	    DoMoveToOriginalPosition();//Animation thing
	    return true;
	}

	//we have tried all our rules and found nothing to do. 
	// So return false to main loop of abstract agent and wait.
	//print("in scheduler, no rules matched:");
	return false;
    }

    // *** ACTIONS ***
    
    protected void AskHostForBreak(){
    System.out.println(name+": asking host for a break.");
    host.msgIWantToGoOnBreak(this);
    synchronized(events){events.remove(0);}
    stateChanged();
    }
    
    protected void TakeBreak(){
    final WaiterAgent w = this;
    breakState = BreakStatus.PENDING;
    System.out.println(name+": taking break now");
    timer.schedule(new TimerTask() {
	    public void run() {  
	    onBreak = false;
	    System.out.println(name+": returning from break");	
		host.msgIAmOffBreak(w);		    
	    }},
	    10000);//how long to wait before running task
    stateChanged();
    }
    
    protected void CancelBreak(){
    breakState = BreakStatus.PENDING;
    System.out.println(name+": denied break - going back to work");
    onBreak = false;
    stateChanged();
    }
    
    /** Seats the customer at a specific table 
     * @param customer customer that needs seated */
    protected void seatCustomer(MyCustomer customer) {
	DoSeatCustomer(customer); //animation	
	customer.state = CustomerState.NO_ACTION;
	customer.cmr.msgFollowMeToTable(this, new Menu());
	DoMoveToOriginalPosition();
	try {
	if(multiStepSema.tryAcquire(10000,TimeUnit.MILLISECONDS))
		takeOrder(customer);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
    }
    /** Takes down the customers order 
     * @param customer customer that is ready to order */
    protected void takeOrder(MyCustomer customer) {
	DoTakeOrder(customer); //animation
	customer.state = CustomerState.NO_ACTION;
	customer.cmr.msgWhatWouldYouLike();
	try {
	if(multiStepSema.tryAcquire(10000,TimeUnit.MILLISECONDS))
		giveOrderToCook(customer);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	//stateChanged();
    }
    protected void reTakeOrder(MyCustomer customer) {
	DoTakeOrder(customer); //animation
	customer.state = CustomerState.NO_ACTION;
	customer.cmr.msgWhatWouldYouLikeAgain();
	try {
	if(multiStepSema.tryAcquire(10000,TimeUnit.MILLISECONDS))
		giveOrderToCook(customer);
	} catch (InterruptedException e) {
		e.printStackTrace();
	}
	//stateChanged();
    }

    /** Gives any pending orders to the cook 
     * @param customer customer that needs food cooked */
    protected void giveOrderToCook(MyCustomer customer) {
	//In our animation the waiter does not move to the cook in
	//order to give him an order. We assume some sort of electronic
	//method implemented as our message to the cook. So there is no
	//animation analog, and hence no DoXXX routine is needed.
	print("Giving " + customer.cmr + "'s choice of " + customer.choice + " to cook");

	customer.orderPassed = true;
	customer.state = CustomerState.NO_ACTION;
	cook.msgHereIsAnOrder(this, customer.tableNum, customer.choice);
	stateChanged();
	
	//Here's a little animation hack. We put the first two
	//character of the food name affixed with a ? on the table.
	//Simply let's us see what was ordered.
	tables[customer.tableNum].takeOrder(customer.choice.substring(0,2)+"?");
	restaurant.placeFood(tables[customer.tableNum].foodX(),
			     tables[customer.tableNum].foodY(),
			     new Color(255, 255, 255), customer.choice.substring(0,2)+"?");
    }

    /** Gives food to the customer 
     * @param customer customer whose food is ready */
    protected void giveFoodToCustomer(MyCustomer customer) {
	DoGiveFoodToCustomer(customer);//Animation
	customer.state = CustomerState.NO_ACTION;
	Bill b = new Bill(customer.choice,Menu.priceMap.get(customer.choice),customer.cmr);
	customer.cmr.msgHereIsYourFood(customer.choice,b);
	stateChanged();
    }
    /** Starts a timer to clear the table 
     * @param customer customer whose table needs cleared */
    protected void clearTable(MyCustomer customer) {
	DoClearingTable(customer);
	customer.state = CustomerState.NO_ACTION;
	stateChanged();
    }
    
    protected void requestOrderChange(final MyCustomer customer){
    	//add gui animation
    print("Attempting to cancel " + customer.cmr.getName()+"'s order");
    customer.state = CustomerState.NO_ACTION;
    if(customer.orderPassed)
    	cook.msgPleaseCancelOrder(this, customer.tableNum);
    else
    	timer.schedule(new TimerTask() {
    	    public void run() {
    	    print("Allowing "+customer.cmr.getName()+" to re-order");
    		msgImReadyToOrder(customer.cmr);    
    	    }},
    	    500);
    stateChanged();
    }
    
    

    // Animation Actions
    void DoSeatCustomer (MyCustomer customer){
	print("Seating " + customer.cmr + " at table " + (customer.tableNum+1));
	//move to customer first.
	GuiCustomer guiCustomer = customer.cmr.getGuiCustomer();
	guiMoveFromCurrentPostionTo(new Position(guiCustomer.getX()+1,guiCustomer.getY()));
	guiWaiter.pickUpCustomer(guiCustomer);
	Position tablePos = new Position(tables[customer.tableNum].getX()-1,
					 tables[customer.tableNum].getY()+1);
	guiMoveFromCurrentPostionTo(tablePos);
	guiWaiter.seatCustomer(tables[customer.tableNum]);
    }
    void DoTakeOrder(MyCustomer customer){
	print("Taking " + customer.cmr +"'s order.");
	Position tablePos = new Position(tables[customer.tableNum].getX()-1,
					 tables[customer.tableNum].getY()+1);
	guiMoveFromCurrentPostionTo(tablePos);
    }
    void DoGiveFoodToCustomer(MyCustomer customer){
	print("Giving finished order of " + customer.choice +" to " + customer.cmr);
	Position inFrontOfGrill = new Position(customer.food.getX()-1,customer.food.getY());
	guiMoveFromCurrentPostionTo(inFrontOfGrill);//in front of grill
	guiWaiter.pickUpFood(customer.food);
	Position tablePos = new Position(tables[customer.tableNum].getX()-1,
					 tables[customer.tableNum].getY()+1);
	guiMoveFromCurrentPostionTo(tablePos);
	guiWaiter.serveFood(tables[customer.tableNum]);
    }
    void DoClearingTable(final MyCustomer customer){
	print("Clearing table " + (customer.tableNum+1) + " (1500 milliseconds)");
	timer.schedule(new TimerTask(){
	    public void run(){		    
		endCustomer(customer);
	    }
	}, 1500);
    }
    /** Function called at the end of the clear table timer
     * to officially remove the customer from the waiter's list.
     * @param customer customer who needs removed from list */
    protected void endCustomer(MyCustomer customer){ 
	print("Table " + (customer.tableNum+1) + " is cleared!");
	customer.food.remove(); //remove the food from table animation
	host.msgTableIsFree(customer.tableNum);
	customers.remove(customer);
	stateChanged();
    }
    protected void DoMoveToOriginalPosition(){
	print("Nothing to do. Moving to original position="+originalPosition);
	guiMoveFromCurrentPostionTo(originalPosition);
    }

    //this is just a subroutine for waiter moves. It's not an "Action"
    //itself, it is called by Actions.
    void guiMoveFromCurrentPostionTo(Position to){
	//System.out.println("[Gaut] " + guiWaiter.getName() + " moving from " + currentPosition.toString() + " to " + to.toString());

	AStarNode aStarNode = (AStarNode)aStar.generalSearch(currentPosition, to);
	List<Position> path = aStarNode.getPath();
	Boolean firstStep   = true;
	Boolean gotPermit   = true;

	for (Position tmpPath: path) {
	    //The first node in the path is the current node. So skip it.
	    if (firstStep) {
		firstStep   = false;
		continue;
	    }

	    //Try and get lock for the next step.
	    int attempts    = 1;
	    gotPermit       = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());

	    //Did not get lock. Lets make n attempts.
	    while (!gotPermit && attempts < 3) {
		//System.out.println("[Gaut] " + guiWaiter.getName() + " got NO permit for " + tmpPath.toString() + " on attempt " + attempts);

		//Wait for 1sec and try again to get lock.
		try { Thread.sleep(1000); }
		catch (Exception e){}

		gotPermit   = new Position(tmpPath.getX(), tmpPath.getY()).moveInto(aStar.getGrid());
		attempts ++;
	    }

	    //Did not get lock after trying n attempts. So recalculating path.            
	    if (!gotPermit) {
		//System.out.println("[Gaut] " + guiWaiter.getName() + " No Luck even after " + attempts + " attempts! Lets recalculate");
		guiMoveFromCurrentPostionTo(to);
		break;
	    }

	    //Got the required lock. Lets move.
	    //System.out.println("[Gaut] " + guiWaiter.getName() + " got permit for " + tmpPath.toString());
	    currentPosition.release(aStar.getGrid());
	    currentPosition = new Position(tmpPath.getX(), tmpPath.getY ());
	    guiWaiter.move(currentPosition.getX(), currentPosition.getY());
	}
	/*
	boolean pathTaken = false;
	while (!pathTaken) {
	    pathTaken = true;
	    //print("A* search from " + currentPosition + "to "+to);
	    AStarNode a = (AStarNode)aStar.generalSearch(currentPosition,to);
	    if (a == null) {//generally won't happen. A* will run out of space first.
		System.out.println("no path found. What should we do?");
		break; //dw for now
	    }
	    //dw coming. Get the table position for table 4 from the gui
	    //now we have a path. We should try to move there
	    List<Position> ps = a.getPath();
	    Do("Moving to position " + to + " via " + ps);
	    for (int i=1; i<ps.size();i++){//i=0 is where we are
		//we will try to move to each position from where we are.
		//this should work unless someone has moved into our way
		//during our calculation. This could easily happen. If it
		//does we need to recompute another A* on the fly.
		Position next = ps.get(i);
		if (next.moveInto(aStar.getGrid())){
		    //tell the layout gui
		    guiWaiter.move(next.getX(),next.getY());
		    currentPosition.release(aStar.getGrid());
		    currentPosition = next;
		}
		else {
		    System.out.println("going to break out path-moving");
		    pathTaken = false;
		    break;
		}
	    }
	}
	*/
    }

    // *** EXTRA ***

    /** @return name of waiter */
    public String getName(){
        return name;
    }
    
    public void setCashier(CashierAgent ca){
    this.cashier=ca;	
    }

    /** @return string representation of waiter */
    public String toString(){
	return "waiter " + getName();
    }
    
    /** Hack to set the cook for the waiter */
    public void setCook(CookAgent cook){
	this.cook = cook;
    }
    
    /** Hack to set the host for the waiter */
    public void setHost(HostAgent host){
	this.host = host;
    }

    /** @return true if the waiter is on break, false otherwise */
    public boolean isOnBreak(){
	return onBreak;
    }

}

