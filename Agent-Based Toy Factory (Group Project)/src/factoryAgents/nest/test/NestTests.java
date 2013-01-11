package factoryAgents.nest.test;

import org.junit.*;
import factoryInterfaces.*;
import factoryObjects.NestVisionResult;
import factoryAgents.nest.NestAgent;
import gfxInterfaces.Gui;
import mockbase.MockGui;
import junit.framework.TestCase;


public class NestTests extends TestCase {
	NestAgent nest;
	MockPartsRobot partsRobot;
	MockLane lane;
	MockGui gui;
	MockVision vision;

	@Test
	public void testGetPartsContinuouslyFromLane(){
		nest = new NestAgent(0);partsRobot = new MockPartsRobot();lane = new MockLane();gui = new MockGui("GUI");vision = new MockVision();
		nest.setLane(lane); nest.setGUI(gui); nest.setPartsRobot(partsRobot); nest.setVision(vision);
		
		
		assertEquals("Nest should have a part needed of type '-1' upon initialization; instead, it already has part type: " 
				+ nest.getPartNeeded(), -1, (int) nest.getPartNeeded());
		
		nest.msgNeedPart(1);
		
		assertFalse("partAskedFor should be false before scheduler is fired."
				,nest.partAskedFor);
		
		nest.pickAndExecuteAnAction();
		
		assertTrue("partAskedFor should be true after scheduler is fired."
				,nest.partAskedFor);
		
		assertTrue(""
				,lane.log.containsString("Recieved msgIneedParts"));
		
		for(int i=0; i<10; i++)
			nest.msgHereIsPart(1);
		
		nest.pickAndExecuteAnAction();
		
		assertTrue("Vision should have recieved message from nest to take a picture because it was fed 10 parts: "
				+vision.log.toString(),vision.log.containsString("Recieved msgTakeNestPicture"));
		
		assertTrue("Lane should have recieved msgIamStable when nest reaches max capacity: "
				+lane.log.toString(),lane.log.containsString("Recieved msgIamStable"));
		
		NestVisionResult nvr = new NestVisionResult(10);
		
		nest.msgHereIsPicture(nvr);
		
		assertEquals("PartsRobot should not have recieved a message from nest until scheduler is called: "
				+partsRobot.log.toString(),0,partsRobot.log.size());
		
		nest.pickAndExecuteAnAction();
		
		assertEquals("PartsRobot should have recieved 10 good parts from nest: "
				+partsRobot.log.size(),10,partsRobot.log.size());

		lane.log.clear();
		nest.msgPickedUpPart(0);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgPickedUpPart(1);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgPickedUpPart(2);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgPickedUpPart(3);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgPickedUpPart(4);
		nest.pickAndExecuteAnAction();
		assertTrue("Lane should be asked for more parts after threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgHereIsPart(1); nest.msgHereIsPart(1); nest.msgHereIsPart(1); nest.msgHereIsPart(1); nest.msgHereIsPart(1);
		nest.pickAndExecuteAnAction();
		
		assertTrue("Vision should have recieved message from nest to take a picture because it was fed 10 parts: "
				+vision.log.toString(),vision.log.containsString("Recieved msgTakeNestPicture"));
		
		assertTrue("Lane should have recieved msgIamStable when nest reaches max capacity: "
				+lane.log.toString(),lane.log.containsString("Recieved msgIamStable"));
	}
	
	@Test
	public void testChangeConfigurationAndGetMoreParts(){
		nest = new NestAgent(0);partsRobot = new MockPartsRobot();lane = new MockLane();gui = new MockGui("GUI");vision = new MockVision();
		nest.setLane(lane); nest.setGUI(gui); nest.setPartsRobot(partsRobot); nest.setVision(vision);
		
		/*
		 * Copied from previous test for "set-up"
		 */
		assertEquals("Nest should have a part needed of type '-1' upon initialization; instead, it already has part type: " 
				+ nest.getPartNeeded(), -1, (int) nest.getPartNeeded());
		
		nest.msgNeedPart(1);
		
		assertFalse("partAskedFor should be false before scheduler is fired."
				,nest.partAskedFor);
		
		nest.pickAndExecuteAnAction();
		
		assertTrue("partAskedFor should be true after scheduler is fired."
				,nest.partAskedFor);
		
		assertTrue(""
				,lane.log.containsString("Recieved msgIneedParts"));
		
		for(int i=0; i<10; i++)
			nest.msgHereIsPart(1);
		
		nest.pickAndExecuteAnAction();
		
		assertTrue("Vision should have recieved message from nest to take a picture because it was fed 10 parts: "
				+vision.log.toString(),vision.log.containsString("Recieved msgTakeNestPicture"));
		
		assertTrue("Lane should have recieved msgIamStable when nest reaches max capacity: "
				+lane.log.toString(),lane.log.containsString("Recieved msgIamStable"));
		
		NestVisionResult nvr = new NestVisionResult(10);
		
		nest.msgHereIsPicture(nvr);
		
		assertEquals("PartsRobot should not have recieved a message from nest until scheduler is called: "
				+partsRobot.log.toString(),0,partsRobot.log.size());
		
		nest.pickAndExecuteAnAction();
		
		assertEquals("PartsRobot should have recieved 10 good parts from nest: "
				+partsRobot.log.size(),10,partsRobot.log.size());

		lane.log.clear();
		nest.msgPickedUpPart(0);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		
		nest.msgPickedUpPart(1);
		nest.pickAndExecuteAnAction();
		assertFalse("Lane should not be asked for more parts until threshold is reached."
				,lane.log.containsString("Recieved msgIneedParts"));
		/*
		 * new changes are added below:
		 */
		lane.log.clear();
		nest.msgNeedPart(3);
		
		assertTrue("Lane should have gotten msgIamStable because part configuration is changing"
				,lane.log.containsString("Recieved msgIamStable"));
		
		assertEquals("Lane log should only be of size 1: "
				+lane.log.size(), 1, lane.log.size());
		
		nest.pickAndExecuteAnAction();
		assertTrue("Gui should have recieved message from nest to dump parts"
				,gui.log.containsString("doing dump nest"));
		
		assertEquals("Lane log should only be of size 1: "
				+lane.log.size(), 1, lane.log.size());
		nest.msgAnimationUnblock();
		
		assertTrue("Last logged event should have been msgIneedParts, instead it was: "
				+lane.log.getLastLoggedEvent(),lane.log.containsString("Recieved msgIneedParts"));
		
		
		for (int i=0;i<10;i++){
			nest.msgHereIsPart(1);
			nest.pickAndExecuteAnAction();
		}
		
		assertEquals("Lane should have gotten msgIamStable because part nest is dumping"
				,lane.log.getLastLoggedEvent(),"Recieved msgIamStable");
		
		assertEquals("Gui should have recieved a second message to dump nest"
				,2,gui.log.size());
		
		assertEquals("Lane's last logged event should be msgIneedParts: "
				+lane.log.getLastLoggedEvent(),lane.log.getLastLoggedEvent(),"Recieved msgIneedParts");
		
		nest.msgHereIsPart(1);
		nest.msgHereIsPart(3);
		nest.pickAndExecuteAnAction();
		assertEquals("Lane should have gotten msgIamStable because part nest is dumping"
				,lane.log.getLastLoggedEvent(),"Recieved msgIamStable");
		
		assertEquals("Gui should have recieved a second message to dump nest"
				,2,gui.log.size());
		
		assertEquals("Lane's last logged event should be msgIneedParts: "
				+lane.log.getLastLoggedEvent(),lane.log.getLastLoggedEvent(),"Recieved msgIneedParts");
	}
}
