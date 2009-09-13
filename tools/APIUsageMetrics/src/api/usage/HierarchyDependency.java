package api.usage;

import pw.common.CommonConstants;

/**
 * A dependency class that stores relation between two different hierarchies
 * There can be two different relations:
 * 
 * 1. TEMPLATE-HOOK relation
 * 2. TEMPLATE-TEMPLATE relation
 * @author suresh_thummalapenta
 *
 */
public class HierarchyDependency {
	
	static public final int TEMPLATE_HOOK = 1;
	static public final int TEMPLATE_TEMPLATE  = 2;
	
	int dependencyType = -1;
	
	HotspotHierarchyStore parent;
	HotspotHierarchyStore child;
	
	public HierarchyDependency(HotspotHierarchyStore parent, HotspotHierarchyStore child) {
		this.parent = parent;
		this.child = child;		
		
		if(parent.getHierarchyCategory() == CommonConstants.TEMPLATE && child.getHierarchyCategory() == CommonConstants.HOOK)
		{
			dependencyType = TEMPLATE_HOOK;
		}
		else if(parent.getHierarchyCategory() == CommonConstants.TEMPLATE && child.getHierarchyCategory() == CommonConstants.TEMPLATE)
		{
			dependencyType = TEMPLATE_TEMPLATE;
		}		
	}
	
	public String toString() {
		return "Type: " + dependencyType + 
			" Parent: " + parent.getEndRanking() + " Child: " + child.getEndRanking();
				
	}
	
	public boolean equals(Object obj) {
		HierarchyDependency hdObj = (HierarchyDependency) obj;
		
		if(this.dependencyType == hdObj.dependencyType && this.parent == hdObj.parent && this.child == hdObj.child)
			return true;
		
		return false;
	}
	
	public int hashCode() {
		return dependencyType + parent.getEndRanking() + child.getEndRanking();
	}
}
