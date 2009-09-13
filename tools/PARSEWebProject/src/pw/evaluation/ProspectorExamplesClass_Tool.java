package pw.evaluation;

/*
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.SequenceInputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Iterator;

import org.apache.commons.collections.IteratorUtils;
import org.apache.tools.ant.taskdefs.optional.junit.Enumerations;
import org.eclipse.core.filebuffers.FileBuffers;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.Viewport;
import org.eclipse.draw2d.parts.ScrollableThumbnail;
import org.eclipse.gef.ContextMenuProvider;
import org.eclipse.gef.EditPart;
import org.eclipse.gef.EditPartViewer;
import org.eclipse.gef.EditPolicy;
import org.eclipse.gef.GraphicalEditPart;
import org.eclipse.gef.Request;
import org.eclipse.gef.RootEditPart;
import org.eclipse.gef.commands.Command;
import org.eclipse.gef.editparts.AbstractEditPart;
import org.eclipse.gef.editparts.FreeformGraphicalRootEditPart;
import org.eclipse.gef.editparts.GraphicalRootEditPart;
import org.eclipse.gef.editparts.ScalableFreeformRootEditPart;
import org.eclipse.gef.examples.flow.actions.FlowContextMenuProvider;
import org.eclipse.gef.examples.flow.policies.StructuredActivityDirectEditPolicy;
import org.eclipse.gef.examples.flow.policies.TransitionEditPolicy;
import org.eclipse.gef.examples.logicdesigner.LogicContextMenuProvider;
import org.eclipse.gef.ui.actions.CopyTemplateAction;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.IWorkingCopyManager;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.OpenInNewWindowAction;
import org.eclipse.ui.examples.readmetool.AdaptableList;
import org.eclipse.ui.views.framelist.TreeFrame;
import org.eclipse.ui.views.navigator.ResourceSelectionUtil;
*/
public class ProspectorExamplesClass_Tool {

/*	
	//All cases dealt with Logic are stored here 
	//Results for Case 1
	public void Case1(org.eclipse.jface.viewers.IStructuredSelection iss) 
	{
		//Rank 1
		//Object object = iss.getFirstElement();
		//IFile file = (IFile) object;
		//java.io.InputStream is1 = file.getContents();
		
		//Rank 2
		//Object object = iss.getFirstElement();
		//IFile file = (IFile) object;
		//java.io.InputStream is2 = file.getContents(false);
		
		
		//Rank 3
		//Iterator iterator = iss.iterator();
		//Enumeration enumeration = IteratorUtils.asEnumeration(iterator);
		//java.io.InputStream is3 = new SequenceInputStream(enumeration); 
		
		
		//Rank 4
		//Object[] objectAry = iss.toArray();
		//Enumeration enumeration = Enumerations.fromArray(objectAry);
		//java.io.InputStream is4 = new SequenceInputStream(enumeration);
		
		
		//Rank 5
		//Object object = iss.getFirstElement();
		//IStorage storage = (IFile) object;
		//java.io.InputStream is5 = storage.getContents();
		
		//Rank 6
		//Collection collection = iss.toList();
		//Enumeration enumeration = Collections.enumeration(collection);
		//java.io.InputStream is6 = new SequenceInputStream(enumeration);
		
		
		//Only upto rank 6 are collected
	}
	
	public void Case2(org.eclipse.gef.ui.actions.ActionRegistry ar)
	{
		//Rank 1
		//Object object;
		//IAction action = ar.getAction(object);
		//org.eclipse.jface.action.IAction ia = (CopyTemplateAction) action; 
	}
	
	public void Case3(IEditorInput editorInput)
	{
		//Rank 1
		//IWorkingCopyManager workingCopyManager;
		//org.eclipse.jdt.core.IJavaElement ije = workingCopyManager.getWorkingCopy(editorInput);
		
		
		//Rank 2
		//IWorkingCopyManager workingCopyManager;
		//ICompilationUnit compilationUnit = workingCopyManager.getWorkingCopy(editorInput);
		//org.eclipse.jdt.core.IJavaElement ije = compilationUnit.getElementAt(0);
		
		//Rank 3
		//IWorkingCopyManager workingCopyManager;
		//IJavaElement javaElement = workingCopyManager.getWorkingCopy(editorInput);
		//org.eclipse.jdt.core.IJavaElement ije = javaElement.getParent();
		
		//Rank 4 - 7 are similar
		
		//Rank 8
		//IContributorResourceAdapter contributorResourceAdapter;
		//IResource resource = contributorResourceAdapter.getAdaptedResource(editorInput);
		//org.eclipse.jdt.core.IJavaElement ije = JavaCore.create(resource);
	}
	
	public void Case4(org.eclipse.gef.GraphicalEditPart gefP)
	{
		//Rank 1
		//Request request;
		//org.eclipse.gef.commands.Command cm = gefP.getCommand(request);
		
		//Rank 2
		//Request request;
		//EditPart editPart = gefP.getParent();
		//org.eclipse.gef.commands.Command cm = editPart.getCommand(request);
		
		//Others are not relevant
	}
	
	public void Case5(java.io.File file)
	{
		//javax.sound.sampled.Clip  clip= 
	}
	
	public void Case6(org.eclipse.ui.IEditorInput iei)
	{
		//Rank 1
		//Class class;
		//Object object = iei.getAdapter(class);
		//org.eclipse.core.resources.IFile ifile = (IFile) object;
		
		//Rank 2
		//IWorkingCopyManager workingCopyManager;
		//ICompilationUnit compilationUnit = workingCopyManager.getWorkingCopy(iei);
		//IResource resource = compilationUnit.getCorrespondingResource();
		//org.eclipse.core.resources.IFile ifile = (IFile) resource;
		
		
		//Rank 3
		//IWorkingCopyManager workingCopyManager;
		//ICompilationUnit compilationUnit = workingCopyManager.getWorkingCopy(iei);
		//IResource resource = compilationUnit.getUnderlyingResource();
		//org.eclipse.core.resources.IFile ifile = (IFile) resource;
		
		//Rank 4
		//IWorkingCopyManager workingCopyManager;
		//ICompilationUnit compilationUnit = workingCopyManager.getWorkingCopy(iei);
		//IResource resource = compilationUnit.getResource();
		//org.eclipse.core.resources.IFile ifile = (IFile) resource;
		
		
		//Rank 5
		//IWorkingCopyManager workingCopyManager;
		//IJavaElement javaElement = workingCopyManager.getWorkingCopy(iei);
		//IPath path = javaElement.getPath();
		//org.eclipse.core.resources.IFile ifile = FileBuffers.getWorkspaceFileAtLocation(path);
		
		//Rank 6
		//IContributorResourceAdapter contributorResourceAdapter;
		//IResource resource = contributorResourceAdapter.getAdaptedResource(iei);
		//IPath path = resource.getFullPath();
		//org.eclipse.core.resources.IFile ifile = FileBuffers.getWorkspaceFileAtLocation(path);
		
		//Ranks 7 to 12 are similar to 6
		
		//No relevant case
		
	}
	
	public void Case7 (org.eclipse.ui.IPageLayout ipl)
	{
		//Rank 1
		//String string;
		//org.eclipse.ui.IFolderLayout ifl = ipl.createFolder(string, 0, 0.0, string);
	}
	
	public void Case8 (org.eclipse.jface.viewers.TreeViewer tv)
	{
		//Rank 1
		//ISelection selection = tv.getSelection();
		//org.eclipse.jface.viewers.IStructuredSelection ss = (IStructuredSelection) selection;
		
		//Rank 2
		//ISelection selection = tv.getSelection();
		//org.eclipse.jface.viewers.IStructuredSelection ss = (IStructuredSelection) selection;
		

		//Rank 3
		//ISelection selection = tv.getSelection();
		//org.eclipse.jface.viewers.IStructuredSelection ss = (IStructuredSelection) selection;

		//Rank 4
		//ISelection selection = tv.getSelection();
		//org.eclipse.jface.viewers.IStructuredSelection ss = (IStructuredSelection) selection;

		//Rank 5
//		Object[] objectAry = tv.getExpandedElements();
//		org.eclipse.jface.viewers.IStructuredSelection ss = new StructuredSelection(objectAry);

		//Rank 6
//		Object[] objectAry = tv.getVisibleExpandedElements();
//		org.eclipse.jface.viewers.IStructuredSelection ss = new StructuredSelection(objectAry);
	
		//Rank 7
//		ISelection selection = tv.getSelection();
//		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);

		//Rank 8
//		ISelection selection = tv.getSelection();
//		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);

		//Rank 9
//		ISelection selection = tv.getSelection();
//		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);

		//Rank 10
//		ISelection selection = tv.getSelection();
//		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);

		//Rank 11
//		Object[] objectAry = tv.getExpandedElements();
//		IStructuredSelection structuredSelection = new StructuredSelection(objectAry);
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);

		//Rank 12
//		Object[] objectAry = tv.getVisibleExpandedElements();
//		IStructuredSelection structuredSelection = new StructuredSelection(objectAry);
//		org.eclipse.jface.viewers.IStructuredSelection ss = ResourceSelectionUtil.allResources(structuredSelection, 0);
	}
	
	//New test cases for PROSPECTOR
	public void test3_add(org.eclipse.gef.requests.DirectEditRequest der)
	{
//		StructuredActivityDirectEditPolicy structuredActivityDirectEditPolicy;
//		org.eclipse.gef.commands.Command cmd = structuredActivityDirectEditPolicy.getCommand(der);

//		TransitionEditPolicy transitionEditPolicy;
//		org.eclipse.gef.commands.Command cmd = transitionEditPolicy.getCommand(der);
	
//		EditPart editPart;
//		org.eclipse.gef.commands.Command cmd = editPart.getCommand(der);
		
//		EditPolicy editPolicy;
//		org.eclipse.gef.commands.Command cmd = editPolicy.getCommand(der); 
			
//		AbstractEditPart abstractEditPart;
//		org.eclipse.gef.commands.Command cmd = abstractEditPart.getCommand(der);
		
//		FreeformGraphicalRootEditPart freeformGraphicalRootEditPart;
//		org.eclipse.gef.commands.Command cmd = freeformGraphicalRootEditPart.getCommand(der);
		
//		GraphicalRootEditPart graphicalRootEditPart;
//		org.eclipse.gef.commands.Command cmd = graphicalRootEditPart.getCommand(der);
		
		
	}
	
	public void add_test1(org.eclipse.gef.requests.DirectEditRequest der)
	{
//		StructuredActivityDirectEditPolicy structuredActivityDirectEditPolicy;
//		org.eclipse.gef.commands.Command cmd = structuredActivityDirectEditPolicy.getCommand(der);
		
	}
	
	
	//Logic Examples
	public void Logic_Examples()
	{
		//Case 1
//		org.eclipse.ui.part.IPageSite ips;
//		org.eclipse.ui.IActionBars iab = ips.getActionBars();
		
		//Case 2
//		org.eclipse.gef.ui.actions.ActionRegistry ar;
//		Object object;
//		IAction action = ar.getAction(object);
//		org.eclipse.jface.action.IAction ia = (CopyTemplateAction) action;
		
		
//		IWorkbenchWindow workbenchWindow;
//		AdaptableList adaptableList;
//		Iterator iterator = ar.getActions();
//		IAdaptable adaptable = adaptableList.add(iterator);
//		org.eclipse.jface.action.IAction ia = new OpenInNewWindowAction(workbenchWindow, adaptable); 
		
		//Case 3 
//		org.eclipse.gef.ui.actions.ActionRegistry ar; 
//		EditPartViewer editPartViewer;
//		org.eclipse.gef.ContextMenuProvider cmp = new FlowContextMenuProvider(editPartViewer, ar);
		
//		EditPartViewer editPartViewer;
//		org.eclipse.gef.ContextMenuProvider cmp = new LogicContextMenuProvider(editPartViewer, ar);
		
		//Case 4
//		org.eclipse.ui.part.IPageSite ips;
//		org.eclipse.jface.viewers.ISelectionProvider isp = ips.getSelectionProvider();

		//Case 5
//		org.eclipse.ui.part.IPageSite ips;
//		IActionBars actionBars = ips.getActionBars();
//		org.eclipse.jface.action.IToolBarManager itbm = actionBars.getToolBarManager();
		
		//Case 6
//		java.lang.String str;
//		org.eclipse.jface.resource.ImageDescriptor id = 
		
		//Case 7
//		org.eclipse.swt.widgets.Composite cmp;
//		org.eclipse.swt.widgets.Control ctl = 
		
		//Case 8
//		org.eclipse.swt.widgets.Composite cmp; 
//		org.eclipse.swt.widgets.Canvas cv = 

		//Case 9
//		org.eclipse.gef.GraphicalViewer gv;
//		Control control = gv.getControl();
//		FigureCanvas figureCanvas = (FigureCanvas) control;
//		Viewport viewport = figureCanvas.getViewport();
//		org.eclipse.draw2d.parts.ScrollableThumbnail st = new ScrollableThumbnail(viewport);
		
//		RootEditPart rootEditPart = gv.getRootEditPart();
//		ScalableFreeformRootEditPart scalableFreeformRootEditPart = (ScalableFreeformRootEditPart) rootEditPart;
//		IFigure figure = scalableFreeformRootEditPart.getFigure();
//		Viewport viewport = (Viewport) figure;
//		org.eclipse.draw2d.parts.ScrollableThumbnail st = new ScrollableThumbnail(viewport); 
		
		//Case 10
//		org.eclipse.gef.GraphicalViewer gv;
//		RootEditPart rootEditPart = gv.getRootEditPart();
//		GraphicalEditPart graphicalEditPart = (ScalableFreeformRootEditPart) rootEditPart;
//		org.eclipse.draw2d.IFigure ifig = graphicalEditPart.getFigure();
		
	}
	
	
	
	
	
*/	
}
