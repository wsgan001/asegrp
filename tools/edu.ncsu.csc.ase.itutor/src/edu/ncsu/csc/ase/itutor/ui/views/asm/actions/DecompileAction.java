/**
 * 
 */
package edu.ncsu.csc.ase.itutor.ui.views.asm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.ui.PlatformUI;

import edu.ncsu.csc.ase.itutor.core.asm.ASMProxy;
import edu.ncsu.csc.ase.itutor.ui.ITutorImages;
import edu.ncsu.csc.ase.itutor.ui.dialogs.BinaryFileDialog;
import edu.ncsu.csc.ase.itutor.ui.help.ITutorHelpContextIds;
import edu.ncsu.csc.ase.itutor.ui.i18n.ITutorMessages;
import edu.ncsu.csc.ase.itutor.ui.views.asm.ASMView;

/**
 * @author Yoonki Song
 */
public class DecompileAction extends Action {

	private final ASMView fView;

	private ASMProxy fProxy = ASMProxy.getInstance();

	public DecompileAction(ASMView view) {
		fView = view;
		setText(ITutorMessages.ASMView_DecompileAction_label);
		setToolTipText(ITutorMessages.ASMView_DecompileAction_tooltip);
		// Set enabled/disabled/local images
		ITutorImages.setToolImageDescriptors(this, ITutorImages.ICON_DECOMPILE);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this,
				ITutorHelpContextIds.DECOMPILE_ACTION);
	}

	public void run() {
		BinaryFileDialog dialog = new BinaryFileDialog(fView.getSite()
				.getShell(), "");
		if (dialog.open() != InputDialog.OK) {
	         return;
		}
		String fileName = dialog.getNamePattern();
		if (fileName != null && !"".equals(fileName)) {
			fProxy.decompile(fileName);
			String x = fProxy.toString();
			fView.processASM(x);
		}
	}
}
