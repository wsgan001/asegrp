package imminer.pattern;


/**
 * Holds patterns of the form "a && b && c" 
 * @author suresh
 *
 */
public class AndOnlyPattern extends MinedPattern {
	
	public AndOnlyPattern(String methodId, ItemSet candidate) 		
	{
		super(methodId, candidate);		
	}
	
	public boolean isSupportedBy(ItemSet itemset)
	{
		return itemset.containsAll(candidate.getItems());
	}
	
	public String toString()
	{
		return this.getKeyString();
	}
	
	public String pettyPrintString()
	{
		StringBuffer sb = new StringBuffer();
		for(String item : this.candidate.getItems())
		{
			sb.append(item + " && ");
		}		
		sb.append(" , ");
		sb.append("Support :" + this.supportValue + ", (" + this.supportingItemSets + "/" + this.totalItemSets + ")");
		return sb.toString();
	}
	
	public String getKeyString()
	{
		StringBuffer sb = new StringBuffer();
		for(String item : this.candidate.getItems())
		{
			sb.append(item + " && ");
		}
		return sb.toString();
	}
	
	public boolean contains(MinedPattern mp)
	{
		return this.candidate.containsAll(mp.candidate.getItems());
	}
}
