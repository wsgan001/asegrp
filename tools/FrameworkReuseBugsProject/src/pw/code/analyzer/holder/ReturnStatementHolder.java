package pw.code.analyzer.holder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.SimpleName;

import pw.code.analyzer.ASTCrawlerUtil;
import pw.code.analyzer.TypeHolder;

public class ReturnStatementHolder extends Holder {

	List<CondVarHolder> condList = new ArrayList<CondVarHolder>();
	String varName = "";
	
	public void setReturnVarName(Expression associatedExpression) {
		if(associatedExpression == null) {
			return;
		}
		
		if(associatedExpression instanceof SimpleName) {
			varName = ((SimpleName)associatedExpression).getIdentifier();
		}
	}
	
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

	public String getVarName() {
		return varName;
	}
}
