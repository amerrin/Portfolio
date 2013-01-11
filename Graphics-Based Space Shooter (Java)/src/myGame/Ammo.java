package myGame;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Ammo extends Immovable{

	public Ammo(int x, int y) {
		super(x, y);
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/ammo.gif");
		still = i.getImage();
	}
	
	public Rectangle bounds(){
		return (new Rectangle(xcoord,ycoord,20,18));
	}

}
