package myGame;

import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import javax.swing.ImageIcon;

public class Boss extends Movable{
	
	public int shootcounter,bosshealth,resetCounter;
	ArrayList<Missile> missiles;
	public int dy;
	boolean isAlive;

	ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/boss.jpg");
	ImageIcon h = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/bosshit.jpg");

	public Boss(int x, int y) {
		super(x, y);
		still = i.getImage();
		missiles = new ArrayList<Missile>();
		shootcounter = 0;
		resetCounter = 0;
		bosshealth=2000;
		dy=-1;
		visible = true;
		isAlive = true;
	}
	
	public void move(int x){
		resetCounter++;
		if (resetCounter == 5)
			still = i.getImage();
		if(this.xcoord>530)
			this.xcoord--;
		//bosses main movement function: allows the boss to scroll from top (north) to bottom (south) to allow a variation of projectiles to be shot at the player from different positions. Also, it will increase difficult for player to hit boss.
		this.ycoord+=dy;
		if(this.ycoord<1)
			dy=1;
		if(this.ycoord>370)
			dy=-1;
		
		//missiles are spanwned every 100 clocks so that there is not an overwhelming amount of missiles at one time.
		if(this.xcoord<600){
			if ((shootcounter%100)==0){
				missiles.add(new Missile(this.xcoord,this.ycoord+16));
			}
			shootcounter++;
			
			for (int i = 0; i<missiles.size(); i++){
				missiles.get(i).move();
				if (missiles.get(i).xcoord<0)
					missiles.remove(i);
			}
		}
	}
	
	public Rectangle bounds(){
		return (new Rectangle(xcoord,ycoord,58,24));
	}
	
	//handler function created to handle collisions with boss and bullets. Boss health is decreased, and remaining life is displayed. Also, the boss changes to a brighter (in contrast) image for a few clocks to 'blink' and notify the player of his hit. If the boss runs out of life, he is destroyed, and all missiles destroyed as well.
	public void hit(){
		bosshealth-=200;
		System.out.println("Boss has " + bosshealth + " life left.");
		still = h.getImage();
		resetCounter = 0;
		if(bosshealth<=0){
			isAlive = false;
			this.xcoord=0;
			this.ycoord=0;
			System.out.println("You have defeated the evil martians!");
			for (int i =0;i<missiles.size();i++){
				missiles.get(i).xcoord = 0;
				missiles.get(i).ycoord = 0;
			}
		}
	}

}
