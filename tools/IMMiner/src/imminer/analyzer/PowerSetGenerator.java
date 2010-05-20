/*
 * PowerSet.java	Version 1.0 	August 6, 2004
 *
 * Copyright 2004 Positronic Software. All Rights Reserved.
 *
 *
 */

package imminer.analyzer;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * Given a set, this class generates a powerset
 * @author suresh
 */
public class PowerSetGenerator implements Iterator {

	private boolean[] membership;
	private String[] array;

	public PowerSetGenerator(String[] array)
	{
	    this.array = array;
	    this.membership = new boolean[this.array.length];
	}

	public PowerSetGenerator(Collection<String> c)
	{
		this.array = new String[c.size()];
	    c.toArray(this.array);
	    this.membership = new boolean[this.array.length];
	}


	/**
     * Returns the next subset in the PowerSet.
     *
     * @return the next subset in the PowerSet.
     * @exception NoSuchElementException PowerSet has no more subsets.
     */
	public Collection<String> next()
	{
		boolean ok = false;
		for(int i = 0; i < this.membership.length; i++)
			if(!this.membership[i])
			{
				ok=true;
				break;
			}
    
		if(!ok)
			throw(new NoSuchElementException("The next method was called when no more objects remained."));
		else
		{
			int n=0;
			this.membership[0] = !this.membership[0];
			boolean carry=!this.membership[0];
			while(n+1 < this.membership.length)
			{
				n++;
				if(carry)
				{
					this.membership[n]=!this.membership[n];
					carry=!this.membership[n];
				}
				else break;
			}
			HashSet<String> vec=new HashSet<String>();
			for(int i=0;i<this.membership.length;i++)
				if(this.membership[i])
					vec.add(this.array[i]);
			
			return vec;
		}
	}

	/**
     *
     * Not supported by this class.
     *
     * @exception UnsupportedOperationException because the <tt>remove</tt>
     *		  operation is not supported by this Iterator.
     */
	public void remove()
	{
		throw new UnsupportedOperationException("The PowerSet class does not support the remove method.");
	}

	/**
     * Returns <tt>true</tt> if the PowerSet has more subsets. (In other
     * words, returns <tt>true</tt> if <tt>next</tt> would return a subset
     * rather than throwing an exception.)
     *
     * @return <tt>true</tt> if the PowerSet has more subsets.
     */
	public boolean hasNext()
	{
		for(int i=0;i<this.membership.length;i++)
			if(!this.membership[i])
				return true;
		return false;
	} 
}
