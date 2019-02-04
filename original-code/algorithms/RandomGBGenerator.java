package algorithms;


import org.apache.commons.math3.util.FastMath;

import utils.EulerAngles;
import utils.InterfaceMatrix;
import utils.Matrix3x3;
import utils.MyMath;
import utils.UnitVector;

public final class RandomGBGenerator {
	
	
	private static final double ONEPI = Math.PI;
	
	private final MitchellMooreGenerator generator;
	
	
	public RandomGBGenerator() {		
		generator = new MitchellMooreGenerator();
	}
	
	
	public InterfaceMatrix nextGB() {
				
		// generate Euler angles/misorientation
		final double phi1 = 2d * ONEPI * generator.nextDouble();
		final double phi2 = 2d * ONEPI * generator.nextDouble();
		final double Phi = MyMath.acos(2d * generator.nextDouble() - 1d);
		
		final EulerAngles angles = new EulerAngles();
		angles.set(phi1, Phi, phi2);
		final Matrix3x3 M = new Matrix3x3();
		M.set(angles);
				
		
		// generate normal to boundary plane
		final double theta = 2d * ONEPI * generator.nextDouble();
		final double u = -1d + 2d * generator.nextDouble();
		final double sqrt = Math.sqrt(1d - u*u);
		
		final UnitVector m1 = new UnitVector();
		m1.set(FastMath.cos(theta)*sqrt, FastMath.sin(theta)*sqrt, u);
						
		final InterfaceMatrix B = new InterfaceMatrix(M, m1);
					
		return B;
	}

}
