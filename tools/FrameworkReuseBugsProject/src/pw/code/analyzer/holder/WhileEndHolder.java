package pw.code.analyzer.holder;

public class WhileEndHolder extends Holder {

	public WhileEndHolder(Holder loopStart) {
		this.loopStart = loopStart;
	}
	
	public String toString() {
		return loopStart.toString();
	}

	public Holder getLoopStart() {
		return loopStart;
	}
	
}