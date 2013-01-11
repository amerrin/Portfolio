package factoryAgents.fcs;


import factoryInterfaces.*;

import java.util.ArrayList;
import java.util.List;

import ServerClient.BlueprintKit;
import agent.Agent;

public class fcsAgent extends Agent implements FCS {

	private static final boolean LOG_AGENTS_ADDED = true;	//False to quit annoying dozen worthless messages
	
	//List<FactoryAgent> agents = new ArrayList<FactoryAgent>();
	//Could be replaced by KitCellDirector interface, so it's easier
	//	to switch to a different agent in command.
	KitRobot kitrobot;
	PartsRobot partsrobot;
	Gantry gantry;
	List<Feeder> feeders=new ArrayList<Feeder>();
	List<Lane> lanes=new ArrayList<Lane>();
	Conveyor	conveyor;
	boolean doInitialize = false;
	boolean doTurnOff=false;
	
	List<KitRequest> jobs = new ArrayList<KitRequest>();
	List<KitRequest> newRequests = new ArrayList<KitRequest>();
	
	public fcsAgent(Conveyor conveyor,Gantry gantry,KitRobot kitrobot,PartsRobot partsrobot, List<Feeder> feeders, List<Lane> lanes)
	{
		this.conveyor=conveyor;
		this.gantry=gantry;
		this.kitrobot=kitrobot;
		this.feeders=feeders;
		this.lanes=lanes;
		this.partsrobot=partsrobot;
	}
	////////////Classes//////////////
	enum KitState {requested, doing, done};
	class KitRequest {
		List<Integer> cfg;
		int num;
		KitState state = KitState.requested;
		KitRequest(List<Integer> cfg, int num) {
			this.cfg = cfg; this.num = num;
		}
	}
	
	
	////////////// Messages T////////////////////
	@Override
	public void msgInitialize() {
		print("MsgInitialize");
		doInitialize = true;
		stateChanged();
	}

	@Override
	public void msgMakeKits(List<Integer> cfg, int num) {
		
		
		print("MsgMakeKits "+num);
		conveyor.msgHereIsConfig(cfg, num);
		/*
		jobs.add(new KitRequest(cfg, num));
		stateChanged();*/
	}
	
	public void msgTurnOff()
	{
		doTurnOff=true;
		stateChanged();
	}

	/** Adds a new agent to the ones we know about.
	 * This agent will not be initialized until/unless FCSAgent is, 
	 * so general use is add all the agents at once at program start, then
	 * call msgInitialize() once, and never touch init or addagent again.
	 */
	/*@Override
	public void msgAddAgent(FactoryAgent a) {
		if (LOG_AGENTS_ADDED) print("MsgAddAgent");
		agents.add(a);
		stateChanged();
	}*/

	/** The Conveyor is special because it commands the kitrobot about what to
	 * matke. In the future we could refer to a KitCommander interface instead,
	 * so we can change who does the big thinking without touching FCSAgent.
	 * 
	 * Should be set before initialize()- undefined behavior else.
	 */
	@Override
	public void msgSetConveyor(Conveyor conveyor) {
		print("MsgSetConveyor");
		this.conveyor = conveyor;
		stateChanged();
	}
	
	
	////////////////////////////
	//	Scheduler
	////

	@Override
	public boolean pickAndExecuteAnAction() {
		
		if (doInitialize) {
			axnInitialize();
			doInitialize = false;
			return true;
		}
		
		if(doTurnOff){
			axnTurnOff();
			doTurnOff=false;
			return true;
		}
		
		//Hacky stuff starts, so comments do as well
		// Jobs is a queue of requests we are going to work on
		// Jobs[0] is the currently worked request
		// If jobs[0] is done, we should move to the next one
		//
		// As a hack, though, we will only have one job in the queue
		// So if a new request is found, the old job is cancelled
		// So for now, jobs can only hold one request max.
		
		if (newRequests.size() > 0) {
			axnHandleNewRequest();
			return true;
		}
		//edit by alex jones, added the jobs thing
		if (jobs.size() > 0){ //)!= null /* && jobs.get(0)!=null*/) {
			if (jobs.get(0).state == KitState.done) {
				axnHandleDoneJob();

				return true;
			}
			else if (jobs.get(0).state == KitState.requested)
			{
				axnStartMaking(jobs.get(0));
				jobs.get(0).state = KitState.doing;
				return true;
			}
		
		}
		
		
		return false;
	}
	
	
	
	
	
	
	//////////////////////////////
	//	Actions
	////
	
	
	private void axnInitialize() {
		print("AxnInitialize");
		for (Lane l : lanes) {
			l.msgInitialize();
		}
		for(Feeder f:feeders)
		{
			f.msgInitialize();
		}
		gantry.msgInitialize();
		kitrobot.msgInitialize();
		conveyor.msgInitialize();
		partsrobot.msgInitialize();
	}
	
	private void axnHandleNewRequest() {
		print("AxnHandleNewRequest");
		axnCancelCurrentJob();
		jobs.set(0, newRequests.get(newRequests.size()-1));
		newRequests.clear();
		
		axnStartMaking(jobs.get(0));
		
	}
	private void axnCancelCurrentJob() {
		//For now, only one job at a time- so clear out everything.
		jobs.clear();
		// Nothing- we're trying to guess what will go here later.
	}
	
	private void axnHandleDoneJob() {
		print("AxnHandleDoneJob");
		//for now, one job at a time, so clear out everything.
		jobs.clear();
	}
	
	private void axnTurnOff()
	{
		print("AxnTurnOff");
		for (Lane l : lanes) {
			l.msgTurnOff();
		}
		for(Feeder f:feeders)
		{
			f.msgTurnOff();
		}
		gantry.msgTurnOff();
		kitrobot.msgTurnOff();
		conveyor.msgTurnOff();
		partsrobot.msgTurnOff();
	}
	
	/** Actually tell the conveyor/big cheese what to start making. */
	private void axnStartMaking(KitRequest job) {
		conveyor.msgHereIsConfig(job.cfg, job.num);
		job.state = KitState.doing;
	}
	
	//////////////////////////////
	//	Do commands
	////
	
	// None, the FCS agent controls agents, not hardware.
	
}
