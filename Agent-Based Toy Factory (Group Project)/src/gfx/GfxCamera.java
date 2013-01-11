package gfx;

import java.awt.Graphics2D;
import java.io.Serializable;
import java.util.concurrent.Semaphore;

import javax.swing.*;

public class GfxCamera implements Serializable{
	int xpos;
	int ypos;
	public int height = 40, width = 40;
	int battery;	//to control GfxCamera disappearing after a certain amount of time
	ImageIcon myImage;
	public Semaphore cmr;
	
	public GfxCamera() {
		xpos=0;
		ypos=0;
		battery=18;
		}
	
	public GfxCamera(int xloc, int yloc) {
		xpos=xloc;
		ypos=yloc;
		battery=15;
		
	}
	
	public void die() {
		battery--;
	}
	
	public int getxpos() {
		return xpos;
	}
	
	public int getypos() {
		return ypos;
	}
	
	public ImageIcon getImage() {
		return myImage;
	}
	
	public void paintCamera(JPanel frame, Graphics2D d, ImageIcon myImage) {
		myImage.paintIcon(frame,d, xpos, ypos);
	}
}

