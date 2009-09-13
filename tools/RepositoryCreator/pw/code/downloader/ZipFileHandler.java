package pw.code.downloader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.apache.log4j.Logger;
import org.apache.tools.tar.TarEntry;
import org.apache.tools.tar.TarInputStream;

/**
 * Class that handles Zip files and extracts the necessary source files from that Zip file 
 * @author suresh_thummalapenta
 */
public class ZipFileHandler {
	
	static int uniqueIDGen = 0;	
	public static Logger logger = Logger.getLogger("ZipFileHandler");
	
	public static void extractCompressedFile(String zipfileName, HashSet<String> sourceFileSet, String targetDir) {		
		if(zipfileName.endsWith(".zip")) {
			extractZIPSourceFile(zipfileName, sourceFileSet, targetDir);
		} else if(zipfileName.endsWith(".gz")) {
			extractGZIPSourceFile(zipfileName, sourceFileSet, targetDir);
		} 	
	}
	
	public static void extractZIPSourceFile(String zipfileName, HashSet<String> sourceFileSet, String targetDir)
	{
		try {
			logger.debug("Extracting files " + sourceFileSet.toString() + " from " + zipfileName); 
			ZipInputStream zipinputstream = new ZipInputStream(new FileInputStream(zipfileName));
	        ZipEntry zipentry = zipinputstream.getNextEntry();
	        
	        while (zipentry != null) 
	        { 
	        	//for each entry to be extracted
	            String entryName = zipentry.getName();
            	            
	            if(sourceFileSet.contains(entryName)) {
	            	System.out.println("Entryname: " + entryName);
	            	//Getting filename from the entry name
	            	String fileName = entryName.substring(entryName.lastIndexOf("/") + 1, entryName.length()); 
	            	extractSpecificFile(zipinputstream, targetDir + "//" + fileName);	            	 
		            zipinputstream.closeEntry();		            
	            }
	            zipentry = zipinputstream.getNextEntry();
	        }	
	        zipinputstream.close();
		} catch(IOException ioex) {   
			ioex.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		}		
	}

	public static void extractGZIPSourceFile(String zipfileName, HashSet<String> sourceFileSet, String targetDir)
	{
		try {			
			InputStream ist = null;
			if(zipfileName.endsWith(".gz")) {
				ist = new GZIPInputStream(new FileInputStream(new File(zipfileName)));
			} else {
				ist = new FileInputStream(new File(zipfileName));
			}
			
			TarInputStream tarStr = new TarInputStream(ist);
			TarEntry tarentry = tarStr.getNextEntry();        
	        while (tarentry != null) 
	        { 
	        	//for each entry to be extracted
	            String entryName = tarentry.getName();
            	            
	            if(sourceFileSet.contains(entryName)) {
	            	System.out.println("Entryname: " + entryName);
	            	//Getting filename from the entry name
	            	String fileName = entryName.substring(entryName.lastIndexOf("/") + 1, entryName.length()); 
	            	FileOutputStream fileoutputstream = new FileOutputStream(targetDir + "//" + fileName);
	            	tarStr.copyEntryContents(fileoutputstream);            		            	 
	            }
	            tarentry = tarStr.getNextEntry();
	        }	
	        tarStr.close();
		} catch(IOException ioex) {   
			ioex.printStackTrace();
		} catch(Exception ex) {
			ex.printStackTrace();
		}		
	}
	
	public static void extractSpecificFile(InputStream inpStream, String outputFile)
	{
		try {
	        int numBytes = 0;
	        byte buf[] = new byte[1024];
			FileOutputStream fileoutputstream = new FileOutputStream(outputFile);        
	        while ((numBytes = inpStream.read(buf, 0, 1024)) > -1)
	            fileoutputstream.write(buf, 0, numBytes);	
	        fileoutputstream.close();
		} catch(Exception ex) {		
			logger.warn("Error occurred while extracting file!!!");
		}
	}	
}
