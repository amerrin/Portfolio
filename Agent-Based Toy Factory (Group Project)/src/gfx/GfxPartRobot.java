//Graphical Part Robot
package gfx;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.*;
import javax.swing.*;

public class GfxPartRobot implements Serializable{
	ArrayList<GfxPart> gripperHand;
	//constants
	private final int nestSize = 10;
	private final int handSize = 4;
	private final int initxpos = 260;
	private final int initypos = 60;
	private final int turboEnginePower = 14;
	private final int restrictedAreaLeftTopCornerx=110;
	private final int restrictedAreaLeftTopCornery=300;
	private final int restrictedAreaRightBottomCornerx=281;
	private final int restrictedAreaRightBottomCornery=406;
	private final int speed = 1; //how many pixels robot moves per timer
	//member data
	int xpos;			//current position
	int ypos;
	int destxpos;		//destination coordinates	
	int destypos;
	boolean isWorking;
	boolean atDestination; //true when at dest coordinates, false while still moving
	public int width = 105, height = 106;

	
	public GfxPartRobot() {	
		gripperHand=new ArrayList<GfxPart> ();
		
		xpos=initxpos;
		ypos=initypos;
		destxpos=initxpos;
		destypos=initypos;
		atDestination=true;
		
	}
	
	public int getxpos() {
		return xpos;
	}
	
	public int getypos() {
		return ypos;
	}
	
	
	public boolean getIfAtDestination() {
		return atDestination;
	}
	
	public void setDestination(int x, int y) {
		destxpos = x;
		destypos = y;
	}
	
	//this function will be called by the panel's timer
	//it checks if the part robot is at it's current destination, 
	public void move() {
		//check if part robot is done moving
		//if at destination: set bool true and end function
		if (Math.abs(xpos-destxpos) < 10 && Math.abs(ypos-destypos)<10) {
			atDestination=true;
			return;
		} 
		else {
			atDestination=false;	
		}
		
		//if not done moving, move a little towards destination
		//cases: (# is destination, X is robot position)
		//   1  2  3
		//   4  X  5
		//   6  7  8
		//
		for(int i=0; i<turboEnginePower; i++) {
			if (!atDestination) {
				//case 1
				if (destxpos < xpos && destypos < ypos) {
					//xpos = xpos-speed;
					ypos = ypos-speed;
				}
				//case 2
				else if (destxpos == xpos && destypos < ypos) {
					ypos = ypos-speed;
				}
				//case 3
				else if (destxpos > xpos && destypos < ypos) {
					xpos = xpos+speed;
					//ypos = ypos-speed;
				}
				//case 4
				else if (destxpos < xpos && destypos == ypos) {
					xpos = xpos-speed;
				}
				//case 5
				else if (destxpos > xpos && destypos == ypos) {
					xpos = xpos+speed;
				}
				//case 6
				else if (destxpos < xpos && destypos > ypos) {
					//xpos = xpos-speed;
					ypos = ypos+speed;
				}
				//case 7
				else if (destxpos == xpos && destypos > ypos) {
					ypos = ypos+speed;
				}
				//case 8
				else if (destxpos > xpos && destypos > ypos) {
					xpos = xpos+speed;
					//ypos = ypos+speed;
				}
			}
		}
		//Robot changes color when he is loaded

	}
	
	public void moveTo(int x, int y) {
		xpos=x;
		ypos=y;
	}
	
	public void addPart(GfxPart p) throws Exception {
		if(isFull())
			throw new Exception("I am full!!!");
		gripperHand.add(p);
	}
	
	public GfxPart removePart() throws Exception {
		if(!isLoaded())  //isEmpty
			throw new Exception("I am poor. I don't have any parts to give out!!!");
		return gripperHand.remove(0);
	}
	
	public boolean isFull() {
		int size=gripperHand.size();
		return (size>=4);
	}
	
	public boolean isLoaded() {
		int size=gripperHand.size();
		return (size>0);
	}
	
	public void paintPartRobot(JPanel frame, Graphics2D d, int x,int y, ImageIcon loadedRobot, ImageIcon emptyRobot) {
		//paint the parts robot, loaded or empty
		if(isLoaded())
			
		loadedRobot.paintIcon(frame,d, x, y);
		else
			emptyRobot.paintIcon(frame,d, x, y);
	
	}
	
	public boolean isInRestrictedArea() {
		if( (xpos>restrictedAreaLeftTopCornerx) && (xpos<restrictedAreaRightBottomCornerx) 				&& (ypos>restrictedAreaLeftTopCornery) && (ypos>restrictedAreaRightBottomCornery) )
			return true;
		return false;
	}
	
	public void paintPartRobot(JPanel frame, Graphics2D d, ImageIcon loadedRobot, ImageIcon emptyRobot) {
		if(isLoaded())		
			loadedRobot.paintIcon(frame,d, xpos, ypos);
		else
			emptyRobot.paintIcon(frame,d, xpos, ypos);
	}
	
}
