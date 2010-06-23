package pw.code.analyzer.holder;

import imminer.core.PatternType;
import imminer.migrator.ADMinedPattern;
import java.util.Comparator;

import pw.common.CommonConstants;

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
	
	//Additional fields added for ASEJournal extension
	public PatternType ptype; 			//includes the ptype that detected this support
	public ADMinedPattern vpattern;		//includes the pattern this recor is violating
			
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
	
	public CodeSampleDefectHolder(String fileName, String methodName, MethodInvocationHolder violatedAPI,
			ADMinedPattern pattern, PatternType ptype)
	{
		super();
		this.fileName = fileName;
		this.methodName = methodName;
		this.violatedAPI = violatedAPI;
		this.vpattern = pattern;
		this.ptype = ptype;
		this.support = pattern.getSupportValue();
	}

	public String toString() {		
		StringBuffer printStr = new StringBuffer();
		printStr.append(fileName + ",");
		printStr.append(methodName + ",");
		printStr.append("\"" + violatedAPI + "\",");
		
		if(!CommonConstants.inputPatternFile.equals("")) 
		{
			printStr.append("\"" + violatedPattern + "\",");
			printStr.append("\"" + violatedHolderObj + "\",");
			printStr.append(support + ",");
			printStr.append(balanced + ",");
			printStr.append(this.violatedPattern.special_balance_flag + ",");
			printStr.append(this.violatedAPI.dominatingSupport + ",");
			printStr.append(this.violatedAPI.getKey());
		}
		else
		{
			printStr.append("\"" + vpattern.getKeyString() + "\",");
			printStr.append("\"" + ptype.toString() + "\",");			
			printStr.append(support + ",");
			
			if(this.violatedAPI.andPatternList.size() > 0) {
				printStr.append("1,");
			} else {
				printStr.append("0,");
			}
			
			if(this.violatedAPI.hasSamePatternFormats)
				printStr.append("0");
			else
				printStr.append("1");
		}
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
		
		//In normal mode, all fields are used for detecting duplicate defects.
		//In minimal mode, only a few fields are used
		if(CommonConstants.DUPLICATE_BUG_MODE == CommonConstants.DUPLICATE_BUG_NORMAL)
		{				
			if(!CommonConstants.inputPatternFile.equals("")) 
			{
				//ASE2009 paper style			
				if(!this.violatedPattern.equals(csdhObj.violatedPattern)) {
					return false;
				}
			}
			else
			{				
				//ASEJournal paper style
				if(this.ptype != csdhObj.ptype)
					return false;
				
				if(!this.vpattern.equals(csdhObj.vpattern))
					return false;
			}
		}
		
		return true;
	}
	
	public int hashCode() {
		int hashValue = 0;		
		hashValue += violatedAPI.getMethodName().hashCode();
		hashValue += fileName.hashCode();
		hashValue += methodName.hashCode();		
		return hashValue;
	}

	public PatternType getPtype() {
		return ptype;
	}
}