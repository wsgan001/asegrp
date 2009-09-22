package rpt.ncsu.transaction.analysis;

public class MethodExit {
	
	public String threadIdRef;	
	public double time;
	public String methodIdRef;
	public String objIdRef;	
	public String classIdRef;		
	public String overhead;
	public String ticket;
	
	public MethodExit(String threadIdRef, double time, String methodIdRef, String objIdRef, String classIdRef, String overhead, String ticket) {
		// TODO Auto-generated constructor stub
		this.threadIdRef = threadIdRef;		
		this.time = time;
		this.methodIdRef = methodIdRef;
		this.objIdRef = objIdRef;
		this.classIdRef = classIdRef;
		this.overhead = overhead;
		this.ticket = ticket;
	}

	public MethodExit() {
		// TODO Auto-generated constructor stub
	}

}
