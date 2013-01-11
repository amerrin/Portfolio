package factoryInterfaces;

import factoryObjects.Kit;
import factoryObjects.KitVisionResult;


public interface KitRobot {
	
	public abstract void msgInitialize();
	public abstract void msgHereIsKit(Kit emptyKit);
	public abstract void msgCanIGoToTable();
	public abstract void msgKitIsReady(int tableIndex);
	public abstract void msgKitStatus(KitVisionResult result);
	public abstract void msgAnimationDone();
	public abstract void msgDoneDumpingParts();
	public abstract void msgTurnOff();
}
