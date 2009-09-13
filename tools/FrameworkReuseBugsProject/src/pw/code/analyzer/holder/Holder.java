package pw.code.analyzer.holder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import minebugs.core.RepositoryAnalyzer;

import pw.code.analyzer.StackObject;
import pw.code.analyzer.TypeHolder;
import pw.common.CommonConstants;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;


/**
 * A generic class for all Holder classes
 * @author suresh_thummalapenta
 * TODO: As the code very much dependent on MethodInvocationHolder, It is still left as parent
 */
public class Holder extends SimpleDirectedSparseVertex implements StackObject {

	private boolean isDummy = false;
	protected List<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();
	protected Holder loopStart = null;
	public static Holder getDummyHolder()
	{
		Holder dummy = new Holder();
		dummy.isDummy = true;
		return dummy;
	}
	
	public static boolean isDummyHolder(Holder dummy)
	{
		if(dummy.isDummy)
		{
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		return "DUMMY";
	}

	/**
	 * Checks whether there are any conditions associated with the given variable
	 * @param thObj
	 * @return
	 */
	public List<CondVarHolder> contains(TypeHolder thObj) {
		Iterator iter = condVarList.iterator();
		
		List<CondVarHolder> foundList = new ArrayList<CondVarHolder>();
		while(iter.hasNext()) {
			CondVarHolder cvh = (CondVarHolder) iter.next();
			if(cvh.varName.equals(thObj.var)) {
				foundList.add(cvh);
			}
		}		
		return foundList;
	}
	
	/**
	 * Checks whether any method invocation exists in the condition with the same receiver variable.
	 * @param thObj
	 * @return
	 */
	public List<CondVarHolder> containsMIs(TypeHolder thObj, int traversalDirection) {
		Iterator iter = condVarList.iterator();
		
		List<CondVarHolder> foundList = new ArrayList<CondVarHolder>();
		while(iter.hasNext()) {
			CondVarHolder cvh = (CondVarHolder) iter.next();
			MethodInvocationHolder assocMIH = cvh.getAssociatedMIH();
			String type = assocMIH != null ? assocMIH.getReceiverClass().getType() : "NONE";
			//Extended this function for identifying preceding conditions on other kinds
			//of variables belonging to list of external obj
			if(assocMIH != null) {
				if(assocMIH.getReceiverClass().var.equals(thObj.var)) {
					foundList.add(cvh);
				} /*else if(traversalDirection == CommonConstants.BACKWARD_TRAVERSAL && 
						RepositoryAnalyzer.getInstance().getExternalObjects().get(type) != null) {
					foundList.add(cvh);
				}*/			
			}
		}		
		return foundList;
	}

	public List<CondVarHolder> getCondVarList() {
		return condVarList;
	}
	
	public Holder getLoopStart() {
		return loopStart;
	}
}
