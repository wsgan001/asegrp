package api.srcparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;


import pw.code.analyzer.ASTCrawlerUtil;
import pw.common.CommonConstants;
import api.usage.LibClassHolder;
import api.usage.LibMethodHolder;
import api.usage.RepositoryAnalyzer;



/*
 * This visitor class justs loads only classes and packages names of the given
 * source library. All the loaded information is directly stored in 
 * RepositoryAnalyzer class. This loads
 * 1. All package names of the current library or framework
 * 2. All class -> full package names mapping.
 * 3. All fully qualified class names.	(Give preference to this always)
 */
public class ClassOnlyVisitor extends ASTVisitor {
	
	String currentPackage;
	public static Logger logger = Logger.getLogger("ClassOnlyVisitor");
	
	public boolean visit(PackageDeclaration pd)
	{
		currentPackage = pd.getName().toString();
		HashSet<String> packageList = RepositoryAnalyzer.getInstance().getLibPackageList(); 
		if(!packageList.contains(currentPackage))
			packageList.add(currentPackage);
		
		super.visit(pd);
		return true;
	}
	
	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration td)
	{
		String currClassName = td.getName().toString();
		String fullClsName = td.resolveBinding().getQualifiedName();

		//Incase of parameterized type, ignore the parameter part
		int indexOfBrace;
		if((indexOfBrace = fullClsName.indexOf("<")) != -1)
		{
			fullClsName = fullClsName.substring(0, indexOfBrace);
			fullClsName = fullClsName.trim();
		}
	
		ASTCrawlerUtil.fullPackageNamesForClasses.put(currClassName, fullClsName);
		
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		
		try
		{
			LibClassHolder lch = new LibClassHolder(fullClsName);
			raObj.getLibClassMap().put(fullClsName, lch);
			
			raObj.getLibClassToPackageMapper().put(currClassName, currentPackage);			
			ArrayList<LibMethodHolder> lmhList = new ArrayList<LibMethodHolder>();
			
			if(td.isInterface()) {
				lch.setInterface(true);
			}
			
			if((td.getModifiers() & Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toFlagValue()) != 0)
				lch.setAbstract(true);
			
			List typeParamListOfCls = td.typeParameters();
			List lchTypeList = lch.getParameterizedTypes();
			for(Iterator iter = typeParamListOfCls.iterator(); iter.hasNext();)
			{
				TypeParameter tpObj = (TypeParameter) iter.next();
				lchTypeList.add(tpObj.getName().toString());
			}
			
			//Add all method declarations
	    	MethodDeclaration mdArr[] = td.getMethods();	    	
	    	for(int i = 0; i < mdArr.length; i++)
	    	{
	    		if((mdArr[i].getModifiers() & Modifier.ModifierKeyword.PUBLIC_KEYWORD.toFlagValue()) == 0  &&
	    				(mdArr[i].getModifiers() & Modifier.ModifierKeyword.PROTECTED_KEYWORD.toFlagValue()) == 0 )
	    		{
	    			continue;
	    		}
	    		
	    		LibMethodHolder lmh = new LibMethodHolder();
	    		lmh.setContainingClass(lch);
	    		raObj.getIdToLibMethod().put(new Integer(lmh.getID()), lmh);
	    		    		
	    		//Setting the method type
	    		//A method can be considered as hook method if
	    		//a. Abstract method
	    		//b. Method of an interface
	    		if((mdArr[i].getModifiers() & Modifier.ModifierKeyword.ABSTRACT_KEYWORD.toFlagValue()) != 0)	
	    		{
	    			lmh.setMethodType(LibMethodHolder.HOOK);
	    			lmh.setAbstract(true);
	    		}	    		
	    		if(lch.isInterface())
	    		{
	    			lmh.setMethodType(LibMethodHolder.HOOK);
	    			lmh.setAbstract(true);
	    		}
	    		
	    		if((mdArr[i].getModifiers() & Modifier.ModifierKeyword.STATIC_KEYWORD.toFlagValue()) != 0
	    				|| (mdArr[i].getModifiers() & Modifier.ModifierKeyword.FINAL_KEYWORD.toFlagValue()) != 0)
	    		{
	    			lmh.setMethodType(LibMethodHolder.TEMPLATE);
	    		}
	    		
	    		//Get the type parameters of Method
	    		List methodTypeParam = mdArr[i].typeParameters();
	    		List lmhTypeList = lmh.getTypeParameters();
	    		for(Iterator iter_in = methodTypeParam.iterator(); iter_in.hasNext();)
	    		{
	    			TypeParameter tp = (TypeParameter) iter_in.next();
	    			lmhTypeList.add(tp.getName().toString());
	    		}
	    			    		
	    		if(mdArr[i].isConstructor())
	    		{
	    			lmh.setReturnType(fullClsName);
	    			lmh.setName("CONSTRUCTOR");
	    		}
	    		else
	    		{	
	    			Type rety = mdArr[i].getReturnType2();
		    		ITypeBinding retType = rety.resolveBinding();
		    		String retTypeQual = "";
		    		
		    		if(retType != null) {	
		    			retTypeQual = retType.getQualifiedName();
		    		}
		    		else if(rety != null) {
		    			retTypeQual = rety.toString();
		    		}
		    		else {
		    			//Return type can be considered as same class type for constructors
		    			retTypeQual = "null";
		    		}
		    		
		    		lmh.setReturnType(retTypeQual);
		    		lmh.setName(mdArr[i].getName().toString());
	    		}	
			    		
	    		List arguments = mdArr[i].parameters(); 
	    		
	    		String argArr[] = new String[arguments.size()];
	    		String argString = "(";
	    		String printArgString = "(";
	    		int count = 0;
	    		for(Iterator iter = arguments.iterator(); iter.hasNext();) 
	    		{	    			
	    			if(count > 0)
	    			{
	    				argString += ",";
	    				printArgString += ",";
	    			}
	    			
	    			Type argType = ((SingleVariableDeclaration)iter.next()).getType();
	    			boolean bParameterizedType = false;
	    			if(argType instanceof ParameterizedType)
	    			{
	    				bParameterizedType = true;
	    			}
	    			
	    			ITypeBinding argTypeBinding = argType.resolveBinding();
	    			
	    			String argumentType = "";
	    			if(argTypeBinding != null)
	    			{	
	    				argumentType = argTypeBinding.getQualifiedName();
	    			}
	    			else
	    			{
	    				argumentType = argType.toString();
	    			}
	    			
	    			if(lchTypeList.contains(argumentType) || lmhTypeList.contains(argumentType)) {
	    				bParameterizedType = true;
	    			}
	    			
	    			if(!bParameterizedType) {
	    				argString += argumentType;
	    				argArr[count] = argumentType;
	    			} else {
	    				argString += CommonConstants.PARAMETERIZED_TYPE;
	    				argArr[count] = CommonConstants.PARAMETERIZED_TYPE;
	    			}
	    			
	    			printArgString += argumentType;
	    				    			
	    			count++;
	    		}
	    		
	    		argString += ")";
	    		printArgString += ")";
	    		
	    		lmh.setArgumentStr(argString );
	    		lmh.setArgTypes(argArr);
	    		lmh.setPrintArgumentStr(printArgString);
	    		
	    		lmhList.add(lmh);
	    	}
	    	
	    	//Handling constant variables. These are also treated as method-invocations
	    	//for generality
	    	FieldDeclaration[] fdArr = td.getFields();
	    	for(int fdCnt = 0; fdCnt < fdArr.length; fdCnt++) {
	    		if((fdArr[fdCnt].getModifiers() & Modifier.ModifierKeyword.FINAL_KEYWORD.toFlagValue()) == 0
	    				|| (fdArr[fdCnt].getModifiers() & Modifier.ModifierKeyword.STATIC_KEYWORD.toFlagValue()) == 0) {
	    			continue;
	    		}
	    		
	    		//Heuristic: In Java, constants are always defined in capital letters
				for(Iterator iter = fdArr[fdCnt].fragments().iterator(); iter.hasNext();)
				{
					VariableDeclarationFragment vdfObj = (VariableDeclarationFragment) iter.next();
					String variableName = vdfObj.getName().getIdentifier();
					if(variableName.toUpperCase().equals(variableName)) {
						//Found a constant, so define a method to handle the future aspects
						LibMethodHolder lmh = new LibMethodHolder();
			    		lmh.setContainingClass(lch);
			    		RepositoryAnalyzer.getInstance().getIdToLibMethod().put(new Integer(lmh.getID()), lmh);
			    		lmh.setReturnType("void");
			    		lmh.setName(variableName);
				    	lmh.setArgumentStr("()");
				    	lmh.setPrintArgumentStr("()");
				    	String argArr[] = new String[0];
				    	lmh.setArgTypes(argArr);
				    	lmhList.add(lmh);						
					}				
				}	    		
	    	}
	    	
	    	//Adding a dummy method that is used after collecting metrics.
	    	//IMPORTANT NOTE: We just treat the class extension as an individual method as
	        //a class can be extended many times but none of its methods are overridden.
	    	//This dummy method helps for that treatment
	    	LibMethodHolder lmh = new LibMethodHolder();
    		lmh.setContainingClass(lch);
    		RepositoryAnalyzer.getInstance().getIdToLibMethod().put(new Integer(lmh.getID()), lmh);
    		lmh.setReturnType("void");
    		lmh.setName(RepositoryAnalyzer.DUMMY_METHOD_NAME);
	    	lmh.setArgumentStr("()");
	    	lmh.setPrintArgumentStr("()");
	    	String argArr[] = new String[0];
	    	lmh.setArgTypes(argArr);
	    	lmhList.add(lmh);	    	
	    	
	    	//Adding another dummy method to deal when the class represents
	    	//an exception and is frequently used among input applications. The name
	    	//of the dummy method is "ExceptionHandler".
	    	LibMethodHolder lmhException = new LibMethodHolder();
	    	lmhException.setContainingClass(lch);
    		RepositoryAnalyzer.getInstance().getIdToLibMethod().put(new Integer(lmhException.getID()), lmhException);
    		lmhException.setReturnType("void");
    		lmhException.setName(RepositoryAnalyzer.EXCEPTION_HANDLER);
    		lmhException.setArgumentStr("()");
    		lmhException.setPrintArgumentStr("()");
    		lmhException.setArgTypes(argArr);
	    	lmhList.add(lmhException);
	    	
	    	
	    	lch.setMethods(lmhList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return true;
	}
}
