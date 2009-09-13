package pw.code.analyzer.holder;

import java.util.HashSet;
import java.util.Set;

import pw.common.CommonConstants;

/** 
 * @author suresh_thummalapenta
 */
public class PrePostPathHolder {

	Set<Holder> prePath = null;
	Set<Holder> postPath = null;
	
	public int balanced = 0; // 0: Balanced, 1: Imbalanced	
	public double globalSupport = 0.0;
	public double localSupport = 0.0;
	
	public int special_balance_flag = CommonConstants.DFC_SINGLE_CHECK_PATTERNS; //A special flag only for collecting stats

	public PrePostPathHolder(Set<Holder> prePath, Set<Holder> postPath) {	
		this.prePath = new HashSet<Holder>();
		this.prePath.addAll(prePath);
		
		this.postPath = new HashSet<Holder>();
		this.postPath.addAll(postPath);			
	}

	public Set<Holder> getPostPath() {
		return postPath;
	}

	public Set<Holder> getPrePath() {
		return prePath;
	}
	
	public String toString() {
		StringBuffer returnString = new StringBuffer();
		
		for(Holder cvh_th : prePath) {
			returnString.append(cvh_th.toString() + "(0)&&");		
		}
		
		for(Holder cvh_th : postPath) {
			returnString.append(cvh_th.toString() + "(1)&&");		
		}
		
		return returnString.toString();
	}
	
	//Two PrePostPathHolders are equal when  
	public boolean equals(Object inputObj) {		
		if(!(inputObj instanceof PrePostPathHolder)) {
			return false;
		}
		
		PrePostPathHolder inpObj = (PrePostPathHolder) inputObj;
		if(this.prePath.containsAll(inpObj.prePath) && this.postPath.containsAll(inpObj.postPath)) {
			return true;
		}		
		return false;	
	}
}
