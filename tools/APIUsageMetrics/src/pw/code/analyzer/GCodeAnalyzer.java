package pw.code.analyzer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import api.usage.RepositoryAnalyzer;

import pw.common.CommonConstants;


/**
 * Main class of the package responsible for collecting the files and invoking the
 * ASTCrawler for futher analysis
 * @author suresh_thummalapenta
 *
 */
public class GCodeAnalyzer {

	
	public static Logger logger = Logger.getLogger("GCodeAnalyzer");
	ASTCrawler astC = null;		//This is described as a member variable as often the previous instance of this class is used for next runs 
	
	//Constructur for processing in the specified order of files
	public GCodeAnalyzer()
	{
		
	}
	
	public TreeSet analyze(String directoryPath, String sourceObject, String destinationObject, List fileNameList, boolean bReUseOldInstance)
	{
		return invokeASTCrawler(directoryPath, fileNameList, sourceObject, destinationObject, bReUseOldInstance);
	}
	
	public TreeSet analyze(String directoryPath, String sourceObject, String destinationObject, boolean bReUseOldInstance)
	{
		List filesList = collect(directoryPath);
		return invokeASTCrawler(directoryPath, filesList, sourceObject, destinationObject, bReUseOldInstance);
	}
	
	/**
	 * Function for reading the files from the given path
	 */
	public List collect(String dirPath)
	{
        File inputFile = new File(dirPath);
        if (!inputFile.exists()) {
            throw new RuntimeException("File " + inputFile.getName() + " doesn't exist");
        }
        List<String> dataSources = new ArrayList<String>();
        if (!inputFile.isDirectory()) {
            //TODO: To give some error message
        } else {
        	String[] candidates = inputFile.list();
        	for (int i = 0; i < candidates.length; i++) {
        		if(candidates[i].endsWith(".java"))
        		{	
        			//File tmp = new File(inputFile + FILE_SEP + candidates[i]);
        			dataSources.add(candidates[i]);
        		}	
        	}    
        }
        return dataSources;

	}
	
	/**
	 * Function for invoking ASTCrawler on each file in the list
	 */
	public TreeSet invokeASTCrawler(String directoryPath, List filesList, String sourceObject, String destinationObject, boolean bReUseOldInstance)
	{
		//ASTParser parser = ASTParser.newParser(DEFAULT_LANGUAGE_SPECIFICATION);
			
		//ASTCrawler class
		if(!bReUseOldInstance || astC == null)
			astC = new ASTCrawler(directoryPath);
		astC.sourceObj = sourceObject;
		astC.destObj = destinationObject;
		astC.bAPIUsageMetrics = true;
		//astC.setDirectoryName(directoryPath);
		
			
		for(Iterator iter = filesList.iterator(); iter.hasNext();)
		{
			try
			{
				String inputFileName = (String) iter.next();
				File inputFile = new File(directoryPath + CommonConstants.FILE_SEP + inputFileName);
				
				logger.info("Analyzing " + inputFile.getName());
				char[] source = new char[(int) inputFile.length()];
		        FileReader fileReader = new FileReader(inputFile);
		        fileReader.read(source);
		        fileReader.close();
				
		        ASTParser parser= ASTParser.newParser(AST.JLS3);
				parser.setResolveBindings(true);
				parser.setStatementsRecovery(true);
				parser.setSource(source);
				parser.setProject(RepositoryAnalyzer.getInstance().getLibProject());			
				
				CompilationUnit root;
				root= (CompilationUnit) parser.createAST(null);
				
				
				if ((root.getFlags() & CompilationUnit.MALFORMED) != 0) {
					logger.error("Error occurred while parsing the file: " + inputFile.getName());
		            continue;
		        }
				
				try
				{
					//This also clears the class specific data of the previous run
					astC.preProcessClass(root, inputFile.getName());	
					MethodInvocationHolder.MIHKEYGEN = 0;
					root.accept(astC);
					astC.closeDebugFile();
				}
				catch(Exception ex)
				{
					ex.printStackTrace();
				}
			}
			catch(Exception ex)
			{
				logger.error("Error occurred while reading the file " + ex);
				logger.error(ex.getStackTrace().toString());
			}
		}
		
		return astC.postProcess();
	}
	
	/**
	 * Main function that starts the execution.
	 * @param args
	 */
	public static void main(String args[])
	{
		try
		{
			new GCodeAnalyzer().analyze(args[0], args[1], args[2], false);
		}
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
	}	
}
