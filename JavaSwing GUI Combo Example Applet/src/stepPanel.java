import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;


public class stepPanel extends JPanel{
	//step slider panel constructor
	public stepPanel(int minValue,int maxValue, int majorTick, int minorTick, final String title){
		//similar to the FPS slider, please see sliderPanel.java for more info
		final JSlider slider = new JSlider(JSlider.HORIZONTAL, minValue, maxValue, 1);
		slider.setMajorTickSpacing(majorTick);
		slider.setMinorTickSpacing(minorTick);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		slider.setPreferredSize(new Dimension(300,50));
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				JSlider source = (JSlider)e.getSource();
				if (!source.getValueIsAdjusting()){
				DescriptionPanel.setDescription(title + " changed to " + slider.getValue());
				colorPanel.changeStepValue(slider.getValue());//changes the step value of the shape
				}
			}
		});
		JPanel sliderPanel = new JPanel();
		sliderPanel.add(slider);
		sliderPanel.setBorder(new TitledBorder(title));
		
		add(sliderPanel);
	}
	}