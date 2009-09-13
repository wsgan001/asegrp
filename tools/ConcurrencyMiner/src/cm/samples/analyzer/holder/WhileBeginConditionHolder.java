package cm.samples.analyzer.holder;


import org.eclipse.jdt.core.dom.Expression;

import cm.samples.analyzer.ASTCrawlerUtil;

public class WhileBeginConditionHolder extends Holder {
	public WhileBeginConditionHolder(Expression condExpr) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);
	}
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}
}
