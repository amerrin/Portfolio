package restaurant.test;

import org.junit.Test;

import restaurant.Bill;
import restaurant.CashierAgent;
import restaurant.test.mock.*;

import junit.framework.TestCase;

public class CashierAgentTests extends TestCase{
	
	/**
	 * This is the CashierAgent to be tested.
	 */
	public CashierAgent cashier;
	
	/**
	 * Test method for
	 * {@link restaurant.CashierAgent#msgHereIsMyMoney(Bill, double)}
	 * .
	 * 
	 * This method creates a CashierAgent and a MockCustomer.  
	 * The cashier is message that the customer is paying. In this test, the customer will have enough money to pay.  
	 * The cashier's scheduler is then called.  The customer should receive msgHereIsYourChange(double change) after the scheduler is called.
	 */
	@Test
	public void testMsgHereIsMyMoneyWithEnoughDollars(){
		cashier = new CashierAgent("cashier");
		
	    MockCustomer customer = new MockCustomer("c1");
		
		cashier.msgHereIsMyMoney(new Bill("chicken",14.99,customer),40);
		
		assertEquals(
				"Mock Customer should have an empty event log before the Cashier's scheduler is called. Instead, the mock customer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue(
				"Mock customer should have received message to receive change. Event log: "
						+ customer.log.toString(), customer.log
						.containsString("Received message msgHereIsYourChange"));
		assertEquals(
				"Only 1 message should have been sent to the customer. Event log: "
						+ customer.log.toString(), 1, customer.log.size());
		
	}
	
	/**
	 * Test method for
	 * {@link restaurant.CashierAgent#msgHereIsMyMoney(Bill, double)}
	 * .
	 * 
	 * This method creates a CashierAgent and a MockCustomer.  
	 * The cashier is message that the customer is paying. In this test, the customer will NOT have enough money to pay.  
	 * The cashier's scheduler is then called.  The customer should receive msgHereIsYourChange(double change) after the scheduler is called.
	 * The host's blacklist should contain the customer after cashier's scheduler is called.
	 */
	@Test
	public void testMsgHereIsMyMoneyWithoutEnoughDollars(){
		cashier = new CashierAgent("cashier");
		
		MockCustomer customer = new MockCustomer("c1");
		MockHost host = new MockHost("host");
		
		cashier.setHost(host);
		cashier.msgHereIsMyMoney(new Bill("chicken",50,customer), 40);
		
		assertEquals(
				"Mock Customer should have an empty event log before the Cashier's scheduler is called. Instead, the mock customer's event log reads: "
						+ customer.log.toString(), 0, customer.log.size());
		assertEquals(
				"Mock host should have an empty blacklist of customer. Instead, log contains: "
						+ host.blackList.toString(),0,host.blackList.size());
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue(
				"Mock customer should have received message to receive change. Event log: "
						+ customer.log.toString(), customer.log
						.containsString("Received message msgHereIsYourChange"));
		assertEquals(
				"Only 1 message should have been sent to the customer. Event log: "
						+ customer.log.toString(), 1, customer.log.size());
		assertTrue(
				"Mock host should have added customer to blacklist. Instead, blackList contains: "
						+ host.blackList.toString(),host.blackList.contains(customer));
		assertEquals(
				"Mock host blacklist should be of length one. Instead it is of length: "
						+ host.blackList.size(),1,host.blackList.size());
	}
	
	/**
	 * Test method for
	 * {@link restaurant.CashierAgent#msgPayMarket(restaurant.interfaces.Market, Bill)}
	 * .
	 * 
	 * This method creates a CashierAgent and a MockMarket.  
	 * The cashier is message that the market needs to be paid.  
	 * The cashier's scheduler is then called.  The market should receive msgHereIsPmt after the scheduler is called.
	 */
	@Test
	public void testMsgPayMarket(){
		cashier = new CashierAgent("cashier");
		
		MockMarket market = new MockMarket("market1");
		
		cashier.msgPayMarket(market,new Bill(40,market));
		
		assertEquals(
				"Mock Market should have an empty event log before the Cashier's scheduler is called. Instead, the mock market's event log reads: "
						+ market.log.toString(), 0, market.log.size());
		
		cashier.pickAndExecuteAnAction();
		
		assertTrue(
				"Mock market should have received message to receive change. Event log: "
						+ market.log.toString(), market.log
						.containsString("Received message msgHereIsPayment"));
		assertEquals(
				"Only 1 message should have been sent to the market. Event log: "
						+ market.log.toString(), 1, market.log.size());
	}
	
}
