package utils;

public class CSLMisor implements Comparable<CSLMisor> {
	
	private Matrix3x3 M;
	
	private int sigma;
	
	
	public CSLMisor(Matrix3x3 M, int sigma) {
		this.sigma = sigma;
		this.M = M;
	}


	public Matrix3x3 getMatrix() {
		return M;
	}


	public int getSigma() {
		return sigma;
	}


	@Override
	public int compareTo(CSLMisor other) {

		return new Integer(sigma).compareTo(new Integer(other.getSigma()));
	}
	
	

}
