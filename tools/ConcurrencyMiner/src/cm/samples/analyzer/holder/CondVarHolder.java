package cm.samples.analyzer.holder;


import java.util.Comparator;

//import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;
import org.eclipse.jdt.core.dom.MethodInvocation;

import cm.common.CommonConstants;


/**
 * A holder for conditional variable.
 * @author suresh_thummalapenta
 *
 */
public class CondVarHolder implements Comparator{
	//private static Logger logger = Logger.getLogger("CondVarHolder");
	
	protected String varName = "";	//Name of the variable participated in the conditional expression
	protected int condType = -1;	//Describes what kind of check is performed w.r.t to the other operand in the condition
	
	//Additional info variables
	private String constValue = "";		//Stores the value of the constant if the condType == CONSTANT_EQUALITY_CHECK or METHODARG_CONSTANT_EQUALITY_CHECK
										//Stores the boolean value if condType == TRUE_FALSE_CHECK or METHODARG_TRUE_FALSE_CHECK
										//Stores the otherType if condType == INSTANCEARG_TRUE_FALSE_CHECK
	private MethodInvocation otherMIiObj; //Stores the other method invocation if condType = RETVAL_EQUALITY_CHECK or METHODARG_RETVAL_EQUALITY_CHECK
	private MethodInvocationHolder associatedMIH = null; //If a method invocation is involved in the condition expression, it is stored in this.	
	private String associatedVarName = "";	//Used as a temporary holder for holding the associated variable name 
	private int frequency = 1;					//Stores the number of times the condition is detected during analysis
	private double support = 0.0;				//Stores the support value of this condition among overall conditions. 
												//Conditions below a certain threshold value are ignored.
	private double totalRelatedFrequency = 0;
	private Operator operator; 					//Operator involved in the condition. Currently this is only used for NULL comparisons and constant equality checks
	private boolean bPromotedSupport = false;	//Due to some heuristics like primitive return value, some condition patterns support is promoted to make them HIGH_CONFIDENCE
		
	private String codeSampleFileName = "";
	private String codeSampleMethodName = "";
		
	public CondVarHolder () {
	}
	
	public CondVarHolder(String varName, int condType) {
		this.varName = varName;
		this.condType = condType;
	}
	
	public String toString() {
		String retStr = "";
		retStr += varName;
		retStr += ":" + condType;
		
		if(condType == CONSTANT_EQUALITY_CHECK || condType == METHODARG_CONSTANT_EQUALITY_CHECK || 
				condType == INSTANCEARG_TRUE_FALSE_CHECK) {
			
			boolean bisLiteral = false;
			try {
				if(!this.constValue.equals(""))
					Integer.parseInt(this.constValue);
				bisLiteral = true;
			} catch (Exception ex) {
				
			}
			
			if(!bisLiteral)
				retStr += ":" + constValue;
		} 
		
		if (condType == RETVAL_EQUALITY_CHECK || condType == METHODARG_RETVAL_EQUALITY_CHECK) {
			retStr += ":" + otherMIiObj;
		}
		
		if(associatedMIH != null) {
			retStr += ":" + associatedMIH.getReceiverClass() + "," + associatedMIH.getMethodName();			
		}
		
		return retStr;
	}
		
	//Function to return the additional information such as constant
	//or class type in instance check or so.
	public String getAdditionalInfo() {
		if(condType == CONSTANT_EQUALITY_CHECK || condType == METHODARG_CONSTANT_EQUALITY_CHECK || 
				condType == TRUE_FALSE_CHECK || condType == METHODARG_TRUE_FALSE_CHECK || condType == INSTANCEARG_TRUE_FALSE_CHECK) {
			return constValue;
		} 		
		return "";		
	}
	
	public String printString() {
		String retStr = varName + ":" + condType;
		
		if(condType == CONSTANT_EQUALITY_CHECK || condType == METHODARG_CONSTANT_EQUALITY_CHECK || 
				condType == TRUE_FALSE_CHECK || condType == METHODARG_TRUE_FALSE_CHECK || condType == INSTANCEARG_TRUE_FALSE_CHECK) {
			
			
			
			retStr += ":" + constValue;
		} 
		
		if ((condType == RETVAL_EQUALITY_CHECK || condType == METHODARG_RETVAL_EQUALITY_CHECK)) {
			retStr += ":" + otherMIiObj;
		}
		
		if(associatedMIH != null) {
			retStr += ":" + associatedMIH.getReceiverClass() + "," + associatedMIH.getMethodName();			
		}
		
		retStr += "   Occurrences:" + frequency;
		retStr += "   Support:" + support;
		if(condType == NULL_CHECK || condType == METHODARG_NULL_CHECK || condType == CONSTANT_EQUALITY_CHECK
				|| condType == METHODARG_CONSTANT_EQUALITY_CHECK) {
			retStr += "   Operator:" + operator;
		}
		
		return retStr;
	}
	
	
	public String getVarName() {
		return varName;
	}

	public void setVarName(String varName) {
		this.varName = varName;
	}

	public int getCondType() {
		return condType;
	}

	public void setCondType(int condType) {
		this.condType = condType;
	}	
	
	public String getConstValue() {
		return constValue;
	}

	public void setConstValue(String constValue) {
		this.constValue = constValue;
	}

	public MethodInvocation getOtherMIiObj() {
		return otherMIiObj;
	}

	public void setOtherMIiObj(MethodInvocation otherMIiObj) {
		this.otherMIiObj = otherMIiObj;
	}
		
	/** Static Conditional Constants Used **/
	public static final int GEN_EQUALITY_CHECK = 0; //Handles all kinds of equality checks like ==, <=, >=, >, < whose constants are other than NULLs.
	
	public static final int NULL_CHECK = 1; //Handles == NULL or != NULL    
    public static final int TRUE_FALSE_CHECK = 2; //Handles verification checks with true or false
    public static final int CONSTANT_EQUALITY_CHECK = 3; //A specific case of equality check where the other operand is a constant
    public static final int RETVAL_EQUALITY_CHECK = 4; //An equality check with the return value of another method
    public static final int CLASS_EQUIVALENCE_CHECK = 5;	//For comparisons like if ((method.getReturnType() == double.class))
    
    public static final int METHODARG = 10;
    public static final int METHODARG_NULL_CHECK = 11; //Null check through a method invocation like for variable 'x', if(m1(x) != null)
    public static final int METHODARG_TRUE_FALSE_CHECK = 12; //True false check done as a part of method invocation
    public static final int METHODARG_CONSTANT_EQUALITY_CHECK = 13; //A constant equality check as a part of method argument
    public static final int METHODARG_RETVAL_EQUALITY_CHECK = 14; //For 'x', this type includes the case if(m1(x) == m2(x))
    public static final int METHODARG_CLASS_EQUIVALENCE_CHECK = 15; //For 'x' comparisons like if(st.type(x) = Stack.class)
    
    public static final int INSTANCEARG_TRUE_FALSE_CHECK = 20;	//For 'x', this type checks the case if(x instanceof SomeType)

    public static final int PRECEDING_METHOD_CALL_CHECK = 21; 
    			//Stores whether any method call precedes the given call with a condition like if(!stack.empty()) stack.pop()

    public static final int MOVE_TO_METHODARG = 10; //A constant that moves the type checks from normal to method arguments.
    
	public MethodInvocationHolder getAssociatedMIH() {
		return associatedMIH;
	}

	public void setAssociatedMIH(MethodInvocationHolder associatedMIH) {
		this.associatedMIH = associatedMIH;
	}
	
	//Two conditions are said to be equal if the variable name, type of conditional check and 
	//the associated information are same. (During equality check, associated MIH is also very important)
	public boolean equals(Object otherObj)
	{
		if(!(otherObj instanceof CondVarHolder)) 
			return false;
		
		CondVarHolder cvh = (CondVarHolder) otherObj;
		if(this.condType != cvh.condType)
			return false;	
		
		//If both variable names exist, both should be equal
		//if(!(this.varName.equals("")) && !(cvh.varName.equals("")) && !(this.varName.equals(cvh.varName))) {
		//	return false;
		//}
				
		//Append other MIs incase of retval or MethodRetval check
		if(!(this.otherMIiObj == null) && !(cvh.otherMIiObj == null) && !(this.otherMIiObj.equals(cvh.otherMIiObj))) {
			return false;
		}
		
		//An additional check is needed incase of some condition types
		if(condType == METHODARG_CONSTANT_EQUALITY_CHECK || condType == METHODARG_TRUE_FALSE_CHECK 
				|| condType == METHODARG_RETVAL_EQUALITY_CHECK || condType == METHODARG_NULL_CHECK || condType == METHODARG) {
			if(this.associatedMIH == null || cvh.associatedMIH == null)
				return false;
			
			if(!this.associatedMIH.equals(cvh.associatedMIH)) 
				return false;		
		}

		if(condType == INSTANCEARG_TRUE_FALSE_CHECK) {
			if(!this.constValue.equals(cvh.constValue)) {
				return false;
			}
		}
		
		//In case of equality checks constants must be same
		if(condType == CONSTANT_EQUALITY_CHECK || condType == METHODARG_CONSTANT_EQUALITY_CHECK) {
			//Heuristic: If both the constants are numbers, then treat them as same.
			//As constant comparison can be done many places.
			boolean bBothAreLiterals = false;
			try {
				if(!this.constValue.equals(""))
					Integer.parseInt(this.constValue);
				if(!cvh.constValue.equals(""))
					Integer.parseInt(cvh.constValue);
				bBothAreLiterals = true;
			} catch (Exception ex) {
				
			}
			
			if(!bBothAreLiterals && !this.constValue.equals(cvh.constValue)) {
				return false;
			}
		}
		
		return true;
	}
	
	public int hashCode()
	{
		int hashVal = 0;
		if(varName == null || varName.equals("")) {
			return 0;
		}		

		for(char ch : varName.toCharArray()) {
			hashVal += ch;
		}
		return hashVal;
	}

	public String getAssociatedVarName() {
		return associatedVarName;
	}

	public void setAssociatedVarName(String associatedVarName) {
		this.associatedVarName = associatedVarName;
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}	
	
	public void incrFrequency() {
		this.frequency++;
	}
	
	/*
	 * Clone function for creating a copy of the CondVarHolder object
	 */
	public CondVarHolder clone() {
		CondVarHolder cvhClone = new CondVarHolder();
		cvhClone.varName = this.varName;
		cvhClone.condType = this.condType;
		cvhClone.constValue = this.constValue;
		cvhClone.otherMIiObj = this.otherMIiObj;
		cvhClone.associatedMIH = this.associatedMIH;
		cvhClone.associatedVarName = this.associatedVarName;
		cvhClone.frequency = this.frequency;
		cvhClone.operator = this.operator;
		cvhClone.support = this.support;
		cvhClone.totalRelatedFrequency = this.totalRelatedFrequency;
		cvhClone.bPromotedSupport = this.bPromotedSupport;
		cvhClone.codeSampleMethodName = this.codeSampleMethodName;
		cvhClone.codeSampleFileName = this.codeSampleFileName;
		return cvhClone;
	}

	public int compare(Object arg0, Object arg1) {
		CondVarHolder cvh1 = (CondVarHolder) arg0;
		CondVarHolder cvh2 = (CondVarHolder) arg1;
		
		if(cvh1.frequency < cvh2.frequency) {
			return 1;
		} else {
			return -1;
		}
	}

	public double getSupport() {
		return support;
	}

	public double getTotalRelatedFrequency() {
		return totalRelatedFrequency;
	}

	public void setTotalRelatedFrequency(double totalRelatedFrequency) {
		this.totalRelatedFrequency = totalRelatedFrequency;
	}

	public Operator getOperator() {
		return operator;
	}

	public void setOperator(Operator operator) {
		this.operator = operator;
	}

	public boolean isBPromotedSupport() {
		return bPromotedSupport;
	}

	public void setBPromotedSupport(boolean promotedSupport) {
		bPromotedSupport = promotedSupport;
	}

	public String getCodeSampleName() {
		return codeSampleFileName;
	}

	public void setCodeSampleName(String codeSampleName) {
		this.codeSampleFileName = codeSampleName;
	}

	public String getCodeSampleMethodName() {
		return codeSampleMethodName;
	}

	public void setCodeSampleMethodName(String codeSampleMethodName) {
		this.codeSampleMethodName = codeSampleMethodName;
	}
}
