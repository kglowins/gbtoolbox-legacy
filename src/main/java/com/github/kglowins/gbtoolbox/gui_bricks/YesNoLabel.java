package com.github.kglowins.gbtoolbox.gui_bricks;
import java.awt.Color;
import java.awt.Dimension;

import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class YesNoLabel extends JLabel {

	public YesNoLabel() {
		super("");
		setHorizontalAlignment(SwingConstants.CENTER);
		setPreferredSize(new Dimension(15, 15));
		setMinimumSize(new Dimension(15, 15));
	}
		
	public final void setEmpty() {
		setOpaque(false);
		setText("");
	}
	
	public final void setYes() {
		
		setOpaque(true);
		setForeground(Color.BLACK);
		setBackground(Color.GREEN);
		setText("Y");
	}
	
	public final void setNo() {
		
		setOpaque(true);
		setForeground(Color.WHITE);
		setBackground(Color.RED);
		setText("N");	
	}		
}
