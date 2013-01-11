package myGame;

import javax.swing.*;
import javax.swing.Timer;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Game extends JPanel{
	
	public Image bckgnd;
	Player spaceship;
	ArrayList<Immovable> asteroids;
	ArrayList<Ammo> ammoRefills;
	Boss boss;	
	
	class MyActionListener implements ActionListener{
		public void actionPerformed(ActionEvent e){
			performTimerTask();
		}
	}
	
	private class KeyActionListener implements KeyListener{
		//key actions are logged, and sent to the player class to be handled.  keyPressed, and keyReleased functions are defined within the Player.java file.
		public void keyReleased(KeyEvent e){
			spaceship.keyReleased(e);
			//boss.keyReleased(e);
		}
		public void keyPressed(KeyEvent e){
			spaceship.keyPressed(e);
		}
		public void keyTyped(KeyEvent e) {
		}
	}
	//takes the place of variable init(), and thus the variables are initialized within the defined constructor.
	public Game(){
		spaceship = new Player(1,180);
		asteroids = new ArrayList<Immovable>();
		ammoRefills = new ArrayList<Ammo>();
		boss= new Boss(2500,175);

		//asteroids are randomly generated so that the player has a 'new' gaming experience every time. Since the active playing board is 7000 long, we iterate over the entire length, placing a single asteroid per column. 
		for(int i=60; i<7000; i+=40){
			Immovable ast = new Immovable(i,(int)(Math.random()*390));
			asteroids.add(ast);
		}
			//the next three for() statements create the 'end' of the game.  They help section off a portion of the screen where the player is designated to 'fly' so as to signal a victory by using asteroids.
			for(int j=0; j<160; j++){
				Immovable ast = new Immovable(7001,j);
				asteroids.add(ast);
			}
			for(int j=240; j<400; j++){
				Immovable ast = new Immovable(7001,j);
				asteroids.add(ast);
			}
		for(int j=7001; j<7600; j++){
			Immovable ast = new Immovable(j,160);
			Immovable ast2 = new Immovable(j,240);
			asteroids.add(ast);
			asteroids.add(ast2);
			
		}
		//similar to the random generation of asteroids, bullet boxes are generated every 400 spaces, so that the player may collect them to increase their ammunition value. Each game, the cases will be randomly scattered to provide a level of difficulty.
		for(int i=60;i<7600; i+=400){
			Ammo refill = new Ammo(i,(int)(Math.random()*390));
			ammoRefills.add(refill);
		}
		//Image variable bckgnd is defined as a 'space' image. This image is approximately 800 pixels long. We not this number as we need to 'overlap' or draw the background again after 800 pixels. Refer to the paintComonent function to see this implemented.
		ImageIcon i = new ImageIcon("/Users/anthonymerrin/Documents/workspace/hw2/Space.gif");
		bckgnd = i.getImage();
		
		addKeyListener(new KeyActionListener());
		setFocusable(true);
		
		Timer t = new Timer(0,null);
		t.setDelay(10);
		t.addActionListener(new MyActionListener());
		t.start();
		
	}
	
	public void performTimerTask(){
	if(!spaceship.isPaused){
		//this case is flagged if and only if the player has won the game. There are two requirements to win the game: reaching the end (7250 spaces) and killing the boss; if these requirements are met, the player is informed of his victory and his score is displayed.
		if(spaceship.winCounter>7250 && boss.isAlive == false){
			System.out.println("******************************");
			System.out.println("Congratulations, you have won!");
			System.out.println("******************************");
			spaceship.returnScore();
			System.exit(0);
		}
		//if the player manages to reach the end (7250 spaces) but has not managed to defeat the boss, then the player is informed of his loss, and his score is displayed.
		else if(spaceship.winCounter>7250 && boss.isAlive == true){
			System.out.println("****************************************************************");
			System.out.println("The Martians have escaped! You lose! Please try again next time!");
			System.out.println("****************************************************************");
			spaceship.returnScore();
			System.exit(0);
		}
		
		//Various Movements Function Calls For All Class Objects Below:
		
		spaceship.move();
		for(int i=0; i<asteroids.size(); i++){
			if(spaceship.xcoord>149)
				asteroids.get(i).move();
			if( collision(i) ){
				spaceship.hit();
				System.out.println("A collision has been detected! " + spaceship.playerlife +" health remaining.");
				asteroids.remove(i);
			}
		}
		for(int i=0; i<ammoRefills.size(); i++){
			if(spaceship.xcoord>149)
				ammoRefills.get(i).move();
			if( playerCollectAmmo(i)){
				spaceship.refill();
				ammoRefills.remove(i);
			}
				
		}
		for(int i=0; i<boss.missiles.size(); i++){
			boolean flag = collide(i);
			if(flag == false){
				if(explode(i)){
					System.out.println("The enemy has hit you!");
					spaceship.hit();
					boss.missiles.remove(i);
				}
			}
		}
		for(int i=0; i<spaceship.bullets.size(); i++){
			if(spaceship.bullets.get(i).visible == true){
				spaceship.bullets.get(i).move();
				boolean flag = shot(i);
				if(flag == false){
					if(bulletHitBoss(i)){
						boss.hit();
						spaceship.bullets.remove(i);
					}
				}
			}
			else
				spaceship.bullets.remove(i);
		}
		if(boss.isAlive)
			boss.move(spaceship.ycoord);
	}
	repaint();
	}
	
	public void paintComponent(Graphics g){
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D)g;

		//recursions used to soft reset xcoordinates used to "scroll" background left.  They must be reset every 1600 pixels, so as to maintain the illusion that 2 X 800 pixel images simulate scrolling background.
		if(spaceship.boardx%1600==0)
			spaceship.boardx=0;
		if(spaceship.boardx2/1600==1)
			spaceship.boardx2=0;
		
		//painting functions used to simulate the scrolling effect of the background.  They are programmed to wait for the player to reach pixel 200 before beginning to scroll.  Once the player does, they intermittently take turns "scrolling" across the frame.
		if(spaceship.boardx2>0)
			g2d.drawImage(bckgnd, 600-spaceship.boardx2,0,null);
		if (spaceship.xcoord>149){
		g2d.drawImage(bckgnd, 600-spaceship.boardx,0,null);
		}
		else{
			g2d.drawImage(bckgnd, -1, 0, null);
			spaceship.boardx=600;
			spaceship.boardx2=-200;
		}
		
		//iteration used to access array of immovable "asteroid" objects.  The asteroids are printed across the screen so the player may avoid collisions with them.  If the asteroid goes off the map (left edge = 0), they are "destroyed" and removed from the memory.
		if(!asteroids.isEmpty()){	
			for(int i=0; i<asteroids.size(); i++){
				if(asteroids.get(i).xcoord==0)
					asteroids.remove(i);
				g2d.drawImage(asteroids.get(i).still,asteroids.get(i).xcoord,asteroids.get(i).ycoord,null);
			}
		}
		if(!ammoRefills.isEmpty()){
			for(int i=0; i<ammoRefills.size(); i++){
				if(ammoRefills.get(i).xcoord==0){
					ammoRefills.remove(i);
				}
				g2d.drawImage(ammoRefills.get(i).still,ammoRefills.get(i).xcoord,ammoRefills.get(i).ycoord,null);
			}
		}
			
		//spaceship is drawn last, so that everything around may move first; however, past the 200th pixel, the player will stay remotely "cenetered" within the screen, thus spaceship.xcoord acts as a constant integer of 200
		g2d.drawImage(spaceship.still,spaceship.xcoord,spaceship.ycoord,null);

		for(int i=0; i<spaceship.bullets.size(); i++){
			g2d.drawImage(spaceship.bullets.get(i).still,spaceship.bullets.get(i).xcoord,spaceship.bullets.get(i).ycoord,null);
		}
		if(boss.isAlive)
			g2d.drawImage(boss.still,boss.xcoord,boss.ycoord,null);
		
		for (int i=0; i<boss.missiles.size(); i++){
			g2d.drawImage(boss.missiles.get(i).still,boss.missiles.get(i).xcoord,boss.missiles.get(i).ycoord,null);
		}
		
		//final drawStrings are used to display the players life and ammunition, as well as print current status of "pause" and inform the player of the pause function 'P'
		g2d.setColor(Color.WHITE);
		g2d.drawString("Player life: "+Integer.toString(spaceship.playerlife),5,15);
		g2d.drawString("Ammunition: "+Integer.toString(spaceship.ammunition),5,30);
		g2d.drawString("Paused: " + spaceship.isPaused, 510, 15);
		g2d.drawString("Press 'P' to pause",490,30);
	}
	//Various collision detection functions and their descriptions below:
	//many of the collisions are detected utilizing the Rectangle class' interects() function. 
	
	//collision detection for spaceship against asteroids
	public boolean collision(int i){
		Rectangle rect1 = spaceship.bounds();
		Rectangle rect2 = asteroids.get(i).bounds();
		
		return rect1.intersects(rect2);
	}
	
	//collision detection for spaceship against bullets
	public boolean shot(int i){
		Rectangle rect1 = spaceship.bullets.get(i).bounds();
		for (int j=0; j<asteroids.size(); j++){
			Rectangle rect2 = asteroids.get(j).bounds();
			if(rect1.intersects(rect2)){
				spaceship.bullets.remove(i);
				asteroids.remove(j);
				return true;
			}
		}
		return false;
	}
	
	//collision detection for spaceship against boss' missiles
	public boolean explode(int i){
		Rectangle rect1 = spaceship.bounds();
		Rectangle rect2 = boss.missiles.get(i).bounds();
		
		return(rect1.intersects(rect2));
	}
	
	//collision detection for boss' missiles against asteroids.
	public boolean collide(int i){
		Rectangle rect1 = boss.missiles.get(i).bounds();
		for (int j=0; j<asteroids.size(); j++){
			Rectangle rect2 = asteroids.get(j).bounds();
			if(rect1.intersects(rect2)){
				asteroids.remove(j);
				boss.missiles.remove(i);
				return true;
			}
		}
		return false;
	}
	
	//collision detection for spaceship's bullets against boss
	public boolean bulletHitBoss(int i){
		Rectangle rect1 = spaceship.bullets.get(i).bounds();
		Rectangle rect2 = boss.bounds();
		
		return rect1.intersects(rect2);
	}
	
	//collision detection for ammunition boxes and player.
	public boolean playerCollectAmmo(int i){
		Rectangle rect1 = spaceship.bounds();
		Rectangle rect2 = ammoRefills.get(i).bounds();
		
		return rect1.intersects(rect2);
	}

}
