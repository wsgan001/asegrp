package xweb.code.analyzer.holder;

import edu.uci.ics.jung.graph.Vertex;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;

/**
 * Class that represents error edges
 * @author suresh_thummalapenta
 *
 */
public class ErrorEdgeHolder extends DirectedSparseEdge {

	public ErrorEdgeHolder(Vertex arg0, Vertex arg1) {
		super(arg0, arg1);		
	}
	
	public String toString() {
		return "(E)" + super.toString();
	}

}
