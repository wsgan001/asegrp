package pattern;

import imminer.core.PATTERN_TYPE;

/**
 * A pattern that represents combinations of patterns such as (a || (b && c))
 * @author suresh
 *
 */
public class ComboPattern extends MinedPattern {

	private MinedPattern left;
	private MinedPattern right;
	private PATTERN_TYPE ptype;
	
	public ComboPattern(MinedPattern left, MinedPattern right, PATTERN_TYPE ptype)
	{
		this.left = left;
		this.right = right;
		this.ptype = ptype;
	}

	/**
	 * Computes support by recursively traversing the left and right childs
	 */
	@Override
	public boolean isSupportedBy(ItemSet itemset) {
		
		boolean leftSupport = this.left.isSupportedBy(itemset);
		boolean rightSupport = this.right.isSupportedBy(itemset);
		
		boolean bresult;
		if(this.ptype == PATTERN_TYPE.OR_PATTERN)
		{
			//TODO: An improvement can be done here since the rightSupport is not
			//required when the leftSupport is already true for the OR pattern
			bresult = leftSupport | rightSupport;
		} else if(this.ptype == PATTERN_TYPE.AND_PATTERN)
		{
			bresult = leftSupport & rightSupport;
		} else
		{
			bresult = leftSupport ^ rightSupport;
		}
					
		return bresult;
	}
	
	public int hashCode()
	{
		return this.left.hashCode() + this.right.hashCode();
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof ComboPattern))
			return false;
		
		ComboPattern cp = (ComboPattern) other;
		if(!this.ptype.equals(cp.ptype))
			return false;
		
		if(!this.left.equals(cp.left))
			return false;
		
		if(!this.right.equals(cp.right))
			return false;
		
		return true;
	}
	
	public String getKeyString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.left.getKeyString());
		if(this.ptype == PATTERN_TYPE.OR_PATTERN)
		{
			sb.append(" || ");
		} else if(this.ptype == PATTERN_TYPE.AND_PATTERN)
		{
			sb.append(" && ");
		} else
		{
			sb.append(" ^^ ");
		}
		
		sb.append(this.right.getKeyString());
		sb.append(")");		
		return sb.toString();
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.left.toString());
		if(this.ptype == PATTERN_TYPE.OR_PATTERN)
		{
			sb.append(" || ");
		} else if(this.ptype == PATTERN_TYPE.AND_PATTERN)
		{
			sb.append(" && ");
		} else
		{
			sb.append(" ^^ ");
		}
		
		sb.append(this.right.toString());
		sb.append(")");
		sb.append(" , ");
		sb.append("Support :" + this.supportValue + ", (" + this.supportingItemSets + "/" + this.totalItemSets + ")");
		return sb.toString();
	}	

	public MinedPattern getLeft() {
		return left;
	}

	public PATTERN_TYPE getPtype() {
		return ptype;
	}

	public MinedPattern getRight() {
		return right;
	}
}
