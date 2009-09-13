package pw.main;

import java.io.File;
import java.util.Scanner;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;

import pw.common.CommonConstants;

/**
 * A class for generating the trace data required by the Visualization tool to visualize
 * the sequence data. This class mainly puts each sequence in a different file. The sequence
 * kept in each file is a raw sequence, where it contains the given source and destination
 * objects in the query, but is not minimal sequence
 * @author suresh_thummalapenta
 *
 */

public class TraceGenerator {

	public static Logger logger = Logger.getLogger("PARSEWeb");
	public static final String language = "java";
	public static final String perlFileName = "HTMLExtract.pl";
	
	public void generateSequences(String workingDir, String sourceCls, String destCls, IProgressMonitor monitor) throws Exception {
		String newWorkingDir = workingDir + "/" + sourceCls.replace('.', '_') + "_" + destCls.replace('.', '_');
		String traceWorkingDir = "C:/PARSEWeb/Visualize_Final/" + sourceCls.replace('.', '_') + "_" + destCls.replace('.', '_');
		
		boolean success = (new File(newWorkingDir)).mkdirs();
		success = (new File(traceWorkingDir)).mkdirs();		
		
		CommonConstants.bDumpTraces = true;
		CommonConstants.traceWorkingDir = traceWorkingDir;
		CommonConstants.dumpFilenameCounter = 0;
		
		new PARSEWeb().initiateProcess(newWorkingDir, sourceCls, destCls, null);	
	}
	
	
	
	public static void main(String args[]) {
		if(args.length < 2)
		{
			logger.info("Incorrect parameters.");
			logger.info("Usage: TraceGenerator <QueryList> <DirectoryNameOfFiles>");
			System.exit(1);
		}
		
		try
		{
			TraceGenerator tGenObj = new TraceGenerator();
			
			Scanner queryReader = new Scanner(new File(args[0]));
			while(queryReader.hasNextLine()) {
				String userQuery = queryReader.nextLine(); 
				
				int indexOfArrow = userQuery.indexOf("->");
				if(indexOfArrow == -1)
				{
					CommonConstants.DestinationObject = userQuery.trim();
					CommonConstants.sourceObject = "";
				}
				else
				{
					CommonConstants.sourceObject = userQuery.substring(0, indexOfArrow).trim();
					CommonConstants.DestinationObject = userQuery.substring(indexOfArrow + 2, userQuery.length()).trim();
				}			
				
				tGenObj.generateSequences(args[1], CommonConstants.sourceObject, CommonConstants.DestinationObject, null);
			}						
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	
	
	
}
