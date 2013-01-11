/*
 * Kit Robot Agent Class 
 * @author: Garima Aggarwal
 */

package factoryAgents.KitRobot;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import ServerClient.Server;
import agent.Agent;
import factoryInterfaces.Conveyor;
import factoryInterfaces.FactoryAgent;
import factoryInterfaces.KitRobot;
import factoryInterfaces.PartsRobot;
import factoryInterfaces.Vision;
import factoryObjects.Kit;
import factoryObjects.KitVisionResult;
import factoryObjects.PartState;
import factoryObjects.Table;


public class KitRobotAgent extends Agent implements KitRobot, FactoryAgent
{
	Server Gui;				// GUI instance
	String name;	// name of the KitRobot
	boolean on=true;
	// this will store the most recent configuration or list of parts sent by FCS
	List<Integer> kitConfig = new ArrayList<Integer>();
	public KitRobotAgent(String name, Server a)
	{
		this.name = name;
		Gui = a;
	}
	
	// controls the state of the kit
	private enum KitState {ReadyForTable, ReadyForInspection, NoAction};

	// private class MyKit
	private class MyKit
	{
		Kit kit = new Kit();
		KitState state = KitState.NoAction;
		int tableIndex = -1;	
		boolean onInspection= false;
		MyKit(Kit k, KitState s)
		{
			kit = k;
			state = s;
		}
		public void setTableIndex(int index)
		{
			tableIndex = index;
		}
	};
	
	private List<MyKit> myKits = Collections.synchronizedList(new ArrayList<MyKit>());
	PartsRobot partsRobot;
	public Table table = new Table();         // will be shared between partsRobot and kitRobot
	
	int tableIndexEmpty;
	// to keep track of all the empty table indices
	List<Integer> emptyIndex = new ArrayList<Integer>();
	
	Vision visionAgent;
	KitVisionResult visionState = null;
	
	// will be sending to partsrobot with whatever part is needed.
	List<Integer> partsNeeded = new ArrayList<Integer>();
	boolean tableIsFree = false;
	private Semaphore sem = new Semaphore(0);
	
	Conveyor conveyorAgent;
	
	// to get MyKit instance
	public MyKit getmyKit(Kit k)
	{
		for(MyKit mykit : myKits)
		{
			if(mykit.kit == k)
			{
				return mykit;
			}
		}
		return null;
	}
	
	/*
	 * msgInitialize() : Initializes KitRobot agent by FCS
	 */
	public void msgInitialize()
	{
		print(this.name +" :msg Initialize");
		on=true;
		
		stateChanged();
	}
	/*
	 * To turn off the kitRobot
	 */
	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}
	/*
	 * msgHereIsKit: sent by Conveyer asking kitRobot to put the kit on the table
	 */
	public void msgHereIsKit(Kit k)
	{
		print(this.name +" :msg Here is Kit");
		kitConfig = k.getConfig();
		myKits.add(new MyKit(k, KitState.ReadyForTable));
		stateChanged();
	}
	/*
	 * Sent by PartsRobot asking if it can go dump parts in the kit placed on the kitting Stand
	 * This is to prevent collision  between the two robots.
	 */
	public void msgCanIGoToTable()
	{
		//print(this.name + " :msg from parts Robot asking if it can go to table");
		tableIsFree = true; 
		stateChanged();
	}
	
	/*
	 * Sent by PartsRobot Informing KitRobot that it is done dumping the parts in the Kit
	 */
	public void msgDoneDumpingParts()
	{
		tableIsFree = false;
		sem.release();
		stateChanged();
	}
	/*
	 * Sent by PartsRobot Informing KitRobot that Kit is ready for Inspection
	 */
	public void msgKitIsReady(int tableIndex)
	{
		//Kit tempKit = table.remove(tableIndex);
		Kit tempKit = table.getKitFromIndex(tableIndex);
		//emptyIndex.add(tableIndex);
		for(MyKit k : myKits)
		{
			if(k.kit == tempKit)
			{
				k.state = KitState.ReadyForInspection;
				k.setTableIndex(tableIndex);
				System.out.println("Kit is on table Index: " + tableIndex);
				//break;
			}
		}
		print(this.name +": Kit is Ready For Inspection");
		stateChanged();
	}

	/*
	 * Sent by Vision Agent with the picture and the Kit Status telling if the kit is Good or bad
	 */
	public void msgKitStatus(KitVisionResult state)
	{
		visionState = state;
		print(this.name + ": Kit Status-" + visionState.pass);
		
		//copy kit config into parts needed
		for (int i = 0; i < kitConfig.size(); ++i)
			partsNeeded.add(kitConfig.get(i));
		//create copy of partsNeeded
		ArrayList<Integer> vspartscopy = new ArrayList<Integer>();
		for (int i = 0; i < visionState.parts.size(); ++i)
			vspartscopy.add(visionState.parts.get(i).partType);
		
		for(int i = partsNeeded.size() - 1; i >= 0; --i) // decrement in order to have correct index after removing
			for (int j = 0; j < vspartscopy.size(); ++j)
				if (partsNeeded.get(i) == vspartscopy.get(j)) {
					partsNeeded.remove(i);
					vspartscopy.remove(j);
					break;
				}
		
		//print parts needed
		for (int i = 0; i < partsNeeded.size(); ++i)
			print("list: "+ partsNeeded.get(i)+" ");
		
		//visionState.pass = false;
		stateChanged();
	}
	
	/*
	 * Scheduler
	 */
	public boolean pickAndExecuteAnAction()
	{
		if(on)
		{
			synchronized(myKits){
				for(MyKit kit : myKits)
				{
					if(kit.state == KitState.ReadyForInspection)
					{
						takeKitForInspection(kit);
						return true;
					}
				}
			}
			if(visionState!=null){
				if(visionState.pass)
				{
					//print("in true vision");
					takeKitToConveyorForDelivery(); 
					return true;
				}
				else
				{
					for (int i = 0; i < visionState.parts.size(); ++i)
						if (!visionState.parts.get(i).good) {
							dumpKit();
							return true;
						}
					
					Gui.appendText("Vision Agent sent bad kit: moving it to table");
					takeKitFromInspectionToTable();
					return true;
				}
			}
			if(tableIsFree)
			{
				//print("table is free");
				tellPartsRobotTableIsFree();
				return true;
			}
			
			synchronized(myKits){
				for(MyKit kit : myKits)
				{
					if(kit.state == KitState.ReadyForTable)
					{
						takeKitOffConveyorAndPutOnTable(kit);
						return true;
					}
				}
			}
		}
		return false;
	}
	
	// Actions
	
	private void dumpKit()
	{
		Kit k = table.remove(0);
		print("dumping kit with bad parts.");
		MyKit myKit = getmyKit(k);
		Kit kit = table.remove(myKit.tableIndex);
		Gui.DoDumpKit();
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myKit.tableIndex = -1;
		myKits.remove(myKit);
		visionState = null;
		partsNeeded.clear();
	}
	
	private void takeKitToConveyorForDelivery()
	{
		Kit k = table.remove(0);

		MyKit myKit = getmyKit(k);
		Kit kit = table.remove(myKit.tableIndex);
		DoTakeKitToConveyorForDelivery(k);
		Gui.DoTakeKitToConveyorForDelivery();
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		print("sending msg to conveyor");
		conveyorAgent.msgFinishedKit(k);
		myKit.tableIndex = -1;
		myKits.remove(myKit);
		visionState = null;
		partsNeeded.clear();
	}
	
	private void DoTakeKitToConveyorForDelivery(Kit k) {
		System.out.println("Taking kit to conveyor for delivery");	
	}
	
	private void takeKitForInspection(MyKit k) 
	{
		boolean empty = table.spotEmpty(0);
		if(empty == true)
		{
			print("in takeKitForInspection");
			k.state = KitState.NoAction;
			DoPlaceKitOnTableForInspection(k.tableIndex);
			Gui.DoPlaceKitOnTableForInspection(k.tableIndex);
			try {
				sem.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			print("kit config passed to vision agent: " + kitConfig);
			visionAgent.msgTakeKitPicture((KitRobot)this, kitConfig);
			table.insert(k.kit, 0);
		}
		else
			k.state = KitState.ReadyForInspection;
		//
		//stateChanged();
	}
	
	
	public void DoPlaceKitOnTableForInspection(int tableIndex) {
		System.out.println("Placing kit on table for inspection");
	}

	private void takeKitFromInspectionToTable()
	{
		Gui.appendText("KitRobot taking Kit from Inspection to Table");
		Kit k = table.remove(0);
		MyKit myKit = getmyKit(k);
		
		k.partsHeld.clear();
		for(int i= 0; i<visionState.parts.size(); i++)
			k.partsHeld.add(visionState.parts.get(i).partType);
			
		Gui.DoPutKitOnTableFromInspection(myKit.tableIndex);
		DoTakeKitFromInspectionToTable(myKit.tableIndex);
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		myKit.state = KitState.NoAction;
		table.insert(myKit.kit, myKit.tableIndex);
		visionState = null;
		partsRobot.msgBadKit(myKit.tableIndex, myKit.kit, partsNeeded);
	}
	
	private void DoTakeKitFromInspectionToTable(int i) {
		// TODO Auto-generated method stub
		System.out.println("Placing kit from inspection to table at "+i);
	}

	private void takeKitOffConveyorAndPutOnTable(MyKit k) 
	{
		boolean x = false;
		int tableIndex = table.getEmptyPositionOnTable();
		//print("empty position: " + tableIndex);
		for(MyKit kit : myKits)
		{
			//print("in take kit off conveyor nd put on table");
			if(tableIndex != kit.tableIndex)
			{
				x = false;
				break;
			}
			else
				x = true;
			//	tableIndex = -1;
		}
		if(x == true)
		{
			//print("x = true");
			tableIndex = -1;
		}
		// keep looking for an empty location on table if all are full
		if(tableIndex == -1)
		{
			k.state = KitState.ReadyForTable;
			return;
			//stateChanged();
			//tableIndex  = table.getEmptyPositionOnTable();	
		}
		else
		{
			k.state = KitState.NoAction;
			tableIndexEmpty = tableIndex;

			System.out.println("tableIndex empty:" + tableIndexEmpty);
		DoPutKitOnTable(tableIndex);

		//Gui.DoTurnOnConveyor();
		Gui.DoPutKitOnTable(tableIndex);
		
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		conveyorAgent.msgPickedUpKit();
		table.insert(k.kit, tableIndex);
		
		partsRobot.msgEmptyKit(tableIndex, k.kit);
		//stateChanged();
		}
	 // }
		/*else
			return;*/
		
	}
	
	private void DoPutKitOnTable(int tableIndex) {

		System.out.println("Placing kit from conveyor on table at slot " + tableIndex);
	}

	/*
	 * will be called by the GUI To wake the agent up
	 */
	public void msgAnimationDone() 
	{
		sem.release();
		print("kit Robot msgAnim done");
		stateChanged();
	}

	void tellPartsRobotTableIsFree()
	{
		tableIsFree = false;
		DotellPartsRobotTableIsFree();
		partsRobot.msgYesYouCan();
		try {
			sem.acquire();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // Ask PartsRobot to send msg done dumping parts so that I can wake up.
		
	   // stateChanged();
	}
	
	private void DotellPartsRobotTableIsFree() {
		System.out.println("Telling Parts Robot Table is accessible");
	}

	/*
	 * Setters to set the agents
	 */
	public void setConveyor(Conveyor conv)
	{
		conveyorAgent = conv;
	}
	public void setPartsRobot(PartsRobot robot)
	{
		partsRobot = robot;
	}
	public void setVisionAgent(Vision vision)
	{
		visionAgent = vision;
	}
}
