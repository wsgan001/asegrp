package imminer.migrator;

import java.util.Set;

import pw.code.analyzer.holder.CondVarHolder_Typeholder;
import pw.code.analyzer.holder.Holder;

public abstract class ADMinedPattern {
	
	double supportValue;
	
	/**
	 * Creates a mined pattern from its counterpart from IMMiner project
	 * @param mp
	 */
	public ADMinedPattern(double supportValue)
	{
		this.supportValue = supportValue;
	}
	
	/**
	 * Gets an instance of CondVarHolder for a given condition check in the
	 * form of string
	 */
	public CondVarHolder_Typeholder getCondVarHolder(String cvhString)
	{
		CondVarHolder_Typeholder cvh_thObj = CondVarHolder_Typeholder.parseFromString(cvhString);
		return cvh_thObj;
	}
	
	/**
	 * Gets the position of the condition from its ID. In general,
	 * the ID is in the form of 19.4.0, where the third part indicates the position
	 * @return
	 */
	protected int getConditionCheckPosition(String conditionChkId)
	{
		String[] elem = conditionChkId.replace(".", ":").split(":");
		if(elem != null && elem.length == 3)
			return Integer.parseInt(elem[2]);
		else
			return -1;
	}
	
	/**
	 * Returns true if this pattern is supported by the holder
	 * @param holderSet
	 * @return
	 */
	public abstract boolean isSupportedBy(Set<Holder> holderSet);	
	
	/**
	 * Returns the simple keystring that can be used as key. 
	 * This is a simplified version of the actual toString implementation
	 * @return
	 */
	public abstract String getKeyString();
	
	public double getSupportValue()
	{
		return this.supportValue;
	}
}
