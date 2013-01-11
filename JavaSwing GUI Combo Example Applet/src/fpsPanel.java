import java.awt.*;

import javax.swing.*;
import javax.swing.border.TitledBorder;


public class fpsPanel extends JPanel{
	public fpsPanel (){
		
		this.setBorder(new TitledBorder("Repaint Rate"));
		this.setPreferredSize(new Dimension(350,150));

	}
	
	//simple paint function created to display the refresh rate in a panel, which will then be added to the frame
	public void paintComponent(Graphics g){
		super.paintComponent(g);
        g.setColor(Color.black);
        g.drawString(String.valueOf(colorPanel.rate) + "/sec", 40, 35);

       repaint();
	}


}
