package myGame;

import javax.swing.ImageIcon;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;

public class Player {
	
	//descriptive variable definitions and implementations are as follows:
	
	public int xcoord,ycoord,dx,dy,boardx,boardx2,playerlife,ammunition,winCounter,playerScore;

	ArrayList<Movable> bullets;
	
	Image still;
	
	boolean isPaused;
	
	public Player(int x, int y){
		this.xcoord=x;
		this.ycoord=y;
		this.dx=1;
		this.dy=0;
		this.boardx=585;
		this.boardx2=0;
		this.playerlife=100;
		this.ammunition=50;
		this.bullets = new ArrayList<Movable>();
		this.isPaused = false;
		this.winCounter = 0;
		this.playerScore = 0;
		
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/Ship.png");
		still = i.getImage();
	}
	
	
	//player all-inclusive movement function. For the first 150 pixels, the player's xcoord increases so the player 'moves right.'  After that point, the xcoord stays constant at 150, and all other classes move left 'around' the player and simulates scrolling. The player may only move up or down from that point, which the user specifies.
	public void move(){
		this.winCounter++;
		if(xcoord<150)
		this.xcoord+=dx;
		//System.out.println(xcoord);
		
		if(this.ycoord<=0)
			this.ycoord=1;
		else if(this.ycoord>=343)
			this.ycoord=342;
		else
			this.ycoord+=dy;
		
		boardx+=dx;
		boardx2+=dx;
		
	}
	
	//bullets array stores several separate movable objects. These are fired by the user, and are created in the 'center' of the ship and shot forward. They increase their xcoord by 1 every clock, thus they seem to be going 2X faster than the player.
	public void fire(){
		if(ammunition>0){
			ammunition--;
			Movable bullet = new Movable(xcoord+20,ycoord+11);
			bullets.add(bullet);
		}
	}

	public void keyReleased(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP){
			dy=0;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){
			dy=0;
		}
	}
	
	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_UP){
			dy=-1;
		}
		if (e.getKeyCode() == KeyEvent.VK_DOWN){
			dy=1;
		}
		if (e.getKeyCode() == KeyEvent.VK_SPACE){
			fire();
		}
		if (e.getKeyCode() == KeyEvent.VK_P){
			isPaused = !isPaused;
		}
	}
	
	public Rectangle bounds(){
		return(new Rectangle(xcoord,ycoord,40,23));
		}

	
	//if the player fails to achieve the objectives, and dies early, they will be informed of their loss, and their score printed out.
	public void hit() {
		playerlife-=20;
		if (playerlife <=0){
			System.out.println("*************************");
			System.out.println("You have died. Game over.");
			System.out.println("*************************");
			returnScore();
			System.exit(0);
		}
	}

	//if the player runs over an ammo case, this function is called, and the player's ammunition is increased by a random possibility of 0-15, and then another possibility of 0-5 if added on top of that. The result is that the player may get 20 bullets, but this is only a rare occasion since the odds are weighted to be 0-15.
	public void refill() {
		int tempRefill = (int) ((Math.random()*14)+1) + (int) ((Math.random()*5)+0);
		ammunition+=tempRefill;
		System.out.println("You have collect "+tempRefill+" bullets!");
		
	}
	
	//score is calculated by taking the distance traveled, multiplying that by 1.75 (175%), and then adding the bullets remaining (salvaging) multiplied by 2 (200%)
	public void returnScore(){
		System.out.println("Final Score Summary");
		System.out.println("\tPoints for distance: "+(winCounter*1.75));
		System.out.println("\tBonus from salvaged bullets: "+(ammunition*3));
		System.out.println("\t-------------------------------");
		System.out.println("\tFinal score: " + ((winCounter*1.75)+(ammunition*3)));
	}

}
