package pw.code.analyzer.holder;

public class ForEndHolder extends Holder {

	public ForEndHolder(ForBeginConditionHolder loopStart) {
		this.loopStart = loopStart;
	}
	
	public String toString() {
		return "ForEndHolder:" + loopStart.toString();
	}

	public Holder getLoopStart() {
		return loopStart;
	}
	
}
