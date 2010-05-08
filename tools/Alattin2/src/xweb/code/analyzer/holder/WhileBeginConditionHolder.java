package xweb.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

public class WhileBeginConditionHolder extends Holder {
	/*private static Logger logger = Logger.getLogger("WhileBeginConditionHolder");
	ArrayList<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();
	
	public WhileBeginConditionHolder(Expression condExpr) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);		
	}
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}*/	
	
	public WhileBeginConditionHolder(Expression condExpr) {
			
	}
	
	public String toString() {
		return "WHILE BLOCK";
	}
}
