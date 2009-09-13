package xweb.core;

import java.util.Iterator;
import java.util.List;
import xweb.code.analyzer.holder.MethodInvocationHolder;

/**
 * A class that stores the context of a pattern in the form of sequence of method-invocations
 * @author suresh_thummalapenta*
 */
		
public class MIHList {

	List<MethodInvocationHolder> mihList = null;
	
	public MIHList(List<MethodInvocationHolder> mihList) {
		this.mihList = mihList;
	}
	
	public boolean equals(Object obj) {
		MIHList pcObj = (MIHList) obj;
		return equalsToList(pcObj.mihList);
	}
	
	public boolean equalsToList(List<MethodInvocationHolder> inplist) {
		if(this.mihList != null && inplist != null) {
			if(this.mihList.size() != inplist.size())
				return false;
			
			Iterator thisIter = mihList.iterator();
			Iterator inpIter = inplist.iterator();
			
			while(inpIter.hasNext()) {
				MethodInvocationHolder thisMIH = (MethodInvocationHolder) thisIter.next();
				MethodInvocationHolder inpMIH = (MethodInvocationHolder) inpIter.next();
				
				if(!thisMIH.equals(inpMIH))
					return false;
			}
		} else {
			if(this.mihList == null && inplist == null)
				return true;
			else
				return false;
		}
		return true;
	}
	
	public String toString() {
		if(mihList == null) {
			return "";
		}
		
		String retStr = "";
		for(MethodInvocationHolder mihObj : mihList) {
			retStr += mihObj.getPrintString() + "#::#";
		}		
		
		return retStr;	
	}

	public List<MethodInvocationHolder> getMihList() {
		return mihList;
	}
	
	
}
