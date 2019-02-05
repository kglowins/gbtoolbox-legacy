package com.github.kglowins.gbtoolbox.gui_bricks;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;


public class HexMillerPlaneFields extends JPanel {
	private JTextField hFld;
	private JTextField kFld;
	private JTextField lFld;
	private JTextField iFld;

	
	public final JTextField getIFld() {
		return iFld;
	}
	
	public final JTextField getHFld() {
		return hFld;
	}
	
	public final JTextField getKFld() {
		return kFld;
	}

	public final JTextField getLFld() {
		return lFld;
	}
	
	private int globalH;
	private int globalK;
	
	
	public final void setIndices(final int h, final int k, final int l) {
		hFld.setText( Integer.toString(h));
		globalH = h;		
		kFld.setText( Integer.toString(k));
		globalK = k;
		iFld.setText( Integer.toString(-(h+k)));
		lFld.setText( Integer.toString(l));
		
		hFld.setCaretPosition(0);
		kFld.setCaretPosition(0);
		iFld.setCaretPosition(0);
		lFld.setCaretPosition(0);
	}
	
	
	public HexMillerPlaneFields() {
		
		globalH = 0;
		globalK = 0;
		
		setLayout(new MigLayout("insets 0", "[][][][][][]", "[]"));
		
		JLabel lblBra1 = new JLabel("(");
		add(lblBra1, "cell 0 0");
		
		hFld = new JTextField();
		hFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(hFld, "cell 1 0");
		hFld.setColumns(3);
		
		kFld = new JTextField();
		kFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(kFld, "cell 2 0");
		kFld.setColumns(3);
		
		iFld = new JTextField();
		iFld.setEditable(false);
		iFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(iFld, "cell 3 0");
		iFld.setColumns(3);
		
		lFld = new JTextField();
		lFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(lFld, "cell 4 0,growx");
		lFld.setColumns(3);
		
		JLabel lblBra2 = new JLabel(")");
		add(lblBra2, "cell 5 0");
		
		
		hFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				try {
					int h = 0;
					if(hFld.getText().length() > 0) h = Integer.parseInt(hFld.getText());
					
					globalH = h;
										
					if(hFld.getText().length() > 0 || kFld.getText().length() > 0) iFld.setText(Integer.toString(-(globalH+globalK)));
					else iFld.setText("");
					
				} catch(NumberFormatException exc) {
					
					JOptionPane.showMessageDialog(null,
						    "Miller indices should be integers.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					hFld.requestFocus();
					return;
				}
			}
		});
		
		kFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				try {
					int k = 0;
					if(kFld.getText().length() > 0) k = Integer.parseInt(kFld.getText());
					
					globalK = k;
										
					if(hFld.getText().length() > 0 || kFld.getText().length() > 0) iFld.setText(Integer.toString(-(globalH+globalK)));
					else iFld.setText("");

					
				} catch(NumberFormatException exc) {
					
					JOptionPane.showMessageDialog(null,
						    "Miller indices should be integers.",
						    "Error",
						    JOptionPane.ERROR_MESSAGE);
					kFld.requestFocus();
					return;
				}
			}
		});

	}

}
