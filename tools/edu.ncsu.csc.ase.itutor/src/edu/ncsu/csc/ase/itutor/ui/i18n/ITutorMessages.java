package edu.ncsu.csc.ase.itutor.ui.i18n;

import org.eclipse.osgi.util.NLS;

/**
 * <code>ITutorMessages</code>. Parts of this code are adapted from <code>org.eclipse.ui.internal.views.bookmarkexplorer</code>
 * 
 * @author Yoonki Song
 */
public class ITutorMessages extends NLS {

	private static final String BUNDLE_NAME = "edu.ncsu.csc.ase.itutor.ui.i18n.ITutorMessages";// $NON-NLS-1$

	// ASMView
	public static String ASMView_ClearAction_label;

	public static String ASMView_ClearAction_tooltip;
	
	public static String ASMView_DecompileAction_label;

	public static String ASMView_DecompileAction_tooltip;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, ITutorMessages.class);
	}

}
