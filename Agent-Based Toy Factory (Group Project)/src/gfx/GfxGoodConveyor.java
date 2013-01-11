package gfx;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GfxGoodConveyor implements Serializable{
	//is being sent over the network
	int timer = 0; int timer2 = 0;
	boolean isMoving;
	//ImageIcon[] conveyorPictures = new ImageIcon[4];
	boolean kitAtEnd = false;
	Point position;
	ArrayList<GfxKit> kitsOnConveyor;
	int imageHeight; //height of the image of the conveyor;
	int imageLength;//length of the imgae of the conveyor

	
	
	public GfxGoodConveyor( Point p) {
		
		kitsOnConveyor = new ArrayList<GfxKit>();
		
		
		
		position = p;
		isMoving = false;
		imageHeight=480;
		imageLength=105;
	}
	
	public void addKitToConveyor(GfxKit kit){
		kit.position.y = position.y;
		kit.position.x = 28;
		kitsOnConveyor.add(kit);
	}
	
	public void turnConveyorOff() {
		isMoving = false;
	}
	
	public void turnConveyorOn() {
		isMoving = true;
	}
	
	public boolean updateGoodKitPosition(){
		
		//if the kits array is not null
		
		//returns this as true if conveyor is done
		boolean shutdownConveyor = false;
		
		if(kitsOnConveyor.size()!=0){
			if(isMoving){
				//move the kits down the conveyor a little bit
				for(int i = 0; i < kitsOnConveyor.size(); i++){
					kitsOnConveyor.get(i).position.y = kitsOnConveyor.get(i).position.y + 4;
				}//end for
			}//end if(isMoving)
			//If the last kit in the array is more than 120 from the top of the conveyor, stop the conveyor.
			/*if(kitsOnConveyor.get(kitsOnConveyor.size()-1).position.y > (position.y +120)){
			 //for Garima - this is where either a message should send to you to shut the conveyor off or I can simply control the off state
			 
			 isMoving = false;
			 }*/
			
			if(kitsOnConveyor.get(0).position.y > (position.y +imageHeight)){
				//gaggarwa - this is where either a message should send to you to shut the conveyor off or I can simply control the off state
				kitsOnConveyor.remove(0);
				shutdownConveyor = true;
				if (kitsOnConveyor.isEmpty())
					isMoving = false;
			}
		}//end if
		return shutdownConveyor;
	}//end method
	
	public boolean isMoving(){
		return isMoving;
	}
	
	public void incTimer() {
		timer++;
	}
	
	//for server we don't need to paint graphics, but I need to update here the position of the kit
	
	
	public void paintConveyor(JPanel frame, Graphics2D d, int x,int y, ImageIcon newConveyour, ImageIcon newConveyourBar, ImageIcon kitImage, ImageIcon[] partsImages,ImageIcon badParts ){
		//If the conveyors are moving, then use timer to display image.
		
		timer=timer%420;
		newConveyour.paintIcon(frame, d, x,y);
		if(isMoving){
			for (int i=0;i<16;i++)
				newConveyourBar.paintIcon(frame, d, x,position.y+(timer*4+i*28)%420);
		} else {
			for (int i=0;i<16;i++)
				newConveyourBar.paintIcon(frame, d, x, position.y+i*28);
		}
		
		//Paint all the kits in the array.
		for (int i = 0; i < kitsOnConveyor.size(); i++)
			kitsOnConveyor.get(i).paintKit(frame, d, kitsOnConveyor.get(i).position.x, kitsOnConveyor.get(i).position.y, kitImage, partsImages, badParts);
	}


}
