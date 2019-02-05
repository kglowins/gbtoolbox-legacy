package com.github.kglowins.gbtoolbox.algorithms;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import org.apache.commons.math3.util.FastMath;

import de.erichseifert.vectorgraphics2d.EPSGraphics2D;

import net.sf.javaml.core.kdtree.KDTree;
import net.sf.javaml.core.kdtree.KeyDuplicateException;
import net.sf.javaml.core.kdtree.KeySizeException;


import com.github.kglowins.gbtoolbox.utils.ColorMaps;
import com.github.kglowins.gbtoolbox.utils.PaintUtilities;
import com.github.kglowins.gbtoolbox.enums.ColormapNames;
import com.github.kglowins.gbtoolbox.enums.PointGroup;

public class GBCD_GBPB_PreviewFigure extends JPanel {
	
	
	private static final double SQRT3 = FastMath.sqrt(3d);
	private static final double TANPI8 = FastMath.tan(0.125d * FastMath.PI);
	private static int NLEVELS = 10;
	
	private static int RADIUS = 250;
	private static int GAPX = 20;
	private static int GAPY = 20;
	private static int LEGENDX = 110;
	
	private static int WIDTH = 300;
		
	private ArrayList<Integer> x;
	private ArrayList<Integer> y;	
	private ArrayList<Double> mrd;
		
	private BufferedImage im;
	private Graphics2D g2d;
	
	private PointGroup ptGrp;
	
	private Color[][] pixelCols;
	
	private Color[] colorLevels;
	
	private double min, max;
	
	private int minx, maxx;
	private int miny, maxy;
	
	private boolean isClear;
	private int fontsize;
	private int decPlaces;
	
	private boolean SSTonly;
	
	private ColormapNames colmap;
	
	private float scalemin;
	private float scalemax;
	
	
	private class DistPt {
		private int x, y;
		private double mrd;
		
		public DistPt(int x, int y, double mrd) {
			this.x = x;
			this.y = y;			
			this.mrd = mrd;
		}
		
		public final int getX() { return x; }
		public final int getY() { return y; }		
		public final double getMRD() { return mrd; }
	}
	
	
	public final void setFontSize(int size) {
		fontsize = size;
	}
	
	public final void setSST(boolean b) {
		SSTonly = b;
	}
	
	public final int getRadius() {
		return RADIUS;
	}
	
	public final int getGapX() {
		return GAPX;
	}
	
	public final int getGapY() {
		return GAPY;
	}
	
	public final void clearPoints() { 
		isClear = true;
	}
	
	public final void setScale(float min, float max) {
		scalemin = min;
		scalemax = max;
	}
	
	
	public final void setPoints(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> mrd, int fontsize, int decplaces) {
		
		this.fontsize = fontsize;		
		this.decPlaces = decplaces;
		
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
					
		minx = 0;// Integer.MAX_VALUE;
		maxx = 2*RADIUS;//Integer.MIN_VALUE;
		
		miny = 0;//Integer.MAX_VALUE;
		maxy = 2*RADIUS;//Integer.MIN_VALUE;
		
		for(double val : mrd) {
			if(val > max) max = val;
			if(val < min) min = val;
		}
						
		this.mrd = new ArrayList<Double>();
		this.x = new ArrayList<Integer>();
		this.y = new ArrayList<Integer>();
		
		for(int i = 0; i < x.size(); i++) {
			
			this.mrd.add( (mrd.get(i) - min) / (max - min) );
			
			final int newX = RADIUS + (int)Math.round(x.get(i) * RADIUS);
			final int newY = RADIUS - (int)Math.round(y.get(i) * RADIUS);
			
			this.x.add(newX);
			this.y.add(newY);
			
			if(newX > maxx) maxx = newX;
			if(newX < minx) minx = newX;
			
			if(newY > maxy) maxy = newY;
			if(newY < miny) miny = newY;			
		}		
		
			
		
		for(int i = 0; i < x.size(); i++) {
			
			this.x.set(i, this.x.get(i) - minx);
			this.y.set(i, this.y.get(i) - miny);
		}	
		
		calcMap();
	}
	
	//TODO
	public final void setPointsSST(ArrayList<Double> x, ArrayList<Double> y, ArrayList<Double> mrd, int fontsize, int decplaces) {
		
		this.fontsize = fontsize;		
		this.decPlaces = decplaces;
		
		//select x,y which are in SST
		final ArrayList<Double> xSST = new ArrayList<Double>();
		final ArrayList<Double> ySST = new ArrayList<Double>();
		final ArrayList<Double> mrdSST = new ArrayList<Double>();
		
		for(int i = 0; i < x.size(); i++) {
			
			final double r = x.get(i)*x.get(i) + y.get(i)*y.get(i);
			
			switch(ptGrp) {
	    	case M3M:
	    		final double cosarctg = FastMath.cos( FastMath.atan2(y.get(i), x.get(i)) );	
	    		if( x.get(i) <= cosarctg * FastMath.tan( 0.5d * FastMath.atan(1d / cosarctg) ) 
	    				&& y.get(i) >= 0 && y.get(i) <= x.get(i)) {
	    			
	    			//scale
	    			xSST.add(x.get(i) / TANPI8);
	    			ySST.add(y.get(i) / TANPI8);
	    			mrdSST.add(mrd.get(i));
	    		}
	    		break;
	    		
	    	case _6MMM:
	    		
	    		
	    		if(r <= 1d && y.get(i) >= 0d && y.get(i) <= x.get(i) / SQRT3 ) {	 
	    			
	    			xSST.add(x.get(i));
	    			ySST.add(y.get(i));
	    			mrdSST.add(mrd.get(i));
	    		}
	    		break;
	    		
	    	case _4MMM:
	    		if(r <= 1d && y.get(i) >= 0d && y.get(i) <= x.get(i)  ) {	 
	    			
	    			xSST.add(x.get(i));
	    			ySST.add(y.get(i));
	    			mrdSST.add(mrd.get(i));
	    		}
	    		break;
	    		
	    	case MMM:
	    		if(r <= 1d && y.get(i) >= 0d && x.get(i) >= 0d) {	 
	    			
	    			xSST.add(x.get(i));
	    			ySST.add(y.get(i));
	    			mrdSST.add(mrd.get(i));
	    		}
	    		break;
	    		
	    		default: break;
	    	}   	
		}
		System.out.println(x.size() + " -> " + xSST.size());
		
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
							
		for(double val : mrdSST) {
			if(val > max) max = val;
			if(val < min) min = val;
		}
						
		this.mrd = new ArrayList<Double>();
		this.x = new ArrayList<Integer>();
		this.y = new ArrayList<Integer>();
		
		for(int i = 0; i < xSST.size(); i++) {
			
			this.mrd.add( (mrdSST.get(i) - min) / (max - min) );
			
/*			final int newX = GAPX + (2*RADIUS-WIDTH) + (int)Math.round(x.get(i) * WIDTH);
			final int newY = GAPY + (2*RADIUS-WIDTH)/2 - (int)Math.round(y.get(i) * WIDTH);
	*/		
			final int newX = (int)Math.round(xSST.get(i) * WIDTH);
			final int newY = (int)Math.round(ySST.get(i) * WIDTH);


			this.x.add(newX);
			this.y.add(newY);					
		}		
		System.out.println(TANPI8);
		calcMapSST();
	}
	
	public final void setPtGrp(PointGroup gr) {
		this.ptGrp =  gr;
	}
	
	
	public final void setColormap(ColormapNames colmap) {
		this.colmap = colmap;
	}
	
	

	public GBCD_GBPB_PreviewFigure() {
		
		setMinimumSize(new Dimension(2*GAPX + 2*RADIUS + LEGENDX, 2*GAPY + 2*RADIUS));  
		setMaximumSize(new Dimension(2*GAPX + 2*RADIUS + LEGENDX, 2*GAPY + 2*RADIUS));  
		setPreferredSize(new Dimension(2*GAPX + 2*RADIUS + LEGENDX, 2*GAPY + 2*RADIUS));
		clearPoints();
		ptGrp = PointGroup.M3M;
		colmap = ColormapNames.SPECTRUM;
		
	}
	
	
	
	private final double distanceSq(int x1, int y1, int x2, int y2)
	{
		final int diff_x, diff_y;
		diff_x = x1 - x2;
		diff_y = y1 - y2;		
		return diff_x*diff_x + diff_y*diff_y;
	}
	
	
	public final void setNLevels(int nlev) {
		NLEVELS = nlev;
	}
	
	
	//TODO
	private final void calcMapSST() {
		
		
		colorLevels = new Color[NLEVELS + 1];
		
		colorLevels[0] = mapColors(FastMath.max(0f, scalemin));
		colorLevels[NLEVELS] = mapColors(FastMath.min(1f, scalemax));
				
								
		for(int i = 1; i < NLEVELS; i++) {
			
			float color2map = scalemin + (float)i / (float)(NLEVELS)*(scalemax-scalemin);
			if(color2map > 1f) color2map = 1f;
			if(color2map < 0f) color2map = 0f;			
					
			colorLevels[i] = mapColors(color2map);
		}
		
	
		KDTree kd = new KDTree(2);
		try {
			for(int k = 0; k < x.size(); k++)
				kd.insert(new double[]{x.get(k), y.get(k)}, new DistPt(x.get(k), y.get(k), mrd.get(k)) );
			
		} catch (KeySizeException | KeyDuplicateException e) {
			e.printStackTrace();
		} 
		
		//TODO ?
		final int sizeX = WIDTH + 1;
		final int sizeY = WIDTH + 1;
		
		pixelCols = new Color[sizeX][sizeY];
		
		
		for(int i = 0; i < sizeX; i++) {
			
			for(int j = 0; j < sizeY; j++) {
				
				pixelCols[i][j] = Color.white; //default;
				
				//TODO triangle only
				
				double x2 = (double) i / (double) WIDTH;
				double y2 = (double) j / (double) WIDTH;
				
				final double r = x2*x2 + y2*y2;
				
				switch(ptGrp) {
		    	case M3M:
		    		x2 *= TANPI8;
		    		y2 *= TANPI8;
		    		
		    		final double cosarctg = FastMath.cos( FastMath.atan2(y2, x2) );	
		    		if( !( x2 <= cosarctg * FastMath.tan( 0.5d * FastMath.atan(1d / cosarctg) ) 
		    				&& y2 >= 0 && y2 <= x2) ) continue;
		    		break;
		    		
		    	case _6MMM:
		    		
		    		
		    		if(! (r <= 1d && y2 >= 0d && y2 <= x2 / SQRT3 )) continue;
		    		break;
		    		
		    	case _4MMM:
		    		if(! (r <= 1d && y2 >= 0d && y2 <= x2  )) continue;
		    		break;
		    		
		    	case MMM:
		    		if(! (r <= 1d && y2 >= 0d && x2 >= 0d ))  continue;
		    		break;
		    		
		    		default: break;
		    	}   	
				
				
				
								
				Object[] nearestObj = null;
				try {
					nearestObj = kd.nearest(new double[]{i, j}, 6);
				} catch (IllegalArgumentException | KeySizeException e) {
					e.printStackTrace();
				} 
			
							
				double nominator = 0d;
				double denominator = 0d;
				
				for(Object obj : nearestObj) if(obj != null) {
					
					final DistPt distPt = (DistPt) obj;
					
					final double distSq = distanceSq(i, j,  distPt.getX(), distPt.getY());
														
					double invDist = 1d / (distSq + 1e-12);
					
									
						
					nominator += invDist * distPt.getMRD();
					denominator += invDist;					
				}
				
				pixelCols[i][j] =  colorLevels[(int) Math.min(NLEVELS, Math.floor(nominator / denominator * (NLEVELS+1))) ];				
			}			
		}
		
		isClear = false;		
	}
	
	
	private final void calcMap() {
		
		
		colorLevels = new Color[NLEVELS + 1];
		
		colorLevels[0] = mapColors(FastMath.max(0f, scalemin));
		colorLevels[NLEVELS] = mapColors(FastMath.min(1f, scalemax));
								
		for(int i = 1; i < NLEVELS; i++) {
			
			float color2map = scalemin + (float)i / (float)(NLEVELS)*(scalemax-scalemin);
			if(color2map > 1f) color2map = 1f;
			if(color2map < 0f) color2map = 0f;			
					
			colorLevels[i] = mapColors(color2map);
		}
		
		/*colorLevels[0] = mapColors(0);
		colorLevels[NLEVELS] = mapColors(1);
								
		for(int i = 1; i < NLEVELS; i++) colorLevels[i] = mapColors((float)i / (float)(NLEVELS));
		*/
	
		KDTree kd = new KDTree(2);
		
		try {
			for(int k = 0; k < x.size(); k++)
				kd.insert(new double[]{x.get(k), y.get(k)}, new DistPt(x.get(k), y.get(k), mrd.get(k)) );
			
		} catch (KeySizeException | KeyDuplicateException e) {
			e.printStackTrace();
		} 
		
		final int sizeX = maxx - minx + 1;
		final int sizeY = maxy - miny + 1;
		
		pixelCols = new Color[sizeX][sizeY];
		
		
		for(int i = 0; i < sizeX; i++) {
			
			for(int j = 0; j < sizeY; j++) {
				
				Object[] nearestObj = null;
				try {
					nearestObj = kd.nearest(new double[]{i, j}, 7);
				} catch (IllegalArgumentException | KeySizeException e) {
					e.printStackTrace();
				} 
			
							
				double nominator = 0d;
				double denominator = 0d;
				
				for(Object obj : nearestObj) if(obj != null) {
					
					final DistPt distPt = (DistPt) obj;
					
					final double distSq = distanceSq(i, j,  distPt.getX(), distPt.getY());
														
					double invDist = 1d / (distSq + 1e-12);
					
									
						
					nominator += invDist * distPt.getMRD();
					denominator += invDist;					
				}
				
				pixelCols[i][j] =  colorLevels[(int) Math.min(NLEVELS, Math.floor(nominator / denominator * (NLEVELS+1))) ];				
			}			
		}
		
		isClear = false;
		

	}
	
	private final float properVal(float val) {
		if(val > 1f) return 1f;
		if(val < 0f) return 0f;
		return val;
	}
	
	private final float R(float x) {
		
		if(x <= 0.37d) {
			return 0f;
		} else if(x > 0.37d && x <= 0.63d) {
			return properVal(-1.42308f + 3.84615f * x);					
		} else if(x > 0.63d && x <= 0.87d) {
			return 1f;
		} else {
			return properVal(-3.84615f*x + 4.34615f);
		}
	}
	
	private final float G(float x) {
		
		if(x <= 0.13d) {
			return 0f;
		} else if(x > 0.13d && x <= 0.37d) {
			return properVal(4.16667f * x - 0.54167f);
		} else if(x > 0.37d && x <= 0.63d) {
			return 1f;
		} else if(x > 0.63d && x <= 0.87d) {
			return properVal(3.625f - 4.16667f * x);
		} else {
			return 0f;
		}
	}
	
	private final float B(float x) {
		
		if(x <= 0.13d) {
			return properVal(0.5f + 3.84615f * x);
		} else if(x > 0.13d && x <= 0.37d) {
			return 1f;
		} else if(x > 0.37d && x <= 0.63d) {
			return properVal(2.42308f - 3.84615f * x);
		} else {
			return 0f;
		}
	}
	
	private final Color mapColors(float val) {
			
		int index = Math.round( val / 0.01f);
		switch(colmap) {
		
		case SPECTRUM: return new Color((float)ColorMaps.SpectralRGB[index][0], (float)ColorMaps.SpectralRGB[index][1], (float)ColorMaps.SpectralRGB[index][2]);
		case REDBLUE: return new Color((float)ColorMaps.RedBlueRGB[index][0], (float)ColorMaps.RedBlueRGB[index][1], (float)ColorMaps.RedBlueRGB[index][2]);
		case YELLOWGRAY: return new Color((float)ColorMaps.GrayYellowRGB[index][0], (float)ColorMaps.GrayYellowRGB[index][1], (float)ColorMaps.GrayYellowRGB[index][2]);
		case GNUPLOT: return new Color((float)ColorMaps.GnuplotRGB[index][0], (float)ColorMaps.GnuplotRGB[index][1], (float)ColorMaps.GnuplotRGB[index][2]);
		case BLUEYELLOWRED: return new Color((float)ColorMaps.BlueYellowRedRGB[index][0], (float)ColorMaps.BlueYellowRedRGB[index][1], (float)ColorMaps.BlueYellowRedRGB[index][2]);
		case BONE: return new Color((float)ColorMaps.BoneRGB[index][0], (float)ColorMaps.BoneRGB[index][1], (float)ColorMaps.BoneRGB[index][2]);
		case REDGRAY: return new Color((float)ColorMaps.RedGrayRGB[index][0], (float)ColorMaps.RedGrayRGB[index][1], (float)ColorMaps.RedGrayRGB[index][2]);
		
			default: return new Color(R(val), G(val), B(val)); 
		}
	}
	
	
	@Override  
    protected void paintComponent(Graphics g) {
		


		im = new BufferedImage(2*GAPX + 2*RADIUS + LEGENDX, 2*GAPY + 2*RADIUS, BufferedImage.TYPE_INT_RGB);
		
		Graphics2D g2d_pane = (Graphics2D) g;
		
		g2d = im.createGraphics();
		
		
		
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		g2d.setBackground(Color.WHITE);
		g2d.clearRect(0, 0, 2*GAPX + 2*RADIUS+LEGENDX, 2*GAPY + 2*RADIUS);
		
		if(!isClear) {
			
			if(SSTonly) {
				for(int i = 0; i < WIDTH; i++) {
					for(int j = 0; j < WIDTH; j++) {
			
						final int posx = GAPX + 200 + i;
						final int posy = GAPY + 400 - j;
						g2d.setColor( pixelCols[i][j] );
						g2d.drawOval(posx, posy, 1, 1);
					}
				}
			} 
			else
			{
				final int sizeX = maxx - minx + 1;
				final int sizeY = maxy - miny + 1;
			
				for(int i = 0; i < sizeX; i++) {
					for(int j = 0; j < sizeY; j++) {
			
						final int posx = GAPX + i + minx;
						final int posy = GAPY + j + miny;
						if((GAPX+RADIUS-posx)*(GAPX+RADIUS-posx) + (GAPY+RADIUS-posy)*(GAPY+RADIUS-posy) < RADIUS*RADIUS) {
							g2d.setColor( pixelCols[i][j] );
							g2d.drawOval(posx, posy, 1, 1);
						}
					}
				}
			}
			
			
			
			//legend
			//g2d.setStroke(new BasicStroke(1f)); //TODO ?
			
			for(int i = 0; i < NLEVELS+1; i++) {
								
				g2d.setColor(colorLevels[i]);
				
				int end = GAPY+RADIUS+170 - (int)( (double)(i)/(double)(NLEVELS+1)* 340 );
				int start = GAPY+RADIUS+170- (int)( (double)(i+1)/(double)(NLEVELS+1)* 340);
				if(i==NLEVELS) start = GAPY+RADIUS-170;
			
				g2d.fillRect(2*GAPX + 2*RADIUS+11, start, 30, end-start);
			}
				
			final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat df;		
			
			g2d.setFont(new Font(null, Font.PLAIN, fontsize));
			
			String decformat = "0.";
			for(int i = 0; i < decPlaces; i++) decformat += '#';
			
			df = new DecimalFormat(decformat, otherSymbols);

			
		    g2d.setColor(Color.BLACK);
		    
		    String s1 = df.format(max);
		    String s2 = df.format(min);
		    System.out.println(min);
		    g2d.drawString(s1, 2*GAPX + 2*RADIUS+26 - g2d.getFontMetrics().stringWidth(s1)/2,
		    		GAPY+RADIUS-170 - (int)(0.66*g2d.getFontMetrics().getHeight() / 2));
		    g2d.drawString(s2, 2*GAPX + 2*RADIUS+26 - g2d.getFontMetrics().stringWidth(s2)/2, 
		    		GAPY+RADIUS+170 + (int)(1.83*g2d.getFontMetrics().getHeight() / 2));
		
		}
		
		
		//legend frame
		g2d.setColor(Color.BLACK);
		g2d.setStroke(new BasicStroke(1.5f));
		g2d.drawRect(2*GAPX + 2*RADIUS+10, GAPY+RADIUS-170, 30, 340);	
	
		
		//axes
		if(SSTonly) {
			switch(ptGrp) {
			case M3M: PaintUtilities.drawSSTCub(g2d, GAPX, GAPY, RADIUS, WIDTH);
				break;
			case _6MMM: PaintUtilities.drawSSTHex(g2d, GAPX, GAPY, RADIUS, WIDTH); 
				break;
			case _4MMM: PaintUtilities.drawSSTTetr(g2d, GAPX, GAPY, RADIUS, WIDTH);
				break;
			case MMM: PaintUtilities.drawSSTOrth(g2d, GAPX, GAPY, RADIUS, WIDTH);
				break;
			}
		}
		else {
			switch(ptGrp) {
			case M3M: PaintUtilities.drawPoleFigureAxes(g2d, GAPX, GAPY, RADIUS, true);
				break;
			case _6MMM: PaintUtilities.drawPoleFigureHex(g2d, GAPX, GAPY, RADIUS, true); 
				break;
			case _4MMM: PaintUtilities.drawPoleFigureTetr(g2d, GAPX, GAPY, RADIUS, true);
				break;
			case MMM: PaintUtilities.drawPoleFigureOrth(g2d, GAPX, GAPY, RADIUS, true);
				break;
			}
		}
		
        g2d_pane.drawImage(im, null, 0, 0);

	}
	
	
	public final void exportToPNG(String f) throws IOException {
		
		ImageIO.write(im, "png" , new File(f));
	}
	
	
	public final void exportToEPS(String f) throws IOException {
		
		EPSGraphics2D vecg = new EPSGraphics2D(0,0, 2*GAPX + 2*RADIUS+ LEGENDX, 2*GAPY + 2*RADIUS);
		
		vecg.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		
		vecg.setBackground(Color.WHITE);
		vecg.clearRect(0, 0, 2*GAPX + 2*RADIUS+LEGENDX, 2*GAPY + 2*RADIUS);
		
		
		
		if(!isClear) {
			
			if(SSTonly) {
				for(int i = 0; i < WIDTH; i++) {
					for(int j = 0; j < WIDTH; j++) {
			
						final int posx = GAPX + 200 + i;
						final int posy = GAPY + 400 - j;
						vecg.setColor( pixelCols[i][j] );
						vecg.drawOval(posx, posy, 1, 1);
					}
				}
			} 
			else
			{
				final int sizeX = maxx - minx + 1;
				final int sizeY = maxy - miny + 1;
			
				for(int i = 0; i < sizeX; i++) {
					for(int j = 0; j < sizeY; j++) {
			
						final int posx = GAPX + i + minx;
						final int posy = GAPY + j + miny;
						if((GAPX+RADIUS-posx)*(GAPX+RADIUS-posx) + (GAPY+RADIUS-posy)*(GAPY+RADIUS-posy) < RADIUS*RADIUS) {
							vecg.setColor( pixelCols[i][j] );
							vecg.drawOval(posx, posy, 1, 1);
						}
					}
				}
			}
			
			
			
			//legend
			//g2d.setStroke(new BasicStroke(1f)); //TODO ?
			
			for(int i = 0; i < NLEVELS+1; i++) {
								
				vecg.setColor(colorLevels[i]);
				
				int end = GAPY+RADIUS+170 - (int)( (double)(i)/(double)(NLEVELS+1)* 340 );
				int start = GAPY+RADIUS+170- (int)( (double)(i+1)/(double)(NLEVELS+1)* 340);
				if(i==NLEVELS) start = GAPY+RADIUS-170;
			
				vecg.fillRect(2*GAPX + 2*RADIUS+11, start, 30, end-start);
			}
				
			final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
			final DecimalFormat df;		
			
			
				vecg.setFont(new Font(null, Font.PLAIN, fontsize));
				df = new DecimalFormat("0.#", otherSymbols);
			
			
			vecg.setColor(Color.BLACK);
		    
		    String s1 = df.format(max);
		    String s2 = df.format(min);
		    vecg.drawString(s1, 2*GAPX + 2*RADIUS+26 - g2d.getFontMetrics().stringWidth(s1)/2,
		    		GAPY+RADIUS-210 + g2d.getFontMetrics().getHeight() / 2);
		    vecg.drawString(s2, 2*GAPX + 2*RADIUS+26 - g2d.getFontMetrics().stringWidth(s2)/2, 
		    		GAPY+RADIUS+217 /*- g2d.getFontMetrics().getHeight() / 2*/);
		
		}
		
		
		//legend frame
		vecg.setColor(Color.BLACK);
		vecg.setStroke(new BasicStroke(1.5f));
		vecg.drawRect(2*GAPX + 2*RADIUS+10, GAPY+RADIUS-170, 30, 340);	
	
		
		//axes
		if(SSTonly) {
			switch(ptGrp) {
			case M3M: PaintUtilities.drawSSTCub(vecg, GAPX, GAPY, RADIUS, WIDTH);
				break;
			case _6MMM: PaintUtilities.drawSSTHex(vecg, GAPX, GAPY, RADIUS, WIDTH); 
				break;
			case _4MMM: PaintUtilities.drawSSTTetr(vecg, GAPX, GAPY, RADIUS, WIDTH);
				break;
			case MMM: PaintUtilities.drawSSTOrth(vecg, GAPX, GAPY, RADIUS, WIDTH);
				break;
			}
		}
		else {
			switch(ptGrp) {
			case M3M: PaintUtilities.drawPoleFigureAxes(vecg, GAPX, GAPY, RADIUS, true);
				break;
			case _6MMM: PaintUtilities.drawPoleFigureHex(vecg, GAPX, GAPY, RADIUS, true); 
				break;
			case _4MMM: PaintUtilities.drawPoleFigureTetr(vecg, GAPX, GAPY, RADIUS, true);
				break;
			case MMM: PaintUtilities.drawPoleFigureOrth(vecg, GAPX, GAPY, RADIUS, true);
				break;
			}
		}
		
		
			       
		FileOutputStream file = new FileOutputStream(f);		
		file.write(vecg.getBytes());
		
	}
}
