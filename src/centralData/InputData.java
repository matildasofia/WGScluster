package centralData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import pipeline.RunPairFiles;
import tools.Tools;

public class InputData {
	
	private List<String> absolute_paths;
	private List<String> paths_relative_to_base;	
	private List<List<String>> pairedData;
	
	public InputData() {
		
	}
	
	
	
	public void setInputData(List<File> selectedData) {
		

		List<String> allowedExtentions = new ArrayList<>(Arrays.asList("tsv", "fasta", "fastq", "gz"));
		
		List<String> filteredData = selectedData.stream()
			.filter(file -> file.isFile() && !file.isHidden())
			.map(File::getAbsolutePath)
			.collect(Collectors.toList());
		
		for (String file : filteredData ) {
			
			String fileExtension = file.substring(file.lastIndexOf(".") + 1);
			
			if(!allowedExtentions.contains(fileExtension)) {
				
				throw new IllegalArgumentException("Selection contains objects of incorrect format (fasta, tsv, fastq)");
				
			}
			
		}
		
		absolute_paths = filteredData;
		setRelativePaths();

	}
	
	/**
	 * Returns a list of files relative to the data base directory.
	 * @param list of file paths
	 * @return list of relative paths
	 */
	private void setRelativePaths() {
		
		String dataBasePath = CentralData.INSTANCE.dirs.getDataPath();
		String baseDir = Tools.getLastPartOfPath(dataBasePath);
		List<String> relativePaths = new ArrayList<>();
		
 		for (String absolute_path : absolute_paths) {
 			
 			Path absolutePath = Paths.get(absolute_path);
 			
 			if (absolute_path.startsWith(dataBasePath)) {
 				
 				Path relativePath = Paths.get(dataBasePath).relativize(absolutePath);
 				relativePath = Paths.get(baseDir).resolve(relativePath);
 				String uniformPath = Tools.uniformFilePath(relativePath.toString());
 				
 				if (!uniformPath.startsWith("/")) {
 					uniformPath = "/" + uniformPath;
 				}
 				
 				relativePaths.add(uniformPath);

 			}
 			else {
 				throw new IllegalArgumentException("Files are outside of the data base directory. Respecify base directory or choose other files.");
 			}
		}
 		paths_relative_to_base = relativePaths;
 		
	}
	
	
	public List<String> getAbsolutePaths() {
		return absolute_paths;
		
	}
	
	public List<String> getRelativePaths() {
		return paths_relative_to_base;
	}
	
	public List<List<String>> getPairedData() {
		return pairedData;
	}
	
	public void createFileList() {
		
		String analysisPath = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
		String dataPath = CentralData.INSTANCE.dirs.getDataPath();
		
		File filelist = new File(analysisPath, "filelist.txt");
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filelist));) {
			
			for (String path : paths_relative_to_base) {
								
				String updatedFile = path.replace(Tools.getLastPartOfPath(dataPath), "data");
				writer.write(updatedFile);
				writer.newLine();
				
			}
			
			writer.close();
			
			RunPairFiles.run();
			setPairedData();
			
		} catch (IOException e) {
			
			e.printStackTrace();
			
		}
		
	}
	
	private void setPairedData() {
		String filelistPath = CentralData.INSTANCE.dirs.getCurrentAnalysisPath() + "/filelist_pairs.txt";
		pairedData = new ArrayList<>();
		
		
		try {
            List<String> lines = Files.readAllLines(Paths.get(filelistPath));
            for (String line : lines) {
                String [] inputs = line.split("\t");
                
                List<String> reads = new ArrayList<>(Arrays.asList(inputs[0], inputs[1]));
                pairedData.add(reads);
      
            }
            
            for (List<String> line: pairedData) {
            	System.out.println("Uppsala: " + line);
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public boolean hasInputData() {


		return (paths_relative_to_base != null && !paths_relative_to_base.isEmpty());
				
	}

}
