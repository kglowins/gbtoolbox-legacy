package utils;



public class UnitVector  {

	
    private static final double ONEPI = Math.PI;
    private static final double TWOPI = 2d*ONEPI;
    
    private static final double EPS = 1e-5d;
    
	
	private double x;
	
	
	private double y;
	
	
	private double z;
	
	
	public final double x() {
		return x;
	}
	
	
	public final double y() {
		return y;
	}
	
	
	public final double z() {
		return z;
	}
	
	
	public UnitVector() {
    	
    	x = 0d;
    	y = 0d;
    	z = 1d;
    }

	
    public UnitVector(UnitVector other) {
    	
    	x = other.x();
    	y = other.y();
    	z = other.z();
    }
       
    
	public final void set(double x, double y, double z) {
		
		final double normSq = x*x + y*y + z*z;
		
		if(Math.abs(normSq) < EPS) {

			throw new IllegalArgumentException("A unit direction cannot be represented by a zero vector.");
			
		} else {			
					
			this.x = x;
			this.y = y;
			this.z = z;
						
			final double norm = Math.sqrt(normSq);
			
			if(Math.abs(norm - 1d) > EPS) {
				this.x = x / norm;
				this.y = y / norm;
				this.z = z / norm;
			}
		}
	}
	
	
	public final void set(double zenith, double azimuth) {
		
		double Z = zenith;
		double A = azimuth;
		 		
		
		if(Math.abs(A) >= TWOPI) A = Math.IEEEremainder(A, TWOPI);
		if(A < 0d) A += TWOPI;
		
		if(Math.abs(Z) >= TWOPI) Z = Math.IEEEremainder(Z, TWOPI);
		
		if(Z < 0d) {
			Z = -Z;
			A += ONEPI;
			if(A >= TWOPI) A = Math.IEEEremainder(A, TWOPI);
		}
		
		if(Z > ONEPI) {
			Z = TWOPI - Z;
			A += ONEPI;
			if(A >= TWOPI) A = Math.IEEEremainder(A, TWOPI);
		}
		
		
		final double S = Math.sin(Z);
		x = Math.cos(A) * S;
		y = Math.sin(A) * S;
		z = Math.cos(Z);	
		
	}
	
	
	public final double zenith() {		
		
		return MyMath.acos(z);
	}
	
	
	public final double azimuth() {
				
		return MyMath.atan2(y,  x);
	}
		

    public final void set(UnitVector other) {
    	
    	x = other.x();
    	y = other.y();
    	z = other.z();
    }
    
    
    public final void cross(UnitVector other) {
    	
    	final double nx = y*other.z() - z*other.y();
    	final double ny = z*other.x() - x*other.z();
    	final double nz = x*other.y() - y*other.x();
    	
    	set(nx, ny, nz);
    }
		        
       
    public final double dot(UnitVector other) {   	
    	return x*other.x() + y*other.y() + z*other.z();
    }

    
    public final double normSquared() {    	
    	return x*x + y*y + z*z;
    }
    
    
    public final double norm() {    	
    	return Math.sqrt(x*x + y*y + z*z);
    }
    
    
    public final void times(double lambda) {
    	x *= lambda;
    	y *= lambda;
    	z *= lambda;
    }
    
    
    public final void transform(Matrix3x3 matrix) {
    	
    	final double nx = matrix.e00()* x + matrix.e01()* y + matrix.e02()* z;
    	final double ny = matrix.e10()* x + matrix.e11()* y + matrix.e12()* z;
    	final double nz = matrix.e20()* x + matrix.e21()* y + matrix.e22()* z;
    	    	
    	x = nx;
    	y = ny;
    	z = nz;
    	
    	final double norm = Math.sqrt(x*x + y*y + z*z);    
    	
    	if(Math.abs(norm - 1d) > EPS) {
    		x /= norm;
    		y /= norm;
    		z /= norm;
    	}    
    }
    
    
    public final void transposedTransform(Matrix3x3 matrix) {
    	
    	final double nx = matrix.e00()* x + matrix.e10()* y + matrix.e20()* z;
    	final double ny = matrix.e01()* x + matrix.e11()* y + matrix.e21()* z;
    	final double nz = matrix.e02()* x + matrix.e12()* y + matrix.e22()* z;
    	    	
    	x = nx;
    	y = ny;
    	z = nz;
    	
    	final double norm = Math.sqrt(x*x + y*y + z*z);    
    	
    	if(Math.abs(norm - 1d) > EPS) {
    		x /= norm;
    		y /= norm;
    		z /= norm;
    	}    
    }
            
    
    public final void negate() {
    	
    	x = -x;
    	y = -y;
    	z = -z;
    }
    
	public final void setAsHexAxis4to3(MillerIndices mill, Matrix3x3 basis) {
		
		final double U =  mill.h();
		final double V =  mill.k();
		final double T = -(U + V);
		final double W =  mill.l();
				
		set(U - T, V - T, W);
		transform(basis);
	}   
           
    public final void setAsNonCubicAxis(MillerIndices indices, Matrix3x3 basis) {
    	 		
    	set(indices.h(), indices.k(), indices.l());
    	transform(basis);
    }
            
    
    public final void setAsHexagonalPlane(MillerIndices indices, double a, double c) {
    	    	
		
		//final double sqrt3div2 = 0.5d * Math.sqrt(3d);
		//final double ac = a * c;
		//final double hac = indices.h() * ac;
			
		//set(sqrt3div2 * hac, 0.5d*hac + ac*indices.k(), sqrt3div2 * a * a * indices.l());
    	
   
		set( indices.h() / a,
			(indices.h() + 2d*indices.k() ) / Math.sqrt(3d) / a,
			indices.l() / c);
		
		System.out.println(this);
    }
    
    public final void setAsTetragonalPlane(MillerIndices indices, double a, double c) {
    	
    //	final double ac = a * c;
    	set(indices.h() / a, indices.k() / a, indices.l() / c);
    }

    public final void setAsOrthorombicPlane(MillerIndices indices, double a, double b, double c) {
    	set(indices.h() / a, indices.k() / b, indices.l() / c);
    }
    
        
    @Override
    public String toString() {
    	return "[" + x + ", " + y + ", " + z+"]";
    }
    
   
 
}
