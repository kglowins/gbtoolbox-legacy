package com.github.kglowins.gbtoolbox.utils;

import java.io.BufferedReader;
import java.io.IOException;

public class GBDistReader {
	
	
	private boolean mrdmis;
	private boolean fracmis;
	
	private boolean mrdpl;
			
	private boolean exp;
	
	private double nMeas;
		
		
	public final void readHeaderLines(BufferedReader br) throws IOException {
		
		String line = null;		
		while ((line = br.readLine()) != null && line.matches("^\\s*#.*"));
		
		exp = line.indexOf("EXP") > -1;
		
		String[] words = line.trim().split("\\s+");
		if(words.length > 1) nMeas = Double.parseDouble(words[1]);
		
		line = br.readLine();
		mrdmis = line.indexOf("MRD_FIXMISOR") > -1;
		fracmis = line.indexOf("FRAC_FIXMISOR") > -1;
		mrdpl = line.indexOf("MRD_PLANE") > -1;		
		
	}
	
	
	public GBDistReader() {
		
		mrdmis = false;
		fracmis = false;
		mrdpl = false;
				
		exp = false;
		nMeas = 0d;
		
		
	}

	
	public final boolean containsMRD_MIS() {
		return mrdmis;
	}
	
	
	public final boolean containsFRAC_MIS() {
		return fracmis;
	}
	
	
	public final boolean containsMRD_PLANE() {
		return mrdpl;
	}
	
	
	
	public final double getNMeas() {
		return nMeas;
	}
	
	
	public final boolean isExperimental() {
		return exp;
	}
}
