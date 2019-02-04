package gui_bricks;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import utils.Quaternion;


public class QuaternionFields extends JPanel {
	
	private JTextField q0Fld;
	private JTextField q1Fld;
	private JTextField q2Fld;
	private JTextField q3Fld;


	public QuaternionFields() {
		setLayout(new MigLayout("insets 0", "[][][][]", "[][][]"));
		
		JLabel q0Lbl = new JLabel("<html>q<sub>0</sub> =");
		add(q0Lbl, "cell 0 0,alignx right");
		
		q0Fld = new JTextField();
		q0Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(q0Fld, "cell 1 0,alignx left");
		q0Fld.setColumns(8);
		
		JLabel q1Lbl = new JLabel("<html>q<sub>1</sub> =");		
		add(q1Lbl, "cell 2 0,alignx trailing,gapx 20");
		
		q1Fld = new JTextField();
		q1Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(q1Fld, "cell 3 0,alignx left");
		q1Fld.setColumns(8);
		
		JLabel q2Lbl = new JLabel("<html>q<sub>2</sub> =");
		add(q2Lbl, "cell 2 1,alignx trailing");
		
		q2Fld = new JTextField();
		q2Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(q2Fld, "cell 3 1,alignx left");
		q2Fld.setColumns(8);
		
		JLabel q3Lbl = new JLabel("<html>q<sub>3</sub> =");
		add(q3Lbl, "cell 2 2,alignx trailing");
		
		q3Fld = new JTextField();
		q3Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(q3Fld, "cell 3 2,alignx left");
		q3Fld.setColumns(8);

	}
	
	public final Quaternion getQuaternion() {
		
		Quaternion quat = new Quaternion();
		
		String qStr = q0Fld.getText().replace(",", ".");				
		double q0 = Double.parseDouble(qStr);
		
		qStr = q1Fld.getText().replace(",", ".");				
		double q1 = Double.parseDouble(qStr);
		
		qStr = q2Fld.getText().replace(",", ".");				
		double q2 = Double.parseDouble(qStr);
		
		qStr = q3Fld.getText().replace(",", ".");				
		double q3 = Double.parseDouble(qStr);
				
		quat.set(q0, q1, q2, q3);
		
		return quat;
	}
	
		
	public final void setQuaternion(Quaternion quat) {
				
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);		
		final DecimalFormat df = new DecimalFormat("0.#####", otherSymbols);
			
		
		q0Fld.setText(df.format(quat.q0()));	
		q1Fld.setText(df.format(quat.q1()));
		q2Fld.setText(df.format(quat.q2()));
		q3Fld.setText(df.format(quat.q3()));
		
		q0Fld.setCaretPosition(0);		
		q1Fld.setCaretPosition(0);
		q2Fld.setCaretPosition(0);
		q3Fld.setCaretPosition(0);
	}
	
	public final void clear() {
				
		q0Fld.setText("");	
		q1Fld.setText("");
		q2Fld.setText("");
		q3Fld.setText("");
	}

}
