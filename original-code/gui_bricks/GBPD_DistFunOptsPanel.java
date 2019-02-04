package gui_bricks;



import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;



public class GBPD_DistFunOptsPanel extends JPanel {
	
	private JTextField nBinsFld;
	private JLabel tol2Lbl;
	private JTextField planeTolFld;
	private JLabel deg2Lbl;

	public GBPD_DistFunOptsPanel(boolean misax) {
		setLayout(new MigLayout("insets 0", "[]", "[][]"));
		
		tol2Lbl = new JLabel();
		if(misax) tol2Lbl.setText("Resolution for misorientation axes:");
		else tol2Lbl.setText("Tolerance for boundary plane normals:");
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
		
	
				
	}	
	
	
	public final double getPlaneTol() {
		
		return Math.toRadians(Double.parseDouble(planeTolFld.getText().replace(",", ".")));
	}	
	
	public final int getNBins() {
		
		return Integer.parseInt(nBinsFld.getText());
	}
	
	
}
