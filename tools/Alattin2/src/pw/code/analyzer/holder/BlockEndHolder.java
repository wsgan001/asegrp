package pw.code.analyzer.holder;

/**
 * A dummy class to distinguish a general holder object from the end of the
 * block object
 * @author suresh_thummalapenta
 *
 */
public class BlockEndHolder extends Holder {
	
	public BlockEndHolder() {
		
	}
	
	public void setLoopStart(Holder loopS) {
		loopStart = loopS;
	}
	
	public String toString(){
		if(loopStart != null) {
			return "BlockEndHolder:" + loopStart.toString();
		} else {
			return "BlockEndHolder:";
		}
			
	}
	
}
