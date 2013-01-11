package ServerClient;

import gfx.GfxFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.net.*;
import java.io.*;

//		Purpose
//Base class for all 6 managers. Should have common data such as sockets, 
//in and out streams, port #, jframe data, timer, CardLayout, and JPanel for Swing components.	
public class Manager extends JFrame implements ActionListener, WindowListener {
	
	//constants
	static int PORT = 17889;
	static int DEFAULT_TIMER_SPEED = 100; //10 fps
	
	//member data
	Socket mySocket;
	HandleAServerConnection serverConnection;
	javax.swing.Timer myGUITimer;
	public JPanel swingPanel; //holds all swing components
	
	public Manager() {

		connectToServer(); //establish connection to server and streams
		//start timer
		myGUITimer = new javax.swing.Timer(DEFAULT_TIMER_SPEED, this);
		myGUITimer.start();
		setupGUI(); //setup swing panel
	}
	
	public void connectToServer() {
		try {
			//establish connection to server
			System.out.print("Debug: Connecting to server...");
			mySocket = new Socket("localhost", PORT);
			System.out.println("Connected!");
			
			//establish streams
			serverConnection = new HandleAServerConnection(mySocket, this);
			new Thread(serverConnection).start();
		} catch(Exception ex) {
			ex.printStackTrace();
			System.exit(0);
		}
	}
	
	public void setupGUI() {
		//create GUI swing panel
		swingPanel = new JPanel();
		this.add(swingPanel);
		//swingPanel.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		System.out.println("Action performed called in Manager");
	}
	
	public void switchGUIPanelVisible() { //switch visibility of the gui panel
		swingPanel.setVisible(!swingPanel.isVisible());
		
		if (swingPanel.isVisible())
			System.out.println("Swing panel is now visible.");
	}
	
	public void sendMessage(MessageToServer msg) {
			serverConnection.sendMessage(msg);
	}
	
	public Manager getRef() {return this;}
	
	public void setLocalGfxFactory (GfxFactory gf) {
		//do nothing, this should be overridden
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.invalidate();
		this.validate();
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		// TODO Auto-generated method stub
		this.invalidate();
		this.validate();
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	//managers that extend Manager should create their own main methods
	/*
	public static void main(String[] args) {
	//unit test, make sure to run UTManager if testing this 
		Manager mg = new Manager();
		
		mg.setVisible(true);
		mg.setDefaultCloseOperation(EXIT_ON_CLOSE);
		mg.setResizable(false);
		mg.setSize(400, 400);
	}
	*/
}

//written by Alex Jones
