package framework.reuse;

import java.util.ArrayList;
import java.util.List;

import pw.code.analyzer.holder.CondVarHolder;
import pw.code.analyzer.holder.ConditionVarHolderSet;
import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.LibMethodHolder;

public class LibMethodReuse {
	
	LibMethodHolder associatedLibMethodHolder;
	
	int frequency; //number of times the method is reused
	List<String> mihTransactions = new ArrayList<String>(1);	
	
	ConditionVarHolderSet argumentCondVars[];
	ConditionVarHolderSet returnCondVar;
	
	public boolean bCondVarsInitialized = false;
	int ID = RepositoryAnalyzer.getInstance().reuseMethodIdGen++;
	
	public void initializeCondVarMaps() {
		int noOfArguments = associatedLibMethodHolder.getArgTypes().length;
		argumentCondVars = new ConditionVarHolderSet[associatedLibMethodHolder.getArgTypes().length];
		for(int tcnt = 0; tcnt < noOfArguments; tcnt++) {
			argumentCondVars[tcnt] = new ConditionVarHolderSet();
		}	
		returnCondVar = new ConditionVarHolderSet();
		bCondVarsInitialized = true;
	}
	
	public int getFrequency() {
		return frequency;
	}
	public void incrFrequency() {
		frequency++;
	}
	
	public LibMethodHolder getAssociatedLibMethodHolder() {
		return associatedLibMethodHolder;
	}
	public void setAssociatedLibMethodHolder(
			LibMethodHolder associatedLibMethodHolder) {
		this.associatedLibMethodHolder = associatedLibMethodHolder;
	}
	
	public String toString() {
		return associatedLibMethodHolder.toString();
	}
	
	public void addToArgumentCondVarMaps(CondVarHolder cvhObj, int argPosition) {
		if(!bCondVarsInitialized) {
			initializeCondVarMaps();
		}
		
		CondVarHolder cloneCVH = cvhObj.clone();
		cloneCVH.setVarName("");
		CondVarHolder existingCVH = argumentCondVars[argPosition].getTotalCondVar().get(cloneCVH.toString());
		if(existingCVH == null) {
			argumentCondVars[argPosition].getTotalCondVar().put(cloneCVH.toString(), cloneCVH);
		} else {
			existingCVH.incrFrequency();
		}	
	}
	
	public void incrNumNoneArgumentCondVars(int argPosition) {
		argumentCondVars[argPosition].incrNumEmptyCondVar();
	}

	public void incrNumNoneReturnCondVar() {
		returnCondVar.incrNumEmptyCondVar();
	}
	
	public void addToReturnCondVarMaps(CondVarHolder cvhObj) {
		if(!bCondVarsInitialized) {
			initializeCondVarMaps();
		}
		
		CondVarHolder cloneCVH = cvhObj.clone();
		cloneCVH.setVarName("");
		CondVarHolder existingCVH = returnCondVar.getTotalCondVar().get(cloneCVH.toString());
		if(existingCVH == null) {
			returnCondVar.getTotalCondVar().put(cloneCVH.toString(), cloneCVH);
		} else {
			existingCVH.incrFrequency();
		}	
	}
	
	public void addToMihTransactions(String transStr) {
		mihTransactions.add(transStr);
	}	
	
	public List<String> getTransactions() {
		return mihTransactions;
	}

	public ConditionVarHolderSet[] getArgumentCondVars() {
		return argumentCondVars;
	}

	public ConditionVarHolderSet getReturnCondVar() {
		return returnCondVar;
	}

	public int getID() {
		return ID;
	}
}
