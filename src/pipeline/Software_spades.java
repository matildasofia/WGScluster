package pipeline;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import centralData.CentralData;
import data.DataHandler;
import data.FileList;
import data.Pair;
import tools.Tools;

public class Software_spades {
	private static String software = "spades";
	private String outputName = "contigs.fasta";
	
	
	public Software_spades() {
		
	}
	
	public void writeCommands() {
		
		String outDir;
		List<Pair> input;
		String filePath;
		String trimPath = CentralData.INSTANCE.dirs.getTrimmedPath();
		String workDir = CentralData.INSTANCE.currentAnalysis.getContainerBaseDir();
		SoftwareConfigurations runConfig = CentralData.INSTANCE.currentAnalysis.getSoftwareConfigurations(software);
		
		String assemblyPath = CentralData.INSTANCE.dirs.getAssemblyPath();
		String scriptName = CentralData.INSTANCE.currentAnalysis.getCommandScriptName();

		File spadesCommands = new File(assemblyPath, scriptName);
		
		try (FileWriter writer = new FileWriter(spadesCommands)) {
			
			String baseName = "";
			String string = "";
			
			input = prepareInput();
			outDir = getContainerPath(assemblyPath);
			
			List<String> flags = runConfig.getFlags();
			List<String> baseCmds = runConfig.getBaseCommands();
			List<String> spadesoutDirs = new ArrayList<>();
			
			for (Pair pair : input) {
				
				List<String> args = new ArrayList<>();
				
				args.addAll(baseCmds);
				
				if(pair.isPaired()) {
					filePath = Tools.getMountedPath(trimPath, pair.getR1());
					System.out.println(trimPath);
					System.out.println(pair.getR1());
					System.out.println(filePath);
					filePath = Tools.mergePaths(workDir, filePath);
					args.add("-1");
					args.add(filePath);
					
					filePath = Tools.getMountedPath(trimPath, pair.getR2());
					filePath = Tools.mergePaths(workDir, filePath);
					args.add("-2");
					args.add(filePath);
					
					baseName = pair.getBaseName();
				}
				else {
					filePath = Tools.getMountedPath(trimPath, pair.getR1());
					filePath = Tools.mergePaths(workDir, filePath);
					args.add("-s");
					args.add(filePath);
					
					baseName = pair.getBaseName();
				}
				
				String assemblyOutDir = getContainerPath(assemblyPath);
				assemblyOutDir = Tools.mergePaths(assemblyOutDir, baseName);
				spadesoutDirs.add(assemblyOutDir);
				
				args.add("-o");
				
				if (assemblyOutDir != null) {
					args.add(assemblyOutDir);
				}
				
				if (flags != null) {
					args.addAll(flags);
				}
				
				args.add(System.lineSeparator());
				
				string = String.join(" ", args);
				writer.write(string);				
			}
			
			writer.write(System.lineSeparator());

			
			// Move the target file out of the spades dir, rename and delete spades dir.
			for (String dir : spadesoutDirs) {
				
				String newPath = "";
				String outputPath= "";
				
				//String spadesOutDir = getContainerPath(assemblyPath);
				outputPath = Tools.mergePaths(dir, outputName);
				newPath = dir + ".fasta";
				//newName = Tools.mergePaths(spadesOutDir, newName);
				
				writer.write(String.join(" ", "mv", outputPath, newPath));
				writer.write(System.lineSeparator());
				writer.write(String.join(" ", "rm", "-r", dir));
				writer.write(System.lineSeparator());

				
//			    writer.write(String.join(" ", "mv", outputPath, newPath));
//			    writer.write(System.lineSeparator());
//				
//			    writer.write(String.join(" ", "if [ $? -eq 0 ]; then"));
//			    writer.write(System.lineSeparator());
//			    writer.write(String.join(" ", "rm", "-r", dir));
//			    writer.write(System.lineSeparator());
//			    writer.write(String.join(" ", "fi"));
//			    writer.write(System.lineSeparator());
				
			}

			writer.close();
			
		} catch (IOException e) {
			System.out.println("An error occurred while writing commands for software: " + software + e.getMessage());
			e.printStackTrace();
        }
		
	}

	private List<Pair> prepareInput() {
		
		File dir;
		String inputDir;
		FileList filelist;
		DataHandler inputData;
        List<File> files;
        
        inputDir = CentralData.INSTANCE.dirs.getTrimmedPath();
        dir = new File(inputDir);
        files = Arrays.asList(dir.listFiles());
        System.out.println();
        
        inputData = new DataHandler();
        filelist = new FileList();
        
        inputData.setData(files);
        filelist.setData(inputData);
        filelist.makeFileList(inputDir);

        RunPairFiles.run();
        inputData.setPairedData(inputDir);
        
        return inputData.getPairedData();
		
	}
	
	private String getContainerPath(String path) {
		
		String workdir = CentralData.INSTANCE.currentAnalysis.getContainerBaseDir();
		String outDir = Tools.getLastPartOfPath(path);
		
		path = Tools.mergePaths(workdir, outDir);
		
		return path;		
		
	}
	
}
