package restaurant;

import java.util.*;

import agent.Agent;
import restaurant.interfaces.*;
import restaurant.CookAgent.FoodData;

public class MarketAgent extends Agent implements Market{

	private Map<String,FoodData> inventory = Collections.synchronizedMap(new HashMap<String,FoodData>());
	private Map<String,Double> prices = Collections.synchronizedMap(new HashMap<String,Double>());
	private List<List<FoodData>> orders = Collections.synchronizedList(new ArrayList<List<FoodData>>());
	private Map<Bill,Double> debtMoney = Collections.synchronizedMap(new HashMap<Bill,Double>());
	private List<Bill> debts = Collections.synchronizedList(new ArrayList<Bill>());
	private CookAgent cook;
	public String name;
	private Timer timer = new Timer();
	private double dollars = 100;
	
	public MarketAgent(String name,CookAgent cook){
	super();
	this.name = name;
	this.cook = cook;
	inventory.put("Steak", cook.new FoodData("Steak",(int) Math.random()*30 + 20));
		prices.put("Steak",9.99);
	inventory.put("Chicken", cook.new FoodData("Chicken",(int) Math.random()*30 + 20));
		prices.put("Chicken", 4.99);
	inventory.put("Pizza", cook.new FoodData("Pizza",(int) Math.random()*30 +20));
		prices.put("Pizza", 3.99);
	inventory.put("Salad", cook.new FoodData("Salad",(int) Math.random()*30 +20));
		prices.put("Salad",1.99);
	}
	
	//messages
	public void msgHereIsAnOrder(List<FoodData> needFood){
	orders.add(needFood);
	stateChanged();
	}
	
	public void msgHereIsPayment(Bill b,double dollars){
	debtMoney.put(b,dollars);
	debts.add(b);
	stateChanged();
	}


	protected boolean pickAndExecuteAnAction() {
	synchronized(orders){
	for(List<FoodData> list:orders){
		fulfillOrder(list);
		return true;
	}}
	synchronized(debts){
	for(Bill b:debts){
		takePayment(b);
		return true;
	}}
	
		return false;
	}
	
	//actions
	private void fulfillOrder(List<FoodData> list){
	System.out.println(name+": Checking stocks for order");
	final List<FoodData> outgoingOrder = new ArrayList<FoodData>();final Market market = this;int totalDue = 0;
	for(FoodData f:list){
		if(inventory.get(f.type).count < f.count){
			System.out.println(name+": Cannot fulfill order - notifying cook.");
			synchronized(this){cook.msgCannotFulfillOrder(this);}
			synchronized(orders){orders.remove(list);}
			return;
		}
		else{
			outgoingOrder.add(cook.new FoodData(f.type,f.count));
			totalDue += f.count * prices.get(f.type);
			inventory.get(f.type).count -= f.count;
		}
	}
	final Bill bill = new Bill(totalDue,this);
	System.out.println(name+": Fulfilling order for restuarant");
	timer.schedule(new TimerTask(){
	    public void run(){//this routine is like a message reception
		cook.msgOrderFulfilled(outgoingOrder,market,bill);
	    }
	}, 10000);
	synchronized(orders){orders.remove(list);}
	}
	
	private void takePayment(Bill b){
	System.out.println(name+": Payment received from restaurant");
	synchronized(debtMoney){
	b.price -= debtMoney.get(b);
	dollars+=debtMoney.get(b);
	debtMoney.remove(b);}
	synchronized(debts){debts.remove(b);}
	}
	
	public void setCook(CookAgent c){
	this.cook = c;
	}
	
	public String getName(){
		return this.name;
	}
	
}
