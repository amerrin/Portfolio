package factoryAgents.KitRobot.test;

import java.util.ArrayList;

import factoryObjects.Kit;
import factoryInterfaces.Nest;
import factoryInterfaces.PartsRobot;

public class MockPartsRobot extends MockAgent implements PartsRobot{

	public MockPartsRobot(String name) {
		super(name);
	}

	public EventLog log = new EventLog();
	@Override
	public void msgYesYouCan() {
		log.add(new LoggedEvent("Received message msgYesYouCan from KitRobot that PartsRobot can " +
				"move to the table to dump parts in the kit"));
	}

	@Override
	public void msgEmptyKit(int tableIndex, Kit k) {
		log.add(new LoggedEvent("Received message msgEmptyKit from KitRobot that  " +
		"empty kit is available at table slot " + tableIndex));
		
	}

	@Override
	public void msgHereAreGoodParts(Nest n, ArrayList<Integer> goodPart) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgAnimationUnblock() {
		// TODO Auto-generated method stub
		
	}

}
