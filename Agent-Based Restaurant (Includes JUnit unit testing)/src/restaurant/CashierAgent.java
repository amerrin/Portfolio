package restaurant;

import java.util.*;
import agent.Agent;
import restaurant.interfaces.*;

public class CashierAgent extends Agent implements Cashier{
	
	private List<Bill> customerDebts = Collections.synchronizedList(new ArrayList<Bill>());
	private List<Bill> marketDebts = Collections.synchronizedList(new ArrayList<Bill>());
	private String name;
	private Map<Bill,Double> billMoney = Collections.synchronizedMap(new HashMap<Bill,Double>());
	private Market market = null;
	private Host host = null;
	
	public CashierAgent(String name){
		super();
		this.name = name;
	}
	
	public void msgHereIsMyMoney(Bill b,double dollars){
		customerDebts.add(b);
		billMoney.put(b, dollars);
		stateChanged();
	}
	
	public void msgPayMarket(Market m,Bill b){
		marketDebts.add(b);
		this.market = m;
		stateChanged();
	}

	public boolean pickAndExecuteAnAction() {
		synchronized(customerDebts){
		for (Bill b:customerDebts){
			takeCustomerPayment(b);
			return true;
		}}
		synchronized(marketDebts){
		for(Bill b:marketDebts){
			PayMarket(market,b);
			return true;
		}}
		return false;
	}
	
	private void takeCustomerPayment(Bill b){
		System.out.println(name+": Processing customer's payment.");
		synchronized(billMoney){b.price = b.price - billMoney.get(b); billMoney.remove(b);}
		if (b.price <= 0){
			System.out.println(name+": Returning change to customer.");
			b.customer.msgHereIsYourChange(b.price*-1);
			customerDebts.remove(b);
			stateChanged();
		}
		else if(b.price > 0){
			System.out.println(name+": Customer has not enough money - adding to blacklist.");
			b.customer.msgHereIsYourChange(0);
			customerDebts.remove(b);
			host.addCustomerBlacklist(b.customer);
			stateChanged();
		}
	}
	
	private void PayMarket(Market m,Bill b){
		System.out.println(name+": paying market " + market.getName() +" for food received");
		m.msgHereIsPayment(b,b.price);
		synchronized(marketDebts){marketDebts.remove(b);}
		stateChanged();
	}
	
	public void setHost(Host host){
		this.host = host;
	}
	
}
