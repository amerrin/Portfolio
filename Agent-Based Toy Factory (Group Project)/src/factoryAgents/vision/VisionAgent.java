package factoryAgents.vision;
import java.util.ArrayList;
import java.util.List;

import agent.Agent;
import factoryInterfaces.FCS;
import factoryInterfaces.KitRobot;
import factoryInterfaces.Nest;
import factoryInterfaces.Vision;
import factoryObjects.KitVisionResult;
import factoryObjects.NestVisionResult;
import factoryObjects.PartState;
import gfxInterfaces.Gui;
import ServerClient.*;
/** The agent in control of the cameras
 * 
 * @author Fintan O'Grady
 *2
 *
 * Main purpose: Encapsulation of the camera setting process, which includes
 *	setting proper camera config, interfacing with the GUI camera, making sure
 *	only one camera fires at a time, and translating nest #s into camera numbers,
 *	if applicable.
 */

public class VisionAgent extends Agent implements Vision {

	
	FCS fcs;
	List<KitVisionRequest> kitRequests = new ArrayList<KitVisionRequest>();
	List<NestVisionRequest> nestRequests = new ArrayList<NestVisionRequest>();
	boolean doInitialize = false;
	boolean on=true;
	
	private Server gui;
	
	// Internal Classes
	class KitVisionRequest {
		KitRobot	agent;
		List<Integer>			cfg;
		KitVisionRequest(KitRobot agent, List<Integer> cfg) {
			this.agent = agent; this.cfg = cfg;
		}
	}
	class NestVisionRequest {
		Nest	agent;
		int				nestNum;
		NestVisionRequest(Nest agent, int nestNum) {
			this.agent = agent; this.nestNum = nestNum;
		}
	}
	

	
	///////////////////////////////////////////
	//		Messages		//
	//////////////////////////
	
	/** Start up the vision systems. Must be called before anything else happens */
	@Override
	public void msgInitialize() {
		print("MsgInitialize");
		doInitialize = true;
		on=true;
		stateChanged();
	}

	/** Take a picture of the numbered nest. Nests are numbered individually, however, the result
	 * to the NestInterface will include parts from both this nest and one adjacent, since the camera
	 * takes pictures of two nests at a time. This is why the PartStates have nestNum fields, in addition
	 * to position.
	 */
	@Override
	public void msgTakeNestPicture(Nest p, int nestNum) {
		print("MsgTakeNestPicture nest: "+nestNum);
		nestRequests.add(new NestVisionRequest(p, nestNum));
		stateChanged();
	}

	/** Compare a kit to the theoretical cfg. If the kit at the inspection table does not match cfg,
	 * it will return failure.
	 */
	@Override
	public void msgTakeKitPicture(KitRobot kitRobot, List<Integer> cfg) {
		print("MsgTakeKitPicture");
		kitRequests.add(new KitVisionRequest(kitRobot, cfg));
		stateChanged();
	}

	public void msgTurnOff()
	{
		on=false;
		stateChanged();
	}
	
	
	
	////////////////////////////////////////////////////
		//				Scheduler				//
	////////////////////////////////////////////////////
	
	/** Simple, right? So simple it doesn't need documentation? Said every
	 * idiot, ever.
	 */
	@Override
	public boolean pickAndExecuteAnAction() {
		if(on)
		{
		if (doInitialize) {
			axnCameraInit();
			doInitialize = false;
			return true;
		}
		
		for (KitVisionRequest k : kitRequests) {
			axnTakeKitPicture(k);
			return true;
		}
		
		for (NestVisionRequest n : nestRequests) {
			axnTakeNestPicture(n);
			return true;
		}
		}
		return false;
	}
	
	
	
	
	///////////////	Actions	//////////////////////////////
	
	private void axnCameraInit() {
		print("AxnCameraInit");
		//Hope this is implemented!
		DoCameraInitialize();
	}
	
	private void axnTakeKitPicture(KitVisionRequest k) {
		print("AxnTakeKitPicture ");
		DoCameraSetKitCfgInfo(k.cfg);
		KitVisionResult result = DoCameraTakeKitPicture(k.cfg);
		k.agent.msgKitStatus(result);
		kitRequests.remove(k);
	}
	
	private void axnTakeNestPicture(NestVisionRequest n) {
		print("AxnTakeNestPicture #"+n.nestNum);
		NestVisionResult result = DoCameraTakeNestPicture(n.nestNum);
		n.agent.msgHereIsPicture(result); 
		nestRequests.remove(n);
	}
	
	
	
	//	Do Commands	//
	// For possible linkage to something else
	private void DoCameraInitialize() {
		print("DoCameraInitialize placeholder");
		gui.DoCameraInitialize();
	}
	private void DoCameraSetKitCfgInfo(List<Integer> cfg) {
		print("DoCameraSetCfgInfo placeholder");
		gui.DoCameraSetKitCfgInfo(cfg);
	}
	private KitVisionResult DoCameraTakeKitPicture(List<Integer> cfg) {
		print("DoCameraTakeKitPicture");
		KitVisionResult kvr = gui.DoCameraTakeKitPicture(cfg);
		
		return kvr;
	}
	private NestVisionResult DoCameraTakeNestPicture(int nestNum) {
		print("DoCameraTakeNestPicture #"+nestNum+" placeholder");
		NestVisionResult New = gui.DoCameraTakeNestPicture(nestNum);
		if (nestNum < 0 || nestNum > 7) {
			print("ERROR: VisionAgent asked to take picture of non-existant Nest");
			return null;
		}
		return New;
	}
	
	public void setGui(Server gui) { this.gui = gui; }
	
	
}
