package factoryAgents.nest.test;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Lane;

public class MockLane implements Lane {

	public EventLog log = new EventLog();


	@Override
	public void msgHereIsPart(int part) {
		log.add(new LoggedEvent("Recieved msgHereIsPart "+part));
	}

	@Override
	public void msgIneedParts(int part) {
		log.add(new LoggedEvent("Recieved msgIneedParts"));
	}

	@Override
	public void msgIamStable() {
		log.add(new LoggedEvent("Recieved msgIamStable"));
		
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
