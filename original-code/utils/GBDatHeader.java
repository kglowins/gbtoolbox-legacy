package utils;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import enums.PointGroup;

public class GBDatHeader {
	
	private String path;
	
	private boolean experimental;
	
	private String expOrRand;
	
	private int numberOfGBs;
	
	private PointGroup pointGrp;
	
	private String pointGrpName;
				
	private boolean containsTiltDist;
	
	private boolean containsTwistDist;
	
	private boolean containsSymDist;
	
	private boolean containsImpropDist;
	
	private boolean containsTiltAngle;
	
	private boolean containsTwistAngle;
	
	private boolean containsMinTTC;
	private boolean containsMaxTTC;
	
	private boolean containsSymTTC;
	private boolean containsImpropTTC;
	
	
	private boolean containsDisorTTC;
		
	private boolean containsDisTiltAngle;	
	private boolean containsDisTwistAngle;
	
		
	public static final void skipHeaderLines(BufferedReader br) throws IOException {
		
		String line = null;		
		while ((line = br.readLine()) != null && line.matches("^\\s*#.*"));
		br.readLine();
		br.readLine();
	}
	
		
	public GBDatHeader(File f) throws IOException, NumberFormatException {
		
		path = f.getAbsolutePath();
		
		final BufferedReader in	= new BufferedReader(new FileReader(f));
			
		String line = null;
		while ((line = in.readLine()) != null && line.matches("^\\s*#.*"));

		// EXP/RAND
		line = line.trim();
		if(line.compareTo("EXP") == 0) experimental = true;
		else if(line.compareTo("RANDOM") == 0) experimental = false;
		else throw new IOException();
		
		if(experimental) expOrRand = "EXP"; else expOrRand = "RANDOM";
		
		// POINT GROUP
		line = in.readLine().trim();
		switch(line) {
		case "m-3m": pointGrp = PointGroup.M3M; pointGrpName = "m3\u0305m"; break;
		
		case "6/mmm": pointGrp = PointGroup._6MMM; pointGrpName = "6/mmm"; break;
		case "4/mmm": pointGrp = PointGroup._4MMM; pointGrpName = "4/mmm"; break;
		case "mmm": pointGrp = PointGroup.MMM; pointGrpName = "mmm"; break;
		
	
		case "2/m": pointGrp = PointGroup._2M; pointGrpName = "2/m"; break;
		case "-3m": pointGrp = PointGroup._3M; pointGrpName = "3\u0305m"; break;
		case "-1": pointGrp = PointGroup._1; pointGrpName = "1\u0305"; break;
		
		default: throw new IOException(); 
		}
						
		// CONTENT
		line = in.readLine();		
		containsTiltDist = line.indexOf("DIST_TILT") > -1;
		containsTwistDist = line.indexOf("DIST_TWIST") > -1;
		containsSymDist = line.indexOf("DIST_SYM") > -1;
		containsImpropDist = line.indexOf("DIST_180-TILT") > -1;
		containsTiltAngle = line.indexOf("F_TILT_ANGLE") > -1;
		containsTwistAngle = line.indexOf("F_TWIST_ANGLE") > -1;
		
		containsMinTTC = line.indexOf("APPROX_D_TWIST") > -1;	
		containsMaxTTC = line.indexOf("APPROX_D_TILT") > -1;
		
		containsSymTTC = line.indexOf("APPROX_D_SYM") > -1;	
		containsImpropTTC = line.indexOf("APPROX_D_180-TILT") > -1;	
		
		containsDisorTTC = line.indexOf("DISOR_TTC") > -1;	
		
		containsDisTiltAngle = line.indexOf("DISOR_TILT_A") > -1;
		containsDisTwistAngle = line.indexOf("DISOR_TWIST_A") > -1;
				
		// N
		try {
			while(in.readLine() != null) numberOfGBs++; //TODO 
		} catch (EOFException exc) {
			System.out.println("GBData: end of file encountered");
		}
	}
	

	public final String getPath() {
		return path;	
	}
	
	public final boolean isExperimental() {
		return experimental;
	}

	public final int getNumberOfGBs() {
		return numberOfGBs;
	}

	public final PointGroup getPointGrp() {
		return pointGrp;
	}
	
	public final String getPointGrpName() {
		return pointGrpName;
	}



	public final boolean containsTiltDist() {
		return containsTiltDist;
	}

	public final boolean containsTwistDist() {
		return containsTwistDist;
	}

	public final boolean containsSymDist() {
		return containsSymDist;
	}

	public final boolean containsImpropDist() {
		return containsImpropDist;
	}

	public final boolean containsTiltAngle() {
		return containsTiltAngle;
	}

	public final boolean containsTwistAngle() {
		return containsTwistAngle;
	}
	
	public final boolean containsMinTTC() {
		return containsMinTTC;
	}
	
	public final boolean containsMaxTTC() {
		return containsMaxTTC;
	}
	
	
	public final boolean containsSymTTC() {
		return containsSymTTC;
	}
	
	public final boolean containsImpropTTC() {
		return containsImpropTTC;
	}
		
	
	public final boolean containsDisorTTC() {
		return containsDisorTTC;
	}
	
	public static final char isInFile(boolean b) {
		if(b) return '\u25cf'; else return '\u25cb';
	}
	
	public final String getExpOrRand() {
		return expOrRand;
	}
	
	public final boolean containsDisTiltAngle() {
		return containsDisTiltAngle;
	}

	public final boolean containsDisTwistAngle() {
		return containsDisTwistAngle;
	}

}
