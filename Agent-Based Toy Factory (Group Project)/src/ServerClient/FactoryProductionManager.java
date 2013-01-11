package ServerClient;

import java.util.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class FactoryProductionManager extends GfxManager implements KeyListener{
	//Core Panels
	//JPanel controlPanel = new JPanel();
	//JPanel graphicPanel = new JPanel();
	//Menu / menu items
	/*JMenuBar menuBar = new JMenuBar();
	JMenu jmnView = new JMenu("View");
	JMenuItem jmiGraphics = new JMenuItem("Graphics");
	JMenuItem jmiControl = new JMenuItem("Control");
	JMenuItem jmiExit = new JMenuItem("Exit");
	*/
	
	//all of this has already been coded and is in gfx manager, please read design doc
	
	//JLabels
	JLabel jlbProdMgr = new JLabel("Production Manager");
	JLabel jlbFacCtrl = new JLabel("Factory Control");
	//JLabel jlbNonNorm = new JLabel("Non-Normative Cases");
	JLabel jlbProdschd = new JLabel("Production Schedule");
	//JComboBox and JTextField
	JComboBox jcbKitSelect;
	JTextField jtfKitNumber;
	//JButtons
	JButton jbtAddSchd = new JButton("Add To Schedule");
	JButton jbtStart = new JButton("Start Sim");
	JButton jbtStop = new JButton("Stop Sim");
	/* //Tentative Non-Normative Cases (Definitely Tentative!)
	JButton jbtNonNorm1 = new JButton("Case 1");
	JButton jbtNonNorm2 = new JButton("Case 2");
	JButton jbtNonNorm3 = new JButton("Case 3");
	JButton jbtNonNorm4 = new JButton("Case 4");
	JButton jbtNonNorm5 = new JButton("Case 5");
	JButton jbtNonNorm6 = new JButton("Case 6");
	*/
	//JTextArea and its scrollpane
	JTextArea jtaProdschd;
	JTextArea jtaNewOrders;
	JScrollPane jspTextArea;		//For jtaProdschd
	JScrollPane jspTextArea2;		//For jtaNewOrders
    //Data
    ArrayList<Order> newOrderList = new ArrayList<Order> ();
    ArrayList<Order> orderList = new ArrayList<Order> ();
    ArrayList<BlueprintKit> possibleKits = new ArrayList<BlueprintKit> ();
	boolean started;
	
	public FactoryProductionManager() {
		super(0,0);
		myGUITimer.stop();
		setTitle("Factory Production Manager");
		//Add Menu
		/*
		this.setJMenuBar(menuBar);
		menuBar.add(jmnView);
		jmnView.add(jmiGraphics);
		jmnView.add(jmiControl);
		jmnView.addSeparator();
		jmnView.add(jmiExit);
		*/
		
		//Add ActionListeners
		/*
		jmiGraphics.addActionListener(this);
		jmiControl.addActionListener(this);
		jmiExit.addActionListener(this);
		*/
		jbtAddSchd.addActionListener(this);
		jbtStart.addActionListener(this);
		jbtStop.addActionListener(this);
		/*jbtNonNorm1.addActionListener(this);
		jbtNonNorm2.addActionListener(this);
		jbtNonNorm3.addActionListener(this);
		jbtNonNorm4.addActionListener(this);
		jbtNonNorm5.addActionListener(this);
		jbtNonNorm6.addActionListener(this);
		*/
		
		//Create GUI Components
		jcbKitSelect = new JComboBox();
		jcbKitSelect.addActionListener(this);
		jtfKitNumber = new JTextField(10);
		jtfKitNumber.addKeyListener(this);
		//Create New JTextArea
		jtaProdschd = new JTextArea();
		jtaProdschd.setLineWrap(true);
		jtaProdschd.setEditable(false);
		jtaNewOrders = new JTextArea();
		jtaNewOrders.setLineWrap(true);
		jtaNewOrders.setEditable(false);
		//Add JTextArea to ScrollPane
		jspTextArea = new JScrollPane(jtaProdschd);
		jspTextArea2 = new JScrollPane(jtaNewOrders);
		//Layout and BoxLayout Setup
		Box[] horizontalBoxes = new Box[8];
		for(int i=0;i<8;i++) {
			horizontalBoxes[i] = Box.createHorizontalBox();
		}
		Box leftVerticalBox = Box.createVerticalBox();
		Box rightVerticalBox = Box.createVerticalBox();
		Box mainBox = Box.createHorizontalBox();
		//Add Everything
		horizontalBoxes[0].add(jlbProdMgr);
		horizontalBoxes[1].add(jcbKitSelect);

		horizontalBoxes[1].setMaximumSize(new Dimension(380,30));
		horizontalBoxes[2].add(jbtAddSchd);
		horizontalBoxes[3].add(jlbFacCtrl);
		horizontalBoxes[4].add(jbtStart);
		horizontalBoxes[4].add(Box.createHorizontalStrut(10));
		horizontalBoxes[4].add(jbtStop);
		//horizontalBoxes[5].add(jlbNonNorm);
		/*horizontalBoxes[6].add(jbtNonNorm1);
		horizontalBoxes[6].add(Box.createHorizontalStrut(10));
		horizontalBoxes[6].add(jbtNonNorm2);
		horizontalBoxes[6].add(Box.createHorizontalStrut(10));
		horizontalBoxes[6].add(jbtNonNorm3);
		horizontalBoxes[7].add(jbtNonNorm4);
		horizontalBoxes[7].add(Box.createHorizontalStrut(10));
		horizontalBoxes[7].add(jbtNonNorm5);
		horizontalBoxes[7].add(Box.createHorizontalStrut(10));
		horizontalBoxes[7].add(jbtNonNorm6);*/
		//add boxes to left ver. box
		leftVerticalBox.add(Box.createVerticalStrut(10));
		leftVerticalBox.add(horizontalBoxes[0]);
		leftVerticalBox.add(Box.createVerticalStrut(10));
		leftVerticalBox.add(horizontalBoxes[1]);
		jtfKitNumber.setMaximumSize(new Dimension(100,30));
		leftVerticalBox.add(jtfKitNumber);
		leftVerticalBox.add(horizontalBoxes[2]);
		leftVerticalBox.add(Box.createVerticalStrut(150));
		leftVerticalBox.add(horizontalBoxes[3]);
		leftVerticalBox.add(Box.createVerticalStrut(10));
		leftVerticalBox.add(horizontalBoxes[4]);
		leftVerticalBox.add(Box.createVerticalStrut(150));
		leftVerticalBox.add(horizontalBoxes[5]);
		leftVerticalBox.add(Box.createVerticalStrut(10));
		leftVerticalBox.add(horizontalBoxes[6]);
		leftVerticalBox.add(horizontalBoxes[7]);
		leftVerticalBox.add(Box.createVerticalStrut(150));
		rightVerticalBox.add(Box.createVerticalStrut(10));
		rightVerticalBox.add(jlbProdschd);
		rightVerticalBox.add(Box.createVerticalStrut(15));
		rightVerticalBox.add(jspTextArea);
		rightVerticalBox.add(Box.createVerticalStrut(100));
		rightVerticalBox.add(new JLabel("New Order Queue"));
		rightVerticalBox.add(Box.createVerticalStrut(15));
		rightVerticalBox.add(jspTextArea2);
		rightVerticalBox.add(Box.createVerticalStrut(100));
		mainBox.add(leftVerticalBox);
		mainBox.add(Box.createHorizontalStrut(10));
		mainBox.add(rightVerticalBox);
		mainBox.add(Box.createHorizontalStrut(50));
		swingPanel.add(mainBox);
		this.invalidate();
		this.validate();
		//Add CardLayout
		/*
		CardLayout cardLayout = new CardLayout();
		setLayout(cardLayout);
		*/
		//Graphic Panel is where everything should be painted
		sendMessage(new GetPossibleKitsMessage());
		
		//test
		/*
		jtaProdschd.setText("*");
		for(int i=100000;i<100456;i++) {
			jtaProdschd.append("\nnumber is "+i+"sznig vsilhrrrrrrfuslduvg dghiulvfhsvh iufev eisfiulszuvzhl hv");
			
		}
		jtaNewOrders.setText("*");
		for(int i=100000;i<100456;i++) {
			jtaNewOrders.append("\nnumber is "+i+"sznig vsilhrrrrrrfuslduvg dghiulvfhsvh iufev eisfiulszuvzhl hv");
			
		}*/
		myGUITimer.start();
		
		//jcbKitSelect.setSelectedIndex(0);
	}
    
	/* Call GfxManager  */
    public void actionPerformed(ActionEvent ae) {
    	super.actionPerformed(ae);
    	
        if(ae.getSource()==jbtStart) {
        	if(true) {
        		for(Order o : newOrderList) {
        			sendMessage(new SendOrderMessage(o));
        		}
        		newOrderList.clear();		
        		//Once sent to the server, they're wasted !
        		started = true;
        	} else
        		System.out.println("Error: started already.");
        }
        else if(ae.getSource()==jbtStop) {
        	if(true)
        		sendMessage(new StopProductionMessage());
        	else
        		System.out.println("Error: stopped already.");
        }
        else if(ae.getSource()==jbtAddSchd) {
            int kitSelected = jcbKitSelect.getSelectedIndex();
            try {
            	int number=Integer.parseInt(jtfKitNumber.getText());
            	newOrderList.add(new Order(kitSelected,number));
            } catch (Exception e) {
            	System.out.println("Error: enter a number.");
            }
          //Update current added order
        	jtaNewOrders.setText("*");
        	jtaNewOrders.append("Orders to be placed:\n");
        	for(Order o: newOrderList) {
        		String name = possibleKits.get(o.kID).getName();
        		int number = o.n;
        		jtaProdschd.append("Kit " + name + " : " + number + " to be produced\n");
        	}
        }
        else if (ae.getSource() == myGUITimer) {//update production schedule
        	sendMessage(new GetProductionScheduleMessage());
        	sendMessage(new GetPossibleKitsMessage());
        	//System.out.println("debug: i sent a GetGfxFactoryMessage to the server");
        	//Production schedule will be sent by the server and updateProductionSchedule will be called
        }
    }
    
    /* Called by SendProductionScheduleMessage. */
    public void updateProductionSchedule() {
    	//Update current ProductionSchedule
    	jtaProdschd.setText("*");
		jtaProdschd.append("Current Production Schedule:\n");
    	for(Order o: orderList) {
    		String name = possibleKits.get(o.kID).getName();
    		int number = o.n;
    		jtaProdschd.append("Kit " + name + " : " + number + " remaining\n");
    	}
    	//Update current added order
    	jtaNewOrders.setText("*");
    	jtaNewOrders.append("Orders to be placed:\n");
    	for(Order o: newOrderList) {
    		String name = possibleKits.get(o.kID).getName();
    		int number = o.n;
    		jtaNewOrders.append("Kit " + name + " : " + number + " to be produced\n");
    	}
    }
    
	//stops user from entering a character that is not a digit
    public void keyTyped(KeyEvent e) {  
        char c = e.getKeyChar();  
        if (!(Character.isDigit(c) ||  (c == KeyEvent.VK_BACK_SPACE) || (c == KeyEvent.VK_DELETE))) {
            e.consume();  
        }
    }
    
    //these methods must be declared to implement KeyListener, but are not used
    public void keyReleased(KeyEvent e) { }
    public void keyPressed(KeyEvent e) { }
    
    public void setPossibleKits(ArrayList<BlueprintKit> pk) {
    	try {
	    	ArrayList<BlueprintKit> newPossibleKits = pk;
	    	if(! (newPossibleKits.size() == possibleKits.size()) ) {
	    		possibleKits = pk;
	    		//int selectedIndex = jcbKitSelect.getSelectedIndex();
	    		jcbKitSelect.removeAllItems();
	    		for(BlueprintKit k: possibleKits) {
	    			jcbKitSelect.addItem(k.getName());
	    		}
	    		/*try {
	    			jcbKitSelect.setSelectedIndex(selectedIndex);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}*/
	    	}
	    	else {
	    		boolean flag = true;
	    		for(int i=0; i<pk.size();i++ ) {
	    			if(! (newPossibleKits.get(i).equals(possibleKits.get(i))))
	    				flag = false;
	    		}
	    		if(!flag) {
	    			possibleKits = pk;
		    		//int selectedIndex = jcbKitSelect.getSelectedIndex();
		    		jcbKitSelect.removeAllItems();
		    		for(BlueprintKit k: possibleKits) {
		    			jcbKitSelect.addItem(k.getName());
		    		}
	    		}
	    	}
    	} catch(NullPointerException e) {
    		//do nothing
    	}
    }
    
    public void setProductionSchedule(ArrayList<Order> ps) {
    	orderList = ps;
    }
    
    public static void main(String[] args) {
    	FactoryProductionManager fpm = new FactoryProductionManager();
    	fpm.setSize(1050,750);
    	fpm.setLocationRelativeTo(null);
    	fpm.setVisible(true);
    }

	  
}//authored by Yinong Dai
