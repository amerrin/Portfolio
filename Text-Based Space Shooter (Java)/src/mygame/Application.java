package mygame;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
//application class to sign user in
public class Application {
	protected String name;
	protected ArrayList<String> users=new ArrayList<String>();
	
	Scanner input = new Scanner(System.in);
	//signs user in and catalogs it into a file
	public String signin() throws FileNotFoundException{
		java.io.File file=new java.io.File("regusers.txt");
		PrintWriter fout=new PrintWriter(file);
		Scanner fin=new Scanner(file);
		while(fin.hasNext()){
			String name=fin.next();
			users.add(name);
		}
		boolean checker=false;
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
						fout.print(users.get(i));
					}
					fout.close();
				}
			if(checker==false){
				System.out.println("You have entered invalid login information. Please try again.");
			}
		}while(checker==false);
		fin.close();
		fout.close();
		return name;
	}
}
