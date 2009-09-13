package pw.code.analyzer;

import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

import edu.uci.ics.jung.graph.impl.SimpleDirectedSparseVertex;

/**
 * Class for holding entire information of a method invocation.
 * This holds information:
 * 1. Reference class: (Class to which current method belongs to) "this" is also a valid value
 * 2. Return type: (Return type of current method)
 * 3. List of arguments with type of each argument
 * @author suresh_thummalapenta
 *
 */
public class MethodInvocationHolder extends SimpleDirectedSparseVertex implements StackObject {

	public static Logger logger = Logger.getLogger("MethodInvocationHolder");
	
	String referenceClsName;
	String returnType;
	String argumentString;
	String argStringAr[];
	String methodName;
	int noOfArguments;
	int key = MIHKEYGEN++;
	boolean bDowncast = false;
	
	public static int MIHKEYGEN = 0;
	
	
	public MethodInvocationHolder()
	{
		
	}
	
	/**
	 * Constructor for initializing MethodInvocationHolder from a CastExpression
	 * @param referenceClsName
	 * @param returnType
	 * @param ceObj
	 */
	public MethodInvocationHolder(String referenceClsName, String returnType, CastExpression ceObj)
	{
		super();
		
		this.referenceClsName = referenceClsName;
		this.returnType = returnType;
		this.methodName = "DOWNCAST";

		this.noOfArguments = 0;
		this.argumentString = "()"; 
		this.argStringAr = new String[0];
	}
	
	/**
	 * Constructor for handling new object creations as they also cause transformation
	 */
	public MethodInvocationHolder(String referenceClsName, String returnType, ClassInstanceCreation cicObj)
	{
		super();
		
		this.referenceClsName = referenceClsName;
		this.returnType = returnType;
		this.methodName = "CONSTRUCTOR";
		
		this.noOfArguments = 0;
		List methodArgList = cicObj.arguments();
		StringBuffer tmpArgStr = new StringBuffer("(");
		argStringAr = new String[methodArgList.size()];
		for(Iterator iter = methodArgList.iterator(); iter.hasNext();)
		{
			Expression expr = (Expression) iter.next();
			String refClsName = ASTCrawlerUtil.getRefClassName(expr); 
			tmpArgStr.append(refClsName + ",");
			argStringAr[noOfArguments] = refClsName; 
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}	

	}
	
	/**
	 * Constructor for initializing MethodInvocationHolder from MethodDeclaration object
	 * @param referenceClsName
	 * @param returnType
	 * @param miObj
	 */
	public MethodInvocationHolder(String referenceClsName, String returnType, MethodDeclaration miObj) 
	{
		super();
		setValuesForMeDecl(referenceClsName, returnType, miObj, true);
	}
	
	public MethodInvocationHolder(String referenceClsName, String returnType, MethodDeclaration miObj, boolean heuristicResolve) 
	{
		super();
		setValuesForMeDecl(referenceClsName, returnType, miObj, heuristicResolve);
	}

	
	private void setValuesForMeDecl(String referenceClsName, String returnType, MethodDeclaration miObj, boolean heuristicResolve)
	{
		
		this.referenceClsName = referenceClsName;
		this.returnType = returnType;
		
		if(miObj.isConstructor())
		{
			this.methodName = "CONSTRUCTOR";
		}
		else
		{	
			this.methodName = miObj.getName().getIdentifier();
		}	
			
		this.noOfArguments = 0;
		StringBuffer tmpArgStr = new StringBuffer("(");
		argStringAr = new String[miObj.parameters().size()];
		for(Iterator iter = miObj.parameters().iterator(); iter.hasNext();)
		{
			SingleVariableDeclaration expr = (SingleVariableDeclaration) iter.next();
			
			String refClsName = "";
			
			if(heuristicResolve)
				refClsName = ASTCrawlerUtil.getFullClassName(expr.getType());
			else
				refClsName = expr.getType().toString();
			
			tmpArgStr.append(refClsName + ",");
			argStringAr[noOfArguments] = refClsName; 
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}

	}
	
	private void setValues(String referenceClsName, String returnType, String name, List methodArgList, boolean heuristicResolve)
	{
		this.referenceClsName = referenceClsName;
		this.returnType = returnType;
		this.methodName = name;
			
		this.noOfArguments = 0;
		StringBuffer tmpArgStr = new StringBuffer("(");
		argStringAr = new String[methodArgList.size()];
		for(Iterator iter = methodArgList.iterator(); iter.hasNext();)
		{
			Expression expr = (Expression) iter.next();
			
			String refClsName = "";
			if(heuristicResolve)
				refClsName = ASTCrawlerUtil.getRefClassName(expr);
			else
			{	
				ITypeBinding exprTypeBinding = expr.resolveTypeBinding();
				
				if(exprTypeBinding != null)
				{
					refClsName = exprTypeBinding.getQualifiedName();
				}					
				else
				{
					logger.debug("ERROR: To DEAL WITH THIS...");
				}
			}	
			tmpArgStr.append(refClsName + ",");
			argStringAr[noOfArguments] = refClsName; 
			noOfArguments ++;
		}
		
		if(tmpArgStr.toString().equals("(") )
		{
			argumentString = "()";
		}
		else
		{	
			String tmp = tmpArgStr.toString();
			argumentString = tmp.substring(0, tmp.length()-1) + ")";
		}
			
		
	}

	/**
	 * Constructor for initializing MethodInvocationHolder from MethodInvocation object
	 * @param referenceClsName
	 * @param returnType
	 * @param miObj
	 */
	public MethodInvocationHolder(String referenceClsName, String returnType, MethodInvocation miObj) 
	{
		super();
		setValues(referenceClsName, returnType, miObj.getName().getIdentifier(), miObj.arguments(), true);
	}
	
	public MethodInvocationHolder(String referenceClsName, String returnType, MethodInvocation miObj, boolean heuristicResolve) 
	{
		super();
		setValues(referenceClsName, returnType, miObj.getName().getIdentifier(), miObj.arguments(), heuristicResolve);
	}


	//TODO: To do this later..
//	private void handleInlineMultiMethodInvoc(MethodInvocation miObj)
//	{
//		this.methodName = "";
//		
//		Expression exprP = miObj.getExpression(); 
//		while(exprP instanceof MethodInvocation)
//		{	
//			this.methodName = miObj.getName().getIdentifier() + this.methodName;
//			
//			this.noOfArguments = 0;
//			List methodArgList = miObj.arguments();
//			StringBuffer tmpArgStr = new StringBuffer("(");
//			argStringAr = new String[methodArgList.size()];
//			for(Iterator iter = methodArgList.iterator(); iter.hasNext();)
//			{
//				Expression expr = (Expression) iter.next();
//				String refClsName = ASTCrawlerUtil.getRefClassName(expr); 
//				tmpArgStr.append(refClsName + ",");
//				argStringAr[noOfArguments] = refClsName; 
//				noOfArguments ++;
//			}
//			
//			if(tmpArgStr.toString().equals("(") )
//			{
//				argumentString = "()";
//			}
//			else
//			{	
//				String tmp = tmpArgStr.toString();
//				argumentString = tmp.substring(0, tmp.length()-1) + ")";
//			}
//			
//		
//			exprP = ((MethodInvocation)exprP).getExpression();
//		}
//		
//	}
	

	public String getReferenceClsName() 
	{
		return referenceClsName;
	}
	
	public void setReferenceClsName(String referenceClsName) 
	{
		this.referenceClsName = referenceClsName;
	}
	
	public String getReturnType() 
	{
		return returnType;
	}
	
	public void setReturnType(String returnType) 
	{
		this.returnType = returnType;
	}
	
	public static MethodInvocationHolder getDummyMethodInvocationHolder()
	{
		MethodInvocationHolder dummy = new MethodInvocationHolder();
		dummy.referenceClsName = "#DUMMY";
		dummy.returnType = "#DUMMY";
		dummy.methodName = "#DUMMY";
		return dummy;
	}
	
	public static boolean isDummyMethodInvocationHolder(MethodInvocationHolder dummy)
	{
		if(dummy.referenceClsName.equals("#DUMMY") && dummy.returnType.equals("#DUMMY"))
		{
			return true;
		}
		
		return false;
	}
	
	public static MethodInvocationHolder parseFromString(String input)
	{
		MethodInvocationHolder newObj = new MethodInvocationHolder();
		
		int positionOfFirstSpace = input.indexOf(' ');
		int positionOfComma = input.indexOf(',');
		int positionOfLeftBrace = input.indexOf("(");
		int positionOfRightBrace = input.indexOf(")");
		int positionOfColon = input.indexOf(":");
		
		newObj.key = Integer.parseInt(input.substring(0, positionOfFirstSpace));
		newObj.referenceClsName = input.substring(positionOfFirstSpace, positionOfComma).trim();
		newObj.argumentString = input.substring(positionOfLeftBrace, positionOfRightBrace + 1).trim();
		newObj.returnType = input.substring(positionOfColon + 1, input.length()).trim();
		newObj.methodName = input.substring(positionOfComma + 1, positionOfLeftBrace).trim();
	
		return newObj;
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof MethodInvocationHolder)
		{
			MethodInvocationHolder mihObj = (MethodInvocationHolder) obj;
			if(isDummyMethodInvocationHolder(this) || isDummyMethodInvocationHolder(mihObj))
					return false;
			
			if(this.referenceClsName.equals(mihObj.referenceClsName) 
					&& this.returnType.equals(mihObj.returnType) 
					&& this.methodName.equals(mihObj.methodName)
					&& this.argumentString == mihObj.argumentString)
					{
						return true;
					}
			
		}
	
		return false;
	}
	
	public String[] getArgumentArr()
	{
		return argStringAr;
	}
	
	public boolean isPresentInArgumentArr(String objType)
	{
		boolean bFound = false;
		for(int i = 0; i < noOfArguments; i++)
		{
			if(argStringAr[i].equals(objType))
				return true;
		}
		
		return bFound;
	}
	
	public String toString()
	{
		if(referenceClsName.equals("#DUMMY"))
			return "#DUMMY";
		else
		{
			String tempStr = referenceClsName + "," + methodName + argumentString + " ";
			if(bDowncast)
			{
				tempStr = tempStr + " DownCasted_ReturnType:"; 
			}
			else
			{
				tempStr = tempStr + " ReturnType:";
			}
			return tempStr + getReturnType();
		}
	}

	public String getArgumentString() {
		return argumentString;
	}

	public void setArgumentString(String argumentString) {
		this.argumentString = argumentString;
	}

	public String getMethodName() {
		return methodName;
	}

	public int getNoOfArguments() {
		return noOfArguments;
	}
}