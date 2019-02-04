package parallel;

public final class TaskResultGridOfNormals {
	
	public static final double INFTY = Double.MAX_VALUE;
	
	public double stereoProjX;
	public double stereoProjY;
	
	public double zenith;
	public double azimuth;
	
	public double tiltDist;
	public double twistDist;
	public double symDist;
	public double impropDist;

	public double tiltAngle;
	public double twistAngle;
	
	public double minTtc;
	public double maxTtc;
	
	public double symTtc;
	public double impropTtc;
	
	public TaskResultGridOfNormals() {
		
		stereoProjX = INFTY;
		stereoProjY = INFTY;
		
		zenith = INFTY;
		azimuth = INFTY;
		
		tiltDist = INFTY;
		twistDist = INFTY;
		symDist = INFTY;
		impropDist = INFTY;

		tiltAngle = INFTY;
		twistAngle = INFTY;
		
		minTtc = INFTY;
		maxTtc = 0d;
		
		symTtc = INFTY;
		impropTtc = INFTY;
	}

}
