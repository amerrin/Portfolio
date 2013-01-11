package gfx;

import java.util.*;
import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.*;

public class GfxNest implements Serializable{
	private final int nestSize = 10;
	private final int partSize = 21;
	
	int xpos;
	int ypos;
	int myid;		//myid is 0-7.
	ArrayList<GfxPart> myParts;
	
	boolean isUnstable;
	boolean partsPiledUp;
	
	public GfxNest() {
		xpos=0;
		ypos=0;
		myid=0;
		myParts=new ArrayList<GfxPart> ();
		//prepare to handle exceptions
		isUnstable=false;
		partsPiledUp=false;
	}
	
	public GfxNest(int id, int xloc, int yloc) {
		xpos=xloc;
		ypos=yloc;
		myid=id;
		myParts=new ArrayList<GfxPart> ();
		//prepare to handle exceptions
		isUnstable=false;
		partsPiledUp=false;
	}
	
	public GfxNest(int id, ArrayList<GfxPart> p, int xloc, int yloc) {
		xpos=xloc;
		ypos=yloc;
		myid=id;
		myParts=p;
		//prepare to handle exceptions
		isUnstable=false;
		partsPiledUp=false;
	}
	
	public void move(int x, int y) {
		xpos = x;
		ypos = y;
	}
	
	//after we set up an Exception Package, modify here
	public void addPart(GfxPart p) throws Exception {
		if(myParts.size() < nestSize)
			myParts.add(p);		//I have another part now
		else
			throw new Exception("I can't eat anymore!!!");
	}
	
	public int getPartNumber() {
		return myParts.size();
	}
	
	public GfxPart removePart(GfxPart p) throws Exception {
		if(myParts.size()==0)
			throw new Exception("I am empty!!!");
		myParts.remove(p);
		return p;
	}
	
	public GfxPart removePart() throws Exception {
		if(myParts.size()==0)
			throw new Exception("I am empty!!!");
		return myParts.remove(0);
	}
	
	public GfxPart removePart(int slot) throws Exception {
		if(myParts.size()==0)
			throw new Exception("I am empty!!!");
		try {
			return myParts.remove(slot);
		} catch (Exception e) {
			throw new Exception("I can't find the part you want !!!");
		}
	}
	
	public void fill(GfxPart p) {
		while(myParts.size() < nestSize) {
			myParts.add(p);
		}		
	}
	
	public boolean isFull() {
		if(myParts.size() >= nestSize)
			return true;
		return false;
	}
	
	public void dump() {
		myParts.clear();
		isUnstable=false;
		partsPiledUp=false;
	}
	
	public int getxpos() {
		return xpos;
	}
	
	public int getypos() {
		return ypos;
	}
	
	public int getid() {
		return myid;
	}
	
	public ArrayList<GfxPart> getParts() {
		return myParts;
	}
	
	
	
	public void paintNest(JPanel frame, Graphics2D d, ImageIcon myImage, ImageIcon[] partImages, ImageIcon badPartImage) {
		myImage.paintIcon(frame,d, xpos, ypos);
		//TODO paint parts inside the kit
		for(int i=0; i<myParts.size(); i++) {
			GfxPart thisPart = myParts.get(i);
			if(i<5) {		//part 0-4
				if(myParts.get(i).isUnstable) {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (4-i) + 1 + (int)((Math.random()-0.5)*18), 
							ypos + (partSize+1) + 3 + (int)((Math.random()-0.5)*18), 
							partImages[thisPart.getid()], badPartImage);
				}
				else if(myParts.get(i).isPiled) {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (4-i) + 1 - 10, 
							ypos + (partSize+1) + 3 - 10, 
							partImages[thisPart.getid()], badPartImage);
				}
				else {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (4-i) + 1, 
							ypos + (partSize+1) + 3, partImages[thisPart.getid()], badPartImage);
				}
				//thisPart.paintPart(frame, d, xpos + (partSize+1) * (4-i) + 1, ypos + (partSize+1) + 3, partImages[thisPart.getid()], badPartImage );
			} else {
				if(myParts.get(i).isUnstable) {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (9-i) + 1 + (int)((Math.random()-0.5)*18),
							ypos + 3 + (int)((Math.random()-0.5)*18), 
							partImages[thisPart.getid()], badPartImage);
				} 
				else if(myParts.get(i).isPiled) {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (9-i) + 1 - 10,
							ypos + 3 - 10, 
							partImages[thisPart.getid()], badPartImage);
				}
				else {
					myParts.get(i).paintPart(frame, d, xpos + (partSize+1) * (9-i) + 1, ypos + 3, 
							partImages[thisPart.getid()], badPartImage);
				}
				//thisPart.paintPart(frame, d, xpos + (partSize+1) * (9-i) + 1, ypos + 3, partImages[thisPart.getid()], badPartImage);
			}
				
		}
	}

	public void removeAllParts() {
		// TODO Auto-generated method stub
		myParts.clear();
	}

	public void makeAllPartsBad() {
		// TODO Auto-generated method stub
		for (GfxPart part: myParts)
		{
			part.makeBad();
		}
	}
	
	public boolean isUnstable() {
		return isUnstable;
	}
	
	public void unstablize() {
		isUnstable=true;
		for(GfxPart p: myParts) {
			p.unstablize();
		}
	}
	
	public void stablize() {
		isUnstable=false;
		for(GfxPart p: myParts) {
			p.stablize();
		}
	}
	
	public boolean partsPiledUp() {
		return partsPiledUp;
	}
	
	public void setPartsPiledUp(boolean p) {
		partsPiledUp=p;
	}
	
	public void screwUp() {
		partsPiledUp=true;
		try{
			myParts.get(1).screwUp();
			myParts.get(3).screwUp();
			myParts.get(6).screwUp();
			myParts.get(8).screwUp();
		} catch (Exception e) {
			//do nothing
		}
	}
	
	public void deScrewUp() {
		partsPiledUp=false;
		try {
			myParts.get(1).deScrewUp();
			myParts.get(3).deScrewUp();
			myParts.get(6).deScrewUp();
			myParts.get(8).deScrewUp();
		} catch (Exception e) {
			//do nothing
		}
	}
}
