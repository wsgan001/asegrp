package imminer.pattern;

import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Represents a set of items
 * @author suresh
 *
 */
public class ItemSet {

	TreeSet<String> items = new TreeSet<String>();
	
	/**
	 * Expects the string format as "19.1.0 19.4.0 19.23.1"
	 * @param itemset
	 */
	public ItemSet(String itemset)
	{
		String[] titems = itemset.split(" ");		
		for(String item : titems)
			items.add(item.trim());
	}
	
	public ItemSet(Collection<String> otherItems)
	{
		this.items.addAll(otherItems);
	}
	
	public Collection<String> getItems()
	{
		return items;
	}
	
	public int size()
	{
		return items.size();
	}
	
	public void addItem(String item)
	{
		this.items.add(item);
	}
	
	public void addAllItems(Collection<String> oitems)
	{
		this.items.addAll(oitems);
	}
	
	public int hashCode()
	{
		int hashCodeVal = 0;
		for(String item : this.items)
			hashCodeVal += item.hashCode();
		return hashCodeVal;
	}
	
	public boolean equals(Object other)
	{
		if(!(other instanceof ItemSet))
			return false;
		
		ItemSet otherSet = (ItemSet) other;
		if(this.items.size() != otherSet.items.size())
			return false;
		
		Iterator otherIter = otherSet.items.iterator();
		Iterator thisIter = this.items.iterator();
		while(thisIter.hasNext())
		{
			String thisElem = (String) thisIter.next();
			String otherElem = (String) otherIter.next();
			if(!thisElem.equals(otherElem))
				return false;
		}
		return true;
	}
	
	public boolean containsAll(Collection<String> otherItems)
	{
		return this.items.containsAll(otherItems);
	}
	
	public boolean containsAtleastOne(Collection<String> otherItems)
	{		
		for(String otherItem : otherItems)
		{
			if(this.items.contains(otherItem))
				return true;
		}
		
		return false;
	}
	
	public boolean contains(String item)
	{
		return this.items.contains(item);
	}
	
	public boolean containsNone(Collection<String> otherItems)
	{
		for(String otherItem : otherItems)
		{
			if(this.items.contains(otherItem))
				return false;
		}
		
		return true;
	}
	
	public String toString()
	{
		return this.items.toString();
	}
}
