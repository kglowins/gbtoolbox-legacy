package com.github.kglowins.gbtoolbox.algorithms;


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



import com.github.kglowins.gbtoolbox.utils.AxisAngle;
import com.github.kglowins.gbtoolbox.utils.CSLMisor;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.UnitVector;


@SuppressWarnings("deprecation")
public final class IndividualGBTester {
	
	
	// constants	
	private static final double INFTY = Double.MAX_VALUE;
	private static final double EPS = 1e-4d;
	private static final double ONEPI = Math.PI;
	private static final int A_LOT = Integer.MAX_VALUE;

	
			
	// settings
	
	private boolean prepareReport;	
	
	private Matrix3x3[] setOfAllC;
	
	private boolean testTiltMin;
	private boolean testTwistMin;
	private boolean testSymMin;
	private boolean testImpropMin;
	
	private boolean testDecomp; 			
	private boolean testCSL;	
	
	private boolean testTTC;
	
	private double p;
	private double omega0;
	private CSLMisor[] cslMisor;
	
		
	// output
	
	// CSL
	private CSLMisor lowestSigma;
	
		
	// minimiziation
	private double nearestTwistDist;	
	private InterfaceMatrix equivForTwist;
	private InterfaceMatrix nearestTwist;
	private Matrix3x3 C1twist;
	private Matrix3x3 C2twist;
	
	private double nearestTiltDist;
	private InterfaceMatrix equivForTilt;
	private InterfaceMatrix nearestTilt;
	private Matrix3x3 C1tilt;
	private Matrix3x3 C2tilt;
	
	private double nearestSymDist;
	private InterfaceMatrix equivForSymmetric;
	private InterfaceMatrix nearestSymmetric;
	private Matrix3x3 C1symmetric;
	private Matrix3x3 C2symmetric;
	
	private double nearestImpropDist;
	private InterfaceMatrix equivForImprop;
	private InterfaceMatrix nearestImprop;
	private Matrix3x3 C1improp;
	private Matrix3x3 C2improp;
	
	public boolean equivTiltT;
	public boolean equivTwistT;
	public boolean equivSymT;
	public boolean equivImpT;
	
	public boolean equivTiltM;
	public boolean equivTwistM;
	public boolean equivSymM;
	public boolean equivImpM;
	
	// Lange decomposition
	private double langeMinTwist;
	private double langeMinTilt;
	
	
	// TTC parameter
	private double alphaN;
	private double alphaL;	
	private double alphaS;
	private double alphaI;
	

	private int multiplicity;
	
	
	private InterfaceMatrix Binit;
	
	private boolean BT;
	private boolean Bmin;
			
	
	public IndividualGBTester() {
				
		testTiltMin = false;
		testTwistMin = false;
		testSymMin = false;
		testImpropMin = false;
		
		prepareReport = false;
		
		setInfiniteDistances();
		setInfiniteAngles();	
				
		equivForTwist = null;
		nearestTwist = null;
		C1twist = null;
		C2twist = null;
		
		equivForTilt = null;
		nearestTilt = null;
		C1tilt = null;
		C2tilt = null;
		
		equivForSymmetric = null;
		nearestSymmetric = null;
		C1symmetric = null;
		C2symmetric = null;
		
		equivForImprop = null;
		nearestImprop = null;
		C1improp = null;
		C2improp = null;	
		

	
				
		testCSL = false;		
	
		p = 0.5d;
		omega0 = Math.toRadians(15d);
		lowestSigma = null;
		
		setOfAllC = null;
		cslMisor = null;
		
		Binit = null;
		
		BT = false;
		Bmin = false;
		
		
		testTTC = false;		
	
		multiplicity = 0;
		
	}
	
	public void setSymmetriesInvolved(boolean BT, boolean Bmin) {
		this.BT = BT;
		this.Bmin = Bmin;
	}
		
	private void setInfiniteDistances() {
		
		if(testTwistMin) nearestTwistDist = INFTY;
		if(testTiltMin) nearestTiltDist = INFTY;
		if(testSymMin) nearestSymDist = INFTY;
		if(testImpropMin) nearestImpropDist = INFTY;	
	}
	
	private void setInfiniteAngles() {
		
		if(testDecomp) {
			langeMinTwist = INFTY;
			langeMinTilt = INFTY;
		}
	}
	
	private void resetTTC() {
		
		if(testTTC) {
			alphaL = INFTY;
			alphaN = INFTY;			
			alphaS = INFTY;
			alphaI = INFTY;
		}
	}
	
	public void useMinimization(boolean tilt, boolean twist, 
								boolean sym, boolean improp) {
		
		testTiltMin = tilt;
		testTwistMin = twist;
		testSymMin = sym;
		testImpropMin = improp;
	}
	
	public void useDecomp(boolean decomp) {	
		testDecomp = decomp;
	}
	
	
	public void useTTC(boolean b) {	
		testTTC = b;
	}
	
	
	public void setSaveDetails(boolean save) {		
		prepareReport = save;
	}
	
	

	
	public void testCSL(double p, double omega0, CSLMisor[] cslMisor) {
		testCSL = true;				
		this.p = p;
		this.omega0 = omega0;
		this.cslMisor = cslMisor;
	}
	
	public void dontTestCSL() {
		testCSL = false;
	}
	
	
	public void setSymmetryTransformations(Matrix3x3[] setOfAllC) {
		this.setOfAllC = setOfAllC;
	}
			

	public void test(InterfaceMatrix Binit) {

		this.Binit = Binit;
		
		multiplicity = 0;
		setInfiniteDistances();
		setInfiniteAngles();
		resetTTC();
		
		if(testCSL) lowestSigma = null;
		
		boolean[] transposeTF;
		boolean[] minusTF;
		
		if(BT) transposeTF = new boolean[]{false, true}; else transposeTF = new boolean[]{false};
		if(Bmin) minusTF = new boolean[]{false, true}; else minusTF = new boolean[]{false};
		
		for(boolean transpose : transposeTF) for(boolean minus : minusTF)
			for(Matrix3x3 C1 : setOfAllC) for(Matrix3x3 C2 : setOfAllC) 
			{
				
				
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
    	        final double[] resPt = minimum.getPoint();
				
				if(distVal < nearestTiltDist) {
					
					nearestTiltDist = distVal;
					
					if(prepareReport) {						
						
						final UnitVector resN = new UnitVector();
			    		resN.set(resPt[0], resPt[1]);
			    		
			    		final AxisAngle resAa = new AxisAngle();
			    		resAa.set(resN, resPt[2]);
			    		
			    		final Matrix3x3 resM = new Matrix3x3();
			    		resM.set(resAa);
			    			
			    		final UnitVector resM1 = new UnitVector();
			    		resM1.set(FastMath.atan(
			    				-1d / ( 
			    						FastMath.tan(resPt[0]) * ( FastMath.cos(resPt[1])*FastMath.cos(resPt[3]) + FastMath.sin(resPt[1])*FastMath.sin(resPt[3]) ) 
			    				)
			    			), resPt[3]);

			    		C1tilt = C1;
			    		C2tilt = C2;
			    		equivForTilt = B;
			    		nearestTilt = new InterfaceMatrix(resM, resM1);
			    		equivTiltT = transpose;
			    		equivTiltM = minus;
			    		
			    		
			    		/*if(distVal < 1e-4 ){ //some rubish
			    			
			    			System.out.println("-----------------------");
			    		
			    			System.out.println(nearestTilt.m1() + "  " + resM1);
			    			System.out.println(nearestTilt.m2());
			    			System.out.println(resM);
			    			System.out.println(resAa + "  " + resN);
			    			System.out.println(C1);
			    			System.out.println(C2);
			    			System.out.println(transpose);
			    			System.out.println(minus);
			    			System.out.println("-----------------------");
			    		}*/
					}
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
    	        
    	        final double[] resultPos = minimumPos.getPoint();
    	        final double[] resultNeg = minimumNeg.getPoint();
    	      
    			
    			if(distValPos < nearestTwistDist) {
    					
    				nearestTwistDist = distValPos;
    						
    				if(prepareReport) {
    						    						
    					final UnitVector resN = new UnitVector();
    			   		resN.set(resultPos[0], resultPos[1]);
    			    		
    			    	final AxisAngle resAa = new AxisAngle();
    			    	resAa.set(resN, resultPos[2]);
    			    		
    			    	final Matrix3x3 resM = new Matrix3x3();
    			    	resM.set(resAa);
    			    			
    			    	equivForTwist = B;
    			    	nearestTwist = new InterfaceMatrix(resM, resN);
    			    	C1twist = C1;
    			    	C2twist = C2;
    			    	equivTwistT = transpose;
			    		equivTwistM = minus;
    				}		
    			}
  					
    				
    			if(distValNeg < nearestTwistDist) {
    					
    				nearestTwistDist = distValNeg;
    						
    				if(prepareReport) {
    						   						
    					final UnitVector resN = new UnitVector();
    			    	resN.set(resultNeg[0], resultNeg[1]);
    			    		
    			    	final AxisAngle resAa = new AxisAngle();
    			    	resAa.set(resN, resultNeg[2]);
    			    		
    			    	final Matrix3x3 resM = new Matrix3x3();
    			    	resM.set(resAa);
    			    		
    			    	resN.negate();
    			    			
    			    	equivForTwist = B;
    			    	nearestTwist = new InterfaceMatrix(resM, resN);
    			    	C1twist = C1;
    			    	C2twist = C2;
    			    	equivTwistT = transpose;
			    		equivTwistM = minus;
    				}		
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
    	        
    	        final double[] resultPos = minimumPos.getPoint();
    	        final double[] resultNeg = minimumNeg.getPoint();    			
    		 					
   				if(distValPos < nearestSymDist) {
    					
    				nearestSymDist = distValPos;
    						
    				if(prepareReport) {
    						    						
    					final UnitVector resN = new UnitVector();
    		    		resN.set(resultPos[0], resultPos[1]);
    			    		
    		    		final AxisAngle resAa = new AxisAngle();
    		    		resAa.set(resN, ONEPI);
    			    		
    		    		final Matrix3x3 resM = new Matrix3x3();
    		    		resM.set(resAa);
    			    			
    		    		equivForSymmetric = B;
    		    		nearestSymmetric = new InterfaceMatrix(resM, resN);
    		    		C1symmetric = C1;
    		    		C2symmetric = C2;
    		    		equivSymT = transpose;
			    		equivSymM = minus;
    				}		
    			}
   					
   				if(distValNeg < nearestSymDist) {
    					
    				nearestSymDist = distValNeg;
    						
    				if(prepareReport) {
    						   						
    					final UnitVector resN = new UnitVector();
    		    		resN.set(resultNeg[0], resultNeg[1]);
    			    		
    		    		final AxisAngle resAa = new AxisAngle();
    		    		resAa.set(resN, ONEPI);
    			    		
    		    		final Matrix3x3 resM = new Matrix3x3();
    		    		resM.set(resAa);
    			    			
    		    		resN.negate();
    			    		
    		    		equivForSymmetric = B;
    		    		nearestSymmetric = new InterfaceMatrix(resM, resN);
    		    		C1symmetric = C1;
    		    		C2symmetric = C2;
    		    		equivSymT = transpose;
			    		equivSymM = minus;
    				}		
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
    	        final double[] resPt = minimum.getPoint();
  				
   				if(distVal < nearestImpropDist) {
    					
   					nearestImpropDist = distVal;
    						
   					if(prepareReport) {
   						
   						final UnitVector resN = new UnitVector();
   			    		resN.set(resPt[0], resPt[1]);
    			    		
   			    		final AxisAngle resAa = new AxisAngle();
   			    		resAa.set(resN, ONEPI);
    			    		
   			    		final Matrix3x3 resM = new Matrix3x3();
   			    		resM.set(resAa);
    			    			
   			    		final UnitVector resM1 = new UnitVector();
   			    		resM1.set(FastMath.atan(-1d / (FastMath.tan(resPt[0]) * ( FastMath.cos(resPt[1])*FastMath.cos(resPt[2]) + FastMath.sin(resPt[1])*FastMath.sin(resPt[2]) ) ) ), resPt[2]);
    			    			
   			    		equivForImprop = B;
   			    		nearestImprop = new InterfaceMatrix(resM, resM1);
   			    		C1improp = C1;
   			    		C2improp = C2;
   			    		equivImpT = transpose;
			    		equivImpM = minus;
   					}
   				}
    		}
    		
    		
    		if(testTTC) {
    			final double alpha = MyMath.acos(Math.abs(aa.axis().dot(B.m1())));
    			final double ninetyMinAlpha = Math.toRadians(90d) - alpha;
    			
    			double deltaOmega = ONEPI - aa.angle();
    			
    			deltaOmega = deltaOmega*deltaOmega;
    			
    			final double alphaScandSq = alpha*alpha + deltaOmega;
    			final double alphaIcandSq = ninetyMinAlpha*ninetyMinAlpha + deltaOmega;
    			
    			if(ninetyMinAlpha < alphaL) alphaL = ninetyMinAlpha;
    			
    			if(alpha < alphaN) alphaN = alpha;
    			
    			if(alphaScandSq < alphaS) alphaS = alphaScandSq;
    			if(alphaIcandSq < alphaI) alphaI = alphaIcandSq;    			
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
    			if (Math.abs(dot) < 1e-3d) {
    				
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
    			
    			if (Phi < langeMinTwist) {
    				langeMinTwist = Phi;
    				
    				if(prepareReport) {					
    				/*	equivForDecompTilt = B;
    					C1decompTilt = C1;
    					C2decompTilt = C2;*/
    				}
    			}
    			
    			if (omega < langeMinTilt) {
    				langeMinTilt = omega;
    				
    				if(prepareReport) {
    					/*equivForDecompTwist = B;
    					C1decompTwist = C1;
    					C2decompTwist = C2;*/
    				}
    			}
    			
    		}
    		
    		
    		
    		if(testCSL) {
    			
    			for(CSLMisor csl : cslMisor) {
   	     			
    				final Matrix3x3 M = new Matrix3x3(B.M());   	    		
   	    			M.timesTransposed(csl.getMatrix());
   	    			
   	    			final double angle = MyMath.acos(0.5d * (M.tr() - 1d));
   	    				
   	    			if(angle < omega0 / FastMath.pow(csl.getSigma(), p) ) {
   	    					
   	    				lowestSigma = csl;
   	    				//if(prepareReport) {
   	    					
   	    				//}
   	    				break;
   	    			}
    			}    			
    		}
    		
    		
    		
			if(    Math.abs(B.m1().x() - Binit.m1().x()) < 1e-3d
					&& Math.abs(B.m1().y() - Binit.m1().y()) < 1e-3d 
					&& Math.abs(B.m1().z() - Binit.m1().z()) < 1e-3d 
					&&		Math.abs(B.M().e00()-Binit.M().e00()) < 1e-3d &&
							Math.abs(B.M().e01()-Binit.M().e01()) < 1e-3d &&
							Math.abs(B.M().e02()-Binit.M().e02()) < 1e-3d &&
							
							Math.abs(B.M().e10()-Binit.M().e10()) < 1e-3d &&
							Math.abs(B.M().e11()-Binit.M().e11()) < 1e-3d &&
							Math.abs(B.M().e12()-Binit.M().e12()) < 1e-3d &&
							
							Math.abs(B.M().e20()-Binit.M().e20()) < 1e-3d &&
							Math.abs(B.M().e21()-Binit.M().e21()) < 1e-3d &&
							Math.abs(B.M().e22()-Binit.M().e22()) < 1e-3d
						)				
					{
					
		
					multiplicity++;							
				}
    		
    		
    		
		}
	}
	
	
	public int getMinSigma() {
		
		if(lowestSigma != null) return lowestSigma.getSigma();
		
		return 0;
	}
	
	
	// minimization
	
	public double getNearestTwistDist() {
		return Math.sqrt(nearestTwistDist);
	}

	public InterfaceMatrix getEquivForTwist() {
		return equivForTwist;
	}

	public InterfaceMatrix getNearestTwist() {
		return nearestTwist;
	}

	public Matrix3x3 getC1twist() {
		return C1twist;
	}

	public Matrix3x3 getC2twist() {
		return C2twist;
	}

	public double getNearestTiltDist() {
		return Math.sqrt(nearestTiltDist);
	}

	public InterfaceMatrix getEquivForTilt() {
		return equivForTilt;
	}

	public InterfaceMatrix getNearestTilt() {
		return nearestTilt;
	}

	public Matrix3x3 getC1tilt() {
		return C1tilt;
	}

	public Matrix3x3 getC2tilt() {
		return C2tilt;
	}

	public double getNearestSymDist() {
		return Math.sqrt(nearestSymDist);
	}

	public InterfaceMatrix getEquivForSymmetric() {
		return equivForSymmetric;
	}

	public InterfaceMatrix getNearestSymmetric() {
		return nearestSymmetric;
	}

	public Matrix3x3 getC1symmetric() {
		return C1symmetric;
	}

	public Matrix3x3 getC2symmetric() {
		return C2symmetric;
	}

	public double getNearestImpropDist() {
		return Math.sqrt(nearestImpropDist);
	}

	public InterfaceMatrix getEquivForImprop() {
		return equivForImprop;
	}

	public InterfaceMatrix getNearestImprop() {
		return nearestImprop;
	}

	public Matrix3x3 getC1improp() {
		return C1improp;
	}

	public Matrix3x3 getC2improp() {
		return C2improp;
	}

	
	
	
	public double getMinTiltLange() {
		return langeMinTilt;
	}
	
	public double getMinTwistLange() {
		return langeMinTwist;
	}
	

	public double getAlphaN() {
		return alphaN;
	}
	
	public double getAlphaL() {
		return alphaL;
	}
	
	public double getAlphaS() {
		return Math.sqrt(alphaS);
	}
	
	public double getAlphaI() {
		return Math.sqrt(alphaI);
	}
	

	public boolean isTestTiltMin() {
		return testTiltMin;
	}

	public boolean isTestTwistMin() {
		return testTwistMin;
	}

	public boolean isTestSymMin() {
		return testSymMin;
	}

	public boolean isTestImpropMin() {
		return testImpropMin;
	}



	public boolean isTestDecomp() {
		return testDecomp;
	}



	public boolean isTestCSL() {
		return testCSL;
	}

	public InterfaceMatrix lastTested() {
		return Binit;
	}
	
	
	public final int getMultiplicity() {
		return multiplicity;
	}

}
