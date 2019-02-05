package com.github.kglowins.gbtoolbox.utils;


public class EulerAngles {

	
	private double phi1;
		
	
	private double Phi;
		
	
	private double phi2;
	
	
	private static final double ONEPI = Math.PI;
	private static final double TWOPI = 2d * ONEPI;	
		
	
	public final double phi1() {
		return phi1;
	}
	
	
	public final double Phi() {
		return Phi;
	}
	
	
	public final double phi2() {
		return phi2;
	}
	
	
	public EulerAngles() {	
		
		phi1 = 0d;
		Phi = 0d;
		phi2 = 0d;			
	}
	
	public final void set(EulerAngles other) {
		phi1 = other.phi1();
		Phi = other.Phi();
		phi2 = other.phi2();	
	}
	
	public final void set(double _phi1, double _Phi, double _phi2) {
		
		
		double phi1 = _phi1;
		double Phi = _Phi;
		double phi2 = _phi2;
		
	 	if(Math.abs(phi1) >= TWOPI) phi1 = Math.IEEEremainder(phi1, TWOPI);
    	if(phi1 < 0d) phi1 += TWOPI;
    	
    	if(Math.abs(phi2) >= TWOPI) phi2 = Math.IEEEremainder(phi2, TWOPI);
    	if(phi2 < 0d) phi2 += TWOPI;
    	
    	if(Math.abs(Phi) > TWOPI) Phi = Math.IEEEremainder(Phi, TWOPI);
    	if(Phi < 0d) Phi += TWOPI;
    	
    	if(Phi > ONEPI) {
    		
    		Phi = TWOPI - Phi;
    		
    		phi1 -= ONEPI;
    		if(phi1 < 0d) phi1 += TWOPI;
    		
    		phi2 -= ONEPI;
    		if(phi2 < 0d) phi2 += TWOPI;
    	}
    	

    	this.phi1 = phi1;
    	this.Phi = Phi;
    	this.phi2 = phi2;
    	    	
   	}
	
			
	@Override
	public String toString() {
		
		return "{" + Math.toDegrees(phi1) + ", " +  Math.toDegrees(Phi) + ", " +  Math.toDegrees(phi2) + "}";
	}
		
	
	public final void set(AxisAngle aa) {
		
		final Matrix3x3 M = new Matrix3x3();
		M.set(aa);
		set(M);
	}
	
		
	public final void set(Matrix3x3 M) {
				
		if(Math.abs(M.e22() - 1d) < 1e-5d || M.e22() > 1d) {
			
			phi1 = MyMath.atan2(M.e01(), M.e00());
			Phi = 0d;
			phi2 = 0d;
			
		} else if(Math.abs(M.e22() + 1d) < 1e-5d || M.e22() < -1d) {
			
			phi1 = MyMath.atan2(M.e01(), M.e00());
			Phi = Math.PI;
			phi2 = 0d;
			
		} else {
			
			phi1 = MyMath.atan2(M.e20(), -M.e21());
			Phi = MyMath.acos(M.e22());
			phi2 = MyMath.atan2(M.e02(), M.e12());
		}				
	}
	
		
	public final void set(Quaternion Q) {
		
		Phi = MyMath.acos(Q.q0()*Q.q0() + Q.q3()*Q.q3() - (Q.q1()*Q.q1() + Q.q2()*Q.q2()) );
		
		if(Math.abs(Phi) < 1e-5d) {
			
			phi1 = MyMath.atan2(-2d * Q.q0() * Q.q3(), Q.q0()*Q.q0() - Q.q3()*Q.q3());
			phi2 = 0d;
			
			
		} else if(Math.abs(Phi - Math.PI) < 1e-5d) {
			
			phi1 = MyMath.atan2(2d * Q.q1() * Q.q2(), Q.q1()*Q.q1() - Q.q2()*Q.q2());
			phi2 = 0d;
			
		} else {
			
			final double q01 = Q.q0() * Q.q1();
			final double q23 = Q.q2() * Q.q3();
			final double q02 = Q.q0() * Q.q2();
			final double q13 = Q.q1() * Q.q3();
		
			phi1 = MyMath.atan2(-q02 + q13, -(q23 + q01));
			phi2 = MyMath.atan2(q13 + q02, q23 - q01);		
		}
		
	}
	
		
	public final void set(RodriguesParams G) {
		
		final Matrix3x3 M = new Matrix3x3();
		M.set(G);
		set(M);
	}
}
