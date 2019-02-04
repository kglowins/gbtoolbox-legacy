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
import enums.PointGroup;


public class SymmetryFigure extends JPanel  implements MouseMotionListener {

	private static final double ONEPI = Math.PI;
	private static final double TWOPI = 2d*Math.PI;
	private static final int ISOSAMPLES = 512;
	
	public static int RADIUS = 250;
	public static int GAPX = 50;
	public static int GAPY = 50;
		
	private Matrix3x3[] setOfAllC;
	private Matrix3x3 M;
	
	
	private ArrayList< ArrayList<Point2D> > mirrorPts;	
		
	private BufferedImage im;
	private Graphics2D g2d;
		
	private PointGroup ptGrp;
	private double a;
	private double b;
	private double c;
	

	private double mousX = 0d;
	private double mousY = 0d;
	private int maxIndex;
	
	private ArrayList< SymmetryAxis > symAxes;
	

	
	public SymmetryFigure(Matrix3x3 M, PointGroup ptGrp, double a, double b, double c, int idx) {
		
				
		setMinimumSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));  
		setMaximumSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));  
		setPreferredSize(new Dimension(2*GAPX + 2*RADIUS, 2*GAPY + 2*RADIUS));
				
		setOfAllC = Transformations.getSymmetryTransformations(ptGrp);
		this.M = M;
		
		this.ptGrp = ptGrp;;
		
		this.a = a;
		this.b = b;
		this.c = c;
		
		maxIndex = idx;
		
		symAxes = new ArrayList <SymmetryAxis>();
		mirrorPts = new ArrayList < ArrayList<Point2D> >();

		addMouseMotionListener(this);

		findAxes();
		
		repaint();
		
	}
	
	
	
	
	
	private final boolean includedIn(Matrix3x3 M, ArrayList<Matrix3x3> A) {
		
		for(Matrix3x3 R : A) {
			if(R.isEqualTo(M)) return true;
		}		
		return false;
	}
	
	private final void findAxes() {
		
		//Comparator<Matrix3x3> cmp = new MatricesComparator();
		//TreeSet<Matrix3x3> group = new TreeSet<Matrix3x3>(cmp);
		
		ArrayList<Matrix3x3> group = new ArrayList<Matrix3x3>();
		
		for(Matrix3x3 C1 : setOfAllC) {
			for(Matrix3x3 C2 : setOfAllC) {
			
				final Matrix3x3 Mcp = new Matrix3x3(M);
				Mcp.leftMul(C1);
				Mcp.timesTransposed(C2);
				
				if(Mcp.isEqualTo(M)) {
					if(!includedIn(C1, group)) group.add(C1);
				}
				
				final Matrix3x3 MTcp = new Matrix3x3(M);
				MTcp.transpose();
				MTcp.leftMul(C1);
				MTcp.timesTransposed(C2);
				
				if(MTcp.isEqualTo(M)) {
					
					final Matrix3x3 MC = new Matrix3x3(M);
					MC.times(C2);
					if(!includedIn(MC, group)) group.add(MC);
				}
			}			
		}
		
		
		
		boolean newAdded = true;
		
		while(newAdded) {
									
			newAdded = false;
			
			ArrayList<Matrix3x3> groupCp = new ArrayList<Matrix3x3>();
			groupCp.addAll(group);
			
			for(Matrix3x3 G1 : group) {
				
				for(Matrix3x3 G2 : group) {
					
					final Matrix3x3 prod = new Matrix3x3(G1);
					final Matrix3x3 prod2 = new Matrix3x3(G1);
					
					prod.times(G2);
					prod2.leftMul(G2);
					
					if (!includedIn(prod,groupCp)) {
						groupCp.add(prod);
						newAdded = true;
					}
					if (!includedIn(prod2,groupCp)) {
						groupCp.add(prod2);
						newAdded = true;
					}
					
				}
			}
			
			group.clear();
			group.addAll(groupCp);
		}

		
		//System.out.println(group.size());
		
		for(Matrix3x3 G : group) {
			
			final AxisAngle aa = new AxisAngle();
			aa.set(G);
			if(Math.abs(aa.angle()) > 0.001) {
				
				int fold = (int)Math.round(2d*Math.PI / aa.angle());
				
				UnitVector minus = new UnitVector();
				minus.set(aa.axis());
				minus.negate();
				
				if(aa.axis().z() > -0.001) {
					symAxes.add(new SymmetryAxis(aa.axis(), fold));
					
					if(fold == 2) {
						mirrorPts.add( zonePoints(aa.axis()) );
					}
				}
				if(minus.z() > -0.001) {
					symAxes.add(new SymmetryAxis(minus, fold));
					if(fold == 2) {
						mirrorPts.add( zonePoints(minus) );
					}

				}
				
			
				//System.out.println(aa);
				//System.out.println((int)Math.round(2d*Math.PI / aa.angle()));
			}
		
		}
		
		//System.out.println(symAxes.size());
    	
	}

	
	private final void drawSymmetryElements(Graphics2D g2d) {
		
		g2d.setColor(Color.black);
		g2d.setStroke(new BasicStroke(2.5f));
		for(ArrayList<Point2D> list : mirrorPts) PaintUtilities.drawZone(g2d, list);
		
		for(SymmetryAxis elem : symAxes) {
			
			
			double theta = FastMath.atan2(elem.getAxis().y(), elem.getAxis().x()); 
			double phi = MyMath.acos(elem.getAxis().z());
			double r = FastMath.tan(0.5d * phi);
			
			final double rX = GAPX + RADIUS*(1d + r*FastMath.cos(theta));
			final double rY = GAPY + RADIUS*(1d - r*FastMath.sin(theta));
			
			
			g2d.setColor(Color.black);
			if(elem.getMul() == 2) {
				
				boolean covered = false;
				
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01  && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				
				if(!covered) g2d.fillOval((int)rX - 8, (int)rY - 16, 16, 32);
				
			} else if(elem.getMul() == 3) {
					
				boolean covered = false;
				
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01 && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				double rad = 20;
				if(!covered) g2d.fillPolygon(
						new int[]{(int)(rX - rad*Math.cos(Math.PI / 2d + Math.PI / 3d)),
								(int)(rX - rad*Math.cos(Math.PI / 2d + Math.PI )),
								(int)(rX - rad*Math.cos(Math.PI / 2d + 5d*Math.PI / 3d))
													
						},
						new int[]{(int)(rY - rad*Math.sin(Math.PI / 2d + Math.PI / 3d)),								
								(int)(rY - rad*Math.sin(Math.PI / 2d + Math.PI)),
								(int)(rY - rad*Math.sin(Math.PI / 2d + 5d*Math.PI / 3d))}, 3);
				
				
				
			} else if(elem.getMul() == 4) {
				
				boolean covered = false;
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01  && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				
				if(!covered)
					g2d.fillRect((int)rX - 11, (int)rY - 11, 22, 22);
				
			} else if(elem.getMul() == 6) {
				
				double rad = 24;
				
				boolean covered = false;
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01  && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				
				if(!covered)
				g2d.fillPolygon(
						new int[]{(int)(rX - rad*Math.cos(0d)),
								(int)(rX - rad*Math.cos(Math.PI / 3d)),
								(int)(rX - rad*Math.cos(Math.PI * 2d / 3d)),
								(int)(rX - rad*Math.cos(Math.PI )),
								(int)(rX - rad*Math.cos(Math.PI * 4d / 3d)),
								(int)(rX - rad*Math.cos(Math.PI * 5d / 3d)),								
						},
						new int[]{(int)(rY - rad*Math.sin(0d)),
								(int)(rY - rad*Math.sin(Math.PI / 3d)),
								(int)(rY - rad*Math.sin(Math.PI * 2d / 3d)),
								(int)(rY - rad*Math.sin(Math.PI )),
								(int)(rY - rad*Math.sin(Math.PI * 4d / 3d)),
								(int)(rY - rad*Math.sin(Math.PI * 5d / 3d))}, 6);
			
			} else if(elem.getMul() == 8) {
				
				double rad = 25;
				
				boolean covered = false;
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01  && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				
				if(!covered)
				g2d.fillPolygon(
						new int[]{(int)(rX - rad*Math.cos(0d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 2d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 3d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 4d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 5d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 6d)),
								(int)(rX - rad*Math.cos(Math.PI / 4d * 7d))
																
						},
						new int[]{(int)(rY - rad*Math.sin(0d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d )),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 2d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 3d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 4d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 5d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 6d)),
								(int)(rY - rad*Math.sin(Math.PI / 4d * 7d)),
								}, 8);
				
			
			} else if(elem.getMul() == 12) {
				
				boolean covered = false;
				for(SymmetryAxis elem2 : symAxes) 
					if(Math.abs(elem2.getAxis().dot(elem.getAxis()) - 1d) < 0.01  && elem2.getMul() > elem.getMul()) {
					covered = true;
					break;
				}
				
				double rad = 42;
				if(!covered)
					g2d.fillPolygon(
							new int[]{(int)(rX - rad*Math.cos(0d)),
									(int)(rX - rad*Math.cos(Math.PI / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 2d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 3d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 4d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 5d / 6d)),	
									(int)(rX - rad*Math.cos(Math.PI )),
									(int)(rX - rad*Math.cos(Math.PI * 7d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 8d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 9d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 10d / 6d)),
									(int)(rX - rad*Math.cos(Math.PI * 11d / 6d)),
							},
							new int[]{(int)(rY - rad*Math.sin(0d)),
									(int)(rY - rad*Math.sin(Math.PI / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 2d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 3d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 4d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 5d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI )),
									(int)(rY - rad*Math.sin(Math.PI * 7d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 8d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 9d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 10d / 6d)),
									(int)(rY - rad*Math.sin(Math.PI * 11d / 6d))
									
							}, 12);
				
				
			} else {			
				System.out.println("Unsupported " + elem.getMul() + "-fold symmetry axis.");
			}
			
			
			
		}
		g2d.setColor(Color.black);
		g2d.fillOval(GAPX + RADIUS-8, GAPY + RADIUS-8,16,16);
		g2d.setColor(Color.white);
		g2d.fillOval(GAPX + RADIUS-5, GAPY + RADIUS-5,10,10);
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
		
		
		drawSymmetryElements(g2d);
		
		
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
		
		
		drawSymmetryElements(vecg);		
		
			
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
			
	
	



	@Override
	public void mouseDragged(MouseEvent arg0) {
	}

	@Override
	public void mouseMoved(MouseEvent evt) {
		mousX = evt.getX();
		mousY = evt.getY();		
		repaint();
	}
	
	
	
	private class SymmetryAxis {
		
		private UnitVector axis;
		
		private int multiplicity;
		
		SymmetryAxis(UnitVector axis, int multiplicity) {
			this.axis = axis;
			this.multiplicity = multiplicity;
		}
		
		public final int getMul() { return multiplicity; }
		
		public final UnitVector getAxis() { return axis; }
	}
}
