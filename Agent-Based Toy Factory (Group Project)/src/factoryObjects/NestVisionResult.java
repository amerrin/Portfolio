package factoryObjects;

import java.util.*;


public class NestVisionResult {
	public List<PartState> parts;
	public NestVisionResult(List<PartState> parts) {
		this.parts = parts;
	}
	public ArrayList<Boolean> getGoodBadPartsList(){
		ArrayList<Boolean> partsAnalysis = new ArrayList<Boolean>();
		for (int i=0; i<parts.size();i++){
			for (int j=0; j<parts.size(); j++){
				if (parts.get(j).index == i)
					partsAnalysis.add(parts.get(j).good);
			}
		}
		return partsAnalysis;
	}
}
