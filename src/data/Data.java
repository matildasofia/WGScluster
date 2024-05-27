package data;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;


import centralData.CentralData;
import tools.Tools;

public class Data {
	
	private String absolutepath;
	private String relativepath;	//path relative to data base dir
	private String name;
	
	public Data(String _absolutepath) {
		
		absolutepath = Tools.universalPath(_absolutepath);
		
		setRelativePath();
		setName();
		
	}
	
	private void setRelativePath() {
		
		String basePath = "";
		String extension = "";
		String baseDir = "";
		Path absolutePath;
		
		extension = Tools.getFileExtension(absolutepath);
		
		if (!CentralData.INSTANCE.dirs.getCurrentProcessPath().isEmpty()) {
			
			basePath = CentralData.INSTANCE.dirs.getCurrentProcessPath();
			
		}
		else {
			basePath = CentralData.INSTANCE.dirs.getDataPath();
		}
		

		baseDir = Tools.getLastPartOfPath(basePath);
		absolutePath = Paths.get(absolutepath);
 			
 		if (!absolutepath.startsWith(basePath)) {
 			
 			basePath = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
 		}
 		
 		if (!absolutepath.startsWith(basePath)) {
 			
 			CentralData.INSTANCE.log.warning("Paths do not align and relavive path cannot be set: " + absolutepath);
 			return;
 		}
 		
 		Path relativePath = Paths.get(basePath).relativize(absolutePath);
 		relativePath = Paths.get(baseDir).resolve(relativePath);
 		
 		String uniformPath = Tools.uniformFilePath(relativePath.toString());
 			
 		if (!uniformPath.startsWith("/")) {
 			uniformPath = "/" + uniformPath;
 		}
 		
 		relativepath = uniformPath;

	}
	
	private void setName() {
		File file = new File(absolutepath);
		
		name = file.getName();
	}

	
	public String getAbsolutePath() {
		return absolutepath;
	}
	
	public String getRelativePath() {
		return relativepath;
	}
	
	public String getName() {
		return name;
	}

}
