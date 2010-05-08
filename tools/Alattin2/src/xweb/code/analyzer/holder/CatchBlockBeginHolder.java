package xweb.code.analyzer.holder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Type;
import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.common.CommonConstants;

public class CatchBlockBeginHolder extends ExceptionHandlerHolder {
	
	String exceptionStr;
	Holder lastStmtHolder = null;
	boolean bEmptyCatchBlk = false;
	
	public CatchBlockBeginHolder(TryBlockBeginHolder tbhObj, Type exceptionType) {
		this.tbhObj = tbhObj;		
		if(exceptionType != null)
			exceptionStr = ASTCrawlerUtil.getFullClassName(exceptionType);
	}
	
	public String toString() {
		return "CATCH BLOCK (" + exceptionStr + ")";
	}

	/** GETTERS and SETTERS **/
	public Holder getLastStmtHolder() {
		return lastStmtHolder;
	}

	public void setLastStmtHolder(Holder lastStmtHolder) {
		this.lastStmtHolder = lastStmtHolder;
		
		if(lastStmtHolder == this) {
			bEmptyCatchBlk = true;	
		}	
	}

	public String getExceptionStr() {
		return exceptionStr;
	}

	public void setExceptionStr(String exceptionStr) {
		this.exceptionStr = exceptionStr;
	}

	public boolean isBEmptyCatchBlk() {
		return bEmptyCatchBlk;
	}	
}
