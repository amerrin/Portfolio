package factoryInterfaces;

import java.util.List;


public interface FCS {
	public void msgMakeKits(List<Integer> cfg, int num);
	/** MAKE SURE THE AGENT HAS A msgInitialize() MESSAGE OR ELSE CRASH */
	//public void msgAddAgent(FactoryAgent a);
	public void msgTurnOff();
	public void msgSetConveyor(Conveyor conveyor);
	public void msgInitialize();
}

