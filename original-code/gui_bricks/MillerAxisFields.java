package gui_bricks;


import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;


public class MillerAxisFields extends JPanel {
	
	private JTextField hFld;
	private JTextField kFld;
	private JTextField lFld;
	
	
	public final JTextField getHFld() {
		return hFld;
	}
	
	public final JTextField getKFld() {
		return kFld;
	}

	public final JTextField getLFld() {
		return lFld;
	}


	public final void setIndices(final int h, final int k, final int l) {
		hFld.setText( Integer.toString(h));	
		kFld.setText( Integer.toString(k));
		lFld.setText( Integer.toString(l));
		
		hFld.setCaretPosition(0);
		kFld.setCaretPosition(0);
		lFld.setCaretPosition(0);
	}
	
	public final void clear() {
		hFld.setText("");	
		kFld.setText("");
		lFld.setText("");		
	}
	
	
	public MillerAxisFields() {
		setLayout(new MigLayout("insets 0", "[][][][][]", "[]"));
		
		JLabel lblBra1 = new JLabel("[");
		add(lblBra1, "cell 0 0,alignx left");
		
		hFld = new JTextField();
		hFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(hFld, "cell 1 0,alignx left");
		hFld.setColumns(3);
		
		kFld = new JTextField();
		kFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(kFld, "cell 2 0,alignx left");
		kFld.setColumns(3);
		
		lFld = new JTextField();
		lFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lFld, "cell 3 0,alignx left");
		lFld.setColumns(3);
		
		JLabel lblBra2 = new JLabel("]");
		add(lblBra2, "cell 4 0,alignx left");

	}

}
