import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

//main class, and main frame constructor
public class GuiLabApplet extends JFrame{
	//message panel constructor
	private DescriptionPanel descriptionPanel = new DescriptionPanel();
	
	//Shape radio button list, and Size radio button list strings
	public static final String[] radioButtonList1 = {"Rectangle","Oval","Rounded Rectangle","Shape"};
	public static final String[] radioButtonList2 = {"Small","Medium","Large","Size"};
	
	//color list established to pass into radio button lists
	public static final String[] colorList = {"Background color","Object color","Text color"};
	
	//Shape radio button list, and Size radio button list constructors
	private radioButtons radioButtons1 = new radioButtons(radioButtonList1);
	private radioButtons2 radioButtons2 = new radioButtons2(radioButtonList2);
	
	//FPS and Step Size slider panel constructors
	private sliderPanel slider1 = new sliderPanel(0,30,10,1,"Frames per second");
	private stepPanel slider2 = new stepPanel(1,9,1,0, "Step size");
	
	//JPanel with 3 separate combo boxes (all choosing colors) for font, background, and shape colors.
	private comboPanel combo = new comboPanel(colorList);
	
	//JPanel on screen where all shape/coords will be drawn
	private colorPanel colorPane = new colorPanel(140,0,85,20);
	
	//fps display panel constructor
	private fpsPanel fpsPane = new fpsPanel();

	//Create and Show GUI
	public static void main(String[] args) {
		GuiLabApplet frame = new GuiLabApplet();
		frame.pack();
		frame.setTitle("GuiLabApplet");
		frame.setDefaultCloseOperation(frame.EXIT_ON_CLOSE);
		frame.setSize(700,710);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.setResizable(false);
		
	}

	public GuiLabApplet(){
		//JPanel that combines both radio buttons into a single panel to be placed onto frame.
		JPanel buttonPanel = new JPanel(new GridLayout(1,2));
		buttonPanel.add(radioButtons1);
		buttonPanel.add(radioButtons2);
		
		//GridBagLayout setup - this layout is used to separate the frame into separate sectors for easy positioning and layout
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		
		//GridBagConstraints are initialized as per sector, and FPS slider is added to spot (1,0)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 0;
		add(slider1,c);
		
		//Step Size slider is added to spot (1,1)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 1;
		add(slider2,c);
		
		//3 combo boxes panel is added to spot (1,2)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 2;
		add(combo,c);
		
		//Radio buttons panel is added to spot (1,3)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 3;
		add(buttonPanel,c);
		
		//FPS display window is added to spot (1,4)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 4;
		c.ipadx = 150;
		c.ipady = 20;
		add(fpsPane,c);
		
		//Message box is added last, and anchored to (1,4) or bottom right of JFrame
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 0.5;
		c.gridx = 1;
		c.gridy = 5;
		c.ipadx = 350;
		c.ipady = 125;
		c.anchor = GridBagConstraints.PAGE_END;
		add(descriptionPanel,c);

		//Pane with all drawings is added last, and takes up the entire column of (0,0) to (0,5)
		c.fill = GridBagConstraints.HORIZONTAL;
		c.ipady = 675;
		c.gridheight = 6;
		c.weightx = 0.5;
		c.gridx = 0;
		c.gridy = 0;
		add(colorPane,c);
	}

}