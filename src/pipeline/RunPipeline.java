package pipeline;

import java.io.IOException;
import java.util.List;

import centralData.CentralData;
import tools.Tools;


public class RunPipeline {
	
	
	public RunPipeline() {


	}
	
	
	public void run() {
		
		String path;
		List<String> processes = CentralData.INSTANCE.currentAnalysis.getProcessClasses();
		
		System.out.println("Processes: " + processes);
		
		for (String software : processes) {
			
			switch (software) {
			
				case "fastp":
					
					path = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
					path = Tools.mergePaths(path, "/trimmed");
					CentralData.INSTANCE.dirs.setTrimmedPath(path);
					
					RunDocker runFastp = new RunDocker(software);
					runFastp.runContainer();
					
					CentralData.INSTANCE.dirs.setCurrentProcessPath(path);
					
					break;
					
				case "trimmomatic":
					
					path = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
					path = Tools.mergePaths(path, "/trimmed");
					CentralData.INSTANCE.dirs.setTrimmedPath(path);
					
					RunDocker runTrimmomatic = new RunDocker(software);
					runTrimmomatic.runContainer();
					
					CentralData.INSTANCE.dirs.setCurrentProcessPath(path);
					
					break;
					
				case "qc":
					break;
			
				case "spades":
					
					path = CentralData.INSTANCE.dirs.createProcessDir(software);
					CentralData.INSTANCE.dirs.setAssemblyPath(path);
					
					Software_spades spades = new Software_spades();
					spades.writeCommands();
					
					RunDocker runSpades = new RunDocker(software);
					runSpades.runContainer();
					
					CentralData.INSTANCE.dirs.setCurrentProcessPath(path);
					
					break;
					
				case "chewbbaca":
					
					path = CentralData.INSTANCE.dirs.createProcessDir(software);
					CentralData.INSTANCE.dirs.setAlleleCallPath(path);
					
					Software_chewbbaca chewbbaca = new Software_chewbbaca();
					try {
						chewbbaca.writeCommands();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
					
					RunDocker runChewbbaca = new RunDocker(software);
					runChewbbaca.runContainer();
					
					CentralData.INSTANCE.dirs.setCurrentProcessPath(path);
					
					break;
	
			}
		}
		
	}
//	
//	public void runGrapeTree() {
//		Map<String, String> mounts = new HashMap<>();
//		
//		Software_grapetree run = new Software_grapetree();
//		String software = run.getSoftware();
//		SoftwareConfigurations runConfigurations = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations("grapetree");
//		
//		RunDocker runDocker = new RunDocker(software);
//		runDocker.runContainer();
//	}
	
//	public String createProcessDir(String software) {
//		
//		String baseDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
//
//		String processPath = Paths.get(baseDir, software).toString();
//		
//		File dir = new File(processPath);
//		
//		if (!dir.mkdirs()) {
//			
//			if (Tools.MakeSureDirExists(processPath)) {
//				
//				System.out.println("Process directory already exists: " + dir.getAbsolutePath());
//			}
//			else {
//				System.out.println("Failed to create directory: " + dir.getAbsolutePath());
//			}
//			
//		}
//		System.out.println("Process: " + software);
//		
//		return processPath;
//		
//	}
	
//	public void setInputMount(Map<String, String> processMount, String path) {
//		if (processMount == null || !processMount.containsKey("in")) {
//			processMount.put("in", path);
//		}
//	}
//	
//	private void setOutputMount(Map<String, String> processMount, String path) {
//		if (processMount == null || !processMount.containsKey("out")) {
//			processMount.put("out", path);
//		}
//	}
//	
//	private void setOtherMount(Map<String, String> processMount, String path) {
//		if (processMount == null || !processMount.containsKey("other")) {
//			processMount.put("other", path);
//		}
//	}
	
//	public String getDirectoryFromPath(String _path) {
//		Path path = Paths.get(_path);
//		return path.getFileName().toString();
//	}
	
}
