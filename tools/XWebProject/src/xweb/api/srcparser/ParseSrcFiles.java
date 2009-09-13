package xweb.api.srcparser;

import java.io.File;
import java.io.FileReader;

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
 * A Class that parses the given source folder and generates the classes
 * and methods of interest that serve as queries for GCSE.
 */
public class ParseSrcFiles {
	static private Logger logger = Logger.getLogger("ParseSrcFiles");
	
	ParseSrcVisitor psv;
	String debugDir;
	ClassOnlyVisitor cov;
	InheritanceHierarchyVisitor ihv;
	IJavaProject ijp;
	
	public ParseSrcFiles()
	{
		try
		{
			psv = new ParseSrcVisitor();
			cov = new ClassOnlyVisitor();
			ihv = new InheritanceHierarchyVisitor();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
	/**
	 * Parsing library sources by accepting the entire project
	 * that automatically handles the type resolution.
	 * @param ijp
	 */
	public void parseSources(IJavaProject ijp)
	{
		this.ijp = ijp;
		
		//Phase 1: Parse the library sources and load all package names and class names
		parseSourceInternal(ijp, 1);

		//Phase 2: Compute inheritance hierarchies. This is treated as a different
		//step as it is required for phase 3 to complete successfully.
		parseSourceInternal(ijp, 2);
		
		//Phase 3: Parse the entire sources set
		parseSourceInternal(ijp, 3);
	}
	
	
	private void parseSourceInternal(IJavaProject ires, int phase)
	{
		try
		{
			IPackageFragment[] fragments = ires.getPackageFragments();
	        for (int j = 0; j < fragments.length; j++)
	        {
	            switch (fragments[j].getKind())
	            {
	                case IPackageFragmentRoot.K_SOURCE :
	
	                    IPackageFragmentRoot root =
	                        (IPackageFragmentRoot) fragments[j].getAncestor(
	                            IJavaElement.PACKAGE_FRAGMENT_ROOT);
	
	                    if (!root.isArchive())
	                    {
	                    	ICompilationUnit[] units = fragments[j].getCompilationUnits();
	                    	for(ICompilationUnit icu: units)
	                    	{
	                    		ASTParser parser= ASTParser.newParser(AST.JLS3);
	                    		parser.setProject(ires);
	                    		parser.setResolveBindings(true);
	                    		parser.setStatementsRecovery(true);

	                    		parser.setSource(icu);
	                    		CompilationUnit cu_java = (CompilationUnit) parser.createAST(null);
	                    		switch(phase)
	                    		{
		                    		case 1: cu_java.accept(cov); break;
		                    		case 2: cu_java.accept(ihv); break;
		                    		case 3: cu_java.accept(psv); break;
		                    		default: break;
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
	}
	
	
	/**
	 * Parsing the library source files in a partial manner instead of parsing
	 * entire library. (OBSOLETE)
	 * @param inputFile
	 */
	public void parseSources(File inputFile)
	{
		//Phase 1: Parse the library sources and load all package names and class names
		parseSourceInternal(inputFile, 1);

		//Phase 2: Parse the entire sources set
		parseSourceInternal(inputFile, 2);
    }
	
	
	private void parseSourceInternal(File inputFile, int phase)
	{
		ASTParser parser= ASTParser.newParser(AST.JLS3);
		

        if(inputFile.isDirectory())
        {
        	String[] candidates = inputFile.list();
        	for (String file : candidates) {
        		File fcand = new File(inputFile.getAbsolutePath() + CommonConstants.FILE_SEP + file);
        		parseSourceInternal(fcand, phase);
        	}
        }
        else
        {
        	//Source file
        	if(phase == 2)
        		logger.debug(inputFile.getAbsolutePath());
        	
        	if(inputFile.getName().endsWith(".java") || inputFile.getName().endsWith(".JAVA"))
        	{
        		try
        		{
					char[] source = new char[(int) inputFile.length()];
			        FileReader fileReader = new FileReader(inputFile);
			        fileReader.read(source);
			        fileReader.close();
					
					parser.setSource(source);
					parser.setResolveBindings(true);
					parser.setStatementsRecovery(true);

					CompilationUnit root = (CompilationUnit) parser.createAST(null);
					
					if(phase == 1)
					{
						root.accept(cov);
					}
					else
					{	
						//This also clears the class specific data of the previous run
						root.accept(psv);
					}	
        		}
        		catch(Exception ex)
        		{
        			ex.printStackTrace();
        		}
        	}        	
        }
	}
	
	/**
	 * For handling clean up activities at the end
	 *
	 */
	public void cleanUp()
	{

	}
	
}
