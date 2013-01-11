package ServerClient;

import factoryAgents.Conveyor.ConveyorAgent;
import factoryAgents.KitRobot.KitRobotAgent;
import factoryAgents.feeder.FeederAgent;
import factoryAgents.gantry.GantryAgent;
import factoryAgents.lane.LaneAgent;
import factoryAgents.nest.NestAgent;
import factoryAgents.partsRobot.PartsRobotAgent;
import factoryAgents.vision.VisionAgent;
import factoryInterfaces.Feeder;
import factoryInterfaces.Lane;
import factoryObjects.KitVisionResult;
import factoryObjects.NestVisionResult;
import gfx.GfxFactory;
import gfx.GfxPart;
import gfxInterfaces.Gui;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

import javax.swing.JSlider;

/*
 * The server is the backend for managing the factory. 
 * It will contain t0te with all of the Manager clients through sockets.
 */

public class Server implements Gui {
	//const framerate makes timings more accurate, but causes severe flicker
	//	on slow computers.

	private static final boolean USE_CONSTANT_FRAMERATE = true;
	static int GFX_UPDATE_SPEED = 55;

	private ServerSocket myServerSocket;
	private javax.swing.Timer updateTimer;
	private ArrayList<HandleAClient> clients;
	private ArrayList<BlueprintPart> possibleParts;
	private ArrayList<BlueprintKit> possibleKits;
	//factory data
	private GfxFactory myGfxFactory;
	//production schedule
	private ArrayList<Order> orders;
	
    //Agents
	ConveyorAgent	conveyorAgent;
	GantryAgent		gantryAgent;
	KitRobotAgent	kitRobotAgent;
	PartsRobotAgent	partsRobotAgent;
	VisionAgent		visionAgent;
	factoryAgents.fcs.fcsAgent fcsAgent;
	
	
	NestAgent[] nests = new NestAgent[8];
	LaneAgent[] lanes = new LaneAgent[8];
	FeederAgent[] feeders = new FeederAgent[4];
	
	Semaphore gfSem, psSem;
	
	PanelServer panelServer; //GUI panel with buttons that starts managers
	
	/* Initialize factory state and gfx factory state, start agents, start graphics thread, and wait for connections from managers. */
	public Server() {
		myGfxFactory = new GfxFactory();
		gfSem = new Semaphore(1);
		psSem = new Semaphore(1);
		
		clients = new ArrayList<HandleAClient>();
		possibleParts = new ArrayList<BlueprintPart>();
		possibleKits = new ArrayList<BlueprintKit>();
		
		// if fail load possible parts and kits, then add default possible parts and kits
		if (!loadPossiblePartsAndKits()) {
			addDefaultParts();
			addDefaultKit();
		}
		
		orders = new ArrayList<Order>();
		
		//start up all threads
		startAgentThreads();
		startGfxUpdateThread();
		startConnectToManagersThread();
		panelServer=new PanelServer(this);
		panelServer.setVisible(true);
	}
	
	//start agent threads (from 201 teams)
	public void startAgentThreads() {
		System.out.println("Starting agents");
		
		conveyorAgent	= new ConveyorAgent("Conveyor", this);
		gantryAgent		= new GantryAgent("Gantry");
		kitRobotAgent	= new KitRobotAgent("KitRobot", this);

		for (int i=0; i<nests.length;i++)	nests[i] = new NestAgent(i);
		for (int i=0; i<lanes.length; i++)	lanes[i] = new LaneAgent(i);
		for (int i=0; i<feeders.length; i++)feeders[i] = new FeederAgent(("Feeder" +i), lanes[2*i], lanes[2*i+1], gantryAgent, i);
		
		partsRobotAgent	= new PartsRobotAgent();
		visionAgent		= new VisionAgent();
		
		partsRobotAgent . setGUI(this);
		visionAgent		. setGui(this);
		gantryAgent		. setGui(this);
		for (int i=0; i<feeders.length; i++) feeders[i].setGui(this);
		
		ArrayList<FeederAgent> tempFeeders = new ArrayList<FeederAgent>();
		
		List<Feeder> tempFA = new ArrayList<Feeder>();
		for (int i=0;i<feeders.length;i++) {
			tempFA.add(feeders[i]);
		}
		List<Lane> tempLA = new ArrayList<Lane>();
		for (int i=0;i<lanes.length;i++)
		{
			tempLA.add(lanes[i]);
		}
		gantryAgent		. setFeeders(tempFA);
		conveyorAgent.setKitRobot(kitRobotAgent);
		kitRobotAgent.setPartsRobot(partsRobotAgent);
		kitRobotAgent.setVisionAgent(visionAgent);
		kitRobotAgent.setConveyor(conveyorAgent);
		partsRobotAgent . setNests(nests);
		partsRobotAgent.setKitRobot(kitRobotAgent);
		for (int i=0; i<nests.length;i++) {
			nests[i].setGUI(this);
			nests[i].setLane(lanes[i]);
			nests[i].setVision(visionAgent);
			nests[i].setPartsRobot(partsRobotAgent);
		}
		for (int i=0; i<lanes.length;i++) {
			lanes[i].setGui(this);
			lanes[i].setFeeder(feeders[i/2]);
			lanes[i].setNest(nests[i]);
		}
		
		fcsAgent = new factoryAgents.fcs.fcsAgent(conveyorAgent, gantryAgent, kitRobotAgent, partsRobotAgent, tempFA, tempLA);
		
		//start threads
		//initialize all
		conveyorAgent.startThread();
		for (int i=0;i<feeders.length;i++) feeders[i].startThread();
		gantryAgent.startThread();
		kitRobotAgent.startThread();
		for (int i=0;i<lanes.length;i++) lanes[i].startThread();
		
		for (int i=0; i<nests.length;i++) {
			nests[i].startThread();
		}
		
		partsRobotAgent.startThread();
		visionAgent.startThread();
		
		fcsAgent.startThread();
		fcsAgent.msgInitialize();
	}
	
	//makes a thread for updating the graphics
	public void startGfxUpdateThread() {
		new Thread(new UpdateGfxThread()).start();
	}
	
	public void startConnectToManagersThread(){
		new Thread(new ConnectToManagers()).start();
	}
	
	//start server socket and establish connections to all managers
	public void connectToManagers() {
		try {
			System.out.println("Debug: connecting to managers. waiting for connections.");
			myServerSocket = new ServerSocket(Manager.PORT);
			while (true) {
				Socket socketTemp = myServerSocket.accept();
				//accepted socket connection
				System.out.println("Debug: one accepted connection");
				
				//create a thread with this client
				HandleAClient hacTemp = new HandleAClient(this, socketTemp);
				//add to list of clients
				clients.add(hacTemp);
				//start thread
				new Thread(hacTemp).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class ConnectToManagers implements Runnable {
		public void run() {
			try {
				connectToManagers();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Thread to update graphics every GFX_UPDATE_SPEED ms. */
	private class UpdateGfxThread implements Runnable {
		public void run() {
			long lastRun=System.currentTimeMillis();
			try {
				while (true) {
					if (USE_CONSTANT_FRAMERATE) {
						Thread.sleep(Math.max(0,GFX_UPDATE_SPEED-(System.currentTimeMillis() - lastRun)));
					} else {
						Thread.sleep(GFX_UPDATE_SPEED);
					}
					
					//update all the graphics
					gfSem.acquire();
					lastRun = System.currentTimeMillis();
					updateGraphics();
					gfSem.release();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/* Call GfxFactory update method. */
	public void updateGraphics() {
		//called with every tick of the clock
		
		boolean[] messageFlags = myGfxFactory.updateGfxFactory();
		
		//send messages to agents corresponding to flags, then set flags to null in GfxFactory
		if (messageFlags[0])
			kitRobotAgent.msgAnimationDone();
		if (messageFlags[1])
			partsRobotAgent.msgAnimationUnblock();
		if (messageFlags[2])
			gantryAgent.msgAtDestination();
		if (messageFlags[3])
			conveyorAgent.msgAnimationDone();
		for (int i = 0; i < 4; ++i) {
			if (messageFlags[5 + i])
				feeders[i].msgFedPart();
			if (messageFlags[9 + i])
				feeders[i].msgPartsLow();
		}
		
		//whenever a kit reaches end of exit conveyor
		if (messageFlags[4]) {
			//subtract from number remaining to make and check if we don't need any more
			if (orders.size() > 0) {
				try {
					psSem.acquire();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				if (--orders.get(0).n == 0) {
					orders.remove(0);
					if (!orders.isEmpty()) { //do method to tell agents to start making next kit type
						System.out.print("Debug: telling agents to start making kits. partids: ");
						//message to tell agents to start making kits
						ArrayList<BlueprintPart> parts = possibleKits.get(orders.get(0).kID).getParts();
						ArrayList<Integer> partids = new ArrayList<Integer>();
						for (BlueprintPart p : parts)
							partids.add(Integer.parseInt(PartsManager.getImageFileNumberFromName(p.getImageType())));
						System.out.println(partids);
						fcsAgent.msgMakeKits(partids, orders.get(0).n);
					}
				}
				psSem.release();
			}
		}
		for(int i=0; i<8; i++) {
			if(messageFlags[13+i])
				nests[i].msgAnimationUnblock();
		}
		myGfxFactory.setFlagsFalse();
	}
	
	/* Stop factory production and clear production schedule when message is received. */
	public void stopProduction() {
		//tell agents to stop/dump everything
		//FCSAgent.msgStopFactory();
		orders.clear();
	}
	
	//getters
	public GfxFactory getGfxFactory() { return myGfxFactory; }
	public ArrayList<Order> getOrders() { return orders; }
	public ArrayList<BlueprintPart> getPossibleParts() { return possibleParts; }
	public ArrayList<BlueprintKit> getPossibleKits() { return possibleKits; }
	
	//setters
	public void addOrder(Order o) {
		orders.add(o);
		if (orders.size() == 1) {
			System.out.print("Debug: telling agents to start making kits. partids: ");
			//message to tell agents to start making kits
			ArrayList<BlueprintPart> parts = possibleKits.get(orders.get(0).kID).getParts();
			ArrayList<Integer> partids = new ArrayList<Integer>();
			for (BlueprintPart p : parts)
				partids.add(Integer.parseInt(PartsManager.getImageFileNumberFromName(p.getImageType())));
			System.out.println(partids);
			fcsAgent.msgMakeKits(partids, orders.get(0).n);
		}
	}
	
	//adds part, then saves possible parts/kits
	public void addPart(BlueprintPart p) {
		possibleParts.add(p);
		savePossiblePartsAndKits();
	}
	
	//adds kit, then saves possible parts/kits
	public void addKit(BlueprintKit k) {
		possibleKits.add(k);
		savePossiblePartsAndKits();
	}
	
	//searches possibleParts for part to remove and removes it if found, then saves possible parts/kits
	public void removePart(BlueprintPart p) {
		for (int i = 0; i < possibleParts.size(); ++i)
			if (possibleParts.get(i).equals(p))
				possibleParts.remove(i);
		savePossiblePartsAndKits();
	}
	
	//searches possibleKits for kit to remove and removes it if found, then saves possible parts/kits
	public void removeKit(BlueprintKit k) {
		for (int i = 0; i < possibleKits.size(); ++i)
			if (possibleKits.get(i).equals(k))
				possibleKits.remove(i);
		savePossiblePartsAndKits();
	}
	
	//Load up the default parts into the possible parts list
	public void addDefaultParts(){
		possibleParts.add(new BlueprintPart("None", "0", "None", "", "None"));
		possibleParts.add(new BlueprintPart("Al Part", "1", "An Alien", "src/images/part1.png","Green Alien"));
		possibleParts.add(new BlueprintPart("Pi Part", "2", "Hammy", "src/images/part2.png","Pig"));
		possibleParts.add(new BlueprintPart("Bi Part", "3", "See farther", "src/images/part3.png","Binoculars"));
		possibleParts.add(new BlueprintPart("As Part", "4", "An astronaut", "src/images/part4.png","Buzz"));
		possibleParts.add(new BlueprintPart("Co Part", "5", "A cowboy", "src/images/part5.png","Woody"));
		possibleParts.add(new BlueprintPart("Pe Part", "6", "A penguin", "src/images/part6.png","Penguin"));
		possibleParts.add(new BlueprintPart("Po Part", "7", "Irish want this", "src/images/part7.png","Potato Man"));
		possibleParts.add(new BlueprintPart("Bo Part", "8", "And her sheep", "src/images/part8.png","Bo Peep"));
	}
	
	//Set up the Default kit
	public void addDefaultKit(){
		try {
			ArrayList<BlueprintPart> firstKitParts = new ArrayList<BlueprintPart>();
			for(int i =1; i < 9; i ++){
				firstKitParts.add(possibleParts.get(i));
			}
			possibleKits.add(new BlueprintKit("Default", firstKitParts));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	/* Load possible parts/kits when server starts and return whether or not load was successful. */
	public boolean loadPossiblePartsAndKits() {
		try {
			FileInputStream fis = new FileInputStream("parts_kits.sav");
			ObjectInputStream ois = new ObjectInputStream(fis);
			ArrayList<BlueprintPart> loadedParts = (ArrayList<BlueprintPart>)ois.readObject();
			ArrayList<BlueprintKit> loadedKits = (ArrayList<BlueprintKit>)ois.readObject();
			ois.close();
			fis.close();
			
			// deep copy loaded parts/kits into possible parts/kits in order to avoid invalid pointer problems
			possibleParts.clear();
			for (BlueprintPart loaded : loadedParts)
				possibleParts.add(loaded);
			possibleKits.clear();
			for (BlueprintKit loaded : loadedKits)
				possibleKits.add(loaded);
			
			System.out.println("Debug: Loaded possible parts and kits.");
			return true;
		} catch (Exception e) {
			System.out.println("Debug: Failed to load possible parts and kits.");
			return false;
		}
	}
	
	/* Save possible parts/kits whenever these lists change. */
	public void savePossiblePartsAndKits() {
		try {
			FileOutputStream fos = new FileOutputStream("parts_kits.sav");
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(possibleParts);
			oos.writeObject(possibleKits);
			oos.close();
			fos.close();
			
			System.out.println("Debug: Saved possible parts and kits.");
		} catch (Exception e) {
			System.out.println("Debug: Failed to save possible parts and kits.");
		}
	}
	
	//DoXXXs
	public void doPickUpPart(int targetNestIndex, int targetNestSlotIndex) {
		myGfxFactory.doPickUpPart(targetNestIndex, targetNestSlotIndex);
	}
	
	public void doDumpParts(int kitIndex) {
		myGfxFactory.doDumpParts(kitIndex);
	}
	
	public void doShowCamera(int nestIndex) {
		//TODO: Fix camera location here
		myGfxFactory.doShowCamera(nestIndex);
	}

	public void addPartToNest(int index, GfxPart p) {
		myGfxFactory.addPartToNest(index, p);
	}
	
	public void DoTakeKitToConveyorForDelivery(){//from inspection to conveyor away
		myGfxFactory.DoTakeKitToConveyorForDelivery();
	}
    
	public void DoPutKitOnTable(int spot){  //from conveyor to specific spot
		myGfxFactory.DoPutKitOnTable(spot);
	}
	
	public void DoPlaceKitOnTableForInspection(int spot){ //from specific spot to inspection
		myGfxFactory.DoPlaceKitOnTableForInspection(spot);
	}
	
	public void DoPutKitOnConveyor(int number){
		myGfxFactory.DoPutKitOnConveyor(number);
	}
	
	public void DoTurnOffConveyor() {
		myGfxFactory.DoTurnOffConveyor();
	}
	
	public void DoTurnOnConveyor(){
		myGfxFactory.DoTurnOnConveyor();
	}
	
	public void DoTurnOnGoodConveyor(){
		myGfxFactory.DoTurnOnGoodConveyor();
	}
	
	public void doServiceFeeder(int partid, int targetFeederIndex) {  //scenario 1
		myGfxFactory.doServiceFeeder(partid, targetFeederIndex);
	}
	
	public void doPickUpBin(int targetBinIndex) { //deprecated
		myGfxFactory.doPutBinHome(targetBinIndex);
	}
	
	public void doEmptyBinInFeeder(int targetFeederIndex) { //deprecated
		myGfxFactory.doPutBinHome(targetFeederIndex);
	}
	
	public void doPutBinHome(int feederIndex) {  //scenario 2 destroy bin
		myGfxFactory.doPutBinHome(feederIndex);
	}
	
	public void DoLaneTurnOn(int laneindex) {
		myGfxFactory.DoTurnLaneOn(laneindex);
	}
	
	public void DoLaneTurnOff(int laneindex) {
		myGfxFactory.DoTurnLaneOff(laneindex);
	}
	
	public void DoLaneFrequencyHigh(int laneindex) {
		myGfxFactory.DoLaneFrequencyHigh(laneindex);
	}
	
	public void DoLaneFrequencyLow(int laneindex) {
		myGfxFactory.DoLaneFrequencyLow(laneindex);
	}
	
	public void startFeeding(int id){
		myGfxFactory.startFeeding(id);
	}
	
	public void stopFeeding(int id){
		myGfxFactory.stopFeeding(id);
	}
	
	public void divert(int id){
		myGfxFactory.divert(id);
	}
	public void doDumpNest(int id){
		myGfxFactory.doDumpNest(id);
	}
	
	public void DoCameraInitialize() {
		myGfxFactory.DoCameraInitialize();
	}

	public KitVisionResult DoCameraTakeKitPicture(List<Integer> cfg) {
		return myGfxFactory.DoCameraTakeKitPicture(cfg);
	}

	public NestVisionResult DoCameraTakeNestPicture(int nestNum) {
		NestVisionResult kvr = myGfxFactory.DoCameraTakeNestPicture(nestNum);
		return kvr;
	}

	public void DoCameraSetKitCfgInfo(List<Integer> cfg) {
		myGfxFactory.DoCameraSetKitCfgInfo(cfg);
	}
	/*V2 non-normative*/
	public void DoBadKitNonNormative(String badkitPositions)
	{
		panelServer.appendTheTextArea("Making Bad Kit when it moves to inspection stand");
		String[] badPos = badkitPositions.split(",");
		ArrayList<Integer> kitsToDelete = new ArrayList<Integer>();
		
		for (int i = 0; i < badPos.length; i++)
			kitsToDelete.add(Integer.parseInt(badPos[i]));
		
		myGfxFactory.DoBadKitNonNormative(kitsToDelete);
	}
	//end non-normative

	@Override
	public void setPurge(int index) {
		// TODO Auto-generated method stub
		
	}
	
	public void appendText(String message){
		panelServer.appendTheTextArea(message);
	}

	
	public void DoDumpKit() {
		myGfxFactory.DoDumpKit();
	}

	public void DoRemovePartsFromNestAndPlaceOnLane(String data) {
		if (data.length() > 0)
		{
		int num = Integer.parseInt(data.substring(data.length()-1));
		panelServer.appendTheTextArea("Removing parts from nest id " + num + " if possible.");
		myGfxFactory.DoRemovePartsFromNestAndPlaceOnLane(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: remove parts from nest");
		}
	}

	public void DoPutTwoTypesOfPartsInTheSameLane(String data) {
		if (data.length() > 0)
		{
		int num = Integer.parseInt(data.substring(data.length()-1));
		panelServer.appendTheTextArea("Putting two diff. types of parts in lane " + num + " if possible.");
		System.out.println("Putting two diff. types of parts in lane " + num + " if possible.");
		myGfxFactory.DoPutTwoTypesOfPartsInTheSameLane(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: put two types of parts in one lane");
		}
	}

	public void DoTurnAllThePartsBad(String data) {
		if (data.length() > 0)
		{
		int num = Integer.parseInt(data.substring(data.length()-1));
		panelServer.appendTheTextArea("Make all parts bad" + num + " if possible.");
		System.out.println("Make all parts bad index" + num + " if possible.");
		myGfxFactory.DoMakeAllPartsBad(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative:all parts bad");
		}
	}
	
	public void DoJamLane(String data){
		if (data.length() > 0)
		{
		int num = Integer.parseInt(data.substring(data.length()-1));
		panelServer.appendTheTextArea("Jam lane " + num + " if possible.");
		System.out.println("Jam lane " + num + " if possible.");
		myGfxFactory.DoJamLane(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: jam lane");
		}
	}
	
	public void DoMakeThePartsUnstable(String nndata) {
		if (nndata.length() > 0)
		{
		int num = Integer.parseInt(nndata.substring(nndata.length()-1));
		panelServer.appendTheTextArea("Make the parts in nest " + num + " unstable if possible.");
		System.out.println("Make the parts in nest " + num + " unstable if possible.");
		myGfxFactory.DoMakeThePartsUnstable(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: make the parts unstable");
		}
	}
	
	public void DoPutKitOnTableFromInspection(int spot) {
		myGfxFactory.DoPutKitOnTableFromInspection(spot);
	}

	public void MakeThePartsPileOnTopOfEachOther(String nndata) {
		if (nndata.length() > 0)
		{
		int num = Integer.parseInt(nndata.substring(nndata.length()-1));
		panelServer.appendTheTextArea("Make the parts pile on top of each other in nest " + num + " if possible.");
		
		System.out.println("Make the parts pile on top of each other in nest " + num + " if possible.");
		myGfxFactory.MakeThePartsPileOnTopOfEachOther(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: make the parts pile on top of each other");
		}
	}
	
	public static void main (String[] args) {
		Server s = new Server();
		
	}

	public void updateGfxSpeed(int speed) {
		// TODO Auto-generated method stub
		myGfxFactory.updateLaneSpeed(speed);
		GFX_UPDATE_SPEED = -speed + 200;
	}

	public void DoPutPartsRobotInFrontOfCamera(String nndata) {
		// TODO Auto-generated method stub
		if (nndata.length() > 0)
		{
		int num = Integer.parseInt(nndata.substring(nndata.length()-1));
		panelServer.appendTheTextArea("Put robot in front of camera " + num + " when it takes a picture of that nest nest.");
		//System.out.println("Make the parts pile on top of each other in nest " + num + " if possible.");
		myGfxFactory.DoPutPartsRobotInFrontOfCamera(num);
		}
		else
		{
			System.out.println("Error: no data given to non-normative: robot in front of camera");
		}
	}
}
