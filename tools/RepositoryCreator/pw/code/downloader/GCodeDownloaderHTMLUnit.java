package pw.code.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.log4j.Logger;
import org.xml.sax.SAXParseException;

import pw.common.CommonConstants;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gdata.client.codesearch.CodeSearchService;
import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.CodeSearchFeed;
import com.google.gdata.data.codesearch.Package;

/*
 * Downloads source files from Google using HTMLUnit plugin
 */

public class GCodeDownloaderHTMLUnit {

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
    
        
    BufferedWriter bwPackageNamesWriter = null;
    BufferedWriter bwURLNameWriter = null;
    private HashSet<String> alreadyDownloadedURLs = new HashSet<String>();
	
	public GCodeDownloaderHTMLUnit(String searchTerm, String targetDir, String language)
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
		this.relatedClsName = targetDir.substring(targetDir.lastIndexOf("/") + 1, targetDir.length());
		this.language = language;
		
		codesearchService = new CodeSearchService("gdata-sample-codesearch");
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
	
	/**
	 * Download URLs work with multiple threads. This can be enhanced by distributing the job to threads and then
	 * wait for them to return instead of initiating them one by one.
	 * @return
	 */
	public List downLoadURLs()
	{	
    	try
    	{
	    	int threadCnt = 0, activeCount = 0;
	    	Thread dwnLoadThr[] = new Thread[CommonConstants.MAX_THREAD_CNT];
			//Using multiple threads for download to gain performance.
			String directoryName = targetDir;
			
			boolean success = (new File(directoryName)).mkdirs();
		    if (!success) {
		      logger.info("Directories: " + directoryName + " already present, Skipping downloading files for this class");	      
		    }
	    	
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
		    	List<String> URLLinkList = new LinkedList<String>();
		    	List<String> FileNameList = new LinkedList<String>();
		    	for(int cnt = 0; cnt < cseObjArr.length; cnt ++)
		    	{
		    		if(cseObjArr[cnt] instanceof CodeSearchEntry)
		    		{
		    			CodeSearchEntry cseObj = (CodeSearchEntry)cseObjArr[cnt];		    			
		    			if(CommonConstants.bUsePackageNames)
		    			{	
		    				//Extract the package name and check back to get additional files
		    				Package objPackage = cseObj.getPackage();
			    			String urlOfPackage = objPackage.getUri();
			    			URL feedUrlPackage = new URL(createURL(urlOfPackage));
			    			
			    			CodeSearchFeed resultFeedInternal = null;
			    			
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
					    		addToLists(cseObj, URLLinkList, FileNameList, objPackage.getName());
					    	}
					    	else
					    	{	
						    	for(CodeSearchEntry subcseObj : sub_entries)
						    	{
						    		addToLists(subcseObj, URLLinkList, FileNameList, objPackage.getName());
						    	}
					    	}	
		    			}
		    			else
		    			{	
		    				addToLists(cseObj, URLLinkList, FileNameList, cseObj.getPackage().getName());
		    			}	
		    		}
		    	}	    	
		    	
		    	
		    	Downloader dwnLoadObj = new Downloader(directoryName, URLLinkList, FileNameList);
		    	
    			dwnLoadThr[threadCnt % CommonConstants.MAX_THREAD_CNT] = new Thread(dwnLoadObj);
    			dwnLoadThr[threadCnt % CommonConstants.MAX_THREAD_CNT].start();
    			++threadCnt;
    			++activeCount;
    			
    			if(activeCount == CommonConstants.MAX_THREAD_CNT)
    			{
    				try
    				{
    					
    					dwnLoadThr[threadCnt % CommonConstants.MAX_THREAD_CNT].join();
    					dwnLoadThr[threadCnt % CommonConstants.MAX_THREAD_CNT] = null;
    					activeCount --;
    				}
    				catch(Exception e)
    				{
    					logger.info("Interrupted exception " + e);
    				}
    			}

	            startIndex = startIndex + 10;
	            if(startIndex > resultFeed.getTotalResults() || startIndex > CommonConstants.MAX_FILES_TO_DOWNLOAD)
	            	break;
	            
	            retryCount++;
		    }
		    
	    	//Just wait if any pending threads left before proceeding
	    	for(int tcnt = 0; tcnt < CommonConstants.MAX_THREAD_CNT; tcnt ++)
	    	{
	    		if(dwnLoadThr[tcnt] != null)
	    		{
	    			try
	    			{
	    				dwnLoadThr[tcnt].join();
	    			}
	    			catch(InterruptedException e)
	    			{	    				
	    			}
	    		}
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
    		       "\n  Line number: " + spe.getLineNumber() +
    		       "\nColumn number: " + spe.getColumnNumber()+
    		       "\n Public ID: " + spe.getPublicId() +
    		       "\n System ID: " + spe.getSystemId() ;
    		    logger.info( err );
    		}
    	}
    	
    	return returningFileNameList;
	}
	
	private synchronized void addToLists(CodeSearchEntry cseObj, List<String> URLLinkList, 
			List<String> localFileNameList, String objPackageName)
	{
		String URLlink = cseObj.getHtmlLink().getHref();
		
		if(alreadyDownloadedURLs.contains(URLlink))
		{
			//File already downloaded
			return;
		}
		
		try
		{  				    		
			alreadyDownloadedURLs.add(URLlink);			
			URLLinkList.add(URLlink);
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
			//logger.error("IndexOutOfBounds : " + URLlink);
			String fileName = "";			
			fileName = fileCnt + "";
			String fileNameWithLang = fileName + "." + language;
			returningFileNameList.add(fileNameWithLang);
			localFileNameList.add(fileName);
			fileCnt++;
		}
		catch(IOException ex)
		{
			logger.error("Exception " + ex);
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
		    			outFileName = directoryName + "//" +  fileName +  ".java";		    					    		
		        		
		    			WebClient webClient = new WebClient();		    			
		                HtmlPage page = webClient.getPage(URLlink);
		                
		                //it is very important to wait
		                int retValue = webClient.waitForBackgroundJavaScript(10000);
		                if(retValue > 0)
		                {
		                	logger.warn("A javascript terminated prematurely!!!");
		                }
		                
		                
		                // DIV
		                HtmlDivision div = (HtmlDivision) page
		                                   .getByXPath("//html/body/div[2]/div[2]/div/div[4]/div/div[4]/div/div[1]/div/div/div/div/div[2]/div/div/div[2]")
		                                   .get(0);
		                                   
		                String xmlContent = div.asXml();         
		                		                
		                BufferedWriter bw = new BufferedWriter(new FileWriter(outFileName));
		                String[] xmlContentArr = xmlContent.split("\n");
		                int contentlen = xmlContentArr.length;
		                for(int cnt = 0; cnt < contentlen; cnt++)
		                {
		                	String tempStr = xmlContentArr[cnt].trim();
		                	if(tempStr.startsWith("<"))
		                		continue;              
		                	tempStr = tempStr.replaceAll("&lt;", "<");
		                	tempStr = tempStr.replaceAll("&gt;", ">");
		                	tempStr = tempStr.replaceAll("&nbsp;", " ");
		                	tempStr = tempStr.replaceAll("&quot;", "\"");
		                	tempStr = tempStr.replaceAll("&amp;", "&");
		                	tempStr = tempStr.replaceAll("&divide;", "/");
		                	tempStr = tempStr.replaceAll("&ndash;", "-");
		                	
		                	bw.write(tempStr);
		                	if(tempStr.contains(";") || tempStr.contains("//"))
		                	{
		                		bw.write("\n");
		                	} 
		                	else
		                	{
		                		bw.write(" ");
		                	}		                	
		                }		                		                
		                bw.close();
		            }
					catch(MalformedURLException e)
					{
						logger.info("Exception occurred " + e);
					}
					catch(Exception e)
					{
						logger.info("Exception occurred " + e);
					}
					
					System.out.print(".");
		    	}		
			}
    	    catch(Exception ex) {
    			logger.error("Exception at global level " + ex.getMessage());
    			ex.printStackTrace();
    		}
    	}
    }	
}
