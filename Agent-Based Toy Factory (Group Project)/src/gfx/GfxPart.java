package gfx;
//removed when integrating

import javax.swing.*;
import java.awt.Graphics2D;
import java.io.Serializable;

public class GfxPart implements Serializable{
	int xpos;
	int ypos;
	int myid;
	boolean isVisible;
	boolean isBad;
	boolean isUnstable;
	boolean isPiled;
	
	public GfxPart(int id) {
		isVisible=true;
		myid=id;
		xpos=0;
		ypos=0;
		isBad = false;
		isUnstable = false;
	}
	
	public GfxPart(int id, int xloc, int yloc) {
		myid=id;
		xpos=xloc;
		ypos=yloc;
		isBad = false;
		isUnstable = false;
	}
	
	public int getX() {
		return xpos;
	}
	
	public int getY() {
		return ypos;
	}
	
	public int getid() {
		return myid;
	}
	
	public void move(int x, int y) {
		xpos=x;
		ypos=y;
	}

	public void paintPart(JPanel j, Graphics2D g, ImageIcon myImage, ImageIcon myBadImage){
		if (isBad)
			myBadImage.paintIcon(j, g, xpos, ypos);
		else
			myImage.paintIcon(j, g, xpos, ypos);
	}
	
	public void paintPart(JPanel j, Graphics2D g, int x, int y, ImageIcon myImage, ImageIcon myBadImage) {
		if (isBad)
			myBadImage.paintIcon(j, g, x, y);
		else
			myImage.paintIcon(j, g, x, y);
	}

	public void changeIntoSomethingElse() {
		// TODO Auto-generated method stub
		if (myid < 10)
		{
			myid++;
		}
		else
		{
			myid--;
		
		}
	}

	public void makeBad() {
		// TODO Auto-generated method stub
		isBad = true;
	}
	
	public void unstablize() {
		isUnstable = true;
	}
	
	public void stablize() {
		isUnstable = false;
	}
	
	public boolean isUnstable() {
		return isUnstable;
	}

	public void screwUp() {
		isPiled = true;
	}

	public void deScrewUp() {
		isPiled = false;
	}
	
	public boolean isPiled() {
		return isPiled;
	}
}
