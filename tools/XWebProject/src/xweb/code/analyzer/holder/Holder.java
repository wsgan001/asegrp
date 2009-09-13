package xweb.code.analyzer.holder;

import java.util.List;
import java.util.Set;

import xweb.code.analyzer.MinedPattern;
import xweb.code.analyzer.StackObject;
import xweb.code.analyzer.CodeExampleStore;
import xweb.core.NormalErrorPath;
import xweb.core.MIHList;
import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;


/**
 * A generic class for all Holder classes
 * @author suresh_thummalapenta
 * TODO: As the code very much dependent on MethodInvocationHolder, It is still left as parent
 */
public class Holder extends SimpleDirectedSparseVertex implements StackObject {

	private boolean isDummy = false;
	public static Holder getDummyHolder()
	{
		Holder dummy = new Holder();
		dummy.isDummy = true;
		return dummy;
	}
	
	public static boolean isDummyHolder(Holder dummy)
	{
		if(dummy.isDummy)
		{
			return true;
		}
		
		return false;
	}
	
	public String toString() {
		return "DUMMY";
	}
	
	//Specific methods for LibMethodHolder and MethodInvocationHolder
	public void addToErrorPathAPIs(MethodInvocationHolder errorMIH, String fileName, String methodName, List<MethodInvocationHolder> contextList) {}
	public List<CodeExampleStore> getCodeExamples() {return null;}
	public void incrNoErrorPathCount() {}
	
	//A function that keeps track of number of call sites that are used during mining
	int numCallSitesFound = 0;
	public void incrNumCallSites() { numCallSitesFound++; }
	public int getNumCallSites() { return numCallSitesFound;}
	//End of functions defined specific to LibMethodHolder and MethodInvocationHolder that helps to keep track of number of call sites
	
	public List<MethodInvocationHolder> getErrorMIHList() {return null;}
	public List<IntHolder> getErrorMIFrequency() {return null;}
	public int getNoErrorPathCount() {return -1;}
	public List<String> getThrowsExceptionList() {return null;}	
	public void addToMinedPatterns(MinedPattern mpObj) {};
	public List<MinedPattern> getMinedPatterns() {return null;}
	public String getActualReturnType() {return "";}
	public String getPrintString() {return "";} 
	public List<MIHList> getAssociatedPatternContext() { return null;}
	public Set<String> getExceptionSet() {return null;}
	
	//Specific to Association Rule Miner data collection
	public void addMIHId(int mihId) {}
	public void addToNormalErrorList(List<MethodInvocationHolder> normalMIHList, List<MethodInvocationHolder> errorMIHList, String filename, String methodName) {}
	public List<NormalErrorPath> getNormal_error_APIList() { return null; }
	public void setNormal_error_APIList(List<NormalErrorPath> normal_error_APIList) {}
	public int getKey() {return -1;}
}
