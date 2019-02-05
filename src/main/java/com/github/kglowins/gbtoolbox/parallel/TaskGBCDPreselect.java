package com.github.kglowins.gbtoolbox.parallel;


import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.swing.SwingWorker;

import com.github.kglowins.gbtoolbox.enums.PointGroup;

import com.github.kglowins.gbtoolbox.utils.EulerAngles;
import com.github.kglowins.gbtoolbox.utils.GBDatHeader;
import com.github.kglowins.gbtoolbox.utils.InterfaceMatrix;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;


public final class TaskGBCDPreselect implements Callable {
	
	private static double INFTY = Double.MAX_VALUE;

	
	private final TaskResultGBCDPreselect res;		
	
	private final double tolerance;
	
	private final String file;
	
	private final boolean isExp;
	private final boolean elimin;
	private final double areaThr;
	
	private final Matrix3x3[] setC;
	private final Matrix3x3 Mfix;
	private final SwingWorker<Void,Void> parent;
	
	

						
	public TaskGBCDPreselect(SwingWorker<Void,Void> parent, Matrix3x3 Mfix, String file, double tolerance, boolean isExp, boolean elimin, double areaThr, PointGroup ptGrp)
	{ 		
		this.Mfix = Mfix;
		this.tolerance = tolerance;
		this.file = file;
		this.isExp = isExp;
		this.elimin = elimin;
		this.areaThr = areaThr;
		this.parent = parent;
		res = new TaskResultGBCDPreselect();
		setC = Transformations.getSymmetryTransformations(ptGrp);
		
	
	}
	
	
	@Override
	public Object call() throws Exception {
				 	
   		
		final BufferedReader in = new BufferedReader(new FileReader(file));
		
	
		GBDatHeader.skipHeaderLines(in);
		
		String line = null;
								
		while ((line = in.readLine()) != null && !parent.isCancelled()) { //TODO
							
			final String[] num = line.trim().split("\\s+");
							
			double A = INFTY;
			double corr = 0d;
				
			if(isExp) {
				corr = 1d / Integer.parseInt(num[8]);
				A = Double.parseDouble(num[9]);
			}
												
			if(!elimin || (elimin && A <= areaThr) ) {
				
				if(isExp) {
					res.totalArea += A;
					res.nMeas += corr;
				} else {
					res.totalArea += 1d;
					res.nMeas += 1d;
				}
				
				final double phi1L = Math.toRadians( Double.parseDouble(num[0]));
				final double PhiL = Math.toRadians( Double.parseDouble(num[1]));
				final double phi2L = Math.toRadians( Double.parseDouble(num[2]));
				
				final double phi1R = Math.toRadians( Double.parseDouble(num[3]));
				final double PhiR = Math.toRadians( Double.parseDouble(num[4]));
				final double phi2R = Math.toRadians( Double.parseDouble(num[5]));
						
				final EulerAngles eulL = new EulerAngles();
				eulL.set(phi1L, PhiL, phi2L);
				final EulerAngles eulR = new EulerAngles();
				eulR.set(phi1R, PhiR, phi2R);
												
				final Matrix3x3 ML = new Matrix3x3();
				ML.set(eulL);
				final Matrix3x3 MR = new Matrix3x3();
				MR.set(eulR);

				final Matrix3x3 M = new Matrix3x3(ML);
				M.timesTransposed(MR);
																
				
				boolean acceptedGB = false;
				
				for(boolean transpose : new boolean[]{false, true}) {
					for(int i = 0; i < setC.length; i++) {
						for(int j = 0; j < setC.length; j++) {
						
										
							final Matrix3x3 C1MC2 = new Matrix3x3(M);
																						
							C1MC2.leftMul(setC[i]);
							C1MC2.timesTransposed(setC[j]);
							
							if(transpose) C1MC2.transpose();
							
							final Matrix3x3 R = new Matrix3x3(C1MC2);
							R.timesTransposed(Mfix);							
							
							final double angle = R.rotationAngle();
									
							if(angle <= tolerance) {
								
								if(!acceptedGB) {
									
									final double zenith = Math.toRadians( Double.parseDouble(num[6]));
									final double azimuth = Math.toRadians( Double.parseDouble(num[7]));				
									final UnitVector m1 = new UnitVector();
									m1.set(zenith, azimuth);
									m1.transform(ML); 
									final InterfaceMatrix B = new InterfaceMatrix(M, m1);
									
									res.blist.add(B);
									if(isExp) {
										res.area.add(A);
										res.acceptedGBArea += A;
									} else {
										res.area.add(1d);
										res.acceptedGBArea += 1d;
									}
									
									res.C1list.add( new ArrayList<Integer>() );
									res.C2list.add( new ArrayList<Integer>() );
									res.Tlist.add( new ArrayList<Boolean>() );
								}
								
								acceptedGB = true;
								
								res.acceptedRep++;
								
								if(isExp) {
									res.acceptedRepArea += A;
								} else {
									res.acceptedRepArea += 1d;
								}
								
								final int pos = res.C1list.size() - 1;
				
								res.C1list.get( pos ).add(i);
								res.C2list.get( pos ).add(j);
								res.Tlist.get( pos ).add(transpose);
																								
							}		
						}
					}
					
				}
			
										
			}
		}


		return res;
	}	

}
