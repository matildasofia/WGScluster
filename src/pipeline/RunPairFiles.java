package pipeline;

import java.io.File;

import centralData.CentralData;

public class RunPairFiles {
	
	private static String software = "pairfiles";
	
	public RunPairFiles() {
		
	}
	
	public static void run() {
		
		String fileListName;
		String currentProcessDir;
		
		fileListName = CentralData.INSTANCE.currentAnalysis.getFileListName();
		
		if (!CentralData.INSTANCE.dirs.getCurrentProcessPath().isEmpty()) {
			
			currentProcessDir = CentralData.INSTANCE.dirs.getCurrentProcessPath();
			
		}
		else {
			currentProcessDir = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
		}
		
		File fileList = new File(currentProcessDir, fileListName);
		
		if(!fileList.exists()) {
			
//			File dir;
//			String inputDir;
//			FileList filelist;
//			DataHandler inputData;
//	        List<File> files;
//	        
//	        inputDir = currentProcessDir
//			
//			dir = new File(inputDir);
//	        files = Arrays.asList(dir.listFiles());
//	        
//	        inputData = new DataHandler();
//	        filelist = new FileList();
//	        
//	        inputData.setData(files);
//	        filelist.setData(inputData);
//	        filelist.makeFileList(inputDir);
			
			CentralData.INSTANCE.log.warning("No filelist exist, attempting to create");
			CentralData.INSTANCE.inputData.createFileList();
		}
		
		RunDocker runPairFile = new RunDocker(software);
		runPairFile.runContainer();
	}

}
