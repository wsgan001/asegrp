package api.usage;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Iterator;
import java.util.Scanner;

import org.apache.log4j.Logger;

import api.srcparser.ParseSrcFiles;

/**
 * Main class of the project that takes the folder of source files as input
 * Arg0: Source files of the input library
 * Arg1: Location for storing downloaded code samples from the CSE
 * @author suresh_thummalapenta
 *
 */
public class APIUsageMain {
	
	static private Logger logger = Logger.getLogger("APIUsageMain");
	
	public void calculateAPIUsages(String sourceDir, String codesamplesDir)
	{
		RepositoryAnalyzer.resetMIIdGen();
		ParseSrcFiles psf = new ParseSrcFiles();
		psf.parseSources(new File(sourceDir));
		
		/*Debugging section for getting list of classes*/
		try
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter("APIList.txt"));
	        for(Iterator iter = RepositoryAnalyzer.getInstance().getLibClassMap().values().iterator(); iter.hasNext();)
	        {
	        	LibClassHolder lcc = (LibClassHolder) iter.next();
	        	
	        	bw.write(lcc.getName() + "#");
	        	for (LibMethodHolder lmh : lcc.getMethods()) {
	        		bw.write(lmh.toString() + "#");
	        	}
	        	
	        	bw.write("\n");
	        }
	        bw.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		/*End of Debug Section*/
		
		
		RepositoryCreator rcObj = new RepositoryCreator();
		rcObj.createLocalRepos(codesamplesDir);
		
		logger.debug("Finished downloading files...");
		
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		raObj.analyzeRepository(codesamplesDir);
	}
	
	
	public static void main(String args[])
	{
		logger.debug("API Usage Metrics ...");
		
		APIUsageMain apiMain = new APIUsageMain();
		if(args.length == 2)
		{
			apiMain.calculateAPIUsages(args[0], args[1]);
		}
		else
		{
			try
			{
				//Take the input from the input file
				Scanner scan = new Scanner(new File("InputLocations.txt"));
				
				while(scan.hasNextLine())
				{
					String inpLine = scan.nextLine();
					
					String parts[] = inpLine.split(" ");
					apiMain.calculateAPIUsages(parts[1], parts[2]);
					
					//Rename the output file
					File outFile = new File("APIUsageMetrics.txt");
					outFile.renameTo(new File(parts[0] + "_" + System.currentTimeMillis() + "_APIUsageMetrics.txt"));
				}
				
				scan.close();
			
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
		
		logger.debug("Finished analyzing downloaded code samples...");
	}
}
