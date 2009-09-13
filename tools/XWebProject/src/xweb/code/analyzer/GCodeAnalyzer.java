package xweb.code.analyzer;

import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import xweb.core.RepositoryAnalyzer;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import xweb.common.CommonConstants;


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
		//astC.setDirectoryName(directoryPath);
		
		for(Iterator iter = filesList.iterator(); iter.hasNext();)
		{
			try
			{
				String inputFileName = (String) iter.next();
				File inputFile = new File(directoryPath + CommonConstants.FILE_SEP + inputFileName);
				
				//XWeb: To avoid duplicates
				//If the combination of package name and file name is same and 
	        	//is already visitied, don't visit again to reduce duplicate
	        	//patterns, as duplicate samples can be downloaded from GCSE
				int indexOfUnderscore = 0;
				if((indexOfUnderscore = inputFileName.indexOf("_")) != -1) {
					RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
					String searchStr = sourceObject.replace(".", "_") + inputFileName;
					String file_packagename = inputFileName.substring(indexOfUnderscore + 1, inputFileName.length()) + "#"
													+ raObj.getCodeSampleIDToPackageMapper().get(searchStr);
					if(raObj.getVisitedCodeSamples().contains(file_packagename)){
						continue;
					} else {
						raObj.getVisitedCodeSamples().add(file_packagename);
					}					
				}				
				
				//Ignoring files bigger than 50KB. In experience, these are usually not useful
				if(inputFile.length() > 50000) {
					continue;
				}
								
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
					//logger.error("Error occurred while parsing the file: " + inputFile.getName());
		            continue;
		        }
				
				try
				{
					//This also clears the class specific data of the previous run
					astC.preProcessClass(root, inputFile.getName());					
					root.accept(astC);
					astC.closeDebugFile();
				}
				catch(Exception ex)
				{
					logger.error("Error occurred while processing the file " + inputFile.getName() + " " + ex.getMessage());
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

	public TreeSet analyzeLibraryCode(String directoryPath, String sourceObject, String destinationObject, boolean bReUseOldInstance) 
	{
		if(!bReUseOldInstance || astC == null)
			astC = new ASTCrawler(directoryPath);
		astC.sourceObj = "";
		astC.destObj = "";
		
		IJavaProject libProject = RepositoryAnalyzer.getInstance().getLibProject();
		if(libProject == null) {
			logger.warn("No library project is available as input. Nothing can be done, Sorry!!!!");
			return null;
		}
		
		try
		{
			IPackageFragment[] fragments = libProject.getPackageFragments();
	        for (int j = 0; j < fragments.length; j++)
	        {
	            switch (fragments[j].getKind())
	            {
	                case IPackageFragmentRoot.K_SOURCE :
	
	                    /**
	                     * @todo I'm not sure whether K_SOURCE actually means
	                     *       non-Archive (and therefore further testing
	                     *       is obsolete)
	                     */
	                    IPackageFragmentRoot root = (IPackageFragmentRoot) fragments[j].getAncestor(IJavaElement.PACKAGE_FRAGMENT_ROOT);
	
	                    if (!root.isArchive())
	                    {
	                    	ICompilationUnit[] units = fragments[j].getCompilationUnits();
	                    	for(ICompilationUnit icu: units)
	                    	{
	                    		ASTParser parser= ASTParser.newParser(AST.JLS3);
	                    		parser.setProject(libProject);
	                    		parser.setResolveBindings(true);
	                    		parser.setStatementsRecovery(true);

	                    		parser.setSource(icu);
	                    		CompilationUnit cu_java = (CompilationUnit) parser.createAST(null);
	                    		
	                    		try
	            				{
	            					//This also clears the class specific data of the previous run
	                    			String path = icu.getPath().toString();
	                    			int indexOfLastSlash; 
	                    			String localdirPath = "";
	                    			if((indexOfLastSlash = path.lastIndexOf("/")) != -1) {
	                    				localdirPath  = path.substring(0, indexOfLastSlash);
	                    				path = path.substring(indexOfLastSlash + 1, path.length());	                    					                    				
	                    			}
	                    			astC.setDirectoryName(directoryPath + "//" + localdirPath);
	                    			
	            					astC.preProcessClass(cu_java, path);	            					
	            					cu_java.accept(astC);
	            					astC.closeDebugFile();
	            				}
	            				catch(Exception ex)
	            				{
	            					ex.printStackTrace();
	            				}
	                    		
	                    	}
	                    }
	
	                    break;
	
	                default :
	                    break;
	            }
	        }
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
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
	
	public void clearAstc() 
	{
		astC = null;
	}

}
