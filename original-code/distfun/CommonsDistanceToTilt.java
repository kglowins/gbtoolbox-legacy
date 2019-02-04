package distfun;

import org.apache.commons.math3.analysis.MultivariateFunction;

import utils.AxisAngle;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MyMath;
import utils.UnitVector;


public final class CommonsDistanceToTilt implements MultivariateFunction {
	
	
	// Parameters of a given GB
	private final Matrix3x3 M;
	private final UnitVector m1;
	private final UnitVector m2;
	
		
	public CommonsDistanceToTilt(InterfaceMatrix B) {
		
		M = B.M();
		m1 = B.m1();
		m2 = B.m2();
	}

	
	@Override
	public double value(double[] arg) {
			
			
		// misorientation M' axis
		final UnitVector n = new UnitVector();
		n.set(arg[0], arg[1]);
		
		// M' matrix		
		final AxisAngle aa = new AxisAngle();
		aa.set(n, arg[2]);
		
		final Matrix3x3 Mprim = new Matrix3x3();
		Mprim.set(aa);
		
		// M' = R M
		final Matrix3x3 R = new Matrix3x3(Mprim);
		R.timesTransposed(M);
		
		final double m1primAzimuth = arg[3];

		final double omega = MyMath.acos(0.5d * (R.tr() - 1d));
						
		final double m1primZenith = Math.atan(-1d / (Math.tan(n.zenith()) * ( Math.cos(n.azimuth())*Math.cos(m1primAzimuth) + Math.sin(n.azimuth())*Math.sin(m1primAzimuth) ) ) );
		
		final UnitVector m1prim = new UnitVector();
		m1prim.set(m1primZenith, m1primAzimuth);
		
		final UnitVector m2prim = new UnitVector(m1prim);
		m2prim.transposedTransform(Mprim);
		m2prim.negate();
		
		final double theta1 = MyMath.acos(m1.dot(m1prim));
		final double theta2 = MyMath.acos(m2.dot(m2prim));
		
		return omega*omega + 0.5d * (theta1*theta1 + theta2*theta2);
	}
	

}
