package myGame;

import java.awt.*;

import javax.swing.ImageIcon;

public class Immovable{
	int xcoord, ycoord;
	Image still;
	
	public Immovable(int x, int y) {
		this.xcoord=x;
		this.ycoord=y;
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/Asteroid.png");
		still = i.getImage();
	}
	
	//the move function will always decrease the xcoord by one because the background is moving in relativity to the player, thus simulating scrolling.
	public void move(){
		this.xcoord--;
		//System.out.println("ast" + " " + xcoord);
	}
	
	public Rectangle bounds(){
		return (new Rectangle(xcoord,ycoord,20,20));
	}

}
