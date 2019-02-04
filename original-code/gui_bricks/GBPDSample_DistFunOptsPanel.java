package gui_bricks;



import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;



public class GBPDSample_DistFunOptsPanel extends JPanel {
	
	private static final String AXISX = "X";
	private static final String AXISY = "Y";
	private static final String AXISZ = "Z";
	
	private JTextField nBinsFld;
	private JLabel tol2Lbl;
	private JTextField planeTolFld;
	private JLabel deg2Lbl;
	private JLabel axisLbl;
	private JComboBox axisCb;

	public GBPDSample_DistFunOptsPanel() {
		setLayout(new MigLayout("insets 0", "[]", "[][][]"));
		
		tol2Lbl = new JLabel("Tolerance for boundary plane normals:");
		add(tol2Lbl, "flowx,cell 0 0");
		
		JLabel nBinsLbl = new JLabel("Number of sampling directions:");
		nBinsLbl.setToolTipText("Distributions will be computed for a given number of direcions spread uniformly on a hemi-sphere");
		add(nBinsLbl, "flowx,cell 0 1,alignx left");
		
		nBinsFld = new JTextField();
		nBinsFld.setHorizontalAlignment(SwingConstants.RIGHT);
		nBinsFld.setText("4000");
		add(nBinsFld, "cell 0 1,alignx left");
		nBinsFld.setColumns(6);
		
		JLabel info1Lbl = new JLabel("(on the upper hemi-sphere)");
		add(info1Lbl, "cell 0 1,alignx left,gapx 30");
		
		planeTolFld = new JTextField();
		planeTolFld.setText("7");
		planeTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		planeTolFld.setColumns(4);
		add(planeTolFld, "cell 0 0");
		
		deg2Lbl = new JLabel("\u00B0");
		add(deg2Lbl, "cell 0 0");
		
		axisLbl = new JLabel("Projection along axis:");
		add(axisLbl, "flowx,cell 0 2");
		
		axisCb = new JComboBox();
		axisCb.setModel(new DefaultComboBoxModel(new String[] {AXISX, AXISY, AXISZ}));
		add(axisCb, "cell 0 2");
		axisCb.setSelectedIndex(2);
	
				
	}	
	
	
	public final double getPlaneTol() {
		
		return Math.toRadians(Double.parseDouble(planeTolFld.getText().replace(",", ".")));
	}	
	
	public final int getNBins() {
		
		return Integer.parseInt(nBinsFld.getText());
	}
	
	public final int getAxis() {
		
		switch(axisCb.getSelectedIndex()) {
		case 0: return 0;
		case 1: return 1;
		default: return 2;
		}
	}
	
}
