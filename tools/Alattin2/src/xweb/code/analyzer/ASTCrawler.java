package xweb.code.analyzer;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.TreeSet;

import xweb.core.ExternalObject;
import xweb.core.MIHList;
import xweb.core.RepositoryAnalyzer;
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
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Statement;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationExpression;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;
import org.eclipse.jdt.core.dom.WhileStatement;

import xweb.code.analyzer.holder.CatchBlockBeginHolder;
import xweb.code.analyzer.holder.DefectHolder;
import xweb.code.analyzer.holder.DoWhileBeginHolder;
import xweb.code.analyzer.holder.DoWhileEndConditionHolder;
import xweb.code.analyzer.holder.ErrorEdgeHolder;
import xweb.code.analyzer.holder.ExceptionHandlerHolder;
import xweb.code.analyzer.holder.FinallyBlockBeginHolder;
import xweb.code.analyzer.holder.ForBeginConditionHolder;
import xweb.code.analyzer.holder.ForEndHolder;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.IfConditionHolder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.code.analyzer.holder.TryBlockBeginHolder;
import xweb.code.analyzer.holder.TryBlockEndHolder;
import xweb.code.analyzer.holder.WhileBeginConditionHolder;
import xweb.code.analyzer.holder.WhileEndHolder;
import xweb.common.CommonConstants;
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
	Holder mihRoot = null; 	//Variable that holds the root of the tree

	String outputFile = "PatternSequence.dat";
	public String sourceObj = "", destObj = "";	//In AnamolyDetector, only sourceObj is used, which is the object of interest gathered from the folder name of code samples
												//destObj is not used.
	
	Boolean bTrue = new Boolean(true);
	Boolean bFalse = new Boolean(false);
	static ASTCrawler currentInstance;
	
	//List of possible sources. The current class and its extends.
	HashSet<String> currentSources = new HashSet<String>(); 
	String currPackageName = "";
	
    /************** Existing Problems with JDT *******************
    1. Obj.method1(param.method2()) : Here JDT initially visits Obj.method1 and then param.method2(). How can this be altered?
    
    ************* End *****************/
    
	//Debugging Information Section
    BufferedWriter brDebug;
    int noOfSpaces = 0;
    String dirName, debugFileName;   
    
    /** Additional variables for Method Inlining **/
    HashSet<MethodDeclaration> visitedMethodsInInling;
    /** End of additional variables for Method Inlining **/
    
    /** Additional stacks for XWeb **/
    Stack<TryBlockBeginHolder> tryHolderStack = new Stack<TryBlockBeginHolder>();
    Stack<ExceptionHandlerHolder> cth_finallyHolderStack = new Stack<ExceptionHandlerHolder>();
    Stack<Holder> exceptionBlkStack = new Stack<Holder>();	//A stack that gives where is the current position like in a try block
    														//or catch block or finally block
    Stack<Holder> errorEdgeStack = new Stack<Holder>();	//In errorEdgeStack, only top level Try blocks are inserted
    													//This stack is mainly used to check whether we need error edges or normal edges
    MethodInvocationHolder lastAddedMI = null;			//Stores the most recently added method invocation
    HashMap<Integer, MethodInvocationHolder> IdToSamplesMethodHolder = new HashMap<Integer, MethodInvocationHolder>();
		//Given an ID, this Hashmap provides the method-invocation holders that are formed while parsing the code samples and local files in the input application
    /** End of additional stacks added for XWeb **/
    
    HashMap<String, HashSet<String>> varDependencyMap = new HashMap<String, HashSet<String>>();
    HashMap<String, MethodInvocationHolder> varCreationMap = new HashMap<String, MethodInvocationHolder>(); 
    HashSet<String> localVariableSet = new HashSet<String>();	//This hashset stores all local variables inside a method declaration. It does not include
    															//arguments of the method declaration due to the purpose this set is used
    HashSet<String> returnObjHolderSet = new HashSet<String>();	//A holder for storing the return objects of the current method-declaration
    boolean bReturnObjectsLoaded = false;   
    
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
	    	
	    	//if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT)
	    	(new File(dirName)).mkdirs();
	    	
	    	brDebug = new BufferedWriter(new FileWriter(debugFileName));	    	
    	}
    	catch(IOException ex)
    	{
    		logger.error("Error: Unable to open file for writing DEBUG info" + ex.getMessage());    		
    	}
    	
    	currentFileName = fileName;

    	//Clear the current class
    	currPackageName = "";
    	
    	//The field declaration map must be specific to each class.
    	//So, it has to be recreated for every class.
    	ASTCrawlerUtil.fieldDecls.clear();
    	ASTCrawlerUtil.listOfClassDecl.clear();
    	ASTCrawlerUtil.listOfMethodDecl.clear();
    	ASTCrawlerUtil.unknownIDMapper.clear();
    	CommonConstants.unknownIdGen = 0;
 
    	loopHolderStack.clear();
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
     * XWeb: When a try statement is encountered, add the try object and 
     * associated catch and finally objects to the graph.
     */
    public boolean visit(TryStatement tryObj) {

    	if(!bStartGraph) {
    		return false;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		//A situation where try block is inside a catch or finally block. IGNORING this try block but not the statements inside this
    		return true;
    	} 
    	
    	TryBlockBeginHolder tbhObj = new TryBlockBeginHolder(tryObj);
    	methodDeclGraph.addVertex(tbhObj);
    	methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, tbhObj));    		
    	currentHolderForEdge = tbhObj;
		tryHolderStack.push(tbhObj);
		
		//Add its catch blocks and finally blocks to the graph and associated stacks in reverse order
		if(tbhObj.getFbhObj() != null) {
			cth_finallyHolderStack.push(tbhObj.getFbhObj());
			methodDeclGraph.addVertex(tbhObj.getFbhObj());
		}	
		
		List<CatchBlockBeginHolder> cbhList = tbhObj.getCbHolderList();
		for(int tcounter = cbhList.size() - 1; tcounter >= 0; tcounter--) {
			CatchBlockBeginHolder cbhObj = cbhList.get(tcounter);  
			methodDeclGraph.addVertex(cbhObj);
			cth_finallyHolderStack.push(cbhObj);
		}  	
    	
		exceptionBlkStack.push(tbhObj);		
		errorEdgeStack.push(tbhObj);
		
		return true;
    }

    /**
     * IMPORTANT NOTE: When parsing between try-catch or try-finally and so, sometimes the main variable
     * "currentHolderForEdge" in the program may become invalid and new value is assigned immediately
     * at the begin or end of the blocks. USE WITH CAUTION.
     */
    
    /**
     * Reached the end of the try block that is on the top of the stack
     * This end includes the end of all catch and finally blocks associated
     * with the try block
     */
    public void endVisit(TryStatement tryObj){
    	if(!bStartGraph) {
    		return;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		return;
    	}
    	
    	
    	//Append last statement of try block to the finally block
    	TryBlockBeginHolder tbhObj = tryHolderStack.peek();
    	
    	TryBlockEndHolder tbehObj = new TryBlockEndHolder();
    	methodDeclGraph.addVertex(tbehObj);
    	
    	//If there is no finally block associated with this try block, then perform 
    	//all actions that are performed in the beginVisit of a finally block (ideally this is done in the handle Method-invocation holder itself)
    	FinallyBlockBeginHolder fbbhObj = tbhObj.getFbhObj(); 
    	if(fbbhObj == null) {
    		if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
        		methodDeclGraph.addEdge(new ErrorEdgeHolder(tbhObj.getLastStmtHolder(), tbehObj));
        	} else {
        		methodDeclGraph.addEdge(new DirectedSparseEdge(tbhObj.getLastStmtHolder(), tbehObj));
        	}	
    	
    		//Append all ends of all catch blocks to this finally block
    		for(CatchBlockBeginHolder cbbObj : tbhObj.getCbHolderList()) {
    			if(cbbObj.getLastStmtHolder() != null)
    				methodDeclGraph.addEdge(new ErrorEdgeHolder(cbbObj.getLastStmtHolder(), tbehObj));
    		}   	
    	} else {
    		//Since there is a finally block, add an edge from last possible statement of try block to the beginning of the finally block
    		methodDeclGraph.addEdge(new ErrorEdgeHolder(tbhObj.getLastStmtHolder(), fbbhObj));    		
    		
    		if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
        		methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, tbehObj));
        	} else {
        		methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, tbehObj));
        	}
    		
    		//Sometimes even there is a finally block, some catch blocks with
    		//no statements inside requires attention of adding the error edge to the finally block
    		for(CatchBlockBeginHolder cbbObj : tbhObj.getCbHolderList()) {
    			if(cbbObj.getLastStmtHolder() != null && cbbObj.isBEmptyCatchBlk())
    				methodDeclGraph.addEdge(new ErrorEdgeHolder(cbbObj.getLastStmtHolder(), fbbhObj));
    		}    		
    	}   	
    	
		//make the end of the complete try block as the current edge
		currentHolderForEdge = tbehObj;
        	
		//Remove associated elemetns from all stacks
		tryHolderStack.pop();
    	int numCatchBlocks = tbhObj.getCbHolderList().size();
    	for(int tcnt = 0; tcnt < numCatchBlocks; tcnt++) {
    		cth_finallyHolderStack.pop();
    	}
    	
    	if(tbhObj.getFbhObj() != null) {
    		cth_finallyHolderStack.pop();
    	}
    }
    
    /**
     * Function that handles actual end of the block associated with just TRY statement
     */
    public void endVisitTryActualBlock() {
    	if(!bStartGraph) {
    		return;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		return;
    	}

     	TryBlockBeginHolder tbh = tryHolderStack.peek();
    	tbh.setLastStmtHolder(currentHolderForEdge);
    	exceptionBlkStack.pop();
    	  
    	if(errorEdgeStack.size() > 0 && errorEdgeStack.peek() == tbh) {
    		errorEdgeStack.pop();
    	}   	  
    }

    
    /**
     *Function that handles a catch clause 
     */
    public boolean visit(CatchClause ccObj) {
    	if(!bStartGraph) {
    		return false;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		errorEdgeStack.push(new CatchBlockBeginHolder(null, null));
    		return true;
    	}
    	
    	//Process the variables of catch clause
    	SingleVariableDeclaration svdObj = (SingleVariableDeclaration) ccObj.getException();
		addSingleVariableDeclaration(svdObj);
    	
		//Get this catch clause from try's catch list
    	TryBlockBeginHolder tbh = tryHolderStack.peek();
    	CatchBlockBeginHolder cbbhObj = tbh.getCatchHolder(ASTCrawlerUtil.getFullClassName(ccObj.getException().getType()));
    	currentHolderForEdge = cbbhObj;
    	exceptionBlkStack.push(cbbhObj);
    	errorEdgeStack.push(cbbhObj);
    	return true;
    }
    
    public void endVisit(CatchClause ccObj) {
    	if(!bStartGraph) {
    		return;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		errorEdgeStack.pop();
    		if(errorEdgeStack.size() == 0 || (errorEdgeStack.size() > 0 && errorEdgeStack.peek() instanceof TryBlockBeginHolder)) {
    			TryBlockBeginHolder tbh = tryHolderStack.peek();
    	    	CatchBlockBeginHolder cbbhObj = tbh.getCatchHolder(ASTCrawlerUtil.getFullClassName(ccObj.getException().getType()));
    	    	cbbhObj.setLastStmtHolder(currentHolderForEdge);
    	    	exceptionBlkStack.pop();
    		}
    		return;
    	}

    	TryBlockBeginHolder tbh = tryHolderStack.peek();
    	CatchBlockBeginHolder cbbhObj = tbh.getCatchHolder(ASTCrawlerUtil.getFullClassName(ccObj.getException().getType()));
    	cbbhObj.setLastStmtHolder(currentHolderForEdge);
    	exceptionBlkStack.pop();
    	errorEdgeStack.pop();
    }
    
    /**
     * Function for handling throw statement
     */
    public boolean visit(ThrowStatement thStmtObj) {
    	if(!bStartGraph) {
    		return false;
    	}    	
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		return false;
    	}

    	//When ever there is throw statement error edges are added from the last method-invocation. Sometimes the edge
    	//might have been added before. In that case, we ignore the newly added edge
    	if(lastAddedMI != null) {
    		Expression thExprObj = thStmtObj.getExpression();
    		String clsName = "";
    		
    		if(thExprObj instanceof ClassInstanceCreation) {
    			clsName = ASTCrawlerUtil.getFullClassName(((ClassInstanceCreation)thExprObj).getType());
    		} else {
    			clsName = ASTCrawlerUtil.getRefClassName(thExprObj).type; 
    		}
    		
    		//After detecting the associated class name add 
    		if(tryHolderStack.size() > 0) {
    			
    			//This code below applies only when the Method is inside a try block that is OUTER one or is inside another try block
    			if(exceptionBlkStack.peek() instanceof TryBlockBeginHolder && errorEdgeStack.peek() instanceof TryBlockBeginHolder) {
    				//Add an error edge to all relevant catch blocks till we find either
    				//a finally block or a catch block which handles the class "Exception" directly
    				for(int tcnt = cth_finallyHolderStack.size() - 1; tcnt >= 0; tcnt --) {
    					ExceptionHandlerHolder echObj = cth_finallyHolderStack.get(tcnt);
    					
    					if(echObj instanceof CatchBlockBeginHolder && ((CatchBlockBeginHolder)echObj).getExceptionStr().equals(clsName)) {
    						try {
    							ErrorEdgeHolder eeh = new ErrorEdgeHolder(lastAddedMI, echObj);
    							methodDeclGraph.addEdge(eeh);
    						} catch(Exception ex) {/*Ignore in case of any exceptions*/}	
   							break;
    					} else if (echObj instanceof FinallyBlockBeginHolder) {
    						try {
	    						ErrorEdgeHolder eeh = new ErrorEdgeHolder(lastAddedMI, echObj);
	    						methodDeclGraph.addEdge(eeh);
    						} catch(Exception ex) {/*Ignore in case of any exceptions*/}
    						break;				
    					}			
    				}
    			}
    		}
    		
    	}
    	
    	return true;
    }
    
    
    /**
     * Function that captures the begin of Finally block
     */
    public void visitFinally(FinallyBlockBeginHolder finallyObj) {
    	if(!bStartGraph) {
    		return;
    	}
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		errorEdgeStack.push(new FinallyBlockBeginHolder(null, null));
    		return;
    	}
    	
    	//make the finally begin as the current edge
    	currentHolderForEdge = finallyObj;    	
    	exceptionBlkStack.push(finallyObj);
    	errorEdgeStack.push(finallyObj);
    }

    /**
     * Function that captures the begin of Finally block
     */
    public void endVisitFinally(FinallyBlockBeginHolder finallyObj) {
    	if(!bStartGraph) {
    		return;
    	} 
    	
    	if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
    		errorEdgeStack.pop();
    		if(errorEdgeStack.size() == 0 || (errorEdgeStack.size() > 0 && errorEdgeStack.peek() instanceof TryBlockBeginHolder)) {
    	    	exceptionBlkStack.pop();    	    	
    		}     		
    		return;
    	}

    	exceptionBlkStack.pop();
    	errorEdgeStack.pop();
    }   
	
    boolean bErrorPathAPIsFound = false;
    List<MethodInvocationHolder> foundMIHForDefects = new ArrayList<MethodInvocationHolder>();
    HashSet<String> visitedTraces = new HashSet<String>(3);
    
    /**
     * Function that generates sequences of different classes
     */
    public void generateTraceSequences(Holder srcMIObj, Holder destMIObj)
    {
    	String IDTraceStr = "";
    	depthFirstTraversal(this.mihRoot, IDTraceStr);    	
    	if(this.visitedTraces.size() == 0)
			return;
    	
    	RepositoryAnalyzer raobj = RepositoryAnalyzer.getInstance();		
		try
		{
			if(CommonConstants.MINER_LOGGING_MODE == CommonConstants.MAXIMAL_LOGGING_MODE)
			{
				raobj.bwAssocMiner.write("*******************\n");			
				raobj.bwAssocMiner.write("Filename: " + currentFileName + ", Methodname: " + currentMethodDeclaration.getName().toString() + "\n");
			}
			
			List<MIHList> mihListLists = new ArrayList<MIHList>();
			for(String trace : this.visitedTraces)
			{			
				trace = trace.trim();
				if(trace.length() == 0)
					continue;
				
				String idArr[] = trace.split(" ");
				List<MethodInvocationHolder> mihList = new ArrayList<MethodInvocationHolder>();  				
				for(int tCnt = 0; tCnt < idArr.length; tCnt++) {
					MethodInvocationHolder mihobj = IdToSamplesMethodHolder.get(new Integer(idArr[tCnt]));			
					mihList.add(mihobj);
					if(CommonConstants.MINER_LOGGING_MODE == CommonConstants.MAXIMAL_LOGGING_MODE)
					{
						raobj.bwAssocMiner.write("\t" + mihobj.getCompleteString() + " (" + mihobj.getKey() + ")\n");
					}
				}
				
				if(CommonConstants.MINER_LOGGING_MODE == CommonConstants.MAXIMAL_LOGGING_MODE)
				{
					raobj.bwAssocMiner.write("\n");
				}
				
				MIHList ml = new MIHList(mihList);
				mihListLists.add(ml);			
			}
			
			SequenceAnalyzer.sliceObjectSequences(mihListLists);			
			if(CommonConstants.MINER_LOGGING_MODE == CommonConstants.MAXIMAL_LOGGING_MODE)
			{
				raobj.bwAssocMiner.write("*******************\n");
				raobj.bwAssocMiner.flush();
			}
		}
		catch(Exception ex)
		{
			logger.error("IOException occurred while writing contents " + ex.getMessage());
			ex.printStackTrace();
		}
    }  
        
    /**
	 * Function that generates traces for several nodes in the input graph
	 */
	public void generateTraces(Holder srcMIObj, Holder destMIObj)
	{
		try
		{		
			//Identify traces
			for(Holder srcObj : sourceHolderList) {		
				bErrorPathAPIsFound = false;
				String IDTraceStr = "";
				visitedTraces.clear();
				if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)	
					foundMIHForDefects.clear();
				
				depthFirstTraversal(srcObj, IDTraceStr);
				
				MethodInvocationHolder startMIHObj = (MethodInvocationHolder) srcObj;
				if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT) {
					//This is handled in the below part.
				} else {
					Holder assocHolderObj = startMIHObj.getAssociatedLibMIH();
					if(!bErrorPathAPIsFound){
						
						boolean bReturnObjectInReturnStmt = false;
						String retObj = startMIHObj.getReturnType().var;
						if(returnObjHolderSet.contains(retObj)) {
							bReturnObjectInReturnStmt = true;
						}
						String startMIHReceiverVarName = startMIHObj.getReceiverClass().var;
											
						//Heuristic: If the current method declaration has a throws clause, then this need not be increased
						//However, if the receiver variable is a local variable and the return object of it is not part
						//of the return expression of the method declaration, then NEPC value can still be increased
						if(currentMethodDeclaration != null && (currentMethodDeclaration.thrownExceptions().size() == 0
								/*|| (localVariableSet.contains(startMIHReceiverVarName) && !bReturnObjectInReturnStmt)*/))
						{							
							assocHolderObj.incrNoErrorPathCount();					
						}					
					} 
				}
			}						
		}
		catch(Exception ex)
		{
			logger.error("Error occurred in file " + debugFileName + " " + ex.getMessage() + " while generating traces");
			ex.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void depthFirstTraversal(Holder srcObj, String IDTraceStr) {		
		if(srcObj instanceof MethodInvocationHolder) {
			IDTraceStr += ((MethodInvocationHolder)srcObj).getKey() + " ";			
		}
		
		boolean bHasOutgoingEdges = false;
		for(Iterator iter = srcObj.getOutEdges().iterator(); iter.hasNext();) {
			DirectedSparseEdge dseObj = (DirectedSparseEdge) iter.next();			
			bHasOutgoingEdges = true;
			depthFirstTraversal((Holder)dseObj.getDest(), IDTraceStr);						
		}
		
		if(bHasOutgoingEdges) {
			return;			//As we are interested in longest sequences
		}
		
		this.visitedTraces.add(IDTraceStr);					
	}
	
	//Function that matches context by treating the function calls as a sequence
	public boolean matchPatternContextSeqMode(MethodInvocationHolder srcMIH, List<MethodInvocationHolder> pcContextList) 
	{
		if(pcContextList.size() == 0)
			return true;
		
		MethodInvocationHolder mihSrc = varCreationMap.get(srcMIH.getReceiverClass().var);
		if(mihSrc == null)
			mihSrc = srcMIH;
		
		//Compute the shortest path
		DijkstraShortestPath dspObj = new DijkstraShortestPath(methodDeclGraph);
		List path = dspObj.getPath(mihRoot, mihSrc);
		
		Stack<MethodInvocationHolder> mihPathStack = new Stack<MethodInvocationHolder>();
		DirectedSparseEdge dseEdge = null;
		for(Iterator iter = path.iterator(); iter.hasNext();)
		{
			dseEdge = (DirectedSparseEdge)iter.next();
			Holder graphObj = (Holder) dseEdge.getSource();
			if(graphObj instanceof MethodInvocationHolder) {
				mihPathStack.push((MethodInvocationHolder)graphObj);
			}			
		}	
		
		if(dseEdge != null)
		{
			Holder mihObj = (Holder) dseEdge.getDest();
			if(mihObj instanceof MethodInvocationHolder) {
				mihPathStack.push((MethodInvocationHolder)mihObj);
			}
		}
		
		int contextCounter = pcContextList.size() - 1;
		while(!mihPathStack.isEmpty()) {
			MethodInvocationHolder mihObj = (MethodInvocationHolder) mihPathStack.pop();
			MethodInvocationHolder pcMIHObj = pcContextList.get(contextCounter);
			if(pcMIHObj.heuristicEquals(mihObj)) {
				contextCounter--;
			}	
			
			if(contextCounter == -1) {
				return true;
			}
		}		
		return false;
	}	
	
	//Function that matches the context by treating as a set of function calls
	public boolean matchPatternContextSetMode(MethodInvocationHolder srcMIH, List<MethodInvocationHolder> pcContextList) 
	{		
		if(pcContextList.size() == 0)
			return true;
		
		MethodInvocationHolder mihSrc = varCreationMap.get(srcMIH.getReceiverClass().var);
		if(mihSrc == null)
			mihSrc = srcMIH;
		
		//Compute the shortest path
		DijkstraShortestPath dspObj = new DijkstraShortestPath(methodDeclGraph);
		List path = dspObj.getPath(mihRoot, mihSrc);
		
		Stack<MethodInvocationHolder> mihPathStack = new Stack<MethodInvocationHolder>();
		DirectedSparseEdge dseEdge = null;
		for(Iterator iter = path.iterator(); iter.hasNext();)
		{
			dseEdge = (DirectedSparseEdge)iter.next();
			Holder graphObj = (Holder) dseEdge.getSource();
			if(graphObj instanceof MethodInvocationHolder) {
				mihPathStack.push((MethodInvocationHolder)graphObj);
			}			
		}	
		
		if(dseEdge != null)
		{
			Holder mihObj = (Holder) dseEdge.getDest();
			if(mihObj instanceof MethodInvocationHolder) {
				mihPathStack.push((MethodInvocationHolder)mihObj);
			}
		}
		
		//Iterator pcIter = pcContextList.iterator();
		//MethodInvocationHolder pcMIH = (MethodInvocationHolder ) pcIter.next();
		List<MethodInvocationHolder> clone_pcContextList = new ArrayList<MethodInvocationHolder>(pcContextList.size());
		clone_pcContextList.addAll(pcContextList);
		while(!mihPathStack.isEmpty()) {
			MethodInvocationHolder mihObj = (MethodInvocationHolder) mihPathStack.pop();
			//TODO: Instead of entire path, only relavant APIs in this path needs to be verified
			//if(pcMIH.heuristicEquals(mihObj)) {				
			//	if(!pcIter.hasNext()) {
			//		return true;
			//	} else {
			//		pcMIH = (MethodInvocationHolder ) pcIter.next();
			//	}				
			//}
			
			//TO adapt frequent itemset mining the order among the APIs in the context
			//is relaxed so that any oder is accepted
			MethodInvocationHolder pcMIHObj = null;
			boolean bMihFoundInPath = false;
			for(MethodInvocationHolder temp_pcMIHObj : clone_pcContextList) {
				if(temp_pcMIHObj.heuristicEquals(mihObj)) {
					pcMIHObj = temp_pcMIHObj;
					bMihFoundInPath = true;
					break;
				}
			}
			
			if(bMihFoundInPath) {
				clone_pcContextList.remove(pcMIHObj);
			}
			
			if(clone_pcContextList.size() == 0) {
				return true;
			}			
		}
		
		return false;
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
    private class MethodIdentifier implements StackObject { }    
    private class CatchClauseIdentifier implements StackObject   { }
    
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
				scopeVarStack.push(new CatchClauseIdentifier());
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
					//Heuristic: Ignoring control flow statements in Catch and Finally block.  
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
						break;
					}
					
					//Identifying whether the current block is THEN Block or ELSE block
					IfStatement ifNode = (IfStatement) parentNode;
					if(ifNode.getThenStatement() == astNode)
					{
						//Handling the begin of THEN block of IF statement
						IfConditionHolder ifCHObj = new IfConditionHolder(ifNode.getExpression());
						methodDeclGraph.addVertex(ifCHObj);
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
							methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, ifCHObj));
						} else {
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, ifCHObj));
						}
						currentHolderForEdge = ifCHObj;						
						scopeMethodStack.push(currentHolderForEdge);
												
						//Debugging Code
						try
						{
							noOfSpaces = noOfSpaces + 1;
							printSpacestoDebugFile();
							brDebug.write("IF-THEN " + ifCHObj + "\n");
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
					//Heuristic: Ignoring control flow statements in Catch and Finally block.  
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
						break;
					}

					WhileBeginConditionHolder wbchObj = new WhileBeginConditionHolder(((WhileStatement)parentNode).getExpression());
					methodDeclGraph.addVertex(wbchObj);
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
						methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, wbchObj));
					} else {
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, wbchObj));
					}	
					currentHolderForEdge = wbchObj;						
					scopeMethodStack.push(currentHolderForEdge);
					loopHolderStack.push(wbchObj);
					//Debugging Code
					try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("WHILE " + wbchObj + "\n");
					} catch(IOException ex)	{
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code					
					break;
					
				case ASTNode.FOR_STATEMENT:
					//Heuristic: Ignoring control flow statements in Catch and Finally block.  
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
						break;
					}
		
					//Handling the for statement block
					ForStatement fsObj = (ForStatement) parentNode;
					ForBeginConditionHolder fbchObj = new ForBeginConditionHolder(fsObj.getExpression());
					methodDeclGraph.addVertex(fbchObj);
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
						methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, fbchObj));
					} else {
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, fbchObj));
					}	
					currentHolderForEdge = fbchObj;
					scopeMethodStack.push(currentHolderForEdge);
					loopHolderStack.push(fbchObj);
					//Debugging Code
					try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("FOR " + fbchObj + "\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				case ASTNode.DO_STATEMENT:
					//Heuristic: Ignoring control flow statements in Catch and Finally block.  
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
						break;
					}

					//Handling the do-while statement block: 
					DoWhileBeginHolder dwbhObj = new DoWhileBeginHolder();
					methodDeclGraph.addVertex(dwbhObj);
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
						methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, dwbhObj));
					} else {
						methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, dwbhObj));
					}
					currentHolderForEdge = dwbhObj;	
					loopHolderStack.push(dwbhObj);
					//Debugging Code
					try {
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("DO WHILE BLOCK\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				case ASTNode.SWITCH_STATEMENT:
					//TODO: Handling the switch statement
					break;
				case ASTNode.TRY_STATEMENT:
					//For all associated catch and finally, Try is marked as parent
					if(tryHolderStack.size() > 0 && (astNode instanceof Block) 
							&& tryHolderStack.peek().getFbhObj() != null  
							&& tryHolderStack.peek().getFbhObj().getAssociatedBlockObj() == (Block)astNode) {
						visitFinally(tryHolderStack.peek().getFbhObj());
					}					
					
					//Debugging Code
					try	{
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("TRY BLOCK\n");
					} catch(IOException ex) {
						logger.info(ex.getStackTrace().toString());
					}
					//End of debugging code
					break;
				case ASTNode.CATCH_CLAUSE:
					//Debugging Code
					try {
						noOfSpaces = noOfSpaces + 1;
						printSpacestoDebugFile();
						brDebug.write("CATCH BLOCK\n");
					} catch(IOException ex) {
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
				generateTraceSequences(sourceHolder, currentHolderForEdge);							
				
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
					logger.info("Exception occurred in file: " + debugFileName + ex);
					logger.info(ex.getStackTrace().toString());
				}
				
				bStartGraph = false;
			}
			else if (astNode instanceof CatchClause)
			{
				try	{
					StackObject sObj = (StackObject) scopeVarStack.pop();
					while(! (sObj instanceof CatchClauseIdentifier))
					{	
						//Restore the full class name of the variable with its earlier class name
						VarAndFullClass vandFObj = (VarAndFullClass) sObj;
						ASTCrawlerUtil.fieldDecls.put(vandFObj.variableName, vandFObj.fullClassName);
						sObj = (StackObject) scopeVarStack.pop();
					}
				} catch(Exception ex) {
					logger.info("Exception occurred in file: " + debugFileName + ex.getMessage() + " while restoring local variables from the stack");				
				}
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
					logger.info("Exception occurred in file: " + debugFileName + ex);
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
						//Heuristic: Ignoring only IF statements in Catch and Finally block.  
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
							break;
						}

						//Identifying whether the current block is THEN Block or ELSE block
						IfStatement ifNode = (IfStatement) parentNode;
						if(ifNode.getThenStatement() == astNode)
						{
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
								printSpacestoDebugFile();
								brDebug.write("END-IF-THEN\n");
							}
						}
						else if(ifNode.getElseStatement() == astNode)
						{
							handleEndOfBlock();
							printSpacestoDebugFile();
							brDebug.write("END-IF-THEN-ELSE\n");
						}
						else
						{
							//An unexpected case. Should not occur
							logger.error("Unexpected case!!! ERROR> Should not occur");
						}
						noOfSpaces = noOfSpaces - 1;
						break;
					
					case ASTNode.WHILE_STATEMENT:
						//Heuristic: Ignoring control flow statements in Catch and Finally block.  
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
							break;
						}
						
						WhileEndHolder wehObj = new WhileEndHolder((WhileBeginConditionHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(wehObj);
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
							methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, wehObj));
						} else {
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, wehObj));
						}
						currentHolderForEdge = wehObj;
						handleEndOfBlock();
						printSpacestoDebugFile();
						brDebug.write("END-WHILE " + wehObj + "\n");
						noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.FOR_STATEMENT:	
						//Heuristic: Ignoring control flow statements in Catch and Finally block.  
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
							break;
						}
				
						//Handling the While and For statement block
						ForEndHolder fehObj = new ForEndHolder((ForBeginConditionHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(fehObj);
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
							methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, fehObj));
						} else {
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, fehObj));
						}
						currentHolderForEdge = fehObj;
						handleEndOfBlock();	
						printSpacestoDebugFile();
						brDebug.write("END-FOR " + fehObj + "\n");
						noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.DO_STATEMENT:
						//Heuristic: Ignoring control flow statements in Catch and Finally block.  
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {
							break;
						}
						
						//Handling the do while statement block. Nothing has to be done as the code
						//inside do-while has to execute atleast once
						DoStatement dsObj = (DoStatement) parentNode;
						DoWhileEndConditionHolder dwechObj = new DoWhileEndConditionHolder(dsObj.getExpression()
											, (DoWhileBeginHolder)loopHolderStack.pop());
						methodDeclGraph.addVertex(dwechObj);
						if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
							methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, dwechObj));
						} else {
							methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, dwechObj));
						}	
						currentHolderForEdge = dwechObj;						
						printSpacestoDebugFile();
						brDebug.write("END-DO-WHILE " + dwechObj + "\n");
						noOfSpaces = noOfSpaces - 1;
						break;
					case ASTNode.SWITCH_STATEMENT:
						//TODO: Handling the switch statement
						break;
					case ASTNode.TRY_STATEMENT:
						//For all associated catch and finally, Try is marked as parent
						if(tryHolderStack.size() > 0 && (astNode instanceof Block)) { 
							TryBlockBeginHolder tbhObj = tryHolderStack.peek(); 
							if(tbhObj.getFbhObj() != null && tbhObj.getFbhObj().getAssociatedBlockObj() == (Block) astNode) {
								endVisitFinally(tryHolderStack.peek().getFbhObj());
							} else if(tbhObj.getAssociatedBlk() == astNode) {
								endVisitTryActualBlock();
							}							
						}					
						break;
					default:
						break;					
					}
				}
			}
		}
		catch(Exception ex)
		{
			logger.info("Exception occurred in file: " + debugFileName + ex.getMessage());
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
				
				if(ASTCrawlerUtil.fullPackageNamesForClasses.get(className) == null)	
					ASTCrawlerUtil.fullPackageNamesForClasses.put(className, impName);
			}
			else
			{
				//TODO: To load all classes contained in that given package by some means
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
     * Function for handling Inner classes, interfaces, which are stored as TypeDeclaration object
     */
    public boolean visit(TypeDeclaration typeNode)
    {
    	//While analyzing code samples, if the sample belongs
    	//to the input application ignore it
    	if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_CODE_SAMPLES &&  
    			RepositoryAnalyzer.getInstance().getLibPackageList().contains(currPackageName)) {
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
    			logger.error("Error occurred while adding possible sources from current class " + ex.getMessage());
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
		
		//Clearing the new stacks for XWeb
    	tryHolderStack.clear();
        cth_finallyHolderStack.clear();
        exceptionBlkStack.clear();
        errorEdgeStack.clear();
        lastAddedMI = null;       	
		varDependencyMap.clear();
		varCreationMap.clear();
		IdToSamplesMethodHolder.clear();
		localVariableSet.clear();
		returnObjHolderSet.clear();
		visitedTraces.clear();
		
		bReturnObjectsLoaded = false;
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
		
		if(!bInlining) {	
			//Debugging section
			try	{
				brDebug.write("\n\n" + md.getName().getIdentifier() + "\n");
			} catch(IOException ex) {
				logger.info(ex.getStackTrace().toString());
			} catch (NullPointerException npe) {
				int i = 0;
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
	public boolean handleMethodInvocationHolder(MethodInvocationHolder mihObj, TypeHolder refType, TypeHolder returnType, ASTNode parentObj)
	{
		lastAddedMI = mihObj;	
		
		int newKey = MethodInvocationHolder.MIHKEYGEN++;
		mihObj.setKey(newKey);
		IdToSamplesMethodHolder.put(new Integer(newKey), mihObj);		
		
		//Computing dependent variables for Data-flow analysis
		HashSet<String> dependentSet = new HashSet<String>(3, 3);
		if(!refType.equals(returnType)) {		//For constructors reference and return types can be same
			if(!ASTCrawlerUtil.isPrimitiveType(refType.type)) {
				dependentSet.add(refType.var);
				HashSet<String> tDepSet = varDependencyMap.get(refType.var);
				if(tDepSet != null)	
					dependentSet.addAll(tDepSet);
			}
		}
		
		for(TypeHolder argType : mihObj.getArgumentArr()) {
			if(!ASTCrawlerUtil.isPrimitiveType(argType.type)) {
				dependentSet.add(argType.var);
				HashSet<String> tDepSet = varDependencyMap.get(argType.var);
				if(tDepSet != null)
					dependentSet.addAll(tDepSet);
			}
		}		
		
		if(!returnType.type.equals("void") && !ASTCrawlerUtil.isPrimitiveType(returnType.type)) {
			varDependencyMap.put(returnType.var, dependentSet);
			varCreationMap.put(returnType.var, mihObj);
			mihObj.getDependentVarSet().add(returnType.var);
		}
		
		mihObj.getDependentVarSet().addAll(dependentSet);				
				
		try {
			methodDeclGraph.addVertex(mihObj);
			if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
				methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, mihObj));
			} else {
				methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihObj));
			}	
		} catch(edu.uci.ics.jung.exceptions.ConstraintViolationException ex) {
			throw ex;
		}
		currentHolderForEdge = mihObj;
		
		//The reference type should be searched in both Library objects and External objects
		boolean bSearchForRefType = false;
		Holder associatedHolderObj = null;
		Set<String> possibleExceptionSet = null;
		if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT || 
				CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT || refType.type.equals(sourceObj)) {
			//Checking in library objects			
			if((associatedHolderObj = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(refType.type, mihObj)) != null) {
				mihObj.setAssociatedLibMIH(associatedHolderObj);
				bSearchForRefType = true;
				possibleExceptionSet = associatedHolderObj.getExceptionSet();
			} else {
				//Checking in external objects
				ExternalObject eeObj = RepositoryAnalyzer.getInstance().getExternalObjects().get(refType.type);
				if(eeObj != null && (associatedHolderObj = eeObj.containsMI(mihObj)) != null) {
					mihObj.setAssociatedLibMIH(associatedHolderObj);
					possibleExceptionSet = associatedHolderObj.getExceptionSet();
					bSearchForRefType = true;
				}	
			}						
		}	
		
		//modifying the return type to actual return type
		if(associatedHolderObj != null) {
			String actReturnType = associatedHolderObj.getActualReturnType();
			if(actReturnType != null && !actReturnType.equals("")) {
				mihObj.getReturnType().type = actReturnType;
				
				//modify the actual type of the associated variable, if one exists
				if(!mihObj.getReturnType().var.startsWith(CommonConstants.unknownType)) {
					ASTCrawlerUtil.fieldDecls.put(mihObj.getReturnType().var, actReturnType);	
				}
				
			}
		}

		//Gathering the number of call-sites (used in mining)
		if(associatedHolderObj != null && (CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_CODE_SAMPLES ||
				CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT))
			associatedHolderObj.incrNumCallSites();
		
		//If this statement is not in a try block but the associated mih has some
		//mined patterns log the defect
		if(tryHolderStack.size() == 0 && associatedHolderObj != null) {
			
			//Sometimes, when control comes here the associated return statement might not be
			//parsed. Therefore, to address the problem, we have this additional code, where
			//we check all return statements and load variables
			if(!bReturnObjectsLoaded) {
				bReturnObjectsLoaded = true;
				loadReturnVariables(currentMethodDeclaration.getBody());
			}
			
			if(CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT && 
					 associatedHolderObj.getMinedPatterns() != null && associatedHolderObj.getMinedPatterns().size() > 0) {
				
				if(currentMethodDeclaration != null) { 
					int numThrownExceptions = currentMethodDeclaration.thrownExceptions().size();
					if(numThrownExceptions == 0 || (numThrownExceptions > 0 && localVariableSet.contains(refType.var))) { 
						
						for(MinedPattern mpObj : associatedHolderObj.getMinedPatterns()) {
							MIHList pcObj = mpObj.getPcObj();
							//If context does not match, ignore the issue
							if(pcObj.getMihList() != null) {
								if(!matchPatternContextSeqMode(mihObj, pcObj.getMihList()))
									continue;
							}								
														
							//Heuristic: If the return object of the violated method invocation
							//is a part of return statement of the enclosing method-declaration, then
							//the violation can be ignored, as we are dealing with intra-procedural analysis
							String retObj = mihObj.getReturnType().var;
							if(returnObjHolderSet.contains(retObj) || parentObj instanceof ReturnStatement) {
								continue;
							}
							
							String methodName = currentMethodDeclaration.getName().toString();						
							DefectHolder dhObj = new DefectHolder(mpObj, mihObj, dirName + "/" + currentFileName, methodName);
							dhObj.setLowPreference(false);
							RepositoryAnalyzer.getInstance().addToDefectSet(dhObj);
						}
					}
				}
			} else if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_CODE_SAMPLES ||
					CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT) {
				
				boolean bReturnObjectInReturnStmt = false;
				String retObj = mihObj.getReturnType().var;
				if(returnObjHolderSet.contains(retObj)) {
					bReturnObjectInReturnStmt = true;
				}
				String startMIHReceiverVarName = mihObj.getReceiverClass().var;
									
				//Heuristic: If the current method declaration has a throws clause, then this need not be increased
				//However, if the receiver variable is a local variable and the return object of it is not part
				//of the return expression of the method declaration, then NEPC value can still be increased
				if(currentMethodDeclaration.thrownExceptions().size() == 0 
						/*|| (localVariableSet.contains(startMIHReceiverVarName) && !bReturnObjectInReturnStmt)*/)
					associatedHolderObj.incrNoErrorPathCount();
			}
		}
		
		
		//Add error edges for each method invocation. Here we go through the CATCH stack 
		//and try to add edges till either we get the catch that handles "Exception" or
		//reaches "finally". 
		if(tryHolderStack.size() > 0) {			
			//This code below applies only when the Method is inside a try block that is OUTER one or is inside another try block
			if(exceptionBlkStack.peek() instanceof TryBlockBeginHolder && errorEdgeStack.peek() instanceof TryBlockBeginHolder) {								
				if(bSearchForRefType) {
					bSourceObjectFound = true;
					sourceHolderList.add(mihObj);
				}		

				//Add an error edge to all relevant catch blocks till we find either
				//a finally block or a catch block which handles the class "Exception" directly
				for(int tcnt = cth_finallyHolderStack.size() - 1; tcnt >= 0; tcnt --) {
					ExceptionHandlerHolder echObj = cth_finallyHolderStack.get(tcnt);
					
					if(echObj instanceof CatchBlockBeginHolder) {
						String exceptionStr = ((CatchBlockBeginHolder)echObj).getExceptionStr();
						//Additional code for making sure whether the exception block is possible or not
						//This is controlled with a flag as sometimes the information is not available
						if(!CommonConstants.IGNORE_JEX) {
							if(bSearchForRefType && !(exceptionStr.equals("Exception") || exceptionStr.equals("java.lang.Exception"))) {
								if(!possibleExceptionSet.contains(exceptionStr)) {
									continue;
								}
							}
						}	
						
						if(!CommonConstants.IGNORE_EXCEPTION_PATHS)
						{
							ErrorEdgeHolder eeh = new ErrorEdgeHolder(mihObj, echObj);						
							methodDeclGraph.addEdge(eeh);
						}
						
						if(exceptionStr.equals("java.lang.Exception") || exceptionStr.equals("Exception")) {
							break;
						}					
					} else if (echObj instanceof FinallyBlockBeginHolder) {
						ErrorEdgeHolder eeh = new ErrorEdgeHolder(mihObj, echObj);
						methodDeclGraph.addEdge(eeh);
						break;				
					}
				}				
			} else if (exceptionBlkStack.peek() instanceof TryBlockBeginHolder && !(errorEdgeStack.peek() instanceof TryBlockBeginHolder)) {
				//A case where we are currently in a try block but this TRY block is again inside some catch or finally block
				//In this case, we just add edges according to a localized analysis
				/*TryBlockBeginHolder relatedTBHObj = cth_finallyHolderStack.peek().getTbhObj(); 
				for(int tcnt = cth_finallyHolderStack.size() - 1; tcnt >= 0; tcnt --) {
					ExceptionHandlerHolder echObj = cth_finallyHolderStack.get(tcnt);
					if(echObj.getTbhObj() == relatedTBHObj) {
						if(echObj instanceof CatchBlockBeginHolder) {
							ErrorEdgeHolder eeh = new ErrorEdgeHolder(mihObj, echObj);
							methodDeclGraph.addEdge(eeh);
							if(((CatchBlockBeginHolder)echObj).getExceptionStr().equals("java.lang.Exception")
									|| ((CatchBlockBeginHolder)echObj).getExceptionStr().equals("Exception")) {
								break;
							}					
						} else if (echObj instanceof FinallyBlockBeginHolder) {
							ErrorEdgeHolder eeh = new ErrorEdgeHolder(mihObj, echObj);
							methodDeclGraph.addEdge(eeh);
							break;				
						}
					} else {
						break;
					}
				}*/
				FinallyBlockBeginHolder fbbObj = tryHolderStack.peek().getFbhObj();
				if(fbbObj != null) {
					methodDeclGraph.addEdge(new ErrorEdgeHolder(mihObj, fbbObj));
				}	
				
			} else if(exceptionBlkStack.peek() instanceof CatchBlockBeginHolder) {
				//When the method is in a catch block				
				//Check whether the current try block has finally, if yes add an error edge to that
				//If not, advance to the other catch blocks in the stack
				FinallyBlockBeginHolder fbbObj = tryHolderStack.peek().getFbhObj();
				if(fbbObj != null) {
					methodDeclGraph.addEdge(new ErrorEdgeHolder(mihObj, fbbObj));
				} 
				else {	
					if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {								
						Holder hObj = (Holder) errorEdgeStack.pop();
						if(errorEdgeStack.size() == 0 || errorEdgeStack.peek() instanceof TryBlockBeginHolder)				
							addAdditionalEdgesToSubsequentBlocks(mihObj, (ExceptionHandlerHolder) exceptionBlkStack.peek());
						
						errorEdgeStack.push(hObj);
					}	
				}			
			} else if(exceptionBlkStack.peek() instanceof FinallyBlockBeginHolder) {	
				//When the method is in a finally block
				if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)) {								
					Holder hObj = (Holder) errorEdgeStack.pop();
					if(errorEdgeStack.size() == 0 || errorEdgeStack.peek() instanceof TryBlockBeginHolder)				
						addAdditionalEdgesToSubsequentBlocks(mihObj, (ExceptionHandlerHolder) exceptionBlkStack.peek());
					errorEdgeStack.push(hObj);					
				}			
			}			
		}	
		
		//Debugging section
		try {
			printSpacestoDebugFile();
			brDebug.write("\t " + mihObj + "\n");
		} catch(IOException ex) {
			logger.info(ex.getStackTrace().toString());
		}
		//End of debugging section

		return true;
	}
	
	/**
	 * Function that takes care of adding additional edges to subsequent finally or catch blocks 
	 * in case an a method invocation is inside a catch or finally block
	 * @param mihObj
	 * @param currentExceptionStkHolder
	 */
	public void addAdditionalEdgesToSubsequentBlocks(MethodInvocationHolder mihObj, ExceptionHandlerHolder currentExceptionStkHolder) {
		//Initially traverse the elements in the exception stack till we reach 
		//the catch blocks of the enclosing try blocks
		int tCounter = cth_finallyHolderStack.size() - 1;
		while(tCounter >= 0 && cth_finallyHolderStack.get(tCounter).getTbhObj() == currentExceptionStkHolder.getTbhObj()) {
			tCounter--;
		}
		
		//This function will be called only for catch blocks that are inside a try blocks. 
		//as we ignore the try-catch-blocks inside a catch block
		if(tCounter >= 0) {
			//Once we find the enclosing catch or finally blocks
			for(int tcnt = tCounter; tcnt >= 0; tcnt --) {
				ExceptionHandlerHolder echObj = cth_finallyHolderStack.get(tcnt);
				
				ErrorEdgeHolder eeh = new ErrorEdgeHolder(mihObj, echObj);
				methodDeclGraph.addEdge(eeh);
				if(echObj instanceof CatchBlockBeginHolder) {
					if(((CatchBlockBeginHolder)echObj).getExceptionStr().equals("java.lang.Exception")
							|| ((CatchBlockBeginHolder)echObj).getExceptionStr().equals("Exception")) {
						break;
					}					
				} else if (echObj instanceof FinallyBlockBeginHolder) {
					break;				
				}			
			}			
		}	
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
			
			TypeHolder refType = ASTCrawlerUtil.getRefClassName(miObj.getExpression());
			TypeHolder returnType = ASTCrawlerUtil.getReturnType(miObj);
			ASTCrawlerUtil.unknownIDMapper.put(miObj, returnType);
			
			//Method-inlining commented for XWeb. This is required for inter-procedural analysis
			/*if(refType.equals("this"))
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
					refType.type = CommonConstants.multipleCurrTypes;
				}
			}*/
						
			MethodInvocationHolder mihObj;
			if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT || CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)
				mihObj = new MethodInvocationHolder(refType, returnType, miObj, false);
			else 
				mihObj = new MethodInvocationHolder(refType, returnType, miObj);
			
			if(miObj.getParent() instanceof CastExpression)
			{
				mihObj.bDowncast = true;
			}
			
			return handleMethodInvocationHolder(mihObj, refType, returnType, miObj.getParent());
			
		}
		catch(Exception ex)
		{
			logger.info("Happened in file: " + debugFileName + " " + ex.getMessage());
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

		try
		{
			if(!bStartGraph)
				return false;
			
			//Visit the arguments first
			handleArgumentMethodInvocations(cicObj.arguments());
			TypeHolder thObj = ASTCrawlerUtil.getReturnType(cicObj); 
			if(thObj.type.startsWith(CommonConstants.unknownType))
			{
				thObj = new TypeHolder(ASTCrawlerUtil.getFullClassName(cicObj.getType()));
			}
			
			//Getting the actual type from the new expression
			String fullclsname = ASTCrawlerUtil.getFullClassName(cicObj.getType());
			if(!thObj.type.equals(fullclsname)) {
				thObj.type = fullclsname;
				ASTCrawlerUtil.fieldDecls.put(thObj.var, thObj.type);							
			}
			
			ASTCrawlerUtil.unknownIDMapper.put(cicObj, thObj);
						
			MethodInvocationHolder mihObj;
			if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT || CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)
				mihObj = new MethodInvocationHolder(thObj, thObj, cicObj, false);
			else
				mihObj = new MethodInvocationHolder(thObj, thObj, cicObj);
			
			return handleMethodInvocationHolder(mihObj, thObj, thObj, cicObj.getParent());
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
			return handleMethodInvocationHolder(mihObj, refType, returnType, ceObj.getParent());				
		}	
		catch(Exception ex)
		{
			logger.info(ex.getStackTrace().toString());
		}
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
	
	public boolean visit(Assignment asNode)
	{
		Expression leftHandSide = asNode.getLeftHandSide();
		Expression rightHandSide = asNode.getRightHandSide();
		
		//APIUsage: The run-time type of a variable can be modified if the assignment
		//statement contains some hints in the changes of type. Currently only constructor and typecast
		//are considered.
		if(leftHandSide instanceof SimpleName && (rightHandSide instanceof ClassInstanceCreation || rightHandSide instanceof CastExpression))
		{
			String initializerType = ASTCrawlerUtil.getRefClassName(rightHandSide).getType();
			if(!initializerType.equals(CommonConstants.unknownType) && !initializerType.equals("this") && !initializerType.equals(CommonConstants.multipleCurrTypes) &&!initializerType.equals("null"))
			{
				ASTCrawlerUtil.fieldDecls.put(((SimpleName)leftHandSide).getIdentifier(), initializerType);
			}
		}
		
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
		
		methodDeclGraph.addVertex(mihDummy);
		if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
			methodDeclGraph.addEdge(new ErrorEdgeHolder(currentHolderForEdge, mihDummy));
		} else {
			methodDeclGraph.addEdge(new DirectedSparseEdge(currentHolderForEdge, mihDummy));
		}	
		

		//If no nodes are added to the graph by the then block or else block, no need to add the second aspect
		if(topStack != currentHolderForEdge) {
			if(errorEdgeStack.size() > 0 && (errorEdgeStack.peek() instanceof CatchBlockBeginHolder || errorEdgeStack.peek() instanceof FinallyBlockBeginHolder)){
				methodDeclGraph.addEdge(new ErrorEdgeHolder(topStack, mihDummy));
			} else {
				methodDeclGraph.addEdge(new DirectedSparseEdge(topStack, mihDummy));
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
     * Function for storing objects inside a return statement
     */
    public boolean visit(ReturnStatement rtObj) 
    {
    	Expression retExprObj = rtObj.getExpression();
    	if(retExprObj == null)
    		return true;
    	
    	retExprObj.accept(this);
    	if(retExprObj instanceof SimpleName) {
    		returnObjHolderSet.add(((SimpleName)retExprObj).getIdentifier());
    	} else if (retExprObj instanceof MethodInvocation || retExprObj instanceof ClassInstanceCreation) {
    		TypeHolder retObj = ASTCrawlerUtil.unknownIDMapper.get(retExprObj);
    		if(retObj != null)	
    			returnObjHolderSet.add(retObj.var);    		
    	} else {
    		//TODO: Currently handling only simple variables and method-invocation
    		//holders as a part of return statement.
    	}
    	   	
    	return true;
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
			localVariableSet.add(vdfObj.getName().getIdentifier());
			if(prevClassName != null)
			{
				//There is an earlier full class name associated with this variable outside the current scope
				VarAndFullClass vandfobj = new VarAndFullClass(vdfObj.getName().getIdentifier(), prevClassName);
				scopeVarStack.push(vandfobj);
			}
		}

    }
    
    public void loadReturnVariables(Statement stmtObj) {
    	if(stmtObj instanceof ReturnStatement) {
			ReturnStatement rtObj = (ReturnStatement) stmtObj;
			Expression exprObj = rtObj.getExpression();
			if(exprObj != null && exprObj instanceof SimpleName)
			{
				returnObjHolderSet.add(((SimpleName)exprObj).getIdentifier());
			}					
    	} else if(stmtObj instanceof Block) {
    	   	List stmtList = ((Block)stmtObj).statements();
			for(Iterator iter = stmtList.iterator(); iter.hasNext();)
			{
				ASTNode astNode = (ASTNode) iter.next();
				switch(astNode.getNodeType())
				{
					case ASTNode.IF_STATEMENT:
						IfStatement ifObj = (IfStatement) astNode;
						loadReturnVariables(ifObj.getThenStatement());
						loadReturnVariables(ifObj.getElseStatement());
						break;
					case ASTNode.WHILE_STATEMENT:
						WhileStatement whObj = (WhileStatement) astNode;
						loadReturnVariables(whObj.getBody());
						break;
					case ASTNode.DO_STATEMENT:
						DoStatement dObj = (DoStatement) astNode;
						loadReturnVariables(dObj.getBody());
						break;
					case ASTNode.FOR_STATEMENT:
						ForStatement fObj = (ForStatement) astNode;
						loadReturnVariables(fObj.getBody());
						break;
					case ASTNode.TRY_STATEMENT:
						TryStatement tryObj = (TryStatement) astNode;
						loadReturnVariables(tryObj.getBody());						
						break;
					case ASTNode.CATCH_CLAUSE:
						CatchClause ccObj = (CatchClause) astNode;
						loadReturnVariables(ccObj.getBody());
						break;
					case ASTNode.RETURN_STATEMENT:
						ReturnStatement rtObj = (ReturnStatement) astNode;
						Expression exprObj = rtObj.getExpression();
						if(exprObj != null && exprObj instanceof SimpleName)
						{
							returnObjHolderSet.add(((SimpleName)exprObj).getIdentifier());
						}					
						break;
				}
			}
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
