package edu.ncsu.csc.ase.itutor.ui.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * A specialized filter dialog for the Favorites view that presents the user the
 * option of filtering content based on name, type, or location. The dialog
 * restricts itself to presenting and gathering information from the user and
 * providing accessor methods for the filter action.
 */
public class BinaryFileDialog extends Dialog {
	private String namePattern;
	private Text namePatternField;

	/**
	 * Creates a dialog instance. Note that the window will have no visual
	 * representation (no widgets) until it is told to open. By default,
	 * <code>open</code> blocks for dialogs.
	 * 
	 * @param parentShell
	 *            the parent shell, or <code>null</code> to create a top-level
	 *            shell
	 */
	public BinaryFileDialog(Shell parentShell, String namePattern) {
		super(parentShell);
		this.namePattern = namePattern;
	}

	/**
	 * Creates and returns the contents of the upper part of this dialog (above
	 * the button bar).
	 * 
	 * @param parent
	 *            the parent composite to contain the dialog area
	 * @return the dialog area control
	 */
	protected Control createDialogArea(Composite parent) {
		Composite container = (Composite) super.createDialogArea(parent);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 2;
		container.setLayout(gridLayout);

		final Label filterLabel = new Label(container, SWT.NONE);
		filterLabel.setLayoutData(new GridData(GridData.BEGINNING,
				GridData.CENTER, false, false, 2, 1));
		filterLabel.setText("Enter *.class file"
				+ "\nfor example, c:/HelloWorld.class");

		final Label nameLabel = new Label(container, SWT.NONE);
		nameLabel.setLayoutData(new GridData(GridData.END, GridData.CENTER,
				false, false));
		nameLabel.setText("Name:");

		namePatternField = new Text(container, SWT.BORDER);
		namePatternField.setLayoutData(new GridData(GridData.FILL,
				GridData.CENTER, true, false));

		initContent();
		return container;
	}

	/**
	 * Initialize the various fields in the dialog
	 */
	private void initContent() {
		namePatternField.setText(namePattern != null ? namePattern : "");
		namePatternField.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				namePattern = namePatternField.getText();
			}
		});
	}

	/**
	 * Configures the given shell in preparation for opening this window in it.
	 * In this case, we set the dialog title.
	 */
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Binary File");
	}

	/**
	 * Answer the user specified name pattern
	 * 
	 * @return the pattern (not <code>null</code>)
	 */
	public String getNamePattern() {
		return namePattern;
	}

}
