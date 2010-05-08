package xweb.code.analyzer.holder;

public class ForEndHolder extends Holder {

	ForBeginConditionHolder loopStart = null;
	
	public ForEndHolder(ForBeginConditionHolder loopStart) {
		this.loopStart = loopStart;
	}
	
	public String toString() {
		return "END OF " + loopStart.toString();
	}	
	
}
