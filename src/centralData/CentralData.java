package centralData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.eclipse.jface.dialogs.MessageDialog;

import pipeline.CurrentAnalysis;
import pipeline.RunGrapetree;
import tools.Tools;



public enum CentralData {

	   INSTANCE;
    
		/// Version Info
	    public static String version = "1.0.0";
	    public static String build = "2024-06-03";

	    
	    /// running flag
	    
	    public boolean isrunning = false;
	    
	    /// Configfile
	    private String config_path = "";
	    
	    ///////////////////////////////////////////////////////////////
	    // Data classes
	    public Dirs dirs = new Dirs();
	    public Current_Preferenses preferenses = new Current_Preferenses();
	    public Current_ErrorLog log = new Current_ErrorLog(); 


	    public CurrentAnalysis currentAnalysis = new CurrentAnalysis();
	    public InputData inputData = new InputData();
	    
		///////////////////////////////////////////////////////////////
		// initiate 
		
		private CentralData(){
		
		}
		
		//////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////
		///// GET functions
		
	    public String getversion(){
			return "Gegenees " + CentralData.version ;
		}
	    
	    ////////////
	    public String getversionLong(){
			return "Gegenees " + CentralData.version + " (build=" + CentralData.build + ")";
		}
	    
	    ////////////
	    public String getConfigFilePath(){
	    	return config_path;
	    }
		
		//////////////////////////////////////////////////////////////////////
		/////////  INITIATIONS
		
		
		// last initatins...during the first set focus message received
		private void firstFocus(){  
			
			///////////////////////////////////////////////////
			/// Initiations

			loadConfigFile();	
			isConfigurationOK();		

		}
		
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		private void loadConfigFile(){
			
			/// SEARCH FOR CONFIG FILE
			
			File file1 = new File(Tools.uniformPath(System.getProperty("user.home")) + "WGScluster.conf");
			File file2 = new File(Tools.uniformPath(System.getProperty("user.home")) + "WGScluster.conf.txt");
			File file3 = new File(Tools.uniformPath(System.getProperty("user.dir")) + "WGScluster.conf");
			File file4 = new File(Tools.uniformPath(System.getProperty("user.dir")) + "WGScluster.conf.txt");
			
		
			File file = null;
			
			if(file1.exists()){
				file = new File(file1.getAbsolutePath());
			}
			if(file==null){
				if(file2.exists()){
					file = new File(file2.getAbsolutePath());
				}
			}
			if(file==null){
				if(file3.exists()){
					file = new File(file3.getAbsolutePath());
				}
			}
			if(file==null){
				if(file4.exists()){
					file = new File(file4.getAbsolutePath());
				}
			}
			
			
			////////////////////////////////////
			//// A file was found.... read it
			if(file !=null && file.exists()){
				
				config_path = file.getPath();
			
				/// READ KEY VALUES FROM CONFIG FILE
				Scanner scanner=null;
				String analyseslocation = "";
				String datalocation = "";
				String schema = "";
				String containerbasedir = "";
				String configlocation = "";
				String krakenlocation = "";
				
				try {
					scanner = new Scanner(new FileReader(file));
							
				  while ( scanner.hasNextLine() ){
						String line = scanner.nextLine();
						
						line = line.trim();
						
						if(!line.contains("=")){
							continue;
						}

						
						if(line.startsWith("Analyses Base Path=")){
							analyseslocation = line.substring(19);		
							dirs.setAnalysesPath(analyseslocation);
							System.out.println(analyseslocation);
						}
						if(line.startsWith("Data Base Path=")){
							datalocation = line.substring(15);
							dirs.setDataPath(datalocation);
							System.out.println(datalocation);
						}
						if(line.startsWith("Schema Path=")){
							schema = line.substring(12);	
							dirs.setSchemaPath(schema);
							System.out.println(schema);
							
						}
						if(line.startsWith("Container Base Dir=")){
							containerbasedir = line.substring(19);
							dirs.setContainerBaseDir(containerbasedir);
							System.out.println(containerbasedir);
						}
						
						if(line.startsWith("Config Path=")){
							configlocation = line.substring(12);
							dirs.setConfigPath(configlocation);
							System.out.println(configlocation);
						}
						
						if(line.startsWith("Kraken Path=")){
							krakenlocation = line.substring(12);
							dirs.setKrakenPath(krakenlocation);
							System.out.println(krakenlocation);
						}
						
						
						
						}	
			
				  
				scanner.close();
				
				
				
				} catch (FileNotFoundException e) {
					log.warning("CentralData - FileNotFoundException for configfile" );
					return ;
				} 
				finally{
					if(scanner!=null){
						scanner.close();
					}
					
				}
			}  // END if file exists
			
			
			//// This code runs if no configuration file was found...
			else{
				// If no config file is found....make an empty one...
				log.log("No configuration file path. Making new empty configfile=" + file3.getAbsolutePath());
				
				try {
					String basepath = Tools.getBaseDir(file3.getAbsolutePath());
					File baseDir = new File(basepath);
					if(baseDir.canWrite()){
						BufferedWriter out = new BufferedWriter(new FileWriter(Tools.uniformFilePath(file3.getAbsolutePath() ),false));
						out.write("Startup Workspace=\n" );	
						out.write("Blast Path=\n" );	
						out.close();
					}
					else{
						log.warning("CentralData - Config files directory is read only!! (user.dir)");
					}
				} catch (IOException e) {	
					log.warning("CentralData - IOException, Failed creating file " + file3.getAbsolutePath() );
					
				}
				
				if(!file3.exists()){
					
					try {
						String basepath = Tools.getBaseDir(file1.getAbsolutePath());
						File baseDir = new File(basepath);
						if(baseDir.canWrite()){ 
							BufferedWriter out = new BufferedWriter(new FileWriter(Tools.uniformFilePath(file1.getAbsolutePath() ),false));
							out.write("Analyses Base Path=\n" );	
							out.write("Data Base Path=\n" );
							out.write("Schema Path=\n" );
							out.write("Container Base Dir=\n" );
							out.write("Config Path=\n" );
							out.write("Kraken Path=\n" );
							out.close();
						}
						else{
							log.warning("Config files directory is read only!! (user.home)");
						}
					} catch (IOException e) {	
						log.warning("Failed creating file " + file1.getAbsolutePath() );
						
					}
					
				}
				
				config_path ="";
				
				if(file1.exists()){
					config_path = file1.getPath();
				}
				
				if(!file1.exists() && file3.exists()){
					config_path = file3.getPath();
				}
				
				if(!file1.exists() && !file3.exists()){
					log.warning("Failed to create configurationfile");
					
					
				}
				
			}
		}
		
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		public void updateConfigFileContent(){
		BufferedWriter saveFile;
			
			try {
				File saveFileFile = new File(CentralData.INSTANCE.getConfigFilePath());
				if(saveFileFile.isFile()) {
					saveFileFile.delete();
				}
				FileWriter fw = new FileWriter(saveFileFile,false);
				saveFile = new BufferedWriter(fw);

				saveFile.write("Analyses Base Path=" + CentralData.INSTANCE.dirs.getAnalysesPath()  + "\n");
				saveFile.write("Data Base Path="  + CentralData.INSTANCE.dirs.getDataPath() + "\n");
				saveFile.write("Schema Path=" + CentralData.INSTANCE.dirs.getSchemaPath() +"\n" );
				saveFile.write("Container Base Dir=" + CentralData.INSTANCE.dirs.getContainerBaseDir() +"\n" );
				saveFile.write("Config Path=" + CentralData.INSTANCE.dirs.getConfigPath() +"\n" );
				saveFile.write("Kraken Path=" + CentralData.INSTANCE.dirs.getKrakenPath() +"\n" );

				saveFile.close();
				fw.close();
			} catch (IOException e1) {
				log.error("CentralData - Configfile update IO exception");
				return;
			}
		
		}
		
		/////////////////////////////////////////////////////////////////
		/////////////////////////////////////////////////////////////////
		
		public boolean isConfigurationOK(){
			
			boolean result = true;
			
			return result;
		}
		

		//////////////////////////////////////////////////////////////////////
		////////////////// TAB CONTROL //////////////////////////////////////
		
		private String activeTab = "";
		
		public static String PartAnalyses = "PartAnalyses";
		public static String PartRunconfigurations = "PartRunconfigurations";
		public static String PartQualitycontrol = "PartQualitycontrol";
		public static String PartMultiQC = "PartMultiQC";
		public static String PartClusteringresultsoverview = "PartClusteringresultsoverview";
		public static String PartTree = "PartTree";
		
		public String getActiveTab(){
			return activeTab;
		}
		
		//////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////

		
		public void setActiveTab(String arg){  // keeps track of which tab is active -- called by set focus in the "parts"
			
			if( !arg.equals(CentralData.PartAnalyses) &&
				!arg.equals(CentralData.PartRunconfigurations)  &&
				!arg.equals(CentralData.PartQualitycontrol) &&
				!arg.equals(CentralData.PartMultiQC) &&
				!arg.equals(CentralData.PartClusteringresultsoverview) &&
				!arg.equals(CentralData.PartTree)) {
				       log.warning("CentralData - unknown Active Tab name:" + arg);
			}
			
			if(activeTab.equals("")){		
				firstFocus();	
			}
			
			 activeTab = arg;
		}
		
		
		//////////////////////////////////////////////////////////////////////
		//////////////////////////////////////////////////////////////////////


	
}
