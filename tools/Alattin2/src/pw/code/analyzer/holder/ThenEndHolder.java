package pw.code.analyzer.holder;

public class ThenEndHolder extends Holder {
	
	public ThenEndHolder(Holder ifh) {
		loopStart = ifh;
	}
	
	public String toString() {
		return "ThenEndHolder:" + loopStart.toString();
	}

}