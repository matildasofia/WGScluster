 
package centralData;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class handlerCreateNewAnalysis {
	
	@Execute
	public void execute() {
		
        Display display = Display.getDefault();
        Shell shell = new Shell(display);

        InputDialog dialog = new InputDialog(shell, "Input Directory Name", "Enter the directory name:", "", null);
        
        if (dialog.open() == InputDialog.OK) {
        	
            String directoryName = dialog.getValue();
            
            if (directoryName != null && !directoryName.isEmpty()) {
            	
            	if(directoryName.contains(" ")) {
            		directoryName.replace(" ", "_");
            	}
            	
            	 String analysesBaseDir = CentralData.INSTANCE.dirs.getAnalysesPath();
                 File newdir = new File(analysesBaseDir, directoryName);
                 
                 if (newdir.mkdir()) {
//                	 
//                	 String configPath;
//                	 String newPath;
//                	 
//                	 configPath = CentralData.INSTANCE.dirs.getConfigPath();
                	 
                	 MessageDialog.openInformation(shell, "New Analysis", "New Analysis: " + directoryName);
                	 
                 }
                 else {
                	 
                	 MessageDialog.openError(shell, "Error", "Name is wrongly formatted or already used.");

                 }
           }
        }
	}
		
}