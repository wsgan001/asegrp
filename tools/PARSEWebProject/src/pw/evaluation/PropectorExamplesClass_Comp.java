package pw.evaluation;

/*
import java.io.BufferedReader;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.AbstractList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.adaptor.LocationManager;
import org.eclipse.draw2d.FigureCanvas;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.WorkingCopyOwner;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.ui.JavaElementImageDescriptor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.osgi.framework.util.SecureAction;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IContributorResourceAdapter;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IPageService;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchSite;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.actions.BaseSelectionListenerAction;
import org.eclipse.ui.actions.ExportResourcesAction;
import org.eclipse.ui.actions.ImportResourcesAction;
import org.eclipse.ui.actions.OpenFileAction;
import org.eclipse.ui.actions.OpenSystemEditorAction;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.texteditor.DocumentProviderRegistry;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;
import org.eclipse.ui.views.contentoutline.ContentOutline;
import org.eclipse.ui.views.navigator.ResourceSelectionUtil;
import org.eclipse.ui.views.navigator.ShowInNavigatorAction;
import org.eclipse.ui.views.tasklist.DefaultTaskListResourceAdapter;
import org.eclipse.ui.views.tasklist.ITaskListResourceAdapter;
import org.eclipse.update.core.Utilities;
import org.tigris.subversion.subclipse.ui.decorator.SVNLightweightDecorator.CachedImageDescriptor;
import org.tigris.subversion.subclipse.ui.sync.OverlayIcon;

*/

public class PropectorExamplesClass_Comp {
/*
	public void Comp1 (java.io.InputStream is)
	{
		//Rank 1
		//Reader reader = new InputStreamReader(is);
		//java.io.BufferedReader br = new BufferedReader(reader);
		
		//Rank 2
		//Reader reader = new InputStreamReader(is);
		//java.io.BufferedReader br = new BufferedReader(reader, 0);
		
		//Rank 3
		//Reader reader = new InputStreamReader(is);
		//java.io.BufferedReader br = new LineNumberReader(reader);
		
		//Rank 4
		//Reader reader = new InputStreamReader(is);
		//java.io.BufferedReader br = new LineNumberReader(reader, 0);
		
		//Rank 5
		//Reader reader = new InputStreamReader(is);
		//Reader reader = new PushbackReader(reader);
		//java.io.BufferedReader br = new BufferedReader(reader);
		
		//Ranks 6 to 12 are similar
	}
	
	public void Comp2(java.lang.String str)
	{
		//Rank 1
		//FileChannel$MapMode fileChannel$MapMode;
		//FileInputStream fileInputStream = new FileInputStream(str);
		//FileChannel fileChannel = fileInputStream.getChannel();
		//java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
		
		//Rank 2
//		FileChannel$MapMode fileChannel$MapMode;
//		FileOutputStream fileOutputStream = new FileOutputStream(str);
//		FileChannel fileChannel = fileOutputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);

		//Rank 3
//		FileChannel$MapMode fileChannel$MapMode;
//		FileOutputStream fileOutputStream = new FileOutputStream(str, false);
//		FileChannel fileChannel = fileOutputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);

		//Rank 4
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = new File(str);
//		FileInputStream fileInputStream = new FileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);

		//Rank 5
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = LocationManager.getConfigurationFile(str);
//		FileInputStream fileInputStream = new FileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);

		//Rank 6
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = Utilities.lookupLocalFile(str);
//		FileInputStream fileInputStream = new FileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
		
		//Rank 7
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = new File(str);
//		FileInputStream fileInputStream = SecureAction.getFileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
		
		//Rank 8
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = LocationManager.getConfigurationFile(str);
//		FileInputStream fileInputStream = SecureAction.getFileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
		
		//Rank 9
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = Utilities.lookupLocalFile(str);
//		FileInputStream fileInputStream = SecureAction.getFileInputStream(file);
//		FileChannel fileChannel = fileInputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
	
		//Rank 10
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = new File(str);
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		FileChannel fileChannel = fileOutputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
		
		//Rank 11
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = LocationManager.getConfigurationFile(str);
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		FileChannel fileChannel = fileOutputStream.getChannel();
//		java.nio.MappedByteBuffer mp =fileChannel.map(mapMode, 0, 0); 
		
		//Rank 12
//		FileChannel$MapMode fileChannel$MapMode;
//		File file = Utilities.lookupLocalFile(str);
//		FileOutputStream fileOutputStream = new FileOutputStream(file);
//		FileChannel fileChannel = fileOutputStream.getChannel();
//		java.nio.MappedByteBuffer mp = fileChannel.map(mapMode, 0, 0);
	
	
	}
	
	
	public void Comp3 (org.eclipse.jface.viewers.TableViewer tv)
	{
		//Rank 1
//		org.eclipse.swt.widgets.Table tb = tv.getTable();

		//Rank 2
//		Control control = tv.getControl();
//		org.eclipse.swt.widgets.Table tb = (Table) control;

		//Rank 3	
//		Composite composite = tv.getTable();
//		org.eclipse.swt.widgets.Table tb = new Table(composite, 0);
	
		//Rank 4		
//		FormToolkit formToolkit;
//		Composite composite = tv.getTable();
//		org.eclipse.swt.widgets.Table tb = formToolkit.createTable(composite, 0);
	
	
	}
	
	
	public void Comp4(org.eclipse.ui.IWorkbench iwb)
	{

		//Rank 1
//		IPageService pageService = iwb.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = pageService.getActivePage();
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();

		//Rank 2
//		IAdaptable adaptable;
//		IWorkbenchWindow workbenchWindow = iwb.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = workbenchWindow.openPage(adaptable);
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();

		//Rank 3
//		IAdaptable adaptable;
//		IWorkbenchWindow workbenchWindow = iwb.openWorkbenchWindow(adaptable);
//		IWorkbenchPage workbenchPage = workbenchWindow.openPage(adaptable);
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();
		
		//Rank 4
//		IAdaptable adaptable;
//		IPageService pageService = iwb.openWorkbenchWindow(adaptable);
//		IWorkbenchPage workbenchPage = pageService.getActivePage();
//		org.eclipse.ui.IEditorPart iep =  workbenchPage.getActiveEditor();
		
		//Rank 5
//		IEditorInput editorInput;
//		IPageService pageService = iwb.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = pageService.getActivePage();
//		org.eclipse.ui.IEditorPart iep =  workbenchPage.findEditor(editorInput);
		
		//Rank 6
//		IFile file;
//		IPageService pageService = iwb.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = pageService.getActivePage();
//		org.eclipse.ui.IEditorPart iep = IDE.openEditor(workbenchPage, file, false); 

		//Next 7,8,9 are similar
		
		//Rank 10
//		String string;
//		IWorkbenchWindow workbenchWindow;
//		IWorkbenchPage workbenchPage = iwb.showPerspective(string, workbenchWindow);
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();

		//Rank 11
//		String string;
//		IAdaptable adaptable;
//		IWorkbenchWindow workbenchWindow = iwb.openWorkbenchWindow(string, adaptable);
//		IWorkbenchPage workbenchPage = workbenchWindow.openPage(adaptable);
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();
	
		//Rank 12
//		String string;
//		IWorkbench workbench;
//		IWorkbenchWindow workbenchWindow = iwb.getActiveWorkbenchWindow();
//		IWorkbenchPage workbenchPage = workbench.showPerspective(string, workbenchWindow);
//		org.eclipse.ui.IEditorPart iep = workbenchPage.getActiveEditor();
	
	
	}
	
	
	public void Comp5(org.eclipse.gef.ui.parts.ScrollingGraphicalViewer sgv )
	{
			//Rank 1
	//		Control control = sgv.getControl();
	//		org.eclipse.draw2d.FigureCanvas fc = (FigureCanvas) control;
			
	//		Rank 2
	//		Control control = sgv.getControl();
	//		Composite composite = control.getParent();
	//		org.eclipse.draw2d.FigureCanvas fc = new FigureCanvas(composite);
			
	//		Rank 3
	//		Control control = sgv.getControl();
	//		Composite composite = (FigureCanvas) control;
	//		org.eclipse.draw2d.FigureCanvas fc = new FigureCanvas(composite);
		
		
	}
	
	
//	public void Comp6(java.util.Enumeration e)
//	{
//		//Rank 1
//		//java.util.Iterator it = IteratorUtils.asIterator(e);
//		
//		//Rank 2
//		//java.util.Iterator it = new EnumerationIterator(e);
//		
//		//Rank 4
//		//AbstractList abstractList = Collections.list(e);
//		//java.util.Iterator it = abstractList.iterator();
//		
//		//Rank 8
//		//Collection collection = Collections.list(e);
//		//java.util.Iterator it = IteratorUtils.chainedIterator(collection);
//		
//		//Rank 11
//		//Iterator iterator = IteratorUtils.asIterator(e);
//		//java.util.Iterator it = IteratorUtils.toListIterator(iterator);
//		
//		//Rank 12
//		//List list = Collections.list(e);
//		//java.util.Iterator it = list.listIterator();
//		
//	}
	
	public void Comp6 (org.eclipse.jface.viewers.SelectionChangedEvent sce )
	{
	
		//org.eclipse.jface.viewers.ISelection is = sce.getSelection();
		
		//ISelectionProvider selectionProvider = sce.getSelectionProvider();
		//org.eclipse.jface.viewers.ISelection is = selectionProvider.getSelection();
		 
//		ISelection selection = sce.getSelection();
//		org.eclipse.jface.viewers.ISelection is = (IStructuredSelection) selection;

		
//		ISelection selection = sce.getSelection();
//		org.eclipse.jface.viewers.ISelection is =  (StructuredSelection) selection;

//		ISelection selection = sce.getSelection();
//		org.eclipse.jface.viewers.ISelection is = (ITextSelection) selection;
		
	}
	
	public void Comp7 (org.eclipse.jface.resource.ImageRegistry ir)
	{
//		String string;
//		org.eclipse.jface.resource.ImageDescriptor id = ir.getDescriptor(string);

//		String string;
//		ImageDescriptor imageDescriptor = ir.getDescriptor(string);
//		org.eclipse.jface.resource.ImageDescriptor id = new CachedImageDescriptor(imageDescriptor);
	
	
//		String string;
//		Point point;
//		ImageDescriptor imageDescriptor = ir.getDescriptor(string);
//		org.eclipse.jface.resource.ImageDescriptor id = new JavaElementImageDescriptor(imageDescriptor, 0, point);	
	
//		String string;
//		int[] intAry;
//		Point point;
//		ImageDescriptor[] imageDescriptorAry;
//		Image image = ir.get(string);
//		org.eclipse.jface.resource.ImageDescriptor id = new OverlayIcon(image, imageDescriptorAry, intAry, point);	
	}
	
	public void Comp8(java.util.Map m)
	{
//		Collection collection = m.values();
//		java.util.Iterator it = collection.iterator();

//		Collection collection = m.entrySet();
//		java.util.Iterator it = collection.iterator();
		
//		Collection collection = m.keySet();
//		java.util.Iterator it = collection.iterator();
		
		
		
		
		
	}
	
	
	public void Comp9(org.eclipse.jdt.core.ICompilationUnit icu)
	{

		
//		org.eclipse.jdt.core.dom.CompilationUnit cu = AST.parseCompilationUnit(icu, false);
	
//		IProgressMonitor progressMonitor;
//		WorkingCopyOwner workingCopyOwner;
//		org.eclipse.jdt.core.dom.CompilationUnit cu = icu.reconcile(0, false, workingCopyOwner, progressMonitor); 
		

//		ICompilationUnit compilationUnit;
//		IProgressMonitor progressMonitor;
//		WorkingCopyOwner workingCopyOwner = icu.getOwner();
//		org.eclipse.jdt.core.dom.CompilationUnit cu = compilationUnit.reconcile(0, false, workingCopyOwner, progressMonitor);
		
//		char[] charAry;
//		String string;
//		IJavaProject javaProject = icu.getJavaProject();
//		org.eclipse.jdt.core.dom.CompilationUnit cu = AST.parseCompilationUnit(charAry, string, javaProject);
		
		
	}
	
	
	public void Comp10 (org.eclipse.debug.ui.IDebugView idv)
	{
//		Class class;
//		Object object = idv.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;
		
		
//		Class class;
//		IAdaptable adaptable = idv.getViewSite();
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object; 		
		

//		Class class;
//		IAdaptable adaptable = idv.getSite();
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

//		String string;
//		Class class;
//		Object object = idv.getAdapter(class);
//		IJavaDebugTarget javaDebugTarget = (IJavaDebugTarget) object;
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = javaDebugTarget.findVariable(string);
	
//		String string;
//		Class class;
//		Object object = idv.getAdapter(class);
//		IJavaStackFrame javaStackFrame = (IJavaStackFrame) object;
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = javaStackFrame.findVariable(string);

		
//		IContributorResourceAdapter contributorResourceAdapter;
//		Class class;
//		IAdaptable adaptable = contributorResourceAdapter.getAdaptedResource(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

		
//		Class class;
//		DefaultTaskListResourceAdapter defaultTaskListResourceAdapter;
//		IAdaptable adaptable = defaultTaskListResourceAdapter.getAffectedResource(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

//		Class class;
//		ITaskListResourceAdapter taskListResourceAdapter;
//		IAdaptable adaptable = taskListResourceAdapter.getAffectedResource(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

//		Class class;
//		AdaptableList adaptableList;
//		IAdaptable adaptable = adaptableList.add(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

//		Class class;
//		ReadmeModelFactory readmeModelFactory;
//		IAdaptable adaptable = readmeModelFactory.getContentOutline(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

//		Class class;
//		AdaptableList adaptableList;
//		IAdaptable adaptable = adaptableList.add(idv);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;

		
//		String string;
//		Class class;
//		IAdaptable adaptable = new MarkElement(idv, string, 0, 0);
//		Object object = adaptable.getAdapter(class);
//		org.eclipse.jdt.debug.core.IJavaVariable ijv = (IJavaVariable) object;
	}
	
	public void Comp11(org.eclipse.core.resources.IWorkspace iws)
	{
		//org.eclipse.core.resources.IFile if =
		//No output
	}
	
	public void Comp12(org.eclipse.ui.IWorkbenchWindow iww)
	{
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IEditorPart editorPart = workbenchPage.getActiveEditor();
//		org.eclipse.ui.IViewPart ivp = (ContentOutline) editorPart;
		
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IWorkbenchPart workbenchPart = workbenchPage.getActivePart();
//		org.eclipse.ui.IViewPart ivp =  (ContentOutline) workbenchPart;

//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IWorkbenchPart workbenchPart = workbenchPage.getActivePart();
//		org.eclipse.ui.IViewPart ivp =  (IDebugView) workbenchPart;
		
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		org.eclipse.ui.IViewPart ivp =  workbenchPage.findView(string);

	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		org.eclipse.ui.IViewPart ivp = workbenchPage.showView(string);
	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		org.eclipse.ui.IViewPart ivp = workbenchPage.showView(string, string, 0); 
	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IViewReference viewReference = workbenchPage.findViewReference(string);
//		org.eclipse.ui.IViewPart ivp = viewReference.getView(false);

//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IViewReference viewReference = workbenchPage.findViewReference(string, string);
//		org.eclipse.ui.IViewPart ivp = viewReference.getView(false);
	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IViewReference viewReference = workbenchPage.findViewReference(string, string);
//		org.eclipse.ui.IViewPart ivp = viewReference.getView(false);
	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IViewPart viewPart = workbenchPage.showView(string, string, 0);
//		org.eclipse.ui.IViewPart ivp = (IConsoleView) viewPart;

//		IAdaptable adaptable;
//		IWorkbenchPage workbenchPage = iww.openPage(adaptable);
//		IEditorPart editorPart = workbenchPage.getActiveEditor();
//		org.eclipse.ui.IViewPart ivp = (ContentOutline) editorPart;
	
//		IAdaptable adaptable;
//		IWorkbenchPage workbenchPage = iww.openPage(adaptable);
//		IWorkbenchPart workbenchPart = workbenchPage.getActivePart();
//		org.eclipse.ui.IViewPart ivp = (ContentOutline) workbenchPart;
	
//		String string;
//		IWorkbenchPage workbenchPage = iww.getActivePage();
//		IViewPart viewPart = workbenchPage.findView(string);
//		org.eclipse.ui.IViewPart ivp = (IDebugView) viewPart;
	
		
		
	}
	
	public void Comp13(org.eclipse.core.resources.IFile ifile)
	{
//		String str  =  
	}
	
	
	public void Comp14(org.eclipse.ui.IWorkbenchPage iwb)
	{
//		IEditorPart[] editorPartAry = iwb.getEditors();
//		IEditorPart editorPart = editorPartAry[__index__];
//		ITextEditor textEditor = (ITextEditor) editorPart;
//		org.eclipse.ui.texteditor.IDocumentProvider idp  = textEditor.getDocumentProvider();
		
		
//		IEditorPart[] editorPartAry = iwb.getDirtyEditors();
//		IEditorPart editorPart = editorPartAry[__index__];
//		ITextEditor textEditor = (ITextEditor) editorPart;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = textEditor.getDocumentProvider();  
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		org.eclipse.ui.texteditor.IDocumentProvider idp =  documentProviderRegistry.getDocumentProvider(editorInput);

//		DocumentProviderRegistry documentProviderRegistry;
//		IWorkbenchPart workbenchPart = iwb.getActivePart();
//		IEditorPart editorPart = (IEditorPart) workbenchPart;
//		IEditorInput editorInput = editorPart.getEditorInput();
//		org.eclipse.ui.texteditor.IDocumentProvider idp =  documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IEditorInput editorInput = (FileEditorInput) editorInput;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);

//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IEditorInput editorInput = (FileEditorInput) editorInput;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IEditorInput editorInput = (IFileEditorInput) editorInput;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IEditorInput editorInput = (FileEditorInput) editorInput;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IEditorInput editorInput = (FileEditorInput) editorInput;
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.getActiveEditor();
//		IEditorInput editorInput = editorPart.getEditorInput();
//		IDocumentProvider documentProvider = documentProviderRegistry.getDocumentProvider(editorInput);
//		org.eclipse.ui.texteditor.IDocumentProvider idp = new TextFileDocumentProvider(documentProvider);

//		IEditorInput editorInput;
//		DocumentProviderRegistry documentProviderRegistry;
//		IEditorPart editorPart = iwb.findEditor(editorInput);
//		IEditorInput editorInput = editorPart.getEditorInput();
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
		
//		DocumentProviderRegistry documentProviderRegistry;
//		IFile file;
//		IEditorPart editorPart = IDE.openEditor(iwb, file, false);
//		IEditorInput editorInput = editorPart.getEditorInput();
//		org.eclipse.ui.texteditor.IDocumentProvider idp = documentProviderRegistry.getDocumentProvider(editorInput);
	}
	
	public void Comp15(org.eclipse.ui.IWorkbenchPage iwp)
	{
//		BaseSelectionListenerAction baseSelectionListenerAction = new OpenFileAction(iwp);
//		org.eclipse.jface.viewers.IStructuredSelection iss = baseSelectionListenerAction.getStructuredSelection();
		
//		BaseSelectionListenerAction baseSelectionListenerAction = new OpenSystemEditorAction(iwp);
//		org.eclipse.jface.viewers.IStructuredSelection iss = baseSelectionListenerAction.getStructuredSelection();
		
//		ISelection selection = iwp.getSelection();
//		org.eclipse.jface.viewers.IStructuredSelection iss = (IStructuredSelection) selection;

//		IWorkbenchWindow workbenchWindow = iwp.getWorkbenchWindow();
//		BaseSelectionListenerAction baseSelectionListenerAction = new ExportResourcesAction(workbenchWindow);
//		org.eclipse.jface.viewers.IStructuredSelection iss = baseSelectionListenerAction.getStructuredSelection();

//		IWorkbenchWindow workbenchWindow = iwp.getWorkbenchWindow();
//		BaseSelectionListenerAction baseSelectionListenerAction = new ImportResourcesAction(workbenchWindow);
//		org.eclipse.jface.viewers.IStructuredSelection iss = baseSelectionListenerAction.getStructuredSelection();

//		BaseSelectionListenerAction baseSelectionListenerAction = new OpenFileAction(iwp);
//		IStructuredSelection structuredSelection = baseSelectionListenerAction.getStructuredSelection();
//		org.eclipse.jface.viewers.IStructuredSelection iss = ResourceSelectionUtil.allResources(structuredSelection, 0);

//		BaseSelectionListenerAction baseSelectionListenerAction = new OpenSystemEditorAction(iwp);
//		IStructuredSelection structuredSelection = baseSelectionListenerAction.getStructuredSelection();
//		org.eclipse.jface.viewers.IStructuredSelection iss = ResourceSelectionUtil.allResources(structuredSelection, 0);

//		ISelection selection = iwp.getSelection();
//		IStructuredSelection structuredSelection = (IStructuredSelection) selection;
//		org.eclipse.jface.viewers.IStructuredSelection iss = ResourceSelectionUtil.allResources(structuredSelection, 0);

//		IEditorDescriptor editorDescriptor;
//		BaseSelectionListenerAction baseSelectionListenerAction = new OpenFileAction(iwp, editorDescriptor);
//		org.eclipse.jface.viewers.IStructuredSelection iss = baseSelectionListenerAction.getStructuredSelection();

//		
//		ISelectionProvider selectionProvider;
//		SelectionProviderAction selectionProviderAction = new ShowInNavigatorAction(iwp, selectionProvider);
//		org.eclipse.jface.viewers.IStructuredSelection iss =  selectionProviderAction.getStructuredSelection();

	
//		String string;
//		ISelection selection = iwp.getSelection(string);
//		org.eclipse.jface.viewers.IStructuredSelection iss = (IStructuredSelection) selection;

//		String string;
//		ISelection selection = iwp.getSelection(string);
//		org.eclipse.jface.viewers.IStructuredSelection iss = (IStructuredSelection) selection;
		
	
		
	}
	
	public void Comp16(org.eclipse.jface.viewers.TableViewer tv)
	{
//		Table table = tv.getTable();
//		org.eclipse.swt.widgets.TableColumn tc = table.getColumn(0);
		
//		Table table = tv.getTable();
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);
		
//		Table table = tv.getTable();
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0, 0); 
		
//		Control control = tv.getControl();
//		Table table = (Table) control;
//		org.eclipse.swt.widgets.TableColumn tc = table.getColumn(0);
		
//		Control control = tv.getControl();
//		Table table = (Table) control;
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);
		
//		Control control = tv.getControl();
//		Table table = (Table) control;
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);	
		
//		Composite composite = tv.getTable();
//		Table table = new Table(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = table.getColumn(0);
		
//		Composite composite = tv.getTable();
//		Table table = new Table(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);
		
//		Composite composite = tv.getTable();
//		Table table = new Table(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);
		
//		Composite composite = tv.getTable();
//		Table table = new Table(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc =new TableColumn(table, 0, 0);
		
//		FormToolkit formToolkit;
//		Composite composite = tv.getTable();
//		Table table = formToolkit.createTable(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = table.getColumn(0);
		
//		FormToolkit formToolkit;
//		Composite composite = tv.getTable();
//		Table table = formToolkit.createTable(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0);
		
//		FormToolkit formToolkit;
//		Composite composite = tv.getTable();
//		Table table = formToolkit.createTable(composite, 0);
//		org.eclipse.swt.widgets.TableColumn tc = new TableColumn(table, 0, 0);
	}
	
	public void Comp17(java.lang.String str)
	{
		//Rank 3
//		Reader reader = new FileReader(str);
//		java.io.BufferedReader br = new BufferedReader(reader);
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//Additional things to be added later to our document
	public void Comp_Ad1(java.util.Enumeration en)
	{
		//Rank 1
		//java.util.Iterator it = IteratorUtils.asIterator(en);
	}
	
	public void Comp_Ad2(org.eclipse.core.resources.IFile ifile)
	{
		//java.lang.String str = ifile.getName();
	}
	
	public void Comp_Ad3(org.eclipse.swt.events.KeyEvent ke)
	{
//		Display display = ke.display;
//		org.eclipse.swt.widgets.Shell sh = display.getActiveShell();
	}
	
	public void Comp_Ad4(org.eclipse.ui.IViewPart ivp)
	{
//		IWorkbenchSite workbenchSite = ivp.getViewSite();
//		Shell shell = workbenchSite.getShell();
//		ApplicationWindow applicationWindow = new ApplicationWindow(shell);
//		org.eclipse.jface.action.MenuManager mm= applicationWindow.getMenuBarManager();
		
	}
	
	public void Comp_Ad5(org.eclipse.ui.IEditorSite ies)
	{
		//org.eclipse.ui.ISelectionService iss = ies.getPage();
		
//		IWorkbenchWindow workbenchWindow = ies.getWorkbenchWindow();
//		org.eclipse.ui.ISelectionService iss = workbenchWindow.getSelectionService();
	}
	
	
*/	
}
