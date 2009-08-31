package edu.ncsu.csc.ase.itutor.ui;

import java.net.URL;
import java.util.HashMap;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.osgi.framework.Bundle;

import edu.ncsu.csc.ase.itutor.ITutorPlugin;

/**
 * <code>ITutorImages</code>
 * 
 * @author Yoonki Song
 */
public class ITutorImages {
	private static final IPath ICONS_PATH = new Path("$nl$/icons/full");//$NON-NLS-1$

	private static final String NAME_PREFIX = ITutorPlugin.PLUGIN_ID + "."; //$NON-NLS-1$

	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();

	/**
	 * The image registry containing <code>Image</code>s and the
	 * <code>ImageDescriptor</code>s.
	 */
	private static ImageRegistry fImageRegistry;

	private static HashMap<String, ImageDescriptor> fAvoidSWTErrorMap = null;

	public static final String T_OBJ = "obj16"; //$NON-NLS-1$
	public static final String T_OVR = "ovr16"; //$NON-NLS-1$
	public static final String T_WIZBAN = "wizban"; //$NON-NLS-1$
	public static final String T_ELCL = "elcl16"; //$NON-NLS-1$
	public static final String T_DLCL = "dlcl16"; //$NON-NLS-1$
	public static final String T_ETOOL = "etool16"; //$NON-NLS-1$
	public static final String T_DTOOL = "dtool16"; //$NON-NLS-1$
	public static final String T_EVIEW = "eview16"; //$NON-NLS-1$

	public static final String ICON_CLEAR = "clear.gif";//$NON-NLS-1$
	public static final String ICON_DECOMPILE = "decompile.gif";//$NON-NLS-1$
	public static final String ICON_HELP = "help.gif";//$NON-NLS-1$

	// Keys for images available from the iTutor plug-in image registry.
	public static final String IMG_CLEAR = NAME_PREFIX + ICON_CLEAR;
	public static final String IMG_DECOMPILE = NAME_PREFIX + ICON_DECOMPILE;
	public static final String IMG_HELP = NAME_PREFIX + ICON_HELP;

	// Set of predefined Image Descriptors.
	public static final ImageDescriptor DESC_CLEAR = createManagedFromKey(
			T_ETOOL, IMG_CLEAR);
	public static final ImageDescriptor DESC_DECOMPILE = createManagedFromKey(
			T_ETOOL, IMG_DECOMPILE);
	public static final ImageDescriptor DESC_HELP = createManagedFromKey(
			T_ETOOL, IMG_HELP);

	/**
	 * Returns the image managed under the given key in this registry.
	 * 
	 * @param key
	 * @return
	 */
	public static Image get(String key) {
		return getImageRegistry().get(key);
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the bundle.
	 * The path can contain variables like $NL$. If no image could be found, the
	 * 'missing image descriptor' is returned.
	 */
	private static ImageDescriptor createUnManaged(String prefix, String name) {
		return create(prefix, name, true);
	}

	private static ImageDescriptor createManagedFromKey(String prefix,
			String key) {
		return createManaged(prefix, key.substring(NAME_PREFIX_LENGTH), key);
	}

	private static ImageDescriptor createManaged(String prefix, String name,
			String key) {
		ImageDescriptor result = create(prefix, name, true);

		if (fAvoidSWTErrorMap == null) {
			fAvoidSWTErrorMap = new HashMap();
		}
		fAvoidSWTErrorMap.put(key, result);
		if (fImageRegistry != null) {
			System.out.println("Image registry already defined"); //$NON-NLS-1$
		}
		return result;
	}

	/*
	 * Creates an image descriptor for the given prefix and name in the JDT UI
	 * bundle. The path can contain variables like $NL$. If no image could be
	 * found, <code>useMissingImageDescriptor</code> decides if either the
	 * 'missing image descriptor' is returned or <code>null</code>. or
	 * <code>null</code>.
	 */
	private static ImageDescriptor create(String prefix, String name,
			boolean useMissingImageDescriptor) {
		IPath path = ICONS_PATH.append(prefix).append(name);
		return createImageDescriptor(ITutorPlugin.getDefault().getBundle(),
				path, useMissingImageDescriptor);
	}

	/**
	 * Returns the standard display to be used. The method first checks, if the
	 * thread calling this method has an associated display. If so, this display
	 * is returned. Otherwise the method returns the default display.
	 */
	public static Display getStandardDisplay() {
		Display display = Display.getCurrent();
		if (display == null) {
			display = Display.getDefault();
		}
		return display;
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *tool16 folders.
	 * 
	 * @param action
	 *            the action
	 * @param iconName
	 *            the icon name
	 */
	public static void setToolImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "tool16", iconName); //$NON-NLS-1$
	}

	/**
	 * Sets the three image descriptors for enabled, disabled, and hovered to an
	 * action. The actions are retrieved from the *lcl16 folders.
	 * 
	 * @param action
	 *            the action
	 * @param iconName
	 *            the icon name
	 */
	public static void setLocalImageDescriptors(IAction action, String iconName) {
		setImageDescriptors(action, "lcl16", iconName); //$NON-NLS-1$
	}

	/*
	 * Sets all available image descriptors for the given action.
	 */
	public static void setImageDescriptors(IAction action, String type,
			String relPath) {
		ImageDescriptor id = create("d" + type, relPath, false); //$NON-NLS-1$
		if (id != null) {
			action.setDisabledImageDescriptor(id);
		}

		// id = create("c" + type, relPath, false); //$NON-NLS-1$ 
		// if (id != null) {
		// action.setHoverImageDescriptor(id);
		// }

		ImageDescriptor descriptor = create("e" + type, relPath, true); //$NON-NLS-1$
		action.setHoverImageDescriptor(descriptor);
		action.setImageDescriptor(descriptor);
	}

	/*
	 * Creates an image descriptor for the given path in a bundle. The path can
	 * contain variables like $NL$. If no image could be found,
	 * <code>useMissingImageDescriptor</code> decides if either the 'missing
	 * image descriptor' is returned or <code>null</code>. Added for 3.1.1.
	 */
	public static ImageDescriptor createImageDescriptor(Bundle bundle,
			IPath path, boolean useMissingImageDescriptor) {
		URL url = FileLocator.find(bundle, path, null);
		if (url != null) {
			return ImageDescriptor.createFromURL(url);
		}
		if (useMissingImageDescriptor) {
			return ImageDescriptor.getMissingImageDescriptor();
		}
		return null;
	}

	public static ImageRegistry getImageRegistry() {
		if (fImageRegistry == null) {
			fImageRegistry = new ImageRegistry();
			for (String key : fAvoidSWTErrorMap.keySet()) {
				fImageRegistry.put(key, fAvoidSWTErrorMap.get(key));
			}
			fAvoidSWTErrorMap = null;
		}
		return fImageRegistry;
	}

	/**
	 * Returns the image descriptor for the given key in this registry. Might be
	 * called in a non-UI thread.
	 * 
	 * @param key
	 *            the image's key
	 * @return the image descriptor for the given key
	 */
	public static ImageDescriptor getImageDescriptor(String key) {
		if (fImageRegistry == null) {
			return (ImageDescriptor) fAvoidSWTErrorMap.get(key);
		}
		return getImageRegistry().getDescriptor(key);
	}
}
