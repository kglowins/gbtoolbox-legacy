package com.github.kglowins.gbtoolbox.distfun;


import org.apache.commons.math3.analysis.MultivariateFunction;


import com.github.kglowins.gbtoolbox.utils.AxisAngle;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MyMath;
import com.github.kglowins.gbtoolbox.utils.UnitVector;


public final class CommonsDistanceToSymPosAxis implements MultivariateFunction {
	
	
	private static final double ONEPI = Math.PI;
	
	
	// Parameters of a given GB
	private final Matrix3x3 M;
	private final UnitVector m1;
	private final UnitVector m2;
	
			
	public CommonsDistanceToSymPosAxis(InterfaceMatrix B) {
		
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
		aa.set(n, ONEPI);
				
		final Matrix3x3 Mprim = new Matrix3x3();
		Mprim.set(aa);
					
		// M' = R M
		final Matrix3x3 R = new Matrix3x3(Mprim);
		R.timesTransposed(M);
				
		final double omega = MyMath.acos(0.5d * (R.tr() - 1d));
							
		// m1' = axis(R)
		UnitVector m1prim = new UnitVector(aa.axis());
		UnitVector m2prim = new UnitVector(aa.axis());
		m2prim.transposedTransform(Mprim);
		m2prim.negate();
		
		double theta1 = MyMath.acos(m1.dot(m1prim));
		double theta2 = MyMath.acos(m2.dot(m2prim));
		
		return omega*omega + 0.5d * (theta1*theta1 + theta2*theta2);
		

	}
	
	
}
