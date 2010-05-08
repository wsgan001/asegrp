package xweb.code.analyzer.holder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Block;

import xweb.common.CommonConstants;

public class FinallyBlockBeginHolder extends ExceptionHandlerHolder {

	Block associatedBlockObj = null;

	public FinallyBlockBeginHolder(TryBlockBeginHolder tbhObj, Block associatedBlockObj) {
		this.tbhObj = tbhObj;
		this.associatedBlockObj = associatedBlockObj;
	}
	
	public String toString() {
		return  "FINALLY BLOCK";
	}
	
	/** GETTERS & SETTERS **/
	public Block getAssociatedBlockObj() {
		return associatedBlockObj;
	}

	public void setAssociatedBlockObj(Block associatedBlockObj) {
		this.associatedBlockObj = associatedBlockObj;
	}
}
