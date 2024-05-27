package parts;

import java.io.File;
import java.net.URI;
import java.nio.file.Paths;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.browser.TitleEvent;
import org.eclipse.swt.browser.TitleListener;
import org.eclipse.swt.events.ShellEvent;
import org.eclipse.swt.events.ShellListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;
import org.eclipse.ui.PlatformUI;

import centralData.CentralData;
import jakarta.annotation.PostConstruct;
import tools.Tools;

public class PartMultiQC {
	
	private Browser browser;
	private Composite composite;
	private Label label;
	
	@PostConstruct
 	public void createComposite(Composite parent) {
	
        parent.setLayout(new FillLayout());
        
        composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout(1, false);
        composite.setLayout(compositeLayout);

        browser = new Browser(composite, SWT.NONE);
        GridData browserGridData = new GridData(SWT.FILL, SWT.FILL, true, true); 
        browser.setLayoutData(browserGridData);
        browser.addTitleListener(new TitleListener() {
            @Override
            public void changed(TitleEvent event) {
            }
        });
        
        update();
	}
	
	private ProgressListener progressListener = new ProgressListener() {
	    @Override
	    public void changed(ProgressEvent event) {
	        // Not used
	    }

	    @Override
	    public void completed(ProgressEvent event) {
	        // Page has been successfully loaded
	        browser.setVisible(true);
	    }
	};
	
	private void update() {
		
        String path;
        URI url;
        
        path = CentralData.INSTANCE.dirs.getCurrentAnalysisPath();
        path = Tools.mergePaths(path, "/qc/multiqc/multiqc_report.html");
        
        if (new File(path).exists()) {
        	
        	url = Paths.get(path).toUri();
        	
        	browser.removeProgressListener(progressListener);
        	browser.addProgressListener(progressListener);
			
            browser.setUrl(url.toString());
            
            label.dispose();
            label = null;
            composite.layout(true, true);
            
        } else {
        	
            if (label == null || label.isDisposed()) {
                label = new Label(composite, SWT.NONE);
                label.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
                label.setText("Run analysis to see results");
            }
            label.setVisible(true);
            browser.setVisible(false);
            
            composite.layout(true, true);
        
    }
        	

		
	}
	
 	@Focus
	public void setFocus() {
		
		CentralData.INSTANCE.setActiveTab("PartMultiQC");
		
		update();

	}
	
	

}
