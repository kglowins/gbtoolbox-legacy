package algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

import org.apache.commons.math3.util.FastMath;

import utils.ConstantsAndStatic;
import utils.EulerAngles;
import utils.GBDatHeader;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.Transformations;
import utils.UnitVector;
import enums.PointGroup;

public final class GBCD_BinsWorker extends SwingWorker<Void,Void> {
		
	private Matrix3x3 Mfix;
	private ArrayList<GBDatHeader> gbFiles;
	private boolean isExp;
	
	private int D1;
	private int D2;
	private boolean elimin;
	private double areaThr;
	private JTextField outFld;
	
	private JButton fireBtn;
	private JButton abortBtn;
	
	private JProgressBar progressBar;
	
		
	private static double INFTY = Double.MAX_VALUE;
	private static double HALFPI = 0.5d * Math.PI;
	private static double TWOPI = 2d * Math.PI;
	private static double ONEDEG = Math.PI / 180d;
	private static double ONEPI = Math.PI;
	private static double EPS = 1e-6d;
	
	
	
	
	public GBCD_BinsWorker(Matrix3x3 Mfix, ArrayList<GBDatHeader> gbFiles,
			int D1, int D2, boolean elimin, double areaThr, JTextField outFld,
			JButton fireBtn, JButton abortBtn, JProgressBar progressBar
			) {
		
		this.Mfix = Mfix;	
		this.gbFiles = gbFiles;
		this.isExp = gbFiles.get(0).isExperimental();
		
		this.D1 = D1;
		this.D2 = D2;
		
		this.elimin = elimin;
		this.areaThr = areaThr;
		this.outFld = outFld;
		
		this.abortBtn = abortBtn;
		this.fireBtn = fireBtn;
		
		this.progressBar = progressBar;
	}	
							
			
	@Override
	public Void doInBackground() throws IOException {
			
		setProgress(0);
		progressBar.setValue(0);
		abortBtn.setEnabled(true);
		fireBtn.setEnabled(false);
		long startTime = System.currentTimeMillis();
		
		final PointGroup ptGrp = gbFiles.get(0).getPointGrp();		
		final Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);			
		
		final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));

		
		long NtoCompute = 31 * 121;
		int nGBs = 0;
		long index = 0;
		
		for(GBDatHeader head : gbFiles) nGBs += head.getNumberOfGBs();
		
		NtoCompute += nGBs;
		NtoCompute = NtoCompute * setC.length * setC.length * 2;
	
		final DecimalFormat df4;
		
		
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df4 = new DecimalFormat("0.####", otherSymbols);
		
		
	    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
		out.println("# it contains a distribution of grain boundary planes for the fixed misorientation");
		out.println("# calculated using the method based on partition of the boundary space into bins.");		
		if(isExp) {
			out.println("EXP");
		} else {
			out.println("RANDOM");
		}		
		out.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH MRD_FIXMISOR");
			
		final double[][][][][] gbcd = new double[D1][D1][D1][D2][4*D2];
		
		for(int i = 0; i < D1; i++)
			for(int j = 0; j < D1; j++)
				for(int k = 0; k < D1; k++)
					for(int l = 0; l < D2; l++)
						for(int m = 0; m < 4*D2; m++) gbcd[i][j][k][l][m] = 0d;
		
		
		Iterator<GBDatHeader> iterator = gbFiles.iterator();
			
		
		while(iterator.hasNext() && !isCancelled()) {

			GBDatHeader header = iterator.next();		
			

			
			final BufferedReader in = new BufferedReader(new FileReader(header.getPath()));
				
			GBDatHeader.skipHeaderLines(in);
			
			String line = null;		
			
			while ((line = in.readLine()) != null && !isCancelled()) {
					
				final String[] num = line.trim().split("\\s+");
					
				double A = INFTY;

					
				if(isExp) A = Double.parseDouble(num[9]);
					
				if(!elimin || (elimin && A <= areaThr) ) {
					
										
					final double phi1L = Math.toRadians( Double.parseDouble(num[0]));
					final double PhiL = Math.toRadians( Double.parseDouble(num[1]));
					final double phi2L = Math.toRadians( Double.parseDouble(num[2]));
					
					final double phi1R = Math.toRadians( Double.parseDouble(num[3]));
					final double PhiR = Math.toRadians( Double.parseDouble(num[4]));
					final double phi2R = Math.toRadians( Double.parseDouble(num[5]));
							
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
					
					final double zenith = Math.toRadians( Double.parseDouble(num[6]));
					final double azimuth = Math.toRadians( Double.parseDouble(num[7]));
					
					final UnitVector m1 = new UnitVector();
					
					m1.set(zenith, azimuth);
					m1.transform(ML);
					
								
					final InterfaceMatrix B = new InterfaceMatrix(M, m1);
					
					for(boolean transpose : new boolean[]{false, true}) {
						for(Matrix3x3 C1 : setC) {
							for(Matrix3x3 C2 : setC) {
					
								final InterfaceMatrix Bprim = new InterfaceMatrix(B);
								
								if(transpose) Bprim.transpose();
								
								Bprim.applySymmetry1(C1);
								Bprim.applySymmetry2(C2);
																
							
								final EulerAngles eul = new EulerAngles();
								eul.set(Bprim.M());
																	
								if(eul.phi1() < HALFPI+EPS && eul.phi2() < HALFPI+EPS && eul.Phi() < HALFPI+EPS ) {
																		
									int i1 = (int) (D1 * 2d * eul.phi1() / ONEPI);
									int i2 = (int) (D1 * FastMath.cos(eul.Phi()));
									int i3 = (int) (D1 * 2d * eul.phi2() / ONEPI);
									
									if(Bprim.m1().z() < 0d) Bprim.toMinus();
									
									int i4 = (int) (D2 * FastMath.cos(Bprim.m1().zenith()));									
									int i5 = (int) (4d * D2 * Bprim.m1().azimuth() / TWOPI);
									
									if (i1 == D1) i1 = D1 - 1;
									if (i2 == D1) i2 = D1 - 1;
									if (i3 == D1) i3 = D1 - 1;
									if (i4 == D2) i4 = D2 - 1;								
									if (i5 == 4*D2) i5 = 4*D2 - 1;
																		
									if(isExp) gbcd[i1][i2][i3][i4][i5] += A; else gbcd[i1][i2][i3][i4][i5] += 1d;
									
									
								
								}	
								index++;								
								setProgress((int)Math.round((double)index/(double)NtoCompute*100d));
							}
						}
					} // for transpose
				}
				
			}
			in.close();
		}	
					
		if(!isCancelled()) {
		    		
	    double avg = 0d;
		for(int i = 0; i < D1; i++)
			for(int j = 0; j < D1; j++)
				for(int k = 0; k < D1; k++)
					for(int l = 0; l < D2; l++)
						for(int m = 0; m < 4*D2; m++) avg += gbcd[i][j][k][l][m];
		
		avg /= (4*D1*D1*D1*D2*D2);
			
		
		for(int i = 0; i < D1; i++)
			for(int j = 0; j < D1; j++)
				for(int k = 0; k < D1; k++)
					for(int l = 0; l < D2; l++)
						for(int m = 0; m < 4*D2; m++) {
							gbcd[i][j][k][l][m] /= avg;
						}
		}
		
		
		
		for(int i = 0; i <= 30; i++) {
			for(int j = 0; j <= 120; j++) if(!isCancelled()) {
											
			    final double zenith = 3d * ONEDEG * i;
			    final double azimuth = 3d * ONEDEG * j;
			    
			    final UnitVector m1 = new UnitVector();
			    m1.set(zenith, azimuth);
			    
			    
			    double sum = 0d;
			    int ct = 0;
			    
			    final InterfaceMatrix B = new InterfaceMatrix(Mfix, m1);
			    
				for(boolean transpose : new boolean[]{false, true}) {
					for(Matrix3x3 C1 : setC) {
						for(Matrix3x3 C2 : setC) {
				
							final InterfaceMatrix Bprim = new InterfaceMatrix(B);
							if(transpose) Bprim.transpose();
							
							Bprim.applySymmetry1(C1);
							Bprim.applySymmetry2(C2);
							
							
							
							final EulerAngles eul = new EulerAngles();
						
							eul.set(Bprim.M());
						
							if(eul.phi1() < HALFPI+EPS && eul.phi2() < HALFPI+EPS && eul.Phi() < HALFPI+EPS ) {
								
								int i1 = (int) (D1 * 2d * eul.phi1() / ONEPI);
								int i2 = (int) (D1 * FastMath.cos(eul.Phi()));
								int i3 = (int) (D1 * 2d * eul.phi2() / ONEPI);
								
								if(Bprim.m1().z() < 0d) Bprim.toMinus();
								
								int i4 = (int) (D2 * FastMath.cos(Bprim.m1().zenith()));									
								int i5 = (int) (4d * D2 * Bprim.m1().azimuth() / TWOPI);
								
								if (i1 == D1) i1 = D1 - 1;
								if (i2 == D1) i2 = D1 - 1;
								if (i3 == D1) i3 = D1 - 1;
								if (i4 == D2) i4 = D2 - 1;								
								if (i5 == 4*D2) i5 = 4*D2 - 1;
								
								
								sum += gbcd[i1][i2][i3][i4][i5];
								ct++;															
							}	
							
							index++;
							setProgress((int)Math.round((double)index/(double)NtoCompute*100d));		
						}
					}
				} // for transpose
			    
			    
				final double r = FastMath.tan(0.5d * zenith);
				final double xp = r * FastMath.cos(azimuth);
				final double yp = r * FastMath.sin(azimuth);
					 
				double amrd = sum / ct;
				
			    out.println(df4.format(xp) + ' ' + df4.format(yp) + ' '
			    		+ df4.format(Math.toDegrees(zenith)) + ' ' + df4.format(Math.toDegrees(azimuth)) + ' '
			    		+ df4.format(amrd));
			}
		}
		        
		if(!isCancelled()) setProgress(100);
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
