package com.gs.ui;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class MainFrame extends JFrame {
	private static final long serialVersionUID = 1L;
	public static final int HGAP = 10;
	public static final int VGAP = 10;
	
	
	private JPanel topFrame;
	private JPanel bottomeFrame;
	

	public static void main(String[] args) {
		MainFrame frame = new MainFrame();
		
		frame.setVisible(true);
	}

	public MainFrame() {
		this.setTitle("劫庄神器");
		getContentPane().setLayout(new BorderLayout(HGAP, VGAP));
		this.setSize(600, 500);
		setTopContainer();
		setBottomContainer();
	}

	private void setTopContainer() {
		if(topFrame==null){
			topFrame = new JPanel();
			add(topFrame, "North");
			
			IndexView indexView = new IndexView();
			indexView.init(getSize());
			add(indexView);
		}
		topFrame.setSize(topFrame.getParent().getSize().width, topFrame.getParent().getSize().height);
	}
	
	private void setBottomContainer() {
		if(bottomeFrame==null){
			bottomeFrame = new JPanel();
			add(bottomeFrame, "South");
		}
		bottomeFrame.setSize(topFrame.getParent().getSize().width, topFrame.getParent().getSize().height);
	}

	

}
