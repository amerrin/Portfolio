package factoryAgents.nest.test;

import java.util.List;

import mockbase.EventLog;
import mockbase.LoggedEvent;

import factoryInterfaces.KitRobot;
import factoryInterfaces.Nest;
import factoryInterfaces.Vision;

public class MockVision implements Vision{

	public EventLog log=new EventLog();
	
	@Override
	public void msgInitialize() {}

	@Override
	public void msgTakeNestPicture(Nest p, int nestNum) {
		log.add(new LoggedEvent("Recieved msgTakeNestPicture"));
	}

	@Override
	public void msgTakeKitPicture(KitRobot kitRobot, List<Integer> cfg) {}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}

}
