package rpt.ncsu.transaction.analysis;

public class ClassDef {

	public String threadIdRef;
	public String name;
	//public String sourceName;
	public String classId;
	public String objIdRef;
	public String time;
	
	public ClassDef(String threadIdRef, String name, String classId, String objIdRef, String time) {
		// TODO Auto-generated constructor stub
		this.threadIdRef = threadIdRef;
		this.name = name;
		//this.sourceName = sourceName;
		this.classId = classId;
		this.objIdRef = objIdRef;
		this.time = time;
	}

}
