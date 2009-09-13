package anamolydetector.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import minebugs.core.RepositoryAnalyzer;
import minebugs.core.RepositoryCreator;
import minebugs.srclibhandlers.ExternalObject;
import minebugs.srclibhandlers.LibClassHolder;
import minebugs.srclibhandlers.LibMethodHolder;
import minebugs.srclibparser.ParseSrcFiles;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;

import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.code.downloader.GCodeDownloader;
import pw.common.CommonConstants;

public class AnamolyDetector implements IObjectActionDelegate {

	static private Logger logger = Logger.getLogger("FrameworkReuseBugs");
	private IWorkbenchPart _part;
	
	//A workaround for storing package names locally and reusing them. This
	//variable must be set to "" during final release.
	static String localPackageDataFile = "";
	private String codesamples_dir = System.getenv("MINEBUGS_PATH");
    public static String results_dir = "";
	
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
		        	results_dir = codesamples_dir + "/" + project.getElementName().toString() + "_results";
		        	//If the directory is not existing, create one.
		        	(new File(results_dir)).mkdirs();		        	
		        	
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
		    			
		    			/*BufferedWriter bw = new BufferedWriter(new FileWriter("APIList.txt"));
		    	        for(Iterator iter = ra.getLibClassMap().values().iterator(); iter.hasNext();)
		    	        {
		    	        	LibClassHolder lcc = (LibClassHolder) iter.next();
		    	        	
		    	        	bw.write(lcc.getName() + "\n");
		    	        	for (LibMethodHolder lmh : lcc.getMethods()) {
		    	        		//Filtering out our hidden methods
		    	        		if(lmh.getDescriptor().equals("Overrides") || lmh.getName().indexOf("ExtendsClass") != -1 ||  lmh.getName().indexOf("ImplementsInterface") != -1)
		    	        			continue;
		    	        		bw.write("," + lmh.getPrintString() + "\n");
		    	        	}
		    	        	
		    	        	bw.write("\n");
		    	        }
		    	        bw.close();*/
		    	        
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
		    	        		bw.write(mihObj.getPrintString() + "#");
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
	
	public static void startProcess(String codesamples_dir_local) throws IOException
	{
		//Common initialization stuff
		RepositoryAnalyzer.numDownloadedCodeSamples = 0;
		RepositoryAnalyzer.numAnalyzedCodeSamples = 0;
		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS;
		GCodeDownloader.bwPackageDetails = new BufferedWriter(new FileWriter("PackageMappings.csv")); 
		RepositoryAnalyzer.resetMIIdGen();
		loadPackageInfoToMemory();
		
		//Downloading code samples collected from Google code search engine
		logger.warn("TIMING: Start of CodeDownloader: " + System.currentTimeMillis());
		RepositoryAnalyzer ra = RepositoryAnalyzer.getInstance();
		RepositoryCreator rcObj = new RepositoryCreator();
		rcObj.createLocalRepos(codesamples_dir_local);
		logger.warn("TIMING: End of CodeDownloader: " + System.currentTimeMillis());
		logger.warn("TIMING: Total number of samples downloaded: " + RepositoryAnalyzer.numDownloadedCodeSamples);
		
		logger.warn("TIMING: Start of Mine Patters: " + System.currentTimeMillis());
		ra.analyzeRepository(codesamples_dir_local);
		logger.warn("TIMING: End of Mine Patterns: " + System.currentTimeMillis());
		logger.warn("TIMING: Total number of samples analyzed for mining: " + RepositoryAnalyzer.numAnalyzedCodeSamples);

		/*CommonConstants.OPERATION_MODE = CommonConstants.userConfiguredMode;
		logger.warn("TIMING: Start of finding violations: " + System.currentTimeMillis());
		ra.analyzeRepository(codesamples_dir_local);
		logger.warn("TIMING: End of finding violations: " + System.currentTimeMillis());*/
				
		//Freeing the memory and post processing conditions
		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS;
		RepositoryAnalyzer.clearInstance();
		GCodeDownloader.bwPackageDetails.close();
		GCodeDownloader.bwPackageDetails = null;
		localPackageDataFile = "";
	}
	
	public static void loadPackageInfoToMemory() 
	{
		if(localPackageDataFile.equals(""))
			return;
		try
		{
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
			HashMap<String, String> sampleIDMapper = raObj.getCodeSampleIDToPackageMapper();
			Scanner packageSc = new Scanner(new File(localPackageDataFile));
			while(packageSc.hasNextLine()) {
				String packageInfo = packageSc.nextLine();
				int indexOfComma = packageInfo.indexOf(","); 
				sampleIDMapper.put(packageInfo.substring(0,indexOfComma ), packageInfo.substring(indexOfComma + 1, packageInfo.length()));
			}
				
			packageSc.close();	
		
		} catch(Exception ex) 
		{
			logger.error("Exception occurred" + ex);
		}
		
	}
	

	/**
	 * @see IActionDelegate#selectionChanged(IAction, ISelection)
	 */
	public void selectionChanged(IAction action, ISelection selection) {
	}

}
