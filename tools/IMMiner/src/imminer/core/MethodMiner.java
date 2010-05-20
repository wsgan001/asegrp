package imminer.core;

import imminer.analyzer.LatticeElemGenerator;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import java.util.Scanner;

import pattern.AndOnlyPattern;
import pattern.ComboPattern;
import pattern.ItemSet;
import pattern.MinedPattern;
import pattern.OrOnlyPattern;
import pattern.SinglePattern;
import pattern.XorOnlyPattern;

/**
 * Generates counting for all elements in the itemset
 * @author suresh
 */
public class MethodMiner {
	
	private static Logger logger = Logger.getLogger("MethodMiner");	
	
	//Id of the method under analysis
	String methodId;
	
	//Name of the method under analysis
	String methodName;
	
	//All specific IDs of this method.
	HashMap<String, String> methodSpecificIDs = new HashMap<String, String>(); 
	
	//All itemsets of this method
	List<ItemSet> itemsetsList = new ArrayList<ItemSet>();
	
	//All unique ids of elements in itemsets
	List<String> uniqueIDList = new ArrayList<String>();
	String dummyMethodId = null;
	
	MinerStorage ms;
	Map<String, MinedPattern> minedPatterns = new HashMap<String, MinedPattern>();
	
	
	public MethodMiner(String methodId, String methodName)
	{
		this.methodId = methodId;
		this.methodName = methodName;
		this.ms = MinerStorage.getInstance();		
	}
	
	/**
	 * Generates the entire lattice of And, Or, and Xor
	 *
	 */
	public void generateLattice()
	{
		try
		{
			loadAllItemSets();
			loadAllMethodIds();
			
			int numItemsBefore = this.uniqueIDList.size();
			pruneUniqueIDList();
			int numItemsAfter = this.uniqueIDList.size();
			
			logger.debug("Number of items pruned for method \"" + this.methodName + "\": " + (numItemsBefore - numItemsAfter));
			
			List<MinedPattern> temppatternList = new ArrayList<MinedPattern>();
						
			//The initial set will be the elements, where each itemset includes only one element
			Set<ItemSet> initialSet = LatticeElemGenerator.generateNextElements(null, this.uniqueIDList, 1);
			Map<String, MinedPattern> tempSupportValues = new HashMap<String, MinedPattern>();			
			List<MinedPattern> allSinglePatterns = new ArrayList<MinedPattern>();
			
			for(ItemSet candidateIs : initialSet)
			{
				//If there is only a single pattern, create SinglePattern
				SinglePattern sp = new SinglePattern(candidateIs);
				sp.calculateSupport(this.itemsetsList);
				allSinglePatterns.add(sp);			
				
				if(sp.getSupportValue() >= AllConstants.MIN_SUP)
					temppatternList.add(sp);
				tempSupportValues.put(sp.getCandidate().toString(), sp);
			}
			
			minedPatterns.clear();
			
			for(String key : tempSupportValues.keySet())
			{
				minedPatterns.put(key, tempSupportValues.get(key));
			}
			//Generates simple patterns of the form "a && b && c" or "a || b || c"
			//generateSimplePatterns(temppatternList, initialSet, tempSupportValues);
			
			//Generates complex combo patterns
			generateComboPatterns(temppatternList, allSinglePatterns);			
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}

	private void generateSimplePatterns(List<MinedPattern> temppatternList, Set<ItemSet> initialSet, Map<String, MinedPattern> tempSupportValues) {
		
		List<MinedPattern> patternList = new ArrayList<MinedPattern>();
		patternList.addAll(temppatternList);
		System.out.println("\tMining And Patterns");
		this.generateAndPatterns(initialSet, patternList);
		Collections.sort(patternList);
		ms.dumpAllPatterns(this, patternList);
					
		minedPatterns.clear();
		System.out.println("\tMining Or Patterns");
		for(String key : tempSupportValues.keySet())
		{
			minedPatterns.put(key, tempSupportValues.get(key));
		}
		patternList.clear();
		patternList.addAll(temppatternList);
		this.generateOrPatterns(initialSet, patternList, PATTERN_TYPE.OR_PATTERN);
		Collections.sort(patternList);
		ms.dumpAllPatterns(this, patternList);
					
		minedPatterns.clear();
		System.out.println("\tMining Xor Patterns");
		for(String key : tempSupportValues.keySet())
		{
			minedPatterns.put(key, tempSupportValues.get(key));
		}
		patternList.clear();
		patternList.addAll(temppatternList);			
		this.generateOrPatterns(initialSet, patternList, PATTERN_TYPE.XOR_PATTERN);		
		Collections.sort(patternList);
		ms.dumpAllPatterns(this, patternList);
	}	
	
	private void generateComboPatterns(List<MinedPattern> patternList, List<MinedPattern> initialPList) 
	{
		System.out.println("\tMining Combo Patterns");		
		Set<MinedPattern> currentSet = new HashSet<MinedPattern>();					
		currentSet.addAll(initialPList);
		
		Set<String> alreadyVisitedLattices = new HashSet<String>();
		
		int latticelevel = 2;
		//Iterate through the lattice to generate more meaningful patterns
		while(true)
		{
			System.out.println("\t\tProcessing lattice level " + latticelevel);
			System.out.println("\t\tNumber of items in the child level " + currentSet.size());
			Set<MinedPattern> nextSet = new HashSet<MinedPattern>();
			
			int size = currentSet.size();
			MinedPattern[] mpArr = new MinedPattern[size];
			currentSet.toArray(mpArr);
			
			Set<MinedPattern> childMpToKeep = new HashSet<MinedPattern>();
			
			//Form new mined patterns
			for(int outtcnt = 0; outtcnt < size; outtcnt++)
			{
				for(int inntcnt = outtcnt + 1; inntcnt < size; inntcnt++)
				{
					//For OR or XOR pattern, the new pattern should have higher support
					//than its children. If not the children subsumes the parent
					MinedPattern leftChild = mpArr[outtcnt];
					MinedPattern rightChild = mpArr[inntcnt];
										
					//Computing the dominant child		
					double leftChildSupport = leftChild.getSupportValue();
					double rightChildSupport = rightChild.getSupportValue();
					
					MinedPattern dominatingPattern = null;
					double maximumSupport;					
					if(leftChildSupport > rightChildSupport)
					{
						maximumSupport = leftChildSupport;
						dominatingPattern = leftChild;							
					} 
					else
					{
						maximumSupport = rightChildSupport;
						dominatingPattern = rightChild;
					}
					
					ComboPattern nextLatticeCp = getComboPattern(leftChild, rightChild);
					if(nextLatticeCp == null)
					{
						childMpToKeep.add(dominatingPattern);
						continue;
					}
					
					Set<String> allitems = new TreeSet<String>();
					this.ms.getAllUniqueItems(nextLatticeCp, allitems);
					String keyString = allitems.toString();
					if(alreadyVisitedLattices.contains(keyString))
						continue;
					alreadyVisitedLattices.add(keyString);
											
					//Checking whether this pattern can be qualified to be a part of next set	
					boolean bPassedQualifier = true;
					if(nextLatticeCp.getPtype() != PATTERN_TYPE.AND_PATTERN)
					{					
						if(maximumSupport + AllConstants.NEXT_LEVEL_SUPPORT >= nextLatticeCp.getSupportValue())
						{
							bPassedQualifier = false;
							childMpToKeep.add(dominatingPattern);
						}
					}
					
					if(bPassedQualifier)
					{
						nextSet.add(nextLatticeCp);
						patternList.add(nextLatticeCp);
					}
				}
			}	
		
			if(nextSet.size() <= 1) {
				System.out.println("\t\tNo patterns found at this level " + nextSet.size());
				break;
			}
			
			System.out.println("\t\tNumber of items in the parent level after pruning " + nextSet.size());
			
			//Adding the childMP. Rather than adding childs blindly, search whether
			//this child is indeed participated in any of the parents, if yes, no need to add
			//the child to the lattice
			for(MinedPattern childMp : childMpToKeep)
			{
				boolean bChildMpExists = false;
				
				for(MinedPattern mp : nextSet)
				{
					if(!(mp instanceof ComboPattern))
						continue;
					
					ComboPattern cp = (ComboPattern) mp;
					if(cp.getLeft().equals(childMp))
					{
						bChildMpExists = true;
						break;
					}
					
					if(cp.getRight().equals(childMp))
					{
						bChildMpExists = true;
						break;
					}
				}			
				
				if(!bChildMpExists)
					nextSet.add(childMp);
			}
			
			System.out.println("\t\tNumber of items in the parent level after adding childs " + nextSet.size());
			
			currentSet = nextSet;
			latticelevel++;
		}
		
		Collections.sort(patternList);
		ms.dumpAllPatterns(this, patternList);
	}
	
	private ComboPattern getComboPattern(MinedPattern left, MinedPattern right)
	{
		ComboPattern returnAndCP = new ComboPattern(left, right, PATTERN_TYPE.AND_PATTERN);
		returnAndCP.calculateSupport(this.itemsetsList);	
		if(returnAndCP.getSupportValue() >= AllConstants.MIN_SUP)
		{
			ComboPattern returnXorCP = new ComboPattern(left, right, PATTERN_TYPE.XOR_PATTERN);
			returnXorCP.calculateSupport(this.itemsetsList);			
			if(returnXorCP.getSupportValue() >= AllConstants.MIN_SUP)
			{
				//Both And and Xor are >= MIN_SUP. So, OR pattern is more suitable
				ComboPattern returnOrCP = new ComboPattern(left, right, PATTERN_TYPE.OR_PATTERN);
				returnOrCP.calculateSupport(this.itemsetsList);
				return returnOrCP;
			} 
			else 
			{
				//And patterns are allowed only when both left and right are simple
				//patterns or include only AND.
				Set<PATTERN_TYPE> allOperators = new HashSet<PATTERN_TYPE>();
				this.ms.getAllOperators(left, allOperators);		
				
				if(allOperators.contains(PATTERN_TYPE.OR_PATTERN) || allOperators.contains(PATTERN_TYPE.XOR_PATTERN))
					return null;
				
				allOperators.clear();								
				this.ms.getAllOperators(right, allOperators);	
				if(allOperators.contains(PATTERN_TYPE.OR_PATTERN) || allOperators.contains(PATTERN_TYPE.XOR_PATTERN))
					return null;
				else
					return returnAndCP;
			}							
		} else
		{
			ComboPattern returnXorCP = new ComboPattern(left, right, PATTERN_TYPE.XOR_PATTERN);
			returnXorCP.calculateSupport(this.itemsetsList);
			
			if(returnXorCP.getSupportValue() >= AllConstants.MIN_SUP)
				return returnXorCP;
		}		
		return null;
	}
	
	
	/**
	 * Generates AndPatterns by consistently pruning at all levels
	 *
	 */
	public void generateOrPatterns(Set<ItemSet> initialSet, List<MinedPattern> patternList, PATTERN_TYPE pt)
	{		
		if(pt != PATTERN_TYPE.OR_PATTERN 
				&& pt != PATTERN_TYPE.XOR_PATTERN)
		{
			logger.error("Only OR and XOR pattern types are allowed in generateOrPatterns!!!");
			return;
		}
		
		Set<ItemSet> currentSet = initialSet;
		int latticelevel = 2;
		while(true)
		{
			System.out.println("\t\tProcessing lattice level " + latticelevel);
			//Get the next set of itemsets
			Set<ItemSet> nextItemSet = LatticeElemGenerator.generateNextElements(currentSet, this.uniqueIDList, latticelevel);
			System.out.println("\t\tNumber of items to process in this level " + nextItemSet.size());

			//list of items to be removed
			List<ItemSet> removeList = new ArrayList<ItemSet>();						 
			Set<ItemSet> childIsToKeep = new HashSet<ItemSet>();
				
			//Compute support values for all newly generated itemsets
			for(ItemSet candidateIs : nextItemSet)
			{
				//Due to pruning strategy, our approach may get the previously 
				//seen itemsets. Discard them here.
				if(this.minedPatterns.containsKey(candidateIs.toString()))
				{
					removeList.add(candidateIs);
					continue;
				}
				
				MinedPattern op = null;								
				if(pt == PATTERN_TYPE.OR_PATTERN)
				{
					op = new OrOnlyPattern(candidateIs);
				}
				else
				{
					op = new XorOnlyPattern(candidateIs);					
				}
					
				op.calculateSupport(this.itemsetsList);
				this.minedPatterns.put(candidateIs.toString(), op);
			}
			
			//Prune the nextset based on the current remove list
			for(ItemSet removeItem : removeList)
				nextItemSet.remove(removeItem);
			removeList.clear();
			
			//If greedy approach is chosen, for every element in the previous
			//lattice level, only one new itemset that is of maximum support is allowed
			Set<ItemSet> greedyItemSet = new HashSet<ItemSet>();
			
			if(AllConstants.ENABLE_GREEDY_APPROACH)
			{
				for(ItemSet childis : currentSet)
				{
					double maximumSupportVal = 0;	
					ItemSet bestNextItemSet = null;
					
					//Get all its successors in the next lattice level
					for(ItemSet candidateIs : nextItemSet)
					{				
						if(candidateIs.containsAll(childis.getItems()))
						{
							MinedPattern op = this.minedPatterns.get(candidateIs.toString());
							if(maximumSupportVal < op.getSupportValue())
							{
								maximumSupportVal = op.getSupportValue();
								bestNextItemSet = candidateIs;
							}
						}
					}
					
					if(bestNextItemSet != null)
					{					
						greedyItemSet.add(bestNextItemSet);
					}
				}
			} else
			{
				greedyItemSet = nextItemSet;
			}
						
			for(ItemSet candidateIs : greedyItemSet)
			{				
				MinedPattern op = this.minedPatterns.get(candidateIs.toString());
				//Computed support should be greater than all the children used to construct this 
				//pattern. If not the pattern is of no use. First calcute the maxiumum support among the
				//children
				double maximumSupportInPrevLevel = 0;
				ItemSet dominatingChildIs = null;
				for(ItemSet childis : currentSet)
				{
					if(candidateIs.containsAll(childis.getItems()))
					{
						double supportVal = this.minedPatterns.get(childis.toString()).getSupportValue();
						if(maximumSupportInPrevLevel < supportVal) {
							maximumSupportInPrevLevel = supportVal;
							dominatingChildIs = childis;
						}
					}
				}
				
				if(maximumSupportInPrevLevel >= op.getSupportValue()
						|| (!AllConstants.ENABLE_GREEDY_APPROACH && op.getSupportValue() < AllConstants.MIN_SUP))
				{
					//When one of its child is dominating the parent OR pattern,
					//keep the child for generating next level of elements in the lattice.
					removeList.add(candidateIs);
					childIsToKeep.add(dominatingChildIs);
				} 
				else
				{
					patternList.add(op);
				}
			}		
			
			//Prune the nextset based on the current remove list
			for(ItemSet removeItem : removeList)
				greedyItemSet.remove(removeItem);
			removeList.clear();		
			
			//Add the dominating child itemsets for continuation
			//for(ItemSet childIs : childIsToKeep)
			//	greedyItemSet.add(childIs);
			
			System.out.println("\t\tNumber of elements after pruning in this level: " + greedyItemSet.size() + ", child elements: " + childIsToKeep.size());
			if(greedyItemSet.size() <= 1)
				break;
			
			currentSet = greedyItemSet;
			latticelevel++;
		}
	}
	
	/**
	 * Generates AndPatterns by consistently pruning at all levels
	 *
	 */
	public void generateAndPatterns(Set<ItemSet> initialSet, List<MinedPattern> patternList)
	{		
		Set<ItemSet> currentSet = initialSet;
		int latticeLevel = 2;
		while(true)
		{
			//Get the next set of itemsets
			Set<ItemSet> nextItemSet = LatticeElemGenerator.generateNextElements(currentSet, this.uniqueIDList, latticeLevel);
			
			//list of items to be removed
			List<ItemSet> removeList = new ArrayList<ItemSet>(); 
			for(ItemSet candidateIs : nextItemSet)
			{
				AndOnlyPattern ap = new AndOnlyPattern(candidateIs);	
				ap.calculateSupport(this.itemsetsList);
				if(ap.getSupportValue() >= AllConstants.MIN_SUP)
					patternList.add(ap);
				else
					removeList.add(candidateIs);
			}
			
			//Prune the nextset based on their support values by removing the items
			for(ItemSet removeItem : removeList)
				nextItemSet.remove(removeItem);
			
			if(nextItemSet.size() <= 1)
				break;
			currentSet = nextItemSet;
			latticeLevel++;
		}	
	}	

	public String decodeMethodId(String methodId)
	{
		return this.methodSpecificIDs.get(methodId);
	}
	
	/**
	 * Prunes the list by eliminating those uniqueIds that are minimal support values.
	 * currently, we eliminate only those uniqueIds whose count value is one.
	 */
	private void pruneUniqueIDList()
	{
		//removing the dummy method id, since we do not need any permutations with this method id
		if(this.dummyMethodId != null)
		{
			this.uniqueIDList.remove(this.dummyMethodId);
		}
		
		List<String> removeIDList = new ArrayList<String>();
		for(String uniqueId : this.uniqueIDList)
		{
			SinglePattern sp = new SinglePattern(uniqueId);
			sp.calculateSupport(this.itemsetsList);
			if(sp.getSupportingItemSets() == 1)
			{
				removeIDList.add(uniqueId);
			}
		}
		
		//Remove all elements in the removeIDList
		for(String removeId : removeIDList)
		{
			this.uniqueIDList.remove(removeId);
		}		
	}
	
	private void loadAllItemSets()
	{
		String dataFile = ms.parentDirectory + AllConstants.FILE_SEP 
							+ AllConstants.METHOD_SPECIFIC_DATA_FOLDER + AllConstants.FILE_SEP
							+ methodId + ".txt";
		try
		{
			HashSet<String> tempUniqueIds = new HashSet<String>();
			Scanner sc = new Scanner(new File(dataFile));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				ItemSet is = new ItemSet(line);
				itemsetsList.add(is);
				tempUniqueIds.addAll(is.getItems());
			}
			sc.close();		
			
			this.uniqueIDList.addAll(tempUniqueIds);
			Collections.sort(this.uniqueIDList);			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("Exception occurred while loading all itemsets" + ex.getMessage());
		}
	}
	
	private void loadAllMethodIds()
	{
		String dataFile = ms.parentDirectory + AllConstants.FILE_SEP 
				+ AllConstants.METHOD_SPECIFIC_ID_FOLDER + AllConstants.FILE_SEP
				+ methodId + ".txt";
		try	
		{
			HashMap<String, String> tempIdSet = new HashMap<String, String>();
			Scanner sc = new Scanner(new File(dataFile));
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				String items[] = line.split(":");
				if(items.length != 2)
				{
					logger.error("Incorrect entry in method specific IDs " + line);
					continue;
				}				
				tempIdSet.put(items[0].trim(), items[1].trim());
			}
			sc.close();
						
			for(String uniqueId : this.uniqueIDList)
			{
				String elements[] = uniqueId.replace(".", ":").split(":");			
				String tmname = tempIdSet.get(elements[1].trim());				
				if(this.dummyMethodId == null && tmname.equals(AllConstants.DUMMY_MINING_ENTRY))
					this.dummyMethodId = uniqueId;				
				this.methodSpecificIDs.put(uniqueId, tmname);
			}
			
			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("Exception occurred while loading all itemsets" + ex.getMessage());
		}
	}

	public Map<String, MinedPattern> getSupportValues() {
		return minedPatterns;
	}
	
}
