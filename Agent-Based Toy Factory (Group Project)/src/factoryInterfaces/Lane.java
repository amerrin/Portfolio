package factoryInterfaces;

public interface Lane {
	//Request parts to start moving again
	public void msgIneedParts(int part);
	//Request parts to stop
	public void msgIamStable();
	//Part presumably just added to the lane.
	public void msgHereIsPart(int part);
	//Dump parts! TODO
	public void msgDumpParts();
	public void msgTurnOff();
	public void msgInitialize();
	//We've been messing up- do some "acoustic maintenance"
	public void msgPartsMissing();
	public void msgFixedStatus(boolean fixed);
	
	public void msgDidThatFixIt();
	
}
