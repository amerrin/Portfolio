package factoryAgents.partsRobot.test;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Nest;
import factoryObjects.NestVisionResult;

public class MockNest implements Nest {

	public EventLog log = new EventLog();
	int nestID = -1;
	
	public MockNest(int nestIndex){
		this.nestID = nestIndex;
	}


	@Override
	public void msgHereIsPart(int name) {
		log.add(new LoggedEvent("Received Part "+name));
	}

	@Override
	public void msgNeedPart(int partID) {
		log.add(new LoggedEvent("Received Part "+partID));
	}


	@Override
	public void msgHereIsPicture(NestVisionResult partsChecked) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgNestFull() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getNestID() {
		return this.nestID;
	}

	@Override
	public void msgPickedUpPart(Integer partIndex) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Integer getPartNeeded() {
		// TODO Auto-generated method stub
		return null;
	}

}
