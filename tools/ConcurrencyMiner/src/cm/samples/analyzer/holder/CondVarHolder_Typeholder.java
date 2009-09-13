package cm.samples.analyzer.holder;

import cm.samples.analyzer.ASTCrawlerUtil;
import cm.samples.analyzer.TypeHolder;

/**
 * 
 * @author suresh_thummalapenta
 * A class that stores both the condition variables and the associated variable
 * on which the condition is applicable
 */
public class CondVarHolder_Typeholder extends Holder {
	
	public int key = 0;
	public CondVarHolder cvhObj;
	public TypeHolder thObj;	
	
	public boolean equals(Object inpObj){
		if(!(inpObj instanceof CondVarHolder_Typeholder))
			return false;
		
		CondVarHolder_Typeholder inpCvhObj = (CondVarHolder_Typeholder) inpObj;
		
		if(!this.cvhObj.getAssociatedMIH().equals(inpCvhObj.cvhObj.getAssociatedMIH())) {
			return false;
		}		
		
		if(!this.cvhObj.equals(inpCvhObj.cvhObj)) {
			return false;
		}	
		
		if(this.thObj.getElemType() != inpCvhObj.thObj.getElemType()) {
			return false;
		}
		
		return true;
	}
	
	public String toString()
	{
		String retString = ((cvhObj.getAssociatedMIH() != null)? cvhObj.getAssociatedMIH().toString() : "TOFIX") 
		+ "#" + ASTCrawlerUtil.getElementTypeStr(thObj.getElemType()) 
		+ "#" + ASTCrawlerUtil.getConditionTypeStr(cvhObj.getCondType())
		+ "#" + cvhObj.getAdditionalInfo();
		 
		return retString;
	}
	
	//Input strings are of the form "java.util.Stack:push(java.lang.Integer)#ARGUMENT_PATTERNS#NULL_CHECK#"
	static public CondVarHolder_Typeholder parseFromString(String inpStr) {
		
		String[] inpParts = inpStr.split("#");
		
		//Gathering the method invocation from "java.util.Stack:push(java.lang.Integer::)"
		String mihStr = inpParts[0];
		int firstColon = mihStr.indexOf(':');
		String receiverObj = mihStr.substring(0, firstColon);
		String argumentStr = mihStr.substring(firstColon + 1);
		argumentStr = argumentStr.replaceAll("::", ":");
		argumentStr = argumentStr.replaceAll(":", ",");
		
		MethodInvocationHolder associatedMIH = MethodInvocationHolder.parseFromString(argumentStr, new TypeHolder(receiverObj));		
		
		TypeHolder thObj = new TypeHolder();
		thObj.setElemType(ASTCrawlerUtil.getElementTypeID(inpParts[1]));
		
		CondVarHolder cvhObj = new CondVarHolder();
		cvhObj.setAssociatedMIH(associatedMIH);
		
		int condType = ASTCrawlerUtil.getConditionTypeID(inpParts[2]);
		cvhObj.setCondType(condType);
		
		if(inpParts.length == 4 && (condType == CondVarHolder.CONSTANT_EQUALITY_CHECK || 
				condType == CondVarHolder.METHODARG_CONSTANT_EQUALITY_CHECK || 
				condType == CondVarHolder.TRUE_FALSE_CHECK || 
				condType == CondVarHolder.METHODARG_TRUE_FALSE_CHECK || 
				condType == CondVarHolder.INSTANCEARG_TRUE_FALSE_CHECK)) {
			cvhObj.setConstValue(inpParts[3]);
		}	
		
		CondVarHolder_Typeholder cvh_thObj = new CondVarHolder_Typeholder();
		cvh_thObj.cvhObj = cvhObj;
		cvh_thObj.thObj = thObj;
		return cvh_thObj;
	}
	
	public int getKey()
	{
		return key;
	}
}
