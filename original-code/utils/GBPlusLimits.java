package utils;

public class GBPlusLimits {


	private String flag;
	private UnitVector m1;
	private Matrix3x3 M;
	private double planeLimit;
	private double misorLimit;
	
	private boolean arbitraryPlane;
	private boolean arbitraryMisor;
	
	public GBPlusLimits(
			String flag,
			boolean arbitraryMisor,
			boolean arbitraryPlane,			
			double misorLimit,
			double planeLimit,
			Matrix3x3 M,
			UnitVector m1
			) {
		
		this.flag = flag;
		this.arbitraryMisor = arbitraryMisor;
		this.arbitraryPlane = arbitraryPlane;			
		this.misorLimit = misorLimit;
		this.planeLimit = planeLimit;
		this.M = M;
		this.m1 = m1;
	}

	public final String getFlag() {
		return flag;
	}

	

	public final UnitVector getM1() {
		return m1;
	}

	
	public final Matrix3x3 getM() {
		return M;
	}

	
	public final double getPlaneLimit() {
		return planeLimit;
	}


	public final double getMisorLimit() {
		return misorLimit;
	}

	
	public final boolean isArbitraryPlane() {
		return arbitraryPlane;
	}

	public final boolean isArbitraryMisor() {
		return arbitraryMisor;
	}
	
	

}
