package algorithms;

import utils.EulerAngles;
import utils.MyMath;

public final class RandomMisorGenerator {
	
	private static final double ONEPI = Math.PI;
	
	private final MitchellMooreGenerator generator;
	
	
	public RandomMisorGenerator() {		
		generator = new MitchellMooreGenerator();
	}
	
	
	public EulerAngles nextMisor() {
				
		// generate Euler angles/misorientation
		final double phi1 = 2d * ONEPI * generator.nextDouble();
		final double phi2 = 2d * ONEPI * generator.nextDouble();
		final double Phi = MyMath.acos(2d * generator.nextDouble() - 1d);
		
		final EulerAngles angles = new EulerAngles();
		angles.set(phi1, Phi, phi2);
		
		return angles;
	}

}
