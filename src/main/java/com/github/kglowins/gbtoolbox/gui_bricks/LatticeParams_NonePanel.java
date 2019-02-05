package com.github.kglowins.gbtoolbox.gui_bricks;

import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;

public class LatticeParams_NonePanel extends JPanel {

	public LatticeParams_NonePanel() {
		setToolTipText("No more details need to be specified for this point group");
		setLayout(new MigLayout("insets 0", "[]", "[]"));
		
		JLabel lbl = new JLabel("---");
		add(lbl, "cell 0 0");

	}

}
