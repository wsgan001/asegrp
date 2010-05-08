package xweb.code.analyzer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

import org.apache.log4j.Logger;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.core.MIHList;
import xweb.core.RepositoryAnalyzer;

/**
 * Analyzes sequences and returns lists of sequences based on data-dependency analysis.
 * @author Suresh
 */
public class SequenceAnalyzer {

	private static Logger logger = Logger.getLogger("SequenceAnalyzer");
	
	/**
	 * Slices a given sequence into multiple sequences, where each sequence starts with a constructor
	 * followed by the methods of that object. Each sequence exactly corresponds to a single object. 
	 * Also stores the sliced sequences in a RepositoryAnalyzer instance
	 */
	public static void sliceSingleObjectSequences(List<MIHList> lMihList)	
	{
		
		
		
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
			
			//process the final mihlist collected from several sequences into repository analysis
			raobj.finalMihList.addAll(finalMihSetForMethod);			
		}
		catch(Exception ex)
		{
			logger.error("Failed while computing slices !!!");
			ex.printStackTrace();
		}
	}

	private static void extractIndividualSequences(RepositoryAnalyzer raobj, List<MIHList> lMihList, Set<MIHList> finalMihSet) 
	{
		//This set is used to keep track of types found in the path to extract the sequence
		HashSet<String> typeSet = new HashSet<String>();
		HashSet<Integer> keySet = new HashSet<Integer>();		
				
		//Processing each mihlist independently. Each list itself includes
		//various different independet method invocation sequences
		for(MIHList ml : lMihList)
		{
			typeSet.clear();
			keySet.clear();
			
			MIHList localml = ml;
			if(localml.getMihList().size() == 0)
			{
				continue;
			}
			
			//Each milist is iterated multiple times till it is divided into multiple
			//different sequences
			while(true) 
			{			
				int milen = localml.getMihList().size();
				MethodInvocationHolder[] miarr = new MethodInvocationHolder[milen]; 
				localml.getMihList().toArray(miarr);
								
				if(milen == 0) 
				{
					//no elements left to process. break from the loop
					break;
				}
				
				if(milen == 1)
				{
					finalMihSet.add(localml);				
					break;
				}
				
				MIHList minimizedList = new MIHList();
				MIHList remainingList = new MIHList();
				
				minimizedList.AddMethodInvocationHolder(miarr[milen - 1]);
				addToTypeSet(miarr[milen - 1], typeSet);	
				keySet.add(new Integer(miarr[milen - 1].getKey()));
				for(int tcnt = milen - 2 ; tcnt >= 0; tcnt--)
				{
					MethodInvocationHolder mihObj = miarr[tcnt];
					boolean bTypeSetContains = false;
					if(typeSet.contains(mihObj.getReturnType())) 
					{
						bTypeSetContains = true;
						typeSet.remove(mihObj.getReturnType());
					}
					
					if(typeSet.contains(mihObj.getReceiverClass().var))
					{
						bTypeSetContains = true;
						typeSet.remove(mihObj.getReceiverClass().var);
					}
						
					if(bTypeSetContains)
					{	
						if(!keySet.contains(new Integer(mihObj.getKey())))
						{						
							addToTypeSet(mihObj, typeSet);
							keySet.add(new Integer(mihObj.getKey()));
							
							if(!raobj.ignoreClassSet.contains(mihObj.getReceiverClass().type))
								minimizedList.AddMethodInvocationHolder(mihObj);
						}
					}
					else
					{
						remainingList.AddMethodInvocationHolder(mihObj);
					}					
				}
				
				//process the minimized list
				minimizedList.reverseList();
				finalMihSet.add(minimizedList);				
				localml = remainingList;				
			}
		}
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
	public static void addToTypeSet(MethodInvocationHolder mihObj, HashSet<String> typeSet)
	{
		if(!mihObj.getMethodName().equals("CONSTRUCTOR")) {
			String searchType = mihObj.getReceiverClass().var;
			typeSet.add(searchType);
		}	
		
		TypeHolder argArr[] =  mihObj.getArgumentArr();
		for(int count = 0; count < argArr.length; count++)
		{
			if(argArr[count].type.contains(".") && !argArr[count].type.equals("java.lang.String"))
			{
				typeSet.add(argArr[count].var);
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
