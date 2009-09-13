package minebugs.core;

import java.io.File;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import minebugs.srclibhandlers.ExternalObject;
import minebugs.srclibhandlers.LibClassHolder;

import org.apache.log4j.Logger;

import pw.code.downloader.GCodeDownloader;
import pw.common.CommonConstants;

public class RepositoryCreator {
		
	static private Logger logger = Logger.getLogger("RepositoryCreator");
	public static final String perlFileName = "HTMLExtract.pl";

	public void createLocalRepos(String parentDir)
	{
		try
		{
			
			if(System.getenv("SERVER_MODE") != null) {
				//No need to run the perl script in server mode
				return;
			}			
			
			HashMap<String, ExternalObject> externalObjects = RepositoryAnalyzer.getInstance().getExternalObjects();
					
			for(Iterator iter = externalObjects.values().iterator(); iter.hasNext();)
			{
				ExternalObject input = (ExternalObject)iter.next();				
				
				if(!CommonConstants.ALL_METHODS_OF_CLASS && input.getMiList().size() == 0)
					continue;
				
				String loc_dir = parentDir + "/" + input.getClassName();
				loc_dir = loc_dir.replace(".", "_");
				
				
				try{
					@SuppressWarnings("unused")
					GCodeDownloader gdc =new GCodeDownloader(input.getClassName() + " " + input.getClassName(), loc_dir, "java"); 
					//gdc.downLoadURLs();
				}
				catch(Exception ex) {
					logger.error("Exception in GCodeDownloader " + ex);
				}
				
				try
				{
					@SuppressWarnings("unused")
					Runtime javaRuntime = Runtime.getRuntime();
					@SuppressWarnings("unused")
					String cmdToExec = "perl  " + CommonConstants.baseDirectoryName + CommonConstants.FILE_SEP + perlFileName + " " + loc_dir;
				
					Process perlProcess = javaRuntime.exec(cmdToExec);
					perlProcess.waitFor();  //NOTE: This method hangs if the process initiated produce any console output and it is not consumed
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
