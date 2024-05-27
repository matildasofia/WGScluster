package centralData;

import tools.Tools;

public class Current_Preferenses {


	///////////////////////////////////////////////////////
    /// Debug parameter
    public boolean echo = true;
	/////////////////////////////////////////////////////// 
    
    
	// constants
	private final  int  WIN = 1;
	private final  int  MAC = 2;
	private final  int  LNX = 3;
	
	// system properties
	public String system_osName = "";
	public String system_osVersion = "";
	public String system_osArch = "";
	public String system_javaVersion = "";
	public String system_javaVendor = "";
	public String system_javaVMVersion = "";
	public String system_javaVMVendor = "";
	public String system_tempPath = "";
	public String system_FileSeparator = "";
	public String system_PathSeparator = "";
	public String system_LineSeparator = "";
	public String system_UserHomeDir = "";
	public String system_UserName = "";
	public String system_UserDir = "";
	public String system_javaRuntimeName = "";
	public String system_CurrentWorkinDirAtStartup = "";
	public int system_AvaliableProcessorCores = 0;
	public String system_PATH = ""; 
	public  int system =LNX;  /// WIN, MAC, LNX  default (if system is not recognizable...treat as linux)
	
	public String programPath = "";
		

	private int pref_MaxThreads = default_MaxThreads;	
	
	// hardcoded preferences

	public static int default_MaxThreads = 100;
	public int ComparisonLogwindowLimit=300;
	

	
	/////////////////////////////////////////////////////////////////
	// constructor
	public Current_Preferenses() {
	

		readSystemProperties();

		
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////

	

	private void readSystemProperties(){
		/// initilaze system properties
		system_osName = System.getProperty("os.name");
		system_osVersion = System.getProperty("os.version");
		system_osArch = System.getProperty("os.arch");
		
		system_javaVersion = System.getProperty("java.version");
		system_javaVendor = System.getProperty("java.vendor");
		system_javaVMVersion = System.getProperty("java.vm.version");
		system_javaVMVendor = System.getProperty("java.vm.vendor");
		system_javaRuntimeName = System.getProperty("java.runtime.name");
		
		system_tempPath = System.getProperty("java.io.tmpdir");
		
		system_FileSeparator = System.getProperty("file.separator");
		system_PathSeparator = System.getProperty("path.separator");
		system_LineSeparator = System.getProperty("line.separator");
		
		system_UserHomeDir = System.getProperty("user.home");
		system_UserName = System.getProperty("user.name");
		system_CurrentWorkinDirAtStartup = System.getProperty("user.dir");
		system_PATH = System.getenv("PATH");
		
		Runtime rt = Runtime.getRuntime();
		system_AvaliableProcessorCores = rt.availableProcessors();
		
	
		if(system_osName.startsWith("Windows")){
			system = WIN;
		}
		if(system_osName.startsWith("Linux")){
			system = LNX;
		}
		if(system_osName.startsWith("Mac")){
			system = MAC;
		}
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public String getSystemInfoMsg(){
		String msg = "";
		
		// Geg version
		msg += "Gegenees version: " + CentralData.version + "\n";
		msg += "Gegenees build: " + CentralData.build + "\n\n";
		
		// OS
		msg += "OS name: " + system_osName + "\n";
		msg += "OS version: " + system_osVersion + "\n";
		msg += "OS architecture: " + system_osArch + "\n\n";
		
		// Java
		msg += "Java Runtime version: " + system_javaVersion + "\n";
		msg += "Java Runtime Vendor: " + system_javaVendor + "\n";
		msg += "Java Runtime Name: " + system_javaRuntimeName + "\n";
		msg += "Java Viritual Mashine version: " + system_javaVMVersion + "\n";
		msg += "Java Viritual Mashine vendor: " + system_osVersion + "\n\n";

		
		// OS properties
		msg += "System temp path: " + system_tempPath + "\n\n";
		msg += "File separator: " + system_FileSeparator + "\n";
		msg += "Path separator: " + system_PathSeparator + "\n";
		if(system_LineSeparator.equals("\n")) { msg += "Line separator: " + "\\n" + "\n\n"; }
		if(system_LineSeparator.equals("\r")) { msg += "Line separator: " + "\\r" + "\n\n"; }
		if(system_LineSeparator.equals("\r\n")) { msg += "Line separator: " + "\\r\\n" + "\n\n"; }
		if(system_LineSeparator.equals("\n\r")) { msg += "Line separator: " + "\\n\\r" + "\n\n"; }
		msg += "OS User Name: " + system_UserName + "\n";
		msg += "OS User home directory: " + system_UserHomeDir + "\n\n";
		msg += "Path at startup: " + system_CurrentWorkinDirAtStartup + "\n\n";
		msg += "System environment PATH variable: " + system_PATH + "\n\n";
		
		Runtime rt = Runtime.getRuntime();
		
		msg += "System Avaliable processor cores: " + rt.availableProcessors() + "\n";
		msg += "System Free memory: " + Tools.FormatMemory(rt.freeMemory()) + "bytes" + "\n"; 
		msg += "Java allocated memory: " + Tools.FormatMemory(rt.totalMemory()) + "bytes\n";
		if(rt.maxMemory() != Long.MAX_VALUE ){
			msg += "Java memory limit: " + Tools.FormatMemory(rt.maxMemory()) + "bytes\n\n";
		}
		else{
			msg += "Java memory limit: " + "Long Max Value - "  + Tools.FormatMemory(rt.maxMemory()) + "bytes" + "\n\n";
		}
		
		
        
        msg += "Configuration path: " + CentralData.INSTANCE.getConfigFilePath() + "\n";
        msg += "Current workspace path: " + CentralData.INSTANCE.dirs.getAnalysesPath() + "\n";
        msg += "Blast path: " + CentralData.INSTANCE.dirs.getDataPath() + "\n\n";
        
        msg += "Gegenees Status active Tab: " +CentralData.INSTANCE.getActiveTab() + "\n";
        msg += "Gegenees Status active Workspace: " +CentralData.INSTANCE.dirs.getAnalysesPath() + "\n";
        msg += "Gegenees Status active Comparison: " +CentralData.INSTANCE.dirs.getCurrComparisonPath() + "\n";
        
		return msg;
	}
	



	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public boolean isWINDOWS(){
		return (system==WIN);
	}



	  /////////////////////////////////
    public int getThreadMaxLimit(){

    	return pref_MaxThreads;
	}

	
	
    public void setThreadMaxLimits(int arg_threads){
    	if( arg_threads<1 ){
    		return;
    	}
    	pref_MaxThreads =arg_threads;
    }
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	// GET functions
	

	public String getOs(){
		if(system == WIN){ return "win"; }
		if(system == MAC){ return "mac"; }
		if(system == LNX){ return "lnx"; }
		return "lnx";
	}
	
	public String getArchitecture(){
		return system_osArch; 
	}
	

	
	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	


	//////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////
	
}
