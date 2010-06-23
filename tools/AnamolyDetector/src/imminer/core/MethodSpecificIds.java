package imminer.core;

import java.util.*;

/**
 * In the miner data, across methods, there can be different IDs for
 * the same method names
 * @author suresh
 */
public class MethodSpecificIds {
	Map<String, String> conditionCheckMapper = new HashMap<String, String>();
	
	public void addMethodSpecificID(String methodId, String conditionChk)
	{
		//replace "#MULTICURRTYPES#" with "MULTICURRTYPES"
		//replace "#UNKNOWN#" with "UNKNOWN"
		conditionChk = conditionChk.replace("#MULTICURRTYPES#", "MULTICURRTYPES");
		conditionChk = conditionChk.replace("#UNKNOWN#", "UNKNOWN");		
		this.conditionCheckMapper.put(methodId, conditionChk);
	}
	
	public String getConditionCheck(String methodId)
	{
		return this.conditionCheckMapper.get(methodId);
	}
}
