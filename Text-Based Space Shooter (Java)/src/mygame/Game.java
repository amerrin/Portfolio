package mygame;

import java.util.ArrayList;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class Game {

	/**
	 * @param args
	 * @throws FileNotFoundException 
	 */
	//main class where game will take place
	public static void main(String[] args) throws FileNotFoundException {
		//input scanner to accept user input
		Scanner input = new Scanner(System.in);
		//user choice variable
		char choice;
		String inputbuffer;
		//implementation of game "board" with dimensions 22wX100l
		char[][] board;
		board=new char[22][102];
		//creation of 2D array to simulate the "board"
		//initializing border of "asteroids"
		for(int i=0;i<22;i++){
			if(i==0 || i==21){
				for(int j=0;j<102;j++){
					board[i][j]='@';
				}
			}
			else{
				board[i][0]='@';
				for(int j=1;j<101;j++){
					board[i][j]=' ';
				}
				if(i!=8&&i!=9&&i!=10&&i!=11&&i!=12)
				board[i][101]='@';
			}
		}
		//player implementation
		Player spaceship = new Player(11,1);
		board[11][1]='P';
		//movable and immovable object lists
		ArrayList<Missile> missiles=new ArrayList<Missile>();
		ArrayList<Asteroid> field=new ArrayList<Asteroid>();
		Missile buffermis;
		Application frame=new Application();
		
		//randomly generated asteroids (1 per row)
		for(int i=1;i<101;i++){
			Asteroid ast=new Asteroid((int) (0+Math.random()*21),i);
			field.add(ast);
			board[ast.getycoord()][ast.getxcoord()]='@';
		}
		
		//time to spawn missiles from right edge of screen
		int missiletimer=0,terminate=0,life,buffership[];

		String username=frame.signin();
	
		//While statement to simulate a menu for the user
		//menu displays player options, and accepts user input to move the player through the game
		do{
			System.out.println();
			System.out.println("Player's Life: " + spaceship.getplayerlife());
			System.out.println("Clock: " + missiletimer);
			
			boardprint(board,spaceship);
			//randomly generates missiles each clock tick
				if(spaceship.getxcoord()+20<=100){
				Missile mis=new Missile((int) (1+Math.random()*20),spaceship.getxcoord()+20);
				missiles.add(mis);
				board[mis.getycoord()][mis.getxcoord()]='<';
				}
			//choice menu
			System.out.println("Please choose from the following options:");
			System.out.println("\tD:Move Spaceship Forward");
			System.out.println("\tW:Move Spaceship Up");
			System.out.println("\tS:Move Spaceship Down");
			System.out.println("\tQ:Quit");
			System.out.print("Please enter your choice: ");
			inputbuffer=input.next();
			choice=inputbuffer.charAt(0);
			System.out.println();
			//Game logic is nested in a switch statement
			//it will handle users choice, and ask all classes where they wish to be, it will then decide based on the rules where the object will actually move
			switch(choice){
			//terminate program case
			case 'Q':	
			case'q':	terminate=1;
						break;
			//game logic for missiles and player class
			case 'S':	case 's':	case 'W':	case 'w':	case 'D':
			case 'd':	buffership=spaceship.playermove(choice);
						if(board[buffership[1]][buffership[0]]=='@' && buffership[0]<101){
						}
						else if(board[buffership[1]][buffership[0]]=='<'){
							for(int i=0;i<missiles.size();i++){
								Missile temp=missiles.get(i);
								if(temp.getycoord()==buffership[1] && temp.getxcoord()==buffership[0]){
									board[temp.getycoord()][temp.getxcoord()]=' ';
									missiles.remove(i);
								}
							}
							life=spaceship.hit();
							System.out.println("****A missile has hit you for [34] damage!****");
							if(life<=0){
								System.out.println("Your ship has been destroyed!");
								return;
							}
							
						}
						else if(buffership[0]>=101){
							if(board[buffership[1]][buffership[0]]!='@' ){
								board[spaceship.getycoord()][spaceship.getxcoord()]=' ';
								spaceship.setcoords(buffership);
								board[spaceship.getycoord()][spaceship.getxcoord()]='P';
								boardprint(board,spaceship);
								System.out.println("Congratulations! You have won the game!");
								return;
							}
						}
						else{
							board[spaceship.getycoord()][spaceship.getxcoord()]=' ';
							spaceship.setcoords(buffership);
							board[spaceship.getycoord()][spaceship.getxcoord()]='P';
						}
						for(int i=0;i<missiles.size();i++){
							Missile temp=missiles.get(i);
							int[] temp2=temp.missilemove(choice);
							if (board[temp2[1]][temp2[0]]=='@'){
								board[temp.getycoord()][temp.getxcoord()]=' ';
								missiles.remove(i);
							}
							else if(board[temp2[1]][temp2[0]]=='P'){
								life=spaceship.hit();
								System.out.println("****A missile has hit you for [34] damage!****");
								if(life<=0){
									System.out.println("Your ship has been destroyed!");
									return;
								}
								board[temp.getycoord()][temp.getxcoord()]=' ';
								missiles.remove(i);
							}
							else{
								buffermis=missiles.get(i);
								board[buffermis.getycoord()][buffermis.getxcoord()]=' ';
								buffermis.setcoords(temp2);
								board[buffermis.getycoord()][buffermis.getxcoord()]='<';
							}
						}
						break;
			//default case to validate user input
			default:	System.out.println("You have entered an invalid choice. Please check your entry and try again.");
						break;
			}
			missiletimer++;
		}while(terminate!=1);
	}
	//smart printing function
	//print function makes the board "scroll" by only showing specified sections at a time.
	public static void boardprint(char[][] board,Player spaceship){
		if(spaceship.getxcoord()<10){
			for(int i=0;i<22;i++){
				for(int j=0;j<spaceship.getxcoord()+20;j++){
					System.out.print(board[i][j]);
				}
				System.out.println();
			}
			return;
		}
		else if(spaceship.getxcoord()<80){
			for(int i=0;i<22;i++){
				for(int j=spaceship.getxcoord()-10;j<spaceship.getxcoord()+20;j++){
					System.out.print(board[i][j]);
				}
				System.out.println();
			}
			return;
		}
		else{
			for(int i=0;i<22;i++){
				for(int j=spaceship.getxcoord()-10;j<102;j++){
					System.out.print(board[i][j]);
				}
				System.out.println();
			}
			return;
		}
			
	}
	//unused
	/*public static void gamesave(char[][] board,String filename) throws FileNotFoundException{
		File file=new java.io.File(filename);
		PrintWriter fout=new PrintWriter(file);
		
		}*/
	}	