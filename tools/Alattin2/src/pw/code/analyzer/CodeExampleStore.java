package pw.code.analyzer;

/**
 * Class for preserving the related example from which the sequence
 * is extracted, esp. for debugging purpose
 */

public class CodeExampleStore {
	public String javaFileName;	//Java file name in which the sequence is found 
	public String methodName;		//Name of the method inside the java file, where the sequence is found
}
