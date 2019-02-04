package gui_bricks;
import java.awt.CardLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import net.miginfocom.swing.MigLayout;
import enums.PointGroup;



public class LatticePanel extends JPanel {
	
	private JComboBox groupCb;	
		
	// constants
	private static final String M3M = "m3\u0305m";
	private static final String _6MMM = "6/mmm";
	private static final String _4MMM = "4/mmm";
	private static final String MMM = "mmm";	
	
	// forms for lattice details input
	private JPanel detailsCards;
	
	private LatticeParams_NonePanel noDetails; // m3m
	private LatticeParams_CDivAPanel cDivA; // 6/mmm 
	private LatticeParams_ABCPanel abcPane; // mmm
	private LatticeParams_ACPanel acPane; // 4/mmm
	
	private PointGroup ptGrp;
	private BoundaryParamsPanel boundaryParamsPane;
	private MisorPanel misorPane;
	
	private TestCSLOptsPanel cslOpts;
	
	public final void setParamsReference(BoundaryParamsPanel boundaryParamsPane) {
		this.boundaryParamsPane = boundaryParamsPane;
	}
	
	public final void setMisorReference(MisorPanel misorPane) {
		this.misorPane = misorPane;
	}
		
	
	public final void setPointGroupLocked(PointGroup ptGrp) {
		//this.ptGrp = ptGrp;
		int grpIdx;
		switch(ptGrp) {
		case M3M: grpIdx = 0; break;
		case _6MMM: grpIdx = 1; break;
		case _4MMM: grpIdx = 2; break;
		case MMM: grpIdx = 3; break;
		default: throw new IllegalArgumentException("Lattice: Unknown point group");
		}
		groupCb.setSelectedIndex(grpIdx);
		groupCb.setEnabled(false);
	}
	
	public final void setCSLOpts(TestCSLOptsPanel cslOpts) {
		this.cslOpts = cslOpts;		
	}
	
	public final TestCSLOptsPanel getCSLOpts() {
		return cslOpts;
	}

	
	public LatticePanel(BoundaryParamsPanel boundaryParams, MisorPanel misor) {
		
			
		// initial values
		cslOpts = null;
		noDetails = new LatticeParams_NonePanel();
		noDetails.setToolTipText("No details need to be specified for this point group");
		cDivA = new LatticeParams_CDivAPanel();
		abcPane = new LatticeParams_ABCPanel();
		acPane = new LatticeParams_ACPanel();
		ptGrp = PointGroup.M3M;
		boundaryParamsPane = boundaryParams;
		misorPane = misor;
		
			
		setLayout(new MigLayout("insets 0", "[]", "[][]"));
				
		JLabel groupLbl = new JLabel("Point group:");
		groupLbl.setToolTipText("<html>By choosing a point group, the set of symmetry transformations is determined;<br>The same crystal lattice is assumed for both adjacent grains");
		add(groupLbl, "flowx,cell 0 0,alignx left");
		
		groupCb = new JComboBox();
		groupCb.setToolTipText("<html>By choosing a point group, the set of symmetry transformations is determined;<br>The same crystal lattice is assumed for both adjacent grains");
		groupCb.setModel(new DefaultComboBoxModel(new String[] {M3M, _6MMM, _4MMM, MMM}));
		
		groupCb.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
                JComboBox cb = (JComboBox) e.getSource();
                String sel = cb.getSelectedItem().toString();
                
                CardLayout cl = (CardLayout) detailsCards.getLayout();
            	cl.show(detailsCards, sel);
            	
            	if(sel.compareTo(_6MMM) == 0) {
            		if(boundaryParamsPane != null) boundaryParamsPane.setFourIndicesVisible(true);
            		if(misorPane != null) misorPane.setFourIndicesVisible(true);
            	} else {
            		if(boundaryParamsPane != null) boundaryParamsPane.setFourIndicesVisible(false);
            		if(misorPane != null) misorPane.setFourIndicesVisible(false);
            	}
            	
            	
            	
            	
            	switch(sel) {
            	case M3M:
            		ptGrp = PointGroup.M3M; 
            		if(cslOpts != null) cslOpts.setCSLLocked(false);
            		break;
            	case _6MMM:
            		ptGrp = PointGroup._6MMM; 
            		cDivA.getmFld().setText("8");
            		cDivA.getnFld().setText("3");
            		if(cslOpts != null) cslOpts.setCSLLocked(false);
            		break;
            	case _4MMM:
            		ptGrp = PointGroup._4MMM; 
            		acPane.getaFld().setText("1");
            		acPane.getcFld().setText("1");
            		if(cslOpts != null) cslOpts.setCSLLocked(true);
            		break;
            		
            	case MMM:
            		ptGrp = PointGroup.MMM; 
            		abcPane.getaFld().setText("1");
            		abcPane.getbFld().setText("1");
            		abcPane.getcFld().setText("1");            		
            		if(cslOpts != null) cslOpts.setCSLLocked(true);
            		break;
            		
            		default:
            			break;
            	}
                                
            }
        });
		
		add(groupCb, "cell 0 0,alignx left");
		
		JLabel constLbl = new JLabel("Lattice:");
		add(constLbl, "flowx,cell 0 1");
		
		detailsCards = new JPanel();
		add(detailsCards, "cell 0 1");
		detailsCards.setLayout(new CardLayout(0, 0));
		detailsCards.add(noDetails, M3M);
		detailsCards.add(cDivA, _6MMM);
		detailsCards.add(acPane, _4MMM);
		detailsCards.add(abcPane, MMM);

	}
	

	public PointGroup getPointGroup() {
		return ptGrp;
	}
	
	public final LatticeParams_CDivAPanel getCDivAPane() {
		return cDivA;
	}
	
	public final LatticeParams_ACPanel getACPane() {
		return acPane;
	}
	
	public final LatticeParams_ABCPanel getABCPane() {
		return abcPane;
	}

}
