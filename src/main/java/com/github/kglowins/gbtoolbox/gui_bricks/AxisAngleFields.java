package com.github.kglowins.gbtoolbox.gui_bricks;


import java.awt.CardLayout;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import net.miginfocom.swing.MigLayout;
import com.github.kglowins.gbtoolbox.utils.MillerIndices;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;


public class AxisAngleFields extends JPanel {
	
	private JTextField wFld;
	private JPanel axisCards;
	
	private MillerAxisFields axisFlds;
	private HexMillerAxisFields hexAxisFlds;
	private JLabel lblmillerIndices;
	
	private final static String HEX = "HEX";
	private final static String NON_HEX = "NON_HEX";
	
	private LatticePanel latticePane;
	
	private boolean fourIndices;
	
	public final boolean getFourIndices() {
		return fourIndices;
	}
			
	public final void setFourIndicesVisible(boolean b) {
			
	
		if(b && !fourIndices) {						 
             CardLayout cl = (CardLayout) axisCards.getLayout();
             cl.show(axisCards, HEX);
             
             hexAxisFlds.getHFld().setText("");
             hexAxisFlds.getKFld().setText("");
             hexAxisFlds.getIFld().setText("");
             hexAxisFlds.getLFld().setText("");
             
		} else if(!b && fourIndices) {
			CardLayout cl = (CardLayout) axisCards.getLayout();
            cl.show(axisCards, NON_HEX);
            
            axisFlds.getHFld().setText("");
            axisFlds.getKFld().setText("");
            axisFlds.getLFld().setText("");
            
		}
		
		fourIndices = b;
	}
	
	
	public AxisAngleFields(LatticePanel latticePane) {
		
		this.latticePane = latticePane;
		fourIndices = false;
		
		setLayout(new MigLayout("insets 0", "[][][]", "[][]"));
		
		JLabel nLbl = new JLabel("<html><b>n</b> =");		
		add(nLbl, "cell 0 0,alignx right");
		
		axisCards = new JPanel();
		add(axisCards, "cell 1 0,alignx left,growy");
		axisCards.setLayout(new CardLayout(0, 0));
		
		axisFlds = new MillerAxisFields();
		hexAxisFlds = new HexMillerAxisFields();
		
		axisCards.add(axisFlds, NON_HEX);
		axisCards.add(hexAxisFlds, HEX);	
		
		lblmillerIndices = new JLabel("[Miller indices]");
		add(lblmillerIndices, "cell 2 0,gapx 30");
		
		JLabel wLbl = new JLabel("\u03c9 =");		
		add(wLbl, "cell 0 1,alignx right");
		
		wFld = new JTextField();
		wFld.setHorizontalAlignment(SwingConstants.RIGHT);
		add(wFld, "flowx,cell 1 1,alignx left");
		wFld.setColumns(6);
		
		JLabel degLbl = new JLabel("[degrees]");
		add(degLbl, "cell 2 1,alignx left,gapx 30");

	}

	
	public final UnitVector getAxis() {
		
		UnitVector axis = new UnitVector();
		
		switch(latticePane.getPointGroup()) {
		case M3M:
			int h = Integer.parseInt(axisFlds.getHFld().getText());
			int k = Integer.parseInt(axisFlds.getKFld().getText());
			int l = Integer.parseInt(axisFlds.getLFld().getText());
			
			axis.set(h, k, l);
			
			break;
			
		case _6MMM:
			h = Integer.parseInt(hexAxisFlds.getHFld().getText());
			k = Integer.parseInt(hexAxisFlds.getKFld().getText());
			l = Integer.parseInt(hexAxisFlds.getLFld().getText());
			
			
			
			MillerIndices miller = new MillerIndices();
			miller.set(h, k, l);
			
			int aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
			int cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
			
			if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
			
			double a = Math.sqrt(aSq);
			double c = Math.sqrt(cSq);
			
			//double c = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getmFld().getText()));				
			//double a = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getnFld().getText()));
			
			axis.setAsHexAxis4to3(miller, Transformations.getHexToCartesian(a, c));
			
			//System.out.println("AxisAngleFileds: " + miller + " -> " + axis);
			
			//axis.setAsNonCubicAxis(miller, Transformations.getHexToCartesian(a, c));
			
			break;
			
		case _4MMM:
			h = Integer.parseInt(axisFlds.getHFld().getText());
			k = Integer.parseInt(axisFlds.getKFld().getText());
			l = Integer.parseInt(axisFlds.getLFld().getText());
			
			miller = new MillerIndices();
			miller.set(h, k, l);
			
			c = Double.parseDouble(latticePane.getACPane().getcFld().getText().replace(",", "."));				
			a = Double.parseDouble(latticePane.getACPane().getaFld().getText().replace(",", "."));
			
			if(a <= 0d || c <= 0d) throw new NumberFormatException();
			
			axis.setAsNonCubicAxis(miller, Transformations.getTetrToCartesian(a, c));
			
			break;
			
		case MMM:
			h = Integer.parseInt(axisFlds.getHFld().getText());
			k = Integer.parseInt(axisFlds.getKFld().getText());
			l = Integer.parseInt(axisFlds.getLFld().getText());
			
			miller = new MillerIndices();
			miller.set(h, k, l);
			
			c = Double.parseDouble(latticePane.getABCPane().getcFld().getText().replace(",", "."));				
			double b = Double.parseDouble(latticePane.getABCPane().getbFld().getText().replace(",", "."));
			a = Double.parseDouble(latticePane.getABCPane().getaFld().getText().replace(",", "."));
			
			if(a <= 0d || b <= 0d ||  c <= 0d) throw new NumberFormatException();
			
			axis.setAsNonCubicAxis(miller, Transformations.getOrthToCartesian(a, b, c));
			
			break;
			
			default: break;			
		}
				
		return axis;
	}
	
		
	public final void setAxis(UnitVector axis, int maxIndex) {
		
		switch(latticePane.getPointGroup()) {
		case M3M:
			MillerIndices axisMiller = new MillerIndices();			
			axisMiller.setAsCubic(axis, maxIndex);
					
			axisFlds.setIndices(axisMiller.h(), axisMiller.k(), axisMiller.l());
			break;
			
		case _6MMM:
		//	double c = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getmFld().getText()));				
		//	double a = Math.sqrt(Integer.parseInt(latticePane.getCDivAPane().getnFld().getText()));	
			
			int aSq = Integer.parseInt(latticePane.getCDivAPane().getnFld().getText());
			int cSq = Integer.parseInt(latticePane.getCDivAPane().getmFld().getText());
			
			//System.out.println("AAflds: " + aSq + " " + cSq);
			
			if(aSq <= 0 || cSq <= 0) throw new NumberFormatException();
			
			double a = Math.sqrt(aSq);
			double c = Math.sqrt(cSq);
			
			axisMiller = new MillerIndices();			
			axisMiller.setAsHexAxis3to4(axis, maxIndex, Transformations.getCartesianToHex(a, c)); 
			hexAxisFlds.setIndices(axisMiller.h(), axisMiller.k(), axisMiller.l());
			
			break;
			
		case _4MMM:

			c = Double.parseDouble(latticePane.getACPane().getcFld().getText().replace(",", "."));				
			a = Double.parseDouble(latticePane.getACPane().getaFld().getText().replace(",", "."));
			
			if(a <= 0d || c <= 0d) throw new NumberFormatException();
			
			axisMiller = new MillerIndices();			
			axisMiller.setAsNonCubicAxis(axis, maxIndex, Transformations.getCartesianToTetr(a, c)); 
			
			axisFlds.setIndices(axisMiller.h(), axisMiller.k(), axisMiller.l());
			
			break;
			
		case MMM:
			
			c = Double.parseDouble(latticePane.getABCPane().getcFld().getText().replace(",", "."));				
			double b = Double.parseDouble(latticePane.getABCPane().getbFld().getText().replace(",", "."));
			a = Double.parseDouble(latticePane.getABCPane().getaFld().getText().replace(",", "."));
			
			if(a <= 0d || b <= 0d ||  c <= 0d) throw new NumberFormatException();
			
			axisMiller = new MillerIndices();			
			axisMiller.setAsNonCubicAxis(axis, maxIndex, Transformations.getCartesianToOrth(a, b, c)); 
			
			axisFlds.setIndices(axisMiller.h(), axisMiller.k(), axisMiller.l());
			
			break;
			
			default:
				break;
		}		
	}

	
	public final double getAngle() {		
		double angle = Double.parseDouble(wFld.getText().replace(",", "."));						
		angle = Math.toRadians(angle);				
		return angle;
	}
	

	public final void setAngle(double angle) {
		
		DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);	
		final DecimalFormat df = new DecimalFormat("0.####", otherSymbols);

		double omega = Math.toDegrees(angle);
		
		wFld.setText(df.format(omega));		
		wFld.setCaretPosition(0);		
	}
	
	
	public final void clear() {
		
		wFld.setText("");
		hexAxisFlds.clear();
		axisFlds.clear();
	}
}
