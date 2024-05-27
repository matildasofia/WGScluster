package pipeline;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import centralData.CentralData;
import tools.Tools;


public class RunDocker {
	
	private String software;
	private SoftwareConfigurations softwareConfigurations;
	
	public RunDocker(String software) {
		this.software = software;
		this.softwareConfigurations = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
		
	}
	
	public void runContainer() {
		try {
			
			String[] command = configureArguments();
          
			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			
			
			//Read output from the process
			String line;
			while ((line = reader.readLine()) != null) {
              System.out.println("Process output: " + line);
			}
			
			int exitCode = process.waitFor();
			
			System.out.println("runContainer exited with code: " + exitCode);

		} catch (IOException | InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	private String[] configureArguments() {
				
		String imageName = softwareConfigurations.getImageName();
		List<String> baseFlags = CentralData.INSTANCE.currentAnalysis.getContainerBaseCommands();
		List<String> softwareSpecFlags = setCommandScript(softwareConfigurations.getContainerFlags());
		List<String> containerAddOns = softwareConfigurations.getContainerAddOns();
		
		System.out.println(imageName);
		
		List<String> combinedArguments = new ArrayList<>();
		
		combinedArguments.addAll(Tools.getEmptyList(baseFlags));
		combinedArguments.addAll(Tools.getEmptyList(softwareSpecFlags));
		combinedArguments.addAll(Tools.getEmptyList(setMounts()));
		combinedArguments.add(imageName);
		combinedArguments.addAll(Tools.getEmptyList(containerAddOns));
		
		System.out.println("Docker Command: " + String.join(" ", combinedArguments));
		
		String[] argumentsArray = combinedArguments.toArray(new String[0]);
		return argumentsArray;
		
	}
	
	private List<String> setMounts() {
		
		List<String> mounts = new ArrayList<String>();
		String inDir;
		String outDir;
		String dataBaseDir;
		
		switch(software) {
		
		case "pairfiles":
			
			if (!CentralData.INSTANCE.dirs.getCurrentProcessPath().isEmpty()) {
				
				inDir = outDir = CentralData.INSTANCE.dirs.getCurrentProcessPath();
			}
			
			else {
				
				inDir = CentralData.INSTANCE.dirs.getDataPath();
				outDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
			}
						
			mounts.add("-v");
			mounts.add(String.join(":",inDir, "/data"));
			mounts.add("-v");
			mounts.add(String.join(":", outDir, "/analysis"));
			
			break;
			
		case "fastp":
			
			inDir = CentralData.INSTANCE.dirs.getDataPath();
			outDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
			dataBaseDir = CentralData.INSTANCE.dirs.getKrakenPath();
			
			mounts.add("-v");
			mounts.add(String.join(":",inDir, "/data"));
			mounts.add("-v");
			mounts.add(String.join(":", outDir, "/analysis"));
			
			if(!dataBaseDir.isEmpty()) {
				mounts.add("-v");
				mounts.add(String.join(":", dataBaseDir, "/db"));
			}
			
			break;
			
		case "trimmomatic":
			
			inDir = CentralData.INSTANCE.dirs.getDataPath();
			outDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
			dataBaseDir = CentralData.INSTANCE.dirs.getKrakenPath();
			
			mounts.add("-v");
			mounts.add(String.join(":",inDir, "/data"));
			mounts.add("-v");
			mounts.add(String.join(":", outDir, "/analysis"));
			
			if(!dataBaseDir.isEmpty()) {
				mounts.add("-v");
				mounts.add(String.join(":", dataBaseDir, "/db"));
			}
			
			break;
		
		case "spades": 
			inDir = CentralData.INSTANCE.dirs.getTrimmedPath();
			outDir = CentralData.INSTANCE.dirs.getAssemblyPath();
			
			mounts.add("-v");
			mounts.add(inDir + ":" + setContainerTarget(inDir));
			mounts.add("-v");
			mounts.add(outDir + ":" + setContainerTarget(outDir));
			
			break;
		
		case "chewbbaca":
			
			inDir = CentralData.INSTANCE.dirs.getAssemblyPath();
			outDir = CentralData.INSTANCE.dirs.getAllelecallPath();
			dataBaseDir = CentralData.INSTANCE.dirs.getSchemaPath();
			
			mounts.add("-v");
			mounts.add(inDir + ":" + setContainerTarget(inDir));
			mounts.add("-v");
			mounts.add(outDir + ":" + setContainerTarget(outDir));
			mounts.add("-v");
			mounts.add(dataBaseDir + ":" + setContainerTarget(dataBaseDir));
			
			break;
			
		case "grapetree":
			
			inDir = CentralData.INSTANCE.dirs.getAllelecallPath();
			
			if (inDir.isEmpty()) {
				
				inDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
				inDir = Tools.mergePaths(inDir, "chewbbaca");
			}
			
			mounts.add("-v");
			mounts.add(String.join(":",inDir, "/data"));
			
			break;
			
		default:
			break;
			
		}
		
		return mounts;
		
	}
	
	private String setContainerTarget(String hostPath) {
		
		String containerBaseDir = CentralData.INSTANCE.currentAnalysis.getContainerBaseDir();
		String containerTarget = Paths.get(containerBaseDir, Tools.getLastPartOfPath(hostPath)).toString();
		
		return containerTarget;
		
	}
	
	private List<String> setCommandScript(List<String> flags) {
		
		String path = "";
		
		String parameter = "COMMAND_SCRIPT=";
		String containerBaseDir = CentralData.INSTANCE.currentAnalysis.getContainerBaseDir();
		String commandScript = CentralData.INSTANCE.currentAnalysis.getCommandScriptName();
		//String path = Paths.get(containerBaseDir, software, commandScript).toString();
		path = Tools.mergePaths(containerBaseDir, software);
		path = Tools.mergePaths(path, commandScript);
		
		if( flags == null || flags.isEmpty() ) {
			return flags;
		}
		
		if( software != null && flags.contains(parameter) ) {
			
			int index = flags.indexOf(parameter);
			
			String newValue = String.join("", parameter, path);
			
			flags.set(index,  newValue);
		}
		
		return flags;
		
	}
	
	public void killContainer() {
		
		try {
			
			List<String> lstCommand;
			String[] command;
			String strcommand;
			
			lstCommand = CentralData.INSTANCE.currentAnalysis.getKillGrapeTree();
			command = lstCommand.toArray(new String[0]);
			
			strcommand = String.join(" ", command);
			System.out.println("Kill command: " + strcommand);

			ProcessBuilder pb = new ProcessBuilder(command);
			Process process = pb.start();
			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));

			// Read output from the process
			String line;
			while ((line = reader.readLine()) != null) {
 
              CentralData.INSTANCE.log.log("Process output: " + line);
			}

			int exitCode = process.waitFor();
			
			CentralData.INSTANCE.log.log("killcontainer exited with code: " + exitCode);

		} catch (IOException | InterruptedException e) {
		e.printStackTrace();
		
		}
		
	}
	
	/**
	 * Checks if a container with a given containerName is running.
	 * @param containerName
	 * @return 
	 */
    public static boolean isRunning(String containerName) {
        boolean isRunning = false;
        
        try {
            Process process = new ProcessBuilder("docker", "inspect", "--format='{{.State.Running}}'", containerName).start();
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                isRunning = true;
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        return isRunning;
    }

}
