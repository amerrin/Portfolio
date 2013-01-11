package restaurant.gui;

import restaurant.CookAgent;
import restaurant.CustomerAgent;
import restaurant.WaiterAgent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.io.File;


/** Main GUI class.
 * Contains the main frame and subsequent panels */
public class RestaurantGui extends JFrame implements ActionListener{
   
    private final int WINDOWX = 500;
    private final int WINDOWY = 400;

    private RestaurantPanel restPanel = new RestaurantPanel(this);
    private JPanel infoPanel = new JPanel();
    private JLabel infoLabel = new JLabel(
    "<html><pre><i>(Click on a customer/waiter)</i></pre></html>");
    private JCheckBox stateCB = new JCheckBox();
    private JCheckBox reorderCB = new JCheckBox();
	private JButton addTable = new JButton("Add Table");
	private JButton changeSupply = new JButton("Change Supply");
	private JPanel buttonHolder = new JPanel();
	
    private Object currentPerson;
    
    private CookAgent cook;

    /** Constructor for RestaurantGui class.
     * Sets up all the gui components. */
    public RestaurantGui(){

	super("Restaurant Application");

	setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	setBounds(50,50, WINDOWX, WINDOWY);

	getContentPane().setLayout(new BoxLayout((Container)getContentPane(),BoxLayout.Y_AXIS));

	Dimension rest = new Dimension(WINDOWX, (int)(WINDOWY*.6));
	Dimension info = new Dimension(WINDOWX, (int)(WINDOWY*.25));
	restPanel.setPreferredSize(rest);
	restPanel.setMinimumSize(rest);
	restPanel.setMaximumSize(rest);
	infoPanel.setPreferredSize(info);
	infoPanel.setMinimumSize(info);
	infoPanel.setMaximumSize(info);
	infoPanel.setBorder(BorderFactory.createTitledBorder("Information"));

	stateCB.setVisible(false);
	stateCB.addActionListener(this);
	reorderCB.setVisible(false);
	reorderCB.addActionListener(this);
	
	infoPanel.setLayout(new GridLayout(1,2, 30,0));
	infoPanel.add(infoLabel);
	infoPanel.add(stateCB);
	infoPanel.add(reorderCB);
	
	buttonHolder.add(addTable);
	buttonHolder.add(changeSupply);
	
	getContentPane().add(restPanel);
	getContentPane().add(buttonHolder);
	getContentPane().add(infoPanel);
	
	addTable.addActionListener(this);
	changeSupply.addActionListener(this);
    }


    /** This function takes the given customer or waiter object and 
     * changes the information panel to hold that person's info.
     * @param person customer or waiter object */
    public void updateInfoPanel(Object person){
	stateCB.setVisible(true);
	currentPerson = person;
	
	if(person instanceof CustomerAgent){
	    CustomerAgent customer = (CustomerAgent) person;
	    stateCB.setText("Hungry?");
	    stateCB.setSelected(customer.isHungry());
	    stateCB.setEnabled(!customer.isHungry());
		reorderCB.setVisible(true);
	    reorderCB.setText("Reorder?");
	    reorderCB.setSelected(customer.hasReordered());
	    reorderCB.setEnabled(customer.hasOrdered()&&!customer.hasReordered());
	    infoLabel.setText(
	    "<html><pre>     Name: " + customer.getName() + " </pre></html>");

	}else if(person instanceof WaiterAgent){
	    WaiterAgent waiter = (WaiterAgent) person;
		reorderCB.setVisible(false);
	    stateCB.setText("On Break?");
	    stateCB.setSelected(waiter.isOnBreak());
	    stateCB.setEnabled(true);
	    infoLabel.setText(
	    "<html><pre>     Name: " + waiter.getName() + " </html>");
	}//else if(person instanceof MarketAgent){
		
	//}

	infoPanel.validate();
    }

    /** Action listener method that reacts to the checkbox being clicked */
    public void actionPerformed(ActionEvent e){

	if(e.getSource() == stateCB){
	    if(currentPerson instanceof CustomerAgent){
		CustomerAgent c = (CustomerAgent) currentPerson;
		c.setHungry();
		stateCB.setEnabled(false);

	    }else if(currentPerson instanceof WaiterAgent){
		WaiterAgent w = (WaiterAgent) currentPerson;
		w.setBreakStatus(stateCB.isSelected());
	    }
	}
	else if (e.getSource() == reorderCB){
		if(currentPerson instanceof CustomerAgent){
		CustomerAgent c = (CustomerAgent) currentPerson;
		c.setReorder();
		reorderCB.setEnabled(false);
		}
	}
	else if (e.getSource() == addTable)
	{
		try {
			System.out.println("[Gautam] Add Table!");
			//String XPos = JOptionPane.showInputDialog("Please enter X Position: ");
			//String YPos = JOptionPane.showInputDialog("Please enter Y Position: ");
			//String size = JOptionPane.showInputDialog("Please enter Size: ");
			//restPanel.addTable(10, 5, 1);
			//restPanel.addTable(Integer.valueOf(YPos).intValue(), Integer.valueOf(XPos).intValue(), Integer.valueOf(size).intValue());
			restPanel.addTable();
		}
		catch(Exception ex) {
			System.out.println("Unexpected exception caught in during setup:"+ ex);
		}
	}
	else if (e.getSource() == changeSupply){
	String food = JOptionPane.showInputDialog(null, "Chicken, Salad, Pizza, or Steak: ","Enter Name of Food",  1).toLowerCase();
	String foods[] = new String[]{"chicken","steak","pizza","salad"};
	for(int i=0; i<foods.length;i++){
		if(food.equals(foods[i])){
			try{
			String supplyStr = JOptionPane.showInputDialog(null,"Please enter desired amount: ","0-99",1);
			Integer supply = Integer.valueOf(supplyStr).intValue();
			cook.setStock(food,supply);
			return;
			}
			catch (NumberFormatException exc) {
			JOptionPane.showMessageDialog(null,"input was not a number, please try again");
			return;
			}
		}
	}

	JOptionPane.showMessageDialog(null,"invalid food choice, please try again");
	}
	    
    }

    /** Message sent from a customer agent to enable that customer's 
     * "I'm hungery" checkbox.
     * @param c reference to the customer */
    public void setCustomerEnabled(CustomerAgent c){
	if(currentPerson instanceof CustomerAgent){
	    CustomerAgent cust = (CustomerAgent) currentPerson;
	    if(c.equals(cust)){
		stateCB.setEnabled(true);
		stateCB.setSelected(false);
		reorderCB.setSelected(false);
	    }
	}}
	
    public void toggleReorderEnabled(CustomerAgent c){
    if(currentPerson instanceof CustomerAgent){
    	CustomerAgent cust = (CustomerAgent) currentPerson;
    	if(c.equals(cust)){
    		if(reorderCB.isEnabled()){
    			reorderCB.setEnabled(false);	
    			reorderCB.setEnabled(c.hasReordered());
    		}
    		else{
    		if(c.hasOrdered())
    			reorderCB.setEnabled(true);
    			reorderCB.setSelected(c.hasReordered());
    		}
    	}
    }
    }
	
    public void setCook(CookAgent c){
    this.cook = c;
    }
    
    /** Main routine to get gui started */
    public static void main(String[] args){
	RestaurantGui gui = new RestaurantGui();
	gui.setVisible(true);
	gui.setResizable(false);
    }
}
