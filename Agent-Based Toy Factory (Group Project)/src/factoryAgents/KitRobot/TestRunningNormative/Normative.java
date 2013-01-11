package factoryAgents.KitRobot.TestRunningNormative;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.Timer;
import interfaces.Conveyor;
import interfaces.KitRobot;
import mock.MockConveyor;
import mock.MockPartsRobot;
import mock.MockVisionAgent;
import VisionAgent.KitVisionResult;
import VisionAgent.VisionAgent;
import FactoryAgents.ConveyorAgent;
import FactoryAgents.Kit;
import FactoryAgents.KitRobotAgent;
import FactoryAgents.NestAgent;
import FactoryAgents.Part;
import FactoryAgents.PartsRobotAgent;
import GUI.*;
import junit.framework.TestCase;

public class Normative extends TestCase{
	
	public KitAssembly kitAssembly = new KitAssembly();
	
	public void testKitManagement() {
		JFrame frame = new JFrame();
		frame.add(kitAssembly);
		frame.setVisible(true);
		frame.setSize(500, 500);
		 
		PartsRobotAgent partsRobotmock = new PartsRobotAgent(kitAssembly);
		VisionAgent visionAgent = new VisionAgent("visionAgent");
		NestAgent nest = new NestAgent(0);
		Part p = new Part(1, 2);
		Part p1 = new Part(2, 2);
		List<Integer> conf = new ArrayList<Integer>();
		//conf.addPart(3);
		//conf.addPart(3);
		//conf.addPart(3);
		//conf.addPart(3);
		conf.add(1);
		conf.add(1);
		conf.add(1);
		conf.add(1);
		
		KitRobotAgent kitRobot = new KitRobotAgent("KitRobotAgent", kitAssembly);
		ConveyorAgent conveyor = new ConveyorAgent("conveyor", kitAssembly);
		
		kitAssembly.setKitRobot((KitRobot) kitRobot);
	    kitAssembly.setConveyor((Conveyor) conveyor); 
		
		conveyor.setKitRobot(kitRobot);
		partsRobotmock.setKitRobot(kitRobot);
		kitRobot.setConveyor(conveyor);
		kitRobot.setVisionAgent(visionAgent);
		kitRobot.setPartsRobot(partsRobotmock);
		Kit emptyKit = new Kit(conf);
		 
		conveyor.msgInitialize();
		kitRobot.msgInitialize();
		 
		conveyor.msgHereIsConfig(conf, 3);
		//conveyor.msgHereIsConfig(conf, 1);
		conveyor.pickAndExecuteAnAction();
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		//kitRobot.pickAndExecuteAnAction();
	
		// making gripper full (since no nest here)
		 partsRobotmock.msgGripperFull();
	
		partsRobotmock.pickAndExecuteAnAction();
		kitRobot.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		 
		kitRobot.pickAndExecuteAnAction();

		visionAgent.pickAndExecuteAnAction();
	
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		
		
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		// making gripper full (since no nest here)
	    partsRobotmock.msgGripperFull();
	
		partsRobotmock.pickAndExecuteAnAction();
		kitRobot.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		 
		kitRobot.pickAndExecuteAnAction();

		visionAgent.pickAndExecuteAnAction();
	
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		
		
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		// making gripper full (since no nest here)
	    partsRobotmock.msgGripperFull();
	
		partsRobotmock.pickAndExecuteAnAction();
		kitRobot.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		
		partsRobotmock.pickAndExecuteAnAction();
		 
		kitRobot.pickAndExecuteAnAction();

		visionAgent.pickAndExecuteAnAction();
	
		kitRobot.pickAndExecuteAnAction();
		conveyor.pickAndExecuteAnAction();
		
		
		/*conveyor.msgHereIsConfig(conf, 1);
		//conveyor.msgAnimationDone();
		conveyor.pickAndExecuteAnAction();
		//conveyor.msgAnimationDone();
		
		//kitRobot.msgHereIsKit(emptyKit);
		// kitRobot.msgAnimationDone();
		kitRobot.pickAndExecuteAnAction();
		 
	//	assertTrue("Mock partsRobot should have received message with the kit. Event log: "
		//		 		+ partsRobotmock.log.toString(), partsRobotmock.log.containsString("Received message msgEmptyKit"));
		 
		// assert of do methods are called
		//kitRobot.msgCanIGoToTable();
	/*	partsRobotmock.gripper.arms.set(0, 3);
		partsRobotmock.gripper.arms.set(1, 3);
		partsRobotmock.gripper.arms.set(2, 3);
		partsRobotmock.gripper.arms.set(3, 3);
		*/
		//partsRobotmock.pickAndExecuteAnAction();
		 
		/*kitRobot.pickAndExecuteAnAction();
		 

//		partsRobotmock.msgYesYouCan();
		partsRobotmock.pickAndExecuteAnAction();
		//assertTrue("Mock partsRobot should have received message with the answer. Event log: "
			//	 		+ partsRobotmock.log.toString(), partsRobotmock.log.containsString("Received message msgYesYouCan"));
		 
		// check if nothing new
		emptyKit.addPartHeld(1);
		emptyKit.addPartHeld(2);
		//kitRobot.msgKitIsReady(1);
		 
		kitRobot.pickAndExecuteAnAction();

		kitRobot.msgKitStatus(new KitVisionResult(true));
		kitRobot.pickAndExecuteAnAction();
		//conveyor.msgAnimationDone();
		//kitRobot.pickAndExecuteAnAction();*/
		}
}
