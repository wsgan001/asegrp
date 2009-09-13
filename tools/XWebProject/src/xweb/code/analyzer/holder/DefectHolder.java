package xweb.code.analyzer.holder;

import java.util.Comparator;

import xweb.code.analyzer.MinedPattern;
import xweb.common.CommonConstants;

public class DefectHolder implements Comparator {
	MinedPattern mpObj;
	MethodInvocationHolder violatedMIH;	
	String codeSampleName;
	String methodName;
	String associatedErrorPath;
	
	boolean lowPreference = false;	//A variable that indicates that low preference
							//can be given to this defect as there is a throws clause included for the enclosing
							//method declaration

	public DefectHolder(MinedPattern mpObj, MethodInvocationHolder violatedMIH, String codeSampleName, String methodName) {
		super();
		this.mpObj = mpObj;
		this.violatedMIH = violatedMIH;
		this.codeSampleName = codeSampleName;
		this.methodName = methodName;
	}	
	
	public DefectHolder() {}

	public int compare(Object arg0, Object arg1) {
		
		MinedPattern m1Obj = ((DefectHolder) arg0).mpObj;
		MinedPattern m2Obj = ((DefectHolder) arg1).mpObj;
		
		if(!CommonConstants.bEnableAssocMiner) {
			if(m1Obj.getSupport_category() < m2Obj.getSupport_category()) {
				return -1;
			} else if(m1Obj.getSupport_category() > m2Obj.getSupport_category()) {
				return 1;
			}
		
			if(m1Obj.getSupport() > m2Obj.getSupport())
				return -1;
			else if(m1Obj.getSupport() < m2Obj.getSupport())
				return 1;
			
			if(m1Obj.getChildMIDFrequency() > m2Obj.getChildMIDFrequency())
				return -1;
			else if(m1Obj.getChildMIDFrequency() < m2Obj.getChildMIDFrequency())
				return 1;
		} else {
			if(m1Obj.getChildMIDFrequency() > m2Obj.getChildMIDFrequency())
				return -1;
			else if(m1Obj.getChildMIDFrequency() < m2Obj.getChildMIDFrequency())
				return 1;
			
			if(m1Obj.getSupport() > m2Obj.getSupport())
				return -1;
			else if(m1Obj.getSupport() < m2Obj.getSupport())
				return 1;
			
		}
		if(m1Obj.getParentMID().getNoErrorPathCount() < m2Obj.getParentMID().getNoErrorPathCount())
			return -1;
		else if(m1Obj.getParentMID().getNoErrorPathCount() > m2Obj.getParentMID().getNoErrorPathCount())
			return 1;
		
		return -1;
	}
	
	
	public String getCodeSampleName() {
		return codeSampleName;
	}
	public void setCodeSampleName(String codeSampleName) {
		this.codeSampleName = codeSampleName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public MinedPattern getMpObj() {
		return mpObj;
	}
	public void setMpObj(MinedPattern mpObj) {
		this.mpObj = mpObj;
	}
	public MethodInvocationHolder getViolatedMIH() {
		return violatedMIH;
	}
	public void setViolatedMIH(MethodInvocationHolder violatedMIH) {
		this.violatedMIH = violatedMIH;
	}
	public boolean isLowPreference() {
		return lowPreference;
	}
	public void setLowPreference(boolean lowPreference) {
		this.lowPreference = lowPreference;
	}

	public String getAssociatedErrorPath() {
		return associatedErrorPath;
	}

	public void setAssociatedErrorPath(String associatedErrorPath) {
		this.associatedErrorPath = associatedErrorPath;
	}
	
	public boolean equals(Object obj) {
		DefectHolder dchObj = (DefectHolder) obj;
		
		if(!this.codeSampleName.equals(dchObj.codeSampleName) || !this.methodName.equals(dchObj.methodName))
			return false;		

		//NOTE: Here objects are compared with != intentionally instead of equals method.
		if(this.violatedMIH != dchObj.violatedMIH)
			return false;
		
		if(this.mpObj != dchObj.mpObj)
			return false;
		
		return true;	
	}
}
