package rpt.ncsu.transaction.analysis;

import java.text.DecimalFormat;
import java.text.NumberFormat;

public class MethodInvocation {
	private String className = "";
	private String methodName = "";
	//private String startTime = "";
	private double endTime = 0;
	//private String responseTime = "";
	private double aveResponseTime =0;
	private double aveTimeWithoutInvocations =0;
		
	public double getEndTime() {
		return endTime;
	}

	public void setEndTime(double endTime) {
		this.endTime = endTime;
	}

	
	public double getAveTimeWithoutInvocations() {
		return aveTimeWithoutInvocations;
	}

	public void setAveTimeWithoutInvocations(double aveTimeWithoutInvocations) {
		this.aveTimeWithoutInvocations = aveTimeWithoutInvocations;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	
	public double getAveResponseTime() {
		return aveResponseTime;
	}

	public void setAveResponseTime(double aveResponseTime) {
		this.aveResponseTime = aveResponseTime;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	private int total =0;

//	public void updateResponseTime() {
//		if (endTime.length() > 0 && startTime.length() > 0) {
//			double response = Double.parseDouble(this.endTime)-Double.parseDouble(this.startTime);
//			//double start = Double.parseDouble(this.startTime);			
//			NumberFormat formatter = new DecimalFormat("###.#####");			 
//			this.responseTime = formatter.format(response); 			
//		}
//	}

	

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	

	public MethodInvocation() {
		// TODO Auto-generated constructor stub
	}


	public double getTotal() {
		// TODO Auto-generated method stub
		return total;
	}

}
