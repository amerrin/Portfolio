package factoryAgents.KitRobot.test;

import java.util.List;

import factoryAgents.NestAgent;
import factoryAgents.Part;

import factoryInterfaces.KitRobot;
import factoryInterfaces.Vision;

public class MockVisionAgent extends MockAgent implements Vision{

	public MockVisionAgent(String name) {
		super(name);
	}
	
	public EventLog log = new EventLog();
	
	@Override
	public void msgInitialize() {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received message msgInitialize from FCS to initialize camera agent"));
	}

	@Override
	public void msgTakeKitPicture(KitRobot kitRobot, List<Integer> kitConfig) {
		// TODO Auto-generated method stub
		log.add(new LoggedEvent("Received message msgTakePicture from KitRobot" +
				kitRobot.toString()+ " to take picture of kit with kitConfig"));	
	}

	@Override
	public void msgIHaveParts(NestAgent nestAgent) {
		// TODO Auto-generated method stub
		
	}

}
