package factoryAgents.feeder.test;

import java.util.List;

import mockbase.EventLog;
import mockbase.LoggedEvent;
import mockbase.MockAgent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Gantry;

public class MockGantry extends MockAgent implements Gantry {

	public EventLog log=new EventLog();
	
	public MockGantry(String name) {
		super(name);
	}

	@Override
	public void msgInitialize() {
		log.add(new LoggedEvent("I recieved msgInitialize"));
	}

	@Override
	public void msgIneedParts(Feeder feeder, int partid) {
		log.add(new LoggedEvent("I recieved part request from "+ feeder));
	}

	@Override
	public void msgPurgedFeeder(Feeder feeder) {
		log.add(new LoggedEvent("I recieved msgPurgedFeeder"));
	}
	
	public void msgAtDestination()
	{
		log.add(new LoggedEvent("recieved at destination"));
	}

	@Override
	public void msgTurnOff() {
		// TODO Auto-generated method stub
		
	}

}
