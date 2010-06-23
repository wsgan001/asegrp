package imminer.migrator;

import java.util.Set;

import pw.code.analyzer.holder.Holder;
import imminer.core.MethodSpecificIds;
import imminer.core.PatternType;
import imminer.pattern.ComboPattern;
import imminer.pattern.ItemSet;
import imminer.pattern.MinedPattern;
import imminer.pattern.SinglePattern;

/**
 * Counterpart of ComboPattern
 * @author suresh
 */
public class ADComboPattern extends ADMinedPattern {

	ADMinedPattern left;
	ADMinedPattern right;
	PatternType ptype;
	
	private ADComboPattern(double supportValue)
	{
		super(supportValue);
	}
	
	/**
	 * Creates a ComboPattern using recursive function calls for its
	 * left and right childs
	 * @param cp
	 * @param msi
	 * @return
	 */
	public static ADComboPattern initializeComboPattern(ComboPattern cp, MethodSpecificIds msi)
	{		
		ADComboPattern acp = new ADComboPattern(cp.getSupportValue());
		acp.ptype = cp.getPtype();
		acp.left = initializeChilds(cp.getLeft(), msi);
		acp.right = initializeChilds(cp.getRight(), msi);
		return acp;
	}
	
	private static ADMinedPattern initializeChilds(MinedPattern mp, MethodSpecificIds msi)
	{
		if(mp instanceof SinglePattern)
		{
			return new ADSinglePattern((SinglePattern) mp, msi);
		}
		
		ComboPattern cp = (ComboPattern) mp;
		ADComboPattern acp = new ADComboPattern(cp.getSupportValue());
		acp.ptype = cp.getPtype();
		acp.left = initializeChilds(cp.getLeft(), msi);
		acp.right = initializeChilds(cp.getRight(), msi);
		return acp;
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.left.toString());
		sb.append(ComboPattern.getPatternTypeStr(this.ptype));
		sb.append(this.right.toString());
		sb.append(")");
		return sb.toString();
	}
	
	/**
	 * Computes support by recursively traversing the left and right childs
	 */
	@Override
	public boolean isSupportedBy(Set<Holder> holderSet) {
		
		boolean leftSupport = this.left.isSupportedBy(holderSet);
		boolean rightSupport = this.right.isSupportedBy(holderSet);
		
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
	
	public String getKeyString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append("(");
		sb.append(this.left.getKeyString());
		sb.append(ComboPattern.getPatternTypeStr(this.ptype));
		sb.append(this.right.getKeyString());
		sb.append(")");		
		return sb.toString();
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof ADComboPattern))
			return false;
		
		ADComboPattern cp = (ADComboPattern) other;
		if(!this.ptype.equals(cp.ptype))
			return false;
		
		if(!this.left.equals(cp.left))
			return false;
		
		if(!this.right.equals(cp.right))
			return false;
		
		return true;
	}
}
