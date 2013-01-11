package gfx;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Ellipse2D;
import java.io.*;
import java.util.*;

import javax.swing.*;

import factoryAgents.lane.LaneAgent;
import factoryObjects.*;

public class GfxFactory implements Serializable{
	/*This is the graphics factory state on the server. The data in this class is the visual representation of the information in the Factory state. 
	 * This class will be sent to the clients when they request the graphics state data from the server.
	 */
	final static int GFX_UPDATE_SPEED = 55;
	
	private final int initbinx = 910;
	private final int initbiny = 510;
	private final int initlanex = 612;
	private final int initlaney = 43;
	private final int initfeederx = 910;
	private final int initfeedery = 50;
	private final int initpartrobotx = 295;
	private final int initpartroboty = 10;
	private final int speed = 5;
	private final int kitSize = 3;
	private final int nestSize = 8;
	private final int partSize = 21;
		
	//Painters
    Ellipse2D.Double myEllipse;
   // ImageIcon background;
    //Gfx Objects
    GfxKitStation kitStation;
    GfxConveyor conveyor;//conveyor for empty kits
    GfxGoodConveyor conveyor2;//conveyor for good kits that move away
	ArrayList<GfxBin> myBins;
	ArrayList<GfxFeeder> myFeeders;
    ArrayList<GfxLane> myLanes;
    ArrayList<GfxPart> myParts;
	GfxNest[] myGfxNests; //8 max
	GfxCamera myGfxCamera;
    //Lovely Robots
    GfxKitRobot kitRobot;
	GfxGantryRobot gr;
	GfxPartRobot myGfxPartRobot;
	
    //Targets / status (controlling movement of objects)
    //For KitRobot/KitAssembly
	double theta;		//Angular Position of KitRobot	
    int spotToMoveOnTable;
    int spotToMoveFromTable;
    int spotToMoveBackOnTable;
    int numberOfKits;
    boolean[] status; //array of statuses
	//For GantryRobot/Lanes
    GfxBin destBin;
	GfxFeeder destFeeder;
	ArrayList<GantryAgentMessage> gantryInstructions;
	GantryAgentMessage gam;
	int bincounter = 0;
    int count = 0;
	boolean gantryProcessing;
	boolean gotToBin=false;
	boolean gotToFeeder=false;
	//For PartsRobot/Nests
	ArrayList<PartRobotAgentMessage> partRobotInstructions;
	PartRobotAgentMessage pram=null;
	int destNest;
	int destNestSlot;
	int destKit;
	boolean partRobotProcessing;
	
	// flags for messages to send to agents from Server
	boolean[] messageFlags;
	
	
	//V2 Flags for non-normatives
	ArrayList<Integer> makeBadKitPositions;
	int removePartsNestIndex = -1;
	double additionAngle;
	private int twoTypesOfPartsLaneIndex = -1;
	int teleportPartsRobotIndex = -1;
	List<Integer[]> unstablizedList = Collections.synchronizedList(new ArrayList<Integer[]> ());
	
	public GfxFactory() {
		makeBadKitPositions = new ArrayList<Integer>();
		messageFlags = new boolean[21];
		
		//background = new ImageIcon("src/images/background.png");
	    //Create Graphic Components
	    conveyor = new GfxConveyor(new Point(0,0));
	    conveyor2 = new GfxGoodConveyor(new Point(0,285));
	    kitStation = new GfxKitStation(110,300);    	
	    //myparts=new ArrayList<GfxPart>();
    	myBins=new ArrayList<GfxBin>();
    	myFeeders=new ArrayList<GfxFeeder>();
    	myLanes=new ArrayList<GfxLane>();
		myGfxNests=new GfxNest[nestSize];
		
		additionAngle=0.4;
		//Create Nests
		for(int i=0; i<nestSize; i++) {
			//build new GfxNest and set the location to(260, y) where y=10,70,130,190,250,310,370,430
			myGfxNests[i]=new GfxNest(i,492,40+60*i);
		}
		//Create Bins, Lanes and Feeders
    	for (int i=0; i<8; i++){
    		//myparts.add(new GfxPart(i,partImage));
    		//TODO: part configuration should be created by the Part Manager.
    		//it should be able to tell here whichever parts are added.
    		//TODO: think about how to tell this the config of part inside a specific
    		//bin
    		//myBins.add(new GfxBin(i, -1, initbinx+(50*(i%4)),initbiny+(50*(i/4)))); //no longer need to initialize array of possible bins,
    		//bins are created one at a time when feeder requests a part
    		myLanes.add(new GfxLane(i, initlanex,initlaney+(60*i), myFeeders, myGfxNests));
    		if (i<4){
    			myFeeders.add(new GfxFeeder(this, i, initfeederx, initfeedery+(120*i), myBins, myLanes));
    		}
    	}
	    //Hire new robots
	    kitRobot=new GfxKitRobot(150,200);
    	gr = new GfxGantryRobot(myBins, myFeeders);
		myGfxPartRobot=new GfxPartRobot();
	    //Target / status initialization
	    //Initialize status of KitRobot
		theta = 0.0;
	    status=new boolean[9];
	    status[0] = false;//from inspection to conveyor with good kits
	    status[1] = false;//from conveyor to station (specific spot)
	    status[2] = false;//from station (specific spot) to inspection
	    status[3] = false;//draw new kit;
	    status[4] = false;//
	    status[5] = false;//for conveyor
	    status[6] = false;
	    status[7] = false;//dumping the kit from
	    status[8] = false;//for moving the kit back to kitStation from inspection if it misses part
	    
	    //Initialize status of GantryRobot
	    gam=new GantryAgentMessage(0,0,0);
    	gantryInstructions=new ArrayList<GantryAgentMessage>();
		//Initialize status of PartRobot
    	pram=null;
		partRobotInstructions=new ArrayList<PartRobotAgentMessage> ();
		destNest=-1;
		destKit=-1;
		
	}
	
	public void updateKitAssembly() {
		if (conveyor.isMoving()){
			messageFlags[3] = conveyor.updateKitPosition();
			conveyor.incTimer();
		}
			
		if (conveyor2.isMoving()){
			messageFlags[4] = conveyor2.updateGoodKitPosition();
			conveyor2.incTimer();
		}
		
		//Paint the conveyors for updating the kit position
		//conveyor.paintConveyor( conveyor.position.x, conveyor.position.y);
		//conveyor2.paintConveyor( conveyor2.position.x, conveyor2.position.y);
		
		
		/*==Some theta positions, center stand = 1.55, right stand = 1.10, inspect = 1.90
		 Move to Stand from conveyor = 2.90,move from stand to conveyor = 2.40*/
		
		if (status[2]){// moving kit to inspection
			if (kitRobot.rotate(theta,0, Math.PI / 6 + 1 / spotToMoveFromTable)){
				if (kitRobot.hasNoKit() && (theta >= Math.PI / 6  / spotToMoveFromTable) && (theta <= Math.PI / 6 + 2 / spotToMoveFromTable+0.5)) {
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[spotToMoveFromTable]));
				}//if has no kit
				if (kitRobot.rotate(theta, 0, 1.72)) {
					if (kitRobot.hasAKit() && (theta > 1.5) && (theta < 2.3))
						kitStation.putKitOnStation(kitRobot.removeKit(), 0);
					if (kitRobot.rotate(theta, 1.72, 2*Math.PI)){
						//message to garima
						
						status[2] = false;
						theta = 0.0;
						kitRobot.resetTheta();
						//kitRobotAgent.msgAnimationDone();
						messageFlags[0] = true;
						kitRobot.waitReset();
					}
				}
			}
			if(kitRobot.notInWaitingMode(20)&&(!myGfxPartRobot.isInRestrictedArea()))
				theta=theta+additionAngle;
		}
		if (status[0]){ //kit from inspection to conveyor and move away
			if (kitRobot.rotate(theta,0,1.74)){
				if (kitRobot.hasNoKit()&&((theta>=-0.4)&&(theta<=2.2))){ //
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[0]));
				}//if has no kit
				if (kitRobot.rotate(theta,1.72,2.3)){
					if (kitRobot.hasAKit()&&(theta<2.6)&&(theta>1.6))
						conveyor2.addKitToConveyor(kitRobot.removeKit());
					
					if (kitRobot.rotate(theta,2.3, 2*Math.PI)){
						status[0] = false;
						theta = 0.0;
						kitRobot.resetTheta();
						//kitRobotAgent.msgAnimationDone();
						messageFlags[0] = true;
						kitRobot.waitReset();
					}
				}
			}
			if(kitRobot.notInWaitingMode(20)&&(!myGfxPartRobot.isInRestrictedArea()))
				theta=theta+additionAngle;
		}
		if (status[1]) {
			//kit from conveyer to stand with specific spot
			if (kitRobot.rotate(theta,0,3.14)){
				if (kitRobot.hasNoKit()&&((theta>=2.8)&&(theta<=3.45))){
					kitRobot.getKit(conveyor.kitsOnConveyor.remove(0));
				}//if has no kit
				if (kitRobot.rotate(theta, 3.14,6.28+1.5/spotToMoveOnTable)){
					if ((kitRobot.hasAKit())&&(( theta>=(6.28+1.5/spotToMoveOnTable)-0.2  )&&( theta<= (6.28+1.5/spotToMoveOnTable)+0.8 )))
						kitStation.putKitOnStation(kitRobot.removeKit(), spotToMoveOnTable);
					
					if (kitRobot.rotate(theta,3,4*Math.PI)){
						theta = 0.0;
						kitRobot.resetTheta();
						status[1] = false;
						//kitRobotAgent.msgAnimationDone();
						messageFlags[0] = true;
					}
				}
			}
			if (!myGfxPartRobot.isInRestrictedArea())
				theta=theta+additionAngle;
		}
		if (status[3]){ //if the method for creating a kit was called
			for (int i=0;i<numberOfKits;i++) {
				conveyor.addKitToConveyor(new GfxKit(new Point(28, conveyor.position.y)));
			}
			status[3] = false;
		}
		if (status[4]) {//turn off conveyor
			conveyor.turnConveyorOff();
			status[4]=false;
		}
		if (status[5]) {//turn on conveyor
			conveyor.turnConveyorOn();
			status[5]=false;
			
		}
		if (status[6]) {//turn on conveyor
			conveyor2.turnConveyorOn();
			status[6]=false;
		}
		if (status[7]) { //dumping kit from inspection
			if (kitRobot.rotate(theta,0,1.74)){
				if (kitRobot.hasNoKit()&&((theta>=-0.2)&&(theta<=2.25))){ //
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[0]));
				}//if has no kit
				if (kitRobot.rotate(theta,1.72,4.3)){
					if (kitRobot.hasAKit()&&(theta<4.7)&&(theta>3.9))
						kitRobot.kitOfRobot=null;//dumping the kit
					
					if (kitRobot.rotate(theta,.3, 2*Math.PI)){
						status[7] = false;
						theta = 0.0;
						kitRobot.resetTheta();
						//kitRobotAgent.msgAnimationDone();
						messageFlags[0] = true;
						kitRobot.waitReset();
					}
				}
			}
			if(kitRobot.notInWaitingMode(20)&&(!myGfxPartRobot.isInRestrictedArea()))
				theta=theta+additionAngle;
		}
		if (status[8]) {
			//kit from inspection back to stand with specific spot
			if (kitRobot.rotate(theta,0,1.74)){
				if (kitRobot.hasNoKit()&&((theta>=0.0)&&(theta<=2.25))){
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[0]));
				}//if has no kit
				if (kitRobot.rotate(theta, 1.70,6.28+1.5/spotToMoveBackOnTable)){
					if ((kitRobot.hasAKit())&&(( theta>=(6.28+1.5/spotToMoveBackOnTable)-0.1  )&&( theta<= (6.28+1.5/spotToMoveBackOnTable)+0.8 )))
						kitStation.putKitOnStation(kitRobot.removeKit(), spotToMoveBackOnTable);
					
					if (kitRobot.rotate(theta,3,4*Math.PI)){
						theta = 0.0;
						kitRobot.resetTheta();
						status[8] = false;
						//kitRobotAgent.msgAnimationDone();
						messageFlags[0] = true;
						kitRobot.waitReset();
					}
				}
			}
			if(kitRobot.notInWaitingMode(20)&&(!myGfxPartRobot.isInRestrictedArea()))
				theta=theta+additionAngle;
		}
	}
	
	public void updatePartRobotandNest() {
		int taskCount=partRobotInstructions.size();		//manage how many tasks are left
		//Check if it's under a process. If not, grab a new task
		//System.out.println(myGfxPartRobot.getIfAtDestination());
		for(int i=0; i<nestSize;i++) {
			if(myGfxNests[i].isFull())
				myLanes.get(i).setNestFull(true);
			else
				myLanes.get(i).setNestFull(false);
		}
		if(!partRobotProcessing) { 	//No Animation under way
			if(taskCount!=0) {		//Some tasks left
				//Grab a task from the instruction list
				pram=partRobotInstructions.remove(0);
				if(pram.action==RobotAction.PickUpPartFromNest) {
					partRobotProcessing=true;	//start process
					//set destination to the target nest
					destNest=pram.targetNestIndex;
					destNestSlot=pram.targetNestSlotIndex;
					myGfxPartRobot.setDestination(myGfxNests[destNest].xpos, 
							myGfxNests[destNest].ypos);
					myGfxPartRobot.move();
				}
				else if(pram.action==RobotAction.PutPartIntoKit) {
					partRobotProcessing=true;	//start process
					//set destination to THE KIT
					destKit=pram.targetKitIndex;
					myGfxPartRobot.setDestination(kitStation.x + destKit * 42 + 5,kitStation.y + 5);
					myGfxPartRobot.move();
				}
			}
			else {//no task left. Although this's a move, it shouldn't be considered as a task
								//i.e. move robot back to resting location
				myGfxPartRobot.setDestination(initpartrobotx, 
							initpartroboty);	//Original Point
				myGfxPartRobot.atDestination=false;
				if(!myGfxPartRobot.getIfAtDestination()) {		//if it's not at the original point
					myGfxPartRobot.move();
				}
			}
		}
		else {		//animation under process, continue doing it
			myGfxPartRobot.move();
//			System.out.println("Dest:"+myGfxPartRobot.destxpos+","+myGfxPartRobot.destypos);
//			System.out.println("Now:"+myGfxPartRobot.getxpos()+","+myGfxPartRobot.getypos());
			if(myGfxPartRobot.getIfAtDestination()) { //next time MAKE SURE IT WON'T GO BACK HERE
				partRobotProcessing=false;
				if(pram.action==RobotAction.PickUpPartFromNest) {		
					try {
						//move part from nest to robot's hand
						myGfxPartRobot.addPart(myGfxNests[destNest].removePart(destNestSlot));
						//partsRobot.msgAnimationUnblock();
						messageFlags[1] = true;
					} catch(Exception e) {
						System.out.println(e.getMessage());
					}
				}
				else if(pram.action==RobotAction.PutPartIntoKit) {
					try {
						//move part from robot's hand to kit
						while(myGfxPartRobot.isLoaded()) {
							kitStation.kitsOnTheTable[destKit].addPart(myGfxPartRobot.removePart());
						}
						//partsRobot.msgAnimationUnblock();
						messageFlags[1] = true;
					} catch(Exception e) {
						System.out.println(e.getMessage());
					}
				}
			}
		}
		if(myGfxCamera!=null) {
			myGfxCamera.die();
			if(myGfxCamera.battery==0)
				myGfxCamera=null;	//camera disappear
		}
	}
	
	public void updatePartandFeeder() {
		count++;
		for (GfxFeeder f : myFeeders) {
			if (f.checkIsFeeding()) {
				if (count%10==0) {//remove if 
					f.feedPart();
				}
			}
		}
		for (GfxLane l : myLanes) {
			if (l.getIsOn()) {
				l.incTimer();
				l.moveLane();
			}
		}
		int taskCount=gantryInstructions.size();//manage how many tasks are left
		
		//Check if it's under a process. If not, grab a new task
		if(!gantryProcessing) { 	//No Animation under way
			
			if(taskCount!=0) {		//Some tasks left
//				System.out.println("TASKCOUNT " +taskCount);
				
				//Grab a task from the instruction list
				gam=gantryInstructions.get(0);
				
				if(gam.action==1) { //move gantry to bin
										//set destination to the target bin
					//myBins.add(destBin); //add to arraylist of bins to keep track
					destBin=myBins.get(myBins.size()-1);
					destFeeder=myFeeders.get(gam.targetFeederIndex);
					
					if (!gotToBin)  {
						gr.setDestination(destBin.getX(),destBin.getY());
					}
					else {
						gr.setDestination(destFeeder.getX()+40,  destFeeder.getY()+30);
					}
					gr.move();
					if (!gotToBin && gr.getIfAtDestination()) {
						gotToBin=true;
						gr.pickedUpBin(destBin.getid());
						System.out.println("Dest bin is "+destBin.getid());
					}
					else if (gotToBin && gr.getIfAtDestination()) {
						gotToFeeder=true;
						gotToBin=false;
						gotToFeeder=false;
						gr.droppedBin(destBin.getid());
						System.out.println("Dest bin is "+destBin.getid());
						destFeeder.receivePart(destBin.getpartid(), destBin.getid());
						System.out.println("Dest feeder is "+destFeeder.getBinID());
						int tempbinindex=0;
						for (GfxBin b : myBins) {
							if (b.getid()==destBin.getid()) {
								break;
							}
							else 
								tempbinindex++;
						}
//						System.out.println("BIN STATUS" + myBins.get(tempbinindex).getStatus());
						//destBin.setStatus();
						myBins.get(tempbinindex).setStatus(); //set bin appear empty because it was dumped into feeder
//						System.out.println("BIN STATUS CHANGED.");
//						System.out.println("BIN STATUS" + myBins.get(tempbinindex).getStatus());
						gantryInstructions.remove(0);
						//gantry.msgAtDestination();
						messageFlags[2] = true;
						
					}
					gantryProcessing=true;	//start process
				}
				else if(gam.action==3) { //destroy bin (called when feeder asks for purge)
					try {
						if (myBins.size()>0)
						{
							System.out.println("In action 3");
							int tempbinid = myFeeders.get(gam.targetFeederIndex).getBinID();
							System.out.println("gam feeder index is"  +gam.targetFeederIndex);
							int tempbinindex=0;
					
							//set destination to the target bin
							for (GfxBin b : myBins) {
								System.out.println("bin id is " +b.getid());
								System.out.println("temp is "+ tempbinindex);
								System.out.println("temp id is "+ tempbinid);
								if (b.getid() == tempbinid) {
									break;
								}
								else
									tempbinindex++;
							}
					//got the index of specified bin in myBins
					//quick edit
					//myBins.get(tempbinindex).setStatus(); //make bin appear full because the feeder was purged
							if (!gotToBin)  {
								gr.setDestination(myBins.get(tempbinindex).getX(),myBins.get(tempbinindex).getY());
							}
							gr.move();
							if (!gotToBin && gr.getIfAtDestination()) {
								gotToBin=true;
								//Test fix: Fuck bin index. by Fintan O'Grady (201)
								myBins.remove(tempbinindex);
								gotToBin=false;
								gantryInstructions.remove(0);
								//gantry.msgAtDestination();
								messageFlags[2] = true;
						
								//gr.pickedUpBin(gam.targetBinIndex);
							}
							/*else if (gotToBin && gr.getIfAtDestination()) {
								gotToFeeder=true;
								//gantry.msgAtDestination();
								messageFlags[2] = true;
								gotToBin=false;
								gotToFeeder=false;
								
								gr.droppedBin(gam.targetFeederIndex);
								gantryInstructions.remove(0);
							}*/
							gantryProcessing=true;	//start process
						}
					} catch (IndexOutOfBoundsException e) {
						System.out.println("Bin that doesn't exist was accessed.");
						messageFlags[2] = true;
					}
				}
			}
		} //closes !processing if statement
		else {		//animation under process, continue doing it
			gr.move();
			
			if(gr.getIfAtDestination()) { 
				gantryProcessing=false;
			}
		}
	}
	public void updateNonNormatives()
	{
		//check non-norm flags and perform actions

		
		if (!makeBadKitPositions.isEmpty())
		{
			if (kitStation.isKitInInspection())
			{
				//delete parts fromkit
				System.out.println("Non-normative: Making Bad Kit");
				for (int i = 0; i < makeBadKitPositions.size(); i++){
					kitStation.getInspectionKit().deletePart(makeBadKitPositions.get(i) - i);
				}
				makeBadKitPositions.clear();
			}
		}
		if (removePartsNestIndex != -1)
		{
			System.out.println("Non-normative: Removing Parts From Nest");
			//remove parts
			myGfxNests[removePartsNestIndex].removeAllParts();
			removePartsNestIndex = -1;
		}
		if (twoTypesOfPartsLaneIndex != -1)
		{
			System.out.println("Non-normative: Two types of parts on lane, i.e. diverter too slow.");
			myLanes.get(twoTypesOfPartsLaneIndex).splitPartsIntoTwoTypes();
			
			twoTypesOfPartsLaneIndex = -1;
		}
		ArrayList<Integer[]> itemsToRemove = new ArrayList<Integer[]> ();
		for (Integer[] il: unstablizedList) {
			--il[1];
			if(il[1]==0) {
				DoMakeThePartsStable(il[0]);
				itemsToRemove.add(il);
			}
		}
		for (Integer[] i: itemsToRemove) {
			unstablizedList.remove(i);
		}
	}
	//call update methods and return messageFlags so server can send messages to agents
	public boolean[] updateGfxFactory() {
		updateNonNormatives();
		updateKitAssembly();
		updatePartRobotandNest();
		updatePartandFeeder();
		updateNonNormatives();
		return messageFlags;
	}
	
	//DoXXXs
	public void doPickUpPart(int targetNestIndex, int targetNestSlotIndex) {
		logDoMethod("DoPickUpPart from nest "+targetNestIndex+", slot "+targetNestSlotIndex);
		PartRobotAgentMessage am;
		//First part of task: pick up a part from a specific nest
		am=new PartRobotAgentMessage(RobotAction.PickUpPartFromNest, targetNestIndex, targetNestSlotIndex);
		partRobotInstructions.add(am);
		//Second part of task: put that part into a specific kit
	}
	
	public void doDumpParts(int kitIndex) {
		logDoMethod("DoDumpParts into kit "+kitIndex);
		PartRobotAgentMessage am;
		//First part of task: pick up a part from a specific nest
		am=new PartRobotAgentMessage(RobotAction.PutPartIntoKit, kitIndex);
		partRobotInstructions.add(am);
		//Second part of task: put that part into a specific kit
	}
	
	public void doShowCamera(int nestIndex) {
		myGfxCamera=new GfxCamera(235,10+60*nestIndex);	
	}

	public void addPartToNest(int index, GfxPart p) {
		try {
			myGfxNests[index].addPart(p);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	public void DoTakeKitToConveyorForDelivery(){//from inspection to conveyor away
		logDoMethod("DoTakeKitToConveyorForDelivery");
    	status[0] = true;
    }
    
    public void DoPutKitOnTable(int spot){  //from conveyor to specific spot
    	logDoMethod("DoPutKitOnTable "+spot);
    	spotToMoveOnTable=spot;
    	status[1] = true;
    }

    public void DoPlaceKitOnTableForInspection(int spot){ //from specific spot to inspection
    	logDoMethod("DoPlaceKitOnTableForInspection, from stand "+spot);
    	spotToMoveFromTable=spot;
    	status[2] = true;
    }
    
    public void DoPutKitOnConveyor(int number){
    	logDoMethod("DoPutKitOnConveyor "+number);
    	numberOfKits = number; //this value will hold how many kits to create
    	status[3] = true;
    }
    
    public void DoTurnOffConveyor() {
    	logDoMethod("DoTurnOffConveyor");
    	status[4] = true;
    }
    
    public void DoTurnOnConveyor(){
    	logDoMethod("DoTurnOnConveyor");
    	status[5] = true;
    }
    
    public void DoTurnOnGoodConveyor(){
    	logDoMethod("DoTurnOnGoodConveyor");
    	status[6] = true;
    }
    public void DoDumpKit(){ //when the agent asks to dump the kit from inspection
    	logDoMethod("DoDumpKit");
    	status[7] = true;
    }
    public void DoPutKitOnTableFromInspection(int spot){  //from conveyor to specific spot
    	logDoMethod("DoPutKitOnTableFromInspection "+spot);
    	spotToMoveBackOnTable=spot;
    	status[8] = true;
    }
    
    public void doServiceFeeder(int partid, int targetFeederIndex) {  //scenario 1
    	logDoMethod("DoServiceFeeder feeder "+targetFeederIndex+" with part type "+partid);
		GantryAgentMessage am;
		int a=1;
		
		am=new GantryAgentMessage(a,partid,targetFeederIndex);
		myBins.add(new GfxBin(bincounter,partid,initbinx,initbiny)); //create the target bin with needed part
		System.out.println("bincounter is "+ bincounter);
		bincounter++;
		gantryInstructions.add(am);
	}
	
	public void doPickUpBin(int targetBinIndex) {
		logDoMethod("DoPickUpBin "+targetBinIndex);
		GantryAgentMessage am;
		int a=1;
		//First part of task: pick up the specified bin
		am=new GantryAgentMessage(a, targetBinIndex);
		gantryInstructions.add(am);
	}

	public void doEmptyBinInFeeder(int targetFeederIndex) {
		logDoMethod("DoEmptyBinInFeeder "+targetFeederIndex);
		GantryAgentMessage am;
		int a=2;
		am=new GantryAgentMessage(a,targetFeederIndex);
		gantryInstructions.add(am);
	}
	
	public void doPutBinHome(int feederIndex) {  //scenario 2 destroy bin
		logDoMethod("DoPutBinHome "+feederIndex);
		int targetFeederIndex=feederIndex;
		//myFeeders.get(feederIndex).getGetBinID();
		GantryAgentMessage am;
		int a = 3;
		am=new GantryAgentMessage(a, targetFeederIndex);
		gantryInstructions.add(am);
	}
	
	public void DoTurnLaneOn(int laneindex) {
		logDoMethod("DoTurnLaneOn lane "+laneindex);
		myLanes.get(laneindex).turnLaneOn();
	}
	
	public void DoTurnLaneOff(int laneindex) {
		logDoMethod("DoTurnLaneOff lane "+laneindex);
		myLanes.get(laneindex).turnLaneOff();
	}
	/** @author Fintan O'Grady */
	public void DoLaneFrequencyHigh(int laneindex) {
		logDoMethod("DoLaneFrequencyHigh "+laneindex);
		myLanes.get(laneindex).upAmplitude();
	}
	/** @author Fintan O'Grady */
	public void DoLaneFrequencyLow(int laneindex) {
		logDoMethod("DoLaneFrequencyLow "+laneindex);
		myLanes.get(laneindex).downAmplitude();
	}
	public void divert(int feeder){
		logDoMethod("divert feeder "+feeder);
		myFeeders.get(feeder).divert();
	}
	
	public void stopFeeding(int feeder){
		logDoMethod("stopFeeding "+feeder);
		myFeeders.get(feeder).stopFeeding();
	}
	public void startFeeding(int feeder){
		logDoMethod("startFeeding "+feeder);
		myFeeders.get(feeder).startFeeding();
	}
	
	public void doDumpNest(int nestID){
		logDoMethod("DoDumpNest "+nestID);
		myGfxNests[nestID].dump();
		/*GfxPart p = myLanes.get(nestID).removeOneStackedPart();
		while (p != null) {
			while(p != null && ! (myGfxNests[nestID].isFull())) {
				try {
					myGfxNests[nestID].addPart(p);
				} catch (Exception e) {
					e.printStackTrace();
				}
				p = myLanes.get(nestID).removeOneStackedPart();
			}
			myGfxNests[nestID].dump();
		}*/
		messageFlags[nestID + 13]=true;
	}
	
	public void DoCameraInitialize() {
		logDoMethod("DoCameraInitialize");
		//do nothing
	}

	public KitVisionResult DoCameraTakeKitPicture(List<Integer> cfg) {
		logDoMethod("DoCameraTakeKitPicture");
		myGfxCamera = new GfxCamera(kitStation.x + 5, kitStation.y + 5);
		List<PartState> parts = Collections.synchronizedList(new ArrayList<PartState>());
		int iterator = 0;
		if (kitStation.kitsOnTheTable[0] == null) {
			logDoMethod("ERROR: DoCameraTakeKitPicture called with no kit on inspection stand.");
		} else {
			for(GfxPart p : kitStation.kitsOnTheTable[0].partsInsideKit) {
				PartState ps = new PartState();
				ps.index = iterator;
				ps.partType = p.getid();
				ps.good = !p.isBad;
				parts.add(ps);
				++iterator;
			}
		}
		KitVisionResult kvr = new KitVisionResult(parts, cfg);
		logDoMethod("DoCameraTakeKitPicture: Picture has "+kvr.parts.size()+"parts, pass: "+kvr.pass);
		return kvr;
	}

	public NestVisionResult DoCameraTakeNestPicture(int nestNum) {
		boolean intersection = false;
		logDoMethod("DoCameraTakeNestPicture nest "+nestNum);
		myGfxCamera = new GfxCamera(myGfxNests[nestNum].getxpos(),
				myGfxNests[nestNum].getypos());
		List<PartState> parts = Collections.synchronizedList(new ArrayList<PartState>());
		int iterator = 0;
		
		/*Rectangle cameraRect;
		if (myGfxCamera != null)
			cameraRect = new Rectangle(myGfxCamera.getxpos(), myGfxCamera.getypos(), myGfxCamera.width, myGfxCamera.height);
		else
			cameraRect  = new Rectangle(0,0,0,0);
		*/
		//Rectangle partrobotRect = new Rectangle(myGfxPartRobot.getxpos(), myGfxPartRobot.getypos(), myGfxPartRobot.width, myGfxPartRobot.height);
		//boolean intersection = cameraRect.intersects(partrobotRect);
		
		if (teleportPartsRobotIndex != -1)
		{
			myGfxPartRobot.xpos = myGfxNests[teleportPartsRobotIndex].xpos;
			myGfxPartRobot.ypos = myGfxNests[teleportPartsRobotIndex].ypos;
			System.out.println("From gui, parts robot moved in front of nest camera " +  nestNum);
			//System.out.println("myGfxCamera.xpos, myGfxCamera.ypos, myGfxPartRobot.xpos, myGfxPartRobot.ypos");
			//System.out.println(myGfxCamera.getxpos() + " " + myGfxCamera.getypos() + " " + myGfxPartRobot.getxpos()+ " " +myGfxPartRobot.getypos());
			intersection = true;// myGfxPartRobot.getxpos()  <= (myGfxCamera.getxpos() + myGfxCamera.width) && (myGfxPartRobot.getxpos() + myGfxPartRobot.width) >= (myGfxCamera.getxpos()) &&
			//myGfxPartRobot.getypos()  <= (myGfxPartRobot.getypos() + myGfxPartRobot.height) && (myGfxPartRobot.getypos() + myGfxPartRobot.height) >= myGfxCamera.getypos();
		}
		
		for(GfxPart p : myGfxNests[nestNum].myParts) {
			PartState ps = new PartState();
			ps.index = iterator;
			ps.partType = p.getid();
			if(myGfxNests[nestNum].partsPiledUp==true || intersection)
				ps.good = false;
			else
				ps.good = !(p.isBad);
			parts.add(ps);
			++iterator;
		}
		for (int i = 0; i < parts.size(); ++i)
			if ((myGfxNests[nestNum].partsPiledUp==true || intersection) || myGfxNests[nestNum].myParts.get(i).isBad) {
				for (int j = 0; j < parts.size(); ++j)
					parts.get(j).good = false;
				break;
			}
		
		NestVisionResult nvr = new NestVisionResult(parts);
		teleportPartsRobotIndex = -1; //non-norm camera blocked by parts robot
		logDoMethod("DoCameraTakeNestPicture: Picture has "+nvr.parts.size()+" parts");

		return nvr;
	}

	public void	 DoCameraSetKitCfgInfo(List<Integer> cfg) {
		logDoMethod("DoCameraSetKitCfgInfo");
	}
	
	/*
	public void paintFactory(JPanel p, Graphics2D g2) {
		//Paint PartRobot / Nests
		//Paint Bins / Feeders
		background.paintIcon(p, g2, 0, 0);
		for (GfxLane l : myLanes) {
			l.paintLane(p, g2);
		}
		for (GfxBin b : myBins) {
			b.paintBin(p, g2);
		}
		for (GfxFeeder f : myFeeders) {
			f.paintFeeder(p, g2);
		}
		gr.paintGantry(p, g2);
		//Paint PartRobot / Nests
		for(GfxNest n : myGfxNests) {
			n.paintNest(p, g2);
		}
		
		//paint kit robot
		kitStation.paintKittingStation(p, g2);
		conveyor.paintConveyor(p, g2, 0, 0);
		conveyor2.paintConveyor(p, g2, 0, 285);
		kitRobot.paintKitStationRobot(p, g2, kitRobot.getXPos(), kitRobot.getYPos(), kitRobot.getTheta());
		//paint kits and its parts
		
		myGfxPartRobot.paintPartRobot(p, g2);
		if(myGfxCamera != null) {
			//System.out.println("painted camera");
			myGfxCamera.paintCamera(p, g2);
		}
		
		//kitRobot.paintKitStationRobot(p, g2, x, y, painterY, gg2)
	}
	*/
	
	private enum RobotAction {
		NONE, PickUpPartFromNest, PutPartIntoKit
	}
	
	private class PartRobotAgentMessage implements Serializable{
		RobotAction action;
		int targetNestIndex;
		int targetNestSlotIndex;
		int targetKitIndex;
		
		public PartRobotAgentMessage(RobotAction a, int tgNstIndex, int tgNstSltIndex) {
			action=a;
			targetNestIndex=tgNstIndex;
			targetNestSlotIndex=tgNstSltIndex;
		}
		
		public PartRobotAgentMessage(RobotAction a, int tgKtIndex) {
			action=a;
			targetKitIndex=tgKtIndex;
		}
	}
	
	private class GantryAgentMessage implements Serializable{
		int partid;
		int targetFeederIndex;
		int action;
	
		public GantryAgentMessage(int a, int pid, int tFeederIndex) {
			action=a;
			partid=pid;
			targetFeederIndex=tFeederIndex;
		}
		public GantryAgentMessage(int a, int index) {
			action=a;
			if (a==1) //target is bin
				partid=index;
			else if (a==3) //target is feeder
				targetFeederIndex=index;
		}
		public GantryAgentMessage(int a) {
			action=a;
		}	
	}
	
	//set flags to false after server processes flags
	public void setFlagsFalse() {
		for (int i = 0; i < messageFlags.length; ++i)
			messageFlags[i] = false;
	}
	
	/** Guys. It's impossible to debug because your logging is on crack.
	 * I'm making this because otherwise I have no idea what's actually happening.
	 * 
	 * Output will have "DO: " prepended for ease of search.
	 * Convention is to write the name of the DoX function plus paramaters, including its do. D capitalized.
	 * @param msg Message to be logged
	 * 
	 * @Author Fintan O'Grady (201)
	 */
	private void logDoMethod(String msg) {
		if (LOG_DO_METHODS) {
			System.out.println("    DO: "+msg);
		}
	}
	private static final boolean LOG_DO_METHODS = true;
	
	/*V2 non-normative*/
	public void DoBadKitNonNormative(ArrayList<Integer> kitsToDelete) {
		//go through the next inspection stand and delete parts in the positions specified there
		makeBadKitPositions = kitsToDelete;
	}

	public void DoRemovePartsFromNestAndPlaceOnLane(int num) {
		if (num >-1 && num < 8)
		removePartsNestIndex = num;
		myLanes.get(num).removeAllParts();
	}

	public void DoPutTwoTypesOfPartsInTheSameLane(int num) {
		if (num >-1 && num < 8)
			myFeeders.get(num/2).setNonNorm();
	}

	public void DoJamLane(int num) {
		myLanes.get(num).jamLane();
		System.out.println("Jamming lane " + num);
	}

	public void DoMakeAllPartsBad(int num) {
		myFeeders.get(num / 2).feedBadParts(); // feed bad parts until it runs out of parts
		myLanes.get(num).makeAllPartsBad();
		myGfxNests[num].makeAllPartsBad();
	}

	public ArrayList<GfxLane>  getMyLanes() {
		return myLanes;
	}

	public  ArrayList<GfxBin> getMyBins() {
		return myBins;
	}

	public GfxGantryRobot getGantry() {
		return gr;
	}

	public  GfxNest[] getMyGfxNests() {
		return myGfxNests;
	}

	public  ArrayList<GfxFeeder> getMyFeeders() {
		return myFeeders;
	}

	public  GfxKitStation getKitStation() {
		return kitStation;
	}

	public  GfxConveyor getConveyor() {
		return conveyor;
	}

	public  GfxGoodConveyor getGoodConveyor() {
		return conveyor2;
	}

	public  GfxKitRobot getKitRobot() {
		return kitRobot;
	}

	public  GfxPartRobot getMyGfxPartRobot() {
		return myGfxPartRobot;
	}


	public  GfxCamera getMyGfxCamera() {
		return myGfxCamera;
	}
	
	public void DoMakeThePartsUnstable(int num) {
		myGfxNests[num].unstablize();
		unstablizedList.add(new Integer[] {num, 100});
	}
	
	public void DoMakeThePartsStable(int num) {
		myGfxNests[num].stablize();
	}

	public void MakeThePartsPileOnTopOfEachOther(int num) {
		myGfxNests[num].screwUp();
	}

	public void updateLaneSpeed(int GFX_UPDATE_SPEED2) {
		LaneAgent.PART_SLIDE_TIME = GFX_UPDATE_SPEED2 / 55;
	}

	public void DoPutPartsRobotInFrontOfCamera(int num) {
		teleportPartsRobotIndex = num;
	}
}
