package rpt.ncsu.transaction.analysis;

import java.util.ArrayList;

public class Transaction {
	private ArrayList<Object[]> methodCallTree = new ArrayList<Object[]>();
	//private String startTime = "";
	//private String endTime = "";
	//private String responseTime = "";	
	private String signature = "";
	private int currentLevel=0;
	
	public Object[] getLastLeaf(){
		
		return null;		
	}
	
	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public double getAveResponseTime() {
		return aveResponseTime;
	}

	public void setAveResponseTime(double aveResponseTime) {
		this.aveResponseTime = aveResponseTime;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	private String threadId = ""; //doGet threadId, i.e., father thread
	private double aveResponseTime = 0;
	private int total = 0;
	
	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public void addMethodCall(MethodInvocation call, double entryTime, double lastExitTime){
		//MethodInvocation preOne = null;
			
		if (entryTime<lastExitTime){
			//with in the last invocation
			currentLevel=currentLevel+1;	
			MethodInvocation  preOne = (MethodInvocation) this.getMethodInvoke().get(this.getMethodInvoke().size()-1)[1];			
			((MethodInvocation) this.getMethodInvoke().get(this.getMethodInvoke().size()-1)[1]).setAveTimeWithoutInvocations(preOne.getAveTimeWithoutInvocations()-call.getAveResponseTime());;	
			
		}
		else{			
			for (int i=this.getMethodInvoke().size()-1;i>=0;i--){
				MethodInvocation  preOne = (MethodInvocation) this.getMethodInvoke().get(i)[1];
				if(preOne.getEndTime()>entryTime){
					currentLevel = (Integer) this.getMethodInvoke().get(i)[0] + 1;		
					((MethodInvocation) this.getMethodInvoke().get(i)[1]).setAveTimeWithoutInvocations(preOne.getAveTimeWithoutInvocations()-call.getAveResponseTime());;	
					
					break;
				}
			}
		}
		this.getMethodInvoke().add(new Object[]{currentLevel, call});	
		
	}
	
//	public void updateResponseTime(){
//		if(endTime.length()>0&&startTime.length()>0){
//			float end = Float.parseFloat(this.endTime);
//			float start = Float.parseFloat(this.startTime);
//			this.responseTime = Float.toString(end - start);
//		}		
//	}
	
//	public void updateEndTime(){
//		if(this.methodCallSequence.size()>0){			
//			this.endTime = ((MethodInvocation)this.methodCallSequence.get(this.methodCallSequence.size()-1)).getEndTime();
//		}		
//	}
//	
//	public void updateStartTime(){
//		if(this.methodCallSequence.size()>0){			
//			this.endTime = ((MethodInvocation)this.methodCallSequence.get(0)).getStartTime();
//		}		
//	}
	
	public ArrayList<Object[]> getMethodInvoke() {
		return methodCallTree;
	}

	
}
