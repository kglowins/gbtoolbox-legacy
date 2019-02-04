package gui_bricks;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import utils.RodriguesParams;


public class RodriguesParamsFields extends JPanel {
	
	private JTextField r1Fld;
	private JTextField r2Fld;
	private JTextField r3Fld;

	public RodriguesParamsFields() {
		setLayout(new MigLayout("insets 0", "[][]", "[][][]"));
		
		JLabel r1Lbl = new JLabel("<html>r<sub>1</sub> =");
		add(r1Lbl, "cell 0 0,alignx trailing");
		
		r1Fld = new JTextField();
		r1Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(r1Fld, "cell 1 0,alignx left");
		r1Fld.setColumns(8);
		
		JLabel r2Lbl = new JLabel("<html>r<sub>2</sub> =");
		add(r2Lbl, "cell 0 1,alignx trailing");
		
		r2Fld = new JTextField();
		r2Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(r2Fld, "cell 1 1,alignx left");
		r2Fld.setColumns(8);
		
		JLabel r3Lbl = new JLabel("<html>r<sub>3</sub> =");
		add(r3Lbl, "cell 0 2,alignx trailing");
		
		r3Fld = new JTextField();
		r3Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(r3Fld, "cell 1 2,alignx left");
		r3Fld.setColumns(8);

	}
	
	
	public final RodriguesParams getRodrigues() {
		
		RodriguesParams rod = new RodriguesParams();
		
		String rStr = r1Fld.getText().replace(",", ".");		
		double r1 = Double.parseDouble(rStr);
		
		rStr = r2Fld.getText().replace(",", ".");
		double r2 = Double.parseDouble(rStr);
		
		rStr = r3Fld.getText().replace(",", ".");
		double r3 = Double.parseDouble(rStr);
						
		rod.set(r1, r2, r3);
		
		return rod;
	}
	
		
	public final void setRodrigues(RodriguesParams rod) {
				
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);		
		final DecimalFormat df = new DecimalFormat("0.#####", otherSymbols);
		
		r1Fld.setText(df.format(rod.r1()));	
		r2Fld.setText(df.format(rod.r2()));
		r3Fld.setText(df.format(rod.r3()));
		
		r1Fld.setCaretPosition(0);		
		r2Fld.setCaretPosition(0);
		r3Fld.setCaretPosition(0);
	}
	
	
	public final void clear() {
		r1Fld.setText("");	
		r2Fld.setText("");
		r3Fld.setText("");		
	}

}
