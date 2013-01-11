package ServerClient;

import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;

/* LaneManager class views lanes, and manages lane non-normatives for V2. */
public class LaneManager extends GfxManager {
	// constants for the LaneManager's offset from the top left corner of the Factory animation
	static final int xOff = 450, yOff = 0;
	JLabel lblNewLabel;
	JLabel lblMissingPartsIn;
	
	//JRadioButtons and ButtonGroups
	ButtonGroup group1;
	ButtonGroup group2;
	JRadioButton rdbtnRemovePartsFrom;
	JRadioButton rdbtnRemovePartsAndJam;
	JRadioButton rdbtnPutTypes;
	JButton breakType1Button;
	//JButton fixType1Button;
	
	JRadioButton rdbtnTurnAllBad;
	JRadioButton teleportPartsRobot;
	JLabel lblMissing;
	JSeparator separator;
	JRadioButton rdbtnMakePartsPile;
	JRadioButton rdbtnMakePartsUnstable;
	JButton breakType2Button;
	//JButton fixType2Button;
	
	JComboBox chooseLaneType1;
	JComboBox chooseLaneType2;
	
	/* Pass offsets to GfxManager constructor, and set up non-normative panel. */
	public LaneManager() {
		
		super(xOff, yOff);
		setTitle("Lane Manager");
		setSize(600, 750);
		setVisible(true);
		setResizable(false);
		
		swingPanel.setLayout(null);
		
		
		
		
		 lblNewLabel = new JLabel("Nest - Non Normative");
		lblNewLabel.setBounds(214, 37, 172, 22);
		lblNewLabel.setFont(new Font("Tahoma", Font.PLAIN, 18));
		swingPanel.add(lblNewLabel);
		
		lblMissingPartsIn = new JLabel("Missing Parts In Nest - Non Normative");
		lblMissingPartsIn.setBounds(185, 94, 229, 17);
		lblMissingPartsIn.setFont(new Font("Tahoma", Font.PLAIN, 14));
		swingPanel.add(lblMissingPartsIn);
		
		 rdbtnRemovePartsFrom = new JRadioButton("Remove Parts From Nest ");
		rdbtnRemovePartsFrom.setBounds(167, 136, 435, 23);
		
		swingPanel.add(rdbtnRemovePartsFrom);
		rdbtnRemovePartsAndJam = new JRadioButton("Jam Lane");
		rdbtnRemovePartsAndJam.setBounds(178, 162, 413, 23);
		swingPanel.add(rdbtnRemovePartsAndJam);
		
		 rdbtnPutTypes = new JRadioButton("Feeder diverts too slow (associated w/ lane");
		rdbtnPutTypes.setBounds(184, 188, 409, 23);
		swingPanel.add(rdbtnPutTypes);
		
		 breakType1Button = new JButton("BREAK");
		breakType1Button.setBounds(193, 228, 89, 23);
		swingPanel.add(breakType1Button);
		
		/* fixType1Button = new JButton("FIX");
		fixType1Button.setBounds(317, 228, 89, 23);
		swingPanel.add(fixType1Button);*/
		
		 rdbtnTurnAllBad = new JRadioButton("Turn all the Parts Bad");
		rdbtnTurnAllBad.setBounds(235, 330, 200, 23);
		swingPanel.add(rdbtnTurnAllBad);
		
		 lblMissing = new JLabel("No Good Parts in Nest - Non Normative");
		lblMissing.setFont(new Font("Tahoma", Font.PLAIN, 14));
		lblMissing.setBounds(177, 302, 245, 14);
		swingPanel.add(lblMissing);
		
		 separator = new JSeparator();
		separator.setBounds(0, 273, 600, 2);
		swingPanel.add(separator);
		
		 rdbtnMakePartsPile = new JRadioButton("Make the Parts Pile on Top of Each Other");
		rdbtnMakePartsPile.setBounds(178, 356, 480, 23);
		swingPanel.add(rdbtnMakePartsPile);
		
		 rdbtnMakePartsUnstable = new JRadioButton("Make the Parts Unstable");
		rdbtnMakePartsUnstable.setBounds(228, 382, 200, 23);
		swingPanel.add(rdbtnMakePartsUnstable);
		
		teleportPartsRobot = new JRadioButton("Parts Robot in front of camera.");
		teleportPartsRobot.setBounds(180, 410, 250, 23);
		swingPanel.add(teleportPartsRobot);
		
		 breakType2Button = new JButton("BREAK");
		breakType2Button.setBounds(185, 440, 89, 23);
		swingPanel.add(breakType2Button);
		
		/* fixType2Button = new JButton("FIX");
		fixType2Button.setBounds(317, 412, 89, 23);
		swingPanel.add(fixType2Button);*/
		 chooseLaneType1 = new JComboBox();
		 chooseLaneType1.setBounds(317, 228, 100, 25);

			chooseLaneType2 = new JComboBox();
			chooseLaneType2.setBounds(317, 440, 100, 25);
			
		 for (int i = 0; i < 8; i++)
		 {
			 chooseLaneType1.addItem("Lane " + i);
			 chooseLaneType2.addItem("Nest " + i);
			 	 
		 }
		 
		swingPanel.add(chooseLaneType1);
		swingPanel.add(chooseLaneType2);
		
		//create JRadioButton groups
		group1 = new ButtonGroup();
		group1.add(rdbtnRemovePartsFrom);
		//set first radio button as selected as is customary for radit button groups
		rdbtnRemovePartsFrom.setSelected(true);
	    group1.add(rdbtnRemovePartsAndJam);
	    group1.add(rdbtnPutTypes);
	    
	    //same for second group of buttons
	    group2 = new ButtonGroup();
	    group2.add(rdbtnTurnAllBad);
	    rdbtnTurnAllBad.setSelected(true);
	    group2.add(rdbtnMakePartsPile);
	    group2.add(rdbtnMakePartsUnstable);
		group2.add(teleportPartsRobot);
		this.invalidate();
		this.validate();
		//revalidate
		teleportPartsRobot.addActionListener(this);
		 rdbtnRemovePartsFrom.addActionListener(this);
		rdbtnRemovePartsAndJam.addActionListener(this);
		 rdbtnPutTypes.addActionListener(this);
		breakType1Button.addActionListener(this);
		//fixType1Button.addActionListener(this);
		rdbtnTurnAllBad.addActionListener(this);	
		rdbtnMakePartsPile.addActionListener(this);
		rdbtnMakePartsUnstable.addActionListener(this);
		breakType2Button.addActionListener(this);
		//fixType2Button.addActionListener(this);
		
	}
	
	public static void main(String[] args){
		LaneManager lm = new LaneManager();
		//set size of window
		
	}
	
	public void actionPerformed(ActionEvent ae){
		super.actionPerformed(ae);
		
		//break type 1 non norm
		 if (ae.getSource() == breakType1Button) {
			if (rdbtnRemovePartsFrom.isSelected())
				sendMessage(new NonNormativeMessage("Remove Parts From Nest;" + chooseLaneType1.getSelectedIndex()));	
			else if (rdbtnRemovePartsAndJam.isSelected())
				sendMessage(new NonNormativeMessage("Jam Lane;" + chooseLaneType1.getSelectedIndex()));	
			else if (rdbtnPutTypes.isSelected())
				sendMessage(new NonNormativeMessage("Put 2 Types of Parts in the Same Lane;" + chooseLaneType1.getSelectedIndex()));	
		}/*
		 //fix type 1 non norms
		else if (ae.getSource() == fixType1Button) {
			if (rdbtnRemovePartsFrom.isSelected())
				sendMessage(new NonNormativeMessage("Remove Parts From Nest and Place on Lane;fix"));	
			else if (rdbtnRemovePartsAndJam.isSelected())
				sendMessage(new NonNormativeMessage("Remove Parts From Nest and Jam Lane;fix"));	
			else if (rdbtnPutTypes.isSelected())
				sendMessage(new NonNormativeMessage("Put 2 Types of Parts in the Same Lane;fix"));	
		}*/
		 
		 
		 //break type 2 nonnorms
		else if (ae.getSource() == breakType2Button) {
			if (rdbtnTurnAllBad.isSelected())
				sendMessage(new NonNormativeMessage("Turn all the Parts Bad;" + chooseLaneType2.getSelectedIndex()));	
			else if (rdbtnMakePartsPile.isSelected())
				sendMessage(new NonNormativeMessage("Make the Parts Pile on Top of Each Other;" + chooseLaneType2.getSelectedIndex()));	
			else if (rdbtnMakePartsUnstable.isSelected())
				sendMessage(new NonNormativeMessage("Make the Parts Unstable;" + chooseLaneType2.getSelectedIndex()));	
			else if (teleportPartsRobot.isSelected())
				sendMessage(new NonNormativeMessage("Parts Robot In Front Of Camera;" + chooseLaneType2.getSelectedIndex()));	
			
				}
		 /*
		 //fix type 2 non norms
		else if (ae.getSource() == fixType2Button) {
			if (rdbtnTurnAllBad.isSelected())
				sendMessage(new NonNormativeMessage("Turn all the Parts Bad;fix"));	
			else if (rdbtnMakePartsPile.isSelected())
				sendMessage(new NonNormativeMessage("Make the Parts Pile on Top of Each Other;fix"));	
			else if (rdbtnMakePartsUnstable.isSelected())
				sendMessage(new NonNormativeMessage("Make the Parts Unstable;fix"));	
	
		}*/
			 
	}
}
//implemented Yinog Dai, Alex Jones, Michael Bock
