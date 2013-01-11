package factoryAgents.KitRobot.test;

import junit.framework.TestCase;
import mockbase.MockGUI;
import factoryAgents.ConveyorAgent;
import factoryObjects.Kit;

//import GUI.KitAssembly;

public class ConveyorTest extends TestCase{

	public ConveyorAgent conveyor;
	
	public void testConveyor()
	{
		MockKitRobotAgent KitRobotmock = new MockKitRobotAgent("kitRobot");
		//KitAssembly kitAssembly = new KitAssembly();
		MockGUI gui = new MockGUI("test");
		conveyor = new ConveyorAgent("Conveyor", gui);
		conveyor.setKitRobot(KitRobotmock);
		//Part p = new Part(1, 2);
		//Part p1 = new Part(2, 2);
		Kit conf = new Kit();
		conf.addPart(3);
		conf.addPart(3);
		conf.addPart(3);
		conf.addPart(3);
		conveyor.msgHereIsConfig(conf, 1);
		conveyor.pickAndExecuteAnAction();
		
		assertTrue("MockGUI should have recieved message to put empty kit on conveyor. EVent log:" 
				+ gui.log.toString(), gui.log.containsString("doing put kit on conveyor with 1 kits(s)."));
		
		 assertTrue(
					"Mock kitRobot should have received message with the empty kit. Event log: "
				 		+ KitRobotmock.log.toString(), KitRobotmock.log.containsString("Received message msgEmptyKit"));
		 assertEquals(
					"Only 1 message should have been sent to the KitRobotmock. Event log: "
							+ KitRobotmock.log.toString(), 1, KitRobotmock.log.size());
	}
}
