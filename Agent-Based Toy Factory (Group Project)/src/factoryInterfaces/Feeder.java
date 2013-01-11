package factoryInterfaces;

public interface Feeder {

	public abstract void msgInitialize();
	
	public abstract void msgIneedParts(Lane lane,int partid);
	
	public abstract void msgHereAreParts(int partid);
	
	public abstract void msgPartsLow();
	
	public abstract void msgIamStable(Lane lane);
	
	public abstract void msgFedPart();
	
	public abstract void msgTurnOff();
	
	public abstract void msgFixedStatus(boolean fixed,Lane lane);
	
	public abstract void msgFixYourSelf(Lane lane);
	
}
