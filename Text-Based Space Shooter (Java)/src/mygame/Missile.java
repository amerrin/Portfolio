package mygame;
//moving "missile" class the player must avoid
public class Missile{
	
	protected int xcoord;
	protected int ycoord;
	//simple coordinate manipulation for movement
	Missile(int y, int x){
		this.xcoord=x;
		this.ycoord=y;
	}
	
	public int getxcoord(){
		return this.xcoord;
	}
	
	public int getycoord(){
		return this.ycoord;
	}
	//returns where the "missile" would like to move, always moving in the west direction
	public int[] missilemove(char choice){
		int[] coords=new int[2];
		coords[0]=xcoord-1;
		coords[1]=ycoord;
		return coords;
		}
	
	public void setcoords(int[] coords){
		this.xcoord=coords[0];
		this.ycoord=coords[1];
	}
	
}
	
