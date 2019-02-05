package com.github.kglowins.gbtoolbox.parallel;


import java.util.concurrent.Callable;


import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToImprop;
import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToSymNegAxis;
import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToSymPosAxis;
import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToTilt;
import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToTwistNegAxis;
import com.github.kglowins.gbtoolbox.distfun.CommonsDistanceToTwistPosAxis;
import com.github.kglowins.gbtoolbox.enums.PointGroup;



import com.github.kglowins.gbtoolbox.utils.AxisAngle;
import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.UnitVector;



public final class TaskTestRandomGB implements Callable {
	
	private static final int A_LOT = Integer.MAX_VALUE;
	private static final double EPS = 1e-4d;
	private static final double HALFPI = 0.5d * Math.PI;
	private static final double ONEPI = Math.PI;

	
			
	private final Matrix3x3[] setOfAllC;
	private final InterfaceMatrix Binit;
		
	private final boolean testTiltMin;
	private final boolean testTwistMin;
	private final boolean testSymMin;
	private final boolean testImpropMin;
	
	private final boolean testDecomp;
	
	private final boolean testTTC;
	private final boolean testSymTTC;
	
	
	private final boolean BT;
	private final boolean Bmin;
	
	private final TaskResultRandomGB res; 
	
	
						
	public TaskTestRandomGB(boolean BT, boolean Bmin, double phi1L, double PhiL, double phi2L,
							double phi1R, double PhiR, double phi2R,
							double zenith, double azimuth, 
									  boolean tiltMin, boolean twistMin, boolean symMin, boolean impropMin, 
									  boolean decomp, boolean ttc, boolean symTtc,
									  boolean disTtc, boolean decompDis, PointGroup ptGrp,
									  Matrix3x3[] setOfAllC) {
		
		res = new TaskResultRandomGB();
		
		res.phi1L = phi1L;
		res.PhiL = PhiL;
		res.phi2L = phi2L;
		
		res.phi1R = phi1R;
		res.PhiR = PhiR;
		res.phi2R = phi2R;
		
		res.zenith = zenith;
		res.azimuth = azimuth;
			
		this.setOfAllC = setOfAllC;
		
		final EulerAngles eulerL = new EulerAngles();
		eulerL.set(phi1L, PhiL, phi2L);
		
		final EulerAngles eulerR = new EulerAngles();
		eulerR.set(phi1R, PhiR, phi2R);
		
		final Matrix3x3 ML = new Matrix3x3();
		ML.set(eulerL);
		
		final Matrix3x3 MR = new Matrix3x3();
		MR.set(eulerR);
		
		final Matrix3x3 M = new Matrix3x3(ML);
		M.timesTransposed(MR);
		
		final UnitVector m1 = new UnitVector();
		m1.set(zenith, azimuth);		
		m1.transform(ML); 
		
		Binit = new InterfaceMatrix(M, m1);
						
		testTiltMin = tiltMin;
		testTwistMin = twistMin;
		testSymMin = symMin;
		testImpropMin = impropMin;
		testDecomp = decomp;
		testTTC = ttc;
		testSymTTC = symTtc;
							
		if(disTtc || decompDis) {
			
			final InterfaceMatrix Bdisor = Binit.getRepresWithDisor(ptGrp, true);
			final AxisAngle aa = new AxisAngle();			
			aa.set(Bdisor.M());
			if(disTtc) res.disTtc = MyMath.acos(Math.abs(aa.axis().dot(Bdisor.m1())));
			
			if(decompDis) {
			
				
				final double dot = aa.axis().dot(Bdisor.m1());    			   			
    			double Phi;
    			double omega;
    			
    			if (Math.abs(dot) < 1e-4d) {
    				
    				Phi = 0d;
    				omega = aa.angle();
    				
    			} else {
    				
    				final double alpha = MyMath.acos( dot );
    				
    				final double tg = FastMath.tan(alpha);
    				final double cos = FastMath.cos(0.5d * aa.angle() );
    			 		    			
    				Phi = FastMath.asin( FastMath.sin(0.5d*aa.angle()) / Math.sqrt(1d + tg*tg*cos*cos) );
    				omega = FastMath.asin( FastMath.sin(alpha) * FastMath.sin(0.5d * aa.angle()));
    				    				
    				Phi = 2d * Phi;
    				omega = 2d * omega;    				    				
       			}
    			
    			res.disTwistAngle = Phi;
    			res.disTiltAngle = omega;				
			}
		}	
		
		
		this.BT = BT;
		this.Bmin = Bmin;
	}
	
	
	@Override
	public Object call() throws Exception {
		
		if(!(testTiltMin || testTwistMin || testSymMin || testImpropMin || testDecomp || testTTC || testSymTTC)) return res;
		
		final boolean[] T;
		final boolean[] M;
				
		if(BT) T = new boolean[]{false, true}; else T = new boolean[]{false};
		if(Bmin) M = new boolean[]{false, true}; else M = new boolean[]{false};

		for(boolean transpose : T) for (boolean minus : M)	
		for(Matrix3x3 C1 : setOfAllC) for(Matrix3x3 C2 : setOfAllC) {

			final InterfaceMatrix B = new InterfaceMatrix(Binit);

			if(transpose) B.transpose();
			if(minus) B.toMinus();
			
			B.applySymmetry1(C1);
			B.applySymmetry2(C2);
						
			final AxisAngle aa = new AxisAngle();
			aa.set( B.M() );
			
    		if(testTiltMin) {
    			
    			final SimplexOptimizer optimizer = new SimplexOptimizer(EPS, EPS);
    	        final CommonsDistanceToTilt dist = new CommonsDistanceToTilt(B);
    	        
    	        final PointValuePair minimum = optimizer.optimize(new MaxEval(A_LOT),
                                     new ObjectiveFunction(dist),
                                     GoalType.MINIMIZE,                                  
                                     new InitialGuess(new double[] { aa.axis().zenith(), aa.axis().azimuth(), aa.angle(), B.m1().azimuth() }),
                                     new NelderMeadSimplex(4));
    	        
    	        final double distVal = minimum.getValue();
    	        if(distVal < res.tiltDist) {		
					res.tiltDist = distVal;
				}
    	        
    		}
    		
 			
			
    		if(testTwistMin) {
    			
      			final SimplexOptimizer optimizer = new SimplexOptimizer(EPS, EPS);
      			
    	        final CommonsDistanceToTwistPosAxis distPos = new CommonsDistanceToTwistPosAxis(B);
    	        final CommonsDistanceToTwistNegAxis distNeg = new CommonsDistanceToTwistNegAxis(B);
    	        
    	        final PointValuePair minimumPos = optimizer.optimize(new MaxEval(A_LOT),
                                     new ObjectiveFunction(distPos),
                                     GoalType.MINIMIZE,                                  
                                     new InitialGuess(new double[] { aa.axis().zenith(), aa.axis().azimuth(), aa.angle() }),
                                     new NelderMeadSimplex(3));
    	        
    	        final PointValuePair minimumNeg = optimizer.optimize(new MaxEval(A_LOT),
    	        						new ObjectiveFunction(distNeg),
    	        						GoalType.MINIMIZE,                                  
    	        						new InitialGuess(new double[] { aa.axis().zenith(), aa.axis().azimuth(), aa.angle() }),
    	        						new NelderMeadSimplex(3));
    	        
    	        final double distValPos = minimumPos.getValue();
    	        final double distValNeg = minimumNeg.getValue();
    	        
    	        if(distValPos < res.twistDist) {			
    				res.twistDist = distValPos;	
    			}
    	        if(distValNeg < res.twistDist) {			
    				res.twistDist = distValNeg;	
    			}    	        
		    }
    		
    		
    		
    		if(testSymMin) {
    			
    			
    			final SimplexOptimizer optimizer = new SimplexOptimizer(EPS, EPS);
      			
    	        final CommonsDistanceToSymPosAxis distPos = new CommonsDistanceToSymPosAxis(B);
    	        final CommonsDistanceToSymNegAxis distNeg = new CommonsDistanceToSymNegAxis(B);
    	        
    	        final PointValuePair minimumPos = optimizer.optimize(new MaxEval(A_LOT),
                                     new ObjectiveFunction(distPos),
                                     GoalType.MINIMIZE,                                  
                                     new InitialGuess(new double[] { aa.axis().zenith(), aa.axis().azimuth() }),
                                     new NelderMeadSimplex(2));
    	        
    	        final PointValuePair minimumNeg = optimizer.optimize(new MaxEval(A_LOT),
    	        						new ObjectiveFunction(distNeg),
    	        						GoalType.MINIMIZE,                                  
    	        						new InitialGuess(new double[] {aa.axis().zenith(), aa.axis().azimuth() }),
    	        						new NelderMeadSimplex(2));
    	        
    	        final double distValPos = minimumPos.getValue();
    	        final double distValNeg = minimumNeg.getValue();
    	        
    	    	if(distValPos < res.symDist) {		
   					res.symDist = distValPos;	
    			}
   					
   				if(distValNeg < res.symDist) { 					
   					res.symDist = distValNeg;	
    			} 	        
		    }
    		
    		
    		if(testImpropMin) {
    			
    			final SimplexOptimizer optimizer = new SimplexOptimizer(EPS, EPS);
    	        final CommonsDistanceToImprop dist = new CommonsDistanceToImprop(B);
    	        
    	        final PointValuePair minimum = optimizer.optimize(new MaxEval(A_LOT),
                                     new ObjectiveFunction(dist),
                                     GoalType.MINIMIZE,                                  
                                     new InitialGuess(new double[] { aa.axis().zenith(), aa.axis().azimuth(), B.m1().azimuth() }),
                                     new NelderMeadSimplex(3));
    	        
    	        final double distVal = minimum.getValue();
    	        if(distVal < res.impropDist) {			
   					res.impropDist = distVal;
   				}
    		}
    	
    		
    		
    		if(testTTC) {
    			
    			final double newTTC = MyMath.acos(Math.abs(aa.axis().dot(B.m1())));
    			
    			if(newTTC > res.maxTtc) {
    				res.maxTtc = newTTC;
    			}	
    			if(newTTC < res.minTtc) {
    				res.minTtc = newTTC;
    			}
    		}
    		
    		
    		if(testSymTTC) {
    			
    			final double newTTC = MyMath.acos(Math.abs(aa.axis().dot(B.m1())));
    			    			
    			final double symTTCSq = newTTC*newTTC + (ONEPI - aa.angle())*(ONEPI - aa.angle());
    			
    			final double impropTTCSq = (HALFPI - newTTC)*(HALFPI - newTTC) 
    					+ (ONEPI - aa.angle())*(ONEPI - aa.angle());
    			
    			if(symTTCSq < res.symTtc) {
    				 res.symTtc = symTTCSq;
    			}	
    			if(impropTTCSq < res.impropTtc) {
    				res.impropTtc = impropTTCSq;
    			}
    		}
    		
    	
  		
    		if(testDecomp) {
    			   			
    			// by Lange  
    			final double dot = aa.axis().dot(B.m1());
    			
    			    			
    			double Phi;
    			double omega;
    			
    			/*if( Math.abs(alpha) < 1e-4d) {
    				
    				Phi = aa.angle();
    				omega = 0d;
    				
    				System.out.println("IndividualGBTester: alpha = 0");
    				
    			} else
    			*/	
    			if (Math.abs(dot) < 1e-4d) {
    				
    				Phi = 0d;
    				omega = aa.angle();
    				
    			} else {
    				
    				final double alpha = MyMath.acos( aa.axis().dot(B.m1()) );
    				
    				final double tg = FastMath.tan(alpha);
    				final double cos = FastMath.cos(0.5d * aa.angle() );
    			 		    			
    				Phi = FastMath.asin( FastMath.sin(0.5d*aa.angle()) / Math.sqrt(1d + tg*tg*cos*cos) );
    				omega = FastMath.asin( FastMath.sin(alpha) * FastMath.sin(0.5d * aa.angle()));
    				    				
    				Phi = 2d * Phi;
    				omega = 2d * omega;    				    				
       			}
    			
    			
    			if (Phi < res.twistAngle) {
    				res.twistAngle = Phi;
    			}
    			
    			if (omega < res.tiltAngle) {
    				res.tiltAngle = omega;
    			}		
    		}	
		}
		 
		
		return res;
	}	

}
