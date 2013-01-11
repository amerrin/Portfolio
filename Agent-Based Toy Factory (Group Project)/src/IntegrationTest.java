import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Semaphore;

import mockbase.MockGui;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import agent.Agent;
import factoryAgents.Conveyor.ConveyorAgent;
import factoryAgents.KitRobot.KitRobotAgent;
import factoryAgents.feeder.FeederAgent;
import factoryAgents.gantry.GantryAgent;
import factoryAgents.lane.LaneAgent;
import factoryAgents.nest.NestAgent;
import factoryAgents.partsRobot.PartsRobotAgent;
import factoryAgents.vision.VisionAgent;
import factoryInterfaces.Feeder;
import factoryObjects.Kit;


/** Fintan O'Grady */
public class IntegrationTest {

	ConveyorAgent	conveyorAgent;
	GantryAgent		gantryAgent;
	KitRobotAgent	kitRobotAgent;
	PartsRobotAgent	partsRobotAgent;
	VisionAgent		visionAgent;
	
	MockGui		mockGui;
	
	NestAgent[] nests = new NestAgent[8];
	LaneAgent[] lanes = new LaneAgent[8];
	FeederAgent[] feeders = new FeederAgent[4];
	
	
	@BeforeClass
	public static void setupLogging() {
		Agent.LOG_IN_LOG = true;
		LaneAgent.PART_SLIDE_TIME = 200;
	}
	
	
	@Before
	public void setupEverything() {
		print("=======Setup New Test=======");
		LaneAgent.LOG_EACH_PART = true;
		
		mockGui			= new MockGui("Mock Gui");
		
		conveyorAgent	= new ConveyorAgent("Conveyor", mockGui);
		gantryAgent		= new GantryAgent("Gantry");
		kitRobotAgent	= new KitRobotAgent("KitRobot", mockGui);

		for (int i=0; i<nests.length;i++)	nests[i] = new NestAgent(i);
		for (int i=0; i<lanes.length; i++)	lanes[i] = new LaneAgent(i);
		for (int i=0; i<feeders.length; i++)feeders[i] = new FeederAgent("Feeder", lanes[2*i], lanes[2*i+1], gantryAgent, i);
		
		
		partsRobotAgent	= new PartsRobotAgent();
		visionAgent		= new VisionAgent();
		
		
		
		partsRobotAgent . setGUI(mockGui);
		visionAgent		. setGui(mockGui);
		gantryAgent		. setGui(mockGui);
		for (int i=0; i<feeders.length; i++) feeders[i].setGui(mockGui);
		
		gantryAgent		. setFeeders(new ArrayList<Feeder>(Arrays.asList(feeders)));
		partsRobotAgent . setNests(nests);
		for (int i=0; i<nests.length;i++) {
			nests[i].setGUI(mockGui);
			nests[i].setLane(lanes[i]);
			nests[i].setVision(visionAgent);
			nests[i].setPartsRobot(partsRobotAgent);
		}
		for (int i=0; i<lanes.length;i++) {
			lanes[i].setGui(mockGui);
			lanes[i].setFeeder(feeders[i/2]);
			lanes[i].setNest(nests[i]);
		}
		
		msgInitializeEverything();
		System.out.println("...Starting test...   ...   ...   ...");
	}
	
	private void msgInitializeEverything() {
		conveyorAgent.msgInitialize();
		for (int i=0;i<feeders.length;i++) feeders[i].msgInitialize();
//		gantryAgent.msgInitialize();
		kitRobotAgent.msgInitialize();
		for (int i=0;i<lanes.length;i++) lanes[i].msgInitialize();
//		nestAgent.msgInitialize();
//		partsRobotAgent.msgInitialize();
		visionAgent.msgInitialize();
		
	}
	
	private void msgStartThreadEverything() {
		conveyorAgent.startThread();
		for (int i=0;i<lanes.length;i++) lanes[i].startThread();
		gantryAgent.startThread();
		kitRobotAgent.startThread();
		for (int i=0;i<feeders.length;i++) feeders[i].startThread();
		for (int i=0; i<nests.length;i++) nests[i].startThread();
		partsRobotAgent.startThread();
		visionAgent.startThread();
	}
	
	
	
	
	@Test
	public void testNestRequestsPartsLane() {
		wait(100);
		print("=Testing nest requesting lane=");
		
		nests[0].msgNeedPart(4);
		pex(nests[0]);
		assertTrue("Nest should have requested lane for parts", lanes[0].log.containsString("MsgIneedParts 4"));
	}
	@Test
	public void testFeederStartup() {
		wait(100);
		print("Testing feeder startup");
		pex(feeders[0]);
		assertFalse("Feeder should not be requesting gantry immediately", feeders[0].log.containsString("requesting parts"));
	}
	@Test
	public void testFeederGivesPartsCorrect() {
		
	}
	@Test
	public void test201FullRun() {
		LaneAgent.PART_SLIDE_TIME = 200;
		
		wait(100);
		print("==TestFullRun==");
		Kit kit = new Kit();
		for (int i=0;i<8;i++) {
			kit.addPart(i);
		}
		
		
		partsRobotAgent.msgEmptyKit(1, kit);
		pex(partsRobotAgent);
		assertTrue("PartsRobot should have requested parts from the first nest",
				nests[0].log.containsString("Got message to send part to PartsRobot 0"));
		pex(nests[0]);
		assertTrue("NestAgent should have requested parts from the lane", lanes[0].log.containsString("MsgIneedParts 0"));
		
		pex(lanes[0]);
		assertTrue("Lane should have requested parts from the feeder", feeders[0].log.containsString("got msgIneedpart 0from lane"));
		
		pex(feeders[0]);
		assertTrue("Feeder should have requested parts from the gantry", gantryAgent.log.containsString("got msgIneedpart"));
		doReleaseLater(gantryAgent.s, 100);
		pex(gantryAgent);
		
		assertTrue("Gantry should have started feding", gantryAgent.log.containsString("doing service feeder"));
		assertTrue("Feeder should have gotten parts", feeders[0].log.containsString("I got parts"));
		pex(feeders[0]);
		
		feeders[0].msgFedPart();
		pex(feeders[0]);
		
		
		assertTrue("Feeder sholud have fed a part", feeders[0].log.containsString("I have fed"));
		assertTrue("Lane should have gotten a part", lanes[0].log.containsString("MsgHeresPart 0"));
		pex(lanes[0]);
		assertFalse("Lane shouldn't have passed part on so quick", lanes[0].log.containsString("MsgHereIsPart 0"));
		wait(LaneAgent.PART_SLIDE_TIME+50);
		pex(lanes[0]);
		assertTrue("Lane should have passed part on by now", nests[0].log.containsString("MsgHereIsPart 0"));
		pex(nests[0]);
		
		for (int i=0; i<9; i++) {
			feeders[0].msgFedPart();
			pex(feeders[0]);
			pex(lanes[0]);
		}
		wait(250);
		pex(lanes[0]);
		pex(nests[0]);
		pex(lanes[0]);
		
		assertTrue("Nest should have tried to get picture", visionAgent.log.containsString("MsgTakeNestPicture"));
		
		pex(visionAgent);
		assertTrue("Vision should have returned analysis to nest", nests[0].log.containsString("Received analysis from camera"));

		pex(nests[0]);
		
		doReleaseLater(partsRobotAgent.animationBlocker, 200);
		pex(partsRobotAgent);
		doReleaseLater(partsRobotAgent.animationBlocker, 200);
		pex(partsRobotAgent);
		
		
		
	}
	
	
	
	
	//Helpers
	private void pex(Agent agent) {
		while(agent.pickAndExecuteAnAction()) {}
	}
	private void pex(Collection<Agent> agents) {
		for (Agent each : agents) {
			pex(each);
		}
	}
	private void pex(Agent[] agents) {
		for (int i=0;i<agents.length;i++) {
			pex(agents[i]);
		}
	}
	private void wait(int millis) {
		try { Thread.sleep(millis); } catch (InterruptedException e) {}
	}
	private void print(String msg) {
		System.out.println(msg);
	}
	//For testing, call the feeder's fed a couple times, as though the thing was running.
	private void makeGuiFeederFeed(Feeder feeder, int num) {
		//TODO
		
	}
	//For testing, we have all agents on one thread, but some of the schedulers will lock until other
	//	things call them, so we have to fake some multithreading.
	private void doReleaseLater(final Semaphore s, int delay) {
		Timer t = new Timer();
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				s.release();
				System.out.println("Debug Semaphore released");
				this.cancel();
			}
			
		}, delay);
	}

}
