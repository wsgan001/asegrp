package xweb.code.analyzer.holder;

import org.eclipse.jdt.core.dom.MethodInvocation;

/**
 * A holder for conditional variable.
 * @author suresh_thummalapenta
 *
 */
public class CondVarHolder {

	protected String varName = "";	//Name of the variable participated in the conditional expression
	protected int condType = -1;	//Describes what kind of check is performed w.r.t to the other operand in the condition
	
	//Additional info variables
	private String constValue = "";		//Stores the value of the constant if the condType == CONSTANT_EQUALITY_CHECK or METHODARG_CONSTANT_EQUALITY_CHECK
										//Stores the boolean value if condType == TRUE_FALSE_CHECK or METHODARG_TRUE_FALSE_CHECK
										//Stores the otherType if condType == INSTANCEARG_TRUE_FALSE_CHECK
	private MethodInvocation otherMIiObj; //Stores the other method invocation if condType = RETVAL_EQUALITY_CHECK or METHODARG_RETVAL_EQUALITY_CHECK
	
	
	
	public CondVarHolder () {
	}
	
	public CondVarHolder(String varName, int condType) {
		this.varName = varName;
		this.condType = condType;
	}
	
	public String toString() {
		String retStr = varName + ":" + condType;
		
		if(condType == CONSTANT_EQUALITY_CHECK || condType == METHODARG_CONSTANT_EQUALITY_CHECK || 
				condType == TRUE_FALSE_CHECK || condType == METHODARG_TRUE_FALSE_CHECK || condType == INSTANCEARG_TRUE_FALSE_CHECK) {
			retStr += ":" + constValue;
		} else if (condType == RETVAL_EQUALITY_CHECK || condType == METHODARG_RETVAL_EQUALITY_CHECK) {
			retStr += ":" + otherMIiObj;
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
    
    public static final int METHODARG = 5;
    public static final int METHODARG_NULL_CHECK = 6; //Null check through a method invocation like for variable 'x', if(m1(x) != null)
    public static final int METHODARG_TRUE_FALSE_CHECK = 7; //True false check done as a part of method invocation
    public static final int METHODARG_CONSTANT_EQUALITY_CHECK = 8; //A constant equality check as a part of method argument
    public static final int METHODARG_RETVAL_EQUALITY_CHECK = 9; //For 'x', this type includes the case if(m1(x) == m2(x))
    
    public static final int INSTANCEARG_TRUE_FALSE_CHECK = 10;	//For 'x', this type checks the case if(x instanceof SomeType)

    public static final int CLASS_EQUIVALENCE_CHECK = 11;	//For comparisons like if ((method.getReturnType() == double.class))
    public static final int METHODARG_CLASS_EQUIVALENCE_CHECK = 12;
    
}
