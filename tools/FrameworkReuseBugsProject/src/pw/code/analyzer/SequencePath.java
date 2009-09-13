package pw.code.analyzer;

import java.util.HashSet;

import pw.common.CommonConstants;

/**
 * Class that stores the sequence path information. Used in mining process
 * @author suresh_thummalapenta
 *
 */
public class SequencePath  {

	
	String actualPath; //Variable holding the actual path sequence
	HashSet keySet;	   //HashSet holding the set of keys used for comparison later
	boolean confidence;	//Low confidence sequences are not used as a part of heuristics
	
	public SequencePath(String actualPath, HashSet keySet, boolean confidence)
	{
		this.actualPath = actualPath;
		this.keySet = keySet;
		this.confidence = confidence;
	}
	
	@SuppressWarnings("unchecked")
	public boolean equals(Object arg)
	{
		if(!(arg instanceof SequencePath))
			return false;
		
		SequencePath spObj = (SequencePath) arg;
		
		/** Initially perfect matching is performed **/
		//If paths are exactly equal
		if(actualPath.equals(spObj.actualPath))
			return true;
	
		
		//For low confidence sequences, no additional checks are made
		if(!this.confidence || !spObj.confidence)
			return false;
		
		//If no heuristics are configured or sequences are of low confidence levels
		if(CommonConstants.methodSequenceClusterPrecision == 0)
			return false;
		
		//If one is a subset of other.
		if(this.keySet.containsAll(spObj.keySet) || spObj.keySet.containsAll(this.keySet))
			return true;

		
		/**Matching with heuristics **/
		//When CommonConstants.methodSequenceClusterPrecision = 1 -> "1 4 5 7" and "1 4 7 9" are treated as same
		HashSet interSectSet = (HashSet) this.keySet.clone();
		
		
		//Gather the intersection of both the sets
		interSectSet.retainAll(spObj.keySet);
		
		int intersectionCnt = interSectSet.size();
		int firstSetSize = this.keySet.size(); 
		int secSetSize = spObj.keySet.size();
		
		if(intersectionCnt > 0 && ((firstSetSize - intersectionCnt <= CommonConstants.methodSequenceClusterPrecision) 
				|| (secSetSize - intersectionCnt <= CommonConstants.methodSequenceClusterPrecision)))
			return true;
		else
		{
			//A special case where Intersection Count is zero, because both the sequences are of length one only
			//if(this.keySet.size() == 1 && spObj.keySet.size() == 1)
			//{
			//	ASTCrawler astc = ASTCrawler.getCurrentInstance();
			//	if(astc != null)
			//	{
			//		String seq1 = ((SequenceStore)astc.sequenceMap.get(this)).sequence;
			//		String seq2 = ((SequenceStore)astc.sequenceMap.get(spObj)).sequence;
			//		
			//		
			//		
			//	}
			//}
		}
		
		
		
		return false;
	}
	
	//HashCode value made constant to make sure that all elements are stored in the same list. Performance doesn't matter because
	//number of elements in this list as always near to 10 only.
	public int hashCode()
	{
		return 10;
	}
}
