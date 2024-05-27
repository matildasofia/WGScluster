package parts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;

import centralData.CentralData;
import jakarta.annotation.PostConstruct;
import tools.Tools;



public class PartAnalyses {

	// widgets
	private Tree tree;    
	
	private Text status;
    ////////////////////////////////////////////////////
	// constructor
	public PartAnalyses() {
		
	}

	
	////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
	// empties and re-fills the tree control with data from filesystem
	public void update() {
		
		// empty tree
		 tree.removeAll();
		 System.out.println("Updating list\n");
		// assertion
		if(!CentralData.INSTANCE.dirs.hasWorkspacePath()){
			System.out.println("No wsp-path\n");
			return;
		}
		 //list dirs in workspace
		 File[] wspDirListing = (new File(CentralData.INSTANCE.dirs.getAnalysesPath()).listFiles());

		 // check if data exists
	     if(wspDirListing==null){
	    	 System.out.println("No wsp-listing\n");
	     	 return;
	     }
	    
	     // refill tree
	     for (int i = 0; i < wspDirListing.length; i++) {
	    	
	        // only prosecss dirs		
	    	if(wspDirListing[i].isDirectory()){
	    		
	    		String dirName = wspDirListing[i].getName();
	 //   		System.out.println("dirname: " + dirName + "\n");
	    		String path = Tools.uniformPath(wspDirListing[i].getAbsolutePath());
	    		
	    		//////////////////////////////////////
	    	    ///// A Comparison 
//	    		if (dirName.startsWith("analysis")) {
//			   		
//	    			String displayName = Tools.makeDisplayName(path);
//		    		
//	    			String[] content = wspDirListing[i].list();
//		    		
//	    			int nrsamples = 0;
//	    			
//	    			if (Arrays.asList(content).contains("filelist_pairs.txt")) {
//	    				String listpaired = new File(path, "filelist_pairs.txt").toString();
//	    				try (BufferedReader br = new BufferedReader(new FileReader(listpaired))) {
//	    		            while (br.readLine() != null) {
//	    		                nrsamples ++;
//	    		            }
//	    		        } catch (IOException e) {
//	    		            e.printStackTrace();
//	    		        }
//	    			}
//			   	
//			   		String[] comparisonInfo = CentralData.INSTANCE.dirs.GetInfoFromcomparisonPath(path);
//			   			
//			   		TreeItem row = new TreeItem(tree, SWT.NONE);
//			    	
//			   		if(comparisonInfo[4].equals("true")){
//		    			displayName = displayName + " (Finalized)";
//		    		}
//			   		row.setText(0,displayName);  // name
//			   		row.setText(1,comparisonInfo[3]);   // type
//			   		row.setText(2,String.valueOf(nrsamples));  // nr genomes
//			   		row.setText(3,comparisonInfo[0]);  // created
//			   		row.setText(4,comparisonInfo[1]);  // modified
//			   		row.setText(5,comparisonInfo[4]);  // info (size in bases)
//				    	
//			   		row.setData(wspDirListing[i]);  // data is the File object for the directory
//				    
//				    /// check if it should be selected
////				    if(CentralData.INSTANCE.dirs.getCurrComparisonPath().equals(path)){
////				    	tree.select(row);
////				    	tree.showSelection();
////				    }
//				   
//			    }
	    		
	    		String displayName = Tools.makeDisplayName(path);

	    		String[] content = wspDirListing[i].list();

	    		int nrsamples = 0;

	    		if (Arrays.asList(content).contains("filelist_pairs.txt")) {
	    		    String listpaired = new File(path, "filelist_pairs.txt").toString();
	    		    try (BufferedReader br = new BufferedReader(new FileReader(listpaired))) {
	    		        while (br.readLine() != null) {
	    		            nrsamples++;
	    		        }
	    		    } catch (IOException e) {
	    		        e.printStackTrace();
	    		    }
	    		}

	    		String[] comparisonInfo = CentralData.INSTANCE.dirs.GetInfoFromcomparisonPath(path);

	    		TreeItem row = new TreeItem(tree, SWT.NONE);

	    		if (comparisonInfo[4].equals("true")) {
	    		    displayName = displayName + " (Finalized)";
	    		}
	    		row.setText(0, displayName);  // name
	    		row.setText(1, comparisonInfo[0]);   // type
	    		row.setText(2, String.valueOf(nrsamples));  // nr samples
	    		row.setText(3, comparisonInfo[1]);  // created
	    		row.setText(4, comparisonInfo[2]);  // finalized
//	    		row.setText(5, comparisonInfo[1]);  // info (size in bases)

	    		row.setData(wspDirListing[i]);  // data is the File object for the directory

	    		/// check if it should be selected
	    		// if(CentralData.INSTANCE.dirs.getCurrComparisonPath().equals(path)){
//	    		     tree.select(row);
//	    		     tree.showSelection();
	    		// }
	    	}
	    	

		   		
	    }	
	    
	     tree.update();
	}
	

	  
	public boolean commandDelete() {
		
		return deleteSelectedComparison();
	}
	 
	public boolean isSelection() {
				
		TreeItem[] selection = tree.getSelection();
		
		if(selection.length>0){
			return true;
		}
		return false;
	}
 	/////////////////////////////////////////////////////7
 	/////////////////////////////////////////////////////
 	
	private boolean deleteSelectedComparison(){
		
		TreeItem[] selection = tree.getSelection();
   		
		if(selection.length==0){
			return false;
		}
   		
       	Display display = Display.getCurrent();
       	Shell shell = new Shell(display);
       	MessageBox mb = new MessageBox(shell,SWT.ICON_WARNING | SWT.OK | SWT.CANCEL);
		mb.setText("Warning");
		mb.setMessage("You are about to remove '"  + selection[0].getText() +"' permanently!\n" +	
				      " Are you sure? \n" );
		int response = mb.open();
		
		if(response == SWT.OK){
	    
			
	       	File toRemove =  (File) (selection[0].getData());
			
	       	try{
	       	  if(Tools.deleteDir(toRemove)==true){
	       		    update();
	       		    if(tree.getItemCount()>0){
	       		    	tree.select(tree.getItem(0));
	       		    	ChangeComparisonFromTreeselection();
	       		    }
	       		    else{
	       		    	tree.deselectAll();
	       		    	ChangeComparisonFromTreeselection();
	       		    }
					update();
			  }
			  else {
					System.out.println("Failed to remove: " + toRemove.getName());
					update();
			  }
			}
	       	catch(IOException ioe){
	       		System.out.println("IOException. Failed to remove: " + toRemove.getName());
	       	  return false;
	       	}
//	       	CentralData.INSTANCE.updateStatusBarMessage();
	       	return true;
			}

   		return false;
	}
 	

 	/////////////////////////////////////////////////////7
 	/////////////////////////////////////////////////////
	
	public boolean renameSelectedComparison(){
		
		TreeItem[] selection = tree.getSelection();
   		
		if(selection.length==0){
			return false;
		}
   		
       	Display display = Display.getCurrent();
       	Shell shell = new Shell(display);
       	
 //       Dialog_RenameComparison dlg = new Dialog_RenameComparison(shell);

 //       dlg.open();
       
        update();
//        CentralData.INSTANCE.updateStatusBarMessage();
        
   		return false;
	}
	///////////////////////////////////////////////////
	

	///////////////////////////////////////////////////
	
 	private void ChangeComparisonFromTreeselection(){
 		
 		
 		
 		if(tree.getSelectionCount()==0){
 //			CentralData.INSTANCE.dirs.setCurrCompPath(""); 	
 //			CentralData.INSTANCE.updateStatusBarMessage();
 			return;
 		}
 	    // get selected items
 		TreeItem[] selection = tree.getSelection();

 		//String path = ((File) (selection[0].getData())).getAbsolutePath();
 		
 		//File analysisPath = new File(CentralData.INSTANCE.dirs.getAnalysesPath(), (String) selection[0].getData());
 		String analysisPath = selection[0].getData().toString();
 
 		CentralData.INSTANCE.dirs.setCurrentAnalysisPath(analysisPath);
 		CentralData.INSTANCE.currentAnalysis.setRunConfigurations();
 		
 		
 		
 //		path = Tools.uniformPath(path);
// 		String dirName = Tools.getLastPartOfPath(path);
 		
 //		if(dirName.startsWith("comparison_") || dirName.startsWith("analysis_")){
 	//		System.out.println("Changing comp to:" + path);
// 			CentralData.INSTANCE.dirs.setCurrCompPath(path); 			
 //		}

// 		CentralData.INSTANCE.updateStatusBarMessage();
		
			 			 

 	}
 	
 	
	///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
    ////////////////////////////////////////////////////
 	// Creates the viewparts
	
 	@PostConstruct
 	public void createComposite(Composite parent) {	
		
		
//		System.out.print("Creating view\n");
//		CentralData.INSTANCE.registerpointerProjectManagerPart(this);
	//	Data_ProjectManager.INSTANCE.registerViewPart_WorkspaceOverview(this);
		System.out.println("ProjectManagerPartPointer is registered");
		
		// LAYOUT
		GridLayout layout4 = new GridLayout(4, false);
		parent.setLayout(layout4);	
		
	    tree = new Tree(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
		tree.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1));
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		
		TreeColumn columnName = new TreeColumn(tree, SWT.LEFT);
		columnName.setText("Name");
		columnName.setWidth(300);
		
		TreeColumn columnType = new TreeColumn(tree, SWT.LEFT);
		columnType.setText("Processes");
		columnType.setWidth(200);
		
		TreeColumn columnNrGenomes = new TreeColumn(tree, SWT.LEFT);
		columnNrGenomes.setText("Samples");
		columnNrGenomes.setToolTipText("Number of genomes");
		columnNrGenomes.setWidth(100);
		
		TreeColumn columnCreated = new TreeColumn(tree, SWT.LEFT);
		columnCreated.setText("Performed");
		columnCreated.setToolTipText("Time and date when the analysis was executed");
		columnCreated.setWidth(180);
		
		TreeColumn columnModified = new TreeColumn(tree, SWT.LEFT);
		columnModified.setText("Finalized");
		columnModified.setWidth(180);
		columnModified.setToolTipText("Time and date when the analysis was finalized");
		
//		TreeColumn columnSize = new TreeColumn(tree, SWT.LEFT);
//		columnSize.setText("Finalized");
//		columnSize.setWidth(150);
		
		
		/////////////////////////////////////
		////   keyboard listener
		tree.addListener(SWT.KeyDown, new Listener(){
			public void handleEvent(Event event){
				// DEL
				if(event.character== SWT.DEL) {
					deleteSelectedComparison();
					update();
		       		tree.setFocus();
				}
				// F5
				if(event.keyCode==SWT.F5) {
					update();
		       		tree.setFocus();
				}
			}
		});
		/////////////////////////////////////	

	    /////////////////////////////////////////
		// CLICK ON LIST
		tree.addListener(SWT.Selection, new Listener() {
		      public void handleEvent(Event e) {
		    	  ChangeComparisonFromTreeselection();
		      }
		});
		
	    /////////////////////////////////////////    
		// Mouse double click listner
		tree.addMouseListener(new MouseListener(){
			public void mouseUp(MouseEvent e) {
			}	
			public void mouseDown(MouseEvent e) {			
			}
			public void mouseDoubleClick(MouseEvent e) {
				ChangeComparisonFromTreeselection();
	//			CentralData.INSTANCE.setActivePerspective(CentralData.INSTANCE.dirs.GetCurrentComparisonPerspective());
				
			}
		});
		
		status = new Text(parent, SWT.READ_ONLY);
	    status.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 4, 1));
	    // update the tree control
	    update();
	    tree.setFocus();
	}

 	
	///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////
	
 	@Focus
	public void setFocus() {
		
		CentralData.INSTANCE.setActiveTab("PartAnalyses");
		
 		if(CentralData.INSTANCE.dirs.getAnalysesPath().equals("")){
 			tree.setEnabled(false);
 			status.setText("Current workspace: No Workspace path selected!!!. Please select File->select workspace..." );
 		}
 		else{
 			tree.setEnabled(true);
 			status.setText("Current workspace: " + CentralData.INSTANCE.dirs.getAnalysesPath());
 		}
 			
 
 		
//		CentralData.INSTANCE.updateStatusBarMessage();
		
		
		
		update();
	}
	///////////////////////////////////////////////////////////////////////////////
    /////////////////////////////////////////////////////////////////////////////	
	
}
