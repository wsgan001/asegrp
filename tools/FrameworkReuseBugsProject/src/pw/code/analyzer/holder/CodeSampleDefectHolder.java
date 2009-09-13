package pw.code.analyzer.holder;

import java.util.Comparator;

import pw.common.CommonConstants;

/**
 * A class that holds defects detected in the analyzed code samples.
 * @author suresh_thummalapenta
 *
 */
public class CodeSampleDefectHolder implements Comparator{
	
	String fileName; 			//File name of the code sample that violated
	String methodName;			//Method name inside that code sample
	String violatedAPI;			//External API of the library that is violated
	int violationType; 			//Type of violation like Argument Pattern, Receiver Pattern etc., (Stored as possible constants)
	String violatedCondition;	//The condition variable that provides additional details of violation
	
	double confidenceLevel;	//Stores HIGH_CONFIDENCE, AVERAGE_CONFIDENCE, LOW_CONFIDENCE, NO_CONFIDENCE 
	boolean possibleFalsePositive = false;	//Stores information if this can be a false positive
	double supportOfViolation;		//Every violation is associated with a support value that gives the confidence
									//to decide whether it is a bug or warning etc.,
	int favorableNumber;			//Favorable number of conditions
	double totalRelatedNumber;			//Total number of conditions
	
		
	public CodeSampleDefectHolder()
	{
		
	}
	
	public CodeSampleDefectHolder(String fileName, String methodName, String violatedAPI, int violationType, String violatedCondition) {
		super();
		this.fileName = fileName;
		this.methodName = methodName;
		this.violatedAPI = violatedAPI;
		this.violationType = violationType;
		this.violatedCondition = violatedCondition;
	}

	public String getPrintString() {
		
		StringBuffer printStr = new StringBuffer();
		printStr.append(fileName + "#");
		printStr.append(methodName + "#");
		printStr.append(violatedAPI + "#");
		printStr.append(violationType + "#");
		printStr.append(violatedCondition + "#");
		printStr.append(supportOfViolation + "(" + favorableNumber + "/" + totalRelatedNumber + ")#");
		printStr.append(confidenceLevel + "#");
		printStr.append(possibleFalsePositive);
		return printStr.toString();
	}
	
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getMethodName() {
		return methodName;
	}
	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	public String getViolatedAPI() {
		return violatedAPI;
	}
	public void setViolatedAPI(String violatedAPI) {
		this.violatedAPI = violatedAPI;
	}
	public String getViolatedCondition() {
		return violatedCondition;
	}
	public void setViolatedCondition(String violatedCondition) {
		this.violatedCondition = violatedCondition;
	}
	public int getViolationType() {
		return violationType;
	}
	public void setViolationType(int violationType) {
		this.violationType = violationType;
	}

	public double getSupportOfViolation() {
		return supportOfViolation;
	}

	
	public void setSupportOfViolation(double supportOfViolation) {
		/*if(supportOfViolation == 1.0 && totalRelatedNumber <= CommonConstants.PENALTY_THRESHOLD) {
			supportOfViolation -= CommonConstants.PENALTY_FOR_LOW_SAMPLES;			
		}*/
		this.supportOfViolation = supportOfViolation;
	}

	public double getConfidenceLevel() {
		return confidenceLevel;
	}

	public void setConfidenceLevel(double confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	public boolean isPossibleFalsePositive() {
		return possibleFalsePositive;
	}

	public void setPossibleFalsePositive(boolean possibleFalsePositive) {
		this.possibleFalsePositive = possibleFalsePositive;
	}

	public int getFavorableNumber() {
		return favorableNumber;
	}

	public void setFavorableNumber(int favorableNumber) {
		this.favorableNumber = favorableNumber;
	}

	public double getTotalRelatedNumber() {
		return totalRelatedNumber;
	}

	public void setTotalRelatedNumber(double totalRelatedNumber) {
		this.totalRelatedNumber = totalRelatedNumber;
	}
	
	public int compare(Object arg0, Object arg1) {
		
		if(!(arg0 instanceof CodeSampleDefectHolder) || !(arg1 instanceof CodeSampleDefectHolder))
		{
			return -1;
		}
		
		CodeSampleDefectHolder csdh1 = (CodeSampleDefectHolder)arg0;
		CodeSampleDefectHolder csdh2 = (CodeSampleDefectHolder)arg0;
		
		if(csdh1.confidenceLevel < csdh2.confidenceLevel) {
			return -1;
		} else if(csdh1.confidenceLevel > csdh2.confidenceLevel) {
			return 1;
		}
			
		
		if(csdh1.supportOfViolation > csdh2.supportOfViolation) {
			return -1;
		} else if(csdh1.supportOfViolation < csdh2.supportOfViolation) {
			return 1;
		}
		
		if(csdh1.favorableNumber > csdh2.favorableNumber) {
			return -1;
		} else if(csdh1.favorableNumber < csdh2.favorableNumber) {
			return 1;
		}
				
		return 1;
	}
}
