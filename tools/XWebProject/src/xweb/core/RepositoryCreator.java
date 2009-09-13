package xweb.core;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import xweb.code.downloader.GCodeDownloader;
import xweb.common.CommonConstants;

public class RepositoryCreator {
		
	static private Logger logger = Logger.getLogger("RepositoryCreator");
	public static final String perlFileName = "HTMLExtract.pl";

	public void createLocalRepos(String parentDir)
	{
		try
		{
			//Gathering samples for library classes
			RepositoryAnalyzer ra = RepositoryAnalyzer.getInstance();
			HashMap<String, LibClassHolder> libClasses = ra.getLibClassMap();
			for(Iterator iter = libClasses.values().iterator(); iter.hasNext();)
			{
				LibClassHolder input = (LibClassHolder)iter.next();				
				gatherFiles(parentDir, input.getName());
			}
			
			//Gathering samples for external objects
			for(Iterator iter = ra.getExternalObjects().values().iterator(); iter.hasNext();)
	        {
	        	ExternalObject eeObj = (ExternalObject) iter.next();
	        	gatherFiles(parentDir, eeObj.getClassName());
	        }	
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
	}
	
	private void gatherFiles(String parentDir, String name) {
		String loc_dir = parentDir + CommonConstants.FILE_SEP + name;
		loc_dir = loc_dir.replace(".", "_");
		
		@SuppressWarnings("unused")
		GCodeDownloader gdc =new GCodeDownloader(name + " " + name, loc_dir, "java"); 
		//gdc.downLoadURLs();
		
		try
		{
			@SuppressWarnings("unused")
			Runtime javaRuntime = Runtime.getRuntime();
			@SuppressWarnings("unused")
			String cmdToExec = "perl  " + CommonConstants.baseDirectoryName + CommonConstants.FILE_SEP + perlFileName + " " + loc_dir;
		
			Process perlProcess = javaRuntime.exec(cmdToExec);
			perlProcess.waitFor();			
		}
		catch(Exception ex)
		{
			logger.error("Error in executing perl process. Please check your Perl installation!!!" + ex);
		}
	}
	
	/**
	 * A function for using Repository creater as a separate application
	 * @param args : Filename with list of classes && Location of directory for storing the samples
	 */
	public static void main(String args[])
	{
		if(args.length != 3)
		{
			logger.error("Usage: RepositoryCreater <APIList> <DirLocation> <PackageFlag>");
			return;
		}
		
		try
		{
			Scanner inpScan = new Scanner(new File(args[0]));
			HashMap<String, LibClassHolder> raMap = RepositoryAnalyzer.getInstance().getLibClassMap();
			while(inpScan.hasNextLine())
			{
				String line = inpScan.nextLine(); 
				String className = line.split("#")[0];
				
				LibClassHolder lch = new LibClassHolder(className);
				raMap.put(className, lch);
				
			}
			
			inpScan.close();
			
			if(args[2].equals("Y"))
			{	
				CommonConstants.bUsePackageNames = true;
				logger.debug("Running in Package extraction mode...");
			}	
			else
			{
				logger.debug("Running in Normal extraction mode...");	
				CommonConstants.bUsePackageNames = false;
			}
				
			
			new RepositoryCreator().createLocalRepos(args[1]);
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
