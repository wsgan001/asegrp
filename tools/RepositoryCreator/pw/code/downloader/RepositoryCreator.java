package pw.code.downloader;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import pw.code.downloader.GCodeDownloaderOld;
import pw.common.CommonConstants;

public class RepositoryCreator {
		
	static private Logger logger = Logger.getLogger("RepositoryCreator");
	public static final String perlFileName = "HTMLExtract.pl";
	static List<String> queryList = new ArrayList<String>();
	
	/*
	 * Stores information of all libraries associated with the classes
	 */
	static HashSet<ClassLibraryStore> allClassLibSet = new HashSet<ClassLibraryStore>();
		
	public void createLocalRepos(String parentDir, String language)
	{
		try
		{
			for(Iterator iter = queryList.iterator(); iter.hasNext();)
			{
				String input = (String)iter.next();				
				System.out.println("For input: " + input);				
				String loc_dir = parentDir + CommonConstants.FILE_SEP + input;
				loc_dir = loc_dir.replace(".", "_");
				
				@SuppressWarnings("unused")
				GCodeDownloaderHTMLUnit gdc =new GCodeDownloaderHTMLUnit(input, loc_dir, language); 
				gdc.downLoadURLs();
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}		
	}
	
	/**
	 * A function for using Repository creater as a separate application
	 * @param args : Filename with list of classes && Location of directory for storing the samples
	 */
	public static void main(String args[])
	{		
		if(args.length != 5)
		{
			printUsage();
			return;
		}
		
		if(args[0].equals("0"))
			downloadCodeExamples(args);
		else if(args[0].equals("1"))
			downloadLibraries(args);
		else
			printUsage();		
	}

	/*
	 * Downloads libraries for all code examples
	 */
	private static void downloadLibraries(String[] args) {		
		
		HashMap<String, PackageStore> librariesToDownload = new HashMap<String, PackageStore>();
		String parentDir = args[2];
		String language = args[3];
		
		try {
			GCodeDownloaderOld.bwPackageDetails = new BufferedWriter(new FileWriter("PackageMappings.csv" + System.currentTimeMillis()));					
			Scanner inpScan = new Scanner(new File(args[1]));
			while(inpScan.hasNextLine())
			{
				String line = inpScan.nextLine(); 
				String clsArr[] = line.split("#"); 
				//Filtering out primitive types
				if(clsArr.length != 0) {
					queryList.add(clsArr[0]);	
				}			
			}			
			inpScan.close();	
			(new File(parentDir)).mkdirs();			
						
			for(Iterator iter = queryList.iterator(); iter.hasNext();)
			{
				try
				{
					String input = (String)iter.next();				
					System.out.println("For input: " + input);
					
					ClassLibraryStore clstore = new ClassLibraryStore(input);	
					allClassLibSet.add(clstore);
					
					@SuppressWarnings("unused")
					GCodePackageDownloader gdc =new GCodePackageDownloader(input, parentDir, language); 
					gdc.downLoadURLs(clstore);
					
					Thread.sleep(60000);
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}			
			
			for(ClassLibraryStore clstore : allClassLibSet)
			{
				Collection<PackageStore> pckSet = clstore.getPckSet();
				
				if(pckSet.isEmpty())
					continue;
				
				TreeSet<PackageStore> clsPackSet = new TreeSet<PackageStore>();
				clsPackSet.addAll(pckSet);
				
				int count = 0;
				for(PackageStore ps : clsPackSet)
				{
					PackageStore existingStore = librariesToDownload.get(ps.packageUrl);
					if(existingStore == null)
					{
						librariesToDownload.put(ps.packageUrl, ps);
					} 
					else
					{
						existingStore.getFileSet().addAll(ps.getFileSet());
					}
					
					count ++;
					if(count == CommonConstants.MAX_LIBRARIES_FOR_PACKAGE)
						break;
				}
			}			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		logger.warn("Finished gathering URLs. Starting downloading package files");
		try
		{
			//Printing all URLs to be downloaded
			BufferedWriter bwPck = new BufferedWriter(new FileWriter("PackagesToDownload"));
			for(PackageStore pckURL : librariesToDownload.values())
			{
				StringBuffer sb = new StringBuffer();
				sb.append(pckURL.packageUrl);
				sb.append("###");
				
				for(String fname : pckURL.getFileSet())
				{
					sb.append(fname + "###");
				}				
				bwPck.write(sb.toString() + "\n");				
			}			
			bwPck.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		logger.warn("Gathering zip files...");
		//Downloading complete zipfiles
		for(PackageStore pckURL : librariesToDownload.values())
		{
			downloadZipFile(parentDir, pckURL);
		}
	}
	
	private static void downloadZipFile(String directoryName, PackageStore ps)
    {
    	try { 		
	    	//Check whether the file is already existing. useful when the process is
	    	//killed in the middle and re-started again
    		int lastIndex = ps.packageUrl.lastIndexOf("/");
			String zipFileName = ps.packageUrl.substring(lastIndex + 1, ps.packageUrl.length());
    		
	    	File fObj = new File(directoryName + "//" + zipFileName);
	    	if(fObj.exists()) {
	    		logger.debug("Skipping download of " + fObj.getName() + ", which is already existing!!!");
	    		return;
	    	}
    				    			    			
	    	URL url = new URL(ps.packageUrl);
	    	URLConnection connection;
	    	if(CommonConstants.bUseProxy == 0)
	    	{	
	    		connection = url.openConnection();
	    	}
			else
			{
				InetSocketAddress isa = new InetSocketAddress(CommonConstants.pHostname, CommonConstants.pPort);
				Proxy prox = new Proxy(Proxy.Type.HTTP, isa);
				connection = url.openConnection(prox);
			}
						
			connection.connect();
			//If the file size is greater than 5MB, skip downloading the file
			if(connection.getContentLength() >  CommonConstants.FILE_DOWNLOAD_LIMIT)  {
				logger.warn("Skipping file of size " + connection.getContentLength() + ":" + zipFileName);
				return;
			}					
							
			InputStream in = connection.getInputStream();
			if(in.available() > CommonConstants.FILE_DOWNLOAD_LIMIT) {
				in.close();
				logger.warn("Skipping file of size " + in.available());
				return;
			}
					
			logger.debug("Downloading file: " + zipFileName);
					
			FileOutputStream fos = new FileOutputStream(fObj);
			BufferedOutputStream bos = new BufferedOutputStream(fos);				
			byte byteArr[] = new byte[1024];
			int numRead;
			long numWritten = 0;			
			while ((numRead = in.read(byteArr)) != -1) {
				bos.write(byteArr, 0, numRead);
				numWritten += numRead;
				System.out.print(".");
			}		    				
			in.close();
			bos.close();
			
			//Extract the relavent files from Zip file
			ZipFileHandler.extractCompressedFile(directoryName + "//" + zipFileName, ps.fileSet, directoryName);
			
    	} catch(Exception ex) {
    		logger.warn(ex.getMessage());
    		ex.printStackTrace();
    	}
    }

	private static void printUsage() {
		System.err.println("Usage: RepositoryCreater <Mode> <QueryList> <DirLocation> <Language> <PackageFlag>");
		System.err.println("Made 0: Downloads code examples");
		System.err.println("Made 1: Downloads entire libraries in top 10 list");
	}

	/*
	 * Downloads code examples
	 */
	private static void downloadCodeExamples(String[] args) {
		String line;
		try
		{
			GCodeDownloaderOld.bwPackageDetails = new BufferedWriter(new FileWriter("PackageMappings.csv" + System.currentTimeMillis()));			
			Scanner inpScan = new Scanner(new File(args[1]));			
			while(inpScan.hasNextLine())
			{
				line = inpScan.nextLine(); 
				String clsArr[] = line.split("#"); 
				//Filtering out primitive types
				if(clsArr.length != 0) {
					queryList.add(clsArr[0]);	
				}			
			}
			
			inpScan.close();			
			if(args[4].equals("Y"))
			{	
				CommonConstants.bUsePackageNames = true;
				logger.debug("Running in Package extraction mode...");
			}	
			else
			{
				logger.debug("Running in Normal extraction mode...");	
				CommonConstants.bUsePackageNames = false;
			}
				
			logger.info("TIMING: Start time of the download " + System.currentTimeMillis());
			new RepositoryCreator().createLocalRepos(args[2], args[3]);
			logger.info("TIMING: End time of the download " + System.currentTimeMillis());
			GCodeDownloaderOld.bwPackageDetails.close();
		}
		catch(Exception ex){
			ex.printStackTrace();			
			System.err.println("Usage: RepositoryCreater <Mode> <QueryList> <DirLocation> <Language> <PackageFlag>");
		}
	}
}

//HashSet<String> srcFileSet = new HashSet<String>();
//srcFileSet.add("checkstyle-src-4.3/src/testinputs/com/puppycrawl/tools/checkstyle/InputImport.java");
//ZipFileHandler.extractCompressedFile("C:\\RepositoryCreator\\java_sql_Connection\\checkstyle-src-4.3.tar.gz", 
//		srcFileSet, "C:\\RepositoryCreator\\java_sql_Connection\\");

//if(true)
//	return;
