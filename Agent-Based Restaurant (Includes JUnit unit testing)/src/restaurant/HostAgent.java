package restaurant;

import agent.Agent;
import restaurant.interfaces.*;
import java.util.*;


/** Host agent for restaurant.
 *  Keeps a list of all the waiters and tables.
 *  Assigns new customers to waiters for seating and 
 *  keeps a list of waiting customers.
 *  Interacts with customers and waiters.
 */
public class HostAgent extends Agent implements Host{

    /** Private class storing all the information for each table,
     * including table number and state. */
    private class Table {
		public int tableNum;
		public boolean occupied;
	
		/** Constructor for table class.
		 * @param num identification number
		 */
		public Table(int num){
		    tableNum = num;
		    occupied = false;
		}	
    }

    /** Private class to hold waiter information and state */
    private class MyWaiter {
	public WaiterAgent wtr;
	public boolean working = true;

	/** Constructor for MyWaiter class
	 * @param waiter
	 */
	public MyWaiter(WaiterAgent waiter){
	    wtr = waiter;
	}
    }

    //List of all the customers that need a table
    private List<CustomerAgent> waitList =
		Collections.synchronizedList(new ArrayList<CustomerAgent>());

    //List of all waiter that exist.
    private List<MyWaiter> waiters =
		Collections.synchronizedList(new ArrayList<MyWaiter>());
    private int nextWaiter =0; //The next waiter that needs a customer
    
    private List<MyWaiter> breakList = Collections.synchronizedList(new ArrayList<MyWaiter>());
    private List<Customer> blackList = Collections.synchronizedList(new ArrayList<Customer>());
    //List of all the tables
    int nTables;
    private Table tables[];

    //Name of the host
    private String name;

    public enum CustomerState {WAITING, NOTWAITING, NONE};
    
    /** Constructor for HostAgent class 
     * @param name name of the host */
    public HostAgent(String name, int ntables) {
	super();
	this.nTables = ntables;
	tables = new Table[nTables];

	for(int i=0; i < nTables; i++){
	    tables[i] = new Table(i);
	}
	this.name = name;
    }

    // *** MESSAGES ***

    /** Customer sends this message to be added to the wait list 
     * @param customer customer that wants to be added */
    public void msgIWantToEat(Customer customer){
    if (verifyCustomer((CustomerAgent)customer))
    	return;
	waitList.add((CustomerAgent)customer);
	stateChanged();
    }

    /** Waiter sends this message after the customer has left the table 
     * @param tableNum table identification number */
    public void msgTableIsFree(int tableNum){
	synchronized(tables){tables[tableNum].occupied = false;}
	stateChanged();
    }
    
    public void msgIWantToGoOnBreak(Waiter waiter){
    breakList.add(new MyWaiter((WaiterAgent)waiter));
    stateChanged();
    }
    
    public void msgIAmOffBreak(Waiter waiter){
    WaiterAgent w = (WaiterAgent) waiter;
    print("begin assigning "+w.getName()+" customers again.");
    waiters.add(new MyWaiter((WaiterAgent)waiter));
    }

    public void msgImNotWaiting(Customer c){
    waitList.remove(c);
    stateChanged();
    }
    /** Scheduler.  Determine what action is called for, and do it. */
    protected boolean pickAndExecuteAnAction() {
	
	if(!waitList.isEmpty() && !waiters.isEmpty()){
	    synchronized(waiters){
		//Finds the next waiter that is working
		while(!waiters.get(nextWaiter).working){
		    nextWaiter = (nextWaiter+1)%waiters.size();
		}
	    }
	    print("picking waiter number:"+nextWaiter);
	    //Then runs through the tables and finds the first unoccupied 
	    //table and tells the waiter to sit the first customer at that table
	    for(int i=0; i < nTables; i++){

		if(!tables[i].occupied){
		    synchronized(waitList){
			tellWaiterToSitCustomerAtTable(waiters.get(nextWaiter),waitList.get(0), i);
		    }
		    return true;
		}
	    }
	    synchronized(waitList){
		if(waitList.get(0).hostState != CustomerState.WAITING){
			System.out.println(name+": No seats available, informing " + waitList.get(0) +" of wait");
			waitList.get(0).msgNoSeatAvailable();
		}}
	}
	
	for(MyWaiter w:breakList){
		tryBreakWaiter(w);
		return true;
	}

	//we have tried all our rules (in this case only one) and found
	//nothing to do. So return false to main loop of abstract agent
	//and wait.
	return false;
    }
    
    // *** ACTIONS ***
    
    /** Assigns a customer to a specified waiter and 
     * tells that waiter which table to sit them at.
     * @param waiter
     * @param customer
     * @param tableNum */
    private void tellWaiterToSitCustomerAtTable(MyWaiter waiter, Customer customer, int tableNum){
	print("Telling " + waiter.wtr + " to sit " + customer +" at table "+(tableNum+1));
	waiter.wtr.msgSitCustomerAtTable(customer, tableNum);
	tables[tableNum].occupied = true;
	synchronized(waitList){waitList.remove(customer);
	nextWaiter = (nextWaiter+1)%waiters.size();}
	stateChanged();
    }
	
    private void tryBreakWaiter(MyWaiter w){
    if(waiters.size()>1){
    	System.out.println(name+": approving "+w.wtr.name+"'s break.");
    	synchronized(breakList){breakList.remove(w);}
    	synchronized(waiters){
    	for(MyWaiter w2:waiters){
    	if(w2.wtr == w.wtr)
    		w=w2;
    	}
    	waiters.remove(w);
    	w.wtr.msgOKToBreak();}
    }
    else{
    	System.out.println(name+": denying "+w.wtr.name+"'s break.");
    	w.wtr.msgNOTOKToBreak();
    	synchronized(breakList){breakList.remove(w);}
    }
    stateChanged();
    }

    // *** EXTRA ***

    /** Returns the name of the host 
     * @return name of host */
    public String getName(){
        return name;
    }   
    
    public void addCustomerBlacklist(Customer c){
    	blackList.add(c);
    }
    
    public boolean verifyCustomer(CustomerAgent c){
    	for (Customer cust:blackList){
    		if (cust == c){
    		c.msgPleaseLeave();
    		return true;
    		}
    	}
    	return false;
    }

    /** Hack to enable the host to know of all possible waiters 
     * @param waiter new waiter to be added to list
     */
    public void setWaiter(WaiterAgent waiter){
	waiters.add(new MyWaiter(waiter));
	stateChanged();
    }
    
    //Gautam Nayak - Gui calls this when table is created in animation
    public void addTable() {
	nTables++;
	Table[] tempTables = new Table[nTables];
	for(int i=0; i < nTables - 1; i++){
	    tempTables[i] = tables[i];
	}  		  			
	tempTables[nTables - 1] = new Table(nTables - 1);
	tables = tempTables;
    }
}
