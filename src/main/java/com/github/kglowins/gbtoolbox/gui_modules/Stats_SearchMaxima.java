package com.github.kglowins.gbtoolbox.gui_modules;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.WindowConstants;
import javax.swing.border.EmptyBorder;
import com.github.kglowins.gbtoolbox.utils.Transformations;

import net.miginfocom.swing.MigLayout;
import net.sf.javaml.core.kdtree.KDTree;
import net.sf.javaml.core.kdtree.KeyDuplicateException;
import net.sf.javaml.core.kdtree.KeySizeException;
import com.github.kglowins.gbtoolbox.utils.AxisAngle;
import com.github.kglowins.gbtoolbox.utils.CSLMisor;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.GBDatHeader;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MillerIndices;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.RodriguesParams;
import com.github.kglowins.gbtoolbox.utils.UnitVector;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

import org.apache.commons.math3.util.FastMath;

import com.github.kglowins.gbtoolbox.algorithms.CSLMisorientations;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Font;


public class Stats_SearchMaxima extends JFrame {

	
	private static final double _3div4PISQUARE = 3d / 4d / Math.PI / Math.PI;
	private static final double _1div3 = 1d/3d;
	
	
	private static final double INFTY = Double.MAX_VALUE;

	private static final double ONEPI = Math.PI;
	
	private static final double MAXRHO = 0.62035d;
	
	
	private static final double A = - 0.016767042483d;
	private static final double B = 0.253192477955d;
	private static final double C = - 0.005101415571d;
		
	
	private static final double MAXDIS_M3M =  62.8d;	
	private static final double MAXDIS_6MMM = 93.84d;
	private static final double MAXDIS_4MMM = 98.42d;
	private static final double MAXDIS_MMM = 120d;
	
	final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
	final DecimalFormat df2 = new DecimalFormat("0.##", otherSymbols);
				
	private static final int A_LOT = Integer.MAX_VALUE;


	private JLabel omitLbl;
	
	private JPanel contentPane;
	
	private ArrayList<GBDatHeader> gbFiles;
	
	private SearchMisorOnlyTask task;
		
	private JButton fireBtn;
	private JButton abortBtn;	
	private JProgressBar progressBar;
	
	private boolean eliminate;
	private double areaThreshold;
	
	boolean isCancelled = false;
	
	
	private PointGroup pointGrp;
	private JScrollPane scrollPane;
	private JTextArea textArea;
	
	
	private double misBallRadius;
	private double planeBallRadius;
	

	
	//private double misorSigma;
	//private double planeSigma;
	
	private long nGBs;
	private JLabel lblmisorientationToleranceball;
	private JLabel lbltoleranceForBoundary;
	private JTextField planeBallFld;
	private JTextField misBallFld;
	private JLabel label_4;
	private JLabel label_5;

		
	public Stats_SearchMaxima(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr, long nTotal) {
		
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		this.nGBs = nTotal;
		
		gbFiles = gbs;
		
		pointGrp = gbs.get(0).getPointGrp();
		
		omitLbl = new JLabel();
	
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
		
		
		
		setTitle("GBToolbox: Search for maxima in the distibution");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_SearchMaxima.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[grow]", "[][][][][][grow]"));
		
		contentPane.add(omitLbl, "cell 0 0");
		
		lblmisorientationToleranceball = new JLabel("<html>Misorientation tolerance (\u2245 \u00BD \"bin width\"):");
		contentPane.add(lblmisorientationToleranceball, "flowx,cell 0 1");
		
		lbltoleranceForBoundary = new JLabel("<html>Tolerance for boundary plane parameters (\u2245 \u00BD \"bin width\"):");
		contentPane.add(lbltoleranceForBoundary, "flowx,cell 0 2");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 3,growx,aligny center,gapy 5 5");
		
		fireBtn = new JButton("Calculate");
		contentPane.add(fireBtn, "flowx,cell 0 4");
		
		
		fireBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				double maxDisor = 0d;
				try {
					misBallRadius = Double.parseDouble(misBallFld.getText().replace(",","."));					
					if(misBallRadius < 0d) throw new NumberFormatException();
										
					switch(pointGrp) {					
					case M3M: maxDisor = MAXDIS_M3M; break;
					case _6MMM: maxDisor = MAXDIS_6MMM; break;
					case _4MMM: maxDisor = MAXDIS_4MMM; break;
					case MMM: maxDisor = MAXDIS_MMM; break;
						default: break;
					}
					
					if(misBallRadius > maxDisor) throw new NumberFormatException();
					misBallRadius = Math.toRadians(misBallRadius);
					
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SearchMaxima.this,
							"Resolution for misorientations must be a positive decimal number less than " + df2.format(maxDisor) + "\u00b0.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				
				try {
					planeBallRadius = Double.parseDouble(planeBallFld.getText().replace(",","."));
					
					if(planeBallRadius < 0d) throw new NumberFormatException();
						
					planeBallRadius = Math.toRadians(planeBallRadius);
					
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SearchMaxima.this,
							"Resolution for bounbdary planes must be a positive decimal number",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				

				
				
			/*	
				try {
					misorSigma = Double.parseDouble(misSigmaFld.getText().replace(",","."));
					
					if(misorSigma < Math.toDegrees(misBallRadius)) throw new NumberFormatException();
					
					misorSigma = Math.toRadians(misorSigma);
											
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SearchMaxima.this,
							"Separating distance must be a positive decimal number larger than the corresponding radius.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;
				}
				*/
				
				
				

				task = new SearchMisorOnlyTask();
				task.addPropertyChangeListener(new PropertyChangeListener() {
					@Override
					public void propertyChange(PropertyChangeEvent evt) {
						if ("progress".equals(evt.getPropertyName())) {
							progressBar.setValue((Integer)evt.getNewValue());
			             	}						
					}
				});
				task.execute();
			}
		});
		
		
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		contentPane.add(progressBar, "cell 0 4,gapx 20");
		
		abortBtn = new JButton();
		abortBtn.setEnabled(false);
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Stats_SearchMaxima.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(abortBtn, "cell 0 4,gapx 20");
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, "cell 0 5,gapy 10,grow");
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setFont(new Font("Monospaced", Font.PLAIN, 11));
		textArea.setRows(25);
		textArea.setColumns(80);
		scrollPane.setViewportView(textArea);
		
		planeBallFld = new JTextField();
		planeBallFld.setText("7");
		planeBallFld.setHorizontalAlignment(SwingConstants.RIGHT);
		planeBallFld.setColumns(3);
		contentPane.add(planeBallFld, "cell 0 2");
		
		misBallFld = new JTextField();
		misBallFld.setText("2");
		misBallFld.setHorizontalAlignment(SwingConstants.RIGHT);
		misBallFld.setColumns(3);
		contentPane.add(misBallFld, "cell 0 1");
		
		label_4 = new JLabel("\u00B0");
		contentPane.add(label_4, "cell 0 2");
		
		label_5 = new JLabel("\u00B0");
		contentPane.add(label_5, "cell 0 1");
		
	
		
		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				task.cancel(true);				
			}			
		});
		
		
		pack();
		setResizable(true);
	}
	
	

	
	private class SearchMisorOnlyTask extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() throws Exception {
			
			
			long startTime = System.currentTimeMillis();
			
			abortBtn.setEnabled(true);
			fireBtn.setEnabled(false);	
			progressBar.setValue(0);
			
			double totalArea = 0d;
		    
		    // FIND MISORIENTATIONS
		    System.out.println("Generating uniformly distributed misorientations...");
		    		    
		    // generate uniformly dispersed isochoric parameters		    
		    double maxMagnitude = 0d;
		    double maxDisor = 0d;
		    
		    switch(pointGrp) {
	    	case M3M : maxMagnitude = 0.25d; maxDisor = Math.toRadians(MAXDIS_M3M); break;
	    	case _6MMM : maxMagnitude = 0.365d; maxDisor = Math.toRadians(MAXDIS_6MMM); break; 
	    	case _4MMM : maxMagnitude = 0.381d; maxDisor = Math.toRadians(MAXDIS_4MMM); break;
	    	case MMM : maxMagnitude = 0.4536d; maxDisor = Math.toRadians(MAXDIS_MMM); break;
	    	default: break;
		    }
		    		   		    
		    Matrix3x3[] setC = Transformations.getSymmetryTransformations(pointGrp);
		    	  		    		   
		    final double misBallVol = (misBallRadius - FastMath.sin(misBallRadius)) / ONEPI;
		    final double planeBallVol = (1d - FastMath.cos(planeBallRadius)) / 2d;
		    
		    final double ptsInCube = (1d / misBallVol) * Math.pow((maxMagnitude / 0.62035d), 3d) * 1.91d;
		    
		    System.out.println("ptsInCube = " + ptsInCube);
		    
		    final int ptsPerEdge = (int) (Math.ceil( Math.pow(ptsInCube, 1d/3d) ) );
		    
		    System.out.println("ptsPerEdge = " + ptsPerEdge);
		    
		    final double cubeStep = 2d * maxMagnitude / ptsPerEdge;
		    
		    ArrayList<Double> rhoX = new ArrayList<Double>();
		    ArrayList<Double> rhoY = new ArrayList<Double>();
		    ArrayList<Double> rhoZ = new ArrayList<Double>();
		    
		    for(int i = 0; i < ptsPerEdge; i++) {
		    
		    	for(int j = 0; j < ptsPerEdge; j++) {
		    		
		    		for(int k = 0; k < ptsPerEdge; k++) {
		    					    			
		    			final double x = -maxMagnitude + cubeStep/2d + cubeStep*i;
		    			final double y = -maxMagnitude + cubeStep/2d + cubeStep*j;
		    			final double z = -maxMagnitude + cubeStep/2d + cubeStep*k;
		    			
		
		    			final double mag = Math.sqrt(x*x + y*y + z*z);		    			
		    			
		    			if(mag < maxMagnitude && mag > 1e-3d) {
		    				rhoX.add(x);
		    				rhoY.add(y);
		    				rhoZ.add(z);
		    			}				 
				    }	
			    }
		    }
		    
		    	
		    // transform isochoric params to axis/angle
		    // transform axis/angle to rodrigues    	    		    
		    // select those in asymemtric domain    	        	    		    
		    // transform to matrices
						
		    final ArrayList<Matrix3x3> misorMatrices = new ArrayList<Matrix3x3>();
		    		    
		    KDTree kd_mis = new KDTree(3);
		    int mis_id = -1;
		    
		    // m3m
		    final double sqrt2_minOne = Math.sqrt(2d) - 1d;
			final double[] sign = new double[]{-1d, 1d};
			// 6/mmm
			final double a = 0.5d;
			final double b = Math.sqrt(3d) * 0.5d;
			final double oneDivSqrt3 = 1d / Math.sqrt(3d);			
			//4mmm
			final double sqrt2 = Math.sqrt(2d);
			
			
			int unsolved = 0;
		    
    	    for(int i = 0; i < rhoX.size(); i++) {     
    	    	    	    	
 	    	
    	    	final UnitVector n = new UnitVector();
    			n.set(rhoX.get(i), rhoY.get(i), rhoZ.get(i));
      			
    			
    			int N = 0;
    			double avg = 0d;
    			if(Math.abs(n.x()) > 1e-4) { avg += rhoX.get(i) / n.x(); N++; }
    			if(Math.abs(n.y()) > 1e-4) { avg += rhoY.get(i) / n.y(); N++; }
    			if(Math.abs(n.z()) > 1e-4) { avg += rhoZ.get(i) / n.z(); N++; }
    			avg /= N;
    			
    			
    			double delta = B*B - 4d * A * (C - avg);
    			
    			if(delta < 0d) {
    				unsolved++;
    				continue;
    			} else delta = Math.sqrt(delta);
    			
    			final double omega1 = (-B - delta) / 2d / A;
    			final double omega2 = (-B + delta) / 2d / A;
    			  			    			
    			AxisAngle aa = new AxisAngle();
    			
    			if(omega1 > 0d && omega1 < maxDisor) {
    				
    				aa.set(n, omega1);
    				
    			} else if (omega2 > 0d && omega2 < maxDisor) {
    				
    				aa.set(n, omega2);
    				
    			} else {
    				unsolved++;
    				continue;
    			}
    	    	
    	    	
    	    	final RodriguesParams rodr = new RodriguesParams();
    	    	rodr.set(aa);
    	    	

    	    	    	    	
    	    	switch(pointGrp) {
    	    	
    	    	case M3M: 
    	    		
    	    		boolean asym = true;
        			       		
        			if(asym) if( Math.abs(rodr.r1()) > sqrt2_minOne ||
        				Math.abs(rodr.r2()) > sqrt2_minOne || 
        				Math.abs(rodr.r3()) > sqrt2_minOne ) {
        				asym = false;
        				break;
        			}
        			
        			if(asym) {
        				for(double s1 : sign) {
        					for(double s2 : sign) {
        						for(double s3 : sign) {
        							if(1d < s1*rodr.r1() + s2*rodr.r2() + s3*rodr.r3()) {
        								asym = false;
        								break;
        							}
        						}    			
        						if(!asym) break;
        					}
        					if(!asym) break;    				    					
        				}
        			}
        			
        			if(asym) {    	
        				
        				final Matrix3x3 M = new Matrix3x3();
        				M.set(aa);        		
        				misorMatrices.add(M);
        				
        				try {
        					kd_mis.insert(new double[]{rhoX.get(i), rhoY.get(i), rhoZ.get(i)}, new Integer(++mis_id) );
        					
        				} catch (KeySizeException | KeyDuplicateException e) {
        					e.printStackTrace();
        				} 
        			}
    	    		
    	    		break;
    	    		
    	    		
    	    	case _6MMM:
        			asym = true;
        			        			        			
        			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
        				asym = false;
        			}
        			   		    			
        			if(asym) {    				
        				for(double s : sign) {						
       						if( 1d < Math.abs(a + s*b*rodr.r3() ) ||
        						1d < Math.abs(a*rodr.r1() + s*b*rodr.r2() ) ||
        						1d < Math.abs(b*rodr.r1() + s*a*rodr.r2() ) ||
        						1d < Math.abs(b + s*a*rodr.r3()) ) {
        							
       							asym = false;
       							break;
       						}    						
        				}
        			}
        			if(asym) {
        				
        				final Matrix3x3 M = new Matrix3x3();
        				M.set(aa);        		
        				misorMatrices.add(M);
        				
        				try {
        					kd_mis.insert(new double[]{rhoX.get(i), rhoY.get(i), rhoZ.get(i)}, new Integer(++mis_id) );
        					
        				} catch (KeySizeException | KeyDuplicateException e) {
        					e.printStackTrace();
        				} 
        			} 	
        			break;
        			
        		case _4MMM:
        			asym = true;        			        			
        		            		
        			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
        				asym = false;
        			}
        			
        			if(asym) if( Math.abs(rodr.r3()) > sqrt2_minOne ) asym = false;
        			
        			    			
        			if(asym) {
        				for(double s1 : sign) {
        					for(double s2 : sign) {
        						
        							if(sqrt2 < s1*rodr.r1() + s2*rodr.r2()) {
        								asym = false;
        								break;
        							}    						
        					}   				    					
        				}
        			}
        			
        			if(asym) {
        				
        				final Matrix3x3 M = new Matrix3x3();
        				M.set(aa);        		
        				misorMatrices.add(M);
        				
        				
        				try {
        					kd_mis.insert(new double[]{rhoX.get(i), rhoY.get(i), rhoZ.get(i)}, new Integer(++mis_id) );
        					
        				} catch (KeySizeException | KeyDuplicateException e) {
        					e.printStackTrace();
        				} 
        			} 	
        			break;
        			
        			
        		case MMM:
        			asym = true;
        			
        			if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
        				asym = false;
        			}
        			
        			if(asym) {
        				
        				final Matrix3x3 M = new Matrix3x3();
        				M.set(aa);        		
        				misorMatrices.add(M);
        				
        				try {
        					kd_mis.insert(new double[]{rhoX.get(i), rhoY.get(i), rhoZ.get(i)}, new Integer(++mis_id) );
        					
        				} catch (KeySizeException | KeyDuplicateException e) {
        					e.printStackTrace();
        				} 
        			} 	
        			break;
	
        			
   	    		default: break;
    	    	}    	    	
    	    }
    	    
    	    System.out.println("Unsolved: " + unsolved);   	    	
    	    System.out.println("ptsInBall = " + rhoX.size());
		    System.out.println("ptsInAsymDom = " + mis_id);
		    
		    // GENERATE UNIFORMLY DISPERSED BOUNDARY PLANES
		    System.out.println("Generating uniformly dispersed boundary plane normals...");
		   	
		    // initial points Golden Ratio Spiral						
			final ArrayList<UnitVector> allPts = new ArrayList<UnitVector>();
			
			KDTree kd_plane = new KDTree(3);
			
			System.out.println("planesOnSphere = " + (int) (1d/planeBallVol));
		    
			int nPts = (int) (1d/planeBallVol);
			if(!isCancelled()) {	
			
				final double inc = Math.PI * (3d - Math.sqrt(5d));
			    final double off = 2d / nPts;
			    
			    for(int k = 0; k < nPts; k++) {
			        final double y = k * off - 1d + (off / 2d);
			        final double r = Math.sqrt(1d - y*y);
			        final double phi = k * inc;
			        
			        final UnitVector newPoint = new UnitVector();	        
					newPoint.set(FastMath.cos(phi)*r, y, FastMath.sin(phi)*r);
					allPts.add(newPoint);
					
					try {
    					kd_plane.insert(new double[]{newPoint.x(), newPoint.y(), newPoint.z()}, new Integer(k) );
    					
    				} catch (KeySizeException | KeyDuplicateException e) {
    					e.printStackTrace();
    				} 
					
			    }    
			}
		    
		    // INIT DISTRIBUTION
		    final double[][] fractions = new double[mis_id+1][allPts.size()];
		    for(int i = 0; i < fractions.length; i++) 
		    	for(int j = 0; j < fractions[i].length; j++) fractions[i][j] = 0d;
		    
		    
		    // LOOP OVER MESH SEGMENTS		
		    System.out.println("Entering the loop over the GBs in the files...");
		    
		    long nproc = 0;
		    
			final boolean expData = gbFiles.get(0).isExperimental();
			GBDatHeader header = null;

			Iterator<GBDatHeader> iterator = gbFiles.iterator();
			
			while(iterator.hasNext() && !isCancelled()) {

				header = iterator.next();
				System.out.println("Processing: " + header.getPath());
				final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
			
				GBDatHeader.skipHeaderLines(in);
			
				String line = null;
									
				while ((line = in.readLine()) != null && !isCancelled()) {
						
					nproc++;
					
									
					final String[] num = line.trim().split("\\s+");
								
					double A = 1d;					
					if(expData) A = Double.parseDouble(num[9]);
					
					
													
					if(!eliminate || (eliminate && A <= areaThreshold) ) {
						
											
						totalArea += A;
					
						final double phi1L = Math.toRadians( Double.parseDouble(num[0]));
						final double PhiL = Math.toRadians( Double.parseDouble(num[1]));
						final double phi2L = Math.toRadians( Double.parseDouble(num[2]));
					
						final double phi1R = Math.toRadians( Double.parseDouble(num[3]));
						final double PhiR = Math.toRadians( Double.parseDouble(num[4]));
						final double phi2R = Math.toRadians( Double.parseDouble(num[5]));
							
						final double zenith = Math.toRadians( Double.parseDouble(num[6]));
						final double azimuth = Math.toRadians( Double.parseDouble(num[7]));
						
						
						final EulerAngles eulL = new EulerAngles();
						eulL.set(phi1L, PhiL, phi2L);
						final EulerAngles eulR = new EulerAngles();
						eulR.set(phi1R, PhiR, phi2R);
													
						final Matrix3x3 ML = new Matrix3x3();
						ML.set(eulL);
						final Matrix3x3 MR = new Matrix3x3();
						MR.set(eulR);

						final Matrix3x3 M = new Matrix3x3(ML);
						M.timesTransposed(MR);
												
						final UnitVector m1 = new UnitVector();
						m1.set(zenith, azimuth);		
						m1.transform(ML); 
									
						final InterfaceMatrix __B = new InterfaceMatrix(M, m1);
						
						
						for(boolean transpose : new boolean[]{false, true}) {
							
							for(Matrix3x3 C1 : setC) {
															
								for(Matrix3x3 C2 : setC) {
									
									final InterfaceMatrix B = new InterfaceMatrix(__B);
									if(transpose) B.transpose();
									B.applySymmetry1(C1);
									B.applySymmetry2(C2);
									
									
									final AxisAngle aa = new AxisAngle();
									aa.set(B.M());
						
									//verify if in the domain
						
					    	    	final RodriguesParams rodr = new RodriguesParams();
					    	    	rodr.set(aa);
					    	    	

					    	    	boolean asym = true;    	
					    	    	switch(pointGrp) {
					    	    	
					    	    	case M3M: 

					        			if(asym) if( Math.abs(rodr.r1()) > sqrt2_minOne ||
					        				Math.abs(rodr.r2()) > sqrt2_minOne || 
					        				Math.abs(rodr.r3()) > sqrt2_minOne ) {
					        				asym = false;
					        				break;
					        			}
					        			
					        			if(asym) {
					        				for(double s1 : sign) {
					        					for(double s2 : sign) {
					        						for(double s3 : sign) {
					        							if(1d < s1*rodr.r1() + s2*rodr.r2() + s3*rodr.r3()) {
					        								asym = false;
					        								break;
					        							}
					        						}    			
					        						if(!asym) break;
					        					}
					        					if(!asym) break;    				    					
					        				}
					        			}	
					    	    		break;
					    	    		
					    	    		
					    	    	case _6MMM:
					    	    		
					        			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
					        				asym = false;
					        			}
					        			   		    			
					        			if(asym) {    				
					        				for(double s : sign) {						
					       						if( 1d < Math.abs(a + s*b*rodr.r3() ) ||
					        						1d < Math.abs(a*rodr.r1() + s*b*rodr.r2() ) ||
					        						1d < Math.abs(b*rodr.r1() + s*a*rodr.r2() ) ||
					        						1d < Math.abs(b + s*a*rodr.r3()) ) {
					        							
					       							asym = false;
					       							break;
					       						}    						
					        				}
					        			}
	
					        			break;
					        			
					        		case _4MMM:
					        			
					        			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
					        				asym = false;
					        			}
					        			
					        			if(asym) if( Math.abs(rodr.r3()) > sqrt2_minOne ) asym = false;
					        			
					        			    			
					        			if(asym) {
					        				for(double s1 : sign) {
					        					for(double s2 : sign) {
					        						
					        							if(sqrt2 < s1*rodr.r1() + s2*rodr.r2()) {
					        								asym = false;
					        								break;
					        							}    						
					        					}   				    					
					        				}
					        			}
					        			
					        			break;
					        			
					        			
					        		case MMM:
					        			asym = true;
					        			
					        			if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
					        				asym = false;
					        			}
					        			
					        			break;
							
					   	    		default: break;
					    	    	}
					    	    	
					    	    	if(!asym) continue;
					    	    	
									// end verify
									
									final double f = FastMath.pow(_3div4PISQUARE * (aa.angle() - FastMath.sin(aa.angle())), _1div3 );
					    			
									final double rho1 = f * aa.axis().x();
									final double rho2 = f * aa.axis().y();
									final double rho3 = f * aa.axis().z();
							    			
							    			
									int bin_mis = -1;
									try {
										bin_mis = (Integer) kd_mis.nearest(new double[]{rho1, rho2, rho3}); 
									} catch (IllegalArgumentException | KeySizeException e) {
										e.printStackTrace();
									} 
							    			
									
							    					
									int bin_plane = -1;
									try {
										bin_plane = (Integer) kd_plane.nearest(new double[]{B.m1().x(), B.m1().y(), B.m1().z()}); 
									} catch (IllegalArgumentException | KeySizeException e) {
										e.printStackTrace();
									} 
							    					
							    					
			    					fractions[bin_mis][bin_plane] += A;
			    					
			    					B.toMinus();
			    					
			    					try {
										bin_plane = (Integer) kd_plane.nearest(new double[]{B.m1().x(), B.m1().y(), B.m1().z()}); 
									} catch (IllegalArgumentException | KeySizeException e) {
										e.printStackTrace();
									} 
							    					
							    					
			    					fractions[bin_mis][bin_plane] += A;
									
									
								}
								
							}
							
						}
						

					}	
					setProgress((int)Math.round((double)nproc/(double)nGBs*50d));
				}
			}
		    
	
			//normalize fractions & find maxima
			
			if(!isCancelled()) {
				System.out.println("Normalizing & looking for the maxima...");
						
				ArrayList<DistValue> gbd = new ArrayList<DistValue>();
		
				final double[] maxMRDplanes = new double[mis_id+1];
				for(int i = 0; i < fractions.length; i++) maxMRDplanes[i] = 0d;
				
				
			    for(int i = 0; i < fractions.length; i++) {
			    	
			    	double maxMRD = Double.MIN_VALUE;
			    	
			    	for(int j = 0; j < fractions[i].length; j++) {
			    		
			    		fractions[i][j] /= totalArea;		    		
			    		fractions[i][j] /= 4d *setC.length * setC.length * misBallVol * planeBallVol;
			    		
			    		if(fractions[i][j] > maxMRD) maxMRD = fractions[i][j];
			    	}
			    	
			    	if(maxMRD > 0.999999d) gbd.add(new DistValue(misorMatrices.get(i), maxMRD));
			    }
	
			    DistValComparator comparator = new DistValComparator();		   
			    Collections.sort(gbd, comparator); 
			    
			    
			    CSLMisor[] csl = null;
			    if(pointGrp == PointGroup.M3M) {
					csl = CSLMisorientations.getForCubic(49);
			    }
			    
				// WRITE THE OUTPUT
			    	  
			    textArea.setText("");		    
			    for(int i = 0; i < gbd.size(); i++) {
			    	
			    	if(isCancelled()) break;
			    	
			    	final AxisAngle aa = new AxisAngle();
			    	aa.set(gbd.get(i).getM());
			    	
			    	//check if axis in SST
	    	    	final RodriguesParams rodr = new RodriguesParams();
	    	    	rodr.set(aa);
	
	    	    	boolean asym = true;    	    	
	    	    	switch(pointGrp) {
	    	    	
	    	    	case M3M: 
	
	        			if(rodr.r1() < rodr.r2() || rodr.r2() < rodr.r3() || rodr.r3() < 0d) asym = false;
	        			break;
	    	    		
	    	    		
	    	    	case _6MMM:
	        			        			        			
	        			if(0d > rodr.r2() || rodr.r2() > oneDivSqrt3*rodr.r1() || 0d > rodr.r3()) asym = false;  			
	        			break;
	        			
	        		case _4MMM:
	        			
	        			if(rodr.r1() < rodr.r2() || rodr.r2() < 0d || rodr.r3() < 0d) asym = false;
			
	        			break;
	        			
	        			
	        		case MMM:
	        			
	        			break;
		
	        			
	   	    		default: break;
	    	    	}    
			    	
	    	    	if(!asym) continue;
			    	// end check
			    	
			    	
			    	final EulerAngles eul = new EulerAngles();
			    	eul.set(gbd.get(i).getM());		    			    	
			    	
			    	final MillerIndices millMis = new MillerIndices();
			    	
			    	millMis.setAsCubic(aa.axis(), 30);
	
			    	textArea.append((i+1) + ":    " + df2.format(gbd.get(i).getValue()) + " au    ");
			    	textArea.append(df2.format(Math.toDegrees(aa.angle())) + "\u00b0/[");
			    	textArea.append(millMis.h() + ",");
			    	textArea.append(millMis.k() + ",");
			    	textArea.append(millMis.l() + "]    ");
			    	
			    	
			    	
			    	textArea.append("{ " + df2.format(Math.toDegrees(eul.phi1())) + "\u00b0, ");
			    	textArea.append(df2.format(Math.toDegrees(eul.Phi())) + "\u00b0, ");
			    	textArea.append(df2.format(Math.toDegrees(eul.phi2())) + "\u00b0 }");
			    	
			    	if(pointGrp == PointGroup.M3M /*|| pointGrp == PointGroup._6MMM*/) {
			    		
			    		// policz
			    		int sigmaFound = A_LOT;
			    		int closestSigma = A_LOT;
			    		double angleIfFound = 0d;
			    		double angleIfNotFound = Double.MAX_VALUE;
						
						for(Matrix3x3 C1 : setC) { // TODO
							for(Matrix3x3 C2 : setC) {
								
								final Matrix3x3 R = new Matrix3x3(C1);
								R.times(gbd.get(i).getM());
								R.timesTransposed(C2);
	
								for(CSLMisor __csl : csl) {
					   	     			
					    			final Matrix3x3 RMcslT = new Matrix3x3(R);   	    		
					   	    		RMcslT.timesTransposed(__csl.getMatrix());
					   	    			
					   	    		double angle = MyMath.acos(0.5d * (RMcslT.tr() - 1d));
					   	    				
					   	    		if(angle < Math.toRadians(15d) / FastMath.sqrt(__csl.getSigma()) ) {
					   	    					
					   	    			sigmaFound = __csl.getSigma();
					   	    			angleIfFound = angle;
					   	    			break;
					   	    		} else if(angle < angleIfNotFound) {
					   	    			
					   	    			closestSigma  = __csl.getSigma();
					   	    			angleIfNotFound = angle;
					   	    		}
					    		}
								if(sigmaFound < A_LOT) break;
							}
							if(sigmaFound < A_LOT) break;
						}
			    		
			    		
			    		// wyswietl
						if(sigmaFound < A_LOT) {
							textArea.append("        \u03a3" + sigmaFound + "(\u00b1"+df2.format(Math.toDegrees(angleIfFound)) + "\u00b0)");
						} else {
							textArea.append("        \u03a3" + closestSigma + "("+df2.format(Math.toDegrees(angleIfNotFound)) + "\u00b0)");
						}
			    	}
			    	
			    	textArea.append("\n");
			    }
			}

			abortBtn.setEnabled(false);
			fireBtn.setEnabled(true);
			setProgress(100);

			long estimatedTime = System.currentTimeMillis() - startTime;
			System.out.println("Time elapsed: " + estimatedTime + " microsec.");
			
			return null;
	
		}

		@Override
		public void done() {						
			fireBtn.setEnabled(true);
			abortBtn.setEnabled(false);
		}
	}
	
	
	private class DistValComparator implements Comparator<DistValue> {
		 
		  @Override
		  public int compare(DistValue d1, DistValue d2) {
			  return -Double.compare(d1.getValue(), d2.getValue());		    
		  }
		}
	
	private class DistValue {
		
	///	private UnitVector m1;
		private Matrix3x3 M;
	//	private double rho1;
	//	private double rho2;
	//	private double rho3;
		private double val;
		
		DistValue(Matrix3x3 M,/* double rho1, double rho2, double rho3, UnitVector m1, */ double val) {
	//		this.rho1 = rho1;
		//	this.rho2 = rho2;
		//	this.rho3 = rho3;
			this.val = val;
	//		this.m1 = m1;
			this.M = M;
		}
		
	/*	public final UnitVector getM1() {
			return m1;
		}
	*/	
		public final Matrix3x3 getM() {
			return M;
		}
		
	/*	public final double getRho1() {
			return rho1;
		}
		
		public final double getRho2() {
			return rho2;
		}
		
		public final double getRho3() {
			return rho3;
		}
		*/
		public final double getValue() {
			return val;
		}	
	}
	
		

	
}
