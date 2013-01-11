package factoryAgents.lane.test;

import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Nest;
import factoryObjects.NestVisionResult;

public class MockNest extends MockAgent implements Nest {

	public MockNest() {
		super("Mock Nest");
	}

	@Override
	public void msgHereIsPart(int name) {
		log.add(new LoggedEvent("Received Part "+name));
	}

	@Override
	public void msgNeedPart(int partID) {
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public void msgInitialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}

}
