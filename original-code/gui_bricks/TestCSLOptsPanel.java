package gui_bricks;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class TestCSLOptsPanel extends JPanel {
	private JTextField omega0Fld;
	private JTextField pFld;
	private JTextField sigmaFld;
	
	private YesNoLabel cslYN;
	private JCheckBox useChB;
	private JLabel questionMarkLbl;
	private JLabel lbliscsl;
	private JTextField maxSigmaFld;
	private JLabel label;
	private JLabel lblnbspnbsp;
	
	
	private final void setFieldsEnabled(boolean b) {
		
		omega0Fld.setEnabled(b);
		pFld.setEnabled(b);

	}
	
	
	public final void setCSLLocked(boolean b) {
		
		if(b) {
			useChB.setSelected(false);
			useChB.setEnabled(false);
			useChB.setToolTipText("This feature is not supported for the selected point group");
		} else {
			useChB.setEnabled(true);
			useChB.setToolTipText("Use brandon criterion; \u0394\u03C9 is an angle of deviation from a CSL misorientation");
		}		
	}


	public TestCSLOptsPanel() {
		setLayout(new MigLayout("insets 0", "[][][]", "[]"));
		
		useChB = new JCheckBox("<html><font color=#0000cc>Use criterion \u0394\u03C9 \u2264 \u03C9<sub>0</sub> / \u03A3<sup>p</sup> with \u03C9<sub>0</sub> =</font>");
		useChB.setToolTipText("Use Brandon-like criterion; \u0394\u03C9 is the angle of a deviation from a CSL misorientation");
		add(useChB, "flowx,cell 0 0,aligny center");
		
		useChB.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				JCheckBox src = (JCheckBox) evt.getSource();
				if(src.isSelected()) {
					setFieldsEnabled(true);
				} else {
					setFieldsEnabled(false);
				}
			}
		});
		
		lblnbspnbsp = new JLabel("<html><font color=#0000ff>:<sub>&nbsp;</sub><sup>&nbsp;</sup></font>");
		add(lblnbspnbsp, "cell 1 0");
		
		lbliscsl = new JLabel("<html><b>CSL</b>?");
		lbliscsl.setToolTipText("Is the tested boundary a CSL boundary?");
		add(lbliscsl, "flowx,cell 2 0,gapx 20");
		
		omega0Fld = new JTextField();
		omega0Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(omega0Fld, "cell 0 0");
		omega0Fld.setColumns(3);
		omega0Fld.setText("15");
		
		JLabel pLbl = new JLabel("<html><font color=#0000cc>\u00B0,<sub>&nbsp;</sub><sup>&nbsp;</sup>p =</font>");
		add(pLbl, "cell 0 0");
		
		pFld = new JTextField();
		pFld.setHorizontalAlignment(SwingConstants.RIGHT);
		pFld.setText("0.5");
		add(pFld, "cell 0 0");
		pFld.setColumns(5);
		
		questionMarkLbl = new JLabel("<html><font color=#0000cc>,<sub>&nbsp;</sub><sup>&nbsp;</sup>and \u03A3 \u2264</font>");
		add(questionMarkLbl, "cell 0 0");
		
		maxSigmaFld = new JTextField();
		maxSigmaFld.setText("30");
		maxSigmaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(maxSigmaFld, "cell 0 0");
		maxSigmaFld.setColumns(3);
		
		cslYN = new YesNoLabel();
		add(cslYN, "cell 2 0");
		
		JLabel sigmaLbl = new JLabel("(\u03A3 =");
		sigmaLbl.setToolTipText("\u03A3-value");
		add(sigmaLbl, "cell 2 0,gapx 5");
		
		sigmaFld = new JTextField();
		sigmaFld.setHorizontalAlignment(SwingConstants.RIGHT);
		sigmaFld.setEditable(false);
		add(sigmaFld, "cell 2 0");
		sigmaFld.setColumns(3);
		
		label = new JLabel(");");
		add(label, "cell 2 0");

		setFieldsEnabled(true);
	}

	
	public boolean isChecked() {
		return useChB.isSelected();
	}
	
	public void setCSL(boolean yes, final String sigma) {
		if(yes) {
			cslYN.setYes();
		}  else {
			cslYN.setNo();
		}
		sigmaFld.setText(sigma);

	}
	
	public int getMaxSigma() {
		String s = maxSigmaFld.getText();
		s = s.replace(",", ".");
		return Integer.parseInt(s);
	}
	
	public double getP() {
		String s = pFld.getText();
		s = s.replace(",", ".");
		return Double.parseDouble(s);
	}
	
	public double getOmega0() {
		String s = omega0Fld.getText();
		s = s.replace(",", ".");
		return Double.parseDouble(s);
	}
	
	public void reset() {
		sigmaFld.setText("");		
		cslYN.setEmpty();
	}
	
	
}
