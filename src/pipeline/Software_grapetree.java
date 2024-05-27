package pipeline;

import centralData.CentralData;

public class Software_grapetree {
	private SoftwareConfigurations runConfigurations;
	private String software = "grapetree";
	
	public Software_grapetree() {
		this.runConfigurations = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
	}
	
	public String getSoftware() {
		return software;
	}

}
