package com.github.kglowins.gbtoolbox.utils;

import org.apache.commons.math3.util.FastMath;


public class Quaternion {


	private double q0;
	
	
	private double q1;
	
	
	private double q2;
	
	
	private double q3;
	
		
    public final double q0() {
    	return q0;
    }
    
    
    public final double q1() {
    	return q1;
    }
    
    
    public final double q2() {
    	return q2;
    }
    
    
    public final double q3() {
    	return q3;
    }
	
    
	public Quaternion() {
    	
    	q0 = 1d;
    	q1 = 0d;
    	q2 = 0d;
    	q3 = 0d;
    }
    
    	
    public final void set(double q0, double q1, double q2, double q3) {
    	
    	final double normSq = q0*q0 + q1*q1 + q2*q2 + q3*q3;
    	
    	if(Math.abs(normSq) < 1e-6d) {
    		throw new IllegalArgumentException("Rotation cannot be represented by a zero quaternion");
    		
    	} else {
    		
    		this.q0 = q0;
    		this.q1 = q1;
    		this.q2 = q2;
    		this.q3 = q3;
    		
    		final double norm = Math.sqrt(normSq);
    		
    		if(Math.abs(norm - 1d) > 1e-5d) {
    			this.q0 /= norm;
    			this.q1 /= norm;
    			this.q2 /= norm;
    			this.q3 /= norm;
    		}
    	}
    }
    
    
    public final void set(Quaternion other) {
    	
    	set(other.q0(), other.q1(), other.q2(), other.q3());
    }
      
    
    public final void set(AxisAngle aa) {
    	
    	final double halfOmega = 0.5d * aa.angle();
    	final double S = FastMath.sin(halfOmega);    	  	
    	final UnitVector n = aa.axis();
    	set(Math.cos(halfOmega), S * n.x(), S * n.y(), S * n.z());    	
    }
    
    
    public final void set(Matrix3x3 M) {
    	  	
    	if(Math.abs(M.tr() + 1d) < 1e-4d) {
    		
    		final EulerAngles eul = new EulerAngles();
    		eul.set(M);
    		set(eul);
    		    		    		
    	} else {
    		
    		final double C = MyMath.sqrt(M.tr() + 1d);       		
       		q0 = 0.5d * C;
    	   		
    		final double D = 0.5d / C;    		
    		q1 = D * (M.e21() - M.e12());
    		q2 = D * (M.e02() - M.e20());
    		q3 = D * (M.e10() - M.e01());
    	}       	    	
    }

    
    public final void set(EulerAngles eul) {
    	
    	final double halfPhi = 0.5 * eul.Phi();
    	final double C = Math.cos(halfPhi);
    	final double S = Math.sin(halfPhi);
    	
    	final double avg = 0.5d * (eul.phi1() + eul.phi2());
    	final double diff = 0.5d * (eul.phi1() - eul.phi2());
    	    	
    	set(C * Math.cos(avg),
    		-S * Math.cos(diff),
    		-S * Math.sin(diff),
    		-C * Math.sin(avg)
    	);
    }

      
    public final void set(RodriguesParams rod) {
    	
    	final AxisAngle aa = new AxisAngle();
    	aa.set(rod);
    	set(aa);
    }
    
    
    public final void conj(){    	
    	q1 = -q1;
    	q2 = -q2;
    	q3 = -q3;
    }
    
    public final double misorAngle(Quaternion other) {
    	
    	return 2d * MyMath.acos(q0 * other.q0() - q1 * other.q1() - q2 * other.q2() - q3 * other.q3());
    }
    
    
    public final void mul(Quaternion other) {
    	
    	final double A = q0 * other.q0() - q1 * other.q1() - q2 * other.q2() - q3 * other.q3();
    	
    	final double B = q0 * other.q1() + other.q0() * q1 + q2 * other.q3() - q3 * other.q2();
    	
    	final double C = q0 * other.q2() + other.q0() * q2 + q3 * other.q1() - q1 * other.q3();
    	
    	final double D = q0 * other.q3() + other.q0() * q3 + q1 * other.q2() - q2 * other.q1();
    	
    	
    	 q0 = A;
    	 q1 = B;
    	 q2 = C;
    	 q3 = D;
    	
    }
    
    @Override
    public String toString() {
    	return "{" + q0 + ", " + q1 + ", " + q2 + ", " + q3 + "}";
    }
    
    
}
