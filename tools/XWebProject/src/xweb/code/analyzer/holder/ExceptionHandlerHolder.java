package xweb.code.analyzer.holder;

/**
 * Super Class for both catch clause and Finally blocks
 * @author suresh_thummalapenta
 *
 */
public class ExceptionHandlerHolder extends Holder {
	TryBlockBeginHolder tbhObj = null;
	
	public TryBlockBeginHolder getTbhObj() {
		return tbhObj;
	}

	public void setTbhObj(TryBlockBeginHolder tbhObj) {
		this.tbhObj = tbhObj;
	}
}
