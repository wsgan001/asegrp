package pw.code.analyzer.holder;

import java.util.HashMap;

/**
 * Another level of abstraction of CondVarHolder that holds a set
 * of condition variables. An instance of this class is main
 * @author suresh_thummalapenta
 *
 */
public class ConditionVarHolderSet {

	HashMap<String, CondVarHolder> totalCondVar;
	HashMap<String, CondVarHolder> selectedCondVar;
	CondVarHolder highestSupportCondition;
	int numEmptyCondVar = 0; //This variable indicates that these many code samples have no conditions on the receiver variable prior to the function call
	
	int filteredType;   //Can be HIGH_CONFIDENCE, AVERAGE_CONFIDENCE, and LOW_CONFIDENCE.
	
	public ConditionVarHolderSet() { 
		totalCondVar = new HashMap<String, CondVarHolder>();
		selectedCondVar = new HashMap<String, CondVarHolder>();
	}
	
	public void incrNumEmptyCondVar() {
		numEmptyCondVar++;
	}
		
	public HashMap<String, CondVarHolder> getTotalCondVar() {
		return totalCondVar;
	}

	public void setTotalCondVar(HashMap<String, CondVarHolder> totalCondVar) {
		this.totalCondVar = totalCondVar;
	}

	public HashMap<String, CondVarHolder> getSelectedCondVar() {
		return selectedCondVar;
	}

	public void setSelectedCondVar(HashMap<String, CondVarHolder> selectedCondVar) {
		this.selectedCondVar = selectedCondVar;
	}

	public int getNumEmptyCondVar() {
		return numEmptyCondVar;
	}

	public void setNumEmptyCondVar(int numEmptyCondVar) {
		this.numEmptyCondVar = numEmptyCondVar;
	}

	public int getFilteredType() {
		return filteredType;
	}

	public void setFilteredType(int filteredType) {
		this.filteredType = filteredType;
	}

	public CondVarHolder getHighestSupportCondition() {
		return highestSupportCondition;
	}

	public void setHighestSupportCondition(CondVarHolder highestSupportCondition) {
		this.highestSupportCondition = highestSupportCondition;
	}	
}
