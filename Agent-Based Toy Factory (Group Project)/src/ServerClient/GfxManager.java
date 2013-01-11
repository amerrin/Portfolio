package ServerClient;

import javax.swing.*;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import gfx.*;


import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.swing.*;
import factoryObjects.*;

/* This class extends manager and adds the necessary components to paint graphics for the factory.
It is the base class for the 4 Managers that use graphics. */
public class GfxManager extends Manager implements ActionListener, WindowListener {
	static final int DEFAULT_GFX_CLK_SPEED = 30;
	int xOffset, yOffset;
	
	javax.swing.Timer gfxTimer;
	public JPanel gfxPanel;
	CardLayout cl;
	//JPanel for holding painted graphics
	JMenuBar menuBar;
	//Menu bar for switching between cards in CardLayout and exiting
	JMenu menu;
	//Menu item for switching between cards in CardLayout and exiting
	JMenuItem switchView, exit;
	//switch to the other panel, exit the manager application
	GfxFactory localGfxFactory;
	//a snapshot of the GfxFactory that is updated by the SendGfxFactoryMessage from the server before repaint() is called>>
	String currentVisiblePanel;
	
	//Optimization Attempt - Alex Jones
	private static ImageIcon background = new ImageIcon("src/images/background.png");
	ImageIcon [] binFullImage;
	ImageIcon emptyBinImage;
	ImageIcon cameraImage;
	ImageIcon conveyorImage;
	ImageIcon conveyorBarImage, feederImage, gantryImage, goodconveyorImage, kitPicture, kitRobotImage;
	ImageIcon kitStationPicture, laneImage, nestImage, partRobotImage, badPartImage;
	ImageIcon [] laneBars,  partImages;
	private ImageIcon emptyPartRobot;
	private ImageIcon fullPartRobot;
	BufferedImage bf;// = new BufferedImage( this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
	
	//Note: when making a subclass, call super(xoff, yoff) where the offsets are the starting portion of the factory you'd like to show
	public GfxManager(int xOff, int yOff) {
		
		//images
		//bin
		binFullImage  = new ImageIcon[21];
		partImages = new ImageIcon[21];
		emptyBinImage=new ImageIcon("src/images/bin_empty.png");
		laneBars = new ImageIcon[3];
		for (int i = 0; i < 21; i++)
			binFullImage[i]=new ImageIcon("src/images/bin_full" + i + ".png");
		//camera
		cameraImage = new ImageIcon("src/images/Camera.png");
		//conveyor
		conveyorImage=new ImageIcon("src/images/conveyor.png");
		conveyorBarImage=new ImageIcon("src/images/conveyorBar.png");
		//feeder
		feederImage=new ImageIcon("src/images/feeder.png");
		//gantry
		gantryImage=new ImageIcon("src/images/GantryRobot.png");
		//good conveyor
		goodconveyorImage =new ImageIcon("src/images/goodConveyor.png");
		//kit
		kitPicture = new ImageIcon("src/images/Kit.png");
		//kit robot
		kitRobotImage=new ImageIcon("src/images/KitRobot.png");
		//kitStation
		 kitStationPicture =new ImageIcon("src/images/KittingStand.png");
		//lane
		 laneImage = new ImageIcon("src/images/newLane.png");
		 
		 laneBars[0] = new ImageIcon("src/images/newLaneBar.png");
		 laneBars[1]=new ImageIcon("src/images/newLaneBar2.png");
		laneBars[2]=new ImageIcon("src/images/newLaneBar3.png");
		//nests
		 nestImage=new ImageIcon("src/images/Nest.png");
		//part
		 for (int i = 0; i < 21; i++)
			 partImages[i]=new ImageIcon("src/images/part" + i+ ".png");
		badPartImage= new ImageIcon("src/images/part0.png");
		//partrobot
		 emptyPartRobot=new ImageIcon("src/images/GreenPartRobot.png");
		 fullPartRobot=new ImageIcon("src/images/RedPartRobot.png");
		 
			
		//null until factory state is received from server
		localGfxFactory = new GfxFactory(); 
		xOffset = xOff;
		yOffset = yOff;
		
		gfxPanel = new JPanel();
		cl = new CardLayout();
		setLayout(cl);
		
		menuBar = new JMenuBar();
		menu = new JMenu("Actions");
		switchView = new JMenuItem("Switch View");
		exit = new JMenuItem("Exit");
		
		//add all components to the menu, then set the menu
		menuBar.add(menu);
		menu.add(switchView);
		menu.add(exit);
		setJMenuBar(menuBar);
		
		add(gfxPanel, "1");
		add(swingPanel, "2");
		currentVisiblePanel = "1";
		cl.show(this.getContentPane(), currentVisiblePanel);
		//now window will show the graphics panel
		
		//add action listeners
		switchView.addActionListener(this);
		exit.addActionListener(this);
		
		gfxPanel.setDoubleBuffered(true);
		//set timer
		bf = new BufferedImage( 1050,750, BufferedImage.TYPE_INT_RGB);
		
		gfxTimer = new javax.swing.Timer(DEFAULT_GFX_CLK_SPEED,	this);
		gfxTimer.start();
		}
	
	//switch visibility of the gfx panel
	public void switchPanelVisibility() {
		//cl.next(super.getRef());
		/*gfxPanel.setVisible(!gfxPanel.isVisible());
		swingPanel.setVisible(!swingPanel.isVisible());
		
		if (gfxPanel.isVisible())
			System.out.println("Gfx panel is now visible.");
		if (swingPanel.isVisible())
			System.out.println("Swing panel is now visible.");*/
		if(currentVisiblePanel.equals("1"))
			currentVisiblePanel = "2";
		else
			currentVisiblePanel = "1";
		cl.show(this.getContentPane(), currentVisiblePanel);
	}
	
	//all of the sources should be mutually exclusive
	public void actionPerformed(ActionEvent ae) {
		Object src = ae.getSource();
		
		if (src == switchView) {
			//System.out.println("Debug: Switching View");
			//switch views
			switchPanelVisibility();
		} else if (src == exit) {
			//System.out.println("Debug: Exiting Manager");
			sendMessage(new ClientDoneMessage());
			this.dispose();
		} else if (src == gfxTimer) {//update GFX Factory and repaint
			sendMessage(new GetGfxFactoryMessage());
			// Server will SendGfxFactoryMessage which will call the repaint method
		}
	}
	
	/* Translate origin by offsets, and then call the GfxFactory's paint method. */
	public void paint(Graphics g){
		
		if (gfxPanel.isVisible()) {
			Graphics2D g2 = (Graphics2D)bf.getGraphics();
			//fix offsets
			g.translate(-xOffset, -yOffset + 105);
			//paint each object
			//localGfxFactory.paintFactory(gfxPanel, g2);
			
			background.paintIcon(gfxPanel, g2, 0, 0);
			for (GfxLane l : localGfxFactory.getMyLanes()) {
				l.paintLane(gfxPanel, g2, laneImage, laneBars, partImages, badPartImage );
			}
			for (GfxBin b : localGfxFactory.getMyBins()) {
				b.paintBin(gfxPanel, g2, binFullImage[b.getpartid()], emptyBinImage);
			}
			for (GfxFeeder f : localGfxFactory.getMyFeeders()) {
				f.paintFeeder(gfxPanel, g2, feederImage);
			}
			localGfxFactory.getGantry().paintGantry(gfxPanel, g2, gantryImage);
			//Paint PartRobot / Nests
			for(GfxNest n : localGfxFactory.getMyGfxNests()) {
				n.paintNest(gfxPanel, g2, nestImage, partImages, badPartImage);
			}
			
			//paint kit robot
			localGfxFactory.getKitStation().paintKittingStation(gfxPanel, g2, kitStationPicture,  kitPicture, partImages, badPartImage );
			localGfxFactory.getConveyor().paintConveyor(gfxPanel, g2, 0, 0, conveyorImage, conveyorBarImage, kitPicture, partImages,badPartImage);
			localGfxFactory.getGoodConveyor().paintConveyor(gfxPanel, g2, 0, 285, goodconveyorImage, conveyorBarImage, kitPicture, partImages,badPartImage );
			localGfxFactory.getKitRobot().paintKitStationRobot(gfxPanel, g2,  kitRobotImage, kitPicture, partImages, badPartImage);
			//paint kits and its parts
			
			localGfxFactory.getMyGfxPartRobot().paintPartRobot(gfxPanel, g2, fullPartRobot, emptyPartRobot);
			if(localGfxFactory.getMyGfxCamera() != null) {
				//System.out.println("painted camera");
				localGfxFactory.getMyGfxCamera().paintCamera(gfxPanel, g2, cameraImage);
			}
			g.drawImage(bf,0, 0, null);
		}
	}

	/* Set local factory when called by the SendGfxFactoryMessage. */
	public void setLocalGfxFactory (GfxFactory gf) {
		localGfxFactory = gf;
	}
	
	//make sure to run UTGfxManager if testing this.
	public static void main(String[] args) {
		GfxManager mg = new GfxManager(0, 0);
		
		mg.setVisible(true);
		mg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		mg.setSize(1050, 750);
	}
	
	@Override
	public void windowClosed(WindowEvent e) {
		System.out.println("Debug: Exiting Manager");
		sendMessage(new ClientDoneMessage());
	}
	
	@Override
	public void windowActivated(WindowEvent e) {
	}

	@Override
	public void windowClosing(WindowEvent e) {
	}

	@Override
	public void windowDeactivated(WindowEvent e) {
	}

	@Override
	public void windowDeiconified(WindowEvent e) {
	}

	@Override
	public void windowIconified(WindowEvent e) {
	}

	@Override
	public void windowOpened(WindowEvent e) {
	}
}

//written by Alex Jones
