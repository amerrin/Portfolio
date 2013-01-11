package factoryInterfaces;

import java.util.List;

public interface Gantry {
	 
	public void msgInitialize();
	
	public void msgIneedParts(Feeder feeder,int part);
	
	public void msgPurgedFeeder(Feeder feeder);
	
	public void msgAtDestination();
	
	public void msgTurnOff();
	
	
}
