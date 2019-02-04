package gui_bricks;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;

public class LatticeParams_CDivAPanel extends JPanel {
	private JTextField mFld;
	private JTextField nFld;


	public LatticeParams_CDivAPanel() {
		setLayout(new MigLayout("insets 0", "[][][][][]", "[]"));
		
		JLabel caLbl = new JLabel("<html>(c/a)\u00B2 =");
		add(caLbl, "cell 0 0");
		
		mFld = new JTextField();
		mFld.setText("8");
		mFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(mFld, "cell 1 0,growx");
		mFld.setColumns(3);
		
		JLabel divLbl = new JLabel("/");
		add(divLbl, "cell 2 0");
		
		nFld = new JTextField();
		nFld.setText("3");
		nFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(nFld, "cell 3 0,growx");
		nFld.setColumns(3);
		
		JLabel intLbl = new JLabel("[integers]");
		add(intLbl, "cell 4 0,gapx 30");

	}

	public final JTextField getmFld() {
		return mFld;
	}

	public final JTextField getnFld() {
		return nFld;
	}
	
	

}
