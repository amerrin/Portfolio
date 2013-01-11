package factoryAgents.KitRobot.test;

import factoryObjects.Kit;
import factoryInterfaces.Conveyor;

public class MockConveyor extends MockAgent implements Conveyor{

	public MockConveyor(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	public EventLog log = new EventLog();
	@Override
	public void msgFinishedKit(Kit k) {
		log.add(new LoggedEvent("Received msgFinishedKit from KitRobot that kit has been finished"
				+ "and needs to be put on conveyor for delivery"));
	}

	@Override
	public void msgHereIsConfig(Kit cfg, int numberofKitsToBuild) {
	
		log.add(new LoggedEvent("Received msgHereIsConfig from fcs with kit configuration"+ 
				 "and number of kits to be built is/are " + numberofKitsToBuild));
	}

	@Override
	public void msgAnimationDone() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgKitReady() {
		// TODO Auto-generated method stub
		
	}

}
