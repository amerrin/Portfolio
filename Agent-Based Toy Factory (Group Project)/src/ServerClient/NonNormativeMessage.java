package ServerClient;

/* This message sends the data necessary for the server to carry out a nonnormative scenario. */
public class NonNormativeMessage implements MessageToServer {
	String nnstr;
	
	public NonNormativeMessage(String str) {
		nnstr = str;
	}
	
	/* Parse non-normative string and call do methods on server. */
	public void doIt(HandleAClient hac) {
		Server s = hac.getServer();
		String[] typeAndData = nnstr.split(";");
		String nntype = typeAndData[0];
		String nndata = typeAndData[1];
		
		if(nntype.equals("bk")) // bad kit
		{
			//bad kit data = integers of which parts to delete
			s.DoBadKitNonNormative(nndata);
		}
		else if (nntype.equals("Remove Parts From Nest"))
		{
			s.DoRemovePartsFromNestAndPlaceOnLane(nndata);
		}
		else if (nntype.equals("Jam Lane"))
		{
			s.DoJamLane(nndata);
		}
		else if (nntype.equals("Put 2 Types of Parts in the Same Lane"))
		{
			s.DoPutTwoTypesOfPartsInTheSameLane(nndata);
		}
		else if (nntype.equals("Turn all the Parts Bad"))
		{
			s.DoTurnAllThePartsBad(nndata);
		}
		else if (nntype.equals("Make the Parts Pile on Top of Each Other"))
		{
			s.MakeThePartsPileOnTopOfEachOther(nndata);
		}
		else if (nntype.equals("Make the Parts Unstable"))
		{
			s.DoMakeThePartsUnstable(nndata);
		}
		else if (nntype.equals("Parts Robot In Front Of Camera"))
		{
			s.DoPutPartsRobotInFrontOfCamera(nndata);
		}
		
		
		else
			System.out.println("Error: invalid non-normative case.");
		
	}
}
