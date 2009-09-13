package api.usage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

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
	
	int endRanking = 0; //A end ranking value assigned after computing the metrics.
						//This helps to sort out internally.
	
	boolean isAbstract = false;
	
	/**
	 * IMPORTANT: Why there are two kinds of variables like 'numInvocations' and 'localNumInvocations'?
	 * 
	 * numInvocations -> represents one per a project that is using this method.
	 * localNumInvocations -> represents all usages, which makes sense only for computing deadspots.
	 */
	
	int numInvocations = 0; 	//Variable storing the number of times the method is invoked
	int numOverloads = 0;		//Variable storing the number of times the method is overloaded
	int numImplements = 0;		//Variable storing the number of times the method is implemented. (Applies if the contained object is interface)
	
	
	HashSet<String> visitedPackagesForInvocations = new HashSet<String>();
	HashSet<String> visitedPackagesForOverloads = new HashSet<String>();
	HashSet<String> visitedPackagesForImplements = new HashSet<String>();
	
	int localNumInvocations = 0; 	//Counter parts of above three variables and these are independent of
	int localNumOverloads = 0;		//package names. These are basically used for deadspots
	int localNumImplements = 0;
		
	int methodType = UNKNOWN_TYPE;				//Variable that stores whether the method type is TEMPLATE or HOOK
	int category = UNKNOWN_TYPE;
	double usagePercentage;
	
	List<CodeExampleStore> templateExampleStore = new ArrayList<CodeExampleStore>();
	List<CodeExampleStore> hookExampleStore = new ArrayList<CodeExampleStore>();
	
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
	
	
	public void incrNumInvocations(String className, String codeSampleName, boolean bLocalOnly, String methodName)
	{
		if(!bLocalOnly) {
			String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
			if(!visitedPackagesForInvocations.contains(packageName)) {
				visitedPackagesForInvocations.add(packageName);
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
				
				//Storing the code examples for this method invocation
				CodeExampleStore cesObj = new CodeExampleStore();
				cesObj.filename = codeSampleName;
				cesObj.methodname = methodName;
				cesObj.type = LibMethodHolder.TEMPLATE;
				templateExampleStore.add(cesObj);
			}		
		}		
		
		localNumInvocations++;	
	}

	public void incrNumOverloads(String className, String codeSampleName, boolean bLocalOnly, String methodName)
	{
		if(!bLocalOnly) {
			String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
			if(!visitedPackagesForOverloads.contains(packageName)) {
				visitedPackagesForOverloads.add(packageName);
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
				
				//Storing the code examples for this method invocation
				CodeExampleStore cesObj = new CodeExampleStore();
				cesObj.filename = codeSampleName;
				cesObj.methodname = methodName;
				cesObj.type = LibMethodHolder.HOOK;
				hookExampleStore.add(cesObj);

			}		
		}		
		localNumOverloads++;		
	}

	public void incrNumImplements(String className, String codeSampleName, boolean bLocalOnly, String methodName) 
	{
		if(!bLocalOnly) {
			String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
			if(!visitedPackagesForImplements.contains(packageName)) {
				visitedPackagesForImplements.add(packageName);
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
				//Storing the code examples for this method invocation
				CodeExampleStore cesObj = new CodeExampleStore();
				cesObj.filename = codeSampleName;
				cesObj.methodname = methodName;
				cesObj.type = LibMethodHolder.HOOK;
				hookExampleStore.add(cesObj);
			}		
		}
		localNumImplements++;		
	}
	
	public int getNumImplements() {
		return numImplements;
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
		else if(numTotal1 < numTotal2) 
			return 1;
		
		//If both are equal then use local numbers that are collected
		//with out package informations
		int localNumTotal1 = lmh1.localNumImplements + lmh1.localNumInvocations + lmh1.localNumOverloads;
		int localNumTotal2 = lmh2.localNumImplements + lmh2.localNumInvocations + lmh2.localNumOverloads;
		if(localNumTotal1 > localNumTotal2)
			return -1;
		else if(localNumTotal1 < localNumTotal2)
			return 1;	
		
		return -1;
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
	
	public void clearVisitedInfo() {
		visitedPackagesForInvocations.clear();
		visitedPackagesForOverloads.clear();
		visitedPackagesForImplements.clear();
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

	public int getLocalNumImplements() {
		return localNumImplements;
	}

	public void setLocalNumImplements(int localNumImplements) {
		this.localNumImplements = localNumImplements;
	}

	public int getLocalNumInvocations() {
		return localNumInvocations;
	}

	public void setLocalNumInvocations(int localNumInvocations) {
		this.localNumInvocations = localNumInvocations;
	}

	public int getLocalNumOverloads() {
		return localNumOverloads;
	}

	public void setLocalNumOverloads(int localNumOverloads) {
		this.localNumOverloads = localNumOverloads;
	}

	public void setNumOverloads(int numOverloads) {
		this.numOverloads = numOverloads;
	}

	public int getEndRanking() {
		return endRanking;
	}

	public void setEndRanking(int endRanking) {
		this.endRanking = endRanking;
	}

	public void setNumImplements(int numImplements) {
		this.numImplements = numImplements;
	}

	public List<CodeExampleStore> getHookExampleStore() {
		return hookExampleStore;
	}

	public List<CodeExampleStore> getTemplExampleStore() {
		return templateExampleStore;
	}	
	
	public void addToHookExampleStore(CodeExampleStore cesObj) {
		hookExampleStore.add(cesObj);
	}
	
	public void addToTemplateExampleStore(CodeExampleStore cesObj) {
		templateExampleStore.add(cesObj);		
	}
}
