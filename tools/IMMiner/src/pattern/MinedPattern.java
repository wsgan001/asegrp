package pattern;

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
	
	/**
	 * A constructor intended for use by Combo pattern
	 * since the itemset is not precisely defined
	 */
	public MinedPattern()
	{
		
	}
	
	/**
	 * Constructor that helps create mined pattern with single item
	 * @param candidate
	 */
	public MinedPattern(String candidate)
	{
		this.candidate = new ItemSet(candidate);
	}
	
	/**
	 * Constructor that helps create mined pattern with multiple candidate
	 * items
	 * @param candidate
	 */
	public MinedPattern(ItemSet candidate)
	{
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
	
	public int compareTo(Object other) {
		MinedPattern otherp = (MinedPattern) other;
		if(this.supportValue < otherp.supportValue)
			return -1;
		
		if(this.supportValue > otherp.supportValue)
			return 1;
		
		return 0;
	}
	
	public int compare(Object arg1, Object arg2)
	{
		MinedPattern mp1 = (MinedPattern) arg1;
		MinedPattern mp2 = (MinedPattern) arg2;
				
		return mp1.compareTo(mp2);
	}
	
	
}

