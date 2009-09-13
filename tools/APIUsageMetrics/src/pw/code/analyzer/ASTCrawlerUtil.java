package pw.code.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;


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
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeLiteral;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;
import pw.common.CommonConstants;
import org.eclipse.jdt.core.dom.ParameterizedType;

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

	
    //Variables for storing the scope of the variable
    static protected int CLASS_SCOPE = 1;
    static protected int INNER_CLASS_SCOPE = 2;
    static protected int BLOCK_SCOPE = 3;

    /********* Begin of private functions for handling minor aspects  ****************/
    public static String getFullClassName(Type typeObjP)
    {
    	try
    	{
    		String fullClassName = CommonConstants.unknownType;
    		if(typeObjP instanceof SimpleType)
    		{	
    			SimpleType typeObj =  (SimpleType) typeObjP;
				String typeName = typeObj.getName().getFullyQualifiedName(); 
					//TODO: To check this, there can be a problem here
				
				if(!typeName.contains("."))
				{
					fullClassName = (String) ASTCrawlerUtil.fullPackageNamesForClasses.get(typeName);
					
					if(fullClassName == null)
					{
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
			else if(typeObjP instanceof PrimitiveType)
			{
				PrimitiveType pmType = (PrimitiveType) typeObjP;
				fullClassName = pmType.getPrimitiveTypeCode().toString();
			}
			else if(typeObjP instanceof ArrayType)
			{
				Type origType = ((ArrayType)typeObjP).getComponentType();
				fullClassName = getFullClassName(origType);
			}
			else if(typeObjP instanceof ParameterizedType)
			{
				Type baseType = ((ParameterizedType)typeObjP).getType();
				fullClassName = getFullClassName(baseType);			
			}
    		
    		return fullClassName;
    	}
    	catch(Exception ex)
    	{
    		logger.info(ex.getStackTrace().toString());
    	}
    	return CommonConstants.unknownType;
    }
    
    public static String getRefClassName(Expression exprNode)
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
    	
    	
		if(exprNode == null || exprNode instanceof ThisExpression)
		{
			return "this";
		}
		
		
		//exprNode is NULL if the method invocation is not associated with any object
		if(exprNode instanceof SimpleName)
		{
			SimpleName exprNodeName = (SimpleName) exprNode;
			String variableName = exprNodeName.getIdentifier();
			
			String fullClassName = (String) ASTCrawlerUtil.fieldDecls.get(variableName);
			if(fullClassName != null)
				return fullClassName;

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
            	fullClassName = CommonConstants.unknownType;
            }

			return fullClassName;
			
		}
		else if(exprNode instanceof QualifiedName)
		{
			QualifiedName qn = (QualifiedName) exprNode;
			
			//In case of System.out return System.out itself
			if(qn.toString().equals("System.out"))
			{
				return "System.out";
			}
			else
			{
				//Check in case the Qualified Name is already existing in full package names like javax.jms.Session
				String qualName = qn.toString();
				String partName = qualName.substring(qualName.lastIndexOf(".") + 1, qualName.length());
				String fullPackageName = fullPackageNamesForClasses.get(partName);
				if(fullPackageName != null)
				{
					return fullPackageName;
				}
			}
			
			return getRefClassName(qn.getQualifier()) + "." + getRefClassName(qn.getName());
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
				return getFullClassName(classMethodDeclObj.getReturnType2());
			}
			
			return CommonConstants.unknownType;
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
					return refType;
				}
			}
			else if(type instanceof PrimitiveType)
			{
				return ((PrimitiveType)type).getPrimitiveTypeCode().toString();
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
			return getFullClassName(((CastExpression) exprNode).getType());
		}
		else if(exprNode instanceof BooleanLiteral)
		{
			return "boolean";
		}
		else if(exprNode instanceof CharacterLiteral)
		{
			return "char";
		}
		else if(exprNode instanceof StringLiteral)
		{
			return "java.lang.String";
		}
		else if(exprNode instanceof NumberLiteral)
		{
			return "int";
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
			return "null";
		}
		else if(exprNode instanceof Assignment)
		{
			return getRefClassName(((Assignment)exprNode).getLeftHandSide());
		}
		else if(exprNode instanceof ClassInstanceCreation)
		{
			return getFullClassName(((ClassInstanceCreation) exprNode).getType());
		}
		else if(exprNode instanceof InstanceofExpression || exprNode instanceof ConditionalExpression)
		{
			return "boolean";
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
			return CommonConstants.unknownType;
		}
		else if(exprNode instanceof ArrayAccess)
		{
			return getRefClassName(((ArrayAccess) exprNode).getArray());
		}
		else if(exprNode instanceof ArrayCreation)
		{
			return getFullClassName(((ArrayCreation)exprNode).getType()) + "[]";
		}
		else
		{
			//TODO: What could be the other cases
			logger.error("Unhandled case in finding the reference type..." + exprNode);
		}
    	
		//Return N/A if it is not resolved to the exact class name
		//logger.info("Unhandled case _UNKNOWN_ while getting reference type " + exprNode);
    	return CommonConstants.unknownType;
    }
    
    /**
     * Function for getting the return type of a method invocation. This can be done only by identifying the type
     * of the variable to which this will be assigned
     * @param miObj
     * @return
     */
    public static String getReturnType(ASTNode miObj)
    {
    	//The return type can be inferred in different ways depending on how the method invocation object is used in its parent
    	ASTNode parentObj = miObj.getParent();
    	
    	
    	
    	if(parentObj instanceof ExpressionStatement)
    	{
        	//Case 1: Parent is an Expression statement --> Return type is void primitive type
        	//eg: obj.start()

    		return "void";
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
    				return CommonConstants.unknownType;
    			}
    			
    			logger.info("Case 3 to be handled while getting the return type");
    		}
    		else if(parentMI.getExpression() == miObj)
    		{
    			//Case 4: Current method invocation is a pre method call of parent method invocation
    			//As this case cannot be handled, reference type is considered as UNDEFINED
    			return CommonConstants.unknownType;
    		}
    		else
    		{
    			logger.info("Unhandled case while calculating the return type of method invocation in which parent is method invocation");
    			
    			return CommonConstants.unknownType;
    		}
    		
    	}
    	else if(parentObj instanceof InfixExpression)
    	{
    		//Case 5: When current method invocation is a part of an expression
    		//eg: logger.info("" + object1.method())
    		return "String";
    	}
    	else if(parentObj instanceof ReturnStatement)
    	{
    		//Case 6: Current method invocation is a part of return statement
    		if(ASTCrawler.currentMethodDeclaration == null)
    		{
    			logger.error("Error: Current method declaration cannot be null while parsing a return statement");
    		}
    		
    		Type retType = ASTCrawler.currentMethodDeclaration.getReturnType2();
    		return getFullClassName(retType);
    	}
    	else if(parentObj instanceof VariableDeclarationFragment)
    	{
    		VariableDeclarationFragment vdfObj = (VariableDeclarationFragment) parentObj;
    		String returnType = (String) ASTCrawlerUtil.fieldDecls.get(vdfObj.getName().getIdentifier());
    		if(returnType != null)
    		{
    			return returnType;
    		}
    		else
    		{
    			logger.info("Unhandled case _UNKNOWN_ while getting return type of VariableDeclarationFragment");
    			return CommonConstants.unknownType;
    		}
    		
    	}
    	else if(parentObj instanceof IfStatement)
    	{
    		//Case when method invocation is used inside IfStatement conditional like If(obj.method())
    		Object condition = ((IfStatement)parentObj).getExpression();
    		if(condition == miObj)
    		{
    			return "boolean";
    		}
    		else
    		{
    			logger.info("Unhandled case _UNKNOWN_ while condition of IF statement");
    			return CommonConstants.unknownType;
    		}
    	}
    	else if(parentObj instanceof WhileStatement)
    	{
    		//Case when method invocation is used inside IfStatement conditional like If(obj.method())
    		Object condition = ((WhileStatement)parentObj).getExpression();
    		if(condition == miObj)
    		{
    			return "boolean";
    		}
    		else
    		{
    			logger.info("Unhandled case _UNKNOWN_ while condition of WHILE statement");
    			return CommonConstants.unknownType;
    		}
    	}
    	else if(parentObj instanceof CastExpression)
    	{
    		//Happens when there is a type cast immediately after the method invocation
    		return getRefClassName((CastExpression) parentObj);
    	}
    	else if(parentObj instanceof PrefixExpression)
    	{
    		return "boolean";
    	}
    	else
    	{
    		//TODO: Any cases left to be handled???
    	}
    	
 
    	//logger.info("Unhandled case _UNKNOWN_ while getting return type");	
    	
    	return CommonConstants.unknownType;
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
    			
    			//TODO: THIS MAY BREAK PARSEWeb. THIS IS ONLY A TEMPORARY SOLUTION 
    			//NEED TO ALTER LATER
    			return mdObj;
    			
    			/*
    			Iterator argIter = arguments.iterator();
    			Iterator paramIter = paramList.iterator();
    			
    			boolean bSame = true;
    			for(;argIter.hasNext();)
    			{
    				Expression expr = (Expression) argIter.next();
    				String argClsName = ASTCrawlerUtil.getRefClassName(expr);
    				
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
    			*/	
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
    
    
    
}
