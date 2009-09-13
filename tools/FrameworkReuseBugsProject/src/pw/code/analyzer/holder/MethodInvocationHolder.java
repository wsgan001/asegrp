package pw.code.analyzer.holder;

import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import pw.code.analyzer.ASTCrawlerUtil;
import pw.code.analyzer.TypeHolder;
import pw.common.CommonConstants;


/**
 * Class for holding entire information of a method invocation.
 * This holds information:
 * 1. Reference class: (Class to which current method belongs to) "this" is also a valid value
 * 2. Return type: (Return type of current method)
 * 3. List of arguments with type of each argument
 * @author suresh_thummalapenta
 *
 */
public class MethodInvocationHolder extends Holder {

	public static Logger logger = Logger.getLogger("MethodInvocationHolder");
	
	TypeHolder receiverClass = new TypeHolder();
	TypeHolder returnType = new TypeHolder();
	String argumentString;
	TypeHolder argumentAr[];
	String methodName;
	int noOfArguments;
	int key = MIHKEYGEN++;
	public boolean bDowncast = false;
	
	public static int MIHKEYGEN = 0;
	
	String descriptor = "";
	
	//Anomaly Detector additional storage variables
	ConditionVarHolderSet receiverCondVar;
	ConditionVarHolderSet preceedingCondVar;
	ConditionVarHolderSet argumentCondVars[];
	ConditionVarHolderSet returnCondVar;
	ConditionVarHolderSet suceedingCondVar;
	ConditionVarHolderSet suceedingReceiverCondVar;

	MethodInvocationHolder associatedLibMIH = null;
	
	public MethodInvocationHolder()
	{
		
	}
	
	/**
	 * Constructor for initializing MethodInvocationHolder from a CastExpression
	 * @param referenceClsName
	 * @param returnType
	 * @param ceObj
	 */
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, CastExpression ceObj)
	{
		super();
		
		this.receiverClass = referenceClsName;
		this.returnType = returnType;
		this.methodName = descriptor + "DOWNCAST";
		
		this.noOfArguments = 0;
		this.argumentString = "()"; 
		this.argumentAr = new TypeHolder[0];
	}
	
	/**
	 * Constructor for handling new object creations as they also cause transformation
	 */
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, ClassInstanceCreation cicObj)
	{
		super();
		
		this.receiverClass = referenceClsName;
		this.returnType = returnType;
		this.methodName = descriptor + "CONSTRUCTOR";
		
		this.noOfArguments = 0;
		List methodArgList = cicObj.arguments();
		StringBuffer tmpArgStr = new StringBuffer("(");
		argumentAr = new TypeHolder[methodArgList.size()];
				
		for(Iterator iter = methodArgList.iterator(); iter.hasNext();)
		{
			Expression expr = (Expression) iter.next();
			TypeHolder refClsName = (ASTCrawlerUtil.getRefClassName(expr)); 
			tmpArgStr.append(refClsName + ",");
			argumentAr[noOfArguments] = refClsName;
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}	

	}
	
	/**
	 * Constructor for initializing MethodInvocationHolder from MethodDeclaration object
	 * @param referenceClsName
	 * @param returnType
	 * @param miObj
	 */
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, MethodDeclaration miObj, String descriptor) 
	{
		super();
		this.descriptor = descriptor;
		setValuesForMeDecl(referenceClsName, returnType, miObj, true);
	}
	
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, MethodDeclaration miObj, boolean heuristicResolve) 
	{
		super();
		setValuesForMeDecl(referenceClsName, returnType, miObj, heuristicResolve);
	}

	
	private void setValuesForMeDecl(TypeHolder referenceClsName, TypeHolder returnType, MethodDeclaration miObj, boolean heuristicResolve)
	{
		
		this.receiverClass = referenceClsName;
		this.returnType = returnType;
		
		if(miObj.isConstructor())
		{
			this.methodName = descriptor + "CONSTRUCTOR";
		}
		else
		{	
			this.methodName = descriptor + miObj.getName().getIdentifier();
		}	
			
		this.noOfArguments = 0;
		StringBuffer tmpArgStr = new StringBuffer("(");
		argumentAr = new TypeHolder[miObj.parameters().size()];
		for(Iterator iter = miObj.parameters().iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration expr = (SingleVariableDeclaration) iter.next();
			
			String refClsName = "";
			
			if(heuristicResolve)
				refClsName = ASTCrawlerUtil.getFullClassName(expr.getType());
			else
				refClsName = expr.getType().toString();
			
			tmpArgStr.append(refClsName + ",");
			TypeHolder thObj = new TypeHolder();
			thObj.setType(refClsName);
			thObj.var = expr.getName().getIdentifier();
			argumentAr[noOfArguments] = thObj; 
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}

	}
	
	private void setValues(TypeHolder referenceClsName, TypeHolder returnType, String name, List methodArgList, boolean heuristicResolve)
	{
		this.receiverClass = referenceClsName;
		this.returnType = returnType;
		this.methodName = descriptor + name;
				
		this.noOfArguments = 0;
		StringBuffer tmpArgStr = new StringBuffer("(");
		argumentAr = new TypeHolder[methodArgList.size()];
		for(Iterator iter = methodArgList.iterator(); iter.hasNext();)
		{
			Expression expr = (Expression) iter.next();
			
			TypeHolder thObj = new TypeHolder();
			if(heuristicResolve)
				thObj = (ASTCrawlerUtil.getRefClassName(expr));
			else
			{	
				ITypeBinding exprTypeBinding = expr.resolveTypeBinding();
				
				if(exprTypeBinding != null)
				{
					thObj.setType(exprTypeBinding.getQualifiedName());
				}					
				else
				{
					logger.warn("ERROR: To DEAL WITH THIS...");
				}
			}	
			tmpArgStr.append(thObj + ",");
			argumentAr[noOfArguments] = thObj;
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}
			
		
	}

	/**
	 * Constructor for initializing MethodInvocationHolder from MethodInvocation object
	 * @param referenceClsName
	 * @param returnType
	 * @param miObj
	 */
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, MethodInvocation miObj) 
	{
		super();
		setValues(referenceClsName, returnType, miObj.getName().getIdentifier(), miObj.arguments(), true);
	}
	
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, MethodInvocation miObj, boolean heuristicResolve) 
	{
		super();
		setValues(referenceClsName, returnType, miObj.getName().getIdentifier(), miObj.arguments(), heuristicResolve);
	}
	
	/**
	 * Constructs a MethodInvocationHolder from a given string
	 * Accepted Format: getInstance(java.lang.String,java.lang.String)
	 * @param input
	 * @return
	 */
	public static MethodInvocationHolder parseFromString(String input, TypeHolder receiverClass)
	{
		MethodInvocationHolder newObj = new MethodInvocationHolder();
		int positionOfLeftBrace = input.indexOf("(");
		int positionOfRightBrace = input.indexOf(")");
		
		newObj.setReceiverClass(receiverClass);
		newObj.setReturnType(new TypeHolder());
		newObj.setArgumentString(input.substring(positionOfLeftBrace, positionOfRightBrace + 1));
		newObj.methodName = input.substring(0, positionOfLeftBrace);
		
		if(!newObj.getArgumentString().equals("()")) {
			String argSplits[] = input.substring(positionOfLeftBrace + 1, positionOfRightBrace).split(",");
			newObj.argumentAr = new TypeHolder[argSplits.length];
			newObj.noOfArguments = argSplits.length;
			for(int tcnt = 0; tcnt < argSplits.length; tcnt ++) {
				newObj.argumentAr[tcnt] = new TypeHolder(argSplits[tcnt]);		
			}
		} else {
			newObj.noOfArguments = 0;
			newObj.argumentAr = new TypeHolder[0];
		}
				
		return newObj;
	}

	public TypeHolder getReceiverClass() 
	{
		return receiverClass;
	}
	
	public void setReceiverClass(TypeHolder referenceClsName) 
	{
		this.receiverClass = referenceClsName;
	}
	
	public TypeHolder getReturnType() 
	{
		return returnType;
	}
	
	public void setReturnType(TypeHolder returnType) 
	{
		this.returnType = returnType;
	}
	
	
	public boolean equals(Object obj)
	{
		if(obj instanceof MethodInvocationHolder)
		{
			MethodInvocationHolder mihObj = (MethodInvocationHolder) obj;
			if(isDummyHolder(this) || isDummyHolder(mihObj))
					return false;
			
			if(this.receiverClass.equals(mihObj.receiverClass) && this.methodName.equals(mihObj.methodName)) {
				if(this.noOfArguments != mihObj.noOfArguments)
					return false;
				
    			boolean bSame = true;
    			
    			//A heuristic for avoiding the type failures to identify
    			//more interesting patterns.
    			if(true)
    				return bSame;
    			for(int tcnt = 0; tcnt < this.noOfArguments; tcnt++) {
    				String arg1 = this.argumentAr[tcnt].getType();
    				String arg2 = mihObj.argumentAr[tcnt].getType();    				
    				
    				if(arg1.equals(CommonConstants.unknownType) || arg2.equals(CommonConstants.unknownType) || 
    						arg1.equals(CommonConstants.multipleCurrTypes) || arg2.equals(CommonConstants.multipleCurrTypes) ||
    						arg1.equals("") || arg2.equals(""))
    					continue;
    				
    				if(arg1.equals(arg2))
    					continue;
    				
    				//Some times, in case of constants, the right most part will be in capitals,
    				//which indicates it is a constant. Eg: TestRunListener.STATUS_ERROR
    				String partName1 = arg1.substring(arg1.lastIndexOf(".") + 1, arg1.length());
    				String partName2 = arg2.substring(arg2.lastIndexOf(".") + 1, arg2.length());
    				if(partName1.toUpperCase().equals(partName1) || partName2.toUpperCase().equals(partName2))
    					continue;
    				
    				bSame = false;
    				break;    					
    			}
    			
    			if(bSame)
    				return true;			
			}
		}
	
		return false;
	}
	
	public TypeHolder[] getArgumentArr()
	{
		return argumentAr;
	}
	
	public boolean isPresentInArgumentArr(String objType)
	{
		boolean bFound = false;
		for(int i = 0; i < noOfArguments; i++)
		{
			if(argumentAr[i].getType().equals(objType))
				return true;
		}
		
		return bFound;
	}
	
	
	public String getCompleteString() 
	{
		if(receiverClass.getType().equals("#DUMMY"))
			return "#DUMMY";
		else
		{
			String tempStr = receiverClass.getType() + "," + methodName + argumentString + " ";
			if(bDowncast)
			{
				tempStr = tempStr + " DownCasted_ReturnType:"; 
			}
			else
			{
				tempStr = tempStr + " ReturnType:";
			}
			tempStr += getReturnType().getType();			
			tempStr += "(ReceiverVar: " + receiverClass.var + ")";
			
			String argVarStr = "";
			for(TypeHolder argVar : argumentAr)
			{
				argVarStr += argVar.var + ","; 
			}		
			
			tempStr += "(ArgumentVar: " + argVarStr +") " + "(ReturningVar: " +  returnType.var + ")";
			return tempStr;
		}
	}
	
	public String toString()
	{
		if(receiverClass.equals("#DUMMY"))
			return "#DUMMY";
		else
		{
			String tempStr = receiverClass + "," + methodName + argumentString + " ";
			if(bDowncast)
			{
				tempStr = tempStr + " DownCasted_ReturnType:"; 
			}
			else
			{
				tempStr = tempStr + " ReturnType:";
			}
			return tempStr + getReturnType();
		}	
	}
	
	public String getPrintString() {
		
		String argStrLocal = "";
		for(TypeHolder atype: argumentAr) {
			argStrLocal += atype.getType() + "::"; 
		}
		
		return receiverClass.getType() + ":" + methodName + "(" + argStrLocal + ")" + ":" + returnType.getType();
	}

	
	public String formattedPrintString()
	{
		if(receiverClass.equals("#DUMMY"))
			return "#DUMMY";
		else
		{
			String tempStr = receiverClass + "," + methodName + argumentString + " ";
			return tempStr;
		}	
	}

	
	
	public String getArgumentString() {
		return argumentString;
	}

	public void setArgumentString(String argumentString) {
		this.argumentString = argumentString;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getNoOfArguments() {
		return noOfArguments;
	}

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
	}

	/**** Methods handling data in and out flow in condition var holders ****/
	/**
	 * Method for initializing hashmaps for holding conditional variables
	 * This is not done by default as this initialization is not required
	 * for all types of variables.
	 */
	@SuppressWarnings("unchecked")
	public void initializeCondVarMaps() {
		receiverCondVar = new ConditionVarHolderSet();
		preceedingCondVar = new ConditionVarHolderSet();
		argumentCondVars = new ConditionVarHolderSet[noOfArguments];
		for(int tcnt = 0; tcnt < noOfArguments; tcnt++) {
			argumentCondVars[tcnt] = new ConditionVarHolderSet();
		}	
		returnCondVar = new ConditionVarHolderSet();
		suceedingCondVar = new ConditionVarHolderSet();	
		suceedingReceiverCondVar = new ConditionVarHolderSet();
	}

	public ConditionVarHolderSet getReceiverCondVar() {
		return receiverCondVar;
	}

	public ConditionVarHolderSet getPreceedingCondVar() {
		return preceedingCondVar;
	}

	public ConditionVarHolderSet getArgumentCondVars(int argPosition) {
		return argumentCondVars[argPosition];
	}

	public ConditionVarHolderSet getReturnCondVar() {
		return returnCondVar;
	}

	public ConditionVarHolderSet getSuceedingCondVar() {
		return suceedingCondVar;
	}
	
	public MethodInvocationHolder getAssociatedLibMIH() {
		return associatedLibMIH;
	}

	public void setAssociatedLibMIH(MethodInvocationHolder associatedLibMIH) {
		this.associatedLibMIH = associatedLibMIH;
	}

	/*Getter methods for variables having variables that store no conditions around*/
	public int getNumNoneArgumentCondVars(int argPosition) {
		return argumentCondVars[argPosition].getNumEmptyCondVar();
	}

	public int getNumNonePreceedingCondVar() {
		return preceedingCondVar.getNumEmptyCondVar();
	}

	public int getNumNoneReceiverCondVar() {
		return receiverCondVar.getNumEmptyCondVar();
	}

	public int getNumNoneReturnCondVar() {
		return returnCondVar.getNumEmptyCondVar();
	}

	public int getNumNoneSuceedingCondVar() {
		return suceedingCondVar.getNumEmptyCondVar();
	}
	
	public int getNumNoneSuceedingReceiverCondVar() {
		return suceedingReceiverCondVar.getNumEmptyCondVar();
	}
	
	public void incrNumNoneArgumentCondVars(int argPosition) {
		argumentCondVars[argPosition].incrNumEmptyCondVar();
	}

	public void incrNumNonePreceedingCondVar() {
		preceedingCondVar.incrNumEmptyCondVar();
	}

	public void incrNumNoneReceiverCondVar() {
		receiverCondVar.incrNumEmptyCondVar();
	}

	public void incrNumNoneReturnCondVar() {
		returnCondVar.incrNumEmptyCondVar();
	}

	public void incrNumNoneSuceedingCondVar() {
		suceedingCondVar.incrNumEmptyCondVar();
	}
	
	public void incrNumNoneSuceedingReceiverCondVar() {
		suceedingReceiverCondVar.incrNumEmptyCondVar();
	}
	

	public ConditionVarHolderSet getSuceedingReceiverCondVar() {
		return suceedingReceiverCondVar;
	}

	public void setSuceedingReceiverCondVar(
			ConditionVarHolderSet suceedingReceiverCondVar) {
		this.suceedingReceiverCondVar = suceedingReceiverCondVar;
	}
}