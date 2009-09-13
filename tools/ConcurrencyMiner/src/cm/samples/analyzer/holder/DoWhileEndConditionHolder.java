package cm.samples.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

import cm.samples.analyzer.ASTCrawlerUtil;

public class DoWhileEndConditionHolder extends Holder {
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
	}	
}
