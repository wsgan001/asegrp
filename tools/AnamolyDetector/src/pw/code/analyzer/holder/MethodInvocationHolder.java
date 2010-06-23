package pw.code.analyzer.holder;

import imminer.migrator.ADAndPattern;
import imminer.migrator.ADComboPattern;
import imminer.migrator.ADMinedPattern;
import imminer.migrator.ADSinglePattern;

import java.util.ArrayList;
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
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import pw.code.analyzer.ASTCrawlerUtil;
import pw.code.analyzer.CodeExampleStore;
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
	public static int MINER_ID_GEN = 1;	//Generates IDs for trace generation
	
	String descriptor = "";
	
	//Anomaly Detector additional storage variables
	ConditionVarHolderSet receiverCondVar;
	ConditionVarHolderSet preceedingCondVar;
	ConditionVarHolderSet argumentCondVars[];
	ConditionVarHolderSet returnCondVar;
	ConditionVarHolderSet suceedingCondVar;
	ConditionVarHolderSet suceedingReceiverCondVar;

	MethodInvocationHolder associatedLibMIH = null;
	
	//Before and After Path : New code added for collecting 
	//traces for the mining
	List<PrePostPathHolder> prePostList = null;  
	List<Holder> existingHolderElements = null;
	List<CodeExampleStore> codeExamplesList = null;
	int numEmptyCallSites = 0;	//Stores the number of callsites without any condition checks around
								//This has to be considered to compute 
	
	public double dominatingSupport = 0; //A support especially used for sorting the final set of defects
								  //Mainly used during printing.	
	
	/*******************************************/
	//New variables specific for Alattin Journal version
	public List<ADMinedPattern> singlePatternList = new ArrayList<ADMinedPattern>(1);
	public List<ADMinedPattern> andPatternList = new ArrayList<ADMinedPattern>(1);
	public List<ADMinedPattern> orPatternList = new ArrayList<ADMinedPattern>();
	public List<ADMinedPattern> xorPatternList = new ArrayList<ADMinedPattern>();
	public List<ADMinedPattern> comboPatternList = new ArrayList<ADMinedPattern>();
	public boolean hasSamePatternFormats = false;
	/*******************************************/
	
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
		try
		{
			newObj.setArgumentString(input.substring(positionOfLeftBrace, positionOfRightBrace + 1));
			newObj.methodName = input.substring(0, positionOfLeftBrace);
		}
		catch(StringIndexOutOfBoundsException sb)
		{
			int i = 0;
		}
		
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
				boolean bSame = true;
    			//A heuristic for avoiding the type failures to identify
    			//more interesting patterns.
    			if(true)
    				return bSame;
				
				if(this.noOfArguments != mihObj.noOfArguments)
					return false;
    			
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
			String tempStr = receiverClass + "," + methodName + argumentString;
			return tempStr;
			/*if(bDowncast)
			{
				tempStr = tempStr + " DownCasted_ReturnType:"; 
			}
			else
			{
				tempStr = tempStr + " ReturnType:";
			}
			return tempStr + getReturnType();*/		
		}	
	}
	
	public String getPrintString() {
		
		String argStrLocal = "";
		for(TypeHolder atype: argumentAr) {
			argStrLocal += atype.getType() + "::"; 
		}
		
		//return receiverClass.getType() + ":" + methodName + "(" + argStrLocal + ")" + ":" + returnType.getType();
		return receiverClass.getType() + ":" + methodName + "(" + argStrLocal + ")";
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
		/*receiverCondVar = new ConditionVarHolderSet();
		preceedingCondVar = new ConditionVarHolderSet();
		argumentCondVars = new ConditionVarHolderSet[noOfArguments];
		for(int tcnt = 0; tcnt < noOfArguments; tcnt++) {
			argumentCondVars[tcnt] = new ConditionVarHolderSet();
		}	
		returnCondVar = new ConditionVarHolderSet();
		suceedingCondVar = new ConditionVarHolderSet();	
		suceedingReceiverCondVar = new ConditionVarHolderSet();*/
		//Suresh : Deprecated
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

	public int getKey() {
		return key;
	}
	
	/*****************************************************************
	* 			NEW METHODS ADDED FOR COLLECTING TRACES				 *
	******************************************************************/
	
	/*
	 * Adds elements to PrePost List. Also assigns unique ID to each
	 * element in the list and this ID is independent to each method invocation
	 * holder
	 */
	public void addToPrePostList(Set<Holder> preList, Set<Holder> postList, String filename, String methodname) 
	{
		if(preList.size() == 0 && postList.size() == 0) {
			numEmptyCallSites++;
			return;
		}
		
		//Initialize lists if not done yet
		if(prePostList == null) {
			prePostList = new ArrayList<PrePostPathHolder>();
		}
		
		if(existingHolderElements == null) {
			existingHolderElements = new ArrayList<Holder>();
		}
		
		//assign unique IDs
		for(Holder hObj : preList) {
			assignIDs(hObj, existingHolderElements);
		}
		
		for(Holder hObj : postList) {
			assignIDs(hObj, existingHolderElements);
		}
		
		PrePostPathHolder pppHObj = new PrePostPathHolder(preList, postList);
		prePostList.add(pppHObj);			
		
		addToCodeExampleStore(filename, methodname);		
	}
	
	public void addToPrePostList(PrePostPathHolder pppHObj)
	{
		if(prePostList == null) {
			prePostList = new ArrayList<PrePostPathHolder>();
		}
		
		prePostList.add(pppHObj);
	}
	
	private void assignIDs(Holder holderObj, List<Holder> existingHolderElements)
	{
		boolean bObjExists = false;
		for(Holder existingObj : existingHolderElements) {
			if(holderObj instanceof MethodInvocationHolder && existingObj instanceof MethodInvocationHolder)
			{
				MethodInvocationHolder mHolderObj = (MethodInvocationHolder) holderObj;				
				MethodInvocationHolder mExistingObj = (MethodInvocationHolder) existingObj;
				
				if(mHolderObj.equals(mExistingObj)) {
					mHolderObj.key = mExistingObj.key;
					bObjExists = true;
					break;
				}				
			} 
			
			if(holderObj instanceof CondVarHolder_Typeholder && existingObj instanceof CondVarHolder_Typeholder)
			{
				CondVarHolder_Typeholder cHolderObj = (CondVarHolder_Typeholder) holderObj;
				CondVarHolder_Typeholder cExistingObj = (CondVarHolder_Typeholder) existingObj;
								
				if(cHolderObj.equals(cExistingObj)) {
					cHolderObj.key = cExistingObj.key;
					bObjExists = true;
					break;
				}				
			}		
		}
		
		if(!bObjExists) {			
			if(holderObj instanceof MethodInvocationHolder) {
				((MethodInvocationHolder)holderObj).key = MINER_ID_GEN++;
			} else if(holderObj instanceof CondVarHolder_Typeholder) {
				((CondVarHolder_Typeholder)holderObj).key = MINER_ID_GEN++;
			}
			existingHolderElements.add(holderObj);
		}		
	}
	
	/*
	 * Clears the allocated memory. This function should be invoked after
	 * the processing with respect to a method invocation is finished.
	 */
	public void clearStorageElements()
	{
		prePostList = null;
		existingHolderElements = null;		
		receiverCondVar = null;
		preceedingCondVar = null;
		argumentCondVars = null;
		returnCondVar = null;
		suceedingCondVar = null;
		suceedingReceiverCondVar = null;
	}

	public List<PrePostPathHolder> getPrePostList() {
		return prePostList;
	}

	public List<Holder> getExistingHolderElements() {
		return existingHolderElements;
	}
	
	public void addToCodeExampleStore(String filename, String methodname)
	{
		if(codeExamplesList == null) {
			codeExamplesList = new ArrayList<CodeExampleStore>();
		}
		
		CodeExampleStore ces = new CodeExampleStore();
		ces.javaFileName = filename;
		ces.methodName = methodname;		
		codeExamplesList.add(ces);
	}

	public List<CodeExampleStore> getCodeExamplesList() {
		return codeExamplesList;
	}
	
	public int getNumEmptyCallSites() {
		return numEmptyCallSites;
	}
	
	/**
	 * Hashcode is computed based on the method name
	 */
	/*public int hashCode()
	{
		int retVal = 0;
		
		for(char ch : methodName.toCharArray()) {
			retVal = retVal + ch;
		}
		
		return retVal;	
	}*/
}