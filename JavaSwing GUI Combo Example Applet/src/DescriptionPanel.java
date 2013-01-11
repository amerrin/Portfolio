import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class DescriptionPanel extends JPanel{
	//message area where all changes will be printed out to
	private static JTextArea messageArea;
	
	public DescriptionPanel(){
		messageArea = new JTextArea("",10,25);
		
		//set message area font to Serif, with size 14 print
		messageArea.setFont(new Font("Serif", Font.PLAIN, 14));
		
		//Scrolpane is created to put message box into so old messages may be read
		JScrollPane scrollPane = new JScrollPane(messageArea);
		scrollPane.setBorder(new TitledBorder("Messages"));
		scrollPane.setBackground(Color.LIGHT_GRAY);
		
		messageArea.setLineWrap(true);
		messageArea.setEditable(false);
		
		setLayout(new BorderLayout(5,5));
		add(scrollPane, BorderLayout.SOUTH);
	}
	//append function for text within message panel. Text is passed into panel, displayed, and new line character is entered last
	public static void setDescription (String text){
		messageArea.append(text + "\n");
	}
}
