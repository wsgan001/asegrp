package xweb.code.analyzer.holder;

public class WhileEndHolder extends Holder {

	WhileBeginConditionHolder loopStart;
	
	public WhileEndHolder(WhileBeginConditionHolder loopStart) {
		this.loopStart = loopStart;
	}
	
	public String toString() {
		return "END OF " + loopStart.toString();
	}
	
}
