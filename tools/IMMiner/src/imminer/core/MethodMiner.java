package imminer.core;

import imminer.analyzer.LatticeElemGenerator;
import imminer.pattern.AndOnlyPattern;
import imminer.pattern.ComboPattern;
import imminer.pattern.ItemSet;
import imminer.pattern.MinedPattern;
import imminer.pattern.OrOnlyPattern;
import imminer.pattern.SinglePattern;
import imminer.pattern.XorOnlyPattern;

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
	
	//All itemsets of this method
	List<ItemSet> itemsetsList = new ArrayList<ItemSet>();
	
	//All unique ids of elements in itemsets
	List<String> uniqueIDList = new ArrayList<String>();
	String dummyMethodId = null;
	
	MinerStorage ms;
	Map<String, MinedPattern> minedPatterns = new HashMap<String, MinedPattern>();
	MethodSpecificIds msi = null;
	
	
	public MethodMiner(String methodId, String methodName)
	{
		this.methodId = methodId;
		this.methodName = methodName;
		this.ms = MinerStorage.getInstance();
		this.msi = this.ms.getMethodSpecificIdMapper(this.methodId);
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
									
			//The initial set will be the elements, where each itemset includes only one element
			Set<ItemSet> initialSet = LatticeElemGenerator.generateNextElements(null, this.uniqueIDList, 1);
			List<MinedPattern> allSinglePatterns = new ArrayList<MinedPattern>();
			
			for(ItemSet candidateIs : initialSet)
			{
				//If there is only a single pattern, create SinglePattern
				SinglePattern sp = new SinglePattern(this.methodId, candidateIs);
				sp.calculateSupport(this.itemsetsList);
				allSinglePatterns.add(sp);			
				
				if(sp.getSupportValue() >= AllConstants.MIN_SUP) 
					this.ms.storePattern(this.methodId, sp, PatternType.SINGLE_PATTERN);				
			}
			
			this.generateAndPatterns(initialSet);
			this.generateComboPatterns(allSinglePatterns, PatternType.OR_PATTERN);
			this.generateComboPatterns(allSinglePatterns, PatternType.XOR_PATTERN);		
				
			//Generates complex combo patterns
			this.generateComboPatterns(allSinglePatterns, PatternType.COMBO_PATTERN);			
		} 
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	private void generateComboPatterns(List<MinedPattern> allSinglePatterns, PatternType ptype) 
	{
		System.out.println("\tMining Patterns of the form " + ptype.toString());		
		Set<MinedPattern> currentSet = new TreeSet<MinedPattern>();
		currentSet.addAll(allSinglePatterns);
		
		Set<String> alreadyVisitedLattices = new HashSet<String>();		
		int latticelevel = 2;
		HashMap<String, Integer> numTimesPatternFound = new HashMap<String, Integer>();
		
		if(ptype == PatternType.COMBO_PATTERN)
		{
			//A special step for first getting all the and patterns
			//and modify the currentSet			
			currentSet = getPreComboPatterns(currentSet);
		}
		
		//Iterate through the lattice to generate more meaningful patterns
		while(true)
		{
			System.out.println("\t\tProcessing lattice level " + latticelevel);
			System.out.println("\t\tNumber of items in the child level " + currentSet.size());
			Set<MinedPattern> nextSet = new TreeSet<MinedPattern>();
			
			int size = currentSet.size();
			MinedPattern[] mpArr = new MinedPattern[size];
			currentSet.toArray(mpArr);
			Set<MinedPattern> childMpToKeep = new HashSet<MinedPattern>();			
						
			if(currentSet.size() > 1)
			{
				//Form new mined patterns
				for(int outtcnt = 0; outtcnt < size; outtcnt++)
				{
					//Compute all successor patterns that bypass the qualifier check
					List<ComboPattern> allSuccessorPatterns = new ArrayList<ComboPattern>();
					
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
						
						ComboPattern nextLatticeCp = getComboPattern(leftChild, rightChild, ptype);
						if(nextLatticeCp == null)
						{
							childMpToKeep.add(dominatingPattern);
							continue;
						}
						
						//Here TreeSet is very important to keep the IDs sorted for
						//find equivalence among sets.
						Set<String> allitems = new TreeSet<String>();
						this.ms.getAllUniqueItems(nextLatticeCp, allitems);
						String keyString = allitems.toString();
						if(alreadyVisitedLattices.contains(keyString))
							continue;
						alreadyVisitedLattices.add(keyString);
												
						//Checking whether this pattern can be qualified to be a part of next set	
						boolean bPassedQualifier = true;
						if(nextLatticeCp.getPtype() != PatternType.AND_PATTERN)
						{					
							if(maximumSupport >= nextLatticeCp.getSupportValue())
							{
								bPassedQualifier = false;
								childMpToKeep.add(dominatingPattern);
							}
						}
						
						if(bPassedQualifier)
						{						
							allSuccessorPatterns.add(nextLatticeCp);
						}
					}			
					
					//Add all successor patterns to the final pattern list
					if(allSuccessorPatterns.size() > 0)
					{
						if(AllConstants.ENABLE_GREEDY_APPROACH)					
						{						
							Collections.sort(allSuccessorPatterns);																		
							for(ComboPattern cp : allSuccessorPatterns)
							{						
								//a mined pattern is added only if its children
								//have not yet reached the limit of NUMBER_OF_PATTERNS_TO_CHOOSE
								String leftString = cp.getLeft().getKeyString();
								String rightString = cp.getRight().getKeyString();
								
								Integer numLeftTimes = numTimesPatternFound.get(leftString);
								if(numLeftTimes == null)
								{
									numLeftTimes = new Integer(0);
								}
								
								Integer numRightTimes = numTimesPatternFound.get(rightString);
								if(numRightTimes == null)
								{
									numRightTimes = new Integer(0);
								}
								
								if(cp.getPtype() == PatternType.AND_PATTERN)
								{
									this.ms.storePattern(this.methodId, cp, ptype);
									numTimesPatternFound.put(leftString, new Integer(numLeftTimes.intValue() + 1));
									numTimesPatternFound.put(rightString, new Integer(numRightTimes.intValue() + 1));
									continue;
								}							
								
								if(numLeftTimes.intValue() < AllConstants.NUMBER_OF_PATTERNS_TO_CHOOSE
										&& numRightTimes.intValue() < AllConstants.NUMBER_OF_PATTERNS_TO_CHOOSE) 
								{
									nextSet.add(cp);
									this.ms.storePattern(this.methodId, cp, ptype);
									numTimesPatternFound.put(leftString, new Integer(numLeftTimes.intValue() + 1));
									numTimesPatternFound.put(rightString, new Integer(numRightTimes.intValue() + 1));
								}
							}
						}
						else 
						{
							for(MinedPattern mp : allSuccessorPatterns)
							{
								this.ms.storePattern(this.methodId, mp, ptype);	
							}
						}
					}
				}
			} else
			{
				if(currentSet.size() == 1) {
					MinedPattern mp = currentSet.iterator().next();
					if(mp instanceof ComboPattern && mp.getSupportValue() >= AllConstants.MIN_SUP)
					{
						this.ms.storePattern(this.methodId, mp, ptype);
					}
					break;
				}
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
			
			if(nextSet.size() <= 1) {
				System.out.println("\t\tNo patterns found at this level " + nextSet.size());
				break;
			}
			
			System.out.println("\t\tNumber of items in the parent level after adding childs " + nextSet.size());
			
			currentSet = nextSet;
			latticelevel++;
		}
	}
	
	/**
	 * A function for pre-computing the AND patterns.
	 * @param currSet
	 * @return
	 */
	private Set<MinedPattern> getPreComboPatterns(Set<MinedPattern> initialSet) 
	{	
		System.out.println("Generating Anded patterns");
		Set<MinedPattern> returnSet = new HashSet<MinedPattern>();
		Set<MinedPattern> currentSet = new HashSet<MinedPattern>();
		currentSet.addAll(initialSet);
		
		Set<String> alreadyVisitedLattices = new HashSet<String>();		
				
		//Iterate through the lattice to generate more meaningful patterns
		while(true)
		{
			Set<MinedPattern> nextSet = new HashSet<MinedPattern>();
			
			int size = currentSet.size();
			MinedPattern[] mpArr = new MinedPattern[size];
			currentSet.toArray(mpArr);
								
			//Form new mined patterns
			for(int outtcnt = 0; outtcnt < size; outtcnt++)
			{	
				for(int inntcnt = outtcnt + 1; inntcnt < size; inntcnt++)
				{
					MinedPattern leftChild = mpArr[outtcnt];
					MinedPattern rightChild = mpArr[inntcnt];
					ComboPattern nextLatticeCp = getComboPattern(leftChild, rightChild, PatternType.PRE_COMBO_PATTERN);
					if(nextLatticeCp == null)
					{						
						continue;
					}
					
					//Here TreeSet is very important to keep the IDs sorted for
					//find equivalence among sets.
					Set<String> allitems = new TreeSet<String>();
					this.ms.getAllUniqueItems(nextLatticeCp, allitems);
					String keyString = allitems.toString();
					if(alreadyVisitedLattices.contains(keyString))
						continue;
					alreadyVisitedLattices.add(keyString);
											
					nextSet.add(nextLatticeCp);
					returnSet.add(nextLatticeCp);
					if(returnSet.contains(leftChild))
						returnSet.remove(leftChild);
					
					if(returnSet.contains(rightChild))
						returnSet.remove(rightChild);
				}			
			}		
		
			if(nextSet.size() <= 1) {
				break;
			}			
			currentSet = nextSet;			
		}	
		
		//Browse through all the elements in the initial set
		//If any element is not already be a part of some higher AND pattern,
		//add the elements
		Set<String> allUniqueReturnItems = new HashSet<String>();
		for(MinedPattern mp : returnSet)
		{
			this.ms.storePattern(this.methodId, mp, PatternType.COMBO_PATTERN);			
			this.ms.getAllUniqueItems(mp, allUniqueReturnItems);
		}
		
		for(MinedPattern mp : initialSet)
		{
			if(mp instanceof SinglePattern)
			{
				SinglePattern sp = (SinglePattern) mp;
				if(!allUniqueReturnItems.contains(sp.getKeyString()))
				{
					if(sp.getSupportValue() >= AllConstants.MIN_SUP)
						this.ms.storePattern(this.methodId, sp, PatternType.COMBO_PATTERN);	
					returnSet.add(mp);
				}
			}
		}
		
		return returnSet;
	}

	
	private ComboPattern getComboPattern(MinedPattern left, MinedPattern right, PatternType ptype)
	{
		if(ptype == PatternType.OR_PATTERN)
		{
			ComboPattern returnOrCP = new ComboPattern(this.methodId, left, right, PatternType.OR_PATTERN);
			returnOrCP.calculateSupport(this.itemsetsList);			
			//System.out.println(returnOrCP.getKeyString() + " -> " + returnOrCP.getSupportValue());			
			if(returnOrCP.getSupportValue() >= AllConstants.MIN_SUP)
				return returnOrCP;
			else
				return null;
		}
		
		if(ptype == PatternType.XOR_PATTERN)
		{
			ComboPattern returnXorCP = new ComboPattern(this.methodId, left, right, PatternType.XOR_PATTERN);
			returnXorCP.calculateSupport(this.itemsetsList);
			//System.out.println(returnXorCP.getKeyString() + " -> " + returnXorCP.getSupportValue());
			if(returnXorCP.getSupportValue() >= AllConstants.MIN_SUP)
				return returnXorCP;
			else
				return null;
		}
		
		if(ptype == PatternType.PRE_COMBO_PATTERN)
		{
			//During combo patterns, first we compute AND patterns since they cannot
			//survive among OR and XOR patterns.
			ComboPattern returnAndCP = new ComboPattern(this.methodId, left, right, PatternType.AND_PATTERN);
			returnAndCP.calculateSupport(this.itemsetsList);	
			if(returnAndCP.getSupportValue() >= AllConstants.MIN_SUP)
			{
				ComboPattern returnXorCP = new ComboPattern(this.methodId, left, right, PatternType.XOR_PATTERN);
				returnXorCP.calculateSupport(this.itemsetsList);			
				if(returnXorCP.getSupportValue() < AllConstants.MIN_SUP)
				{
					//System.out.println(returnAndCP.getKeyString() + " -> " + returnAndCP.getSupportValue());
					return returnAndCP;
				} 
			}			
			return null;
		}	
	
		if(ptype != PatternType.COMBO_PATTERN)
		{
			logger.warn("Incorrect option for the function getComboPattern!!!");
			return null;
		}
		
		ComboPattern returnAndCP = new ComboPattern(this.methodId, left, right, PatternType.AND_PATTERN);
		returnAndCP.calculateSupport(this.itemsetsList);	
		if(returnAndCP.getSupportValue() >= AllConstants.MIN_SUP)
		{
			ComboPattern returnXorCP = new ComboPattern(this.methodId, left, right, PatternType.XOR_PATTERN);
			returnXorCP.calculateSupport(this.itemsetsList);			
			if(returnXorCP.getSupportValue() >= AllConstants.MIN_SUP)
			{
				//Both And and Xor are >= MIN_SUP. So, OR pattern is more suitable
				ComboPattern returnOrCP = new ComboPattern(this.methodId, left, right, PatternType.OR_PATTERN);
				returnOrCP.calculateSupport(this.itemsetsList);
				//System.out.println(returnOrCP.getKeyString() + " -> " + returnOrCP.getSupportValue());
				return returnOrCP;
			} 
			else 
			{
				return null;
				//And patterns are allowed only when both left and right are simple
				//patterns or include only AND.
				//Set<PatternType> allOperators = new HashSet<PatternType>();
				//this.ms.getAllOperators(left, allOperators);		
				
				//if(allOperators.contains(PatternType.OR_PATTERN) || allOperators.contains(PatternType.XOR_PATTERN))
				//	return null;
				
				//allOperators.clear();								
				//this.ms.getAllOperators(right, allOperators);	
				//if(allOperators.contains(PatternType.OR_PATTERN) || allOperators.contains(PatternType.XOR_PATTERN))
				//	return null;
				//else
				//	return returnAndCP;
			}							
		} else
		{
			ComboPattern returnXorCP = new ComboPattern(this.methodId, left, right, PatternType.XOR_PATTERN);
			returnXorCP.calculateSupport(this.itemsetsList);
			
			if(returnXorCP.getSupportValue() >= AllConstants.MIN_SUP)
			{
				//System.out.println(returnXorCP.getKeyString() + " -> " + returnXorCP.getSupportValue());
				return returnXorCP;
			}
		}		
		return null;
	}
	
	/**
	 * Generates AndPatterns by consistently pruning at all levels. none of the parameters
	 * should be modified by the function
	 */
	public void generateAndPatterns(Set<ItemSet> initialSet)
	{		
		System.out.println("\tMining patterns of the form AND_PATTERN");
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
				AndOnlyPattern ap = new AndOnlyPattern(this.methodId, candidateIs);	
				ap.calculateSupport(this.itemsetsList);
				if(ap.getSupportValue() >= AllConstants.MIN_SUP) 
				{
					this.ms.storePattern(this.methodId, ap, PatternType.AND_PATTERN);
				}
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
		return this.msi.getConditionCheck(methodId);
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
			SinglePattern sp = new SinglePattern(this.methodId, uniqueId);
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
				this.msi.addMethodSpecificID(uniqueId, tmname);
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

	/**
	 * Generates AndPatterns by consistently pruning at all levels. Not used currently anywhere 
	 */
	public void generateOrPatterns(Set<ItemSet> initialSet, List<MinedPattern> patternList, PatternType pt)
	{		
		if(pt != PatternType.OR_PATTERN 
				&& pt != PatternType.XOR_PATTERN)
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
				if(pt == PatternType.OR_PATTERN)
				{
					op = new OrOnlyPattern(this.methodId, candidateIs);
				}
				else
				{
					op = new XorOnlyPattern(this.methodId, candidateIs);					
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
}
