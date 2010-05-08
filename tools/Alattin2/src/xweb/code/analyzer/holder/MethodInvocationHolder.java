package xweb.code.analyzer.holder;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.code.analyzer.CodeExampleStore;
import xweb.code.analyzer.MinedPattern;
import xweb.code.analyzer.TypeHolder;
import xweb.common.CommonConstants;
import xweb.core.NormalErrorPath;
import xweb.core.MIHList;

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
	public static int MIHKEYGEN = 0;
	public static int MIH_LIBKEYGEN = 1;
	int key = -1;
	public boolean bDowncast = false;
	
	String descriptor = "";
	
	Holder associatedLibMIH = null;		//Associated object
		
	HashSet<String> dependentVarSet = new HashSet<String>(3,3);		//A set that stores the set of associated variables for this method invocation
	private int noErrorPathCount = 0;	//A variable that stores the number of call sites that does not contain error paths for this API.
	
	//Method-invocations that are error paths of this lib method
	List<MethodInvocationHolder> errorMIHList = new ArrayList<MethodInvocationHolder>(3);
	List<IntHolder> errorMIFrequency = new ArrayList<IntHolder>(3);
	List<CodeExampleStore> codeExamples = new ArrayList<CodeExampleStore>(3);
	
	List<MinedPattern> minedPatternList = null;	
	List<NormalErrorPath> normal_error_APIList = null;		
	Set<String> exceptionSet = new HashSet<String>(1);
	
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
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, ClassInstanceCreation cicObj, boolean heuristicResolve)
	{
		super();		
		setValues(referenceClsName, returnType, "CONSTRUCTOR", cicObj.arguments(), heuristicResolve);		
	}
	
	/**
	 * Constructor for handling new object creations as they also cause transformation
	 */
	public MethodInvocationHolder(TypeHolder referenceClsName, TypeHolder returnType, ClassInstanceCreation cicObj)
	{
		super();
		setValues(referenceClsName, returnType, "CONSTRUCTOR", cicObj.arguments(), true);
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
			else {				
				refClsName = expr.getType().toString();
			}	
			
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
				if(exprTypeBinding != null) {
					thObj.setType(exprTypeBinding.getQualifiedName());
					if(expr instanceof SimpleName) {
		    			thObj.var = ((SimpleName)expr).getIdentifier();
		    		}
				}					
				else {
					thObj = (ASTCrawlerUtil.getRefClassName(expr));
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
	
	/**
	 * A counter part of equals function that performs the comparison heuristically,
	 * for example, if the receiver type of any class is UNKNOWN, this method ignores it.
	 * NOTE: This should not be used for general comparisons.
	 * @param obj
	 * @return
	 */
	public boolean heuristicEquals(Object obj)
	{
		
		if(obj instanceof MethodInvocationHolder) {
			MethodInvocationHolder mihObj = (MethodInvocationHolder) obj;
			String receiverType1 = mihObj.receiverClass.type;
			String receiverType2 = this.receiverClass.type;
			if(receiverType1.startsWith(CommonConstants.unknownType)) {
				mihObj.receiverClass.type = receiverType2;
			} else if(receiverType2.startsWith(CommonConstants.unknownType)) {
				this.receiverClass.type = receiverType1;
			}
			
			
			boolean returnVal = this.equals(mihObj);
			mihObj.receiverClass.type = receiverType1;
			this.receiverClass.type = receiverType2;
			return returnVal;
		}
		
		return false;
	}
	
	
	public boolean equals(Object obj)
	{
		boolean bTrue = true;
		if(obj instanceof MethodInvocationHolder)
		{
			MethodInvocationHolder mihObj = (MethodInvocationHolder) obj;
			if(isDummyHolder(this) || isDummyHolder(mihObj))
					return false;
			
			if(this.receiverClass.equals(mihObj.receiverClass) && this.methodName.equals(mihObj.methodName)) {
				//TODO: Ignoring arguments for time being (Making the approach similar to Weimer)
				//if(bTrue)
				//	return true;
				
				if(this.noOfArguments != mihObj.noOfArguments)
					return false;
				
    			boolean bSame = true;
    			
    			//A heuristic for avoiding the type failures to identify
    			//more interesting patterns.
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

	List<MIHList> associatedPatternContext = new ArrayList<MIHList>(3);
	
	//Methods for XWeb
	public void addToErrorPathAPIs(MethodInvocationHolder errorMIH, String fileName, String methodName, List<MethodInvocationHolder> contextList) {
		ASTCrawlerUtil.addToErrorPathAPIs(this, errorMIFrequency, errorMIHList, codeExamples, 
				associatedPatternContext, errorMIH, fileName, methodName, contextList);		
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
	
	public int getKey() {
		return key;
	}
	
	public void setKey(int key) {
		this.key = key;
	}

	public HashSet<String> getDependentVarSet() {
		return dependentVarSet;
	}

	public int getNoErrorPathCount() {
		return noErrorPathCount;
	}

	public void incrNoErrorPathCount() {
		noErrorPathCount++;
	}

	public void clearDependentSet() {
		dependentVarSet = new HashSet<String>(0,0);
	}

	public List<CodeExampleStore> getCodeExamples() {
		return codeExamples;
	}

	public Holder getAssociatedLibMIH() {
		return associatedLibMIH;
	}

	public void setAssociatedLibMIH(Holder associatedLibMIH) {
		this.associatedLibMIH = associatedLibMIH;
	}
	
	public List<MethodInvocationHolder> getErrorMIHList() {
		return errorMIHList;
	}
	
	public List<IntHolder> getErrorMIFrequency() {
		return errorMIFrequency;
	}
	
	public void addToMinedPatterns(MinedPattern mpObj) {
		if(minedPatternList == null) {
			minedPatternList = new ArrayList<MinedPattern>();
		}		
		minedPatternList.add(mpObj);
	}
	
	public List<MinedPattern> getMinedPatterns() {
		return minedPatternList;
	}
	
	public String getActualReturnType() {
		return returnType.type;
	}
	
	public String getPrintString() {
		
		String argStrLocal = "";
		for(TypeHolder atype: argumentAr) {
			argStrLocal += atype.type + "::"; 
		}
		
		return receiverClass.type + ":" + methodName + "(" + argStrLocal + ")" + ":" + returnType.type;		 
	}

	public List<MIHList> getAssociatedPatternContext() {
		return associatedPatternContext;
	}

	public Set<String> getExceptionSet() {
		return exceptionSet;
	}
	
	public void addToExceptionSet(String exceptionStr) {
		exceptionSet.add(exceptionStr);
	}
	
	public void addToNormalErrorList(List<MethodInvocationHolder> normalList, List<MethodInvocationHolder> errorList, String filename, String methodName) {
		if(normal_error_APIList == null) {
			normal_error_APIList = new ArrayList<NormalErrorPath>();
		}
		
		NormalErrorPath nepObj = new NormalErrorPath(normalList, errorList);	
		normal_error_APIList.add(nepObj);
		
		CodeExampleStore cesObj = new CodeExampleStore();
		cesObj.javaFileName = filename;
		cesObj.methodName = methodName;
		codeExamples.add(cesObj);
	}

	public List<NormalErrorPath> getNormal_error_APIList() {
		return normal_error_APIList;
	}

	public void setNormal_error_APIList(List<NormalErrorPath> normal_error_APIList) {
		this.normal_error_APIList = normal_error_APIList;
	}
	
	
	
}