package main;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class ServerFrame extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JTextField portField = new JTextField();
	private JLabel jLabel1 = new JLabel();
	private JTextField fishField = new JTextField();
	private JLabel jLabel2 = new JLabel();
	private DefaultListModel jList1Model = new DefaultListModel();
	private JScrollPane jList1ScrollPane = new JScrollPane();
	private JLabel jLabel3 = new JLabel();
	private JLabel jLabel4 = new JLabel();
	private JTextField populationField = new JTextField();
	private JTextField priceField = new JTextField();
	private JButton submitButton = new JButton();
	private JButton playButton = new JButton();
	private JButton pauseButton = new JButton();
	private JButton stopButton = new JButton();
	public JList list = new JList(jList1Model);
	private final JLabel lblRoundtime = new JLabel();
	private final JTextField roundTimeField = new JTextField();
	private final JButton btnKick = new JButton();
	// Ende Attribute

	public ServerFrame() {

		super();
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		int frameWidth = 275;
		int frameHeight = 500;
		setSize(frameWidth, frameHeight);
		Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (d.width - getSize().width) / 2;
		int y = (d.height - getSize().height) / 2;
		setLocation(x, y);
		setTitle("Server");
		setResizable(false);
		Container cp = getContentPane();
		cp.setLayout(null);
		// Anfang Komponenten

		portField.setBounds(160, 8, 83, 28);
		portField.setText("63564");
		cp.add(portField);
		jLabel1.setBounds(8, 8, 110, 28);
		jLabel1.setText("Port");
		cp.add(jLabel1);
		fishField.setBounds(160, 48, 83, 28);
		fishField.setText("1000");
		cp.add(fishField);
		jLabel2.setBounds(8, 48, 110, 28);
		jLabel2.setText("max. Fish");
		cp.add(jLabel2);
		jLabel3.setBounds(8, 88, 110, 28);
		jLabel3.setText("Population");
		cp.add(jLabel3);
		jLabel4.setBounds(8, 128, 110, 28);
		jLabel4.setText("Price");
		cp.add(jLabel4);
		populationField.setBounds(160, 88, 83, 28);
		populationField.setText("100");
		cp.add(populationField);
		priceField.setBounds(160, 128, 83, 28);
		priceField.setText("10");
		cp.add(priceField);
		submitButton.setBounds(8, 216, 235, 25);
		submitButton.setText("Submit");
		submitButton.setMargin(new Insets(2, 2, 2, 2));
		submitButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				submitButton_ActionPerformed(evt);
			}
		});
		cp.add(submitButton);
		playButton.setBounds(8, 248, 75, 25);
		playButton.setText("Play");
		playButton.setMargin(new Insets(2, 2, 2, 2));
		playButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				playButton_ActionPerformed(evt);
			}
		});
		cp.add(playButton);
		pauseButton.setBounds(168, 248, 75, 25);
		pauseButton.setText("Pause");
		pauseButton.setMargin(new Insets(2, 2, 2, 2));
		pauseButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				pauseButton_ActionPerformed(evt);
			}
		});
		cp.add(pauseButton);
		stopButton.setBounds(88, 248, 75, 25);
		stopButton.setText("STOP");
		stopButton.setMargin(new Insets(2, 2, 2, 2));
		stopButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				stopButton_ActionPerformed(evt);
			}
		});
		cp.add(stopButton);
		lblRoundtime.setText("Roundtime");
		lblRoundtime.setBounds(8, 167, 110, 28);
		
		getContentPane().add(lblRoundtime);
		roundTimeField.setText("60");
		roundTimeField.setBounds(160, 167, 83, 28);
		
		getContentPane().add(roundTimeField);
		btnKick.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				kickButton_ActionPerformed(arg0);
			}
		});
		btnKick.setText("Kick");
		btnKick.setMargin(new Insets(2, 2, 2, 2));
		btnKick.setBounds(8, 419, 235, 25);
		
		getContentPane().add(btnKick);
		
		
		list.setBorder(new LineBorder(Color.LIGHT_GRAY));
		list.setBounds(8, 284, 238, 124);
		getContentPane().add(list);
		// Ende Komponenten

		setVisible(true);
	}

	public void submitButton_ActionPerformed(ActionEvent evt) {
		if (isInt(portField.getText()) && portField.getText() != null) {
			FishServer.port = Integer.parseInt(portField.getText());

			FishServer.wait = false;
		}
		
		if (isInt(fishField.getText())) {
			FishServer.pond.targetPopulation = Integer.parseInt(fishField.getText());
			FishServer.pondSize = Integer.parseInt(fishField.getText());
		}
		
		if (isInt(populationField.getText())) {
			if (FishServer.pond.targetPopulation >= Integer.parseInt(populationField.getText())){
				FishServer.pond.population = Integer.parseInt(populationField.getText());
			} else {
				FishServer.pond.population = FishServer.pond.targetPopulation;
			}
		}
		
		if (isInt(priceField.getText())) {
			FishServer.market1.basePrice = Integer.parseInt(priceField.getText());
		}
		
		if (isInt(roundTimeField.getText())) {
			FishServer.c1.setCounter(Integer.parseInt(roundTimeField.getText()));
		}
	} // end of submitButton_ActionPerformed

	public void playButton_ActionPerformed(ActionEvent evt) {
		if (FishServer.c1.getPause()) {
			FishServer.serverContinue = true;
		}
	} // end of playButton_ActionPerformed

	public void pauseButton_ActionPerformed(ActionEvent evt) {
		FishServer.pause("Server");
	} // end of pauseButton_ActionPerformed

	public void stopButton_ActionPerformed(ActionEvent evt) {
		try {
			FishServer.stopServer();
		} catch (InterruptedException e) {
		}
	} // end of stopButton_ActionPerformed
	
	public void kickButton_ActionPerformed(ActionEvent evt) {
		FishServer.kick(jList1Model.getElementAt(list.getSelectedIndex()).toString());
	}

	public boolean isInt(String s) {
		try {
			double b = Integer.parseInt(s);
			return true;
		} catch (Exception e) {
			return false;
		}
	}
	
	public void addList(String s) {
		jList1Model.addElement(s);
	}
	
	public void removeList(String s) {
		jList1Model.removeElement(s);
	}
}
