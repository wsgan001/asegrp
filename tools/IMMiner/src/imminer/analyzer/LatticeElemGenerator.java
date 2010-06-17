package imminer.analyzer;

import imminer.pattern.ItemSet;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * Generates lattice of elements such as for the set {a, b, c}, this generates all
 * combinations: [a, b, {a, b}, {b, c}, {a, c}, {a, b, c}]
 * @author suresh
 *
 */
public class LatticeElemGenerator {

	/**
	 * Generates next set of elements in the lattice, based on previous set of elements
	 * @param existingItemSet
	 * @param uniqueIds
	 * @param latticeLevel : ensures that the patterns generated are all on this level
	 * @return
	 */
	public static Set<ItemSet> generateNextElements(Set<ItemSet> existingItemSet, List<String> uniqueIds, int latticeLevel)
	{
		Set<ItemSet> returnSet = new HashSet<ItemSet>();
				
		if(existingItemSet == null || existingItemSet.size() == 0)
		{
			//no existing elements. create an ItemSet for each element and return
			for(String uniqueId : uniqueIds)
			{
				ItemSet is = new ItemSet(uniqueId);
				returnSet.add(is);
			}
			return returnSet;
		}
		
		//Generate combinations among existing Item Sets to generate new itemsets
		int size = existingItemSet.size();
		ItemSet[] existIsArr = new ItemSet[size];
		existingItemSet.toArray(existIsArr);
		
		for(int outtcnt = 0; outtcnt < size; outtcnt++)
		{
			for(int inntcnt = outtcnt + 1; inntcnt < size; inntcnt++)
			{
				ItemSet is = new ItemSet(existIsArr[outtcnt].getItems());
				is.addAllItems(existIsArr[inntcnt].getItems());
				
				if(is.size() == latticeLevel)
					returnSet.add(is);	
			}
		}
		
		return returnSet;
	}	
}
