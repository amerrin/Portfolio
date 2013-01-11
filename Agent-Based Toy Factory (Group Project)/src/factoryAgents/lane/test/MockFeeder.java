package factoryAgents.lane.test;

import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Lane;

public class MockFeeder extends MockAgent implements Feeder {
	public MockFeeder() {
		super("Mock Feeder");
	}

	private static final boolean PRINT_LOG = true;

	
	private void addLog(String message) {
		log.add(new LoggedEvent(message));
		if (PRINT_LOG) { System.out.println(message); };
	}

	@Override
	public void msgInitialize() {
		addLog("msgInitialize");
	}

	@Override
	public void msgIneedParts(Lane lane, int partid) {
		addLog("Part Requested: "+partid);
	}

	@Override
	public void msgHereAreParts(int partid) {
		
	}

	@Override
	public void msgPartsLow() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIamStable(Lane lane) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgFedPart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}
}
