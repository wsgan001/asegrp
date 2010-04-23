package pw.code.downloader;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gdata.data.codesearch.CodeSearchFeed;
import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.Package;
import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;
import pw.common.CommonConstants;
import com.google.gdata.client.codesearch.CodeSearchService;


/**
 * Class that downloads the source files matching a given pattern
 * @author suresh_thummalapenta
 *
 */
public class GCodeDownloaderZipMode {

	public static Logger logger = Logger.getLogger("GCodeDownloader");
	
	String searchTerm, targetDir, language;
	String relatedClsName = "";
	int startIndex = 1;
	String url = "http://www.google.com/codesearch/feeds/search";
	
    private URL feedUrl;
    private CodeSearchService codesearchService;
    private CodeSearchFeed resultFeed;
    private List<CodeSearchEntry> entries;
    
    //Any External class can initialize this variable to a file. 
    //If not null, "GCodeDownloader" writes the package details...
    public static BufferedWriter bwPackageDetails = null;
    
    public int fileCnt = 0;
    public List<String> returningFileNameList = new ArrayList<String>();
    Object lockObj = new Object();
    
    private HashSet<String> packagesToDownload = new HashSet<String>();   
    int FILE_DOWNLOAD_LIMIT = 10 * 1024 * 1024;	//A limit of 5MB is used
    private HashSet<String> downloadedURLLinks = new HashSet<String>();
        
	public GCodeDownloaderZipMode(String searchTerm, String targetDir, String language)
	{
		this.searchTerm = searchTerm.trim();
		
		//Usually people donot write code like "java.lang.String". They just write "String".
		//This has to be taken care while firing the queries
		if(searchTerm.indexOf("java.lang.String") != -1)
		{
			searchTerm = searchTerm.replace("java.lang.String","");
			searchTerm = searchTerm + " " + searchTerm;
		}
	
		this.targetDir = targetDir;
		relatedClsName = targetDir.substring(targetDir.lastIndexOf("/") + 1, targetDir.length());
		this.language = language;
		
		codesearchService = new CodeSearchService("gdata-sample-codesearch");
	}
		
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length == 0)
		{
			logger.info("Incorrect usage");
			logger.info("Parameters needed <SearchTerm> <TargetDir> <Language>");
			return;
		}

		new GCodeDownloaderZipMode(args[0], args[1], args[2]).downLoadURLs(); 
	}
	
	/**
	 * Download URLs work with multiple threads. This can be enhanced by distributing the job to threads and then
	 * wait for them to return instead of initiating them one by one.
	 * @return
	 */
	public List downLoadURLs()
	{	
    	try
    	{
	    	//Using multiple threads for download to gain performance.
			String directoryName = targetDir;
			
			boolean success = (new File(directoryName)).mkdirs();
		    if (!success) {
		      logger.info("Directories: " + directoryName + " already present, Skipping downloading files for this class");	      
		    }
	    	
		    bwPackageNamesWriter = new BufferedWriter(new FileWriter("Packages_Files.txt"));
		    bwURLNameWriter = new BufferedWriter(new FileWriter("AllURLsList.txt"));
		    
		    int retryCount = 0;
		    while(true)
		    {
		    	feedUrl = new URL(createURL());

		    	if(CommonConstants.bUseProxy == 1)
				{	
					System.setProperty("http.proxyHost", CommonConstants.pHostname);
			        System.setProperty("http.proxyPort", "" + CommonConstants.pPort);
			        System.setProperty("http.proxySet", "true");
				}    
		    	
		    	try
		    	{
		    		resultFeed = codesearchService.getFeed(feedUrl,CodeSearchFeed.class);
		    	}
		    	catch(Exception ex)
		    	{
		    		//TODO: Still not able to figure out how to get rid of this problem.
		    		//So, just bi-passing for time being for the next queries
		    		logger.debug("Exception with Google code search service. Results need analysis: "  + searchTerm);
		    		logger.debug(feedUrl);
		    		
		    		//Try again in case of exceptions
		    		if(retryCount == 0 || retryCount == 1)
		    		{
		    			retryCount++;
		    			continue;
		    		}
		    		else
		    		{
			            startIndex = startIndex + 10;
			            if(startIndex > resultFeed.getTotalResults() || startIndex > CommonConstants.MAX_FILES_TO_DOWNLOAD)
			            	break;
			            
			            continue;
		    		}
		    	}
		        entries = resultFeed.getEntries();
		  	
		    	Object cseObjArr[] = entries.toArray();	
		    	for(int cnt = 0; cnt < cseObjArr.length; cnt ++)
		    	{
		    		if(cseObjArr[cnt] instanceof CodeSearchEntry)
		    		{
		    			CodeSearchEntry cseObj = (CodeSearchEntry)cseObjArr[cnt];
		    			
	    				//Extract the package name and check back to get additional files
	    				Package objPackage = cseObj.getPackage();
		    			String urlOfPackage = objPackage.getUri();
		    			URL feedUrlPackage = new URL(createURL(urlOfPackage));			    			
		    			CodeSearchFeed resultFeedInternal = null;
			    			
		    			String packageURL = cseObj.getPackage().getUri();
		    			if(!(packageURL.endsWith(".zip") || packageURL.endsWith(".gz") || packageURL.endsWith(".tar"))) {
		    				continue;
		    			}
		    			
		    			if(downloadedURLLinks.contains(packageURL)) {
		    				continue;
		    			}
		    			downloadedURLLinks.add(packageURL);
		    			
		    			try
				    	{
				    		resultFeedInternal = codesearchService.getFeed(feedUrlPackage,CodeSearchFeed.class);
				    	}
				    	catch(Exception ex)
				    	{
				    		logger.debug("Exception with Google code search service with package name. Results need analysis: "  + searchTerm);
				    		logger.debug(feedUrlPackage);
				    	}    	
				    	
				    	List<CodeSearchEntry> sub_entries = resultFeedInternal.getEntries();					    	
				    	if(sub_entries.size() == 0)
				    	{
				    		//String URLlink = cseObj.getHtmlLink().getHref();
				    		//bwURLNameWriter.write(URLlink + "\n");
				    		packageURL = cseObj.getPackage().getUri(); 
			    			com.google.gdata.data.codesearch.File fObj = cseObj.getFile();
			    			gatherZipFileEntries(packageURL, directoryName, fObj.getName());
				    	}
				    	else
				    	{	
					    	for(CodeSearchEntry subcseObj : sub_entries)
					    	{
					    		//String URLlink = subcseObj.getHtmlLink().getHref();
					    		//bwURLNameWriter.write(URLlink + "\n");
					    		packageURL = subcseObj.getPackage().getUri(); 
				    			com.google.gdata.data.codesearch.File fObj = subcseObj.getFile();
				    			gatherZipFileEntries(packageURL, directoryName, fObj.getName());
					    	}
				    	}	
		    		}		    		
		    	}		    	

	            startIndex = startIndex + 10;
	            if(startIndex > resultFeed.getTotalResults() || startIndex > CommonConstants.MAX_FILES_TO_DOWNLOAD)
	            	break;
	            
	            retryCount++;
		    }
		    
		    for(String key : downloadedPackages.keySet()) {
		    	ZipFileStore zfsObj = downloadedPackages.get(key);
		    	if(zfsObj == null || zfsObj.fileNameSet.size() == 0)
		    		continue;
		    	
		    	for(String fileName : zfsObj.fileNameSet) {
		    		bwPackageNamesWriter.write(key + "###" + fileName + "\n");
		    	}		    	
		    }		    
			
			bwPackageNamesWriter.close();
			bwURLNameWriter.close();
			
			//All zip files are downloaded
			downloadZipFiles(directoryName);
			
			//Extract the actual source filename from the zip file
			for(String key : downloadedPackages.keySet()) {
		    	ZipFileStore zfsObj = downloadedPackages.get(key);
		    	if(zfsObj == null || zfsObj.fileNameSet.size() == 0)
		    		continue;
		    	
		    	ZipFileHandler.extractCompressedFile(targetDir + "//" + zfsObj.shortZipEntry, zfsObj.fileNameSet, targetDir);
		    }  
		}
    	catch(Exception e)
    	{
    		logger.error("Error " + e);
    		e.printStackTrace();
    		if(e instanceof SAXParseException)
    		{
    			SAXParseException spe = (SAXParseException) e;
    			String err = spe.toString() +
    		       "\n Line number: " + spe.getLineNumber() +
    		       "\n Column number: " + spe.getColumnNumber()+
    		       "\n Public ID: " + spe.getPublicId() +
    		       "\n System ID: " + spe.getSystemId() ;
    		    logger.info( err );
    		}
    	}
    	
    	return returningFileNameList;
	}
	
	
	private synchronized void addToLists(CodeSearchEntry cseObj, List<String> URLLinkList, List<String> localFileNameList, String objPackageName)
	{
		String URLlink = cseObj.getHtmlLink().getHref();
		
		if(packagesToDownload.contains(URLlink))
		{
			//File already downloaded
			return;
		}
		
		try
		{
			packagesToDownload.add(URLlink);
			URLLinkList.add(URLlink);
			
			//if(bwPackageDetails != null)
			//	bwPackageDetails.write(URLlink + "\n");
			
			//Getting the actual name of the file
			int startPos = URLlink.lastIndexOf("/");
			int endPos = URLlink.lastIndexOf(".");
			
			
			String tempFilename = URLlink.substring(startPos + 1, endPos);
			if(tempFilename.indexOf("=") != -1)
			{
				//To handle somefile names whose names is of form "19_JMS_exemples.zip&cs_f=QRetailer.html"
				startPos = URLlink.lastIndexOf("=");
			}
			
			String fileName = "";
			
			fileName = fileCnt + "_" + URLlink.substring(startPos + 1, endPos);
			String fileNameWithLang = fileName + "." + language;
			returningFileNameList.add(fileNameWithLang);
			localFileNameList.add(fileName);
			
			//Storing URLs with respect to FileID; (AnamolyDetector SPECIFIC)
			if(bwPackageDetails != null)
				bwPackageDetails.write(relatedClsName + fileNameWithLang + "," + objPackageName + "\n");
			fileCnt++;			
		}
		catch(StringIndexOutOfBoundsException ex)
		{
			logger.error("IndexOutOfBounds : " + URLlink);
		}
		catch(IOException ex)
		{
			logger.error("Exception " + ex);
		}
	}
	
    private String createURL() throws UnsupportedEncodingException {
        String query = "";
        query = URLEncoder.encode("lang:" + language + " " + searchTerm, "UTF-8");
        //query = "lang:" + language + "&" + searchTerm;
        String urlF = url + "?start-index=" + startIndex + "&q=" + query;
        
       	urlF += "&hl=en";
       
        return urlF;
    }
    
    private String createURL(String packageName) throws UnsupportedEncodingException {
        String query = "";
        query = URLEncoder.encode("lang:" + language + " " + searchTerm, "UTF-8");
        
        //query = "lang:xml&" + searchTerm;
        String urlF = url + "?start-index=1&q=" + query;
       	urlF += "+package:%22" + packageName + "%22&as_pkgcrowd=n&sa=N&cd=3&ct=rm&hl=en";
       
        return urlF;
    }
    
    
    HashMap<String, ZipFileStore> downloadedPackages = new HashMap<String, ZipFileStore>();
    BufferedWriter bwPackageNamesWriter = null;
    BufferedWriter bwURLNameWriter = null;

    public void gatherZipFileEntries(String urllink, String directoryName, String fileName)
    {
    	try {
    		logger.debug("Gathering zip file entries for: " + urllink);   		
    		
    		//Get output filename from input filename
			int lastIndex = urllink.lastIndexOf("/");
			String zipFileName = urllink.substring(lastIndex + 1, urllink.length());
			if(!(zipFileName.endsWith(".zip") || zipFileName.endsWith(".gz") || zipFileName.endsWith(".tar"))) {
				return;
			}
			//End of getting output filename
						
			//Package already downloaded
			ZipFileStore zfsObj = downloadedPackages.get(zipFileName);			
			if(zfsObj == null) {
				HashSet<String> fileNameSet = new HashSet<String>();
				zfsObj = new ZipFileStore(urllink, zipFileName, fileNameSet); 
				fileNameSet.add(fileName);
				downloadedPackages.put(zipFileName, zfsObj);
			} else {
				zfsObj.fileNameSet.add(fileName);
			}
			System.out.print(".");
    	} catch(Exception ex) {
    		ex.printStackTrace();
    	}
    }
   
    public void downloadZipFiles(String directoryName)
    {
    	try {    		
    		TreeMap<String, ZipFileStore> tmapObj = new TreeMap<String, ZipFileStore>();
    		
    		for(String key : downloadedPackages.keySet()) {
		    	ZipFileStore zfsObj = downloadedPackages.get(key);
		    	if(zfsObj == null || zfsObj.fileNameSet.size() == 0)
		    		continue;
		    	tmapObj.put(key, zfsObj);
		    }    		
    		
    		//Download top 25 zip files now
    		int downloadCnt = 0;
    		for(String key : tmapObj.keySet()) {    			
    			try {
	    			if(downloadCnt > CommonConstants.NUM_PROJECTS_TO_DOWNLOAD) {
	    				break;
	    			}
	    			
	    			downloadCnt++;
	    			ZipFileStore zfsObj = downloadedPackages.get(key);
	    			if(zfsObj == null)
	    				continue;
	    			
	    			//Check whether the file is already existing. useful when the process is
	    			//killed in the middle and re-started again
	    			File fObj = new File(directoryName + "//" + zfsObj.shortZipEntry);
	    			if(fObj.exists()) {
	    				logger.debug("Skipping download of " + fObj.getName() + ", which is already existing!!!");
	    				continue;
	    			}
    				    			    			
	    			URL url = new URL(zfsObj.urlLink);
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
					if(connection.getContentLength() >  FILE_DOWNLOAD_LIMIT)  {
						logger.debug("Skipping file of size " + connection.getContentLength() + ":" + zfsObj.shortZipEntry);
						continue;
					}					
							
					InputStream in = connection.getInputStream();
					if(in.available() > FILE_DOWNLOAD_LIMIT) {
						in.close();
						logger.debug("Skipping file of size " + in.available());
						continue;
					}
					
					logger.debug("Downloading file: " + zfsObj.shortZipEntry);
					
					FileOutputStream fos = new FileOutputStream(new File(directoryName + "//" + zfsObj.shortZipEntry));
					BufferedOutputStream bos = new BufferedOutputStream(fos);				
					byte byteArr[] = new byte[1024];
					int numRead;
					long numWritten = 0;			
					while ((numRead = in.read(byteArr)) != -1) {
						bos.write(byteArr, 0, numRead);
						numWritten += numRead;
					}		    				
					in.close();
					bos.close();
					System.out.print(".");
    			} catch(Exception ex) {	
    				logger.warn(ex.getMessage());
    				ex.printStackTrace();
    			}
    		}		
    	} catch(Exception ex) {
    		logger.warn(ex.getMessage());
    		ex.printStackTrace();
    	}
    }

    class Downloader implements Runnable 
    {
    	String URLlink, outFileName, directoryName;
    	Object cseObjArr[];
    	
    	List URLLinkList, FileNameList;
    	
    	public Downloader(String directoryName, Object cseObjArr[])
    	{
    		this.cseObjArr = cseObjArr;
    		this.directoryName = directoryName;
    	}
    	
    	public Downloader(String directoryName, List URLLinkList, List FileNameList)
    	{
    		this.URLLinkList = URLLinkList;
    		this.FileNameList = FileNameList;
    		this.directoryName = directoryName;
    	}
    	
    	public Downloader(String URLlink, String outFileName, Object cseObjArr[])
    	{
    		this.URLlink = URLlink;
    		this.outFileName = outFileName;
    		this.cseObjArr = cseObjArr;
    	}
    	
    	//The multi-threading feature still preserves the actual order of the files
    	//downloaded through this feature. We must preserve the order for comparing with GCSE results.
    	public void run()
    	{
    		try
			{
	    		Iterator urlIt = URLLinkList.iterator();
	    		Iterator fileIt = FileNameList.iterator();
			    for(;urlIt.hasNext();)
		    	{
		    		try
					{    			
		    			URLlink = (String)urlIt.next();
		    			String fileName = (String)fileIt.next();
		    			outFileName = directoryName + "//" +  fileName +  ".html";	
			    		URL url = new URL(URLlink);

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
							
						InputStream in = connection.getInputStream();
						FileOutputStream fos = new FileOutputStream(new File(outFileName));
						BufferedOutputStream bos = new BufferedOutputStream(fos);
							
						byte byteArr[] = new byte[1024];
						int numRead;
						long numWritten = 0;
						
						while ((numRead = in.read(byteArr)) != -1) {
							bos.write(byteArr, 0, numRead);
							numWritten += numRead;
						}		    				
						in.close();
						bos.close();						
						System.out.print(".");						
					}
					catch(MalformedURLException e)
					{
						logger.info("Exception occurred " + e);
					}
					catch(IOException e)
					{
						logger.info("Exception occurred " + e);
					}
		    	}
		
			}
    	    catch(Exception ex) {
    			logger.error("Exception at global level " + ex);
    		}
    	}
    }    	
}