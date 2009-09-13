package framework.reuse;

import java.util.ArrayList;
import java.util.List;

import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.LibClassHolder;

/**
 * Stores the classes of the frameworh that are reused by the code examples
 * @author suresh_thummalapenta
 *
 */
public class LibClassReuse {
	
	LibClassHolder associatedLibClass;
	List<LibMethodReuse> reusedMethods = new ArrayList<LibMethodReuse>(1);
	boolean isInterface = false;
	int frequency = 0; //Stores the number of class is reused	
	int ID = RepositoryAnalyzer.getInstance().reuseClassIdGen++;
	
	public boolean isInterface() {
		return isInterface;
	}
	public void setInterface(boolean isInterface) {
		this.isInterface = isInterface;
	}
	public List<LibMethodReuse> getReusedMethods() {
		return reusedMethods;
	}
	public void addToReusedMethods(LibMethodReuse methodToAdd) {
		reusedMethods.add(methodToAdd);
	}
	
	public int getFrequency() {
		return frequency;
	}
	public void incrFrequency() {
		frequency++;
	}
	public LibClassHolder getAssociatedLibClass() {
		return associatedLibClass;
	}
	public void setAssociatedLibClass(LibClassHolder associatedLibClass) {
		this.associatedLibClass = associatedLibClass;
	}
	
	public String toString() {
		return associatedLibClass.toString();
	}
	public int getID() {
		return ID;
	}
}
