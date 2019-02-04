package utils;


public class RodriguesParams {

	
	private double r1;
	
	
	private double r2;
	
	
	private double r3;
		
	
	public final double r1() {
		return r1;
	}
	
	
	public final double r2() {
		return r2;
	}


	public final double r3() {
		return r3;
	}
	
	
	public RodriguesParams() {		
		
		r1 = 0d;
		r2 = 0d;
		r3 = 0d;		
	}
	
	
	public RodriguesParams(double r1, double r2, double r3) {		
		
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;		
	}

			
	public final void set(double r1, double r2, double r3) {		
		
		this.r1 = r1;
		this.r2 = r2;
		this.r3 = r3;
	}
	
		
	public final void set(AxisAngle aa) {
	    
		if(Math.abs(aa.angle() - Math.PI) < 1e-4d) {
			
			r1 = r2 = r3 = Double.POSITIVE_INFINITY;
			
		} else {			
			final double t = Math.tan(0.5d * aa.angle());
			final UnitVector n = aa.axis();
			r1 = t * n.x();
			r2 = t * n.y();
			r3 = t * n.z();
		}
	}
	
	
	public final void set(Matrix3x3 M) {
		
		final double C = 1d + M.tr();
		
		if(Math.abs(C) < 1e-4d) {
			
			r1 = r2 = r3 = Double.POSITIVE_INFINITY;
			
		} else {
			
			r1 = (M.e21() - M.e12()) / C;
			r2 = (M.e02() - M.e20()) / C;
			r3 = (M.e10() - M.e01()) / C;
		}
	}
	
		
	public final void set(EulerAngles eul) {
		
		final Matrix3x3 R = new Matrix3x3();
		R.set(eul);
		set(R);
	}
	
		
	public final void set(Quaternion Q) {
		
		if(Math.abs(Q.q0()) < 1e-4d) {
			
			r1 = r2 = r3 = Double.POSITIVE_INFINITY;
			
		} else {
			
			r1 = Q.q1() / Q.q0();
			r2 = Q.q2() / Q.q0();
			r3 = Q.q3() / Q.q0();
		}
	}
	
	
	public final boolean isHalfTurn() {
		return Double.isInfinite(r1) || Double.isInfinite(r2) || Double.isInfinite(r3);		
	}
	
	
	@Override
	public String toString() {
		return "[" + r1 + ", " + r2 + ", " + r3 + "]";
	}
}
