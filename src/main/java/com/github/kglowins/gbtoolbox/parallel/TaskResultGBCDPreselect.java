package com.github.kglowins.gbtoolbox.parallel;

import java.util.ArrayList;

import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;

public final class TaskResultGBCDPreselect {
	
	public final ArrayList<Double> area;
	public final ArrayList<InterfaceMatrix> blist;
	
	
	public final ArrayList< ArrayList<Integer> > C1list;
	public final ArrayList< ArrayList<Integer> > C2list;
	public final ArrayList< ArrayList<Boolean> > Tlist;
	
	public double totalArea;
	
	public double acceptedGBArea;
	
	public double acceptedRepArea;
	
	public double nMeas;
	
	public long acceptedRep;
	
	
	public TaskResultGBCDPreselect() {
		
		area = new ArrayList<Double>();
		blist = new ArrayList<InterfaceMatrix>();
		
		C1list = new ArrayList< ArrayList<Integer> >();
		C2list = new ArrayList< ArrayList<Integer> >();
		Tlist = new ArrayList< ArrayList<Boolean> >();
		
		totalArea = 0d;
		nMeas = 0d;
		acceptedGBArea = 0d;
		acceptedRepArea = 0d;
		acceptedRep = 0;
		
	}

}
