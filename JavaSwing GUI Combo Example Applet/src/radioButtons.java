import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class radioButtons extends JPanel implements ActionListener{
	
	//boolean arrays used to control which paint function (inside colorPanel.java) is allowed to print to screen. There are 3 booleans respectively: oval, rectangle, rounded rectangle; thus, these arrays are of 3 bools
	private boolean[] rectangle = {false,true,false};
	private boolean[] oval = {true,false,false};
	private boolean[] rectangleround = {false,false,true};
	
	//radiobutton constructor created using template so code may be reused for second radiobutton set
	public radioButtons(final String[] buttonNames){
		JRadioButton firstButton = new JRadioButton(buttonNames[0],true);
		firstButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[0]);
				colorPanel.changeShape(rectangle);//function for changing shape based on booleans is called within colorPanel
			}
		});
		//similar to the above, but uses the second string within the array
		JRadioButton secondButton = new JRadioButton(buttonNames[1]);
		secondButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[1]);
				colorPanel.changeShape(oval);//changes shape to oval
				
			}
		});
		
		JRadioButton thirdButton = new JRadioButton(buttonNames[2]);
		thirdButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[2]);
				colorPanel.changeShape(rectangleround);//changes shape to rounded rectangle
			}
		});
		
		//all radiobuttons are added to button group so as to be mutually exclusive
		ButtonGroup buttonList = new ButtonGroup();
		buttonList.add(firstButton);
		buttonList.add(secondButton);
		buttonList.add(thirdButton);
		
		//buttons are added to a separate panel which will then be added to the frame
		JPanel radioPanel = new JPanel(new GridLayout(3,1));
		radioPanel.add(firstButton);
		radioPanel.add(secondButton);
		radioPanel.add(thirdButton);
		radioPanel.setBorder(new TitledBorder(buttonNames[3]));
		
		add(radioPanel);
		}

	public void actionPerformed(ActionEvent e) {
	}
	}
