package algorithms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;


import utils.AxisAngle;
import utils.Matrix3x3;
import utils.MillerIndices;
import utils.MyMath;
import utils.PaintUtilities;
import utils.Transformations;
import utils.UnitVector;
import de.erichseifert.vectorgraphics2d.EPSGraphics2D;
import enums.Layer;
import enums.PointGroup;

public class Catalog_AnalyticalFigure extends JPanel  implements MouseMotionListener {

	private static final double ONEPI = Math.PI;
	private static final double TWOPI = 2d*Math.PI;
	private static final int ISOSAMPLES = 512;
	
	public static int RADIUS = 250;
	public static int GAPX = 50;
	public static int GAPY = 50;
		
	private Matrix3x3[] setOfAllC;
	private Matrix3x3 M;
	
	private ArrayList<Point2D> twistPts;
	private ArrayList<Point2D> symPts;
	
	private ArrayList<MillerIndices> millTwist;
	private ArrayList<MillerIndices> millSym;
	
	private ArrayList<MillerIndices> millTwist2;
	private ArrayList<MillerIndices> millSym2;
	
	private ArrayList<Point2D> uniqueTwistPts;
	private ArrayList<Point2D> uniqueSymPts;
   	
	private ArrayList<MillerIndices> uniqueTwistMill;
	private ArrayList<MillerIndices> uniqueSymMill;
	
	private ArrayList<MillerIndices> uniqueTwistMill2;
	private ArrayList<MillerIndices> uniqueSymMill2;

	
	private ArrayList<Point2D> drawTwistPts;
	private ArrayList<Point2D> drawSymPts;
	
	private ArrayList< ArrayList<Point2D> > tiltPts;
	private ArrayList< ArrayList<Point2D> > impropPts;
	
	private Color cTilt;
	private Color cTwist;
	private Color cSym;
	private Color cImprop;
	
	private int sizeTwist;
	private int sizeSym;
	private float widthTilt;
	private float widthImprop;
		
	private Layer lay1;
	private Layer lay2;
	private Layer lay3;
	private Layer lay4;
		
	private BufferedImage im;
	private Graphics2D g2d;
	
	private boolean filledSym;
	private int maxIndex;
	
	private int showMiller;
	private Color millerCol;
	private Font millerFont;
	
	private PointGroup ptGrp;
	private double a;
	private double b;
	private double c;
	
	
	private double mousX = 0d;
	private double mousY = 0d;
		
	
	public final void recalculate(boolean calcTwist, boolean calcSym) {
			
		if(calcTwist) {
			drawTwistPts = new ArrayList<Point2D>();
			for(Point2D pt : twistPts) {
		   		final double rX = pt.getX();
		   		final double rY = pt.getY();
		   		drawTwistPts.add(new Point2D.Double(rX - 0.5d*sizeTwist, rY - 0.5d*sizeTwist));
			}	
		}
		
		if(calcSym) {
			drawSymPts = new ArrayList<Point2D>();
			for(Point2D pt : symPts) {
		   		final double rX = pt.getX();
		   		final double rY = pt.getY();
		   		drawSymPts.add(new Point2D.Double(rX - 0.5d*sizeSym, rY - 0.5d*sizeSym));
			}
		}			
	}
	
	
	
	private final void calculate() {

		twistPts = new ArrayList<Point2D>();
		symPts = new ArrayList<Point2D>();
		
		millTwist = new ArrayList<MillerIndices>();
		millSym = new ArrayList<MillerIndices>();
		
		millTwist2 = new ArrayList<MillerIndices>();
		millSym2 = new ArrayList<MillerIndices>();
		
		tiltPts = new ArrayList< ArrayList<Point2D> >();
		impropPts = new ArrayList< ArrayList<Point2D> >();

		for(Matrix3x3 C1 : setOfAllC) for(Matrix3x3 C2 : setOfAllC)
		{
			final Matrix3x3 M = new Matrix3x3(this.M);    		
			M.leftMul(C1);
			M.timesTransposed(C2);
					
			final AxisAngle aa = new AxisAngle();
			aa.set(M);
			
			final UnitVector nR = new UnitVector(aa.axis());
			
			//System.out.println("MapAnalytical: axis = " +  nR + ", angle = " + Math.toDegrees(aa.angle()));
			nR.transposedTransform(C1);
		//	System.out.println("MapAnalytical: C1T.axis = " +  nR);
			
			final boolean isPi = Math.abs(ONEPI - aa.angle()) < 0.00017d;
			
			final UnitVector minus_nR = new UnitVector(nR);    		        	
			minus_nR.negate();
			
			final UnitVector m2 = new UnitVector(minus_nR);
			final UnitVector minus_m2 = new UnitVector(nR);
			m2.transposedTransform(M);
			minus_m2.transposedTransform(M);

			double theta = FastMath.atan2(nR.y(), nR.x()); 
			double phi = MyMath.acos(nR.z());
			double r = FastMath.tan(0.5d * phi);

			if(r < 1d + 1e-4d) {
	        	
				final MillerIndices indices = new MillerIndices();
    			final MillerIndices indices2 = new MillerIndices();
    			
				switch(ptGrp) {
				case M3M:				
					indices.setAsCubic(nR, maxIndex);    			
    				indices2.setAsCubic(m2, maxIndex);
    				break;
    			
				case _6MMM:
					indices.setAsNonCubicPlane(nR, maxIndex, Transformations.getHexToCartesian(a, c));    			
    				indices2.setAsNonCubicPlane(m2, maxIndex, Transformations.getHexToCartesian(a, c));					
					break;
					
				case _4MMM:
					indices.setAsNonCubicPlane(nR, maxIndex, Transformations.getTetrToCartesian(a, c));    			
    				indices2.setAsNonCubicPlane(m2, maxIndex, Transformations.getTetrToCartesian(a, c));					
					break;
					
				case MMM:
					indices.setAsNonCubicPlane(nR, maxIndex, Transformations.getOrthToCartesian(a, b, c));    			
    				indices2.setAsNonCubicPlane(m2, maxIndex, Transformations.getOrthToCartesian(a, b, c));					
					break;
					
				default: break;
				}
				 			
    							
				final double rX = GAPX + RADIUS*(1d + r*FastMath.cos(theta));
				final double rY = GAPY + RADIUS*(1d - r*FastMath.sin(theta));
				final Point2D pt = new Point2D.Double(rX, rY);						
				twistPts.add(pt);
				millTwist.add(indices);
				millTwist2.add(indices2);
				if(isPi) {
					symPts.add(pt);
				//	System.out.println("> " + ((pt.getX()-GAPX)/RADIUS-1d) + " " + (1d-(pt.getY()-GAPY)/RADIUS));
					millSym.add(indices);
					millSym2.add(indices2);
					
					//System.out.println("MapAnalytical: axis = " +  nR + ", angle = " + Math.toDegrees(aa.angle()));					
					//System.out.println(C1);					
					//System.out.println(M);
				}
		
			}	
		       			       		

			theta = FastMath.atan2(minus_nR.y(), minus_nR.x()); 
			phi = MyMath.acos(minus_nR.z());
			r = FastMath.tan(0.5d * phi);

			if(r < 1d + 1e-4d) {
				
				final MillerIndices indices = new MillerIndices();
    			final MillerIndices indices2 = new MillerIndices();
    			
				switch(ptGrp) {
				case M3M:				
					indices.setAsCubic(minus_nR, maxIndex);    			
    				indices2.setAsCubic(minus_m2, maxIndex);
    				break;
    			
				case _6MMM:
					indices.setAsNonCubicPlane(minus_nR, maxIndex, Transformations.getHexToCartesian(a, c));    			
    				indices2.setAsNonCubicPlane(minus_m2, maxIndex, Transformations.getHexToCartesian(a, c));					
					break;
					
				case _4MMM:
					indices.setAsNonCubicPlane(minus_nR, maxIndex, Transformations.getTetrToCartesian(a, c));    			
    				indices2.setAsNonCubicPlane(minus_m2, maxIndex, Transformations.getTetrToCartesian(a, c));					
					break;
					
				case MMM:
					indices.setAsNonCubicPlane(minus_nR, maxIndex, Transformations.getOrthToCartesian(a, b, c));    			
    				indices2.setAsNonCubicPlane(minus_m2, maxIndex, Transformations.getOrthToCartesian(a, b, c));					
					break;
					
				default: break;
				}
	
				final double rX = GAPX + RADIUS*(1d + r*FastMath.cos(theta));
				final double rY = GAPY + RADIUS*(1d - r*FastMath.sin(theta));
				final Point2D pt = new Point2D.Double(rX, rY);						
				
				twistPts.add(pt);
				millTwist.add(indices);
				millTwist2.add(indices2);

				if(isPi) {
					symPts.add(pt);
			//		System.out.println("> " + ((pt.getX()-GAPX)/RADIUS-1d) + " " + (1d-(pt.getY()-GAPY)/RADIUS));
					millSym.add(indices);
					millSym2.add(indices2);
					

			//		System.out.println("MapAnalytical: axis = " +  nR + ", angle = " + Math.toDegrees(aa.angle()));					
			//		System.out.println(C1);					
			//		System.out.println(M);
				}

			}

			final ArrayList<Point2D> nextZone = zonePoints(nR);
			tiltPts.add(nextZone);
			if(isPi) { impropPts.add(nextZone);
			//	System.out.print("{");
				//for(Point2D pt : nextZone) 	System.out.println("{"+((pt.getX()-GAPX)/RADIUS-1d) +","+(1d-(pt.getY()-GAPY)/RADIUS)+"},");
				//System.out.println("}");
				
			}
		}
	
		
		uniqueTwistPts = new ArrayList<Point2D>();
       	uniqueSymPts = new ArrayList<Point2D>();
       	
       	uniqueTwistMill = new ArrayList<MillerIndices>();
       	uniqueSymMill = new ArrayList<MillerIndices>();
       	
       	uniqueTwistMill2 = new ArrayList<MillerIndices>();
       	uniqueSymMill2 = new ArrayList<MillerIndices>();
       	
       	for(int i = 0; i < twistPts.size(); i++) {
    					
        		boolean alreadyIncluded = false;
        		
        		for(int j = 0; j < uniqueTwistPts.size(); j++) {
        			
        			if( Math.abs(twistPts.get(i).getX() - uniqueTwistPts.get(j).getX()) < 0.5d &&
        				Math.abs(twistPts.get(i).getY() - uniqueTwistPts.get(j).getY()) < 0.5d) {
        				alreadyIncluded = true;
        				break;
        			}
        		}
        		
        		if(!alreadyIncluded) {
        			uniqueTwistPts.add(twistPts.get(i));
        			uniqueTwistMill.add(millTwist.get(i));
        			uniqueTwistMill2.add(millTwist2.get(i));
        		}
       	}
    			
       	
       	for(int i = 0; i < symPts.size(); i++) {
			
    		boolean alreadyIncluded = false;
    		
    		for(int j = 0; j < uniqueSymPts.size(); j++) {
    			
    			if( Math.abs(symPts.get(i).getX() - uniqueSymPts.get(j).getX()) < 0.5d &&
    				Math.abs(symPts.get(i).getY() - uniqueSymPts.get(j).getY()) < 0.5d) {
    				alreadyIncluded = true;
    				break;
    			}
    		}
    		
    		if(!alreadyIncluded) {
    			uniqueSymPts.add(symPts.get(i));
    			uniqueSymMill.add(millSym.get(i));
    			uniqueSymMill2.add(millSym2.get(i));
    		}
       	}
    	
	}
	
	
	
	public final void setColors(Color cTwist, Color cTilt,
								Color cSym, Color cImprop) {
		this.cTwist = cTwist;
		this.cTilt = cTilt;
		this.cSym = cSym;
		this.cImprop = cImprop;
	}
	
	
	public final void setSize(int sizeTwist, int sizeSym, 
							  float widthTilt, float widthImprop) {
		this.sizeTwist = sizeTwist;
		this.sizeSym = sizeSym;
		this.widthTilt = widthTilt;
		this.widthImprop =  widthImprop;
	}
	
	public final void setLayers(Layer l1, Layer l2, Layer l3, Layer l4) {
		lay1 = l1;
		lay2 = l2;
		lay3 = l3;
		lay4 = l4;
	}
		
	public final void setFilledSym(boolean b) {
		filledSym = b;
	}
	
	public final void setShowMiller(int i) {
		
		showMiller = i;
	}
	
	public final void setMillerFont(Font font) {
		millerFont = font;
	}
	
	public final void setMillerColor(Color col) {
	
		millerCol = col;
	}
	
	public final void setMaxIndex(int i) {
		maxIndex = i;
	}
	
	
	public Catalog_AnalyticalFigure(Matrix3x3 M, PointGroup ptGrp, double a, double b, double c, int idx) {
		
		setMaxIndex(idx);
		
		//System.out.println(M);
		
		setMinimumSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));  
		setMaximumSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));  
		setPreferredSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));
		
		
		setOfAllC = Transformations.getSymmetryTransformations(ptGrp);
		this.M = M;
		
		this.ptGrp = ptGrp;;
		
		this.a = a;
		this.b = b;
		this.c = c;
		
	
		//default 
		cTilt = Color.CYAN;
		cTwist = Color.RED;
		cSym = Color.BLUE;
		cImprop = Color.GREEN;		

		
		sizeTwist = 10;
		sizeSym = 18;
		widthTilt = 1.0f;
		widthImprop = 3.0f;
		
		
	
		
		filledSym = false;
		
		lay1 = Layer.TILT; 
		lay2 = Layer.IMPROP;
		lay3 = Layer.SYMMETRIC;
		lay4 = Layer.TWIST;
		
			
		millerFont = new Font("Arial", Font.PLAIN, 12);
		millerCol = Color.BLACK;
		
		addMouseMotionListener(this);

		calculate();
		recalculate(true, true);
		repaint();
		
	}
	
	
	private void drawIndices(Graphics2D g) {
		
		final FontRenderContext frc = g.getFontRenderContext();
					
		if(showMiller > 0) {
			
			final float MOVX = 10f;
			final float MOVY = 10f; 
			g.setFont(millerFont);
			g.setColor(millerCol);
						
			if(lay1 == Layer.TWIST || lay2 == Layer.TWIST || lay3 == Layer.TWIST || lay4 == Layer.TWIST) {
						
				if(showMiller == 1) {
					for(int i = 0; i < uniqueTwistPts.size(); i++) {
						
						final TextLayout textlay = millerStr(
								uniqueTwistMill.get(i).h(), uniqueTwistMill.get(i).k(), uniqueTwistMill.get(i).l(), ptGrp, frc);
						
						textlay.draw(g, (float)uniqueTwistPts.get(i).getX()+MOVX, (float)uniqueTwistPts.get(i).getY()+MOVY);			
					}
				} else {
					
					for(int i = 0; i < uniqueTwistPts.size(); i++) {
						
						final TextLayout textlay = millerStr(
								uniqueTwistMill2.get(i).h(), uniqueTwistMill2.get(i).k(), uniqueTwistMill2.get(i).l(), ptGrp, frc);

						textlay.draw(g, (float)uniqueTwistPts.get(i).getX()+MOVX, (float)uniqueTwistPts.get(i).getY()+MOVY);
					}

					
				}
		
			} else {
				
				if(lay1 == Layer.SYMMETRIC || lay2 == Layer.SYMMETRIC || lay3 == Layer.SYMMETRIC || lay4 == Layer.SYMMETRIC) {
					
					
					if(showMiller == 1) {
						for(int i = 0; i < uniqueSymPts.size(); i++) {
				
							final TextLayout textlay = millerStr(
									uniqueSymMill.get(i).h(), uniqueSymMill.get(i).k(), uniqueSymMill.get(i).l(), ptGrp, frc);

							textlay.draw(g, (float)uniqueSymPts.get(i).getX()+MOVX, (float)uniqueSymPts.get(i).getY()+MOVY);
						}
					} else {
						
						for(int i = 0; i < uniqueSymPts.size(); i++) {
					
							final TextLayout textlay = millerStr(
									uniqueSymMill2.get(i).h(), uniqueSymMill2.get(i).k(), uniqueSymMill2.get(i).l(), ptGrp, frc);

							textlay.draw(g, (float)uniqueSymPts.get(i).getX()+MOVX, (float)uniqueSymPts.get(i).getY()+MOVY);
						}
						
					}				
					
				}				
			}
		}
	}
	
	
	@Override  
    protected void paintComponent(Graphics g) {
		
		im = new BufferedImage(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d_pane = (Graphics2D) g;
		
		g2d = im.createGraphics();
			
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, 2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS);
		
		switch(ptGrp) {
		case M3M: PaintUtilities.drawPoleFigureAxes(g2d, GAPX, GAPY, RADIUS, false);
			break;
		case _6MMM: PaintUtilities.drawPoleFigureHex(g2d, GAPX, GAPY, RADIUS, false); 
			break;
		case _4MMM: PaintUtilities.drawPoleFigureTetr(g2d, GAPX, GAPY, RADIUS, false);
			break;
		case MMM: PaintUtilities.drawPoleFigureOrth(g2d, GAPX, GAPY, RADIUS, false);
			break;
		}
		
		drawLayer(g2d,lay1);
		drawLayer(g2d,lay2);
		drawLayer(g2d,lay3);
		drawLayer(g2d,lay4);
		
		drawIndices(g2d);	
		
		final double xproj = (mousX-GAPX)/RADIUS-1d;
		final double yproj = 1d-(mousY-GAPY)/RADIUS;
		final double rproj = MyMath.sqrt(xproj*xproj+yproj*yproj);
		final double theta = FastMath.atan2(yproj,xproj);
		final double phi = 2d*FastMath.atan(rproj);
		final double zs = FastMath.cos(phi);
		final double xs = FastMath.sin(phi)*FastMath.cos(theta);
		final double ys = FastMath.sin(phi)*FastMath.sin(theta);
		
		final UnitVector dir = new UnitVector();
		dir.set(xs,ys,zs);
		
		final UnitVector dir2 = new UnitVector(dir);
		dir2.transposedTransform(M);
		dir2.negate();

		
		final MillerIndices indices = new MillerIndices();
		final MillerIndices indices2 = new MillerIndices();		

		switch(ptGrp) {
		case M3M:				
			indices.setAsCubic(dir, maxIndex);
			indices2.setAsCubic(dir2, maxIndex);
			break;
		
		case _6MMM:
			indices.setAsNonCubicPlane(dir, maxIndex, Transformations.getHexToCartesian(a, c));    								
			indices2.setAsNonCubicPlane(dir2, maxIndex, Transformations.getHexToCartesian(a, c));
			break;
			
		case _4MMM:
			indices.setAsNonCubicPlane(dir, maxIndex, Transformations.getTetrToCartesian(a, c));    								
			indices2.setAsNonCubicPlane(dir2, maxIndex, Transformations.getTetrToCartesian(a, c));
			break;
			
		case MMM:
			indices.setAsNonCubicPlane(dir, maxIndex, Transformations.getOrthToCartesian(a, b, c));    			
			indices2.setAsNonCubicPlane(dir2, maxIndex, Transformations.getOrthToCartesian(a, b, c));
			break;
			
		default: break;
		}
		
		final String posstr;
		if(ptGrp == PointGroup._6MMM) {
			posstr = "("+ indices.h() + "," + indices.k() + "," + (-indices.h()-indices.k()) + "," + indices.l() + ")";			
		} else {
			posstr = "("+ indices.h() + "," + indices.k() + "," + indices.l() + ")";
		}
		final String posstr2;
		if(ptGrp == PointGroup._6MMM) {
			posstr2 = "("+ indices2.h() + "," + indices2.k() + "," + (-indices2.h()-indices2.k()) + "," + indices2.l() + ")";			
		} else {
			posstr2 = "("+ indices2.h() + "," + indices2.k() + "," + indices2.l() + ")";
		}
		
		g2d.setFont(new Font("TimesRoman", Font.PLAIN, 12));        
        g2d.setColor(Color.MAGENTA);        
        g2d.drawString("m\u2081 = " + posstr, 10, 590);
        g2d.setColor(Color.MAGENTA);        
        g2d.drawString("m\u2082 = " + posstr2, 300, 590);
        
        
        g2d_pane.drawImage(im, null, 0, 0);
        
     
      
                       
	}
	
	
	private void drawLayer(Graphics2D g, Layer layer) {
		
		switch(layer) {
		case TILT:
			g.setColor(cTilt);		
			g.setStroke(new BasicStroke(widthTilt));
			for(ArrayList<Point2D> list : tiltPts) if(list.size()>0) PaintUtilities.drawZone(g, list);
			break;
			
		case TWIST:
			PaintUtilities.drawPoints(g, drawTwistPts, cTwist, sizeTwist);
			break;
		
		case SYMMETRIC:
			g.setStroke(new BasicStroke(1.0f));//TODO
			if(symPts != null) {
				if(filledSym) {
					PaintUtilities.drawPoints(g, drawSymPts, cSym, sizeSym);
				} else {
					PaintUtilities.drawCircles(g, drawSymPts, cSym, sizeSym);
				}
			}
			break;
			
		case IMPROP:
			g.setColor(cImprop);
			g.setStroke(new BasicStroke(widthImprop));
			if(impropPts != null) for(ArrayList<Point2D> list : impropPts) PaintUtilities.drawZone(g, list);
			break;
			
		case EMPTY: break;
		default: break;
		}
	
	}
	
	
	public final void exportToPNG(String f) throws IOException {
		
		ImageIO.write(im, "png" , new File(f));
	}
	
	
	public final void exportToEPS(String f) throws IOException {
				

		EPSGraphics2D vecg = new EPSGraphics2D(0,0, 2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS);
		
	
		vecg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		vecg.setBackground(Color.WHITE);
		vecg.clearRect(0, 0, 2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS);
		
		switch(ptGrp) {
		case M3M: PaintUtilities.drawPoleFigureAxes(vecg, GAPX, GAPY, RADIUS, false);
			break;
		case _6MMM: PaintUtilities.drawPoleFigureHex(vecg, GAPX, GAPY, RADIUS, false); 
			break;
		case _4MMM: PaintUtilities.drawPoleFigureTetr(vecg, GAPX, GAPY, RADIUS, false);
			break;
		case MMM: PaintUtilities.drawPoleFigureOrth(vecg, GAPX, GAPY, RADIUS, false);
			break;
		}
		
		drawLayer(vecg,lay1);
		drawLayer(vecg,lay2);
		drawLayer(vecg,lay3);
		drawLayer(vecg,lay4);
		
		drawIndices(vecg);	
			
		 FileOutputStream file = new FileOutputStream(f);
		 file.write(vecg.getBytes());

	}
	
				
	private final ArrayList<Point2D> zonePoints(UnitVector n) {
		
		ArrayList<Point2D> pts = new ArrayList<Point2D>();

		final double x = n.x();
		final double y = n.y();
		final double z = n.z();
		
		final double t0 = FastMath.atan2(y, x);
		
		final double[] t = new double[ISOSAMPLES + 1];
		final double dt = TWOPI / ISOSAMPLES;
		for(int i = 0; i <= ISOSAMPLES; ++i) t[i] = t0 + i * dt;
		
		final double xsq = x*x;
		final double ysq = y*y;
		
		final double xsqPlusYsq = xsq + ysq;
		
		
		if(Math.abs(xsqPlusYsq) > 1e-4d) {
			
			final double zsq = z*z;				
		
			final double oneMinZ = 1d - z;
			final double ksi = oneMinZ / xsqPlusYsq;
				
			final double oneMinZsq = 1d - zsq;
			final double lambda =  MyMath.sqrt(oneMinZsq) / Math.sqrt(xsqPlusYsq);
				
			final double xy = x*y;
			
		
			final double o11 = z + ysq * ksi;
			final double o12 = -xy * ksi;
			final double o21 = o12;
			final double o22 = z + xsq * ksi;
			final double o31 = -x * lambda;
			final double o32 = -y * lambda;
			

		
			for(int i = 0; i <= ISOSAMPLES; ++i) {
				
				final double cosT = FastMath.cos(t[i]);
				final double sinT = FastMath.sin(t[i]);
				
				final double vx = o11*cosT + o12*sinT;
				final double vy = o21*cosT + o22*sinT;
				final double vz = o31*cosT + o32*sinT;
				
				final double theta = FastMath.atan2(vy, vx);
				final double phi = FastMath.acos(vz);
				final double r = FastMath.tan(0.5d * phi);
	
				if(r < 1d + 1e-4d) {
					
					pts.add(new Point2D.Double(GAPX + RADIUS*(1d + r*FastMath.cos(theta)), 
												GAPY + RADIUS*(1d - r*FastMath.sin(theta))));
					
				}
			}
		}
		else {
			for(int i = 0; i <= ISOSAMPLES; ++i) {
				
				pts.add(new Point2D.Double(GAPX + RADIUS*(1d + FastMath.cos(t[i])), 
												GAPY + RADIUS*(1d - FastMath.sin(t[i]))));
					
			}
		}
		
		return pts;		
	}
			
	
	private final TextLayout millerStr(int h, int k, int l, PointGroup pointGrp, FontRenderContext frc) {
		
		if(pointGrp == PointGroup._6MMM) {
			
			return new TextLayout("("+ h + "," + k + "," + (-h-k) + "," + l + ")", millerFont, frc);
			
		} else {
			return new TextLayout("("+ h + "," + k + "," + l + ")", millerFont, frc);
		}
	}



	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		mousX = evt.getX();
		mousY = evt.getY();		
		repaint();
	}
}
