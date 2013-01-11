package gfx;

import java.util.*;
import java.awt.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.io.Serializable;

public class GfxFeeder implements Serializable{

	//constants
	int xpos;
	int ypos;
	int myid;
	boolean feederOn;
	boolean nNorm;
	boolean feedingBadParts;
	int currentlane=0;

	//member data
	int numparts;
	int mypartid;
	int mybinid;
	ArrayList <GfxBin> binlist;
	ArrayList <GfxLane> lanelist;
	GfxFactory myGfxFactory;
	
	public GfxFeeder(GfxFactory gf, int id, int x, int y, ArrayList<GfxBin> b, ArrayList<GfxLane> l) {
		myGfxFactory = gf;
		binlist=b;
		lanelist=l;
		xpos=x;
		ypos=y;
		myid=id;
		feederOn=false;
		nNorm=false;
		feedingBadParts = false;
	}
	
	public void setNonNorm() {
		nNorm=true;
	}
	
	public void startFeeding() {
		feederOn=true;
	}
	
	public void stopFeeding() {
		feederOn=false;
	}
	
	public void feedBadParts() {
		feedingBadParts = true;
	}
	
	public boolean checkIsFeeding() {
		return feederOn;
	}
	
	public void receivePart(int partid, int binid) {
		feedingBadParts = false;
		mypartid=partid;
		mybinid=binid;
		numparts=25;
	}
	
	public int getX(){
		return xpos;
	}
	
	public int getY(){
		return ypos;
	}
	
	public int getPartID() {
		return mypartid;
	}
	
	public int getBinID() {
		return mybinid;
	}
	
	public void feedPart() {
		if(feederOn && numparts>0){
			lanelist.get(myid*2+currentlane).addPart(mypartid, feedingBadParts); 
			numparts--;
			//stop feeding bad parts when run out
			if (numparts==0)
				feedingBadParts = false;
			//feeder.msgFedPart();
			myGfxFactory.messageFlags[5 + myid] = true;
			if (numparts==5)
				//feeder.msgPartsLow();
				myGfxFactory.messageFlags[9 + myid] = true;
		}
	}
	
	public int getid() {
		return myid;
	}
	
	public int getCurrentLane() {
		return currentlane;
	}
	
	public void divert(){
		if (!nNorm) {
			if(currentlane==0)
				currentlane=1;
			else
				currentlane=0; 
		}
		else
			nNorm=false;
	}
	
	public void paintFeeder(JPanel j, Graphics2D g, ImageIcon myImage){
		myImage.paintIcon(j,g, xpos, ypos);
	}
	
	public void setPurge() {
		stopFeeding();
		binlist.get(mypartid).setStatus(); //set bin to full
	}
}

