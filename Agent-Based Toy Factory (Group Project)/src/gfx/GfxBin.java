package gfx;

import java.awt.*;
import java.awt.event.*;
import java.io.Serializable;

import javax.swing.*;


public class GfxBin implements Serializable{

	int initxpos;
	int initypos;
	int xpos;
	int ypos;
	int mypartid;
	int mybinid;
	int mystatus;
	

	boolean atDestination;
	
	public GfxBin(int binid, int partid, int x, int y) {
		initxpos=x;
		xpos=x;
		
		initypos=y;
		ypos=y;
		
		mybinid=binid;
		mypartid=partid;
		mystatus=1;

		}
	
	public int getX() {
		return xpos;
	}
	
	public int getY() {
		return ypos;
	}
	
	public int getid() {
		return mybinid;
	}
	
	public int getinitX() {
		return initxpos;
	}
	
	public int getinitY() {
		return initypos;
	}
	
	public int getbinid() {
		return mybinid;
	}
	
	public int getpartid() {
		return mypartid;
	}
	
	public int getStatus() {
		return mystatus;
	}
	
	public void setStatus() {
		if (mystatus==0)
			mystatus=1;//full
		else
			mystatus=0;//empty
	}
	
	public void addPart(int partid) {
		mypartid=partid;
	}
		
	public void paintBin(JPanel j, Graphics2D g, ImageIcon myImage, ImageIcon myImage2){
		if (mystatus==1)
			myImage.paintIcon(j,g, xpos, ypos);
		else
			myImage2.paintIcon(j,g,xpos,ypos);
	}
	
	public void move(int destxpos, int destypos) {
		xpos=destxpos;
		ypos=destypos;
	}
}
