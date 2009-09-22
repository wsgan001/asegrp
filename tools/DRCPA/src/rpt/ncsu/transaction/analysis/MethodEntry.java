package rpt.ncsu.transaction.analysis;

public class MethodEntry {

	public String threadIdRef;	
	public double time;
	public String methodIdRef;
	public String objIdRef;
	public String classIdRef;	
	public String ticket;
	
	public MethodEntry(String threadIdRef, double time, String methodIdRef, String objIdRef, String classIdRef, String ticket) {
		// TODO Auto-generated constructor stub
		this.threadIdRef = threadIdRef;		
		this.time = time;
		this.methodIdRef = methodIdRef;
		this.objIdRef = objIdRef;
		this.classIdRef = classIdRef;
		this.ticket = ticket;
	}

}
