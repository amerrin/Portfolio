package myGame;

import javax.swing.*;
import java.util.*;

public class Application {

	
	//main class is created, and asks the user to log-in to the system. The user enters a name, and they are entered into the database, and the game is started.
	public static void main(String[] args){
		boolean checker = false;
		String name;
		ArrayList<String> users = new ArrayList<String>();
		Scanner input = new Scanner(System.in);
		
		System.out.println("Welcome to Anthony's Amazing Spaceship Game!");
		System.out.println("\t...to continue, please sign in below:");
		do{
			System.out.println("\t\t*Login Information*");
			System.out.print("\t\tUsername (one word): ");
			name=input.next();
			System.out.println();
			for(int i=0;i<users.size();i++){
				if(users.get(i).equals(name)){
					System.out.println("User Detected!");
					System.out.println("Welcome back " + name);
					checker=true;
				}
			}
			if(checker==false){
					System.out.println("New User Detected");
					users.add(name);
					System.out.println(name + " " +"has been successfully added.");
					checker=true;
					for(int i=0;i<users.size();i++){
						//fout.print(users.get(i));
					}
					//fout.close();
				}
			if(checker==false){
				System.out.println("You have entered invalid login information. Please try again.");
			}
		}while(checker==false);
		
		
		//applet is created, window is opened, and the game is staretd
		JFrame frame = new JFrame("Space - it - up!");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(600,400);
		frame.setLocationRelativeTo(null);
		frame.add(new Game());
		frame.setVisible(true);
	}
	
	
	
}
