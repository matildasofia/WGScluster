package parts;

import java.util.ArrayList;
import java.util.Collections;
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
import org.eclipse.jface.resource.JFaceResources;

public class ChewbbacaSettings extends Dialog {
	
	private Text mode;
	private Text blastsr;
	private Text minlen;
	private Text sizethresh;
	private Text cores;
	private Button outputnovel;
	private Button forcecontinue;
	private Button noinferred;
	
	private List<String> options;	
	private List<String> selection;
	
	public ChewbbacaSettings(Shell parentShell) {
		super(parentShell);
		
	}
	
	//Order of inputs matter, this is dependent on the order in the config file.
	
    @Override
    protected Control createDialogArea(Composite parent) {
        
    	Composite container = (Composite) super.createDialogArea(parent);
        
        GridLayout layout2 = new GridLayout(2, false);
        
        Composite container2 = new Composite(container, SWT.NONE);
        container2.setLayout(layout2);
        
        Label setLabl = new Label(container2, SWT.BOLD);
        setLabl.setText("Settings:");
        setLabl.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
        setLabl.setFont(
        	    JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT)
        	);
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label modeLabl = new Label(container2, SWT.NONE);
        modeLabl.setText("Mode");
        
        mode = new Text(container2, SWT.WRAP);
        mode.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label blastsrLabl = new Label(container2, SWT.NONE);
        blastsrLabl.setText("Blast Score Ratio");
        
        blastsr = new Text(container2, SWT.WRAP);
        blastsr.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        //////////////////////////////////////////////////////////////////////////////

        Label minlenLabl = new Label(container2, SWT.NONE);
        minlenLabl.setText("Minimum Length");
        
        minlen = new Text(container2, SWT.WRAP);
        minlen.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label sizethreshLabl = new Label(container2, SWT.NONE);
        sizethreshLabl.setText("Size Threshold");
        
        sizethresh = new Text(container2, SWT.WRAP);
        sizethresh.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label coresLabl = new Label(container2, SWT.NONE);
        coresLabl.setText("Number of CPU cores");
        
        cores = new Text(container2, SWT.WRAP);
        cores.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label outputnovelLabl = new Label(container2, SWT.NONE);
        outputnovelLabl.setText("Output novel");
        
        outputnovel = new Button(container2, SWT.CHECK);
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label forcecontinueLabl = new Label(container2, SWT.NONE);
        forcecontinueLabl.setText("Force continue");
        
        forcecontinue = new Button(container2, SWT.CHECK);
        
        //////////////////////////////////////////////////////////////////////////////
        
        Label noinferredLabl = new Label(container2, SWT.NONE);
        noinferredLabl.setText("No inferred");
        
        noinferred = new Button(container2, SWT.CHECK);
        
        //////////////////////////////////////////////////////////////////////////////

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
    	
    	String newArg;

    	selection = new ArrayList<>();
    	List<Boolean> buttonValues = new ArrayList<>();
    	
    	if( !mode.getText().isEmpty()) {
    		newArg = options.get(0);
    		newArg = String.join(" ", newArg, mode.getText());
    		selection.add(newArg);
    	}
    	
    	if( !blastsr.getText().isEmpty()) {
    		newArg = options.get(1);
    		newArg = String.join(" ", newArg, blastsr.getText());
    		selection.add(newArg);
    	}
    	
    	if( !minlen.getText().isEmpty()) {
    		newArg = options.get(2);
    		newArg = String.join(" ", newArg, minlen.getText());
    		selection.add(newArg);
    	}
    	
    	if( !sizethresh.getText().isEmpty()) {
    		newArg = options.get(3);
    		newArg = String.join(" ", newArg, sizethresh.getText());
    		selection.add(newArg);
    	}
    	
    	if( !cores.getText().isEmpty()) {
    		newArg = options.get(4);
    		newArg = String.join(" ", newArg, cores.getText());
    		selection.add(newArg);
    	}

    	buttonValues.add(outputnovel.getSelection());
    	buttonValues.add(forcecontinue.getSelection());
    	buttonValues.add(noinferred.getSelection());
    	
    	buttonValues.addAll(0, Collections.nCopies(options.size() - buttonValues.size(), false));
    	
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
        newShell.setText("Settings for spades assembly");
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
    	System.out.println(selection);
    	return selection;
    	
    }

	
	

}
