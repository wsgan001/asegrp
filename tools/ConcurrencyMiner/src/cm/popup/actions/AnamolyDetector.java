package cm.popup.actions;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

import cm.common.CommonConstants;
import cm.inpapp.holder.ExternalObject;
import cm.inpapp.holder.LibClassHolder;
import cm.inpapp.holder.LibMethodHolder;
import cm.inpapp.parser.ParseSrcFiles;
import cm.samples.analyzer.holder.MethodInvocationHolder;



/**
 * Main class that is used when the tool is invoked as an Eclipse plugin
 * @author suresh_thummalapenta
 *
 */

public class AnamolyDetector implements IObjectActionDelegate {

	static private Logger logger = Logger.getLogger("AnamolyDetector");
	private IWorkbenchPart _part;
	
	
	private String codesamples_dir = System.getenv("MINEBUGS_PATH");
	
	/**
	 * Constructor for Action1.
	 */
	public AnamolyDetector() {
		super();
	}

	/**
	 * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)
	 */
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		_part = targetPart;
	}

	/**
	 * When used in plug-in mode, control comes here.
	 * 
	 * 
	 * @see IActionDelegate#run(IAction)
	 */
	public void run(IAction action) {
		logger.warn("TIMING: Start of process: " + System.currentTimeMillis());
		try
		{
			IWorkbenchPartSite site = _part.getSite();
	        ISelectionProvider provider = site.getSelectionProvider();
	        StructuredSelection selection = (StructuredSelection) provider.getSelection();
	        Object[] items = selection.toArray();
	        RepositoryAnalyzer ra = RepositoryAnalyzer.getInstance();
	
	        if(items.length != 1)
	        {
	        	logger.error("ERROR: This should be run only on one item");
	        }
	        
	        if(items[0] instanceof IJavaProject)
	        {
	        	try
	        	{
	        		IJavaProject project = (IJavaProject) items[0];
		        	
		        	String codesamples_dir_local = codesamples_dir + "/" + project.getElementName().toString(); 
		        	
		        	logger.warn("TIMING: Start of library scanner: " + System.currentTimeMillis());
		        	ra.setLibProject(project);
		        	ParseSrcFiles psf = new ParseSrcFiles(codesamples_dir_local + CommonConstants.FILE_SEP + "Dummy");
		        	psf.parseSources(project);
		        	logger.warn("TIMING: End of library scanner: " + System.currentTimeMillis());
		        			        	
		        	/*Debugging section for getting list of classes*/
		    		try
		    		{
		    			BufferedWriter bw = new BufferedWriter(new FileWriter("APIList.txt"));
		    	        for(Iterator iter = ra.getLibClassMap().values().iterator(); iter.hasNext();)
		    	        {
		    	        	LibClassHolder lcc = (LibClassHolder) iter.next();
		    	        	
		    	        	bw.write(lcc.getName() + "#");
		    	        	for (LibMethodHolder lmh : lcc.getMethods()) {
		    	        		//Filtering out our hidden methods
		    	        		if(lmh.getDescriptor().equals("Overrides") || lmh.getName().indexOf("ExtendsClass") != -1 ||  lmh.getName().indexOf("ImplementsInterface") != -1)
		    	        			continue;
		    	        		bw.write(lmh.getName() + lmh.getArgumentStr() + "#");
		    	        	}
		    	        	
		    	        	bw.write("\n");
		    	        }
		    	        bw.close();
     
		    			bw = new BufferedWriter(new FileWriter("ExternalObjects.txt"));
		    	        for(Iterator iter = ra.getExternalObjects().values().iterator(); iter.hasNext();)
		    	        {
		    	        	ExternalObject eeObj = (ExternalObject) iter.next(); 
		    	        	String strName = eeObj.getClassName();
		    	        	
		    	        	 	        	
		    	        	if(eeObj.getMiList().size() == 0) {
		    	        		continue;
		    	        	}
		    	        	
		    	        	bw.write(strName + "#");
		    	        		    	        	
		    	        	for(Iterator iterMI = eeObj.getMiList().iterator(); iterMI.hasNext(); ) {
		    	        		MethodInvocationHolder mihObj = (MethodInvocationHolder) iterMI.next();
		    	        		bw.write(mihObj.getMethodName() + mihObj.getArgumentString() + "#");
		    	        	}
		    	        	bw.write("\n");
		    	        }
		    	        bw.close();		    	        
		    		}
		    		catch(Exception ex)
		    		{
		    			ex.printStackTrace();
		    		}
		    		/*End of Debug Section*/
		    		
		    		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS;
		    		startProcess(codesamples_dir_local);
	        	}
	        	catch(Throwable th) {
	        		th.printStackTrace();
	        	}
	        }
	        else {
	        	logger.error("ERROR: This should be run only on a Java project");
	        }	        
	        logger.warn("TIMING: End of complete process: " + System.currentTimeMillis());      
		}
		catch(Exception ex)
		{			
			logger.error("Exception occured in main" + ex);
		}
	}
	
	//Main function for collecting patterns
	public static void startProcess(String codesamples_dir_local) throws IOException
	{
		//Common initialization stuff
		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS;
		RepositoryAnalyzer.resetMIIdGen();
				
		//Downloading code samples collected from Google code search engine
		RepositoryAnalyzer ra = RepositoryAnalyzer.getInstance();
		ra.analyzeRepository(codesamples_dir_local);
		
		//Freeing the memory and post processing conditions
		RepositoryAnalyzer.clearInstance();
	}
	
	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
