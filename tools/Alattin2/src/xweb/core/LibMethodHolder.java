package xweb.core;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.code.analyzer.CodeExampleStore;
import xweb.code.analyzer.MinedPattern;
import xweb.code.analyzer.TypeHolder;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.IntHolder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;

public class LibMethodHolder extends Holder implements Comparator, Cloneable {

	LibClassHolder containingClass;
	HashMap<String, Integer> childClassInfoMap = new HashMap<String, Integer>(1,1); 
	
	String name;
	String returnType;
	String argTypes[];
	String argumentStr;
	String printArgumentStr;	//This string is actually sent as output
	int ID;
	
	List<String> throwsExceptionList;
	
	HashSet<LibMethodHolder> methodsInvoked = new HashSet<LibMethodHolder>(1,1);	//Other method invocations inside the current class, invoked by this method
	HashSet<LibMethodHolder> callingMethods = new HashSet<LibMethodHolder>(1,1);	//Set of calling methods 
	
	List<String> typeParameters = new ArrayList<String>();
	Set<String> exceptionSet = new HashSet<String>(1);									//Set that stores set of exceptions thrown
	
	boolean isAbstract = false;
	
	//Method-invocations that are error paths of this lib method
	List<MethodInvocationHolder> errorMIHList = new ArrayList<MethodInvocationHolder>(3);
	List<IntHolder> errorMIFrequency = new ArrayList<IntHolder>(3);
	private int noErrorPathCount = 0;	//A variable that stores the number of call sites that does not contain error paths for this API.
	List<CodeExampleStore> codeExamples = new ArrayList<CodeExampleStore>(3);
	
	List<MinedPattern> minedPatternList = null;
	List<NormalErrorPath> normal_error_APIList = null;
	
	public LibMethodHolder()
	{
		ID = RepositoryAnalyzer.getUniqueIDForMI();
		argTypes = new String[0];
		printArgumentStr = "()";
	}
	
	public LibMethodHolder(String methodSig)
	{
		ID = RepositoryAnalyzer.getUniqueIDForMI();
		int firstComma = methodSig.indexOf(",");
		int leftBrace = methodSig.indexOf("(");
			
		returnType = methodSig.substring(0, firstComma);
		name = methodSig.substring(firstComma + 1, leftBrace);
		argumentStr = methodSig.substring(leftBrace + 1, methodSig.length() - 1);
		
		if(!argumentStr.equals(""))
			argTypes = argumentStr.split(",");
		else
			argTypes = new String[0];
		
		argumentStr = "(" + argumentStr + ")";		
	}
	
	public String toString()
	{			
		return containingClass.getName() + "," + name + printArgumentStr + " ReturnType:" + returnType;
	}
	
	/****** Getters and Setters *********/
	public String[] getArgTypes() {
		return argTypes;
	}

	public void setArgTypes(String[] argTypes) {
		this.argTypes = argTypes;
	}

	public String getArgumentStr() {
		return argumentStr;
	}

	public void setArgumentStr(String argumentStr) {
		this.argumentStr = argumentStr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getReturnType() {
		return returnType;
	}

	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public HashSet<LibMethodHolder> getInvokedMethods() {
		return methodsInvoked;
	}
	
	//Adds the method to the invoked list to all children also
	public void addToInvokedMethodList(LibMethodHolder addLmh) {
		
		if(true)	//Not required for XWeb
			return;
		
		if(!methodsInvoked.contains(addLmh))
		{	
			methodsInvoked.add(addLmh);
			addLmh.getCallingMethods().add(this);
			
			//If this LMH is an abstract, then extend this behaviour to 
			//all its children
			if(addLmh.isAbstract())
				addToAllChildren(addLmh);		
		}	
	}
	
	private void addToAllChildren(LibMethodHolder addLmh) {
		
		LibClassHolder containingClass = addLmh.getContainingClass();
		if(containingClass.getAllChildTypes() == null)
		{
			containingClass.setAllChildTypes(RepositoryAnalyzer.getInstance().getAllChildTypes(containingClass.getName()));
		}
		
		for(String childClass : containingClass.getAllChildTypes()) {
			LibMethodHolder childLmh = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(childClass, addLmh);
			if(childLmh != null)
			{	
				methodsInvoked.add(childLmh);
				childLmh.getCallingMethods().add(this);
			}	
		}
		
	}	
	
	List<MIHList> associatedPatternContext = new ArrayList<MIHList>(3);
	
	//Methods for XWeb
	public void addToErrorPathAPIs(MethodInvocationHolder errorMIH, String fileName, String methodName, List<MethodInvocationHolder> contextList) {
		ASTCrawlerUtil.addToErrorPathAPIs(this, errorMIFrequency, errorMIHList, codeExamples, 
				associatedPatternContext, errorMIH, fileName, methodName, contextList);	
	}
	
	

	public HashSet<LibMethodHolder> getCallingMethods() {
		return callingMethods;
	}
	
	
	public int compare(Object obj1, Object obj2)
	{
		return -1;	
	}

	public LibClassHolder getContainingClass() {
		return containingClass;
	}

	public void setContainingClass(LibClassHolder containingClass) {
		this.containingClass = containingClass;
	}
	
	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	public List<String> getTypeParameters() {
		return typeParameters;
	}

	public String getPrintArgumentStr() {
		return printArgumentStr;
	}

	public void setPrintArgumentStr(String printArgumentStr) {
		this.printArgumentStr = printArgumentStr;
	}

	public HashMap<String, Integer> getChildClassInfoMap() {
		return childClassInfoMap;
	}

	public Object clone() throws CloneNotSupportedException {
		LibMethodHolder newLMH = new LibMethodHolder();
		newLMH.setName(this.getName());
		newLMH.setReturnType(this.getReturnType());
		newLMH.setAbstract(this.isAbstract());
		newLMH.setArgTypes(this.getArgTypes());
		newLMH.setArgumentStr(this.getArgumentStr());
		newLMH.setPrintArgumentStr(this.getPrintArgumentStr());	
		newLMH.setContainingClass(this.getContainingClass());
		return newLMH;
	}
	
	public void incrNoErrorPathCount() {
		noErrorPathCount++;
	}

	public List<MethodInvocationHolder> getErrorMIHList() {
		return errorMIHList;
	}

	public List<IntHolder> getErrorMIFrequency() {
		return errorMIFrequency;
	}

	public List<CodeExampleStore> getCodeExamples() {
		return codeExamples;
	}

	public int getNoErrorPathCount() {
		return noErrorPathCount;
	}

	public List<String> getThrowsExceptionList() {
		return throwsExceptionList;
	}
	
	public void setThrowsExceptionList(List<String> throwList) {
		throwsExceptionList = throwList;
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
		return returnType;
	}
	
	public String getPrintString() {
		String argStrLocal = "";
		for(String atype: argTypes) {
			argStrLocal += atype + "::"; 
		}
		return containingClass.getName() + ":" + name + "(" + argStrLocal + ")" + ":" + returnType;
		
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
	
	public int getKey() {
		return ID;
	}
	
	/**
	 * Compares with another LibMethodHolder instance
	 */	
	public boolean equals(Object obj)
	{
		if(obj instanceof MethodInvocationHolder)
			return this.equals((MethodInvocationHolder)obj);
		
		if(!(obj instanceof LibMethodHolder))
			return false;
		
		LibMethodHolder other = (LibMethodHolder) obj;
		if(isDummyHolder(this) || isDummyHolder(other))
			return false;
			
		if(this.containingClass.equals(other.containingClass) && this.name.equals(other.name)) {
			if(this.argTypes.length != other.argTypes.length)
				return false;
				
    		boolean bSame = true;
    			
    		//A heuristic for avoiding the type failures to identify more interesting patterns.
    		for(int tcnt = 0; tcnt < this.argTypes.length; tcnt++) {
    			String arg1 = this.argTypes[tcnt];
    			String arg2 = other.argTypes[tcnt];		

    			if(arg1.equals(arg2))
    				continue;
    				
    			bSame = false;
    			break;    					
    		}
    			
    		if(bSame)
    			return true;			
		}	
		return false;
	}

	/**
	 * Compares with another MethodInvocationHolder instance
	 */
	public boolean equals(MethodInvocationHolder other)
	{			
		if(isDummyHolder(this) || isDummyHolder(other))
			return false;
			
		if(this.containingClass.name.equals(other.getReceiverClass().type) && this.name.equals(other.getMethodName())) {
			TypeHolder[] otherTypes = other.getArgumentArr();
			if(this.argTypes.length != otherTypes.length)
				return false;
			
    		//A heuristic for avoiding the type failures to identify more interesting patterns.
    		if(!CommonConstants.FUNCTION_OVERLOADING)
			{
				return true;
			}
    		
    		boolean bSame = true;    		
    		for(int tcnt = 0; tcnt < this.argTypes.length; tcnt++) {
    			String arg1 = this.argTypes[tcnt];
    			String arg2 = otherTypes[tcnt].type;		

    			if(arg1.equals(arg2))
    				continue;
    				
    			bSame = false;
    			break;    					
    		}
    			
    		if(bSame)
    			return true;			
		}	
		return false;
	}	
}
