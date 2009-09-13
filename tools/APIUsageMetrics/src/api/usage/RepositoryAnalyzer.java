package api.usage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import pw.code.analyzer.GCodeAnalyzer;
import pw.code.analyzer.MethodInvocationHolder;
import pw.common.CommonConstants;

public class RepositoryAnalyzer {
	
	static private Logger logger = Logger.getLogger("RepositoryAnalyzer");
	
	/**
	 * Static constants for representing the dummy methods
	 */
	public static final String DUMMY_METHOD_NAME = "#ExtendsOrImplements#";
	public static final String EXCEPTION_HANDLER = "#ExceptionHandler#";
	
	HashMap<String, LibClassHolder> libClassMap = new HashMap<String, LibClassHolder>();
	HashSet<String> libPackageList = new HashSet<String>();
	HashMap<String, String> libClassToPackageMapper = new HashMap<String, String>();
	HashMap<Integer, LibMethodHolder> IdToLibMethod = new HashMap<Integer, LibMethodHolder>();
	HashMap<String, String> codeSampleIDToPackageMapper = new HashMap<String, String>(); 
			//This is related to code downloader, where a code sample is mapped to the open source application 
	
	HashSet<CodeExampleStore> codeExampleSet = new HashSet<CodeExampleStore>();
	
	
	//Associated IJavaProject
	IJavaProject libProject = null;
	
	private static int miCounterGen = 0;
	
	private static RepositoryAnalyzer instance;
	String currentLibClass;
	String currentFolderName;
	
	public static final double upperThreshold = 0.1;	//A threshold for detecting hotspots
	public static final int HOTSPOT_PERCENT = 15;		//TODO: To compute the empirical value of this.
	public static final int DEADSPOT_PERCENT = 60;
	
	
	int zeroUsages = 0;
	int numTotalUsages = 0;

	/*
	 * Method sequences that give the usage of APIs in the library
	 */
	List<String> methodSequences = new ArrayList<String>();
	List<String> methodIDSequences = new ArrayList<String>();
	
	private RepositoryAnalyzer()
	{		
	}
	
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
	
	public HashMap<String, LibClassHolder> getLibClassMap()
	{
		return libClassMap;
	}

	public HashSet<String> getLibPackageList()
	{
		return libPackageList;		
	}
	
	@SuppressWarnings("unchecked")
	public void analyzeRepository(String parentDir)
	{
		zeroUsages = numTotalUsages = 0;	
		
		try
		{
			//Invoke GCodeAnalyzer for the each parent directory
			GCodeAnalyzer gdc = new GCodeAnalyzer();
	        File inputFile = new File(parentDir);
	        String[] candidates = inputFile.list();
	        for (String file : candidates) {
	        	File fcand = new File(parentDir + CommonConstants.FILE_SEP + file);
	        	if (!fcand.isDirectory()) {
	        		continue;
	        	}
	        	
	        	logger.debug("Analyzing directory " + file);        	
	        	currentLibClass = file.replace("_", ".");
	        	currentFolderName = file;
	        	gdc.analyze(parentDir + CommonConstants.FILE_SEP + file, "", "", false);
	        	clearVisitedInfo();
			}
		
	        //Identifying the types of other methods, which are not identified by earlier process
	        //Also add a special method that stores the number of times a class is extended.
	        //IMPORTANT NOTE: We just treat the class extension as an individual method as
	        //a class can be extended many times but none of its methods are overridden.
	        for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	        
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
        		
	        		if(lmh.getNumInvocations() > 0) {
	        			lmh.setMethodType(LibMethodHolder.TEMPLATE);
	        		}
	        		if(lmh.getNumOverloads() > 0 && lmh.getMethodType() != LibMethodHolder.HOOK) {
	        			lmh.setMethodType(LibMethodHolder.HOOK);
	        			lmh.setMethodType(LibMethodHolder.TEMPLATE);
	        		}
	        		
	        		if(lmh.getName().equals(DUMMY_METHOD_NAME)) {
	        			if(!lcc.isInterface())
	        				lmh.setNumOverloads(lcc.getNumExtensions());
	        			else 
	        				lmh.setNumImplements(lcc.getNumImplements());
	        		}	        		
	        	}	        	
	        }
	        	        
	        //Iterate the classes in HashMap and print the results
	        BufferedWriter bw = new BufferedWriter(new FileWriter("APIUsageMetrics.txt"));
	        for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	        	
	        	bw.write("Class: " + lcc.getName() + "\n");
	        	bw.write("\tInstances Created: " + lcc.getNumInstances() + "\n");
	        	bw.write("\tExtensions : " + lcc.getNumExtensions() + "\n");
	        	bw.write("\tImplements : " + lcc.getNumImplements() + "\n");
	        	
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	        		bw.write("\t" + lmh.toString() + "\n\t\t -> Type: " + lmh.getMethodType() +" Invocations: " + lmh.getNumInvocations() + ", Overrides: " + lmh.getNumOverloads() + 
	        				", Implements: " + lmh.getNumImplements() + "\n");
	        		bw.write("\t\t\t LocalInvocations: " + lmh.getLocalNumInvocations() + ", LocalOverrides: " + lmh.getLocalNumOverloads() + 
	        				", LocalImplements: " + lmh.getLocalNumImplements() + "\n");
	        		bw.write("\t\t Invoked Methods" + lmh.getInvokedMethods() + "\n\n");
	        	}
	        }
	        
	        
	        //Sort all APIs of the library with respect to their numbers
	        TreeSet<LibMethodHolder> sortedSet = new TreeSet<LibMethodHolder>(new LibMethodHolder());
	        
	        int numMaxUsages = 0;
	        for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	        	
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	        		sortedSet.add(lmh);
	        		numTotalUsages += lmh.getTotalUsages();
	        		if(lmh.getTotalUsages() > numMaxUsages)
	        			numMaxUsages = lmh.getTotalUsages();
	        	}
	        }
	        
	        //Additional information about the number of classes and APIs.
	        bw.write("\n\nTotal number of classes " + libClassMap.size());
	        bw.write("\nTotal number of methods " + sortedSet.size() + "\n\n");
	                
	        int totalNumMethods = sortedSet.size();
	        int hotspotLimit = (HOTSPOT_PERCENT * totalNumMethods) / 100;
	                
   
	        //Compute usage percentages and categorize methods
	        bw.write("\n\n********* APIs in sorted order ************\n");
	        int tcounter = 0;
	        for(Iterator iter = sortedSet.iterator(); iter.hasNext();)
	        {
	        	LibMethodHolder lmh = (LibMethodHolder) iter.next();
	        	boolean isItADeadSpot = false;	        	
	        	double percentageUsage = (((double)lmh.getTotalUsages()) /  numTotalUsages) * 100;
	        	lmh.setUsagePercentage(percentageUsage);
	        		
	        	if(percentageUsage != 0) {	        		
	        		if(tcounter <= hotspotLimit)
	        			lmh.setCategory(LibMethodHolder.HOTSPOT);
	        		else
	        			lmh.setCategory(LibMethodHolder.WEAKSPOT);	        		
	        	}
	        	else if(percentageUsage == 0) 
	        	{
	        		if(lmh.getCategory() != LibMethodHolder.DEADSPOT)
	        		{	
	        			HashSet<LibMethodHolder> visitedLMH = new HashSet<LibMethodHolder>();
	        			if(isADeadspot(lmh, visitedLMH)) {
	        				lmh.setCategory(LibMethodHolder.DEADSPOT);
	        				isItADeadSpot = true;
	        			}	
	        		}	
	        	}
	        	
	        	bw.write("ID: " + lmh.getID() + " Class: " + lmh.getContainingClass().getName() + " Method: " + lmh.toString() + " Usages: " + 
						lmh.getTotalUsages() + " Percentage: " + percentageUsage +"% Additional info:" +
						lmh.getChildClassInfoMap());
	        	if(isItADeadSpot)
	        	{
	        		bw.write(" : DEADSPOT ");
	        	}
	        	
	        	bw.write("\n");  
	        	tcounter++;
	        }
	        
	        handleHotspots(sortedSet, bw);
	        handleDeadspots(bw);
	        bw.close();
	        
	        //Printing code example info into a separate file
	        bw = new BufferedWriter(new FileWriter("CodeExamples.txt"));
	        for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	        	
	        	bw.write("Class: " + lcc.getName() + "\n");
	        	if(lcc.getTemplateExampleStore().size() != 0) {
		        	bw.write("\tInstantiation Code Examples (Templates): \n");
		        	for(CodeExampleStore templCES : lcc.getTemplateExampleStore()) {
		        		bw.write("\t\t" + templCES.filename + "\t" + templCES.methodname + "\n");
		        	}
	        	}	
	        	
	        	if(lcc.getHookExampleStore().size() != 0) {
		        	bw.write("\t" + (lcc.isInterface() ? "Implementation" : "Extension") + " Code Examples (Hooks): \n");
		        	for(CodeExampleStore hookCES : lcc.getHookExampleStore()) {
		        		bw.write("\t\t" + hookCES.filename + "\t" + hookCES.methodname + "\n");
		        	}    
	        	}
	        	
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	        		bw.write("\tMethod ID: " + lmh.getID() + " " + lmh.toString() + "\n");
	        		
	        		if(lmh.getTemplExampleStore().size() != 0) {
	        			bw.write("\t\tInvocation Code Examples (Templates): \n");
	        			for(CodeExampleStore templCES : lmh.getTemplExampleStore()) {
	        				bw.write("\t\t\t" + templCES.filename + "\t" + templCES.methodname + "\n");
	        			}
	        		}

	        		if(lmh.getHookExampleStore().size() != 0) {
	        			bw.write("\t\t" + (lcc.isInterface() ? "Implementation" : "Extension") + " Code Examples (Hooks): \n");
	        			for(CodeExampleStore hookCES : lmh.getHookExampleStore()) {
	        				bw.write("\t\t\t" + hookCES.filename + "\t" + hookCES.methodname + "\n");
	        			}
	        		}	
		        }
	        }
	        bw.close();
	        
	        
	        
	        /*** Gathering statistics of constructors (COMMENTED TEMPORARILY)****/
	        /*int numTotalClassWithMultiConstructors = 0;
	        int numClassesWithMoreDefConsUsages = 0;
	        bw.write("\n\n Statistics of constructors ... \n\n");
	        for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
       	
	        	int numConstructors = 0; 
	        	boolean bAllAreZeroUsages = true;
	        	LibMethodHolder defConstructor = null;
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	        		if(lmh.getName().equals("CONSTRUCTOR"))	{
	        			numConstructors++;
	        			
	        			if(lmh.getArgTypes().length == 0) {
	        				defConstructor = lmh;
	        			}
	        			
	        			if(lmh.getTotalUsages() > 0) {
	        				bAllAreZeroUsages = false;
	        			}
	        				
	        		}	
	        	}
	        	
	        	if(numConstructors <= 1 || defConstructor == null || bAllAreZeroUsages)
	        		continue;
	        	
	        	//If there are more than one constructor and default constructor
	        	//is one of them, check whether the number of usages of default
	        	//constructor are more
	        	boolean bDefaultIsGreater = true;
	        	numTotalClassWithMultiConstructors++;
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
		        	if(lmh.getName().equals("CONSTRUCTOR") && lmh != defConstructor) {
		        		if(lmh.getTotalUsages() > defConstructor.getTotalUsages())
		        		{
		        			bw.write("Other Constructors Leading: " + lcc.getName() + "\n");
		        			bDefaultIsGreater = false;
		        			break;
		        		}
		        	}
	        	}

	        	if(!bDefaultIsGreater)
	        		continue;
	        	
	        	numClassesWithMoreDefConsUsages++;
	        	bw.write("Default Constructor Leading: " + lcc.getName() + "\n");
	        }
	        
	        bw.write("Total classes with multiple constructors " + numTotalClassWithMultiConstructors + "\n");
	        bw.write("Classes with more usages of DefConstructor " + numClassesWithMoreDefConsUsages + "\n"); */  
	        		        
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/**
	 * Methods of HOTSPOTS belong from here
	 * @param sortedSet
	 * @param bw
	 * @throws IOException
	 */
	public void handleHotspots(TreeSet<LibMethodHolder> sortedSet, BufferedWriter bw) throws IOException
	{
        HashSet<String> hotspotSet = new HashSet<String>();
        ArrayList<LibClassHolder> hotspotList = new ArrayList<LibClassHolder>();
        int endClsRankingCounter = 1, endMethodRankingCounter = 1;
        for(Iterator iter = sortedSet.iterator(); iter.hasNext();)
        {
        	LibMethodHolder lmh = (LibMethodHolder) iter.next();
        	if(lmh.getCategory() != LibMethodHolder.HOTSPOT) {
        		continue;
        	}
        	
        	LibClassHolder containingCls = lmh.getContainingClass();
        	if(!hotspotSet.contains(containingCls.getName())) {
        		containingCls.setEndRanking(endClsRankingCounter++);
        		hotspotList.add(containingCls);        		
        		hotspotSet.add(containingCls.getName());
        	}      	
        	
        	lmh.setEndRanking(endMethodRankingCounter++);        
        }	
        
        //Classifying the classes as templates and hooks 
        //Prnting list of identified hotspots as a clustered classes with methods
        List<LibClassHolder> templateLCHList = new ArrayList<LibClassHolder>();
        List<LibClassHolder> hookLCHList = new ArrayList<LibClassHolder>();
        for(Iterator iter = hotspotList.iterator(); iter.hasNext();)
        {
        	LibClassHolder lch = (LibClassHolder) iter.next();
           	for(LibMethodHolder lmh : lch.getMethods()) {
        		if(lmh.getEndRanking() == 0) { 
        			continue;
        		}        		
        		
        		/**
        		 * A class having the dummy method involved and is in the set of
        		 * hotspot is treated as a HOOK.
        		 */
        		if(lmh.getName().equals(DUMMY_METHOD_NAME) && (lmh.getTotalUsages() > lch.getNumInstances())) {
        			lch.setEndCategory(CommonConstants.HOOK);
        			hookLCHList.add(lch);
        			break;
        		}        		
        	}
        	
        	//If the class doesnot contain the DUMMY_METHOD in the hotspot list,
        	//And still the class in hotspots category, it is treated as TEMPLATE
        	if(lch.getEndCategory() == CommonConstants.UNKNOWN_CATEGORY) {
        		lch.setEndCategory(CommonConstants.TEMPLATE);
        		templateLCHList.add(lch);
        	}        	
        }       
              	        
        //Clustering them into hierarchy groups before defining dependencies.
        HashSet<HotspotHierarchyStore> templateHierarchySet = getHierarchyList(templateLCHList, CommonConstants.TEMPLATE);
        HashSet<HotspotHierarchyStore> hookHierarchySet = getHierarchyList(hookLCHList, CommonConstants.HOOK);
        
        TreeSet<HotspotHierarchyStore> sortedTemplateHierarchySet = new TreeSet<HotspotHierarchyStore>(new HotspotHierarchyStore()); 
        sortedTemplateHierarchySet.addAll(templateHierarchySet);
        
        TreeSet<HotspotHierarchyStore> sortedHookHierarchySet = new TreeSet<HotspotHierarchyStore>(new HotspotHierarchyStore()); 
        sortedHookHierarchySet.addAll(hookHierarchySet);
        
        
        //After classifying groups, scroll through TEMPLATE classes and
        //try associating the HOOK classes to them through associations.
        
        //Debugging section
        bw.write("/*** Template Hierarchy Lists ***/\n");
        for(Iterator iter = sortedTemplateHierarchySet.iterator(); iter.hasNext();)
        {
        	HotspotHierarchyStore hhsObj = (HotspotHierarchyStore) iter.next();
        	bw.write("Hierarchy :  " + hhsObj.getEndRanking() + "\n");
        	
        	for(Iterator iterInner = hhsObj.getLchList().iterator(); iterInner.hasNext();)
        	{
        		LibClassHolder lch = (LibClassHolder)iterInner.next();
        		bw.write("\tClass: " + lch + " Rank: " + lch.getEndRanking() + "\n");
        		
        		bw.write("\tMethods: \n");
	        	lch.sortMethods();
	        	for(LibMethodHolder lmh : lch.getMethods()) {
	        		if(lmh.getEndRanking() == 0) { 
	        			continue;
	        		}

	        		bw.write("\t\t" + lmh.getEndRanking() + " " + lmh.toString() + "\n");      	
	        	}        		
        	}	        	
        }      
        
        bw.write("/*** Hook Hierarchy Lists ***/\n");
        for(Iterator iter = sortedHookHierarchySet.iterator(); iter.hasNext();)
        {
        	HotspotHierarchyStore hhsObj = (HotspotHierarchyStore) iter.next();
        	bw.write("Hierarchy :  " + hhsObj.getEndRanking() + "\n");
        	
        	for(Iterator iterInner = hhsObj.getLchList().iterator(); iterInner.hasNext();)
        	{
        		LibClassHolder lch = (LibClassHolder)iterInner.next();
        		bw.write("\tClass: " + lch + " Rank: " + lch.getEndRanking() + "\n");
        		
        		bw.write("\tMethods: \n");
        		lch.sortMethods();
	        	for(LibMethodHolder lmh : lch.getMethods()) {
	        		if(lmh.getEndRanking() == 0) { 
	        			continue;
	        		}

	        		bw.write("\t\t" + lmh.getEndRanking() + " " + lmh.toString() + "\n");      	
	        	}
        		
        	}
        }
        //End of debugging section
        
        //Associating hooks & templates to other template hierarchies (2 phases)
        
        //Phase 1: Associating hook hierarchies to templates
        //Traverse all methods of template hierarchies and associate the other hook hierarchies to the template
        //by identifying the arguments. 
        
        //Phase 2: Defining template dependencies (By verifying constructors)
        //
        HashSet<HierarchyDependency> dependencySet = new HashSet<HierarchyDependency>(); 
        for(Iterator iter = sortedTemplateHierarchySet.iterator(); iter.hasNext();)
        {
        	HotspotHierarchyStore hhsObj = (HotspotHierarchyStore) iter.next();
        	for(Iterator iterInner = hhsObj.getLchList().iterator(); iterInner.hasNext();)
        	{
        		LibClassHolder lch = (LibClassHolder)iterInner.next();
	        	for(LibMethodHolder lmh : lch.getMethods()) 
	        	{
	        		if(lmh.getEndRanking() == 0) { 
	        			continue;
	        		}
	        		
	        		boolean bIsAConstructor = false;
	        		if(lmh.getName().equals("CONSTRUCTOR")) {
	        			bIsAConstructor = true;
	        		}
	        		
	        		for(String argument : lmh.getArgTypes()) 
	        		{
	        			LibClassHolder argumentLCH = libClassMap.get(argument);
	        			if(argumentLCH == null)
	        				continue;
	        			
	        			HotspotHierarchyStore libLchHssObj = argumentLCH.getEndHierarchyStore();
	        			if(libLchHssObj != null && libLchHssObj.getEndRanking() != hhsObj.getEndRanking()) 
	        			{
	        				HierarchyDependency hdObj = null;
	        				if(libLchHssObj.getHierarchyCategory() == CommonConstants.HOOK) {
	        					hdObj = new HierarchyDependency(hhsObj, libLchHssObj);	        				
	        				} else if(bIsAConstructor) {
	        					hdObj = new HierarchyDependency(libLchHssObj, hhsObj);	        					
	        				}        				
	        				dependencySet.add(hdObj);
	        			} 
	        			
        				//Check with the child classes as they can be existing inside
        				//the hook hierarchies
        				if(argumentLCH.getAllChildTypes() == null) {
        					argumentLCH.setAllChildTypes(getAllChildTypes(argumentLCH.getName()));
        				}
        				
        				for(String childType : argumentLCH.getAllChildTypes()) 
        				{
        					LibClassHolder childLCH = libClassMap.get(childType);
        					if(childLCH == null)
        						continue;
        					
        					libLchHssObj = childLCH.getEndHierarchyStore();
        					
        					if(libLchHssObj != null && libLchHssObj.getEndRanking() != hhsObj.getEndRanking()) 
        					{
        						HierarchyDependency hdObj = null;
		        				if(libLchHssObj.getHierarchyCategory() == CommonConstants.HOOK) {
		        					hdObj = new HierarchyDependency(hhsObj, libLchHssObj);		        					
		        				} else if(bIsAConstructor) {
		        					hdObj = new HierarchyDependency(libLchHssObj, hhsObj);		        					
		        				}
		        				dependencySet.add(hdObj);
        					}	        					
        				}        			
	        		}        	
	        	}
        	}	
        }
        
        //Printing the dependency of hierarchies
        bw.write("\n\n/**** Hierarchy dependencies ****/\n\n");
        for(HierarchyDependency hdObj : dependencySet)
        {
        	try
        	{
        		bw.write(hdObj.toString() + "\n");
        	} 
        	catch (Exception ex) 
        	{
        		
        	}
        }

	}
	
	
	public HashSet<HotspotHierarchyStore> getHierarchyList(List<LibClassHolder> lchList, int hierarchyType) {		
		HashSet<HotspotHierarchyStore> templateHierarchySet = new HashSet<HotspotHierarchyStore>();
		List<LibClassHolder> visitedList = new ArrayList<LibClassHolder>();
		for(Iterator iter = lchList.iterator(); iter.hasNext();) {
        	LibClassHolder lch = (LibClassHolder) iter.next();
        	
        	if(lch.isInterface())
        		continue;
        
        	//Check whether this lch is related to some of the already existing hierarchies
        	boolean bLchAdded = false;
        	for(Iterator iterInner = templateHierarchySet.iterator(); iterInner.hasNext();) {
        		HotspotHierarchyStore hhsObj = (HotspotHierarchyStore) iterInner.next();
        		LibClassHolder existingLch = hhsObj.baseClass;
        		if(isRelationExists(lch, existingLch)) {
        			hhsObj.addToLchList(lch);
        			bLchAdded = true;
        			lch.setEndHierarchyStore(hhsObj);
        			break;
        		}	        		
        	}
        	
        	if(!bLchAdded) {
        		HotspotHierarchyStore hhsObj = new HotspotHierarchyStore(); 
        		hhsObj.baseClass = lch;
        		hhsObj.addToLchList(lch);
        		hhsObj.setHierarchyCategory(hierarchyType);
        		templateHierarchySet.add(hhsObj);
        		lch.setEndHierarchyStore(hhsObj);        		
        	}
        	
        	visitedList.add(lch);
        	
        }
		
		lchList.removeAll(visitedList);
		if(lchList.size() == 0)
			return templateHierarchySet;
		
		//Handling interfaces
		for(Iterator iter = lchList.iterator(); iter.hasNext();) {
			LibClassHolder lch = (LibClassHolder) iter.next();
			
			//Check whether this lch is related to some of the already existing hierarchies
        	boolean bLchAdded = false;
        	for(Iterator iterInner = templateHierarchySet.iterator(); iterInner.hasNext();) {
        		HotspotHierarchyStore hhsObj = (HotspotHierarchyStore) iterInner.next();
        		
        		for(Iterator iterDeeper = hhsObj.getLchList().iterator(); iterDeeper.hasNext();) {
        			LibClassHolder lchObj = (LibClassHolder) iterDeeper.next();
        			boolean bIsBelongs = false;
        			if(lchObj.isInterface()) {
        				bIsBelongs = isRelationExists(lchObj, lch);
        			} else {
        				bIsBelongs = isAnyClassImplements(lchObj, lch);
        			}
        			
        			
        			if(bIsBelongs) {
        				hhsObj.addToLchList(lch);
        				lch.setEndHierarchyStore(hhsObj);
        				bLchAdded = true;
        				break;
        			}    			
        			
        		}
        		
        		if(bLchAdded)
        			break;
        	}
        	
        	if(!bLchAdded) {
        		HotspotHierarchyStore hhsObj = new HotspotHierarchyStore(); 
        		hhsObj.baseClass = lch;
        		hhsObj.addToLchList(lch);
        		hhsObj.setHierarchyCategory(hierarchyType);
        		templateHierarchySet.add(hhsObj);
        		lch.setEndHierarchyStore(hhsObj);
        	}        	
		}		
		
		return templateHierarchySet;
	}
	
	
	public boolean isRelationExists(LibClassHolder lch1, LibClassHolder lch2) {
		
		boolean retVal1 = isAParentOf(lch1, lch2);
		if(retVal1)
			return retVal1;
		
		boolean retVal2 = isAParentOf(lch2, lch1);
		if(retVal2)
			return retVal2;
				
		return false;
	}
	
	/**
	 * This function basically answers the question "Is lch1 is a parent of lch2?"
	 * IMPORTANT: This has to be invoked only when both of them are interfaces or classes....
	 * @param lch1
	 * @param lch2
	 * @return
	 */
	public boolean isAParentOf(LibClassHolder lch1, LibClassHolder lch2) 
	{
		String parentObj = lch2.getParentType();
		if(parentObj != null) {
			
			if(parentObj.equals(lch1.getName())) {
				return true;
			}
			
			LibClassHolder newParent = libClassMap.get(parentObj);
			boolean retVal = isAParentOf(lch1, newParent);
			if(retVal)
				return retVal;
		}
		
		return false;
	}
	
	public boolean isAnyClassImplements(LibClassHolder classObj, LibClassHolder interfaceObj)
	{
		HashSet<String> interfaceSet = classObj.getAllInterfaceTypes();
		
		if(interfaceSet.contains(interfaceObj.getName())) {
			return true;
		}
		
		return false;
	}
	
	/**
	 * End of methods of HOTSPOTS
	 */
	
	/**
	 * Methods of DEADSPOTS
	 * @throws JavaModelException 
	 */
	public void handleDeadspots(BufferedWriter bw) throws IOException, JavaModelException
	{
		HashSet<String> deadSpotSet = new HashSet<String>();
		//Classify the classes according to their package names
		HashMap<String, List> packageSets = new HashMap<String, List>();
		
		for(Iterator iter = libClassMap.values().iterator(); iter.hasNext();)
        {
        	LibClassHolder lcc = (LibClassHolder) iter.next();
        	
        	//Gathering set of classes in each package
        	int lastIndex = lcc.getName().lastIndexOf(".");
        	if(lastIndex == -1) {
        		continue;
        	}
        	String packageName = lcc.getName().substring(0, lastIndex);
        	//For inner classes there can be multiple class names in the qualified names
        	while(libClassMap.get(packageName) != null)
        	{
        		packageName = packageName.substring(0, packageName.lastIndexOf("."));
        	}
        	
        	List classList = packageSets.get(packageName);
        	if(classList == null) {
        		classList = new ArrayList<LibClassHolder>();
        		packageSets.put(packageName, classList);
        	}       	
        	classList.add(lcc);
        	//End of gathering classes in each package
        	        	
        	//If all methods are deadspots excluding the dummy method, then mark the class as deadspot
        	int totalNumMethods = 0, numOfDeadMethods = 0;
        	boolean bHotspotExistsInThisClass = false;
        	for (LibMethodHolder lmh : lcc.getMethods()) {        		
        		if(lmh.getName().equals(RepositoryAnalyzer.DUMMY_METHOD_NAME)) {
        			continue;
        		}
        		
        		if(lmh.getCategory() == LibMethodHolder.DEADSPOT) {
        			numOfDeadMethods++;
        		} else if(lmh.getCategory() == LibMethodHolder.HOTSPOT) {
        			bHotspotExistsInThisClass = true;
        		}
        		
        		totalNumMethods++;
        	}
        	
        	//Heuristic: If dead methods comprise 80% and there are
        	//no hotspot classes, then the package can be considered as deadspot package
        	double percentageOfDeadspots = ((double) numOfDeadMethods / (double) totalNumMethods) * 100;
        	if(percentageOfDeadspots >= DEADSPOT_PERCENT && !bHotspotExistsInThisClass) {
        		deadSpotSet.add(lcc.getName());
        	}        	
        }
		
		bw.write("/*** DEADSPOT classes ***/\n");
		bw.write(deadSpotSet.toString());
		bw.write("\nNumber of Deadspot classes " +  deadSpotSet.size() + "\n\n");
		
		bw.write("\n/*** DEADSPOT packages ***/\n");		
        for(Iterator iter = packageSets.keySet().iterator(); iter.hasNext();)
        {
        	String packageName = (String) iter.next();
        	List classList = packageSets.get(packageName);
        	
        	int totalNumClasses = 0, numOfDeadClasses = 0;
        	boolean bHotspotExistsInThisPackage = false;
        	for(Iterator iterInner = classList.iterator(); iterInner.hasNext();)
        	{
        		totalNumClasses++;
        		LibClassHolder lchDead = (LibClassHolder) iterInner.next();
        		if(deadSpotSet.contains(lchDead.getName())) {
        			numOfDeadClasses++;
        		}        		
        		
        		HotspotHierarchyStore hhsObj = lchDead.getEndHierarchyStore();
        		if(hhsObj != null) {
        			bHotspotExistsInThisPackage = true;
        		}
        		
        	}
        	
        	//Heuristic: If dead classes comprise 80% and there are
        	//no hotspot classes, then the package can be considered as deadspot package
        	double percentageOfDeadspots = ((double) numOfDeadClasses / (double) totalNumClasses) * 100;
        	if(percentageOfDeadspots >= DEADSPOT_PERCENT && !bHotspotExistsInThisPackage) {
        		bw.write(packageName + "\n");
        	}
        }
	}
	
	/**
	 * Method that increments the instances value of dummy method "Exception_Handler" that is used
	 * to identify popular exceptions of the given framework
	 * @param className
	 * @param miObj
	 */
	public void handleExceptionInstance(String className, String sampleFileName, String methodName)
	{	
		if(!currentLibClass.equals(className))
			return;
		
		sampleFileName = currentFolderName + sampleFileName;

		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		for (LibMethodHolder lmh : lcc.getMethods()) {
    		if(lmh.getName().equals(RepositoryAnalyzer.EXCEPTION_HANDLER))
    		{
    			lmh.incrNumInvocations(className, sampleFileName, false, methodName);
    			return;
    		}
		}	
	}
	
	/**
	 * Method that increments the instances value of dummy methods created for member variables
	 * @param className
	 * @param miObj
	 */
	public void handleMemberVarInstance(String className, String memberVarName, String sampleFileName, String methodName)
	{	
		if(!currentLibClass.equals(className))
			return;
		
		sampleFileName = currentFolderName + sampleFileName;

		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		for (LibMethodHolder lmh : lcc.getMethods()) {
    		if(lmh.getName().equals(memberVarName))
    		{
    			lmh.incrNumInvocations(className, sampleFileName, false, methodName);
    			return;
    		}
		}	
	}

	
	/**
	 * Method that handles increment of methods called directly from client code
	 * @param className
	 * @param miObj
	 */
	public void handleMethodInvocation(String className, MethodInvocationHolder miObj, String sampleFileName, String methodName)
	{	
		if(!currentLibClass.equals(className))
			return;
		
		sampleFileName = currentFolderName + sampleFileName;

		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		LibMethodHolder equiMH = getEqviMethodDeclaration(className, miObj);
		if(equiMH == null)
			return;
		
		if((lcc.isInterface() || equiMH.isAbstract() || lcc.getChildTypes() != null) 
				&& CommonConstants.bExtendInterfacesToClasses && !equiMH.getName().equals("CONSTRUCTOR"))
		{
			//Incase of interface, instead of considering for interface, increment this
			//method invocation for all implementing classes. This
			//is done for identification of deadspots basically
			equiMH.incrNumInvocations(className, sampleFileName, false, methodName);
					
			if(lcc.getAllChildTypes() == null)
			{
				lcc.setAllChildTypes(getAllChildTypes(lcc.getName()));
			}
			
			for(String implementingClass: lcc.getChildTypes())
			{
				LibMethodHolder childLmh = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(implementingClass, equiMH);
				if(childLmh != null)
				{	
					childLmh.incrNumInvocations(className, sampleFileName, true, methodName);
				}	
			}
			return;
		}	
		equiMH.incrNumInvocations(className, sampleFileName, false, methodName);
	}
	
	/**
	 * Method that handles increment of methods called directly from client code
	 * in case the reference type is this
	 * @param className
	 * @param miObj
	 */
	public void handleMethodInvocation(HashSet<String> classNameSet, MethodInvocationHolder miObj, String origClsName, String codeSampleName, String methodName)
	{
		//if(!currentLibClass.equals(className))
		//	return;
		for(String className : classNameSet) {
			handleMethodInvocation(className, miObj, codeSampleName, methodName);
		}	
		//logger.warn("Failed to get equivalent method declaration " + miObj + " for class: " + origClsName);
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
    			String[] rightArgArr = miObj.getArgumentArr();
    			
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
		
		if(rightArg.equals(CommonConstants.unknownType) || rightArg.equals(CommonConstants.multipleCurrTypes) || rightArg.equals("this"))
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
	public void handleClassInstanceCreation(String className, String currentFileName, String methodName)
	{
		if(!currentLibClass.equals(className))
			return;
		
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;

		String sampleFileName = currentFolderName + currentFileName;
		lcc.incrNumInstances(sampleFileName, methodName);
	}
	
	/*
	 * A temporary function specific only for JUnit framework to exclude DNSJava 
	 * from detecting hotspots
	 */
	public boolean ignoreDNSJavaSample(String currentFileName) {
		String sampleFileName = currentFolderName + currentFileName;
		String pkgname = getCodeSampleIDToPackageMapper().get(sampleFileName);
		if(pkgname == null)
			return false;
		
		if(pkgname.indexOf("james") != -1) {
			System.out.println("Ignored DNSJava package elements");
			return true;
		}
		return false;
	}
	
	/**
	 * Method that handles the number of times this class is extended
	 * @param className
	 */
	public void handleClassExtension(String className, TypeDeclaration typeNode, boolean bInterface, String fileName, String methodName, String codeSampleName)
	{
		if(!currentLibClass.equals(className))
			return;
		
		codeSampleName = currentFolderName + codeSampleName;
		
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		HashSet <LibMethodHolder> addedLmhSet = new HashSet<LibMethodHolder>();
		
		if(bInterface)
		{
			lcc.incrNumImplements(codeSampleName, methodName);
		}
		else
		{	
			lcc.incrNumExtensions(codeSampleName, methodName);
		}
		
		
		//Increment the related dummy method in the corresponding class or interface (Not required due to changed implementation)
		/*for(LibMethodHolder memLMH : lcc.getMethods())
		{
			if(memLMH.getName().equals(methodName)) {
				if(bInterface) {
					memLMH.incrNumImplements(className, codeSampleName, false);	
				} else {
					memLMH.incrNumOverloads(className, codeSampleName, false);
				}
				
				addedLmhSet.add(memLMH);
				break;
			}
		}*/
		
    	//Identify and increment counters for all overloaded methoddeclarations
    	MethodDeclaration mdArr[] = typeNode.getMethods();
    	for(int i = 0; i < mdArr.length; i++)
    	{
    		//Ignoring the abstract methods, as they have to be overloaded anyway.
    		if((mdArr[i].getModifiers() & Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toFlagValue()) != 0)	
    		{
    			continue;
    		}
    		
    		if(mdArr[i].isConstructor())
    			continue;
    			
    		MethodInvocationHolder mihObj = null;
    		
    		try{
      			mihObj = new MethodInvocationHolder(typeNode.getName().toString(), mdArr[i].getReturnType2().toString(), mdArr[i]);
    		} catch(Exception ex) {
    			//ex.printStackTrace(); /*Happening because of some junk data coming from GCSE*/
    			return;
    		}
    
    		LibMethodHolder lmh = getEqviMethodDeclaration(className, mihObj);
    		
    		if(lmh != null) {
    			if(bInterface) {
    				lmh.incrNumImplements(className, fileName, false, methodName);
    				addedLmhSet.add(lmh);
    			} else {
    				lmh.incrNumOverloads(className, fileName, false, methodName);
    	    		addedLmhSet.add(lmh);
    			}
    		}
    	}
    	
    	//Dropping the whole concept of method sequences
    	//addToMethodSequences(addedLmhSet, fileName, methodName);
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

	/**
	 * Method for adding sequences to the list
	 */
	public void addToMethodSequences(MethodInvocationHolder mihObj, String filename, String methodName)
	{
		LibMethodHolder lmhObj = getEqviMethodDeclaration(mihObj.getReferenceClsName(), mihObj);
		if(lmhObj != null)
		{
			methodIDSequences.add(lmhObj.getID() + " ");
			CodeExampleStore cesObj = new CodeExampleStore();
			cesObj.methodIds.add(new Integer(lmhObj.getID()));
			cesObj.filename = filename;
			cesObj.methodname = methodName;
			if(!codeExampleSet.contains(cesObj))
				codeExampleSet.add(cesObj);
		}
	}
	
	public void addToMethodSequences(List<MethodInvocationHolder> mihObjList, String filename, String methodName)
	{
		HashSet <LibMethodHolder> addedLmhSet = new HashSet<LibMethodHolder>();
		for(Iterator iter = mihObjList.iterator(); iter.hasNext();)
		{	
			MethodInvocationHolder mihObj = (MethodInvocationHolder) iter.next();
			LibMethodHolder lmhObj = getEqviMethodDeclaration(mihObj.getReferenceClsName(), mihObj);
			
			if(lmhObj != null && !addedLmhSet.contains(lmhObj))
			{
				addedLmhSet.add(lmhObj);
			}
		}	
		addToMethodSequences(addedLmhSet, filename, methodName);	
	}
	
	public void addToMethodSequences(HashSet<LibMethodHolder> lmhSet, String filename, String methodName)
	{
		CodeExampleStore cesObj = new CodeExampleStore();
		
		StringBuffer sequenceID = new StringBuffer("");
		for(LibMethodHolder lmhObj : lmhSet)
		{
			sequenceID.append(lmhObj.getID() + " ");
			cesObj.methodIds.add(new Integer(lmhObj.getID()));
		}
		
		String sequenceIDStr = sequenceID.toString();
		if(!sequenceIDStr.equals(""))
		{	
			methodIDSequences.add(sequenceIDStr);
		}	
		cesObj.filename = filename;
		cesObj.methodname = methodName;
		if(!codeExampleSet.contains(cesObj))
			codeExampleSet.add(cesObj);	
	}

	/**
	 * Main function for detecting deadspots
	 */
	public boolean isADeadspot(LibMethodHolder lmh, HashSet<LibMethodHolder> visitedLMH)
	{
		if(visitedLMH.contains(lmh))
		{
			lmh.setCategory(LibMethodHolder.DEADSPOT);
			return true;
		}
		
		visitedLMH.add(lmh);
		
		//If this is already identified as hotspot
		if(lmh.getCategory() == LibMethodHolder.HOTSPOT)
			return false;
		
		//If this is already identified as deadspot
		if(lmh.getCategory() == LibMethodHolder.DEADSPOT)
			return true;
			
		if(lmh.getTotalUsages() != 0)
		{
			return false;
		}
		
		//If the container is an interface or this lmh is abstract, check whether all its implementing
		//classes are deadspots. 
		LibClassHolder containingLCH = lmh.getContainingClass();
		if(containingLCH.isInterface() || lmh.isAbstract)
		{
			boolean bAllImplementingAreDeadspots = true;
			if(containingLCH.getAllChildTypes() == null) {
				containingLCH.setAllChildTypes(getAllChildTypes(containingLCH.getName()));
			}
			
			for(String implLCH : containingLCH.getAllChildTypes())
			{
				LibMethodHolder relatedLMH = getEqviMethodDeclaration(implLCH, lmh);
				if(relatedLMH == null || relatedLMH == lmh)
					continue;
				
				boolean bRetVal = isADeadspot(relatedLMH, visitedLMH);
				if(!bRetVal)
				{
					bAllImplementingAreDeadspots = false;
					break;
				}									
			}
			
			if(bAllImplementingAreDeadspots)
			{
				lmh.setCategory(LibMethodHolder.DEADSPOT);
				return true;
			}
			else
				return false;			
		}
		
		//This is not an abstract. Now check all the invoking method to see
		//whether they are deadspots.
		boolean bAllCallingAreDeadspots = true;
		for(LibMethodHolder callingLMH : lmh.getCallingMethods())
		{
			//Eliminating the direct recursion aspect
			if(callingLMH == lmh)
				continue;
			
			boolean bRetVal = isADeadspot(callingLMH, visitedLMH);
			if(!bRetVal) {
				bAllCallingAreDeadspots = false;
				break;
			}
		}
		
		if(!bAllCallingAreDeadspots) {
			return false;
		}	
		
		lmh.setCategory(LibMethodHolder.DEADSPOT);		
		return true;
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
	
	/**
	 * Clearing all visted info when switching between folders
	 */
	public void clearVisitedInfo() {
		for(LibClassHolder lch : libClassMap.values()) {
			lch.clearVisitedInfo();			
		}
	}
	
	
	/*** GETTERS AND SETTERS ***/

	public HashSet<CodeExampleStore> getCodeExampleSet() {
		return codeExampleSet;
	}

	public HashMap<String, String> getCodeSampleIDToPackageMapper() {
		return codeSampleIDToPackageMapper;
	}

	public void setCodeSampleIDToPackageMapper(
			HashMap<String, String> codeSampleIDToPackageMapper) {
		this.codeSampleIDToPackageMapper = codeSampleIDToPackageMapper;
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
	
	
	
	
}
