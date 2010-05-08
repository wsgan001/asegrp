package apiusagemetrics.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionDelegate;

import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.code.analyzer.CodeExampleStore;
import xweb.code.analyzer.TypeHolder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;

import xweb.api.srcparser.ParseSrcFiles;
import xweb.core.ExternalObject;
import xweb.core.LibClassHolder;
import xweb.core.LibMethodHolder;
import xweb.core.RepositoryAnalyzer;
import xweb.core.RepositoryCreator;

public class APIUsageActions extends ActionDelegate implements IObjectActionDelegate {

	static private Logger logger = Logger.getLogger("APIUsageActions");
 
    /** The part target. */
    private IWorkbenchPart _part;

    private String codesamples_dir = System.getenv("ALATTIN_PATH");
    private static String localPackageDataFile = "PackageMappings.csv";
    private static String jexExceptionFile = "Jex_Exceptions.txt";	//This file is an output of Jex tool 
    																// + processed by my script. This gives exceptions thrown by a given method-invocation
    
    public static int MODE_RUNNING = CommonConstants.MINE_PATTERNS_CODE_SAMPLES;
    //public static int MODE_RUNNING = CommonConstants.DETECT_BUGS_IN_INPUTPROJECT;
    
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		 _part = targetPart;
	}

	
    public void run(IAction action)
    {
        IWorkbenchPartSite site = _part.getSite();
        ISelectionProvider provider = site.getSelectionProvider();
        StructuredSelection selection = (StructuredSelection) provider.getSelection();
        Object[] items = selection.toArray();
        
        RepositoryAnalyzer ra = RepositoryAnalyzer.getInstance();
        CommonConstants.reloadProperties();
        
        if(items.length != 1)
        {
        	logger.error("ERROR: This should be run only on one item");
        }
        
        if(items[0] instanceof IJavaProject)
        {
        	try
        	{
        		RepositoryAnalyzer.resetMIIdGen();
        		
	        	IJavaProject project = (IJavaProject) items[0];
	        	
	        	String codesamples_dir_local = codesamples_dir + "//" + project.getElementName().toString(); 
	        	
	        	String jexComplFileName = codesamples_dir + CommonConstants.FILE_SEP  + project.getElementName().toString() + CommonConstants.FILE_SEP + jexExceptionFile;
	        	//Check the jex exceptions file. If not present throw error and stop
	        	if(!CommonConstants.bIgnoreJex && !(MODE_RUNNING == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)) {
		        	try {
		        		File fObj = new File(jexComplFileName);
		        		FileReader frObj = new FileReader(fObj);
		        		frObj.close();
		        	} catch(FileNotFoundException ex) {
		        		System.err.println("Jex exceptions file missing!!! Exiting");
		        		logger.error("Jex exceptions file missing!!! Exiting");
		        		return;
		        	}
	        	}	
	        	
	        	ra.setLibProject(project);
	        	ParseSrcFiles psf = new ParseSrcFiles();
	        	psf.parseSources(project);
	        	
	        	loadPackageInfoToMemory(project);
	        	
	        	if(!CommonConstants.bIgnoreJex && !(MODE_RUNNING == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)) {
	        		loadJexExceptions(jexComplFileName);
	        	}
	        	       	
	        	/*Debugging section for getting list of classes (Includes both self APIs and External APIs*/
	    		try
	    		{
	    			BufferedWriter bw = new BufferedWriter(new FileWriter("APIList.txt"));
	    	        for(Iterator iter = ra.getLibClassMap().values().iterator(); iter.hasNext();)
	    	        {
	    	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	    	        	
	    	        	if(lcc.getMethods().length == 0)	//PERFORMANCE: Don't put classes with no methods interested
	    	        		continue;
	    	        	
	    	        	bw.write(lcc.getName() + "#");
	    	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	    	        		bw.write(lmh.toString() + "#");
	    	        	}
	    	        	
	    	        	bw.write("\n");
	    	        }
	    	       
	    	        for(Iterator iter = ra.getExternalObjects().values().iterator(); iter.hasNext();)
	    	        {
	    	        	ExternalObject eeObj = (ExternalObject) iter.next(); 
	    	        	String strName = eeObj.getClassName();
	    	        	
	    	        	//PERFORMANCE: Don't put java.lang classes and classes with no methods of interest
	    	        	if(strName.startsWith("java.lang"))
	    	        		continue;
	    	        	if(eeObj.getMiList().size() == 0)
	    	        		continue;
	    	        	
	    	        	bw.write(strName + "#");
	    	        		    	        	
	    	        	for(Iterator iterMI = eeObj.getMiList().iterator(); iterMI.hasNext(); ) {
	    	        		MethodInvocationHolder mihObj = (MethodInvocationHolder) iterMI.next();
	    	        		bw.write(mihObj.getMethodName() + mihObj.getArgumentString() + "#");
	    	        	}
	    	        	bw.write("\n");
	    	        }
	    	        bw.close();  
	    	        
	    	        bw = new BufferedWriter(new FileWriter("PackageList.txt"));
	    	        bw.write("<jexpath> c:\\jexfiles\n");
	    	        
	    	        
	    	        for(Iterator iter = ra.getImpStatementList().iterator();iter.hasNext();)
	    	        {
	    	        	String pckName = iter.next().toString();
	    	        	//bw.write("<genstub> " + pckName + "\n");
	    	        	bw.write("<package> " + pckName + "\n");
	    	        }
	    	        
	    	        for(Iterator iter = ra.getLibPackageList().iterator(); iter.hasNext();)
	    	        {
	    	        	String packageName = iter.next().toString();
	    	        	packageName = packageName.replace('.', '\\');
	    	        	packageName = "<###>" + packageName; 
	    	        	bw.write("<dir> " + packageName + "\n");	    	        	
	    	        }
	    	        	    	        
	    	        bw.close();
	    		}
	    		catch(Exception ex)
	    		{
	    			ex.printStackTrace();
	    		}	    		
	    		/*End of Debug Section*/
	    		
	    		//Analysing code samples collected from Google code search engine
	    		long beginReposCreation = System.currentTimeMillis();
	    		RepositoryCreator rcObj = new RepositoryCreator();
	    		rcObj.createLocalRepos(codesamples_dir_local);
	    		long endReposCreation = System.currentTimeMillis();
	    		
	    		logger.warn("Time taken for creating the repository " + (endReposCreation - beginReposCreation) + " msec");
	    			    		
	    		logger.debug("Finished downloading files...");
	    		ra.analyzeRepository(codesamples_dir_local);

	    		/*if(System.getenv("SERVER_MODE") != null)
	    		{
	    			runMAFIA(ra.getStaticTraceList());
	    			runBIDE(ra.getStaticTraceList());
	    			return;
	    		}	    		
	    		
	    		List<String> frequentSeqs = null;	
	    		switch(CommonConstants.miner_type) {
	    		case CommonConstants.FREQUENT_ITEMSET_MINER :
	    			frequentSeqs = runMAFIA(ra.getStaticTraceList());
	    			break;
	    		case CommonConstants.SUBSEQUENCE_MINER :
	    			frequentSeqs = runBIDE(ra.getStaticTraceList());
	    			break;
	    		case CommonConstants.ASSOCIATION_MINER :
	    			break;
	    		default: break;    		
	    		}
	    		
	    		//Printing the mined traces
	    		BufferedWriter bwOutFile = new BufferedWriter(new FileWriter("MinedTraces.txt"));
	    		HashMap<Integer, MethodInvocationHolder> mihMap = ra.getIdToSamplesMethodHolder();
	    		int trace_cnt = 1;
	    		for(String fseq : frequentSeqs) {
	    			String[] seqArr = fseq.split(" "); 
	    			
	    			//For MAFIA, the array should contain atleast one method id that can exist in non-exceptional blocks
	    			//If not, that sequence can be ignored
	    			if(CommonConstants.miner_type == CommonConstants.FREQUENT_ITEMSET_MINER) {
	    				boolean bNonExceptionMIDFound = false;
	    				
	    				for(int tcnt_tmp = 0; tcnt_tmp < seqArr.length; tcnt_tmp++) {
	    					
	    					if(nonExceptionMIDs.contains(new Integer(seqArr[tcnt_tmp]))) {
	    						bNonExceptionMIDFound = true;
	    						break;
	    					}    					
	    				}	    				
	    				
	    				if(!bNonExceptionMIDFound)
	    					continue;	    				
	    			}
	    			
	    			bwOutFile.write("Trace: " + trace_cnt + "\n");	    			
	    			for(int tcnt_tmp = 0; tcnt_tmp < seqArr.length; tcnt_tmp++) {
	    				MethodInvocationHolder mihObj = mihMap.get(new Integer(seqArr[tcnt_tmp]));
	    				bwOutFile.write(mihObj + "\n");	    				
	    			}
	    			
	    			bwOutFile.write("\n");
	    			trace_cnt++;
	    		}
	    		bwOutFile.close();*/	    		
	    			
	    		//Freeing the memory
	    		RepositoryAnalyzer.clearInstance();
	    		MethodInvocationHolder.MIHKEYGEN = 0;
	    		
	    	}
        	catch(Throwable th)
        	{
        		th.printStackTrace();
        	}
        }
        else
        {
        	logger.error("ERROR: This should be run only on a Java project");
        }
    }	
    
    /*
     * Function for loading the package information from local memory
     */    
    public void loadPackageInfoToMemory(IJavaProject project) 
	{
		try
		{
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
			HashMap<String, String> sampleIDMapper = raObj.getCodeSampleIDToPackageMapper();
			
			String packageMappings = codesamples_dir + CommonConstants.FILE_SEP  + project.getElementName().toString() + CommonConstants.FILE_SEP + localPackageDataFile;
			Scanner packageSc = new Scanner(new File(packageMappings));
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
     * Function for loading the Jex exceptions file
     * @param jexExFile
     */
    public void loadJexExceptions(String jexExFile) {
    	try {
    		Scanner packageSc = new Scanner(new File(jexExFile));
    		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
    		Set<String> libClsSet = raObj.getLibClassMap().keySet();
			while(packageSc.hasNextLine()) {
				String packageInfo = packageSc.nextLine();
				String parts[] = packageInfo.split(":");
				
				if(parts.length != 3)
					continue;
				
				int dollorIndex = -1;
				if((dollorIndex = parts[1].indexOf("$")) != -1) {
					//Class name is of the form java.lang.NullPointerException:org.axiondb.util.BTree$StateStack:isEmpty()
					//Target is to convert into the form java.lang.NullPointerException:org.axiondb.util.StateStack:isEmpty()
					
					int lastDotIndex = parts[1].lastIndexOf('.');
					parts[1] = parts[1].substring(0, lastDotIndex) + "." + parts[1].substring(dollorIndex + 1, parts[1].length());
				}
				
				//Check whether this is an external or internal class
				MethodInvocationHolder mihObj = MethodInvocationHolder.parseFromString(parts[2], new TypeHolder(parts[1]));
				if(libClsSet.contains(parts[1])) {
					LibMethodHolder lmhObj = raObj.getEqviMethodDeclaration(parts[1], mihObj);
					if(lmhObj == null)
						continue;
					lmhObj.addToExceptionSet(parts[0]);				
				} else {
					ExternalObject relObj = raObj.getExternalObjects().get(parts[1]);
					if(relObj == null)
						continue;
					
					MethodInvocationHolder equiMihObj = relObj.containsMI(mihObj);
					if(equiMihObj != null)
						equiMihObj.addToExceptionSet(parts[0]);			
					
					//Add this to its children also
					if(relObj.getChildClasses().size() != 0) {
						addToChildClasses(relObj, mihObj, parts[0], raObj);
					}					
				}	
				
			}
				
			packageSc.close();
    		
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
    
    private void addToChildClasses(ExternalObject eeObj, MethodInvocationHolder mihObj, String exceptionName, RepositoryAnalyzer raObj) {
    	for(String childClsName : eeObj.getChildClasses()) {
    		ExternalObject relObj = raObj.getExternalObjects().get(childClsName);
    		
    		if(relObj == null)
    			continue;
    		
    		mihObj.getReceiverClass().type = relObj.getClassName();
    		MethodInvocationHolder equiMihObj = relObj.containsMI(mihObj);
    		if(equiMihObj != null)
				equiMihObj.addToExceptionSet(exceptionName);	
			
    		if(relObj.getChildClasses().size() != 0) {
				addToChildClasses(relObj, mihObj, exceptionName, raObj);
			} 		
    	} 	
    }
    
    
    
    /**
     * Function for generating output for subsequence miner BIDE
     * @return The file name with the mined output
     */
    public List<String> runBIDE(List<CodeExampleStore> traceList) {
    	
    	List<String> minedList = new ArrayList<String>();
    	
    	try {
	    	BufferedWriter bwIDs = new BufferedWriter(new FileWriter("mcseq_bide.txt"));
	    	int maxLength = 0, totalLength = 0;
	    	HashSet<String> uniqueItemSet = new HashSet<String>();
	    	/*for(StaticTraceStore traceStr : RepositoryAnalyzer.getInstance().getStaticTraceList()) {
	    		bwIDs.write(traceStr.sequence + "-1\n");
	    		String strArr[] = traceStr.sequence.split(" "); 
	    		
	    		for(String mthId : strArr) {
	    			uniqueItemSet.add(mthId);   			
	    		}    		
	    		
				if(strArr.length > maxLength) {
					maxLength = strArr.length; 
				}
				totalLength += strArr.length;
			}*/	
	    	bwIDs.write("-2\n");
			bwIDs.close();
	    	  	
	    	
			BufferedWriter bwSpec = new BufferedWriter(new FileWriter("mcseq.spec"));
			bwSpec.write("mcseq_bide.txt\n");
			bwSpec.write(uniqueItemSet.size() + "\n");
			bwSpec.write(traceList.size() + "\n");
			bwSpec.write(maxLength + "\n");
			bwSpec.write((totalLength / traceList.size()) + "\n\n");
			
			
			bwSpec.write("#" + uniqueItemSet.size() + ":number of unique items\n");
			bwSpec.write("#" + traceList.size() + ":number of sequences\n");
			bwSpec.write("#" + maxLength + ":maximal length of a sequence\n");
			bwSpec.write("#" + (totalLength / traceList.size()) + ":average length of a sequence\n\n");
			bwSpec.close();
	
			if(System.getenv("SERVER_MODE") != null) {
	    		return minedList;
	    	}		
			
			try
			{
				Runtime javaRuntime = Runtime.getRuntime();
				String command = "bide_without_output.exe mcseq.spec 0.2 > bidoutput.txt ";
			
				@SuppressWarnings("unused")
				Process bideProcess = javaRuntime.exec(command);
				Thread.sleep(5 * 1000); //TODO: To replace with wait function later 
				//bideProcess.waitFor();  //NOTE: This method hangs if the process initiated process any console output and it is not consumed
			}
			catch(Exception ex)
			{
				logger.error("Error in executing BIDE!!!" + ex);
			}
			
			
			Scanner bide_out = new Scanner(new File("frequent.dat"));    		
    		while(bide_out.hasNextLine())
    		{
    			String freqSeq = bide_out.nextLine();
    			minedList.add((freqSeq.substring(0,freqSeq.indexOf(":") - 1))); 		
    		}
    		
    		bide_out.close();			    	
    	}
    	catch(Exception ex) {
    		ex.printStackTrace();
    	}
    	
    	return minedList;
    }
    
    
    //As a frequent itemset miner can change the order, we maintain a list to note the list
    //of APIs that appear in non-exceptional blocks in the Hashset
    HashSet<Integer> nonExceptionMIDs = new HashSet<Integer>();
    
    
    /**
     * Function for generating output for itemset miner MAFIA
     * @return The file name with the mined output
     */
    public List<String> runMAFIA(List<CodeExampleStore> traceList) {
    	
    	List<String> minedList = new ArrayList<String>();
    	BufferedWriter bwIDs;
		try {
			bwIDs = new BufferedWriter(new FileWriter("mcseq_mafia.txt"));
		   	/*for(StaticTraceStore traceStr : RepositoryAnalyzer.getInstance().getStaticTraceList()) {
	    		bwIDs.write(traceStr.sequence + "\n");  
	    		
	    		String strArr[] = traceStr.sequence.split(" "); 
	    		nonExceptionMIDs.add(new Integer(strArr[0]));	    		
			}*/	
	    	bwIDs.close();	    	    
	    	
	    	if(System.getenv("SERVER_MODE") != null) {
	    		return minedList;
	    	}
	    	
	    	//Executing Mafia tool
    		try
			{
				Runtime javaRuntime = Runtime.getRuntime();
				String command = "mafia.exe -mfi .008 -ascii mcseq_mafia.txt frequent.dat ";
			
				@SuppressWarnings("unused")
				Process bideProcess = javaRuntime.exec(command);
				Thread.sleep(5 * 1000); //TODO: To replace with WAIT later
				//bideProcess.waitFor();  //NOTE: This method hangs if the process initiated process any console output and it is not consumed
			}
			catch(Exception ex)
			{
				logger.error("Error in executing MAFIA!!!" + ex);
			}	   
			
			Scanner mafia_out = new Scanner(new File("frequent.dat"));    		
    		while(mafia_out.hasNextLine())
    		{
    			String freqSeq = mafia_out.nextLine();
    			if(freqSeq.length() <= 3) {
    				//This indicates no sequences are mined. which could be a rare case though 
    				continue;
    			}
    			minedList.add((freqSeq.substring(0,freqSeq.indexOf("(") - 1))); 		
    		}    		
    		mafia_out.close();	    	
		} catch (IOException e) {			
			e.printStackTrace();
		}
		
		return minedList;
    }
}
