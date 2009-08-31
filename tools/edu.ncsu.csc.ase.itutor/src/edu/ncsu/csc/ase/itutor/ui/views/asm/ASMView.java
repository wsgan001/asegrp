package edu.ncsu.csc.ase.itutor.ui.views.asm;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.part.ViewPart;

import edu.ncsu.csc.ase.itutor.ui.views.asm.actions.ClearAction;
import edu.ncsu.csc.ase.itutor.ui.views.asm.actions.DecompileAction;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class ASMView extends ViewPart {
	protected Composite fStackComposite;
	private TextViewer fTextViewer;
	private StyledText fTextControl;
	//private TableViewer viewer;
	// Actions
	private ClearAction fClearAction;
	private DecompileAction fDecompileAction;
	private Action doubleClickAction;
	
	/**
	 * The constructor.
	 */
	public ASMView() {
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	public void createPartControl(Composite parent) {
//		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL
//				| SWT.V_SCROLL);
//		viewer.setContentProvider(new ViewContentProvider());
//		viewer.setLabelProvider(new ViewLabelProvider());
//		viewer.setSorter(new NameSorter());
//		viewer.setInput(getViewSite());

		fStackComposite = new Composite(parent, SWT.NONE);
		fStackComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
		fStackComposite.setLayout(new StackLayout());
		
		createTextControl();

		// Create the help context id for the viewer's control
//		PlatformUI.getWorkbench().getHelpSystem().setHelp(viewer.getControl(),
//				"edu.ncsu.csc.ase.itutor.viewer");
		makeActions();
		hookContextMenu();
		hookDoubleClickAction();
		contributeToActionBars();
		
		((StackLayout)fStackComposite.getLayout()).topControl = fTextControl;
	}

	private void createTextControl() {
		fTextViewer = new TextViewer(fStackComposite, SWT.H_SCROLL | SWT.V_SCROLL);
		fTextViewer.setEditable(false);
		fTextControl = fTextViewer.getTextWidget();
		IDocument document = new Document("");
		fTextViewer.setDocument(document);
	}
	
	public void dispose() {
		fTextViewer = null;
		fStackComposite = null;
		fTextControl = null;
		fClearAction = null;
		fDecompileAction = null;
		super.dispose();
	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ASMView.this.fillContextMenu(manager);
			}
		});
		Menu menu = menuMgr.createContextMenu(fTextViewer.getControl());
		fTextViewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, fTextViewer);
	}

	private void contributeToActionBars() {
		IActionBars bars = getViewSite().getActionBars();
		fillLocalPullDown(bars.getMenuManager());
		fillLocalToolBar(bars.getToolBarManager());
	}

	private void fillLocalPullDown(IMenuManager manager) {
		//manager.add(action1);
		//manager.add(new Separator());
		//manager.add(action2);
		manager.add(fDecompileAction);
		manager.add(fClearAction);
	}

	private void fillContextMenu(IMenuManager manager) {
		//manager.add(action1);
		//manager.add(action2);
		manager.add(fClearAction);
		// Other plug-ins can contribute there actions here
		manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void fillLocalToolBar(IToolBarManager manager) {
		//manager.add(action1);
		//manager.add(action2);
		manager.add(fDecompileAction);
		manager.add(fClearAction);
	}

	private void makeActions() {
		fDecompileAction = new DecompileAction(this);
		fClearAction = new ClearAction(this);
		doubleClickAction = new Action() {
			public void run() {
				ISelection selection = fTextViewer.getSelection();
				Object obj = ((IStructuredSelection) selection)
						.getFirstElement();
				showMessage("Double-click detected on " + obj.toString());
			}
		};
	}

	private void hookDoubleClickAction() {
//		fTextViewer.addDoubleClickListener(new IDoubleClickListener() {
//			public void doubleClick(DoubleClickEvent event) {
//				doubleClickAction.run();
//			}
//		});
	}

	private void showMessage(String message) {
		MessageDialog.openInformation(fTextViewer.getControl().getShell(),
				"ASM View", message);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	public void setFocus() {
		fTextViewer.getControl().setFocus();
	}
	
	public void processASM(String text) {
		IDocument document = new Document(text);
		fTextViewer.setDocument(document);
		fTextViewer.refresh();
	}
}