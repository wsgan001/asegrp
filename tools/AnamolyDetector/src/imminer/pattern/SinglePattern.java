package imminer.pattern;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Represents patterns that includes only one element such as "a" or "b"
 * @author suresh
 *
 */
public class SinglePattern extends MinedPattern {
	private static Logger logger = Logger.getLogger("SinglePattern");	
	
	public SinglePattern(String methodId, ItemSet candidate) 		
	{
		super(methodId, candidate);
		if(candidate.items.size() != 1)
		{
			logger.warn("SinglePattern can contain only single pattern candidate");
		}
	}
	
	public SinglePattern(String methodId, String item)
	{
		super(methodId, item);
	}	
	
	@Override
	public void calculateSupport(List<ItemSet> itemsets) {		
		this.totalItemSets = itemsets.size();
		for(ItemSet is : itemsets)
		{	
			if(this.isSupportedBy(is))
				this.supportingItemSets++;
		}
		
		this.supportValue = (double)this.supportingItemSets / (double)this.totalItemSets;
	}
	
	public boolean isSupportedBy(ItemSet itemset)
	{
		return itemset.containsAll(candidate.getItems());
	}
	
	public String toString()
	{		
		return getKeyString();
	}
	
	public String pettyPrintString()
	{		
		StringBuffer sb = new StringBuffer();
		sb.append(this.candidate.getItems().iterator().next());
		sb.append(" , ");
		sb.append("Support :" + this.supportValue + ", (" + this.supportingItemSets + "/" + this.totalItemSets + ")");
		return sb.toString();
	}
	
	public String getKeyString()
	{
		return (String) this.candidate.getItems().iterator().next();		
	}
}
