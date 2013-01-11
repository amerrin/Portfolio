package gfx;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GfxKitStation  implements Serializable{
	//is being sent over the network
	
	int x;
	int y;
	GfxKit[] kitsOnTheTable=new GfxKit[3];
	
	public  GfxKitStation(int xLocation, int yLocation) {//constructor
		x=xLocation;
		y=yLocation;
	}
	
	public GfxKit removeKitFromStation(GfxKit kit){
		for (int i=0;i<3;i++){
			if (kitsOnTheTable[i]==kit){
				GfxKit temp=kitsOnTheTable[i];
				kitsOnTheTable[i]=null;
				return temp;
			}
		}
		return null;
	}
	
	public void putKitOnStation(GfxKit kit, int spot){
		kitsOnTheTable[spot]=kit;
	}
	//method for painting the graphics
	public void paintKittingStation(JPanel frame,Graphics2D d, ImageIcon kitStationPicture, ImageIcon kitImage, ImageIcon[] partsImages,ImageIcon badParts ) {
		kitStationPicture.paintIcon(frame,d, x, y);
		for (int i=0;i<3;i++){
			if (kitsOnTheTable[i] != null){
				kitsOnTheTable[i].paintKit(frame, d, x+7 + (55 * i), y + 6, kitImage, partsImages, badParts);
			}
		}
	}
	
	public boolean isKitInInspection()
	{
		if (kitsOnTheTable[0] != null)
			return true;
		else
			return false;
	}
	public GfxKit getInspectionKit()
	{
		//returns null if no kit in inspection
		return kitsOnTheTable[0];
		
	}
}
