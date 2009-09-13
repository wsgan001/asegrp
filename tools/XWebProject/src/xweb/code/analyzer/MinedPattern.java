package xweb.code.analyzer;

import java.util.Comparator;

import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.core.MIHList;

public class MinedPattern implements Comparator {

	Holder parentMID;	
	MIHList childMID;
	double support;
	int childMIDFrequency;
	boolean bIntMethodPattern = false;
	MIHList pcObj;	//An object that stores the context in which this mined pattern is valid
	
	//Constants for support category
	public final static int HIGH_CONFIDENCE = 0;
	public final static int AVERAGE_CONFIDENCE = 1;
	public final static int LOW_CONFIDENCE = 2;
	
	int support_category = 10;
	
	public MinedPattern(Holder parentMID, MIHList childMID, double support, int childMIDFrequency, MIHList pcObj) {
		super();
		this.parentMID = parentMID;
		this.childMID = childMID;
		this.support = support;
		this.childMIDFrequency = childMIDFrequency;
		bIntMethodPattern = false;
		this.pcObj = pcObj;
	}
	
	public MinedPattern() {
		
	}

	public int compare(Object arg0, Object arg1) {
		
		MinedPattern m1Obj = (MinedPattern) arg0;
		MinedPattern m2Obj = (MinedPattern) arg1;
		
		if(m1Obj.support_category < m2Obj.support_category) {
			return -1;
		} else if(m1Obj.support_category > m2Obj.support_category) {
			return 1;
		}
		
		if(m1Obj.support > m2Obj.support)
			return -1;
		else if(m1Obj.support < m2Obj.support)
			return 1;
		
		if(m1Obj.childMIDFrequency > m2Obj.childMIDFrequency)
			return -1;
		else if(m1Obj.childMIDFrequency < m2Obj.childMIDFrequency)
			return 1;
		
		if(m1Obj.parentMID.getNoErrorPathCount() < m2Obj.parentMID.getNoErrorPathCount())
			return -1;
		else if(m1Obj.parentMID.getNoErrorPathCount() > m2Obj.parentMID.getNoErrorPathCount())
			return 1;
		
		return -1;
	}
	
	public String toString() {
		return parentMID.toString() + "\n" + childMID.toString(); 
	}

	public int getSupport_category() {
		return support_category;
	}

	public void setSupport_category(int support_category) {
		this.support_category = support_category;
	}

	public double getSupport() {
		return support;
	}

	public void setSupport(double support) {
		this.support = support;
	}

	public static int getAVERAGE_CONFIDENCE() {
		return AVERAGE_CONFIDENCE;
	}

	public static int getHIGH_CONFIDENCE() {
		return HIGH_CONFIDENCE;
	}

	public static int getLOW_CONFIDENCE() {
		return LOW_CONFIDENCE;
	}

	public boolean isBIntMethodPattern() {
		return bIntMethodPattern;
	}

	public MIHList getChildMID() {
		return childMID;
	}

	public int getChildMIDFrequency() {
		return childMIDFrequency;
	}

	public Holder getParentMID() {
		return parentMID;
	}

	public MIHList getPcObj() {
		return pcObj;
	}	
}
