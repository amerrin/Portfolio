package myGame;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Missile extends Movable{

	public Missile(int x, int y) {
		super(x, y);
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/missile.jpg");
		still = i.getImage();
	}
	
	public void move(){
		this.xcoord+=-2;
	}

	public Rectangle bounds(){
		return(new Rectangle(xcoord,ycoord,20,4));
	}
}
