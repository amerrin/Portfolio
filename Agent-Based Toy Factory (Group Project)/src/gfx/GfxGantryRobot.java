package gfx;


import java.awt.*;
import java.util.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.io.Serializable;

public class GfxGantryRobot implements Serializable{
	//constants
	private final int initxpos = 900;
	private final int initypos = 520;
	private final int speed = 10;

	//member data
	int mybinid;
	int xpos;	//current position
	int ypos;
	int destxpos;	//destination coordinates
	int destypos;
	int tempbinindex;
	
	boolean atDestination;
	boolean binPickedUp;
	
	
	ArrayList<GfxBin> binlist;
	ArrayList<GfxFeeder> feederlist;
	
	public GfxGantryRobot(ArrayList<GfxBin> b, ArrayList<GfxFeeder> f) {
		binlist=b;
		feederlist=f;
		
		xpos=initxpos;
		ypos=initypos;
		
		destxpos=-1;
		destypos=-1;
		
		binPickedUp=false;
		atDestination=false;
		
		mybinid=-1;
	}
	
	public boolean getIfAtDestination() {
		return atDestination;
	}
	
	public void getBin(int binid) {
		mybinid=binid;
		destxpos=binlist.get(binid).getX();
		destypos=binlist.get(binid).getY();
	}
	
	public void setDestination(int x, int y) {
		destxpos = x;
		destypos = y;
	}
	
	public void checkAtDestination(int destxpos, int destypos){
		if (xpos == destxpos && ypos == destypos) {
			atDestination=true;
		} 
		else {
			atDestination=false;
		}
	}
	
	public int getXDestination(){
		return destxpos;
	}
	
	public void move() {
		if (xpos == destxpos && ypos == destypos) {
			atDestination=true;
			return;
		} 
		else {
			atDestination=false;	
		}
	
		if (!atDestination) {
			//case 1
			if (destxpos < xpos && destypos < ypos) {
				xpos = xpos-speed;
				ypos = ypos-speed;
			}
			//case 2
			else if (destxpos == xpos && destypos < ypos) {
				ypos = ypos-speed;
			}
			//case 3
			else if (destxpos > xpos && destypos < ypos) {
				xpos = xpos+speed;
				ypos = ypos-speed;
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
				xpos = xpos-speed;
				ypos = ypos+speed;
			}
			//case 7
			else if (destxpos == xpos && destypos > ypos) {
				ypos = ypos+speed;
			}
			//case 8
			else if (destxpos > xpos && destypos > ypos) {
				xpos = xpos+speed;
				ypos = ypos+speed;
			}
			if (binPickedUp==true) {
				tempbinindex=0;
				for (GfxBin b : binlist) {
					if (b.getid()==mybinid) {
						break;
					}
					else 
						tempbinindex++;
				}
				binlist.get(tempbinindex).move(xpos,ypos); //need to fix index
			}
		}
	}
	
	public void serviceFeeder(int feederid, int binid) {
		pickUpBin(binid);
		
		if (atDestination==true) {
			moveBinToFeeder(feederid);
		}
	
		if (atDestination==true) {
			binPickedUp=false;
			
			feederlist.get(feederid).receivePart(-1, binid); //dump parts in feeder
			binlist.get(binid).setStatus(); //set bin to empty
		}
	}
	
	public void pickedUpBin(int binid) {
		binPickedUp=true;
		mybinid=binid;
	}
	
	public void droppedBin(int binid) {
		binPickedUp=false;
		mybinid=binid;
	}
	
	public void pickUpBin(int binid) {
		mybinid=binid;
		setDestination(binlist.get(mybinid).getX(),binlist.get(mybinid).getY()); //set bin as destination
	}
	
	public void moveBinToFeeder(int feederid) {
		int tempx=feederlist.get(feederid).getX(); 
		int tempy=feederlist.get(feederid).getY();
		
		binPickedUp=true;
		setDestination(tempx+40,tempy+20);  //set behind feeder as destination
	}
	
	public void movePurgedBin(int binid) {
		mybinid=binid;
		setDestination(binlist.get(binid).getX(),binlist.get(binid).getY()); //set purged bin as destination
		
		//check if arrived at purged bin
		if (atDestination==true) {
			binPickedUp=true;
			setDestination(binlist.get(binid).getinitX(),binlist.get(binid).getinitY()); //set bin origin as destination
		}
		if (atDestination==true) {
			binPickedUp=false;
		}
	}
	
	public void paintGantry(JPanel j, Graphics2D g, ImageIcon myImage){
		myImage.paintIcon(j,g, xpos, ypos);
	}
}
