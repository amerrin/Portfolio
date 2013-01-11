
package factoryAgents.KitRobot.TestRunningNormative;

import factoryInterfaces.*;
import gfx.*;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.Timer;

public class KitAssembly extends JPanel implements ActionListener {
	double theta;
	
    Ellipse2D.Double myEllipse;
    Rectangle2D.Double backgroundRectangle;
   
    
    GfxKitStation kitStation;
    
    GfxConveyor conveyor;//conveyor for empty kits
    GfxGoodConveyor conveyor2;//conveyor for good kits that move away
    
   
    
    GfxKitRobot kitRobot;
    
    KitRobot kitRobotAgent;
    Conveyor conveyorAgent;
    int spotToMove;
    Timer timer;
    boolean[] status; //array of statuses
    
   
    
    public KitAssembly() {
    	theta = 0.0;
    	
	    backgroundRectangle = new Rectangle2D.Double( 0, 0, 600, 600 );
	    timer = new Timer(50, this);
	    timer.start();
	    conveyor = new GfxConveyor(new Point(0,0));
	    conveyor2 = new GfxGoodConveyor(new Point(0,285));
	    
	    kitStation = new GfxKitStation();
	    
	    
	    
	    status=new boolean[7];
	    status[0] = false;//from inspection to conveyor with good kits
	    status[1] = false;//from conveyor to station (specific spot)
	    status[2] = false;//from station (specific spot) to inspection
	    status[3] = false;//draw new kit;
	    status[4] = false;//
	    status[5] = false;//for conveyor
	    status[6] = false;
	    
	    
	    
	    kitRobot=new GfxKitRobot();
    }
    
    public void DoTakeKitToConveyorForDelivery(){//from inspection to conveyor away
    	
    	status[0] = true;
    }
    
    public void DoPutKitOnTable(int spot){  //from conveyor to specific spot
    	spotToMove=spot;
    	
    	status[1] = true;
    }

    public void DoPlaceKitOnTableForInspection(int spot){ //from specific spot to inspection
    	spotToMove=spot;
    	status[2] = true;
    }
    
    public void DoPutKitOnConveyor(int number){
    	spotToMove = number; //this value will hold how many kits to create
    	status[3] = true;
    }
    
    public void DoTurnOffConveyor() {
    	System.out.println("GUI conveyor OFF");
    	status[4] = true;
    }
    
    public void DoTurnOnConveyor(){
    	System.out.println("GUI conveyor ON");
    	status[5] = true;
    }
    public void DoTurnOnGoodConveyor(){
    	status[6] = true;
    }
    
	/*public static void main(String[] args) {
		KitAssembly window=new KitAssembly();
		window.setSize(600,600);
		window.setVisible(true);
		window.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		new Timer(100,window).start();
		
		//status[1] = true;
		
	    //This is the code used to test operation on our side.
		kitStation.putKitOnStation(new KitGfx(new Point(0,0)), 0);
		spotToMove=1;
		conveyor.addKitToConveyor(new KitGfx(new Point(28, conveyor.position.y)));
		//conveyor2.addKitToConveyor(new KitGfx(new Point(28, conveyor2.position.y)));
		status[1] = true;
		status[0] = true;//sets robot to move from inspection to conveyor with good kits
		kitStation.putKitOnStation(new KitGfx(new Point(28, conveyor.position.y)),0);
	}*/
	
	public void setKitRobot(KitRobot robot){
		kitRobotAgent = robot;
	}
    
	public void setConveyor(Conveyor robot){
		conveyorAgent = robot;
		conveyor.setConveyorAgent(robot);
	}
	
	public void actionPerformed(ActionEvent e) {
		repaint();
	}
	
	public void paint(Graphics g) {
		Graphics2D g2 = (Graphics2D)g;
		Graphics2D gg2 = (Graphics2D)g;
		
		
		
		//Paint the background white so that everything updates.
		g2.setColor(Color.white);
		g2.fill(backgroundRectangle);
		
		//Paint the conveyors.
		conveyor.paintConveyor(this, g2, conveyor.position.x, conveyor.position.y);
		conveyor2.paintConveyor(this, g2, conveyor2.position.x, conveyor2.position.y);
		
		//Paint the kitting station.
		kitStation.paintKittingStation(this, g2, 110, 300);

		/*==Some theta positions, center stand = 1.55, right stand = 1.10, inspect = 1.90
		Move to Stand from conveyor = 2.90,move from stand to conveyor = 2.40*/
	
		if (status[2]){// moving kit to inspection
			if (kitRobot.rotate(theta,0, Math.PI / 6 + 1 / spotToMove, this, g2,gg2)){
				if (kitRobot.hasNoKit() && (theta >= Math.PI / 6 + 0.5 / spotToMove) && (theta <= Math.PI / 6 + 2 / spotToMove)) {
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[spotToMove]));
				}//if has no kit
				if (kitRobot.rotate(theta, 0, 1.72, this, g2,gg2)) {
					if (kitRobot.hasAKit() && (theta > 1.59) && (theta < 1.91))
						kitStation.putKitOnStation(kitRobot.removeKit(), 0);
					if (kitRobot.rotate(theta, 1.72, 2*Math.PI, this, g2,gg2)){
						//message to garima
						
						status[2] = false;
						theta=0.0;
						kitRobotAgent.msgAnimationDone();
					}
				}
			}
			theta=theta+0.1;
		}
		if (status[0]){ //kit from inspection to conveyor and move away
			if (kitRobot.rotate(theta,0,1.72,this,g2,gg2)){
				if (kitRobot.hasNoKit()&&((theta>=-0.1)&&(theta<=1.85))){ //
					kitRobot.getKit(kitStation.removeKitFromStation(kitStation.kitsOnTheTable[0]));
				}//if has no kit
				if (kitRobot.rotate(theta,1.72,2.3,this,g2,gg2)){
					if (kitRobot.hasAKit()&&(theta<2.4)&&(theta>2.0))
						conveyor2.addKitToConveyor(kitRobot.removeKit());
						
					if (kitRobot.rotate(theta,2.3, 2*Math.PI, this, g2,gg2)){
						//message to garima
						//kitRobotAgent.msgAnimationDone();
						status[0] = false;
						theta=0.0;
						kitRobotAgent.msgAnimationDone();
					}
				}
			}
			theta=theta+0.1;
		}
		if (status[1]) {
			//kit from conveyer to stand with specific spot
			if (kitRobot.rotate(theta,0,3.14,this,g2,gg2)){
				if (kitRobot.hasNoKit()&&((theta>=3.0)&&(theta<=3.25))){
					kitRobot.getKit(conveyor.kitsOnConveyor.remove(0));
				}//if has no kit
				if (kitRobot.rotate(theta, 3.14,6.28+0.8/spotToMove,this,g2,gg2)){
					if ((kitRobot.hasAKit())&&(( theta>=(6.28+0.8/spotToMove)-0.4  )&&( theta<= (6.28+0.8/spotToMove)+0.4 )))
					kitStation.putKitOnStation(kitRobot.removeKit(), spotToMove);
					
					if (kitRobot.rotate(theta,3,4*Math.PI, this, g2,gg2)){
						theta=0.0;
						status[1] = false;
						kitRobotAgent.msgAnimationDone();
					}
				}
			}
			theta=theta+0.1;
		}
		if (status[3]){ //if the method for creating a kit was called
			for (int i=0;i<spotToMove;i++) {
				conveyor.addKitToConveyor(new GfxKit(new Point(28, conveyor.position.y)));
			}
			//finish function so each time the kit reaches the edge of the conveyor I need
			//send a message to Garima
			//conveyorAgent.msgAnimationDone();
			status[3] = false;
			//send garima a message
		}
		if (status[4]) {//turn off conveyor
			conveyor.turnConveyorOff();
			status[4]=false;
		}
		if (status[5]) {//turn on conveyor
			conveyor.turnConveyorOn();
			status[5]=false;
			
		}
		if (status[6]) {//turn on conveyor
			conveyor2.turnConveyorOn();
			status[6]=false;
		}
		if (!status[0] && !status[1] && !status[2] && !status[3]) 
			kitRobot.paintKitStationRobot(this, g2, 0, 0, theta,gg2);
	}
}

