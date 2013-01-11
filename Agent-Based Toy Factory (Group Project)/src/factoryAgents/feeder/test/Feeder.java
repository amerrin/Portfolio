package factoryAgents.feeder.test;

public interface Feeder {

	public abstract void msgInitialize();
	
	public abstract void msgIneedPart(LaneInterface lane,int partid);
	
	public abstract void msgHereAreParts(int partid);
	
	public abstract void msgDivert();
	
	public abstract void msgPartsLow();
	
	public abstract void msgIamStable(LaneInterface lane);
	
	public abstract void msgFedPart();
	
	public abstract void msgCase6(LaneInterface lane);
	
}
