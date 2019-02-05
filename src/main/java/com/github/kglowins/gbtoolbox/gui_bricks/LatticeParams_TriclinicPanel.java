package com.github.kglowins.gbtoolbox.gui_bricks;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LatticeParams_TriclinicPanel extends JPanel {
	private JTextField aFld;
	private JTextField cFld;
	private JTextField bFld;
	private JLabel angLbl;
	private JLabel alphaLbl;
	private JLabel betaLbl;
	private JLabel gammaLbl;
	private JTextField alphaFld;
	private JTextField betaFld;
	private JTextField gammaFld;
	private JLabel label;
	
	public LatticeParams_TriclinicPanel() {
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
		
		alphaLbl = new JLabel("\u03b1 =");
		add(alphaLbl, "cell 0 1,alignx trailing");
		
		alphaFld = new JTextField();
		alphaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		alphaFld.setColumns(5);
		add(alphaFld, "cell 1 1,growx");
		
		betaLbl = new JLabel("\u03b2 =");
		add(betaLbl, "cell 2 1,alignx trailing");
		
		betaFld = new JTextField();
		betaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		betaFld.setColumns(5);
		add(betaFld, "cell 3 1,growx");
		
		gammaLbl = new JLabel("\u03b3 =");
		add(gammaLbl, "cell 4 1,alignx trailing");
		
		gammaFld = new JTextField();
		gammaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		gammaFld.setColumns(5);
		add(gammaFld, "cell 5 1,growx");
		
		label = new JLabel("[\u00b0]");
		add(label, "cell 6 1,gapx 30");

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
	
	public final JTextField getAlphaFld() {
		return betaFld;
	}
	
	public final JTextField getBetaFld() {
		return betaFld;
	}
	
	public final JTextField getGammaFld() {
		return gammaFld;
	}
}
