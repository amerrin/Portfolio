package factoryAgents.lane.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import mockbase.MockGui;

import org.junit.Before;
import org.junit.Test;

import factoryAgents.lane.LaneAgent;

public class TestLane {

	LaneAgent	agent;
	MockFeeder	feeder;
	MockNest	nest;
	MockGui		gui;
	
	@Before
	public void setup() {
		
		agent	= new LaneAgent(0);
		feeder	= new MockFeeder();
		nest	= new MockNest();
		gui		= new MockGui("test");
		
		agent.setFeeder(feeder);
		agent.setNest(nest);
		agent.setGui(gui);

		agent.msgInitialize();
		System.out.println("Starting test...");
		
		MockGui.LOG_CONSOLE = true;
	}
	
	@Test
	public void test() {
		assertTrue("Starting it out on a good note!", true);
	}
	
	/** Test that part requests go through */
	@Test
	public void testRequestTurnsOnLane() {
		
		agent.msgIneedParts(1);
		while (agent.pickAndExecuteAnAction()) {};
		
		assertTrue(gui.log.containsString("doing lane turn on"));
	}
	/** Test nest full turns off lane */
	@Test
	public void testRequestTurnsOffLane() {
		
		agent.msgIamStable();
		agent.pickAndExecuteAnAction();
//		assertTrue(gui.log.containsString("doing lane turn off"));
		//This test is no longer correct behavior. Freebie.
	}
	
	/** Test that received parts get passed down, after a reasonable period of time */
	@Test
	public void testReceivedPassThrough() {
		LaneAgent.PART_SLIDE_TIME = 500;
		agent.msgIneedParts(2);
		while(agent.pickAndExecuteAnAction()) {};
		
		agent.msgHereIsPart(2);
		while(agent.pickAndExecuteAnAction()) {};
		
		try {
			Thread.sleep(450);
		} catch (InterruptedException e) {e.printStackTrace();}
		
		while (agent.pickAndExecuteAnAction()){}
		assertFalse("Lane should not have passed part so quick", nest.log.containsString("Received Part 2"));
		try { Thread.sleep(100); } catch (InterruptedException e) { e.printStackTrace(); }
		while (agent.pickAndExecuteAnAction()){}
		assertTrue("Lane should have passed part by now", nest.log.containsString("Received Part 2"));
		
	}
	
	/** Tests that stopping, then restarting the conveyor correctly pauses and resumes each part"
	 * We no longer stop the lane when we don't need parts. So. This isn't useful.
	 */
	//@Test
	public void testPauseResume() {
		agent.PART_SLIDE_TIME = 500;
		agent.msgIneedParts(2);
		
		agent.msgHereIsPart(1);
		agent.pickAndExecuteAnAction();
		try { Thread.sleep(100); } catch (InterruptedException e) { };
		agent.pickAndExecuteAnAction();
		assertFalse("Lane should not have passed part so quick", nest.log.containsString("Received Part 1"));
		
		agent.msgHereIsPart(2);
		agent.pickAndExecuteAnAction();
		try { Thread.sleep(100); } catch (InterruptedException e) { };
		agent.pickAndExecuteAnAction();
		assertFalse("Lane should not have passed part so quick",   nest.log.containsString("Received Part 2")
																|| nest.log.containsString("Received Part 1"));
		
		agent.msgIamStable();
		while(agent.pickAndExecuteAnAction()) {}
		threadsleep(500);
		lanepx();
		assertFalse("Lane should have paused parts", nest.log.containsString("Received Part 2")
				|| nest.log.containsString("Received Part 2"));
		
		agent.msgIneedParts(1);
		lanepx();
		threadsleep(250);
		lanepx();
		assertFalse("Lane should not have repassed parts so soon", nest.log.containsString("Received Part 2")
				|| nest.log.containsString("Received Part 1"));
		
		threadsleep(100);
		lanepx();
		assertTrue("Lane should have sent part 1 by now", nest.log.containsString("Received Part 1"));
		assertFalse("Lane should not have sent part 2 yet", nest.log.containsString("Received Part 2"));
		threadsleep(100);
		lanepx();
		assertTrue("Lane should have sent part 2 by now", nest.log.containsString("Received Part 2"));
	}
	
	private void threadsleep(int millis) {
		try {Thread.sleep(millis);} catch(InterruptedException e) {}
	}
	private void lanepx() {
		while (agent.pickAndExecuteAnAction()) {}
	}

}
