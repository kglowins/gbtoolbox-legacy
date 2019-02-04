package algorithms;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import utils.FileUtils;
import utils.GBDatHeader;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MyMath;
import utils.Transformations;
import utils.UnitVector;
import enums.PointGroup;

public final class GBPD_DistFunWorker extends SwingWorker<Void,Void> {
		
	
	private static final double SQRT3 = Math.sqrt(3d);
	private ArrayList<GBDatHeader> gbFiles;
	private boolean isExp;
					
	private double planeTol;
	private int nBins;
	private boolean elimin;
	private double areaThr;

	private JTextField outFld;
	
	
	private JButton fireBtn;
	private JButton abortBtn;
	
	private JProgressBar progressBar;
	


	
	public GBPD_DistFunWorker(ArrayList<GBDatHeader> gbFiles,
			double planeTol, int nBins, boolean elimin, double areaThr, 
			JTextField outFld,
			JButton fireBtn, JButton abortBtn, JProgressBar progressBar) {
		
		
		this.gbFiles = gbFiles;
		this.isExp = gbFiles.get(0).isExperimental();
				
		
		this.planeTol = planeTol;
		this.nBins = nBins;
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
		
		final ArrayList<UnitVector> M1list = new ArrayList<UnitVector>();
		final ArrayList<UnitVector> M2list = new ArrayList<UnitVector>();
		final ArrayList<Double> Area = new ArrayList<Double>();
		final ArrayList<Double> Corr = new ArrayList<Double>();

				
		//READ DATA			

	    double totalArea = 0d;
	    double totalNmeas = 0d;
	    
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
					
		
								
				final String[] num = line.trim().split("\\s+");
							
				double A = 1d;					
				if(expData) A = Double.parseDouble(num[9]);
				
												
				if(!elimin || (elimin && A <= areaThr) ) {
					
										
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
					
					final InterfaceMatrix readB = new InterfaceMatrix(M, m1);
					
					M1list.add(readB.m1());
					M2list.add(readB.m2());
					
					Area.add(A);
					
					if(isExp) {
						final double correl = Double.parseDouble(num[8]);
						Corr.add(correl);
						totalNmeas += correl;
					} else {
						Corr.add(1d);
						totalNmeas += 1d;
					}
					
				}	
			}
		}
	    

		
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
		
	//	final DecimalFormat df8;		
		
	//	df8 = new DecimalFormat("0.########", otherSymbols);
	    
	    // calculate the distribution
		final ArrayList<UnitVector> asymPts = new ArrayList<UnitVector>();    	    
	    for(UnitVector p : allPts) {
	    	
	    	switch(ptGrp) {
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
	    
	    switch(ptGrp) {
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

	    final Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
		final boolean[] TF = new boolean[]{false, true};
		  	    			
		
    	for(int i = 0; i < M1list.size(); i++) {
    		
    		if(isCancelled()) break;
    		
        	for(Matrix3x3 C1 : setC) {
        		for(boolean minus : TF) {
        					
        			final UnitVector m1 = new UnitVector();
        			m1.set(M1list.get(i));
        			m1.transform(C1);
        					        					
        			if(minus) m1.negate();
        				
        					
        			for(int j = 0; j < asymPts.size(); j++) {
	    				
        				final double gamma = MyMath.acos(asymPts.get(j).dot(m1));
	       											
        				if(gamma <= planeTol) distVals[j] += Area.get(i);
	       	    	}
    			}
    		}
        	
        	
        	for(Matrix3x3 C2 : setC) {
        		for(boolean minus : TF) {
        					
        			final UnitVector m2 = new UnitVector();
        			m2.set(M2list.get(i));
        			m2.transform(C2);
        					        					
        			if(minus) m2.negate();
        				
        					
        			for(int j = 0; j < asymPts.size(); j++) {
	    				
        				final double gamma = MyMath.acos(asymPts.get(j).dot(m2));
	       											
        				if(gamma <= planeTol) distVals[j] += Area.get(i);
	       	    	}
    			}
    		}
        	
        	
        	setProgress((int)((double)i / (double) M1list.size() *99d));
    	}
	    
  	
    	if(!isCancelled()) {
    		
			final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
					
	//		String ext = FileUtils.getExtension(new File(outFld.getText()));		
		//	PrintWriter outErr = null;	
				
		/*	if(ext != null) {
				outErr = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText().substring(0, outFld.getText().length() - ext.length() - 1) + "_err." + ext)));
			} else {
				outErr = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText() + "_err")));
			}*/
				
		    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			out.println("# it contains a distribution of grain boundary planes averaged over misorientations");
			out.println("# calculated using the metric-based approach.");		
			if(isExp) {
				out.print("EXP ");
			} else {
				out.print("RANDOM ");
			}		
			out.println(df4.format(totalNmeas));		
			out.print("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH ");
			out.println("MRD_PLANE");
		
		
		    /*outErr.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + ".");
			outErr.println("# It contains errors a distribution of grain boundary planes for a fixed misorientation");
			outErr.println("# calculated using the approach based on a distance function.");		
			if(isExp) {
				outErr.print("MEASURED ");
			} else {
				outErr.print("RANDOM ");
			}		
			outErr.println(df4.format(totalNmeas));		
			outErr.println("ST_PROJ_X ST_PROJ_Y LAB_ZENITH LAB_AZIMUTH MRD_MIS_ERR");*/
				
		    
		    int ns = setC.length;
		    double ballVolume = ns * 2d * (1d - FastMath.cos(planeTol));
		        	   
		    
		   for(int i = 0; i < asymPts.size(); i++) {
			   
			   		   
			   
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
					   
				/*  outErr.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
					   			df4.format(Math.toDegrees(resPt.zenith())) + ' ' + df4.format(Math.toDegrees(resPt.azimuth())) + ' ' +
					   			df8.format(Math.sqrt( distVals[i] / totalArea / totalNmeas)));*/
				  
				  
				  if(plusAndMinus) {
					  
					  resPt.negate();
					   rStereo = FastMath.tan(0.5d * resPt.zenith());
					   xStereo = rStereo * FastMath.cos(resPt.azimuth());
					   yStereo = rStereo * FastMath.sin(resPt.azimuth());
					   
				   
					   out.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
						  			df4.format(Math.toDegrees(resPt.zenith())) + ' ' + df4.format(Math.toDegrees(resPt.azimuth())) + ' ' +
						   			df4.format(distVals[i] / totalArea / ballVolume));
						   
					 /* outErr.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
						   			df4.format(Math.toDegrees(resPt.zenith())) + ' ' + df4.format(Math.toDegrees(resPt.azimuth())) + ' ' +
						   			df8.format(Math.sqrt( distVals[i] / totalNmeas / totalArea)));*/
				  }
					   
	
				  
			   }
	
			}
	   
  
	   		setProgress(100);
	   		out.close();
	   		//outErr.close();
		}

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
