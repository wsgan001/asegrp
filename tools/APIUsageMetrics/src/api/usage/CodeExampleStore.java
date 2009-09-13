package api.usage;

import java.util.Comparator;
import java.util.HashSet;

public class CodeExampleStore implements Comparator {
	public HashSet<Integer> methodIds = new HashSet<Integer>();
	public String filename;
	public String methodname;
	public int type;	/* Can be TEMPLATE or HOOK (For hook type, there won't be any method)*/
	
	public boolean equals(Object cesObj)
	{
		if(!(cesObj instanceof CodeExampleStore))
			return false;
		
		HashSet<Integer> otherMethodIds = ((CodeExampleStore)cesObj).methodIds;
		
		if(otherMethodIds.containsAll(methodIds))
			return true;
		
		return false;
	}
	
	public int compare(Object obj1, Object obj2)
	{
		if(!(obj1 instanceof CodeExampleStore) || !(obj2 instanceof CodeExampleStore))
			return -1;
		
		CodeExampleStore cesObj1 = (CodeExampleStore) obj1;
		CodeExampleStore cesObj2 = (CodeExampleStore) obj2;
		
		if(cesObj1.equals(cesObj2))
			return 0;
		
		return -1;
	}
	
	//Return same hash code for all
	public int hashCode()
	{
		return 10;
	}
}