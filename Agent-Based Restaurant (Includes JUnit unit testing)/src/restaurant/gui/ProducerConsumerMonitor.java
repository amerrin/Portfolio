package restaurant.gui;

import java.util.Vector;
import restaurant.CookAgent.Order;

public class ProducerConsumerMonitor extends Object {
    private final int N = 5;
    private int count = 0;
    private Vector<Order> theData;
    
    synchronized public void insert(Order data) {
        while (count == N) {
            try{ 
                System.out.println("TurnTable: Full, waiting");
                wait(5000);                         // Full, wait to add
            } catch (InterruptedException ex) {};
        }
            
        insert_Order(data);
        count++;
        if(count == 1) {
            System.out.println("TurnTable: order put");
            notify();                               // Not empty, notify a 
                                                    // waiting consumer
        }
    }
    
    synchronized public Order remove() {
        Order data;
        while(count == 0)
            try{ 
                //System.out.println("TurnTable: Empty, waiting");
                wait(5000);// Empty, wait to consume
                return (null);
            } catch (InterruptedException ex) {};

        data = remove_Order();
        count--;
        if(count == N-1){ 
            System.out.println("TurnTable: order removed");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    synchronized public Order remove(Order o) {
        Order data;
        while(count == 0)
            try{ 
                //System.out.println("TurnTable: Empty, waiting");
                wait(5000);// Empty, wait to consume
                o.tableNum = -1;
            	return o;
            } catch (InterruptedException ex) {};

        data = remove_Order(o);
        if (data == null){
        	o.tableNum = -1;
        	return o;
        }
        		
        count--;
        if(count == N-1){ 
            System.out.println("TurnTable: order removed");
            notify();                               // Not full, notify a 
                                                    // waiting producer
        }
        return data;
    }
    
    private void insert_Order(Order data){
        theData.addElement(data);
    }
    
    private Order remove_Order(){
        Order data = (Order) theData.firstElement();
        theData.removeElementAt(0);
        return data;
    }
    
    private Order remove_Order(Order o){
	synchronized(theData){
    	for(int i=0;i<theData.size();i++){
	    if(theData.get(i).tableNum==o.tableNum){
	    	Order data = (Order) theData.get(i);
	    	theData.removeElementAt(i);
	    	return data;
	    	}}
	    return o;
    }}
    
    public ProducerConsumerMonitor(){
        theData = new Vector<Order>();
    }
}
