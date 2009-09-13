package pw.main;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TreeSet;
import java.util.logging.Logger;

import org.eclipse.core.runtime.IProgressMonitor;

import pw.code.analyzer.GCodeAnalyzer;
import pw.code.analyzer.SequenceStore;
import pw.code.downloader.GCodeDownloader;
import pw.common.CommonConstants;


/**
 * Main class for integrating Code Downloader and Code Analyzer components
 * @author suresh_thummalapenta
 *
 */
public class PARSEWeb {

	public static Logger logger = Logger.getLogger("PARSEWeb");
	public static final String language = "java";
	public static final String perlFileName = "HTMLExtract.pl";
	
	/**
	 * Function for intiating the process of downloading source code, transforming and analysing the files
	 * @param workingDir
	 * @param sourceCls
	 * @param destCls
	 */
	public TreeSet initiateProcess(String workingDir, String sourceCls, String destCls, IProgressMonitor monitor) throws Exception
	{
		CommonConstants.uniqueMIHolderId = 0;
		
		long beginTComplete = System.currentTimeMillis();
		if(CommonConstants.baseDirectoryName == null)
		{
			throw new Exception("Environment variable PARSEWeb_Path not set!!!");
		}

		boolean prevAlternateValue = CommonConstants.bAlternateMethodArgumentSeq;
		if(sourceCls.indexOf("java.lang.String") != -1 || destCls.indexOf("java.lang.String") != -1)
		{
			//it is not good to extract alternate method sequences when one of the elements is String
			CommonConstants.bAlternateMethodArgumentSeq = false;
		}
		
		TreeSet resultSet = downloadAndAnalyze(workingDir, sourceCls, destCls, monitor, false);
		
		//Check if all the results are of low confidence only
		boolean bNoHighConfidence = true;
		for(Iterator iter2 = resultSet.iterator(); iter2.hasNext();)
		{
			SequenceStore ssStore = (SequenceStore) iter2.next();
			if(ssStore.confidenceLevel)
			{
				bNoHighConfidence = false;
				break;
			}
		}
		
		//If there is some High Confidence pattern or User does not want to run in Mode 2
		//Mode 2 is turned on, even there is a high confidence pattern and its occurrence is just 1.
		int numberOfOccurrencesOfFirst = 0;
		if(resultSet.size() == 1)
		{
			numberOfOccurrencesOfFirst = ((SequenceStore)resultSet.iterator().next()).numOfTimes;
		}
		
		if((CommonConstants.bUseMode2 != 1) || (!bNoHighConfidence && (resultSet.size() > 1 || numberOfOccurrencesOfFirst > 1)))
			return resultSet;
		
		if(monitor != null)
			monitor.beginTask("Analyzing In Mode 2", 4);
		
		//Indicates that PARSEWeb failed to find the high confidence results due to lack of samples
		//Fall back to MODE 2 in case of user request.
		String newWorkingDir = workingDir + "\\DestOnly";
		
		//Generate sequence patterns only with Destination
		TreeSet resultSetWithDestOnly = downloadAndAnalyze(newWorkingDir, "", destCls, monitor, true);
		
		//Final set of result set will be available in this TreeSet
		HashSet<SequenceStore> resultSetWithAltDest = new HashSet<SequenceStore>();
		
		HashSet<String> newPossibleDest = new HashSet<String>();
		HashSet<String> tmpPossibleDest = new HashSet<String>();
		
		int numOfResultsConsidered = 0;
		
		//Get Immediate sources and make a new Set of Destinations
		//The reference types and argument types of the last method invocation are considered
		//as immediate sources
		for(Iterator iter = resultSetWithDestOnly.iterator(); iter.hasNext();)
		{
			tmpPossibleDest.clear();
		
			SequenceStore ssStore = (SequenceStore) iter.next();
			//Mark all these patterns as low confidence
			ssStore.confidenceLevel = false;
			
			String actualSequence = ssStore.sequence;
			
			//Get the last method invocation and its actual Id
			StringTokenizer stNewLine = new StringTokenizer(actualSequence, "\n");
			String lastMIInSeq = "";
			while(stNewLine.hasMoreTokens())
			{
				lastMIInSeq = stNewLine.nextToken();
			}

			ssStore.actualPath = ssStore.actualPath.trim();	
			int lastIndex = ssStore.actualPath.lastIndexOf(" ");
			int length = ssStore.actualPath.length();
			String IdOflastMIInSeq = ssStore.actualPath.substring(lastIndex + 1, length);
			
			//Get the reference type and argument types if they are not equal to actual destination
			String referenceType = lastMIInSeq.substring(0,lastMIInSeq.indexOf(","));
			if(!referenceType.equals(destCls) && !referenceType.equals("CONSTRUCTOR") 
					&& !referenceType.equals(CommonConstants.unknownType) && !referenceType.equals("#LOCAL#") && !referenceType.equals("this"))
			{	
				if(!newPossibleDest.contains(referenceType))
				{	
					newPossibleDest.add(referenceType);
					tmpPossibleDest.add(referenceType);
				}	
			}	
				
			String argumentList =  lastMIInSeq.substring(lastMIInSeq.indexOf("(") + 1, lastMIInSeq.indexOf(")"));
			StringTokenizer stArg = new StringTokenizer(argumentList, ",");
			while(stArg.hasMoreTokens())
			{
				String argument = stArg.nextToken();
				if(!argument.equals(destCls) && !argument.equals("#UNKNOWN#") && !argument.equals("this") && !argument.equals("#LOCAL#") && (argument.indexOf(".") != -1))
				{
					if(!newPossibleDest.contains(referenceType))
					{	
						newPossibleDest.add(argument);
						tmpPossibleDest.add(argument);
					}	
				}
			}
			
			//Generate alternate sequences for all the elements in Temp possible destinations
			for(Iterator iterInt = tmpPossibleDest.iterator(); iterInt.hasNext();)
			{
				String newDestCls = (String) iterInt.next();
				String newWorkingDirT = workingDir + "\\" + newDestCls;
				
				if(monitor != null)
					monitor.beginTask("Analyzing With Alternate Destination" + newDestCls, 4);
				TreeSet tmpRS = downloadAndAnalyze(newWorkingDirT, sourceCls, newDestCls, monitor, true);
				
				for(Iterator tmpIter = tmpRS.iterator(); tmpIter.hasNext();)
				{	
					//Propogate the values of parent SequenceStore to child. 
					//Attach the last sequence to it
					//Also propogate the rank of its parent to give this sequence a highest preference (To check whether this makes sense)
					SequenceStore ssStoreChild = (SequenceStore)tmpIter.next();
					
					ssStoreChild.sequence = ssStoreChild.sequence + lastMIInSeq;
					ssStoreChild.numOfMethodCalls = ssStoreChild.numOfMethodCalls + 1;
					ssStoreChild.numOfTimes = ssStoreChild.numOfTimes + ssStore.numOfTimes; //TODO: This addition of frequency may give high priority  
					//TODO: To deal with Java filename and Method name as this is a clustered sequence.
					ssStoreChild.actualPath += " " + IdOflastMIInSeq;
					ssStoreChild.keySet.add(new Integer(Integer.parseInt(IdOflastMIInSeq)));
										
					//TODO: Do we need to perform an additional clustering at this point?
					resultSetWithAltDest.add(ssStoreChild);
				}
			}
			
			//Currently only first five results are considered. This has to be made configurable later
			numOfResultsConsidered++;
			if(numOfResultsConsidered > 5)
				break;
		}
		
		
		//Add the special case result set from the beginning over here
		if(!bNoHighConfidence)
		{
			resultSetWithAltDest.add((SequenceStore)resultSet.iterator().next());		
		}
		
		//Assign new ranks to final set of results and eliminate low confidence sequences if there exists 
		//a high confidence seqeunce
		int rank = 1;
		boolean bHighConfSequence = false;
		TreeSet<SequenceStore> sortedSet = new TreeSet<SequenceStore>(new SequenceStore());
		for(Iterator iter = resultSetWithAltDest.iterator(); iter.hasNext();)
		{
			SequenceStore ssStore = (SequenceStore) iter.next();
			if(ssStore.confidenceLevel)
			{	
				bHighConfSequence = true;
				ssStore.rank = rank;
				rank++;
				sortedSet.add(ssStore);
			}	
			
		}
		
		//Logging the end results for time being: This is only for debugging purpose
		BufferedWriter bwOutput = new BufferedWriter(new FileWriter(workingDir+"//MultipleSeqOut.txt"));
		for(Iterator iter = resultSetWithAltDest.iterator(); iter.hasNext();)
		{
			SequenceStore ssStore = (SequenceStore) iter.next();
			bwOutput.write("\nFileName:" + ssStore.javaFileName + " MethodName:" + ssStore.methodName 
											+ " Rank:" + ssStore.rank + " NumberOfOccurrences:" + ssStore.numOfTimes + " Confidence:" + ssStore.confidenceLevel
											+ " Path:" + ssStore.actualPath);
			bwOutput.write("\n\t" + ssStore.sequence + "\n");
		}
		bwOutput.close();
		//End of debugging output

		//If there is no high confidence pattern, just output the results with destination only.
		//It may always contain some useful things.
		if(!bHighConfSequence)
		{	
			return resultSetWithDestOnly;
		}
		
		if(monitor != null)
			monitor.worked(1);
		
		//Restoring modifications made specific to this run
		CommonConstants.bAlternateMethodArgumentSeq = prevAlternateValue;
		
		
		long endTComplete = System.currentTimeMillis();
		logger.info("Total time taken including Multiple Query Feature " + (endTComplete - beginTComplete) + " ms");
		
		return sortedSet;
	}
	
	public TreeSet downloadAndAnalyze(String workingDir, String sourceCls, String destCls, IProgressMonitor monitor, boolean bReUseOldInstance) throws Exception
	{
		//Download java files through Google Code Search Engine
		logger.info("Downloading related source code files from web...");
		
		String searchTerm = "";
		if(sourceCls.equals(""))
		{
			searchTerm = destCls + " " + destCls;
		}
		else
		{
			searchTerm = sourceCls + " " + destCls;
		}
		GCodeDownloader gdc = new GCodeDownloader(searchTerm, workingDir, language);
		//UNCOMMENT THIS
		
		long beginT = System.currentTimeMillis();
		
		if(monitor != null)
		{	
			monitor.worked(1);
		}
		
		List fileNamelist = gdc.downLoadURLs();
		if(monitor != null)
			monitor.worked(1);
		long endT = System.currentTimeMillis();
		long codeDownloaderTime = endT - beginT;
		logger.info("Time for Code Downloader " + codeDownloaderTime + " ms");
		
		long postProcessorTime = 0;
		
		//Transform java files from HTML to Java through Perl parser
		/*try
		{
			logger.info("\nTransforming downloaded files...");
			Runtime javaRuntime = Runtime.getRuntime();
			String cmdToExec = "perl  " + CommonConstants.baseDirectoryName + CommonConstants.FILE_SEP + perlFileName + " " + workingDir;
						
			//UNCOMMENT THIS
			beginT = System.currentTimeMillis();
			Process perlProcess = javaRuntime.exec(cmdToExec);
			
			if(monitor != null)
				monitor.worked(1);
			
			perlProcess.waitFor();  //NOTE: This method hangs if the process initiated process any console output and it is not consumed
			
			if(monitor != null)
				monitor.worked(1);
			endT = System.currentTimeMillis();
			postProcessorTime = endT - beginT;
			
		}
		catch(Exception ex)
		{
			throw new Exception("Error in executing perl process. Please check your Perl installation!!!");
		}
		
		logger.info("Time for Post Processor " + postProcessorTime + " ms");*/
		
		//Analyze the downloaded code
		logger.info("Analyzing files for generating patterns...");
		GCodeAnalyzer gca = new GCodeAnalyzer();
		
		beginT = System.currentTimeMillis();
		if(monitor != null)
			monitor.worked(1);
		
		TreeSet resultSet = gca.analyze(workingDir, sourceCls, destCls, fileNamelist, bReUseOldInstance);
		
		//This is a temporary for only running GCodeAnalyzer
		//TreeSet resultSet = gca.analyze(workingDir, sourceCls, destCls, bReUseOldInstance);				
		endT = System.currentTimeMillis();
		long codeAnalyzerTime = endT - beginT;

		logger.info("Time for Code Analyzer " + codeAnalyzerTime + " ms");
		logger.info("Total time taken for single round" + (codeDownloaderTime + postProcessorTime + codeAnalyzerTime) + " ms");
	
		
		return resultSet;
	}
	
	public static void main(String args[])
	{
		if(args.length < 2)
		{
			logger.info("Incorrect parameters.");
			logger.info("Usage: GCodeAnalyzer <DirectoryNameOfFiles> <OptionalSourceClassName> <DestinationClassName>");
			System.exit(1);
		}
		
		try
		{
			if(args.length == 3)
			{	
				new PARSEWeb().initiateProcess(args[0], args[1], args[2], null);
			}
			else if(args.length == 2)
			{
				new PARSEWeb().initiateProcess(args[0], "", args[1], null);
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}	
}
