package cm.samples.analyzer.holder;

public class ElseEndHolder extends Holder {

	public ElseEndHolder(Holder ifch) {
		loopStart = ifch;
	}
	
	public String toString(){
		return "ElseEndHolder:" + loopStart.toString();
	}
	
}
