package pw.evaluation;


import java.io.FileOutputStream;

public class StrathconaExamples {

/*	
	//Strathcona driver for Case 1
	public void case1() {
		org.eclipse.jface.viewers.IStructuredSelection sel;
		java.io.InputStream is; 
	}

	//Case 2
	public void case2()
	{
//		org.eclipse.gef.ui.actions.ActionRegistry ar;
//		org.eclipse.jface.action.IAction ia;
	}
	
	
	//Case 3
	public void case3()
	{
		org.eclipse.ui.IEditorInput editorInput;
		org.eclipse.jdt.core.IJavaElement ije;

	}
	
	//Case 4
	public void Case4()
	{
//		org.eclipse.gef.GraphicalEditPart gefP;
//		org.eclipse.gef.commands.Command cm;
	}
	
	//Case 5
	public void Case5()
	{
		java.io.File file;
		javax.sound.sampled.Clip clip;
	}
	
	//Case 6
	public void Case6() {
		org.eclipse.ui.IEditorInput input;
		org.eclipse.core.resources.IFile ifile;
	}

	
	//Case 7
	public void case7()
	{
		org.eclipse.ui.IPageLayout ipl;
		org.eclipse.ui.IFolderLayout ifl;
	}
	
	//Case 8
	public void case8()
	{
		org.eclipse.jface.viewers.TreeViewer tv;
		org.eclipse.jface.viewers.IStructuredSelection ss;
	}
	
	
	//Strathcona Evaluation for logic code samples
	public void Logic_eg1()
	{
		org.eclipse.ui.part.IPageSite ips;
		org.eclipse.ui.IActionBars iab;
	}
	
	public void Logic_eg2()
	{
		org.eclipse.gef.ui.actions.ActionRegistry ar;
		org.eclipse.jface.action.IAction ia;
	}
	
	public void Logic_eg3()
	{
		org.eclipse.gef.ui.actions.ActionRegistry ar;
		org.eclipse.gef.ContextMenuProvider cmp;
	}
	
	public void Logic_eg4()
	{
		org.eclipse.ui.part.IPageSite ips;
		org.eclipse.jface.viewers.ISelectionProvider isp;
	}
	
	public void Logic_eg5()
	{
		org.eclipse.ui.part.IPageSite ips;
		org.eclipse.jface.action.IToolBarManager itb;
	}
	
	public void Logic_eg6()
	{
		java.lang.String str;
		org.eclipse.jface.resource.ImageDescriptor id;
	}
	
	
	public void Logic_eg7()
	{
		org.eclipse.swt.widgets.Composite cps;
		org.eclipse.swt.widgets.Control ctr;
	}
	
	public void Logic_eg8()
	{
		org.eclipse.swt.widgets.Composite cmp;
		org.eclipse.swt.widgets.Canvas cv;
	}
	
	public void Logic_eg9()
	{
		org.eclipse.gef.GraphicalViewer gv;
		org.eclipse.draw2d.parts.ScrollableThumbnail st;
	}
	
	
	public void Logic_eg10()
	{
		org.eclipse.gef.GraphicalViewer gv;
		org.eclipse.draw2d.IFigure ifig;
	}
	
	
	// Examples from ASTView 
	public void ASTView_eg1()
	{
		org.eclipse.jface.viewers.TreeViewer tv;
		org.eclipse.jface.viewers.StructuredSelection ss;
	}
	
	public void ASTView_eg2()
	{
		org.eclipse.core.filebuffers.IFileBuffer ifb;
		org.eclipse.jface.text.IDocument id;
	}
	
	public void ASTView_eg3()
	{
		org.eclipse.jface.text.DocumentEvent de;
		org.eclipse.jface.text.IDocument id;
	}
	
	public void ASTView_eg4()
	{
		org.eclipse.jface.viewers.SelectionChangedEvent sce;
		org.eclipse.jface.viewers.ISelection is;
	}
	
	public void ASTView_eg5()
	{
		org.eclipse.ui.IWorkbenchPartReference iwpr;
		org.eclipse.ui.IWorkbenchPart iwp;
		
	}
	
	public void ASTView_eg6()
	{
		org.eclipse.ui.IViewSite ivw;
		org.eclipse.ui.ISelectionService iss;
	}
	
	public void ASTView_eg7()
	{
		org.eclipse.ui.texteditor.ITextEditor ite;
		org.eclipse.jdt.core.IOpenable iop;
	}
	
	public void ASTView_eg8()
	{
		org.eclipse.ui.texteditor.ITextEditor ite;
		org.eclipse.jface.text.ITextSelection its;
	}
	
	public void ASTView_eg9()
	{
		org.eclipse.jdt.core.IJavaElement ije;
		org.eclipse.jdt.core.IJavaProject ijp;
	}
	
	public void ASTView_eg10()
	{
		org.eclipse.jdt.core.IOpenable ipop;
		org.eclipse.jdt.core.dom.CompilationUnit cu;
	}
	
	
	//Examples from Flow4j
	public void Flow4j_eg1()
	{
		String str;
		java.util.ResourceBundle rb;
	}
	
	public void Flow4j_eg2()
	{
		org.eclipse.core.runtime.Platform pt;
		org.eclipse.core.runtime.IAdapterManager iam;
	}
	public void Flow4j_eg3()
	{
		org.eclipse.ui.plugin.AbstractUIPlugin aui;
		java.lang.ClassLoader cl;
	}
	public void Flow4j_eg4()
	{
		org.eclipse.ui.plugin.AbstractUIPlugin aui;
		org.eclipse.ui.IEditorPart iep;
	}
	public void Flow4j_eg5()
	{
		org.eclipse.core.runtime.IPath ipath;
		java.net.URL url;
	}
	public void Flow4j_eg6()
	{
		org.eclipse.core.runtime.IPath ipath;
		java.io.File ifile;
	}
	public void Flow4j_eg7()
	{
		org.eclipse.ui.plugin.AbstractUIPlugin aui;
		String str;
	}
	public void Flow4j_eg8()
	{
		org.eclipse.ui.plugin.AbstractUIPlugin aui;
		java.io.File file;
	}
	public void Flow4j_eg9()
	{
		Class cls;
		java.io.InputStream is; 
	}
	public void Flow4j_eg10()
	{
		java.io.File file;
		java.io.FileOutputStream fos;
	}
	
	public void Flow4j_eg11()
	{
		java.io.File file;
		org.acm.seguin.util.FileSettings fs; 
	}

	public void Flow4j_eg12()
	{
		org.eclipse.core.resources.IContainer ic;
		org.eclipse.core.resources.IFolder if0;
	}
	
	
	// Evaluation of queries from XSnippet 
	public void XSnippet_q1()
	{
		org.eclipse.jface.viewers.ISelection iss;
		org.eclipse.jdt.core.ICompilationUnit icu;
	}
	
	public void XSnippet_q2()
	{
		org.eclipse.jface.viewers.IStructuredSelection iss;
		org.eclipse.jdt.core.ICompilationUnit icu;
	}
	
	public void XSnippet_q3()
	{
		org.eclipse.jdt.core.ElementChangedEvent iss;
		org.eclipse.jdt.core.ICompilationUnit icu;
	}
	
	public void XSnippet_q4()
	{
		org.eclipse.ui.IEditorPart iep;
		org.eclipse.jdt.core.ICompilationUnit icu;
	}
	
	public void XSnippet_q5()
	{
		org.eclipse.ui.IEditorPart iep;
		org.eclipse.ui.IEditorInput iei;
	}
	
	public void XSnippet_q6()
	{
		org.eclipse.ui.part.ViewPart vp;
		org.eclipse.ui.ISelectionService iss;
	}	
	
	public void XSnippet_q7()
	{
		org.eclipse.ui.texteditor.TextEditorAction tea;
		org.eclipse.ui.texteditor.ITextEditor ite;
	}
	
	public void XSnippet_q8()
	{
		org.eclipse.ui.texteditor.TextEditorAction tea;
		org.eclipse.jface.text.ITextSelection its;
	}
	
	public void XSnippet_q9()
	{
		org.eclipse.ui.texteditor.ITextEditor ite;
		org.eclipse.jface.text.ITextSelection its;
	}
	
	public void XSnippet_q10()
	{
		org.eclipse.ui.texteditor.AbstractDecoratedTextEditor adt;
		ProjectViewer pv; 
	}
	
	public void XSnippet_q11()
	{
		org.eclipse.ui.editors.text.TextEditor te;
		org.eclipse.jface.text.IDocument id;
	}
	
	public void XSnippet_q12()
	{
		org.eclipse.ui.editors.text.TextEditor te;
		org.eclipse.jface.text.ITextSelection its;	
	}
*/	
}
