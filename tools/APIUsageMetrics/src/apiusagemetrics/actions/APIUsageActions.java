package apiusagemetrics.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.actions.ActionDelegate;
import api.srcparser.ParseSrcFiles;
import api.usage.LibClassHolder;
import api.usage.LibMethodHolder;
import api.usage.RepositoryAnalyzer;
import api.usage.RepositoryCreator;
import api.usage.CodeExampleStore;

public class APIUsageActions extends ActionDelegate implements IObjectActionDelegate {

	static private Logger logger = Logger.getLogger("APIUsageActions");
 
    /** The part target. */
    private IWorkbenchPart _part;

    private String codesamples_dir = "C:\\APIUsage\\";
    private static String localPackageDataFile = "PackageMappings.csv";
    
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
	        	
	        	String codesamples_dir_local = codesamples_dir + project.getElementName().toString(); 
	        	
	        	ra.setLibProject(project);
	        	ParseSrcFiles psf = new ParseSrcFiles();
	        	psf.parseSources(project);
	        	
	        	loadPackageInfoToMemory(project);
	        	
	        	/*Debugging section for getting list of classes*/
	    		try
	    		{
	    			BufferedWriter bw = new BufferedWriter(new FileWriter("APIList.txt"));
	    	        for(Iterator iter = ra.getLibClassMap().values().iterator(); iter.hasNext();)
	    	        {
	    	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	    	        	
	    	        	bw.write(lcc.getName() + "#");
	    	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	    	        		bw.write(lmh.toString() + "#");
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
	    		
	    		//Analysing code samples collected from Google code search engine
	    		RepositoryCreator rcObj = new RepositoryCreator();
	    		rcObj.createLocalRepos(codesamples_dir_local);
	    		
	    		logger.debug("Finished downloading files...");
	    		ra.analyzeRepository(codesamples_dir_local);

	    		
	    		//Debugging output of all collected sequences from the samples
	    		logger.info("Outputting all gathered sequenes...");
	    		List<String> seqList = ra.getMethodIDSequences();
	    		BufferedWriter bw = new BufferedWriter(new FileWriter("MethodSequences.txt"));
	    		int counter = 0;
	    		for(String sequence : seqList)
	    		{
	    			counter++;
	    			bw.write("Sequence " + counter + "\n");
	    			
	    			String elementArr[] = sequence.split(" ");
	    			for(String elem : elementArr)
	    			{
	    				LibMethodHolder lmh = ra.getIdToLibMethod().get(Integer.parseInt(elem));	
	    				bw.write("\t " + /*lmh.getDescriptor() + " : " +*/ lmh.getContainingClass().getName() + " : " + lmh + "\n");
	    			}
	    		}
	    		bw.close();
	    		//End of debugging output
	    		    		
	    		//MAFIA: Writing files for mining (mcseq.txt and mcseq.spec)
	    		/*List<String> seqIDList = ra.getMethodIDSequences();
	    		BufferedWriter bwBideMcSeq = new BufferedWriter(new FileWriter("mcseq.txt"));
	    		int seqCounter = 0, maxLen = 0, totalSeqLength = 0;
	    		for(String IDSequence : seqIDList)
	    		{
	    			bwBideMcSeq.write(IDSequence + " \n");
	    			seqCounter++;
	    			if(IDSequence.length() > maxLen)
	    				maxLen = IDSequence.length();
	    			totalSeqLength += IDSequence.length();
	    		}
	    		bwBideMcSeq.write(" \n");
	    		bwBideMcSeq.close();
	    		//End of Mafia output
	    		
	    		//Executing Mafia tool
	    		try
				{
					Runtime javaRuntime = Runtime.getRuntime();
					String command = "mafia.exe -mfi .008 -ascii mcseq.txt frequent.dat ";
				
					@SuppressWarnings("unused")
					Process bideProcess = javaRuntime.exec(command);
					Thread.sleep(60 * 1000); //TODO: To replace with WAIT later
					//bideProcess.waitFor();  //NOTE: This method hangs if the process initiated process any console output and it is not consumed
				}
				catch(Exception ex)
				{
					logger.error("Error in executing MAFIA!!!" + ex);
				}

	    		//Reading the BIDE output: Each frequent sequence must include only APIs categorized
				//as hotspots
				HashSet<Integer> hotspotSet = new HashSet<Integer>();
				BufferedWriter bw_freqSeq = new BufferedWriter(new FileWriter("FrequentSeq.txt"));
	    		Scanner bide_out = new Scanner(new File("frequent.dat"));
	    		
	    		HashSet<CodeExampleStore> codeExampleStore = RepositoryAnalyzer.getInstance().getCodeExampleSet();
	    		
	    		
	    		int fcounter = 0;
	    		while(bide_out.hasNextLine())
	    		{
	    			fcounter++;
	    			String freqSeq = bide_out.nextLine();
	    			String[] freqSeqList = (freqSeq.substring(0,freqSeq.indexOf("("))).split(" "); 
	    			bw_freqSeq.write("Frequent Usage Scenario: " + fcounter + "\n");
	    			
	    			HashSet<Integer> currentSeqSet = new HashSet<Integer>();
	    			String scenarioType = "InvokesUS";
	    			for(String miId : freqSeqList)
	    			{
	    				Integer intObj = new Integer(miId);
	    				hotspotSet.add(intObj);
	    				currentSeqSet.add(intObj);
	    				LibMethodHolder lmh = ra.getIdToLibMethod().get(intObj);
	    				
	    				bw_freqSeq.write("\t " + lmh.getContainingClass().getName() + " : " + lmh + "\n");
	    				if(lmh.getName().indexOf("ExtendsClass") != -1) {
	    					scenarioType = "ExtendsUS";
	    				}
	    				
	    				if(lmh.getName().indexOf("ImplementsInterface") != -1) {
	    					scenarioType = "ImplementsUS";
	    				}
	    					
	    				//if((lmh.getMethodType() & LibMethodHolder.TEMPLATE) == 0 && (lmh.getMethodType() & LibMethodHolder.HOOK) != 0)
	    				//{
	    				//	logger.warn("Warning: Hook method appeared in main sequence " + lmh);
	    				//}
	    				
	    				//Get the corresponding hook hotspots
	    				//HashSet<LibMethodHolder> methodsInvoked = lmh.getInvokedMethods();
	    				//for(LibMethodHolder invokedLMH : methodsInvoked)
	    				//{
	    				//	if((invokedLMH.getMethodType() & LibMethodHolder.TEMPLATE) == 0 && (invokedLMH.getMethodType() & LibMethodHolder.HOOK) != 0)
	    				//	{
	    				//		bw_freqSeq.write("\t\tStrict Hook: " + invokedLMH.getContainingClass().getName() + " : " + invokedLMH + "\n");
	    				//	} else if ((invokedLMH.getMethodType() & LibMethodHolder.HOOK) != 0)
	    				//	{
	    				//		bw_freqSeq.write("\t\tOptional Hook: " + invokedLMH.getContainingClass().getName() + " : " + invokedLMH + "\n");
	    				//	}
	    				//}	    				
	    			}  
	    			bw_freqSeq.write("Scenario Type : " + scenarioType);
	    			//Identify the equivalent example
	    			for(CodeExampleStore cesObj : codeExampleStore) {
	    				if(cesObj.methodIds.containsAll(currentSeqSet))
	    				{
	    					bw_freqSeq.write(" FileName : " + cesObj.filename + " MethodName : " + cesObj.methodname + "\n");
	    					break;
	    				}
	    			}
	    		}
	    		
				bide_out.close();
				bw_freqSeq.close();
	
				
				logger.info("Number of Final Hotspots: " + hotspotSet.size());
				logger.info("Hotspot IDs: " + hotspotSet);
				*/
	    		//Freeing the memory
	    		RepositoryAnalyzer.clearInstance();
	    		
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
    
    
    public void loadPackageInfoToMemory(IJavaProject project) 
	{
		try
		{
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
			HashMap<String, String> sampleIDMapper = raObj.getCodeSampleIDToPackageMapper();
			
			String packageMappings = codesamples_dir + project.getElementName().toString() + "\\" + localPackageDataFile;
			Scanner packageSc = new Scanner(new File(packageMappings));
			while(packageSc.hasNextLine()) {
				String packageInfo = packageSc.nextLine();
				
				if(packageInfo.indexOf("junit_awtui_AboutDialog3_AboutDialog.Java") != -1) {
					int i = 0;
				}
				
				int indexOfComma = packageInfo.indexOf(","); 
				sampleIDMapper.put(packageInfo.substring(0,indexOfComma ), packageInfo.substring(indexOfComma + 1, packageInfo.length()));
			}
				
			packageSc.close();	
		
		} catch(Exception ex) 
		{
			logger.error("Exception occurred" + ex);
		}
		
	}
}
