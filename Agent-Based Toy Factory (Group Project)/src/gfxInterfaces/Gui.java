package gfxInterfaces;

import java.util.List;

import factoryObjects.KitVisionResult;
import factoryObjects.NestVisionResult;

public interface Gui {

	public abstract void doServiceFeeder(int partid, int index);

	public abstract void doPutBinHome(int index);

	public abstract void setPurge(int index);

	public abstract void divert(int index);

	public abstract void stopFeeding(int index);

	public abstract void startFeeding(int index);

	public abstract void DoTurnOffConveyor();

	public abstract void DoPutKitOnConveyor(int numberOfKits);

	public abstract void DoTurnOnConveyor();

	public abstract void DoTurnOnGoodConveyor();

	public abstract void DoDumpKit();

	public abstract void DoTakeKitToConveyorForDelivery();

	public abstract void DoPlaceKitOnTableForInspection(int tableindex);

	public abstract void DoPutKitOnTable(int tableindex);

	public abstract void DoLaneTurnOn(int index);

	public abstract void DoLaneTurnOff(int index);
	
	public abstract void DoLaneFrequencyHigh(int index);
	
	public abstract void DoLaneFrequencyLow(int index);

	public abstract void doDumpNest(int index);

	public abstract void doDumpParts(int kitindex);

	public abstract void doPickUpPart(int part1,int part2 );

	public abstract void DoCameraInitialize();

	public abstract void DoCameraSetKitCfgInfo(List<Integer> cfg);

	public abstract KitVisionResult DoCameraTakeKitPicture(List<Integer> cfg);

	public abstract NestVisionResult DoCameraTakeNestPicture(int nestNum);
	
	public abstract void appendText(String message);
}