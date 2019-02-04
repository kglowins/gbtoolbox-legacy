package gui_bricks;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class GBCD_GBPD_BinsOptsPanel extends JPanel {
	private JTextField D1Fld;
	private JTextField D2Fld;

	public GBCD_GBPD_BinsOptsPanel() {
		setLayout(new MigLayout("insets 0", "[][]", "[][]"));
		
		JLabel D1Lbl = new JLabel("Number of bins (per \u03C0/2) for misorientation Euler angles:");
		add(D1Lbl, "flowx,cell 0 0,alignx left,gapx 0 20");
		
		D1Fld = new JTextField();
		D1Fld.setText("9");
		D1Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(D1Fld, "cell 1 0,alignx left");
		D1Fld.setColumns(4);
		
		JLabel D2Lbl = new JLabel("Number of bins (per \u03C0/2) for direction spherical angles:");
		add(D2Lbl, "flowx,cell 0 1,alignx left");
		
		D2Fld = new JTextField();
		D2Fld.setText("9");
		D2Fld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(D2Fld, "cell 1 1,alignx left");
		D2Fld.setColumns(4);
	}	
	
	public final int getD1() {
		
		return Integer.parseInt(D1Fld.getText());
	}	
	
	public final int getD2() {
		
		return Integer.parseInt(D2Fld.getText());
	}
	

}
