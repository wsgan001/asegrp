package pw.code.analyzer.holder;


import org.eclipse.jdt.core.dom.Expression;
import pw.code.analyzer.ASTCrawlerUtil;

public class WhileBeginConditionHolder extends Holder {
	//private static Logger logger = Logger.getLogger("WhileBeginConditionHolder");
	public WhileBeginConditionHolder(Expression condExpr) {
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
