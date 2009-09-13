package cm.samples.analyzer.holder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;

import cm.samples.analyzer.ASTCrawlerUtil;
import cm.samples.analyzer.TypeHolder;


public class ReturnStatementHolder extends Holder {

	List<CondVarHolder> condList = new ArrayList<CondVarHolder>();

	public void setAssociatedExpression(Expression associatedExpression) {
		ASTCrawlerUtil.loadConditionalVariables(associatedExpression, condList);		
	}

	public boolean isReturnStmtContains(TypeHolder thObj) {		
		for(CondVarHolder cvh : condList) {
			if(cvh.varName.equals(thObj.var)) {
				return true;
			}		
		}		
		return false;
	}
}
