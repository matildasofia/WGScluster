package data;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

import centralData.CentralData;
import tools.Tools;

public class FileList {
	
	private List<Data> data;
	private String path;
	
	public FileList() {
	}
	
	public void makeFileList(String path) {
		
		String basePath;
		String baseDir;
		String fileName;
		File fileList;
		
		if (!CentralData.INSTANCE.dirs.getCurrentProcessPath().isEmpty()) {
			
			basePath = CentralData.INSTANCE.dirs.getCurrentProcessPath();
		}
		else {
			basePath = CentralData.INSTANCE.dirs.getDataPath();
		}
		
		baseDir = Tools.getLastPartOfPath(basePath);
		fileName = CentralData.INSTANCE.currentAnalysis.getFileListName();
		
		fileList = new File(path, fileName);
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileList));) {
			
			for (Data data : data) {
				
				path = data.getRelativePath();
				path = path.replaceFirst(baseDir, "data");
								
				writer.write(path);
				writer.newLine();
				
			}
			
			writer.close();
			
			this.path = path;
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
	}
	
	public void setData(DataHandler data) {
		
		this.data = data.getData();
		
	}
	
	public String getPath() {
		return path;
	}

}
