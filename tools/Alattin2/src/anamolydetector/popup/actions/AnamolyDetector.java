package anamolydetector.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

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

import pw.code.analyzer.TypeHolder;
import pw.code.analyzer.holder.CondVarHolder_Typeholder;
import pw.code.analyzer.holder.Holder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.code.analyzer.holder.PrePostPathHolder;
import pw.code.downloader.GCodeDownloader;
import pw.common.CommonConstants;

public class AnamolyDetector implements IObjectActionDelegate {

	static private Logger logger = Logger.getLogger("AnamolyDetector");
	private IWorkbenchPart _part;
	
	
	//A workaround for storing package names locally and reusing them. This
	//variable must be set to "" during final release.
	static String localPackageDataFile = "";
	

    private String codesamples_dir = System.getenv("ALATTIN_PATH");
	
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
		    		
		    		//User can set the operation mode to mine patterns or detect defects
		    		if(CommonConstants.userConfiguredMode == CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES
		    				|| CommonConstants.userConfiguredMode == CommonConstants.MINE_PATTERNS_FROM_LIBRARY) {
		    			startProcess(codesamples_dir_local);
		    		} else {
		    			detectDefects("", CommonConstants.inputPatternFile);
		    		}	    		
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
	
	//function for collecting patterns
	public static void startProcess(String codesamples_dir_local) throws IOException
	{
		//Common initialization stuff
		RepositoryAnalyzer.numDownloadedCodeSamples = 0;
		RepositoryAnalyzer.numAnalyzedCodeSamples = 0;
		
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
		
		//TODO: For time being commented out the defect detection portion
		/*CommonConstants.OPERATION_MODE = CommonConstants.userConfiguredMode;
		logger.warn("TIMING: Start of finding violations: " + System.currentTimeMillis());
		ra.analyzeRepository(codesamples_dir_local);
		logger.warn("TIMING: End of finding violations: " + System.currentTimeMillis());*/
				
		//Freeing the memory and post processing conditions
		RepositoryAnalyzer.clearInstance();
		GCodeDownloader.bwPackageDetails.close();
		GCodeDownloader.bwPackageDetails = null;
		localPackageDataFile = "";
	}
	
	public static void detectDefects(String codesamples_dir_local, String inpPatternFile) throws IOException, Exception
	{
		//Common initialization stuff
		RepositoryAnalyzer.numDownloadedCodeSamples = 0;
		RepositoryAnalyzer.numAnalyzedCodeSamples = 0;		
		GCodeDownloader.bwPackageDetails = new BufferedWriter(new FileWriter("PackageMappings.csv")); 
		RepositoryAnalyzer.resetMIIdGen();
		loadPackageInfoToMemory();
		
		//Load the patterns from the input file. Assuming the pattern file name as ConsolidatedOutput.csv
		File patternFile = new File(inpPatternFile);
		if(!patternFile.exists()) {
			System.err.println("Pattern file does not exist");
			logger.error("Pattern file does not exist");
			return;
		}
		
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		
		//All input patterns are loaded into PrePostLists 
		Scanner patternScanner = new Scanner(patternFile);
		patternScanner.nextLine(); //removing the header		
		
		while(patternScanner.hasNextLine()) {
			String inpPattern = patternScanner.nextLine();
			
			String[] inpPatternParts = inpPattern.split(",");
			String libMIHStr = inpPatternParts[1];	 
			int firstColon = libMIHStr.indexOf(':');
			String receiverObj = libMIHStr.substring(0, firstColon);
			String argumentStr = libMIHStr.substring(firstColon + 1);
			argumentStr = argumentStr.replaceAll("::", ":");
			argumentStr = argumentStr.replaceAll(":", ",");			
			MethodInvocationHolder inpLibMIH = MethodInvocationHolder.parseFromString(argumentStr, new TypeHolder(receiverObj));
					
			int position = Integer.parseInt(inpPatternParts[2]); // 0: Backward, 1: Forward
			int balanced = Integer.parseInt(inpPatternParts[3]); // 0: Balanced, 1: Imbalanced
			
			//No need to load imbalance patterns, if the bug detection mode is not imbalanced patterns			
			if(balanced == 1 && CommonConstants.BUG_DETECTION_MODE != CommonConstants.IMBALANCED_PATTERNS) {
				continue;
			}			
			
			String actualPatternPart = inpPatternParts[4]; 
			float globalSupport = Float.parseFloat(inpPatternParts[6]);
			float localSupport = Float.parseFloat(inpPatternParts[7]);
			
			String[] conditionChkArr = actualPatternPart.split("&&");			
			Set<Holder> prePath = new HashSet<Holder>();
			Set<Holder> postPath = new HashSet<Holder>();
			try
			{
				for(String conditionChk : conditionChkArr) {				
					//Extracting the position (before or after) from the pattern
					int lastOpenBrace = conditionChk.lastIndexOf("(");
					int lastCloseBrace = conditionChk.lastIndexOf(")");
					String positionStr = conditionChk.substring(lastOpenBrace + 1, lastCloseBrace);
					int positionOfPattern = Integer.parseInt(positionStr);				
					
					conditionChk = conditionChk.substring(0, lastOpenBrace);
					
					CondVarHolder_Typeholder cvh_thObj = CondVarHolder_Typeholder.parseFromString(conditionChk);				
					if(positionOfPattern == 0) {
						prePath.add(cvh_thObj);
					} else {
						postPath.add(cvh_thObj);
					}
				}
			}
			catch(Exception ex)
			{
				ex.printStackTrace();	
				System.out.println(inpPattern);
				throw ex;
			}
			
			//Read the entire pattern from the input file. Load the pattern into MethodinvocationHolder
			HashMap<String, ExternalObject> externalObjectSet = RepositoryAnalyzer.getInstance().getExternalObjects(); 
			ExternalObject eeObj = externalObjectSet.get(receiverObj);
			
			if(eeObj == null) {
				logger.error("Could not find object: " + receiverObj + ", IGNORING !!!");
				continue;
			}
			
			MethodInvocationHolder eeMIH = eeObj.containsMI(inpLibMIH);	
			if(eeMIH == null) {
				logger.error("Could not find methodinvocation: " + inpLibMIH + ", IGNORING !!!");
				continue;
			}		
			
			PrePostPathHolder pppHObj = new PrePostPathHolder(prePath, postPath);
			pppHObj.balanced = balanced;
			pppHObj.globalSupport = globalSupport;
			pppHObj.localSupport = localSupport;			
			eeMIH.addToPrePostList(pppHObj);		
		}		
		
		//Classifying the patterns into three categories: Mainly for collecting statistics
		//SingleCheck, Balanced, Imbalanced... Also computing the support value for sorting
		//detected defects
		int bTotalNumPattern = 0, bTotalSingleCheck = 0, bTotalBalanced = 0, bTotalImbalanced = 0;
		for(ExternalObject eeObj : RepositoryAnalyzer.getInstance().getExternalObjects().values()) {
			for(MethodInvocationHolder eeMIH : eeObj.getMiList()) {
				List<PrePostPathHolder> pppHList = eeMIH.getPrePostList();
				if(pppHList == null || pppHList.size() == 0) {
					continue;
				}
				
				bTotalNumPattern++;
				
				boolean bNoImbalance = true;
				for(PrePostPathHolder pppHObj : pppHList) {
					if(pppHObj.balanced == 1) {
						bNoImbalance = false;
						break;
					}				
				}
				
				if(!bNoImbalance) {
					//Mark the special flag as all a result of imbalance patterns
					bTotalImbalanced++;
					for(PrePostPathHolder pppHObj : pppHList) {
						pppHObj.special_balance_flag = CommonConstants.DFC_IMBALANCED_PATTERNS;
					}
				} else {
					//Not an imbalance. Check whether there is only one possibility
					//In this case, mark this pattern as SINGLE_CHECK pattern
					if(pppHList.size() == 1) {
						bTotalSingleCheck++;
						for(PrePostPathHolder pppHObj : pppHList) {
							pppHObj.special_balance_flag = CommonConstants.DFC_SINGLE_CHECK_PATTERNS;
						}	
					} 
				}				
					
				if(pppHList.size() > 1) {
					//Set them as balanced, only when there are more than one subpattern for this MI
					//atleast two of them should be registered as balanced. TODO: This part is not correct
					bTotalBalanced++;
					for(PrePostPathHolder pppHObj : pppHList) {
						if(bNoImbalance)
							pppHObj.special_balance_flag = CommonConstants.DFC_BALANCED_PATTERNS;
					}				
				}
				
				double maxGlobalSupport = 0.0;
				for(PrePostPathHolder pppHObj : pppHList) {
					if(maxGlobalSupport < pppHObj.globalSupport) {
						maxGlobalSupport = pppHObj.globalSupport;
					}				
				}
				
				eeMIH.dominatingSupport = maxGlobalSupport; 
			}		
		}
		
				
		//Downloading code samples collected from Google code search engine		
		CommonConstants.OPERATION_MODE = CommonConstants.userConfiguredMode;
		logger.warn("TIMING: Start of finding violations: " + System.currentTimeMillis());
		raObj.analyzeRepository(codesamples_dir_local);
		logger.warn("TIMING: End of finding violations: " + System.currentTimeMillis());
				
		//Freeing the memory and post processing conditions
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
