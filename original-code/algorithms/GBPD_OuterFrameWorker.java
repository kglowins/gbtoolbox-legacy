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
import utils.FileUtils;
import utils.GBDatHeader;
import utils.MyMath;
import utils.UnitVector;

public final class GBPD_OuterFrameWorker extends SwingWorker<Void,Void> {
		
	
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
	
	private int axis;
	
	public GBPD_OuterFrameWorker(ArrayList<GBDatHeader> gbFiles,
			double planeTol, int nBins, boolean elimin, double areaThr, 
			JTextField outFld,
			JButton fireBtn, JButton abortBtn, JProgressBar progressBar, int axis) {
		
		
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
		
		this.axis = axis;
			
	}
	
		
	
	@Override
	public Void doInBackground() throws IOException {
		
		
		setProgress(0);
		progressBar.setValue(0);
		abortBtn.setEnabled(true);
		fireBtn.setEnabled(false);
		long startTime = System.currentTimeMillis();
		
		
		final ArrayList<UnitVector> Nlist = new ArrayList<UnitVector>();
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
				
							
					final double zenith = Math.toRadians( Double.parseDouble(num[6]));
					final double azimuth = Math.toRadians( Double.parseDouble(num[7]));
						
									
					final UnitVector nread = new UnitVector();
					nread.set(zenith, azimuth);
					
					final UnitVector n = new UnitVector();
					if(axis == 0) {
						n.set(nread.y(), nread.z(), nread.x());
					} else if(axis == 1) {
						n.set(nread.z(), nread.x(), nread.y());
					} else {
						n.set(nread);
					}
					
					if(n.z() < 0d) n.negate();
 
					Nlist.add(n);			

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
		
		//final DecimalFormat df8;				
		//df8 = new DecimalFormat("0.########", otherSymbols);
	    
	    // calculate the distribution
		final ArrayList<UnitVector> upperPts = new ArrayList<UnitVector>();    	    
	    for(UnitVector p : allPts) if(p.z() >= -1.501d * planeTol) upperPts.add(p);
    	        
	    
	    double[] distVals = new double[upperPts.size()];
	    for(int i = 0; i < distVals.length; i++) distVals[i] = 0d;

	 
		
    	for(int i = 0; i < Nlist.size(); i++) {
    		
    		if(isCancelled()) break;
    		
    		for(int j = 0; j < upperPts.size(); j++) {
	    	
    			
        		final double gamma = MyMath.acos(upperPts.get(j).dot(Nlist.get(i)));	
        		if(gamma <= planeTol) distVals[j] += Area.get(i);
    		}
    	
    		
    		if(Nlist.get(i).z() < 3.002d*planeTol) {
    			final UnitVector minusN = new UnitVector(Nlist.get(i));
    			minusN.negate();
    			
    			for(int j = 0; j < upperPts.size(); j++) {
    				
            		final double gamma = MyMath.acos(upperPts.get(j).dot(minusN));	
            		if(gamma <= planeTol) distVals[j] += Area.get(i);
        		}
    			
    		}
            	
        	
        	setProgress((int)((double)i / (double) Nlist.size() * 99d));
    	}
	    
  	
    	if(!isCancelled()) {
    		
    	
    		final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));
				
    		String ext = FileUtils.getExtension(new File(outFld.getText()));		
			//PrintWriter outErr = null;	
				
			/*if(ext != null) {
				outErr = new PrintWriter(new BufferedWriter(new FileWriter(
				outFld.getText().substring(0, outFld.getText().length() - ext.length() - 1) + "_err." + ext)));
			} else {
				outErr = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText() + "_err")));
			}*/
				
		    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			out.println("# it contains a distribution of boundary planes in the sample reference frame");
			out.println("# calculated using the metric-based approach.");		
			if(isExp) {
				out.println("MEASURED ");
			} else {
				out.println("RANDOM ");
			}		
			//out.println(df4.format(totalNmeas));		
			out.println("ST_PROJ_X ST_PROJ_Y LAB_ZENITH LAB_AZIMUTH MRD_PLANE_SAMPL");
			
		
		   /* outErr.println("# This file was generated by " + ConstantsAndStatic.GBTOOLBOX + ".");
			outErr.println("# It contains errors of boundar-plane distribution.");
			outErr.println("# calculated using the matric-based approach.");		
			if(isExp) {
				outErr.print("MEASURED ");
			} else {
				outErr.print("RANDOM ");
			}		
			outErr.println(df4.format(totalNmeas));		
			outErr.println("ST_PROJ_X ST_PROJ_Y LAB_ZENITH LAB_AZIMUTH MRD_PLANE_ERR");*/
				
		    	    
		    double ballVolume = (1d - FastMath.cos(planeTol));    
		    
		   	    
		    for(int i = 0; i < upperPts.size(); i++) {
			   
			 
		    	final double rStereo = FastMath.tan(0.5d * upperPts.get(i).zenith());
				final double xStereo = rStereo * FastMath.cos(upperPts.get(i).azimuth());
				final double yStereo = rStereo * FastMath.sin(upperPts.get(i).azimuth());
				   
			   
				   out.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
					  			df4.format(Math.toDegrees(upperPts.get(i).zenith())) + ' ' + df4.format(Math.toDegrees(upperPts.get(i).azimuth())) + ' ' +
					   			df4.format(distVals[i] / totalArea / ballVolume));
					   
				/*  outErr.println(df4.format(xStereo) + ' ' + df4.format(yStereo) + ' ' +
					   			df4.format(Math.toDegrees(upperPts.get(i).zenith())) + ' ' + df4.format(Math.toDegrees(upperPts.get(i).azimuth())) + ' ' +
					   			df8.format(Math.sqrt( distVals[i] / totalNmeas / totalArea)));*/
					   
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
