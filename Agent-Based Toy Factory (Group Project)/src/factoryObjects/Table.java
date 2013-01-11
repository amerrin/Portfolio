package factoryObjects;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Table {

	private MyKit[] tableSlots = new MyKit[3];
	private class MyKit
	{
		Kit k;
		boolean occupied = false;
		MyKit(Kit kit)
		{
			k = kit;
		}
	}

	public void insert(Kit k, int index)
	{
		MyKit kit = new MyKit(k);
		tableSlots[index] = kit;
		tableSlots[index].occupied = true;
		System.out.println("inserting kit "+ k + " at index "+ index);
	}
	
	public boolean spotEmpty(int tableIndex)
	{
		if(tableSlots[tableIndex] == null)
			return true;
		return false;
	}
	public int getIndexOf(Kit k)
	{
		MyKit kit = new MyKit(k);
		for(int i= 1; i < 3; i++)
		{
			if(tableSlots[i] == kit)
			{
				return i;
			}
		}
		return -1;   // not found
	}
	
	public Kit remove(int index)
	{
		Kit k = tableSlots[index].k;
		tableSlots[index].occupied = false;
		tableSlots[index] = null;
		return k;
	}
	
	public Kit getKitFromIndex(int index)
	{
		Kit k = tableSlots[index].k;
		tableSlots[index].occupied = true;
		return k;
	}
	
	// returns the first empty location on the table
	public int getEmptyPositionOnTable()
	{
		if(tableSlots[1] == null)
			return 1;
		else if(tableSlots[2] == null) 
			return 2;
		else
			return -1;
	}
}
