package factoryAgents.gantry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import agent.Agent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Gantry;
import gfxInterfaces.Gui;

import ServerClient.*;

public class GantryAgent extends Agent implements Gantry {

	public List<Integer> parts=new ArrayList<Integer>(); //list of possible parts
	public enum feederState{needsparts,fulfilled,purged,nothing}; //enum for feeder state
	public List<MyFeeder> Myfeeders=Collections.synchronizedList(new ArrayList<MyFeeder>()); //list of feeders
	public String name;
	public boolean on=true;
	public Semaphore s=new Semaphore(0); //semaphore for animation waiting
	Server gui; //instance of the animations for the gantry
	
	class MyFeeder{ //encapsulates the feeder and state and other such things
		Feeder feeder;
	    feederState state;
	    int partid;
	    int index;
	    boolean purged;
	    boolean needsparts;
	    public MyFeeder(Feeder feeder,int index)
	    {
	    	this.feeder=feeder;
	    	state=feederState.nothing;
	    	purged=false;
	    	needsparts=false;
	    	this.index=index;
	    }
	}
	
	public GantryAgent(String name) 
	{
		super();
		this.name=name;
	}
	
	/** scheulder**/
	public boolean pickAndExecuteAnAction()
	{
		if(on)
		{
			synchronized(Myfeeders)
			{
		for(MyFeeder f:Myfeeders)
		{
			if(f.state==feederState.purged||f.purged)
			{
				f.purged=false;
				refillPurgedBin(f);
				return true;
			}
		}
			}
			synchronized(Myfeeders)
			{
		for(MyFeeder f:Myfeeders)
		{
			if(f.state==feederState.needsparts||f.needsparts)
			{
				f.needsparts=false;
				f.state=feederState.fulfilled;
				serviceFeeder(f);
				return true;
			}
		}
			}
		}
		//goHome()?
		return false;
	}
	/** messages**/
	public void msgInitialize() { 
		on=true;
		stateChanged();
	}

	public void msgAtDestination() //gui calls this when animation is finished
	{
		print("got at destinatnion message");
		s.release();
	}
	
	public synchronized void msgIneedParts(Feeder feeder, int part) { //feeder calls this when it needs parts
		print("got msgIneedpart "+part);
		for(MyFeeder f:Myfeeders)
		{
			if(f.feeder==feeder)
			{
				f.needsparts=true;
				f.partid=part;
				f.state=feederState.needsparts;
			}
		}
		stateChanged();
	}
	
	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}

	public void msgPurgedFeeder(Feeder feeder) { //feeder calls this when it purges
		for(MyFeeder f:Myfeeders)
		{
			if(f.feeder==feeder)
			{
				f.purged=true;
				f.state=feederState.purged;
			}
		}
		stateChanged();
	}

	/**Actions**/
	public void refillPurgedBin(MyFeeder f) //moves a bin to home to 'refill' it
	{
		print("moving bin to home for feeder "+f.index);
		f.state=feederState.nothing;
		gui.doPutBinHome(f.index);
		try {
			s.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		stateChanged();
	}
	
	public void serviceFeeder(MyFeeder f) //gives the feeder the part it wants
	{
		print("doing service feeder with "+f.partid+" for "+f.index);
		f.state=feederState.fulfilled;
		gui.doServiceFeeder(f.partid,f.index);
		try {
			s.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		f.feeder.msgHereAreParts(f.partid);
		f.state=feederState.fulfilled;
		stateChanged();
	}
	
	/**hacks**/
	
	public void setGui(Server gui)
	{
		this.gui=gui;
	}
	
	public void setFeeders(List<Feeder> feeders)
	{
		int i=0;
		for(Feeder f:feeders)
		{
			Myfeeders.add(new MyFeeder(f,i));
			i++;
		}
	}
	
	public void setParts(List<Integer> parts)
	{
		this.parts=parts;
	}
	
	public String toString()
	{
		return name;
	}

}
