package restaurant;

import java.util.*;


public class Menu {

    public String choices[] = new String[]
	{ "Steak"  ,
	  "Chicken", 
	  "Salad"  , 
	  "Pizza"  };
    public double prices[] = new double[]{15.99,10.99,5.99,8.99};
    public static Map<String,Double> priceMap = new HashMap<String,Double>();
    
    Menu(){
        for(int i=0; i<choices.length;i++)
        	priceMap.put(choices[i],prices[i]);
    }
}
    
