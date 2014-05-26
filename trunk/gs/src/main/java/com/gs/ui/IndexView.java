package com.gs.ui;

import java.awt.Dimension;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class IndexView extends JPanel{
	private static final long serialVersionUID = 1L;

	private JLabel jLabel;
	private JTextField jTextField;
	private JButton jButton;
	private int myheight = 100;
	
	
	public void init(Dimension parentDimension){
		//setLayout(new FlowLayout(FlowLayout.LEFT, MainFrame.HGAP, MainFrame.VGAP));
		setSize(parentDimension.width, myheight);
		
		
		this.add(getJLabel());
		this.add(getJTextField());
		this.add(getJButton());
		
	}
	
	
	private javax.swing.JLabel getJLabel() {
		if (jLabel == null) {
			jLabel = new javax.swing.JLabel();
			jLabel.setSize(100, myheight - MainFrame.VGAP*2);
			jLabel.setText("股票代码:");
		}
		return jLabel;
	}

	private javax.swing.JTextField getJTextField() {
		if (jTextField == null) {
			jTextField = new JTextField();
			jTextField.setText("600036");
			jTextField.setMinimumSize(new Dimension(100, myheight - MainFrame.VGAP*2));
			jTextField.setSize(100, myheight - MainFrame.VGAP*2);
		}
		return jTextField;
	}

	private javax.swing.JButton getJButton() {
		if (jButton == null) {
			jButton = new javax.swing.JButton();
			jButton.setSize(50, myheight - MainFrame.VGAP*2);
			jButton.setText("OK");
		}
		return jButton;
	}
}
