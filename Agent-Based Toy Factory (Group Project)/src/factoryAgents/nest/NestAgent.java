package factoryAgents.nest;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.Timer;

import agent.Agent;
import factoryAgents.partsRobot.PartsRobotAgent;
import factoryInterfaces.Lane;
import factoryInterfaces.Nest;
import factoryInterfaces.PartsRobot;
import factoryInterfaces.Vision;
import factoryObjects.NestVisionResult;
import gfxInterfaces.Gui;

import ServerClient.*;

public class NestAgent extends Agent implements Nest,ActionListener{
	private int partNeeded;
	private int nestID,inspectionCounter = 0, remainingPartAmount=10;
	private boolean nestFull = false;
	public boolean partAskedFor = true;
	public boolean on=true;
	public boolean checkBadParts = true,retookPicture = false;
	private boolean cameraInspected = true;
	private Semaphore animationBlocker = new Semaphore(0);
	private enum NestState {NORMAL,DUMPING,STABLE,JAMMED}; 
	private NestState nestState = NestState.NORMAL;
	private Boolean partsAskedForAgain = true;
	private Timer lanesJammingTimer = new Timer(15000,this);

	public void actionPerformed(ActionEvent arg0) {
		print("Received laneJam timer. ");
		
		lanesJammingTimer.stop();
		cameraInspected = false;
		stateChanged();
	}
	
	/*
	 * MyPicture class represents a VisionAnalysis (essentially this is an ArrayList<Boolean>(10) )
	 * Pictures have two states: Analyzed and Needs_Analysis to signal the nest to alert PartsRobot he "hasGoodParts"
	 */
	public class MyPicture{
		AnalysisState state;
		List<Boolean> partsChecked;
		
		public MyPicture(List<Boolean> list){
			this.partsChecked = Collections.synchronizedList(new ArrayList<Boolean>(list));
			state = AnalysisState.NEEDS_ANALYSIS;
		}
		
		public MyPicture(String s){
			if (s == "dump"){
				this.partsChecked = new ArrayList<Boolean>();
				this.state = AnalysisState.ANALYZED;
			}
		}
	}
	enum AnalysisState {NEEDS_ANALYSIS,ANALYZED};
	MyPicture nestPic = null;
	
	//constructor
	public NestAgent(int nestID){
		this.nestID = nestID;
		partNeeded =  -1;
	}
	
	//agents nest will communicate with
	private PartsRobot partsRobot;
	private Lane lane;
	private Vision vision;
	private Server GUI;
	
	//messages
	/*
	 * @see agentInterfaces.Nest#msgNeedPart(int)
	 * This message is sent from PartsRobot upon initialization, or when kit configuration changes.
	 * Lane is also messages to dump parts for new configuration
	 */
	public void msgNeedPart(int partID){
		if(partID == this.partNeeded){
			return;
		}
		if(partNeeded != -1)
			lane.msgDumpParts();
		print("Got message to send part to PartsRobot " + partID);
		if(partID != -1)
			partAskedFor = false;
		this.partNeeded = partID;
		nestState = NestState.NORMAL;
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
	 * @see agentInterfaces.Nest#msgHereIsPart(int)
	 * Lane messages nest "hereIsPart" for every individual part passed to the nest.
	 * Thus, nest keeps track of how many times message is received,
	 * and calls nestFull() to alert lane
	 */
	public void msgHereIsPart(int partID){
		print ("MsgHereIsPart " +partID);
		if (partID == partNeeded && (nestState == NestState.NORMAL || nestState==NestState.JAMMED) ){
			lanesJammingTimer.restart();
			inspectionCounter++;
		}
		else if (partID != partNeeded && nestState == NestState.NORMAL){
			nestState = NestState.DUMPING;
			nestPic = new MyPicture("dump");
			nestPic.partsChecked.add(false);
			stateChanged();
			return;
		}
		else if (partID != partNeeded && nestState == NestState.DUMPING){
			nestPic.partsChecked.add(false);
			stateChanged();
			return;
		}
		else if (partID == partNeeded && nestState == NestState.DUMPING){
			nestPic.partsChecked.add(true);
			int i = nestPic.partsChecked.size();
			while(i < 10){
				nestPic.partsChecked.add(false);
				i++;
			}
			stateChanged();
			return;
		}
		if (inspectionCounter == remainingPartAmount){
			this.msgNestFull();
			inspectionCounter = 0;
		}
	}
	
	/*0
	 * @see agentInterfaces.Nest#msgHereIsPicture(java.util.ArrayList)
	 * Vision sends "hereIsPicture" to nest with "results" of good/bad parts (essentially this is an ArrayList<Boolean>(10))
	 */
	public void msgHereIsPicture(NestVisionResult nvr){ 
		print("Received analysis from camera.");
		checkBadParts = true;
		this.nestPic = new MyPicture(nvr.getGoodBadPartsList());
		if(nestState == NestState.JAMMED){
			if(nestPic.partsChecked.size() > 0){
				lane.msgFixedStatus(true);
				this.nestState = NestState.NORMAL;
			}
			else if(nestPic.partsChecked.size()==0)
				lane.msgFixedStatus(false);
			return;
		}
		if (nestPic.partsChecked.size() == 0){
			checkBadParts=false;
			lane.msgPartsMissing();
			this.nestState = NestState.JAMMED;
		}
		for(int i=0;i<nestPic.partsChecked.size();i++){
			if(nestPic.partsChecked.get(i) == true){
				checkBadParts = false;
			}
		}
		if(checkBadParts){
			if(retookPicture){
				print("All parts verified as bad from second picture - dumping nest");
				GUI.appendText("All parts verified as bad from second picture - dumping nest");
				this.nestState = NestState.DUMPING;
			}
			else{
				print("All parts are bad! Waiting 3 seconds to verify image wasn't blocked.");
				GUI.appendText("All parts are bad! Waiting 3 seconds to verify image wasn't blocked.");
				Timer t2 = new Timer(3000,new ActionListener(){
					public void actionPerformed(ActionEvent e){
						print("Taking another picture to verify bad parts");
						GUI.appendText("Taking another picture to verify bad parts");
						cameraInspected = false;
						retookPicture = true;
						nestPic.state = AnalysisState.ANALYZED;
						stateChanged();
					}
				});
				t2.start();t2.setRepeats(false);
			}
		}
		stateChanged();
	}
	
	/*
	 * @see agentInterfaces.Nest#msgNestFull()
	 * Message is received from nest itself, and will trigger "asking" camera for an inspection.
	 */
	public void msgNestFull(){
		print("Received message that nest if full");
		lane.msgIamStable();
		nestState = NestState.STABLE;
		cameraInspected = false;
		nestFull = true;
		lanesJammingTimer.stop();
		stateChanged();
	}
	
	/*
	 * @see agentInterfaces.Nest#msgPickedUpPart(java.lang.Integer)
	 * PartsRobot alerts nest when he "takes a part" from the nest
	 * Useful for keeping track of how full the neset is
	 */
	public void msgPickedUpPart(Integer partIndex){
			nestPic.partsChecked.remove(0);
			if(nestPic.partsChecked.size() <= 7 && nestState == NestState.STABLE)
				partsAskedForAgain = false;
			stateChanged();
	}

	public void msgDidThatFixIt() {
		print("Received msgDidThatFixIt from lane.");
		GUI.appendText("Received msgDidThatFixIt from lane.");
		cameraInspected = false;
		stateChanged();
	}
	
	/*
	 * Called by GUI to signal the animation has finished
	 */
	public void msgAnimationUnblock(){
		animationBlocker.release();
	}
	
	//scheduler
	public boolean pickAndExecuteAnAction(){
		if(on)
		{
		if (nestPic!= null &&nestPic.state == AnalysisState.NEEDS_ANALYSIS){
			ChooseAndSendGoodParts();
			return true;
		}
		if (nestState == NestState.STABLE && !partsAskedForAgain){
			AskForMoreParts();
			return true;
		}
		if (!partAskedFor){
			AskForPart();
			return true;
		}
		if (/*nestFull &&*/ !cameraInspected){
			GetPartsInspected();
			return true;
		}
		if (nestState == NestState.DUMPING){
			if(nestPic.partsChecked.size() == 10){
				DoDumpNest();
				return true;}
		}
		}
		return false;
	}
	
	//actions
	/*
	 * Passes all "good" parts (at most 10) to PartsRobot so he may pick them up
	 */
	private void ChooseAndSendGoodParts(){

			nestPic.state = AnalysisState.ANALYZED;
			for (int i=(nestPic.partsChecked.size()-1); i>-1; i--){
				if(i<nestPic.partsChecked.size()){
					if (nestPic.partsChecked!=null &&nestPic.partsChecked.get(i) == true){
						ArrayList<Integer> goodPart = new ArrayList<Integer>(2);
						goodPart.add(nestID);
						goodPart.add(i);
						partsRobot.msgHereAreGoodParts(this,goodPart);
					}
				}
			}
			
	}
	
	/*
	 * Asks the lane for 10 parts of new part type
	 * Nest will dump if configuration has changed, and ask lane for new configuration part
	 */
	private void AskForPart(){
		if (nestPic != null){
			partsRobot.msgClearNestData(this.nestID);
			GUI.doDumpNest(nestID);
	    	try {
				animationBlocker.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nestPic=null;
		}
		partAskedFor = true;
		lane.msgIneedParts(partNeeded);
		stateChanged();
	}
	
	private void DoDumpNest(){
		lane.msgDumpParts();
		if (nestPic.partsChecked != null){
			GUI.doDumpNest(nestID);
			
	    	try {
				animationBlocker.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			nestPic=null;
		}
		partsRobot.msgClearNestData(this.nestID);
		lane.msgIneedParts(partNeeded);
		nestState = NestState.NORMAL;
		retookPicture = false;
		remainingPartAmount = 10;
		inspectionCounter = 0;
	}
	
	/*
	 * Asks lane for X more parts when nest has 5 or fewer parts
	 */
	private void AskForMoreParts(){
		remainingPartAmount = 10 - nestPic.partsChecked.size();
		lane.msgIneedParts(partNeeded);
		partsAskedForAgain = true;
		nestState = NestState.NORMAL;
	}
	
	/*
	 * asks camers to inspect parts when nest is full
	 */
	private void GetPartsInspected(){
		cameraInspected = true;
		vision.msgTakeNestPicture(this,nestID);
		stateChanged();
	}
	
	//various setter and getter functions
	public void setPartsRobot(PartsRobot partsRobot2){
		this.partsRobot = partsRobot2;
	}
	
	public void setVision(Vision vision){
		this.vision = vision;
	}
	
	@Override
	public String getName() {
		return "Nest "+nestID;
	}
	
	public void setLane(Lane l){
		this.lane = l;
	}
	
	public void setGUI(Server p){
		this.GUI = p;
	}
	
	public Integer getNestID(){
		return this.nestID;
	}
	
	public Integer getPartNeeded(){
		return this.partNeeded;
	}
}