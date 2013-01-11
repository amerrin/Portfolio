package factoryAgents.feeder.test;

import java.util.List;

public interface Gantry {
	 
	public void msgInitialize(List<Integer> parts);
	
	public void msgIneedParts(Feeder feeder,int part);
	
	public void msgPurgedFeeder(Feeder feeder);
	
	public void msgAtDestination();
	
}
