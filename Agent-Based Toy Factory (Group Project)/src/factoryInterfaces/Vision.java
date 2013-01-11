package factoryInterfaces;


import java.util.List;


public interface Vision {
	public void msgInitialize();
	public void msgTurnOff();
	public void msgTakeNestPicture(Nest p, int nestNum);
	public void msgTakeKitPicture(KitRobot kitRobot, List<Integer> cfg);
}
