package xweb.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

public class DoWhileEndConditionHolder extends Holder {
	/*private static Logger logger = Logger.getLogger("DoWhileEndConditionHolder");
	ArrayList<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();
	DoWhileBeginHolder loopStart = null;
	
	public DoWhileEndConditionHolder(Expression condExpr, DoWhileBeginHolder loopStart) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);
		this.loopStart = loopStart;
	}
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}*/	
	
	public DoWhileEndConditionHolder(Expression condExpr, DoWhileBeginHolder loopStart) {
	}

}
