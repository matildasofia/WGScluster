package data;

import centralData.CentralData;

public class Pair {
	
	Data R1;
	Data R2;
	String basename;
	
	public Pair() {
		
	}
	
	public void setR1(String path) {
		R1 = new Data(path);
	}
	
	public void setR2(String path) {
		R2 = new Data(path);
	}
	
	public void setBaseName(String baseName) {
		basename = baseName;
	}
	
	public String getR1() {
		return R1.getAbsolutePath();
	}
	
	public String getR2() {
		return R2.getAbsolutePath();
	}
	
	public String getBaseName() {
		return basename;
	}
	
	public String toString() {
		String string = "";
		
		if(R1 != null && R2 != null) {
			string = "R1: " + R1.getAbsolutePath() + "\n" + "R2: " + R2.getAbsolutePath();
			
		}
		
		return string;
	}
	
	public boolean isPaired() {
		
		if(R1 == null || R2 == null) {
			return false;
		}
		
		return true;
	}
	
	

}
