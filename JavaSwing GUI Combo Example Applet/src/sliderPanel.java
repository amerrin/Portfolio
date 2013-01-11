import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;


public class sliderPanel extends JPanel{
	public sliderPanel(int minValue,int maxValue, int majorTick, int minorTick, final String title){
		
		//slider is responsible for changing FPS
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, 0);
		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPreferredSize(new Dimension(300,50));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()){//code used to only accept a slider value once the user lets go of left mouse button
				DescriptionPanel.setDescription(title + " changed to " + slider.getValue());//prints to message box to display to user
				colorPanel.changeRefreshRate(slider.getValue());//passes selected slider value into refreshRate within colorPanel where refreshRate is used
				colorPanel.repaintCountReset();//repaint counter is reset to 0 so FPS will be updated immediately after user selects it.
				}
			}
		});
		JPanel sliderPanel = new JPanel();
		sliderPanel.add(slider);
		sliderPanel.setBorder(new TitledBorder(title));
		
		add(sliderPanel);
	}
	}