import java.awt.*;

import javax.swing.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

class colorPanel extends JPanel {
	//integer variables created to hold (x,y) coordinates, as well as width and height of the shape
    private static int x = 0;      // circle's left-top corner x
    private static int y = 0;      // circle's left-top corner y
    private static int width = 0;  // circle's width
    private static int height = 0; // circle's height
    
    //integer variables used as the change in x, and change in y respsectively
    public static int px=1;
    public static int py=1;
    
    //color variables used to change the color of the background, shape, and text
    private static Color backColor = Color.YELLOW;
    private static Color objectColor = Color.red;
    private static Color textColor = Color.red;
    
    //variables related to the calculating and printing of Refresh Rate
    private static int refreshRate = 0;
    static int repaintCount = 0;
    private long startTime = 0;
    static double rate = 0;
    
    //panel width and height of colorPanel ALONE
    private static final int panelH = 685;
    private static final int panelW = 340;
    
    //timer delcaration outside of constructor so entire class can access
    private Timer t = new Timer(0, null);
    
    //booleans created to manipulate specifically which shape/size graphic commands are drawn
    private static boolean oval = false;
    private static boolean rect = true;
    private static boolean rectround = false;
    private static boolean small = false;
    private static boolean medium = true;
    private static boolean large = false;
    
    //font for coordinate pair
    private Font f = new Font("SansSerif", Font.PLAIN, 18);
    
    public void PerformTimerTask() {
        //when timer goes off, as long as FPS > 0, coordinates are incremented in the proper direction, and the shape will bounce off all edges of the panel
    	if(refreshRate !=0){
        x+=px;
        y+=py;
        if (x+width >= panelW) {
            x = panelW - width;
            px = -px;
        }
        if (y+height >= panelH) {
            y = panelH - height;
            py = -py;
        }
        if (x < 0) {
            x = 0;
            px = -px;
        }
        if (y < 0) {
            y = 0;
            py = -py;
        }
        repaint();
    	}
    }
    class MyActionListener implements ActionListener {
        public void actionPerformed(ActionEvent e) {
            // when timer goes off, just call PerformTimerTask() and do all the work there
            PerformTimerTask();
        }
    }
    public colorPanel(int x, int y, int width, int height) {
    	//constructor for initial shape which is always initialized to a square
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(350,675));

        //implement for timer
        t.addActionListener(new MyActionListener()); // specify what to do when timer goes off
        t.start(); // start the timer
        

    }
    //paint component for shape and coordinate
    protected void paintComponent(Graphics g) {
    	
    	if(refreshRate != 0)//if FPS is set to 0, this will simulate a 'frozen' state
    	t.setDelay(1000 / refreshRate);//creates desired FPS
    	//if-else statement used for incrementing and resetting repaintCount, and used for calculating the FPS which is displaye dby fpsPanel.java
    	if (repaintCount++ > 0) {
            long curTime = System.currentTimeMillis();
            long elapseTime = curTime - startTime;
            rate = ((double)(repaintCount - 1)) / (elapseTime / 1000.0);
        } else {
            startTime = System.currentTimeMillis();
        }
        super.paintComponent(g);
        g.setColor(backColor);//backcolor controls the color of the background
        g.fillRect(0,  0, getWidth(), getHeight());
        g.setColor(objectColor);//objectColor controls the color of the shape
        if(oval)
        	g.drawArc(x, y, width, height, 0, 360);
        if (rect)
        	g.drawRect(x, y, width, height);
        if (rectround){
            RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(x, y, width, height,5,5);
            ((Graphics2D) g).draw(roundedRectangle); 
        }
        g.setColor(textColor);//textColor controls the color of the text
        g.setFont(f);
        if (small)
        	g.drawString("("+x+","+y+")",x-15,y+13);//positioned properly for all small sizes
        if(medium)
        	g.drawString("("+x+","+y+")",x+1,y+16);//coordinates position properaly for all medium sizes
        if(large)
        	g.drawString("("+x+","+y+")",x+50,y+25);//coordinates position properly for all large sizes
        	
    }
	
	public static void changeBackColor (Color c){
		backColor = c;
	}
	
	public static void changeObjectColor (Color c){
		objectColor = c;
	}
	
	public static void changeRefreshRate(int r){
		refreshRate = r;
	}
	
	public static void changeShape(boolean[] settings){
		oval = settings[0];
		rect = settings[1];
		rectround = settings[2];
	}
	
	public static void changeSize(int[] dimensions,boolean[] coordsize){
		small = coordsize[0];
		medium = coordsize[1];
		large = coordsize[2];
		width = dimensions[0];
		height = dimensions[1];
	}
	
	public static void changeTextColor(Color c){
		textColor = c;
	}
	
	//step value function which will take into account direction the shape is moving in, and thus apply the slider value in the direciton
	public static void changeStepValue(int v){
		if(px<0)
			px = -v;
		else if(px>=0)
			px = v;
		if(py<0)
			py = -v;
		else if (py>=0)
			py = v;
			
	}
	
	public static void repaintCountReset(){
		repaintCount = 0;
	}

}
