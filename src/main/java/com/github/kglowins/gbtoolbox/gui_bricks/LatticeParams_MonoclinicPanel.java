package com.github.kglowins.gbtoolbox.gui_bricks;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LatticeParams_MonoclinicPanel extends JPanel {
	private JTextField aFld;
	private JTextField cFld;
	private JTextField bFld;
	private JLabel angLbl;
	private JLabel betaLbl;
	private JTextField betaFld;
	private JLabel label_1;
	
	public LatticeParams_MonoclinicPanel() {
		setLayout(new MigLayout("insets 0", "[][][][][][][]", "[][]"));
		
		JLabel aLbl = new JLabel("a =");
		add(aLbl, "cell 0 0,alignx trailing");
		
		aFld = new JTextField();
		aFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(aFld, "cell 1 0,growx");
		aFld.setColumns(5);
		
		JLabel bLbl = new JLabel("b =");
		add(bLbl, "cell 2 0,alignx trailing,gapx 10");
		
		bFld = new JTextField();
		bFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(bFld, "cell 3 0,growx");
		bFld.setColumns(5);
		
		JLabel cLbl = new JLabel("c =");
		add(cLbl, "cell 4 0,alignx trailing,gapx 10");
		
		cFld = new JTextField();
		cFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(cFld, "cell 5 0,growx");
		cFld.setColumns(5);
		
		angLbl = new JLabel("[\u212B]");
		add(angLbl, "cell 6 0,gapx 30");
		
		betaLbl = new JLabel("\u03B2 =");
		add(betaLbl, "cell 0 1,alignx trailing");
		
		betaFld = new JTextField();
		betaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		betaFld.setColumns(5);
		add(betaFld, "cell 1 1,growx");
		
		label_1 = new JLabel("[\u00B0]");
		add(label_1, "cell 6 1,gapx 30");

	}
	
	
	public final JTextField getaFld() {
		return aFld;
	}

	public final JTextField getbFld() {
		return bFld;
	}
	
	public final JTextField getcFld() {
		return cFld;
	}
	
	public final JTextField getBetaFld() {
		return betaFld;
	}
}
