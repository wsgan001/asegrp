package imminer.migrator;

import imminer.core.MethodSpecificIds;
import imminer.pattern.AndOnlyPattern;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import pw.code.analyzer.holder.CondVarHolder_Typeholder;
import pw.code.analyzer.holder.Holder;

public class ADAndPattern extends ADMinedPattern {

	List<Holder> condList = new ArrayList<Holder>();
	
	/**
	 * Creates a single pattern from the single pattern from IMMiner
	 * @param sp
	 */
	public ADAndPattern(AndOnlyPattern ap, MethodSpecificIds msi)
	{
		super(ap.getSupportValue());
		for(String cid : ap.getCandidate().getItems())
		{		
			String conditionChk = msi.getConditionCheck(cid);
			CondVarHolder_Typeholder cvhitem = this.getCondVarHolder(conditionChk);			
			cvhitem.cvhObj.setPosition(this.getConditionCheckPosition(cid));
			this.condList.add(cvhitem);			
		}
	}
	
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		for(Holder cvh : condList)
		{
			sb.append(cvh.toString() + " && ");
		}
		return sb.toString();
	}
	
	public boolean isSupportedBy(Set<Holder> holderSet)
	{
		return holderSet.containsAll(condList);
	}
	
	public String getKeyString()
	{
		StringBuffer sb = new StringBuffer();
		for(Holder item : this.condList)
		{
			sb.append(item.toString() + " && ");
		}
		return sb.toString();
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof ADAndPattern))
			return false;
		
		ADAndPattern ap = (ADAndPattern) other;	
		return this.condList.containsAll(ap.condList) &&
				ap.condList.containsAll(this.condList);
	}
}
