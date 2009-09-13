package minebugs.srclibhandlers;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import minebugs.core.RepositoryAnalyzer;

public class LibMethodHolder implements Comparator, Cloneable {

	/**
	 * Static constants
	 */
	public final static int TEMPLATE = 1;
	public final static int HOOK = 2;
	public final static int UNKNOWN_TYPE = 0;
	
	//Categories
	public final static int HOTSPOT = 1;
	public final static int WEAKSPOT = 2;
	public final static int DEADSPOT = 3;
	
	LibClassHolder containingClass;
	HashMap<String, Integer> childClassInfoMap = new HashMap<String, Integer>(); 
	
	String name;
	String returnType;
	String argTypes[];
	String argumentStr;
	String printArgumentStr;	//This string is actually sent as output
	int ID;
	
	HashSet<LibMethodHolder> methodsInvoked = new HashSet<LibMethodHolder>();	//Other method invocations inside the current class, invoked by this method
	HashSet<LibMethodHolder> callingMethods = new HashSet<LibMethodHolder>();	//Set of calling methods 
	
	List<String> typeParameters = new ArrayList<String>();
	
	boolean isAbstract = false;
	int numInvocations = 0; 	//Variable storing the number of times the method is invoked
	int numOverloads = 0;		//Variable storing the number of times the method is overloaded
	int numImplements = 0;		//Variable storing the number of times the method is implemented. (Applies if the contained object is interface)
	int methodType = UNKNOWN_TYPE;				//Variable that stores whether the method type is TEMPLATE or HOOK
	int category = UNKNOWN_TYPE;
	double usagePercentage;
	
	String descriptor = "Invokes";
	
	public LibMethodHolder()
	{
		ID = RepositoryAnalyzer.getUniqueIDForMI();
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
		int indexOfColon = -1;
		String tempName = name, hiddenDescriptor = "";
		if((indexOfColon = name.indexOf(":")) != -1)
		{
			tempName = name.substring(indexOfColon + 1, name.length());
			hiddenDescriptor = name.substring(0, indexOfColon); 
		}
		
		if(hiddenDescriptor.equals("ExtendsClass"))
			return "(Class Name)";
		
		if(hiddenDescriptor.equals("ImplementsInterface"))
			return "(Interface Name)";
		
		return returnType + "," + tempName + printArgumentStr;
	}
	
	public void incrNumInvocations(String className)
	{
		numInvocations++;
		
		Integer numClsSpecificCnt = childClassInfoMap.get(className);
		if(numClsSpecificCnt == null)
		{
			childClassInfoMap.put(className, new Integer(1));
		}
		else
		{
			childClassInfoMap.put(className, new Integer(numClsSpecificCnt.intValue() + 1));
		}
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

	public int getNumInvocations() {
		return numInvocations;
	}

	public void setNumInvocations(int numInvocations) {
		this.numInvocations = numInvocations;
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

	public int getNumOverloads() {
		return numOverloads;
	}
	
	public void incrNumOverloads(String className)
	{
		numOverloads++;
		
		Integer numClsSpecificCnt = childClassInfoMap.get(className);
		if(numClsSpecificCnt == null)
		{
			childClassInfoMap.put(className, new Integer(1));
		}
		else
		{
			childClassInfoMap.put(className, new Integer(numClsSpecificCnt.intValue() + 1));
		}
	}

	public int getNumImplements() {
		return numImplements;
	}
	
	public void incrNumImplements(String className) {
		numImplements++;
		
		Integer numClsSpecificCnt = childClassInfoMap.get(className);
		if(numClsSpecificCnt == null)
		{
			childClassInfoMap.put(className, new Integer(1));
		}
		else
		{
			childClassInfoMap.put(className, new Integer(numClsSpecificCnt.intValue() + 1));
		}
	}

	public int getMethodType() {
		return methodType;
	}

	public void setMethodType(int methodType) {
		this.methodType = this.methodType | methodType;
	}

	public HashSet<LibMethodHolder> getInvokedMethods() {
		return methodsInvoked;
	}
	
	//Adds the method to the invoked list to all children also
	public void addToInvokedMethodList(LibMethodHolder addLmh) {
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
	

	public HashSet<LibMethodHolder> getCallingMethods() {
		return callingMethods;
	}
	
	
	public int compare(Object obj1, Object obj2)
	{
		if(!(obj1 instanceof LibMethodHolder) || !(obj2 instanceof LibMethodHolder))
			return -1;
		
		LibMethodHolder lmh1 = (LibMethodHolder) obj1;
		LibMethodHolder lmh2 = (LibMethodHolder) obj2;
		int numTotal1 = lmh1.numImplements + lmh1.numInvocations + lmh1.numOverloads;
		int numTotal2 = lmh2.numImplements + lmh2.numInvocations + lmh2.numOverloads;
		
		if(numTotal1 > numTotal2)
			return -1;
		else if(numTotal1 == numTotal2)
			return -1;
		else
			return 1;	
		
	}

	public LibClassHolder getContainingClass() {
		return containingClass;
	}

	public void setContainingClass(LibClassHolder containingClass) {
		this.containingClass = containingClass;
	}
	
	public int getTotalUsages()
	{
		return numImplements + numInvocations + numOverloads;
	}

	public int getID() {
		return ID;
	}

	public void setID(int id) {
		ID = id;
	}

	public int getCategory() {
		return category;
	}

	public void setCategory(int category) {
		this.category = category;
	}

	public double getUsagePercentage() {
		return usagePercentage;
	}

	public void setUsagePercentage(double usagePercentage) {
		this.usagePercentage = usagePercentage;
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

	public String getDescriptor() {
		return descriptor;
	}

	public void setDescriptor(String descriptor) {
		this.descriptor = descriptor;
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
	
	public String getPrintString() {
		
		String argStrLocal = "";
		for(String atype: argTypes) {
			argStrLocal += atype + "::"; 
		}
		
		return containingClass.name + ":" + name + "(" + argStrLocal + ")" + ":" + returnType;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof LibMethodHolder)
		{
			return this.getPrintString().equals(((LibMethodHolder)obj).getPrintString());
		}	
		return false;
	}
	
}
