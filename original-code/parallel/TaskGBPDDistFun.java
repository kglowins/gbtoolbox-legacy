package parallel;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import enums.PointGroup;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MyMath;
import utils.Transformations;
import utils.UnitVector;


public final class TaskGBPDDistFun implements Callable {
	
	private final TaskResultGBCD_GBPD res;
	
	private final UnitVector Nfix;
	private final ArrayList<InterfaceMatrix> Blist;
	private final ArrayList<Double> Area;
	
			
	private final double tolerance;
	
	private final PointGroup ptGrp;
						
	public TaskGBPDDistFun(double xProj, double yProj, double zenith, double azimuth,
			UnitVector Nfix,			
			ArrayList<InterfaceMatrix> Blist, 			
			ArrayList<Double> Area, 			
			double tolerance,
			PointGroup ptGrp)
	{ 		
		res = new TaskResultGBCD_GBPD();
		
		res.xProj = xProj;
		res.yProj = yProj;
		
		res.zenith = zenith;
		res.azimuth = azimuth;
		
		res.area = 0d;
		
		res.counts = 0;
		
		this.Nfix = Nfix;
		
		this.Blist = Blist;
		this.Area = Area;
						
		this.tolerance = tolerance;
		
		this.ptGrp = ptGrp;
	}
	
	
	@Override
	public Object call() throws Exception {
				 	
		final Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
		final boolean[] TF = new boolean[]{false, true};
		  	    	
	
		
    	for(int i = 0; i < Blist.size(); i++) {
    		
    		    		
    		for(boolean trans : TF) for(boolean minus : TF) {   		
    		
    			for(Matrix3x3 C1 : setC) {
    				for(Matrix3x3 C2 : setC) {
    				
	    				final InterfaceMatrix B = new InterfaceMatrix(Blist.get(i));        
	    				
	    				B.applySymmetry1(C1);
	    				B.applySymmetry2(C2);
	    				
	    				if(trans) B.transpose();
	    				
	    				if(minus) B.toMinus();  
	    				
	    				final double gamma = MyMath.acos(Nfix.dot(B.m1()));
	       				
							
	       	    		if(gamma <= tolerance) {
	       	    			res.area += Area.get(i);
	       	    			res.counts++;       	    
	       	    		}
    				}
    			}
    		}
    	}
    		    		    		  		    		   		
  

		return res;
	}	

}
