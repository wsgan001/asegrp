package imminer.pattern;

import java.util.Comparator;
import java.util.List;

/**
 * Class that represents a mined pattern
 * @author suresh
 *
 */
public abstract class MinedPattern implements Comparator, Comparable {

	double supportValue;
	int supportingItemSets;
	int totalItemSets;
	ItemSet candidate;
	
	//Represents the method to which the pattern belongs to 
	String methodId; 
	
	/**
	 * A constructor intended for use by Combo pattern
	 * since the itemset is not precisely defined
	 */
	public MinedPattern(String methodId)
	{
		this.methodId = methodId;
	}
	
	/**
	 * Constructor that helps create mined pattern with single item
	 * @param candidate
	 */
	public MinedPattern(String methodId, String candidate)
	{
		this.methodId = methodId;
		this.candidate = new ItemSet(candidate);
	}
	
	/**
	 * Constructor that helps create mined pattern with multiple candidate
	 * items
	 * @param candidate
	 */
	public MinedPattern(String methodId, ItemSet candidate)
	{
		this.methodId = methodId;
		this.candidate = candidate;
	}		
		
	public void calculateSupport(List<ItemSet> itemsets) {		
		this.totalItemSets = itemsets.size();
		for(ItemSet is : itemsets)
		{	
			if(this.isSupportedBy(is))
				this.supportingItemSets++;
		}		
		
		this.supportValue = (double)this.supportingItemSets / (double)this.totalItemSets;
	}
	
	/**
	 * checks whether the current pattern is supported by the itemset
	 * @param itemset
	 * @return
	 */
	public abstract boolean isSupportedBy(ItemSet itemset);
	
	/**
	 * Returns the simple keystring that can be used as key. 
	 * This is a simplified version of the actual toString implementation
	 * @return
	 */
	public abstract String getKeyString();
	
	public ItemSet getCandidate()
	{
		return this.candidate;
	}

	public double getSupportValue() {
		return supportValue;
	}

	public int getSupportingItemSets() {
		return supportingItemSets;
	}

	public int getTotalItemSets() {
		return totalItemSets;
	}
	
	public int hashCode()
	{
		return this.candidate.hashCode();
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof MinedPattern))
			return false;
		
		MinedPattern mpOther = (MinedPattern) other;
		return this.candidate.equals(mpOther.candidate);
	}
	
	public int compareTo(Object other) 
	{
		MinedPattern otherp = (MinedPattern) other;
		if(this.supportValue < otherp.supportValue)
			return 1;
		
		if(this.supportValue > otherp.supportValue)
			return -1;
		
		return 1;
	}
	
	public int compare(Object arg1, Object arg2)
	{
		MinedPattern mp1 = (MinedPattern) arg1;
		MinedPattern mp2 = (MinedPattern) arg2;				
		return mp1.compareTo(mp2);
	}
	
	public boolean contains(MinedPattern mp)
	{
		return false;
	}

	public String getMethodId() 
	{
		return methodId;
	}

	public void setMethodId(String methodId) 
	{
		this.methodId = methodId;
	}	
}

