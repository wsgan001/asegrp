package xweb.code.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.BooleanLiteral;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.CharacterLiteral;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.NullLiteral;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.PrimitiveType;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;

import xweb.code.analyzer.holder.CondVarHolder;
import xweb.code.analyzer.holder.Holder;
import xweb.code.analyzer.holder.IntHolder;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.common.CommonConstants;
import xweb.core.LibMethodHolder;
import xweb.core.MIHList;
import xweb.core.RepositoryAnalyzer;

import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.InfixExpression.Operator;

/**
 * Utilities class, which is a subclass of ASTCrawler. All functions of this class are static
 * @author suresh_thummalapenta
 *
 */
public class ASTCrawlerUtil {

	public static Logger logger = Logger.getLogger("ASTCrawlerUtil");
	
    static protected HashMap<String, String> fieldDecls = new HashMap<String, String>(); 
    static public HashSet<String> importJDKClasses = new HashSet<String>();
    static public HashMap<String, String> fullPackageNamesForClasses = new HashMap<String, String>();
    static protected ArrayList<TypeDeclaration> listOfClassDecl = new ArrayList<TypeDeclaration>();
    static protected ArrayList<MethodDeclaration> listOfMethodDecl = new ArrayList<MethodDeclaration>();
    static public HashMap<Expression, TypeHolder> unknownIDMapper = new HashMap<Expression, TypeHolder>(); 
    static protected HashMap<String, Boolean> runtimeExceptionMap = new HashMap<String, Boolean>();	//A map that stores whether a given exception is run-time or not
    
	//Variables for storing the scope of the variable
    static protected int CLASS_SCOPE = 1;
    static protected int INNER_CLASS_SCOPE = 2;
    static protected int BLOCK_SCOPE = 3;

    /********* Begin of private functions for handling minor aspects  ****************/
    public static String getFullClassName(Type typeObjP)
    {
    	try
    	{
    		if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT ||
    				CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT)
    		{
    			return typeObjP.resolveBinding().getQualifiedName();
    		} 		
    		    		
    		String fullClassName = CommonConstants.getUniqueIDForUnknown();
    		if(typeObjP instanceof SimpleType)
    		{	
    			SimpleType typeObj =  (SimpleType) typeObjP;
				String typeName = typeObj.getName().getFullyQualifiedName(); 
				//TODO: To check this, there can be a problem here
				
				if(!typeName.contains("."))	{
					fullClassName = (String) ASTCrawlerUtil.fullPackageNamesForClasses.get(typeName);
					
					if(fullClassName == null) {
						//Type of the current class is not found in the list of import statements. This can be because of two reasons:
						//1. An import statement is not there for this class eg: Classes of java.lang package
						//2. An import statement is given as .*: TODO: To handle this case later
						
						//Handling Case 1
						try
						{
							String packageName = Class.forName("java.lang." + typeName).getPackage().toString();
							if(packageName != null)
							{
								fullClassName = "java.lang." + typeName;
							}
						}
						catch(Exception ex)
						{
							//Handle as case 2: These variables belong to local packages having no info
							//So just consider its name as the full package name
							fullClassName = typeName;
						}
					}
				}
				else
				{
					fullClassName = typeName;
					return fullClassName;
				}
    		}	
			else if(typeObjP instanceof PrimitiveType) {
				PrimitiveType pmType = (PrimitiveType) typeObjP;
				fullClassName = pmType.getPrimitiveTypeCode().toString();
			}
			else if(typeObjP instanceof ArrayType) {
				Type origType = ((ArrayType)typeObjP).getComponentType();
				fullClassName = getFullClassName(origType);
			}
			else if(typeObjP instanceof ParameterizedType) {
				Type baseType = ((ParameterizedType)typeObjP).getType();
				fullClassName = getFullClassName(baseType);			
			}
    		
    		return fullClassName;
    	}
    	catch(Exception ex)
    	{
    		logger.info(ex.getStackTrace().toString());
    	}
    	return CommonConstants.getUniqueIDForUnknown();
    }
    
    public static TypeHolder getRefClassName(Expression exprNode)
    {
		//We need to check two cases. TODO: To handle both the cases 
		//1. When the method invocation is done on the source class we are interested
		//2. When the method invocation contains one of the parameters as required source class

    	/*
    	 *    ArrayAccess, ArrayCreation, ArrayInitializer, Assignment, BooleanLiteral, CastExpression, 
    	 *    CharacterLiteral, ClassInstanceCreation, ConditionalExpression, FieldAccess, InfixExpression, InstanceofExpression, 
    	 *    MethodInvocation, Name, NullLiteral, NumberLiteral, ParenthesizedExpression, PostfixExpression, PrefixExpression, 
    	 *    StringLiteral, SuperFieldAccess, SuperMethodInvocation, ThisExpression, TypeLiteral, VariableDeclarationExpression
    	 */
    	
    	TypeHolder thObj = ASTCrawlerUtil.unknownIDMapper.get(exprNode);
    	if(thObj != null)
    	{
    		ASTCrawlerUtil.unknownIDMapper.remove(exprNode);
    		return thObj;
    	}	
    	
    	thObj = new TypeHolder();
    	
    	//If the mode of operation i.e., CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT
    	//Resolve directly, because all this information would be available
    	if(CommonConstants.OPERATION_MODE == CommonConstants.MINE_PATTERNS_FROM_INPUTPROJECT || CommonConstants.OPERATION_MODE == CommonConstants.DETECT_BUGS_IN_INPUTPROJECT) {
    		if(exprNode == null) {
    			thObj.setType("this");
    		} else {
    			ITypeBinding exprTypeBinding = exprNode.resolveTypeBinding();
    			thObj.setType(exprTypeBinding.getQualifiedName());
    		}   		
    		
    		if(exprNode instanceof SimpleName) {
    			thObj.var = ((SimpleName)exprNode).getIdentifier();
    		}
    		
    		return thObj;   		
    	}
    	
    	
		if(exprNode == null || exprNode instanceof ThisExpression)
		{
			thObj.var = "this";
			thObj.type = "this";
			return thObj;
		}
		
		//exprNode is NULL if the method invocation is not associated with any object
		if(exprNode instanceof SimpleName)
		{
			SimpleName exprNodeName = (SimpleName) exprNode;
			String variableName = exprNodeName.getIdentifier();
			thObj.var = variableName;
			
			String fullClassName = (String) ASTCrawlerUtil.fieldDecls.get(variableName);
			thObj.type = fullClassName;
			if(fullClassName != null)
				return thObj;

			//Case where a static method is invoked, in which the expression itself is a class name
			//Eg: Math.sqrt(double) Here an assumption can be worked as in general the variable names start with small letters, 
			//even though this is not true in most of the cases
			
            if ((fullClassName == null) && (variableName != null) && (variableName.substring(0,1).toUpperCase().equals(variableName.substring(0,1)))) 
            {
            	//the reference is a class name
            	fullClassName = (String) ASTCrawlerUtil.fullPackageNamesForClasses.get(variableName);
            	
            	if(fullClassName == null)
            		fullClassName = variableName;
            	ASTCrawlerUtil.fieldDecls.put(variableName, fullClassName);
            }
            else
            {
            	//logger.info("Unhandled case _UNKNOWN_ while getting reference type " + exprNode);
            	fullClassName = CommonConstants.getUniqueIDForUnknown();
            }

            thObj.type = fullClassName;
			return thObj;
			
		}
		else if(exprNode instanceof QualifiedName)
		{
			QualifiedName qn = (QualifiedName) exprNode;
			
			//In case of System.out return System.out itself
			if(qn.toString().equals("System.out"))
			{
				thObj.type = "System.out";
				return thObj;
			}
			else
			{
				//Check in case the Qualified Name is already existing in full package names like javax.jms.Session
				String qualName = qn.toString();
				String partName = qualName.substring(qualName.lastIndexOf(".") + 1, qualName.length());
				String fullPackageName = fullPackageNamesForClasses.get(partName);
				if(fullPackageName != null)
				{
					thObj.type = fullPackageName;
					return thObj;
				}
			}
			
			TypeHolder th1 = getRefClassName(qn.getQualifier());
			TypeHolder th2 = getRefClassName(qn.getName()); 
			
			thObj.type = th1.type + "." + th2.type;
			return thObj;
		}
		else if(exprNode instanceof MethodInvocation)
		{
			//1. It can be a method invocation, which may occur in the case while invoking method2 of "obj.method1().method2()"
			//As it is impossible to calculate return type of this kind, we return UNDEFINED.
			
			//If this method invocation is in the current class, then the return type of this MI is going to
			//be the reference type
			MethodInvocation miObj = (MethodInvocation) exprNode;
			MethodDeclaration classMethodDeclObj = (MethodDeclaration) ASTCrawlerUtil.getMethodDeclNode(miObj.getName().getIdentifier(), miObj.arguments());
			
			if(classMethodDeclObj != null)
			{
				thObj.type = getFullClassName(classMethodDeclObj.getReturnType2());
				return thObj; 
			}
			
			return thObj;
		}
		else if(exprNode instanceof TypeLiteral)
		{
			//Case when  method invocation is static inside the class
			Type type = ((TypeLiteral)exprNode).getType();
			if(type instanceof SimpleType)
			{
				String refType = getFullClassName((SimpleType)type);
				if(refType != null)
				{
					thObj.type = refType;
					return thObj;
				}
			}
			else if(type instanceof PrimitiveType)
			{
				thObj.type = ((PrimitiveType)type).getPrimitiveTypeCode().toString();
				return thObj;
			}
			
		}
		else if(exprNode instanceof ParenthesizedExpression)
		{
			//Case when there is an explicit type cast in the place of expression
			//Eg: ((LinkedList)listObj)
			return getRefClassName(((ParenthesizedExpression)exprNode).getExpression());
		}
		else if(exprNode instanceof CastExpression)
		{
			thObj.type = getFullClassName(((CastExpression) exprNode).getType());
			return thObj;
		}
		else if(exprNode instanceof BooleanLiteral)
		{
			thObj.type = "boolean";
			return thObj;
		}
		else if(exprNode instanceof CharacterLiteral)
		{
			thObj.type = "char";
			return thObj;
		}
		else if(exprNode instanceof StringLiteral)
		{
			thObj.type = "java.lang.String"; 
			return thObj;
		}
		else if(exprNode instanceof NumberLiteral)
		{
			thObj.type = "int";
			return thObj;
		}
		else if(exprNode instanceof InfixExpression)
		{
			InfixExpression infEpr = (InfixExpression ) exprNode;
			return getRefClassName(infEpr.getLeftOperand());
		}
		else if(exprNode instanceof FieldAccess)
		{
			return getRefClassName(((FieldAccess)exprNode).getName());
		}
		else if(exprNode instanceof NullLiteral)
		{
			thObj.type = "null";
			return thObj;
		}
		else if(exprNode instanceof Assignment)
		{
			return getRefClassName(((Assignment)exprNode).getLeftHandSide());
		}
		else if(exprNode instanceof ClassInstanceCreation)
		{
			thObj.type = getFullClassName(((ClassInstanceCreation) exprNode).getType());
			return thObj;
		}
		else if(exprNode instanceof InstanceofExpression || exprNode instanceof ConditionalExpression)
		{
			thObj.type = "boolean"; 
			return thObj;
		}
		else if(exprNode instanceof PostfixExpression)
		{
			return getRefClassName(((PostfixExpression) exprNode).getOperand());
		}
		else if(exprNode instanceof PrefixExpression)
		{
			return getRefClassName(((PrefixExpression) exprNode).getOperand());
		}
		else if(exprNode instanceof SuperFieldAccess || exprNode instanceof SuperMethodInvocation)
		{
			//Nothing can be done
			return thObj;
		}
		else if(exprNode instanceof ArrayAccess)
		{
			return getRefClassName(((ArrayAccess) exprNode).getArray());
		}
		else if(exprNode instanceof ArrayCreation)
		{
			thObj.type = getFullClassName(((ArrayCreation)exprNode).getType()) + "[]";
			return thObj;
		}
		else
		{
			//TODO: What could be the other cases
			logger.error("Unhandled case in finding the reference type..." + exprNode);
		}
    	
		//Return N/A if it is not resolved to the exact class name
		//logger.info("Unhandled case _UNKNOWN_ while getting reference type " + exprNode);
    	return thObj;
    }
    
    /**
     * Function for getting the return type of a method invocation. This can be done only by identifying the type
     * of the variable to which this will be assigned
     * @param miObj
     * @return
     */
    public static TypeHolder getReturnType(ASTNode miObj)
    {
    	//The return type can be inferred in different ways depending on how the method invocation object is used in its parent
    	ASTNode parentObj = miObj.getParent();
    	TypeHolder thObj = new TypeHolder();
    	    	
    	if(parentObj instanceof ExpressionStatement)
    	{
        	//Case 1: Parent is an Expression statement --> Return type is void primitive type
        	//eg: obj.start()
    		thObj.type = "void";
    		thObj.var = "void";
    		return thObj;
    	}
    	else if(parentObj instanceof Assignment)
    	{
    		
    		//Case 2: Parent is an assignment statement --> Return type is the type of LHS
    		//eg: <Type> variable = obj.start();
    		
    		Assignment assignStmt = (Assignment) parentObj;
    		
			Expression leftHandSide = assignStmt.getLeftHandSide();
			Expression rightHandSide = assignStmt.getRightHandSide();
			
			Expression exprToEval = (rightHandSide == miObj)? leftHandSide : rightHandSide;
			return getRefClassName(exprToEval);
    	}
    	else if(parentObj instanceof MethodInvocation)
    	{
    		//Case 3 and 4: Parent is a method invocation
    		//Eg: object1.method(<OurMethodInvocationObject>.method1()) or <OurMethodInvocationObject>.method1().method2();
    		
    		MethodInvocation parentMI = (MethodInvocation) parentObj;
    		if(parentMI.arguments().contains(miObj))
    		{
    			//Case 3: Current method invocation is an argument of its parent method invocation
    			
    			//It is possible to get the return type in this case only when the parent method invocation
    			//is a local method invocation, means its expression is either this or null. Instead it is also from a different class whose signature not available
    			//nothing can be done
    			
    			Expression parentMIExpr = (Expression) parentMI.getExpression();

    			if(parentMIExpr != null || !(parentMIExpr instanceof ThisExpression) )
    			{
    				return thObj;
    			}
    			
    			//TODO;
    			logger.info("Case 3 to be handled while getting the return type");
    		}
    		else if(parentMI.getExpression() == miObj)
    		{
    			//Case 4: Current method invocation is a pre method call of parent method invocation
    			//As this case cannot be handled, reference type is considered as UNDEFINED
    			return thObj;
    		}
    		else
    		{
    			logger.info("Unhandled case while calculating the return type of method invocation in which parent is method invocation");
    			return thObj;
    		}
    		
    	}
    	else if(parentObj instanceof InfixExpression)
    	{
    		//Case 5: When current method invocation is a part of an expression
    		//eg: logger.info("" + object1.method())
    		thObj.type = "String";
    		return thObj;
    	}
    	else if(parentObj instanceof ReturnStatement)
    	{
    		//Case 6: Current method invocation is a part of return statement
    		if(ASTCrawler.currentMethodDeclaration == null)
    		{
    			logger.error("Error: Current method declaration cannot be null while parsing a return statement");
    		}
    		
    		Type retType = ASTCrawler.currentMethodDeclaration.getReturnType2();
    		thObj.type = getFullClassName(retType);
    		return thObj;
    	}
    	else if(parentObj instanceof VariableDeclarationFragment)
    	{
    		VariableDeclarationFragment vdfObj = (VariableDeclarationFragment) parentObj;
    		String returnType = (String) ASTCrawlerUtil.fieldDecls.get(vdfObj.getName().getIdentifier());
    		thObj.var = vdfObj.getName().getIdentifier();
    		if(returnType != null)
    		{
    			thObj.type = returnType;
    			return thObj;
    		}
    		else
    		{
    			logger.error("Unhandled case _UNKNOWN_ while getting return type of VariableDeclarationFragment");
    			return thObj;
    		}    		
    	}
    	else if(parentObj instanceof IfStatement)
    	{
    		//Case when method invocation is used inside IfStatement conditional like If(obj.method())
    		Object condition = ((IfStatement)parentObj).getExpression();
    		if(condition == miObj)
    		{
    			thObj.type = "boolean";
    			return thObj;
    		}
    		else
    		{
    			logger.info("Unhandled case _UNKNOWN_ while condition of IF statement");
    			return thObj;
    		}
    	}
    	else if(parentObj instanceof WhileStatement)
    	{
    		//Case when method invocation is used inside IfStatement conditional like While(obj.method())
    		Object condition = ((WhileStatement)parentObj).getExpression();
    		if(condition == miObj)
    		{
    			thObj.type = "boolean";
    			return thObj;
    		}
    		else
    		{
    			logger.info("Unhandled case _UNKNOWN_ while condition of WHILE statement");
    			return thObj;
    		}
    	}
    	else if(parentObj instanceof CastExpression)
    	{
    		//Happens when there is a type cast immediately after the method invocation
    		TypeHolder retType = getRefClassName((CastExpression) parentObj);
    		if(retType.var.startsWith(CommonConstants.unknownType)) {
    			TypeHolder parentToParentType = getReturnType(parentObj);
    			if(!parentToParentType.var.startsWith(CommonConstants.unknownType))
    				retType.var = parentToParentType.var;
    		}
    		
    		return retType;
    	}
    	else if(parentObj instanceof PrefixExpression)
    	{
    		thObj.type = "boolean";
    		return thObj;
    	}
    	else
    	{
    		//TODO: Any cases left to be handled???
    	}
    	
    	return thObj;
    }  
    
    
    /**
     * Function for getting the run-time type of a variable.
     * This function uses few heuristics to gather type of a variable at runtime. If
     * it fails, it just returns the static type of the variable.
     * This can be used when an assignment statement with a variable declaration is encountered on left hand side
     * 
     * @bResult : Set to success if it finds the runtime type successfully.
     */
    public static String getRuntimeType(Expression exprVar, Boolean bResult)
    {
     	return "";
    }
    
    
 
	/**
	 * Function for getting the ASTNode of the method declaration given a method name and ASTNode of root class
	 * This handles method overloading also
	 */
    public static ASTNode getMethodDeclNode(String methodName, List arguments)
    {
    	for(int i = 0; i < ASTCrawlerUtil.listOfMethodDecl.size(); i++)
    	{
    		MethodDeclaration mdObj = ASTCrawlerUtil.listOfMethodDecl.get(i);
    		
    		if(mdObj.getName().getIdentifier().equals(methodName))
    		{
    			List paramList = mdObj.parameters();
    			
    			if(paramList.size() != arguments.size())
    				continue;
    			
    			Iterator argIter = arguments.iterator();
    			Iterator paramIter = paramList.iterator();
    			
    			boolean bSame = true;
    			for(;argIter.hasNext();)
    			{
    				Expression expr = (Expression) argIter.next();
    				String argClsName = (ASTCrawlerUtil.getRefClassName(expr)).type;
    				
    				String paramClsName = ASTCrawlerUtil.getFullClassName(((SingleVariableDeclaration)paramIter.next()).getType());
    				
    				if(argClsName.equals("#UNKNOWN#") || paramClsName.equals("#UNKNOWN#"))
    					continue;
    				
    				
    				if(argClsName.equals(paramClsName))
    					continue;
    				
    				//Some times, in case of constants, the right most part will be in capitals,
    				//which indicates it is a constant. Eg: TestRunListener.STATUS_ERROR
    				String partName = argClsName.substring(argClsName.lastIndexOf(".") + 1, argClsName.length());
    				boolean bAllUpperCase = true;    				
    				
    				for(char ch : partName.toCharArray())
    				{
    					if(ch >= 'a' && ch <= 'z')
    					{
    						bAllUpperCase = false;
    					}
    				}
    				
    				if(bAllUpperCase)
    					continue;
    				
    				bSame = false;
    				break;    					
    			}
    			
    			if(bSame)	
    				return mdObj;
    			
    		}
    		
    	}
    	return null;
    }
	
    
    /**
     * Function that loads entire information of classes, its methods and fields in to local memory
     * These are required for method inlining, variable look ahead and all.
     */
    public static void preProcessClassDecl(TypeDeclaration root)
    {
    	ASTCrawlerUtil.listOfClassDecl.add(root);
    	
    	//Add all fields to the field declarations
    	FieldDeclaration fd[] = root.getFields();
    	
    	for(int counter = 0; counter < fd.length; counter++)
    	{	
			Type type = fd[counter].getType();
			String fullClassName = "N/A";
			fullClassName = ASTCrawlerUtil.getFullClassName(type);
			
			
			for(Iterator iter = fd[counter].fragments().iterator(); iter.hasNext();)
			{
				VariableDeclarationFragment vdfObj = (VariableDeclarationFragment) iter.next();
				ASTCrawlerUtil.fieldDecls.put(vdfObj.getName().getIdentifier(), fullClassName);
			}
    	}	

    	
    	//Add all method declarations
    	MethodDeclaration mdArr[] = root.getMethods();
    	for(int i = 0; i < mdArr.length; i++)
    	{
    		ASTCrawlerUtil.listOfMethodDecl.add(mdArr[i]);
    	}	
    	
    	
    	//Repeat this for each inner class declaration
    	TypeDeclaration innerCls[] = root.getTypes();
    	for(int counter = 0; counter < innerCls.length; counter++)
    	{
    		preProcessClassDecl(innerCls[counter]);
    	}
    }
    
    public static boolean isPrimitiveType (String type) {
    	if(type.equals("int") || type.equals("float") || type.equals("double") || type.equals("char")
    			|| type.equals("boolean") || type.equals("java.lang.String"))
    	{
    		return true;
    	} 
 
    	return false;
    }
    
    /**
     * Function for checking a whether given exception is run-time or not
     */
    public static boolean isARunTimeException(String exceptionName, String optParentName) {
    	String exceptionPart = exceptionName;
    	
    	if(exceptionName.indexOf(".") != -1)
    		exceptionPart = exceptionName.substring(exceptionName.lastIndexOf(".") + 1, exceptionName.length());
    	
    	Boolean bVal = runtimeExceptionMap.get(exceptionPart);
    	
    	if(bVal != null)
    		return bVal.booleanValue();
    	
    	if(optParentName != null) {
    		if(optParentName.indexOf(".") != -1)
    			optParentName = optParentName.substring(optParentName.lastIndexOf(".") + 1, optParentName.length());
	    	bVal = runtimeExceptionMap.get(optParentName);
	    	if(bVal != null) {
	    		runtimeExceptionMap.put(exceptionPart, bVal);
	    		return bVal.booleanValue();
	    	} 
    	}  	
    	
    	runtimeExceptionMap.put(exceptionPart, new Boolean(false));
    	return false;
    	 	
    }
    
    //Loading runtime exceptions statically
    static {
        String runTimeList[] = new String[] {
        		"Exception",
        		"AnnotationTypeMismatchException", 
        		"ArithmeticException", 
        		"ArrayStoreException", 
        		"BufferOverflowException", 
        		"BufferUnderflowException", 
        		"CannotRedoException", 
        		"CannotUndoException", 
        		"ClassCastException", 
        		"CMMException", 
        		"ConcurrentModificationException", 
        		"DOMException", 
        		"EmptyStackException", 
        		"EnumConstantNotPresentException", 
        		"EventException", 
        		"IllegalArgumentException", 
        		"IllegalMonitorStateException", 
        		"IllegalPathStateException", 
        		"IllegalStateException", 
        		"ImagingOpException", 
        		"IncompleteAnnotationException", 
        		"IndexOutOfBoundsException", 
        		"JMRuntimeException", 
        		"LSException", 
        		"MalformedParameterizedTypeException", 
        		"MissingResourceException", 
        		"NegativeArraySizeException", 
        		"NoSuchElementException", 
        		"NullPointerException", 
        		"ProfileDataException", 
        		"ProviderException", 
        		"RasterFormatException", 
        		"RejectedExecutionException", 
        		"SecurityException", 
        		"SystemException", 
        		"TypeNotPresentException", 
        		"UndeclaredThrowableException", 
        		"UnmodifiableSetException", 
        		"UnsupportedOperationException",
        		"RuntimeException"
        };
        
        
        for(String exceptStr : runTimeList) {
        	runtimeExceptionMap.put(exceptStr, new Boolean(true));       	
        }        
    }
    
    
    
    /**
     * A recursive routine for identifying all condition variables in a given condition expression.
     * Apart from identifying the condition variables, the function also identifies the type 
     * of check performed on each condition variable and stores additional informaiton
     * @param condExpr
     * @param condVarList
     */
    public static void loadConditionalVariables(Expression condExpr, List<CondVarHolder> condVarList) 
    {
    	//Not required for XWeb
    	if(true)
    		return;
    	
    	//Handling null conditional expressions like "for(i = 0;;i++)" or infinite loops like "while(true)"
    	if(condExpr == null || condExpr instanceof BooleanLiteral)
    		return;
    	
    	if(condExpr instanceof PrefixExpression)
		{
    		//Negative variables are treated as prefix expressions. So in case
    		//of negative variables, ignore them.
    		if(condExpr.toString().startsWith("-")) {
    			
    		} else {
    			loadConditionalVariables(((PrefixExpression)condExpr).getOperand(), condVarList);
    		}	
		} 
		else if (condExpr instanceof InfixExpression)
		{
			InfixExpression infiExpr = (InfixExpression) condExpr;
			Expression leftExpr = infiExpr.getLeftOperand();
			Expression rightExpr = infiExpr.getRightOperand();
			Operator operator = infiExpr.getOperator();
			
			//Eliminate paranthesized expressions through While loop
			while(leftExpr instanceof ParenthesizedExpression) {
				leftExpr = ((ParenthesizedExpression) leftExpr).getExpression();
			}
					
			if(leftExpr instanceof InfixExpression || leftExpr instanceof PrefixExpression) {
				loadConditionalVariables(leftExpr, condVarList);
			} else {
				addSpecificCondVar(leftExpr, rightExpr, condVarList, -1, operator);
			}
			
			//Eliminate paranthesized expressions through While loop
			while(rightExpr instanceof ParenthesizedExpression) {
				rightExpr = ((ParenthesizedExpression) rightExpr).getExpression();
			}
			
			if(rightExpr instanceof InfixExpression || rightExpr instanceof PrefixExpression) {
				loadConditionalVariables(rightExpr, condVarList);
			} else {
				addSpecificCondVar(rightExpr, leftExpr, condVarList, -1, operator);
			}
		}
		else if (condExpr instanceof ParenthesizedExpression)
		{
			Expression subExpr = ((ParenthesizedExpression) condExpr).getExpression();
			loadConditionalVariables(subExpr, condVarList);
		} 
		else if (condExpr instanceof SimpleName) 
		{
			addSpecificCondVar(condExpr, null, condVarList, -1, null);
		}
		else if (condExpr instanceof MethodInvocation)
		{
			TypeHolder thObj = ASTCrawlerUtil.unknownIDMapper.get(condExpr);
			if(thObj != null)
	    	{
	    		ASTCrawlerUtil.unknownIDMapper.remove(condExpr);
	    		CondVarHolder cvh = new CondVarHolder(thObj.var, CondVarHolder.TRUE_FALSE_CHECK);
	    		condVarList.add(cvh);
	    	}
		}
		else if (condExpr instanceof InstanceofExpression)
		{
			InstanceofExpression ieExpr = (InstanceofExpression) condExpr;
			CondVarHolder cvh = new CondVarHolder(ieExpr.getLeftOperand().toString(), CondVarHolder.INSTANCEARG_TRUE_FALSE_CHECK);
			cvh.setConstValue(getFullClassName(ieExpr.getRightOperand()));
			condVarList.add(cvh);
		}
		else if (condExpr instanceof Assignment)
		{
			Assignment assignSt = (Assignment) condExpr;
			addSpecificCondVar((Expression)assignSt.getLeftHandSide(), null, condVarList, -1, null);
		}
		else {
			logger.error("Unhandled case while identifying conditional variables " + condExpr);
		}
	}
    
   
    
    /**
     * Routine for adding specific condition variable to the given list.
     * The parameter "otherOperand" is used to compute specific type from the generic type 
     * passed to the function.
     * @param operandToDeal
     * @param otherOperand
     * @param condVarList
     * @param bCheckType
     */
    private static void addSpecificCondVar(Expression operandToDeal, Expression otherOperand, 
    		List<CondVarHolder> condVarList, int bSubCheckType, Operator operator) {
    
    	//Ignore if this operand is a literal or null value.
    	if(operandToDeal instanceof BooleanLiteral || operandToDeal instanceof CharacterLiteral || operandToDeal instanceof StringLiteral
    			|| operandToDeal instanceof NumberLiteral  || operandToDeal instanceof NullLiteral || operandToDeal instanceof ThisExpression
    			|| operandToDeal instanceof TypeLiteral) {
    		return;
    	}
    	
    	//Loading variables
    	if(operandToDeal instanceof SimpleName) {
    		CondVarHolder cvh = new CondVarHolder();
    		cvh.setVarName(((SimpleName) operandToDeal).getIdentifier());
    		setConditionType(otherOperand, bSubCheckType, cvh, operator);
        	condVarList.add(cvh);
    	} else if(operandToDeal instanceof MethodInvocation) {
    		MethodInvocation miObj = (MethodInvocation) operandToDeal;
    		
    		//Here directly or indirectly both method invocation and its parameters are
    		//participating in the conditional statement.
    		TypeHolder thObj = ASTCrawlerUtil.unknownIDMapper.get(miObj);
        	if(thObj != null)
        	{
        		ASTCrawlerUtil.unknownIDMapper.remove(miObj);
        		CondVarHolder mcvh = new CondVarHolder();
        		mcvh.setVarName(thObj.var);
        		setConditionType(otherOperand, bSubCheckType, mcvh, operator);
        		condVarList.add(mcvh);
        	}
        	else
        	{
        		logger.error("Unhandled case in identifying method invocations in condition expr: " + miObj);
        	}
    		
    		List argumentList = miObj.arguments();
    		for(Iterator iter = argumentList.iterator(); iter.hasNext();) {
    			Expression argExpr = (Expression) iter.next();
    			addSpecificCondVar(argExpr, otherOperand, condVarList, CondVarHolder.METHODARG, operator);
    		}
    	} 
		else if (operandToDeal instanceof InstanceofExpression)
		{
			InstanceofExpression ieExpr = (InstanceofExpression) operandToDeal;
			CondVarHolder cvhIE = new CondVarHolder(ieExpr.getLeftOperand().toString(), CondVarHolder.INSTANCEARG_TRUE_FALSE_CHECK);
			cvhIE.setConstValue(getFullClassName(ieExpr.getRightOperand()));
			condVarList.add(cvhIE);
		}  
		else if (operandToDeal instanceof QualifiedName)
		{
			//If the last part is made with capitals, ignore it as it is assumed as a constant (Heuristic)
			String lastPart = operandToDeal.toString().substring(operandToDeal.toString().lastIndexOf(".") + 1);
			if(lastPart.toUpperCase().equals(lastPart)) {
    			return;    			
			} else if (lastPart.trim().equals("length")) {
				//This is a common case where for arrays, the length is checked before accessing
				//them. 
			} else if (lastPart.trim().equals("class")) {
				//Nothing to be done in this case
			}
			else {
				logger.error("Unhandled case in addSpecificCondVar " + operandToDeal);
			}
		} 
		else if (operandToDeal instanceof FieldAccess)
		{
			addSpecificCondVar(((FieldAccess)operandToDeal).getName(), otherOperand, condVarList, bSubCheckType, operator);
		}
		else if (operandToDeal instanceof ArrayAccess)
		{
			ArrayAccess aaObj = (ArrayAccess) operandToDeal;
			addSpecificCondVar(aaObj.getArray(), otherOperand, condVarList, bSubCheckType, operator);
		}
		else if (operandToDeal instanceof Assignment)
		{
			Assignment assignSt = (Assignment) operandToDeal;
			addSpecificCondVar((Expression)assignSt.getLeftHandSide(), otherOperand, condVarList, bSubCheckType, operator);
		}
    	else {
    		logger.error("Unhandled case in addSpecificCondVar " + operandToDeal);
    	}    	
    }
    
    private static void setConditionType(Expression otherOperand, int bSubCheckType, CondVarHolder cvh, Operator operator) {
    	if(bSubCheckType == CondVarHolder.METHODARG) {
    		if(otherOperand == null) {
    			cvh.setCondType(CondVarHolder.METHODARG_TRUE_FALSE_CHECK);
    			cvh.setConstValue("true"); 
    		} else if(otherOperand instanceof NullLiteral) {
    			cvh.setCondType(CondVarHolder.METHODARG_NULL_CHECK);
    		} else if(otherOperand instanceof BooleanLiteral) {
    			cvh.setCondType(CondVarHolder.METHODARG_TRUE_FALSE_CHECK);
    			cvh.setConstValue(((BooleanLiteral)otherOperand).toString());
    		} else if(otherOperand instanceof MethodInvocation) {
    			cvh.setCondType(CondVarHolder.METHODARG_RETVAL_EQUALITY_CHECK);
    			cvh.setOtherMIiObj((MethodInvocation)otherOperand);
    		} else if(otherOperand instanceof CharacterLiteral || 
    				otherOperand instanceof StringLiteral || otherOperand instanceof NumberLiteral) {
    			cvh.setCondType(CondVarHolder.METHODARG_CONSTANT_EQUALITY_CHECK);
    			cvh.setConstValue(otherOperand.toString());
    		} else if(otherOperand instanceof QualifiedName) {
    			//Treat the Qualified names as constants if the last part is made with capital letters only
    			String lastPart = otherOperand.toString().substring(otherOperand.toString().lastIndexOf(".") + 1);
    			if(lastPart.toUpperCase().equals(lastPart))	{
        			cvh.setCondType(CondVarHolder.METHODARG_CONSTANT_EQUALITY_CHECK);        			
    			} else if(lastPart.equals("class")) {
    				cvh.setCondType(CondVarHolder.METHODARG_CLASS_EQUIVALENCE_CHECK);
    			} else {
    				cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    			}    			
    			cvh.setConstValue(otherOperand.toString());
    		} else if(otherOperand instanceof SimpleName || otherOperand instanceof ThisExpression || otherOperand instanceof FieldAccess || otherOperand instanceof ArrayAccess) {
    			cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    		} else if(otherOperand instanceof PrefixExpression && otherOperand.toString().startsWith("-")) {
    			cvh.setCondType(CondVarHolder.METHODARG_CONSTANT_EQUALITY_CHECK);
    		} else if(otherOperand instanceof InfixExpression || otherOperand instanceof PrefixExpression || otherOperand instanceof ParenthesizedExpression) {
    			cvh.setCondType(CondVarHolder.METHODARG_TRUE_FALSE_CHECK);
    		} else if(otherOperand instanceof TypeLiteral) {
    			cvh.setCondType(CondVarHolder.METHODARG_CLASS_EQUIVALENCE_CHECK);
    		} else if (operator == Operator.CONDITIONAL_AND || operator == Operator.CONDITIONAL_OR) {
    			//If the type of check is failed to be identified through otherOperand, operator is used for it
    			cvh.setCondType(CondVarHolder.METHODARG_TRUE_FALSE_CHECK);
    		} else if (operator == Operator.EQUALS || operator == Operator.GREATER || operator == Operator.LESS 
    				|| operator == Operator.GREATER_EQUALS || operator == Operator.LESS_EQUALS) {
    			cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    		} else {
    			logger.error("Unhandled case in identifying method arg type of condition variables " + otherOperand);
    		}
    	} else {
    		if(otherOperand == null) {
    			cvh.setCondType(CondVarHolder.TRUE_FALSE_CHECK);
    			cvh.setConstValue("true"); 
    		} else if(otherOperand instanceof NullLiteral) {
    			cvh.setCondType(CondVarHolder.NULL_CHECK);
    		} else if(otherOperand instanceof BooleanLiteral) {
    			cvh.setCondType(CondVarHolder.TRUE_FALSE_CHECK);
    			cvh.setConstValue(((BooleanLiteral)otherOperand).toString());
    		} else if(otherOperand instanceof MethodInvocation) {
    			cvh.setCondType(CondVarHolder.RETVAL_EQUALITY_CHECK);
    			cvh.setOtherMIiObj((MethodInvocation)otherOperand);
    		} else if(otherOperand instanceof CharacterLiteral || 
    				otherOperand instanceof StringLiteral || otherOperand instanceof NumberLiteral) {
    			cvh.setCondType(CondVarHolder.CONSTANT_EQUALITY_CHECK);
    			cvh.setConstValue(otherOperand.toString());
    		} else if(otherOperand instanceof QualifiedName) {
    			//Treat the Qualified names as constants if the last part is made with capital letters only
    			String lastPart = otherOperand.toString().substring(otherOperand.toString().lastIndexOf(".") + 1);
    			if(lastPart.toUpperCase().equals(lastPart)) {
        			cvh.setCondType(CondVarHolder.CONSTANT_EQUALITY_CHECK);        			
    			} else if(lastPart.equals("class")) {
    				cvh.setCondType(CondVarHolder.CLASS_EQUIVALENCE_CHECK);
    			} else {
    				cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    			}    			
    			cvh.setConstValue(otherOperand.toString());
    		} else if(otherOperand instanceof SimpleName || otherOperand instanceof ThisExpression || otherOperand instanceof FieldAccess || otherOperand instanceof ArrayAccess) {
    			cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    		} else if(otherOperand instanceof PrefixExpression && otherOperand.toString().startsWith("-")) {
    			cvh.setCondType(CondVarHolder.CONSTANT_EQUALITY_CHECK);
    		} else if(otherOperand instanceof InfixExpression || otherOperand instanceof PrefixExpression || otherOperand instanceof ParenthesizedExpression) {
    			cvh.setCondType(CondVarHolder.TRUE_FALSE_CHECK);
    		} else if(otherOperand instanceof TypeLiteral) {
    			cvh.setCondType(CondVarHolder.CLASS_EQUIVALENCE_CHECK);
    		} else if (operator == Operator.CONDITIONAL_AND || operator == Operator.CONDITIONAL_OR) {
    			//If the type of check is failed to be identified through otherOperand, operator is used for it
    			cvh.setCondType(CondVarHolder.METHODARG_TRUE_FALSE_CHECK);
    		} else if (operator == Operator.EQUALS || operator == Operator.GREATER || operator == Operator.LESS 
    				|| operator == Operator.GREATER_EQUALS || operator == Operator.LESS_EQUALS) {
    			cvh.setCondType(CondVarHolder.GEN_EQUALITY_CHECK);
    		}
    		else {
    			logger.error("Unhandled case in identifying type of condition variables " + otherOperand);
    		}
    	}
    }   
    
    /**
     * XWeb specific method. This method is common for both MethodInvocationHolder and LibMethodHolder.
     */
    public static void addToErrorPathAPIs(Holder assocLibHolder, List<IntHolder> errorMIFrequency, List<MethodInvocationHolder> errorMIHList,
    		List<CodeExampleStore> codeExamples, List<MIHList> associatedPatternContext, MethodInvocationHolder errorMIH, 
    		String fileName, String methodName, List<MethodInvocationHolder> contextList) {
    	
    	if(ignoreErrorMIH(assocLibHolder, errorMIH)) {
    		return;
    	}
    	
    	if(!CommonConstants.ENABLE_ASSOC_MINER) {
	    	//A previous strategy where the approach is based on simple counting
    		//Check whether it is alread existing
			Iterator freqIter = errorMIFrequency.iterator();
			Iterator pcIter = associatedPatternContext.iterator();
			boolean bAlreadyAdded = false;
			for(MethodInvocationHolder existingErrorMIH : errorMIHList) {
				IntHolder existFreq = (IntHolder)freqIter.next();
				MIHList pcObj = (MIHList) pcIter.next();
				if(existingErrorMIH.equals(errorMIH)) {
					if (pcObj.equalsToList(contextList)) {
						existFreq.incrValue();
						bAlreadyAdded = true;
						break;
					}
				}		
			}
			
			if(!bAlreadyAdded) {
				errorMIHList.add(errorMIH);
				errorMIFrequency.add(new IntHolder(1));
				MIHList pcObj = new MIHList(contextList);
				associatedPatternContext.add(pcObj);
				CodeExampleStore sst = new CodeExampleStore();
				sst.javaFileName = fileName;
				sst.methodName = methodName;
				codeExamples.add(sst);
			}
    	} 
    }
    
    /**
     * Heuristics used in our approach to ignore irrelevant method invocations
     * @return
     */
    public static boolean ignoreErrorMIH(Holder assocLibHolder, MethodInvocationHolder errorMIH) {
    	//Heuristic : If the errorMIH is a get method or has unknown
    	//types in the receiver type or return type, ignore it. In future work, we can 
    	//check how to explore that case.
    	if(errorMIH.getMethodName().startsWith("get") 
    			|| errorMIH.getReceiverClass().type.startsWith(CommonConstants.unknownType)
    			|| errorMIH.getReturnType().type.startsWith(CommonConstants.unknownType)) {
    		return true;
    	}
    	
    	//Another heuristic is that if the resource-manipulation action is a 
    	//getMethod and throws only NULLPointerException. This is based
    	//on our observation that Jex conservatively identified such methods also 
    	//throw NullPointerException.
    	String containedException = "";
    	if(assocLibHolder instanceof LibMethodHolder) {
    		LibMethodHolder assocLibMih = (LibMethodHolder) assocLibHolder;
    		Set<String> exceptionSet = assocLibMih.getExceptionSet();
    		if(exceptionSet.size() == 1) {
    			containedException = exceptionSet.iterator().next(); 
    		}
    		if(assocLibMih.getName().startsWith("get") && containedException.equals("java.lang.NullPointerException")) {
    			return true;
    		}
    	} else if(assocLibHolder instanceof MethodInvocationHolder) {
    		MethodInvocationHolder assocExtMih = (MethodInvocationHolder) assocLibHolder;
    		Set<String> exceptionSet = assocExtMih.getExceptionSet();
    		if(exceptionSet.size() == 1) {
    			containedException = exceptionSet.iterator().next(); 
    		}
    		if(assocExtMih.getMethodName().startsWith("get") && containedException.equals("java.lang.NullPointerException")) {
    			return true;
    		}
    	}    	
    	//End of heuristics while adding rule candidates lib methods
    	
    	return false;
    }
    
    /*********** Code of Association Rule Mining data gathering *************/
    
    /**
     * A function specific to the association rule miner for mining
     */
    public static void assignKeyToMethodInvocations(MethodInvocationHolder mihObj) 
    {
    	RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
    	
    	//Lookup the Id if the mih is already visited

    	List<MethodInvocationHolder> existingMIHList = raObj.getMihIDMap().get(mihObj.getReceiverClass().type);
    	boolean bObjFound = false;
    	if(existingMIHList != null) {
    		for(MethodInvocationHolder existMIH : existingMIHList) {
    			if(existMIH.equals(mihObj)) {
    				mihObj.setKey(existMIH.getKey());
    				bObjFound = true;
    				break;
    			}
    		}
    		
    		if(!bObjFound) {
	    		mihObj.setKey(MethodInvocationHolder.MIHKEYGEN++);	    		
	    		existingMIHList.add(mihObj);
    		}	
    	} else {
    		mihObj.setKey(MethodInvocationHolder.MIHKEYGEN++);
    		existingMIHList = new ArrayList<MethodInvocationHolder>(3);
    		existingMIHList.add(mihObj);
    		raObj.getMihIDMap().put(mihObj.getReceiverClass().type, existingMIHList);    		
    	}    	
    }
    
    
    /********* End of code specific for gathering assoc rule miner ************/
}
