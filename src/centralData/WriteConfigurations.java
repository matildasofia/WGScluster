package centralData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import pipeline.SoftwareConfigurations;
import tools.Tools;

public class WriteConfigurations {
	
	private Map<String, Object> configurations;
	private Map<String, Object> newConfigurations;
	
	public WriteConfigurations() {
		
	}
	
	public String writeToFile() {
		
		String configPath;
		String currentAnalysisPath;
		String newPath;
		File file;
		
		configPath = CentralData.INSTANCE.dirs.getConfigPath();
		currentAnalysisPath = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
		newPath = Tools.mergePaths(currentAnalysisPath, Tools.getLastPartOfPath(configPath));
		
		file = new File(newPath);

		correctOrder();		
		
		DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
		
		Yaml yaml = new Yaml(options);
		try (FileWriter writer = new FileWriter(file)) {
            yaml.dump(newConfigurations, writer);
            
        } catch (IOException e) {
            e.printStackTrace();
        }
		
		return newPath;
		
	}
	
	public void setConfigs() {
		
		Yaml yaml = new Yaml();
		String configpath = CentralData.INSTANCE.dirs.getAnalysesPath();
		configpath = Tools.mergePaths(configpath, "config.yml");
		
		try (InputStream inputStream = new FileInputStream(configpath)) {
			
            configurations = yaml.load(inputStream);
            newConfigurations = deepCopy(configurations);
            
        } catch (Exception e) {
        	
            e.printStackTrace();
        }
		
	}
	
	public void setTrimmingConfigurations(List<String> settings, String software) {
		
		SoftwareConfigurations allSettings;
		Map<String, Object> newSettings;
		
		if (isChanged(software)) {
			
			restart(software);
			
		}
		
		allSettings = getSoftwareConfigurations(software);
		allSettings.setContainerAddOns(settings);
		
		newSettings = allSettings.getSoftwareConfigurations();
		newSettings = deepCopy(newSettings);
		
		newConfigurations.put(software, newSettings);

	}
	
	public void setConfigurations(List<String> settings, String software) {
		
		SoftwareConfigurations allSettings;
		Map<String, Object> newSettings;
		
		if (isChanged(software)) {
			
			restart(software);
			
		}
		
		allSettings = getSoftwareConfigurations(software);
		allSettings.setFlags(settings);
		
		newSettings = allSettings.getSoftwareConfigurations();
		
		newConfigurations.put(software, newSettings);
		
	}
	
	public void setStartTime() {
		
		newConfigurations.put("created", Tools.stamptime());

		
	}
	
	public void setEndTime() {
		
		newConfigurations.put("finalized", Tools.stamptime());
		
	}
	
	private SoftwareConfigurations getSoftwareConfigurations(String software) {
		
		if(configurations.get(software) instanceof Map) {
			
			Map<?, ?> configurationMap = (Map<?, ?>) configurations.get(software);
			
			if(Tools.isMapOfStringAndObject(configurationMap)) {
				
				@SuppressWarnings("unchecked")
				Map<String, Object> copy = deepCopy((Map<String, Object>) configurationMap);
				SoftwareConfigurations softwareConfigurations = new SoftwareConfigurations(copy);
				
				return softwareConfigurations;
			}
		}
		throw new IllegalArgumentException("No configuration found for software: " + software);
	}
	
	public void restart(String software) {
		
		Map<String, Object> copiedConfigurations = deepCopy(configurations);
		
		Object value;
		value = copiedConfigurations.get(software);
		
		newConfigurations.put(software, value);
		
	}
	
	public boolean isChanged(String software) {
		Object softwareConfig = newConfigurations.get(software);
		
		if ( softwareConfig == null ) {
			
			return true;
		}
		
		if ( configurations.get(software).equals(softwareConfig) ) {
			
			return false;
		}
		
		return true;
	}
	
	public void remove(String software) {
		
		newConfigurations.remove(software);
		
	}
	
    private void correctOrder() {
        if (configurations == null || newConfigurations == null) {
            throw new IllegalArgumentException("Maps cannot be null");
        }

        LinkedHashMap<String, Object> orderedMap = new LinkedHashMap<>();

        for (String key : configurations.keySet()) {

        	if (newConfigurations.containsKey(key)) {
                orderedMap.put(key, newConfigurations.get(key));
            }
        }

        newConfigurations = orderedMap;
    }
	
	public boolean choicesMade() {
		
		if (configurations.equals(newConfigurations)) {
			
			return false;
		}
		
		return true;
		
	}
	
	@SuppressWarnings("unchecked")
	public static Map<String, Object> deepCopy(Map<String, Object> original) {
	    Map<String, Object> copy = new HashMap<>();

	    for (Map.Entry<String, Object> entry : original.entrySet()) {
	        String key = entry.getKey();
	        Object value = entry.getValue();

	        if (value instanceof Map) {
	            
	            copy.put(key, deepCopy((Map<String, Object>) value));
	        } else if (value instanceof List) {
	            
	            copy.put(key, deepCopyList((List<Object>) value));
	        } else if (value instanceof Cloneable) {
	            
	            try {
	                Object clonedValue = value.getClass().getMethod("clone").invoke(value);
	                copy.put(key, clonedValue);
	            } catch (Exception e) {
	                
	                copy.put(key, value);
	            }
	        } else {
	            
	            copy.put(key, value);
	        }
	    }
	    return copy;
	}
	
	@SuppressWarnings("unchecked")
	public static List<Object> deepCopyList(List<Object> original) {
	    List<Object> copy = new ArrayList<>();

	    for (Object item : original) {
	        if (item instanceof Map) {
	            
	            copy.add(deepCopy((Map<String, Object>) item));
	        } else if (item instanceof List) {
	            
	            copy.add(deepCopyList((List<Object>) item));
	        } else if (item instanceof Cloneable) {
	            
	            try {
	                Object clonedItem = item.getClass().getMethod("clone").invoke(item);
	                copy.add(clonedItem);
	            } catch (Exception e) {
	                
	                copy.add(item);
	            }
	        } else {
	            
	            copy.add(item);
	        }
	    }

	    return copy;
	}
	
	
	
	public List<String> getContOptions(String software) {
		
		SoftwareConfigurations softwareConfig;
		
		softwareConfig = getSoftwareConfigurations(software);
		
		return softwareConfig.getContainerAddOns();
		
	}
	
	public List<String> getOptions(String software) {
		
		SoftwareConfigurations softwareConfig;
		
		softwareConfig = getSoftwareConfigurations(software);
		
		return softwareConfig.getFlags();
		
	}

}
