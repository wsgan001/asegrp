package xweb.code.analyzer.holder;

import java.util.Comparator;

/**
 * An alternative class for holding an integer value
 * so that it can be used in storing lists and all
 * @author suresh_thummalapenta
 *
 */
public class IntHolder implements Comparator {

	int value = 0;
	
	public IntHolder() {}
	public IntHolder(int value) {
		this.value = value;
	}
	
	public boolean equals(Object obj) {
		return this.value == ((IntHolder)obj).value;
	}
	
	public int compare(Object arg1, Object arg2) {
		IntHolder obj1 = (IntHolder) arg1;
		IntHolder obj2 = (IntHolder) arg2;
		
		return obj1.value - obj2.value;
		
	}
	
	public String toString() {
		return "" + value;
	}
	
	public void incrValue() {
		value++;
	}
	
	public int intValue() {
		return value;
	}
	
}
