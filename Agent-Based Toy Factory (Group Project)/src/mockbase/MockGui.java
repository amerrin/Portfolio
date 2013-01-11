package mockbase;
import factoryObjects.KitVisionResult;
import factoryObjects.NestVisionResult;
import gfxInterfaces.Gui;

import java.util.ArrayList;
import java.util.List;

public class MockGui extends MockAgent implements Gui {

	public static boolean LOG_CONSOLE = false;
	
	String name;

	public MockGui(String name)
	{
		super(name);
		this.name=name;
	}
	public String toString()
	{
		return name;
	}

	public void log(String str) {
		this.log.add(new LoggedEvent(str));
		if (LOG_CONSOLE) {
			System.out.println(str);
		}
	}
	@Override
	public void doServiceFeeder(int partid, int index) {
		log("doing service feeder");
	}

	@Override
	public void doPutBinHome(int index) {
		log("doing put bin home");
	}

	@Override
	public void setPurge(int index) {
		log("doing set purge");
	}

	@Override
	public void divert(int index) {
		log("doing divert");
	}

	@Override
	public void stopFeeding(int index) {
		log("doing stop feeding");
	}

	@Override
	public void startFeeding(int index) {
		log("doing start feeding");
	}

	@Override
	public void DoTurnOffConveyor() {
		log("doing turn off converyor");
	}

	@Override
	public void DoPutKitOnConveyor(int numberOfKits) {
		log("doing put kit on conveyor with "+numberOfKits+" kit(s)");
	}

	@Override
	public void DoTurnOnConveyor() {
		log("doing turn on conveyor");
	}

	@Override
	public void DoTurnOnGoodConveyor() {
		log("doing turn on good conveyor");
	}

	@Override
	public void DoDumpKit() {
		log("doing dump kit");
	}

	@Override
	public void DoTakeKitToConveyorForDelivery() {
		log("doing take kit to conveyor for delivery");
	}

	@Override
	public void DoPlaceKitOnTableForInspection(int tableindex) {
		log("doing do place kit on table for inspection at index "+tableindex);
	}

	@Override
	public void DoPutKitOnTable(int tableindex) {
		log("doing do put kit on table at index "+tableindex);
	}

	@Override
	public void DoLaneTurnOn(int index) {
		log("doing lane turn on");
	}

	@Override
	public void DoLaneTurnOff(int index) {
		log("doing lane turn off");
	}

	@Override
	public void doDumpNest(int index) {
		log("doing dump nest");
	}

	@Override
	public void doDumpParts(int kitindex) {
		log("doing dump parts in to kit "+kitindex);
	}

	@Override
	public void doPickUpPart(int part1, int part2) {
		log("doing pick up part "+ part1+" "+part2);
	}

	@Override
	public void DoCameraInitialize() {
		log("doing camera initialize");
	}

	@Override
	public KitVisionResult DoCameraTakeKitPicture(List<Integer> cfg) {
		log("doing camera take kit picture");
		return new KitVisionResult(null);
	}

	@Override
	public NestVisionResult DoCameraTakeNestPicture(int nestNum) {
		log("doing camera take nest picture at nest number "+nestNum);
		return new NestVisionResult(null);
	}
	@Override
	public void DoCameraSetKitCfgInfo(List<Integer> cfg) {
		log("doing camera set cfg info");		
	}

}