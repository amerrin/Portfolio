package factoryAgents.feeder.test;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Lane;

public class MockLane extends MockAgent implements Lane {

	public EventLog log=new EventLog();
	
	public MockLane(String name) {
		super(name);
	}

	@Override
	public void msgHereIsPart(int part) {
		log.add(new LoggedEvent("Recieved msgHereIsPart "+part));
	}

	@Override
	public void msgIneedParts(int part) {
		log.add(new LoggedEvent("Recieved msgNeedPart"));
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgIamStable() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgDumpParts(int part) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgInitialize() {
		// TODO Auto-generated method stub
		
	}

}
