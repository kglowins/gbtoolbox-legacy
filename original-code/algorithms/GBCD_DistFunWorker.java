package algorithms;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import javax.swing.SwingWorker;



import org.apache.commons.math3.util.FastMath;

import parallel.TaskGBCDDistFun;
import parallel.TaskGBCDPreselect;
import parallel.TaskResultGBCD_GBPD;
import parallel.TaskResultGBCDPreselect;


import utils.ConstantsAndStatic;
import utils.FileUtils;
import utils.GBDatHeader;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.UnitVector;
import enums.PointGroup;

public final class GBCD_DistFunWorker extends SwingWorker<Void,Void> {
		
	private Matrix3x3 Mfix;
	private ArrayList<GBDatHeader> gbFiles;
	private boolean isExp;
		
		
	private double misTol;
	private double planeTol;
	private int nBins;
	private boolean elimin;
	private double areaThr;
	private JTextField outFld;
	
	
	private JButton fireBtn;
	private JButton abortBtn;
	
	private JProgressBar progressBar;
	
	private List futuresList;
	
	private boolean normalize;

	
	public GBCD_DistFunWorker(Matrix3x3 Mfix, ArrayList<GBDatHeader> gbFiles,
			double misTol, double planeTol, int nBins, boolean elimin, double areaThr, 
			JTextField outFld,
			JButton fireBtn, JButton abortBtn, JProgressBar progressBar, boolean normalize) {
		
		this.Mfix = Mfix;
		this.gbFiles = gbFiles;
		this.isExp = gbFiles.get(0).isExperimental();
		
		this.misTol = misTol;
		this.planeTol = planeTol;
		this.nBins = nBins;
		this.elimin = elimin;
		this.areaThr = areaThr;
	//	this.repulse = repulse;
		this.outFld = outFld;
				
		this.abortBtn = abortBtn;
		this.fireBtn = fireBtn;
		this.normalize = normalize;
		
		this.progressBar = progressBar;
	}
	
		
	
	@Override
	public Void doInBackground() throws IOException {
		
		
		setProgress(0);
		progressBar.setValue(0);
		abortBtn.setEnabled(true);
		fireBtn.setEnabled(false);
		long startTime = System.currentTimeMillis();
		
		
		final PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText())));		
		PrintWriter outError = null;
		String ext = FileUtils.getExtension(new File(outFld.getText()));	
		
		
		if(normalize) {
				if(ext != null) {
					outError = new PrintWriter(new BufferedWriter(new FileWriter(
							outFld.getText().substring(0, outFld.getText().length() - ext.length() - 1) + "_err." + ext)));
				} else {
					outError = new PrintWriter(new BufferedWriter(new FileWriter(outFld.getText() + "_err")));
				}								
		}
				
		final PointGroup ptGrp = gbFiles.get(0).getPointGrp();
		
		final ArrayList<InterfaceMatrix> Blist = new ArrayList<InterfaceMatrix>();
		
		final ArrayList< ArrayList<Integer> > C1list = new ArrayList< ArrayList<Integer> >();
		final ArrayList< ArrayList<Integer> > C2list = new ArrayList< ArrayList<Integer> >();
		final ArrayList< ArrayList<Boolean> > Tlist = new ArrayList< ArrayList<Boolean> >();
				
		final ArrayList<Double> Area = new ArrayList<Double>();	

		
		
		//READ DATA			
		int nThreads = Runtime.getRuntime().availableProcessors();
		System.out.println("nThreads = " + nThreads);
		final ExecutorService eservice = Executors.newFixedThreadPool(nThreads);
	    final CompletionService < Object > cservice = new ExecutorCompletionService < Object > (eservice);
	    
	    futuresList = new ArrayList();
	    
		long nGBs = 0;
		long accRep = 0;
		
		double totalArea = 0d;
		double totalAccGBArea = 0d;
		double totalAccRepArea = 0d;
				
		double totalNmeas = 0d;
		
		System.out.println("Tolerances: " + Math.toDegrees(misTol) + ", " + Math.toDegrees(planeTol));
		
		Iterator<GBDatHeader> iterator = gbFiles.iterator();
		while(iterator.hasNext() && !isCancelled()) {

			final GBDatHeader header = iterator.next();
			
			nGBs += header.getNumberOfGBs();
			
			futuresList.add(cservice.submit(
					new TaskGBCDPreselect(this, Mfix, header.getPath(), misTol, isExp, elimin, areaThr, ptGrp)
	    			 ) );		
		}
		
		
		 for(int i = 0; i < gbFiles.size(); i++) {
			   
			   try {
				   
				   final TaskResultGBCDPreselect taskResult = (TaskResultGBCDPreselect) cservice.take().get();
				   
				   
				   Blist.addAll(taskResult.blist);
				   
				   C1list.addAll(taskResult.C1list);
				   C2list.addAll(taskResult.C2list);
				   Tlist.addAll(taskResult.Tlist);
				   
				   Area.addAll(taskResult.area);
				   
				   accRep += taskResult.acceptedRep;
				   
				   totalArea += taskResult.totalArea;
				   
				   totalAccGBArea += taskResult.acceptedGBArea;
				   totalAccRepArea += taskResult.acceptedRepArea;
				   
				   totalNmeas += taskResult.nMeas;				   				   
				   				   
				   setProgress((int)Math.round((double)i/(double)gbFiles.size()*50d));
					
			   } catch (InterruptedException exc) { 
					
				   out.close();
				   if(normalize) outError.close();
				   exc.printStackTrace();
				   return null;
					
			   } catch (ExecutionException exc) { 
					
					
				   out.close();
				   if(normalize) outError.close();
				   exc.printStackTrace();
				   return null;
			   }
		}
		 
		setProgress(50);
		
		System.out.println("Total number of boundaires: " + nGBs);
		System.out.println("Accepted boundaries: "+ Blist.size());
		System.out.println("Number of accepted representations: "+ accRep);		
		System.out.println("Total number of measurements: " + totalNmeas);			
		System.out.println("Total area: " + totalArea);			
		System.out.println("Area of accepted boundaries: "+ totalAccGBArea);
		System.out.println("Area of accepted representations: "+ totalAccRepArea);
		
				
		
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
		final DecimalFormat df10;
		final DecimalFormat df2;

		
		final DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		df4 = new DecimalFormat("0.####", otherSymbols);
		df2 = new DecimalFormat("0.####", otherSymbols);
		df10 = new DecimalFormat("0.##########", otherSymbols);
	    
	    
	    // calculate the distribution
		    
	    	
	    futuresList = new ArrayList();
	    
		final ArrayList<UnitVector> upperPts = new ArrayList<UnitVector>();    
	    
	    for(UnitVector p : allPts) if(p.z() >= -1.501d * planeTol) upperPts.add(p);
    	        
	    for(int i = 0; i < upperPts.size(); i++) {
	    	    	    	
	    	final InterfaceMatrix Bfix = new InterfaceMatrix(Mfix, upperPts.get(i));
	    	
	    	final double rStereo = FastMath.tan(0.5d * upperPts.get(i).zenith());
			final double xStereo = rStereo * FastMath.cos(upperPts.get(i).azimuth());
			final double yStereo = rStereo * FastMath.sin(upperPts.get(i).azimuth());
	    	
	    	futuresList.add(cservice.submit(new TaskGBCDDistFun(xStereo, yStereo,
	    			upperPts.get(i).zenith(), upperPts.get(i).azimuth(), Bfix,
	    					Blist, Area, C1list, C2list, Tlist, planeTol, ptGrp)
	    			 ) );	
	   }
	    	
	    
	    
	    out.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
		out.println("# it contains a distribution of grain boundary planes for the fixed misorientation");
		out.println("# calculated using the metric-based approach.");		
		if(isExp) {
			out.print("EXP ");
		} else {
			out.print("RANDOM ");
		}		
		out.println(df4.format(totalNmeas));	
		
		if(!normalize) {
			out.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH FRAC_FIXMISOR");
		} else {
			out.println("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH MRD_FIXMISOR");
		}

		
		if(normalize) {
			
		    outError.println("# This file was created by " + ConstantsAndStatic.GBTOOLBOX + " ver. " + ConstantsAndStatic.VERSION + ";");
			outError.println("# it contains errors of grain boundary distribution for the fixed misorientation");
			outError.println("# calculated using the metric-based approach.");		
			if(isExp) {
				outError.print("EXP ");
			} else {
				outError.print("RANDOM ");
			}		
			outError.println(df4.format(totalNmeas));		
			outError.print("ST_PROJ_X ST_PROJ_Y ZENITH AZIMUTH ");												
			outError.println("MRD_FIXMISOR_ERR");			
		}
	
									

	
	    double ballVolume = 0d;
	    
	    if(normalize) {
	    	
	    	if(Math.abs(Math.toDegrees(planeTol) - 7d) < 1e-5 && Math.abs(Math.toDegrees(misTol) - 3d) < 1e-5) {	    		
	    		ballVolume = 0.0000641d;
	    
	    	} else if(Math.abs(Math.toDegrees(planeTol) - 5d) < 1e-5 && Math.abs(Math.toDegrees(misTol) - 5d) < 1e-5) {
	    		ballVolume = 0.000139d;
	    		
	    	} else if(Math.abs(Math.toDegrees(planeTol) - 8d) < 1e-5 && Math.abs(Math.toDegrees(misTol) - 5d) < 1e-5) {
	    		ballVolume = 0.00038019d;
	    	}
	    }
	    
	     
	   
	    
	   for(int i = 0; i < upperPts.size(); i++) {
		   
		 
		   try {
			   
			   final TaskResultGBCD_GBPD taskResult = (TaskResultGBCD_GBPD) cservice.take().get();
			   
			   if(!normalize) {
			   	out.println(df4.format(taskResult.xProj) + ' ' + df4.format(taskResult.yProj) + ' ' +
				   			df4.format(Math.toDegrees(taskResult.zenith)) + ' ' + df4.format(Math.toDegrees(taskResult.azimuth)) + ' ' +
				   			df10.format(taskResult.area / totalArea) );
			   } else {
				   
				   out.println(df4.format(taskResult.xProj) + " " +
							df4.format(taskResult.yProj) + " " +
							df4.format(Math.toDegrees(taskResult.zenith)) + " " +
							df4.format(Math.toDegrees(taskResult.azimuth)) + " " +
							df2.format(taskResult.area / totalArea / ballVolume)																
							);
						
						
					outError.println(df4.format(taskResult.xProj) + " " +
							df4.format(taskResult.yProj) + " " +
							df4.format(Math.toDegrees(taskResult.zenith)) + " " +
							df4.format(Math.toDegrees(taskResult.azimuth)) + " " +
							df2.format(Math.sqrt(taskResult.area / totalArea / totalNmeas) / ballVolume)
							);
				   
			   }
		
				   
			   setProgress(50 + (int)Math.round((double)i/(double)upperPts.size()*50d));
				
			} catch (InterruptedException exc) { 
				
				exc.printStackTrace();
				out.close();	
				if(normalize) outError.close();
				return null;
				
			} catch (ExecutionException exc) { 
				
				exc.printStackTrace();
				out.close();
				if(normalize) outError.close();
				return null;
				
			} catch(Exception exc) {
				exc.printStackTrace();
				out.close();
				if(normalize) outError.close();
				return null;
			}
		}
	   

	    	   
	     
		if(!isCancelled()) setProgress(100);
		out.close();
		if(normalize) outError.close();

		long estimatedTime = System.currentTimeMillis() - startTime;
		System.out.println("Time elapsed: " + estimatedTime + " microsec.");
		return null;
		
	}
	
	
	@Override 
	public void done() {
		if(isCancelled()) if(futuresList != null) for(Object future : futuresList) ((Future) future).cancel(true);
		fireBtn.setEnabled(true);
		abortBtn.setEnabled(false);
	}

}

