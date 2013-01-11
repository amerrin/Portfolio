package ServerClient;
public class TestServer {
    
    public static void main (String[] args) {
        Server s = new Server();

        try{Thread.sleep(5000);}
		catch(Exception e){}
		
		s.DoTurnOnConveyor();
		s.DoTurnOnGoodConveyor();
		s.startFeeding(0);
		s.DoLaneTurnOn(0);
		
		System.out.println("TEST");
		s.doServiceFeeder(11, 0);
		System.out.println("TEST2");
		s.DoPutKitOnConveyor(10);
		//s.DoPutKitOnTable(0);
		//s.DoPutKitOnTable(1);
		s.DoPutKitOnTable(2);
		s.DoTurnOffConveyor();
		s.doPutBinHome(0);
		System.out.println("TEST3");
		
		try{Thread.sleep(13000);}
		catch(Exception e){}
		
		s.DoCameraTakeNestPicture(0);
		s.doPickUpPart(0, 0);
		s.doPickUpPart(0, 0);
		s.doPickUpPart(0, 0);
		s.doPickUpPart(0, 0);
		s.doDumpParts(2);

		try{Thread.sleep(10000);}
		catch(Exception e){}
		
		s.DoPlaceKitOnTableForInspection(2);
		//s.DoCameraTakeKitPicture();
		try{Thread.sleep(10000);}
		catch(Exception e){}
		s.DoTakeKitToConveyorForDelivery();
		
		try{Thread.sleep(15000);}
		catch(Exception e){}
		s.DoLaneTurnOn(4);
		s.DoTurnOnConveyor();
		s.DoTurnOnGoodConveyor();
		s.doServiceFeeder(9, 2);
		s.startFeeding(2);
		s.DoPutKitOnTable(2);
		try{Thread.sleep(13000);}
		catch(Exception e){}
		
		s.DoCameraTakeNestPicture(0);
		s.doPickUpPart(0, 0);
		s.doPickUpPart(4, 0);
		s.doPickUpPart(0, 0);
		s.doPickUpPart(4, 0);
		s.doDumpParts(2);
		
		try{Thread.sleep(10000);}
		catch(Exception e){}
		
		s.DoPlaceKitOnTableForInspection(2);
		//s.DoCameraTakeKitPicture();
		try{Thread.sleep(10000);}
		catch(Exception e){}
		s.DoTakeKitToConveyorForDelivery();
		
    }
}
