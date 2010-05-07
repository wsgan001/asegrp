package pw.code.analyzer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Queue;

import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.ExternalObject;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.BreakStatement;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ContinueStatement;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import pw.code.analyzer.holder.AssignmentCondHolder;
import pw.code.analyzer.holder.BlockEndHolder;
import pw.code.analyzer.holder.CodeSampleDefectHolder;
import pw.code.analyzer.holder.CondVarHolder;
import pw.code.analyzer.holder.CondVarHolder_Typeholder;
import pw.code.analyzer.holder.DoWhileBeginHolder;
import pw.code.analyzer.holder.DoWhileEndConditionHolder;
import pw.code.analyzer.holder.ElseEndHolder;
import pw.code.analyzer.holder.ForBeginConditionHolder;
import pw.code.analyzer.holder.ForEndHolder;
import pw.code.analyzer.holder.Holder;
import pw.code.analyzer.holder.IfConditionHolder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.code.analyzer.holder.PrePostPathHolder;
import pw.code.analyzer.holder.ReturnStatementHolder;
import pw.code.analyzer.holder.ThenEndHolder;
import pw.code.analyzer.holder.WhileBeginConditionHolder;
import pw.code.analyzer.holder.WhileEndHolder;
import pw.common.CommonConstants;
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
    Stack<Holder> loopHolderStack = new Stack<Holder>();
    
    //Variables storing the current holders like current Class, Method that is parsed
    static TypeDeclaration currentClassDeclaration; //TODO: To handle this as this may need stack as class in class is allowed
    static MethodDeclaration currentMethodDeclaration;
    static String currentFileName;
     
    DirectedSparseGraph methodDeclGraph;
    int methodInvocationCounter = 1, noOfSequences = 0, maximumSeqLen = 0, averageSeqLen = 0;
    HashMap methodInvocationMap;
    HashMap sequenceMap;
       
	boolean bStartGraph = false, bSourceObjectFound = false;
	Holder currentHolderForEdge, sourceHolder = null, destHolder = null;
	List<Holder> sourceHolderList = new ArrayList<Holder>();	//Here list is used, as we are interested
																//to get more patterns
	Holder mihRoot; 	//Variable that holds the root of the tree

	String outputFile = "PatternSequence.dat";
	public String sourceObj = "", destObj = "";	//In AnamolyDetector, only sourceObj is used, which is the object of interest gathered from the folder name of code samples
												//destObj is not used.
	
	Boolean bTrue = new Boolean(true);
	Boolean bFalse = new Boolean(false);
	static ASTCrawler currentInstance;
	
	//List of possible sources. The current class and its extends.
	HashSet<String> currentSources = new HashSet<String>(); 
	String currPackageName = "";
	
	boolean bReturnStatementExists = false;	//Sets to true in a block which includes return statements
											//or EXIT statements
	
    /************** Existing Problems with JDT *******************
    1. Obj.method1(param.method2()) : Here JDT initially visits Obj.method1 and then param.method2(). How can this be altered?
    
    ************* End *****************/
 
    //Debugging Information Section
    BufferedWriter brDebug = null;
    int noOfSpaces = 0;
    String dirName, debugFileName;
    
    HashSet<String> currMethodDeclParams = new HashSet<String>();
    HashSet<String> currFieldDeclarations = new HashSet<String>();
    
    HashMap<Integer, MethodInvocationHolder> IdToSamplesMethodHolder = new HashMap<Integer, MethodInvocationHolder>();
	//Given an ID, this Hashmap provides the method-invocation holders that are formed while parsing the code samples and local files in the input application
    
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
    	//this.debugFileName = fileName.substring(0, fileName.indexOf('.'));
    	//this.debugFileName = dirName + "//" + debugFileName + ".debug.full";
    	
    	/*try 
    	{
	    	if(brDebug != null)	{
	    		try	{
	    			brDebug.close();
	    			brDebug = null;
	    		}
	    		catch(Exception ex) {
	    			logger.error(ex);
	    		}
	    	}
	    	brDebug = new BufferedWriter(new FileWriter(debugFileName));    	
    	}
    	catch(IOException ex)
    	{
    		logger.error("Error: Unable to open file for writing DEBUG info" + ex);
    		logger.error(ex.getStackTrace().toString());
    	}*/
    	
    	currentFileName = fileName;
    	//Clear the current class
    	currentSources.clear();
    	currPackageName = "";
    	
    	//Refresh all fields specific for a code sample before starting the new code sample
    	ASTCrawlerUtil.fieldDecls.clear();
    	ASTCrawlerUtil.listOfClassDecl.clear();
    	ASTCrawlerUtil.listOfMethodDecl.clear();
    	ASTCrawlerUtil.unknownIDMapper.clear();
    	CommonConstants.unknownIdGen = 0;
    	loopHolderStack.clear();
    	scopeVarStack.clear();
    	scopeMethodStack.clear();
    	inliningMethodStack.clear();
    	currentSources.clear();
    	currFieldDeclarations.clear();
    }
    
    public void closeDebugFile()
    {
    	/*try	{
    		brDebug.close();
    	} catch(Exception e) {
    		e.printStackTrace();
    	}*/
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
						IfConditionHolder ifCHObj = new IfConditionHolder(ifNode.getExpression());
						methodDeclGraph.addVertex(ifCHObj);
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, ifCHObj));
						currentHolderForEdge = ifCHObj;						
						scopeMethodStack.push(currentHolderForEdge);
												
						//Debugging Code
						/*try
						{
							noOfSpaces = noOfSpaces + 1;
							printSpacestoDebugFile();
							brDebug.write("IF-THEN " + ifCHObj + "\n");
						}
						catch(Exception ex)
						{
							logger.info(ex.getStackTrace().toString());
						}*/
						//End of debugging code
					}
					else if(ifNode.getElseStatement() == astNode)
					{
						//Nothing has to be done as the end of THEN block automatically handles this case
						
						//Debugging Code
						/*try
						{
							noOfSpaces = noOfSpaces + 1;
							printSpacestoDebugFile();
							brDebug.write("IF-ELSE\n");
						}
						catch(IOException ex)
						{
							logger.info(ex.getStackTrace().toString());
						}*/
						//End of debugging code
					}
					bReturnStatementExists = false;
					break;
				case ASTNode.WHILE_STATEMENT:
					Holder wbchObj = new WhileBeginConditionHolder(((WhileStatement)parentNode).getExpression());
					methodDeclGraph.addVertex(wbchObj);
					methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, wbchObj));
					currentHolderForEdge = wbchObj;						
					scopeMethodStack.push(currentHolderForEdge);
					loopHolderStack.push(wbchObj);
					//Debugging Code
					/*try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("WHILE " + wbchObj + "\n");
					} catch(IOException ex)	{
						logger.info(ex.getStackTrace().toString());
					}*/
					//End of debugging code
					bReturnStatementExists = false;
					break;
					
				case ASTNode.FOR_STATEMENT:
					//Handling the for statement block
					ForStatement fsObj = (ForStatement) parentNode;
					//Note: The conditional variables are loaded only when the expression is Infix or Prefix.
					//Rest all will be handled normally (TODO);
					ForBeginConditionHolder fbchObj = new ForBeginConditionHolder(fsObj.getExpression());
					methodDeclGraph.addVertex(fbchObj);
					methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, fbchObj));
					currentHolderForEdge = fbchObj;
					scopeMethodStack.push(currentHolderForEdge);
					loopHolderStack.push(fbchObj);
					//Debugging Code
					/*try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("FOR " + fbchObj + "\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}*/
					//End of debugging code
					bReturnStatementExists = false;
					break;
				case ASTNode.DO_STATEMENT:
					//Handling the do-while statement block: 
					DoWhileBeginHolder dwbhObj = new DoWhileBeginHolder();
					methodDeclGraph.addVertex(dwbhObj);
					methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, dwbhObj));
					currentHolderForEdge = dwbhObj;	
					loopHolderStack.push(dwbhObj);
					//Debugging Code
					/*try {
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("DO WHILE BLOCK\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}*/
					//End of debugging code
					bReturnStatementExists = false;
					break;
				case ASTNode.SWITCH_STATEMENT:
					//TODO: Handling the switch statement
					break;
				case ASTNode.TRY_STATEMENT:
					//Debugging Code
					/*try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("TRY BLOCK\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}*/
					//End of debugging code
					break;
				case ASTNode.CATCH_CLAUSE:
					//Debugging Code
					/*try {
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("CATCH BLOCK\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}*/
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
			logger.info("Exception occurred in file: " + debugFileName + ex);
		}
	}
	
	/**
	 * Function for postprocessing after a block has left
	 */
	public void postVisit(ASTNode astNode)
	{
		try {
			if(astNode instanceof MethodDeclaration)
			{
				//Control flow graph for the entire method is ready. Do
				//depth first traversal for generating patterns
				generatePatterns(currentHolderForEdge);				
				
				currentMethodDeclaration = null;
				methodDeclGraph = null;
				try
				{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(!(sObj instanceof MethodIdentifier))
					{	
						//Restore the full class name of the variable with its earlier class name
						VarAndFullClass vandFObj = (VarAndFullClass) sObj;
						ASTCrawlerUtil.fieldDecls.put(vandFObj.variableName, vandFObj.fullClassName);
						sObj = (StackObject) scopeVarStack.pop();
					}
				}
				catch(Exception ex)
				{
					logger.info("Exception occurred in file: " + debugFileName + ex.getClass());
					logger.info(ex.getStackTrace().toString());
				}
				
				bStartGraph = false;
			}
			else if (astNode instanceof CatchClause)
			{
				
			}
			else if(astNode instanceof Block || astNode instanceof TypeDeclaration)
			{	
				try	{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(! (sObj instanceof BlockIdentifier))
					{	
						//Restore the full class name of the variable with its earlier class name
						VarAndFullClass vandFObj = (VarAndFullClass) sObj;
						ASTCrawlerUtil.fieldDecls.put(vandFObj.variableName, vandFObj.fullClassName);
						sObj = (StackObject) scopeVarStack.pop();
					}
				} catch(Exception ex) {
					logger.info("Exception occurred in file: " + debugFileName + ex.getClass());
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
							//End of then statement
							ThenEndHolder tehObj = new ThenEndHolder((Holder) scopeMethodStack.firstElement());
							methodDeclGraph.addVertex(tehObj);
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, tehObj));
							currentHolderForEdge = tehObj;
							
							//Handling the end of THEN block of IF statement
							if(ifNode.getElseStatement() != null)
							{
								//Else block exists for this if statement
								Holder topStack = (Holder) scopeMethodStack.pop();
								scopeMethodStack.push(currentHolderForEdge);
								currentHolderForEdge = topStack;
							}
							else
							{
								//No else block for the current IF statement. In this case, make a dummy MehtodInvocationHolder
								//for merging the edges in the graph
								handleEndOfBlock();
								//printSpacestoDebugFile();
								//brDebug.write("END-IF-THEN\n");
							}
						}
						else if(ifNode.getElseStatement() == astNode)
						{
							//End of else statement
							ElseEndHolder tehObj = new ElseEndHolder((Holder) scopeMethodStack.firstElement());
							methodDeclGraph.addVertex(tehObj);
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, tehObj));
							currentHolderForEdge = tehObj;
							
							handleEndOfBlock();
							//printSpacestoDebugFile();
							//brDebug.write("END-IF-THEN-ELSE\n");
						}
						else
						{
							//An unexpected case. Should not occur
							logger.error("Unexpected case!!! ERROR> Should not occur");
						}
						noOfSpaces = noOfSpaces - 1;
						break;
					
					case ASTNode.WHILE_STATEMENT:
						WhileEndHolder wehObj = new WhileEndHolder((Holder)loopHolderStack.pop());
						methodDeclGraph.addVertex(wehObj);
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, wehObj));
						currentHolderForEdge = wehObj;
						handleEndOfBlock();
						//printSpacestoDebugFile();
						//brDebug.write("END-WHILE " + wehObj + "\n");
						//noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.FOR_STATEMENT:	
						//Handling the end of For statement block
						
						//Before the end of statement block, add the updaters part, which gets executed 
						//Visiting the updater. As this updater gets executed only after the body
						ForStatement fstmt = (ForStatement) parentNode;
						for(Object updaterObj : fstmt.updaters()) {
							ASTNode astnode = (ASTNode) updaterObj;
							astnode.accept(this);
						}						
						
						ForEndHolder fehObj = new ForEndHolder((ForBeginConditionHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(fehObj);
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, fehObj));
						currentHolderForEdge = fehObj;
						handleEndOfBlock();	
						//printSpacestoDebugFile();
						//brDebug.write("END-FOR " + fehObj + "\n");
						//noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.DO_STATEMENT:
						//Handling the do while statement block. Nothing has to be done as the code
						//inside do-while has to execute atleast once
						DoStatement dsObj = (DoStatement) parentNode;
						DoWhileEndConditionHolder dwechObj = new DoWhileEndConditionHolder(dsObj.getExpression()
											, (DoWhileBeginHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(dwechObj);
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, dwechObj));
						currentHolderForEdge = dwechObj;						
						//printSpacestoDebugFile();
						//brDebug.write("END-DO-WHILE " + dwechObj + "\n");
						//noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.SWITCH_STATEMENT:
						//TODO: Handling the switch statement
						break;
					
					default:
						break;
					}
				}
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName + ex);
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
		}
		catch(Exception ex) {
			logger.info("Exception occurred in file: " + debugFileName + ex);
		}	
		return true;
	}
    
    /**
     * Function for handling Inner classes, interfaces, which are stored as TypeDeclaration object
     */
    public boolean visit(TypeDeclaration typeNode)
    {
    	//APIUsage metrics: Ignore the classes that belong to current package, when not in CommonConstants.DETECT_BUGS_IN_LIBRARY
    	if(RepositoryAnalyzer.getInstance().getLibPackageList().contains(currPackageName) && CommonConstants.OPERATION_MODE != CommonConstants.DETECT_BUGS_IN_LIBRARY)
    	{
    		return false;
    	}
    	
    	//Ignore mining patterns or detecting bugs in test code
    	if(typeNode.getSuperclassType() != null) {
    		Type superClsType = (Type)typeNode.getSuperclassType();
    		String fullNameOfSuperCls = ASTCrawlerUtil.getFullClassName(superClsType);
    		if(fullNameOfSuperCls.equals("junit.framework.TestCase")) {
    			return false;
    		}
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
    			ASTCrawlerUtil.preProcessClassDecl(typeNode, currFieldDeclarations);
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
	    			
	    			if(fullNameOfSuperCls.startsWith(CommonConstants.unknownType)|| fullNameOfSuperCls.equals("#LOCAL#"))
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
		    			
		    			if(fullNameOfSuperInterface.startsWith(CommonConstants.unknownType) || fullNameOfSuperInterface.equals("#LOCAL#"))
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
	
	/**
	 * Function for visiting method declarations
	 */
	public boolean visit(MethodDeclaration md)
	{
		//If this is a test method, ignore further processing
		if(md.getName().toString().startsWith("test"))
			return false;
			
		inliningMethodStack.clear();
		destHolder = null;
		sourceHolder = null;
		sourceHolderList.clear();
		IdToSamplesMethodHolder.clear();
		visitedTraces.clear();
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
			mihRoot = Holder.getDummyHolder();
			methodDeclGraph.addVertex(mihRoot);
			currentHolderForEdge = mihRoot;
			currMethodDeclParams.clear();
		}	
		
		//Add all method arguments to field Declaration map
		List parameters = md.parameters();		
		for(Iterator iter = parameters.iterator(); iter.hasNext();)
		{			
			SingleVariableDeclaration svdObj = (SingleVariableDeclaration) iter.next();
			addSingleVariableDeclaration(svdObj);			
			currMethodDeclParams.add(svdObj.getName().getIdentifier());	
		}
		//End of adding arguments
		
		/*if(!bInlining) {	
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
		}*/		
		return true;
	}
	
	
	/**
	 * Function to handle the creation of MethodInvocationHolder object
	 */
	public boolean handleMethodInvocationHolder(MethodInvocationHolder mihObj, TypeHolder refType, TypeHolder returnType)
	{		
		IdToSamplesMethodHolder.put(new Integer(mihObj.getKey()), mihObj);		
			
		try{
			methodDeclGraph.addVertex(mihObj);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihObj));
		} catch(edu.uci.ics.jung.exceptions.ConstraintViolationException ex) {
			throw ex;
		}
		currentHolderForEdge = mihObj;
		
    	if(refType.getType().equals(sourceObj) || returnType.getType().equals(sourceObj) || mihObj.isPresentInArgumentArr(sourceObj)
				|| (refType.getType().equals(CommonConstants.multipleCurrTypes) && currentSources.contains(sourceObj)) 
				|| CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) 
		{
			//Get the external object associated with this source and see whether
			//such API is of interest for the current library.
			ExternalObject eeObj = null;
			MethodInvocationHolder equiLibMIH = null;
			
			if(CommonConstants.OPERATION_MODE != CommonConstants.DETECT_BUGS_IN_LIBRARY) {
				eeObj = RepositoryAnalyzer.getInstance().getExternalObjects().get(sourceObj);
			} else {
				HashMap<String, ExternalObject> externalMap = RepositoryAnalyzer.getInstance().getExternalObjects();
				eeObj = externalMap.get(refType.getType());
			}
			
			if(eeObj != null)
			{	
				if(CommonConstants.ALL_METHODS_OF_CLASS || (equiLibMIH = eeObj.containsMI(mihObj)) != null) {
					bSourceObjectFound = true;
					sourceHolderList.add(mihObj);
					mihObj.setAssociatedLibMIH(equiLibMIH);
				}
				
				//Create an associated library during runtime if one does not exist
				if(CommonConstants.ALL_METHODS_OF_CLASS) {
					if(eeObj.containsMI(mihObj) == null) {
						mihObj.initializeCondVarMaps();	
						eeObj.getMiList().add(mihObj);
					}
					mihObj.setAssociatedLibMIH(mihObj);
				}				
			}	
		}
    	
    	//handling System.exit in a special manner as this can be an equivalent of existence of return statement
    	if(mihObj.getMethodName().equals("exit") && mihObj.getReceiverClass().getType().equals("System")) {
    		bReturnStatementExists = true;
    	}
		
		//Debugging section
		/*try {
			printSpacestoDebugFile();
			brDebug.write("\t " + mihObj + "\n");
		} catch(IOException ex) {
			logger.info(ex.getStackTrace().toString());
		}*/
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
		miObj.setProperty("visited", bTrue);
		
		try	{
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
			
			TypeHolder refType = ASTCrawlerUtil.getRefClassName(miObj.getExpression());
			TypeHolder returnType = ASTCrawlerUtil.getReturnType(miObj);
			ASTCrawlerUtil.unknownIDMapper.put(miObj, returnType);
			
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
					{	
						bodyNode.accept(this);
					}	
				
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
					refType.setType(CommonConstants.multipleCurrTypes);
				}
			}
						
			//TODO: Currently the method parameters are not considered for time being
			//Only tracing is done with return types and reference class names
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, returnType, miObj);
			if(CommonConstants.ALL_METHODS_OF_CLASS) {
				mihObj.initializeCondVarMaps();
			}
			if(miObj.getParent() instanceof CastExpression)
			{
				mihObj.bDowncast = true;
			}
			return handleMethodInvocationHolder(mihObj, refType, returnType);
		}
		catch(Exception ex) {
			logger.error("Happened in file: " + debugFileName + " " + ex.getClass());
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

		try {
			if(!bStartGraph)
				return false;
			
			//Visit the arguments first
			handleArgumentMethodInvocations(cicObj.arguments());
			
			TypeHolder thObj = ASTCrawlerUtil.getReturnType(cicObj); 
			if(thObj.getType().startsWith(CommonConstants.unknownType))
			{
				thObj = new TypeHolder(ASTCrawlerUtil.getFullClassName(cicObj.getType()));
			}
			
			ASTCrawlerUtil.unknownIDMapper.put(cicObj, thObj);
						
			MethodInvocationHolder mihObj = new MethodInvocationHolder(thObj, thObj, cicObj);
			return handleMethodInvocationHolder(mihObj, thObj, thObj);
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
			
			TypeHolder refType = ASTCrawlerUtil.getRefClassName(ceObj.getExpression());	
			TypeHolder returnType = ASTCrawlerUtil.getRefClassName(ceObj);
			ASTCrawlerUtil.unknownIDMapper.put(ceObj, returnType);
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

	}
	
	public boolean visit(Assignment assignStmt) 
	{
		assignStmt.getRightHandSide().accept(this);
		
		//Sometimes assignemnt statements are including conditional
		//checks in the form : "success = !dir.isFile() && dir.mkdirs();"
		List<CondVarHolder> condVarList = new ArrayList<CondVarHolder>();
		
		ASTCrawlerUtil.loadConditionalVariablesFromAssign(assignStmt.getRightHandSide(), condVarList);
		if(condVarList.size() != 0) {
			//Add a holder node with the conditional variables
			AssignmentCondHolder assignHolder = new AssignmentCondHolder(condVarList);
			
			try{			
				methodDeclGraph.addVertex(assignHolder);
				methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, assignHolder));				
			} catch(Exception ex) {
				logger.error("Exception occurred while handling assignment Stmt " + assignStmt);
			}
			currentHolderForEdge = assignHolder;			
		}
		return true;
	}
	
	//Break statements can serve as return paths from loops
	public boolean visit(BreakStatement brkStmt) {
		bReturnStatementExists = true;
		return super.visit(brkStmt);
	}
	
	//Continue statements also serve as return paths
	public boolean visit(ContinueStatement contStmt) {
		bReturnStatementExists = true;
		return super.visit(contStmt);
	}
	
	public boolean visit(ThrowStatement thObj) {
		bReturnStatementExists = true;
		return super.visit(thObj);
	}
		
	public boolean visit(ReturnStatement rsObj) 
	{
		Expression expr = rsObj.getExpression();
		
		if(expr != null) {
			expr.accept(this);
			//visit((MethodInvocation) expr);
			expr.setProperty("visited", bTrue);
		}
		
		ReturnStatementHolder rsh = new ReturnStatementHolder();
		rsh.setAssociatedExpression(expr);
		try{			
			methodDeclGraph.addVertex(rsh);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, rsh));			
		} catch(Exception ex) {
			logger.error("Exception occurred while handling Return Statement " + ex);
		}
		currentHolderForEdge = rsh;
		
		bReturnStatementExists = true;
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
			logger.info("Exception occurred in file: " + debugFileName + ex);
			logger.info(ex.getStackTrace().toString());
		}
		return true;
	}
	
	/**
	 * Function for visiting FOR statement. This new function iterates the for loop in the similar fashion 
	 * of how FOR loop is executed. The reason for the new function is that the current available function does
	 * not visit in the desired order
	 */
	public boolean visit(ForStatement fstmt) 
	{
		//Visiting all initializers
		for(Object node : fstmt.initializers()) {
			ASTNode astnode = (ASTNode) node;
			astnode.accept(this);
		}		
		
		//Visiting the conditionals
		if(fstmt.getExpression() != null)
			fstmt.getExpression().accept(this);
		
		//Visiting the body
		if(fstmt.getBody() != null)
			fstmt.getBody().accept(this);
		
		return false;
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
		Holder topStack = (Holder) scopeMethodStack.pop();
		BlockEndHolder mihDummy = new BlockEndHolder();
		
		if(this.bReturnStatementExists) {
			mihDummy.bHasReturnStatement = true;
			this.bReturnStatementExists = false;
		}
		
		try {
			methodDeclGraph.addVertex(mihDummy);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihDummy));
			
			if(currentHolderForEdge instanceof IfConditionHolder || currentHolderForEdge instanceof ForBeginConditionHolder
					|| currentHolderForEdge instanceof WhileBeginConditionHolder) {
				mihDummy.setLoopStart(currentHolderForEdge);
			}			
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		//If no nodes are added to the graph by the then block or else block, no need to add the second aspect
		if(topStack != currentHolderForEdge) {
			methodDeclGraph.addEdge(new DirectedSparseEdge(topStack, mihDummy));
			if(topStack instanceof IfConditionHolder || topStack instanceof ForBeginConditionHolder
					|| topStack instanceof WhileBeginConditionHolder) {
				mihDummy.setLoopStart(topStack);
			}
		}	
		
		currentHolderForEdge = mihDummy;
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
	
    /*************** SPECIFIC CODE OF ALATTIN2 for mining object usage models *****************/
       
    HashSet<String> visitedTraces = new HashSet<String>(); 
    
	/**
	 * Main function that traverses the DAG to identify the sequences of patterns 
	 * This function minimizes the current graph by removing the 
	 * redundant nodes.
	 */
	public void generatePatterns(Holder destMIObj)
	{
		String IDTraceStr = "";
		this.depthFirstTraversal(this.mihRoot, IDTraceStr);
		
		if(this.visitedTraces.size() == 0)
			return;
		
		RepositoryAnalyzer raobj = RepositoryAnalyzer.getInstance();
		
		try
		{
			raobj.bwAssocMiner.write("*******************\n");			
			raobj.bwAssocMiner.write("Filename: " + currentFileName + ", Methodname: " + currentMethodDeclaration.getName().toString() + "\n");			
			for(String trace : this.visitedTraces)
			{			
				String idArr[] = trace.split(" ");			
				for(int tCnt = 1; tCnt < idArr.length; tCnt++) {
					MethodInvocationHolder mihobj = IdToSamplesMethodHolder.get(new Integer(idArr[tCnt]));			
					raobj.bwAssocMiner.write("\t" + mihobj.toString() + "(" + mihobj.getKey() + ")\n");											
				}
				raobj.bwAssocMiner.write("\n");				
			}
			raobj.bwAssocMiner.write("*******************\n");
		}
		catch(IOException ex)
		{
			logger.error("IOException occurred while writing contents ");
		}
	}
	
	private void depthFirstTraversal(Holder srcObj, String IDTraceStr) {			
		
		if(srcObj instanceof MethodInvocationHolder) {
			IDTraceStr += ((MethodInvocationHolder)srcObj).getKey() + " ";			
		}
		
		boolean bHasOutGoingEdges = false;
		for(Iterator iter = srcObj.getOutEdges().iterator(); iter.hasNext();) {
			bHasOutGoingEdges = true;
			DirectedSparseEdge dseObj = (DirectedSparseEdge) iter.next();
			depthFirstTraversal((Holder)dseObj.getDest(), IDTraceStr);						
		}
		
		//We are interested only in longest sequences starting from method begining to the method end
		if(bHasOutGoingEdges)
			return;
		
		visitedTraces.add(IDTraceStr);
	}
	
	/******** Begin of Misc functions for debugging ************/
	public void printGraphEdges()
	{
		try	{
			Set allEdges = methodDeclGraph.getEdges();
			//brDebug.write("\nGraph of the method:\n");
			for(Iterator iter = allEdges.iterator(); iter.hasNext();)
			{
				@SuppressWarnings("unused")
				DirectedSparseEdge dseEdge = (DirectedSparseEdge) iter.next();
				//brDebug.write(dseEdge.getSource().toString() + " --> " + dseEdge.getDest().toString() + "\n");
			}
		} catch(Exception ex)	{
			logger.info("Exception " + ex);
		}		
	}
	
	public void printSpacestoDebugFile()
	{
		/*try	{
			for(int i = 0; i < noOfSpaces; i ++)
			{
				brDebug.write("\t");
			}
		} catch(IOException ex)	{
			logger.info(ex.getStackTrace().toString());
		}*/
	}	
	/******** End of Misc functions for debugging **************/
	
}
