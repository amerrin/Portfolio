
package factoryAgents.lane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Timer;

import agent.Agent;
import factoryInterfaces.FactoryAgent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Lane;
import factoryInterfaces.Nest;
import gfxInterfaces.Gui;

//In v1, lane is always on, except for dumping, so everything is pretty fine.
/* Explanation:
 * Each part is declared to have arrived via our msgHereIsPart.
 * Arrived parts are enclosed by a MyPart
 * Upon arrival, each MyPart's timer is started- this signifies expected
 *	"I got to the end" time.
 * When the MyPart gets to the end, the lane has to decide what happened to it.
 * If there is no stacking, then it just is passed directly to the Nest and removed
 * Otherwise, it is added to the stackedParts.
 * If the nest is full, it will just be stored there.
 * Once the nest is empty and the lane is on again, though, the stacked parts are flowing!
 * The stackClock fires every time we expect a stacked part has been sent to the nest
 * The first part in the stackedParts should be sent down, then, and the rest automatically
 *	moved up.
 * We can turn off stackClock when we're out of stacked parts, but it's not really necessary.
 * 
 * We have to do special things with the parts' timers if we turn off while some are still en route.
 */
public class LaneAgent extends Agent implements Lane, FactoryAgent {

	//Constants
	/** How long it takes for a part to slide down the lane.
	 * This is used to guess how long to wait besfore telling the nest
	 * they have a part- we don't actually know where the parts are.
	 */
	public static int	PART_SLIDE_TIME = 6500;	//in (ms)
	/** How long it takes a stacked part to be dumped into a nest before
	 *	 the next part can do the same- for when parts are fed from a stacked
	 *	 up lane.
	 */
	public static int PART_UNSTACK_INTERVAL = 500; //in (ms)
	/** How long to wait in our first try at unsticking missing parts.
	 * If the nest can't find parts that we say we've given it, we do a 
	 * 	troubleshooting process. Step one is to just wait. This is how
	 *	long.
	 */
	public static int FIRST_WAIT_TIME	= 3000; // (ms)
	/** Time to wait for lane increase. */
	public static int SECOND_WAIT_TIME	= 5000; // (ms)
	/** Time to wait after feeder says something's fixed to asking Lane.*/
	public static int THIRD_WAIT_TIME	= 10000;// (ms)
	private int index;
	
	
	
	public LaneAgent(int num) {
		super();
		index = num;
		
	}
	////////////////
	//	Objects
	////
	/** List of all parts in our system */
	List<MyPart> receivedParts	= new ArrayList<MyPart>();
	/** List of parts stacked at the end of the lane */
	List<MyPart> stackedParts	= new ArrayList<MyPart>();
	
	Timer stackClock;
	
	enum Event { needParts, full, stackShifted, dumping, partsMissing, checkFix, fixWorked, fixDidntWork, 
		feederCheckFix};
	List<Event> events = new ArrayList<Event>();
	
	enum NestState { full, notfull }; // dumping will use notfull- parts not given.
	NestState nestState = NestState.notfull;
	
	//During debug, we need to keep track of the steps we'veo taken.
	enum DebugState { working, wait, increaseFrequency, feeder };
	DebugState debugState = DebugState.working;
	
	int curPartType;

	
	
	boolean highFq = false; //Gfx doesn't actually speed up, though.
	Feeder	feeder;
	Nest	nest;
	Gui		gui;
	boolean on;
	
	
	/////////////////
	// Classes
	////
	/** This isn't particularly needed, we could just do it passing strings,
	 * but might as well have half a data structure to keep track of what's
	 * on us.
	 */
	class MyPart {
		int name;
		
		
		//We use these two to keep track of part positions when the lane is paused.
		// When the timer is stopped, we add the total time that was spent 'on'-
		//	the system current time minus the time start- to the timeSpent.
		// When we turn on again, we subtract the time we've already spent from the
		//	normal amount of time a part takes to slide down.
		long timeSpent; //stored time we've spent going down the lane
		long timeStart; //last time we were started down the lane.
		PartState state = PartState.New;
		Timer timer; //This fires when we expect the part to have reached the end
		
		MyPart(int name) {
			this.name = name;
			timeStart = System.currentTimeMillis();
		}
		
		void startTimer() {
			//We really shouldn't be starting a timer for things that are not moving
			if (state != PartState.moving && state != PartState.New) {
				print("Error: expected lane end timer called for nonmoving object");return;}
			if (timer != null ) timer.stop();
			timeStart = System.currentTimeMillis();
			timer = new Timer((int) (PART_SLIDE_TIME-timeSpent), new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					LaneAgent.this.msgPartAtEnd(MyPart.this);
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
		void stopTimer() {
			timeSpent += System.currentTimeMillis()-timeStart;
			timer.stop();
		}
	}
	enum PartState { New, moving, atEnd, stacked, gone };
	
	////////////////
	//	Messages
	////
	@Override
	public void msgInitialize() {
		print("MsgInitialize");
		if (feeder == null) print("ERROR: Lane's feeder not set");
		if (nest   == null) print("ERROR: Lane's nest not set");
		if (gui	   == null) print("ERROR: Lane's gui not set");
		on=false;;
		stackClock = new Timer(PART_UNSTACK_INTERVAL, 
				new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						LaneAgent.this.msgStackShifted();
					}
		});
		stackClock.start();
		
		stateChanged();
	}
	

	// We just stop the lane until we get a msgNeedParts
	@Override
	public void msgDumpParts() {
		print("MsgDumpParts");
		events.add(Event.dumping);
		stateChanged();
	}
	
	@Override
	public void msgIamStable() {
		print("MsgIamStable");
		events.add(Event.full);
		stateChanged();
	}
	@Override
	public void msgIneedParts(int part) {
		print("MsgIneedParts "+part);
		curPartType = part;
		events.add(Event.needParts);
		stateChanged();
	}
	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}
	@Override
	public void msgHereIsPart(int part) {
		print("MsgHeresPart "+part);
		receivedParts.add(new MyPart(part));
		stateChanged();
	}
	
	//Called when a part "should" have reached the end.
	//	We should handle if it didn't because parts are stacked up.
	public void msgPartAtEnd(MyPart part) {
		print("MsgPartAtEnd "+part.name);
		part.state = PartState.atEnd;
		stateChanged();
	}
	//Called when a new stacked part should be sent down.
	//	prevents an entire stack from being sent at once when a nest
	//	becomes empty.
	public void msgStackShifted() {
		events.add(Event.stackShifted);
		stateChanged();
	}
	//Called when we've been giving the Nest we feed wrong information-
	//	check for lanes blocked, algorithm errors, etc.
	public void msgPartsMissing() {
		gui.appendText(this+": got message parts missing!");
		print("MsgPartsMissing");
//		gui.DoLaneFrequencyHigh(index);
		events.add(Event.partsMissing);
		stateChanged();
	}
	//Called by ourselves att debugging to check if stuff has been fixed.
	public void msgCheckIfFixed() {
		gui.appendText(this+": Should check whether the problem is fixed now");
		print("MsgCheckIfFixed");
		events.add(Event.checkFix);
		stateChanged();
	}
	//Called by feeder if it's tried something to fix a jam
	public void msgDidThatFixIt() {
		gui.appendText(this+": Was asked whether a fix worked by the feeder");
		print("MsgDidThatFixIt from feeder");
		events.add(Event.checkFix);
		stateChanged();
	}
	
	
	@Override
	public void msgFixedStatus(boolean fixed) {
		print("msgFixedStatus "+fixed);
		if (fixed) {
			events.add(Event.fixWorked);
		}
		else {
			events.add(Event.fixDidntWork);
		}
		stateChanged();
	}
	
	///////
	//	Hacks
	////
	public void setFeeder(Feeder feeder) {
		this.feeder = feeder;
	}
	public void setNest(Nest nest) {
		this.nest = nest;
	}
	
	
	//////////////////
	//	Scheduler
	////
	@Override
	public boolean pickAndExecuteAnAction() {
		/** Receiving parts is time critical, since the parts will
		 * continue to slide even if we're busy.
		 * This is where agents fails a little- we need a realtime system here.
		 */
		for (MyPart p : receivedParts) {
			if (p.state == PartState.New) {
				axnHandleReceivedPart(p);
				return true;
			}
		}
		
		if (events.size() > 0) {
			Event event = events.remove(0);
			
			//Call to ask Nest about fix.
			if (event == Event.checkFix) {
				axnCheckFixWorked();
				return true;
			}
			if (event == Event.fixWorked) {
				axnFixWorked();
				return true;
			}
			//Called by us to immediately ask for fix
			if (event == Event.fixDidntWork) {
				axnFixDidntWork();
				return true;
			}
			//Called by feeder to check fix in a little while
			if (event == Event.feederCheckFix) {
				axnHandleFeederWantsCheck();
				return true;
			}
			if (event == Event.stackShifted) {
				axnStackShifted();
				return true;
			}
			if (event == Event.partsMissing) {
				axnHandlePartsMissing();
				return true;
			}
			if (event == Event.dumping) {
				axnHandleDumping();
				return true;
			}
			if (event == Event.full) {
				axnNestFull();
				return true;
			}
			if (event == Event.needParts) {
				axnNestNeedParts();
				return true;
			}
			
			print("Event skipped: "+event);
		}
		
		
		for (MyPart p : receivedParts) {
			if (p.state == PartState.atEnd) {
				axnPartAtEnd(p);
				return true;
			}
		}
		
		
		
		return false;
	}

	
	/////////////////
	//	Actions
	////
	
	private void axnNestFull() {
		print("axnNestFull");
		nestState = NestState.full;
		feeder.msgIamStable(this);
		//doLaneStop();
		
		for (MyPart p : receivedParts) {
			if (p.state == PartState.moving) { p.stopTimer(); }
			
		}
		
		
	}
	
	/** HandlePartsMissing!
	 * 
	 * Nest has said it's not seeing parts that we're giving to it. So
	 *	we need to figure out why.
	 *
	 * Alternately, Nest says a fix hasn't worked.
	 * Don't call us if a fix has worked, though. Else bad.
	 * 
	 * Sequence: 
	 * 1) Wait a while. Maybe it was the server skipping out, or just
	 *	a temporary thing.
	 * 2) Try increasing the lane frequency. This should unjam any jams
	 * 	in the lane.
	 * 3) There is nothing else that we can do. Tell the Feeder about it,
	 * 	let him try and figure things out.
	 * 
	 * Recovery:
	 * Let's see if it's terribly broken first before fixing it.
	 * 
	 */
	private void axnHandlePartsMissing() {
		gui.appendText(this+": AxnHandlePartsMissing");
		print("AxnHandlePartsMissing");
		
		//First try: Just wait a while.
		if (debugState == DebugState.working) {
			print("  Fix: Try waiting");
			gui.appendText(this+":   Try: Try Waiting ");
			debugState = DebugState.wait;
			checkFixIn(FIRST_WAIT_TIME);
			
			return;
		}
		
		//Second try: Increase frequency.
		if (debugState == DebugState.wait) {
			print("  Fix: Try increasing frequency");
			gui.appendText(this+":   Try: Try increasing frequency");
			debugState = DebugState.increaseFrequency;
			doLaneFrequencyHigh();
			checkFixIn(SECOND_WAIT_TIME);
			return;
		}
		
		//Third+ try: There is no third try. Let the Feeder deal with it.
		if (debugState == DebugState.increaseFrequency) {
			debugState = DebugState.feeder;
			print("  Fix: Tell the Feeder");
			gui.appendText(this+":   Try: Tell the feeder");
			feeder.msgFixYourSelf(this);
			return;
		}
		if (debugState == DebugState.feeder) {
			print("  Fix: Tell Feeder fix didn't work");
			gui.appendText(this+":   Try: Tell the feeder fix didn't work");
			//No need to keep lane too fast
			doLaneFrequencyLow();
			feeder.msgFixedStatus(false, this);
			return;
		}
		
	}
	/** See if a fix worked- basically just ask NestAgent to take another
	 * pic of itself, and report back if it's getting parts now.
	 */
	private void axnCheckFixWorked() {
		print("AxnCheckFixWorked");
		gui.appendText(this+": Check if fix worked");
		nest.msgDidThatFixIt();
	}
	/** Call our msgCheckFix in delay milliseconds.
	 * Most fixes take time, so this is a common convenience for them.
	 * @param delay
	 */
	private void checkFixIn(int delay) {
		gui.appendText(this+": Check fix in "+delay+" ms");
		Timer t = new Timer(delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				LaneAgent.this.msgCheckIfFixed();
			}
		});
		t.setRepeats(false);
		t.start();
	}
	/** Gaah, forgot about this one, so not everything's organized nice.
	 * The Feeder thinks he's solved something, so wants us to check.
	 * We'll have to wait a while for the "fix" to reach the lane.
	 */
	private void axnHandleFeederWantsCheck() {
		print("AxnHandleFeederWantsCheck");
		gui.appendText(this+": AxnHandleFeederWantsCheck");
		checkFixIn(THIRD_WAIT_TIME);
	}
	/** If whatever we did worked, we should try to figure out our current
	 * state.
	 */
	private void axnFixWorked() {
		print("AxnFixWorked");
		gui.appendText(this+": Fix worked");
		//Return to normal frequency
		doLaneFrequencyLow();
		
		if (debugState == DebugState.feeder) {
			feeder.msgFixedStatus(true, this);
		}
		
		debugState = DebugState.working;
		//pretend like we don't have to do anything.
	}
	/** If what we did didn't work, we need to escalate. Or something. */
	private void axnFixDidntWork() {
		print("AxnFixDidntWork");
		gui.appendText(this+": AxnFixDidn'tWork");
		axnHandlePartsMissing();
	}
	
	
	private void axnNestNeedParts() {
		print("AxnNestNeedParts");
		nestState = NestState.notfull;
		feeder.msgIneedParts(this, curPartType);
		doLaneStart();
		
		for (MyPart p : receivedParts) {
			if (p.state == PartState.moving) {p.startTimer();}
		}
		
	}
	
	private void axnPartAtEnd(MyPart part) {
		//If there's parts stacked, it doesn't matter if the nest is empty or full-
		//	we become part of the stack, where the stack clock will take care of us.
		if (stackedParts.size() != 0) {
			axnHandlePartStacked(part);
			return;
		}
		//If there's nothing stacked, and the nest's not full, we get a fast pass to
		//	the nest.
		if (nestState == NestState.notfull) {
			axnHandleNestFed(part);
			return;
		}
		//If the nest's full but there's no parts stacked, we must be the first part to
		//	be stacked. We just get standard stacked treatment, though.
		if (nestState == NestState.full) {
			axnHandlePartStacked(part);
			return;
		}
		
	}
	private void axnHandleNestFed(MyPart part) {
		part.state = PartState.gone;
		nest.msgHereIsPart(part.name);
		receivedParts.remove(part);
		print("AxnHandleNestFed "+part.name);
	}
	
	private void axnStackShifted() {
		//If a stacked part fell out, tell the world!
		if (on && stackedParts.size() != 0 && nestState != NestState.full) {
			print("AxnStackShifted:");
			axnHandleNestFed(stackedParts.remove(0));
		}
		
	}
	private void axnHandlePartStacked(MyPart part) {
		stackedParts.add(part);
		part.state = PartState.stacked;
		print("AxnHandlePartStacked: type "+part.name+" pos(st0): "+(stackedParts.size()-1));
	}
	
	private void axnHandleReceivedPart(final MyPart p) {
		print("AxnHandleReceivedPart");
		p.state = PartState.moving;
		if (on) {
			p.startTimer();
		}
	}
	
	/** Handling dumping: Stop the lane and feeder.
	 * When the nest is done dumping, it will call msgNeedParts as usual,
	 *	where we can do the same old stuff. The nest will deal with incorrect
	 *	parts still being fed, and tell us again to stop if it needs to dump some
	 *	more.
	 *
	 * We set nestState to full to emulate parts not going into the nest and force no
	 * 	messages, but really nothing should be using it.
	 */
	private void axnHandleDumping() {
		print("AxnHandleDumping");
		nestState = NestState.full;
		feeder.msgIamStable(this);
		doLaneStop();
	}
	
	//GUI
	public void setGui(Gui gui) { this.gui = gui; }
	private void doLaneStop() {
		for (MyPart p : receivedParts) {
			if (p.state == PartState.moving) {
				p.stopTimer();
			}
		}
		gui.DoLaneTurnOff(index);
		on = false;
	}
	private void doLaneStart() {
		for (MyPart p : receivedParts) {
			if (p.state == PartState.moving) {
				p.startTimer();
			}
		}
		gui.DoLaneTurnOn(index);
		on = true;
	}
	private void doLaneFrequencyHigh() {
		gui.DoLaneFrequencyHigh(index);
		highFq = true;
	}
	private void doLaneFrequencyLow() {
		gui.DoLaneFrequencyLow(index);
		highFq = false;
	}

	@Override
	public String getName() {
		return "LaneAgent "+index;
	}
	@Override
	public String toString() {
		return "LaneAgent "+index;
	}

	
	
	
	
}
