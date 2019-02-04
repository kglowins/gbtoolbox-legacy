package utils;

import org.apache.commons.math3.util.FastMath;




public class MyMath {
	
	private static final double ONEPI = Math.PI;
	private static final double TWOPI = 2d * Math.PI;
	    
    public static double sqrt(double arg) {
    	    	
    	if (arg < 0d) return 0d; else return Math.sqrt(arg);
    }
    
    
    public static double acos(double arg) {
    	
    	if (arg > 1d) return 0d;
    	else if (arg < -1d) return ONEPI;    	
    	else return FastMath.acos(arg);
    }

    
    public static double atan2(double y, double x) {
    	
    	double angle = FastMath.atan2(y, x);
    	if(angle < 0d) return TWOPI + angle; else return angle;
    }    
    
        
    public static double atan(double arg) {
    	
    	double angle = FastMath.atan(arg);
    	if(angle < 0d) return ONEPI + angle; else return angle;
    }    
    
    
    public static int gcd(int x, int y) {
        while (y != 0) {
            int temp = y;
            y = x % y;
            x = temp;
        }
        return x;
    }
    

}
