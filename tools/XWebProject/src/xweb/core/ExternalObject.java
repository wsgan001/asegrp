package xweb.core;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import xweb.code.analyzer.holder.MethodInvocationHolder;

/**
 * Class that stores information related to the external object
 * and its APIs referenced by the current library.
 * @author suresh_thummalapenta
 *
 */
public class ExternalObject {
	
	String className;
	List<MethodInvocationHolder> miList = new ArrayList<MethodInvocationHolder>();
	
	String parentClassName;
	Set<String> childClasses = new HashSet<String>(1);
	
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public List<MethodInvocationHolder> getMiList() {
		return miList;
	} 
	
	public boolean equals(Object OtherObj) {
		if(!(OtherObj instanceof ExternalObject))
			return false;
		
		return this.className.equals(((ExternalObject) OtherObj).className);
	}
	
	public int hashCode() {
		int hashVal = 0;
		for(char ch : className.toCharArray()) {
			hashVal += ch;
		}
		return hashVal;
	}

	public MethodInvocationHolder containsMI (MethodInvocationHolder containMI) {
		for(MethodInvocationHolder extMI : miList) {
			if(extMI.equals(containMI)) 
			{	
				return extMI;
			}	
		}
		return null;
	}
	public String getParentClassName() {
		return parentClassName;
	}
	public void setParentClassName(String parentClassName) {
		this.parentClassName = parentClassName;
	}
	public Set<String> getChildClasses() {
		return childClasses;
	}
}
