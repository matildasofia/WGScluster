package parts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Decorations;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolTip;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ListSelectionDialog;
import org.yaml.snakeyaml.Yaml;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.e4.ui.workbench.UIEvents.Window;
import org.eclipse.e4.ui.di.Focus;
import org.eclipse.e4.ui.workbench.UIEvents;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;

import centralData.CentralData;
import centralData.WriteConfigurations;
import data.DataHandler;
import data.FileList;
import jakarta.annotation.PostConstruct;
import pipeline.RunPairFiles;
import tools.Tools;

public class PartRunconfigurations {

	private Shell shell;
	private Tree tree;
	
	WriteConfigurations writeconfigurations = new WriteConfigurations();
	
	public PartRunconfigurations() {
		Display display = Display.getDefault();
        shell = new Shell(display);
        
        shell.setText("PartRunconfigurations");
        
        writeconfigurations.setConfigs();
	}
	
//	private void updateTreeHeight(int numFiles, GridData gridData) {
//		if (numFiles != 0) {
//			gridData.heightHint = numFiles * tree.getItemHeight();
//		}
//		tree.setLayoutData(gridData);
//		tree.getParent().layout(true);
//	}
	
	private void createFileTable() {
		String[] columnNames = { "R1", "R2", "Base Name", "Type", "R1 Name", "R2 Name" };
		
		for (String name : columnNames) {
			TreeColumn column = new TreeColumn(tree, SWT.NONE);
			column.setText(name);
			column.setWidth(100);
		}
		
	}
	
	private void updateFileTable(GridData gridData) {
		
		String path = "";
		String fileName = "";
		
		fileName = CentralData.INSTANCE.currentAnalysis.getPairedFileList();
		path = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
		path = Tools.mergePaths(path, fileName);
		
		//String filelistPath = CentralData.INSTANCE.dirs.getCurrentAnalysisPath() + "/filelist_pairs.txt";
		
		try {
            List<String> lines = Files.readAllLines(Paths.get(path));
            for (String line : lines) {
                String [] inputs = line.split("\t");
        		TreeItem row = new TreeItem(tree, SWT.NONE);
                
                for (int i = 0; i < inputs.length; i ++) {
        			row.setText(i, inputs[i]);
                }
      
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
	}

	
 	@PostConstruct
 	public void createComposite(Composite parent) {
 		
 		GridLayout layout1 = new GridLayout(1, false);
 		GridLayout layout2 = new GridLayout(2, false);
 		GridLayout layout3 = new GridLayout(3, false);
 		
 		parent.setLayout(layout2);
 		
 		
// 		Label analysis = new Label(parent, SWT.BOLD);
// 		analysis.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
// 		
// 		String name = "Project: ";
// 		name += CentralData.INSTANCE.currentAnalysis.getName();
// 		analysis.setText(name);
 		
 		
 		Composite composite1 = new Composite(parent,SWT.NONE);
 		composite1.setLayout(layout1);
 		
 		Point size = shell.getSize();
 		
 		Button folderChoiceBtn = new Button(composite1, SWT.NONE);
 		folderChoiceBtn.setText("Choose");

 		
 		tree = new Tree(composite1, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
 		tree.setSize(tree.computeSize(SWT.DEFAULT, size.y - 10));

		GridData gridData = new GridData(SWT.LEFT, SWT.FILL, false, true);
		gridData.minimumHeight = size.y - 10;
		
		tree.setHeaderVisible(true);
		tree.setLinesVisible(true);
		tree.setLayoutData(gridData);
		
		createFileTable();
 		
 		
 		folderChoiceBtn.addListener(SWT.Selection, new Listener() {
 			public void handleEvent(Event event) {
 				
 				Boolean hasAnalysis = CentralData.INSTANCE.dirs.hasCurrentAnalysis();
 				String currentAnalysis = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
 				
 				if (!hasAnalysis) {
 					
 					MessageDialog.openError(shell, "Error", "No analysis selected");
 					
 					return;
 				}
 	
 				DirectoryDialog dialog = new DirectoryDialog(parent.getShell());
                dialog.setText("Select a folder");
                dialog.setFilterPath(CentralData.INSTANCE.dirs.getDataPath());
                
                String selectedFolder = dialog.open();
                
                if (selectedFolder != null) {

                    File folder = new File(selectedFolder);
                    List<File> files = Arrays.asList(folder.listFiles());
                    
                    DataHandler inputData = new DataHandler();
                    FileList filelist = new FileList();
                    
                    inputData.setData(files);
                    filelist.setData(inputData);
                    filelist.makeFileList(currentAnalysis);

                    RunPairFiles.run();
                    inputData.setPairedData(currentAnalysis);
                    
                    updateFileTable(gridData);
                    
                }
 			}
 		});
 		

 		// Choice of processes to run
 		Composite composite3 = new Composite(parent, SWT.TOP);
 		composite3.setLayout(layout3);
 		GridData compositeGridData = new GridData(SWT.FILL, SWT.FILL, true, true);
        composite3.setLayoutData(compositeGridData);
 		
 		Label analyseslabl = new Label(composite3, SWT.NONE);
 		analyseslabl.setLayoutData(new GridData(SWT.LEFT, SWT.BOTTOM, false, false, 3, 1));
 		analyseslabl.setText("Processes to run:");
 		
 		Label trimlabl = new Label(composite3, SWT.NONE);
 		trimlabl.setText("Trimming");
 		
 		Combo trimchoice = new Combo(composite3, SWT.READ_ONLY);
 		trimchoice.add("fastp");
 		trimchoice.add("Trimmomatic");
 		trimchoice.add("Don't run process");
 		trimchoice.select(0);
 		
 		trimchoice.addListener(SWT.Selection, new Listener() {
 			
 			public  void handleEvent(Event event) {
 				
 				String software1 = trimchoice.getItem(0).toLowerCase();
 				String software2 = trimchoice.getItem(1).toLowerCase();
 				int index = trimchoice.getSelectionIndex();
 				
 				if (index == 2 || trimchoice.getText().equals("Don't run process")) {
 					
 					writeconfigurations.remove(software1);
 					writeconfigurations.remove(software2);
 				}
 				if (index == 0) {
 					writeconfigurations.remove(software2);
 					writeconfigurations.restart(trimchoice.getText());
 				}
 				if (index == 1) {
 					writeconfigurations.remove(software1);
 					writeconfigurations.restart(trimchoice.getText());
 				}
 				
 			}
 			
 		});
 		
 		Button trimsettings = new Button(composite3, SWT.NONE);
 		
 		//Setting image configurations
 		int buttonHeight = trimsettings.computeSize(SWT.DEFAULT, SWT.DEFAULT).y;
 		Image setting = new Image(Display.getDefault(), PartRunconfigurations.class.getResourceAsStream("/icons/settings.982x1024.png"));
 		Image resizedImage = new Image(Display.getDefault(), setting.getImageData().scaledTo(buttonHeight-10, buttonHeight-10));
 		trimsettings.setImage(resizedImage);
 		
 		trimsettings.addListener(SWT.Selection, new Listener() {

 			public void handleEvent(Event event) {
 				
 				String software = trimchoice.getText().toLowerCase();
 				int index = trimchoice.getSelectionIndex();
 				
 				TrimmingSettings dialog = new TrimmingSettings(parent.getShell());
 				
 				System.out.println("Ischanged: " + writeconfigurations.isChanged(software));
 				
 				if(writeconfigurations.isChanged(software)) {
 					
 					writeconfigurations.restart(software);
 				}
 				
 				dialog.setOptions(writeconfigurations.getContOptions(software));
 				dialog.openDialog();
 				
 				writeconfigurations.setTrimmingConfigurations(dialog.getSelection(), software);
 				
 				if( index == 0 ) {
 					
 					writeconfigurations.remove(trimchoice.getItem(1).toLowerCase());
 					
 				}
 				if( index == 1 ) {
 					
 					writeconfigurations.remove(trimchoice.getItem(0).toLowerCase());
 					
 				}

 				}
 			}); 		
 		
 		Label assemblylabl = new Label(composite3, SWT.NONE);
 		assemblylabl.setText("Assembly");
 		
 		Combo assemblychoice = new Combo(composite3, SWT.READ_ONLY);
 		assemblychoice.add("Spades");
 		assemblychoice.add("Don't run process");
 		assemblychoice.select(0);
 		
 		assemblychoice.addListener(SWT.Selection, new Listener() {
 			
 			public  void handleEvent(Event event) {
 				
 				String software = assemblychoice.getItem(0).toLowerCase();
 				int index = assemblychoice.getSelectionIndex();
 				
 				if (index == 1 || assemblychoice.getText().equals("Don't run process")) {
 					
 					writeconfigurations.remove(software);
 				}
 				
 			}
 			
 		});
 		
 		Button assemblysettings = new Button(composite3, SWT.PUSH);
 		assemblysettings.setImage(resizedImage);
 		
 		assemblysettings.addListener(SWT.Selection, new Listener() {
 			
 			public void handleEvent(Event event) {
 				
 				String software = assemblychoice.getText().toLowerCase();
 				
 				SpadesSettings dialog = new SpadesSettings(parent.getShell());
 				dialog.setOptions(writeconfigurations.getOptions(software));
 				dialog.openDialog();
 				
 				writeconfigurations.setConfigurations(dialog.getSelection(), software);

 			}
 			
 		});
 		
 		Label allelecalllabl = new Label(composite3, SWT.NONE);
 		allelecalllabl.setText("Allelecall");
 		
 		Combo allelecallchoice = new Combo(composite3, SWT.READ_ONLY);
 		allelecallchoice.add("ChewBBACA");
 		allelecallchoice.add("Don't run process");
 		allelecallchoice.select(0);
 		
 		allelecallchoice.addListener(SWT.Selection, new Listener() {
 			
 			public  void handleEvent(Event event) {
 				
 				String software = allelecallchoice.getItem(0).toLowerCase();
 				int index = allelecallchoice.getSelectionIndex();
 				
 				if (index == 1 || allelecallchoice.getText().equals("Don't run process")) {
 					
 					writeconfigurations.remove(software);
 				}
 				
 			}
 			
 		});
 		
 		Button allelecallsettings = new Button(composite3, SWT.PUSH);
 		allelecallsettings.setImage(resizedImage);
 		
 		allelecallsettings.addListener(SWT.Selection, new Listener() {
 			
 			public void handleEvent(Event event) {
 				
 				String software = allelecallchoice.getText().toLowerCase();
 				
 				ChewbbacaSettings dialog = new ChewbbacaSettings(parent.getShell());
 				dialog.setOptions(writeconfigurations.getOptions(software));
 				dialog.openDialog();
 				
 				writeconfigurations.setConfigurations(dialog.getSelection(), software);

 			}
 			
 		});
 		
 		
 		Button runBtn = new Button(composite3, SWT.BUTTON1 | SWT.BOTTOM);
 		runBtn.setText("Run");
 		
 		ToolTip toolTip = new ToolTip(shell, SWT.BALLOON | SWT.ICON_INFORMATION);
        toolTip.setText("Run the pipeline");
        runBtn.setToolTipText("Run the pipeline");
        
        runBtn.addListener(SWT.MouseHover, event -> {
            toolTip.setLocation(Display.getDefault().map(null, runBtn, runBtn.getBounds().x, runBtn.getBounds().y + runBtn.getBounds().height));
            toolTip.setVisible(true);
        });
 		
 		runBtn.addListener(SWT.Selection, new Listener(){
	    	public void handleEvent(Event event) {
//	    		if (!CentralData.INSTANCE.inputData.hasInputData()) {
//	    			
//	    			MessageDialog.openError(shell, "Error", "No data selected");
//	    			return;
//	    			
//	    		}
	    		writeconfigurations.setStartTime();
	    		
	    		if (writeconfigurations.choicesMade()) {
	    			
	    			String configPath;
	    			
	    			configPath = writeconfigurations.writeToFile();
	    			
	    			CentralData.INSTANCE.dirs.setConfigPath(configPath);
	    		}
	    		
	    		CentralData.INSTANCE.dirs.setCurrentProcessPath("");
	    		CentralData.INSTANCE.currentAnalysis.setRunConfigurations();
	    		CentralData.INSTANCE.currentAnalysis.runCurrentAnalysis();
	    		
	    		writeconfigurations.setEndTime();
	    		writeconfigurations.writeToFile();
	    		CentralData.INSTANCE.setActiveTab("");
	    	}
	    });

 	}
 	
 	@Focus
	public void setFocus() {
		
		CentralData.INSTANCE.setActiveTab("PartRunconfigurations");

	}
 	
}
