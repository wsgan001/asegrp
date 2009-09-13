package api.usage;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import pw.common.CommonConstants;

public class LibClassHolder {

	String name;
	LibMethodHolder methods[];
		
	boolean isInterface = false;
	boolean isAbstract = false;
	
	HashSet<String> childTypes = new HashSet<String>();	//Stores possible children like for a class, subclasses and 
								//for an interface, this stores the implementing classes
	HashSet<String> allChildTypes = null;
	
	String parentType = null;
	HashSet<String> allInterfaceTypes = new HashSet<String>();
	List<String> parameterizedTypes = new ArrayList<String>();
	
	/**
	 * Refer to the LibMethodHolder for understanding the reasons of why there are two 
	 * kinds of variables over here.
	 */
	
	int numInstances = 0;	//Number of times this class is instantiated using its constructors
	int numExtensions = 0;	//Number of times this class is extended to alter the behaviour of the methods
	int numImplements = 0;	//Number of times the class is implemented (applied to interfaces)
	
	
	int localNumInstances = 0;	//The comments written for LibMethodHolder also applies here.
	int localNumExtensions = 0;
	int localNumImplements = 0;
	
	HashSet<String> visitedPackagesForInstances = new HashSet<String>();
	HashSet<String> visitedPackagesForExtends = new HashSet<String>();
	HashSet<String> visitedPackagesForImplements = new HashSet<String>();
	
	/* Variables that store the end ranking information */
	int endRanking = 0; //A ranking value assigned after gathering the metrics.
	int endCategory = CommonConstants.UNKNOWN_CATEGORY; //Initially assigned to unknown category.
	HotspotHierarchyStore endHierarchyStore = null;
	
	List<CodeExampleStore> templateExampleStore = new ArrayList<CodeExampleStore>();
	List<CodeExampleStore> hookExampleStore = new ArrayList<CodeExampleStore>();
	
	public LibClassHolder(String classSig)
	{
		name = classSig;
	}
	
	public boolean equals(Object lchObj)
	{
		if(!(lchObj instanceof LibClassHolder)) {
			return false;
		}		
		return this.name.equals(((LibClassHolder)lchObj).getName());
	}

	public LibMethodHolder[] getMethods() {
		return methods;
	}

	public void setMethods(LibMethodHolder[] methods) {
		this.methods = methods;
	}

	public void setMethods(ArrayList<LibMethodHolder> methods) {
		this.methods = new LibMethodHolder[methods.size()];
		
		int count = 0;
		for(Iterator iter = methods.iterator(); iter.hasNext();)
		{
			this.methods[count++] = (LibMethodHolder)iter.next();
		}
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isInterface() {
		return isInterface;
	}

	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	
	public void incrNumInstances(String codeSampleName, String methodName)
	{
		String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
		if(!visitedPackagesForInstances.contains(packageName)) {
			visitedPackagesForInstances.add(packageName);
			numInstances++;
			
			//Storing the code examples for this method invocation
			CodeExampleStore cesObj = new CodeExampleStore();
			cesObj.filename = codeSampleName;
			cesObj.methodname = methodName;
			cesObj.type = LibMethodHolder.HOOK;
			templateExampleStore.add(cesObj);
		}		

		localNumInstances++;		
	}
	
	public void incrNumExtensions(String codeSampleName, String methodName)
	{
		String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
		if(!visitedPackagesForExtends.contains(packageName)) {
			visitedPackagesForExtends.add(packageName);
			numExtensions++;
			
			//Storing the code examples for this method invocation
			CodeExampleStore cesObj = new CodeExampleStore();
			cesObj.filename = codeSampleName;
			cesObj.methodname = methodName;
			cesObj.type = LibMethodHolder.HOOK;
			hookExampleStore.add(cesObj);
		}
		localNumExtensions++;
	}
	
	public void incrNumImplements(String codeSampleName, String methodName)
	{
		String packageName = RepositoryAnalyzer.getInstance().getCodeSampleIDToPackageMapper().get(codeSampleName);
		if(!visitedPackagesForImplements.contains(packageName)) {
			visitedPackagesForImplements.add(packageName);
			numImplements++;
		}
		localNumImplements++;
	}
	
	public void clearVisitedInfo() 
	{
		visitedPackagesForInstances.clear();
		visitedPackagesForExtends.clear();
		visitedPackagesForImplements.clear();
		
		//Clearing the methods visited info
		for(LibMethodHolder lmh : methods) {
			lmh.clearVisitedInfo();			
		}		
	}
	
	/** 
	 * Function for sorting methods according to their end rankings
	 * This method is used before priting.
	 */
    public void sortMethods()
    {
    	LibMethodHolder insert;
    	for (int next = 1; next < methods.length; next++ )
    	{
		    insert = methods[next];	
		    int moveItem = next;	
		    while ( moveItem > 0 && methods[ moveItem - 1 ].getEndRanking() > insert.getEndRanking())
		    {
		    	methods[ moveItem ] = methods[ moveItem - 1 ];
		        moveItem--;
		    }		
		    methods[ moveItem ] = insert;	     
      	} 
    }
	

	public int getNumExtensions() {
		return numExtensions;
	}

	public int getNumInstances() {
		return numInstances;
	}

	public int getNumImplements() {
		return numImplements;
	}

	public boolean isAbstract() {
		return isAbstract;
	}

	public void setAbstract(boolean isAbstract) {
		this.isAbstract = isAbstract;
	}

	public HashSet<String> getChildTypes() {
		return childTypes;
	}

	public void setChildTypes(HashSet<String> childTypes) {
		this.childTypes = childTypes;
	}

	public List<String> getParameterizedTypes() {
		return parameterizedTypes;
	}

	public String getParentType() {
		return parentType;
	}
	
	public void setParentType(String type) {
		parentType = type;
	}

	public HashSet<String> getAllInterfaceTypes() {
		return allInterfaceTypes;
	}

	public HashSet<String> getAllChildTypes() {
		return allChildTypes;
	}

	public void setAllChildTypes(HashSet<String> allChildTypes) {
		this.allChildTypes = allChildTypes;
	}

	public int getLocalNumExtensions() {
		return localNumExtensions;
	}

	public void setLocalNumExtensions(int localNumExtensions) {
		this.localNumExtensions = localNumExtensions;
	}

	public int getLocalNumImplements() {
		return localNumImplements;
	}

	public void setLocalNumImplements(int localNumImplements) {
		this.localNumImplements = localNumImplements;
	}

	public int getLocalNumInstances() {
		return localNumInstances;
	}

	public void setLocalNumInstances(int localNumInstances) {
		this.localNumInstances = localNumInstances;
	}

	public int getEndRanking() {
		return endRanking;
	}

	public void setEndRanking(int endRanking) {
		this.endRanking = endRanking;
	}

	public int getEndCategory() {
		return endCategory;
	}

	public void setEndCategory(int endCategory) {
		this.endCategory = endCategory;
	}

	public HotspotHierarchyStore getEndHierarchyStore() {
		return endHierarchyStore;
	}

	public void setEndHierarchyStore(HotspotHierarchyStore endHierarchyStore) {
		this.endHierarchyStore = endHierarchyStore;
	}
	
	public String toString() {
		return name;
	}

	public List<CodeExampleStore> getTemplateExampleStore() {
		return templateExampleStore;
	}

	public List<CodeExampleStore> getHookExampleStore() {
		return hookExampleStore;
	}

}
