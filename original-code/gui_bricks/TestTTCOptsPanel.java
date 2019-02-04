package gui_bricks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;


public class TestTTCOptsPanel extends JPanel {
	private JTextField alphaLFld;
	private JTextField alphaNFld;
	
	private YesNoLabel tiltYN;
	private YesNoLabel twistYN;
	private YesNoLabel symYN;
	private YesNoLabel impYN; 
	
	private JCheckBox useCb;
	private JTextField tolTTC;
	private JTextField alphaSFld;
	private JTextField alphaIFld;
	
	
	private final void setFieldsEnabled(boolean b) {
		
		tolTTC.setEnabled(b);
	}
	
	
	public TestTTCOptsPanel() {
		
		
		setLayout(new MigLayout("insets 0", "[][][][][][][][]", "[][]"));
		
		useCb = new JCheckBox("<html><font color=#0000cc>Use the parameters based on 'tilt/twist component parameter' and assume the tolerance of</font>");
		useCb.setToolTipText("These parameters approximate the distances defined in the boundary space");
		add(useCb, "flowx,cell 0 0 8 1,gapy 0 5");
		
		useCb.addActionListener(new ActionListener() {
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
		
		JLabel tiltLbl = new JLabel("<html><b>Tilt</b>?");
		tiltLbl.setToolTipText("Is the tested boundary a tilt boundary?");
		add(tiltLbl, "flowx,cell 0 1");
		
		tolTTC = new JTextField();
		tolTTC.setText("5");
		tolTTC.setToolTipText("<html>Boundaries will be qualified to given types if \u03B1<sub>L</sub>, <sub>N</sub>, \u03B1<sub>S</sub> or \u03B1<sub>I</sub>, respectively, are less than specified limit.");
		tolTTC.setHorizontalAlignment(SwingConstants.RIGHT);
		add(tolTTC, "cell 0 0,aligny top");
		tolTTC.setColumns(3);
		
		JLabel bracket1Lbl = new JLabel("<html><font color=#0000cc>\u00B0:<sub>&nbsp;</sub></font>");
		add(bracket1Lbl, "cell 0 0");
		
		tiltYN = new YesNoLabel();
		add(tiltYN, "flowx,cell 1 1");
		
		JLabel twistAngleLbl = new JLabel("<html>(\u03B1<sub>L</sub> =");
		twistAngleLbl.setToolTipText("Approximate distance to the nearest pure-tilt boundary.");
		add(twistAngleLbl, "cell 1 1,gapx 5");
		
		alphaLFld = new JTextField();
		alphaLFld.setToolTipText("Approximate distance to the nearest pure-tilt boundary.");
		alphaLFld.setHorizontalAlignment(SwingConstants.RIGHT);
		alphaLFld.setEditable(false);
		add(alphaLFld, "cell 1 1");
		alphaLFld.setColumns(4);
		
		JLabel bra2Lbl = new JLabel("<html>\u00B0<sub>&nbsp;</sub>);");
		bra2Lbl.setToolTipText("");
		add(bra2Lbl, "cell 1 1");
		
	
		JLabel twistLbl = new JLabel("<html><b>Twist</b>?");
		twistLbl.setToolTipText("Is the tested boundary a twist boundary?");
		add(twistLbl, "cell 2 1,gapx 20");
		
		twistYN = new YesNoLabel();
		add(twistYN, "flowx,cell 3 1");
		
		JLabel tiltAngleLbl = new JLabel("<html>(\u03B1<sub>N</sub> =");
		tiltAngleLbl.setToolTipText("Approximate distance to the nearest pure-twist boundary.");
		add(tiltAngleLbl, "cell 3 1,gapx 5");
		
		alphaNFld = new JTextField();
		alphaNFld.setToolTipText("Approximate distance to the nearest pure-twist boundary.");
		alphaNFld.setHorizontalAlignment(SwingConstants.RIGHT);
		alphaNFld.setEditable(false);
		add(alphaNFld, "cell 3 1");
		alphaNFld.setColumns(4);
		
		JLabel bra3Lbl = new JLabel("<html>\u00B0<sub>&nbsp;</sub>);");
		bra3Lbl.setToolTipText("");
		add(bra3Lbl, "cell 3 1");
		
		JLabel lblsymmetric = new JLabel("<html><b>Symmetric</b>?");
		lblsymmetric.setToolTipText("Is the tested boundary a symmetric boundary?");
		add(lblsymmetric, "cell 4 1,gapx 20");
		
		symYN = new YesNoLabel();
		add(symYN, "flowx,cell 5 1");
		
		JLabel lbls = new JLabel("<html>(\u03B1<sub>S</sub> =");
		lbls.setToolTipText("Approximate distance to the nearest pure-symmetric boundary.");
		add(lbls, "cell 5 1,gapx 5");
		
		alphaSFld = new JTextField();
		alphaSFld.setToolTipText("Approximate distance to the nearest pure-symmetric boundary.");
		alphaSFld.setHorizontalAlignment(SwingConstants.RIGHT);
		alphaSFld.setEditable(false);
		alphaSFld.setColumns(4);
		add(alphaSFld, "cell 5 1");
		
		JLabel lblnbsp = new JLabel("<html>\u00B0<sub>&nbsp;</sub>);");
		lblnbsp.setToolTipText("");
		add(lblnbsp, "cell 5 1");
		
		JLabel lbl180tilt = new JLabel("<html><b>180\u00b0-tilt</b>?");
		lbl180tilt.setToolTipText("Is the tested boundary a 180\u00B0-tilt boundary?");
		add(lbl180tilt, "cell 6 1,gapx 20");
		
		impYN = new YesNoLabel();
		add(impYN, "flowx,cell 7 1");
		
		JLabel lbli = new JLabel("<html>(\u03B1<sub>I</sub> =");
		lbli.setToolTipText("Approximate distance to the nearest pure 180\u00B0-tilt boundary.");
		add(lbli, "cell 7 1,gapx 5");
		
		alphaIFld = new JTextField();
		alphaIFld.setToolTipText("Approximate distance to the nearest pure 180\u00B0-tilt boundary.");
		alphaIFld.setHorizontalAlignment(SwingConstants.RIGHT);
		alphaIFld.setEditable(false);
		alphaIFld.setColumns(4);
		add(alphaIFld, "cell 7 1");
		
		JLabel lblnbsp_1 = new JLabel("<html>\u00B0<sub>&nbsp;</sub>);");
		lblnbsp_1.setToolTipText("");
		add(lblnbsp_1, "cell 7 1");
		
		setFieldsEnabled(false);

	}
	
	
	public final void reset() {
		alphaLFld.setText("");
		alphaNFld.setText("");
		alphaSFld.setText("");
		alphaIFld.setText("");
		
		tiltYN.setEmpty();
		twistYN.setEmpty();
		symYN.setEmpty();
		impYN.setEmpty();
	}
	
	
	
	
	public final double getTolTTC() {
		String s = tolTTC.getText();
		s = s.replace(",", ".");
		return Double.parseDouble(s);
	}
	
	public final boolean isChecked() {
		return useCb.isSelected();
	}
	
	public final void setTilt(boolean yes) {
		if(yes) tiltYN.setYes(); else tiltYN.setNo();
	}
	
	public final void setTwist(boolean yes) {
		if(yes) twistYN.setYes(); else twistYN.setNo();
	}
	
	public final void setSym(boolean yes) {
		if(yes) symYN.setYes(); else symYN.setNo();
	}
	
	public final void setImprop(boolean yes) {
		if(yes) impYN.setYes(); else impYN.setNo();
	}
	
	public final void setAlphaN(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		alphaNFld.setText(df.format(w));		
		alphaNFld.setCaretPosition(0);			
	}
	
	public final void setAlphaL(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		alphaLFld.setText(df.format(w));		
		alphaLFld.setCaretPosition(0);	
	}
	
	
	public final void setAlphaS(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		alphaSFld.setText(df.format(w));		
		alphaSFld.setCaretPosition(0);	
	}
	
	public final void setAlphaI(double angle) {
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
				
		final DecimalFormat df = new DecimalFormat("0.##", otherSymbols);
		final double w = Math.toDegrees(angle);		
		alphaIFld.setText(df.format(w));		
		alphaIFld.setCaretPosition(0);	
	}

}
