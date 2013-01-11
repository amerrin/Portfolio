 package factoryInterfaces;

import java.util.ArrayList;
import java.util.List;

import factoryObjects.Kit;

public interface PartsRobot {
	public abstract void msgInitialize();
	public abstract void msgEmptyKit(int kitIndex, Kit kit);
	public abstract void msgHereAreGoodParts(Nest n,ArrayList<Integer> goodPart);
	public abstract void msgYesYouCan();
	public abstract void msgAnimationUnblock();
	public abstract void msgTurnOff();
	public abstract void msgClearNestData(int nestID);
	public abstract void msgBadKit(int tableIndexEmpty, Kit kit, List<Integer> partsNeeded);
}
