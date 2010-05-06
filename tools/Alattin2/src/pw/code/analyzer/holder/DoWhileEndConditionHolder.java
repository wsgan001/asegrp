package pw.code.analyzer.holder;

import org.eclipse.jdt.core.dom.Expression;
import pw.code.analyzer.ASTCrawlerUtil;

public class DoWhileEndConditionHolder extends Holder {
	//private static Logger logger = Logger.getLogger("DoWhileEndConditionHolder");
	public DoWhileEndConditionHolder(Expression condExpr, DoWhileBeginHolder loopStart) {
		ASTCrawlerUtil.loadConditionalVariables(condExpr, condVarList);
		this.loopStart = loopStart;
		
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
