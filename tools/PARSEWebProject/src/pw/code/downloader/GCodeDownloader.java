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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import java.util.logging.Logger;
import org.xml.sax.SAXParseException;

import pw.common.CommonConstants;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.google.gdata.client.codesearch.CodeSearchService;
import com.google.gdata.data.codesearch.CodeSearchEntry;
import com.google.gdata.data.codesearch.CodeSearchFeed;

/**
 * Class that downloads the source files matching a given pattern
 * @author suresh_thummalapenta
 *
 */
public class GCodeDownloader {

	public static Logger logger = Logger.getLogger("GCodeDownloader");
	
	String searchTerm, targetDir, language;
	int startIndex = 1;
	String url = "http://www.google.com/codesearch/feeds/search";
	
    private URL feedUrl;
    private CodeSearchService codesearchService;
    private CodeSearchFeed resultFeed;
    private List<CodeSearchEntry> entries;
    
    
    public int fileCnt = 0;
    public List<String> globalFileNameList = new ArrayList<String>();
    Object lockObj = new Object();
    
	public GCodeDownloader(String searchTerm, String targetDir, String language)
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

		new GCodeDownloader(args[0], args[1], args[2]).downLoadURLs(); 
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
		      logger.info("Directories: " + directoryName + " already present");
		    }
	    	
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
		    		System.err.println("Exception with Google code search service. Results need analysis:");
		    		ex.printStackTrace();
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
		    			
		    			String URLlink = cseObj.getHtmlLink().getHref();
		    			    			
		    			//Getting the actual name of the file
		    			int startPos = URLlink.lastIndexOf("/");
		    			int endPos = URLlink.lastIndexOf(".");
		    			
		    			if(startPos > endPos) {
		    				continue;
		    			}
		    			
		    			URLLinkList.add(URLlink);
		    			
		    			String tempFilename = URLlink.substring(startPos + 1, endPos);
		    			if(tempFilename.indexOf(".java") != -1) {		    			
		    				endPos = URLlink.indexOf(".java") + 5;
		    			} else {
			    			if(tempFilename.indexOf("=") != -1)
			    			{
			    				//To handle somefile names whose names is of form "19_JMS_exemples.zip&cs_f=QRetailer.html"
			    				startPos = URLlink.lastIndexOf("=");
			    			}
		    			}
		    			
		    			String fileName = "";
		    			
		    			fileName = fileCnt++ + "_" + URLlink.substring(startPos + 1, endPos);
		    			globalFileNameList.add(fileName);
		    			FileNameList.add(fileName);
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
    		logger.info("Error " + e);
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
    	
    	return globalFileNameList;
	}
	
    private String createURL() throws UnsupportedEncodingException {
        String query = "";
        query = URLEncoder.encode("lang:java " + searchTerm, "UTF-8");
        String urlF = url + "?start-index=" + startIndex + "&q=" + query;
        
        //Workaroud: Some proxies are not allowing .URL as end term
        if(urlF.endsWith(".URL"))
        {
        	urlF += "&hl=en";
        }
        
        urlF = urlF.replaceAll(" ", "%20");
        return urlF;
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
    		Iterator urlIt = URLLinkList.iterator();
    		Iterator fileIt = FileNameList.iterator();
		    for(;urlIt.hasNext();)
	    	{
	    				
	    		URLlink = (String)urlIt.next();
	    		String fileName = (String)fileIt.next();
	    			
	    		outFileName = directoryName + "//" +  fileName;	
		    			
    			downloadFilesUsingWebClient(URLlink);
				System.out.print(".");			
	    	}
    	}
    	
    	private void downloadFilesUsingWebClient(String URLLink)
    	{
    		try {
    			//testing code
    			final WebClient webClient = new WebClient();
    	        final HtmlPage page = webClient.getPage(URLLink);
    	        //it is very important to wait
    	        webClient.waitForBackgroundJavaScriptStartingBefore(2000);
    	
    	        // DIV
    	        final HtmlDivision div = (HtmlDivision) page
    	                        .getByXPath("//html/body/div/div[2]/div/div/div[3]/div/div[2]/div/div/div/div/div/div[2]")
    	                        .get(0);  	        
    	            	        
    	        FileWriter fos = new FileWriter(new File(outFileName));
    	        BufferedWriter bs = new BufferedWriter(fos);
    	        bs.write(div.asText());
    	        bs.close();
    		} 
    		catch (Exception ex)
    		{
    			ex.printStackTrace();
    		}    	
    	}
    	
    	/*
    	 * Downloading files using old strategy
    	 */
    	private void downloadFileUsingStreams(String URLLink) 
    			throws MalformedURLException, IOException
    	{
    		try {
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
}