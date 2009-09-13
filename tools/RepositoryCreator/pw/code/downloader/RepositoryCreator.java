package pw.code.downloader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.apache.log4j.Logger;

import pw.code.downloader.GCodeDownloader;
import pw.common.CommonConstants;

public class RepositoryCreator {
		
	static private Logger logger = Logger.getLogger("RepositoryCreator");
	public static final String perlFileName = "HTMLExtract.pl";

	static List<String> queryList = new ArrayList<String>();
	
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
				GCodeDownloader gdc =new GCodeDownloader(input + " " + input, loc_dir, language); 
				gdc.downLoadURLs();
				
				try
				{
					//@SuppressWarnings("unused")
					//Runtime javaRuntime = Runtime.getRuntime();
					//@SuppressWarnings("unused")
					//String cmdToExec = "perl  " + CommonConstants.baseDirectoryName + CommonConstants.FILE_SEP + perlFileName + " " + language + " " + loc_dir + "";
				
					//Process perlProcess = javaRuntime.exec(cmdToExec);
					//perlProcess.waitFor();  //NOTE: This method hangs if the process initiated produce any console output and it is not consumed
				}
				catch(Exception ex)
				{
					logger.error("Error in executing perl process. Please check your Perl installation!!!" + ex);
				}
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
		//HashSet<String> srcFileSet = new HashSet<String>();
		//srcFileSet.add("checkstyle-src-4.3/src/testinputs/com/puppycrawl/tools/checkstyle/InputImport.java");
		//ZipFileHandler.extractCompressedFile("C:\\RepositoryCreator\\java_sql_Connection\\checkstyle-src-4.3.tar.gz", 
		//		srcFileSet, "C:\\RepositoryCreator\\java_sql_Connection\\");
		
		//if(true)
		//	return;
		
		if(args.length != 4)
		{
			System.err.println("Usage: RepositoryCreater <QueryList> <DirLocation> <Language> <PackageFlag>");
			return;
		}
		
		String line = null;
			
		try
		{
			GCodeDownloader.bwPackageDetails = new BufferedWriter(new FileWriter("PackageMappings.csv" + System.currentTimeMillis())); 
			
			Scanner inpScan = new Scanner(new File(args[0]));			
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
			
			if(args[3].equals("Y"))
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
			new RepositoryCreator().createLocalRepos(args[1], args[2]);
			logger.info("TIMING: End time of the download " + System.currentTimeMillis());
			GCodeDownloader.bwPackageDetails.close();
		}
		catch(Exception ex){
			ex.printStackTrace();
			
			System.err.println("Usage: RepositoryCreater <QueryList> <DirLocation> <Language> <PackageFlag>");
		}
	}
}
