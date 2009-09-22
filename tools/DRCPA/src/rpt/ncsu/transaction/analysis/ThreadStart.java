package rpt.ncsu.transaction.analysis;

public class ThreadStart {
	public String threadId;	
	public String threadName;
	public String groupName;
	public String parentName;
	
	public ThreadStart(String threadId, String threadName, String groupName, String parentName) {
		// TODO Auto-generated constructor stub
		this.threadId = threadId;		
		this.threadName = threadName;
		this.groupName = groupName;
		this.parentName = parentName;		
	}
		
}
