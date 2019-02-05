package com.github.kglowins.gbtoolbox.gui_bricks;

import java.awt.CardLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.MillerIndices;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;
import com.github.kglowins.gbtoolbox.algorithms.RandomGBGenerator;



public class BoundaryParamsPanel extends JPanel {

	private MillerPlaneFields planeFlds;
	private HexMillerPlaneFields hexPlaneFlds;
	private JPanel planeCards;
	
	private MisorPanel misorPane;
	private JLabel millerLbl;
	
	private static final String HEX = "HEX";
	private static final String NON_HEX = "NON_HEX";
	
	private boolean fourIndices;
		
	LatticePanel latticePane;
	
	public BoundaryParamsPanel(LatticePanel latticePane, JTextField maxIndex) {
		
		this.latticePane = latticePane;
		fourIndices = false;
		
		setLayout(new MigLayout("insets 0", "[]", "[][][][]"));
		
		misorPane = new MisorPanel("Misorientation:", latticePane, maxIndex);
		add(misorPane, "cell 0 0 1 3");
		
		final RandomGBGenerator gbGen = new RandomGBGenerator();
		
		JLabel lblBoundaryPlane = new JLabel("Boundary plane:");
		lblBoundaryPlane.setToolTipText("Miller indices of the boundary plane in the reference frame of the first crystallite");
		add(lblBoundaryPlane, "flowx,cell 0 3");
		
		planeCards = new JPanel();
		add(planeCards, "cell 0 3");
		planeCards.setLayout(new CardLayout(0, 0));
		
		planeFlds = new MillerPlaneFields();
		planeFlds.getKFld().setToolTipText("Miller indices of the boundary plane in the reference frame of the first crystallite");
		hexPlaneFlds = new HexMillerPlaneFields();
		
		planeCards.add(planeFlds, NON_HEX);
		planeCards.add(hexPlaneFlds, HEX);
		
		millerLbl = new JLabel("[Miller indices]");
		add(millerLbl, "cell 0 3,gapx 30");
				
	}

	public final void setFourIndicesVisible(final boolean b) {
		
		misorPane.setFourIndicesVisible(b);
		
		if(b && !fourIndices) {	
            CardLayout cl = (CardLayout) planeCards.getLayout();
            cl.show(planeCards, HEX);                
            hexPlaneFlds.getHFld().setText("");
            hexPlaneFlds.getKFld().setText("");
            hexPlaneFlds.getIFld().setText("");
            hexPlaneFlds.getLFld().setText("");
		} else if(!b && fourIndices) {
			CardLayout cl = (CardLayout) planeCards.getLayout();
			cl.show(planeCards, NON_HEX);
			planeFlds.getHFld().setText("");
			planeFlds.getKFld().setText("");
			planeFlds.getLFld().setText("");           
		}		
		
		fourIndices = b;
	}
	

	public final MisorPanel getMisorPane() {
		return misorPane;
	}
	
		
	public final UnitVector getPlaneNormal() {
		
		UnitVector normal = new UnitVector();
		
		switch(latticePane.getPointGroup()) {
			
		case M3M:
			int h = Integer.parseInt(planeFlds.getHFld().getText());
			int k = Integer.parseInt(planeFlds.getKFld().getText());
			int l = Integer.parseInt(planeFlds.getLFld().getText());
			
			normal.set(h, k, l);
			break;
			
			
		case _6MMM:
			h = Integer.parseInt(hexPlaneFlds.getHFld().getText());
			k = Integer.parseInt(hexPlaneFlds.getKFld().getText());
			l = Integer.parseInt(hexPlaneFlds.getLFld().getText());
			
			int a0Sq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
			int c0Sq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
			
			if(a0Sq <= 0 || c0Sq <= 0) throw new NumberFormatException();
			
			double a0 = Math.sqrt(a0Sq);
			double c0 = Math.sqrt(c0Sq);
			
			MillerIndices planeMiller = new MillerIndices();
			planeMiller.set(h, k, l);
			
			normal.setAsHexagonalPlane(planeMiller, a0, c0);
			break;
			
		case _4MMM:
			
			h = Integer.parseInt(planeFlds.getHFld().getText());
			k = Integer.parseInt(planeFlds.getKFld().getText());
			l = Integer.parseInt(planeFlds.getLFld().getText());
						
			a0 = Double.parseDouble(latticePane.getACPane().getaFld().getText().replace(",", "."));
			c0 = Double.parseDouble(latticePane.getACPane().getcFld().getText().replace(",", "."));
			
			if(a0 <= 0d || c0 <= 0d) throw new NumberFormatException();
			
			planeMiller = new MillerIndices();
			planeMiller.set(h, k, l);
			
			normal.setAsTetragonalPlane(planeMiller, a0, c0);
			break;
			
		case MMM:
			h = Integer.parseInt(planeFlds.getHFld().getText());
			k = Integer.parseInt(planeFlds.getKFld().getText());
			l = Integer.parseInt(planeFlds.getLFld().getText());
			
			a0 = Double.parseDouble(latticePane.getABCPane().getaFld().getText().replace(",", "."));
			double b0 = Double.parseDouble(latticePane.getABCPane().getbFld().getText().replace(",", "."));
			c0 = Double.parseDouble(latticePane.getABCPane().getcFld().getText().replace(",", "."));
			
			if(a0 <= 0d || b0 <= 0d ||  c0 <= 0d) throw new NumberFormatException();
			
			planeMiller = new MillerIndices();
			planeMiller.set(h, k, l);
			
			normal.setAsOrthorombicPlane(planeMiller, a0, b0, c0);
			break;
			
			default: break;
			
		}
				
		return normal;
	}
	
		
	public void setPlane(UnitVector normal, int maxIndex) {
		
		switch(latticePane.getPointGroup()) {
		
		case M3M:
			MillerIndices planeMiller = new MillerIndices();
			planeMiller.setAsCubic(normal, maxIndex);						
			planeFlds.setIndices(planeMiller.h(), planeMiller.k(), planeMiller.l());
			break;
			
		case _6MMM:
			int aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
			int cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
			
			if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
			
			double a = Math.sqrt(aSq);
			double c = Math.sqrt(cSq);
		//	double c = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getmFld().getText()));				
		//	double a = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getnFld().getText()));
				
			planeMiller = new MillerIndices();
			planeMiller.setAsNonCubicPlane(normal, maxIndex, Transformations.getHexToCartesian(a, c)); 				
			hexPlaneFlds.setIndices(planeMiller.h(), planeMiller.k(), planeMiller.l());
			break;
			
		case _4MMM:
			c = Double.parseDouble(latticePane.getACPane().getcFld().getText().replace(",", "."));				
			a = Double.parseDouble(latticePane.getACPane().getaFld().getText().replace(",", "."));
			
			if(a <= 0d || c <= 0d) throw new NumberFormatException();
				
			planeMiller = new MillerIndices();
			planeMiller.setAsNonCubicPlane(normal, maxIndex, Transformations.getTetrToCartesian(a, c)); 				
			planeFlds.setIndices(planeMiller.h(), planeMiller.k(), planeMiller.l());
			
			break;
			
		case MMM:
			c = Double.parseDouble(latticePane.getABCPane().getcFld().getText().replace(",", "."));				
			double b = Double.parseDouble(latticePane.getABCPane().getbFld().getText().replace(",", "."));
			a = Double.parseDouble(latticePane.getABCPane().getaFld().getText().replace(",", "."));
				
			if(a <= 0d || b <= 0d ||  c <= 0d) throw new NumberFormatException();
			
			planeMiller = new MillerIndices();
			planeMiller.setAsNonCubicPlane(normal, maxIndex, Transformations.getOrthToCartesian(a, b, c)); 				
			planeFlds.setIndices(planeMiller.h(), planeMiller.k(), planeMiller.l());
			break;
			
			default: break;
		}
		

	}

	
	public final LatticePanel getLatticePane() {
		return latticePane;
	}
}
