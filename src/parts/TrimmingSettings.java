package parts;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;

public class TrimmingSettings extends Dialog {
	
	private List<String> options;
	
	private Text megaBases;
	private Button md5;
	private Button fastqc;
	private Button qcPostTrim;
	private Button kraken;
	private Button jellyfish;
	
	private String mbDefault = "100";
	private List<String> selection;
	
	public TrimmingSettings(Shell parentShell) {
		super(parentShell);
	}
	
	//Order of inputs matter, this is dependent on the order in the config file.
	
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite container = (Composite) super.createDialogArea(parent);
        
        
        GridLayout layout2 = new GridLayout(2, false);
        Composite container2 = new Composite(container, SWT.NONE);
        container2.setLayout(layout2);
        
        Label megaBasesLabl = new Label(container2, SWT.NONE);
        megaBasesLabl.setText("MegaBases");
        
        megaBases = new Text(container2, SWT.WRAP);
        megaBases.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        megaBases.setText(mbDefault);
        
        Label md5Labl = new Label(container2, SWT.NONE);
        md5Labl.setText("MD5");
        
        md5 = new Button(container2, SWT.CHECK);
        md5.setSelection(true);
        
        Label fastqcLabl = new Label(container2, SWT.NONE);
        fastqcLabl.setText("FastQC");
        
        fastqc = new Button(container2, SWT.CHECK);
        fastqc.setSelection(true);
        
        Label qcPostTrimLabl = new Label(container2, SWT.NONE);
        qcPostTrimLabl.setText("FastqQC Post Trimming");
        
        qcPostTrim = new Button(container2, SWT.CHECK);
        qcPostTrim.setSelection(true);
        
        Label krakenLabl = new Label(container2, SWT.NONE);
        krakenLabl.setText("Kraken");
        
        kraken = new Button(container2, SWT.CHECK);
        
        Label jellyfishLabl = new Label(container2, SWT.NONE);
        jellyfishLabl.setText("Jellyfish");
        
        jellyfish = new Button(container2, SWT.CHECK);
        
        
        
        Display.getCurrent().asyncExec(() -> {
            container2.forceFocus();
        });

        return container;
    }
    
    @Override
    protected void createButtonsForButtonBar(Composite parent) {
        createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }
    
    @Override
    protected void okPressed() {
    	
    	String mb;
    	selection = new ArrayList<>();
    	List<Boolean> buttonValues = new ArrayList<>();
    	
    	System.out.println("MEGABASES: " + megaBases.getText());
    	
    	
    	if( !megaBases.getText().isEmpty() ) {
    		
    		mb = options.get(0);
    		mb += megaBases.getText();
    		selection.add(mb);
    		
    	} else {
    		
    		mb = options.get(0);
    		mb += mbDefault;
    		selection.add(mb);

    	}
    	
    	buttonValues.add(false);
    	buttonValues.add(md5.getSelection());
    	buttonValues.add(fastqc.getSelection());
    	buttonValues.add(qcPostTrim.getSelection());
    	buttonValues.add(kraken.getSelection());
    	buttonValues.add(jellyfish.getSelection());
    	
    	System.out.println("Buttonvalues: " + buttonValues);
    	System.out.println("options: " + options);
    	System.out.println("Selection: " + selection);
    	
    	
    	for( int i = 0; i < buttonValues.size(); i ++ ) {
    		
    		if(buttonValues.get(i)) {
    			
    			selection.add(options.get(i));
    		}
    	}

        super.okPressed();
    }
    
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText("Settings for preprocessing");
    }
    
    @Override
    protected Point getInitialSize() {
        return new Point(300, 300);
    }
    
    public void openDialog() {
    	
        open();
    }
    
    public void setOptions(List<String> options) {
    	
    	this.options = options;

    }
    
    public List<String> getSelection() {
    	
    	return selection;
    }

}
