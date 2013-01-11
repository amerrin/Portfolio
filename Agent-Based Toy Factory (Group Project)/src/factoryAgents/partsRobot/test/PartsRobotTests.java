package factoryAgents.partsRobot.test;

import java.util.*;

import mockbase.MockGui;

import org.junit.Test;

import factoryAgents.partsRobot.PartsRobotAgent;
import factoryAgents.partsRobot.PartsRobotAgent.KitState;
import factoryObjects.Kit;
import junit.framework.TestCase;

public class PartsRobotTests extends TestCase{
	PartsRobotAgent partsRobot;
	MockKitRobot kitRobot;
	MockNest[] mockNests = new MockNest[8];
	MockGui gui;
	
	@Test
	public void testMsgEmptyKit(){
		partsRobot = new PartsRobotAgent(); kitRobot = new MockKitRobot();Kit kit = new Kit();gui = new MockGui("GUI");
		for(int i=0;i<8;i++){
			mockNests[i] = new MockNest(i);
			kit.addPart(i);
		}
		partsRobot.setNests(mockNests);partsRobot.setKitRobot(kitRobot);partsRobot.setGUI(gui);
		
		assertEquals(
				"PartsRobot should not have any kits currently active before msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 0, partsRobot.currentKits.size());
		
		partsRobot.msgEmptyKit(1, kit);
		
		assertEquals(
				"PartsRobot should not have 1 kit currently active after msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 1, partsRobot.currentKits.size());
		
		partsRobot.pickAndExecuteAnAction();
		
		for(int i=0;i<8;i++){
			assertTrue("Nest " + i + " should have recieved partID of " + i + " from PartsRobot: "
					+mockNests[i].log.toString(),mockNests[i].log.containsString("Received Part "+i));
		}
		
	}
	
	@Test
	public void testPassFinishedKit(){
		partsRobot = new PartsRobotAgent(); kitRobot = new MockKitRobot();Kit kit = new Kit();gui = new MockGui("GUI");
		for(int i=0;i<8;i++){
			kit.addPartHeld(i);
		}
		partsRobot.setKitRobot(kitRobot);
		
		assertEquals(
				"PartsRobot should not have any kits currently active before msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 0, partsRobot.currentKits.size());
		
		partsRobot.msgEmptyKit(1, kit);
		
		assertEquals(
				"PartsRobot should not have 1 kit currently active after msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 1, partsRobot.currentKits.size());
		
		partsRobot.currentKits.get(0).state = KitState.KIT_FULL;
		
		partsRobot.pickAndExecuteAnAction();
		
		assertTrue(
				"KitRobot should have been asked by PartsRobot to move kit to inspection: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msg that Kit 1 is ready"));
		
	}
	
	@Test
	public void testFullKitCycle(){
		partsRobot = new PartsRobotAgent(); kitRobot = new MockKitRobot();Kit kit = new Kit();gui = new MockGui("GUI");
		for(int i=0;i<8;i++){
			mockNests[i] = new MockNest(i);
			kit.addPart(i);
		}
		partsRobot.setNests(mockNests);partsRobot.setKitRobot(kitRobot);partsRobot.setGUI(gui);
		
		assertEquals(
				"PartsRobot should not have any kits currently active before msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 0, partsRobot.currentKits.size());
		
		partsRobot.msgEmptyKit(1, kit);
		
		assertEquals(
				"PartsRobot should not have 1 kit currently active after msgEmptyKit is called. Instead, it is size: "
						+ partsRobot.currentKits.size(), 1, partsRobot.currentKits.size());
		
		partsRobot.pickAndExecuteAnAction();
		
		for(int i=0;i<8;i++){
			assertTrue("Nest " + i + " should have recieved partID of " + i + " from PartsRobot: "
					+mockNests[i].log.toString(),mockNests[i].log.containsString("Received Part "+i));
		}
		
		for(int i=0;i<8;i++){
			for(int j=0;j<4;j++){
				ArrayList<Integer> partIndex = new ArrayList<Integer>();
				partIndex.add(i);partIndex.add(j);
				partsRobot.msgHereAreGoodParts(mockNests[i],partIndex);
			}
		}

		partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();
		partsRobot.pickAndExecuteAnAction();
		
		assertTrue(
				"GUI should have received message to pick up parts from nest: "
				+gui.log.toString(),gui.log.containsString("doing pick up part"));
		assertEquals(
				"Message should have been sent 4 times from Parts Robot: "
				+gui.log.size(),4,gui.log.size());
		
		partsRobot.pickAndExecuteAnAction();
		
		assertTrue(
				"KitRobot should have been asked by PartsRobot to move to table: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msgCanIGoToTable"));
		
		partsRobot.msgYesYouCan();
		partsRobot.msgAnimationUnblock();
		partsRobot.pickAndExecuteAnAction();
		assertTrue(
				"Gui should have been asked to dump parts most recently: "
				+gui.log.getLastLoggedEvent(),gui.log.containsString("doing dump parts in to kit"));
		assertTrue(
				"KitRobot should have been informed by PartsRobot that he has dumped parts: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msgDoneDumpingParts"));
		kitRobot.log.clear();
		gui.log.clear();
		
		partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();partsRobot.msgAnimationUnblock();
		partsRobot.pickAndExecuteAnAction();	
		
		assertTrue(
				"GUI should have received message to pick up parts from nest: "
				+gui.log.toString(),gui.log.containsString("doing pick up part"));
		assertEquals(
				"Message should have been sent 4 times from Parts Robot: "
				+gui.log.size(),4,gui.log.size());
		
		partsRobot.pickAndExecuteAnAction();
		assertTrue(
				"KitRobot should have been asked by PartsRobot to move to table: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msgCanIGoToTable"));
				
		partsRobot.msgYesYouCan();
		partsRobot.msgAnimationUnblock();
		partsRobot.pickAndExecuteAnAction();
		
		assertTrue(
				"Gui should have been asked to dump parts most recently: "
				+gui.log.getLastLoggedEvent(),gui.log.containsString("doing dump parts in to kit"));
		
		assertTrue(
				"KitRobot should have been informed by PartsRobot that he has dumped parts: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msgDoneDumpingParts"));
		
		partsRobot.pickAndExecuteAnAction();
		assertTrue(
				"KitRobot should have been asked by PartsRobot to move kit to inspection: "
				+kitRobot.log.toString(),kitRobot.log.containsString("Received msg that Kit 1 is ready"));


	}
	
}
