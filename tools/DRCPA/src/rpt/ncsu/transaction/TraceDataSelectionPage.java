package rpt.ncsu.transaction;

import java.io.File;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import rpt.ncsu.transaction.analysis.TraceAnalyzer;
import rpt.ncsu.transaction.util.TempInfo;

public class TraceDataSelectionPage extends WizardPage {

	private Text fileName;	
	private Composite composite;
	private GridLayout layout;
	//private Label label2;
	private File f;

	protected TraceDataSelectionPage(String pageName) {
		super(pageName);
		setTitle("Trace Data File");
		setDescription("Please choose a trace data file (.xml)");
	}

	public void createControl(Composite parent) {
		composite = new Composite(parent, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);
		setControl(composite);
		Label label1 = new Label(composite, SWT.NONE);
		label1.setText("File: ");
		label1.setFocus();
		fileName = new Text(composite, SWT.BORDER);
		Button browse = new Button(composite, SWT.NONE);
		browse.setText("Browse");

		//label2 = new Label(composite, SWT.NONE);
		//label2.setText("Please choose Entry Classes: ");
		//label2.setVisible(false);

		browse.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				f = getFile(new File("/"));
				if (f != null) {
					fileName.setText(f.getAbsolutePath());
					fileName.setFocus();
					try {
						PlatformUI.getWorkbench().getActiveWorkbenchWindow()
								.getActivePage().showView(
										"rpt.ncsu.transaction.myConsole");
			//			label2.setVisible(true);

						TempInfo.transactionFolder = f.getParent()
								+ "\\"
								+ f.getName().substring(0,
										f.getName().lastIndexOf(".xml"));
						TempInfo.traceFile = f.getAbsolutePath();
						//TempInfo.classDef = (new TraceAnalyzer())
						//		.getEntryClassDef(f);

						//listCheckboxes();

					} catch (PartInitException exp) {
						// TODO Auto-generated catch block
						exp.printStackTrace();
					}

				} else {
					MessageDialog.openInformation(getShell(),
							"Transaction Plug-in", "This file does not exist!");
				}

				composite.layout();

			}

//			private void listCheckboxes() {
//				for (int i = composite.getChildren().length - 1; i >= 0; i--) {
//					if (composite.getChildren()[i].getClass().toString()
//							.equals("class org.eclipse.swt.widgets.Table")) {
//						composite.getChildren()[i].dispose();
//						break;
//					}
//				}
//
//				Table table = new Table(composite, SWT.CHECK | SWT.BORDER
//						| SWT.V_SCROLL | SWT.H_SCROLL);
//
//				for (int i = 0; i < TempInfo.classDef.size(); i++) {
//					TableItem cb = new TableItem(table, SWT.NONE);
//					cb.setText(TempInfo.classDef.get(i).toString());
//				}
//				table.setSize(400, 400);
//				table.addListener(SWT.Selection, new Listener() {
//					public void handleEvent(Event event) {						
//						if(((TableItem)event.item).getChecked())
//							TempInfo.entryClasses.add(((TableItem)event.item).getText());
//						else
//							TempInfo.entryClasses.remove(((TableItem)event.item).getText());
//					}
//				});
//			}
		});

		// // Get Signature methods
		// // Let a user choose signatures
		// // Set signature lists

		
	}

	/**
	 * Helper to open the file chooser dialog.
	 * 
	 * @param startingDirectory
	 *            the directory to open the dialog on.
	 * @return File The File the user selected or <code>null</code> if they do
	 *         not.
	 */
	private File getFile(File startingDirectory) {

		FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
		if (startingDirectory != null)
			dialog.setFileName(startingDirectory.getPath());
		dialog.setFilterExtensions(new String[] { "*.xml", "*" });
		String file = dialog.open();
		if (file != null) {
			file = file.trim();
			if (file.length() > 0)
				return new File(file);
		}

		return null;
	}

	@Override
	public Shell getShell() {
		// TODO Auto-generated method stub
		return super.getShell();
	}
}
