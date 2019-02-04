package parallel;


import java.util.concurrent.Callable;

import org.apache.commons.math3.optim.InitialGuess;
import org.apache.commons.math3.optim.MaxEval;
import org.apache.commons.math3.optim.PointValuePair;
import org.apache.commons.math3.optim.nonlinear.scalar.GoalType;
import org.apache.commons.math3.optim.nonlinear.scalar.ObjectiveFunction;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.NelderMeadSimplex;
import org.apache.commons.math3.optim.nonlinear.scalar.noderiv.SimplexOptimizer;
import org.apache.commons.math3.util.FastMath;

import distfun.CommonsDistanceToImprop;
import distfun.CommonsDistanceToSymNegAxis;
import distfun.CommonsDistanceToSymPosAxis;
import distfun.CommonsDistanceToTilt;
import distfun.CommonsDistanceToTwistNegAxis;
import distfun.CommonsDistanceToTwistPosAxis;



import utils.AxisAngle;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MyMath;
import utils.UnitVector;



public final class TaskTestOneFromGrid implements Callable {
	
	private static final double INFTY = Double.MAX_VALUE;
	private static final double EPS = 1e-4d;
	private static final double HALFPI = 0.5d * Math.PI;
	private static final double ONEPI = Math.PI;	
	private static final int A_LOT = Integer.MAX_VALUE;
	
			
	private final Matrix3x3[] setOfAllC;
	private final InterfaceMatrix Binit;
		
	private final boolean testTiltMin;
	private final boolean testTwistMin;
	private final boolean testSymMin;
	private final boolean testImpropMin;
	
	private final boolean testDecomp;
	
	private final boolean testMinTTC;
	private final boolean testMaxTTC;
	private final boolean testSymTTC;
	private final boolean testImpropTTC;
		
	private final TaskResultGridOfNormals res; 
						
	public TaskTestOneFromGrid(Matrix3x3 Mfix, double zenith, double azimuth, 
									  boolean tiltMin, boolean twistMin, boolean symMin, boolean impropMin,
									  boolean decomp, boolean minTtc, boolean maxTtc, boolean symTtc, boolean impropTtc,
									  Matrix3x3[] setOfAllC) {
		
		res = new TaskResultGridOfNormals();
		
		
				
		this.setOfAllC = setOfAllC;
		
		final UnitVector m1 = new UnitVector();
		m1.set(zenith, azimuth);
		Binit = new InterfaceMatrix(Mfix, m1);
		
		final double r = FastMath.tan(0.5d * zenith);	
		res.stereoProjX = r * FastMath.cos(azimuth);
		res.stereoProjY = r * FastMath.sin(azimuth);	
		
		res.zenith = zenith;
		res.azimuth = azimuth;
				
		testTiltMin = tiltMin;
		testTwistMin = twistMin;
		testSymMin = symMin;
		testImpropMin = impropMin;
		testDecomp = decomp;
		
		
		testMinTTC = minTtc;
		
		
		/*		
			final InterfaceMatrix Bdisor = Binit.getRepresWithDisor(PointGroup.M3M, true);
			final AxisAngle aa = new AxisAngle();
			aa.set(Bdisor.M());
			res.minTtc = MyMath.acos(Math.abs(aa.axis().dot(Bdisor.m1())));*/
		
		
		
		testMaxTTC = maxTtc;
		
		testSymTTC = symTtc;
		testImpropTTC = impropTtc;
		
		
	}
	
	
	@Override
	public Object call() throws Exception {
	
		for(Matrix3x3 C1 : setOfAllC) for(Matrix3x3 C2 : setOfAllC) {//

			final InterfaceMatrix B = new InterfaceMatrix(Binit);
		
			B.applySymmetry1(C1);//
			B.applySymmetry2(C2);//
				
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
    		
    		
    	
    		double newTTC = INFTY;
    		if(testMinTTC || testMaxTTC || testSymTTC || testImpropTTC) newTTC = MyMath.acos(Math.abs(aa.axis().dot(B.m1())));
    		if(testMaxTTC) {
    			    			
    			if(newTTC > res.maxTtc) {
    				res.maxTtc = newTTC;
    			}
    		}

    		if(testMinTTC) {
    			    			    		
    			if(newTTC < res.minTtc) {
    				res.minTtc = newTTC;
    			}
    		}
    		
    		if(testSymTTC) {
    			    			    			
    			final double symTTCSq = newTTC*newTTC + (ONEPI - aa.angle())*(ONEPI - aa.angle());
    			
    			if(symTTCSq < res.symTtc) {
    				 res.symTtc = symTTCSq;
    			}	
    		}
    		
    		
    		if(testImpropTTC) {
    			
    			
    			final double impropTTCSq = (HALFPI - newTTC)*(HALFPI - newTTC) 
    					+ (ONEPI - aa.angle())*(ONEPI - aa.angle());
    			
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
    			if (Math.abs(dot) < 1.745e-3d) {
    				
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
    			
    			
    			if (Phi < res.twistAngle) {
    				res.twistAngle = Phi;
    			}
    			
    			if (omega < res.tiltAngle) {
    				res.tiltAngle = omega;
    			}		
    		}	
		}//
		 
		
		return res;
	}	

}
