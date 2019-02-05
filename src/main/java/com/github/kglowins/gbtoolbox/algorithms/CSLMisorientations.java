package com.github.kglowins.gbtoolbox.algorithms;

import java.util.ArrayList;
import java.util.Collections;

import org.apache.commons.math3.util.ArithmeticUtils;
import org.apache.commons.math3.util.FastMath;




import com.github.kglowins.gbtoolbox.utils.AxisAngle;
import com.github.kglowins.gbtoolbox.utils.CSLMisor;
import com.github.kglowins.gbtoolbox.utils.Matrix3x3;
import com.github.kglowins.gbtoolbox.utils.MillerIndices;
import com.github.kglowins.gbtoolbox.utils.Transformations;
import com.github.kglowins.gbtoolbox.utils.UnitVector;



public final class CSLMisorientations {
	
	
	private static final double EPS = 1e-5d;
	
	
	public static CSLMisor[] getForCubic(int maxSigma) {
		
		final ArrayList<CSLMisor> CSL = new ArrayList<CSLMisor>();
		
		CSL.add(new CSLMisor(new Matrix3x3(), 1));

				
		final int sqrtOfMaxSigma = (int)Math.ceil(Math.sqrt(maxSigma));
			
		for(int m = 1; m <= sqrtOfMaxSigma; m++) {			
			for(int U = 0; U <= m; U++) {				
				for(int V = 0; V <= U; V++) {					
					for(int W = 0; W <= V; W++) {
						if(U != 0 || V != 0 || W != 0)
						if(ArithmeticUtils.gcd(m, ArithmeticUtils.gcd(U, ArithmeticUtils.gcd(V, W))) == 1) {
								
							int N = 0;
							if(m % 2 == 1) N++;
							if(U % 2 == 1) N++;
							if(V % 2 == 1) N++;
							if(W % 2 == 1) N++;
								
							if(N == 1 || N == 3) {
								
								final int msq = m * m;
								final int Usq = U * U;
								final int Vsq = V * V;
								final int Wsq = W * W;
																	
								final int S = msq + Usq + Vsq + Wsq;
									
								if(S <= maxSigma) {
									
									
									
									final int UV = 2 * U * V;
									final int UW = 2 * U * W;
									final int VW = 2 * V * W;
									
									final int mU = 2 * m * U;
									final int mV = 2 * m * V;
									final int mW = 2 * m * W;
									
									
									
									final Matrix3x3 M = new Matrix3x3(
										(double)(msq + Usq - Vsq - Wsq) / (double)S, (double)(UV - mW) / (double)S, (double)(UW + mV) / (double)S,
										(double)(UV + mW) / (double)S, (double)(msq - Usq + Vsq - Wsq) / (double)S, (double)(VW - mU) / (double)S,
										(double)(UW - mV) / (double)S, (double)(VW + mU) / (double)S, (double)(msq - Usq - Vsq + Wsq) / (double)S
									);
									CSL.add(new CSLMisor(M, S));
									
									AxisAngle aa = new AxisAngle();
									aa.set(M);
								//	System.out.println("CSL: " + S + " " + U + " " + V + " " + W + " " + Math.toDegrees(aa.angle()));
								}
							}
						}						
					}
				}
			}			
		}	
	//	for(CSLMisor m : CSL) System.out.println(m.getSigma());
		Collections.sort(CSL);
		CSLMisor[] arrM = new CSLMisor[CSL.size()]; 
		
	//	for(CSLMisor m : CSL) System.out.println(m.getSigma());
		return CSL.toArray(arrM);
	}
	
	
	public static CSLMisor[] getForHexagonal(int maxSigma, int mu, int nu) {
		
		//c^2 = mu, a^2 = nu
		
		final Matrix3x3 S = Transformations.getHexToCartesian(Math.sqrt(nu), Math.sqrt(mu));
				
		final ArrayList<CSLMisor> CSL = new ArrayList<CSLMisor>();
		
		CSL.add(new CSLMisor(new Matrix3x3(), 1));

		for(int m = 1; m <= maxSigma; m++) {
			
			final int maxU = (int) Math.round( Math.sqrt(4d*mu/nu) * m );
			
			for(int U = 0; U <= maxU; U++) {
				
				final int maxV = U / 2;
				
				for(int V = 0; V <= maxV; V++) {
					
					final int maxW = (int) Math.round( m / (2d/Math.sqrt(3d) + 1d));
					
					for(int W = 0; W <= maxW; W++) {

						if(U != 0 || V != 0 || W != 0)
						if(ArithmeticUtils.gcd(m, ArithmeticUtils.gcd(U, ArithmeticUtils.gcd(V,W))) == 1) 
							
							if( m >= Math.sqrt( nu / (4d*mu) ) * U )									
							if( m >= Math.sqrt( nu / (12d*mu) ) * (2d*U - V) )										
							if( m >= (2d/Math.sqrt(3d) + 1d) * W)											
						{
						
								
							if(Math.abs( m - Math.sqrt( nu/(4d*mu) ) * U ) < EPS) {						
								if( W > Math.sqrt( nu/(4d*mu) ) * (2d*U - V) ) continue;
							}
																									
							if(Math.abs( m - Math.sqrt( nu/(12d*mu) ) * (2d*U - V)) < EPS) {
								if( W > Math.sqrt( 3d*nu/(4d*mu) ) * V ) continue;
							}
								
							if(Math.abs( m - (2d / Math.sqrt(3d) + 1d) * W) < EPS) {
								if( U < (2d + Math.sqrt(3d) * V) ) continue;
							}			
								
							
							int F = mu*(3*m*m + W*W) + nu*(U*U - U*V + V*V);
							int F1 = ArithmeticUtils.gcd(2, ArithmeticUtils.gcd(U, ArithmeticUtils.gcd(V, m+W)));
							int F2 = ArithmeticUtils.gcd(3, ArithmeticUtils.gcd(U+V, W));
							int F3 = ArithmeticUtils.gcd(2/F1, ArithmeticUtils.gcd(nu,m+W));
							int F4 = ArithmeticUtils.gcd(nu/F3, ArithmeticUtils.gcd(2*W/(F1*F2), m+W));
							int F5 = ArithmeticUtils.gcd(mu, ArithmeticUtils.gcd(3*U/(F1*F2),(U+V)/F1));
							
							int sigma = F/(F1*F1*F2*F3*F4*F5);
							
							if(sigma <= maxSigma) {
								
								final double theta = 2d * FastMath.atan(Math.sqrt((double)(nu*(U*U - U*V + V*V) + mu*W*W) / (3*mu*m*m)));
								
								final MillerIndices hexAxis = new MillerIndices();
								hexAxis.set(U, V, W);
								
								UnitVector axis = new UnitVector();
								axis.setAsNonCubicAxis(hexAxis, S);
								
								
								AxisAngle aa = new AxisAngle();
								aa.set(axis, theta);
						
								Matrix3x3 M = new Matrix3x3();
								M.set(aa);
								
								CSL.add(new CSLMisor(M, sigma));	
							}
						}			
					}	
				}	
			}		
		}
	
		Collections.sort(CSL);
		CSLMisor[] arrM = new CSLMisor[CSL.size()]; 
		return CSL.toArray(arrM);
	}

}
