package pw.code.analyzer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
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
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

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
	MethodInvocationHolder currentMethodInvocationForEdge, sourceMethodInvocationHolder;

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
    		logger.info("Error: Unable to open file for writing DEBUG info" + ex);
    		logger.info(ex.getStackTrace().toString());
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
    	TreeSet<SequenceStore>  sortSet = new TreeSet<SequenceStore>(new SequenceStore());
    	try
    	{
    		String lowConfSeq = "";
    		
    		//Sorting the final sequences
    		//Eliminate the sequence with confidence "low", if there are sequences if confidence "high"
    		boolean bHighConfSequence = false;
    		for(Iterator iter = sequenceMap.values().iterator(); iter.hasNext();)
    		{
    			SequenceStore ssStore = (SequenceStore) iter.next();
    			if(ssStore.confidenceLevel == true)
    			{
    				bHighConfSequence = true;
    				sortSet.add(ssStore);
    			}
    			else
    			{
    				lowConfSeq = lowConfSeq + "\nFileName:" + ssStore.javaFileName + " MethodName:" + ssStore.methodName 
						+ " NumberOfOccurrences:" + ssStore.numOfTimes + " Confidence:" + ssStore.confidenceLevel
						+ " Path:" + ssStore.actualPath + "\n"; 
    				
    				lowConfSeq = lowConfSeq + "\n\t" + ssStore.sequence + "\n";
    				
    			}
    		}
    		
    		//If there are no sequences of high confidence, then only add the sequences of low confidence
    		if(bHighConfSequence == false)
    			sortSet.addAll(sequenceMap.values());
    		
    		//Values after sorting (In the debug file all sequences are written. But for the user only first 10 are shown)
    		int rank = 1;
    		int counter = 0;
    		for(Iterator iter = sortSet.iterator(); iter.hasNext();)
    		{
    			SequenceStore ssStore = (SequenceStore) iter.next();
    			ssStore.rank = rank;
				bwOutput.write("\nFileName:" + ssStore.javaFileName + " MethodName:" + ssStore.methodName 
												+ " Rank:" + rank + " NumberOfOccurrences:" + ssStore.numOfTimes + " Confidence:" + ssStore.confidenceLevel
												+ " Path:" + ssStore.actualPath);
				bwOutput.write("\n\t" + ssStore.sequence + "\n");
				rank++;
				
				counter ++;
				//if(counter == CommonConstants.MAX_NUM_SEQUENCE_OUTPUT)
				//	break;
    		}
    		
    		
    		if(bHighConfSequence)
    		{
    			//In case of high confidence sequence, the ones with low confidence does not appear.
    			//This part is mainly for debugging purpose only
    			bwOutput.write("\n\n\n===== LOW CONFIDENCE LIST ===== \n\n\n");
    			bwOutput.write(lowConfSeq);
    		}
    		
    		
    		bwOutput.close();
    		bwTraceFileDebug.close();
    	}
    	catch(Exception e)
    	{
    		e.printStackTrace();
    	}
    	
    	return sortSet;
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
     * Class for identifying beginning and end of methods. This is a super set of block as a method also includes parameters
     */
    private class CatchClauseIdentifier implements StackObject
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
						logger.info("Unexpected case!!! ERROR> Should not occur");
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
				//logger.info("Graph built:" + methodDeclGraph);
				
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
				/*try
				{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(! (sObj instanceof CatchClauseIdentifier))
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
				}*/
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
							logger.info("Unexpected case!!! ERROR> Should not occur");
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
			ex.printStackTrace();
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
    			System.out.println("Error occurred while adding possible sources from current class " + ex);
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
		/*try (Handled by preProcess functions)
		{
			Type type = fdNode.getType();
			String fullClassName = "N/A";
			fullClassName = ASTCrawlerUtil.getFullClassName(type);
			addMultipleVariableDeclarations(fdNode.fragments(), fullClassName);
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			logger.info(ex.getStackTrace().toString());
		}*/		
		return true;
	}
	
	//Variable that holds the root of the tree;
	MethodInvocationHolder mihRoot;
	
	
	/**
	 * Function for visiting method declarations
	 */
	public boolean visit(MethodDeclaration md)
	{
		return localVisit(md, false);
	}
	
	/**
	 * A local visit function
	 */
	private boolean localVisit(MethodDeclaration md, boolean bInlining)
	{
		if(!bInlining)
		{	
			if(md.getName().toString().equals("getSelection"))
			{
				int i = 0;
			}
			
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
			
			//If source object is not provided, make source as the Root
			if(sourceObj.equals(""))
			{
				bSourceObjectFound = true;
				sourceMethodInvocationHolder = mihRoot;
			}

		}	
		
		//Add all method arguments to field Declaration map
		List parameters = md.parameters();
		for(Iterator iter = parameters.iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration svdObj = (SingleVariableDeclaration) iter.next();
			addSingleVariableDeclaration(svdObj);
		}
		//End of adding arguments
		
		if(!bInlining)
		{	
			//Debugging section
			try
			{
				brDebug.write("\n\n" + md.getName().getIdentifier() + "\n");
			}
			catch(IOException ex)
			{
				logger.info(ex.getStackTrace().toString());
			}
			
			noOfSpaces = 0;
			//End of debugging section
		}
		else
		{
			try
			{
				printSpacestoDebugFile();
				brDebug.write("INLINING THE METHOD " + md.getName().getIdentifier() + "\n");
			}
			catch(IOException ex)
			{
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
			ex.printStackTrace();
			throw ex;
		}
		currentMethodInvocationForEdge = mihObj;
		
		if(refType.equals(sourceObj) || returnType.equals(sourceObj) || mihObj.isPresentInArgumentArr(sourceObj))
		{
			bSourceObjectFound = true;
			sourceMethodInvocationHolder = mihObj;
		}
		else if(refType.equals(CommonConstants.multipleCurrTypes))
		{
			//A situation where either the parent class or interfaces are possible sources.
			//Implemented for feature: InheritanceHeuristic
			if(currentSources.contains(sourceObj))
			{
				bSourceObjectFound = true;
				sourceMethodInvocationHolder = mihObj;
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

		
		if(returnType.equals(destObj) && bSourceObjectFound)
		{
			//logger.info("Hurry found the required source and destination object!!! in the methodDeclaration " + currentMethodDeclaration);
				
			//TODO: For time being, it is assumed that only one combination could be there 
			//in the given method declaration. In case a method declaration has more than one combination,
			//it needs to be handled in future
			
			//Stop building the graph once the destination object has been reached
			//bStartGraph = false;
			
			//Uncomment this to see the layout of graph.
			//printGraphEdges();
			generateTraceByGraphTraversal(sourceMethodInvocationHolder, mihObj);
			
			return false;
		}
		
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
			
			//TODO: Can we modify this part of the code so that the entire part of
			//method sequence is treated as just one with reference type and return type
			//Say Z = A.b().c().d() then we can say that a sequence b().c().d() results in 
			//type 'Z' when applied on 'A'.
			//Handling the above cases in a special form		
			//while(expr instanceof MethodInvocation)
			//{
			//	expr = ((MethodInvocation)expr).getExpression();
			//	expr.setProperty("visited", bTrue);
			//}
			
			//Visit the arguments first
			handleArgumentMethodInvocations(miObj.arguments());

			
			String refType = ASTCrawlerUtil.getRefClassName(miObj.getExpression());
			String returnType = ASTCrawlerUtil.getReturnType(miObj);
			
			if(refType.equals("this"))
			{
				MethodDeclaration classMethodDeclObj = (MethodDeclaration) ASTCrawlerUtil.getMethodDeclNode
							(miObj.getName().getIdentifier(), miObj.arguments());
			
				//visitedMethodInInlining helps to avoid both direct and indirect recursion
				//The check for classMethodDeclObj with null is done because the method can be there in 
				//parent class, in which the "getMethodDeclNode" returns "null".
				//TODO: This is a place for performance improvement where earlier parsed method declaration can be extracted and clubbed.
				if(classMethodDeclObj != null && !visitedMethodsInInling.contains(classMethodDeclObj))
				{
					visitedMethodsInInling.add(classMethodDeclObj);
					
					localVisit(classMethodDeclObj, true);
					
					inliningMethodStack.push(currentMethodDeclaration);
					currentMethodDeclaration = classMethodDeclObj;
					
					ASTNode bodyNode = classMethodDeclObj.getBody();
					if(bodyNode != null)
						bodyNode.accept(this);
				
					currentMethodDeclaration = inliningMethodStack.pop();
					
					//TODO: For time being, methods are inlined only once for simplicity
					//visitedMethodsInInling.remove(classMethodDeclObj);
				
					return true;
				}
				else
				{
					//If no possible method declaration for this class is found, It can be from 
					//its parent classes. So the parent class or current class or interfaces 
					//can be considered as sources.
					refType = CommonConstants.multipleCurrTypes;
				}
			}
						
			//TODO: Currently the method parameters are not considered for time being
			//Only tracing is done with return types and reference class names
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, returnType, miObj);

			if(miObj.getParent() instanceof CastExpression)
			{
				mihObj.bDowncast = true;
			}
			
			return handleMethodInvocationHolder(mihObj, refType, returnType);
			
		}
		catch(Exception ex)
		{
			logger.info("Happened in file: " + debugFileName);
			ex.printStackTrace();
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
			
			String refType = "CONSTRUCTOR";
			String retType = ASTCrawlerUtil.getFullClassName(cicObj.getType());
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, retType, cicObj);
			
			return handleMethodInvocationHolder(mihObj, refType, retType);
		}	
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
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

		
		//Currently, if cast expressions are a part of Method invocation, there
		//are automatically handled by above MethodInvocationVisitor. So, here 
		//we are not concerned of that
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
			
			String refType = ASTCrawlerUtil.getRefClassName(ceObj.getExpression());	
			String returnType = ASTCrawlerUtil.getRefClassName(ceObj);
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, returnType, ceObj);
				
			return handleMethodInvocationHolder(mihObj, refType, returnType);
				
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
		return false;
		/*SingleVariableDeclaration svdObj = ccObj.getException();
		addSingleVariableDeclaration(svdObj);
		return true;*/
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
	
	/**
	 * Function for visiting Anonymous class declarations
	 */
	public boolean visit(AnonymousClassDeclaration acd)
	{
		//These declarations are not handled currently
		return false;
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
	 * Function for traversing up the tree till the source object is found
	 */
	public void generateTraceByGraphTraversal(MethodInvocationHolder srcMIObj, MethodInvocationHolder destMIObj)
	{
		try
		{
			
			Stack<MethodInvocationHolder> llPathStack = new Stack<MethodInvocationHolder>();
			
			//If source and destination are same in which the transformation from source to 
			//destination can be achieved with in a single call
			StringBuffer traceStringBfr = new StringBuffer();
			if(srcMIObj == destMIObj)
			{
				assignKeyValueToMethodInvocation(srcMIObj);
				llPathStack.push(srcMIObj);
				traceStringBfr.append("\t" + srcMIObj.getVisualizationString() + "\n");
				//bwTraceFileDebug.write("\t" + srcMIObj + "\n");
				
				//The current sequence obtained can have many other unnecassary calls. Extract the most useful from that
				constructAndPrintMinimalSequence(llPathStack);
				
				if(traceStringBfr.length() > 0) {
					bwTraceFileDebug.write(traceStringBfr.toString());
					
					if(CommonConstants.bDumpTraces) {
						BufferedWriter dumpTraceBW = new BufferedWriter(new FileWriter(CommonConstants.traceWorkingDir + "/dump_" + CommonConstants.dumpFilenameCounter++));
						dumpTraceBW.write(traceStringBfr.toString());
						dumpTraceBW.close();				
					}				
				}
				
				
				return;
			}
			
			
			DijkstraShortestPath dspObj = new DijkstraShortestPath(methodDeclGraph);
			
			//List path = dspObj.getPath(srcMIObj, destMIObj);
			List path = dspObj.getPath(mihRoot, destMIObj);
			
			int length = 0;
			
			DirectedSparseEdge dseEdge = null;
			for(Iterator iter = path.iterator(); iter.hasNext();)
			{
				dseEdge = (DirectedSparseEdge)iter.next();
				
				MethodInvocationHolder mihObj = (MethodInvocationHolder) dseEdge.getSource();
				
				if(MethodInvocationHolder.isDummyMethodInvocationHolder(mihObj))
				{
					//Nothing to do as this is DUMMY method added for control flow handling
				}
				else
				{	
					assignKeyValueToMethodInvocation(mihObj);
					llPathStack.push(mihObj);
					
					//debugging infor
					traceStringBfr.append("\t" + mihObj.getVisualizationString() + "\n");
					//bwTraceFileDebug.write("\t" + mihObj + "\n");
				}
			}
			
			if(dseEdge != null)
			{
				MethodInvocationHolder mihObj = (MethodInvocationHolder) dseEdge.getDest();
				assignKeyValueToMethodInvocation(mihObj);
				llPathStack.push(mihObj);
				
				traceStringBfr.append("\t" + mihObj.getVisualizationString() + "\n");
				//bwTraceFileDebug.write("\t" + mihObj + "\n");
			}
			
			if(traceStringBfr.length() > 0) {
				bwTraceFileDebug.write(traceStringBfr.toString());
				
				if(CommonConstants.bDumpTraces) {
					BufferedWriter dumpTraceBW = new BufferedWriter(new FileWriter(CommonConstants.traceWorkingDir + "/dump_" + CommonConstants.dumpFilenameCounter++));
					dumpTraceBW.write(traceStringBfr.toString());
					dumpTraceBW.close();				
				}				
			}
			
			//The current sequence obtained can have many other unnecassary calls. Extract the most useful from that
			constructAndPrintMinimalSequence(llPathStack);
			
			
			
			//Collecting information for generating mcseq.map file for BIDE. TODO: Not required any more
			noOfSequences++;
			if(maximumSeqLen < length)
			{
				maximumSeqLen = length;
			}
			
			averageSeqLen += length;
			
			
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName);
			logger.info(ex.getStackTrace().toString());
		}
	}
	
	
	/*
	 * Main function resposible for extracting the minimal sequence in a method list that can take input
	 * and give output
	 */
	private void constructAndPrintMinimalSequence(Stack llPathStack)
	{
		//This set is used to keep track of types found in the path to extract the sequence
		HashSet<String> typeSet = new HashSet<String>();
		HashSet<Integer> keySet = new HashSet<Integer>();
		
		int numOfMethodCalls = 0;
		String fullPath = "", fullSequence = "";
		int fullNumOfMethodCalls = 0;
		
		try
		{
			String actualPath = "", actualSequence = "";
			boolean bSourceFound = false;
			
			//If no source is given, always declare that Source has found
			if(sourceObj.equals(""))
			{
				bSourceFound = true;
			}
			
			if(!llPathStack.isEmpty())
			{
				//First token contains the destination object we are looking for
				MethodInvocationHolder mihObj = (MethodInvocationHolder) llPathStack.pop();
				
				//If the current pattern is not leading to destination object type, ignore it
				if(!mihObj.getReturnType().equals(destObj))
					return;
				
				
				fullPath = mihObj.key + " " + fullPath;
				fullSequence = mihObj + "\n" + fullSequence; 
				fullNumOfMethodCalls++;
				
				actualPath = mihObj.key + " " + actualPath;
				actualSequence = mihObj + "\n" + actualSequence;
				numOfMethodCalls++;
				
				addToTypeSet(mihObj, typeSet);
				keySet.add(new Integer(mihObj.key));
				
				if(typeSet.contains(sourceObj))
				{
					typeSet.remove(sourceObj);
					bSourceFound = true;
					
					if(actualSequence.indexOf(CommonConstants.multipleCurrTypes) != -1)
					{
						actualSequence = actualSequence.replace(CommonConstants.multipleCurrTypes, sourceObj);
					}
					
					
					if(!CommonConstants.bAlternateMethodArgumentSeq)
					{	
						storeSequence(actualPath, actualSequence, numOfMethodCalls, typeSet.size(), keySet, true);
						return;
					}	
				}
			}
			
			
			
			//Gathering the least path from the extracted path from destination to source
			while(!llPathStack.isEmpty())
			{
				MethodInvocationHolder mihObj = (MethodInvocationHolder) llPathStack.pop();
				fullPath = mihObj.key + " " + fullPath;
				fullSequence = mihObj + "\n" + fullSequence; 
				fullNumOfMethodCalls++;
				
				
				//if(mihObj.getReturnType().equals(searchType))
				boolean bTypeSetContains = false;
				if(typeSet.contains(mihObj.getReturnType())) 
				{
					bTypeSetContains = true;
					typeSet.remove(mihObj.getReturnType());
				}
				
				if(typeSet.contains(mihObj.getReferenceClsName()))
				{
					bTypeSetContains = true;
					typeSet.remove(mihObj.getReferenceClsName());
				}	
					
				if(bTypeSetContains)
				{	
					if(!keySet.contains(new Integer(mihObj.key)))
					{	
						
						
						addToTypeSet(mihObj, typeSet);
						keySet.add(new Integer(mihObj.key));
						
						actualPath = mihObj.key + " " + actualPath;
						String mihObjStrForm = mihObj.toString(); 
						
						if(mihObjStrForm.indexOf(CommonConstants.multipleCurrTypes) != -1)
						{
							if(typeSet.contains(sourceObj))
							{
								mihObjStrForm = mihObjStrForm.replace(CommonConstants.multipleCurrTypes, sourceObj);
							}
							else
							{
								mihObjStrForm = mihObjStrForm.replace(CommonConstants.multipleCurrTypes, currentSources.toString());
							}
						}
					
						actualSequence = mihObjStrForm + "\n" + actualSequence;					
						numOfMethodCalls++;
					}	
				}
				
				if(typeSet.contains(sourceObj))
				{
					//Gathered the minimal required path from the path found
					//Before printing check whether such minimal sequence is already generated as output
					bSourceFound = true;
					typeSet.remove(sourceObj);
					if(!CommonConstants.bAlternateMethodArgumentSeq)
					{
						break;
					}
					//if(!isSequenceAlreadyFound(actualSequence))
					//{					
					//	bwOutput.write("FileName: " + currentFileName + " MethodName: " + currentMethodDeclaration.getName().getIdentifier());
					//	bwOutput.write("\t" + actualPath + "\n");
					//	bwOutput.write("\t" + actualSequence + "\n\n");
					//}
					
				}
			}
			
			if(bSourceFound)
			{	
				storeSequence(actualPath, actualSequence, numOfMethodCalls, typeSet.size(), keySet, true);
			}
			else
			{
				//Some problem, Could n't find the exact sequence from source to destination.
				//But there is some sequence between source and destination. In this case output the entire sequence and give low preference
						
				
				//TODO: To remove some parts of the sequence: Currently not treating with low confidence 
				//level patterns
				storeSequence(fullPath, fullSequence, fullNumOfMethodCalls, typeSet.size(), keySet, false);
			}
		}
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
	}
	
	/**
	 * Function that adds reference class and arguments to type set
	 * This is mainly used for checking
	 */
	private void addToTypeSet(MethodInvocationHolder mihObj, HashSet<String> typeSet)
	{
		String searchType = mihObj.getReferenceClsName();
		
		//Here unknown is not added as it is currently handling the case of multiple method invocation
		if(!searchType.equals("CONSTRUCTOR") && !searchType.equals("#LOCAL#"))
			typeSet.add(searchType);
		
		if(searchType.equals(CommonConstants.multipleCurrTypes))
		{	
			for(Iterator iter = currentSources.iterator(); iter.hasNext();)
				typeSet.add((String) iter.next());
		}
		
		String argArr[] =  mihObj.getArgumentArr();
		for(int count = 0; count < argArr.length; count++)
		{
			//exclude the primitive types while adding to type set Hint: In our current types, primitive types won't be
			//having dots. TODO: To check whether this hint works. Also to check whether we need to consider strings
			
			if(argArr[count].equals(CommonConstants.unknownType))
			{
				typeSet.add(argArr[count]);
			}
			else
			{			
				if(argArr[count].equals("java.lang.String") 
						&& (sourceObj.equals("java.lang.String") || destObj.equals("java.lang.String")))
				{
					typeSet.add(argArr[count]);
				}	
				else if(argArr[count].contains(".") && !argArr[count].equals("java.lang.String"))
				{
					typeSet.add(argArr[count]);
				}
			}	
		}
	}
	
	
	
	/**
	 * Checks whether a gathered sequence is already found before
	 * @param sequence
	 * @return
	 */
	public void storeSequence(String path, String sequence, int numOfMethodCalls, int numOfUndefinedVars, HashSet keySet, boolean confidence)
	{
		SequenceMiner.storeSequence(path, sequence, numOfMethodCalls, numOfUndefinedVars, keySet, 
				confidence, sequenceMap, currentFileName, currentMethodDeclaration.getName().getIdentifier());
	}
	
	public void assignKeyValueToMethodInvocation(MethodInvocationHolder mihObj)
	{
		//Storing the objects in to HashMap for ID allocation and trace generation
		Integer prevIdOfMIObj = (Integer) methodInvocationMap.get(mihObj.toString());
		if(prevIdOfMIObj == null)
		{	
			methodInvocationMap.put(mihObj.toString(), new Integer(methodInvocationCounter));
			mihObj.key = methodInvocationCounter;
			methodInvocationCounter++;
		}
		else
		{
			mihObj.key = prevIdOfMIObj.intValue();
		}
	}
	
	
	/********* End of internal traversal methods **************/
	
	
	
	
	
	
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
