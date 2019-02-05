package com.github.kglowins.gbtoolbox.parallel;


import java.util.ArrayList;
import java.util.concurrent.Callable;

import com.github.kglowins.gbtoolbox.enums.PointGroup;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.Transformations;


public final class TaskGBCDDistFun implements Callable {
	
	private final TaskResultGBCD_GBPD res;
	
	private final InterfaceMatrix Bfix;
	private final ArrayList<InterfaceMatrix> Blist;
	private final ArrayList<Double> Area;
	
	private final ArrayList< ArrayList<Integer>> C1list;
	private final ArrayList< ArrayList<Integer>> C2list;
	private final ArrayList< ArrayList<Boolean>> Tlist;
			
	private final double toleranceSq;
	
	private final PointGroup ptGrp;
						
	public TaskGBCDDistFun(double xProj, double yProj, double zenith, double azimuth,
			InterfaceMatrix Bfix,
			
			ArrayList<InterfaceMatrix> Blist, 
			
			ArrayList<Double> Area, 
			ArrayList< ArrayList<Integer>> C1list,
			ArrayList< ArrayList<Integer>> C2list,
			ArrayList< ArrayList<Boolean>> Tlist,
			
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
		
		this.Bfix = Bfix;
		
		this.Blist = Blist;
		this.Area = Area;
		
		this.C1list = C1list;
		this.C2list = C2list;
		this.Tlist = Tlist;
				
		toleranceSq = tolerance*tolerance;
		
		this.ptGrp = ptGrp;
	}
	
	
	@Override
	public Object call() throws Exception {
				 	
		final Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
		final boolean[] TF = new boolean[]{false, true};
		  	    	
    	for(int i = 0; i < Blist.size(); i++) {
    		
    //		boolean found = false;
    		
    		for(boolean minus : TF) {   		
    		
    			for(int k = 0; k < C1list.get(i).size(); k++) {
    				
    				final InterfaceMatrix B2 = new InterfaceMatrix(Blist.get(i));        
    				
    				B2.applySymmetry1(setC[C1list.get(i).get(k)]);
    				B2.applySymmetry2(setC[C2list.get(i).get(k)]);
    				
    				if(Tlist.get(i).get(k)) B2.transpose();
    				
    				if(minus) B2.toMinus();  
    				
    				final double theta1 = MyMath.acos(Bfix.m1().dot(B2.m1()));
       				final double theta2 = MyMath.acos(Bfix.m2().dot(B2.m2()));
       				
       				final double distSq = 0.5d * (theta1*theta1 + theta2*theta2);
						
       	    		if(distSq <= toleranceSq) {
       	    			res.area += Area.get(i);
       	    			res.counts++;   
       	    	//	found = true;
       	    	//		break;
       	    		}
       	    		
    			} // C
    			
    		//	if(found) break;
    		} // +-
    	}
    		    		    		  		    		   		
  

		return res;
	}	

}

