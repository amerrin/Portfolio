package factoryAgents.feeder.test;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Lane;

public class MockFeeder extends MockAgent implements Feeder {

	public EventLog log=new EventLog();
	
	public MockFeeder(String name) {
		super(name);
	}

	public void msgInitialize() {
		log.add(new LoggedEvent("Recieved initialization message"));
	}

	public void msgDivert() {
		log.add(new LoggedEvent("recieved order to divert lane"));
	}

	public void msgIneedPart(Lane lane, int partid) {
		log.add(new LoggedEvent("Recieved request for part from "+lane));
	}

	public void msgHereAreParts(int partid) {
		log.add(new LoggedEvent("recieved needed parts"));
	}

	public void msgPartsLow() {
		log.add(new LoggedEvent("recieved parts are low"));
	}

	public void msgIamStable(Lane lane) {
		log.add(new LoggedEvent("recieved I am stable from "+ lane));
	}
	
	public void msgFedPart(){
		log.add(new LoggedEvent("recieved msg fed part"));
	}

	@Override
	public void msgIneedParts(Lane lane, int partid) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}

}
