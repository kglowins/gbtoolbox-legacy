package com.github.kglowins.gbtoolbox.gui_bricks;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;


public class EulerAnglesFields extends JPanel {
	private JTextField phi1Fld;
	private JTextField PhiFld;
	private JTextField phi2Fld;
	private JLabel degLbl;


	public EulerAnglesFields() {
		setLayout(new MigLayout("insets 0", "[][][]", "[][][]"));
		
		JLabel phi1Lbl = new JLabel("<html>\u03c6<sub>1</sub> =");
		add(phi1Lbl, "cell 0 0,alignx right");
		
		phi1Fld = new JTextField();
		phi1Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(phi1Fld, "cell 1 0");
		phi1Fld.setColumns(8);
		
		degLbl = new JLabel("[degrees]");
		add(degLbl, "cell 2 0,gapx 30");
		
		JLabel PhiLbl = new JLabel("<html>\u03a6 =");
		add(PhiLbl, "cell 0 1,alignx right");
		
		PhiFld = new JTextField();
		PhiFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(PhiFld, "cell 1 1");
		PhiFld.setColumns(8);
		
		JLabel phi2Lbl = new JLabel("<html>\u03c6<sub>2</sub> =");
		add(phi2Lbl, "cell 0 2,alignx right");
		
		phi2Fld = new JTextField();
		phi2Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(phi2Fld, "cell 1 2");
		phi2Fld.setColumns(8);

	}
	
	public final EulerAngles getAngles() {
		
		EulerAngles angles = new EulerAngles();
		
		String str = new String(phi1Fld.getText().replace(",", "."));		
		double phi1 = Double.parseDouble(str);
		
		str = new String(PhiFld.getText().replace(",", "."));		
		double Phi = Double.parseDouble(str);
		
		str = new String(phi2Fld.getText().replace(",", "."));		
		double phi2 = Double.parseDouble(str);
		
		angles.set(Math.toRadians(phi1), Math.toRadians(Phi), Math.toRadians(phi2));
			
		return angles;
	}
	
	
	public final void setAngles(EulerAngles angles) {
		
		double phi1 = Math.toDegrees(angles.phi1());
		double Phi = Math.toDegrees(angles.Phi());
		double phi2 = Math.toDegrees(angles.phi2());
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		
		final DecimalFormat df = new DecimalFormat("0.####", otherSymbols);	
		
		phi1Fld.setText(df.format(phi1));
		PhiFld.setText(df.format(Phi));
		phi2Fld.setText(df.format(phi2));
		
		phi1Fld.setCaretPosition(0);
		PhiFld.setCaretPosition(0);
		phi2Fld.setCaretPosition(0);		
	}
	
	
	public final void clear() {
		
		phi1Fld.setText("");
		PhiFld.setText("");
		phi2Fld.setText("");
	}

}
