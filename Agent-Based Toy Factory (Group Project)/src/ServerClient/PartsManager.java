package ServerClient;

import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.SpringLayout;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JLabel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;


@SuppressWarnings("serial")
public class PartsManager extends Manager implements KeyListener, WindowListener {
	
	//parts images
	static String[] imageNames = {"Green Alien","Pig","Binoculars","Buzz","Woody","Penguin","Potato Man","Bo Peep","Purple Alien","Octopus","UFO","Bear","Zurg","Etch-a-Sketch","Potato Woman", "Hippo","Clown","Jessie","Monkey","Barbie"};
	
	private JTextField newNameTextField;
	private JTextField newNumberTextField;
	private JTextField newDescriptionTextField;
	@SuppressWarnings("rawtypes")
	JComboBox newImageChooser;
	private JLabel newCurrentImage;
	static private Dimension PART_DIMENSION = new Dimension(21,21);
	JButton createPartButton;
	
	private JMenuItem exitMenuButton;
	
	private JButton deletePartButton;
	
	@SuppressWarnings("rawtypes")
	private JComboBox partToEditChooser;
	private JTextField editNameTextField;
	private JTextField editNumberTextField;
	private JTextField editDescriptionTextField;
	@SuppressWarnings("rawtypes")
	private JComboBox editImageChooser;
	private JLabel editCurrentImage;
	private JButton confirmChangeButton;
	
	private ArrayList<BlueprintPart> possibleParts;
	
	//true during constructor only
	boolean initializing; 
	
	//counter for initilizing from server
	int initializingCount;

	/**
	 * Create the panel.
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public PartsManager() {
		initializing = true;
		myGUITimer.stop();
		
		
		//for closing action
		addWindowListener(this);
		
		//set title
		setTitle("Parts Manager");
		
		swingPanel.setBackground(Color.LIGHT_GRAY);
		swingPanel.setForeground(Color.WHITE);
		SpringLayout springLayout = new SpringLayout();
		swingPanel.setLayout(springLayout);
		
		setMinimumSize(new Dimension(500,400));
		setMaximumSize(new Dimension(500,400));
		setResizable(false);
		
		JMenuBar menuBar = new JMenuBar();
		springLayout.putConstraint(SpringLayout.NORTH, menuBar, 0, SpringLayout.NORTH, swingPanel);
		springLayout.putConstraint(SpringLayout.WEST, menuBar, 0, SpringLayout.WEST, swingPanel);
		setJMenuBar(menuBar);
		
		JMenu menu = new JMenu("File");
		menuBar.add(menu);
		
		exitMenuButton = new JMenuItem("Exit");
		exitMenuButton.addActionListener(this);
		menu.add(exitMenuButton);
		
		JLabel lblPart = new JLabel("Parts Manager");
		lblPart.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.WEST, lblPart, 186, SpringLayout.WEST, swingPanel);
		lblPart.setFont(new Font("Tahoma", Font.PLAIN, 18));
		swingPanel.add(lblPart);
		
		JLabel lblNewLabel = new JLabel("Name:");
		lblNewLabel.setForeground(Color.BLACK);
		swingPanel.add(lblNewLabel);
		
		newNameTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.SOUTH, lblPart, -21, SpringLayout.NORTH, newNameTextField);
		springLayout.putConstraint(SpringLayout.NORTH, newNameTextField, 66, SpringLayout.NORTH, swingPanel);
		springLayout.putConstraint(SpringLayout.WEST, newNameTextField, 107, SpringLayout.WEST, swingPanel);
		springLayout.putConstraint(SpringLayout.NORTH, lblNewLabel, 3, SpringLayout.NORTH, newNameTextField);
		springLayout.putConstraint(SpringLayout.EAST, lblNewLabel, -6, SpringLayout.WEST, newNameTextField);
		swingPanel.add(newNameTextField);
		newNameTextField.setColumns(10);
		
		JLabel lblNumber = new JLabel("Number:");
		lblNumber.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.EAST, newNameTextField, -38, SpringLayout.WEST, lblNumber);
		swingPanel.add(lblNumber);
		
		newNumberTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, newNumberTextField, 304, SpringLayout.WEST, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, lblNumber, -6, SpringLayout.WEST, newNumberTextField);
		swingPanel.add(newNumberTextField);
		newNumberTextField.setColumns(10);
		//add key listener to number text field to ensure only digits are typed
		newNumberTextField.addKeyListener(this);
		
		JLabel lblDescription = new JLabel("Description:");
		lblDescription.setForeground(Color.BLACK);
		swingPanel.add(lblDescription);
		
		newDescriptionTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.SOUTH, lblNumber, -20, SpringLayout.NORTH, newDescriptionTextField);
		springLayout.putConstraint(SpringLayout.NORTH, newDescriptionTextField, 17, SpringLayout.SOUTH, newNameTextField);
		springLayout.putConstraint(SpringLayout.SOUTH, newNumberTextField, -17, SpringLayout.NORTH, newDescriptionTextField);
		springLayout.putConstraint(SpringLayout.WEST, newDescriptionTextField, 107, SpringLayout.WEST, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, newDescriptionTextField, -94, SpringLayout.EAST, swingPanel);
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription, 3, SpringLayout.NORTH, newDescriptionTextField);
		springLayout.putConstraint(SpringLayout.EAST, lblDescription, -6, SpringLayout.WEST, newDescriptionTextField);
		swingPanel.add(newDescriptionTextField);
		newDescriptionTextField.setColumns(10);
		
		JLabel lblSelectImage = new JLabel("Image:");
		lblSelectImage.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.NORTH, lblSelectImage, 17, SpringLayout.SOUTH, lblDescription);
		swingPanel.add(lblSelectImage);
		
		newImageChooser = new JComboBox(imageNames);
		springLayout.putConstraint(SpringLayout.WEST, newImageChooser, 70, SpringLayout.WEST, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, newImageChooser, -210, SpringLayout.EAST, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, lblSelectImage, -4, SpringLayout.WEST, newImageChooser);
		springLayout.putConstraint(SpringLayout.NORTH, newImageChooser, -3, SpringLayout.NORTH, lblSelectImage);
		newImageChooser.addActionListener(this);
		swingPanel.add(newImageChooser);
		
		createPartButton = new JButton("Create Part");
		springLayout.putConstraint(SpringLayout.NORTH, createPartButton, -4, SpringLayout.NORTH, lblSelectImage);
		createPartButton.addActionListener(this);
		swingPanel.add(createPartButton);
		
		partToEditChooser = new JComboBox();
		springLayout.putConstraint(SpringLayout.SOUTH, partToEditChooser, -141, SpringLayout.SOUTH, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, partToEditChooser, 0, SpringLayout.EAST, newImageChooser);
		partToEditChooser.addActionListener(this);
		
		possibleParts = new ArrayList<BlueprintPart>();
		sendMessage(new GetPossiblePartsMessage());
		
		swingPanel.add(partToEditChooser);
		
		JLabel lblChoosePartTo = new JLabel("Choose Part to Edit:");
		lblChoosePartTo.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.WEST, partToEditChooser, 6, SpringLayout.EAST, lblChoosePartTo);
		springLayout.putConstraint(SpringLayout.WEST, lblChoosePartTo, 0, SpringLayout.WEST, lblSelectImage);
		swingPanel.add(lblChoosePartTo);
		
		newCurrentImage = new JLabel(new ImageIcon("src/images/part1.png"));
		newCurrentImage.setForeground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.WEST, createPartButton, 6, SpringLayout.EAST, newCurrentImage);
		springLayout.putConstraint(SpringLayout.NORTH, newCurrentImage, 0, SpringLayout.NORTH, lblSelectImage);
		springLayout.putConstraint(SpringLayout.WEST, newCurrentImage, 16, SpringLayout.EAST, newImageChooser);
		newCurrentImage.setMinimumSize(PART_DIMENSION);
		newCurrentImage.setMaximumSize(PART_DIMENSION);
		swingPanel.add(newCurrentImage);
		
		deletePartButton = new JButton("Delete Selected Part");
		springLayout.putConstraint(SpringLayout.NORTH, deletePartButton, -1, SpringLayout.NORTH, partToEditChooser);
		springLayout.putConstraint(SpringLayout.EAST, deletePartButton, -22, SpringLayout.EAST, swingPanel);
		deletePartButton.addActionListener(this);
		swingPanel.add(deletePartButton);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(Color.BLACK);
		separator.setBackground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.NORTH, partToEditChooser, 20, SpringLayout.SOUTH, separator);
		springLayout.putConstraint(SpringLayout.NORTH, lblChoosePartTo, 23, SpringLayout.SOUTH, separator);
		springLayout.putConstraint(SpringLayout.NORTH, separator, 22, SpringLayout.SOUTH, createPartButton);
		springLayout.putConstraint(SpringLayout.SOUTH, separator, -182, SpringLayout.SOUTH, swingPanel);
		springLayout.putConstraint(SpringLayout.WEST, separator, 0, SpringLayout.WEST, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, separator, 0, SpringLayout.EAST, swingPanel);
		swingPanel.add(separator);
		
		JLabel lblName = new JLabel("Name:");
		lblName.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.EAST, lblName, 0, SpringLayout.EAST, lblNewLabel);
		swingPanel.add(lblName);
		
		editNameTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, lblName, 3, SpringLayout.NORTH, editNameTextField);
		springLayout.putConstraint(SpringLayout.NORTH, editNameTextField, 21, SpringLayout.SOUTH, partToEditChooser);
		springLayout.putConstraint(SpringLayout.WEST, editNameTextField, 0, SpringLayout.WEST, newNameTextField);
		springLayout.putConstraint(SpringLayout.EAST, editNameTextField, 0, SpringLayout.EAST, newNameTextField);
		editNameTextField.setColumns(10);
		swingPanel.add(editNameTextField);
		
		JLabel lblNumber_1 = new JLabel("Number:");
		lblNumber_1.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.NORTH, lblNumber_1, 0, SpringLayout.NORTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblNumber_1, 38, SpringLayout.EAST, editNameTextField);
		springLayout.putConstraint(SpringLayout.EAST, lblNumber_1, 0, SpringLayout.EAST, lblPart);
		swingPanel.add(lblNumber_1);
		
		editNumberTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.NORTH, editNumberTextField, -3, SpringLayout.NORTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, editNumberTextField, 0, SpringLayout.WEST, newNumberTextField);
		editNumberTextField.setColumns(10);
		swingPanel.add(editNumberTextField);
		editNumberTextField.addKeyListener(this);
		
		JLabel lblDescription_1 = new JLabel("Description:");
		lblDescription_1.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.NORTH, lblDescription_1, 29, SpringLayout.SOUTH, lblName);
		springLayout.putConstraint(SpringLayout.WEST, lblDescription_1, 0, SpringLayout.WEST, lblDescription);
		swingPanel.add(lblDescription_1);
		
		editDescriptionTextField = new JTextField();
		springLayout.putConstraint(SpringLayout.WEST, editDescriptionTextField, 6, SpringLayout.EAST, lblDescription_1);
		springLayout.putConstraint(SpringLayout.SOUTH, editDescriptionTextField, -60, SpringLayout.SOUTH, swingPanel);
		springLayout.putConstraint(SpringLayout.EAST, editDescriptionTextField, 0, SpringLayout.EAST, newNumberTextField);
		editDescriptionTextField.setColumns(10);
		swingPanel.add(editDescriptionTextField);
		
		JLabel lblImage = new JLabel("Image:");
		lblImage.setForeground(Color.BLACK);
		springLayout.putConstraint(SpringLayout.NORTH, lblImage, 20, SpringLayout.SOUTH, lblDescription_1);
		springLayout.putConstraint(SpringLayout.WEST, lblImage, 0, SpringLayout.WEST, lblDescription);
		swingPanel.add(lblImage);
		
		editImageChooser = new JComboBox(imageNames);
		springLayout.putConstraint(SpringLayout.NORTH, editImageChooser, -3, SpringLayout.NORTH, lblImage);
		springLayout.putConstraint(SpringLayout.WEST, editImageChooser, 6, SpringLayout.EAST, lblImage);
		editImageChooser.addActionListener(this);
		swingPanel.add(editImageChooser);
		
		editCurrentImage = new JLabel();
		editCurrentImage.setForeground(Color.WHITE);
		springLayout.putConstraint(SpringLayout.EAST, editImageChooser, -12, SpringLayout.WEST, editCurrentImage);
		springLayout.putConstraint(SpringLayout.NORTH, editCurrentImage, 0, SpringLayout.NORTH, lblImage);
		springLayout.putConstraint(SpringLayout.EAST, editCurrentImage, 0, SpringLayout.EAST, newCurrentImage);
		editCurrentImage.setMinimumSize(PART_DIMENSION);
		editCurrentImage.setMaximumSize(PART_DIMENSION);
		swingPanel.add(editCurrentImage);
		
		confirmChangeButton = new JButton("Confirm Change");
		springLayout.putConstraint(SpringLayout.NORTH, confirmChangeButton, -4, SpringLayout.NORTH, lblImage);
		springLayout.putConstraint(SpringLayout.EAST, confirmChangeButton, 0, SpringLayout.EAST, deletePartButton);
		confirmChangeButton.addActionListener(this);
		swingPanel.add(confirmChangeButton);
		
		ImageIcon back = new ImageIcon("src/images/partBackground.png");
		JLabel background = new JLabel(back);
		background.setSize(500,400);
		swingPanel.add(background);
		
		pack();
		
		initializingCount=0;
		myGUITimer.restart();
		initializing=false;
	}
	
	//Main method
	public static void main(String[] args) {
		PartsManager pm = new PartsManager();
		pm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		pm.setVisible(true);
		pm.pack();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void updateImageChooser() {
		newImageChooser = new JComboBox (imageNames);
		//add Action Listener to update showing image
		newImageChooser.addActionListener(this);
	}
	
	//updates JLabels to display currently selected images
	//String s = filename part number
	private void updateLabel(JLabel jl, String s) {
		ImageIcon icon = new ImageIcon("src/images/part"+ s + ".png");
		jl.setIcon(icon);
		jl.setToolTipText("An image of a/an" + s + " part");
	}
	
	public void actionPerformed(ActionEvent ae) {
		//each possible source has its own method for actionPerformed
		//naming convention: nameOfSourceAction(ActionEvent e)
		if (ae.getSource()==newImageChooser) {
			//Updates the image of newCurrentImage Label to display
			//the currently selected image
			ImageChooserAction(ae);
		}
		else if (ae.getSource()==createPartButton) {
			//check that info is entered into all fields
			//add part to server's list of parts if entered
			createPartButtonAction();
		}
		else if (ae.getSource()==deletePartButton) {
			//delete part from server's list of parts
			deletePartButtonAction();
		}
		else if (ae.getSource()==partToEditChooser) {
			//update textfields when part is chosen
			partToEditChooserAction();
		}
		else if (ae.getSource()==editImageChooser) {
			//update shown image for editing image of part
			//on editCurrentImage Label
			ImageChooserAction(ae);
		}
		else if (ae.getSource()==confirmChangeButton) {
			//send a delete part and then a
			//add part to server in order to update all
			//of the info for the edditted part
			confirmChangeButtonAction();
		}
		//note: timer is located in Manager class, which
		//PartsManager extends
		else if (ae.getSource()==myGUITimer) {
			//send message to server to get parts
			//gets back parts info and update combo box
			//--through the setPossibleParts method which is
			//called by SendPossiblePartsMessage
			sendMessage(new GetPossiblePartsMessage());
			
			//for first 1 timer call(s) after Constructor:
			//call the method to update the 
			//text fields associated with editting parts
			//this is to allow time for server to send message to PM
			if (initializingCount < 1) {
				partToEditChooserAction();
				initializingCount++;
			}
		}
		else if (ae.getSource()==exitMenuButton) {
			sendMessage(new ClientDoneMessage());
			//dispose calls windowClosed
			this.dispose();
		}
    }
	
	//Updates the image of newCurrentImage Label to display
	//the currently selected image
	@SuppressWarnings("rawtypes")
	private void ImageChooserAction(ActionEvent e) {
		JComboBox cb = (JComboBox)e.getSource();
		String imageName = (String)cb.getSelectedItem();
		if (e.getSource()==newImageChooser) {
			updateLabel(newCurrentImage, getImageFileNumberFromName(imageName));
		}
		else if (e.getSource()==editImageChooser) {
			updateLabel(editCurrentImage, getImageFileNumberFromName(imageName));
		}
	}
	
	//check that info is entered into all fields
	//add part to server's list of parts if entered
	private void createPartButtonAction() {
		//check if textfields are full
		if (newNameTextField.getText().isEmpty() || 
			newNumberTextField.getText().isEmpty() || 
			newDescriptionTextField.getText().isEmpty()) {
			JOptionPane.showMessageDialog(this,
			    "You must enter info for all the fields!",
			    "Part Creation Error",
			    JOptionPane.ERROR_MESSAGE);
		}
		//else if all info is there,
		else {
			//sends message to server with the newly created
			//blueprint part IF a part with that name does not already exist
			if (!partWithNameExists(newNameTextField.getText())) {
				sendMessage(new AddPartMessage(
							new BlueprintPart(newNameTextField.getText(),
											  newNumberTextField.getText(),
											  newDescriptionTextField.getText(),
											  getPartFilenameFromName((String)newImageChooser.getSelectedItem()),
											  (String)newImageChooser.getSelectedItem() )));
				//clear all textfields
				newNameTextField.setText("");
				newNumberTextField.setText("");
				newDescriptionTextField.setText("");
				newImageChooser.setSelectedIndex(0);
			}
			else {
				JOptionPane.showMessageDialog(this,
					    "A Part with this name already exists.",
					    "Part Creation Error",
					    JOptionPane.ERROR_MESSAGE);
			}
		}
		
	}
	
	//search possible parts for a part with the
	//specified name, return true if one exists,
	//false if not
	private boolean partWithNameExists(String name) {
		for (int i=0; i<possibleParts.size(); i++) {
			if (possibleParts.get(i).getName().equals(name)) {
				return true;
			}
		}
		
		//if no part with name exists:
		return false;
	}
	
	//delete part from server's list of parts
	//get currently selected part to delete
	//if at least 2 parts
	private void deletePartButtonAction() {
		if (partToEditChooser.getItemCount()>1) {
			sendMessage(new DeletePartMessage(getBlueprintPartWithName((String)partToEditChooser.getSelectedItem())));
		}
	}
	
	//update necessary textfields when different part is chosen
	private void partToEditChooserAction() {
		
		//check for null pointer exceptions
		if (partToEditChooser==null) {
			return;
		}
		
		//get part to update info on screen
		BlueprintPart part = getBlueprintPartWithName((String)partToEditChooser.getSelectedItem());
		
		//check for null pointer exceptions
		if (part==null) {
			return;
		}
		if (editNameTextField==null ||
			editNumberTextField==null ||
			editDescriptionTextField==null ||
			editImageChooser==null ||
			editCurrentImage==null) {
			return;
		}
		
		//update text fields with correct info
		editNameTextField.setText(part.getName());
		editNumberTextField.setText(part.getNumber());
		editDescriptionTextField.setText(part.getDescription());
		
		//update selected image and shown (current) image
		editImageChooser.setSelectedItem(part.getImageType());
		
		ImageIcon icon = new ImageIcon(part.getImageName());
		editCurrentImage.setIcon(icon);
		//editCurrentImage.setToolTipText(part.getImageName());
		editCurrentImage.setIcon(icon);
	}
	
	private void confirmChangeButtonAction() {
		//check if textfields are full
		if (editNameTextField.getText().isEmpty() || 
			editNumberTextField.getText().isEmpty() || 
			editDescriptionTextField.getText().isEmpty()) {
			
			JOptionPane.showMessageDialog(this,
			    "You must enter info for all the fields!",
			    "Part Edit Error",
			    JOptionPane.ERROR_MESSAGE);
		}
		//else if all info is there, and it is new info
		else {
			//check if info is the same!
			BlueprintPart oldPart = getBlueprintPartWithName((String)partToEditChooser.getSelectedItem());
			
			//if old part has all the same info, do nothing
			if(oldPart.equals(new BlueprintPart(editNameTextField.getText(), 
												editNumberTextField.getText(), 
												editDescriptionTextField.getText(), 
												getPartFilenameFromName((String)editImageChooser.getSelectedItem()), 
												(String)editImageChooser.getSelectedItem() ))) {
				JOptionPane.showMessageDialog(this,
					    "You didn't change anything!",
					    "Part Edit Error",
					    JOptionPane.WARNING_MESSAGE);
				return;
			}
			
			int numberOfItems = partToEditChooser.getItemCount();
			
			//delete part, then re-add with new info
			//delete old part
			sendMessage(new DeletePartMessage(oldPart));
			
			//add new part with info:
			//sends message to server with the newly editted
			//blue print part
			sendMessage(new AddPartMessage(
						new BlueprintPart(editNameTextField.getText(),
										  editNumberTextField.getText(),
										  editDescriptionTextField.getText(),
										  getPartFilenameFromName((String)editImageChooser.getSelectedItem()),
										  (String)editImageChooser.getSelectedItem() )));
			//after changing part, set the selected
			//object in the comboBox to the newly
			//editted part
		}
	}
	
	//stops user from entering a character that is not a digit
    public void keyTyped(KeyEvent e) {  
        char c = e.getKeyChar();  
        if (!(Character.isDigit(c) ||  (c == KeyEvent.VK_BACK_SPACE) ||  
            (c == KeyEvent.VK_DELETE))) {
            	e.consume();  
        	}
    }
    
    //these methods must be declared to implement KeyListener, but are not used
    public void keyReleased(KeyEvent e) { }
    public void keyPressed(KeyEvent e) { }
    
    // Set the possible parts after getting a message from server.
	public void setPossibleParts(ArrayList<BlueprintPart> parts) {
		possibleParts = parts;
		//now update JComboBox with necessary info
		updatePartToEditChooser();
	}
	
	//update the partToEditChooser after possible parts
	//list is updated from server
	@SuppressWarnings({ "unchecked" })
	private void updatePartToEditChooser() {
		String[] possiblePartNames = getPossiblePartNameArray();
		
		//if number of items in combo box is MORE than possible part names,
		//DELETE correct Item after constructor has ended
		//(only if at least 2 parts)
		if (partToEditChooser.getItemCount() > possiblePartNames.length &&
			!initializing &&
			partToEditChooser.getItemCount()>1) {
			
			//System.out.println("partToEditChooser.getItemCount(): "+partToEditChooser.getItemCount());
			//System.out.println("possiblePartNames.length: "+possiblePartNames.length);
			int i=0;
			for (i=0; i<possiblePartNames.length; i++) {
				//System.out.println("i = "+i);
				if(!possiblePartNames[i].equals(partToEditChooser.getItemAt(i))) {
					partToEditChooser.removeItemAt(i);
					//System.out.println("DELETED");
					break;
				}
			}
			//if run through all of possibleParts,
			//and nothing was deleted, then
			//last one  must be deleted:
			System.out.println("HERE, and i ="+i);
			if (i==possiblePartNames.length) {
				partToEditChooser.removeItemAt(i);
			}
			
		}
		
		//if number of items in combo box is LESS than possible part names,
		//ADD items to bottom, even if in constructor (initializing),
		//add items to combo box
		if (partToEditChooser.getItemCount() < possiblePartNames.length) {
			for (int i=partToEditChooser.getItemCount(); i<possiblePartNames.length; i++) {
				partToEditChooser.addItem(possiblePartNames[i]);
			}
			partToEditChooser.setSelectedIndex(partToEditChooser.getItemCount()-1);
		}
		
		//else if correct # in each, EDIT part
		//if NOT in constructor, if necessary
		if (!initializing && partToEditChooser.getItemCount() == possiblePartNames.length) {
			int stop = possiblePartNames.length;
			for (int i=0; i < stop; i++ ) {
				//check if names are different
				if (!possiblePartNames[i].equals(partToEditChooser.getItemAt(i))) {
					partToEditChooser.removeItemAt(i);
					partToEditChooser.addItem(possiblePartNames[i]);
					partToEditChooser.setSelectedIndex(stop-1);
				}
			}
		}
		
	}
	
	//return BlueprintPart with specified name
	//from the possibleParts ArrayList
	private BlueprintPart getBlueprintPartWithName(String name) {
		for (int i=0; i<possibleParts.size(); i++) {
			if (possibleParts.get(i).getName().equals(name)) {
				return possibleParts.get(i);
			}
		}
		
		return new BlueprintPart("ERROR","NO PART","WITH SPECIFIED NAME","","");
	}
	
	/*
	 * Method that takes the name of the part type
	 * and returns the number (as a String) for that part.
	 * Used to create the image name.
	 */
	public static String getImageFileNumberFromName(final String s) {
		for (int i=0; i<imageNames.length; i++) {
			if(s.equals(imageNames[i])) {
				return String.valueOf(i+1);
			}
		}
		//if String is not found
		return "NOT FOUND";
	}
	
	private String getPartFilenameFromName(final String name) {
		return "src/images/part"+ getImageFileNumberFromName(name) + ".png";
	}
	
	//returns and array of all possible parts, but not including
	//the "None" part used by the Kit Manager
	private String[] getPossiblePartNameArray() {
		//possiblePartNames is created with 1 less entry than
		//the number of possible parts because the 1st (0th) entry
		//is the none part
		String[] possiblePartNames = new String[possibleParts.size()-1];
		
		//go through list of possible parts 
		//for loop begins at 1 rather than 0 in order
		//to skip the None part, ends at 1 less than size
		//of the arraylist
		for(int i=1; i<possibleParts.size(); i++) {
			//i-1 starts at 0
			possiblePartNames[i-1] = possibleParts.get(i).getName();
		}
		
		return possiblePartNames;
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		sendMessage(new ClientDoneMessage());
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
	
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}
	
}
//created by Michael Bock
