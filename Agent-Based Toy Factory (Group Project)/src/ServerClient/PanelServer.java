package ServerClient;

import java.awt.BorderLayout;
import javax.sound.midi.*;
import java.io.*;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JButton;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;


import javax.swing.JLabel;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class PanelServer extends JFrame implements ActionListener, ChangeListener {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	JButton gantryMngBtn;
	JButton btnKitassemblymng;
	JButton btnNewButton;
	JButton btnGfxManager;
	JButton button;
	JButton btnKitManager;
	JButton btnPartManager;
	JButton btnKitAssemblyMng;
	private JLabel lblNonnormativeMessages;
	private JButton btnLaneManager;
	
	JTextArea textArea;
	JSlider fps;
	JScrollPane scroll;	
	

	Server myServer;
	public PanelServer(Server s) {
		myServer = s;
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 400, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		//Music
		try {
			final Sequencer s1=MidiSystem.getSequencer();
			s1.open();
			FileInputStream midiInput=new FileInputStream("src/music.midi");
			Sequence seq=MidiSystem.getSequence(midiInput);
			s1.setSequence(seq);
			s1.start();
			s1.addMetaEventListener(new MetaEventListener() {
			    public void meta (MetaMessage m) {
			        if (m.getType()==0x2F) {
			            s1.setTickPosition(0);
			            s1.start();
			        }
			    }
			});
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.CENTER);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{117};
		gbl_panel.rowHeights = new int[]{29, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[]{ 1.0};
		gbl_panel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		JLabel lblNewLabel = new JLabel("Server");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 0);
		gbc_lblNewLabel.gridx = 0;
		gbc_lblNewLabel.gridy = 2;
		panel.add(lblNewLabel, gbc_lblNewLabel);
		
		 btnPartManager = new JButton("Part Manager");
		btnPartManager.addActionListener(this);
		GridBagConstraints gbc_btnPartManager = new GridBagConstraints();
		gbc_btnPartManager.insets = new Insets(0, 0, 5, 0);
		gbc_btnPartManager.gridx = 0;
		gbc_btnPartManager.gridy = 3;
		panel.add(btnPartManager, gbc_btnPartManager);
		
		 btnKitManager = new JButton("Kit Manager");
		btnKitManager.addActionListener(this);
		GridBagConstraints gbc_btnKitManager = new GridBagConstraints();
		gbc_btnKitManager.insets = new Insets(0, 0, 5, 0);
		gbc_btnKitManager.gridx = 0;
		gbc_btnKitManager.gridy = 4;
		panel.add(btnKitManager, gbc_btnKitManager);
		
		 button = new JButton("Factory Production Mng");
		button.addActionListener(this);
		GridBagConstraints gbc_button = new GridBagConstraints();
		gbc_button.insets = new Insets(0, 0, 5, 0);
		gbc_button.gridx = 0;
		gbc_button.gridy = 5;
		panel.add(button, gbc_button);
		
		 btnGfxManager = new JButton("Gfx Manager");
		btnGfxManager.addActionListener(this);
		GridBagConstraints gbc_btnGfxManager = new GridBagConstraints();
		gbc_btnGfxManager.insets = new Insets(0, 0, 5, 0);
		gbc_btnGfxManager.gridx = 0;
		gbc_btnGfxManager.gridy = 6;
		panel.add(btnGfxManager, gbc_btnGfxManager);
		
		gantryMngBtn = new JButton("Gantry Manager");
		gantryMngBtn.addActionListener(this);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 0);
		gbc_btnNewButton_1.gridx = 0;
		gbc_btnNewButton_1.gridy = 7;
		panel.add(gantryMngBtn, gbc_btnNewButton_1);
		
		btnKitAssemblyMng = new JButton("Kit Assembly Mng");
		btnKitAssemblyMng.addActionListener(this);
		
		btnLaneManager = new JButton("Lane Manager");
		btnLaneManager.addActionListener(this);
		GridBagConstraints gbc_btnLaneManager = new GridBagConstraints();
		gbc_btnLaneManager.insets = new Insets(0, 0, 5, 0);
		gbc_btnLaneManager.gridx = 0;
		gbc_btnLaneManager.gridy = 8;
		panel.add(btnLaneManager, gbc_btnLaneManager);
		GridBagConstraints gbc_btnKitAssemblyMng = new GridBagConstraints();
		gbc_btnKitAssemblyMng.insets = new Insets(0, 0, 5, 0);
		gbc_btnKitAssemblyMng.gridx = 0;
		gbc_btnKitAssemblyMng.gridy = 9;
		panel.add(btnKitAssemblyMng, gbc_btnKitAssemblyMng);
		
		lblNonnormativeMessages = new JLabel("Non-normative messages");
		GridBagConstraints gbc_lblNonnormativeMessages = new GridBagConstraints();
		gbc_lblNonnormativeMessages.insets = new Insets(0, 0, 5, 0);
		gbc_lblNonnormativeMessages.gridx = 0;
		gbc_lblNonnormativeMessages.gridy = 10;
		panel.add(lblNonnormativeMessages, gbc_lblNonnormativeMessages);
		
		
		textArea = new JTextArea();
		textArea.setLineWrap(true);
		textArea.setEditable(false);
		scroll = new JScrollPane(textArea);
		
		
		
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.insets = new Insets(0, 0, 5, 0);
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 11;
		panel.add(scroll, gbc_textPane);
		

		fps = new JSlider(JSlider.HORIZONTAL, 0, 150, 50);
		fps.addChangeListener(this);
		fps.setMajorTickSpacing(25);
		fps.setMinorTickSpacing(10);
		fps.setPaintTicks(true);
		//fps.setPaintLabels(true);
		panel.add(fps);
		panel.add(new JLabel("Speed"));
		
		}


	@Override
	public void actionPerformed(ActionEvent e) {
		if(e.getSource()==btnPartManager){
			PartsManager pm = new PartsManager();
			pm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			pm.setVisible(true);
			
			
		}
		else if(e.getSource()==btnKitManager){
			KitManager mg = new KitManager();
			mg.setVisible(true);
			
		}
		else if(e.getSource()==button){
			FactoryProductionManager fpm = new FactoryProductionManager();
	    	fpm.setSize(1050,750);
	    	fpm.setLocationRelativeTo(null);
	    	fpm.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	    	fpm.setVisible(true);
			
		}
		else if(e.getSource()==btnGfxManager){
			GfxManager mg = new GfxManager(0, 0);
			
			
			mg.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
			mg.setSize(1050, 750);
			mg.setVisible(true);
			
		}
		else if(e.getSource()==gantryMngBtn){
			GantryManager gm = new GantryManager();
			gm.setVisible(true);
			
		}
		else if(e.getSource()==btnKitassemblymng){
			KitAssemblyManager kam = new KitAssemblyManager();
			kam.setVisible(true);

			
		}
		else if(e.getSource()==btnNewButton){
			LaneManager lm = new LaneManager();
			lm.setVisible(true);
			
		}
		else if(e.getSource()==btnKitAssemblyMng){
			KitAssemblyManager kam = new KitAssemblyManager();
			kam.setVisible(true);
			
		}
		else if(e.getSource()==btnLaneManager){
			LaneManager lm = new LaneManager();
			lm.setVisible(true);
			
		}
				
		
	}
	
	public void appendTheTextArea(String message){// the server calls this method when
												//and passes the String message about the non-normativa action 
												//the message will be appended from the beginning of the list
		textArea.setText("\n"+message+textArea.getText());
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub
		JSlider source = (JSlider)e.getSource();
		if (!source.getValueIsAdjusting())
		{
			int speed = (int)source.getValue();
			
			myServer.updateGfxSpeed(speed);
		}
	}


	

}
