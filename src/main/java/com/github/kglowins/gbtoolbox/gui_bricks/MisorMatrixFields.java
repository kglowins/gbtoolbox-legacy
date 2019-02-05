package com.github.kglowins.gbtoolbox.gui_bricks;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;


public class MisorMatrixFields extends JPanel {
	
	private JTextField m00Fld;
	private JTextField m10Fld;
	private JTextField m20Fld;
	private JTextField m01Fld;
	private JTextField m11Fld;
	private JTextField m21Fld;
	private JTextField m02Fld;
	private JTextField m12Fld;
	private JTextField m22Fld;

	public MisorMatrixFields() {
		setLayout(new MigLayout("insets 0", "[][][][]", "[][][]"));
		
		m00Fld = new JTextField();
		m00Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m00Fld, "cell 1 0");
		m00Fld.setColumns(8);
		
		m01Fld = new JTextField();
		m01Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m01Fld, "cell 2 0");
		m01Fld.setColumns(8);
		
		m02Fld = new JTextField();
		m02Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m02Fld, "cell 3 0");
		m02Fld.setColumns(8);
		
		JLabel lblM = new JLabel("M = ");
		add(lblM, "cell 0 1");
		
		m10Fld = new JTextField();
		m10Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m10Fld, "cell 1 1");
		m10Fld.setColumns(8);
		
		m11Fld = new JTextField();
		m11Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m11Fld, "cell 2 1");
		m11Fld.setColumns(8);
		
		m12Fld = new JTextField();
		m12Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m12Fld, "cell 3 1");
		m12Fld.setColumns(8);
		
		m20Fld = new JTextField();
		m20Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m20Fld, "cell 1 2");
		m20Fld.setColumns(8);
		
		m21Fld = new JTextField();
		m21Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m21Fld, "cell 2 2");
		m21Fld.setColumns(8);
		
		m22Fld = new JTextField();
		m22Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(m22Fld, "cell 3 2");
		m22Fld.setColumns(8);

	}

	
	public final Matrix3x3 getMatrix() {
		
		Matrix3x3 M = new Matrix3x3();	
				
		String s = m00Fld.getText().replace(",", ".");
		M.set(0, 0, Double.parseDouble(s));
		
		s = m01Fld.getText().replace(",", ".");
		M.set(0, 1, Double.parseDouble(s));
		
		s = m02Fld.getText().replace(",", ".");
		M.set(0, 2, Double.parseDouble(s));
		
		s = m10Fld.getText().replace(",", ".");
		M.set(1, 0, Double.parseDouble(s));
		
		s = m11Fld.getText().replace(",", ".");
		M.set(1, 1, Double.parseDouble(s));
		
		s = m12Fld.getText().replace(",", ".");
		M.set(1, 2, Double.parseDouble(s));
		
		s = m20Fld.getText().replace(",", ".");
		M.set(2, 0, Double.parseDouble(s));
		
		s = m21Fld.getText().replace(",", ".");
		M.set(2, 1, Double.parseDouble(s));
		
		s = m22Fld.getText().replace(",", ".");		
		M.set(2, 2, Double.parseDouble(s));

	//	System.out.println(M.nearestOrthogonal());
	//	System.out.println( M.distSq( M.nearestOrthogonal() ));
	//	System.out.println(M.nearestOrthogonal().det());
		return M;
	}
	
		
	public final void setMatrix(Matrix3x3 M) {
				
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		
		final DecimalFormat df = new DecimalFormat("0.#####", otherSymbols);
		
		m00Fld.setText(df.format(M.get(0, 0)));
		m00Fld.setCaretPosition(0);
		
		m01Fld.setText(df.format(M.get(0, 1)));
		m01Fld.setCaretPosition(0);
		
		m02Fld.setText(df.format(M.get(0, 2)));
		m02Fld.setCaretPosition(0);
		
		m10Fld.setText(df.format(M.get(1, 0)));
		m10Fld.setCaretPosition(0);
		
		m11Fld.setText(df.format(M.get(1, 1)));
		m11Fld.setCaretPosition(0);
		
		m12Fld.setText(df.format(M.get(1, 2)));
		m12Fld.setCaretPosition(0);
		
		m20Fld.setText(df.format(M.get(2, 0)));
		m20Fld.setCaretPosition(0);
		
		m21Fld.setText(df.format(M.get(2, 1)));
		m21Fld.setCaretPosition(0);
		
		m22Fld.setText(df.format(M.get(2, 2)));
		m22Fld.setCaretPosition(0);			
	}
	
	public final void clear() {
		
		m00Fld.setText("");
		m01Fld.setText("");
		m02Fld.setText("");
		
		m10Fld.setText("");
		m11Fld.setText("");
		m12Fld.setText("");
		
		m20Fld.setText("");
		m21Fld.setText("");
		m22Fld.setText("");		
	}
	
}
