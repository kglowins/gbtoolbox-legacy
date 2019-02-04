package utils;

import java.text.DecimalFormat;

import enums.PointGroup;



public class Matrix3x3 {


	private double e00;
	
	
	private double e01;
	
	
	private double e02;
	
	
	private double e10;
	
	
	private double e11;
	
	
	private double e12;
	
	
	private double e20;
	
	
	private double e21;
	
	
	private double e22;
	
	
	public final double e00() {
		return e00;
	}
	
	
	public final double e01() {
		return e01;
	}
	
	
	public final double e02() {
		return e02;
	}
	
	
	public final double e10() {
		return e10;
	}
	
	
	public final double e11() {
		return e11;
	}
	
	
	public final double e12() {
		return e12;
	}
	
	
	public final double e20() {
		return e20;
	}
	
	
	public final double e21() {
		return e21;
	}
	
	
	public final double e22() {
		return e22;
	}

	
    public Matrix3x3() {    	
    	e00 = 1d; e01 = 0d; e02 = 0d;
        e10 = 0d; e11 = 1d; e12 = 0d;
        e20 = 0d; e21 = 0d; e22 = 1d;
    }
    
    
    public Matrix3x3(double e00, double e01, double e02,
    				 double e10, double e11, double e12,
    				 double e20, double e21, double e22) {
    	
       	this.e00 = e00;
    	this.e01 = e01;
    	this.e02 = e02;
    	
    	this.e10 = e10;
    	this.e11 = e11;
    	this.e12 = e12;
    	
    	this.e20 = e20;
    	this.e21 = e21;
    	this.e22 = e22;
    }
    
    
    public Matrix3x3(Matrix3x3 other) {
    	
    	e00 = other.e00();
    	e01 = other.e01();
    	e02 = other.e02();
    	
    	e10 = other.e10();
    	e11 = other.e11();
    	e12 = other.e12();
    	
    	e20 = other.e20();
    	e21 = other.e21();
    	e22 = other.e22();
    }
	
    
    public final double get(int row, int col) {
    	
    	switch(row) {
    	case 0:
    		switch(col) {
    		case 0: return e00;   			
    		case 1: return e01;
    		case 2: return e02;
    		default: throw new ArrayIndexOutOfBoundsException();    		
    		}
    	case 1:
    		switch(col) {
    		case 0: return e10;   			
    		case 1: return e11;
    		case 2: return e12;
    		default: throw new ArrayIndexOutOfBoundsException(); 
    		}
    	case 2:
    		switch(col) {
    		case 0: return e20;   			
    		case 1: return e21;
    		case 2: return e22;
    		default: throw new ArrayIndexOutOfBoundsException(); 
    		}
    	default: throw new ArrayIndexOutOfBoundsException();
    	}
    }
    
    
    public final void set(int row, int col, double val) {
    	
    	switch(row) {
    	case 0:
    		switch(col) {
    		case 0: e00 = val; break;   			
    		case 1: e01 = val; break;
    		case 2: e02 = val; break;
    		default: throw new ArrayIndexOutOfBoundsException(); 
    		}
    		break;
    	case 1:
    		switch(col) {
    		case 0: e10 = val; break;		
    		case 1: e11 = val; break;
    		case 2: e12 = val; break;
    		default: throw new ArrayIndexOutOfBoundsException(); 
    		}
    		break;
    	case 2:
    		switch(col) {
    		case 0: e20 = val; break;		
    		case 1: e21 = val; break;
    		case 2: e22 = val; break;
    		default: throw new ArrayIndexOutOfBoundsException();
    		}
    		break;
    	default: throw new ArrayIndexOutOfBoundsException();
    	}
    }
    
     
    public final void transpose() {
    	
    	double tmp = e01;
    	e01 = e10;
    	e10 = tmp;

    	tmp = e02;
    	e02 = e20;
		e20 = tmp;

		tmp = e12;
		e12 = e21;
		e21 = tmp;
    }

 
    public final double tr() {
    	
    	return e00 + e11 + e22;
    }
    
    
    public final double rotationAngle() {
    	return MyMath.acos( (tr() - 1d) * 0.5d);   
    }

           
    public final void set(AxisAngle aa) {
    	    	 	
    	final double c = Math.cos(aa.angle());
    	final double s = Math.sin(aa.angle());
    	final double oneMinC = 1d - c;
    	
    	final UnitVector n = aa.axis();
    	
    	final double xy = n.x() * n.y();
    	final double xz = n.x() * n.z();
    	final double yz = n.y() * n.z();
    	
    	final double xyBra = xy * oneMinC;
    	final double xzBra = xz * oneMinC;
    	final double yzBra = yz * oneMinC;
    	
    	final double xS = n.x() * s;
    	final double yS = n.y() * s;
    	final double zS = n.z() * s;
    	
    	e00 = c + n.x()*n.x()*oneMinC;
    	e01 = xyBra - zS;
    	e02 = xzBra + yS;
    	
    	e10 = xyBra + zS;
    	e11 = c + n.y()*n.y()*oneMinC;    	
    	e12 = yzBra - xS;
    	
    	e20 = xzBra - yS;
    	e21 = yzBra + xS;
    	e22 = c + n.z()*n.z()*oneMinC;    	
    }
    
       
    public final void set(EulerAngles eul) {
    
    	final double c1 = Math.cos( eul.phi1() );
    	final double C = Math.cos( eul.Phi() );
    	final double c2 = Math.cos( eul.phi2() );
		
    	final double s1 = Math.sin( eul.phi1() );
    	final double S = Math.sin( eul.Phi() );
    	final double s2 = Math.sin( eul.phi2() );
		
		e00 = c1*c2 - s1*s2*C;
		e01 = s1*c2 + c1*s2*C;
		e02 = s2*S;
		
		e10 = -c1*s2 - s1*c2*C;
		e11 = -s1*s2 + c1*c2*C;
		e12 = c2*S;
		
		e20 = s1*S;
		e21 = -c1*S;
		e22 = C;
    }
    
    
    public final void set(Quaternion Q) {
    	
    	double q0sq = Q.q0(); q0sq *= q0sq;
    	double q1sq = Q.q1(); q1sq *= q1sq;
    	double q2sq = Q.q2(); q2sq *= q2sq;
    	double q3sq = Q.q3(); q3sq *= q3sq;   
    	
    	final double q01 = Q.q0() * Q.q1();
    	final double q02 = Q.q0() * Q.q2();
    	final double q03 = Q.q0() * Q.q3();
    	final double q12 = Q.q1() * Q.q2();
    	final double q13 = Q.q1() * Q.q3();
    	final double q23 = Q.q2() * Q.q3();
  
    	e00 = q0sq + q1sq - (q2sq + q3sq);
    	e01 = 2d * (q12 - q03);
    	e02 = 2d * (q13 + q02);

    	e10 = 2d * (q12 + q03);
    	e11 = q0sq + q2sq - (q1sq + q3sq);
    	e12 = 2d * (q23 - q01);

    	e20 = 2d * (q13 - q02);
    	e21 = 2d * (q23 + q01);
    	e22 = q0sq + q3sq - (q1sq + q2sq);
    }
    
        
    public final void set(RodriguesParams G) {

    	if(G.isHalfTurn()) {    		
    		throw new IllegalArgumentException("A half-turn represented by Rodrigues parameters cannot be converted into a rotation matrix");
    		
    	} else {   
    		
    		final double r12 = G.r1() * G.r2();
    		final double r13 = G.r1() * G.r3();
    		final double r23 = G.r2() * G.r3();
    		final double r1sq = G.r1() * G.r1();
    		final double r2sq = G.r2() * G.r2();
    		final double r3sq = G.r3() * G.r3();
    	
    		final double C = 1d + r1sq + r2sq + r3sq;
    		
    		e00 = (1d + r1sq - r2sq - r3sq) / C;
    		e01 = 2d * (r12 - G.r3()) / C;
    		e02 = 2d * (r13 + G.r2()) / C;
    	
    		e10 = 2d * (r12 + G.r3()) / C;
    		e11 = (1d - r1sq + r2sq - r3sq) / C;
    		e12 = 2d * (r23 - G.r1()) / C;
    	
    		e20 = 2d * (r13 - G.r2()) / C;
    		e21 = 2d * (r23 + G.r1()) / C;
    		e22 = (1d - r1sq - r2sq + r3sq) / C;
    	}
    }
    

    public final void set(Matrix3x3 other) {    	
    	
    	e00 = other.e00;
    	e01 = other.e01;
    	e02 = other.e02;
    	
		e10 = other.e10;
		e11 = other.e11;
		e12 = other.e12;
		
		e20 = other.e20;
		e21 = other.e21;
		e22 = other.e22;
    }


    public final void invert() {
    	
    	final double det = det();
    	
    	if(Math.abs(det) < 1e-5d) {
    		throw new IllegalArgumentException("A singular matrix cannot be inverted");
    		
    	} else {   		
    		final double detInv = 1d / det;
    		
    		final double n00 = detInv * (e11*e22 - e12*e21);
    		final double n01 = detInv * (e02*e21 - e01*e22);
    		final double n02 = detInv * (e01*e12 - e02*e11);
    		
    		final double n10 = detInv * (e12*e20 - e10*e22);
    		final double n11 = detInv * (e00*e22 - e02*e20);
    		final double n12 = detInv * (e02*e10 - e00*e12);
    		
    		final double n20 = detInv * (e10*e21 - e11*e20);
    		final double n21 = detInv * (e01*e20 - e00*e21);
    		final double n22 = detInv * (e00*e11 - e01*e10);
    		
    		e00 = n00;
    		e01 = n01;
    		e02 = n02;
    		
    		e10 = n10;
    		e11 = n11;
    		e12 = n12;
    		
    		e20 = n20;
    		e21 = n21;
    		e22 = n22;
    	}
    }


    public final double det()  {
    	return e00*(e11*e22 - e12*e21) + e01*(e12*e20 - e10*e22) + e02*(e10*e21 - e11*e20);
    }

    
    public final void times(Matrix3x3 other) {
    	   	   	
    	final double n00 = e00*other.e00() + e01*other.e10() + e02*other.e20(); 
    	final double n01 = e00*other.e01() + e01*other.e11() + e02*other.e21(); 
    	final double n02 = e00*other.e02() + e01*other.e12() + e02*other.e22();
    	
    	final double n10 = e10*other.e00() + e11*other.e10() + e12*other.e20(); 
    	final double n11 = e10*other.e01() + e11*other.e11() + e12*other.e21();
    	final double n12 = e10*other.e02() + e11*other.e12() + e12*other.e22();
    	
    	final double n20 = e20*other.e00() + e21*other.e10() + e22*other.e20(); 
    	final double n21 = e20*other.e01() + e21*other.e11() + e22*other.e21(); 
    	final double n22 = e20*other.e02() + e21*other.e12() + e22*other.e22();
    	
		e00 = n00;
		e01 = n01;
		e02 = n02;
		
		e10 = n10;
		e11 = n11;
		e12 = n12;
		
		e20 = n20;
		e21 = n21;
		e22 = n22;
    }
    
    
    public final void timesTransposed(Matrix3x3 other) {
    	
    	final double n00 = e00*other.e00() + e01*other.e01() + e02*other.e02();
    	final double n01 = e00*other.e10() + e01*other.e11() + e02*other.e12(); 
    	final double n02 = e00*other.e20() + e01*other.e21() + e02*other.e22();
    	
    	final double n10 = e10*other.e00() + e11*other.e01() + e12*other.e02(); 
    	final double n11 = e10*other.e10() + e11*other.e11() + e12*other.e12(); 
    	final double n12 = e10*other.e20() + e11*other.e21() + e12*other.e22();
    	
    	final double n20 = e20*other.e00() + e21*other.e01() + e22*other.e02(); 
    	final double n21 = e20*other.e10() + e21*other.e11() + e22*other.e12(); 
    	final double n22 = e20*other.e20() + e21*other.e21() + e22*other.e22();
    	
		e00 = n00;
		e01 = n01;
		e02 = n02;
		
		e10 = n10;
		e11 = n11;
		e12 = n12;
		
		e20 = n20;
		e21 = n21;
		e22 = n22;
    }
    
  
    public final void leftMul(Matrix3x3 other) {
    	
    	final double n00 = e00*other.e00() + e10*other.e01() + e20*other.e02(); 
    	final double n01 = e01*other.e00() + e11*other.e01() + e21*other.e02(); 
    	final double n02 = e02*other.e00() + e12*other.e01() + e22*other.e02();
    	
    	final double n10 = e00*other.e10() + e10*other.e11() + e20*other.e12(); 
    	final double n11 = e01*other.e10() + e11*other.e11() + e21*other.e12();
    	final double n12 = e02*other.e10() + e12*other.e11() + e22*other.e12();
    	
    	final double n20 = e00*other.e20() + e10*other.e21() + e20*other.e22(); 
    	final double n21 = e01*other.e20() + e11*other.e21() + e21*other.e22(); 
    	final double n22 = e02*other.e20() + e12*other.e21() + e22*other.e22();
    	
		e00 = n00;
		e01 = n01;
		e02 = n02;
		
		e10 = n10;
		e11 = n11;
		e12 = n12;
		
		e20 = n20;
		e21 = n21;
		e22 = n22;
    }
  
  
    public final void leftMulTransposed(Matrix3x3 other) {

    	final double n00 = e00*other.e00() + e10*other.e10() + e20*other.e20(); 
    	final double n01 = e01*other.e00() + e11*other.e10() + e21*other.e20(); 
    	final double n02 = e02*other.e00() + e12*other.e10() + e22*other.e20();
    	
    	final double n10 = e00*other.e01() + e10*other.e11() + e20*other.e21(); 
    	final double n11 = e01*other.e01() + e11*other.e11() + e21*other.e21(); 
    	final double n12 = e02*other.e01() + e12*other.e11() + e22*other.e21();
    	
    	final double n20 = e00*other.e02() + e10*other.e12() + e20*other.e22(); 
    	final double n21 = e01*other.e02() + e11*other.e12() + e21*other.e22(); 
    	final double n22 = e02*other.e02() + e12*other.e12() + e22*other.e22();
    	    	
		e00 = n00;
		e01 = n01;
		e02 = n02;
		
		e10 = n10;
		e11 = n11;
		e12 = n12;
		
		e20 = n20;
		e21 = n21;
		e22 = n22;
    }
    
   
    public final void set(double e00, double e01, double e02,
    					  double e10, double e11, double e12,
    					  double e20, double e21, double e22) {	
    	this.e00 = e00;
    	this.e01 = e01;
    	this.e02 = e02;
    	
    	this.e10 = e10;
    	this.e11 = e11;
    	this.e12 = e12;
    	
    	this.e20 = e20;
    	this.e21 = e21;
    	this.e22 = e22;
    }
    
    
    public final void negate() {
    	e00 = -e00;
    	e01 = -e01;
    	e02 = -e02;
    	
    	e10 = -e10;
    	e11 = -e11;
    	e12 = -e12;
    	
    	e20 = -e20;
    	e21 = -e21;
    	e22 = -e22;    	
    }

    
    public final double distSq(Matrix3x3 other) {
    	
    	final double n11 = this.e00 - other.e00();
    	final double n12 = this.e01 - other.e01();
    	final double n13 = this.e02 - other.e02();
    	
    	final double n21 = this.e10 - other.e10();
    	final double n22 = this.e11 - other.e11();
    	final double n23 = this.e12 - other.e12();
    	
    	final double n31 = this.e20 - other.e20();
    	final double n32 = this.e21 - other.e21();
    	final double n33 = this.e22 - other.e22();
    	    	    	
    	return n11*n11 + n12*n12 + n13*n13 + n21*n21 + n22*n22 + n23*n23 + n31*n31 + n32*n32 + n33*n33;
    }
    
    public final Matrix3x3 nearestOrthogonal() {
    	
    	final double[][] entries = {{e00, e01, e02}, {e10, e11, e12}, {e20, e21, e22}};    	
    	final Jama.Matrix M = new Jama.Matrix(entries); 
    	final Jama.SingularValueDecomposition svd = new Jama.SingularValueDecomposition(M);
    	
    	final Jama.Matrix O = svd.getU().times( svd.getV().transpose() );
    	
    	final Matrix3x3 nearest = new Matrix3x3(O.get(0, 0), O.get(0, 1), O.get(0, 2),
    									  O.get(1, 0), O.get(1, 1), O.get(1, 2),
    									  O.get(2, 0), O.get(2, 1), O.get(2, 2));

    	return nearest;
    }
    
    
    @Override
    public String toString() {    	
    	String endl = System.getProperty("line.separator"); 
        return  "[" + e00 + ", " + e01 + ", " + e02 + " ]" + endl +
        		"[" + e10 + ", " + e11 + ", " + e12 + " ]" + endl +
        		"[" + e20 + ", " + e21 + ", " + e22 + " ]";
    }
    
    
    public final boolean isSymmetric() {
    	
    	return Math.abs(e01 - e10) < 1e-4d
    		&& Math.abs(e02 - e20) < 1e-4d
    		&& Math.abs(e12 - e21) < 1e-4d;
    }
    
    public final String toHTMLTable4() {
    	
    	final DecimalFormat df = new DecimalFormat("0.####");		
    	return "<table border=1><tr> <td align=center>1</td> <td align=center>0</td> <td align=center>0</td> <td align=center>0</td> </tr>" +
    			"<tr><td align=center>0</td> <td align=center>"+ df.format(e00) +"</td> <td align=center>"+ df.format(e01) +"</td> <td align=center><"+ df.format(e02) +"</td> </tr>" +
    			"<tr><td align=center>0</td> <td align=center>"+ df.format(e10) +"</td> <td align=center>"+ df.format(e11) +"</td> <td align=center>"+ df.format(e12) +"</td> </tr>" +
    			"<tr><td align=center>0</td> <td align=center>"+ df.format(e20) +"</td> <td align=center>"+ df.format(e21) +"</td> <td align=center>"+ df.format(e22) +"</td> </tr> </table>";
    	
    }
    
    public final Matrix3x3 getDisorientation(PointGroup pointGrp) {
    	
    	final Matrix3x3[] setC = Transformations.getSymmetryTransformations(pointGrp);
    	final double sqrt2_minOne = Math.sqrt(2d) - 1d;
		final double[] sign = new double[]{-1d, 1d};
		
		boolean[] TF = new boolean[]{false, true}; 
		
    	for(boolean transpose : TF) for(Matrix3x3 C1 : setC) for(Matrix3x3 C2 : setC)  {
    		
    		final Matrix3x3 copyM = new Matrix3x3(this);
    		if(transpose) copyM.transpose();
    		copyM.leftMul(C1);
    		copyM.timesTransposed(C2);
    		
    		final RodriguesParams rodr = new RodriguesParams();
    		rodr.set(copyM);
    	
    		
    		switch(pointGrp) {
    		case M3M:
    			boolean asym = true;
    			
    			if(rodr.r1() < rodr.r2() || rodr.r2() < rodr.r3() || rodr.r3() < 0d) asym = false;
    		
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
    				return copyM;
    			}
    			break;
    			
    		case _6MMM:
    			asym = true;
    			
    			   			
    			final double a = 0.5d;
    			final double b = Math.sqrt(3d) * 0.5d;
    			final double oneDivSqrt3 = 1d / Math.sqrt(3d);
    			
    			if(0d > rodr.r2() || rodr.r2() > oneDivSqrt3*rodr.r1() || 0d > rodr.r3()) asym = false;
    			
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
    				return copyM;
    			} 	
    			break;
    			
    		case _4MMM:
    			asym = true;
    			
    			final double sqrt2 = Math.sqrt(2d);
    			
    			if(rodr.r1() < rodr.r2() || rodr.r2() < 0d || rodr.r3() < 0d) asym = false;
        		
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
    				return copyM;
    			} 	
    			break;
    			
    		case MMM:
    			asym = true;
    			
    			if(1d < Math.abs(rodr.r1()) || 1d < Math.abs(rodr.r2()) || 1d < Math.abs(rodr.r3())) {
    				asym = false;
    			}
    			
    			if(asym) {
    				return copyM;
    			} 	
    			break;
    			
    			default:
    				break;
    		}
    	}
    	
    	System.err.println("Could not find a representation in asymetric domain.");
    	return this;
    }
    
    
    public boolean isEqualTo(Matrix3x3 other) {
    	    	    	
    	double accur = 1e-3;
    	return (Math.abs(this.e00() - other.e00()) < accur) &&
    			 (Math.abs(this.e01() - other.e01()) < accur) &&
    			 (Math.abs(this.e02() - other.e02()) < accur) &&
    			 
    			 (Math.abs(this.e10() - other.e10()) < accur) &&
    			 (Math.abs(this.e11() - other.e11()) < accur) &&
    			 (Math.abs(this.e12() - other.e12()) < accur) &&
    			 
    			 (Math.abs(this.e20() - other.e20()) < accur) &&
    			 (Math.abs(this.e21() - other.e21()) < accur) &&
    			 (Math.abs(this.e22() - other.e22()) < accur);
    }
   
    
    
    
}
