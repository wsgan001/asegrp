package pw.code.analyzer;

import java.util.HashMap;
import java.util.HashSet;

/**
 * Miner class that mines several final sequences for gathering final set
 * Heuristics of this class are available in SequencePath
 * @author suresh_thummalapenta
 *
 */
public class SequenceMiner {
	
	public static void storeSequence(String path, String sequence, int numOfMethodCalls, 
			int numOfUndefinedVars, HashSet keySet, boolean confidence, HashMap sequenceMap,
			String currentFileName, String currentMethodName)
	{
		SequencePath spObj = new SequencePath(path, keySet, confidence);
		SequenceStore ssStore = (SequenceStore) sequenceMap.get(spObj);
		if(ssStore == null)
		{
			ssStore = new SequenceStore();
			ssStore.sequence = sequence;
			ssStore.incrNumOfTimes();
			ssStore.javaFileName = currentFileName;
			ssStore.methodName = currentMethodName; 
			ssStore.numOfMethodCalls = numOfMethodCalls;
			ssStore.prevClassName = currentFileName;
			ssStore.numUndefinedVars = numOfUndefinedVars;
			ssStore.confidenceLevel = confidence;
			ssStore.actualPath = path;
			ssStore.keySet = keySet;
			sequenceMap.put(spObj, ssStore);
		}
		else
		{
			//Heuristics are applied while matching current sequence with prior sequences.
			//Increment this only when new sequence is found in a new file. If not the same sequence can be given
			//high priority due to method inlining
			if(!currentFileName.equals(ssStore.prevClassName))
			{	
				ssStore.incrNumOfTimes();
				ssStore.prevClassName = currentFileName;
			}
			
		
			if(ssStore.numOfMethodCalls > numOfMethodCalls)
			{
				//Current sequence is a sub set of old sequence. Give preference to smaller sequence
				
				//Before updating make sure that current sequence has no UNKNOWN
				//because a longer sequence with no UNKNOWNS is better than a shorter with UNKNOWNS
				if(sequence.indexOf("#UNKNOWN#") == -1)
				{
					ssStore.sequence = sequence;
					ssStore.javaFileName = currentFileName;
					ssStore.methodName = currentMethodName;
					ssStore.numOfMethodCalls = numOfMethodCalls;
					ssStore.prevClassName = currentFileName;
					ssStore.numUndefinedVars = numOfUndefinedVars;
					ssStore.actualPath = path;
					ssStore.keySet = keySet;
					
					//Update key also
					sequenceMap.remove(spObj);
					SequencePath spObj1 = new SequencePath(path, keySet, confidence);
					sequenceMap.put(spObj1, ssStore);
				}
			}
		}
	}

}
