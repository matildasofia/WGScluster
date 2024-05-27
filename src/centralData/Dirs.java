package centralData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import org.yaml.snakeyaml.Yaml;

import tools.Tools;



public class Dirs {
    
	private String analyses_path =""; 
	private String comparison_path = "";
	private String blast_path = "";
	private String schema_path = "";
	private String trimmed_path = "";
	private String assembly_path = "";
	private String allelecall_path = "";
	private String current_analysis_path = "";
	private String container_base_dir = "";
	private String config_path = "";
	private String kraken_db_path = "";
	private String current_process_path = "";

	
	public String readPreferences_MaxThreads  = "100";
	
    /////////////////////
	public Dirs() {		

		
	}
	
	////////////////////////////////////////////
	
	public static String uniformPath(String path){
		if(path==null){
			return null;
		}
		if(path.equals("")){
			return path;
		}
		
        if( (path.startsWith("/") || path.startsWith("\\") )  &&  path.contains(":") && CentralData.INSTANCE.preferenses.isWINDOWS()){
            path = path.substring(1);
        }
        
        path = path.replace("\\", "/");
        
		if(!path.endsWith("/")){
			path = path + "/";
		}
		if(path.contains("//")){
			path = path.replaceAll("//", "/") ;
		}
		
		if(System.getProperty("os.name").startsWith("Windows")){
			 path = path.replace("/", "\\");
		}
		
		return  path;
	}
	
	public static String universalPath(String path) {
		
		Path universalPath = Paths.get(path);
		
		return universalPath.toString();
		
	}
	
	public static String uniformFilePath(String path){
		if(path==null){
			return null;
		}
		
        if( (path.startsWith("/") || path.startsWith("\\") )  &&  path.contains(":") && CentralData.INSTANCE.preferenses.isWINDOWS()){
            path = path.substring(1);
        }
        
        path = path.replace("\\", "/");
        
		if(path.endsWith("/")){
			path = path.substring(0, path.lastIndexOf("/"));
		}
		if(path.contains("//")){
			path = path.replaceAll("//", "/") ;
		}
		
		if(System.getProperty("os.name").startsWith("Windows")){
			 path = path.replace("/", "\\");
		}
		
		return  path;
	}
	
public static String getBaseDir(String path ){
		
		if(System.getProperty("os.name").startsWith("Windows")){
			if(path.endsWith("\\")){
				path = path.substring(0, path.length()-1);
			}
			
			if(path.contains("\\")){
				path = path.substring(0,path.lastIndexOf("\\"));
			}
		}
		else{
			if(path.endsWith("/")){
				path = path.substring(0, path.length()-1);
			}
			
			if(path.contains("/")){
				path = path.substring(0,path.lastIndexOf("/"));
			}
		}

		return uniformPath(path + "/");
		
	}
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
	

	
	public String[] GetInfoFromCurrentComparison(){
		return GetInfoFromcomparisonPath(comparison_path);
	}

	public String GetCurrentComparisonType(){
		
		String[] info = GetInfoFromCurrentComparison();
		
		return info[3];
	}

	public String GetComparisonType(String path){
		
		String[] info = GetInfoFromcomparisonPath(path);
		

		return info[3];
	}
	
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
	
//	public String GetCurrentComparisonPerspective(){
//		String[] str = GetInfoFromcomparisonPath(comparison_path);
//		
//		if(str[3].equals("Fragmented All-All comparison")){
//			return CentralData.perspective_FragAllAll;
//		}
//		if(str[3].equals("Primer mapping")){
//			return CentralData.perspective_primerGenome;
//		}
//		
//		if(str[3].equals("Read mapping")){
//			return CentralData.perspective_readMapping;
//		}
//		
//		if(str[3].equals("Pangenome")){
//			return CentralData.perspective_pangenome;
//		}
//		
//		//not found
//		return CentralData.perspective_FragAllAll; //""; 
//	}
	
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
	
    public String[] GetInfoFromcomparisonPath(String path){
    	
    	String retVal[] = new String[5];
    	retVal[0]="";  // processes
    	retVal[1]="";  // modified
    	retVal[2]="";  // total size in bases
    	retVal[3]="";  // comparison type
    	retVal[4]="";  // finalized state
    	
    	
    	
		Scanner scanner=null;
		path = Tools.uniformPath(path);
		
		File dir = new File(path);
		
		if (dir.exists() && dir.isDirectory()) {
			File[] dirs = dir.listFiles(File::isDirectory);
			
			String dirNames = Arrays.stream(dirs)
                    .map(File::getName)
                    .map(name -> name.equals("trimmed") ? "trimming" : name)
                    .collect(Collectors.joining(", "));
			
			retVal[0] = dirNames;
		}
		
		
		
		File thefile = new File( path + "config.yml");
		
		if(thefile.exists()){
			try {
				
				Yaml yaml = new Yaml();
				Map<String, Object> map = new HashMap<>();				
				try (InputStream inputStream = new FileInputStream( thefile )) {
					
		             map = yaml.load(inputStream);
		             
		             if (map.get("created") != null) {
		            	retVal[1] = (String) map.get("created"); 
		             }
		             if (map.get("finalized") != null) {
		            	 retVal[2] = (String) map.get("finalized");
		             }

		            
		        } catch (Exception e) {
		        	
		            e.printStackTrace();
		        }
				
				
				
				
				scanner = new Scanner(new FileReader(thefile));


				while ( scanner.hasNextLine() ){
					
					String[] line = Tools.parseGegFileLine(scanner.nextLine());
					if(line[0].equals("FileType:")) {	retVal[3] = line[1]; }
					if(line[0].equals("Created:")) { 	retVal[0] = Tools.Filterstamptime(line[1]);	}
					if(line[0].equals("Modified:")) { 	retVal[1] = Tools.Filterstamptime(line[1]);	}
					if(line[0].equals("totalSize:")) { 	retVal[2] = line[1];	}
					if(line[0].equals("finalized:")) {	retVal[4] = line[1];}
				
				} // end while
					
				scanner.close();
			} catch (FileNotFoundException ex) {
				CentralData.INSTANCE.log.error("Current_Comparison.GetInfoFromcomparisonPath FileNotFoundexception");
				ex.printStackTrace();
			}
			

    	
		}
		
		//// Backward compatibility translations...
		if(retVal[3].equals("comparison")){
			retVal[3] = "Fragmented All-All comparison";
		}
		
		if(retVal[3].equals("Fragmented All-All comparsion")){
			retVal[3] = "Fragmented All-All comparison";
		}
		
		return retVal;
    }
    
    
    
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
	/// get functions
//    public String getDatabasePath(){
//		return db_path;
//	}
    

    public String getAnalysesPath(){
    //	System.out.println("getting wsp path exists: " + workspace_path + "\n");
    	return analyses_path;
    }
    
    public String getCurrentAnalysisPath() {
    	return current_analysis_path;
    }
    
    public String getContainerBaseDir() {
    	return container_base_dir;
    }
    
    public String getConfigPath() {
    	return config_path;
    }
    
    public String getKrakenPath() {
    	return kraken_db_path;
    }

    public boolean hasWorkspacePath(){
 //   	System.out.println("asking if wsp path exists: " + workspace_path + "\n");
    	return( ! analyses_path.equals(""));
    }
    
    public boolean hasCurrentAnalysis() {
    	return( ! current_analysis_path.equals(""));
    }
 
    public String getCurrComparisonPath(){
    	return comparison_path;
    }
    
    public String getSchemaPath() {
    	return schema_path;
    }
 
    public String getCurrentProcessPath() {
    	return current_process_path;
    }
    
    /////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////
    
    // Set to default functions
    
    public void setDbToDefault(){
  //  	setDbPath(Tools_Db.getDefaultDbPath());
 	}

  
    public void setDataPath(String newPath){
    	newPath = Tools.uniformPath(newPath);
    	blast_path = universalPath(newPath);
    }
  
    public String getDataPath(){
    	return blast_path;
    }
    
    public boolean checkBLASTExists(){
    	String BLASTNpath = Tools.uniformPath(blast_path) + "blastn";
    	if(CentralData.INSTANCE.preferenses.isWINDOWS()) {
    		BLASTNpath= Tools.uniformPath(blast_path) + "blastn.exe";
    	}
    	
    	String MakeBLASTDBpath = Tools.uniformPath(blast_path) + "makeblastdb";
    	if(CentralData.INSTANCE.preferenses.isWINDOWS()) {
    		MakeBLASTDBpath= Tools.uniformPath(blast_path) + "makeblastdb.exe";
    	}
    	
    	if(!(new File(BLASTNpath).exists())) {
    		return false;
    	}
    	
    	if(!(new File(MakeBLASTDBpath).exists())) {
    		return false;
    	}
    	return true;
    }
    
    public String getSoftwarePath(String software) {
    		
    		return Paths.get(analyses_path, software).toString();

    }
    
    public boolean checkBLASTIsExecutable(){
    	String BLASTNpath = Tools.uniformPath(blast_path) + "blastn";
    	if(CentralData.INSTANCE.preferenses.isWINDOWS()) {
    		BLASTNpath= Tools.uniformPath(blast_path) + "blastn.exe";
    	}
    	
    	String MakeBLASTDBpath = Tools.uniformPath(blast_path) + "makeblastdb";
    	if(CentralData.INSTANCE.preferenses.isWINDOWS()) {
    		MakeBLASTDBpath= Tools.uniformPath(blast_path) + "makeblastdb.exe";
    	}
    	
    	if(!(new File(BLASTNpath).canExecute())) {
    		return false;
    	}
    	
    	if(!(new File(MakeBLASTDBpath).canExecute())) {
    		return false;
    	}
    	return true;
    }
    /////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    
//    public boolean setDbPath(String newpath){
//
//    	// Assert - is not empty
//    	if(newpath.equals("") || newpath==null|| workspace_path.equals("")  ){
//			return false;
//		}
//    	
//    	// Assert - is OK format
//		if(!(Tools.getLastPartOfPath(newpath).startsWith("database_"))&&!(Tools.getLastPartOfPath(newpath).equals("database"))){
//			return false;
//		}
//		
//		// Assert - exists
//    	newpath = Tools.uniformPath(newpath);
//		
//    	if(!Tools.MakeSureDirExists(newpath)) {
//			return false;
//		}
//		
//    	// noe set path
//    	db_path = newpath;
//
//		
////		CentralData.INSTANCE.currWorkspace.registerActiveDb(db_path);
//		
//		return true;		
//	}
    
    //////////////////////////////////////////////////////
    /////////////////////////////////////////////////////

    
    public boolean setCurrCompPath(String newpath){
 
    //	if(GetComparisonType(newpath).equals("Fragmented All-All comparison")){
    		                                 
//    		if( Data_FragAllAll.INSTANCE.dirs.setCurrCompPath(newpath)){
//    			comparison_path = Data_FragAllAll.INSTANCE.dirs.getCurrComparisonDir();
//    			return true;
//    		}
   // 	}
    	
    	///#if(GetComparisonType(newpath).equals("Primer mapping")){	
            
    	///#    		if( Data_PrimerGenomes.INSTANCE.dirs.setCurrCompPath(newpath)){
    	///#    			comparison_path = Data_PrimerGenomes.INSTANCE.dirs.getCurrComparisonDir();
    	///#    			return true;
    	///#    		}
    	///#   	}
    	///#   	
    	///#   	if(GetComparisonType(newpath).equals("Pangenome")){	
    	///#           
    	///#   		if( Data_Pangenome.INSTANCE.dirs.setCurrCompPath(newpath)){
    	///#   			comparison_path = Data_Pangenome.INSTANCE.dirs.getCurrComparisonDir();
    	///#  			return true;
    	///#   		}
    	///#   	}
    	
    	return false;
    }
    
    
    //////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
    
    public boolean setAnalysesPath(String newdir) {
    	
    	System.out.println("setting analyses basedir path : " + newdir + "\n");
       	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	analyses_path = newdir;


       	System.out.println(" analyses basedir path : " + newdir + "\n");
       	
//    	CentralData.INSTANCE.currWorkspace.update();
//
// //   	CentralData.INSTANCE.preferenses.addToMRU(workspace_path);
//    	
//    	if(new File((CentralData.INSTANCE.currWorkspace.getLastActiveDb())).isDirectory() ){
//        	if(!setDbPath(CentralData.INSTANCE.currWorkspace.getLastActiveDb())){
//   		     setDbToDefault();
//       	    }
//    	}
//    	else{
//    		setDbToDefault();
//    	}
//
//    	if(new File(CentralData.INSTANCE.currWorkspace.getLastActiveComparison()).isDirectory()){
//    		setCurrCompPath(CentralData.INSTANCE.currWorkspace.getLastActiveComparison());
//    	}
//    	else{
//    		setCurrCompPath("");
//    	}
		return true;
    }
    
    public boolean setSchemaPath(String newdir) {
    	
    	System.out.println("setting schema path : " + newdir + "\n");
       	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = uniformPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	schema_path = newdir;

    	System.out.println(" schema path : " + newdir + "\n");
    	
		return true;
    }
    
    public boolean setTrimmedPath(String newdir) {
    	System.out.println("setting trimming path: " + newdir + "\n");
    	
    	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	trimmed_path = newdir;
       	
       	System.out.println(" trimming path: " + newdir + "\n");
       	
       	return true;
    	
    }
    
    public boolean setCurrentAnalysisPath(String newdir) {
    	System.out.println("setting current analysis path: " + newdir + "\n");
    	
    	if (current_analysis_path != "") {
    		
    		CentralData.INSTANCE.currentAnalysis.killContainer();
    	}
    	
    	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = universalPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	current_analysis_path = newdir;
       	
       	System.out.println(" current analysis path: " + newdir + "\n");
       	
       	return true;
    }
    
    public boolean setConfigPath(String newfile) {
    	System.out.println("setting config path: " + newfile + "\n");
    	
    	if(newfile.equals("") || newfile==null  ){
			return false;
		}
    	
       	newfile = Tools.uniformPath(newfile);
       	newfile = universalPath(newfile);
       	
       	if(!Tools.MakeSureFileExists(newfile)) {
			return false;
		}
       	
       	config_path = newfile;
       	
       	CentralData.INSTANCE.currentAnalysis.setRunConfigurations();
       	
       	System.out.println(" config path: " + newfile + "\n");
       	
       	return true;
    }
    
    public String getTrimmedPath() {
    	
    	if (trimmed_path == "") {
    		
    		String newdir;
    		
    		newdir = current_analysis_path;
    		newdir = Tools.mergePaths(newdir, "trimmed");
    		
    		setTrimmedPath(newdir);
    	}
    	
    	return trimmed_path;
    }
    
    public boolean setAssemblyPath(String newdir) {
    	System.out.println("setting assembly path: " + newdir + "\n");
    	
    	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = universalPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	assembly_path = newdir;
       	
       	System.out.println(" assembly path: " + newdir + "\n");
       	
       	return true;
    }
    
    public String getAssemblyPath() {
    	
    	if (assembly_path == "") {
    		
    		String newdir;
    		
    		newdir = current_analysis_path;
    		newdir = Tools.mergePaths(newdir, "spades");
    		
    		setAssemblyPath(newdir);
    	}
    	return assembly_path;
    }
    
    public boolean setAlleleCallPath(String newdir) {
    	System.out.println("setting allelecall path: " + newdir + "\n");
    	
    	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = universalPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	allelecall_path = newdir;
       	
       	System.out.println(" allelecall path: " + newdir + "\n");
       	
       	return true;
    }
    
    public String getAllelecallPath() {
    	return allelecall_path;
    }
    
    public boolean setContainerBaseDir(String newdir) {
    	System.out.println("setting container base directory: " + newdir + "\n");
    	
    	try {
            Paths.get(newdir);
        } catch (InvalidPathException | NullPointerException ex) {
            return false;
        }
    	
    	container_base_dir = newdir;
    	
    	System.out.println(" container base directory: " + newdir + "\n");
    	
        return true;
    }
    
	public String createProcessDir(String software) {

		String processPath = Paths.get(current_analysis_path, software).toString();
		
		File dir = new File(processPath);
		
		if (!dir.mkdirs()) {
			
			if (Tools.MakeSureDirExists(processPath)) {
				
				System.out.println("Process directory already exists: " + dir.getAbsolutePath());
			}
			else {
				System.out.println("Failed to create directory: " + dir.getAbsolutePath());
			}
			
		}
		System.out.println("Process: " + software);
		
		return processPath;
		
	}
	
	public String getContainerPath(String path) {
		
		String dir = Tools.getLastDir(path);
		
		Path newpath = Paths.get(container_base_dir, dir);
		 
		return universalPath(newpath.toString());
		
	}
	
    public boolean setKrakenPath(String newdir) {
    	
    	System.out.println("setting kraken path : " + newdir + "\n");
       	if(newdir.equals("") || newdir==null  ){
       		System.out.println("Uppsala1");
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = universalPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
       		System.out.println("Uppsala2");
			return false;
		}
       	
       	kraken_db_path = newdir;

    	System.out.println(" kraken path : " + newdir + "\n");
    	
		return true;
    }
    
    public boolean setCurrentProcessPath(String newdir) {
    	
    	System.out.println("setting current process path : " + newdir + "\n");
       	if(newdir.equals("") || newdir==null  ){
			return false;
		}
    	
       	newdir = Tools.uniformPath(newdir);
       	newdir = universalPath(newdir);

       	if(!Tools.MakeSureDirExists(newdir)) {
			return false;
		}
       	
       	current_process_path = newdir;

    	System.out.println(" current process path : " + newdir + "\n");
    	
		return true;
    	
    }
    
    //////////////////////////////////////////////////////
    /////////////////////////////////////////////////////

   
    public String getStatusLineText(){
    	String statusMsg = "Workspace = [" + Tools.getLastPartOfPath(analyses_path)
    	                 + "]     Analysis = [" + Tools.makeDisplayName(comparison_path)
    	                 + "]    Database = [" //+ Tools_Db.databasepath2DisplayName(db_path)
    	                 + "]  ";  
    	return statusMsg;
    }
    
    
  
    
    //////////////////////////////////////////////////////
    /////////////////////////////////////////////////////
	
	
}
