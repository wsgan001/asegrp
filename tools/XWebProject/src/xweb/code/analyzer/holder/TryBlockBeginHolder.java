package xweb.code.analyzer.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.TryStatement;

import xweb.common.CommonConstants;

public class TryBlockBeginHolder extends Holder {

	List<CatchBlockBeginHolder> cbHolderList = new ArrayList<CatchBlockBeginHolder>();
	FinallyBlockBeginHolder fbhObj = null;
	Block associatedBlk = null;
	
	Holder lastStmtHolder = null;
	
	public TryBlockBeginHolder(TryStatement tryObj) {
		this.associatedBlk = tryObj.getBody();
		for(Iterator iter = tryObj.catchClauses().iterator(); iter.hasNext();) {
			CatchClause ccObj = (CatchClause) iter.next();
			CatchBlockBeginHolder cbhObj = new CatchBlockBeginHolder(this, ccObj.getException().getType());
			cbHolderList.add(cbhObj);						
		}	
		
		if(tryObj.getFinally() != null) {
			fbhObj = new FinallyBlockBeginHolder(this, tryObj.getFinally());
		}	
	}
	
	public String toString() {
		return "TRY BLOCK";
	}
	
	public CatchBlockBeginHolder getCatchHolder(String exceptionName) {		
		for(CatchBlockBeginHolder cbbObj : cbHolderList) {
			if(cbbObj.exceptionStr.equals(exceptionName))
				return cbbObj;
		}
		
		return null;		
	}	
	
	/** GETTERS & SETTERS **/
	public List<CatchBlockBeginHolder> getCbHolderList() {
		return cbHolderList;
	}
	public void setCbHolderList(List<CatchBlockBeginHolder> cbHolderList) {
		this.cbHolderList = cbHolderList;
	}
	public FinallyBlockBeginHolder getFbhObj() {
		return fbhObj;
	}
	public void setFbhObj(FinallyBlockBeginHolder fbhObj) {
		this.fbhObj = fbhObj;
	}

	public Block getAssociatedBlk() {
		return associatedBlk;
	}

	public void setAssociatedBlk(Block associatedBlk) {
		this.associatedBlk = associatedBlk;
	}

	public Holder getLastStmtHolder() {
		return lastStmtHolder;
	}

	public void setLastStmtHolder(Holder lastStmtHolder) {
		this.lastStmtHolder = lastStmtHolder;
	}	

}
