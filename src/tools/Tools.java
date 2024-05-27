package tools;



import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.zip.GZIPInputStream;
import java.io.File;
//
//import org.apache.tools.tar.TarEntry;
//import org.apache.tools.tar.TarInputStream;

import centralData.CentralData;



public class Tools {

	//////////////////////////////////////////
	
	public static String stamptime(){
		
		java.util.Date date1= new java.util.Date();
		Timestamp time1 = new Timestamp(date1.getTime());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
	    
		return dateFormat.format(time1);
		
		//return  time1.toString();
	}
	

	////////////////////////////////////////////
	
	public static Date stamptimeDate(){
		
		java.util.Date date1= new java.util.Date();
		return  date1;
	}
	
	////////////////////////////////////////////
	
	public static String Filterstamptime(String stamp){
		if(stamp.contains(":")){
			return stamp.substring(0, stamp.lastIndexOf(":"));
		}
		
		return stamp;
	
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
		
		if(CentralData.INSTANCE.preferenses.isWINDOWS()){
			 path = path.replace("/", "\\");
		}
		
		return  path;
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
		
		if(CentralData.INSTANCE.preferenses.isWINDOWS()){
			 path = path.replace("/", "\\");
		}
		
		return path;
	}
	
	public static String universalPath(String path) {
		
		Path universalPath = Paths.get(path);
		
		return universalPath.toString();
		
	}
	
	public static String mergePaths(String path1, String path2) {
		
		String path = Paths.get(path1, path2).toString();
		
		return path;
		
	}
	
	public static String getMountedPath(String path1, String path2) {
		String path = "";
		
		if(path1.startsWith(path2)) {
			
			path = path1.substring(path2.length());
			path = mergePaths(getLastPartOfPath(path2), path);
			
		}
		
		if(path2.startsWith(path1)) {
			
			path = path2.substring(path1.length());
			path = mergePaths(getLastPartOfPath(path1), path);
					
		}
		
		else {
			
			int index = findIndexOfDifference(path1, path2);
			
			if(path1.length() > path2.length()) {
				
				path = path1.substring(index);
				path = mergePaths(getLastPartOfPath(path2), path);
				
			}
			
			if(path2.length() > path1.length()) {
				
				path = path2.substring(index);
				path = mergePaths(getLastPartOfPath(path1), path);
			}
			
		}
		
		return path;
 		
	}
	
	public static int findIndexOfDifference(String str1, String str2) {
        int minLength = Math.min(str1.length(), str2.length());

        for (int i = 0; i < minLength; i++) {
            if (str1.charAt(i) != str2.charAt(i)) {
                return i;
            }
        }

        if (str1.length() != str2.length()) {
            return minLength;
        }

        return -1;
    }
	
	
////////////////////////////////////////////
	
	public static boolean MakeSureDirExists(String path){
		
        File testdir = new File(path);
		
		if(!testdir.isDirectory()) {
			
			return testdir.mkdir();
		} else {
			
			return true;
			
		}
		
	}
	
	public static boolean MakeSureFileExists(String path){
		File testfile = new File(path);
		
		if(!testfile.isFile()) {
			return false;
			
		} else {
			
			return true;
			
		}
	}
	
	public static String getFileExtension(String filename) {
        // Check for compressed file extensions and remove them
        String[] compressedExtensions = {"zip", "gz", "tar", "bz2"};
        
        for (String ext : compressedExtensions) {
            if (filename.endsWith("." + ext)) {
                filename = filename.substring(0, filename.length() - (ext.length() + 1));  // +1 for the dot
            }
        }

        // Extract the file extension
        int lastIndex = filename.lastIndexOf('.');
        if (lastIndex != -1) {
            return filename.substring(lastIndex + 1);
        }
        
        return "";
    }
	//////////////////////////////////////////////////
	
	public static boolean emptyDir(File dir) throws IOException{
		
		if(!dir.exists() || !dir.isDirectory()){
			return false;
		}
		
		// list files in genome directory
		 File[] files = dir.listFiles();
	        for(int i=0; i<files.length; i++) {
	        	if(files[i].isDirectory()) {
	        		if(!deleteDir(files[i])){
	        			return false;
	        		}
	        	}
	        	else {
	        		if(!files[i].delete()){
	        			return false;
	        		}
	        	}
	        }
	   	return true;
	}
	
	/////////////////////////////////////////////////////////
	
	public static boolean deleteDir(File dir) throws IOException{
		
		if(!emptyDir(dir)){
			return false;
		}
		return dir.delete();
	}
	
	/////////////////////////////////////////////////////////
	
	public static String FormatMemory(long mem){
		
		 long size = mem;
		 long sizeinbytes = size;
		 
		 String unit = " bytes";
		 
		 if(size>= 1000000000000l){
			 size = size/1000000000000l;
			 unit = " Tbytes (" + sizeinbytes +"bytes)";
		 }
		 if(size>=1000000000){
			 size = size/1000000000;
			 unit = " Gbytes (" + sizeinbytes +"bytes)";
		 }
		 if(size>=1000000){
			 size = size/1000000;
			 unit = " Mbytes (" + sizeinbytes +"bytes)";
		 }
		 else if(size>=1000){
			 size = size/1000;
			 unit = " Kbytes (" + sizeinbytes +"bytes)";
		 }
		 
		 return  size + unit;
	}
	/////
	/////////////////////////////////////////////////////////
	
	public static String FormatFileSize(long filesize){
		
		 long size = filesize;
		 long sizeinbytes = size;
		 String unit = " bytes";
		 
		 if(size>=1000000){
			 size = size/1000000;
			 unit = " Mbytes (" + sizeinbytes +"bytes)";
		 }
		 else if(size>=1000){
			 size = size/1000;
			 unit = " Kbytes (" + sizeinbytes +"bytes)";
		 }
		 
		 return  size + unit;
	}
	////////////////////////////////////////////////////////////
	
	public static String FormatGenomeSize(long genomesize){
		
		 long size = genomesize;
		 long sizeinbases = size;
		 String unit = " bp";
		 
		 if(size>=1000000){
			 size = size/1000000;
			 unit = " Mbp (" + sizeinbases +"bp)";
		 }
		 else if(size>=1000){
			 size = size/1000;
			 unit = " Kbp (" + sizeinbases +"bp)";
		 }
		 
		 return  size + unit;
	}
	
	////////////////////////////////////////////////////////////
//	public static boolean updateCompinfoFile(String comppath){
//		
//		 File compdir = new File(comppath);
//		 File infofile = new File(comppath + "info.geg");
//		
//		 if(!compdir.isDirectory()){
//			 return false;
//		 }
//		 if(!infofile.isFile()){
//			 return false;
//		 }
//		 
//		 String created = "";
// 		String modified = "";
// 		String size = "";
// 		String type ="";
// 		String name ="";
// 		
//		
//		 //////////////////////////////
//        /// read in current content
// 		  Scanner scanner;
//			try {
//				scanner = new Scanner(new FileReader(infofile));
//			
// 		  while ( scanner.hasNextLine() ){
//					String line = scanner.nextLine();
//					
//					if(line.startsWith("Name:")){
//						name = line.substring(5);
//					}
//					if(line.startsWith("Created:")){
//						created = line.substring(8);
//					}
//					if(line.startsWith("Modified:")){
//						modified = line.substring(9);
//					}
//					if(line.startsWith("Size:")){
//						size = line.substring(5);
//					}	
//		
// 		  }
// 		scanner.close();
//			} catch (FileNotFoundException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			
//			//////////////////////////////
//			///  gather new info
//			 long long_size =0;
//			
//			 String[] listing = compdir.list();
//			 
//			 for(int i =0 ; i< listing.length;i++){
//				 String filename= listing[i];
//				 if(filename.endsWith("--")){
//					 File genomeinfo = new File(compdir.getAbsolutePath() + "/" + filename + "/info.geg");
//					 if(!genomeinfo.isFile()){
//						 makeGenomeInfofile(compdir.getAbsolutePath() + "/" + filename );
//					 }
//					 
//					 if(genomeinfo.isFile()){
//						 try {
//								scanner = new Scanner(new FileReader(genomeinfo));
//							
//				 		  while ( scanner.hasNextLine() ){
//									String line = scanner.nextLine();
//									
//									if(line.startsWith("Created:")){
//										created = line.substring(8);
//									}
//									if(line.startsWith("Modified:")){
//										modified = line.substring(9);
//									}
//									if(line.startsWith("Size:")){
//										size = line.substring(5);
//										if(size.length()>0){
//										   long sizeint = Integer.parseInt(size);
//										   long_size += sizeint;
//										}
//									}	
//						
//				 		  }
//				 		scanner.close();
//							} catch (FileNotFoundException e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//							}
//						 
//						 
//					 }
//					 
//				 }
//			 }
//			 
//			 ////////////////////////////////////7
//			 // write info
//			 
//		
//			 
//			 
//				 
//				 
//				 File geg = new File(comppath+ "info.geg");
//				 FileWriter fw;
//				try {
//					fw = new FileWriter(geg,false);
//					BufferedWriter	saveFile = new BufferedWriter(fw);
//					
//					saveFile.write("Name:" + name +"\n");
//					saveFile.write("Created:" + created +"\n");
//					saveFile.write("Modified:" + modified +"\n");
//					saveFile.write("Size:" + long_size + "\n");
//					
//					saveFile.close();
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//				 
//			
//			
//				 
//			 
//			return true;
//		 
//	}
////	
//	/////////////////////////////////////////////////////////////
//	public static boolean makeGenomeInfofile(String dir){
//		
//		FileContent_GenomeInfofile fc = new FileContent_GenomeInfofile();
//		
//		fc.genomedir = dir;
//		return fc.make();
//	}
	/////////////////////////////////////////////////////////////
	public static String FilterIllegalPathChars(String name){
		
		String result = name.replaceAll(" ", "_");
		result = result.replaceAll("\t", "_");
		result = result.replaceAll("/", "_");
		result = result.replaceAll(":", "_");
		result = result.replace("\\", "_");
		
		return result;
	}
	
	public static List<String> getEmptyList(List<String> list) {
		
	    return list != null ? list : Collections.emptyList();
	    
	}
///////////////////////////////////////////////////////////////
//	public static struct_CompdirInfoFile readComparisonInfofile(String dir){
//	
//		File CompDirinfo;
//		
//		String created = "";
//		String modified = "";
//		String size = "";
//		long longsize = 0;
//		
//		struct_CompdirInfoFile retvalue = new struct_CompdirInfoFile();
//		
//		if(dir.endsWith("/")){
//			 CompDirinfo = new File(dir + "info.geg");
//		}
//		else {
//			 CompDirinfo = new File(dir + "/info.geg");
//		}
//		
//		
//		if(CompDirinfo.isFile()){
//		  Scanner scanner;
//		try {
//			scanner = new Scanner(new FileReader(CompDirinfo));
//		
//		  while ( scanner.hasNextLine() ){
//				String line = scanner.nextLine();
//				
//				if(line.startsWith("Created:")){
//					created = line.substring(8);
//					
//				}
//				if(line.startsWith("Modified:")){
//					modified = line.substring(9);
//				}
//				if(line.startsWith("Size:")){
//					size = line.substring(5);
//					
//					if(size.length()>0){
//						longsize = Long.parseLong(size);
//					}
//					
//				}	
//	
//		  }
//		scanner.close();
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		}
//		
//		retvalue.created = created;
//		retvalue.modified = modified;
//		retvalue.size = size;
//		retvalue.longsize = longsize;
//		
//		return retvalue;
//	}
//	

	
	/////////////////////////////////////////////////////////////
	public static boolean copyDir(File from_dir, File to_dir) throws IOException{
		
		// not valid from_dir
		if(!from_dir.isDirectory() ){
			return false;
		}
		
		// already exists
		if(to_dir.isDirectory()){
			return false;
		}
		
		to_dir.mkdir();
		
        // list files in from-dir 
	   	String[] filearray = from_dir.list();
		
		for (int j = 0; j < filearray.length; j++){
		   		String filename = filearray[j];
		   		
		   	    File theFile = new File(Tools.uniformFilePath(from_dir.getAbsolutePath() +"/" + filename)); 
		   	    
		   	    if(theFile.isFile()){
		   	    	try { 
			   			
			   			Tools.copy(Tools.uniformFilePath(from_dir.getAbsolutePath() +"/" + filename), Tools.uniformFilePath(to_dir.getAbsolutePath() + "/" + filename ));
			   				
					}
				   	catch (IOException e) {System.err.println(e.getMessage());
							;
					}
		   	    }
		   	    else if(theFile.isDirectory()){
		   	    	try { 
		   	    		File todir = new File(Tools.uniformFilePath(to_dir.getAbsolutePath() + "/" + filename));
		   	    		todir.mkdir();
			   			Tools.copyDir(theFile, todir );
			   				
					}
				   	catch (IOException e) {System.err.println(e.getMessage());
							;
					}
		   	    }

		   		
		   }	// end for all files	   		
	  return true;	   	
	}
	
	
	/////////////////////////////////////////////
	
//	public static void copyClipboardGenomes(File destDir ){
//		copyGenomes(CentralData.INSTANCE.currClipboard.GenomeClipboard, destDir );
//	}

	
	//////////////////////////////////////////
	
	public static void copyGenomes(ArrayList <String> tocopyFile,  File destBaseDir ){
		
		// the basedir for new genome copies must exist
		if(!destBaseDir.isDirectory()){
			return;
		}
		/// Start copying files
		for (int i = 0; i < tocopyFile.size(); i++){
			
			File from_dir = new File(tocopyFile.get(i));
			// a genome must be a directory
			if(!from_dir.isDirectory()){
				continue;
			}
			// the to-dir
		   	File to_dir = new File(Tools.uniformFilePath(destBaseDir.getAbsolutePath() +"/" + from_dir.getName()));
   	
		   	try {
				copyDir(from_dir,to_dir);
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		} // end for
	}
	
	///////////////////////////////////////////////7
    //	Method for copying files...
	public static void copy(String fromFileName, String toFileName)
		throws IOException {
	    File fromFile = new File(fromFileName);
	    File toFile = new File(toFileName);

	    if (!fromFile.exists())
	    	throw new IOException("FileCopy: " + "no such source file: " + fromFileName);
	    if (!fromFile.isFile())
	    	throw new IOException("FileCopy: " + "can't copy directory: " + fromFileName);
	    if (!fromFile.canRead()) 
	    	throw new IOException("FileCopy: " + "source file is unreadable: " + fromFileName);

	    if (toFile.isDirectory()){
	    	toFile = new File(toFile, fromFile.getName());	
	    }



	    FileInputStream from = null;
	    FileOutputStream to = null;
	    try {
	    	from = new FileInputStream(fromFile);
	    	to = new FileOutputStream(toFile);
	    	byte[] buffer = new byte[4096];
	    	int bytesRead;

	    	while ((bytesRead = from.read(buffer)) != -1){
	    		to.write(buffer, 0, bytesRead); // write
	    	}
	    } 
	    finally {
	    	if (from != null){
	    		try {from.close();}
	    		catch (IOException e) {;}
	        } 
	    	if (to != null){
	    		try {to.close();} 
	    		catch (IOException e) {;}
	    	}
	    }
	}	
	
	
	public static boolean  copyFastaAsGbk(File FastaFile, File GbkFile){
	

		
		if(!FastaFile.isFile()){
		   return false;
		}
		
		int seqcnt = 0;
		int subcounter = 1;
		  Scanner scanner;
		try {
			try {
				scanner = new Scanner(new FileReader(FastaFile));
			
				BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter(GbkFile,false));
				
		
		  while ( scanner.hasNextLine() ){
				String line = scanner.nextLine();
				
				
				
				if(line.startsWith(">")){
					String content = "";
					content = line.substring(1);  // remove '>'
					
					if(seqcnt>0){
						out.write("//\n");
					}
					out.write("LOCUS       " + content  + "\n");
					out.write("ACCESSION   " + content  + "\n");
					out.write("DEFINITION  " + content  + "\n");
					out.write("FEATURES             Location/Qualifiers" + "\n");
		    		out.write("ORIGIN      \n");
		    		seqcnt++;
		    		subcounter = 1;
				}
				else{
					
					String formated = line.replaceAll("[0-9]", "");
					formated = formated.replaceAll(" ", "");
					String prefix = "";
					if(subcounter<10) { prefix = "        " + subcounter + " "; }
					else if(subcounter<100) { prefix = "       " + subcounter + " "; }
					else if(subcounter<1000) { prefix = "      " + subcounter + " "; }
					else if(subcounter<10000) { prefix = "     " + subcounter + " "; }
					else if(subcounter<100000) { prefix = "    " + subcounter + " "; }
					else if(subcounter<1000000) { prefix = "   " + subcounter + " "; }
					else if(subcounter<10000000) { prefix = "  " + subcounter + " "; }
					else if(subcounter<100000000) { prefix = " " + subcounter + " "; }
					else if(subcounter<1000000000) { prefix = "" + subcounter + " "; }
					
					out.write(prefix);
					
                   for(int i=0; i<formated.length(); i++)
                   {
                	   if(i>0 && i%10 == 0){
                		   out.write(" "); 
                	   }
                	   out.write(formated.charAt(i));
                	   
                   }
                   
                   out.write( "\n");
					
					
				    subcounter += formated.length();
				}
				
				
					
			} // end while
		  
		  if(seqcnt>0){
				out.write("//\n");
			}
		  
		
	
		  scanner.close();
		  out.close();
			
		}
		finally{
			
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

		
		return true;
	}
	
	/////////////////////////////////////////////
	
	public static boolean  copyFASTQAsGbk(File FASTQFile, File GbkFile){
	

		
		if(!FASTQFile.isFile()){
		   return false;
		}
		
		int seqcnt = 0;
//		int subcounter = 1;
		String tag ="";
		  Scanner scanner;
		try {
			try {
				scanner = new Scanner(new FileReader(FASTQFile));
			
				BufferedWriter out;
				try {
					out = new BufferedWriter(new FileWriter(GbkFile,false));
				
					int part=0;
		
		  while ( scanner.hasNextLine() ){
				String line = scanner.nextLine();
				
				
				
				if(line.startsWith("@")){
					String content = "";
					content = line.substring(1);  // remove '@'
					
					if(seqcnt>0){
						tag = "//\nLOCUS       " + content  + "\nORIGIN      \n";
					}
					else{
						tag = "LOCUS       " + content  + "\nORIGIN      \n";
					}
	
		    		part=1;
				}  // end if starts with @
				else if(line.startsWith("+")){
					part=0;
				}
				else if(part==1){
					
					// check this is a true sequence row
					line = line.toLowerCase();
					boolean lineOK = true;
					
					for(int i=0; i<line.length(); i++)
	                   {
						char ch = line.charAt(i);
	                	   if(ch != 'a' && ch != 'c' && ch != 'g' && ch != 't' && ch != 'n' ){
	                		   tag = "";
	                		   part=0;
	                		   lineOK = false;
	                	   }

	                   }
					
					// line check OK print the sequence
					if(lineOK){
						
						out.write( tag);
						seqcnt++;
			    		

					String prefix = "        1 "; 

					out.write(prefix);
					
                   for(int i=0; i<line.length(); i++)
                   {
                	   if(i>0 && i%10 == 0){
                		   out.write(" "); 
                	   }
                	  
                	   
                	   if(i>0 && i%60 ==0){
                		   
                		prefix = "";
       					if(i<10) { prefix = "        " + i + " "; }
       					else if(i<100) { prefix = "       " + i + " "; }
       					else if(i<1000) { prefix = "      " + i + " "; }
       					else if(i<10000) { prefix = "     " + i + " "; }
       					else if(i<100000) { prefix = "    " + i + " "; }
       					else if(i<1000000) { prefix = "   " + i + " "; }
       					else if(i<10000000) { prefix = "  " + i + " "; }
       					else if(i<100000000) { prefix = " " + i + " "; }
       					else if(i<1000000000) { prefix = "" + i + " "; }
       					
       					out.write("\n" + prefix ); 
                	   }
                	   
                	   out.write(line.charAt(i));
                   }
                   
                   out.write( "\n");
					
	
				    part=0;
				    
					}  // end lineOK
				}
				
				
					
			} // end while
		  
		  if(seqcnt>0){
				out.write("//\n");
			}
		  
		
	
		  scanner.close();
		  out.close();
			
		}
		finally{
			
		}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	

		
		return true;
	}
	/////////////////////////////////////////////
	
	public static String[] parseGegFileLine(String line){
		
		String ret[] = new String[2];
		ret[0]="";
		ret[1]="";
		
		if(!line.contains(":")){
			return ret;
		}
		
		ret[0] = line.substring(0, line.indexOf(":")+1);
		ret[1] = line.substring(line.indexOf(":") +1);
		
		return ret;
		
	}
	/////////////////////////////////////////////
	public static String getLastPartOfPath(String path ){  // returns last
		
		if(CentralData.INSTANCE.preferenses.isWINDOWS()){
			if(path.endsWith("\\")){
				path = path.substring(0, path.length()-1);
			}
			
			if(path.contains("\\")){
				path = path.substring(path.lastIndexOf("\\")+1);
			}
		}
		else{
			if(path.endsWith("/")){
				path = path.substring(0, path.length()-1);
			}
			
			if(path.contains("/")){
				//path = path.substring(path.lastIndexOf("/")+1);
				path = path.substring(path.lastIndexOf("/")+1);
			}
		}
		
		return path;
			
	}
	
	public static String getLastDir(String path) {
		
		File file = new File(path);
		
		if(file.isDirectory()) {
			
			path = file.getName();	
		}
		else {
			
			path = String.join("/", file.getParentFile().getName(), file.getName());
		}
		
		if(!path.startsWith("/")) {
			
			path = String.join("", "/", path);
		}
		
		return universalPath(path);
		
		
	}
	
	public static String getFirstPartOfPath(String _path) {
		
		Path path = Paths.get(_path);

        return path.getName(0).toString();
       
	}
	
	public static String makeDisplayName(String path){
		
		String dirname = getLastPartOfPath(path);
		
		String displayname = dirname;
		
//		if(displayname.contains("_")){
//			displayname = displayname.substring(displayname.indexOf("_") +1);
//		}
		
		return displayname;
	}
	/////////////////////////////////////////////
	
	public static String getBaseDir(String path ){
		
		if(CentralData.INSTANCE.preferenses.isWINDOWS()){
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
	
	/////////////////////////////////////////////
	
	public static String removeAllCharsAfterPattern(String str, String pattern ){
		
		if(!str.contains(pattern)){
			return str;
		}
		return str.substring(0, str.indexOf(pattern)+pattern.length());
		
	}
	/////////////////////////////////////////////
	
	public static boolean  isGbkFile(String pathOrFile ){
		
		if(pathOrFile.endsWith("/")){
			pathOrFile = pathOrFile.substring(0, pathOrFile.length()-1);
		}
		
		if(pathOrFile.endsWith(".gbk")){ return true; }
		if(pathOrFile.endsWith(".gbff")){ return true; }
		if(pathOrFile.endsWith(".gb")){ return true; }
		
		return false;
		
		
	}
	
	/////////////////////////////////////////////
	
	public static boolean  isGegFile(String pathOrFile ){
		

		
		if(pathOrFile.endsWith(".geg")){ return true; }
		
		return false;
		
		
	}

	/////////////////////////////////////////////
	
	public static boolean  isArchiveFile(String pathOrFile ){
		

		
		if(pathOrFile.endsWith(".gz")){ return true; }
		if(pathOrFile.endsWith(".tgz")){ return true; }
		if(pathOrFile.endsWith(".tar.gz")){ return true; }
		
		return false;
		
		
	}


/////////////////////////////////////////////
	
	public static String[] getDirListing(String path ){
		
	    File dir = new File(path);
		return dir.list();
	}
	
/////////////////////////////////////////////
	
	public static String[] getgegeneesGenomeDirListing(String path ){
		
	    File dir = new File(path);
		String[] templist =  dir.list();
		
		int counter = 0;
		for(int i=0; i<templist.length;i++){
			
//			if(Tools_Db.isGenomeDirName(templist[i])){
//				counter++;
//			}
		}
		
		String[] retval = new String[counter];
		int counter2 = 0;
//		for(int i=0; i<templist.length;i++){
//			if(Tools_Db.isGenomeDirName(templist[i])){
//				retval[counter2] = templist[i];
//				counter2++;
//			}
//			
//		}
		return retval;
	}
	
/////////////////////////////////////////////
	
	public static String trimFileExtension(String filename, String filters ){
		
		if(filters.equals("") ){
			return filename;
		}
		if(filters.contains("\t")){
			String[] filtrarray = filters.split("\t");
			for(int i=0; i<filtrarray.length;i++){
				if(filename.endsWith(filtrarray[i])){
					filename = filename.substring(0, filename.lastIndexOf(filtrarray[i]));
					return filename;
				}
			}
		}
		else{
			
			if(filename.endsWith(filters)){
				filename = filename.substring(0, filename.lastIndexOf(filters));
				return filename;
			}
		}
		
		return filename;
	    
	}
	
	public static double round(double number, int decimalPlaces){
		double modifier = Math.pow(10.0, decimalPlaces);
		return Math.round(number * modifier) / modifier;			
    }
	
	
	public static void DumpDirectoryInfo(String path, BufferedWriter Info) throws IOException{
				
		File compdir = new File( Tools.uniformPath(path));
		
		File[] files = compdir.listFiles();
		
		for(int i=0;i<files.length;i++){
			
			Info.write(files[i].getAbsolutePath());
			Info.write("\t");
			
			if(files[i].canExecute()) { Info.write("X"); } 
			else { Info.write("x"); } 
			if(files[i].canRead()) { Info.write("R"); } 
			else { Info.write("r"); } 
			if(files[i].canWrite()) { Info.write("W"); } 
			else { Info.write("w"); } 
			if(files[i].isDirectory()) { Info.write("D"); } 
			else { Info.write("-"); } 
			if(files[i].isFile()) { Info.write("F"); } 
			else { Info.write("-"); } 
			if(files[i].isHidden()) { Info.write("H"); } 
			else { Info.write("-"); } 
			Info.write("\t");
			
			Info.write(new Timestamp(files[i].lastModified()).toString());
			Info.write("\t");
			
			Info.write(Tools.FormatFileSize(files[i].length()));
			Info.write("\n");
	
			if(files[i].isDirectory()) 
			   { DumpDirectoryInfo(files[i].getAbsolutePath(), Info); } 
		}
		
    }
	
	
	public static void DumpFiles(String path, BufferedWriter Info, String extensions, String skippPattern) throws IOException{
		
		File compdir = new File( Tools.uniformPath(path));
		
		String[] exts = null;
		
		if(extensions.contains("\t")){
			exts = extensions.split("\t");
		}
		else {
			exts = new String[1];
			exts[0] =  extensions;
		}
		if(extensions.equals("")){
			return;
		}
		
		File[] files = compdir.listFiles();
		
		for(int i=0;i<files.length;i++){
			
			if(files[i].isDirectory()) { 
				DumpFiles(files[i].getAbsolutePath(),Info,extensions, skippPattern); 
			} 
			else{
				
				for(int ex=0; ex<exts.length; ex++){
					
					if(files[i].getAbsolutePath().endsWith(exts[ex]) && !(files[i].getName().contains(skippPattern))){
						Info.write("##FILE##" + files[i].getAbsolutePath() + "\n" );
						
						File loadFile = new File(Tools.uniformFilePath(files[i].getAbsolutePath()));
				
						Scanner scanner=null;
						
						try {
							scanner = new Scanner(new FileReader(loadFile));
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
						while(scanner.hasNextLine()){
							String line = scanner.nextLine();
		
							Info.write(line + "\n");
						}
						scanner.close();
						
					}
					
					
				}
				
			}
			
			
		}
		
    }
	
	
	public static String unzip(String arg_filepath){
		
		String filepath = Tools.uniformFilePath(arg_filepath);
		boolean tar = true;
		boolean unknown = false;
		
		if(arg_filepath.endsWith(".tar.gz")){
			tar = true;
		}
		else if(arg_filepath.endsWith(".tgz")){
			tar = true;
		}
		else if(arg_filepath.endsWith(".tar")){
			tar = true;
		}
		else if(arg_filepath.endsWith(".gz")){
			tar = false;
		}
		
		else{
			unknown = true;
		}
		
		if(unknown)	return "Unknown";
		
		String Status = "Started";
		System.out.println("started unziping");
		String Targetdir = Tools.getBaseDir(filepath);
		
		try{
			if(tar){ // If filename ends with .tar or .tgz or .tar.gz.
//				TarInputStream tin = new TarInputStream(new GZIPInputStream(new FileInputStream(new File(filepath))));
//				//get the first entry in the archive
//				
//				TarEntry tarEntry = tin.getNextEntry();
//
//				while (tarEntry != null){//create a file with the same name as the tarEntry
//
//					File destPath = new File(Targetdir.toString() + File.separatorChar + tarEntry.getName());
//
//					if(tarEntry.isDirectory()){
//						destPath.mkdir();
//					} 
//					else {
//						FileOutputStream fout = new FileOutputStream(destPath);
//						tin.copyEntryContents(fout);
//						fout.close();
//					}
//
//					tarEntry = tin.getNextEntry();
//				}
//			
//				tin.close();
//				new File(filepath).delete();
			}
			else{	// Else trying gzip.
				GZIPInputStream gin = new GZIPInputStream(new FileInputStream(new File(filepath)));
				
				String outFilePath = filepath.replace(".gz", "");
			    OutputStream out = new FileOutputStream(outFilePath);
			 
			    byte[] buf = new byte[1024];
			    int len;
			    while ((len = gin.read(buf)) > 0)
			        out.write(buf, 0, len);
			 
			    gin.close();
			    out.close();
			 
			    new File(filepath).delete();
				
			}
		}
		catch(Exception e) {

			e.printStackTrace();

			System.out.println(e.getMessage());
		}
		
//		Thread_MakeGenomeInfofile gif = new Thread_MakeGenomeInfofile(Targetdir);
//		gif.start();
		
		Status = "Completed";
		return Status;
	}
	
	public static void waitMiliseconds(long milliseconds){
		
	//// Check status every fifth minute
		long starttime = System.nanoTime();
		long currenttime = System.nanoTime();
		do{
			currenttime = System.nanoTime();
		}while( currenttime-starttime < (milliseconds*1000000l) );
		
		
		
	}
	
	public static boolean isMapOfStringAndObject(Map<?, ?> map) {
	    for (Object key : map.keySet()) {
	        if (!(key instanceof String) || !(map.get(key) instanceof Object) && !(map.get(key) == null)) {
	            return false;
	        }
	    }
	    return true;
	}
}

