package factoryInterfaces;


import java.util.List;

import factoryObjects.Kit;

public interface Conveyor {

	public abstract void msgFinishedKit(Kit k);
	public abstract void msgHereIsConfig(List<Integer> cfg, int numberofKitsToBuild);
	public abstract void msgAnimationDone();
	public abstract void msgKitReady();
	public abstract void msgTurnOff();
	public abstract void msgInitialize();
	public abstract void msgPickedUpKit();

}
