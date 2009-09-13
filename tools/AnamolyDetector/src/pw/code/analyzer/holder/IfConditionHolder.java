package pw.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;
import pw.code.analyzer.ASTCrawlerUtil;

/**
 * This stores the list of variables involved in the conditional statement and
 * the conditional checks performed on those variables
 */
public class IfConditionHolder extends Holder {
	//private static Logger logger = Logger.getLogger("IfConditionHolder");
	public IfConditionHolder(Expression condExpr) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);		
		//A condition to identify additional unhandled cases:
		//logger.info("Condition Expr: " + condExpr + " Variables derived: " + condVarList);
	}	
	
	public String toString() {
		String condVarStr = "";
		
		for(CondVarHolder cv : condVarList) {
			condVarStr += cv + ",";
		}
		
		return condVarStr;
	}
}
