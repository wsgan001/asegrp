package xweb.code.analyzer;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;
import xweb.core.MIHList;
import xweb.core.RepositoryAnalyzer;

/**
 * Analyzes sequences and returns lists of sequences based on data-dependency analysis.
 * @author Suresh
 */
public class SequenceAnalyzer {

	private static Logger logger = Logger.getLogger("SequenceAnalyzer");
	
	/**
	 * slices a single sequence into multiple sequences, based on the mode set in properties file via "ObjectPatternMode"
	 * @param lMihList
	 */
	public static void sliceObjectSequences(List<MIHList> lMihList)
	{
		switch(CommonConstants.OBJECT_PATTERN_MODE)
		{
			case CommonConstants.SINGLE_OBJECT_PATTERN_MODE:
				sliceSingleObjectSequences(lMihList);
				return;
			case CommonConstants.MULTI_OBJECT_PATTERN_MODE:
				sliceMultiObjectSequences(lMihList);
				return;
			default: 
				logger.error("Invalid option for ObjectPatternMode. Possible values are 0 or 1, check the properties file!!!");			
		}
	}
	
	
	/**
	 * Slices a given sequence into multiple sequences, where each sequence starts with a constructor
	 * followed by the methods of that object. Each sequence exactly corresponds to a single object. 
	 * Also stores the sliced sequences in a RepositoryAnalyzer instance
	 */
	public static void sliceSingleObjectSequences(List<MIHList> lMihList)	
	{
		//For time being, there is not much difference in the handling of single and
		//multi-object. Processing inside extractIndividualSequences is different based on the flag
		sliceMultiObjectSequences(lMihList);		
	}	
	
	/**
	 * Slices given sequence into multiple sequences. Each sequence corresponds to the multiple
	 * objects which are interdependent on each other
	 */
	public static void sliceMultiObjectSequences(List<MIHList> lMihList)
	{
		try 
		{
			RepositoryAnalyzer raobj = RepositoryAnalyzer.getInstance();
			Set<MIHList> finalMihSetForMethod = new HashSet<MIHList>();
			extractIndividualSequences(raobj, lMihList, finalMihSetForMethod);
			
			//debugging - dumping the minimized set of individual sequences				
			if(CommonConstants.MINER_LOGGING_MODE == CommonConstants.MAXIMAL_LOGGING_MODE)
			{
				raobj.bwAssocMiner.write("Minimized list of sequences\n");						
				for(MIHList mihlist : finalMihSetForMethod)
				{					
					for(MethodInvocationHolder tmih : mihlist.getMihList())
						raobj.bwAssocMiner.write("\t" + tmih.toString() + "(" + tmih.getKey() +  ")\n");			
					
					raobj.bwAssocMiner.write("\n");
				}
			}
			//end of debugging code
			
			//process the final mihlist collected from several sequences into repository analysis
			raobj.addToFinalMIHList(finalMihSetForMethod);
		}
		catch(Exception ex)
		{
			logger.error("Failed while computing slices !!!");
			ex.printStackTrace();
		}
	}

	/**
	 * Includes code for splitting the traces for both single-object and multi-object patterns
	 * @param raobj
	 * @param lMihList
	 * @param finalMihSet
	 */
	private static void extractIndividualSequences(RepositoryAnalyzer raobj, List<MIHList> lMihList, Set<MIHList> finalMihSet) 
	{
		//This set is used to keep track of types found in the path to extract the sequence
		HashSet<String> typeVarSet = new HashSet<String>();
		//HashSet<Integer> keySet = new HashSet<Integer>();		
			
		//Processing each mihlist independently. Each list itself includes
		//various different independet method invocation sequences
		for(MIHList ml : lMihList) {
			MIHList localml = ml;
			if(localml.getMihList().size() == 0) {
				continue;
			}
			
			//Each milist is iterated multiple times till it is divided into multiple different sequences
			while(true) 
			{	
				int milen = localml.getMihList().size();
				if(milen == 0) 
				{
					//no elements left to process. break from the loop
					break;
				}
				
				if(milen == 1)
				{
					//Only one element left. Make sure the receiver class is not in the black-listed set of classes
					MethodInvocationHolder mihtemp = localml.getMihList().get(0);
					MethodInvocationHolder keyedMIH = getKeyedMethodInvocationHolder(raobj, mihtemp);
					if(keyedMIH != null) {
						MIHList tmihlist = new MIHList();
						tmihlist.AddMethodInvocationHolder(keyedMIH);
						finalMihSet.add(tmihlist);
					}										
					break;
				}
				
				typeVarSet.clear();
				//keySet.clear();				
				MethodInvocationHolder[] miarr = new MethodInvocationHolder[milen]; 
				localml.getMihList().toArray(miarr);								
				MIHList minimizedList = new MIHList();
				MIHList remainingList = new MIHList();
				
				//Sequence is processed in the reverse order and a part of the sequence
				//is extracted. Each sequence is traversed multiple times.
				MethodInvocationHolder keyedMIH = getKeyedMethodInvocationHolder(raobj, miarr[0]);
				if(keyedMIH != null)
					minimizedList.AddMethodInvocationHolder(keyedMIH);
				
				//Initialize the typevar ahead to identify all dependent types.
				//Without such scenario of parsing for two types, several bugs can happen				
				addToTypeVarSet(miarr[0], typeVarSet);
				int tcnt;				
							
				for(tcnt = milen - 2 ; tcnt >= 0; tcnt--)
				{
					MethodInvocationHolder mihObj = miarr[tcnt];
					boolean bTypeSetContains = false;
															
					if(typeVarSet.contains(mihObj.getReturnType().var)) 
					{
						if(CommonConstants.OBJECT_PATTERN_MODE == CommonConstants.MULTI_OBJECT_PATTERN_MODE)
						{
							//In case of multi-object mode, we continue traversing the rest of the sequences, due to transitive dependency						
							bTypeSetContains = true;
							typeVarSet.remove(mihObj.getReturnType().var);
						} 
						else 
						{
							if(mihObj.getMethodName().equals("CONSTRUCTOR"))
							{
								//In case of single-object mode, we can break at this point, since the variable
								//re-defined at this point using an assignment statement. Include the current
								//method into this subsequence only if it is a constructor.
								//Don't consider the current method, if it is not a constructor
								keyedMIH = getKeyedMethodInvocationHolder(raobj, mihObj);
								if(keyedMIH != null)
									minimizedList.AddMethodInvocationHolder(keyedMIH);
								tcnt--;
							}
							break;
						}
					}
					
					if(typeVarSet.contains(mihObj.getReceiverClass().var))
					{
						bTypeSetContains = true;
						typeVarSet.remove(mihObj.getReceiverClass().var);
					}
						
					if(bTypeSetContains)
					{	
						//if(!keySet.contains(new Integer(mihObj.getKey())))
						//{						
						
						addToTypeVarSet(mihObj, typeVarSet);
						
						//keySet.add(new Integer(mihObj.getKey()));
							
						keyedMIH = getKeyedMethodInvocationHolder(raobj, mihObj);
						if(keyedMIH != null)
							minimizedList.AddMethodInvocationHolder(keyedMIH);
						//}
					}
					else
					{
						remainingList.AddMethodInvocationHolder(mihObj);
					}
				}
				
				//processing the remaining elements in the array by adding them to remainingList
				while(tcnt >= 0)
				{
					remainingList.AddMethodInvocationHolder(miarr[tcnt]);
					tcnt--;
				}
				
				//process the minimized list
				if(minimizedList.getMihList().size() > 0)
				{
					minimizedList.reverseList();
					finalMihSet.add(minimizedList);
				}
				
				//Iterate through the remaining list again
				remainingList.reverseList();
				localml = remainingList;
			}
		}
	}
	
	/**
	 * Given a method invocation holder, this function returns an associated method invocation holder that
	 * belongs to declared methods in the library or methods of external objects. The return value "null"
	 * indicates that this method-invocation can be ignored
	 * @param mih
	 * @return
	 */
	private static MethodInvocationHolder getKeyedMethodInvocationHolder(RepositoryAnalyzer raobj, MethodInvocationHolder mihObj)
	{
		String receiverType = mihObj.getReceiverClass().type;
		if(receiverType.equals("this") || raobj.ignoreClassSet.contains(receiverType))
		{
			//logger.warn("Ignored the method invocation: " + mihObj.toString());
			return null;
		}
		
		if(receiverType.contains(CommonConstants.unknownType))
		{
			//logger.warn("Ignored the method invocation: " + mihObj.toString());
			return null;
		}
		
		//Get the keyed method-invocation by series of lookups.		
		Holder assocLibMIH = raobj.getAssocLibMIH(mihObj);
		if(assocLibMIH != null)
		{
			mihObj.setKey(assocLibMIH.getKey());
			return mihObj;
		}
		
		if((CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT ||
				CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT) && !mihObj.getMethodName().equals("DOWNCAST"))
		{
			logger.warn("Could not find equivalent method declaration (needs to fix this)" + mihObj.toString());
		}
		
		ASTCrawlerUtil.assignKeyToMethodInvocations(mihObj);		
		return mihObj;
	}
	
	private static boolean isVariableExistsInMethodInvocationHolder(MethodInvocationHolder mihObj, String varObj) {		
		if(mihObj.getReceiverClass().var.equals(varObj)	|| mihObj.getReturnType().var.equals(varObj)) {
			return true;
		}	
		
		for(TypeHolder thObj : mihObj.getArgumentArr()) {
			if(thObj.var.equals(varObj)) {
				return true;
			}			
		}	
		return false;
	}
	
	/**
	 * Function that adds reference class and arguments to type set
	 * This is mainly used for checking
	 */
	public static void addToTypeVarSet(MethodInvocationHolder mihObj, HashSet<String> typeSet)
	{		
		if(!mihObj.getMethodName().equals("CONSTRUCTOR"))
		{
			typeSet.add(mihObj.getReceiverClass().var);
		}
				
		if(CommonConstants.OBJECT_PATTERN_MODE == CommonConstants.MULTI_OBJECT_PATTERN_MODE) {
			TypeHolder argArr[] =  mihObj.getArgumentArr();
			for(int count = 0; count < argArr.length; count++) {
				if(argArr[count].type.contains(".") && !argArr[count].type.equals("java.lang.String"))
				{
					typeSet.add(argArr[count].var);
				}
			}
		}
	}	
	
	/**
	 * A function that identifies the context in which the pattern is a valid
	 * by traversing back till exact required API is found
	 * @param srcMIH
	 * @param errMIH
	 * @param associatedLibMIH
	 * @ensures: if return is non-null, it is not of empty size
	 */
	public List<MethodInvocationHolder> generatePatternContext(MethodInvocationHolder srcMIH, MethodInvocationHolder errMIH, Holder associatedLibMIH) {
		if(isVariableExistsInMethodInvocationHolder(srcMIH, errMIH.getReceiverClass().var)) {
			return null;
		}

		//Get the node responsible for getting the error MIH and identify the path the leads
		//to that node. This servers as a target to generate the pattern context
		//Holder errMIHRoot = varCreationMap.get(errMIH.getReceiverClass().var);
		//if(errMIHRoot == null) 
		//	errMIHRoot = mihRoot;	
		
		//MethodInvocationHolder mihToCompare = varCreationMap.get(srcMIH.getReceiverClass().var);
		//if(mihToCompare == null)
		//	mihToCompare = srcMIH;
		
		//Compute the shortest path
		//DijkstraShortestPath dspObj = new DijkstraShortestPath(methodDeclGraph);
		//List path = dspObj.getPath(errMIHRoot, mihToCompare);
		
		//Stack<MethodInvocationHolder> mihPathStack = new Stack<MethodInvocationHolder>();
		//DirectedSparseEdge dseEdge = null;
		//for(Iterator iter = path.iterator(); iter.hasNext();)
		//{
		//	dseEdge = (DirectedSparseEdge)iter.next();
		//	Holder graphObj = (Holder) dseEdge.getSource();
		//	if(graphObj instanceof MethodInvocationHolder) {
		//		mihPathStack.push((MethodInvocationHolder)graphObj);
		//	}			
		//}	
		
		//if(dseEdge != null)
		//{
		//	Holder mihObj = (Holder) dseEdge.getDest();
		//	if(mihObj instanceof MethodInvocationHolder) {
		//		mihPathStack.push((MethodInvocationHolder)mihObj);
		//	}
		//}		
		
		//List<MethodInvocationHolder> retMihList = constructAndPrintMinimalSequence(mihPathStack, errMIH);
		//if(retMihList != null && retMihList.size() == 0) 
		//	return null;		
		
		//return retMihList;
		return null;
	}
}
