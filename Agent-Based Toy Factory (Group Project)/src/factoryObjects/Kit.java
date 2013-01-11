package factoryObjects;

import java.util.ArrayList;
import java.util.List;

// Have to add Part class later
public class Kit {
	public List<Integer> partsNeeded = new ArrayList<Integer>();
	public List<Integer> partsHeld = new ArrayList<Integer>();
	
	public enum KitState {empty, finished};
	public KitState state = KitState.empty;
	boolean complete = false;
	public Kit()
	{
		
	}
	
	public List<Integer> getConfig()
	{
		return partsNeeded;
	}
	
	public void addPart(Integer p)		// added by FCS 
	{
		partsNeeded.add(p);
	}
	
	public Kit(List<Integer> parts)
	{
		List<Integer> configurationParts = parts;
		for(Integer p : configurationParts)
		{
			partsNeeded.add(p);
		}
	}

	public void addPartHeld(Integer p)
	{
		partsHeld.add(p);
		for(Integer part : partsNeeded)
			if(part == p)
			{
				partsNeeded.remove(part);
				break;
			}
	}

	// NEED TO CHECK THIS FUNCTION: might not work......
	boolean IsComplete()
	{
		for(Integer part : partsNeeded)
		{
			for(Integer p : partsHeld)
			{
				if(part != p)
				{
					complete = false;
					break;
				}
			}
		}
		return complete;
	}
			
}
