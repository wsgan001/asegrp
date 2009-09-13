package xweb.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import xweb.core.ExternalObject;
import xweb.core.LibClassHolder;
import xweb.core.LibMethodHolder;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;

import com.sun.org.apache.xml.internal.utils.StringToIntTable;

import apiusagemetrics.actions.APIUsageActions;

import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.code.analyzer.CodeExampleStore;
import xweb.code.analyzer.GCodeAnalyzer;
import xweb.code.analyzer.MinedPattern;
import xweb.code.analyzer.SequenceMiner;
import xweb.code.analyzer.TypeHolder;
import xweb.code.analyzer.holder.DefectHolder;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;
import java.io.File;

public class RepositoryAnalyzer {
	
	static private Logger logger = Logger.getLogger("RepositoryAnalyzer");
	
	HashMap<String, LibClassHolder> libClassMap = new HashMap<String, LibClassHolder>();
	HashSet<String> libPackageList = new HashSet<String>();
	HashMap<String, String> libClassToPackageMapper = new HashMap<String, String>();
	HashMap<Integer, LibMethodHolder> IdToLibMethod = new HashMap<Integer, LibMethodHolder>();
	
	HashMap<String, ExternalObject> externalObjects = new HashMap<String, ExternalObject>();		
	HashMap<String, String> codeSampleIDToPackageMapper = new HashMap<String, String>();
	HashSet<String> visitedCodeSamples = new HashSet<String>();
	
	//Only a temporary
	HashSet<String> impStatementList = new HashSet<String>();
	
	/** Variables specific to Association Rule Miner data collection **/
	Map<String, List<MethodInvocationHolder>> MihIDMap = null;	 
    { 
    	if(CommonConstants.bEnableAssocMiner) {
    		MihIDMap = new HashMap<String, List<MethodInvocationHolder>>();
    	}
    }
	BufferedWriter bwAssocMiner = null;    			//Entire information. Mainly useful for logging and debugging
	BufferedWriter bwAssocMinerNum = null;			//Writes all Data for mining into different files specific for each method "AssocMiner_Data"
	BufferedWriter bwAssocMethodIDs = null;			//Writes method Ids for external methods
	BufferedWriter bwAssocSubMethodIDs = null;		//Writes all IDs specific to that Method invocation in a separate folder "AssocMiner_IDs"
		
    /** End of code section for Association Rule Miner **/
    
	//Associated IJavaProject
	IJavaProject libProject = null;
	
	@SuppressWarnings("unchecked")
	Set<DefectHolder> defectSet = new TreeSet<DefectHolder>(new DefectHolder());
	
	private static int miCounterGen = 0;
	
	private static RepositoryAnalyzer instance;
	private RepositoryAnalyzer() {}
	
	public static RepositoryAnalyzer getInstance()
	{
		if(instance == null)
		{
			instance = new RepositoryAnalyzer();
		}
		return instance;
	}
	
	public static void clearInstance()
	{
		instance = null;
	}
	
	@SuppressWarnings("unchecked")
	public void analyzeRepository(String parentDir)
	{
		try {
			//Invoke GCodeAnalyzer for the each parent directory
			GCodeAnalyzer gdc = new GCodeAnalyzer();
			if(APIUsageActions.MODE_RUNNING != CommonConstants.DETECT_BUGS_IN_INPUTPROJECT) {
				//Code related to Association rule miner data collection
				if(CommonConstants.bEnableAssocMiner) {
					bwAssocMiner = new BufferedWriter(new FileWriter("AssocMinerData.txt"));
					bwAssocMethodIDs = new BufferedWriter(new FileWriter("AssocMethodIds.txt"));
					
					//Creating required dummy directories
					(new File("AssocMiner_Data")).mkdirs();
					(new File("AssocMiner_IDs")).mkdirs();
				}
						
				long beginAnalyzeAppl = System.currentTimeMillis();			
				
			    //After analyzing the code samples, analyze the library code also (NOTE: Analysis of library code
				//should be done before code examples as in code examples, we ignore the in-memory data after processing
				//every folder.
				CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT;
		        gdc.analyzeLibraryCode(parentDir, "", "", false);
		        long endAnalyzeAppl = System.currentTimeMillis();
			    logger.warn("Time taken for analyzing input application: " + (endAnalyzeAppl - beginAnalyzeAppl) + " msec");
			    
			    long beginAnalyzeRepos = System.currentTimeMillis();
		        	        
		        CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS_CODE_SAMPLES;
				File inputFile = new File(parentDir);
			    String[] candidates = inputFile.list();
			    for (String file : candidates) {
			      	File fcand = new File(parentDir + CommonConstants.FILE_SEP + file);
			       	if (!fcand.isDirectory()) {
			       		logger.debug("ERROR: Only directories can exist in Parent directory");
			       		continue;
			       	}
			       	
			       	logger.warn("Analyzing directory " + file);
			       	
			       	String currentLibClass = file.replace("_", ".");
			       	gdc.analyze(parentDir + CommonConstants.FILE_SEP + file, currentLibClass, "", false);
			        	
			        	
			       	//A new directory indicates a new object and whole new parsing. 
			       	//Therefore all caches related to these code samples must be cleared 
			       	visitedCodeSamples.clear();
			       	
			       	
			       	//Printing the data collected for association rule mining to output files 
			       	//Code specific to association rule mining, where unique IDs are assigned to each 
				    //method invocation while printing to the file
				    if(CommonConstants.bEnableAssocMiner) {
				    	ExternalObject eeObj = externalObjects.get(currentLibClass);
				    	if(eeObj != null) {
				    		for(MethodInvocationHolder eeMIH : eeObj.getMiList()) { 
				    			emitToAssocFile(eeMIH);
				    		}	
				    	} else {
				    		//This is a method internally declared by the program
				    		LibClassHolder lch = libClassMap.get(currentLibClass);
				    		if(lch != null && lch.getMethods() != null) {
					    		for(LibMethodHolder lmh : lch.getMethods()) {
					    			emitToAssocFile(lmh);
					    		}
					    	}
				    	}
				    	MihIDMap.clear();    	
				    	MethodInvocationHolder.MIHKEYGEN = 0;
				    }
				}
			        
			    gdc.clearAstc();
			    
			    long endAnalyzeRepos = System.currentTimeMillis();
			    logger.warn("Time taken for analyzing gathered code examples: " + (endAnalyzeRepos - beginAnalyzeRepos) + " msec");
			    
			    
			    //Emitting rest of the data collection for association rule mining
			    if(CommonConstants.bEnableAssocMiner) {
			    	for(ExternalObject eeObj : externalObjects.values()) {
			    		for(MethodInvocationHolder eeMIH : eeObj.getMiList()) { 
			    			emitToAssocFile(eeMIH);
			    		}	
			    	}
			    	
			    	for(LibClassHolder lch : libClassMap.values()) {
			    		if(lch.getMethods() == null)
			    			continue;
			    		for(LibMethodHolder lmh : lch.getMethods()) {
			    			emitToAssocFile(lmh);
			    		}
			    	}
			    }
			    
			    //Code related to Association rule miner data collection
				if(CommonConstants.bEnableAssocMiner) {
					bwAssocMiner.close();				
					bwAssocMethodIDs.close();
				}			
		    
				//Mine traces using statistical methods, which is now obsolete
				//as we plan to use the sequence miner...
				//SequenceMiner smObj = new SequenceMiner();
				//long beginMineTraces = System.currentTimeMillis();
				//smObj.mineStaticTraces();
				//long endMineTraces = System.currentTimeMillis();
				//logger.warn("Time taken for mining extracted candidate specifications: " + (endMineTraces - beginMineTraces) + " msec");
			} else {			
				//Load patterns back into memory from output file
				BufferedReader miner_op_file = new BufferedReader(new FileReader("AssocMinerData_MinedConsolidated.csv"));
				miner_op_file.readLine();	//Ignoring the header
				
				String inpLine = "";
				while((inpLine = miner_op_file.readLine()) != null) {
					inpLine = inpLine.replace("#UNKNOWN#", "UNKNOWN");	
					
					StringTokenizer stObj = new StringTokenizer(inpLine, ",");
					int Id = Integer.parseInt(stObj.nextToken().trim());		//reading the ID
					String patternContext = stObj.nextToken().trim();					
					String anchorMethod = stObj.nextToken();
					
					String errorMethods = stObj.nextToken(); 
				
					String idRep = stObj.nextToken();
					int frequency = 0; 
					try {	
						frequency = Integer.parseInt(stObj.nextToken().trim());
					} catch(java.lang.NumberFormatException nef) {
						int i = 0;
					}
					double confidence = Double.parseDouble(stObj.nextToken().trim());
					double relativeSupport = Double.parseDouble(stObj.nextToken().trim());
					
					//Load the information into patterns.
					MethodInvocationHolder anchorMIH = getMethodInvocationHolder(anchorMethod);
					
					List<MethodInvocationHolder> pcMIHList = null;
					if(!patternContext.equals("")) {
						pcMIHList = new ArrayList<MethodInvocationHolder>();
						StringTokenizer stPC = new StringTokenizer(patternContext, "#");
						while(stPC.hasMoreTokens()) {
							pcMIHList.add(getMethodInvocationHolder(stPC.nextToken().trim()));				
						}
					}
					
					List<MethodInvocationHolder> errorMIHList = null;
					if(!errorMethods.equals("")) {
						errorMIHList = new ArrayList<MethodInvocationHolder>();
						StringTokenizer stErr = new StringTokenizer(errorMethods, "#");
						while(stErr.hasMoreTokens()) {
							errorMIHList.add(getMethodInvocationHolder(stErr.nextToken().trim()));				
						}
					}
					
					//create a mined pattern object and get the equivalent 
					//method invocation holder and load the pattern back
					ExternalObject eeObj = externalObjects.get(anchorMIH.getReceiverClass().type);
					Holder assocLibMIH = null;
			    	if(eeObj != null) {
			    		for(MethodInvocationHolder eeMIH : eeObj.getMiList()) { 
			    			if(eeMIH.equals(anchorMIH)) {
			    				assocLibMIH = eeMIH;
			    				break;
			    			}
			    		}
			    	} else {
			    		//This is a method internally declared by the program
			    		LibClassHolder lch = libClassMap.get(anchorMIH.getReceiverClass().type);
			    		if(lch != null && lch.getMethods() != null) {
				    		for(LibMethodHolder lmh : lch.getMethods()) {
				    			if(lmh.equals(anchorMIH)) {
				    				assocLibMIH = lmh;
				    				break;
				    			}
				    		}
				    	}
			    	}
			    	
			    	if(assocLibMIH != null) {
			    		//create a mined patterm
			    		MIHList pcObj = new MIHList(pcMIHList);
			    		MIHList errorObj = new MIHList(errorMIHList);
			    		MinedPattern mpObj = new MinedPattern(assocLibMIH, errorObj, confidence, frequency, pcObj);
			    		assocLibMIH.addToMinedPatterns(mpObj);
			    	}			    	
				}	
				
				//Detect bugs
				long beginDetectBugs = System.currentTimeMillis();
				CommonConstants.OPERATION_MODE = CommonConstants.DETECT_BUGS_IN_INPUTPROJECT;
		        gdc.analyzeLibraryCode(parentDir, "", "", false);			
		        long endDetectBugs = System.currentTimeMillis();
		        logger.warn("Time taken for detecting bugs: " + (endDetectBugs - beginDetectBugs) + " msec");
		        
				//We use a simple technique. We parse all mined traces of the input application
				//and see whether they have all mined APIs on their error paths.
		        
				BufferedWriter bwDefects = new BufferedWriter(new FileWriter("DetectedBugs.csv"));
				int defectRank = 1;
				bwDefects.write("Rank, Frequency, Support, Filename, Methodname, PatternNM, PatternEM, AssociatedErrorPath\n");
				for(DefectHolder defectObj : defectSet) {
					bwDefects.write(defectRank++ + "," + defectObj.getMpObj().getChildMIDFrequency() + "," + defectObj.getMpObj().getSupport() + ",");
					bwDefects.write(defectObj.getCodeSampleName() + "," + defectObj.getMethodName() + ",");
									
					bwDefects.write("" + defectObj.getMpObj().getParentMID().getPrintString() + ",");
					List<MethodInvocationHolder> errorMIHList = defectObj.getMpObj().getChildMID().getMihList();
					int sizeOfErrMIHList = errorMIHList.size();
					int temp_err_cnt = 0;
					for (MethodInvocationHolder childMIH : errorMIHList) {
						temp_err_cnt++;
						if(temp_err_cnt == sizeOfErrMIHList)
							bwDefects.write("" + childMIH.getPrintString());
						else 
							bwDefects.write("" + childMIH.getPrintString() + "#");
					}	
					
					bwDefects.write(",");
					bwDefects.write("" + defectObj.getAssociatedErrorPath() + "\n");
				}
				bwDefects.close();
				
				
			}
	        visitedCodeSamples.clear();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Transforms the given string into a method invocation holder
	 * 1. pre-processes the method invocation so that "parseFromString" method of MethodInvocationHolder can be invoked	 
	 * @return
	 */
	private MethodInvocationHolder getMethodInvocationHolder(String methodStr) {
		try {
			methodStr = methodStr.replace("::", ",");
			
			int indexOfFColon = methodStr.indexOf(":");
			String receiverCls = methodStr.substring(0, indexOfFColon);
			
			int indexOfLColon = methodStr.lastIndexOf(":");
			//String returnType = methodStr.substring(indexOfLColon, methodStr.length());
			String methodSignature = methodStr.substring(indexOfFColon + 1, indexOfLColon);
			
			MethodInvocationHolder mihObj = MethodInvocationHolder.parseFromString(methodSignature, new TypeHolder(receiverCls));
			return mihObj;
		} catch(StringIndexOutOfBoundsException ex) {
			ex.printStackTrace();
			throw ex;
		}
	}
	
	public LibMethodHolder getEqviMethodDeclaration(String className, MethodInvocationHolder miObj)
	{
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return null;
		
		for (LibMethodHolder lmh : lcc.getMethods()) {
    		if(lmh.getName().equals(miObj.getMethodName()))
    		{
    			//NOARGUMENTCOMPARISON: For time being ignoring arguments for comparisons
    			//boolean bTrue = true;
    			//if(bTrue)
    			//	return lmh;
    			
    			String[] leftArgArr = lmh.getArgTypes();
    			TypeHolder[] rightArgArr = miObj.getArgumentArr();
    			
    			if(leftArgArr.length != rightArgArr.length)
    				continue;
    			
    			boolean bSame = true;
    			for(int count = 0;count < leftArgArr.length; count++)
    			{
    				bSame = isBothArgumentsSame(leftArgArr[count], rightArgArr[count].getType());
    				if(!bSame)
    					break;
    			}
    			
    			//Found the required method LibMethodHolder
    			if(bSame)
    			{
    				return lmh;
    			}   				
    		}
		}
		
		//If the method declaration is not found in the current class, go to parent class.
		//If no parents exist then return null
		String parentType = lcc.getParentType();
		if(parentType != null)
		{
			LibMethodHolder equiLMH = getEqviMethodDeclaration(parentType, miObj); 
			if(equiLMH != null)
				return equiLMH;
		}
		
		//Repeat for each interface types
		HashSet<String> allInterfaceTypes = lcc.getAllInterfaceTypes();
		for(String interfaceType : allInterfaceTypes)
		{
			LibMethodHolder equiLMH = getEqviMethodDeclaration(interfaceType, miObj);
			if(equiLMH != null)
				return equiLMH;
		}
		
		return null;
	}
	
	//This method is to get the equivalent method declaration when another LibMethodHolder
	//is given. This does not look in to parents. Only searches in the given class.
	public LibMethodHolder getEqviMethodDeclaration(String className, LibMethodHolder otherLmh)
	{
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return null;
		
		for (LibMethodHolder lmh : lcc.getMethods()) {
    		if(lmh.getName().equals(otherLmh.getName()))
    		{
    			//NOARGUMENTCOMPARISON: For time being ignoring arguments for comparisons
    			//boolean bTrue = true;
    			//if(bTrue)
    			//	return lmh;
    			
    			
    			String[] leftArgArr = lmh.getArgTypes();
    			String[] rightArgArr = otherLmh.getArgTypes();
    			
    			if(leftArgArr.length != rightArgArr.length)
    				continue;
    			
    			boolean bSame = true;
    			for(int count = 0;count < leftArgArr.length; count++)
    			{
    				bSame = isBothArgumentsSame(leftArgArr[count], rightArgArr[count]);
    				if(!bSame)
    					break;
    			}
    			
    			//Found the required method LibMethodHolder
    			if(bSame)
    			{
    				return lmh;
    			}   				
    		}
		}
		
		return null;
	}
	
	
	public boolean isBothArgumentsSame(String leftArg, String rightArg)
	{
		//Bi-passing the parameterized types
		if(leftArg.equals(CommonConstants.PARAMETERIZED_TYPE))
			return true;
		
		if(rightArg.startsWith(CommonConstants.unknownType) || rightArg.equals(CommonConstants.multipleCurrTypes) || rightArg.equals("this"))
			return true;
		
		if(leftArg.startsWith(CommonConstants.unknownType) || leftArg.equals(CommonConstants.multipleCurrTypes) || leftArg.equals("this"))
			return true;
		
		if(leftArg.equals(rightArg))
			return true;
		    				
		//If they are of parameterized types, ignore the parameters in the brackets
		int indexOfLessThen;
		if((indexOfLessThen = rightArg.indexOf("<")) != -1)
		{
			rightArg = rightArg.substring(0, indexOfLessThen);
			
			if(leftArg.equals(rightArg))
				return true;
		}
		
		
		//Some times full class names might not be available due to partial code
		//Even the last classname matches, for time-being we treat it as a match
		int indexOfDot = leftArg.lastIndexOf(".");
		String leftEndCls = leftArg;
		if(indexOfDot != -1)
		{	
			leftEndCls = leftArg.substring(indexOfDot + 1, leftArg.length());
		}
		
		indexOfDot = rightArg.lastIndexOf(".");
		String rightEndCls = rightArg;
		if(indexOfDot != -1)
		{	
			rightEndCls = rightArg.substring(indexOfDot + 1, rightArg.length());
		}                             
		if(leftEndCls.equals(rightEndCls))
			return true;
		
		
		//Check whether there is a match in child types
		LibClassHolder leftLch = RepositoryAnalyzer.getInstance().getLibClassMap().get(leftArg);
		if(leftLch != null && leftLch.getChildTypes().contains(rightArg))
		{
			return true;
		}				
		
		
		//Whether left and right classes associated with inheritance hierarchy
		boolean bRelatedWithClsHierarchy = false;
		try
		{
			Class leftClsObj = Class.forName(leftArg);
			Class rightClsObj = Class.forName(rightArg);
		
			try
			{
				rightClsObj.cast(leftClsObj.newInstance());
				bRelatedWithClsHierarchy = true;	//If no exception occurs, casting is successful
			}	
			catch(Exception ex) {				
			}
			
			try
			{
				leftClsObj.cast(rightClsObj.newInstance());
				bRelatedWithClsHierarchy = true;	//If no exception occurs, casting is successful
			}	
			catch(Exception ex)	{
				
			}
		}
		catch(Exception ex) {
			
		}
		catch(Error ex) {
			
		}
							
		if(bRelatedWithClsHierarchy)
			return true;
		
		return false;
	}
	
	/**
	 * Static function for getting all child types. Like this gives all 
	 * child classes in case of a class, In case of interface, it gives all
	 * implementing classes and its children tooo...
	 */
	public HashSet<String> getAllChildTypes(String type)
	{
		HashSet<String> childTypes = new HashSet<String>();
		
		LibClassHolder lch = (LibClassHolder) libClassMap.get(type);
		for(String implLCH : lch.getChildTypes())
		{
			childTypes.add(implLCH);
			HashSet<String> childChildTypes = getAllChildTypes(implLCH);
			
			for(String childChild : childChildTypes)
				childTypes.add(childChild);
		}
		
		return childTypes;
	}

	public HashMap<String, ExternalObject> getExternalObjects() {
		return externalObjects;
	}

	public HashMap<String, String> getCodeSampleIDToPackageMapper() {
		return codeSampleIDToPackageMapper;
	}

	public HashSet<String> getVisitedCodeSamples() {
		return visitedCodeSamples;
	}

	public void setVisitedCodeSamples(HashSet<String> visitedCodeSamples) {
		this.visitedCodeSamples = visitedCodeSamples;
	}

	/*public HashMap<Integer, MethodInvocationHolder> getIdToSamplesMethodHolder() {
		return IdToSamplesMethodHolder;
	}

	public List<StaticTraceStore> getStaticTraceList() {
		return staticTraceSet;
	}

	public HashMap<String, List<MethodInvocationHolder>> getSamplesMethodHolderToID() {
		return SamplesMethodHolderToID;
	}*/
	
	public HashMap<String, String> getLibClassToPackageMapper() {
		return libClassToPackageMapper;
	}
	
	public static void resetMIIdGen()
	{
		miCounterGen = 0;
	}
	
	public static int getUniqueIDForMI()
	{
		return MethodInvocationHolder.MIH_LIBKEYGEN++; 
	}


	public IJavaProject getLibProject() {
		return libProject;
	}

	public void setLibProject(IJavaProject libProject) {
		this.libProject = libProject;
	}

	public HashMap<Integer, LibMethodHolder> getIdToLibMethod() {
		return IdToLibMethod;
	}

	public void setIdToLibMethod(HashMap<Integer, LibMethodHolder> idToLibMethod) {
		IdToLibMethod = idToLibMethod;
	}
	
	public HashMap<String, LibClassHolder> getLibClassMap()
	{
		return libClassMap;
	}

	public HashSet<String> getLibPackageList()
	{
		return libPackageList;		
	}

	public Set<DefectHolder> getDefectSet() {
		return defectSet;
	}
	
	public void addToDefectSet(DefectHolder dchObj) {
		for(DefectHolder existingDch : defectSet) {
			if(existingDch.equals(dchObj)) {
				return;
			}		
		}		
		defectSet.add(dchObj);	
	}

	/** Functions specific to association rule miner data collection **/
	public Map<String, List<MethodInvocationHolder>> getMihIDMap() {
		return MihIDMap;
	}
	
	public void emitToAssocFile(Holder eeMIH) {
		try {			
			List<NormalErrorPath> nepList = eeMIH.getNormal_error_APIList();
			if(nepList == null)
				return;
			
			bwAssocMethodIDs.write(eeMIH.getKey() + " : " + eeMIH.getPrintString() + "\n");
			    			
			bwAssocMiner.write("\n**********************\n");
			bwAssocMiner.write("MethodInvocation : " + eeMIH.getPrintString() + " (" + eeMIH.getKey() + ") " + "\n\n");
			
			int tcnt = 0;
			Set<Integer> sub_methodList = new HashSet<Integer>();
			bwAssocSubMethodIDs = new BufferedWriter(new FileWriter("AssocMiner_IDs/" + eeMIH.getKey() + ".txt"));
			bwAssocMinerNum = new BufferedWriter(new FileWriter("AssocMiner_Data/" + eeMIH.getKey() + ".txt"));
			
			Iterator<CodeExampleStore> cesIter = eeMIH.getCodeExamples().iterator();
			
			for(NormalErrorPath nepObj : nepList) {
				CodeExampleStore cesObj = cesIter.next();
				
				bwAssocMiner.write("Count " + (tcnt++) + "  " + cesObj.javaFileName + "," + cesObj.methodName + "\n");
				bwAssocMiner.write("Normal:\n");
				for(MethodInvocationHolder normalpathMIH : nepObj.getNormalPath()) {
					ASTCrawlerUtil.assignKeyToMethodInvocations(normalpathMIH);							
					bwAssocMiner.write("\t" + normalpathMIH.getPrintString() + " (" + normalpathMIH.getKey() + ") " + "\n");
					bwAssocMinerNum.write(eeMIH.getKey() + "." + normalpathMIH.getKey() + ".0 ");
					
					
					if(!sub_methodList.contains(new Integer(normalpathMIH.getKey()))) {
						sub_methodList.add(new Integer(normalpathMIH.getKey()));
						bwAssocSubMethodIDs.write(normalpathMIH.getKey() + " : " + normalpathMIH.getPrintString() + "\n");
					}
				}		
				
				bwAssocMiner.write("\nError:\n");
				
				for(MethodInvocationHolder errorMIH : nepObj.getErrorPath()) {
					if(ASTCrawlerUtil.ignoreErrorMIH(eeMIH, errorMIH)) {
						continue;
					}
					ASTCrawlerUtil.assignKeyToMethodInvocations(errorMIH);	
					bwAssocMiner.write("\t" + errorMIH.getPrintString() + " (" + errorMIH.getKey() + ") " + "\n");
					bwAssocMinerNum.write(eeMIH.getKey() + "." + errorMIH.getKey() + ".1 ");
					
					if(!sub_methodList.contains(new Integer(errorMIH.getKey()))) {
						sub_methodList.add(new Integer(errorMIH.getKey()));
						bwAssocSubMethodIDs.write(errorMIH.getKey() + " : " + errorMIH.getPrintString() + "\n");
					}						
				}
				bwAssocMinerNum.write("\n");
			}
			bwAssocMiner.write("\n**********************\n");			    			
			eeMIH.setNormal_error_APIList(null);  //Freeing the memory to handle large code bases
			bwAssocSubMethodIDs.close();
			bwAssocSubMethodIDs = null;
			bwAssocMinerNum.close();
			bwAssocMinerNum = null;    					
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	
	//Function for running Apriori association rule miner
	/*public void runApriori() {
		HashMap<Integer, MethodInvocationHolder> idToMIHMapper = new HashMap<Integer, MethodInvocationHolder>();
		try
		{
			//Printing method ids
			BufferedWriter bwAssocMids = new BufferedWriter(new FileWriter("AssocMethodIds.txt"));
			for(List<MethodInvocationHolder> mihList : MihIDMap.values()) {
				for(MethodInvocationHolder assocMIH : mihList) {
					bwAssocMids.write(assocMIH.getKey() + " : " + assocMIH.getPrintString() + "\n");
					idToMIHMapper.put(assocMIH.getKey(), assocMIH);
				}
			}
			
			bwAssocMids.close();

			if(System.getenv("SERVER_MODE") != null) {
	    		return;
	    	}
			
			Runtime javaRuntime = Runtime.getRuntime();
			String command = "apriori -c60 -tr -o AssocMinerData.txt AssocMinerRules.rul ";
		
			@SuppressWarnings("unused")
			Process aprioriProcess = javaRuntime.exec(command);
			Thread.sleep(5 * 1000); //TODO: To replace with WAIT later
			//bideProcess.waitFor();  //NOTE: This method hangs if the process initiated process any console output and it is not consumed
		}
		catch(Exception ex)
		{
			logger.error("Error in executing Apriori!!!" + ex);
		}
		
		try {
			//Scanning the output file
			Scanner scRuleFile = new Scanner(new File("AssocMinerRules.rul"));
			BufferedWriter bwAssocOut = new BufferedWriter(new FileWriter("AssocMinerFormatted.csv"));
			bwAssocOut.write("TraceID, Support, Category, NM, EM, Context\n");
			int traceCounter = 1;
			while(scRuleFile.hasNextLine()) {
				String inpStr = scRuleFile.nextLine();
				
				int leftId = -1, rightId = -1;
				int indexOfLeft = inpStr.indexOf("left=");
				if(indexOfLeft != -1) {
					indexOfLeft += 5;
					int indexOfEndVal = inpStr.indexOf(" ", indexOfLeft);
					leftId = Integer.parseInt(inpStr.substring(indexOfLeft, indexOfEndVal));				
				}
				
				int indexOfRight = inpStr.indexOf("right=");
				if(indexOfRight != -1) {
					indexOfRight += 6;
					int indexOfEndVal = inpStr.indexOf(" ", indexOfRight);
					rightId = Integer.parseInt(inpStr.substring(indexOfRight, indexOfEndVal));				
				}
				
				if(leftId == -1 || rightId == -1)
					continue;
				
				bwAssocOut.write((traceCounter++) + ",,," + idToMIHMapper.get(leftId).getPrintString() + "," + idToMIHMapper.get(rightId).getPrintString() + ",\n");
			}
			
			bwAssocOut.close();
			scRuleFile.close();
		} catch(Exception ex) {
			logger.error("Error in reading the generated association rules" + ex);
		}
	}*/
	
	

	public HashSet<String> getImpStatementList() {
		return impStatementList;
	}
	
	
	
	
}
