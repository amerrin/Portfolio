package ServerClient;

import java.io.Serializable;

public class Order implements Serializable {
	int kID, n;
	
	public Order(int k, int num) {
		kID = k;
		n = num;
	}
}
