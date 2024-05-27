package pipeline;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import centralData.CentralData;
import tools.Tools;

public class Software_chewbbaca {
												 
	private static String software = "chewbbaca";
	//private SoftwareConfigurations runConfigurations;
	
	
	public Software_chewbbaca() {
		
		//this.runConfigurations = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
	}
	
	
	public void writeCommands() throws IOException {
		
//		String filePath;
//		String trimPath = CentralData.INSTANCE.dirs.getTrimmedPath();
//		String workDir = CentralData.INSTANCE.currentAnalysis.getContainerBaseDir();
//		SoftwareConfigurations runConfig = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
//		
//		String assemblyPath = CentralData.INSTANCE.dirs.getAssemblyPath();
//		String scriptName = CentralData.INSTANCE.currentAnalysis.getCommandScriptName();
//
//		File spadesCommands = new File(assemblyPath, scriptName);
		
		
		
		
		//Host related
		String allelecallDir;
		String inputDir;
		File fileList;
		
		//Container related
		String input;
		String outDir;
		String schema;
		String emptyDir;
		SoftwareConfigurations runConfig;
		
		allelecallDir = CentralData.INSTANCE.dirs.getAllelecallPath();
		inputDir = CentralData.INSTANCE.dirs.getAssemblyPath();
		fileList = new File(createFileList(inputDir));
		
		input = getContainerPath(fileList.getAbsolutePath());
		outDir = getContainerPath(allelecallDir);
		schema = getContainerPath(CentralData.INSTANCE.dirs.getSchemaPath());
		
		String space = " ";
		
		allelecallDir = CentralData.INSTANCE.dirs.getAllelecallPath();
		File dir = new File(allelecallDir);
		
		if (!dir.mkdir()) {
			CentralData.INSTANCE.log.warning("Failed to create directory: " + dir.getAbsolutePath());
		}
		
		emptyDir = "WGSchewbbaca";
		
		if (new File(dir, emptyDir).exists()) {
			
			Tools.deleteDir(new File(dir, emptyDir));
		}
		
		outDir = Tools.mergePaths(outDir, emptyDir);
		
		String commandScript = CentralData.INSTANCE.currentAnalysis.getCommandScriptName();
		File commandFile = new File(dir, commandScript);

		try (FileWriter writer = new FileWriter(commandFile)) {
			
			String baseCommands = "";
			String flags = "";
			
			runConfig = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
			
			baseCommands = String.join(space, runConfig.getBaseCommands());
			
			if (runConfig.getFlags() != null) {
				flags = String.join(space, runConfig.getFlags());
			}
			
			// write commands for run
			writer.write(baseCommands + space);
			writer.write(String.join(space, "-i", input) + space);               // in, assemblies
			writer.write(String.join(space, "-g", schema) + space); 			 // schema
			writer.write(String.join(space, "-o", outDir) + space);				 // out directory	
			
			if (!flags.isEmpty()) {
				writer.write(flags);
			}
			
			writer.write(System.lineSeparator());
			
			String result = "results_alleles.tsv";
			String resultPath = Tools.mergePaths(outDir, result);
			
			String resDir = getContainerPath(allelecallDir);
			
			// move output file to process directory
			writer.write(String.join(" ", "mv", resultPath, resDir));
			writer.write(System.lineSeparator());
			writer.write(String.join(" ", "rm", "-r", outDir));
			writer.write(System.lineSeparator());
			
		} catch (IOException e) {
			e.printStackTrace();
			
			CentralData.INSTANCE.log.error("An error occured while writing commands for ChewBBACA " + e.getMessage());
			
        }
	}
	
	public String getContainerPath(String path) {
		
		String containerBaseDir = CentralData.INSTANCE.dirs.getContainerBaseDir();
		
		String dir = Tools.getLastDir(path);
		
		Path newpath = Paths.get(containerBaseDir, dir);
		 
		return Tools.universalPath(newpath.toString());
		
	}
	
	private String createFileList(String path) {
		
		File dir = new File(path);
		File filelist = new File(path, "filelist.txt");
		File[] files = dir.listFiles();	
		
		try (BufferedWriter writer = new BufferedWriter(new FileWriter(filelist));) {
			
			for (File file : files) {
				
				if(!(file.getName().contains(".fasta"))) {
					continue;
				}
				String contpath = getContainerPath(file.getAbsolutePath());
				
				writer.write(contpath);
				writer.newLine();
				
			}
			
			writer.close();	
				
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		return filelist.toString();
		
	}

}
