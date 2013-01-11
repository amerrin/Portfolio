package factoryObjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KitVisionResult {
	public List<PartState> parts;
	public boolean pass;
	public KitVisionResult(List<PartState> parts, List<Integer> cfg) {
		this.parts = parts;
		pass = true;
		
		System.out.print("kit vision: parts: ");
		for (PartState p: parts)
			System.out.print(p.partType + " ");
		System.out.println("\n\tconfig: " + cfg);
		System.out.print("\tgood: ");
		for (PartState p: parts)
			System.out.print(p.good + " ");
		System.out.println();
		
		if (cfg.size() != parts.size())
			pass = false;
		else
		{
			for (int i = cfg.size() - 1; i >= 0; --i)
				if (!parts.get(i).good) //if the part is bad
					pass = false;
			
			if (pass) // if no bad parts
			{
				//copy parts
				ArrayList<Integer> partsCopy = new ArrayList<Integer>();
				for (int i  = 0; i < parts.size(); ++i)
					partsCopy.add(parts.get(i).partType);
				
				ArrayList<Integer> configCopy = new ArrayList<Integer>();
				for (int i  = 0; i < cfg.size(); ++i)
					configCopy.add(cfg.get(i));
				
				for (int i = configCopy.size() - 1; i >= 0; --i)
				{
					for (int j = 0; j < partsCopy.size(); ++j)
					{
						if (configCopy.get(i) == partsCopy.get(j))
						{
							configCopy.remove(i);
							partsCopy.remove(j);
							break;
						}
					}
				}
			}
		}
	}
	
	public List<Boolean> getGoodBadPartsList(){
		List<Boolean> partsAnalysis = Collections.synchronizedList(new ArrayList<Boolean>());
		for (int i=0; i<parts.size();i++){
			for (int j=0; j<parts.size(); j++){
				if (parts.get(j).index == i)
					partsAnalysis.add(parts.get(j).good);
			}
		}
		return partsAnalysis;
	}
}
