package pw.code.downloader;

import java.util.Collection;
import java.util.HashMap;

/*
 * Stores the list of libraries for a single class
 */
public class ClassLibraryStore {

	HashMap<String, PackageStore> packageMap = new HashMap<String, PackageStore>();
	String className;
	
	public ClassLibraryStore(String className)
	{
		this.className = className;
	}
	
	public boolean equals(Object arg0)
	{
		if(!(arg0 instanceof ClassLibraryStore))
			return false;
		
		ClassLibraryStore other = (ClassLibraryStore) arg0;
		return this.className.equals(other.className);
	}
	
	public int hashCode()
	{
		return className.hashCode();
	}

	public Collection<PackageStore> getPckSet() {
		return packageMap.values();
	}
	
	/**
	 * Adds an element to the package. Here the filename parameter is optional
	 * @param packageURL
	 * @param filename
	 */
	public void AddPackageStore(String packageURL, String filename)
	{
		PackageStore existingStore = packageMap.get(packageURL);
		if(existingStore == null)
		{
			existingStore = new PackageStore(packageURL);
			packageMap.put(packageURL, existingStore);
		}
		
		existingStore.IncrPackageCnt();
		
		if(filename != null)
			existingStore.AddFileToPackage(filename);
	}	
}
