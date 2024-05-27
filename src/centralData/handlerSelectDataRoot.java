 
package centralData;

import java.io.File;

import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class handlerSelectDataRoot {
	@Execute
	public void execute() {
		
		Display display = Display.getDefault();
        Shell shell = new Shell(display);
		
		DirectoryDialog dialog = new DirectoryDialog(shell);
        dialog.setText("Select a data root folder");
        dialog.setFilterPath(System.getProperty("user.home)"));
        
        String selectedFolder = dialog.open();
        
        if (selectedFolder != null) {

            File folder = new File(selectedFolder);
            
            CentralData.INSTANCE.dirs.setDataPath(folder.getAbsolutePath());
            CentralData.INSTANCE.updateConfigFileContent();
            
        }
		
	}
		
}