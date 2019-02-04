package gui_bricks;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import enums.PointGroup;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

public class GBCD_DistFunOptsPanel extends JPanel {
	private JTextField misTolFld;
	private JTextField nBinsFld;
	private JLabel degLbl;
	private JLabel tol2Lbl;
	private JTextField planeTolFld;
	private JLabel deg2Lbl;
	private JComboBox rhoCb;
	private JLabel noteLbl;
	
	private boolean normalize;
	
	private static final String USE37 = "<html>Use \u03C1<sub>m</sub> = 3\u00b0 and \u03C1<sub>p</sub> = 7\u00b0";
	private static final String USE55 = "<html>Use \u03C1<sub>m</sub> = 5\u00b0 and \u03C1<sub>p</sub> = 5\u00b0";
	private static final String USE58 = "<html>Use \u03C1<sub>m</sub> = 5\u00b0 and \u03C1<sub>p</sub> = 8\u00b0";
	
	private static final String RHOUSER = "<html>Use user-specified limiting distances \u03C1<sub>m</sub> and \u03C1<sub>p</sub>";

	public GBCD_DistFunOptsPanel(PointGroup ptGrp) {
		
		
		normalize = true;
		
		setLayout(new MigLayout("insets 0", "[grow]", "[][][][][]"));
		
		rhoCb = new JComboBox();
		rhoCb.setModel(new DefaultComboBoxModel(new String[] {USE37, USE55, USE58, RHOUSER}));
		add(rhoCb, "cell 0 0");
		
		rhoCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JComboBox cb = (JComboBox) e.getSource();
                String sel = cb.getSelectedItem().toString();
                
    	
            	switch(sel) {
            	case USE37: 
            		planeTolFld.setText("7");
            		misTolFld.setText("3");
            		planeTolFld.setEditable(false);
            		misTolFld.setEditable(false);
            		normalize = true;
            		break;
            
            		
            	case USE55: 
            		planeTolFld.setText("5");
            		misTolFld.setText("5");
            		planeTolFld.setEditable(false);
            		misTolFld.setEditable(false);
            		normalize = true;
            		break;
            		
            	case USE58: 
            		planeTolFld.setText("8");
            		misTolFld.setText("5");
            		planeTolFld.setEditable(false);
            		misTolFld.setEditable(false);
            		normalize = true;
            		break;
            	
            	case RHOUSER:
            		planeTolFld.setEditable(true);
            		misTolFld.setEditable(true);
            		normalize = false;
            		break;
            		
            	default: break;
            	}
            	
            }
		});
		
		JLabel tolLbl = new JLabel("<html>Misorientation tolerace \u03C1<sub>m</sub> =");
		add(tolLbl, "flowx,cell 0 1,alignx left,gapx 10");
		
		tol2Lbl = new JLabel("<html>Tolerance for boundary plane normals \u03C1<sub>p</sub> =");
		add(tol2Lbl, "flowx,cell 0 2,gapx 10");
		
		JLabel nBinsLbl = new JLabel("Number of sampling directions:");
		nBinsLbl.setToolTipText("Distributions will be computed for a given number of direcions spread uniformly on a hemi-sphere");
		add(nBinsLbl, "flowx,cell 0 3,alignx left");
		
		misTolFld = new JTextField();
		misTolFld.setEditable(false);
		misTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		misTolFld.setText("3");
		add(misTolFld, "cell 0 1,alignx left");
		misTolFld.setColumns(4);
		
		nBinsFld = new JTextField();
		nBinsFld.setHorizontalAlignment(SwingConstants.RIGHT);
		nBinsFld.setText("4000");
		add(nBinsFld, "cell 0 3,alignx left");
		nBinsFld.setColumns(6);
		
		JLabel info1Lbl = new JLabel("(on the upper hemi-sphere)");
		add(info1Lbl, "cell 0 3,alignx left,gapx 30");
		
		degLbl = new JLabel("\u00b0");
		add(degLbl, "cell 0 1");
		
		planeTolFld = new JTextField();
		planeTolFld.setEditable(false);
		planeTolFld.setText("7");
		planeTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		planeTolFld.setColumns(4);
		add(planeTolFld, "cell 0 2");
		
		deg2Lbl = new JLabel("\u00B0");
		add(deg2Lbl, "cell 0 2");
		
		noteLbl = new JLabel("<html><font color=#0000bb><b>Note:</b> If you do not use standard \u03C1<sub>m</sub> and \u03C1<sub>m</sub>,<br>\r\nyou will have to normalize the obtained distributions manually using the adequate module.</font>");
		add(noteLbl, "cell 0 4,aligny top,gapy 10");
		
	
		
	/*	misTolFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				if(normChB.isSelected()) {
					try {
						
						int roundedTolMis = Integer.parseInt(misTolFld.getText());					
						if(roundedTolMis != 1 && roundedTolMis != 2
								&& roundedTolMis != 3 && roundedTolMis != 5) throw new NumberFormatException();
												
					} catch(NumberFormatException exc) {
					
						JOptionPane.showMessageDialog(null,
						    "If you wish to normalize the distribution by the volume of a ball,\n" +
						    "You can use only values 1, 2, 3 and 5 for misorientation tolerance.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
						misTolFld.requestFocus();
						return;
					}
				}								
			}
		});
		
		planeTolFld.addFocusListener(new FocusListener() {
			@Override
			public void focusGained(FocusEvent e) {
				
			}
			
			@Override
			public void focusLost(FocusEvent e) {
				
				if(normChB.isSelected()) {
					try {
						
						int roundedTolPlane = Integer.parseInt(planeTolFld.getText());
						if(roundedTolPlane < 3 && roundedTolPlane > 10) throw new NumberFormatException();
						
					} catch(NumberFormatException exc) {
					
						JOptionPane.showMessageDialog(null,
						    "If you wish to normalize the distribution by the volume of a ball,\n" +
						    "You can use only discrete values from 3 to 10 for plane tolerance.",
						    "Warning",
						    JOptionPane.WARNING_MESSAGE);
						planeTolFld.requestFocus();
						return;
					}
				}								
			}
		});*/
	}	
	
	public final double getMisorTol() {
		
		return Math.toRadians(Double.parseDouble(misTolFld.getText().replace(",", ".")));
	}	
	
	public final double getPlaneTol() {
		
		return Math.toRadians(Double.parseDouble(planeTolFld.getText().replace(",", ".")));
	}	
	
	public final int getNBins() {
		
		return Integer.parseInt(nBinsFld.getText());
	}
	
	public final boolean doNormalize() {
		return normalize;
	}
	
}

