package parallel;

public final class TaskResultReprocess {
	
	public static final double INFTY = Double.MAX_VALUE;
	
	public double phi1L;
	public double PhiL;
	public double phi2L;
	
	public double phi1R;
	public double PhiR;
	public double phi2R;
	
	public double zenith;
	public double azimuth;
	
	public double area;
	public int correlat;
	
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
		
	public double disTtc;
	
	public double disTiltAngle;
	public double disTwistAngle;
	
	
	public TaskResultReprocess() {
		
		phi1L = 0d;
		PhiL = 0d;
		phi2L = 0d;
		
		phi1R = 0d;
		PhiR = 0d;
		phi2R = 0d;
		
		zenith = 0d;
		azimuth = 0d;
		
		area = 0d;
		correlat = 0;
		
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
		
		disTtc = INFTY;
		
		disTiltAngle = INFTY;
		disTwistAngle = INFTY;
	}

}
