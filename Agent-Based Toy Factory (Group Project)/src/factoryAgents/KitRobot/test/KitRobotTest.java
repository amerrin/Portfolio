
package factoryAgents.KitRobot.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mockbase.MockGui;

import factoryObjects.KitVisionResult;
import junit.framework.TestCase;
import factoryObjects.Kit;
import factoryAgents.kitRobot;
//import FactoryAgents.Part;
import factoryOgents.Table;
//import GUI.Gui;
//import GUI.KitAssembly;
//import mock.MockConveyor;
//import mock.MockGUI;
//import mock.MockPartsRobot;
//import mock.MockVisionAgent;


import org.junit.Before;
import org.junit.Test;

public class KitRobotTest extends TestCase{

	
	Kit conf = new Kit();
	 
	 KitRobotAgent kitRobot;
	 MockGUI mockgui;
	 MockPartsRobot partsRobotmock = new MockPartsRobot("partsRobot");
	 MockConveyor conveyormock = new MockConveyor("conveyor");
	 MockVisionAgent visionAgentmock = new MockVisionAgent("visionAgent");
	 Table table = new Table();
	 
	 @Before
	public void setup()
	{
		 mockgui = new MockGUI("test");
		 kitRobot = new KitRobotAgent("KitRobot", mockgui);
		 Part p = new Part(1, 2);
		 Part p1 = new Part(2, 2);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.msgInitialize();
		 kitRobot.setConveyor(conveyormock);
		 kitRobot.setVisionAgent(visionAgentmock);
		 kitRobot.setPartsRobot(partsRobotmock);
	}
	 
	@Test
	 public void TestKitIsReady()
	 {
		 mockgui = new MockGUI("test1");
		 kitRobot = new KitRobotAgent("KitRobot", mockgui);
		// Part p = new Part(1, 2);
		 //Part p1 = new Part(2, 2);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.msgInitialize();
	//	 kitRobot.setConveyor(conveyormock);
		 kitRobot.setVisionAgent(visionAgentmock);
//		 kitRobot.setPartsRobot(partsRobotmock);
		 //Kit conf = new Kit();
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.setTable(table);
		 table.insert(conf, 1);
		 
		 kitRobot.msgKitIsReady(1);
		 
		 assertEquals("Mock GUI should have received the message to put kit on inspection stand. Event log:"
				 + mockgui.log.toString(), mockgui.log.containsString("Received DoPlaceKitOnTableForInspection from KitRobotAgent to put the kit on inspection stand."));
		 
		 assertEquals(
					"Mock visionAgent should have an empty event log before the kitRobot scheduler is called. Instead, the mock visionAgent event log reads: "
							+ visionAgentmock.log.toString(), 0, visionAgentmock.log.size());
		 kitRobot.pickAndExecuteAnAction();

		 assertTrue(
					"Mock visionAgent should have received message with the msgTakePicture. Event log: "
				 		+ visionAgentmock.log.toString(), visionAgentmock.log.containsString("Received message msgTakePicture"));

		
	 }
	 @Test
	public void testGUIpickUpkit()
	{
		 mockgui = new MockGUI("test");
		 kitRobot = new KitRobotAgent("KitRobot", mockgui);
		 Part p = new Part(1, 2);
		 Part p1 = new Part(2, 2);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.msgInitialize();
		 kitRobot.setConveyor(conveyormock);
		 kitRobot.setVisionAgent(visionAgentmock);
		 kitRobot.setPartsRobot(partsRobotmock);
		 
		Kit emptyKit = new Kit(conf);
		kitRobot.msgHereIsKit(emptyKit);
		
		// kitRobot.msgAnimationDone();
		 kitRobot.pickAndExecuteAnAction();
		/* assertTrue("Mock GUI should have recieved the message to turn on conveyor. Event log:" 
				 + mockgui.log.toString(), mockgui.log.containsString("RReceived DoTurnOnConveyor from KitRobotAgent."));
		 */
		
		 assertTrue("Mock GUI should have recieved the message to pick up the empty kit from conveyor. Event log:" 
				 + mockgui.log.toString(), mockgui.log.containsString("doing turn on conveyor"));
		 assertTrue(
					"Mock partsRobot should have received message with the kit. Event log: "
				 		+ partsRobotmock.log.toString(), partsRobotmock.log.containsString("Received message msgEmptyKit"));
		 assertEquals(
					"Only 1 message should have been sent to the partsRobotmock. Event log: "
							+ partsRobotmock.log.toString(), 1, partsRobotmock.log.size());

	}
	 @Test
	 public void testkitrobot()
	 {
		 mockgui = new MockGUI("test");
		 kitRobot = new KitRobotAgent("KitRobot", mockgui);
		 Part p = new Part(1, 2);
		 Part p1 = new Part(2, 2);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.msgInitialize();
		 kitRobot.setConveyor(conveyormock);
		 kitRobot.setVisionAgent(visionAgentmock);
		 kitRobot.setPartsRobot(partsRobotmock);
		 Kit conf = new Kit();
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);

		 kitRobot.msgCanIGoToTable();
		 
		 kitRobot.pickAndExecuteAnAction();
		 
		 assertTrue(
					"Mock partsRobot should have received message with the answer. Event log: "
				 		+ partsRobotmock.log.toString(), partsRobotmock.log.containsString("Received message msgYesYouCan")); 

		// kitRobot.msgDoneDumpingParts();
		
	 }
	 
	
	 
	 public void testmsgKitStatus()
	 {
		 mockgui = new MockGUI("test");
		 kitRobot = new KitRobotAgent("KitRobot", mockgui);
		 kitRobot.setConveyor(conveyormock);
		 kitRobot.setVisionAgent(visionAgentmock);
		
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 conf.addPart(3);
		 kitRobot.setTable(table);
		 table.insert(conf, 0);
		 
		
		 kitRobot.msgKitStatus(new KitVisionResult(true));
		 
		 kitRobot.pickAndExecuteAnAction();
		 assertEquals("Mock GUI should have received the message to put kit on conveyor for delivery. Event log:"
				 + mockgui.log.toString(), mockgui.log.containsString("Received DoTakeKitToConveyorForDelivery from KitRobotAgent to take the kit for delivery on conveyor."));
		 
		 assertTrue(
					"Mock conveyor should have received message with the msgFinishedKit. Event log: "
				 		+ conveyormock.log.toString(), conveyormock.log.containsString("Received message msgFinishedKit"));		  

	 }

	 
}
