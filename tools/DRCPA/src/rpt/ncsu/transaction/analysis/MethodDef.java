package rpt.ncsu.transaction.analysis;

public class MethodDef {
	private String classIdRef;				
	private String methodId;
	private String methodName;	
	
	public String getClassIdRef() {
		return classIdRef;
	}

	public void setClassIdRef(String classIdRef) {
		this.classIdRef = classIdRef;
	}

	public String getMethodId() {
		return methodId;
	}

	public void setMethodId(String methodId) {
		this.methodId = methodId;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public MethodDef(String classIdRef, String methodId, String methodName){
		this.classIdRef = classIdRef;
		this.methodId = methodId;
		this.methodName = methodName;		
	}
}
