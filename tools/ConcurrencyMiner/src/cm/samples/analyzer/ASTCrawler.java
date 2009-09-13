package cm.samples.analyzer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;
import java.util.Queue;


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

import cm.common.CommonConstants;
import cm.inpapp.holder.ExternalObject;
import cm.popup.actions.RepositoryAnalyzer;
import cm.samples.analyzer.holder.AssignmentCondHolder;
import cm.samples.analyzer.holder.BlockEndHolder;
import cm.samples.analyzer.holder.CondVarHolder;
import cm.samples.analyzer.holder.CondVarHolder_Typeholder;
import cm.samples.analyzer.holder.DoWhileBeginHolder;
import cm.samples.analyzer.holder.DoWhileEndConditionHolder;
import cm.samples.analyzer.holder.ElseEndHolder;
import cm.samples.analyzer.holder.ForBeginConditionHolder;
import cm.samples.analyzer.holder.ForEndHolder;
import cm.samples.analyzer.holder.Holder;
import cm.samples.analyzer.holder.IfConditionHolder;
import cm.samples.analyzer.holder.MethodInvocationHolder;
import cm.samples.analyzer.holder.ReturnStatementHolder;
import cm.samples.analyzer.holder.ThenEndHolder;
import cm.samples.analyzer.holder.WhileBeginConditionHolder;
import cm.samples.analyzer.holder.WhileEndHolder;

import edu.uci.ics.jung.graph.impl.DirectedSparseEdge;
import edu.uci.ics.jung.graph.impl.DirectedSparseGraph;

/**
 * Visitor pattern for analyzing gathered code examples. Tranforms an AST into a CFG 
 * @author suresh_thummalapenta * 
 */
public class ASTCrawler extends ASTVisitor {

	static Logger logger = Logger.getLogger("ASTCrawler");
	
	public boolean bAPIUsageMetrics = false;
	Stack<StackObject> scopeVarStack = new Stack<StackObject>();	//Stack that holds information of all variables that need to be restored when ever scope changes
    Stack<StackObject> scopeMethodStack = new Stack<StackObject>();	//Stack that holds list of method class used for traversing up once a required type was found
    Stack<MethodDeclaration> inliningMethodStack = new Stack<MethodDeclaration>();
    Stack<Holder> loopHolderStack = new Stack<Holder>();
    
    //Variables storing the current holders like current Class, Method that is parsed
    static TypeDeclaration currentClassDeclaration; 
    static MethodDeclaration currentMethodDeclaration;
    static String currentFileName;
     
    DirectedSparseGraph methodDeclGraph = null;
             
	boolean bStartGraph = false, bSourceObjectFound = false;
	Holder currentHolderForEdge, sourceHolder = null, destHolder = null;
	List<Holder> sourceHolderList = new ArrayList<Holder>();	//Here list is used, as we are interested
																//to get more patterns
	Holder mihRoot; 	//Variable that holds the root of the tree

	public String sourceObj = "", destObj = "";	//In AnamolyDetector, only sourceObj is used, which is the object of interest gathered from the folder name of code samples
												//destObj is not used.
	
	Boolean bTrue = new Boolean(true);
	Boolean bFalse = new Boolean(false);
	static ASTCrawler currentInstance;
	
	//List of possible sources. The current class and its extends.
	String currPackageName = "";
	
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
    	currentFileName = fileName;
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
						
					}
					else if(ifNode.getElseStatement() == astNode)
					{
						//Nothing has to be done as the end of THEN block automatically handles this case					
					}
					break;
				case ASTNode.WHILE_STATEMENT:
					Holder wbchObj = new WhileBeginConditionHolder(((WhileStatement)parentNode).getExpression());
					methodDeclGraph.addVertex(wbchObj);
					methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, wbchObj));
					currentHolderForEdge = wbchObj;						
					scopeMethodStack.push(currentHolderForEdge);
					loopHolderStack.push(wbchObj);									
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
					break;
				case ASTNode.DO_STATEMENT:
					//Handling the do-while statement block: 
					DoWhileBeginHolder dwbhObj = new DoWhileBeginHolder();
					methodDeclGraph.addVertex(dwbhObj);
					methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, dwbhObj));
					currentHolderForEdge = dwbhObj;	
					loopHolderStack.push(dwbhObj);					
					break;
				case ASTNode.SWITCH_STATEMENT:
					//TODO: Handling the switch statement
					break;
				case ASTNode.TRY_STATEMENT:
					break;
				case ASTNode.CATCH_CLAUSE:
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
				//If the required source object appeared in the given 
				//method body, then traverse the graph to prepare object models
				if(bSourceObjectFound)
				{
					generatePatterns(currentHolderForEdge);
				}			
				
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
						break;
					case ASTNode.FOR_STATEMENT:	
						//Handling the While and For statement block
						ForEndHolder fehObj = new ForEndHolder((ForBeginConditionHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(fehObj);
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, fehObj));
						currentHolderForEdge = fehObj;
						handleEndOfBlock();	
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
		inliningMethodStack.clear();
		destHolder = null;
		sourceHolder = null;
		sourceHolderList.clear();
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
		
    	if(refType.getType().equals(sourceObj) || returnType.getType().equals(sourceObj) || mihObj.isPresentInArgumentArr(sourceObj)
				|| refType.getType().equals(CommonConstants.multipleCurrTypes)) 
		{
			//Get the external object associated with this source and see whether
			//such API is of interest for the current library.
			ExternalObject eeObj = null;
			MethodInvocationHolder equiLibMIH = null;			
			eeObj = RepositoryAnalyzer.getInstance().getExternalObjects().get(sourceObj);

			if(eeObj != null)
			{	
				if((equiLibMIH = eeObj.containsMI(mihObj)) != null) {
					bSourceObjectFound = true;
					sourceHolderList.add(mihObj);
					mihObj.setAssociatedLibMIH(equiLibMIH);
				}							
			}	
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
			
			MethodInvocationHolder mihObj = new MethodInvocationHolder(refType, returnType, miObj);
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
		BlockEndHolder mihDummy = new BlockEndHolder();
		
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
	
    /*************** SPECIFIC CODE OF ANAMOLY DETECTOR *****************/
        
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
			Iterator srcHolderIter = sourceHolderList.iterator();
			while(srcHolderIter.hasNext()) {
				MethodInvocationHolder srcMINode = (MethodInvocationHolder) srcHolderIter.next();
				MethodInvocationHolder assocLibMIH = srcMINode.getAssociatedLibMIH();
				
				if((CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_CODESAMPLES
						|| CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_LIBRARY) 
						&& assocLibMIH.getPrePostList() == null) {
					continue;	//If there are no patterns, there is no need to proceed further during
								//bug finding.
				}
				 
				//Collect all possible patterns for the current method invocation holder
				//Preparing for backward traversal
				List<TypeHolder> mihHolderList = new ArrayList<TypeHolder>();
				TypeHolder recTypObj = ((MethodInvocationHolder)srcMINode).getReceiverClass();
				recTypObj.setElemType(CommonConstants.RECEIVER_PATTERNS);
				recTypObj.setAssocHolderObj(srcMINode);
				mihHolderList.add(recTypObj);						
				for(TypeHolder argHolderList : ((MethodInvocationHolder)srcMINode).getArgumentArr()) {
					argHolderList.setElemType(CommonConstants.ARGUMENT_PATTERNS);
					argHolderList.setAssocHolderObj(srcMINode);
					mihHolderList.add(argHolderList);
				}				
				
				//Gathering backward elements
				visitedVertices.clear();
				bckTraceStoreArr.clear();
				traversalHolderQ.clear();
				lookUpVarSet.clear();
				gatherBackwardTraces(srcMINode, mihHolderList, assocLibMIH);
								
				//Making preparations for the forward traversal. Captures condition 
				//direct checks only on the return variable or receiver
				mihHolderList = new ArrayList<TypeHolder>();
				mihHolderList.add(recTypObj); 
				TypeHolder retTypeObj = srcMINode.getReturnType();
				retTypeObj.setElemType(CommonConstants.RETURN_PATTERNS);
				retTypeObj.setAssocHolderObj(srcMINode);
				mihHolderList.add(retTypeObj);
				
				//Gathering forward elements
				visitedVertices.clear();					
				fwdTraceStoreArr.clear();
				traversalHolderQ.clear();
				lookUpVarSet.clear();
				gatherForwardTraces(srcMINode, mihHolderList, assocLibMIH);
								
				//Storing elements of backward and forward traversal						
				assocLibMIH.addToPrePostList(bckTraceStoreArr, fwdTraceStoreArr, currentFileName, ((currentMethodDeclaration != null) ? currentMethodDeclaration.getName().toString() : ""));				
			}
		}
		catch(Exception ex) {
			logger.error("Exception occurred" + ex);
		}
	}		
	
	/***********************************************************************************/
	/******************    FUNCTIONS FOR TRACE COLLECTION    ***************************/	
	/***********************************************************************************/
	
	private Set<Holder> bckTraceStoreArr = new HashSet<Holder>();
	private Set<Holder> fwdTraceStoreArr = new HashSet<Holder>();
	//Common Variable declarations used by all methods
	HashSet<Holder> visitedVertices = new HashSet<Holder>();
	HashSet<CondVarHolder> lookUpVarSet = new HashSet<CondVarHolder>();
	private Queue<Holder> traversalHolderQ = new LinkedList<Holder>();	
		
		
	/**
	 * Function that identifies a conditional node that contains the given
	 * variable by traversing back from the given position in the DAG.
	 * 
	 * Uses a breadth-first search backwards
	 *  
	 * The flag "bOnlyMIs" if true, collects only nodes that are associated with method invocations
	 */
	public void gatherBackwardTraces(Holder srcMINode, List<TypeHolder> mihElemList, MethodInvocationHolder assocLibMIH) throws IOException
	{
		if(visitedVertices.contains(srcMINode) || (srcMINode == mihRoot)) {
			return;
		}
				
		//To identify exact dominators, always avoid entering IF/ELSE, FOR, WHILE blocks
		//Also avoid considering any conditions of that IF/FOR/WHILE.. One catch with this
		//is that, if there is a RETURN statement, such blocks may induce control dependency
		boolean bSkipFurtherProcessing = false;
		if(srcMINode instanceof ThenEndHolder || srcMINode instanceof ElseEndHolder || srcMINode instanceof ForEndHolder
				|| srcMINode instanceof WhileEndHolder || srcMINode instanceof DoWhileEndConditionHolder) {
			srcMINode = srcMINode.getLoopStart();
			bSkipFurtherProcessing = true;		
		}
		
		if(srcMINode instanceof BlockEndHolder) {
			BlockEndHolder behObj = (BlockEndHolder) srcMINode;
			Holder loopBegin = behObj.getLoopStart();			
			if(loopBegin != null) {
				srcMINode = loopBegin;
				bSkipFurtherProcessing = true;
			}			
		}
				
		if(bSkipFurtherProcessing) {
			for(Iterator iter = srcMINode.getInEdges().iterator(); iter.hasNext();) {
				Holder srcVertex = (Holder)((DirectedSparseEdge)iter.next()).getSource();
				traversalHolderQ.add(srcVertex);
			}
			
			visitedVertices.add(srcMINode);
			if(traversalHolderQ.size() != 0) {
				gatherBackwardTraces(traversalHolderQ.poll(), mihElemList, assocLibMIH);
			}
			return;
		}
		
		
		visitedVertices.add(srcMINode);
		Set inEdgeSet = srcMINode.getInEdges();
		for(Iterator iter = inEdgeSet.iterator(); iter.hasNext();) {
			Holder srcVertex = (Holder)((DirectedSparseEdge)iter.next()).getSource();
			if(!(srcVertex instanceof MethodInvocationHolder)) {				
				//Check whether there are any conditions on receiver or arguments over here:
				for(TypeHolder thObj : mihElemList) {
					List<CondVarHolder> listToBeAdded = null;
					listToBeAdded = srcVertex.contains(thObj);
									
					if(listToBeAdded != null && listToBeAdded.size() > 0) {
						for(CondVarHolder cvhTest : listToBeAdded){
							CondVarHolder_Typeholder cvh_thObj = new CondVarHolder_Typeholder();
							cvh_thObj.cvhObj = cvhTest.clone();
							if(thObj.getAssocHolderObj() instanceof MethodInvocationHolder)
								cvh_thObj.cvhObj.setAssociatedMIH((MethodInvocationHolder)thObj.getAssocHolderObj());
							else
								logger.error("ERROR: Associated object of a typeholder should be of type MethodInvocationHolder");
							
							cvh_thObj.thObj = thObj.clone();
							if(!ASTCrawlerUtil.ignoreHolderObject(cvh_thObj, assocLibMIH) && !bckTraceStoreArr.contains(cvh_thObj)) {
								bckTraceStoreArr.add(cvh_thObj);
							}													
						}
					}
					
					//Add those conditions that are already not added to cvhList
					//These conditions are helpful later to gather return condition checks
					for(CondVarHolder lookUpCVH : srcVertex.getCondVarList()) {
						if(!listToBeAdded.contains(lookUpCVH))
							lookUpVarSet.add(lookUpCVH);
					}
				}
			} else {
				MethodInvocationHolder mihCasted = (MethodInvocationHolder) srcVertex;
				
				//Stop verifying backward when an assignment is encountered for the
				//variable we are interested in. Because the prior assignment conditions
				//may not affect anything to the variable.
				TypeHolder thToRemove = null;
				boolean bDependencyExists = false;
				for(TypeHolder thObjTemp : mihElemList) {
					if(thObjTemp.var.equals(mihCasted.getReturnType().var)) {
						thToRemove = thObjTemp;	
						bDependencyExists = true;
					}					
					if(thObjTemp.var.equals(mihCasted.getReceiverClass().var)) {						
						bDependencyExists = true;
					}
				}
				
				//As a transitive dependency found, add the elements of current mihCasted to the list
				if(thToRemove != null || bDependencyExists) {
					if(thToRemove != null)
						mihElemList.remove(thToRemove);					
				}
				
				//Looking up for condition checks on return types
				if(!mihCasted.equals(assocLibMIH)) {	//This is a guarding condition to avoid the 
														//traps in the code where the same methodinvocation is found to be a guard.
					Iterator iterLookup = lookUpVarSet.iterator();
					while(iterLookup.hasNext()) {
						CondVarHolder cvhForLookup = (CondVarHolder) iterLookup.next();
						if(cvhForLookup.getVarName().equals(mihCasted.getReturnType().var))
						{
							CondVarHolder mcvh = new CondVarHolder();
				        	mcvh.setVarName(mihCasted.getReturnType().var);
				        	mcvh.setCondType(cvhForLookup.getCondType());
				        	mcvh.setAssociatedMIH(mihCasted);
				        	mcvh.setCodeSampleName(cvhForLookup.getCodeSampleName());
				        	mcvh.setCodeSampleMethodName(cvhForLookup.getCodeSampleMethodName());
				        			        	
				        	CondVarHolder_Typeholder cvh_thObj = new CondVarHolder_Typeholder();
				        	cvh_thObj.cvhObj = mcvh;
				        	cvh_thObj.thObj = mihCasted.getReturnType().clone();
				        	cvh_thObj.thObj.setElemType(CommonConstants.RETURN_PATTERNS);
				        	
				        	if(!ASTCrawlerUtil.ignoreHolderObject(cvh_thObj, assocLibMIH) && !bckTraceStoreArr.contains(cvh_thObj)) {			        	
				        		bckTraceStoreArr.add(cvh_thObj);
				        	}				        	
						}					
					}
				}
			}
			traversalHolderQ.add(srcVertex);
		}
		
		if(traversalHolderQ.size() != 0) {
			gatherBackwardTraces(traversalHolderQ.poll(), mihElemList, assocLibMIH);
		}
		
	}
	
	public void gatherForwardTraces(Holder srcMINode, List<TypeHolder> mihElemList, MethodInvocationHolder assocLibMIH) throws IOException
	{
		if(visitedVertices.contains(srcMINode) || (srcMINode == mihRoot) || mihElemList.size() == 0) {
			return;
		}			
		
		visitedVertices.add(srcMINode);
		Set outEdgeSet = srcMINode.getOutEdges();
		for(Iterator iter = outEdgeSet.iterator(); iter.hasNext();) {
			Holder destVertex = (Holder)((DirectedSparseEdge)iter.next()).getDest();
			
			if(destVertex instanceof ReturnStatementHolder) {
				//Return statement reached. So continue to next node
				continue;
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
			
			if(!(destVertex instanceof MethodInvocationHolder))	{
				//Found a condition node				
				for(TypeHolder thObj : mihElemList) {
					List<CondVarHolder> listToBeAdded = null;
					listToBeAdded = destVertex.contains(thObj);
										
					if(listToBeAdded != null) {
						for(CondVarHolder cvhTest : listToBeAdded){
							CondVarHolder_Typeholder cvh_thObj = new CondVarHolder_Typeholder();
							cvh_thObj.cvhObj = cvhTest.clone();
							if(thObj.getAssocHolderObj() instanceof MethodInvocationHolder) 
								cvh_thObj.cvhObj.setAssociatedMIH((MethodInvocationHolder)thObj.getAssocHolderObj());
							else	
								logger.error("ERROR: Associated object of a typeholder should be of type MethodInvocationHolder");
								
							cvh_thObj.thObj = thObj.clone();
							
							if(!ASTCrawlerUtil.ignoreHolderObject(cvh_thObj, assocLibMIH) && !fwdTraceStoreArr.contains(cvh_thObj)) {
								fwdTraceStoreArr.add(cvh_thObj);
							}							
						}
					}					
				}				
			} else {
				MethodInvocationHolder mihCasted = (MethodInvocationHolder) destVertex;
				
				//Stop verifying forward when an assignment is encountered for the
				//variable we are interested in. Because the next assignment conditions
				//may not affect anything to the variable.
				TypeHolder thToRemove = null;
				boolean bDependencyExists = false;
				for(TypeHolder thObjTemp : mihElemList) {
					if(thObjTemp.var.equals(mihCasted.getReturnType().var)) {
						thToRemove = thObjTemp;	
						bDependencyExists = true;
					}					
					if(thObjTemp.var.equals(mihCasted.getReceiverClass().var)) {						
						bDependencyExists = true;
					}
					
					for(TypeHolder argObj : mihCasted.getArgumentArr()) {
						if(argObj.var.equals(thObjTemp.var)){
							bDependencyExists = true;
							break;
						}
					}					
				}
				
				if(thToRemove != null || bDependencyExists) {
					if(thToRemove != null)
						mihElemList.remove(thToRemove);				
				}			
			}
			
			//Restoring back the vertex
			if(replacedHolder != null) {
				destVertex = replacedHolder;
			}
			traversalHolderQ.add(destVertex);
		}		
		
		if(traversalHolderQ.size() != 0) {
			gatherForwardTraces(traversalHolderQ.poll(), mihElemList, assocLibMIH);
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
	/******** End of Misc functions for debugging **************/	
}
