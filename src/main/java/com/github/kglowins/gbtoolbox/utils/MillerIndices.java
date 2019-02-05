package com.github.kglowins.gbtoolbox.utils;

import java.util.Arrays;


public class MillerIndices {

	
	private static double INFTY = Double.MAX_VALUE;
	
	
	private int h;
	
	
	private int k;
	
	
	private int l;
	
	
	public final int h() {
		return h;
	}
	
	
	public final int k() {
		return k;
	}
	
	
	public final int l() {
		return l;
	}
	
	
	public MillerIndices() {
		
		h = 0;
		k = 0;
		l = 1;
	}
		
	
	public final void set(int h, int k, int l) {
				
		int nonZeroCounter = 0;
		int nonZero[] = new int[3];
			
		if(h != 0) {
			nonZero[nonZeroCounter] = h;
			nonZeroCounter++;
		}
		if(k != 0) {
			nonZero[nonZeroCounter] = k;
			nonZeroCounter++;
		}
		if(l != 0) {
			nonZero[nonZeroCounter] = l;
			nonZeroCounter++;
		}
		
		int gcd = 1;
				
		if(nonZeroCounter == 0) {
			throw new IllegalArgumentException("All indices cannot be zero.");
			
		} else if(nonZeroCounter == 2) {
			gcd = MyMath.gcd(nonZero[0], nonZero[1]);
			
		} else if(nonZeroCounter == 3) {
			gcd = MyMath.gcd(nonZero[0], MyMath.gcd(nonZero[1], nonZero[2]));
			
		} else {
			gcd = nonZero[0];
		}
		
		this.h = h;
		this.k = k;
		this.l = l;
		
		if(gcd < 0) gcd = -gcd;
		
		if (gcd != 1) {
			this.h /= gcd;
			this.k /= gcd;
			this.l /= gcd;
		}
	}
	
	
	public final void setAsCubic(UnitVector n, int maxIndex) { // valid for both crystallographic planes and axes
		set(n, maxIndex);
	}
	
	
	public final void setAsNonCubicAxis(UnitVector n, int maxIndex, Matrix3x3 invBasis) {
				
		final UnitVector transformedN = new UnitVector(n);
		transformedN.transform(invBasis);
		
		set(transformedN, maxIndex); 
	}
	
	
	 
	public final void setAsHexAxis3to4(UnitVector n, int maxIndex, Matrix3x3 invBasis) {
			
		final UnitVector vec = new UnitVector(n);
		vec.transform(invBasis);
		
		final double u = vec.x();
		final double v = vec.y();
		final double w = vec.z();
				
				
		vec.set((2d*u - v) / 3d,
				(2d*v - u) / 3d,				
				w);

		
		set(vec, maxIndex);
	}
			 	
	
	public final void setAsNonCubicPlane(UnitVector n, int maxIndex, Matrix3x3 basis) {
		
		final UnitVector transformedN = new UnitVector(n);										
		transformedN.transposedTransform(basis);
		
		set(transformedN, maxIndex); 
	}
			
	
	private final void set(UnitVector n, int maxIndex) { 
		
		final double[] axisOriginal = { n.x(), n.y(), n.z() };
		double[] axis = new double[3];
		final double[] min = { Math.abs(n.x()), Math.abs(n.y()), Math.abs(n.z()) };
		
		final double invMaxIndex = 1d / maxIndex;
		
		Arrays.sort(min);
		
		boolean valsOK = false;
		int index = 0;
		
		while(valsOK == false) {
			
			if(min[index] > 0.5d * invMaxIndex) {
				axis[0] = axisOriginal[0] / min[index];
				axis[1] = axisOriginal[1] / min[index];
				axis[2] = axisOriginal[2] / min[index];
				
				if (Math.abs( axis[0] ) <= maxIndex &&
					Math.abs( axis[1] ) <= maxIndex &&
					Math.abs( axis[2] ) <= maxIndex) {
					
					valsOK = true;
					
				} else index++;
			
			} else index++;			
		}
		

		int denom = 1;
		double minChi = Double.MAX_VALUE;
		
		
		while(denom <= maxIndex)	
		{
			final double hCand = axis[0] * denom;
			if(Math.abs(hCand) > maxIndex) break;
			
			final double kCand = axis[1] * denom;
			if(Math.abs(kCand) > maxIndex) break;
						
			final double lCand = axis[2] * denom;
			if(Math.abs(lCand) > maxIndex) break;
			
			
			final double hCandRound = Math.round(hCand);
			final double kCandRound = Math.round(kCand);
			final double lCandRound = Math.round(lCand);			
						
			final double du = hCand - hCandRound;
			final double dv = kCand - kCandRound;
			final double dw = lCand - lCandRound;
						
			final int hCandInt = (int)hCandRound;
			final int kCandInt = (int)kCandRound;
			final int lCandInt = (int)lCandRound;
				
			
			int nonZeroCounter = 0;
			int nonZero[] = new int[3];
				
			if(hCandInt != 0) {
				nonZero[nonZeroCounter] = hCandInt;
				nonZeroCounter++;
			}
			if(kCandInt != 0) {
				nonZero[nonZeroCounter] = kCandInt;
				nonZeroCounter++;
			}
			if(lCandInt != 0) {
				nonZero[nonZeroCounter] = lCandInt;
				nonZeroCounter++;
			}
			
			
			int gcd = Integer.MAX_VALUE;
					
			if(nonZeroCounter == 2) {
				gcd = MyMath.gcd(nonZero[0], nonZero[1]);
				
			} else if(nonZeroCounter == 3) {
				gcd = MyMath.gcd(nonZero[0], MyMath.gcd(nonZero[1], nonZero[2]));
				
			} else {
				gcd = nonZero[0];
			}
			
			if(gcd < 0) gcd = -gcd;
			
			if(gcd == 1) {
					
				final double chi = du*du + dv*dv + dw*dw;
				
				if(chi < minChi) {
					minChi = chi;
					h = hCandInt;
					k = kCandInt;
					l = lCandInt;
				}
			}
			
			denom++;
		}	
		
	//	System.out.println(minChi);
	}
	
	
	@Override
	public String toString() {
		return "[" + h + "," + k + "," + l + "]";
	}
		
}
