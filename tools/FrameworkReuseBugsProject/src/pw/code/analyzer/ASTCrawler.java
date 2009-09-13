package pw.code.analyzer;

import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.ExternalObject;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.AnonymousClassDeclaration;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CatchClause;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
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
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import pw.code.analyzer.holder.AssignmentCondHolder;
import pw.code.analyzer.holder.CodeSampleDefectHolder;
import pw.code.analyzer.holder.CondVarHolder;
import pw.code.analyzer.holder.ConditionVarHolderSet;
import pw.code.analyzer.holder.DoWhileBeginHolder;
import pw.code.analyzer.holder.DoWhileEndConditionHolder;
import pw.code.analyzer.holder.ElseEndHolder;
import pw.code.analyzer.holder.ForBeginConditionHolder;
import pw.code.analyzer.holder.ForEndHolder;
import pw.code.analyzer.holder.Holder;
import pw.code.analyzer.holder.IfConditionHolder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.code.analyzer.holder.ReturnStatementHolder;
import pw.code.analyzer.holder.ThenEndHolder;
import pw.code.analyzer.holder.WhileBeginConditionHolder;
import pw.code.analyzer.holder.WhileEndHolder;
import pw.common.CommonConstants;
import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;
import framework.reuse.LibMethodReuse;

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
	List<Holder> returnObjectHolderList = new ArrayList<Holder>();	//Here list is used, as we are interested
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
	
	boolean bPossibleFalsePositive = false; //An important Heuristic for avoiding more false positives on the return variable of a method invocation
	
	HashMap<String, LibMethodReuse> currentReusedMethods = new HashMap<String, LibMethodReuse>();
	LibMethodReuse currentLibMethodReuse;
	String transactionString = "";
	
    /************** Existing Problems with JDT *******************
    1. Obj.method1(param.method2()) : Here JDT initially visits Obj.method1 and then param.method2(). How can this be altered?
    
    ************* End *****************/
 
    //Debugging Information Section
    BufferedWriter brDebug = null;
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
    	currentReusedMethods.clear();
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
				//If this method declaration is of no interest return with out any action
				if(currentLibMethodReuse == null) {
					return;
				}
				
				//If the transaction string is not empty, store it in the list of transactions
				if(!transactionString.equals("")) {
					currentLibMethodReuse.addToMihTransactions(transactionString);					
				}				
				
				//Capture patterns for arguments and return types
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
						//Handling the While and For statement block
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
	    			
	    			if(fullNameOfSuperCls.startsWith(CommonConstants.unknownType)|| fullNameOfSuperCls.equals("#LOCAL#"))
	    			{
	    				if(superClsType instanceof SimpleType)
	    					fullNameOfSuperCls = currPackageName + "." + ((SimpleType)superClsType).getName().getFullyQualifiedName();
	    			}
    				currentSources.add(fullNameOfSuperCls);
    				RepositoryAnalyzer.getInstance().handleClassExtension(fullNameOfSuperCls, typeNode, false, currentReusedMethods, currentFileName, "", "");
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
		    			RepositoryAnalyzer.getInstance().handleClassExtension(fullNameOfSuperInterface, typeNode, true, currentReusedMethods, currentFileName, "", "");
	    			}
	    		}
    		}
    		catch(Exception ex)
    		{
    			logger.error("Error occurred while adding possible sources from current class " + ex);
    			ex.printStackTrace();
    			
    		}
    		if(currentReusedMethods.size() == 0)
    			return false;
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
		String key = md.getName().getIdentifier() + "#(";
		boolean hasParameters = false;
		for(Iterator iter = md.parameters().iterator(); iter.hasNext();)
		{
			hasParameters = true;
			SingleVariableDeclaration expr = (SingleVariableDeclaration) iter.next();
			String refClsName = "";
			refClsName = ASTCrawlerUtil.getFullClassName(expr.getType());
			key = key + refClsName + ",";
		}
		if(hasParameters)
			key = key.substring(0, key.length()-1);
		
		key = key + ")";
		
		currentLibMethodReuse = currentReusedMethods.get(key);
		if(currentLibMethodReuse == null)
			return false;
		
		transactionString = "";
		
		inliningMethodStack.clear();
		destHolder = null;
		sourceHolder = null;
		returnObjectHolderList.clear();
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
		}	
		
		//Add all method arguments to field Declaration map
		List parameters = md.parameters();
		for(Iterator iter = parameters.iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration svdObj = (SingleVariableDeclaration) iter.next();
			addSingleVariableDeclaration(svdObj);
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
		try{
			methodDeclGraph.addVertex(mihObj);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihObj));
		} catch(edu.uci.ics.jung.exceptions.ConstraintViolationException ex) {
			throw ex;
		}
		currentHolderForEdge = mihObj;	
		
		//Create an ID for this and add it to the transaction list later. 
		String mihKey = refType.getType() + "#" + mihObj.getMethodName() + "#" + mihObj.getNoOfArguments();
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		Integer existingID = raObj.getReusedMethodInvocationIdMapper().get(mihKey);
		if(existingID == null) {
			existingID = new Integer(RepositoryAnalyzer.reusedMICounter++);
			raObj.getReusedMethodInvocationIdMapper().put(mihKey, existingID);
		}
		
		transactionString += existingID.intValue() + " ";
		
		
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
			//ex.printStackTrace();
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
		return true;

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
	
	/**
	 * Function for handling return statement.
	 * Along with adding RETURN to the cluster, also capture the object.
	 */
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
		
		//Add the return object to the list
		rsh.setReturnVarName(expr);
		returnObjectHolderList.add(rsh);
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
		Holder mihDummy = Holder.getDummyHolder();
		
		try {
			methodDeclGraph.addVertex(mihDummy);
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihDummy));
		} catch(Exception ex) {
			ex.printStackTrace();
		}

		//If no nodes are added to the graph by the then block or else block, no need to add the second aspect
		if(topStack != currentHolderForEdge)
			methodDeclGraph.addEdge(new DirectedSparseEdge(topStack, mihDummy));
		
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
	
    /*************** SPECIFIC CODE OF FRAMEWORK REUSE DEFECTS *****************/
    
	/**
	 * Main function that traverses the DAG to identify the patterns
	 * related to method-condition and condition-method checks for each source 
	 * node identified in the DAG.
	 * In Anamoly detector, the destination object is simply the end node
	 * of the current method declaration. The paths are computed till
	 * the last node of the method declaration.
	 * 
	 * This function minimizes the current graph by removing the 
	 * redundant nodes.
	 */
	public void generatePatterns(Holder destMIObj)
	{
		destHolder = destMIObj; //The end node of the constructed DAG.
		try {
			generatePatternsOnArguments();
			generatePatternsOnReturnObjects();			
		} catch(Exception ex) {
			logger.error("Exception occurred" + ex);
		}
	}
	
	public void generatePatternsOnArguments() {	
		bPossibleFalsePositive = false;		
		int argPosition = 0;
		for(Iterator iter = currentMethodDeclaration.parameters().iterator(); iter.hasNext();)
		{
			clearCommonVarSet();
			SingleVariableDeclaration expr = (SingleVariableDeclaration) iter.next();
			TypeHolder argTypObj = new TypeHolder(ASTCrawlerUtil.getFullClassName(expr.getType()));
			argTypObj.setVar(expr.getName().getIdentifier());
			gatherConditionsByForwardTraversal(mihRoot, argTypObj, false);
			handleCollectedPatterns(CommonConstants.ARGUMENT_PATTERNS, argPosition++);
		}				
		bPossibleFalsePositive = false;		
	}
	
	/**
	 * Function for handling patterns collected from the code samples. This is a main
	 * function that performs actions based on the mode of the operation.
	 * All patterns are directly stored in the associated library method reuse function
	 * 1. If the mode is MINE_PATTERNS, this collects patterns and stores in the corresponding "ExternalObjects"
	 * 2. If the mode is DETECT_BUGS_IN_CODESAMPLES, this checks whether the newer patterns collected
	 * 		from code samples satisfies the mined patterns. If not, violation is flagged.
	 * 3. If the mode is DETECT_BUGS_IN_LIBRARY, this checks whether the newer patterns satisfy
	 * 		the mined patterns
	 */
	public void handleCollectedPatterns(int patternType, int optionalArgPosition) 
	{
		switch(CommonConstants.OPERATION_MODE)
		{
		case CommonConstants.MINE_PATTERNS:
			storePatterns(patternType, optionalArgPosition);
			break;
		case CommonConstants.DETECT_BUGS_IN_CODESAMPLES:
		case CommonConstants.DETECT_BUGS_IN_LIBRARY:	
			//checkInconsistencies(null, patternType, optionalArgPosition);
			break;
		default:
			break;		
		}	
	}
	
	/**
	 * Function for storing patterns, invoked during MINE_PATTERNS phase
	 * The collected patterns are stored in the LibMethodReuse function. We plan to
	 * use association rule mining instead of simple statistical based analysis
	 */
	public void storePatterns(int patternType, int optionalArgPosition)
	{
		for(CondVarHolder cvhObj : cvhList) {			
			//Nullify the variable names while adding the conditional
			//expressions to the final set of the corresponding method invocation
			CondVarHolder cloneCVH = cvhObj.clone(); 
			cloneCVH.setVarName("");
			switch(patternType) {
				case CommonConstants.ARGUMENT_PATTERNS:
					currentLibMethodReuse.addToArgumentCondVarMaps(cvhObj, optionalArgPosition);
					break;
				case CommonConstants.RETURN_PATTERNS:
					currentLibMethodReuse.addToReturnCondVarMaps(cvhObj);				
					break;
				default: break;			
			}			
		}
		
		//Increment the scenario where there are no conditions associated with the given API
		if(cvhList.size() == 0) {
			switch(patternType) {
			case CommonConstants.ARGUMENT_PATTERNS:
				currentLibMethodReuse.incrNumNoneArgumentCondVars(optionalArgPosition);
				break;
			case CommonConstants.RETURN_PATTERNS:
				currentLibMethodReuse.incrNumNoneReturnCondVar();
				break;
			default: break;			
			}		
		}		
	}
	
	/**
	 * Function for checking inconsistencies. It gathers the already mined patterns
	 * and checks whether the newly collected patterns contain those mined patterns to find
	 * violations
	 */
	public void checkInconsistencies(MethodInvocationHolder assocLibMIH, int patternType, int optionalArgPosition)
	{
		try
		{
			ConditionVarHolderSet cvhSet = null;
			switch(patternType) {
			case CommonConstants.RECEIVER_PATTERNS:
				cvhSet = assocLibMIH.getReceiverCondVar();
				break;	
			case CommonConstants.ARGUMENT_PATTERNS:
				cvhSet = assocLibMIH.getArgumentCondVars(optionalArgPosition);
				break;
			case CommonConstants.PRECEDING_METHOD_CALL_PATTERS:
				cvhSet = assocLibMIH.getPreceedingCondVar();
				break;
			case CommonConstants.RETURN_PATTERNS:
				cvhSet = assocLibMIH.getReturnCondVar();
				break;
			case CommonConstants.SUCCEEDING_METHOD_CALL_PATTERNS:
				cvhSet = assocLibMIH.getSuceedingCondVar();
				break;
			case CommonConstants.SUCCEEDING_RECEIVER_PATTERNS:
				cvhSet = assocLibMIH.getSuceedingReceiverCondVar();
				break;
			default: break;			
			}
			
			HashMap<String, CondVarHolder> minedPatterns = cvhSet.getSelectedCondVar();
			//Heuristic 4 case
			if(cvhSet.getFilteredType() == CommonConstants.NO_CONFIDENCE || minedPatterns.size() == 0) 
				return;
						
			//Check whether the newly collected patterns (stored in cvhList) contain these
			//mined patterns
			if(cvhSet.getFilteredType() == CommonConstants.HIGH_CONFIDENCE) {
				//Every condition has to be satisfied. Any violation is bug
				for(CondVarHolder minedCVH : minedPatterns.values()) {
					boolean bMinedPatternFound = false;
					for(CondVarHolder cvhObj : cvhList) {
						//Nullify the variable names while adding the conditional
						//expressions to the final set of the corresponding method invocation
						CondVarHolder cloneCVH = cvhObj.clone(); 
						cloneCVH.setVarName("");
					
						if(minedCVH.equals(cloneCVH)) {
							bMinedPatternFound = true;
							break;
						}			
					}
					
					if(!bMinedPatternFound) {
						//Heuristic 1: High_confidence violation	
						String tFileName = "";
						if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_CODESAMPLES) {
							tFileName = sourceObj.replace(".", "_") + currentFileName;
						} else if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) {
							tFileName = currPackageName + "." + currentFileName;
						}
						CodeSampleDefectHolder csdfObj = new CodeSampleDefectHolder(tFileName, currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : ""
							,assocLibMIH.formattedPrintString(), patternType, minedCVH.toString());
						
						csdfObj.setFavorableNumber(minedCVH.getFrequency());
						csdfObj.setTotalRelatedNumber(minedCVH.getTotalRelatedFrequency());
						csdfObj.setSupportOfViolation(minedCVH.getSupport());
						
						csdfObj.setConfidenceLevel(cvhSet.getFilteredType());
						RepositoryAnalyzer.getInstance().getCodeSampleDefects().add(csdfObj);
						if(bPossibleFalsePositive) {
							csdfObj.setPossibleFalsePositive(true);
						}
					}
				}	
			} else if(cvhSet.getFilteredType() == CommonConstants.AVERAGE_CONFIDENCE || cvhSet.getFilteredType() == CommonConstants.LOW_CONFIDENCE){
				//In this case, it would be enough, if the new list contains atleast one of the observed patterns
				boolean bAtleastOneFound = false;
				for(CondVarHolder cvhObj : cvhList) {
					CondVarHolder cloneCVH = cvhObj.clone(); 
					cloneCVH.setVarName("");
					boolean bMatched = false;
					for(CondVarHolder minedCVH : minedPatterns.values()) {
						if(cloneCVH.equals(minedCVH)) {
							bMatched = true;
							break;
						}
					}
					
					if(bMatched) {
						bAtleastOneFound = true;
						break;
					}				
				}
								
				CondVarHolder highSupportCond = cvhSet.getHighestSupportCondition();
				if(!bAtleastOneFound && minedPatterns.size() > 0) {
					//Heuristic 2 and 3: Average_confidence and Low_confidence violations	
					String tFileName = "";
					if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_CODESAMPLES) {
						tFileName = sourceObj.replace(".", "_") + currentFileName;
					} else if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) {
						tFileName = currPackageName + "." + currentFileName;
					}
					CodeSampleDefectHolder csdfObj = new CodeSampleDefectHolder(tFileName, currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : ""
						,assocLibMIH.formattedPrintString(), patternType, minedPatterns.values().toString());
					csdfObj.setFavorableNumber(highSupportCond.getFrequency());
					csdfObj.setTotalRelatedNumber(highSupportCond.getTotalRelatedFrequency());	
					csdfObj.setSupportOfViolation(highSupportCond.getSupport());
					
					//Heuristic 2.1 and 3.1: Giving higher preference to those locations that have no
					//condition at all.
					double bugConfidenceAdjustmentFactor = 0.1;
					csdfObj.setConfidenceLevel(cvhSet.getFilteredType() - (cvhList.size() == 0? bugConfidenceAdjustmentFactor : 0));
					RepositoryAnalyzer.getInstance().getCodeSampleDefects().add(csdfObj);	
					
					//Additional heuristic for preventing more false positives	
					if(bPossibleFalsePositive) {
						csdfObj.setPossibleFalsePositive(true);
					}		
				}			
			} else {
				logger.error("ERROR : Unhandled type of confidence level");
			}
		}
		catch(Exception ex) 
		{
			logger.error("Exception occurred " + ex);
		}
	}
	
	//Common Variable declarations used by all methods
	HashSet<Holder> visitedVertices = new HashSet<Holder>();
	HashSet<CondVarHolder> cvhList = new HashSet<CondVarHolder>();
	HashSet<CondVarHolder> lookUpVarSet = new HashSet<CondVarHolder>();
	private void clearCommonVarSet() {
		visitedVertices.clear();
		cvhList.clear();
		lookUpVarSet.clear();
		bPossibleFalsePositive = false;
	}
	
	
	/**
	 * Function for identifying the possible false positives while gathering receiver or argument conditions.
	 * If such receiver or argument patterns are method arguments, they can be false positives as the 
	 * caller might have already performed checks on them
	 */
	private void checkForFalsePositive(TypeHolder thObj) {
		if(currentMethodDeclaration == null) {
			return;
		}
		
		for(Iterator iter = currentMethodDeclaration.parameters().iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration expr = (SingleVariableDeclaration) iter.next();
			String variableName = expr.getName().toString();
			
			if(variableName.equals(thObj.var)) {
				bPossibleFalsePositive = true;
				break;
			}	
		}
	}
	
	/**
	 * Handles two kinds of patterns:
	 * 1. Check conditions on the each return object through backward traversal
	 * 2. Check whether receiver variable is a part of another conditional check like
	 *        if(X.Y(receiverVar) != null) receiverVar.method();
	 * @param srcMINode
	 */
	public void generatePatternsOnReturnObjects()
	{
		try {			
			for(Iterator retIter = returnObjectHolderList.iterator(); retIter.hasNext();) {
				ReturnStatementHolder rshObh = (ReturnStatementHolder) retIter.next();
				if(rshObh.getVarName().equals("")) {
					continue;
				}
				clearCommonVarSet();
				TypeHolder thObj = new TypeHolder();
				thObj.setVar(rshObh.getVarName());
				gatherConditionsByBackTraversal(rshObh, thObj, false);
				handleCollectedPatterns(CommonConstants.RETURN_PATTERNS, -1);
			}			
		} catch (Exception ex) {
			logger.error("Exception occurred " + ex);
		}
	}
	
	/**
	 * Function that identifies a conditional node that contains the given
	 * variable by traversing
	 * back from the given position in the DAG.
	 * 
	 * The flag "bOnlyMIs" if true, collects only nodes that are associated with method invocations
	 * 
	 */
	public void gatherConditionsByBackTraversal(Holder srcMINode, TypeHolder thObj, boolean bOnlyMIs) 
	{
		if(visitedVertices.contains(srcMINode) || (srcMINode == mihRoot)) {
			return;
		}
		
		//To identify exact dominators, always avoid entering IF/ELSE, FOR, WHILE blocks
		if(srcMINode instanceof ThenEndHolder || srcMINode instanceof ElseEndHolder || srcMINode instanceof ForEndHolder
				|| srcMINode instanceof WhileEndHolder || srcMINode instanceof DoWhileEndConditionHolder) {
			srcMINode = srcMINode.getLoopStart();
		}	
		
		visitedVertices.add(srcMINode);
		Set inEdgeSet = srcMINode.getInEdges();
		for(Iterator iter = inEdgeSet.iterator(); iter.hasNext();) {
			Holder srcVertex = (Holder)((DirectedSparseEdge)iter.next()).getSource();
			if(!(srcVertex instanceof MethodInvocationHolder)) {		
				List<CondVarHolder> listToBeAdded = null;
				if(!bOnlyMIs)
					listToBeAdded = srcVertex.contains(thObj);
				else 
					listToBeAdded = srcVertex.containsMIs(thObj, CommonConstants.BACKWARD_TRAVERSAL);
				
				cvhList.addAll(listToBeAdded);
				//Add those conditions that are already not added to cvhList
				for(CondVarHolder lookUpCVH : srcVertex.getCondVarList()) {
					if(!listToBeAdded.contains(lookUpCVH))
						lookUpVarSet.add(lookUpCVH);
				}
				
			} else {
				MethodInvocationHolder mihCasted = (MethodInvocationHolder) srcVertex;
				
				//Stop verifying backward when an assignment is encountered for the
				//variable we are interested in. Because the prior assignment conditions
				//may not affect anything to the variable.
				if(thObj.var.equals(mihCasted.getReturnType().var))
					continue;
				
				if(!mihCasted.getMethodName().equals("CONSTRUCTOR")) {
					Iterator iterLookup = lookUpVarSet.iterator();
					while(iterLookup.hasNext()) {
						CondVarHolder cvhForLookup = (CondVarHolder) iterLookup.next();
						if(cvhForLookup.getVarName().equals(mihCasted.getReturnType().var))
						{
							if(bOnlyMIs) {
								//Check receiver type of this method invocation
								//Extended this function for identifying preceding conditions on other kinds
								//of variables belonging to list of external obj
								if(mihCasted.getReceiverClass().var.equals(thObj.var)/* || 
										RepositoryAnalyzer.getInstance().getExternalObjects().get(mihCasted.getReceiverClass().getType()) != null*/)
								{
									//This is similar to the case where an earlier method invocation
									//is called on the same receiver variable and is involved in the conditional
									//expression 
									CondVarHolder mcvh = new CondVarHolder();
					        		mcvh.setVarName(thObj.var);
					        		mcvh.setCondType(cvhForLookup.getCondType());
					        		mcvh.setAssociatedMIH(mihCasted);
					        		mcvh.setCodeSampleName(cvhForLookup.getCodeSampleName());
					        		mcvh.setCodeSampleMethodName(cvhForLookup.getCodeSampleMethodName());
					        		cvhList.add(mcvh);
								}
							}	
							else {	
								//Check the arguments of this method invocation
								for(TypeHolder argument : mihCasted.getArgumentArr()) {
									if(argument.var.equals(thObj.var)) {
										//Another case of pattern check.
										CondVarHolder mcvh = new CondVarHolder();
						        		mcvh.setVarName(thObj.var);
						        		mcvh.setCondType(cvhForLookup.getCondType() + CondVarHolder.MOVE_TO_METHODARG);
						        		mcvh.setAssociatedMIH(mihCasted);
						        		mcvh.setCodeSampleName(cvhForLookup.getCodeSampleName());
						        		mcvh.setCodeSampleMethodName(cvhForLookup.getCodeSampleMethodName());
						        		cvhList.add(mcvh);		
									}							
								}
							}
							break;
						}				
					}
				}	
			}			
			gatherConditionsByBackTraversal(srcVertex, thObj, bOnlyMIs);
		}
	}
	
	/**
	 * Function that traverses forward and identifies relavant conditional expressions
	 * 
	 * @param srcMINode
	 * @param thObj
	 * @param cvhList
	 * @param visitedVertices
	 * @param bOnlyMIs
	 */
	public void gatherConditionsByForwardTraversal(Holder srcMINode, TypeHolder thObj, boolean bOnlyMIs) 
	{
		if(visitedVertices.contains(srcMINode) || (srcMINode == destHolder)) {
			return;
		}
		
		visitedVertices.add(srcMINode);
		Set outEdgeSet = srcMINode.getOutEdges();
		for(Iterator iter = outEdgeSet.iterator(); iter.hasNext();) {
			Holder destVertex = (Holder)((DirectedSparseEdge)iter.next()).getDest();
			
			//If there is a return statement encountered, the following conditions 
			//may not be mandatory. So, we skip the other conditions
			//The same case applies to method arguments
			if(!bOnlyMIs) {
				if(destVertex instanceof ReturnStatementHolder) {
					ReturnStatementHolder rshObj = (ReturnStatementHolder)destVertex;
					if(rshObj.isReturnStmtContains(thObj)) {
						//This means the conditional variable that we are looking for is found in the return expression.
						bPossibleFalsePositive = true;
					}
					continue;
				} else if(destVertex instanceof MethodInvocationHolder){
					//Check whether the return variable is a part of an assert statement
					MethodInvocationHolder mich = (MethodInvocationHolder) destVertex;
					for(TypeHolder arguments : mich.getArgumentArr()) {
						if(arguments.var.equals(thObj.var)) {
							bPossibleFalsePositive = true;
						}
					}
				}
			} else {
				if(destVertex instanceof ReturnStatementHolder) {
					//Return statement reached. So continue to next node
					continue;
				}
			}
			
			//When end of WHILE or FOR is reached, then we need to verify the beginning
			//conditions again. So temporarily replace with its beginning part and restore back
			Holder replacedHolder = null;
			if(destVertex instanceof WhileEndHolder) {
				replacedHolder = destVertex;
				destVertex = ((WhileEndHolder) destVertex).getLoopStart();  
			} else if (destVertex instanceof ForEndHolder) {
				replacedHolder = destVertex;
				destVertex = ((ForEndHolder) destVertex).getLoopStart();
			}
			
			if(!(destVertex instanceof MethodInvocationHolder))
			{	
				List<CondVarHolder> listToBeAdded = null;				
				if(!bOnlyMIs)
					listToBeAdded = destVertex.contains(thObj);
				else 
					listToBeAdded = destVertex.containsMIs(thObj, CommonConstants.FORWARD_TRAVERSAL);
				
				cvhList.addAll(listToBeAdded);
				//Remove those already added from lookupvarset.
				for(CondVarHolder cvhLookUp : listToBeAdded) {
					if(lookUpVarSet.contains(cvhLookUp)) {
						lookUpVarSet.remove(cvhLookUp);
					}
				}
				
				//Along with the preceding checks also add those variables that
				//are gathered for forward lookups.
				for(Iterator internalIter = destVertex.getCondVarList().iterator(); internalIter.hasNext();)
				{
					CondVarHolder condObj = (CondVarHolder) internalIter.next();
					for(Iterator hashIter = lookUpVarSet.iterator(); hashIter.hasNext();)
					{
						CondVarHolder hashCond = (CondVarHolder) hashIter.next();
						if(condObj.getVarName().equals(hashCond.getVarName())) {
							if(!bOnlyMIs)
							{
								CondVarHolder mcvh = new CondVarHolder();
				        		mcvh.setVarName(hashCond.getAssociatedVarName());
				        		mcvh.setCondType(condObj.getCondType() + CondVarHolder.MOVE_TO_METHODARG);
				        		mcvh.setCodeSampleName(condObj.getCodeSampleName());
				        		mcvh.setCodeSampleMethodName(condObj.getCodeSampleMethodName());
				        		mcvh.setAssociatedMIH(hashCond.getAssociatedMIH());
				        		cvhList.add(mcvh);	
							}
							else
							{
								CondVarHolder mcvh = new CondVarHolder();
								mcvh.setVarName(hashCond.getAssociatedVarName());
								mcvh.setCondType(condObj.getCondType());
								mcvh.setAssociatedMIH(hashCond.getAssociatedMIH());
								mcvh.setCodeSampleName(condObj.getCodeSampleName());
				        		mcvh.setCodeSampleMethodName(condObj.getCodeSampleMethodName());
								cvhList.add(mcvh);
							}
						}
					}					
				}
			}
			else 
			{
				MethodInvocationHolder mihCasted = (MethodInvocationHolder) destVertex;
				
				//Forward traversal on a certain path is terminated
				//when a new assignment for that variable is encountered.
				if(thObj.var.equals(mihCasted.getReturnType().var))
					continue;	
				
				if(!mihCasted.getMethodName().equals("CONSTRUCTOR")) {
					if(!bOnlyMIs) 
					{
						//This scenario occurs when the returning variable
						//of current MI is passed to another MI whose return var is under boolean check.
						for(TypeHolder argument : mihCasted.getArgumentArr()) {
							if(argument.var.equals(thObj.var)) {
								CondVarHolder cvh = new CondVarHolder();
								cvh.setVarName(mihCasted.getReturnType().var);
								cvh.setAssociatedVarName(thObj.var);
								cvh.setAssociatedMIH(mihCasted);
								cvh.setCodeSampleName(currentFileName);
								cvh.setCodeSampleMethodName(currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
								lookUpVarSet.add(cvh);	
							}							
						}
					}
					else
					{
						//This scenario occurs when the succeeding method is not directly
						//involved in the boolean condition check, instead its return value
						//is involved.
						if(mihCasted.getReceiverClass().var.equals(thObj.var)) {
							CondVarHolder cvh = new CondVarHolder();
							cvh.setVarName(mihCasted.getReturnType().var);
							cvh.setAssociatedMIH(mihCasted);
							cvh.setCodeSampleName(currentFileName);
							cvh.setCodeSampleMethodName(currentMethodDeclaration != null ? currentMethodDeclaration.getName().toString() : "");
							lookUpVarSet.add(cvh);	
						}						
					}
				}	
			}
			
			//Restoring back the vertex
			if(replacedHolder != null) {
				destVertex = replacedHolder;
			}
			
			gatherConditionsByForwardTraversal(destVertex, thObj, bOnlyMIs);
		}
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
