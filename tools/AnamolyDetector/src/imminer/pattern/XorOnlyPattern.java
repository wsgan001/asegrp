package imminer.pattern;

/**
 * Represents patterns that are of the Xor only form such as "a ^^ b ^^ c"
 * @author suresh
 *
 */
public class XorOnlyPattern extends MinedPattern {
		
	public XorOnlyPattern(String methodId, ItemSet candidate) 		
	{		
		super(methodId, candidate);		
	}
	
	public boolean isSupportedBy(ItemSet itemset)
	{
		int resultVal = 0;
		for(String citem : candidate.getItems())
		{
			if(itemset.contains(citem))
				resultVal ^= 1;
			else
				resultVal ^= 0;
		}
		
		if(resultVal == 1)
			return true;
		else
			return false;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for(String item : this.candidate.getItems())
		{
			sb.append(item + " ^^ ");
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
			sb.append(item + " ^^ ");
		}		
		return sb.toString();
	}
}
