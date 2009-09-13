package api.usage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * A class that stores a list of templates or hooks related by their inheritance structure
 * @author suresh_thummalapenta
 *
 */
public class HotspotHierarchyStore implements Comparator {

	private List<LibClassHolder> lchList = new ArrayList<LibClassHolder>();
	LibClassHolder baseClass;
	List<HotspotHierarchyStore> dependentHierarchies = new ArrayList<HotspotHierarchyStore>();
	int hierarchyCategory;
	
	//The hierarchy store automatically inherits the least rank 
	//among all the elements of its children
	private int endRanking = 999999; 
	
	
	public String toString() {
		return baseClass.toString() + " :: " + lchList.toString(); 
	}
	
	public LibClassHolder getBaseClass() {
		return baseClass;
	}
	
	public void setBaseClass(LibClassHolder baseClass) {
		this.baseClass = baseClass;
	}
	
	public List<LibClassHolder> getLchList() {
		return lchList;
	}
	
	public void addToLchList(LibClassHolder lchObj) {
		lchList.add(lchObj);
		if(endRanking > lchObj.getEndRanking()) {
			endRanking = lchObj.getEndRanking();
		}
	}

	public int getEndRanking() {
		return endRanking;
	}

	public int getHierarchyCategory() {
		return hierarchyCategory;
	}

	public void setHierarchyCategory(int hierarchyCategory) {
		this.hierarchyCategory = hierarchyCategory;
	}
	
	public int compare(Object arg1, Object arg2) {
		HotspotHierarchyStore hhs1 = (HotspotHierarchyStore) arg1;
		HotspotHierarchyStore hhs2 = (HotspotHierarchyStore) arg2;
		
		
		if(hhs1.endRanking < hhs2.endRanking)
			return -1;
		else 
			return 1;	
	}
	
}
