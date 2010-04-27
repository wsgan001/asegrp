package pw.code.downloader;

import java.util.Comparator;
import java.util.HashSet;

public class PackageStore implements Comparator, Comparable {

	/**
	 * Stores the name of the package
	 */
	String packageUrl;	
	
	/**
	 * Number of times this package is found
	 */
	int packageCnt;
	
	/**
	 * Stores the set of files within the package
	 */
	HashSet<String> fileSet = new HashSet<String>();	
	
	public PackageStore(String packageUrl)
	{
		this.packageUrl = packageUrl;
		this.packageCnt = 0;
	}
	
	public void IncrPackageCnt()
	{
		this.packageCnt++;
	}
	
	public int getPackageCnt()
	{
		return this.packageCnt;
	}
	
	public boolean equals(Object arg0)
	{
		if(!(arg0 instanceof PackageStore))
			return false;
		
		PackageStore other = (PackageStore) arg0;
		return this.packageUrl.equals(other.packageUrl);
	}
	
	public int compare(Object arg0, Object arg1) {
		
		if(!(arg0 instanceof PackageStore))
			return -1;
		
		if(!(arg1 instanceof PackageStore))
			return -1;
		
		PackageStore ps1 = (PackageStore) arg0;
		PackageStore ps2 = (PackageStore) arg1;
		
		if(ps2.packageCnt == ps1.packageCnt)
			return -1;
		
		return ps2.packageCnt - ps1.packageCnt;	
	}
	
	/**
	 * Returns the fileset
	 * @return
	 */
	public HashSet<String> getFileSet()
	{
		return this.fileSet;
	}
	
	/**
	 * Adds a file to the package
	 * @param filename
	 */
	public void AddFileToPackage(String filename)
	{
		this.fileSet.add(filename);
	}
	
	public int hashCode()
	{
		return packageUrl.hashCode();
	}

	public int compareTo(Object arg0) {
		return this.compare(this, arg0);
	}	
}
