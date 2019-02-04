package utils;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import enums.PointGroup;

public class InterfaceMatrix {


	private final static double INFTY = Double.MAX_VALUE;
	
	
	private UnitVector m1;
	
	
	private UnitVector m2;
	
	
	private Matrix3x3 M;
	
	
	public final UnitVector m1() {
		
		return m1;		
	}
	
	
	public final UnitVector m2() {
		
		return m2;
	}


	public final Matrix3x3 M() {
	
		return M;
	}
	
   
	public InterfaceMatrix(Matrix3x3 M, UnitVector m1) {
		
		this.m1 = new UnitVector(m1);
		this.M = new Matrix3x3(M);
		
		m2 = new UnitVector(m1);
		m2.transposedTransform(M);
		m2.negate();
	}
                   
    
    public InterfaceMatrix(InterfaceMatrix other) {
    	
    	m1 = new UnitVector(other.m1());
    	m2 = new UnitVector(other.m2());
    	M = new Matrix3x3(other.M());
    }
    
	  
    public final void transpose() {
    	
    	UnitVector tmp = m1;
    	m1 = m2;
    	m2 = tmp;
    	M.transpose();
    }
    
    public final void toMinus() {
    	m1.negate();
    	m2.negate();
    }
   
    
    public final void applySymmetry1(Matrix3x3 C1) {
    	
    	m1.transform(C1);
    	M.leftMul(C1);
    }
    
    
    public final void applySymmetry2(Matrix3x3 C2) {
    	
    	m2.transform(C2);
    	M.timesTransposed(C2);    	
    }
    
              
    public final String toHTMLTable() {
    	
    	DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.US);
		final DecimalFormat df = new DecimalFormat("0.#####", otherSymbols);    	
    	return "<table border=1><tr> <td align=center>0</td> <td align=center>"+ df.format(m2.x()) +"</td> <td align=center>"+ df.format(m2.y()) +"</td> <td align=center>"+ df.format(m2.z()) +"</td> </tr>" +
    			"<tr><td align=center>"+ df.format(m1.x()) +"</td> <td align=center>"+ df.format(M.e00()) +"</td> <td align=center>"+ df.format(M.e01()) +"</td> <td align=center><"+ df.format(M.e02()) +"</td> </tr>" +
    			"<tr><td align=center>"+ df.format(m1.y()) +"</td> <td align=center>"+ df.format(M.e10()) +"</td> <td align=center>"+ df.format(M.e11()) +"</td> <td align=center>"+ df.format(M.e12()) +"</td> </tr>" +
    			"<tr><td align=center>"+ df.format(m1.z()) +"</td> <td align=center>"+ df.format(M.e20()) +"</td> <td align=center>"+ df.format(M.e21()) +"</td> <td align=center>"+ df.format(M.e22()) +"</td> </tr> </table>";
    	
    }
    
    public final double distance(InterfaceMatrix other, PointGroup ptGrp, boolean interchange, boolean min) {
    	
    	double minDist = INFTY;
    	
    	Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
    	
    	boolean[] minus = null;
    	boolean[] transpose = null;
    	if(min) minus = new boolean[]{false, true}; else minus = new boolean[]{false};
    	if(interchange) transpose = new boolean[]{false, true}; else transpose = new boolean[]{false};
    	
    	for(boolean T1 : transpose) for(boolean T2 : transpose) {
    		
    		for(boolean m1 : minus) for(boolean m2 : minus) {
    			
    			final InterfaceMatrix B1 = new InterfaceMatrix(this);
    			final InterfaceMatrix B2 = new InterfaceMatrix(other);
    			
    			if(T1) B1.transpose();
    			if(m1) B1.toMinus();
    			
    			if(T2) B2.transpose();
    			if(m2) B2.toMinus();
    			  			
   	    		for(Matrix3x3 C1: setC) for(Matrix3x3 C2 : setC) for(Matrix3x3 C3: setC) 
    	    	{
    	    		B1.applySymmetry2(C1);    		
    	    		B2.applySymmetry1(C2);
    	    		B2.applySymmetry2(C3);

    	    		final Matrix3x3 R = new Matrix3x3(B2.M());
    	    		R.timesTransposed(B1.M());
    	    					
    	    		final double omega = MyMath.acos(0.5d * (R.tr() - 1d));
    	    								
    	    		final double theta1 = MyMath.acos(B1.m1().dot(B2.m1()));
    	    		final double theta2 = MyMath.acos(B1.m2().dot(B2.m2()));
    	    		    		    	
    	    		final double dist = omega*omega + 0.5d * (theta1*theta1 + theta2*theta2);
    	    				
    	    		if(dist < minDist) 	minDist = dist;
    	    	}
    		}
    	}

    	return Math.sqrt(minDist);
    }
    
    
    public final double distanceMisorSpace(InterfaceMatrix other, PointGroup ptGrp, boolean interchange) {
    	
    	double minDist = INFTY;
    	
    	Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
    	

    	boolean[] transpose = null;
    	if(interchange) transpose = new boolean[]{false, true}; else transpose = new boolean[]{false};
    	
    	for(boolean T1 : transpose) for(boolean T2 : transpose) {
    		
    			
    			final InterfaceMatrix B1 = new InterfaceMatrix(this);
    			final InterfaceMatrix B2 = new InterfaceMatrix(other);
    			
    			if(T1) B1.transpose();   			
    			if(T2) B2.transpose();
    			    			  			
   	    		for(Matrix3x3 C1: setC) for(Matrix3x3 C2 : setC) for(Matrix3x3 C3: setC) 
    	    	{
    	    		B1.applySymmetry2(C1);    		
    	    		B2.applySymmetry1(C2);
    	    		B2.applySymmetry2(C3);

    	    		final Matrix3x3 R = new Matrix3x3(B2.M());
    	    		R.timesTransposed(B1.M());
    	    					
    	    		final double omega = MyMath.acos(0.5d * (R.tr() - 1d));
    	    								
    	    		    		    	
    	    		final double dist = omega;
    	    				
    	    		if(dist < minDist) 	minDist = dist;
    	    	}
    		
    	}

    	return minDist;
    }
    
    
    public final double distancePlaneSpace(InterfaceMatrix other, PointGroup ptGrp, boolean interchange, boolean min) {
    	
    	double minDist = INFTY;
    	
    	Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
    	
    	boolean[] minus = null;
    	boolean[] transpose = null;
    	if(min) minus = new boolean[]{false, true}; else minus = new boolean[]{false};
    	if(interchange) transpose = new boolean[]{false, true}; else transpose = new boolean[]{false};
    	
    	for(boolean T1 : transpose) for(boolean T2 : transpose) {
    		
    		for(boolean m1 : minus) for(boolean m2 : minus) {
    			
    			final InterfaceMatrix B1 = new InterfaceMatrix(this);
    			final InterfaceMatrix B2 = new InterfaceMatrix(other);
    			
    			if(T1) B1.transpose();
    			if(m1) B1.toMinus();
    			
    			if(T2) B2.transpose();
    			if(m2) B2.toMinus();
    			  			
   	    		for(Matrix3x3 C1: setC) for(Matrix3x3 C2 : setC) for(Matrix3x3 C3: setC) 
    	    	{
    	    		B1.applySymmetry2(C1);    		
    	    		B2.applySymmetry1(C2);
    	    		B2.applySymmetry2(C3);
   	    		    	    					    	    		    	    								
    	    		final double theta1 = MyMath.acos(B1.m1().dot(B2.m1()));
    	    		final double theta2 = MyMath.acos(B1.m2().dot(B2.m2()));
    	    		    		    	
    	    		final double dist = 0.5d * (theta1*theta1 + theta2*theta2);
    	    				
    	    		if(dist < minDist) 	minDist = dist;
    	    	}
    		}
    	}

    	return Math.sqrt(minDist);
    }
    
  /*  public final double misorDiff(InterfaceMatrix other, PointGroup ptGrp, boolean interchangeAllowed) {
    	
    	double minAngle = INFTY;
    	
    	final Matrix3x3 M1 = new Matrix3x3(this.M());
    	final Matrix3x3 M2T = new Matrix3x3(other.M());
    	M2T.transpose();
    	
    	
    	Matrix3x3 M1T = null;
    	Matrix3x3 M2 = null;    	
    	
    	if(interchangeAllowed) {
    		M1T = new Matrix3x3(this.M());
    		M1T.transpose();
    		M2 = new Matrix3x3(other.M());
    	}
    	
    	Matrix3x3[] M1list = null;
    	Matrix3x3[] M2list = null;
    	
    	if(interchangeAllowed) {
    		M1list = new Matrix3x3[]{M1, M1T};
    		M2list = new Matrix3x3[]{M2T, M2};
    	} else {
    		M1list = new Matrix3x3[]{M1};
    		M2list = new Matrix3x3[]{M2T};	
    	}
    	
    	Matrix3x3[] setC = Transformations.getSymmetryTransformations(ptGrp);
    	  
    	for(Matrix3x3 mat1 : M1list) for(Matrix3x3 mat2 : M2list)
    	   	for(Matrix3x3 C1: setC) for(Matrix3x3 C2 : setC) 
    	{
    		final Matrix3x3 R = new Matrix3x3(mat1);
    		R.times(C1);    		
    		R.times(mat2);
    		R.times(C2);
    					
    		final double angle = MyMath.acos(0.5d * (R.tr() - 1d));
    								
    		if(angle < minAngle) 	minAngle = angle;

    	}
    	
    	return minAngle;
    }
    */
   
     

 public final InterfaceMatrix getRepresWithDisor(PointGroup pointGrp, boolean sst) {
    	
    	final Matrix3x3[] setC = Transformations.getSymmetryTransformations(pointGrp);
    	final double sqrt2_minOne = Math.sqrt(2d) - 1d;
		final double[] sign = new double[]{-1d, 1d};
		
		boolean[] TF;
		if(sst) TF = new boolean[]{false, true}; else TF = new boolean[]{false};
		
    	for(boolean transpose : TF) for(Matrix3x3 C1 : setC) for(Matrix3x3 C2 : setC)  {
    		
    		final InterfaceMatrix copyB = new InterfaceMatrix(this);
    		if(transpose) copyB.transpose();
    		copyB.applySymmetry1(C1);
    		copyB.applySymmetry2(C2);
    		
    		final RodriguesParams rodr = new RodriguesParams();
    		rodr.set(copyB.M());
    	
    		
    		switch(pointGrp) {
    		case M3M:
    			boolean asym = true;
    			
    			if(sst) if(rodr.r1() < rodr.r2() || rodr.r2() < rodr.r3() || rodr.r3() < 0d) asym = false;
    		
    			if(asym) if( Math.abs(rodr.r1()) > sqrt2_minOne ||
    				Math.abs(rodr.r2()) > sqrt2_minOne || 
    				Math.abs(rodr.r3()) > sqrt2_minOne ) {
    				asym = false;
    				break;
    			}
    			
    			if(asym) {
    				for(double s1 : sign) {
    					for(double s2 : sign) {
    						for(double s3 : sign) {
    							if(1d < s1*rodr.r1() + s2*rodr.r2() + s3*rodr.r3()) {
    								asym = false;
    								break;
    							}
    						}    			
    						if(!asym) break;
    					}
    					if(!asym) break;    				    					
    				}
    			}
    			
    			if(asym) {    		    				
    				return copyB;
    			}
    			break;
    			
    		case _6MMM:
    			asym = true;
    			
    			   			
    			final double a = 0.5d;
    			final double b = Math.sqrt(3d) * 0.5d;
    			final double oneDivSqrt3 = 1d / Math.sqrt(3d);
    			
    			if(sst) if(0d > rodr.r2() || rodr.r2() > oneDivSqrt3*rodr.r1() || 0d > rodr.r3()) asym = false;
    			
    			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
    				asym = false;
    			}
    			   		    			
    			if(asym) {    				
    				for(double s : sign) {						
   						if( 1d < Math.abs(a + s*b*rodr.r3() ) ||
    						1d < Math.abs(a*rodr.r1() + s*b*rodr.r2() ) ||
    						1d < Math.abs(b*rodr.r1() + s*a*rodr.r2() ) ||
    						1d < Math.abs(b + s*a*rodr.r3()) ) {
    							
   							asym = false;
   							break;
   						}    						
    				}
    			}
    			if(asym) {
    				return copyB;
    			} 	
    			break;
    			
    		case _4MMM:
    			asym = true;
    			
    			final double sqrt2 = Math.sqrt(2d);
    			
    			if(sst) if(rodr.r1() < rodr.r2() || rodr.r2() < 0d || rodr.r3() < 0d) asym = false;
        		
    			if(asym) if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
    				asym = false;
    			}
    			
    			if(asym) if( Math.abs(rodr.r3()) > sqrt2_minOne ) asym = false;
    			
    			    			
    			if(asym) {
    				for(double s1 : sign) {
    					for(double s2 : sign) {
    						
    							if(sqrt2 < s1*rodr.r1() + s2*rodr.r2()) {
    								asym = false;
    								break;
    							}    						
    					}
    					if(!asym) break;    				    					
    				}
    			}
    			if(asym) {
    				return copyB;
    			} 	
    			break;
    			
    		case MMM:
    			asym = true;
    			
    			if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
    				asym = false;
    			}
    			
    			if(asym) {
    				return copyB;
    			} 	
    			break;
    			
    			default:
    				break;
    		}
    	}
    	
    	System.err.println("Could not find a representation in asymetric domain.");
    	return this;
    }
     
}
