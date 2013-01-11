package restaurant;

import restaurant.interfaces.*;

public class Bill{
	String choice;
	double price;
	Customer customer;
	Market market;
	
	public Bill(String ch,double p,Customer c){
	this.choice=ch;
	this.price=p;
	this.customer=c;
	this.market = null;
	}

	
	public Bill(double p,Market market){
	this.market=market;
	this.price = p;
	}
}