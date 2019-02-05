package com.github.kglowins.gbtoolbox.gui_modules;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.github.kglowins.gbtoolbox.gui_bricks.HexMillerPlaneFields;
import com.github.kglowins.gbtoolbox.gui_bricks.LatticePanel;
import com.github.kglowins.gbtoolbox.gui_bricks.MillerPlaneFields;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
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
import com.github.kglowins.gbtoolbox.utils.ConstantsAndStatic;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.FileUtils;
import com.github.kglowins.gbtoolbox.utils.GBDatHeader;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MillerIndices;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.UnitVector;
import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JProgressBar;

import org.apache.commons.math3.util.FastMath;


public class Stats_SecondGrainFrame extends JFrame {
	
	private static final String HEX = "HEX";
	private static final String NON_HEX = "NON_HEX";
	
	private static final double SQRT3 = Math.sqrt(3d);

			
	private MillerPlaneFields planeFlds;
	private HexMillerPlaneFields hexPlaneFlds;
		
	private final FileUtils.GBDistFileFilter gbdistFilter = new FileUtils.GBDistFileFilter(); 
	
	private JLabel omitLbl;
	
	private JPanel contentPane;
	private JTextField outFld;
	
	private ArrayList<GBDatHeader> gbFiles;
	
	private LatticePanel latticePane;	
		
	private JButton fireBtn;
	private JButton abortBtn;	
	private JProgressBar progressBar;
	
	private UnitVector M1fix;

	private boolean eliminate;
	private double areaThreshold;
	
	private PointGroup pointGrp;
		
	private JSeparator separator_2;
	private JLabel lbltolerationForBoundary;
	private JTextField planeTolFld;
	private JPanel planeCards;
	private JLabel lbldegrees;
	private JLabel lblboundaryPlanein;
	private JLabel lblmillerIndices;
	private long nGBs;
	
	private SecondGrainTask task;
	private double planeTol;
	private int nBins;
	private JLabel lblnumberOfSampling;
	private JTextField nBinsFld;
				
	public Stats_SecondGrainFrame(ArrayList<GBDatHeader> gbs, boolean elimin, double areaThr, long nTotal) {
		
		this.eliminate = elimin;
		this.areaThreshold = areaThr;
		this.nGBs = nTotal;
		
		gbFiles = gbs;
		omitLbl = new JLabel();
	
		if(elimin) omitLbl.setText("<html><font color=#0000ff>Mesh triangles with area greater than " + areaThr + " will not be taken into account</i></font>");
		else omitLbl.setText("<html><font color=#0000ff>All mesh triangles will be taken into account</i></font>");
		
		
		planeCards = new JPanel();
	
		planeCards.setLayout(new CardLayout(0, 0));
		
		planeFlds = new MillerPlaneFields();
		planeFlds.getKFld().setToolTipText("Miller indices of the boundary plane in the reference frame of the first crystallite");
		hexPlaneFlds = new HexMillerPlaneFields();
		
		planeCards.add(planeFlds, NON_HEX);
		planeCards.add(hexPlaneFlds, HEX);
		
		pointGrp = gbs.get(0).getPointGrp();
		CardLayout cl_planeCards = (CardLayout) planeCards.getLayout();
		if(pointGrp == PointGroup._6MMM) cl_planeCards.show(planeCards, HEX);
		else cl_planeCards.show(planeCards, NON_HEX);
		
		setTitle("GBToolbox: Distribution of boundary planes in the second grain");
		setIconImage(Toolkit.getDefaultToolkit().getImage(Stats_SecondGrainFrame.class.getResource("/gui_bricks/gbtoolbox.png")));
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new MigLayout("", "[][]", "[][][][][][][][][][][][][]"));
		
		contentPane.add(omitLbl, "cell 0 0 2 1");
		
		separator_2 = new JSeparator();
		contentPane.add(separator_2, "cell 0 1 2 1,growx,aligny center,gapy 5 5");
		
		JLabel specifyLbl = new JLabel("<html><b>Specify lattice parameters, set options and calculate distributions of boundary planes:</b>");
		contentPane.add(specifyLbl, "cell 0 2 2 1");
		
		JLabel structLbl = new JLabel("<html><u>Crystal structure</u>:");
		contentPane.add(structLbl, "cell 0 3 2 1,gapx 10,gapy 5");
		
		latticePane = new LatticePanel(null, null); 
		contentPane.add(latticePane, "cell 0 4 2 1,gapx 20,gapy 5");
		
		JSeparator separator_1 = new JSeparator();
		contentPane.add(separator_1, "cell 0 5 2 1,growx,aligny center,gapy 5 5");
		
		latticePane.setPointGroupLocked(pointGrp);
		
		lblboundaryPlanein = new JLabel("<html><u>Boundary plane (in the first grain)</u>:");
		contentPane.add(lblboundaryPlanein, "flowx,cell 0 6,gapx 10,gapy 5");
		
		lblmillerIndices = new JLabel("[Miller indices]");
		contentPane.add(lblmillerIndices, "cell 1 6,gapx 20,gapy 5");
		
		lbltolerationForBoundary = new JLabel("<html><u>Tolerance</u>:");
		contentPane.add(lbltolerationForBoundary, "flowx,cell 0 7,gapx 10,gapy 5");
		
		lbldegrees = new JLabel("[degrees]");
		contentPane.add(lbldegrees, "cell 1 7,gapx 20,gapy 5");
		
		lblnumberOfSampling = new JLabel("<html><u>Number of sampling points (on the upper hemi-sphere)</u>:");
		contentPane.add(lblnumberOfSampling, "flowx,cell 0 8 2 1,gapx 10,gapy 5");
				
		JLabel outLbl = new JLabel("<html><u>Output <code>dist</code> file</u>:");
		contentPane.add(outLbl, "flowx,cell 0 9 2 1,gapx 10,gapy 5");
		
		JSeparator separator = new JSeparator();
		contentPane.add(separator, "cell 0 10 2 1,growx,aligny center,gapy 5 5");
		
		fireBtn = new JButton("Calculate");
		contentPane.add(fireBtn, "flowx,cell 0 11 2 1");
		
		JLabel overwriteLbl = new JLabel("<html><font color=#cc0000><small>Warning: <b>Existing files will be overwritten!</b></small></font>");
		contentPane.add(overwriteLbl, "cell 0 12 2 1,gapy 5");
		
		outFld = new JTextField();
		contentPane.add(outFld, "cell 0 9 2 1,gapy 5");
		outFld.setColumns(18);
		
		JButton outBtn = new JButton();
		outBtn.setMinimumSize(new Dimension(24,24));
		outBtn.setMaximumSize(new Dimension(24,24));
		outBtn.setPreferredSize(new Dimension(24,24));
		outBtn.setIcon(new ImageIcon(Stats_SecondGrainFrame.class.getResource("/gui_bricks/folder.png")));
		contentPane.add(outBtn, "cell 0 9 2 1,gapy 5");
		
		final JFileChooser fc = new JFileChooser();
		fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fc.setAcceptAllFileFilterUsed(false);
		fc.addChoosableFileFilter(gbdistFilter);
		
		outBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {			
				
				int returnVal = fc.showDialog(Stats_SecondGrainFrame.this, "Save");
				if(returnVal == JFileChooser.APPROVE_OPTION) {
					final File f = fc.getSelectedFile();
					String fName = f.getAbsolutePath();						
					final String ext = FileUtils.getExtension(f);
					
					if(ext != null) {
						if(ext.compareTo("dist") != 0) fName += ".dist";
					} else {							
						if(fName.charAt(fName.length() - 1) == '.') fName += "dist";
						else fName += ".dist";
					}
					
					outFld.setText(fName);
				}	
				
								
			}	
		});
		
		progressBar = new JProgressBar();
		progressBar.setStringPainted(true);
		
		fireBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent evt) {
				
				M1fix = new UnitVector();
				
				// read the vector
				try {
					switch(latticePane.getPointGroup()) {
					
					case M3M:
						int h = Integer.parseInt(planeFlds.getHFld().getText());
						int k = Integer.parseInt(planeFlds.getKFld().getText());
						int l = Integer.parseInt(planeFlds.getLFld().getText());
						
						M1fix.set(h, k, l);
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
						
						M1fix.setAsHexagonalPlane(planeMiller, a0, c0);
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
						
						M1fix.setAsTetragonalPlane(planeMiller, a0, c0);
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
						
						M1fix.setAsOrthorombicPlane(planeMiller, a0, b0, c0);
						break;
						
						default: break;
						
					}
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SecondGrainFrame.this,
							"Miller indices should be positive numbers.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}
				
				System.out.println(M1fix);
				
				// read tolerance				
				try {
					planeTol = Math.toRadians(Double.parseDouble(planeTolFld.getText().replace(",",".")));
					if(planeTol < 0d) throw new NumberFormatException();																		
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SecondGrainFrame.this,
							"Tolerance must be a positive decimal number.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}
					
				// read no. of bins	
				try {
					nBins = Integer.parseInt(nBinsFld.getText());
					if(nBins < 1) throw new NumberFormatException();																		
				} catch(NumberFormatException exc) {
					JOptionPane.showMessageDialog(Stats_SecondGrainFrame.this,
							"Number of sampling points must be a positive integer.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;					
				}
			
				try {
					
					task = new SecondGrainTask();
					task.addPropertyChangeListener(new PropertyChangeListener() {
						@Override
						public void propertyChange(PropertyChangeEvent evt) {
							if ("progress".equals(evt.getPropertyName())) {
								progressBar.setValue((Integer)evt.getNewValue());
				             	}						
						}
					});
					task.execute();
						
				} catch(Exception e) {
					abortBtn.setEnabled(false);
					fireBtn.setEnabled(true);
					JOptionPane.showMessageDialog(Stats_SecondGrainFrame.this,
							"An error occurred.",
							"Error",
							JOptionPane.ERROR_MESSAGE);
					return;	
				}
																
			}
		});
		
	
		
	
		contentPane.add(progressBar, "cell 0 11 2 1,gapx 20");
		
		abortBtn = new JButton();
		abortBtn.setEnabled(false);
		abortBtn.setMaximumSize(new Dimension(24,24));
		abortBtn.setMinimumSize(new Dimension(24,24));
		abortBtn.setPreferredSize(new Dimension(24,24));
		abortBtn.setIcon(new ImageIcon(Stats_SecondGrainFrame.class.getResource("/gui_bricks/abort.png")));
		contentPane.add(abortBtn, "cell 0 11 2 1,gapx 20");
		
		planeTolFld = new JTextField();
		planeTolFld.setText("7");
		planeTolFld.setHorizontalAlignment(SwingConstants.RIGHT);
		contentPane.add(planeTolFld, "cell 0 7,gapy 5");
		planeTolFld.setColumns(3);
		
		contentPane.add(planeCards, "cell 0 6,gapy 5");
		
		nBinsFld = new JTextField();
		nBinsFld.setText("4000");
		nBinsFld.setHorizontalAlignment(SwingConstants.RIGHT);
		nBinsFld.setColumns(5);
		contentPane.add(nBinsFld, "cell 0 8,gapy 5");
		

		abortBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent evt) {				
				task.cancel(true);
			}			
		});
		
		
		pack();
		setResizable(false);
	}
	
	
	private class SecondGrainTask extends SwingWorker<Void, Void> {

		@Override
		public Void doInBackground() throws Exception {
			
			long startTime = System.currentTimeMillis();
				
			abortBtn.setEnabled(true);
			fireBtn.setEnabled(false);	
			progressBar.setValue(0);
			
			double totalArea = 0d;
			
			long nproc = 0;
			
			final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
		    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			out.println("# it contains a distribution of boundary planes in the 2nd grain");
			out.println("# calculated using the metric-based approach for the fixed plane in the 1st grain.");		
			final boolean expData = gbFiles.get(0).isExperimental();
			if(expData) {
				out.println("MEASURED ");
			} else {
				out.println("RANDOM ");
			}		
			out.println("ST_PROJ_X ST_PROJ_Y LAB_ZENITH LAB_AZIMUTH MRD_2ND_PLANE");
			
		    Matrix3x3[] setC = Transformations.getSymmetryTransformations(pointGrp);


			// initial points Golden Ratio Spiral						
			final ArrayList<UnitVector> allPts = new ArrayList<UnitVector>();
			int nPts = 2 * nBins;
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
			    }    
			}
			
	 
			final DecimalFormat df4;		
			final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
			df4 = new DecimalFormat("0.####", otherSymbols);
			
			
			final ArrayList<UnitVector> asymPts = new ArrayList<UnitVector>();    	    
			for(UnitVector p : allPts) {
		    	
		    	switch(pointGrp) {
		    	case M3M:
		    		if( p.z() >= 0d && p.y() >= p.z() && p.x() >= p.y()) asymPts.add(p);	    
		    		break;
		    		
		    	case _6MMM:
		    		
		    		if( p.z() >= 0d && p.y() >= 0d && p.x() >= SQRT3 * p.y()) asymPts.add(p);
		    		break;
		    		
		    	case _4MMM:
		    		if( p.z() >= 0d && p.y() >= 0d && p.x() >= p.y()) asymPts.add(p);
		    		break;
		    		
		    	case MMM:
		    		if( p.z() >= 0d && p.y() >= 0d && p.x() >= 0d) asymPts.add(p);
		    		break;
		    		
		    		default: break;
		    	}   	
		    }
		    
		    switch(pointGrp) {
	    	case M3M:
	    		UnitVector border = new UnitVector();
	    		border.set(0, 0, 1);
	    		asymPts.add(border);
	    		
	    		border = new UnitVector();
	    		border.set(1, 0, 1);
	    		asymPts.add(border);
	    		
	    		border = new UnitVector();
	    		border.set(1, 1, 1);
	    		asymPts.add(border);
	    		
	    		break;
	    		
	    	case _6MMM:    		
	    		break;
	    		
	    	case _4MMM:
	    		break;
	    		
	    	case MMM:
	    		break;
	    		
	    		default: break;
	    	}   	
		    
		    
		    System.out.println("all: " + allPts.size() + "   asym: " + asymPts.size());  
			
		    double[] distVals = new double[asymPts.size()];
		    for(int i = 0; i < distVals.length; i++) distVals[i] = 0d;
			    
			
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
					
					if(nproc % 500 == 0) System.out.println(nproc);
						
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
													
						final UnitVector __m1 = new UnitVector();
						__m1.set(zenith, azimuth);		
						__m1.transform(ML);
						
						final InterfaceMatrix B = new InterfaceMatrix(M, __m1);
						
						final UnitVector __m2 = new UnitVector(B.m2());
							
							
						for(boolean transpose : new boolean[]{false, true}) 
							for(boolean minus : new boolean[]{false, true}) {
								
							for(Matrix3x3 C1 : setC) {
																
								for(Matrix3x3 C2 : setC) {
										
									final UnitVector m1 = new UnitVector();
									final UnitVector m2 = new UnitVector();
									if(transpose) {
										m1.set(__m1);
										m2.set(__m2);
									} else {
										m1.set(__m2);
										m2.set(__m1);										
									}
									if(minus) {
										m1.negate();
										m2.negate();
									}
									m1.transform(C1);
									m2.transform(C2);
								
									
									final double gamma1 = MyMath.acos(M1fix.dot(m1));
									
									if(gamma1 < planeTol) {
										
										// loop over sampling points
										
										for(int i = 0; i < asymPts.size(); i++) {
											
											final double gamma2 = MyMath.acos(m2.dot(asymPts.get(i)));
											
											if(gamma2 < planeTol) {
												distVals[i] += A;
											}
											
										}
										
									}
									
									
								
								}
							}
						}
						
					}
					setProgress((int)Math.round((double)nproc/(double)nGBs*99d));

				}
			
			}
			
			
			int ns = setC.length;
			double ballVolume = ns * (1d - FastMath.cos(planeTol));    
			ballVolume = ballVolume*ballVolume;
		    
			for(int i = 0; i < asymPts.size(); i++) {
				
			
				if(isCancelled()) break;	
								   
				for(Matrix3x3 C : setC) {
					   
					final UnitVector resPt = new UnitVector();
					resPt.set(asymPts.get(i));
					 	   
					resPt.transform(C);
					boolean plusAndMinus = false;
					   
					if(resPt.z() < 0d) {
						
						if(resPt.z() > -1.501d * planeTol) plusAndMinus = true;
						resPt.negate();
					}
			
			 		 
					double rStereo = FastMath.tan(0.5d * resPt.zenith());
					double xStereo = rStereo * FastMath.cos(resPt.azimuth());
					double yStereo = rStereo * FastMath.sin(resPt.azimuth());
					   
				   
					out.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
							df4.format(Math.toDegrees(resPt.zenith())) + ' ' + df4.format(Math.toDegrees(resPt.azimuth())) + ' ' +
							df4.format(distVals[i] / totalArea / ballVolume));
	   	    		 
					   
					   
					if(plusAndMinus) {
							  
						resPt.negate();
						rStereo = FastMath.tan(0.5d * resPt.zenith());
						xStereo = rStereo * FastMath.cos(resPt.azimuth());
						yStereo = rStereo * FastMath.sin(resPt.azimuth());
							   
						   
						out.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
								df4.format(Math.toDegrees(resPt.zenith())) + ' ' + df4.format(Math.toDegrees(resPt.azimuth())) + ' ' +
								df4.format(distVals[i] / totalArea / ballVolume));
								   
					}
				   
				}  
			}
		   
		    
			abortBtn.setEnabled(false);
			fireBtn.setEnabled(true);
			setProgress(100);
			out.close();

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
	
}
