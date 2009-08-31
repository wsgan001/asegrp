/**
 * 
 */
package edu.ncsu.csc.ase.itutor.ui.views.asm.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.ui.PlatformUI;

import edu.ncsu.csc.ase.itutor.ui.ITutorImages;
import edu.ncsu.csc.ase.itutor.ui.help.ITutorHelpContextIds;
import edu.ncsu.csc.ase.itutor.ui.i18n.ITutorMessages;
import edu.ncsu.csc.ase.itutor.ui.views.asm.ASMView;

/**
 * @author Yoonki Song
 */
public class ClearAction extends Action {

	private final ASMView fView;
	
	public ClearAction(ASMView view) {
		fView = view;
		setText(ITutorMessages.ASMView_ClearAction_label);
		setToolTipText(ITutorMessages.ASMView_ClearAction_tooltip);
		// Set enabled/disabled/local images
		ITutorImages.setToolImageDescriptors(this, ITutorImages.ICON_CLEAR);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(this, ITutorHelpContextIds.CLEAR_ACTION);
	}
	
	public void run() {
		fView.processASM("");
	}
}
