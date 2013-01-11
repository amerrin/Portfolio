package factoryAgents.Conveyor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import agent.Agent;
import factoryInterfaces.Conveyor;
import factoryInterfaces.FactoryAgent;
import factoryInterfaces.KitRobot;
import factoryObjects.Kit;
import gfxInterfaces.Gui;
import ServerClient.*;

public class ConveyorAgent extends Agent implements Conveyor, FactoryAgent
{
		KitRobot kitRobot;
		String name;
		Server Gui;
		boolean on=true;
		boolean pickedUp = true;
		List<Integer> conf = new ArrayList<Integer> ();
		public ConveyorAgent(String name, Server a)
		{
			this.name = name;
			Gui = a;
		}
		private enum KitState {Empty, Finished, NoAction};
		private class MyKit
		{
			Kit kit;
			KitState state;
			MyKit(Kit k, KitState s)
			{
				kit = new Kit();
				kit = k;
				state = s;
			}
		};
		
		//GUIConveyor conveyorGui; 
		// turn on the conveyer
		// isolate conveyer and kitRobot
		
		private Semaphore sem = new Semaphore(0);
		public enum ConveyorState { NoAction, In, Out};
		int builtKits = 0;
		int numberOfKitsToBeBuilt = 0;
		private List<MyKit> Kits = Collections.synchronizedList(new ArrayList<MyKit> ());

		public void msgInitialize()
		{
			print("msg Initialize");
			on=true;
			stateChanged();
		}
		public void msgTurnOff()
		{
			on=false;
			stateChanged();
		}
		// Messages
		// Sent by FCS
		public void msgHereIsConfig(List<Integer> cfg, int numberofKitsToBuild)
		{
			print("msg Here is config");
			conf = cfg;
			numberOfKitsToBeBuilt = numberofKitsToBuild;
			for(int i =0; i < numberOfKitsToBeBuilt; i++)
			{
				Kit tempKit = new Kit(cfg);	// this will add the partsNeeded to the kit
				MyKit mytempKit = new MyKit(tempKit, KitState.Empty);
				Kits.add(mytempKit);
			}
			if(numberofKitsToBuild>0)
				Gui.DoTurnOnConveyor();
			stateChanged();
		}
		
		// sent by KitRobot to say that kit has been placed on conveyor. Just slide it down.
		public void msgFinishedKit(Kit k)
		{
			print("in msg finished kit" + Kits.size());
			//synchronized(Kits){
			for(int i = 0; i < Kits.size(); i++)
			{
				print("1kit is finished:" + Kits.size());
				if(Kits.get(i).kit == k)
				{
					print("kit is finished");
					MyKit tempKit = Kits.get(i);
					tempKit.state = KitState.Finished;
					stateChanged();
				}
			//}
		   }
		}

		// from kitrobot
		public void msgPickedUpKit()
		{
			pickedUp = true;
		}
		
		public boolean pickAndExecuteAnAction()
		{
			for(MyKit k : Kits)
			{
				synchronized(Kits){
					if (k.state == KitState.Finished)
					{
						tellGUIToSendGoodKits(k);
						return true;
					}
				}
			}
			
			for(MyKit k : Kits)
			{
				synchronized(Kits){
					if (k.state == KitState.Empty)
					{
						//print("in empty scheduler state");
						tellKitRobotToPickUpKit(k);
						return true;
					}
				}
			}
			return false;
		}

		public void msgStopFactory()
		{
			print("stopping factory");
			for (int i = Kits.size() - 1; i >= builtKits; --i)
				Kits.remove(i);
			stateChanged();
		}
		
		public void msgKitReady()
		{
			print("msg kit is Ready from GUI");
			stateChanged();
		}
		
		// sent by GUI to wake me up after the animation is done
		public void msgAnimationDone()
		{
			print("ConveyorAgent: msg anim done");
			sem.release();
			Gui.DoTurnOffConveyor();
			stateChanged();
		}

		private void DoTurnOffConveyor() {
			print("Telling GUI To Turn off conveyor");
		}

		private void tellKitRobotToPickUpKit(MyKit kit)
		{
			// here I am passing number of kits to be built to the GUI 
			//conveyorGui.
			if(pickedUp){
				kit.state = KitState.NoAction;
				print("changing kit to No Action "+ kit.toString());
				Gui.DoTurnOnConveyor();
				DoPutKitOnConveyor(1);
				Gui.DoPutKitOnConveyor(1);
				++builtKits;
				
				try {
					sem.acquire();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Gui.DoTurnOffConveyor();
				numberOfKitsToBeBuilt--;
				pickedUp = false;
				kitRobot.msgHereIsKit(kit.kit);
			}
			stateChanged();
		}
		
		private void DoTurnOnConveyor() {
			print("Telling GUI To Turn on conveyor");	
		}
		private void DoPutKitOnConveyor(
				int number) {
			System.out.println("Tell Conveyor GUI to put "+ number + " empty kits on conveyor");
			
		}

		void tellGUIToSendGoodKits(MyKit k)
		{
		// 1: implies that it is outgoing conveyor carrying good kits
		//conveyorGui.
			print("Sending good kit back");
			k.state = KitState.NoAction;
			DoTellGuiToPutKitsOnOngoingConveyor();
			Gui.DoTurnOnGoodConveyor();
			
			Kits.remove(k);
			stateChanged();
		}


		private void DoTellGuiToPutKitsOnOngoingConveyor() {
			// TODO Auto-generated method stub
			System.out.println("Tell Conveyor GUI to put finished kits on conveyor for delivery");
			//Gui.DoTakeKitToConveyorForDelivery();	: This is done by KitRobot....Simply Turning on the conveyor slides it down...or at least it did in my code
		}
		
		public void setKitRobot(KitRobot robot)
		{
			kitRobot = robot;
		}
	
}

