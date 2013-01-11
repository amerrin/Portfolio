package ServerClient;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.*;

/* GantryManager class views lanes, and manages lane non-normatives for V2. */
public class KitAssemblyManager extends GfxManager {
	// constants for the kitAssemblyManager offset from the top left corner of the Factory animation
	static final int xOff = 0, yOff = 0; 
	//Label
	JLabel infoLabel;
	//ArrayList of all check boxes
	ArrayList<JCheckBox> checkboxes;
	//ArrayList of labels to display parts images
	ArrayList<JLabel> imageLabels;
	//button for non norm case
	JButton goButton;
	//holds the kit currently being made
	//so that the current kit can be displayed and
	//the user can choose which parts to remove
	BlueprintKit currentKitConfig;
	//array list of all possible kits, updated from Server
	ArrayList<BlueprintKit> possibleKits;
	
	
	/* Pass offsets to GfxManager constructor, and set up non-normative panel. */
	public KitAssemblyManager() {
		super(xOff, yOff);
		setTitle("Kit Assembly Manager");
		setSize(400, 750);
		Box verticalBox = Box.createVerticalBox();
		Box[] horizontalBox = new Box[7];
		for(int i=0; i<7; i++) {
			horizontalBox[i] = Box.createHorizontalBox();
		}
		
		//create Checkboxes & JLabels in ArrayList
		checkboxes = new ArrayList<JCheckBox>();
		imageLabels = new ArrayList<JLabel>();
		for (int i = 1; i<=8; i++) {
			checkboxes.add(new JCheckBox("Part"+i));
			checkboxes.get(i-1).setEnabled(false);
			
			//create image labels (blank)
			imageLabels.add(new JLabel(new ImageIcon("src/images/transparent.png")));
		}
		
		verticalBox.add(Box.createVerticalStrut(100));
		JLabel lblNewLabel = new JLabel("Bad Kit - Non Normative");
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		horizontalBox[4].add(lblNewLabel);			//1
		
		verticalBox.add(horizontalBox[4]);
		
		verticalBox.add(Box.createVerticalStrut(20));
		infoLabel = new JLabel("No Kits Being Made");
		
		infoLabel.setFont(new Font("Tahoma", Font.PLAIN, 14));
		horizontalBox[5].add(infoLabel);		//1.5
		//horizontalBox[5].add(Box.createHorizontalStrut(17));
		verticalBox.add(horizontalBox[5]);
		
		//horizontalBox0
		verticalBox.add(Box.createVerticalStrut(25));
		//horizontalBox[0].add(Box.createHorizontalStrut(50));
		//JCheckBox checkBox = new JCheckBox("Part 1");
		
		horizontalBox[0].add(checkboxes.get(0));		//2
		horizontalBox[0].add(imageLabels.get(0));
		
		//JCheckBox chckbxPart = new JCheckBox("Part 2");
		
		horizontalBox[0].add(checkboxes.get(1));	//2.5
		horizontalBox[0].add(imageLabels.get(1));
		
		verticalBox.add(horizontalBox[0]);	//2.99
		
		verticalBox.add(Box.createVerticalStrut(25));
		//horizontalBox[1].add(Box.createHorizontalStrut(50));
		//JCheckBox chckbxPart_1 = new JCheckBox("Part 3");
		
		horizontalBox[1].add(checkboxes.get(2));	//3
		horizontalBox[1].add(imageLabels.get(2));
		
		//JCheckBox chckbxPart_2 = new JCheckBox("Part 4");
		
		horizontalBox[1].add(checkboxes.get(3));	//3.5
		horizontalBox[1].add(imageLabels.get(3));
		
		verticalBox.add(horizontalBox[1]);	//3.99
		
		verticalBox.add(Box.createVerticalStrut(25));
		//horizontalBox[2].add(Box.createHorizontalStrut(50));
		//JCheckBox chckbxPart_3 = new JCheckBox("Part 5");
		
		horizontalBox[2].add(checkboxes.get(4));	//4
		horizontalBox[2].add(imageLabels.get(4));
		
		//JCheckBox chckbxPart_4 = new JCheckBox("Part 6");
		
		horizontalBox[2].add(checkboxes.get(5));	//4.5
		horizontalBox[2].add(imageLabels.get(5));
		
		verticalBox.add(horizontalBox[2]);	//4.99
		
		verticalBox.add(Box.createVerticalStrut(25));
		//horizontalBox[3].add(Box.createHorizontalStrut(50));
		//JCheckBox chckbxPart_5 = new JCheckBox("Part 7");
		
		horizontalBox[3].add(checkboxes.get(6));	//5
		horizontalBox[3].add(imageLabels.get(6));
		
		//JCheckBox chckbxPart_6 = new JCheckBox("Part 8");
		
		horizontalBox[3].add(checkboxes.get(7));	//5.5
		horizontalBox[3].add(imageLabels.get(7));
		
		verticalBox.add(horizontalBox[3]);	//5.99
		
		verticalBox.add(Box.createVerticalStrut(100));
		goButton = new JButton("GO");
		goButton.addActionListener(this);
		
		horizontalBox[6].add(goButton);
		//horizontalBox[6].add(Box.createHorizontalStrut(35));
		verticalBox.add(horizontalBox[6]);
		//swingPanel.removeAll();
		//swingPanel = new BadKitNonNormative();*/
		swingPanel.add(verticalBox);
		
		setResizable(false);
		
	}
	
	public static void main(String[] args){
		KitAssemblyManager kam = new KitAssemblyManager();
	}
	
	public void actionPerformed(ActionEvent e) {
		//call GfxManager's actionPerformed
		//which includes code for menu bar and timer
		super.actionPerformed(e);
		
		//get which parts to delete and send
		//the correct message to the server
		if (e.getSource()==goButton) {
			//create string of message to send to server
			String message = "bk;";
			//see which checkboxes are checked
			for(int i=0; i<checkboxes.size(); i++) {
				//if checkbox is selected,
				//add part # to message list of data
				if(checkboxes.get(i).isSelected()) {
					//for formatting message commas
					if(message.equals("bk;")) {
						message = message+i;
					}
					else {
						message = message+","+i;
					}
				}
			}
			
			//if any parts are clicked then send message
			if (!message.equals("bk;")) {
				sendMessage(new NonNormativeMessage(message));
				System.out.println("Debug: "+message);
				
				//also un-select all checkboxes after pressing
				for (int i=0; i<checkboxes.size(); i++) {
					checkboxes.get(i).setSelected(false);
				}
			}
			
		}
		//make sure button is correctly activated or not
		//based on whether any checkboxes are pressed
		else if (e.getSource()==myGUITimer) {
			//check if any boxes are pressed,
			//if yes, make button enabled
			for(int i=0; i<checkboxes.size(); i++) {
				if (checkboxes.get(i).isSelected()) {
					if (!goButton.isEnabled()) {
						goButton.setEnabled(true);
					}
					return;
				}
			}
			//if no boxes are pressed,
			//make button disabled
			if (goButton.isEnabled()) {
				goButton.setEnabled(false);
			}
			
			//Get current kit config info
			//step 1: get all possible kits
			//calls setPossibleKits when getting return message
			sendMessage(new GetPossibleKitsMessage());
			//step 2: set the current kit
			//calls setCurrentKitConfig when getting return message
			sendMessage(new GetProductionScheduleMessage());
			
			//after getting kit info from server,
			//update GUI with info
			updateShownKit();
		}
	}
	
	//go through all the parts in the kit 
	//and set the text/icon next to the checkbox as appropriate
	//based on the kit currently being made
	//method called by GUI timer
	private void updateShownKit() {
		//check for null pointers, stop method if null pointers
		if (currentKitConfig==null) {
			return;
		}
		
		//for each part (or none part) in kit config
		int numOfParts = currentKitConfig.getParts().size();
		for(int i=0; i<numOfParts; i++) {
			BlueprintPart part = currentKitConfig.getParts().get(i);
			//if part is a none part
			if(part.getName().equals("None")) {
				checkboxes.get(i).setText("<Empty>");
				//disable checkbox because cannot delete empty part
				checkboxes.get(i).setEnabled(false);
			}
			//else if part is not None:
			//then put image of part next to check and
			//enable the checkbox for deletion ability
			else {
				imageLabels.get(i).setIcon(new ImageIcon(part.getImageName()));
				//enable checkbox
				checkboxes.get(i).setEnabled(true);
				//get rid of text next to box
				checkboxes.get(i).setText("");
			}
		}
		
		//once list of parts has been gone through,
		//set the rest of the checkboxes to empty and disabled
		//if kit has less than 8 parts
		if (numOfParts < 8) {
			for (int i=numOfParts; i<8; i++) {
				checkboxes.get(i).setText("<Empty>");
				checkboxes.get(i).setEnabled(false);
			}
		}
	} //end updateShownKit()
	
	//sets the current kitConfig based on kID from prod. schedule
	public void setCurrentKitConfig(int kID) {
		if (possibleKits!=null) {
			currentKitConfig = possibleKits.get(kID);
		}
		infoLabel.setText("Select Parts To Remove:");
	}
	
	//Called by the SendPossibleKitsMessage from Server
	//sets the ArrayList of possible kits, called each GUI timer
	public void setPossibleKits(ArrayList<BlueprintKit> k) {
		possibleKits = k;
	}
}

//implemented Michael Bock
