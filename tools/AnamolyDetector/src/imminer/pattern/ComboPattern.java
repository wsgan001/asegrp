package imminer.pattern;

import imminer.core.PatternType;

/**
 * A pattern that represents combinations of patterns such as (a || (b && c))
 * @author suresh
 *
 */
public class ComboPattern extends MinedPattern {

	private MinedPattern left;
	private MinedPattern right;
	private PatternType ptype;
	
	public ComboPattern(String methodId, MinedPattern left, MinedPattern right, PatternType ptype)
	{
		super(methodId);
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
		if(this.ptype == PatternType.OR_PATTERN)
		{
			//TODO: An improvement can be done here since the rightSupport is not
			//required when the leftSupport is already true for the OR pattern
			bresult = leftSupport | rightSupport;
		} else if(this.ptype == PatternType.AND_PATTERN)
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
		if(this.ptype == PatternType.OR_PATTERN)
		{
			sb.append(" || ");
		} else if(this.ptype == PatternType.AND_PATTERN)
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
		return this.getKeyString();
	}
	
	public String pettyPrintString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.left.toString());
		sb.append(ComboPattern.getPatternTypeStr(this.ptype));		
		sb.append(this.right.toString());
		sb.append(")");
		sb.append(" , ");
		sb.append("Support :" + this.supportValue + ", (" + this.supportingItemSets + "/" + this.totalItemSets + ")");
		return sb.toString();
	}	
	
	public static String getPatternTypeStr(PatternType ptype)
	{
		if(ptype == PatternType.OR_PATTERN)
		{
			return " || ";
		} else if(ptype == PatternType.AND_PATTERN)
		{
			return " && ";
		} else
		{
			return " ^^ ";
		}
		
	}
	/**
	 * Returns true if "cp" is contained in the current ComboPattern
	 * @param cp
	 * @return
	 */
	public boolean contains(MinedPattern cp)
	{
		if(this.equals(cp))
			return true;
		
		if(this.left.equals(cp))
			return true;
		
		if(this.right.equals(cp))
			return true;
		
		boolean bLeftContains = this.left.contains(cp);
		if(bLeftContains)
			return true;
		
		return this.right.contains(cp);		
	}

	public MinedPattern getLeft() {
		return left;
	}

	public PatternType getPtype() {
		return ptype;
	}

	public MinedPattern getRight() {
		return right;
	}
}
