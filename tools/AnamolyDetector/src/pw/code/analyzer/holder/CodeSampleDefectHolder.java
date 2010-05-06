package pw.code.analyzer.holder;

import java.util.Comparator;

/**
 * A class that holds defects detected in the analyzed code samples.
 * @author suresh_thummalapenta
 *
 */
public class CodeSampleDefectHolder implements Comparator{
	
	public String fileName; 			//File name of the code sample that violated
	public String methodName;			//Method name inside that code sample
	public MethodInvocationHolder violatedAPI;			//External API of the library that is violated
	public PrePostPathHolder violatedPattern;	//Actual violating holder
	public Holder violatedHolderObj;					//Stores the reason for this defect
	
	public double support;				//Support value of the pattern
	public int balanced;				//Is this a balanced pattern?
			
	public CodeSampleDefectHolder() {
		
	}
	
	public CodeSampleDefectHolder(String fileName, String methodName, MethodInvocationHolder violatedAPI, 
			PrePostPathHolder violatedPattern, Holder violatedHolderObj) {
		super();
		this.fileName = fileName;
		this.methodName = methodName;
		this.violatedAPI = violatedAPI;
		this.violatedPattern = violatedPattern;
		this.violatedHolderObj = violatedHolderObj;		
		this.support = this.violatedPattern.globalSupport;
		this.balanced = this.violatedPattern.balanced;
	}

	public String toString() {		
		StringBuffer printStr = new StringBuffer();
		printStr.append(fileName + ",");
		printStr.append(methodName + ",");
		printStr.append("\"" + violatedAPI + "\",");
		printStr.append("\"" + violatedPattern + "\",");
		printStr.append("\"" + violatedHolderObj + "\",");
		printStr.append(support + ",");
		printStr.append(balanced + ",");
		printStr.append(this.violatedPattern.special_balance_flag + ",");
		printStr.append(this.violatedAPI.dominatingSupport + ",");
		printStr.append(this.violatedAPI.getKey());
		return printStr.toString();
	}
	
	public int compare(Object arg0, Object arg1) {		
		if(!(arg0 instanceof CodeSampleDefectHolder) || !(arg1 instanceof CodeSampleDefectHolder))
		{
			return -1;
		}
		
		CodeSampleDefectHolder csdh1 = (CodeSampleDefectHolder)arg0;
		CodeSampleDefectHolder csdh2 = (CodeSampleDefectHolder)arg0;
		
		if(csdh1.support < csdh2.support) {
			return -1;
		} else if(csdh1.support > csdh2.support) {
			return 1;
		}
			
		return 1;	
	}
	
	public boolean equals(Object arg0) {		
		if(!(arg0 instanceof CodeSampleDefectHolder)) {
			return false;
		}
		
		CodeSampleDefectHolder csdhObj = (CodeSampleDefectHolder) arg0;		
		if(!this.fileName.equals(csdhObj.fileName) || !this.methodName.equals(csdhObj.methodName)) {
			return false;
		}
				
		if(!this.violatedAPI.equals(csdhObj.violatedAPI)) {
			return false;
		}
		
		if(!this.violatedPattern.equals(csdhObj.violatedPattern)) {
			return false;
		}
		
		return true;
	}
	
	public int hashCode() {
		char violatedAPIStr[] = violatedAPI.getMethodName().toCharArray();
		int hashValue = 0;
		
		for(int tcnt = 0; tcnt < violatedAPIStr.length; tcnt++) {
			hashValue += violatedAPIStr[tcnt];
		}
		
		char fileNameArr[] = fileName.toCharArray();
		for(int tcnt = 0; tcnt < fileNameArr.length; tcnt++) {
			hashValue += fileNameArr[tcnt];		
		}
		
		char methodNameArr[] = methodName.toCharArray();
		for(int tcnt = 0; tcnt < methodNameArr.length; tcnt++) {
			hashValue += methodNameArr[tcnt];		
		}
		
		return hashValue;
	}
}