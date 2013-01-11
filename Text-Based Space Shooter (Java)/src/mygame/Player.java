package mygame;

//all inclusive player functions
public class Player {

	protected int xcoord;
	protected int ycoord;
	protected int playerlife;
	//constructor
	Player(){
		this.xcoord=5;
		this.ycoord=15;
		playerlife=100;
	}
	//coordinate manipulation
	Player(int y, int x){
		this.xcoord=x;
		this.ycoord=y;
		playerlife=100;
	}
	
	public void setcoords(int[] coords){
		xcoord=coords[0];
		ycoord=coords[1];
	}
	
	public void setxcoord(int x){
		xcoord=x;
	}
	
	public void setycoord(int y){
		ycoord=y;
	}
	
	public int getxcoord(){
		return this.xcoord;
	}
	
	public int getycoord(){
		return this.ycoord;
	}
	
	public int getplayerlife(){
		return this.playerlife;
	}
	
	public int[] getcoords(){
		int[] coords=new int[2];
		coords[0]=xcoord;
		coords[1]=ycoord;
		return coords;
	}
	
	public int hit(){
		this.playerlife-=34;
		return playerlife;
	}
	//player move function
	//takes in user's choice, and decides where it would like to move, and then returns it to the game class
	public int[] playermove(char choice){
		int[] coords=new int[2];
		switch(choice){
		case 'D':	coords[0]=(xcoord+1);
					coords[1]=(ycoord);
					return coords;
		case 'd':	coords[0]=(xcoord+1);
					coords[1]=(ycoord);
					return coords;
		case 'S':	coords[0]=(xcoord);
					coords[1]=(ycoord+1);
					return coords;
		case 's':	coords[0]=(xcoord);
					coords[1]=(ycoord+1);
							return coords;
		case 'A':	coords[0]=(xcoord-1);
					coords[1]=(ycoord);
					return coords;
		case 'a':	coords[0]=(xcoord-1);
					coords[1]=(ycoord);
							return coords;
		case 'W':	coords[0]=(xcoord);
					coords[1]=(ycoord-1);
					return coords;
		case 'w':	coords[0]=(xcoord);
					coords[1]=(ycoord-1);
					return coords;
		default:	break;
		}
		return null;
	}

}
