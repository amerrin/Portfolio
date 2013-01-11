package factoryAgents.feeder.test;

import mockbase.MockGui;
import junit.framework.TestCase;
import factoryAgents.feeder.FeederAgent;

public class test extends TestCase {
	
	public void test1()
	{
		
		MockLane lane1=new MockLane("lane1");
		MockLane lane2=new MockLane("lane2");
		MockGantry gantry=new MockGantry("gantry");
		FeederAgent feeder=new FeederAgent("feeder",lane1,lane2,gantry,0);
		feeder.setGui(new MockGui("gui"));
		feeder.msgIneedParts(lane1,0);
		feeder.pickAndExecuteAnAction();
		assertTrue(gantry.log.containsString("I recieved part request from "+ feeder));
		feeder.msgHereAreParts(0);
		feeder.pickAndExecuteAnAction();
		feeder.msgFedPart();
		feeder.msgIneedParts(lane1,1);
		feeder.pickAndExecuteAnAction();
		assertTrue(gantry.log.containsString("I recieved msgPurgedFeeder"));
		feeder.pickAndExecuteAnAction();
		feeder.msgHereAreParts(1);
		feeder.pickAndExecuteAnAction();
		feeder.msgFedPart();
		feeder.msgIneedParts(lane2,3);
		feeder.pickAndExecuteAnAction();
		feeder.msgIamStable(lane1);
		feeder.pickAndExecuteAnAction();
		assertTrue(gantry.log.getLastLoggedEvent().message.equals("I recieved msgPurgedFeeder"));
		feeder.pickAndExecuteAnAction();
		assertTrue(gantry.log.getLastLoggedEvent().message.equals("I recieved part request from feeder"));
		feeder.msgPartsLow();
		feeder.pickAndExecuteAnAction();
		assertTrue(gantry.log.getLastLoggedEvent().message.equals("I recieved part request from feeder"));
		feeder.msgHereAreParts(3);
		feeder.pickAndExecuteAnAction();
	}
	
	public void test2()
	{
	}
	
	public void test3()
	{
		
	}

}
