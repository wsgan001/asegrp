package xweb.code.analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;
import org.apache.log4j.Logger;

import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.IntHolder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;
import xweb.core.ExternalObject;
import xweb.core.LibClassHolder;
import xweb.core.LibMethodHolder;
import xweb.core.MIHList;
import xweb.core.RepositoryAnalyzer;

/**
 * Miner class that mines several final sequences for gathering final set
 * Heuristics of this class are available in SequencePath
 * @author suresh_thummalapenta
 *
 */
public class SequenceMiner {
	
	public static Logger logger = Logger.getLogger("SequenceMiner");
	
	public SequenceMiner() {}
	
	@SuppressWarnings("unchecked")
	public void mineStaticTraces() {
		try {
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
						
			BufferedWriter bwTraces = new BufferedWriter(new FileWriter("CompleteTraces.txt"));
			bwTraces.write("\n\n**** COMPLETE TRACES ****\n\n");
			
			TreeSet<MinedPattern> sortedPatterns = new TreeSet<MinedPattern>(new MinedPattern());
			
			//Computing Support for each MIH. The support also includes the 
			//number of call sites where no elements in the error path are found
			//Printing internal appl objects
			for(LibClassHolder libClass : raObj.getLibClassMap().values()) {
				
				for(LibMethodHolder libMIH : libClass.getMethods()) {
					addToSortedSet(libMIH, bwTraces, sortedPatterns);	
				}
			}
			
			//Getting external objects
			for(ExternalObject eeObj : raObj.getExternalObjects().values()) {
				for(MethodInvocationHolder extMIH: eeObj.getMiList()) {
					addToSortedSet(extMIH, bwTraces, sortedPatterns);
				}
			}
			
			bwTraces.close();
			
			bwTraces = new BufferedWriter(new FileWriter("MinedPatterns.csv"));
			bwTraces.write("TraceID, Support, Category, NM, EM, Context\n");
			int traceCnt = 1;
						 
			for(MinedPattern mpObj : sortedPatterns) {
				bwTraces.write(traceCnt + "," + mpObj.support + "," + mpObj.support_category + ",");
				bwTraces.write("" + mpObj.parentMID.getPrintString() + ",");
				bwTraces.write("" + mpObj.childMID.getMihList().get(0).getPrintString() + ",");
				bwTraces.write("" + mpObj.getPcObj() + "\n");
				traceCnt++;		
			}
			
			bwTraces.close();	
			
		} catch(Exception ex) {			
			logger.error("Error occurred during mining");
			ex.printStackTrace();
		}	
	}
	
	private void addToSortedSet(Holder libMIH, BufferedWriter bwIDs, TreeSet<MinedPattern> sortedPatterns) throws IOException {
		//If this method invocation was found only once among all the code samples, there is no point in
		//deciding a pattern for it.
		if(libMIH.getNumCallSites() <= 1) {
			bwIDs.write("Method: " + libMIH.toString() + " NoErrorPCount: " + 
					libMIH.getNoErrorPathCount() + " TotalFreq:NA NumCallSites: " + libMIH.getNumCallSites() + "\n");
			return;
		}
		
		
		List<MethodInvocationHolder> errorMIHList = libMIH.getErrorMIHList();
		if(errorMIHList.size() == 0) {
			bwIDs.write("Method: " + libMIH.toString() + " NoErrorPCount: " + 
					libMIH.getNoErrorPathCount() + " TotalFreq:NA NumCallSites: " + libMIH.getNumCallSites() + "\n");
			return;
		}	
		
		List<IntHolder> errorMIFrequency = libMIH.getErrorMIFrequency();
		int totalFrequency = libMIH.getNoErrorPathCount();
	
		for(IntHolder cntVal : errorMIFrequency) {
			totalFrequency += cntVal.intValue(); 
		}
		
		double noneErrorPathSupport = ((double)libMIH.getNoErrorPathCount()) / ((double) totalFrequency);
		
		MinedPattern highSupportErrorMP = null;
		double maximumExistingSupportVal = 0.0;
		Iterator freqIter = errorMIFrequency.iterator();
		Iterator codeExampleIter = libMIH.getCodeExamples().iterator();
		List<MinedPattern> mpList = new ArrayList<MinedPattern>();
		int numConditionsAtHighValue = 0;
		
		bwIDs.write("Method: " + libMIH.toString() + " NoErrorPCount: " + 
				libMIH.getNoErrorPathCount() + " TotalFreq: " + totalFrequency + " NumCallSites: " + libMIH.getNumCallSites() + "\n");
		
		List<MIHList> pcObjList = libMIH.getAssociatedPatternContext();
		Iterator pcIter = pcObjList.iterator();
		
		for(MethodInvocationHolder childMIH : errorMIHList) {
			IntHolder countVal = (IntHolder)freqIter.next();
			double supportVal = ((double)(countVal).intValue() / (double)totalFrequency);
			supportVal = (Math.round(supportVal*100))/100d;
			
			MIHList assocPCObj = (MIHList) pcIter.next();
			
			List<MethodInvocationHolder> childList = new ArrayList<MethodInvocationHolder>();
			childList.add(childMIH);
			MIHList childMIHList = new MIHList(childList);
			
			MinedPattern mpObj = new MinedPattern(libMIH, childMIHList, supportVal, countVal.intValue(), assocPCObj);
			mpList.add(mpObj);
		
			if(maximumExistingSupportVal < supportVal) {
				highSupportErrorMP = mpObj;
				maximumExistingSupportVal = supportVal;
				numConditionsAtHighValue = 1;
			} else if(maximumExistingSupportVal == supportVal) {
				numConditionsAtHighValue++;
			}
			
			//Debugging purpose
			bwIDs.write("\t"  +  countVal + " : " + supportVal + " : " + childMIH.toString() +  "\n");
			CodeExampleStore sstObj = (CodeExampleStore) codeExampleIter.next();
			bwIDs.write("\t\tFilename: " + sstObj.javaFileName + " Method:" + sstObj.methodName + "\n");
			//End of debugging purpose
		}	
		
		if((maximumExistingSupportVal < CommonConstants.LOWER_THRESHOLD && numConditionsAtHighValue != errorMIHList.size()) 
				|| noneErrorPathSupport > CommonConstants.UPPER_THRESHOLD) {
			return;
		}		
		
		if(noneErrorPathSupport <= CommonConstants.LOWER_THRESHOLD || maximumExistingSupportVal >= CommonConstants.UPPER_THRESHOLD) {
			if(highSupportErrorMP != null && maximumExistingSupportVal >= CommonConstants.UPPER_THRESHOLD) {
				//Heuristic 1: To handle high confidence support condition
				highSupportErrorMP.setSupport_category(MinedPattern.HIGH_CONFIDENCE);
				sortedPatterns.add(highSupportErrorMP);
				libMIH.addToMinedPatterns(highSupportErrorMP);
			} else {
				//Heuristic 2: Identify the higher support value patterns and make them average conditions
				
				//Heuristic 2.1:
				//An additional heuristic that defines when the difference between 
				//the total number and selected number is minor. This indicates that
				//all conditions are of same importance.
				for(MinedPattern mpObj : mpList) {
					if(mpObj.support >= CommonConstants.LOWER_THRESHOLD) {
						if(mpObj.support == maximumExistingSupportVal)
							mpObj.setSupport_category(MinedPattern.HIGH_CONFIDENCE);
						else { 
							if(mpObj.support >= noneErrorPathSupport)
								mpObj.setSupport_category(MinedPattern.AVERAGE_CONFIDENCE);
							else
								mpObj.setSupport_category(MinedPattern.LOW_CONFIDENCE);
						}	
						
						sortedPatterns.add(mpObj);
						libMIH.addToMinedPatterns(mpObj);	
					}
				}			
			}			
		} else {
			//Heuristic 4: A tricky situation. The detected violations are reported as LOW_CONFIDENCE bugs
			//if their support is less than EmptyCondition support else AVERAGE_CONFIDENCE
			for(MinedPattern mpObj : mpList) {
				if(mpObj.support >= CommonConstants.LOWER_THRESHOLD) {
					if(mpObj.support >= noneErrorPathSupport && mpObj.support == maximumExistingSupportVal)
						mpObj.setSupport_category(MinedPattern.AVERAGE_CONFIDENCE);
					else 
						mpObj.setSupport_category(MinedPattern.LOW_CONFIDENCE);
										
					sortedPatterns.add(mpObj);
					libMIH.addToMinedPatterns(mpObj);
				}
			}				
		}			
	}	
}
