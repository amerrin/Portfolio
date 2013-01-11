package ServerClient;

import java.awt.Color;

import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.BoxLayout;
import javax.swing.SpringLayout;
import javax.swing.JTabbedPane;
import javax.swing.JLabel;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;

import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.MutableComboBoxModel;

public class KitManager extends Manager implements KeyListener, WindowListener {

	private JPanel contentPane;
	private JTextField nameField;
	private JTextField chgNameField;
	JMenuItem mntmExit;
	JComboBox part1List;
	JComboBox part2List;
	JComboBox part3List;
	JComboBox part4List;
	JComboBox part5List;
	JComboBox part6List;
	JComboBox part7List;
	JComboBox part8List;
	JMenu file;
	JMenuBar menuBar;
	JLabel title;
	ArrayList<JComboBox> partLists;
	ArrayList<JComboBox> chgPartLists;
	JButton btnAddKit;
	JButton btnDeleteKit;
	JButton btnChangeKit;
	JComboBox chgPart1List;
	JComboBox chgPart2List;
	JComboBox chgPart3List;
	JComboBox chgPart4List;
	JComboBox chgPart5List;
	JComboBox chgPart6List;
	JComboBox chgPart7List;
	JComboBox chgPart8List;
	JComboBox kitChangeList;
	JComboBox deleteList;
	//Things needed to build a Blueprint kit.
	ArrayList<BlueprintPart> kitParts;
	String kitName;
	String changeName;
	boolean wasModified;
	int wasModifiedCounter;
	boolean firstModification;
		
	//Server reference
	ArrayList<BlueprintPart> possibleParts;
	ArrayList<BlueprintKit> possibleKits;
		
	//Parts to store from user's selections
	ArrayList<BlueprintPart> selectedParts; 
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		KitManager mg = new KitManager();
		mg.setVisible(true);
	}

	/**
	 * Create the frame.
	 */
	public KitManager() {
		
		setTitle("Kit Manager");
		boolean firstModification = false;
		
		setBounds(100, 100, 592, 450);
		addWindowListener(this);
		
		setBackground(Color.LIGHT_GRAY);
		setForeground(Color.WHITE);
		setResizable(false);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnFile = new JMenu("File");
		menuBar.add(mnFile);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(this);
		mnFile.add(mntmExit);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.X_AXIS));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		contentPane.add(tabbedPane);
		
		JPanel panel = new JPanel();
		tabbedPane.addTab("Add Kit", null, panel, null);
		SpringLayout sl_panel = new SpringLayout();
		panel.setLayout(sl_panel);
		panel.setBackground(Color.LIGHT_GRAY);
		panel.setForeground(Color.WHITE);
		
		JLabel addKitTitle = new JLabel("Add Kit");
		sl_panel.putConstraint(SpringLayout.NORTH, addKitTitle, 10, SpringLayout.NORTH, panel);
		sl_panel.putConstraint(SpringLayout.WEST, addKitTitle, 250, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, addKitTitle, 319, SpringLayout.WEST, panel);
		addKitTitle.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel.add(addKitTitle);
		
		part1List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part1List, 73, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part1List, -402, SpringLayout.EAST, panel);
		panel.add(part1List);
		
		JLabel part1 = new JLabel("Part 1:");
		sl_panel.putConstraint(SpringLayout.NORTH, part1, 3, SpringLayout.NORTH, part1List);
		sl_panel.putConstraint(SpringLayout.EAST, part1, -502, SpringLayout.EAST, panel);
		panel.add(part1);
		
		part2List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part2List, 73, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part2List, -402, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, part1List, -28, SpringLayout.NORTH, part2List);
		panel.add(part2List);
		
		JLabel part2 = new JLabel("Part 2:");
		sl_panel.putConstraint(SpringLayout.NORTH, part2, 3, SpringLayout.NORTH, part2List);
		sl_panel.putConstraint(SpringLayout.EAST, part2, 0, SpringLayout.EAST, part1);
		panel.add(part2);
		
		part3List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part3List, 73, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part3List, -402, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, part2List, -31, SpringLayout.NORTH, part3List);
		panel.add(part3List);
		
		JLabel part3 = new JLabel("Part 3:");
		sl_panel.putConstraint(SpringLayout.NORTH, part3, 3, SpringLayout.NORTH, part3List);
		sl_panel.putConstraint(SpringLayout.EAST, part3, 0, SpringLayout.EAST, part1);
		panel.add(part3);
		
		part4List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part4List, 73, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part4List, -402, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.SOUTH, part3List, -32, SpringLayout.NORTH, part4List);
		sl_panel.putConstraint(SpringLayout.NORTH, part4List, 214, SpringLayout.NORTH, panel);
		panel.add(part4List);
		
		JLabel part4 = new JLabel("Part 4:");
		sl_panel.putConstraint(SpringLayout.NORTH, part4, 3, SpringLayout.NORTH, part4List);
		sl_panel.putConstraint(SpringLayout.EAST, part4, 0, SpringLayout.EAST, part1);
		panel.add(part4);
		
		JLabel part5 = new JLabel("Part 5:");
		sl_panel.putConstraint(SpringLayout.NORTH, part5, 3, SpringLayout.NORTH, part1List);
		panel.add(part5);
		
		part5List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part5List, 348, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part5List, -127, SpringLayout.EAST, panel);
		sl_panel.putConstraint(SpringLayout.EAST, part5, -6, SpringLayout.WEST, part5List);
		sl_panel.putConstraint(SpringLayout.NORTH, part5List, 0, SpringLayout.NORTH, part1List);
		panel.add(part5List);
		
		part6List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part6List, 0, SpringLayout.WEST, part5List);
		sl_panel.putConstraint(SpringLayout.SOUTH, part6List, 0, SpringLayout.SOUTH, part2List);
		sl_panel.putConstraint(SpringLayout.EAST, part6List, 0, SpringLayout.EAST, part5List);
		panel.add(part6List);
		
		JLabel part6 = new JLabel("Part 6:");
		sl_panel.putConstraint(SpringLayout.NORTH, part6, 3, SpringLayout.NORTH, part2List);
		sl_panel.putConstraint(SpringLayout.WEST, part6, 0, SpringLayout.WEST, part5);
		panel.add(part6);
		
		part7List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part7List, 0, SpringLayout.WEST, part5List);
		sl_panel.putConstraint(SpringLayout.SOUTH, part7List, 0, SpringLayout.SOUTH, part3List);
		sl_panel.putConstraint(SpringLayout.EAST, part7List, 0, SpringLayout.EAST, part5List);
		panel.add(part7List);
		
		JLabel part7 = new JLabel("Part 7:");
		sl_panel.putConstraint(SpringLayout.NORTH, part7, 3, SpringLayout.NORTH, part3List);
		sl_panel.putConstraint(SpringLayout.WEST, part7, 0, SpringLayout.WEST, part5);
		panel.add(part7);
		
		part8List = new JComboBox();
		sl_panel.putConstraint(SpringLayout.WEST, part8List, 0, SpringLayout.WEST, part5List);
		sl_panel.putConstraint(SpringLayout.SOUTH, part8List, 0, SpringLayout.SOUTH, part4List);
		sl_panel.putConstraint(SpringLayout.EAST, part8List, 0, SpringLayout.EAST, part5List);
		panel.add(part8List);
		
		JLabel part8 = new JLabel("Part 8:");
		sl_panel.putConstraint(SpringLayout.NORTH, part8, 3, SpringLayout.NORTH, part4List);
		sl_panel.putConstraint(SpringLayout.WEST, part8, 0, SpringLayout.WEST, part5);
		panel.add(part8);
		
		nameField = new JTextField();
		sl_panel.putConstraint(SpringLayout.WEST, nameField, 230, SpringLayout.WEST, panel);
		panel.add(nameField);
		nameField.setColumns(10);
		
		JLabel lblKitName = new JLabel("Kit Name:");
		sl_panel.putConstraint(SpringLayout.NORTH, lblKitName, 3, SpringLayout.NORTH, nameField);
		sl_panel.putConstraint(SpringLayout.EAST, lblKitName, -6, SpringLayout.WEST, nameField);
		panel.add(lblKitName);
		
		btnAddKit = new JButton("Add Kit");
		sl_panel.putConstraint(SpringLayout.EAST, nameField, -9, SpringLayout.WEST, btnAddKit);
		sl_panel.putConstraint(SpringLayout.WEST, btnAddKit, 342, SpringLayout.WEST, panel);
		sl_panel.putConstraint(SpringLayout.NORTH, nameField, 1, SpringLayout.NORTH, btnAddKit);
		sl_panel.putConstraint(SpringLayout.SOUTH, btnAddKit, -55, SpringLayout.SOUTH, panel);
		panel.add(btnAddKit);
		btnAddKit.addActionListener(this);
		
		JPanel panel_2 = new JPanel();
		tabbedPane.addTab("Change Kit", null, panel_2, null);
		SpringLayout sl_panel_2 = new SpringLayout();
		panel_2.setLayout(sl_panel_2);
		panel_2.setBackground(Color.LIGHT_GRAY);
		panel_2.setForeground(Color.WHITE);
		
		JPanel panel_3 = new JPanel();
		sl_panel_2.putConstraint(SpringLayout.NORTH, panel_3, 5, SpringLayout.NORTH, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, panel_3, 284, SpringLayout.WEST, panel_2);
		panel_2.add(panel_3);
		panel_3.setLayout(new SpringLayout());
		panel_3.setBackground(Color.LIGHT_GRAY);
		panel_3.setForeground(Color.WHITE);
		
		JLabel changeTitle = new JLabel("Change Kit");
		sl_panel_2.putConstraint(SpringLayout.NORTH, changeTitle, 6, SpringLayout.SOUTH, panel_3);
		sl_panel_2.putConstraint(SpringLayout.WEST, changeTitle, 255, SpringLayout.WEST, panel_2);
		changeTitle.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_2.add(changeTitle);
		
		chgPart1List = new JComboBox();
		panel_2.add(chgPart1List);
		
		JLabel label_1 = new JLabel("Part 1:");
		sl_panel_2.putConstraint(SpringLayout.NORTH, chgPart1List, -3, SpringLayout.NORTH, label_1);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart1List, 6, SpringLayout.EAST, label_1);
		panel_2.add(label_1);
		
		JLabel label_2 = new JLabel("Part 2:");
		sl_panel_2.putConstraint(SpringLayout.SOUTH, label_1, -34, SpringLayout.NORTH, label_2);
		sl_panel_2.putConstraint(SpringLayout.EAST, label_2, -441, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, label_1, 0, SpringLayout.WEST, label_2);
		panel_2.add(label_2);
		
		chgPart8List = new JComboBox();
		panel_2.add(chgPart8List);
		
		JLabel label_3 = new JLabel("Part 3:");
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_3, 181, SpringLayout.NORTH, panel_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, label_2, -26, SpringLayout.NORTH, label_3);
		sl_panel_2.putConstraint(SpringLayout.WEST, label_3, 0, SpringLayout.WEST, label_1);
		panel_2.add(label_3);
		
		chgPart3List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart1List, 0, SpringLayout.EAST, chgPart3List);
		sl_panel_2.putConstraint(SpringLayout.NORTH, chgPart3List, -3, SpringLayout.NORTH, label_3);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart3List, 6, SpringLayout.EAST, label_3);
		panel_2.add(chgPart3List);
		
		JLabel label_4 = new JLabel("Part 4:");
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_4, 32, SpringLayout.SOUTH, label_3);
		sl_panel_2.putConstraint(SpringLayout.NORTH, chgPart8List, -3, SpringLayout.NORTH, label_4);
		sl_panel_2.putConstraint(SpringLayout.WEST, label_4, 0, SpringLayout.WEST, label_1);
		panel_2.add(label_4);
		
		chgPart4List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart4List, 6, SpringLayout.EAST, label_4);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, chgPart4List, 0, SpringLayout.SOUTH, label_4);
		panel_2.add(chgPart4List);
		
		JLabel label_5 = new JLabel("Kit Name:");
		panel_2.add(label_5);
		
		chgNameField = new JTextField();
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_5, 3, SpringLayout.NORTH, chgNameField);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgNameField, 228, SpringLayout.WEST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, chgNameField, -34, SpringLayout.SOUTH, panel_2);
		chgNameField.setColumns(10);
		panel_2.add(chgNameField);
		
		btnChangeKit = new JButton("Change Kit");
		sl_panel_2.putConstraint(SpringLayout.NORTH, btnChangeKit, -4, SpringLayout.NORTH, label_5);
		sl_panel_2.putConstraint(SpringLayout.WEST, btnChangeKit, 5, SpringLayout.EAST, chgNameField);
		panel_2.add(btnChangeKit);
		btnChangeKit.addActionListener(this);
		
		chgPart7List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart8List, 0, SpringLayout.EAST, chgPart7List);
		sl_panel_2.putConstraint(SpringLayout.NORTH, chgPart7List, -3, SpringLayout.NORTH, label_3);
		panel_2.add(chgPart7List);
		
		JLabel label_6 = new JLabel("Part 8:");
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart4List, -62, SpringLayout.WEST, label_6);
		sl_panel_2.putConstraint(SpringLayout.EAST, label_6, -246, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart8List, 6, SpringLayout.EAST, label_6);
		panel_2.add(label_6);
		
		JLabel label_7 = new JLabel("Part 7:");
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart3List, -61, SpringLayout.WEST, label_7);
		sl_panel_2.putConstraint(SpringLayout.EAST, label_7, -247, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_6, 32, SpringLayout.SOUTH, label_7);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart7List, 7, SpringLayout.EAST, label_7);
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_7, 0, SpringLayout.NORTH, label_3);
		panel_2.add(label_7);
		
		chgPart5List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart7List, 0, SpringLayout.EAST, chgPart5List);
		panel_2.add(chgPart5List);
		
		chgPart6List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.SOUTH, chgPart5List, -28, SpringLayout.NORTH, chgPart6List);
		sl_panel_2.putConstraint(SpringLayout.NORTH, chgPart6List, -3, SpringLayout.NORTH, label_2);
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart6List, -146, SpringLayout.EAST, panel_2);
		panel_2.add(chgPart6List);
		
		JLabel label_8 = new JLabel("Part 6:");
		sl_panel_2.putConstraint(SpringLayout.EAST, label_8, -247, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart6List, 7, SpringLayout.EAST, label_8);
		sl_panel_2.putConstraint(SpringLayout.NORTH, label_8, 0, SpringLayout.NORTH, label_2);
		panel_2.add(label_8);
		
		JLabel label_9 = new JLabel("Part 5:");
		sl_panel_2.putConstraint(SpringLayout.SOUTH, label_9, -34, SpringLayout.NORTH, label_8);
		sl_panel_2.putConstraint(SpringLayout.EAST, label_9, -247, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart5List, 7, SpringLayout.EAST, label_9);
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart5List, 101, SpringLayout.EAST, label_9);
		panel_2.add(label_9);
		
		chgPart2List = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.WEST, chgPart2List, 6, SpringLayout.EAST, label_2);
		sl_panel_2.putConstraint(SpringLayout.SOUTH, chgPart2List, 0, SpringLayout.SOUTH, label_2);
		sl_panel_2.putConstraint(SpringLayout.EAST, chgPart2List, -61, SpringLayout.WEST, label_8);
		panel_2.add(chgPart2List);
		
		kitChangeList = new JComboBox();
		sl_panel_2.putConstraint(SpringLayout.NORTH, kitChangeList, 16, SpringLayout.SOUTH, changeTitle);
		sl_panel_2.putConstraint(SpringLayout.EAST, kitChangeList, 17, SpringLayout.EAST, changeTitle);
		kitChangeList.addActionListener(this);
		panel_2.add(kitChangeList);
		
		JLabel lblKitToChange = new JLabel("Kit to Change:");
		sl_panel_2.putConstraint(SpringLayout.WEST, label_5, 0, SpringLayout.WEST, lblKitToChange);
		sl_panel_2.putConstraint(SpringLayout.NORTH, lblKitToChange, 52, SpringLayout.NORTH, panel_2);
		sl_panel_2.putConstraint(SpringLayout.EAST, lblKitToChange, -320, SpringLayout.EAST, panel_2);
		sl_panel_2.putConstraint(SpringLayout.WEST, kitChangeList, 6, SpringLayout.EAST, lblKitToChange);
		panel_2.add(lblKitToChange);
		
		JPanel panel_1 = new JPanel();
		tabbedPane.addTab("Delete Kit", null, panel_1, null);
		SpringLayout sl_panel_1 = new SpringLayout();
		panel_1.setLayout(sl_panel_1);
		panel_1.setBackground(Color.LIGHT_GRAY);
		panel_1.setForeground(Color.WHITE);
		
		deleteList = new JComboBox();
		panel_1.add(deleteList);
		
		JLabel deleteKitTitle = new JLabel("Delete a Kit");
		sl_panel_1.putConstraint(SpringLayout.NORTH, deleteList, 55, SpringLayout.SOUTH, deleteKitTitle);
		sl_panel_1.putConstraint(SpringLayout.EAST, deleteList, 22, SpringLayout.EAST, deleteKitTitle);
		sl_panel_1.putConstraint(SpringLayout.NORTH, deleteKitTitle, 33, SpringLayout.NORTH, panel_1);
		sl_panel_1.putConstraint(SpringLayout.WEST, deleteKitTitle, 239, SpringLayout.WEST, panel_1);
		deleteKitTitle.setFont(new Font("Tahoma", Font.PLAIN, 18));
		panel_1.add(deleteKitTitle);
		
		JLabel lblKitToDelete = new JLabel("Kit to Delete:");
		sl_panel_1.putConstraint(SpringLayout.NORTH, lblKitToDelete, 113, SpringLayout.NORTH, panel_1);
		sl_panel_1.putConstraint(SpringLayout.EAST, lblKitToDelete, -335, SpringLayout.EAST, panel_1);
		sl_panel_1.putConstraint(SpringLayout.WEST, deleteList, 5, SpringLayout.EAST, lblKitToDelete);
		panel_1.add(lblKitToDelete);
		
		btnDeleteKit = new JButton("Delete Kit");
		sl_panel_1.putConstraint(SpringLayout.NORTH, btnDeleteKit, 45, SpringLayout.SOUTH, deleteList);
		sl_panel_1.putConstraint(SpringLayout.WEST, btnDeleteKit, 240, SpringLayout.WEST, panel_1);
		panel_1.add(btnDeleteKit);
		btnDeleteKit.addActionListener(this);
		
		partLists = new ArrayList<JComboBox>();
		partLists.add(part1List);
		partLists.add(part2List);
		partLists.add(part3List);
		partLists.add(part4List);
		partLists.add(part5List);
		partLists.add(part6List);
		partLists.add(part7List);
		partLists.add(part8List);
		
		chgPartLists = new ArrayList<JComboBox>();
		chgPartLists.add(chgPart1List);
		chgPartLists.add(chgPart2List);
		chgPartLists.add(chgPart3List);
		chgPartLists.add(chgPart4List);
		chgPartLists.add(chgPart5List);
		chgPartLists.add(chgPart6List);
		chgPartLists.add(chgPart7List);
		chgPartLists.add(chgPart8List);
		
		possibleParts = new ArrayList<BlueprintPart>();
		possibleKits = new ArrayList<BlueprintKit>();
		
		//Message the server updated kit list.
		sendMessage(new GetPossiblePartsMessage());
		sendMessage(new GetPossibleKitsMessage());
		System.out.println("Sending Server Message");
		
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}
	
	public void updateLists(){
		updateAddLists();
		updateChangeLists();
	}
	
	public void updateAddLists(){
		String[] parts = new String[possibleParts.size()];
		String[] kits = new String[possibleKits.size()];
		if(!possibleParts.isEmpty()){
			for(int i = 0; i < parts.length; i++){
				parts[i] = possibleParts.get(i).getName();
			}//end for
		}//end if
		for(int i = 0; i < partLists.size(); i++){
			partLists.get(i).removeAllItems();
			for(int k = 0; k < parts.length; k++ ){
			partLists.get(i).addItem(parts[k]);
			}
		}//end for
		if(!possibleKits.isEmpty()){
			for(int k = 0; k < kits.length; k++){
				kits[k] = possibleKits.get(k).getName();
			}//end for
		}//end if
		deleteList.removeAllItems();
		for(int k = 0; k < kits.length; k++ ){
			deleteList.addItem(kits[k]);
		}
	}
	
	public void updateChangeLists(){
		String[] parts = new String[possibleParts.size()];
		String[] kits = new String[possibleKits.size()];
		if(!possibleParts.isEmpty()){
			for(int i = 0; i < parts.length; i++){
				parts[i] = possibleParts.get(i).getName();
			}//end for
		}//end if
		for(int i = 0; i < chgPartLists.size(); i++){
			chgPartLists.get(i).removeAllItems();
			for(int k = 0; k < parts.length; k++) {
				chgPartLists.get(i).addItem(parts[k]);
			}
		}//end for
		if(!possibleKits.isEmpty()){
			for(int k = 0; k < kits.length; k++){
				kits[k] = possibleKits.get(k).getName();
			}//end for
		}//end if
		kitChangeList.removeAllItems();
		for(int k = 0; k < kits.length; k++ ){
			kitChangeList.addItem(kits[k]);
		}
		int stop = kits.length;
		for (int i=0; i < stop; i++ ) {
			//check if names are different
			if (!kits[i].equals((String)kitChangeList.getItemAt(i))) {
				kitChangeList.removeItemAt(i);
				kitChangeList.addItem(possibleParts.get(i).getName());
			}
		}
		if(firstModification)
		kitChangeList.setSelectedIndex(possibleKits.size() - 1);
	}
	
	//update the selected parts on the change kit screen
	public void updateChgKitSelectedParts() {
		if(kitChangeList.getSelectedIndex() != -1){
			BlueprintKit selKit = (BlueprintKit)possibleKits.get(kitChangeList.getSelectedIndex());
			for (int i = 0; i < chgPartLists.size(); i++)
				if(i < selKit.getParts().size())
					chgPartLists.get(i).setSelectedItem(selKit.getParts().get(i).getName());
				else
					chgPartLists.get(i).setSelectedItem("None");
			chgNameField.setText(selKit.getName());
		}
	}
	
	public void actionPerformed(ActionEvent ae){
		if(ae.getSource() == btnAddKit){
			addSelectedKit();
			updateLists();
		} else if(ae.getSource() == btnDeleteKit){
			deleteSelectedKit();
			updateLists();
		} else if(ae.getSource() == kitChangeList){
			//update the selected parts on the change kit screen
			updateChgKitSelectedParts();
		} else if(ae.getSource() == btnChangeKit) {
			saveChangeKitInformation();
			updateLists();
			firstModification = true;
		}
		else if(ae.getSource() == mntmExit){
			System.out.println("Debug: Exiting Manager");
			sendMessage(new ClientDoneMessage());
			//System.exit(0);
			this.dispose();
		} else if (ae.getSource() == myGUITimer) {
			//send message to server to get parts
			//gets back parts info and update combo box
			//--through the setPossibleParts method which is
			//called by SendPossiblePartsMessage
			sendMessage(new GetPossiblePartsMessage());
			sendMessage(new GetPossibleKitsMessage());
		}
	
	}
	
	public void updateListsByTimer(){
		if(possibleParts.size() > partLists.get(0).getItemCount()){
			updateLists();
		}
		if(possibleKits.size() > deleteList.getItemCount()){
			updateLists();
		}
		if(wasModified){
			updateLists();
			wasModifiedCounter++;
			if(wasModifiedCounter == 2){
				wasModified = false;
				wasModifiedCounter = 0;
			}
		}
	}
	
	public BlueprintKit getSelectedParts(ArrayList<JComboBox> passedBoxes ){
		selectedParts = new ArrayList<BlueprintPart>();
		String partName;
		for(JComboBox cb : passedBoxes){
		partName = (String)cb.getSelectedItem();
			for(BlueprintPart p : possibleParts){
				if(partName.equals("None")){
					//Don't do anything
				}
				else if(partName.equals(p.getName())){
					selectedParts.add(p);
				}//end if
			}//end for possibleParts
		}//end for partLists
		try {
			return new BlueprintKit(kitName, selectedParts);
		} catch (Exception e) {
			System.out.println("Tried to add too many NONE parts");
		}
		return null;
	}//end method
	
	public void saveChangeKitInformation(){
		if(kitChangeList.getSelectedIndex() != -1){
			String changedName = chgNameField.getText();
			boolean dupName = false;
			for (BlueprintKit k : possibleKits)
				if (k.getName().equals(changedName))
					dupName = true;
			if (!changedName.isEmpty()) {
				BlueprintKit selKit = (BlueprintKit)possibleKits.get(kitChangeList.getSelectedIndex());
				String oldName = selKit.getName();
				sendMessage(new DeleteKitMessage(selKit));
				selKit = getSelectedParts(chgPartLists);
				if(!dupName){
					selKit.setName(changedName);
					changeName = changedName;
				}
				else{
					selKit.setName(oldName);
					changeName = oldName;
				}
					
				sendMessage(new AddKitMessage(selKit));
				updateLists();
			}
			sendMessage(new GetPossiblePartsMessage());
			sendMessage(new GetPossibleKitsMessage());
			wasModified = true;
			//kitChangeList.setSelectedIndex(possibleKits.size()-1);
		}
	}
	
	public void addSelectedKit(){
		kitName = nameField.getText();
		nameField.setText("");
		boolean dupName = false;
		for (BlueprintKit k : possibleKits)
			if (k.getName().equals(kitName))
				dupName = true;
		if (!dupName && !kitName.isEmpty()) {
			BlueprintKit addedKit = getSelectedParts(partLists);
			if(addedKit != null){
				possibleKits.add(addedKit);
				updateLists();
				sendMessage(new AddKitMessage(addedKit));
			}
		}
	}
	
	public void deleteSelectedKit(){
		if(possibleKits.size() > 1){
		BlueprintKit deletedKit = possibleKits.remove(deleteList.getSelectedIndex());
		sendMessage(new DeleteKitMessage(deletedKit));
		}
	}
	
	//Prevents a user from entering anything except a number in the production number box.
	public void keyTyped(KeyEvent e) {
		char c = e.getKeyChar();  
        if (!(Character.isDigit(c) ||  
        	(c == KeyEvent.VK_BACK_SPACE) ||  
        	(c == KeyEvent.VK_DELETE))) {
        	e.consume();  
        }
	}
	
	//Necessary for KeyListener implementation, but don't do anything.
	public void keyPressed(KeyEvent arg0) {
	}

	public void keyReleased(KeyEvent arg0) {
	}
	
	/* Set possible parts when get a message from server. */
	public void setPossibleParts(ArrayList<BlueprintPart> parts) {
		possibleParts = parts;
		updateListsByTimer();
	}
	
	/* Set possible kits when get a message from server. */
	public void setPossibleKits(ArrayList<BlueprintKit> kits) {
		possibleKits = kits;
		updateListsByTimer();
	}
	
	//All the WindowBuilder methods that need to be implemented.
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


