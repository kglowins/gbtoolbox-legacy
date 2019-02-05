package com.github.kglowins.gbtoolbox.utils;


public class AxisAngle {

	
    private UnitVector axis;
        
    
    private double angle;
    
    
    private static final double ONEPI = Math.PI;
    private static final double TWOPI = 2d*ONEPI;
       
    
    public final UnitVector axis() {
    	return axis;
    }
    
    
    public final double angle() {
    	return angle;
    }
    
    
    public AxisAngle() {
    	axis = new UnitVector();
    	angle = 0d;
    }
    
    
    public AxisAngle(AxisAngle other) {
    	    	
    	axis = new UnitVector(other.axis());
    	angle = other.angle();
    }
    

    public final void set(AxisAngle other) {  
    	
    	axis.set(other.axis());
    	angle = other.angle();
    }
    
    
    public final void set(UnitVector axis, double angle) {
    	
    	this.axis.set(axis);
    	    	    
    	double w = angle;
    	
    	if(Math.abs(w) > TWOPI) w = Math.IEEEremainder(w, TWOPI);
    	if(w < 0d) w += TWOPI;
    	if(w > ONEPI) {
    		w = TWOPI - w;
    		this.axis.negate();
    	}
    	this.angle = w;    	
    }
                   
    
    public final void set(Matrix3x3 M) {
    	    	    	
    	if(M.isSymmetric()) { 
    		    		
    		final EulerAngles eul = new EulerAngles();
    		eul.set(M);    		
    		set(eul);
    		
    		
    	} else {
    		
    		double x = M.e21() - M.e12();
    		double y = M.e02() - M.e20();
    		double z = M.e10() - M.e01();
    		final double norm = Math.sqrt(x*x + y*y + z*z);
    		
    		x /= norm;
    		y /= norm;
    		z /= norm;
    		
            angle = MyMath.acos((M.tr() - 1d) * 0.5d);      
            axis.set(x, y, z);
    	}    	
    	
    }
    
   
    
    public final void set(EulerAngles eul) {
    	
    	final Quaternion quat = new Quaternion();
    	quat.set(eul);
    	set(quat);
    }
 
    
    public final void set(Quaternion Q) {
    	   
    	double x, y, z, w;
    	
    	if(Math.abs(Q.q0() - 1d) < 1e-4) {
    		
    		x = 0d;
			y = 0d;
			z = 1d;  
			w = 0d;
			
    	} else {
    			    	
    		final double S = MyMath.sqrt(1d - Q.q0()*Q.q0());
    		x = Q.q1() / S;
    		y = Q.q2() / S;
    		z = Q.q3() / S;    		
    		w = 2d * MyMath.acos(Q.q0());
    	}
    	
       	final UnitVector n = new UnitVector();
    	n.set(x, y, z);
    	
    	set(n, w);
    	
    }
       
    
    public final void set(RodriguesParams G) {
    	
    	double x, y, z, w;
    	
    	if( G.isHalfTurn() ) {    		
    		throw new IllegalArgumentException("Gibbs vector is infinite");    
    		
    	} else {    		
    		final double rSq = Math.sqrt(G.r1()*G.r1() + G.r2()*G.r2() + G.r3()*G.r3());    		
    		
    		
    		if(rSq > 1e-4d) {
    			x = G.r1() / rSq;
    			y = G.r2() / rSq;
    			z = G.r3() / rSq;
    			w = 2d * MyMath.atan(rSq);
    		} else {
    			x = 0d;
    			y = 0d;
    			z = 1d;
    			w = 0d;
    		}
    		
    	}    	
    	
    	final UnitVector n = new UnitVector();
    	n.set(x, y, z);
    	
    	set(n, w);
    	
    }
  
    
    @Override
    public String toString() {
	    return Math.toDegrees(angle) + "; " + axis;
    } 
}
