package utils;

import enums.PointGroup;

public class Transformations {
	

	public final static Matrix3x3 getHexToCartesian(double a, double c) {
		
		return new Matrix3x3(a, -0.5d*a, 0d,
							 0d, 0.5d*Math.sqrt(3d)*a, 0d,
							 0d, 0d, c);
	}
	
	public final static Matrix3x3 getCartesianToHex(double a, double c) {
		
		return new Matrix3x3(1d/a, 1d/Math.sqrt(3d)/a, 0d,
							 0d, 2d/Math.sqrt(3d)/a, 0d,
							 0d, 0d, 1d/c);
	}
	
	public final static Matrix3x3 getTetrToCartesian(double a, double c) {
		return new Matrix3x3(a, 0d, 0d,
							 0d, a, 0d,
							 0d, 0d, c);
	}
	
	public final static Matrix3x3 getCartesianToTetr(double a, double c) {
		return new Matrix3x3(1/a, 0d, 0d,
				 			 0d, 1/a, 0d,
				 			 0d, 0d, 1/c);
	}
	
	public final static Matrix3x3 getOrthToCartesian(double a, double b, double c) {
		return new Matrix3x3(a, 0d, 0d,
				 			 0d, b, 0d,
				 			 0d, 0d, c);
	}
	
	public final static Matrix3x3 getCartesianToOrth(double a, double b, double c) {
		return new Matrix3x3(1/a, 0d, 0d,
				 			 0d, 1/b, 0d,
				 			 0d, 0d, 1/c);
	}
		
			
	public final static Matrix3x3[] getSymmetryTransformations(PointGroup ptGrp) {
		
		Matrix3x3[] C;
		
		switch(ptGrp) {
		
		case M3M: //cubic
			C = new Matrix3x3[24];
			
			C[0] = new Matrix3x3(1d, 0d, 0d,
					 			0d, 1d, 0d,
					 			0d, 0d, 1d);
				
					
			C[1] = new Matrix3x3(1d, 0d, 0d,
					 			0d, -1d, 0d,
					 			0d, 0d, -1d);
			
			C[2] = new Matrix3x3(-1d, 0d, 0d,
					 			0d, 1d, 0d,
					 			0d, 0d, -1d);
			
			C[3] = new Matrix3x3(-1d, 0d, 0d,
								0d, -1d, 0d,
								0d, 0d, 1d);
			
			C[4] = new Matrix3x3(0d, 0d, 1d,
								1d, 0d, 0d,
								0d, 1d, 0d);
			
			C[5] = new Matrix3x3(0d, 1d, 0d,
								0d, 0d, -1d,
								-1d, 0d, 0d);
			
			C[6] = new Matrix3x3(0d, -1d, 0d,
								0d, 0d, -1d,
								1d, 0d, 0d);
			
			C[7] = new Matrix3x3(0d, -1d, 0d,
								0d, 0d, 1d,
								-1d, 0d, 0d);
			
			C[8] = new Matrix3x3(1d, 0d, 0d,
								0d, 0d, -1d,
								0d, 1d, 0d);
			
			C[9] = new Matrix3x3(0d, 0d, 1d,
								 0d, 1d, 0d,
								 -1d, 0d, 0d);
				
			C[10] = new Matrix3x3(0d, -1d, 0d,
								  1d, 0d, 0d,
								  0d, 0d, 1d);
			
			C[11] = new Matrix3x3(1d, 0d, 0d,
								  0d, 0d, 1d,
								  0d, -1d, 0d);
				
			C[12] = new Matrix3x3(0d, 0d, -1d,
								  0d, 1d, 0d,
								  1d, 0d, 0d);
			
			C[13] = new Matrix3x3(0d, 1d, 0d,
					  			 -1d, 0d, 0d,
					  			 0d, 0d, 1d);
			
			C[14] = new Matrix3x3(0d, 1d, 0d,
								  0d, 0d, 1d,
								  1d, 0d, 0d);
				
			C[15] = new Matrix3x3(0d, 0d, -1d,
								  1d, 0d, 0d,
								  0d, -1d, 0d);
				
			C[16] = new Matrix3x3(0d, 0d, 1d,
					  			 -1d, 0d, 0d,
					  			 0d, -1d, 0d);
				
			C[17] = new Matrix3x3(0d, 0d, -1d,
								 -1d, 0d, 0d,
								 0d, 1d, 0d);
				
			C[18] = new Matrix3x3(0d, 1d, 0d,
					  			  1d, 0d, 0d,
					  			  0d, 0d, -1d);
			
			C[19] = new Matrix3x3(0d, 0d, 1d,
					  			  0d, -1d, 0d,
					  			  1d, 0d, 0d);
			
			C[20] = new Matrix3x3(-1d, 0d, 0d,
					  			  0d, 0d, 1d,
					  			  0d, 1d, 0d);
			
			C[21] = new Matrix3x3(0d, -1d, 0d,
					  			 -1d, 0d, 0d,
					  			 0d, 0d, -1d);
			
			C[22] = new Matrix3x3(0d, 0d, -1d,
					  			  0d, -1d, 0d,
					  			  -1d, 0d, 0d);
			
			C[23] = new Matrix3x3(-1d, 0d, 0d,
					  			  0d, 0d, -1d,
					  			  0d, -1d, 0d);
		
			break;
			
			
			
		case _6MMM: //hexagonal
			
			C = new Matrix3x3[12];
			
			C[0] = new Matrix3x3(1d, 0d, 0d,
					 			 0d, 1d, 0d,
					 			 0d, 0d, 1d);
		 
			
			C[1] = new Matrix3x3(0.5d, 0.5d * Math.sqrt(3d), 0d,
								-0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, 1d);
		 
		    
		    C[2] = new Matrix3x3(1d, 0d, 0d,
					 			 0d, -1d, 0d,
					 			 0d, 0d, -1d);
		 
		    
		    C[3] = new Matrix3x3(-0.5d, 0.5d * Math.sqrt(3d), 0d,
								 -0.5d * Math.sqrt(3d), -0.5d, 0d,
								 0d, 0d, 1d);
		 
		    
		    C[4] = new Matrix3x3(0.5d, -0.5d * Math.sqrt(3d), 0d,
								 -0.5d * Math.sqrt(3d), -0.5d, 0d,
								 0d, 0d, -1d);
		 
		    
		    C[5] = new Matrix3x3(0.5d, 0.5d * Math.sqrt(3d), 0d,
		    					 0.5d * Math.sqrt(3d), -0.5d, 0d,
					 			 0d, 0d, -1d);
		 
		   
			C[6] = new Matrix3x3(-1d, 0d, 0d,
					 			 0d, -1d, 0d,
					 			 0d, 0d, 1d);
		    
			
		    C[7] = new Matrix3x3(-0.5d, -0.5d * Math.sqrt(3d), 0d,
								 -0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, -1d);
			
			
		    C[8] = new Matrix3x3(-0.5d, 0.5d * Math.sqrt(3d), 0d,
		    					 0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, -1d);
		 
		   
			C[9] = new Matrix3x3(0.5d, -0.5d * Math.sqrt(3d), 0d,
								 0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, 1d);
		 
		   
		    C[10] = new Matrix3x3(-0.5d, -0.5d * Math.sqrt(3d), 0d,
		    					  0.5d * Math.sqrt(3d), -0.5d, 0d,
								  0d, 0d, 1d);
		 
		    
		    C[11] = new Matrix3x3(-1d, 0d, 0d,
								  0d, 1d, 0d,
								  0d, 0d, -1d);

			break;
			
		case _4MMM: //tetragonal
			
			C = new Matrix3x3[8];
			
			C[0] = new Matrix3x3(1d, 0d, 0d,
								 0d, 1d, 0d,
								 0d, 0d, 1d);
		    
			C[1] = new Matrix3x3(0d, 1d, 0d,
								-1d, 0d, 0d,
						 		 0d, 0d, 1d);
		 
		    C[2] = new Matrix3x3(1d, 0d, 0d,
		    					 0d, -1d, 0d,
		    					 0d, 0d, -1d);
		 
		    C[3] = new Matrix3x3(-1d, 0d, 0d,
		    					 0d, -1d, 0d,
		    					 0d, 0d, 1d);
		 
		    C[4] = new Matrix3x3(0d, -1d, 0d,
		    					-1d, 0d, 0d,
		    					0d, 0d, -1d);
		 
		    C[5] = new Matrix3x3(0d, 1d, 0d,
		    					1d, 0d, 0d,
		    					0d, 0d, -1d);
		 
		    C[6] = new Matrix3x3(0d, -1d, 0d,
				   				1d, 0d, 0d,
				   				0d, 0d, 1d);
		 
		    C[7] = new Matrix3x3(-1d, 0d, 0d,
		    					 0d, 1d, 0d,
		    					 0d, 0d, -1d);
			
			break;
			
			
		case MMM: //orthorombic
			
			C = new Matrix3x3[4];
			
			C[0] = new Matrix3x3(1d, 0d, 0d,
								 0d, 1d, 0d,
								 0d, 0d, 1d);
			
			C[1] = new Matrix3x3(-1d, 0d, 0d,
				    			 0d, -1d, 0d,
				    			 0d, 0d, 1d	);
			
			C[2] = new Matrix3x3(1d, 0d, 0d,
					             0d, -1d, 0d,
								 0d, 0d, -1d);
			
			C[3] = new Matrix3x3(-1d, 0d, 0d,
							 	 0d, 1d, 0d,
							 	 0d, 0d, -1d);
			break;
			
			
		case _2M: //monoclinic
			
			C = new Matrix3x3[2];
						
			C[0] = new Matrix3x3(1d, 0d, 0d,
								 0d, 1d, 0d,
								 0d, 0d, 1d);
			
			C[1] = new Matrix3x3(-1d, 0d, 0d,
				    			 0d, 1d, 0d,
				    			 0d, 0d, -1d);

			break;
		
		
			
		case _3M: //trigonal
			C = new Matrix3x3[6];
			
			C[0] = new Matrix3x3(1d, 0d, 0d,
								 0d, 1d, 0d,
								 0d, 0d, 1d);
			 
			C[1] = new Matrix3x3(-0.5d, 0.5d * Math.sqrt(3d), 0d,
								 -0.5d * Math.sqrt(3d), -0.5d, 0d,
								 0d, 0d, 1d);
			 
			C[2] = new Matrix3x3(1d, 0d, 0d,
								 0d, -1d, 0d,
								 0d, 0d, -1d);
			 
			C[3] = new Matrix3x3(-0.5d, -0.5d * Math.sqrt(3d), 0d,
								 0.5d * Math.sqrt(3d), -0.5d, 0d,
								 0d, 0d, 1d);
			 
			C[4] = new Matrix3x3(-0.5d, -0.5d * Math.sqrt(3d), 0d,
								 -0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, -1d);
			 
			C[5] = new Matrix3x3(-0.5d, 0.5d * Math.sqrt(3d), 0d,
								 0.5d * Math.sqrt(3d), 0.5d, 0d,
								 0d, 0d, -1d);			
			
			break;
			
			
	
			
		default: //identity only
			C = new Matrix3x3[1];
			C[0] = new Matrix3x3();	
			break;			
		}
		
		return C;
	}
	

}
