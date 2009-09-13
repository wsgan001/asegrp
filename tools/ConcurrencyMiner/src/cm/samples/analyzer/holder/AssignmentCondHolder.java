package cm.samples.analyzer.holder;

import java.util.List;

/**
 * A special class that holds conditions when defined in a part of assignment statment
 * @author suresh_thummalapenta
 *
 */
public class AssignmentCondHolder extends Holder {

	public AssignmentCondHolder(List<CondVarHolder> list) {
		condVarList = list;
	}	
}
