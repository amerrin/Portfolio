import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class comboPanel extends JPanel{
	public comboPanel(final String[] title){
	//string of colors used for combo boxes
	String[] colors={"Red","Green","Blue","Cyan","Magenta","Orange","Pink","Black"};
	
	//hash table used to store actual java color references according to their respective strings.
	//data is retrieved so colors may be passed to other functions (since strings cannot be passed as colors)
    final Hashtable<String, Color> colorChoices = new Hashtable<String, Color>();
    colorChoices.put("Red",Color.red);
    colorChoices.put("Green",Color.green);
    colorChoices.put("Blue",Color.blue);
    colorChoices.put("Cyan",Color.cyan);
    colorChoices.put("Magenta",Color.magenta);
    colorChoices.put("Orange",Color.orange);
    colorChoices.put("Pink",Color.pink);
    colorChoices.put("Black",Color.black);
    
    //first combo box responsible for handling the color of the background
	JLabel comboLabel = new JLabel("Background color");
	final JComboBox combo = new JComboBox(colors);
	combo.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e) {
		DescriptionPanel.setDescription(title[0] + " set to " + combo.getSelectedItem());//prints to message panel
		colorPanel.changeBackColor(colorChoices.get((String) combo.getSelectedItem()));//hash table key is used to pass color to colorPanel class where background color will be changed
		}
	});
	
	//second combo box responsible for handling color of the shape
	JLabel comboLabel1 = new JLabel("Object color");
	final JComboBox combo1 = new JComboBox(colors);
	combo1.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e) {
		DescriptionPanel.setDescription(title[1] + " set to " + combo1.getSelectedItem());
		colorPanel.changeObjectColor(colorChoices.get((String) combo1.getSelectedItem()));//again, hash table is referenced by key to send color value to function responsible for changing shape color
		}
	});
	
	JLabel comboLabel2 = new JLabel("Text color");
	final JComboBox combo2 = new JComboBox(colors);
	combo2.addActionListener(new ActionListener(){
	public void actionPerformed(ActionEvent e) {
		DescriptionPanel.setDescription(title[2] + " set to " + combo2.getSelectedItem());
		colorPanel.changeTextColor(colorChoices.get((String) combo2.getSelectedItem()));//hash table passes color value to text color handler
		}
	});

	//Panel with gridlayout used to add respective JLabels (titles) to the combo boxes
	JPanel comboPanel = new JPanel();
	comboPanel.setLayout(new GridLayout(3,1));
	comboPanel.add(comboLabel);
	comboPanel.add(combo);
	comboPanel.add(comboLabel1);
	comboPanel.add(combo1);
	comboPanel.add(comboLabel2);
	comboPanel.add(combo2);
	comboPanel.setBorder(new TitledBorder("Color"));
	
	add(comboPanel);
	
}
}