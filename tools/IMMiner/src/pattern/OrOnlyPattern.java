package pattern;

import java.util.List;

import org.apache.log4j.Logger;

/**
 * Represents patterns that are of the form "a || b || c"
 * @author suresh
 *
 */
public class OrOnlyPattern extends MinedPattern {
	private static Logger logger = Logger.getLogger("OrPattern");	
	public OrOnlyPattern(ItemSet candidate) 		
	{
		super(candidate);		
	}
	
	public boolean isSupportedBy(ItemSet itemset)
	{
		return itemset.containsAtleastOne(candidate.getItems());
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for(String item : this.candidate.getItems())
		{
			sb.append(item + " || ");
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
			sb.append(item + " || ");
		}	
		return sb.toString();
	}
}
