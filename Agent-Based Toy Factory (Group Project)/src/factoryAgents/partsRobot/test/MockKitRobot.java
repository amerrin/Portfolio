package factoryAgents.partsRobot.test;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import factoryInterfaces.KitRobot;
import factoryObjects.Kit;
import factoryObjects.KitVisionResult;

public class MockKitRobot implements KitRobot{

	public EventLog log = new EventLog();
	
	@Override
	public void msgInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgHereIsKit(Kit emptyKit) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgCanIGoToTable() {
		log.add(new LoggedEvent("Received msgCanIGoToTable"));
		
	}

	@Override
	public void msgKitIsReady(int tableIndex) {
		log.add(new LoggedEvent("Received msg that Kit " + tableIndex + " is ready"));
		
	}

	@Override
	public void msgKitStatus(KitVisionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDoneDumpingParts() {
		log.add(new LoggedEvent("Received msgDoneDumpingParts"));
		
	}

}
