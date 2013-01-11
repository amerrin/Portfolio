package factoryAgents.partsRobot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Semaphore;

import agent.Agent;
import factoryInterfaces.KitRobot;
import factoryInterfaces.Nest;
import factoryInterfaces.PartsRobot;
import factoryObjects.Kit;
import gfxInterfaces.Gui;
import ServerClient.*;
public class PartsRobotAgent extends Agent implements PartsRobot{
	private Map<Integer,Integer> nestPart = Collections.synchronizedMap(new HashMap<Integer,Integer>());
	private Map<Integer,ArrayList<ArrayList<Integer>>> partsReady = Collections.synchronizedMap(new HashMap<Integer,ArrayList<ArrayList<Integer>>>());
	private boolean clearToDumpParts = false,askToDump = false;
	private Semaphore animationBlocker = new Semaphore(0);
	
	//agents PartsRobot will communicate with
	private KitRobot kitRobot;
	private Nest[] nests;
	private Server GUI;
	private boolean on=true;
	
	/*
	 * MyKit merely mimics Kit; however, it has states for tracking, a kitting stand index, and a copy of partsNeeded that is may modify.
	 */
	public class MyKit {
		Kit k;
		KitState state;
		List<Integer> partsNeeded;
		int kittingStandIndex;
		List<Integer> partsHeld;
		
		MyKit(Kit k,int index){
			this.k = k;
			this.state = KitState.KIT_EMPTY;

			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for(int i=0;i<k.partsNeeded.size();i++){
				tempList.add(k.partsNeeded.get(i));
			}
			partsHeld = new ArrayList<Integer>();
			this.partsNeeded = tempList;
			this.kittingStandIndex = index;
		}
		
		MyKit(Kit k,int index,List<Integer> partsMissing){
			this.k = k;
			this.state = KitState.FIRST_PASS;
			ArrayList<Integer> tempList = new ArrayList<Integer>();
			for(int i=0;i<partsMissing.size();i++){
				tempList.add(partsMissing.get(i));
			}
			partsHeld = new ArrayList<Integer>();
			this.partsNeeded = tempList;
			this.kittingStandIndex = index;
		}
		
		void addPart(int partID){
			this.partsHeld.add(partID);
			k.partsHeld.add(partID);
		}
	}
	private enum KitState {KIT_EMPTY, KIT_PENDING, KIT_FULL, FIRST_PASS, SECOND_PASS,NOTHING}
	private List<MyKit> currentKits = Collections.synchronizedList(new ArrayList<MyKit>());
	
	/*
	 * Gripper is robots "4-armed utility" for picking up parts
	 * Has functions for checking status of arms
	 */
	public class Gripper {
		ArrayList<Integer> arms = new ArrayList<Integer>();
		
		Gripper(){
			for(int i=0; i<4; i++)
				arms.add(-1);
		}
		
		public boolean isFull(){
			return (arms.get(0)!=-1 &&arms.get(1)!=-1 &&arms.get(2)!=-1 &&arms.get(3)!=-1);
		}
		
		public boolean isFull(MyKit k){
				if(k.partsNeeded.size()%2==0){
					if( (k.partsNeeded.size()/2) == this.size() )
						return true;
				}
				else{
					if (k.state == KitState.FIRST_PASS){
						if( ((int)(k.partsNeeded.size()/2) +1) == this.size() )
							return true;
					}
					else if (k.state == KitState.SECOND_PASS){
						if( ((int)(k.partsNeeded.size()/2)) == this.size() )
							return true;
					}
				}
			return false;
		}
		
		public boolean hasPart(){
			return (arms.get(0)!=-1 ||arms.get(1)!=-1 ||arms.get(2)!=-1 ||arms.get(3)!=-1);
		}
		
		public int size(){
			int counter = 0;
			for(int i=0;i<4;i++){
				if (arms.get(i)!= -1)
					counter++;
			}
			return counter;
		}
	}
	private Gripper gripper = new Gripper();
	
	/*
	 * @see Interfaces.PartsRobot#msgEmptyKit(int, Factory.Kit)
	 * sent by KitRobot for every empty kit he wishes to be filled
	 */
	public void msgEmptyKit(int kitIndex, Kit kit){
		print("added new kit to queue");
		currentKits.add(new MyKit(kit,kitIndex));
		stateChanged();
	}
	
	public void msgBadKit(int kitIndex,Kit kit,List<Integer> partsMissing){
		print("adding missing parts to 'missing-part' kit");
		GUI.appendText("adding missing parts to 'missing-part' kit");
		currentKits.add(new MyKit(kit,kitIndex,partsMissing));
		stateChanged();
	}
	public void msgInitialize()
	{
		on=true;
		stateChanged();
	}
	
	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}
	
	/*
	 * @see Interfaces.PartsRobot#msgHereAreGoodParts(Interfaces.Nest, java.util.ArrayList)
	 * This looks more complicated than it is:
	 * 		Each part is passed as an ArrayList<Integer>(2) (nestIndex, partIndex)
	 * 		Each separate arraylist (representing individual part) is stored into a Map by part type
	 */
	public void msgHereAreGoodParts(Nest n,ArrayList<Integer> l){
		print("received message to pickup good parts");
		ArrayList<ArrayList<Integer>> tempList = new ArrayList<ArrayList<Integer>>();
		tempList = partsReady.get(nestPart.get(n.getNestID()));
		if (tempList == null){
			tempList = new ArrayList<ArrayList<Integer>>();
			tempList.add(l);}
		else{
			tempList.add(l);}
		partsReady.put(nestPart.get(n.getNestID()), tempList);
		stateChanged();
	}
	
	/*
	 * @see Interfaces.PartsRobot#msgYesYouCan()
	 * must be received from KitRobot before parts can be dumped into kit
	 */
	public void msgYesYouCan(){
		print("received message ok to dump parts");
		clearToDumpParts = true;
		stateChanged();
	}
	
	/*
	 * @see Interfaces.PartsRobot#msgAnimationUnblock()
	 * called from GUI when animation has finished
	 */
	public void msgAnimationUnblock(){
		print("MsgAnimationUnblock");
		animationBlocker.release();
	}
	
	public void msgClearNestData(int nestID){
		ArrayList<ArrayList<Integer>> tempList = partsReady.get(nestPart.get(nestID));
		if(tempList!=null){
			for(int i=0;i<tempList.size();i++){
				ArrayList<Integer> tempList2 = tempList.get(i);
				if (tempList2.get(0) == nestID){
					tempList.remove(i);
				}
			}
			partsReady.put(nestPart.get(nestID),tempList);
	}}
	
	 //Scheduler
    public boolean pickAndExecuteAnAction(){
    	synchronized(partsReady){
    		if(on)
    		{
    		for(MyKit k:currentKits){
                if (gripper.isFull(k)){
                    if(clearToDumpParts){
                            DumpParts();
                    }
                    else {
                    	if(!askToDump){
                            AskToDump();
                    	}
                    }
                    return true;
                }
	            if (k.state == KitState.KIT_FULL){
	                    MoveFinishedKit(k);
	                    return true;
	                    }
	            else if(k.state == KitState.SECOND_PASS && !partsReady.isEmpty() &&!gripper.isFull(k)){
	                    PickUpParts(k);
	                    return true;
	                    }
	            else if(k.state == KitState.FIRST_PASS && !partsReady.isEmpty() &&!gripper.isFull(k)){
	            		PickUpParts(k);
	            		return true;
	            		}
	            else if(k.state == KitState.KIT_EMPTY){
	            		if(k == currentKits.get(0)){
	            			GetParts(k);
	                    	return true;}
	                    }
}}
            return false;
    }}
    
    //Actions
    /*
     * informs KitRobot to move finished kit from the stand
     */
    private void MoveFinishedKit(MyKit k){
    	kitRobot.msgKitIsReady(k.kittingStandIndex);
    	currentKits.remove(k);
    }
    
    /*
     * Passes the part to the lane to nest to start nest/lane/feeder
     * If configuration has changed, nest will be informed to switch parts
     */
    private void GetParts(MyKit k){
    	k.state = KitState.FIRST_PASS;
    	for(int i=0;i<k.partsNeeded.size();i++){
    		if(nests[i].getPartNeeded() != k.partsNeeded.get(i)){
	    		nests[i].msgNeedPart(k.partsNeeded.get(i));
	    		
	    		if(partsReady.get(nestPart.get(nests[i].getNestID())) != null){
		    		ArrayList<ArrayList<Integer>> tempList = partsReady.get(nestPart.get(nests[i].getNestID()));
		    		for(int j=0;j<tempList.size();j++){
		    			ArrayList<Integer> tempList2 = tempList.get(j);
		    			if (tempList2.get(0) == nests[i].getNestID()){
		    				tempList.remove(j);
		    			}
		    		}
		    		partsReady.put(nestPart.get(nests[i].getNestID()),tempList);
	    		}
	    		
	    		nestPart.put(nests[i].getNestID(), k.partsNeeded.get(i));
	    		
    	}}
    	if(k.partsNeeded.size()<8){
    		for(int i=k.partsNeeded.size();i<8;i++){
    			nests[i].msgNeedPart(-1);
    			
	    		if(partsReady.get(nestPart.get(nests[i].getNestID())) != null){
		    		ArrayList<ArrayList<Integer>> tempList = partsReady.get(nestPart.get(nests[i].getNestID()));
		    		for(int j=0;j<tempList.size();j++){
		    			ArrayList<Integer> tempList2 = tempList.get(j);
		    			if (tempList2.get(0) == nests[i].getNestID()){
		    				tempList.remove(j);
		    			}
		    		}
		    		partsReady.put(nestPart.get(nests[i].getNestID()),tempList);
	    		}

    			nestPart.put(nests[i].getNestID(),-1);
    		}
    	}
    }
    
    /*
     * asks KitRobot before dumping, to avoid collisions
     */
    private void AskToDump(){
    	askToDump = true;
    	kitRobot.msgCanIGoToTable();
    }
    
    /*
     * After approval, PartsRobot will dump it's Gripper into the kit
     */
    private void DumpParts(){
    	askToDump = false;
    	MyKit k = currentKits.get(0);

    	GUI.doDumpParts(k.kittingStandIndex);
    	try {
			animationBlocker.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
		for(int i=0; i<4; i++){
			if(gripper.arms.get(i) != -1){
				k.addPart(gripper.arms.get(i));
				gripper.arms.set(i, -1);
				
				
				
				if(k.partsHeld.size() == k.partsNeeded.size()){
					k.state = KitState.KIT_FULL;
					break;
				}
				k.state = KitState.SECOND_PASS;
			}
		}
    	clearToDumpParts = false;
    	kitRobot.msgDoneDumpingParts();
    	stateChanged();
    }
    
    /*
     * Pulls the lists of ArrayList<Integer>(2) that was stored in Map to recall location of "goodPart"
     * PartsRobot fills its 4 grippers with good parts from the nests
     */
    public void PickUpParts(MyKit k){
    	int i, end;
    	if (k.state == KitState.FIRST_PASS){
    		i=0;
    		if(k.partsNeeded.size()%2 == 0)
    			end = k.partsNeeded.size()/2;
    		else
    			end = (int)(k.partsNeeded.size()/2) + 1;
    	}
    	else{
			end = k.partsNeeded.size();
    		if(k.partsNeeded.size()%2 == 0)
    			i = k.partsNeeded.size()/2;
    		else
    			i = (int)(k.partsNeeded.size()/2) + 1;
    	}
    	for(;i<end;i++){
    		if(gripper.arms.get(i%4) == -1){	
    			if(partsReady.get(k.partsNeeded.get(i))!=null &&partsReady.get(k.partsNeeded.get(i)).size()>0){
    				ArrayList<ArrayList<Integer>> tempListOfParts = partsReady.get(k.partsNeeded.get(i));
    				ArrayList<Integer> partToPass = tempListOfParts.get(0);
    				tempListOfParts.remove(0);partsReady.put(k.partsNeeded.get(i), tempListOfParts);
    				print("Trying to pick up "+partToPass.get(0)+", "+partToPass.get(1));
    				GUI.doPickUpPart(partToPass.get(0),partToPass.get(1));
    				nests[partToPass.get(0)].msgPickedUpPart(partToPass.get(1));
    				try {
    					//I don't want this to work....
						animationBlocker.acquire();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
    				gripper.arms.set(i%4,k.partsNeeded.get(i));
    			}
    		}
    	}
    }
    
    //various getters and setters
    public void setKitRobot(KitRobot k){
    	this.kitRobot = k;
    }
    
    public void setNests(Nest[] n){
    	this.nests = n;
    }
    
    
    public void setGUI(Server p){
    	this.GUI = p;
    }
	
}