package utils;

import org.apache.commons.math3.util.FastMath;


public class IsochoricParams {

	private static final double _3div4PISQUARE = 3d / 4d / Math.PI / Math.PI;
	private static final double _1div3 = 1d/3d;
	
	private double rho1;
	
	
	private double rho2;
	
	
	private double rho3;
		
	
	public final double rho1() {
		return rho1;
	}
	
	
	public final double rho2() {
		return rho2;
	}


	public final double rho3() {
		return rho3;
	}
	
	
	public IsochoricParams() {		
		
		rho1 = 0d;
		rho2 = 0d;
		rho3 = 0d;		
	}
	
	
	public IsochoricParams(double rho1, double rho2, double rho3) {		
		
		this.rho1 = rho1;
		this.rho2 = rho2;
		this.rho3 = rho3;		
	}

			
	public final void set(double rho1, double rho2, double rho3) {		
		
		this.rho1 = rho1;
		this.rho2 = rho2;
		this.rho3 = rho3;
	}
	
		
	public final void set(AxisAngle aa) {
	    
		final double f = FastMath.pow(_3div4PISQUARE * (aa.angle() - FastMath.sin(aa.angle())), _1div3 );
		final UnitVector n = aa.axis();
		rho1 = f * n.x();
		rho2 = f * n.y();
		rho3 = f * n.z();		
	}
	
	
	
	
	
	@Override
	public String toString() {
		return "[" + rho1 + ", " + rho2 + ", " + rho3 + "]";
	}
}
