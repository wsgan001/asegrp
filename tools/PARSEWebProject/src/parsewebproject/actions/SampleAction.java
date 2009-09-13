package parsewebproject.actions;

import java.util.logging.Logger;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import pw.common.CommonConstants;

/**
 * Our sample action implements workbench action delegate.
 * The action proxy will be created by the workbench and
 * shown in the UI. When the user tries to use the action,
 * this delegate will be created and execution will be 
 * delegated to it.
 * @see IWorkbenchWindowActionDelegate
 */
public class SampleAction implements IWorkbenchWindowActionDelegate {
	
	public Logger logger = Logger.getLogger("SampleAction");
	
	private IWorkbenchWindow window;
	private String selectedText = "";
	
	/**
	 * The constructor.
	 */
	public SampleAction() {
	}

	/**
	 * The action has been activated. The argument of the
	 * method represents the 'real' action sitting
	 * in the workbench UI.
	 * @see IWorkbenchWindowActionDelegate#run
	 */
	public void run(IAction action) {
		ProgrammerInput pi = new ProgrammerInput(window.getShell(), selectedText);
		pi.open();
		
		
		if(!CommonConstants.bStartAction)
			return;
		
        //Showing the results
		IWorkbenchPage activePage = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
        PARSEWebResultView part = (PARSEWebResultView) activePage.findView(PARSEWebResultView.ID);

        if (part == null) {
            try {
                part = (PARSEWebResultView) activePage.showView(PARSEWebResultView.ID,
                        null, IWorkbenchPage.VIEW_CREATE);
            } catch (PartInitException e) {
            	logger.info(e.getStackTrace().toString());
            }
        }
        activePage.activate(part);
        part.search();

		

	}

	/**
	 * Selection in the workbench has been changed. We 
	 * can change the state of the 'real' action here
	 * if we want, but this can only happen after 
	 * the delegate has been created.
	 * @see IWorkbenchWindowActionDelegate#selectionChanged
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	        ITextSelection textSelection = (ITextSelection) selection;
	        selectedText = textSelection.getText();
	}

	/**
	 * We can use this method to dispose of any system
	 * resources we previously allocated.
	 * @see IWorkbenchWindowActionDelegate#dispose
	 */
	public void dispose() {
	}

	/**
	 * We will cache window object in order to
	 * be able to provide parent shell for the message dialog.
	 * @see IWorkbenchWindowActionDelegate#init
	 */
	public void init(IWorkbenchWindow window) {
		this.window = window;
	}
}