package pw.code.downloader;

import java.util.Comparator;
import java.util.HashSet;

/**
 * A class that stores zip files
 * @author suresh_thummalapenta
 *
 */
public class ZipFileStore implements Comparator {
	
	String urlLink;
	String shortZipEntry;
	HashSet<String> fileNameSet;
	
	public ZipFileStore(String completeZipEntry, String shortZipEntry, HashSet<String> fileNameSet)
	{
		this.urlLink = completeZipEntry;
		this.shortZipEntry = shortZipEntry;
		this.fileNameSet = fileNameSet;
	}
	
	public int compare(Object arg0, Object arg1) {
	
		if(!(arg0 instanceof ZipFileStore) && !(arg1 instanceof ZipFileStore)) {
			return -1;
		}
		
		ZipFileStore zfs0 = (ZipFileStore) arg0;
		ZipFileStore zfs1 = (ZipFileStore) arg1;
		
		if(zfs0.fileNameSet.size() > zfs1.fileNameSet.size()) {
			return 1;
		} else if(zfs0.fileNameSet.size() < zfs1.fileNameSet.size()) {
			return -1;
		}			
		return 0;
	}	
}
