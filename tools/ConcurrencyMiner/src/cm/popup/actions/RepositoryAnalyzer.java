package cm.popup.actions;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;

import cm.common.CommonConstants;
import cm.inpapp.holder.ExternalObject;
import cm.inpapp.holder.LibClassHolder;
import cm.inpapp.holder.LibMethodHolder;
import cm.samples.analyzer.CodeExampleStore;
import cm.samples.analyzer.GCodeAnalyzer;
import cm.samples.analyzer.TypeHolder;
import cm.samples.analyzer.holder.CodeSampleDefectHolder;
import cm.samples.analyzer.holder.Holder;
import cm.samples.analyzer.holder.MethodInvocationHolder;
import cm.samples.analyzer.holder.PrePostPathHolder;

/**
 * Class that analyzes samples of each directory and dumps traces into text files associated
 * with each method invocation. This is a singleton class and is used as a storage for
 * all temporary information during the execution.
 * @author suresh_thummalapenta
 */

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

	IJavaProject libProject = null;
	
	private static int miCounterGen = 0;
	
	private static RepositoryAnalyzer instance;
	String currentLibClass;
	
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
			
			//Code related to Association rule miner data collection
			bwAssocMiner = new BufferedWriter(new FileWriter("AssocMinerData.txt"));
			bwAssocMethodIDs = new BufferedWriter(new FileWriter("AssocMethodIds.txt"));
					
			//Creating required dummy directories
			(new File("AssocMiner_Data")).mkdirs();
			(new File("AssocMiner_IDs")).mkdirs();
						
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
	        	
		    	ExternalObject eeObj = externalObjects.get(currentLibClass);
		    	if(eeObj != null) {
		    		for(MethodInvocationHolder eeMIH : eeObj.getMiList()) { 
	    				emitToAssocFile(eeMIH);	    			
		    		}	
		    	} else {
		    		logger.error("Problem!!! Check this out");
		    	}
		    	
		    	MethodInvocationHolder.MINER_ID_GEN = 0;   	       	
			}
								
			gdc.clearAstc();			
	        
			bwAssocMiner.close();
			bwAssocMethodIDs.close();						
	        visitedCodeSamples.clear();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	/**
	 * Function for dumping patterns to related output files
	 * @param eeMIH
	 */
	@SuppressWarnings("static-access")
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
			
			//Enter the dummy entries to make sure that the mining algorithms takes them into consideration.
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
