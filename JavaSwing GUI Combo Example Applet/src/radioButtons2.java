import java.awt.GridLayout;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class radioButtons2 extends JPanel implements ActionListener{

	//similar to the first radio button class, this class is created as a replica but with different actionPerformed functions
	//arrays of integers that are passed as {width,height} into colorPanel so that shapes will change size.
	private int[] small = {43,10};
	private int[] medium = {85,20};
	private int[] large = {170,40};
	//boolean arrays used to pass to colorPanel.  Arrays are utilized to change the position of the (x,y) coordinate printout so that it always is centered within the shape
	private boolean[] xsmall = {true,false,false};
	private boolean[] xmedium = {false,true,false};
	private boolean[] xlarge = {false,false,true};
	
	//similar to radioButtons.java, please see code comment there for more information
	public radioButtons2(final String[] buttonNames){
		JRadioButton firstButton = new JRadioButton(buttonNames[0]);
		firstButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[0]);
				colorPanel.changeSize(small,xsmall);//sets shape size to small
			}
		});
		
		JRadioButton secondButton = new JRadioButton(buttonNames[1],true);
		secondButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[1]);
				colorPanel.changeSize(medium,xmedium);//sets shape size to medium
			}
		});
		
		JRadioButton thirdButton = new JRadioButton(buttonNames[2]);
		thirdButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				DescriptionPanel.setDescription(buttonNames[3] + " set to " + buttonNames[2]);
				colorPanel.changeSize(large,xlarge);//sets shape size to large
			}
		});
		
		ButtonGroup buttonList = new ButtonGroup();
		buttonList.add(firstButton);
		buttonList.add(secondButton);
		buttonList.add(thirdButton);
		
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
