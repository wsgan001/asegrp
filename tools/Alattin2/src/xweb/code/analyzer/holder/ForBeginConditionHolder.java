package xweb.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

public class ForBeginConditionHolder extends Holder {
	/*private static Logger logger = Logger.getLogger("ForBeginConditionHolder");
	ArrayList<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();
	public ForBeginConditionHolder(Expression condExpr) {	
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);	
	}
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}*/	
	
	public ForBeginConditionHolder(Expression condExpr) {	
			
	}
	
	public String toString() {
		return "FOR BEGIN";
	}
}
