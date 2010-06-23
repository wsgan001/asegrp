package imminer.migrator;



import java.util.Set;

import imminer.core.MethodSpecificIds;
import imminer.pattern.SinglePattern;

import org.apache.log4j.Logger;

import pw.code.analyzer.holder.CondVarHolder_Typeholder;
import pw.code.analyzer.holder.Holder;

public class ADSinglePattern extends ADMinedPattern {
	private static Logger logger = Logger.getLogger("ADSinglePattern");
	CondVarHolder_Typeholder cvhitem;
	
	/**
	 * Creates a single pattern from the single pattern from IMMiner
	 * @param sp
	 */
	public ADSinglePattern(SinglePattern sp, MethodSpecificIds msi)
	{
		super(sp.getSupportValue());		
		String conditionChk = msi.getConditionCheck(sp.getKeyString());
		this.cvhitem = this.getCondVarHolder(conditionChk);			
		this.cvhitem.cvhObj.setPosition(this.getConditionCheckPosition(sp.getKeyString()));
		if(this.cvhitem.cvhObj.getCondType() == -1)
		{
			logger.error("Error occurred while parsing condition check back " + conditionChk);
		}
	}	
	
	public String toString()
	{
		return cvhitem.toString();
	}
	
	public boolean isSupportedBy(Set<Holder> holderSet)
	{
		return holderSet.contains(cvhitem);
	}
	
	public String getKeyString()
	{
		return (String) this.cvhitem.toString();		
	}
	
	public boolean equals(Object obj)
	{
		if(!(obj instanceof ADSinglePattern))
			return false;
		
		ADSinglePattern adp = (ADSinglePattern) obj;
		return this.cvhitem.equals(adp.cvhitem);
	}
}
