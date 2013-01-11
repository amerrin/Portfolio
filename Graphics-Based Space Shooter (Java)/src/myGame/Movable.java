package myGame;

import java.awt.Rectangle;

import javax.swing.ImageIcon;

public class Movable extends Immovable {
	
	boolean visible = true;
	
	//this class extends the asteroid class, only has the bullets move in the opposite direction, and twice as fast. Bullets travel to the right, and disappear (deleted) once they leave the right edge of the screen.
	public Movable(int x, int y) {
		super(x, y);
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/bullet.png");
		still = i.getImage();
	}
	
	public void move(){
		this.xcoord+=2;
		if(this.xcoord>600)
			visible = false;
	}
	
	public Rectangle bounds(){
		return (new Rectangle(xcoord,ycoord,10,4));
	}

}
