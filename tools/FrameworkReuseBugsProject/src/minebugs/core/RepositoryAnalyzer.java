package minebugs.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import minebugs.srclibhandlers.ExternalObject;
import minebugs.srclibhandlers.LibClassHolder;
import minebugs.srclibhandlers.LibMethodHolder;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import anamolydetector.popup.actions.AnamolyDetector;

import framework.reuse.LibClassReuse;
import framework.reuse.LibMethodReuse;

import pw.code.analyzer.GCodeAnalyzer;
import pw.code.analyzer.TypeHolder;
import pw.code.analyzer.holder.CodeSampleDefectHolder;
import pw.code.analyzer.holder.CondVarHolder;
import pw.code.analyzer.holder.ConditionVarHolderSet;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.common.CommonConstants;

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
	
	//New variables for FrameworkReuseBugs project
	HashMap<String, LibClassReuse> reusedLibClassMap = new HashMap<String, LibClassReuse>();
	HashMap<String, Integer> reusedMethodInvocationIdMapper = new HashMap<String, Integer>();
	static public int reusedMICounter = 0;
	
	static public int reuseClassIdGen = 0;
	static public int reuseMethodIdGen = 0;
	
	public class CodeExampleStore implements Comparator {
		public HashSet<Integer> methodIds = new HashSet<Integer>();
		public String filename;
		public String methodname;
		
		public boolean equals(Object cesObj)
		{
			if(!(cesObj instanceof CodeExampleStore))
				return false;
			
			HashSet<Integer> otherMethodIds = ((CodeExampleStore)cesObj).methodIds;
			
			if(otherMethodIds.containsAll(methodIds))
				return true;
			
			return false;
		}
		
		public int compare(Object obj1, Object obj2)
		{
			if(!(obj1 instanceof CodeExampleStore) || !(obj2 instanceof CodeExampleStore))
				return -1;
			
			CodeExampleStore cesObj1 = (CodeExampleStore) obj1;
			CodeExampleStore cesObj2 = (CodeExampleStore) obj2;
			
			if(cesObj1.equals(cesObj2))
				return 0;
			
			return -1;
		}
		
		//Return same hash code for all
		public int hashCode()
		{
			return 10;
		}
	}
	
	HashSet<CodeExampleStore> codeExampleSet = new HashSet<CodeExampleStore>();
		
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
		try {
			//Invoke GCodeAnalyzer for the each parent directory
			GCodeAnalyzer gdc = new GCodeAnalyzer();
			
			if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) {
				gdc.analyze(parentDir,"","",false);
			} else {
		        File inputFile = new File(parentDir);
		        String[] candidates = inputFile.list();
		        
		        if(candidates == null) {
		        	return;
		        }
		        
		        for (String file : candidates) {
		        	File fcand = new File(parentDir + CommonConstants.FILE_SEP + file);
		        	if (!fcand.isDirectory()) {
		        		logger.debug("ERROR: Only directories can exist in Parent directory");
		        		continue;
		        	}
		        	
		        	logger.warn("Analyzing directory " + file);
		        	
		        	currentLibClass = file.replace("_", ".");
		        	gdc.analyze(parentDir + CommonConstants.FILE_SEP + file, currentLibClass, "", false);
		        	
		        	//Control comes here after finishing the analysis of one complete folder
		        	//Output all transactions collected to different files and also
		        	//dump the method Ids of this class   	
		        	dumpTransactionsAndMethodIds(currentLibClass);
		        			        	
		        	//A new directory indicates a new object and whole new parsing. 
		        	//Therefore all caches related to these code samples must be cleared 
		        	visitedCodeSamples.clear();
		        	reusedMICounter = 0;
		        	reusedMethodInvocationIdMapper.clear();		        	
				}
			} 
			
			gdc.clearAstc();
			
			//Dumping the conditions captured from all code examples
			BufferedWriter bwCondAssoc = new BufferedWriter(new FileWriter("AssocMinerData.txt"));
			BufferedWriter bwCondWriter = new BufferedWriter(new FileWriter("CapturedConditions.txt"));
			BufferedWriter bwClassIdWriter = new BufferedWriter(new FileWriter("AssocClassIds.txt"));
			BufferedWriter bwMethodIdWriter = new BufferedWriter(new FileWriter("AssocMethodIds.txt"));
			for(LibClassReuse lcrObj : reusedLibClassMap.values()) {
				bwCondWriter.write("ClassName: " + lcrObj.getAssociatedLibClass().getName() + "\n");
				bwClassIdWriter.write(lcrObj.getID() + " : " + lcrObj.getAssociatedLibClass().getName() + "\n");
				for(LibMethodReuse lmrObj : lcrObj.getReusedMethods()) {
					bwMethodIdWriter.write(lmrObj.getID() + " : " + lmrObj.getAssociatedLibMethodHolder().toString() + "\n");
					bwCondWriter.write("\tMethod: " + lmrObj.getAssociatedLibMethodHolder() + "\n");
										
					if(lmrObj.getArgumentCondVars() != null) {
						for(int argCounter = 0; argCounter < lmrObj.getAssociatedLibMethodHolder().getArgTypes().length;argCounter++) {
							bwCondWriter.write("\t\tArgument : "+ argCounter + "\n"); 
							for(CondVarHolder cvhObj : lmrObj.getArgumentCondVars()[argCounter].getTotalCondVar().values()) {
								bwCondWriter.write("\t\t\t " + cvhObj.toString() + ", Frequency: "+  cvhObj.getFrequency() +"\n");
								
								//Print number of lines equivalent to frequency
								for(int tcnt = 0; tcnt < cvhObj.getFrequency(); tcnt ++) {
									bwCondAssoc.write(lcrObj.getID() + "," + lmrObj.getID() + ",ARG" + argCounter + "," + cvhObj.getCondType() + "\n");
								}								
							}													
						}
					}
					
					bwCondWriter.write("\t\tReturn : \n");
					if(lmrObj.getReturnCondVar() != null) {
						for(CondVarHolder retCVHObj : lmrObj.getReturnCondVar().getTotalCondVar().values()) {
							bwCondWriter.write("\t\t\t " + retCVHObj.toString() + ", Frequency: "+  retCVHObj.getFrequency() +"\n");
							
							//Print number of lines equivalent to frequency
							for(int tcnt = 0; tcnt < retCVHObj.getFrequency(); tcnt ++) {
								bwCondAssoc.write(lcrObj.getID() + "," + lmrObj.getID() + ",RET" + "," + retCVHObj.getCondType() + "\n");
							}							
						}
					}	
				}			
			}
			bwCondWriter.close();
			bwCondAssoc.close();
			bwClassIdWriter.close();
			bwMethodIdWriter.close();		
	        
	        //The second section depends on the mode of operation
	        /*switch(CommonConstants.OPERATION_MODE) {
	        case CommonConstants.MINE_PATTERNS:
	        	minePatterns(); break;
	        case CommonConstants.DETECT_BUGS_IN_CODESAMPLES:
	        case CommonConstants.DETECT_BUGS_IN_LIBRARY:	
	        	detectBugsInCodeSamples(); break;
	        default: break;	
	        }*/
	        
	        visitedCodeSamples.clear();
		}
		catch(Exception ex) {
			ex.printStackTrace();
		}
	}
	
	public void dumpTransactionsAndMethodIds(String className) {
		//Create the directory for this class
		String parentFolder = AnamolyDetector.results_dir + "/" + className;  
		(new File(parentFolder)).mkdirs();
				
		LibClassReuse lcrObj = reusedLibClassMap.get(className);
		if(lcrObj == null) {
			return;
		}		
		
		BufferedWriter bwFileNameIDMapper = null;
		try {
			bwFileNameIDMapper = new BufferedWriter(new FileWriter(parentFolder + "/" + "MethodsEquivalentOfFileNames.txt"));
		} catch(Exception ex) {
			
		}
		
		for(LibMethodReuse lmrObj : lcrObj.getReusedMethods()) {
			try {	
				
				String fileNameKey = lmrObj.getAssociatedLibMethodHolder().getName() + "#" + lmrObj.getAssociatedLibMethodHolder().getArgumentStr();
				bwFileNameIDMapper.write("" + lmrObj.getID() + " : " + fileNameKey + "\n");
				BufferedWriter bwLmr = new BufferedWriter(new FileWriter(parentFolder + "/" + lmrObj.getID() + ".txt"));
				for(String trans : lmrObj.getTransactions()) {
					bwLmr.write(trans + "\n");					
				}				
				bwLmr.close();
			} catch(Exception ex) {				
			}
		}		
			
		//Also dump the transaction ids
		try {
			bwFileNameIDMapper.close();
			BufferedWriter bwLmr = new BufferedWriter(new FileWriter(parentFolder + "/" + "MethodIds.txt"));
			for(String methodStr : reusedMethodInvocationIdMapper.keySet()) {
				Integer intId = reusedMethodInvocationIdMapper.get(methodStr);
				bwLmr.write(intId + " : " + methodStr + "\n");
			}			
			bwLmr.close();
		} catch(Exception ex) {			
		}		
	}
	
	
	
	/**
	 * MINE PATTERNS section for identifying conditional patterns on APIs of interest
	 *
	 */
	public void minePatterns() throws IOException
	{
		logger.warn("TIMING: Start of report generation and analysis of patterns: " + System.currentTimeMillis());
        //Extract the conditions that have minimum support defined by the constant CONDITION_SUPPORT_THRESHOLD
        HashMap<String, ExternalObject> externalObjMap = RepositoryAnalyzer.getInstance().getExternalObjects();
        BufferedWriter bwMinedPatterns = new BufferedWriter(new FileWriter("MinedPatterns.dat"));
        for(ExternalObject extObj : externalObjMap.values()) {
        	bwMinedPatterns.write("Statistics for class " + extObj.getClassName() + "\n");
        	for(MethodInvocationHolder extMIH : extObj.getMiList()) {
        		bwMinedPatterns.write("\t\n\nMethod: " + extMIH.toString() + "\n");	
        		
        		bwMinedPatterns.write("\tReceiver Conditions: NoConditionCount ->" + extMIH.getNumNoneReceiverCondVar() + "\n");
        		computeFrequentConditionalRules(extMIH, extMIH.getReceiverCondVar(), extMIH.getNumNoneReceiverCondVar(), bwMinedPatterns, CommonConstants.RECEIVER_PATTERNS);
        		
        		for(int tcounter = 0; tcounter < extMIH.getNoOfArguments(); tcounter++){
        			bwMinedPatterns.write("\tArgument Conditions (Position : " + tcounter + "): NoConditionCount ->" + extMIH.getNumNoneArgumentCondVars(tcounter) + "\n");
        			computeFrequentConditionalRules(extMIH, extMIH.getArgumentCondVars(tcounter), extMIH.getNumNoneArgumentCondVars(tcounter), bwMinedPatterns, CommonConstants.ARGUMENT_PATTERNS);
        		}

        		bwMinedPatterns.write("\tPreceding Method Call Conditions: NoConditionCount -> " + extMIH.getNumNonePreceedingCondVar() + "\n");        		
        		computeFrequentConditionalRules(extMIH, extMIH.getPreceedingCondVar(), extMIH.getNumNonePreceedingCondVar(),bwMinedPatterns, CommonConstants.PRECEDING_METHOD_CALL_PATTERS);
        		
        		bwMinedPatterns.write("\tReturn variable conditions: NoConditionCount -> " + extMIH.getNumNoneReturnCondVar() + "\n");
        		computeFrequentConditionalRules(extMIH, extMIH.getReturnCondVar(), extMIH.getNumNoneReturnCondVar(),bwMinedPatterns, CommonConstants.RETURN_PATTERNS);
        		
        		bwMinedPatterns.write("\tSucceeding Method Call Conditions: NoConditionCount -> " + extMIH.getNumNoneSuceedingCondVar() + "\n");
        		computeFrequentConditionalRules(extMIH, extMIH.getSuceedingCondVar(), extMIH.getNumNoneSuceedingCondVar(),bwMinedPatterns, CommonConstants.SUCCEEDING_METHOD_CALL_PATTERNS);
        		
        		bwMinedPatterns.write("\tSucceeding Receiver Var Conditions: NoConditionCount -> " + extMIH.getNumNoneSuceedingReceiverCondVar() + "\n");
        		computeFrequentConditionalRules(extMIH, extMIH.getSuceedingReceiverCondVar(), extMIH.getNumNoneSuceedingReceiverCondVar(),bwMinedPatterns, CommonConstants.SUCCEEDING_RECEIVER_PATTERNS);
        		
        		//Retaining conditions satisfying the minimum support criteria
        		try {
					
					bwMinedPatterns.write("\tMethod: " + extMIH.toString() + "\n");	
					bwMinedPatterns.write("\tCONDITIONS PASSED MINIMUM SUPPORT CRITERIA\n");
					bwMinedPatterns.write("\tReceiver Conditions: NoConditionCount ->" + extMIH.getNumNoneReceiverCondVar() + "\n");
	        		printCondVarMaps(extMIH.getReceiverCondVar(),bwMinedPatterns);
	        		for(int tcounter = 0; tcounter < extMIH.getNoOfArguments(); tcounter++){
	        			bwMinedPatterns.write("\tArgument Conditions (Position : " + tcounter + "): NoConditionCount ->" + extMIH.getNumNoneArgumentCondVars(tcounter) + "\n");
	        			printCondVarMaps(extMIH.getArgumentCondVars(tcounter),bwMinedPatterns);
	        		}
	        		bwMinedPatterns.write("\tPreceding Method Call Conditions: NoConditionCount -> " + extMIH.getNumNonePreceedingCondVar() + "\n");
	        		printCondVarMaps(extMIH.getPreceedingCondVar(),bwMinedPatterns);
	        		bwMinedPatterns.write("\tReturn variable conditions: NoConditionCount -> " + extMIH.getNumNoneReturnCondVar() + "\n");
	        		printCondVarMaps(extMIH.getReturnCondVar(),bwMinedPatterns);
	        		bwMinedPatterns.write("\tSucceeding Method Call Conditions: NoConditionCount -> " + extMIH.getNumNoneSuceedingCondVar() + "\n");
	        		printCondVarMaps(extMIH.getSuceedingCondVar(),bwMinedPatterns);
	        		bwMinedPatterns.write("\tSucceeding Receiver Var Conditions: NoConditionCount -> " + extMIH.getNumNoneSuceedingReceiverCondVar() + "\n");
	        		printCondVarMaps(extMIH.getSuceedingReceiverCondVar(),bwMinedPatterns);
	        		
        		} catch (IOException e) {
					logger.error("Exception occurred" + e);
				}
        		
        		//Pattern report with its support
        		
        		
        		
        	}	        	
        }
        
        bwMinedPatterns.close();
        
        //Generating consolidated report of mined patterns
        BufferedWriter bwMinedConsol = new BufferedWriter(new FileWriter("MinedPatternsConsolidated.csv"));
        BufferedWriter bwMinedPatternsCompl = new BufferedWriter(new FileWriter("MinedPatternsComplete.csv"));
        bwMinedConsol.write("APIName#Receiver(Total)#Receiver(Mined)#Argument(Total)#Argument(Mined)" +
        		"#Return(Total)#Return(Mined)#Preceding(Total)#Preceding(Mined)#SuceedingMethod(Total)#SuceedingMethod(Mined)#SuceedingReceiver(Total)" +
        		"#SuceedingReceiver(Mined)\n");
        bwMinedPatternsCompl.write("APIName#PATTERN_TYPE#CONDITION_TYPE#Support#Favourable#Total#CodeSampleFile#CodeSampleMethod\n");
        for(ExternalObject extObj : externalObjMap.values()) {
        	for(MethodInvocationHolder extMIH : extObj.getMiList()) {        
        		bwMinedConsol.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#");
        		bwMinedConsol.write(extMIH.getReceiverCondVar().getTotalCondVar().size() + "#" + extMIH.getReceiverCondVar().getSelectedCondVar().size() + "#");
        		
        		for(CondVarHolder recCVH : extMIH.getReceiverCondVar().getSelectedCondVar().values()) {
        			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#RECEIVER#" + recCVH + "#" + recCVH.getSupport()
        					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
        		}
        		
        		int totalArgCondvar = 0, minedArgCondVar = 0;
        		for(int tcounter = 0; tcounter < extMIH.getNoOfArguments(); tcounter++){
        			totalArgCondvar += extMIH.getArgumentCondVars(tcounter).getTotalCondVar().size();
        			minedArgCondVar += extMIH.getArgumentCondVars(tcounter).getSelectedCondVar().size();
        			
            		for(CondVarHolder recCVH : extMIH.getArgumentCondVars(tcounter).getSelectedCondVar().values()) {
            			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#ARGUMENT" + tcounter + "#" + recCVH + "#" + recCVH.getSupport()
            					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
            		}       			
        		}
        		
        		bwMinedConsol.write(totalArgCondvar + "#" + minedArgCondVar + "#"); 
        		bwMinedConsol.write(extMIH.getReturnCondVar().getTotalCondVar().size() + "#" + extMIH.getReturnCondVar().getSelectedCondVar().size() + "#");
        		for(CondVarHolder recCVH : extMIH.getReturnCondVar().getSelectedCondVar().values()) {
        			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#RETURN#" + recCVH + "#" + recCVH.getSupport()
        					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
        		}       		
        		
        		bwMinedConsol.write(extMIH.getPreceedingCondVar().getTotalCondVar().size() + "#" + extMIH.getPreceedingCondVar().getSelectedCondVar().size() + "#");
        		for(CondVarHolder recCVH : extMIH.getPreceedingCondVar().getSelectedCondVar().values()) {
        			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#PRE_METHOD#" + recCVH + "#" + recCVH.getSupport()
        					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
        		}       		

        		bwMinedConsol.write(extMIH.getSuceedingCondVar().getTotalCondVar().size() + "#" + extMIH.getSuceedingCondVar().getSelectedCondVar().size() + "#");
        		for(CondVarHolder recCVH : extMIH.getSuceedingCondVar().getSelectedCondVar().values()) {
        			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#SUC_METHOD#" + recCVH + "#" + recCVH.getSupport()
        					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
        		}
        		
        		bwMinedConsol.write(extMIH.getSuceedingReceiverCondVar().getTotalCondVar().size() + "#" + extMIH.getSuceedingReceiverCondVar().getSelectedCondVar().size() + "#");
        		for(CondVarHolder recCVH : extMIH.getSuceedingReceiverCondVar().getSelectedCondVar().values()) {
        			bwMinedPatternsCompl.write(extObj.getClassName() + "," + extMIH.getMethodName() + "#SUC_RECEIVER#" + recCVH + "#" + recCVH.getSupport()
        					+ "#" + recCVH.getFrequency() + "#" + recCVH.getTotalRelatedFrequency() + "#" + recCVH.getCodeSampleName() + "#" + recCVH.getCodeSampleMethodName() + "\n");
        		}
        		
        		bwMinedConsol.write("\n");  		
        	}
        }	
        bwMinedConsol.close();
        bwMinedPatternsCompl.close();
        logger.warn("TIMING: End of report generation and analysis of patterns: " + System.currentTimeMillis());
	}
	
	/**
	 * Input: Takes a hashmap of conditions and also the number of times that API is used without surronding any conditions
	 * Process: Computes support for different conditions in the HashMap and eliminates
	 * the conditions that have support value less than given threshold value.
	 * @param condVarMap
	 */
	public void computeFrequentConditionalRules(MethodInvocationHolder mainMIH, ConditionVarHolderSet cvhSet, 
			int noneConditionCount, BufferedWriter bwMinedPatterns, int violationPType) throws IOException 
	{
		int totalConditions = noneConditionCount;
			
		for(CondVarHolder recCVH : cvhSet.getTotalCondVar().values()) {
			//Printing for debugging purpose
			bwMinedPatterns.write("\t\t" + recCVH.printString() + "\n");
			totalConditions += recCVH.getFrequency();
		}
		
		double noneConditionSupport = ((double)noneConditionCount) / ((double) totalConditions);
		
		CondVarHolder highSupportCondition = null;
		double maximumExistingSupportVal = 0.0;
		for(CondVarHolder recCVH : cvhSet.getTotalCondVar().values()) {
			double supportVal = ((double)recCVH.getFrequency() / (double)totalConditions);
			recCVH.setTotalRelatedFrequency(totalConditions);
			recCVH.setSupport(supportVal);
			
			
			//Additional heuristic for marking few conditions as high support conditions, esp. the return variable conditions
			//if(recCVH.isBPromotedSupport() && violationPType == CommonConstants.RETURN_PATTERNS) {
			//	highSupportCondition = recCVH;
			//	maximumExistingSupportVal = 0.9001;
			//} else 
			if(maximumExistingSupportVal < supportVal) {
				highSupportCondition = recCVH;
				maximumExistingSupportVal = supportVal;
			}			
		}	
		
		if(noneConditionSupport < CommonConstants.LOWER_THRESHOLD || maximumExistingSupportVal >= CommonConstants.UPPER_THRESHOLD) {
			if(highSupportCondition != null && maximumExistingSupportVal >= CommonConstants.UPPER_THRESHOLD) {
				//Heuristic 1: To handle high confidence support condition
				cvhSet.getSelectedCondVar().clear();			//Clear if any conditions are already added
				for(CondVarHolder recCVH : cvhSet.getTotalCondVar().values()) {
					//Due to the promotion heuristic, sometimes even the support is low, the confidence will be high.
					if(recCVH.getSupport() >= CommonConstants.UPPER_THRESHOLD || (recCVH.isBPromotedSupport() && violationPType == CommonConstants.RETURN_PATTERNS)) {	
						cvhSet.getSelectedCondVar().put(recCVH.toString(), recCVH);	//Add the highest confidence condition to the set
					}	
				}				
				cvhSet.setFilteredType(CommonConstants.HIGH_CONFIDENCE);
				cvhSet.setHighestSupportCondition(highSupportCondition);
			} else {
				//Heuristic 2: Identify the higher support value patterns and make them
				//average conditions
				
				//Heuristic 2.1:
				//An additional heuristic that defines when the difference between 
				//the total number and selected number is minor. This indicates that
				//all conditions are of same importance.
				if((cvhSet.getTotalCondVar().size() - cvhSet.getSelectedCondVar().size()) <=  CommonConstants.AVERAGE_TO_HIGH_DIFF) {
					cvhSet.setFilteredType(CommonConstants.HIGH_CONFIDENCE);
				} else {
					cvhSet.setFilteredType(CommonConstants.AVERAGE_CONFIDENCE);
				}	
				cvhSet.setHighestSupportCondition(highSupportCondition);
				for(CondVarHolder recCVH : cvhSet.getTotalCondVar().values()) {
					if(((maximumExistingSupportVal - recCVH.getSupport()) <= CommonConstants.DIFF_THRESHOLD) && (recCVH.getSupport() > CommonConstants.LOWER_THRESHOLD)) {
						cvhSet.getSelectedCondVar().put(recCVH.toString(), recCVH);
					}
				}			
			}
		} else {
			if(noneConditionSupport >= CommonConstants.UPPER_THRESHOLD || cvhSet.getTotalCondVar().size() == 0) {
				//Heuristic 3: A check that doesnot need any conditions
				cvhSet.getSelectedCondVar().clear();
				cvhSet.setFilteredType(CommonConstants.NO_CONFIDENCE);
			} else {
				//Heuristic 4: A tricky situation. The detected violations are reported as LOW_CONFIDENCE bugs
				//if their support is less than EmptyCondition support.
				if(maximumExistingSupportVal >= noneConditionSupport) {
					cvhSet.setFilteredType(CommonConstants.AVERAGE_CONFIDENCE);
				} else {
					cvhSet.setFilteredType(CommonConstants.LOW_CONFIDENCE);
				}	
				cvhSet.setHighestSupportCondition(highSupportCondition);
				for(CondVarHolder recCVH : cvhSet.getTotalCondVar().values()) {
					if(((maximumExistingSupportVal - recCVH.getSupport()) <= CommonConstants.DIFF_THRESHOLD) && (recCVH.getSupport() > CommonConstants.LOWER_THRESHOLD)) {
						cvhSet.getSelectedCondVar().put(recCVH.toString(), recCVH);
					}
				}			
			}			
		}		
	}
	
	//Debugging function for printing
	private void printCondVarMaps(ConditionVarHolderSet cvhSet, BufferedWriter bw) throws IOException 
	{
		//Printing for debugging purpose
		bw.write("\t\t Heuristic Category:");
		switch(cvhSet.getFilteredType()) {
		case CommonConstants.HIGH_CONFIDENCE : bw.write("HIGH_CONFIDENCE\n"); break;
		case CommonConstants.AVERAGE_CONFIDENCE : bw.write("AVERAGE_CONFIDENCE\n"); break;
		case CommonConstants.LOW_CONFIDENCE : bw.write("LOW_CONFIDENCE\n"); break;
		case CommonConstants.NO_CONFIDENCE : bw.write("NO_CONFIDENCE\n"); break;
		default : break;	
		}
		for(CondVarHolder recCVH : cvhSet.getSelectedCondVar().values()) {
			bw.write("\t\t" + recCVH.printString() + "\n");
		}
	}
	
	/**
	 * DETECT BUGS IN CODE SAMPLES section for identifying bugs in the downloaded code samples
	 */
	@SuppressWarnings("unchecked")
	public void detectBugsInCodeSamples() 
	{
		//Currently only debugging info is written here
		HashSet<CodeSampleDefectHolder> detectedBugsSet = RepositoryAnalyzer.getInstance().getCodeSampleDefects();
		
		//Sort the results using a TreeSet and print them
		TreeSet<CodeSampleDefectHolder> sortedBugs = new TreeSet<CodeSampleDefectHolder>(new CodeSampleDefectHolder());
		for(CodeSampleDefectHolder csdhObj : detectedBugsSet) {
			if(!csdhObj.isPossibleFalsePositive())
				sortedBugs.add(csdhObj);
		}	
		
		try {
			BufferedWriter bwBugs = new BufferedWriter(new FileWriter("MinedBugs.csv"));
			bwBugs.write("Filename#Method#Violated API#Violation Type#ConditionExpressionType#Support#ConfidenceLevel#FalsePositive#PackageName\n");
			HashMap<String, String> sampleIDMapper = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper();
			
			for(CodeSampleDefectHolder csdhObj : sortedBugs) {
				bwBugs.write(csdhObj.getPrintString());
				if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_CODESAMPLES) {
					bwBugs.write("#" + sampleIDMapper.get(csdhObj.getFileName()) + "\n");
				} else {
					bwBugs.write("#NA\n");
				}					
			}
			
			bwBugs.close();
			
		} catch (IOException e) {
			logger.error("Exception occurred" + e);
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

	/**
	 * Method for adding sequences to the list
	 */
	public void addToMethodSequences(MethodInvocationHolder mihObj, String filename, String methodName)
	{
		LibMethodHolder lmhObj = getEqviMethodDeclaration(mihObj.getReceiverClass().getType(), mihObj);
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
			LibMethodHolder lmhObj = getEqviMethodDeclaration(mihObj.getReceiverClass().getType(), mihObj);
			
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
		if(containingLCH.isInterface() || lmh.isAbstract())
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
	 * Method that handles the number of times this class is extended
	 * @param className
	 */
	public void handleClassExtension(String className, TypeDeclaration typeNode, boolean bInterface, HashMap<String, LibMethodReuse> currentReusedMethods, String fileName, String methodName, String codeSampleName)
	{
		if(!currentLibClass.equals(className))
			return;
				
		LibClassHolder lcc = (LibClassHolder) libClassMap.get(className);
		if(lcc == null)
			return;
		
		//Identify and increment counters for all overloaded methoddeclarations
    	MethodDeclaration mdArr[] = typeNode.getMethods();
    	for(int i = 0; i < mdArr.length; i++)
    	{
    		if(mdArr[i].isConstructor())
    			continue;
    			
    		MethodInvocationHolder mihObj = null;
    		
    		try{
      			mihObj = new MethodInvocationHolder(new TypeHolder(typeNode.getName().toString()), new TypeHolder(mdArr[i].getReturnType2().toString()), mdArr[i], "");
    		} catch(Exception ex) {
    			//ex.printStackTrace(); /*Happening because of some junk data coming from GCSE*/
    			return;
    		}
    
    		LibMethodHolder lmh = getEqviMethodDeclaration(className, mihObj);
    		
    		if(lmh != null) {
    			addToReusedClasses(lcc, lmh, bInterface, currentReusedMethods);
    		}
    	}
	}
	
	public void addToReusedClasses(LibClassHolder libClsObj, LibMethodHolder inplmhObj, boolean bInterface, HashMap<String, LibMethodReuse> currentReusedMethods) {
		LibClassReuse lcrObj = reusedLibClassMap.get(libClsObj.getName());
		
		if(lcrObj == null) {
			lcrObj = new LibClassReuse();
			lcrObj.setInterface(bInterface);
			lcrObj.setAssociatedLibClass(libClsObj);
			reusedLibClassMap.put(libClsObj.getName(), lcrObj);
		} else {
			lcrObj.incrFrequency();
		}
		
		boolean bLMRProcessed = false;
		List<LibMethodReuse> lmrList = lcrObj.getReusedMethods();
		for(Iterator iter = lcrObj.getReusedMethods().iterator(); iter.hasNext();) {
			LibMethodReuse lmrObj = (LibMethodReuse) iter.next(); 
			if(lmrObj.getAssociatedLibMethodHolder().equals(inplmhObj)) {
				lmrObj.incrFrequency();
				String key = inplmhObj.getName() + "#" + inplmhObj.getArgumentStr();
				currentReusedMethods.put(key, lmrObj);
				bLMRProcessed = true;
				break;
			}		
		}
		
		
		if(!bLMRProcessed) {			
			LibMethodReuse lmrObj = new LibMethodReuse();
			lmrObj.setAssociatedLibMethodHolder(inplmhObj);
			lcrObj.addToReusedMethods(lmrObj);
			String key = inplmhObj.getName() + "#" + inplmhObj.getArgumentStr();
			currentReusedMethods.put(key, lmrObj);
		}
	}
	
	public HashSet<CodeExampleStore> getCodeExampleSet() {
		return codeExampleSet;
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

	public HashMap<String, Integer> getReusedMethodInvocationIdMapper() {
		return reusedMethodInvocationIdMapper;
	}
}
