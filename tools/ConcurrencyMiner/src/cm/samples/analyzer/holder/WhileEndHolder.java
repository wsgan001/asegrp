package cm.samples.analyzer.holder;

public class WhileEndHolder extends Holder {

	public WhileEndHolder(Holder loopStart) {
		this.loopStart = loopStart;
	}
	
	public String toString() {
		return "WhileEndHolder:" + loopStart.toString();
	}

	public Holder getLoopStart() {
		return loopStart;
	}	
}
