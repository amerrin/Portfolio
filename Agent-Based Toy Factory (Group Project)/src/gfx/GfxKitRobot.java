package gfx;

import java.awt.Graphics2D;
import java.io.Serializable;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class GfxKitRobot implements Serializable{
	//is being sent over the network

	
	GfxKit kitOfRobot;
	double thetaRobot;
	int x;
	int y;
	int waitingTimer;
	boolean waitingMode;
	
	public GfxKitRobot(int xLocation,int yLocation){
		this.x=xLocation;
		this.y=yLocation;
		waitingTimer=0;
		waitingMode=false;
	}
	
	public int getXPos(){return x;}
	public int getYPos(){return y;}
	public double getTheta(){return thetaRobot;}
	
	
	
	public void getKit(GfxKit kit){
		kitOfRobot = kit;
	}
	
	public GfxKit removeKit(){
		GfxKit kitRemoved = kitOfRobot;
		kitOfRobot = null;
		return kitRemoved;
	}
	
	public boolean hasAKit() {
		if (kitOfRobot!=null){
			return true;
		} else {
			return false;
		}
	}
	
	
	public void paintKitStationRobot(JPanel j, Graphics2D g2, ImageIcon kitRobotImage, ImageIcon kitImage, ImageIcon [] partsImages, ImageIcon badPartImage) {
		Graphics2D g = (Graphics2D)g2.create();
		g.translate(x, y);
		if (hasAKit()) {
			
	    	g.rotate(thetaRobot,kitRobotImage.getIconWidth()/5,kitRobotImage.getIconHeight()/2);
	    	kitOfRobot.paintKit(j, g, kitRobotImage.getIconWidth(), y-200, kitImage, partsImages, badPartImage);
	    	
	    	kitRobotImage.paintIcon(j,g, 0, 0);
		} else {
			
	    	g.rotate(thetaRobot,kitRobotImage.getIconWidth()/5,kitRobotImage.getIconHeight()/2);
	    	//kitOfRobot.paintKit(j, g2, x+153, y-12);
	    	kitRobotImage.paintIcon(j,g, 0, 0);
		}
	}
	
	
	
	public boolean hasNoKit() {
		if (this.kitOfRobot == null)
			return true;
		return false;
	}
	
	public void incTheta(){
		thetaRobot+=0.1;
	}
	public void resetTheta(){
		thetaRobot=0.0;
	}
	
	
	//method for rotation of actual graphics
	public boolean rotate(double theta, double initialPosition, double finalPosition) {
		thetaRobot=theta;
		if ((theta>=initialPosition)&&(theta<=finalPosition)){
			//this.paintKitRobot(j, g2, 0, 0, theta,gg2);
			return false;
		} else {
			return true;
		}
	
	}

	public boolean notInWaitingMode(int i) {
		waitingTimer++;
		if (waitingTimer>i)
			return true;
		else
			return false;
		
	}

	

	public void waitReset() {
		waitingTimer=0;
		waitingMode=false;
		
	}
	
}
