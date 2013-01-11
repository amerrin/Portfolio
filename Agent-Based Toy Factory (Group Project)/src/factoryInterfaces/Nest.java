package factoryInterfaces;

import java.util.ArrayList;

import factoryObjects.NestVisionResult;

public interface Nest {
	public abstract void msgNeedPart(int partID);
	public abstract void msgHereIsPart(int partID);
	public abstract void msgHereIsPicture(NestVisionResult nvr);
	public abstract void msgNestFull();
	public abstract void msgInitialize();
	public abstract Integer getNestID();
	public abstract void msgPickedUpPart(Integer partIndex);
	public abstract Integer getPartNeeded();
	public abstract void msgTurnOff();
	
	public abstract void msgDidThatFixIt();
}
