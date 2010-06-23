package minebugs.core;

import imminer.core.PatternType;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import minebugs.srclibhandlers.ExternalObject;
import minebugs.srclibhandlers.LibClassHolder;
import minebugs.srclibhandlers.LibMethodHolder;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import pw.code.analyzer.CodeExampleStore;
import pw.code.analyzer.GCodeAnalyzer;
import pw.code.analyzer.TypeHolder;
import pw.code.analyzer.holder.CodeSampleDefectHolder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.code.analyzer.holder.PrePostPathHolder;
import pw.common.CommonConstants;
import pw.code.analyzer.holder.Holder;

public class RepositoryAnalyzer {
	
	static private Logger logger = Logger.getLogger("RepositoryAnalyzer");
	
	HashMap<String, LibClassHolder> libClassMap = new HashMap<String, LibClassHolder>();
	HashSet<String> libPackageList = new HashSet<String>();
	HashMap<String, String> libClassToPackageMapper = new HashMap<String, String>();
	HashMap<Integer, LibMethodHolder> IdToLibMethod = new HashMap<Integer, LibMethodHolder>();
	
	HashMap<String, ExternalObject> externalObjects = new HashMap<String, ExternalObject>();
	HashSet<CodeSampleDefectHolder> codeSampleDefects = new HashSet<CodeSampleDefectHolder>();
	
	HashMap<String, String> codeSampleIDToPackageMapper = new HashMap<String, String>();
	HashSet<String> visitedCodeSamples = new HashSet<String>();
	
	IJavaProject currentJProject = null;
	static public int numDownloadedCodeSamples = 0;
	static public int numAnalyzedCodeSamples = 0;
	
	BufferedWriter bwAssocMiner = null;    			//Entire information. Mainly useful for logging and debugging
	BufferedWriter bwAssocMethodIDs = null;			//Writes method Ids for external methods
	
	BufferedWriter bwAssocSubMinerData = null;			//Writes all Data for mining into different files specific for each method "AssocMiner_Data"
	BufferedWriter bwAssocSubMethodIDs = null;		//Writes all IDs specific to that Method invocation in a separate folder "AssocMiner_IDs"

	//Associated IJavaProject
	IJavaProject libProject = null;
	
	private static int miCounterGen = 0;
	
	private static RepositoryAnalyzer instance;
	String currentLibClass;
	
	public static double lowerThreshold = 0.1;
	public static double upperThreshold = 1;
	
	int zeroUsages = 0;
	int numTotalUsages = 0;
	
	/*
	 * Category : For a range of 0.05% 
	 */
	double categoryRange = 0.05;
	int numEntries = (int)((double)100/categoryRange);
	int categories[];
	
	/*
	 * Method sequences that give the usage of APIs in the library
	 */
	List<String> methodSequences = new ArrayList<String>();
	List<String> methodIDSequences = new ArrayList<String>();
	
	private RepositoryAnalyzer()
	{		
	}
	
	public static RepositoryAnalyzer getInstance() {
		if(instance == null)
		{
			instance = new RepositoryAnalyzer();
		}
		return instance;
	}
	
	public static void clearInstance() {
		instance = null;
	}
	
	public HashMap<String, LibClassHolder> getLibClassMap()	{
		return libClassMap;
	}

	public HashSet<String> getLibPackageList() {
		return libPackageList;		
	}
	
	@SuppressWarnings("unchecked")
	public void analyzeRepository(String parentDir) 
	{
		try {
			//Invoke GCodeAnalyzer for the each parent directory
			GCodeAnalyzer gdc = new GCodeAnalyzer();
			
			if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) {
				gdc.analyze(parentDir,"","",false);
			} else {
				//Code related to Association rule miner data collection
				if((CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES || 
						CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_LIBRARY)  
						&& CommonConstants.B_COLLECT_MINER_DATA) {
					bwAssocMiner = new BufferedWriter(new FileWriter("AssocMinerData.txt"));
					bwAssocMethodIDs = new BufferedWriter(new FileWriter("AssocMethodIds.txt"));					
					//Creating required dummy directories
					(new File("AssocMiner_Data")).mkdirs();
					(new File("AssocMiner_IDs")).mkdirs();
				}
				
				switch(CommonConstants.OPERATION_MODE)
				{
					case CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES:
						File inputFile = new File(parentDir);
				        String[] candidates = inputFile.list();
				        for (String file : candidates) {
				        	File fcand = new File(parentDir + CommonConstants.FILE_SEP + file);
				        	if (!fcand.isDirectory()) {
				        		logger.debug("ERROR: Only directories can exist in Parent directory");
				        		continue;
				        	}
				        	
				        	logger.warn("Analyzing directory " + file);
				        	
				        	currentLibClass = file.replace("_", ".");
				        	gdc.analyze(parentDir + CommonConstants.FILE_SEP + file, currentLibClass, "", false);
				        			        	
				        	//A new directory indicates a new object and whole new parsing. 
				        	//Therefore all caches related to these code samples must be cleared 
				        	visitedCodeSamples.clear();
				        	
					    	this.emitExternalObjectDetails();			    	
					    	MethodInvocationHolder.MINER_ID_GEN = 0;   	       	
						}
						break;
					case CommonConstants.MINE_PATTERNS_FROM_LIBRARY:
						gdc.analyze(parentDir,"","",false);	//Invokes the main function in GCodeAnalyer that analyzes entire library code
						
						for(String externalObject : externalObjects.keySet())
						{
							currentLibClass = externalObject;
							this.emitExternalObjectDetails();							
						}
						
						break;
					default:
						logger.error("Invalid option for operation mode!!!");			
				}		
			} 
					
			gdc.clearAstc();
	        
			if((CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES || 
					CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_LIBRARY) 
					&& CommonConstants.B_COLLECT_MINER_DATA) {
				bwAssocMiner.close();
				bwAssocMethodIDs.close();
			}		
			
	        //The second section depends on the mode of operation
	        switch(CommonConstants.OPERATION_MODE) {
	        case CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES:
	        case CommonConstants.MINE_PATTERNS_FROM_LIBRARY:	        	
	        	//minePatterns(); break;	//Deprecated: Mining is done externally using another algorithm
	        	break;
	        case CommonConstants.DETECT_BUGS_IN_CODESAMPLES:
	        case CommonConstants.DETECT_BUGS_IN_LIBRARY:	
	        	dumpDetectedBugs(); break;
	        default: break;	
	        }
	        
	        visitedCodeSamples.clear();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}

	private void emitExternalObjectDetails() {
		ExternalObject eeObj = externalObjects.get(currentLibClass);
		if(eeObj != null) {
			for(MethodInvocationHolder eeMIH : eeObj.getMiList()) { 
				if((CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES
						|| CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_LIBRARY)
						&& CommonConstants.B_COLLECT_MINER_DATA) {
					emitToAssocFile(eeMIH);
				}	
				eeMIH.clearStorageElements();
			}	
		} else {
			logger.error("Problem!!! Check this out");
		}
	}
	
	public void emitToAssocFile(MethodInvocationHolder eeMIH) 
	{
		int tcnt = 0;
		try {			
			List<PrePostPathHolder> prePostList = eeMIH.getPrePostList();
			if(prePostList == null)
				return;
			
			bwAssocMethodIDs.write(eeMIH.getKey() + " : " + eeMIH + "\n");
			    			
			bwAssocMiner.write("\n**********************\n");
			bwAssocMiner.write("MethodInvocation : " + eeMIH.getPrintString() + " (" + eeMIH.getKey() + ") " + "\n\n");			
			bwAssocSubMethodIDs = new BufferedWriter(new FileWriter("AssocMiner_IDs/" + eeMIH.getKey() + ".txt"));
			bwAssocSubMinerData = new BufferedWriter(new FileWriter("AssocMiner_Data/" + eeMIH.getKey() + ".txt"));
			
			Iterator<CodeExampleStore> cesIter = eeMIH.getCodeExamplesList().iterator();
			
			//Outputting data
			HashSet<Integer> addedCnts = new HashSet<Integer>();
			for(PrePostPathHolder prePostObj : prePostList) {
				CodeExampleStore cesObj = cesIter.next();
				addedCnts.clear();			
				
				bwAssocMiner.write("Count " + (tcnt++) + "  " + cesObj.javaFileName + "," + cesObj.methodName + "\n");
				bwAssocMiner.write("Before:\n");
				for(Holder beforePathHolder : prePostObj.getPrePath()) {
					Integer intKey = new Integer(beforePathHolder.getKey());
					if(!addedCnts.contains(intKey)) {
						addedCnts.add(intKey);
						bwAssocMiner.write("\t" + beforePathHolder + "(" + beforePathHolder.getKey() + ")"    + "\n");
						bwAssocSubMinerData.write(eeMIH.getKey() + "." + beforePathHolder.getKey() + ".0 ");					
					}					
				}		
				
				bwAssocMiner.write("\nAfter:\n");
				addedCnts.clear();
				for(Holder errorMIH : prePostObj.getPostPath()) {
					Integer intKey = new Integer(errorMIH.getKey());
					if(!addedCnts.contains(intKey)) {
						addedCnts.add(intKey);
						bwAssocMiner.write("\t" + errorMIH + " (" + errorMIH.getKey() + ") " + "\n");
						bwAssocSubMinerData.write(eeMIH.getKey() + "." + errorMIH.getKey() + ".1 ");					
					}	
				}				
				if(prePostObj.getPrePath().size() != 0 ||  prePostObj.getPostPath().size() != 0)
					bwAssocSubMinerData.write("\n");
			}
			
			//Outputting IDs
			for(Holder hObj : eeMIH.getExistingHolderElements()) {
				bwAssocSubMethodIDs.write(hObj.getKey() + " : " + hObj + "\n");
			}
			
			//Enter the dummy entries to make sure that the mining algorithms 
			//takes them into consideration.
			int dummyEntryId = ++(eeMIH.MINER_ID_GEN);
			bwAssocSubMethodIDs.write(dummyEntryId + " : " + "DUMMY_MINING_ENTRY\n");
			for(int tcnt10 = 0; tcnt10 < eeMIH.getNumEmptyCallSites(); tcnt10++) {
				bwAssocSubMinerData.write(eeMIH.getKey() + "." + dummyEntryId + ".0" + "\n");
			}	
			
			bwAssocMiner.write("\n**********************\n");			    			
			bwAssocSubMethodIDs.close();
			bwAssocSubMethodIDs = null;
			bwAssocSubMinerData.close();
			bwAssocSubMinerData = null;    					
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * DETECT BUGS IN CODE SAMPLES section for identifying bugs in the downloaded code samples
	 */
	@SuppressWarnings("unchecked")
	public void dumpDetectedBugs() 
	{
		//Currently only debugging info is written here
		HashSet<CodeSampleDefectHolder> detectedBugsSet = RepositoryAnalyzer.getInstance().getCodeSampleDefects();
		
		//Sort the results using a TreeSet and print them
		TreeSet<CodeSampleDefectHolder> sortedBugs = new TreeSet<CodeSampleDefectHolder>(new CodeSampleDefectHolder());
		for(CodeSampleDefectHolder csdhObj : detectedBugsSet) {
			sortedBugs.add(csdhObj);
		}
		
		try {
			if(!CommonConstants.inputPatternFile.equals(""))
			{
				BufferedWriter bwBugs = new BufferedWriter(new FileWriter("MinedBugs.csv"));
				bwBugs.write("Filename, Method, Violated API, Violation Pattern, Pattern Type, Support");
				bwBugs.write(", Balanced, SpecialFlg, SortingSupport, MID\n");
				for(CodeSampleDefectHolder csdhObj : sortedBugs) {
					bwBugs.write(csdhObj.toString() + "\n");									
				}
				bwBugs.close();
			}
			else
			{
				//dumping all related defects
				dumpDefectsOfType(PatternType.AND_PATTERN, sortedBugs);
				dumpDefectsOfType(PatternType.OR_PATTERN, sortedBugs);
				dumpDefectsOfType(PatternType.XOR_PATTERN, sortedBugs);
				dumpDefectsOfType(PatternType.COMBO_PATTERN, sortedBugs);	
				
				//Dump all defects into one file for a consolidated analysis
				//to form the base line for number of real defects among all
				//violations
				CommonConstants.DUPLICATE_BUG_MODE = CommonConstants.DUPLICATE_BUG_MINIMUM;				
				HashSet<CodeSampleDefectHolder> minimalSetOfBugs = new HashSet<CodeSampleDefectHolder>();
				for(CodeSampleDefectHolder csdhObj : detectedBugsSet) {
					minimalSetOfBugs.add(csdhObj);
				}
				BufferedWriter bwBugs = new BufferedWriter(new FileWriter("MinedBugs_Consolidated.csv"));
				bwBugs.write("Filename, Method, Violated API, Violation Pattern, Pattern Type, Support, HasAndPattern, UsedForDefectDetection\n");								
				for(CodeSampleDefectHolder csdhObj : minimalSetOfBugs) {
					bwBugs.write(csdhObj.toString() + "\n");									
				}
				bwBugs.close();
				CommonConstants.DUPLICATE_BUG_MODE = CommonConstants.DUPLICATE_BUG_NORMAL;
				//End of consolidated list
			}
		} catch (IOException e) {
			logger.error("Exception occurred" + e);
		}		
	}
	
	private void dumpDefectsOfType(PatternType ptype, Set<CodeSampleDefectHolder> sortedBugs)
	{
		try
		{
			BufferedWriter bwBugs = new BufferedWriter(new FileWriter("MinedBugs_" + ptype.toString() + ".csv"));
			bwBugs.write("Filename, Method, Violated API, Violation Pattern, Pattern Type, Support, HasAndPattern, UsedForDefectDetection\n");
							
			for(CodeSampleDefectHolder csdhObj : sortedBugs) {				
				if(csdhObj.getPtype() != ptype)
					continue;	
				bwBugs.write(csdhObj.toString() + "\n");									
			}
			bwBugs.close();
		}
		catch(Exception ex)
		{
			logger.error("Error occurred while writing patterns to file " + ex.getMessage());
		}
	}
	
	/**
	 * Method that handles increment of methods called directly from client code
	 * @param className
	 * @param miObj
	 */
	public void handleMethodInvocation(String className, MethodInvocationHolder miObj)
	{
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		LibMethodHolder equiMH = getEqviMethodDeclaration(className, miObj);
		if(equiMH == null)
			return;
		
		if((lcc.isInterface() || equiMH.isAbstract() || lcc.getChildTypes() != null) 
				&& CommonConstants.bExtendInterfacesToClasses && !equiMH.getName().equals("CONSTRUCTOR"))
		{
			//Incase of interface, instead of considering for interface, increment thi
			//method invocation for all implementing classes
			equiMH.incrNumInvocations(className);
					
			if(lcc.getAllChildTypes() == null)
			{
				lcc.setAllChildTypes(getAllChildTypes(lcc.getName()));
			}
			
			for(String implementingClass: lcc.getChildTypes())
			{
				LibMethodHolder childLmh = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(implementingClass, equiMH);
				if(childLmh != null) {	
					childLmh.incrNumInvocations(className);
				}	
			}
			return;
		}		
		
		
		equiMH.incrNumInvocations(className);
	}
	
	/**
	 * Method that handles increment of methods called directly from client code
	 * in case the reference type is this
	 * @param className
	 * @param miObj
	 */
	public void handleMethodInvocation(HashSet<String> classNameSet, MethodInvocationHolder miObj, String origClsName)
	{
		for(String className : classNameSet) {
			handleMethodInvocation(className, miObj);
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
	
	
	private boolean isBothArgumentsSame(String leftArg, String rightArg)
	{
		//Bi-passing the parameterized types
		if(leftArg.equals(CommonConstants.PARAMETERIZED_TYPE))
			return true;
		
		if(rightArg.startsWith(CommonConstants.unknownType) || rightArg.equals(CommonConstants.multipleCurrTypes) || rightArg.equals("this"))
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
	 * Method that increments the number of times this class is instantiated
	 * @param className
	 */
	public void handleClassInstanceCreation(String className)
	{
		//if(!currentLibClass.equals(className))
		//	return;
		
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;

		lcc.incrNumInstances();
	}
	
	public HashMap<String, String> getLibClassToPackageMapper() {
		return libClassToPackageMapper;
	}
	
	public static void resetMIIdGen()
	{
		miCounterGen = 0;
	}
	
	public static int getUniqueIDForMI()
	{
		return miCounterGen++; 
	}

	public List<String> getMethodSequences() {
		return methodSequences;
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

	public List<String> getMethodIDSequences() {
		return methodIDSequences;
	}

	public void setMethodIDSequences(List<String> methodIDSequences) {
		this.methodIDSequences = methodIDSequences;
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

	public HashSet<CodeSampleDefectHolder> getCodeSampleDefects() {
		return codeSampleDefects;
	}

	public HashMap<String, String> getCodeSampleIDToPackageMapper() {
		return codeSampleIDToPackageMapper;
	}

	public IJavaProject getCurrentJProject() {
		return currentJProject;
	}

	public void setCurrentJProject(IJavaProject currentJProject) {
		this.currentJProject = currentJProject;
	}

	public HashSet<String> getVisitedCodeSamples() {
		return visitedCodeSamples;
	}

	public void setVisitedCodeSamples(HashSet<String> visitedCodeSamples) {
		this.visitedCodeSamples = visitedCodeSamples;
	}
}
