package parts;

import java.net.HttpURLConnection;
import java.net.URL;

import org.eclipse.e4.ui.di.Focus;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import centralData.CentralData;
import jakarta.annotation.PostConstruct;
import pipeline.RunGrapetree;
import pipeline.RunDocker;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressAdapter;
import org.eclipse.swt.browser.ProgressEvent;

public class PartClusteringresultoverview {
	
	private Browser browser;
	
	@PostConstruct
 	public void createComposite(Composite parent) {
	
        parent.setLayout(new FillLayout());
        
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout compositeLayout = new GridLayout(1, false);
        composite.setLayout(compositeLayout);

        browser = new Browser(composite, SWT.NONE);
        GridData browserGridData = new GridData(SWT.FILL, SWT.FILL, true, true); 
        browser.setLayoutData(browserGridData);

        //Browser browser = new Browser(parent, SWT.NONE);
//        browser.addTitleListener(new TitleListener() {
//            @Override
//            public void changed(TitleEvent event) {
//               
//            }
//        });
//        
        browser.addProgressListener(new ProgressAdapter() {
            @Override
            public void completed(ProgressEvent event) {
                // Page load completed successfully
                browser.setVisible(true);
            }

            @Override
            public void changed(ProgressEvent event) {

                if ((event.total == 0 && event.current == 0) || event.current == event.total) {
     
                    update();
                }
            }
        });
        
        if (!RunDocker.isRunning("grapetreecont")) {
        	RunGrapetree.run();
        }
//        RunGrapetree.run();

        waitToStart("http://localhost:8000");
		browser.setUrl("http://localhost:8000");

	}
	
	private void waitToStart(String urlString) {
	    boolean serverAvailable = false;

	    for (int i = 0; i < 30; i++) {
	        if (isServerAvailable(urlString)) {
	            serverAvailable = true;
	            break;
	        }
	        try {
	            Thread.sleep(500);
	        } catch (InterruptedException e) {
	            e.printStackTrace();
	        }
	    }

	    if (!serverAvailable) {
	        System.err.println("Server not available");
	    }
	}
	
	private boolean isServerAvailable(String urlString) {
	    try {
	    	
	        URL url = new URL(urlString);
	        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	        connection.setRequestMethod("HEAD");
	        int responseCode = connection.getResponseCode();
	        return (200 <= responseCode && responseCode <= 399);
	    
	    } catch (Exception e) {
	    	
	        return false;
	    }
	}
	
	private void update() {
		
        if (!RunDocker.isRunning("grapetreecont")) {
        	RunGrapetree.run();
        	waitToStart("http://localhost:8000");
        	browser.refresh();
        }
		
		//RunGrapetree.run();
        
		//browser.refresh();
        
    }
	
 	@Focus
	public void setFocus() {
		
		CentralData.INSTANCE.setActiveTab("PartClusteringresultsoverview");
		
//		update();

	}
}
