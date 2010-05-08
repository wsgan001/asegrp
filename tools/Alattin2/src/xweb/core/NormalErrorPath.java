package xweb.core;

import java.util.List;

import xweb.code.analyzer.holder.MethodInvocationHolder;

/**
 * A Class that holds normal and error path APIs combination for a method invocation
 * @author suresh_thummalapenta 
 */
public class NormalErrorPath {

	List<MethodInvocationHolder> normalPath = null;
	List<MethodInvocationHolder> errorPath = null;
	
	public NormalErrorPath(List<MethodInvocationHolder> normalPath, List<MethodInvocationHolder> errorPath) {
		this.normalPath = normalPath;
		this.errorPath = errorPath;	
	}

	public List<MethodInvocationHolder> getErrorPath() {
		return errorPath;
	}

	public List<MethodInvocationHolder> getNormalPath() {
		return normalPath;
	}
	
	
}
