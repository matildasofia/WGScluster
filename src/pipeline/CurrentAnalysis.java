package pipeline;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.yaml.snakeyaml.Yaml;
import centralData.CentralData;
import data.DataHandler;
import tools.Tools;


public class CurrentAnalysis {

 	private RunPipeline runPipeline;
 	private Map<String, Object> runConfigurations;
 	private List<String> processClasses;
 	private DataHandler inputData;

	public CurrentAnalysis() {
		
	}
	
	public void runCurrentAnalysis() {
		
		createAnalysisDir();
		setProcessClasses();
		
		runPipeline = new RunPipeline();
		runPipeline.run();
		
	}
	
	// Setters
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	/**
	 * Gets information from configuration file and sets configuration variables, as well as creating analysis directory.
	 */
	public void setRunConfigurations() {
		
		Yaml yaml = new Yaml();
		try (InputStream inputStream = new FileInputStream(CentralData.INSTANCE.dirs.getConfigPath())) {
			
            runConfigurations = yaml.load(inputStream);
            
        } catch (Exception e) {
        	
            e.printStackTrace();
        }

	}
	
	/**
	 * Specifies a Map with the class of software for each process.
	 */
	private void setProcessClasses() {
		
		processClasses = new ArrayList<>(runConfigurations.keySet());
		
		if (processClasses.isEmpty()) {
			
			CentralData.INSTANCE.log.warning("No name found for analysis");
			
//			throw new IllegalArgumentException("Classes not found in configuration file");
		}
	}
	
	
	// Getters
	////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public String getName() {
		Object nameObj = runConfigurations.get("name");
		if (nameObj instanceof String) {
			return (String) nameObj;
		}
		
		CentralData.INSTANCE.log.warning("No name found for analysis");
		
		return null;
//		throw new IllegalArgumentException("No name found for analysis");
	}
	
	
	/**
	 * Runs grapetree TODO: should be somewhere else.
	 */
//	public void runVisualization() {
//		
//		runPipeline.runGrapeTree();
//		
//	}
	
	
	public List<String> getProcessClasses() {
		return processClasses;
	}

	/**
	 * Gets the configurations for the process specified and checks cast.
	 * @param software the class searched for.
	 * @return configurations for software.
	 */
	@SuppressWarnings("unchecked")
	public SoftwareConfigurations getSoftwareConfigurations(String software) {
		
		if(runConfigurations.get(software) instanceof Map) {
			
			Map<?, ?> configurationMap = (Map<?, ?>) runConfigurations.get(software);
			
			if(isMapOfStringAndObject(configurationMap)) {
				
				SoftwareConfigurations softwareConfigurations = new SoftwareConfigurations((Map<String, Object>) configurationMap);
				
				return softwareConfigurations;
			}
		}
		
		CentralData.INSTANCE.log.warning("No configuration found for software: " + software);
		
		return null;
	
//		throw new IllegalArgumentException("No configuration found for software: " + software);
	}
	
	/**
	 * Checks if type of object is Map<String, Object>
	 * @param map that is checked
	 * @return true if object type is Map<String, Object>, else false.
	 */
	@SuppressWarnings("unchecked")
	public List<String> getContainerBaseCommands() {
		if(runConfigurations.get("containerBaseCommands") instanceof List) {
			
			List<?> contBaseCommands = (List<?>) runConfigurations.get("containerBaseCommands");
			
			if(isListOfStrings(contBaseCommands)) {
				return (List<String>) contBaseCommands;
			}
		}
//		throw new IllegalArgumentException("No container base commands found for run");
		CentralData.INSTANCE.log.warning("No container base commands found for run");
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getKillGrapeTree() {
		
		String key = "killGrapeTreeContainer";
		
		if(runConfigurations.get(key) instanceof List) {
			
			List<?> killCmds = (List<?>) runConfigurations.get(key);
			
			if(isListOfStrings(killCmds)) {
				return (List<String>) killCmds;
			}
		}
		
		CentralData.INSTANCE.log.warning("No commands for killing container");
		
		return null;
	}
	
	
	private boolean isMapOfStringAndObject(Map<?, ?> map) {
	    for (Object key : map.keySet()) {
	        if (!(key instanceof String) || !(map.get(key) instanceof Object) && !(map.get(key) == null)) {
	            return false;
	        }
	    }
	    return true;
	}
	
	
	private boolean isListOfStrings(List<?> list) {
		
		for(Object element : list) {
			
			if(!(element instanceof String)) {
				return false;
			}	
				
		}
		return true;
	}
	
	
	public String getContainerBaseDir() {
		if(runConfigurations.get("containerBaseDir") instanceof String) {
			
			return (String) runConfigurations.get("containerBaseDir");
			
		}
		
		CentralData.INSTANCE.log.warning("No container base directory found for run in configuration file");
		return "";
	}
	
	public String getCommandScriptName() {
		if(runConfigurations.get("commandScriptName") instanceof String) {
			
			return (String) runConfigurations.get("commandScriptName");
		}
		
		CentralData.INSTANCE.log.warning("No command script found for run in configuration file");
		return "";
	}
	
	public String getFileListName() {
		if(runConfigurations.get("fileListName") instanceof String) {
			
			return (String) runConfigurations.get("fileListName");
		}
		
		CentralData.INSTANCE.log.warning("No file list found for run in configuration file");
		return "";
	}
	
	public String getPairedFileList() {
		if(runConfigurations.get("pairedFile") instanceof String) {
			
			return (String) runConfigurations.get("pairedFile");
		}
		
		CentralData.INSTANCE.log.warning("No paired file list found for run in configuration file");
		return "";
		
	}
	
	public DataHandler getinputData() {
		
		return inputData;
	
	}
	
	private void createAnalysisDir() {

		String baseDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
		
		File dir = new File(baseDir);
		
		if (!dir.mkdirs()) {
			if (Tools.MakeSureDirExists(baseDir)) {
				System.out.println("Process directory already exists: " + dir.getAbsolutePath());
			}
			else {
				System.out.println("Failed to create directory: " + dir.getAbsolutePath());
			}
			
		}
		
		CentralData.INSTANCE.dirs.setCurrentAnalysisPath(dir.getAbsolutePath());
		
	}
	
	public Map<String, Object> getRunConfigurations() {
		return runConfigurations;
	}
	
	public void killContainer() {
		RunDocker docker = new RunDocker("grapetree");
		docker.killContainer();
	}
	
	
	
}