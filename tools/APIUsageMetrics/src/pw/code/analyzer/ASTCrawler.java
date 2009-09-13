package pw.code.analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import api.usage.LibClassHolder;
import api.usage.RepositoryAnalyzer;
import pw.common.CommonConstants;
import edu.uci.ics.jung.algorithms.shortestpath.DijkstraShortestPath;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;



/**
 * 
 * @author suresh_thummalapenta
 * A class that crawls through the AST for the required nodes.
 */
public class ASTCrawler extends ASTVisitor {

	static Logger logger = Logger.getLogger("ASTCrawler");
	

	public boolean bAPIUsageMetrics = false;
	
    Stack<StackObject> scopeVarStack = new Stack<StackObject>();	//Stack that holds information of all variables that need to be restored when ever scope changes
    Stack<StackObject> scopeMethodStack = new Stack<StackObject>();	//Stack that holds list of method class used for traversing up once a required type was found
    Stack<MethodDeclaration> inliningMethodStack = new Stack<MethodDeclaration>();
    
    
    //Variables storing the current holders like current Class, Method that is parsed
    static TypeDeclaration currentClassDeclaration; //TODO: To handle this as this may need stack as class in class is allowed
    static MethodDeclaration currentMethodDeclaration;
    static String currentFileName;
    
 
    DirectedSparseGraph methodDeclGraph;
    int methodInvocationCounter = 1, noOfSequences = 0, maximumSeqLen = 0, averageSeqLen = 0;
    HashMap methodInvocationMap;
    HashMap sequenceMap;
      
    
	boolean bStartGraph = false, bSourceObjectFound = false;
	MethodInvocationHolder currentMethodInvocationForEdge, sourceMethodInvocationHolder = null, destMethodInvocationHolder = null;

	String outputFile = "PatternSequence.dat";
	public String sourceObj = "", destObj = "";
	
	Boolean bTrue = new Boolean(true);
	Boolean bFalse = new Boolean(false);
	static ASTCrawler currentInstance;
	
	//List of possible sources. The current class and its extends.
	HashSet<String> currentSources = new HashSet<String>(); 
	String currPackageName = "";
	
    /************** Existing Problems with JDT *******************
    1. Obj.method1(param.method2()) : Here JDT initially visits Obj.method1 and then param.method2(). How can this be altered?
    
    
    
    
    
    
    
    ************* End *****************/
    
	//Member variables for trace file
    BufferedWriter bwTraceFileDebug;
    BufferedWriter bwOutput;
    
    //Debugging Information Section
    BufferedWriter brDebug;
    int noOfSpaces = 0;
    String dirName, debugFileName;
    
    
    /** Additional variables for Method Inlining **/
    HashSet<MethodDeclaration> visitedMethodsInInling;
    /** End of additional variables for Method Inlining **/
    
    
   
    public ASTCrawler(String dirName)
    {
    	this.dirName = dirName;
    	
    	//Map that contains all method invocations from all files
    	methodInvocationMap = new HashMap();
    	sequenceMap = new HashMap();
    	visitedMethodsInInling = new HashSet<MethodDeclaration>();
    	
    	try
    	{
    		bwTraceFileDebug = new BufferedWriter(new FileWriter(dirName + "//tracefile.debug.log"));
    		bwOutput = new BufferedWriter(new FileWriter(dirName + "//" + outputFile));
    	}
    	catch(IOException ex)
    	{
    		logger.info(ex.getStackTrace().toString().toString());
    	}
    	
    	currentInstance = this;
    	
    }
  
    public static ASTCrawler getCurrentInstance()
    {
    	return currentInstance;
    }


    /**
     * Function for setting the directory name where DEBUG files are written
     * @param dirName
     */
    public void setDirectoryName(String dirName)
    {
    	this.dirName = dirName;
    }
    
    /**
     * Function for preprocessing the class file.
     * 1. setting the DEBUG filename. File name of the Java class that will be parsed
     * 2. Clearing the class specific data
     * 3. Loading all classes (including inner classes)
     * 4. Loading all method declarations (including method declarations from inner classes)
     * 5. Loading all field declarations
     * (TODO: One case that may not be handled is if the same variable name is used in both parent and inner classes. But this is rare)
     * @param dirName
     * @param fileName
     */
    public void preProcessClass(ASTNode root, String fileName)
    {
    	this.debugFileName = fileName.substring(0, fileName.indexOf('.'));
    	this.debugFileName = dirName + "//" + debugFileName + ".debug.full";
    	
    	try
    	{
	    	if(brDebug != null)
	    	{
	    		try
	    		{
	    			brDebug.close();
	    			brDebug = null;
	    		}
	    		catch(Exception ex)
	    		{
	    			logger.info(ex.getStackTrace().toString());
	    		}
	    	}
	    	brDebug = new BufferedWriter(new FileWriter(debugFileName));
	    	bwTraceFileDebug.write("\n" + fileName + "\n");
	    	currentFileName = fileName;
    	}
    	catch(IOException ex)
    	{
    		logger.error("Error: Unable to open file for writing DEBUG info" + ex);
    		logger.error(ex.getStackTrace().toString());
    	}

    	//Clear the current class
    	currentSources.clear();
    	currPackageName = "";
    	
    	//The field declaration map must be specific to each class.
    	//So, it has to be recreated for every class.
    	ASTCrawlerUtil.fieldDecls.clear();
    	ASTCrawlerUtil.listOfClassDecl.clear();
    	ASTCrawlerUtil.listOfMethodDecl.clear();
    }
    
    public void closeDebugFile()
    {
    	try
    	{
    		brDebug.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    }
    
    /**
     * Function for closing the Trace file at the end
     */
    public TreeSet postProcess()
    {
    	return null;	
    }
    
    
    /**
     * Classes for maintaining variable types, when ever scope of the variable changes
     */
    
     /**
     * Class for identifying the begin and end of the blocks
     * @author suresh_thummalapenta
     *
     */
    private class BlockIdentifier implements StackObject
    {
    	boolean isAClass;
    	
    	public BlockIdentifier(boolean isAClass)
    	{
    		this.isAClass = isAClass;
    	}
    }
    
    /**
     * Class for identifying beginning and end of methods. This is a super set of block as a method also includes parameters
     */
    private class MethodIdentifier implements StackObject
    {
    	
    	
    }
    
    /**
     * Class for restoring the variables to its earlier full class names
     */
    private class VarAndFullClass implements StackObject
    {
    	String variableName;
    	String fullClassName;
    	
    	public VarAndFullClass(String variableName, String fullClassName)
    	{
    		this.variableName = variableName;
    		this.fullClassName = fullClassName;
    	}
    }

    /********* Begin of Pre / Post visitor methods ********/
	/**
	 * Function to handle preprocessing before visiting the block
	 */
	public void preVisit(ASTNode astNode)
	{
		try
		{
			if(astNode instanceof MethodDeclaration)
			{
				currentMethodDeclaration = (MethodDeclaration) astNode;
				scopeVarStack.push(new MethodIdentifier());
			}
			else if(astNode instanceof CatchClause)
			{
				//scopeVarStack.push(new CatchClauseIdentifier());
			}
			else if(astNode instanceof Block || astNode instanceof TypeDeclaration)
			{	
				
				//Handling variable declarations
				scopeVarStack.push(new BlockIdentifier(false));
			}
				
			//Handling method invocations. For IF, WHILE, FOR etc., the statements inside can be 
			//blocks or individual statements
			if(astNode instanceof Block || astNode instanceof Statement)
			{	
				//Return if this block is not contained in any method declaration
				if(!bStartGraph)
					return;
				
				//Handling the control flows for gathering the method invocation sequences
				ASTNode parentNode = astNode.getParent();
				switch(parentNode.getNodeType())
				{
				case ASTNode.IF_STATEMENT:
					//Identifying whether the current block is THEN Block or ELSE block
					IfStatement ifNode = (IfStatement) parentNode;
					if(ifNode.getThenStatement() == astNode)
					{
						//Handling the begin of THEN block of IF statement
						//logger.info("Entered the THEN block of if statement");
						scopeMethodStack.push(currentMethodInvocationForEdge);
						
						
						//Debugging Code
						try
						{
							noOfSpaces = noOfSpaces + 1;
							printSpacestoDebugFile();
							brDebug.write("IF-THEN\n");
						}
						catch(IOException ex)
						{
							logger.info(ex.getStackTrace().toString());
						}
						//End of debugging code
					}
					else if(ifNode.getElseStatement() == astNode)
					{
						//Handling the begin ELSE block of IF statement
						//logger.info("Entered the ELSE block of if statement");
						
						//Nothing has to be done as the end of THEN block automatically handles this case
						//prevMethodInvocationForEdge = ((Integer) scopeMethodStack.pop()).intValue();
						
						//Debugging Code
						try
						{
							noOfSpaces = noOfSpaces + 1;
							printSpacestoDebugFile();
							brDebug.write("IF-ELSE\n");
						}
						catch(IOException ex)
						{
							logger.info(ex.getStackTrace().toString());
						}
						//End of debugging code
						}
					else
					{
						//An unexpected case. Should not occur
						logger.error("Unexpected case!!! ERROR> Should not occur");
					}
					
					break;
				
				case ASTNode.WHILE_STATEMENT:
				case ASTNode.FOR_STATEMENT:
					//Handling the While statement block
					scopeMethodStack.push(currentMethodInvocationForEdge);
					
					//Debugging Code
					try
					{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("WHILE/FOR\n");
					}
					catch(IOException ex)
					{
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					
					break;
				case ASTNode.DO_STATEMENT:
					//Handling the do-while statement block: Nothing has to be done as the code
					//inside Do-While has to be executed atleast once
					//Debugging Code
					try
					{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("DO WHILE BLOCK\n");
					}
					catch(IOException ex)
					{
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				case ASTNode.SWITCH_STATEMENT:
					//TODO: Handling the switch statement
					break;
				case ASTNode.TRY_STATEMENT:
					//Debugging Code
					try
					{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("TRY BLOCK\n");
					}
					catch(IOException ex)
					{
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				case ASTNode.CATCH_CLAUSE:
					//Debugging Code
					try
					{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("CATCH BLOCK\n");
					}
					catch(IOException ex)
					{
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				default:
					//TODO: To check whether any other control flow statements are missing
					break;
					
				}
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			logger.info(ex.getStackTrace().toString());
		}
	}
	
	/**
	 * Function for postprocessing after a block has left
	 */
	public void postVisit(ASTNode astNode)
	{
		try
		{
			if(astNode instanceof MethodDeclaration)
			{
				//APIUsage: In the end of method declaration, if source object found is true,
				//generate a sequence that gives usage pattern of the given
				if(bAPIUsageMetrics && bSourceObjectFound)
				{
					generateTraceByGraphTraversal(sourceMethodInvocationHolder, destMethodInvocationHolder);
				}
				
				currentMethodDeclaration = null;
				methodDeclGraph = null;
								
				
				try
				{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(! (sObj instanceof MethodIdentifier))
					{	
						//Restore the full class name of the variable with its earlier class name
						VarAndFullClass vandFObj = (VarAndFullClass) sObj;
						ASTCrawlerUtil.fieldDecls.put(vandFObj.variableName, vandFObj.fullClassName);
						sObj = (StackObject) scopeVarStack.pop();
					}
				}
				catch(Exception ex)
				{
					logger.info("Exception occurred in file: " + debugFileName);
					logger.info(ex.getStackTrace().toString());
				}
				
				bStartGraph = false;
	
			}
			else if (astNode instanceof CatchClause)
			{
				
			}
			else if(astNode instanceof Block || astNode instanceof TypeDeclaration)
			{	
				
				try
				{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(! (sObj instanceof BlockIdentifier))
					{	
						//Restore the full class name of the variable with its earlier class name
						VarAndFullClass vandFObj = (VarAndFullClass) sObj;
						ASTCrawlerUtil.fieldDecls.put(vandFObj.variableName, vandFObj.fullClassName);
						sObj = (StackObject) scopeVarStack.pop();
					}
				}
				catch(Exception ex)
				{
					logger.info("Exception occurred in file: " + debugFileName);
					logger.info(ex.getStackTrace().toString());
				}
			}
			
			//Handling method invocations. For IF, WHILE, FOR etc., the statements inside can be 
			//blocks or individual statements
			if(astNode instanceof Block || astNode instanceof Statement)
			{	
				//Handling the control flows for gathering the method invocation sequences
				//Return if this block is not contained in any method declaration
				if(bStartGraph)
				{
					ASTNode parentNode = astNode.getParent();
					switch(parentNode.getNodeType())
					{
					case ASTNode.IF_STATEMENT:
						//Identifying whether the current block is THEN Block or ELSE block
						IfStatement ifNode = (IfStatement) parentNode;
						if(ifNode.getThenStatement() == astNode)
						{
							//Handling the end of THEN block of IF statement
	
							if(ifNode.getElseStatement() != null)
							{
								//Else block exists for this if statement
								MethodInvocationHolder topStack = (MethodInvocationHolder) scopeMethodStack.pop();
								scopeMethodStack.push(currentMethodInvocationForEdge);
								currentMethodInvocationForEdge = topStack;
							}
							else
							{
								//No else block for the current IF statement. In this case, make a dummy MehtodInvocationHolder
								//for merging the edges in the graph
								handleEndOfBlock();
							}
						}
						else if(ifNode.getElseStatement() == astNode)
						{
							handleEndOfBlock();
						}
						else
						{
							//An unexpected case. Should not occur
							logger.error("Unexpected case!!! ERROR> Should not occur");
						}
						noOfSpaces = noOfSpaces - 1;
						break;
					
					case ASTNode.WHILE_STATEMENT:
					case ASTNode.FOR_STATEMENT:	
						//Handling the While and For statement block
						handleEndOfBlock();	
						noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.DO_STATEMENT:
						//Handling the do while statement block. Nothing has to be done as the code
						//inside do-while has to execute atleast once
						noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.SWITCH_STATEMENT:
						//TODO: Handling the switch statement
						break;
					
					default:
						//TODO: To check whether any other control flow statements are missing
						break;
						
					}
				}
				
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			//ex.printStackTrace();
		}
	}
    /********* End of Pre / Post visitor methods *********/

	
    
    /**
     * Store each import declaration
     */
    public boolean visit(ImportDeclaration idNode)
	{
    	try
    	{
    		String impName = idNode.getName().getFullyQualifiedName();
			String className = impName.substring(impName.lastIndexOf(".") + 1, impName.length());
	
			if(!className.equals("*"))
			{	
				if(impName.startsWith("java."))
				{
					ASTCrawlerUtil.importJDKClasses.add(className);
				}
				ASTCrawlerUtil.fullPackageNamesForClasses.put(className, impName);
			}
			else
			{
				//TODO: To load all classes contained in that given package by some means
			}
			
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			logger.info(ex.getStackTrace().toString());
		}	
		return true;
	}
    
    /**
     * Function for handling Inner classes, interfaces, which are stored as TypeDeclaration object
     */
    public boolean visit(TypeDeclaration typeNode)
    {
    	//APIUsage metrics: Ignore the classes that belong to current package
    	RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
    	if(bAPIUsageMetrics && (raObj.getLibPackageList().contains(currPackageName)
    			/*|| raObj.ignoreDNSJavaSample(currentFileName)*/))
    	{
    		return false;
    	}
    	
    	if(typeNode.isInterface())
    	{
    		//Not handling interfaces
    		return false;
    	}
    	else 
    	{
    		//Handle a class declaration
    		currentClassDeclaration = typeNode;
    		
    		//Preprocess the entire class, if this is the top most class
    		if(ASTCrawlerUtil.listOfClassDecl.size() == 0)
    		{
    			ASTCrawlerUtil.preProcessClassDecl(typeNode);
    		}
    		
    		//Extract the parent class type and interfaces it implements
    		//because they can be sources of given query.
    		currentSources.add(currPackageName + "." + currentClassDeclaration.getName());
    		ASTCrawlerUtil.fullPackageNamesForClasses.put(currentClassDeclaration.getName().toString(), CommonConstants.multipleCurrTypes);
    		    		
    		try
    		{
	    		if(typeNode.getSuperclassType() != null)
	    		{
	    			Type superClsType = (Type)typeNode.getSuperclassType();
	    			String fullNameOfSuperCls = ASTCrawlerUtil.getFullClassName(superClsType);
	    			
	    			//APIUsage Metrics
	    			if(bAPIUsageMetrics)
	    				RepositoryAnalyzer.getInstance().handleClassExtension(fullNameOfSuperCls, typeNode, false, dirName + "//" + currentFileName, "NA", currentFileName);
	    			
	    			if(fullNameOfSuperCls.equals(CommonConstants.unknownType)|| fullNameOfSuperCls.equals("#LOCAL#"))
	    			{
	    				if(superClsType instanceof SimpleType)
	    					fullNameOfSuperCls = currPackageName + "." + ((SimpleType)superClsType).getName().getFullyQualifiedName();
	    			}
    				currentSources.add(fullNameOfSuperCls);
	    		}
    		
	    		List superInterfaces = typeNode.superInterfaceTypes();
	    		if(superInterfaces != null)
	    		{
	    			for(Iterator tmp_iter = superInterfaces.iterator(); tmp_iter.hasNext(); )
	    			{
	    				Type superInterfaceType = (Type) tmp_iter.next();
		    			String fullNameOfSuperInterface = ASTCrawlerUtil.getFullClassName(superInterfaceType);
		    			
		    			//APIUsage Metrics
		    			if(bAPIUsageMetrics)
		    				RepositoryAnalyzer.getInstance().handleClassExtension(fullNameOfSuperInterface, typeNode, true, dirName + "//" + currentFileName, "NA", currentFileName);

		    			
		    			if(fullNameOfSuperInterface.equals(CommonConstants.unknownType) || fullNameOfSuperInterface.equals("#LOCAL#"))
		    			{
		    				if(superInterfaceType instanceof SimpleType)
		    					fullNameOfSuperInterface = currPackageName + "." + ((SimpleType)superInterfaceType).getName().getFullyQualifiedName();
		    			}
		    			currentSources.add(fullNameOfSuperInterface);
		    			
	    			}
	    		}
	    		
	    		
    		}
    		catch(Exception ex)
    		{
    			logger.error("Error occurred while adding possible sources from current class " + ex);
    			ex.printStackTrace();
    			
    		}
	    	return true;
    	}
    }
    
    /**
     * Function for handling package statement
     */
    public boolean visit(PackageDeclaration pdNode)
    {
    	currPackageName = pdNode.getName().getFullyQualifiedName();
   	
    	return true;
    }
    
	/**
	 * Function for handling field declarations
	 */
	public boolean visit(FieldDeclaration fdNode)
	{
		return true;
	}
	
	//Variable that holds the root of the tree;
	MethodInvocationHolder mihRoot;	
	
	/**
	 * Function for visiting method declarations
	 */
	public boolean visit(MethodDeclaration md)
	{
		inliningMethodStack.clear();
		destMethodInvocationHolder = null;
		sourceMethodInvocationHolder = null;
		
		//If the method declaration has throws clause that includes
		//exceptions of interest from the input framework, then also increment
		//the number of invocations
		if(md.thrownExceptions().size() != 0) {
			for(Object exceptionObj : md.thrownExceptions()) {
				if(exceptionObj instanceof SimpleName) {
					String exceptionClsName = ASTCrawlerUtil.fullPackageNamesForClasses.get(((SimpleName)exceptionObj).getIdentifier());
					if(exceptionClsName != null)
						RepositoryAnalyzer.getInstance().handleExceptionInstance(exceptionClsName, currentFileName,
							currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
				}		
			}		
		}
		
		return localVisit(md, false);
	}
	
	/**
	 * A local visit function
	 */
	private boolean localVisit(MethodDeclaration md, boolean bInlining)
	{
		if(!bInlining)
		{	
			methodDeclGraph = new DirectedSparseGraph();
			bSourceObjectFound = false;
			bStartGraph = true;
			//Note: Turning this flag to false makes sure that graphs are built for a method only when required, but 
			//there is a risk with that if source and destination objects are referred in different places.
			
			visitedMethodsInInling.clear();
			
			//Declaraing the root node of method as 0 and adding it to the graph
			mihRoot = MethodInvocationHolder.getDummyMethodInvocationHolder();
			methodDeclGraph.addVertex(mihRoot);
			currentMethodInvocationForEdge = mihRoot;
		}	
		
		//Add all method arguments to field Declaration map
		List parameters = md.parameters();
		for(Iterator iter = parameters.iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration svdObj = (SingleVariableDeclaration) iter.next();
			addSingleVariableDeclaration(svdObj);
		}
		//End of adding arguments
		
		if(!bInlining) {	
			//Debugging section
			try	{
				brDebug.write("\n\n" + md.getName().getIdentifier() + "\n");
			} catch(IOException ex) {
				logger.info(ex.getStackTrace().toString());
			}			
			noOfSpaces = 0;
			//End of debugging section
		} else {
			try {
				printSpacestoDebugFile();
				brDebug.write("INLINING THE METHOD " + md.getName().getIdentifier() + "\n");
			} catch(IOException ex)	{
				logger.info(ex.getStackTrace().toString());
			}
		}		
		return true;
	}
	
	
	/**
	 * Function to handle the creation of MethodInvocationHolder object
	 */
	public boolean handleMethodInvocationHolder(MethodInvocationHolder mihObj, String refType, String returnType)
	{
		try
		{
			methodDeclGraph.addVertex(mihObj);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentMethodInvocationForEdge, mihObj));
		}
		catch(edu.uci.ics.jung.exceptions.ConstraintViolationException ex)
		{
			//ex.printStackTrace();
			throw ex;
		}
		currentMethodInvocationForEdge = mihObj;
		
		if(bAPIUsageMetrics)
		{
			LibClassHolder lch = RepositoryAnalyzer.getInstance().getLibClassMap().get(refType);
			
			if(lch != null)
			{	
				if(!bSourceObjectFound)
				{
					bSourceObjectFound = true;
					sourceMethodInvocationHolder = mihObj;			
				}
				else
				{
					destMethodInvocationHolder = mihObj;
				}
			}	
		}
		
				
		//Debugging section
		try
		{
			printSpacestoDebugFile();
			brDebug.write("\t " + mihObj + "\n");
		}
		catch(IOException ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
		//End of debugging section

		return true;
	}
	
	
	/**
	 * Function for visiting method invocation
	 */
	public boolean visit(MethodInvocation miObj)
	{	
		Object prop = miObj.getProperty("visited"); 
		if(prop != null && ((Boolean)prop).booleanValue())
		{
			miObj.setProperty("visited", null);
			return false;
		}
		
		try
		{
			//Method invocations outside a method declaration are not handled
			if(!bStartGraph)
				return false;
			
			//In case of expressions like m_factory.createQueueConnection().createQueueSession().createQueueSession()
			//ASTVisitor calls method invocations in a wrong order. To override it, the current visitor 
			//method is made as recursive function
			Expression expr = miObj.getExpression();
						
			if(expr instanceof MethodInvocation)
			{
				visit((MethodInvocation) expr);
				expr.setProperty("visited", bTrue);
			}
			
			//Visit the arguments first
			handleArgumentMethodInvocations(miObj.arguments());

			String refType = ASTCrawlerUtil.getRefClassName(miObj.getExpression());
			String returnType = ASTCrawlerUtil.getReturnType(miObj);
			
			if(refType.equals("this"))
			{
				refType = CommonConstants.multipleCurrTypes;
			}
						
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, returnType, miObj);
			
			//APIUsage: Call back for APIUsage (Don't count when methods are inlined. This is to prevent counting of same invocation again
			if(bAPIUsageMetrics && inliningMethodStack.size() == 0) {	
				if(refType.equals(CommonConstants.multipleCurrTypes)) {
					RepositoryAnalyzer.getInstance().handleMethodInvocation(currentSources, mihObj, refType, currentFileName,
							currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
				} else {
					RepositoryAnalyzer.getInstance().handleMethodInvocation(refType, mihObj, currentFileName,
							currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
				}	
			}	
			
			if(miObj.getParent() instanceof CastExpression)
			{
				mihObj.bDowncast = true;
			}			
		}
		catch(Exception ex)
		{
			logger.info("Happened in file: " + debugFileName);
			
		}			
		
		return true;
	}
	
	
	/*
	 * Special function for handling the case where Arguments of 
	 * a method invocation are method invocations... The reason for this is
	 * JDT visits them in a wrong order
	 */
	public void handleArgumentMethodInvocations(List argumentArr)
	{
		for(Iterator iter = argumentArr.iterator(); iter.hasNext();)
		{
			Expression expr = (Expression) iter.next();
		
			if((expr instanceof MethodInvocation))
			{
				visit((MethodInvocation) expr);
				expr.setProperty("visited", bTrue);
			}
			else if(expr instanceof CastExpression)
			{
				visit((CastExpression) expr);
				expr.setProperty("visited", bTrue);
			}
			else if(expr instanceof ClassInstanceCreation)
			{
				visit((ClassInstanceCreation) expr);
				expr.setProperty("visited", bTrue);
			}
		}
	}

	/**
	 * Function for handling class instantiation. As class intiations are also resulting in transformation of 
	 * objects, they are very important
	 */
	public boolean visit(ClassInstanceCreation cicObj)
	{
		Object prop = cicObj.getProperty("visited"); 
		if(prop != null && ((Boolean)prop).booleanValue())
		{	
			cicObj.setProperty("visited", null);
			return false;
		}	

		try
		{
			if(!bStartGraph)
				return false;
			
			//Visit the arguments first
			handleArgumentMethodInvocations(cicObj.arguments());
			
			String retType = ASTCrawlerUtil.getFullClassName(cicObj.getType());
			MethodInvocationHolder mihObj = new MethodInvocationHolder(retType, retType, cicObj);
			
			//APIUsage Metrics
			if(bAPIUsageMetrics && inliningMethodStack.size() == 0)
			{	
				RepositoryAnalyzer.getInstance().handleMethodInvocation(retType, mihObj, currentFileName,
						currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
				RepositoryAnalyzer.getInstance().handleClassInstanceCreation(retType, currentFileName,
						currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
			}		
			
		}	
		catch(Exception ex)
		{
			logger.info("Exception occurred  " + ex);
		}
		return true;
	}
	
	/**
	 * Function for handling down cast expressions. These expressions are very important
	 * as they contain important information
	 */
	public boolean visit(CastExpression ceObj)
	{		
		Object prop = ceObj.getProperty("visited"); 
		if(prop != null && ((Boolean)prop).booleanValue())
		{	
			ceObj.setProperty("visited", null);
			return false;
		}	

		try
		{
			//Castings outside method declarations are not handled
			if(!bStartGraph)
				return false;
			
			Expression expr = ceObj.getExpression();
			if((expr instanceof MethodInvocation))
			{
				visit((MethodInvocation) expr);
				
				//Below line is commented because of some un-expected behaviour of 
				//JDT, where it is not calling the visitor method invocation after calling
				//the cast expression visitor method. The reason is unknown.
				//expr.setProperty("visited", bTrue);
				return false;
			}			
		}	
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
		return true;
	}
	
	/**
	 * Function for visiting Catch Clause
	 */
	public boolean visit(CatchClause ccObj)
	{
		String exceptionClsName = ASTCrawlerUtil.getFullClassName(ccObj.getException().getType());
		RepositoryAnalyzer.getInstance().handleExceptionInstance(exceptionClsName, currentFileName,
				currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
		return true;
	}
	
	public boolean visit(VariableDeclarationExpression vdeObj)
	{
		Type type = vdeObj.getType();
		String fullClassName = "N/A";
		fullClassName = ASTCrawlerUtil.getFullClassName(type);		
		addMultipleVariableDeclarations(vdeObj.fragments(), fullClassName);
		return true;
	}
	
	/**
	 * Function for visiting block
	 */
	public boolean visit(Block block)
	{
		try
		{
			List stmtList = block.statements();
			//In the beginning of each block, add new variables to ASTCrawlerUtil.fieldDecls map and incase an old value is replaced
			//Push it on to the stack. TODO: To think whether to delete the new values that are added to HashMap as a 
			//part of this function. 
			for(Iterator iter = stmtList.iterator(); iter.hasNext();)
			{
				Statement stmt = (Statement) iter.next();
				if(stmt instanceof VariableDeclarationStatement)
				{
					VariableDeclarationStatement vdStmt = (VariableDeclarationStatement) stmt;
					Type type = vdStmt.getType();
					String fullClassName = "N/A";
					fullClassName = ASTCrawlerUtil.getFullClassName(type);
					addMultipleVariableDeclarations(vdStmt.fragments(), fullClassName);
				}
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			logger.info(ex.getStackTrace().toString());
		}
		return true;
	}
	
	public boolean visit(Assignment asNode)
	{
		if(!bAPIUsageMetrics)
			return true;
		
		Expression leftHandSide = asNode.getLeftHandSide();
		Expression rightHandSide = asNode.getRightHandSide();
		
		//APIUsage: The run-time type of a variable can be modified if the assignment
		//statement contains some hints in the changes of type. Currently only constructor and typecast
		//are considered.
		if(leftHandSide instanceof SimpleName && (rightHandSide instanceof ClassInstanceCreation || rightHandSide instanceof CastExpression))
		{
			String initializerType = ASTCrawlerUtil.getRefClassName(rightHandSide);
			if(!initializerType.equals(CommonConstants.unknownType) && !initializerType.equals("this") && !initializerType.equals(CommonConstants.multipleCurrTypes) &&!initializerType.equals("null"))
			{
				ASTCrawlerUtil.fieldDecls.put(((SimpleName)leftHandSide).getIdentifier(), initializerType);
			}
		}
		
		return true;
	}
	
	
	/**
	 * Function for visiting Anonymous class declarations
	 */
	public boolean visit(AnonymousClassDeclaration acd)
	{
		//These declarations are not handled currently
		return false;
	}
	
	/**
	 * Function for handling the qualified name
	 */
	public boolean visit(QualifiedName qnObj) 
	{
		if(qnObj.getQualifier() instanceof SimpleName) {
			String secondPart = qnObj.getName().getIdentifier();
			if(secondPart.toUpperCase().equals(secondPart)) {
				//Can be a constant variable whose usage metrics needs to be incremented
				String className = ASTCrawlerUtil.fullPackageNamesForClasses.get(((SimpleName)qnObj.getQualifier()).getIdentifier());
				if(className == null)
					return true;
				RepositoryAnalyzer.getInstance().handleMemberVarInstance(className, secondPart, currentFileName,
						currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
			}		
		}
		return true;
	}
	
	/********* Begin of internal traversal methods *******************/
	/**
	 * Function for connecting the edges in the graph at the end of the block
	 */
	public void handleEndOfBlock()
	{
		MethodInvocationHolder topStack = (MethodInvocationHolder) scopeMethodStack.pop();
		MethodInvocationHolder mihDummy = MethodInvocationHolder.getDummyMethodInvocationHolder();
		
		try
		{
			methodDeclGraph.addVertex(mihDummy);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentMethodInvocationForEdge, mihDummy));
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		//If no nodes are added to the graph by the then block or else block, no need to add the second aspect
		if(topStack != currentMethodInvocationForEdge)
			methodDeclGraph.addEdge(new DirectedSparseEdge(topStack, mihDummy));
		
		currentMethodInvocationForEdge = mihDummy;
	}
	
	/**
	 * Function for adding single variable declararion fragments to FieldDecls set
	 * @param svdObj
	 */
    private void addSingleVariableDeclaration(SingleVariableDeclaration svdObj)
    {
		String fullClassName = ASTCrawlerUtil.getFullClassName(svdObj.getType());
		
		String prevFullClassName = (String) ASTCrawlerUtil.fieldDecls.put(svdObj.getName().getIdentifier(), fullClassName);
		if(prevFullClassName != null)
		{
			//There is an earlier full class name associated with this variable outside the current scope
			VarAndFullClass vandfobj = new VarAndFullClass(svdObj.getName().getIdentifier(), prevFullClassName);
			scopeVarStack.push(vandfobj);
		}

    }
    
    /**
     * Function for adding multiple variable declaration fragments to FieldDecls set
     */
    private void addMultipleVariableDeclarations(List fragments, String fullClassName)
    {
		//Code for traversing the children in each declaration
		
		for(Iterator iter = fragments.iterator(); iter.hasNext();)
		{
			VariableDeclarationFragment vdfObj = (VariableDeclarationFragment) iter.next();
			
			if(vdfObj.getInitializer() != null)
			{
				//An attempt to get runtime type if it is specified right in the initializer. : APIUSage
				if(bAPIUsageMetrics)
				{	
					String initializerType = ASTCrawlerUtil.getRefClassName(vdfObj.getInitializer());
					if(!initializerType.equals(CommonConstants.unknownType) && !initializerType.equals("this") && !initializerType.equals(CommonConstants.multipleCurrTypes) &&!initializerType.equals("null"))
						fullClassName = initializerType;
				}	
			}
			
			String prevClassName = (String) ASTCrawlerUtil.fieldDecls.put(vdfObj.getName().getIdentifier(), fullClassName);
			if(prevClassName != null)
			{
				//There is an earlier full class name associated with this variable outside the current scope
				VarAndFullClass vandfobj = new VarAndFullClass(vdfObj.getName().getIdentifier(), prevClassName);
				scopeVarStack.push(vandfobj);
			}
		}

    }
	
	/**
	 * Function for traversing up the tree till the source object is found: Function modified for APIUsage
	 */
	public void generateTraceByGraphTraversal(MethodInvocationHolder srcMIObj, MethodInvocationHolder destMIObj)
	{
		if(true)
			return;
		
		try
		{
			//If there is just a single method call, dest can be null
			if(destMIObj == null)
			{
				//TODO: There is some problem over here.
				try{
					RepositoryAnalyzer.getInstance().addToMethodSequences(srcMIObj, dirName + "//" + currentFileName, 
							currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
				} catch(Exception ex) {
					
				}				
				return;
			}
			
			//TODO: To figure out how to obtain all paths
			
			DijkstraShortestPath dspObj = new DijkstraShortestPath(methodDeclGraph);
			List path = dspObj.getPath(srcMIObj, destMIObj);
			
			//Calculate the list of method calls that belong to current library of interest
			List<MethodInvocationHolder> mihList = new ArrayList<MethodInvocationHolder>();
			DirectedSparseEdge dseEdge = null;
			for(Iterator iter = path.iterator(); iter.hasNext();)
			{
				dseEdge = (DirectedSparseEdge)iter.next();
				mihList.add((MethodInvocationHolder) dseEdge.getSource());				
			}
			
			if(dseEdge != null)
			{
				mihList.add((MethodInvocationHolder) dseEdge.getDest());
			}
			
			RepositoryAnalyzer.getInstance().addToMethodSequences(mihList, dirName + "//" + currentFileName, currentMethodDeclaration.getName().toString());
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	/******** Begin of Misc functions for debugging ************/
	public void printGraphEdges()
	{
		try
		{
			Set allEdges = methodDeclGraph.getEdges();
			
			brDebug.write("\nGraph of the method:\n");
			
			for(Iterator iter = allEdges.iterator(); iter.hasNext();)
			{
				DirectedSparseEdge dseEdge = (DirectedSparseEdge) iter.next();
				brDebug.write(dseEdge.getSource().toString() + " --> " + dseEdge.getDest().toString() + "\n");
			}
		}
		catch(IOException ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
		
	}
	
	public void printSpacestoDebugFile()
	{
		try
		{
			for(int i = 0; i < noOfSpaces; i ++)
			{
				brDebug.write("\t");
			}
		}
		catch(IOException ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
	}
	
	/******** End of Misc functions for debugging **************/
	
}
