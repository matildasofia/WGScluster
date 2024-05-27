package data;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import centralData.CentralData;
import tools.Tools;

public class DataHandler {
	
	private List<Pair> pairedData;
	private List<Data> data;
	
	public DataHandler() {
		
	}
	
	public void setData(List<File> files) {
		
		data = new ArrayList<>();
		List<String> allowedExtentions = new ArrayList<>(Arrays.asList("tsv", "fasta", "fastq", "gz"));
		
		List<String> filteredData = files.stream()
			.filter(file -> file.isFile() && !file.isHidden())
			.map(File::getAbsolutePath)
			.collect(Collectors.toList());
				
		for (String file : filteredData ) {
			String fileExtension = file.substring(file.lastIndexOf(".") + 1);
			 			
			if(!allowedExtentions.contains(fileExtension)) {
				
				CentralData.INSTANCE.log.warning( "Illeagal argument exception for data: " + file );
				
			}
			
			
			data.add(new Data(file));
			
		}
		
	}
	
	public void setPairedData(String listPath) {
		
		String R1 = "";
		String R2 = "";
		String baseName = "";
		String dataPath = "";
		String pairedFile = "";
		String remove = "/data";
		
		if (!CentralData.INSTANCE.dirs.getCurrentProcessPath().isEmpty()) {
			
			dataPath = CentralData.INSTANCE.dirs.getCurrentProcessPath();
			
		}
		else {
			dataPath = CentralData.INSTANCE.dirs.getDataPath();
		}
		
		pairedFile = CentralData.INSTANCE.currentAnalysis.getPairedFileList();
		pairedFile = Tools.mergePaths(listPath, pairedFile);
		
		pairedData = new ArrayList<>();
		
		try {
            List<String> lines = Files.readAllLines(Paths.get(pairedFile));
                        
            for (String line : lines) {
        		String fileList;
        		String pairedFileList;
        		String [] inputs = line.split("\t");
        		
        		if (inputs.length > 3) {
        			R1 = inputs[0];
        			R2 = inputs[1];
        			baseName = inputs[2];
        		}
        		
        		else {
        			CentralData.INSTANCE.log.warning("Paired file of incorrect format.");
        		}
                
                
            	
            	fileList = CentralData.INSTANCE.currentAnalysis.getFileListName();
        		pairedFileList = CentralData.INSTANCE.currentAnalysis.getPairedFileList();
        		
        		if (R1.endsWith(fileList) || R1.endsWith(pairedFileList)) {
        			
        			CentralData.INSTANCE.log.warning("Filelist in directory, ignoring");
        			continue;
        		}
        			
        		if (R2.endsWith(fileList) || R2.endsWith(pairedFileList)) {
        			CentralData.INSTANCE.log.warning("Filelistpaired in directory, ignoring");
        			continue;
        		}
        			
            	Pair pair = new Pair();
    			
            	if(!R1.isEmpty()) {
            		
            		if(R1.startsWith(remove)) {
            			
            			R1 = R1.substring(remove.length());
            			R1 = Tools.mergePaths(dataPath, R1);
            			
            		}
            		pair.setR1(R1);
            		
            	}
            	if(!R2.isEmpty()) {
            		
            		if(R2.startsWith(remove)) {
            			
            			R2 = R2.substring(remove.length());
            			R2 = Tools.mergePaths(dataPath, R2);
            			                			
            		}
        			pair.setR2(R2);

            	}
            	
            	pair.setBaseName(baseName);
            	pairedData.add(pair);
      
            }
            
        } catch (IOException e) {
            e.printStackTrace();
            CentralData.INSTANCE.log.error("Pairing files not successful.");
            CentralData.INSTANCE.log.error(e.toString());
        }
		
	}
	
	public List<Data> getData() {
		return data;
	}
	
	public List<Pair> getPairedData() {
		return pairedData;
	}
	
	public String pairedToString() {
		String string = "[";
		
		List<String> baseNames = pairedData.stream()
				.map(paired -> paired.getBaseName())
				.collect(Collectors.toList());
		
		string += String.join(", ", baseNames) + "]";
		
		return string;
	}

	@Override
	public String toString() {
		String string = "[";
		
		List<String> dataNames = data.stream()
				.map(data -> data.getAbsolutePath())
				.collect(Collectors.toList());
		
		string += String.join(", ", dataNames) + "]";
		
		return string;
	}

	public int length() {
		
		if (pairedData != null) {
			
			return pairedData.size();
		}
		else {
			
			return data.size();
		}
		
	}

}


