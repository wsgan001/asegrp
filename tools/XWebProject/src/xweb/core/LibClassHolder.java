package xweb.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

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
	

	int numInstances = 0;	//Number of times this class is instantiated using its constructors
	int numExtensions = 0;	//Number of times this class is extended to alter the behaviour of the methods
	int numImplements = 0;	//Number of times the class is implemented (applied to interfaces)
	
	public LibClassHolder(String classSig)
	{
		name = classSig;
	}
	
	public boolean equals(Object lchObj)
	{
		if(!(lchObj instanceof LibClassHolder))
		{
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
	
	public void incrNumInstances()
	{
		numInstances++;
	}
	
	public void incrNumExtensions()
	{
		numExtensions++;
	}
	
	public void incrNumImplements()
	{
		numImplements++;
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

}
