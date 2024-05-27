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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.graphics.Point;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.resource.JFaceResources;

public class SpadesSettings extends Dialog {
	
	private Button isolate;
	private Button careful;
	private Button onlyassembler;
	
	private List<String> options;	
	private List<String> selection;
	
	public SpadesSettings(Shell parentShell) {
		super(parentShell);
		
	}
	
	//Order of inputs matter, this is dependent on the order in the config file.
	
    @Override
    protected Control createDialogArea(Composite parent) {
        
    	Composite container = (Composite) super.createDialogArea(parent);
        
        GridLayout layout2 = new GridLayout(2, false);
        
        Composite container2 = new Composite(container, SWT.NONE);
        container2.setLayout(layout2);
        
        Label modesLabl = new Label(container2, SWT.BOLD);
        modesLabl.setText("Modes: ");
        modesLabl.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false, 2, 1));
        modesLabl.setFont(
        	    JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT)
        	);
        
        Label isolLabl = new Label(container2, SWT.NONE);
        isolLabl.setText("isolate");
        
        isolate = new Button(container2, SWT.CHECK);
        
        
        Label careLabl = new Label(container2, SWT.NONE);
        careLabl.setText("careful");
        
        careful = new Button(container2, SWT.CHECK);
        
        
        Label onAssLabl = new Label(container2, SWT.NONE);
        onAssLabl.setText("only assembler");
        
        onlyassembler = new Button(container2, SWT.CHECK);
        
        
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

    	selection = new ArrayList<>();
    	List<Boolean> buttonValues = new ArrayList<>();

    	buttonValues.add(isolate.getSelection());
    	buttonValues.add(careful.getSelection());
    	buttonValues.add(onlyassembler.getSelection());
    	
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
