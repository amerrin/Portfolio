package factoryAgents.KitRobot.test;

import factoryObjects.Kit;
import factoryObjects.KitVisionResult;
import factoryInterfaces.KitRobot;

public class MockKitRobotAgent extends MockAgent implements KitRobot{

	public MockKitRobotAgent(String name) {
		super(name);
	}

	public EventLog log = new EventLog();
	@Override
	public void msgInitialize() {
		log.add(new LoggedEvent("Received msgInitialize from FCS "));
	}

	@Override
	public void msgHereIsKit(Kit emptyKit) {
		log.add(new LoggedEvent("Received msgHereIsKit from conveyor that empty kit is ready to put on table"));
		
	}

	@Override
	public void msgCanIGoToTable() {
		log.add(new LoggedEvent("Received msgCanIGoToTable from partsRobot if partsRobot can dump parts in empty kit on table"));
	}
	@Override
	public void msgKitStatus(KitVisionResult result) {
		log.add(new LoggedEvent("Received msgKitStatus from visionAgent with Kit Status"));
	}

	@Override
	public void msgAnimationDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgKitIsReady(int tableIndex) {
		log.add(new LoggedEvent("Received msgKitIsReady from partsRobot that finished kit is placed on table"));
	}

	@Override
	public void msgDoneDumpingParts() {
		// TODO Auto-generated method stub
		
	}

}
