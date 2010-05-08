package xweb.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;

/**
 * This stores the list of variables involved in the conditional statement and
 * the conditional checks performed on those variables
 */
public class IfConditionHolder extends Holder {
	/*private static Logger logger = Logger.getLogger("IfConditionHolder");
	ArrayList<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();	
	
	public IfConditionHolder(Expression condExpr) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);		
	}	
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}*/
	
	public IfConditionHolder(Expression condExpr) {
			
	}
	
}
