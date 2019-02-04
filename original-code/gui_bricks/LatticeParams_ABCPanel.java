package gui_bricks;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class LatticeParams_ABCPanel extends JPanel {
	private JTextField aFld;
	private JTextField cFld;
	private JTextField bFld;
	private JLabel angLbl;
	
	public LatticeParams_ABCPanel() {
		setLayout(new MigLayout("insets 0", "[][][][][][][]", "[]"));
		
		JLabel aLbl = new JLabel("a =");
		add(aLbl, "cell 0 0,alignx trailing");
		
		aFld = new JTextField();
		aFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(aFld, "cell 1 0,growx");
		aFld.setColumns(5);
		
		JLabel bLbl = new JLabel("b =");
		add(bLbl, "cell 2 0,alignx trailing,gapx 10");
		
		bFld = new JTextField();
		bFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(bFld, "cell 3 0,growx");
		bFld.setColumns(5);
		
		JLabel cLbl = new JLabel("c =");
		add(cLbl, "cell 4 0,alignx trailing,gapx 10");
		
		cFld = new JTextField();
		cFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(cFld, "cell 5 0,growx");
		cFld.setColumns(5);
		
		angLbl = new JLabel("[\u212B]");
		add(angLbl, "cell 6 0,gapx 30");

	}
	
	
	public final JTextField getaFld() {
		return aFld;
	}

	public final JTextField getbFld() {
		return bFld;
	}
	
	public final JTextField getcFld() {
		return cFld;
	}
}
