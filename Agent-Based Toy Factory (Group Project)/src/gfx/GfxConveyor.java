package gfx;

import java.awt.Graphics2D;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GfxConveyor implements Serializable{
	//is being sent over the network
	int timer,timer2;
	boolean isMoving = true;
	//ImageIcon[] conveyorPictures = new ImageIcon[4];
	boolean kitAtEnd = false;
	Point position;
	ArrayList<GfxKit> kitsOnConveyor;
	boolean sentOnce;
	int imageHeight; //height of the image of the conveyor;
	int imageLength;//length of the imgae of the conveyor
	
	
	
	public GfxConveyor(Point p) {
		timer=0;
		timer2=0;
		kitsOnConveyor = new ArrayList<GfxKit>();
		
		sentOnce = false;
		
		
		
		
		position = p;
		imageHeight=280;
		imageLength=105;
	}
	
	public boolean isMoving(){
		return isMoving;
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
		sentOnce = true;
		isMoving = true;
	}
	
	public boolean updateKitPosition() {
		boolean endOfLineMsg = false;
		
		if (!kitsOnConveyor.isEmpty()) {
			//if first kits position is less than 92 from the bottom of the conveyor
			if (kitsOnConveyor.get(0).position.y < (position.y + (imageHeight-91))) {
				sentOnce=false;
				if (kitsOnConveyor.size() == 1)
					kitsOnConveyor.get(0).position.y = kitsOnConveyor.get(0).position.y + 4;
				else {
					//update each kits position
					for( int i = 0; i < kitsOnConveyor.size(); i++) {
						//only update a kits position if it is 100 away from the kit in front of it or it is the first kit
						if( (i == 0) || (kitsOnConveyor.get(i-1).position.y - kitsOnConveyor.get(i).position.y > 100))
						kitsOnConveyor.get(i).position.y = kitsOnConveyor.get(i).position.y + 4;
					}
				}
				//when the first kit in the array is at the end of the conveyor
			} else {
				//If this else statement is reached then the kit has reached the end of the line.
				
				if (!sentOnce){
					//conveyorAgent.msgAnimationDone();
					endOfLineMsg = true;
					
					sentOnce = !sentOnce;
					System.out.println("	GUI : KitsReach end of line message sent!!!");
				}
			}
		}
		return endOfLineMsg;
	}
	public void incTimer() {
		
			timer++;
}
	
	//here I update the position of the kits

	
	
		  public void paintConveyor(JPanel frame, Graphics2D d, int x,int y, ImageIcon newConveyour, ImageIcon newConveyourBar, ImageIcon kitImage, ImageIcon[] partsImages,ImageIcon badParts){
				
				//If the conveyors are moving, then use timer to display image.
			  
				timer=timer%280;
				newConveyour.paintIcon(frame, d, x,y);
				if(isMoving){
					for (int i=0;i<10;i++)
					newConveyourBar.paintIcon(frame, d, x,position.y+(timer*4+i*28)%280);
					}
				else{
					for (int i=0;i<10;i++)
						newConveyourBar.paintIcon(frame, d, x, position.y+i*28);
				}
				
				
				
				//Paint all the kits in the array.
				for (int i = 0; i < kitsOnConveyor.size(); i++)
					kitsOnConveyor.get(i).paintKit(frame, d, kitsOnConveyor.get(i).position.x, kitsOnConveyor.get(i).position.y, kitImage, partsImages, badParts);
					
				
				
				
			}
		 

}
