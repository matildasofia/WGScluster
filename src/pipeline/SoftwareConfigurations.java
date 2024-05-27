package pipeline;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SoftwareConfigurations {
	
	private String version;
	private String imageName;
	private String commonName;
	private List<String> baseCommands;
	private List<String> flags;
	private List<String> containerFlags;
	private List<String> containerAddOns;
	
	private Map<String, Object> softwareConfigurations;
	
	@SuppressWarnings("unchecked")
	public SoftwareConfigurations( Map<String, Object> softwareConfigurations ) {
		
		this.softwareConfigurations = softwareConfigurations;
		
		for (Entry<String, Object> configuration : softwareConfigurations.entrySet()) {
			switch(configuration.getKey()) {
				
			case "version":
				version = configuration.getValue().toString();
				break;
				
			case "imageName":
				imageName = configuration.getValue().toString();
				break;

			case "commonName":
				commonName = configuration.getValue().toString();
				break;
				
			case "baseCommands":
				if (configuration.getValue() instanceof List) {
					List<?> listConfiguration = (List<?>) configuration.getValue();
					
					if(isListOfString(listConfiguration)) {
						baseCommands = (List<String>) configuration.getValue();
					}
					
				}
				
				break;

			case "flags":
				if (configuration.getValue() instanceof List) {
					List<?> listConfiguration = (List<?>) configuration.getValue();
					
					if(isListOfString(listConfiguration)) {
						flags = (List<String>) configuration.getValue();
					}
					
				}
				
				break;

			case "containerFlags":
				if (configuration.getValue() instanceof List) {
					List<?> listConfiguration = (List<?>) configuration.getValue();
					
					if(isListOfString(listConfiguration)) {
						containerFlags = (List<String>) configuration.getValue();
					}
					
				}
				
				break;
				
			case "containerAddOns":
				if (configuration.getValue() instanceof List) {
					List<?> listConfiguration = (List<?>) configuration.getValue();
					
					if(isListOfString(listConfiguration)) {
						containerAddOns = (List<String>) configuration.getValue();
					}
					
				}
			
			default:
				break;
				
			
			}
			
			
			
		}	
		
	}
	
	public String getVersion() {
		return version;
	}
	
	public String getImageName() {
		return imageName;
	}
	
	public String getCommonName() {
		return commonName;
	}
	
	public List<String> getBaseCommands() {
		return baseCommands;
	}
	
	public List<String> getFlags() {
		return flags;
	}
	
	public List<String> getContainerFlags() {
		return containerFlags;
	}
	
	public List<String> getContainerAddOns() {
		return containerAddOns;
	}
	
	private boolean isListOfString(List<?> configuration) {
		for(Object entry:configuration) {
			if(!(entry instanceof String)) {
				return false;
			}
		}
		return true;
		
	}
	
	public void setContainerAddOns(List<String> addOns) {
		String key = "containerAddOns";
		
		softwareConfigurations.put(key, addOns);
	}
	
	public void setFlags(List<String> flags) {
		String key = "flags";
		
		softwareConfigurations.put(key, flags);
	}
	
	public Map<String, Object> getSoftwareConfigurations() {
		return softwareConfigurations;
		
	}

}
