package gfx;

import java.util.*;
import javax.swing.*;
import java.awt.Graphics2D;
import java.io.Serializable;

public class GfxLane implements Serializable {
	
	//constants
	private final int xupdate = 5;	
	int imageBar;//will be used for vibration and will define the amplitude
	int frequency;
	int xpos;
	int ypos;
	int myid;
	int tempy;
	boolean removepart;
	boolean stackpart;
	boolean jampart;
	boolean jammed;
	boolean unjampart;
	int jamedVar; 
	
	boolean status;
	boolean listflipped;
	GfxPart mypart;
	
	ArrayList<GfxFeeder> feederlist;
	ArrayList<GfxPart>mypartslist;
	ArrayList<GfxPart>stackedparts;
	ArrayList<GfxPart>jammedparts;
	GfxNest[] nestslist;
	
	int timer; //is used to move bars on the lane to simulate the smooth motion
	
	boolean nestfull;
	
	public GfxLane(int id, int x, int y, ArrayList<GfxFeeder> f, GfxNest[] n) {
		timer=0;
		jamedVar=0;
		feederlist = f;
		nestslist = n;
		xpos=x;
		ypos=y;
		myid=id;
		jammed=false;
		unjampart=false;
		jampart=false;
		listflipped=false;
		status=false; //default off
		mypartslist=new ArrayList<GfxPart>();
		stackedparts=new ArrayList<GfxPart>();
		jammedparts=new ArrayList<GfxPart>();
		//below we assigning 2 images of the sliding bars of the lane
		
		nestfull = false;
		removepart=false;
		stackpart=false;
		imageBar=0;
		frequency=10;
	}
	
	public void addPart(int partid, boolean bad) {
		mypart=new GfxPart(partid);
		if (bad)
			mypart.makeBad();
		mypart.move(912,ypos+10);
		mypartslist.add(mypart);
	}
	
	public void upAmplitude() {
		frequency=5;
jamedVar=2;
		if(jammed)
			feederlist.get(myid/2).startFeeding();
		jammed=false;
	}
	
	public void downAmplitude() {
		frequency=10;
jamedVar=1;
	}
	
	public void jamLane() {
		jammed=true;
	}
	
	public void moveLane() {
		if (status==true) {
			for (GfxPart p : mypartslist) {
				p.move(p.getX()- xupdate,p.getY());
				if (!jammed) {
					if (jammedparts.size()==0 && p.getX()<612+(20*stackedparts.size()/2)) {  //if part rolls off the left end of the lane
						if (!nestfull){// && stackedparts.size()==0) {
							removepart=true;
						}
						else if (nestfull) {
							if (stackedparts.size()%2==0)
								tempy=-10;
							else
								tempy=10;
							if (listflipped) {
								tempy*=-1;
								listflipped=false;
							}
							p.move(p.getX(),p.getY()+tempy);
							feederlist.get(myid/2).stopFeeding();
							stackpart=true;
						}
					/*else if (!nestfull && stackedparts.size()>0) {
						try {
							nestslist[myid].addPart(stackedparts.remove(stackedparts.size()-1));
						} catch(Exception e) {
							System.out.println("You dumb shit, the nest you're gonna add to is already full. ");
						}
					}*/
					}
				}
				else if (jammed) {
					if (p.getX()<762+(20*jammedparts.size())) {
						jampart=true;
						feederlist.get(myid/2).stopFeeding();
					}
				}
			} //end for loop
			if(!jammed&&!jammedparts.isEmpty())
			{
				unjampart=true;
			}
			if (removepart==true) {
				if (stackedparts.size()>0) {
					try {
						nestslist[myid].addPart(stackedparts.get(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
					stackedparts.remove(0);
				}
				else if (mypartslist.size()>0) {
					try {
						nestslist[myid].addPart(mypartslist.get(0));
					} catch (Exception e) {
						e.printStackTrace();
					}
					mypartslist.remove(0);
				}
				removepart=false;
			}
			else if (stackpart==true) {
				stackedparts.add(mypartslist.get(0));
				stackpart=false;
				mypartslist.remove(0);
			}
			else if (jampart==true) {
				jammedparts.add(mypartslist.get(0));
				jampart=false;
				mypartslist.remove(0);
			}
			else if (unjampart==true) {
				mypartslist.add(jammedparts.get(0));
				unjampart=false;
				jammedparts.remove(0);
			}
			if (stackedparts.size()>0 && !nestfull) {
				try {
					nestslist[myid].addPart(stackedparts.get(0));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				stackedparts.remove(0);
				for (GfxPart sp : stackedparts) {
					if (stackedparts.size()%2==0)
						tempy=20;
					else
						tempy=0;
					sp.move(sp.getX()-10,sp.getY());
					listflipped=true;
				}
			}
		}
	}
	
	public boolean getIsOn() {
		return status;
	}
	
	public int getid() {
		return myid;
	}
	
	public void turnLaneOn() {
		status=true;
	}
	
	public void turnLaneOff() {
		status=false;
	}
	
	public void setNestFull(boolean nf) {
		nestfull = nf;
	}
	
	public boolean isNestFull() {
		return nestfull;
	}
	
	public void incTimer() {
		if (status==true)
			timer++;
	}
	
	public ArrayList<GfxPart> getMyPartsList()
	{
		return mypartslist; //.add(e)
		
	}
	public ArrayList<GfxPart> getStackedParts()
	{
		return stackedparts; //.add(e)
		
	}
	public ArrayList<GfxPart> getJammedParts()
	{
		return jammedparts; //.add(e)
		
	}
	
	public void paintLane(JPanel j, Graphics2D g, ImageIcon newLane, ImageIcon [] bar, ImageIcon[] partImages, ImageIcon badPartImage){
		timer=timer%newLane.getIconWidth();
		imageBar=timer%frequency;
		if (imageBar>=0&&imageBar<frequency/2)
			imageBar=0;
		else
			imageBar=jamedVar;
		
		newLane.paintIcon(j,g, xpos, ypos);//paints the the lane
		for (int i=0;i<15;i++)// paints 15 bars on the lane separated by 20 px. they moved by timer amoount
			bar[imageBar].paintIcon(j, g, xpos+290-(i*20+timer*5)%newLane.getIconWidth(), ypos);
		
		for (GfxPart p : mypartslist) {
			p.paintPart(j,g, partImages[p.getid()], badPartImage);
		}
		for (GfxPart sp : stackedparts) {
			sp.paintPart(j,g, partImages[sp.getid()], badPartImage);
		}
		for (GfxPart jp : jammedparts) {
			jp.paintPart(j, g, partImages[jp.getid()], badPartImage);
		}
	}
	
	public GfxPart removeOneStackedPart() {
		if(stackedparts.size()==0)
			return null;
		else
			return stackedparts.remove(0);
	}
	
	public ArrayList<GfxPart> dumpStackedParts() {
		ArrayList<GfxPart> list = stackedparts;
		stackedparts.clear();
		return list;
	}

	public void splitPartsIntoTwoTypes() {
		//changes half of the parts into a different type
		int count = 1;
		
		for (GfxPart part: jammedparts)
		{
			if (count % 2 == 0)
			continue;
			else
				part.changeIntoSomethingElse();
		}
		for (GfxPart part: stackedparts){
			if (count % 2 == 0)
				continue;
				else
					part.changeIntoSomethingElse();
		}
		for (GfxPart part: mypartslist){
			if (count % 2 == 0)
				continue;
				else
					part.changeIntoSomethingElse();
		}
	}

	public void makeAllPartsBad() {
		for (GfxPart part: mypartslist)
			part.makeBad();
		for (GfxPart part: stackedparts)
			part.makeBad();
		for (GfxPart part: jammedparts)
			part.makeBad();
	}
	
	public void removeAllParts() {
		mypartslist.clear();
		stackedparts.clear();
		jammedparts.clear();
	}
}
