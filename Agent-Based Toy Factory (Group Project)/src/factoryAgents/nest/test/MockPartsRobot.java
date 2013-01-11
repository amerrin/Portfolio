package factoryAgents.nest.test;

import java.util.ArrayList;

import mockbase.EventLog;
import mockbase.LoggedEvent;

import factoryInterfaces.Nest;
import factoryInterfaces.PartsRobot;
import factoryObjects.Kit;

public class MockPartsRobot implements PartsRobot{

	public EventLog log=new EventLog();
	
	public void msgHereAreGoodParts(Nest n, ArrayList<Integer> goodPart) {
		log.add(new LoggedEvent("Recieved msgHereAreGoodParts"));
	}
	
	public void msgYesYouCan() {}
	public void msgAnimationUnblock() {}
	public void msgEmptyKit(int kitIndex, Kit kit) {}

	@Override
	public void msgInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}
}
