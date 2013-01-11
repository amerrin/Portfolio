package factoryAgents.feeder;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Timer;

import agent.Agent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Gantry;
import factoryInterfaces.Lane;
import gfxInterfaces.Gui;

import ServerClient.*;

public class FeederAgent extends Agent implements Feeder {

	MyLane current; //the lane you are currently servicing (defaults to the top one)
	MyLane other; //the lane you are not servicing
	Gantry gantry; //gantry 
	enum laneState{none,requestedParts,feeding,stable} //states the lanes can be in
	boolean divert=false; // true if you need to change lanes
	boolean hasparts=false; //true if gantry gave it parts
	boolean feedPart=false; // true if the lane needs a part to be fed
	boolean on=true; //true if on 
	boolean partslow=false; //true if the parts are low
	boolean feeding=false; //true if the feeder is currently feeding
	boolean requested=false; //true if the feeder is waiting for parts from the gantry
	int partsCounter=0; //how many parts were fed
	int currentpart = -1; //the current type of part being fed
	int index; //indentifier for 200 side stuffs
	String name; //name of the feeder
	Gui gui; //reference to the animation
	
	class MyLane { //class to keep track of the lanestate and which part it is asking for
		Lane lane;
		int partid;
		laneState state=laneState.none;
		public MyLane(Lane lane)
		{
			this.lane=lane;
		}
	}
	public FeederAgent(String name,Lane laneA,Lane laneB,Gantry gantry, int index)
	{
		super();
		this.name=name;
		current=new MyLane(laneA);
		other=new MyLane(laneB);
		this.index=index;
		this.gantry=gantry;
	}
	
	/** scheduler**/
	
	public boolean pickAndExecuteAnAction()
	{
		if(on)
		{
		if(divert&&hasparts ||hasparts&& currentpart!=current.partid)
		{
			purgeCycle();
			return true;
		}
		if(divert)
		{
			switchLanes();
			return true;
		}
		if(partslow&&current.state==laneState.feeding||(!hasparts&&!requested)&&current.state==laneState.requestedParts)
		{
			requestParts();
			return true;
		}
		if(current.state==laneState.stable&&other.state!=laneState.requestedParts&&feeding)
		{
			stopfeed();
			return true;
		}
		if(hasparts&&feedPart)
		{
			feedPart(); 
			return true;
		}
		}
		return false;
	}
	
	/** actions**/
	public void purgeCycle() //dumps parts and switches lanes
	{
		print("doing purge");
		//gui.setPurge();
		gui.stopFeeding(index);
		//on=false;
		hasparts=false;
		feeding=false;
		if(divert)
		{
			//gui.divert(index);
			switchLanes();
		}
		divert=false;
		partsCounter=0;
		gantry.msgPurgedFeeder(this);
		currentpart=current.partid;
		stateChanged();
	}
	
	public void switchLanes() //changes lanes
	{
		MyLane temp=current;
		current=other;
		other=temp;
		divert=false;
		feedPart=false;
		gui.divert(index);
		stateChanged();
	}
	public void stopfeed()
	{
		feeding=false;
		gui.stopFeeding(index);
		stateChanged();
	}
	public void requestParts() //asks the gantry for a type of part
	{
		print("requesting parts");
		gantry.msgIneedParts(this,current.partid);
		requested=true;
		partslow=false;
		stateChanged();
	}
	
	public void feedPart() //feeds one part to the current lane
	{
//		partsCounter++;
		print("I am feeding parts to "+current.lane);
		feedPart=false;
		feeding=true;
		gui.startFeeding(index);
		current.state=laneState.feeding;
//		print("I fed a part. total "+ partsCounter+" times");
//		current.lane.msgHereIsPart(current.partid);
		stateChanged();
	}
	
	public void askIfItIsFixed(Lane lane)
	{
		gui.appendText(this+" Seeing if the issue is fixed");
		lane.msgDidThatFixIt();
		stateChanged();
	}
	
	/**Messages**/
	public void msgInitialize() { //turns it on doeesnt really do anything
		on=true;
		stateChanged();
	}
	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}
	/** handles requests for parts, if the other lane requests parts and the current one doesnt
	 * need service the feeder gets ready to switch
	 */
	@Override
	public synchronized void msgIneedParts(Lane lane, int partid) {
		print("got msgIneedpart "+partid+"from lane");
		if(current.lane==lane)
		{
			current.partid=partid;
			if(hasparts)
			{
				current.state=laneState.feeding;
				feedPart=true;
			}
			else
				current.state=laneState.requestedParts;
		}
		else
		{
			other.state=laneState.requestedParts;
			other.partid=partid;
			if(current.state==laneState.none||current.state==laneState.stable)
				divert=true;
		}
		stateChanged();
	}

	public void msgFedPart()
	{
		partsCounter++;
		print("I have fed "+partsCounter+" parts of type "+currentpart);
		current.lane.msgHereIsPart(currentpart);
		stateChanged();
	}
	public void msgHereAreParts(int partid) { //gantry calls this when it dumps a bin
		print("I got parts "+partid);
		feedPart=true;
		hasparts=true;
		requested=false;
		partslow=false;
		on=true;
		currentpart=partid;
		stateChanged();
	}
	
	public void msgFixYourSelf(Lane lane)
	{
		print("nest isnt getting full"); //maybe have a cool print function for these 2 so it prints to a textarea
		print("forcing divert");
		gui.appendText(this+" nest isnt getting full");
		gui.appendText(this+" forcing divert");
		gui.divert(index);
		gui.startFeeding(index);
		Timer t=new Timer(8000,new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				askIfItIsFixed(current.lane);
			}	
		});
		t.setRepeats(false);
		t.start();
		stateChanged();
	}
	
	public void msgFixedStatus(boolean fixed,Lane lane)
	{
		print("msgfixedstatus "+fixed);
		gui.appendText("msgfixedstatus "+fixed);
		if(!fixed)
		{
			print("that didnt work, we did we can all do");
			gui.appendText(this+" that didnt work, we did all we can do");
		}
	}

	public void msgDivert() { // I dont even think this is used but I dont want to remove it just incase it is
		divert=true;
		stateChanged();
	}

	public void msgPartsLow() //gui calls this when parts are low
	{
		print("parts are low");
		partslow=true;
		stateChanged();
	}
	
	public void msgIamStable(Lane lane) //lane calls this when it doesnt need more parts currently 
	{
		if(lane==current.lane)
		{
			current.state=laneState.stable;
			if(other.state==laneState.requestedParts)
				divert=true;
		}
		else
			other.state=laneState.stable;
		stateChanged();
	}
	
	/**hacks**/
	public void setGui(Gui gui)
	{
		this.gui=gui;
	}
	
	public String toString()
	{
		return name;
	}
}
