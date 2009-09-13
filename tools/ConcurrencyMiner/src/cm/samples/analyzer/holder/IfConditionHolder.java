package cm.samples.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

import cm.samples.analyzer.ASTCrawlerUtil;

/**
 * This stores the list of variables involved in the conditional statement and
 * the conditional checks performed on those variables
 */
public class IfConditionHolder extends Holder {
	
	public IfConditionHolder(Expression condExpr) {
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
