package xweb.api.srcparser;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.TypeParameter;

import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.common.CommonConstants;
import xweb.core.*;



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
		
		//If it is an exception class, adding the type to Map.
		if(currClassName.endsWith("Exception")) {
			String fullNameOfSuperCls = null;
			if(td.getSuperclassType() != null) {
				fullNameOfSuperCls = td.getSuperclassType().resolveBinding().getQualifiedName();
			}
			ASTCrawlerUtil.isARunTimeException(currClassName, fullNameOfSuperCls);
		}
			
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
	    		
	    		//Idenitfying the exceptions thrown by this method
	    		List<Object> excepList = mdArr[i].thrownExceptions();
	    		if(excepList.size() > 0) {
		    		List<String> excepStrList = new ArrayList<String>(excepList.size());
		    		for(Iterator exceptIter = excepList.iterator(); exceptIter.hasNext();) {
		    			Object exceptionObj = exceptIter.next(); 
		    			if(exceptionObj instanceof SimpleName) {
		    				excepStrList.add(((SimpleName)exceptionObj).getFullyQualifiedName());
		    			}    			
		    		}
		    		lmh.setThrowsExceptionList(excepStrList);
	    		}
	    		
	    		lmhList.add(lmh);
	    	}
	    	    	
	    	lch.setMethods(lmhList);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return true;
	}
}
