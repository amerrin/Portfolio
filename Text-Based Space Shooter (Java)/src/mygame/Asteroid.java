package mygame;
//immovable class simulating "asteroid" field
public class Asteroid{
	protected int xcoord;
	protected int ycoord;
	//simple coordinate manipulation functions for simple non-moving object
	Asteroid(int y,int x){
		this.xcoord=x;
		this.ycoord=y;
	}
	
	public int getxcoord(){
		return this.xcoord;
	}
	
	public int getycoord(){
		return this.ycoord;
	}
	
	public int[] move(){
		int[] coords=new int[2];
		coords[0]=this.xcoord;
		coords[1]=this.ycoord;
		return coords;
	}
}
