package xweb.api.srcparser;

import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.IMethodBinding;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import xweb.code.analyzer.ASTCrawlerUtil;
import xweb.code.analyzer.TypeHolder;
import xweb.core.*;
import xweb.code.analyzer.holder.MethodInvocationHolder;
import xweb.core.RepositoryAnalyzer;

/**
 * TODO: To replace this visitor with a full fledged visitor that can automatically
 * handle type resolutions.
 * @author suresh_thummalapenta
 *
 */
public class ParseSrcVisitor extends ASTVisitor  {

	static private Logger logger = Logger.getLogger("ParseSrcVisitor");
	
	String currentPackage;
	String currentClsName;
	LibClassHolder currentLch = null;
	LibMethodHolder currentLmh = null;
	RepositoryAnalyzer reposAnal = null;
			
	public ParseSrcVisitor()
	{
		reposAnal = RepositoryAnalyzer.getInstance();
	}
	
	public boolean visit(PackageDeclaration pd)
	{
		currentPackage = pd.getName().toString();
		super.visit(pd);
		return true;
	}
	
	public boolean visit(ImportDeclaration idNode)
	{
    	try
    	{
    		String impName = idNode.getName().getFullyQualifiedName();
			String packageName;			
			String className = impName.substring(impName.lastIndexOf(".") + 1, impName.length());
			if(!className.substring(0, 1).toUpperCase().equals(className.substring(0, 1))) {
				packageName = impName;
			} else {
				packageName = impName.substring(0, impName.lastIndexOf("."));
			}
			
			if(ASTCrawlerUtil.fullPackageNamesForClasses.get(className) == null)
				ASTCrawlerUtil.fullPackageNamesForClasses.put(className, impName);
						
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
			if(!raObj.getLibPackageList().contains(packageName)) {
				raObj.getImpStatementList().add(packageName);
			}			
		}
		catch(Exception ex)
		{			
		}	
		return true;
	}
	
	public boolean visit(TypeDeclaration td)
	{
		try
		{
			currentClsName = td.resolveBinding().getQualifiedName();
			
			int indexOfBrace;
			if((indexOfBrace = currentClsName.indexOf("<")) != -1)
			{
				currentClsName = currentClsName.substring(0, indexOfBrace);
				currentClsName = currentClsName.trim();
			}		
			
			currentLch = reposAnal.getLibClassMap().get(currentClsName);
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		
		super.visit(td);
		return true;	
	}
	
	public boolean visit(MethodDeclaration mdNode)
	{
		try
		{
			String retType = "null";
			if(!mdNode.isConstructor())
			{
				retType = mdNode.getReturnType2().toString();
			}
			
			MethodInvocationHolder mihObj = new MethodInvocationHolder(new TypeHolder(currentClsName), new TypeHolder(retType), mdNode, false);
			currentLmh = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(currentClsName, mihObj);
		
			//If an equivalent LibMethodHolder is not found, it means it is a private method, which is of no interest.
			if(currentLmh == null)
			{
				//logger.warn("Failed to get equivalent method declaration " + mihObj + " for class: " + currentClsName);
				return false;
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		super.visit(mdNode);
		return true;
	}
	
	
	public void postVisit(ASTNode astNode)
	{
		if(astNode instanceof MethodDeclaration)
		{
			currentLmh = null;			
		}
		super.postVisit(astNode);
	}
	
	public boolean visit(ClassInstanceCreation cicObj) 
	{
		ITypeBinding refTypeBinding = cicObj.getType().resolveBinding();
		String refClassName = refTypeBinding.getQualifiedName();
		String retClassName = refTypeBinding.getQualifiedName();
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance(); 
		Set libClsCollection = raObj.getLibClassMap().keySet();
		
		//As we are analyzing complete library over here, gather as much info as possible into fullPackage names 
		String className = refClassName.substring(refClassName.lastIndexOf(".") + 1, refClassName.length());
		if(ASTCrawlerUtil.fullPackageNamesForClasses.get(className) == null)
			ASTCrawlerUtil.fullPackageNamesForClasses.put(className, refClassName);
		//End of adding to ASTCrawlerUtil packages
		
		if(libClsCollection.contains(refClassName))
		{	
		}
		else 
		{
			//This API belongs to external objects				
			ExternalObject relObj = raObj.getExternalObjects().get(refClassName);
			
			//Sometime related object might now found, if any variable for that is not declared
			//Therefore, we explicitly try to create one here
			if(relObj == null) {
				relObj = addToExternalObjectsSet(cicObj.getType());				
			}
			
			if(relObj != null) {
				MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(retClassName), cicObj, false);
				if(relObj.containsMI(mihForMi) == null) {
					relObj.getMiList().add(mihForMi);
					mihForMi.setKey(MethodInvocationHolder.MIH_LIBKEYGEN++);
				}	
			}						
		}		
		return true;
	}
	
	public boolean visit(MethodInvocation miNode)
	{
		try
		{
			Expression expr = miNode.getExpression();
			String refClassName = "";
			if(expr == null || expr instanceof ThisExpression)
			{
				refClassName = currentClsName;
			}
			else
			{	
				ITypeBinding refTypeBinding = expr.resolveTypeBinding();
				if(refTypeBinding != null)
				{
					refClassName = refTypeBinding.getQualifiedName();
					
					//As we are analyzing complete library over here, gather as much info as possible into fullPackage names 
					String className = refClassName.substring(refClassName.lastIndexOf(".") + 1, refClassName.length());
					if(ASTCrawlerUtil.fullPackageNamesForClasses.get(className) == null)
						ASTCrawlerUtil.fullPackageNamesForClasses.put(className, refClassName);
					//End of adding to ASTCrawlerUtil packages
					
				}
				else
				{
					logger.debug("ERROR : TO DEAL WITH THIS...");
				}
			}
			
			
			
			
			//If this reference class name belong to the classes in the current library, then add this to
			//the invoked methods list
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance(); 
			Set libClsCollection = raObj.getLibClassMap().keySet();
			
			//Getting the return type of the method invocation
			ITypeBinding exprTypeBinding = miNode.resolveTypeBinding();	
			String retType = "";
			if(exprTypeBinding != null) {
				retType = exprTypeBinding.getQualifiedName();
			}					
			
			
			
			if(libClsCollection.contains(refClassName))
			{	
				MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(retType), miNode, false);
				LibMethodHolder invokedLMH = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(refClassName, mihForMi);
				
				if(invokedLMH == null)
				{
					//logger.warn("Failed to get equivalent method declaration for " + mihForMi + " for class: " + refClassName);
				}
				
				if(invokedLMH != null && currentLmh != null)
					currentLmh.addToInvokedMethodList(invokedLMH);
			}
			else 
			{
				//This API belongs to external objects				
				ExternalObject relObj = raObj.getExternalObjects().get(refClassName);
				
				//Sometime related object might now found, if any variable for that is not declared
				//Therefore, we explicitly try to create one here
				if(relObj == null) {
					relObj = new ExternalObject();
					relObj.setClassName(refClassName);
					raObj.getExternalObjects().put(refClassName, relObj);				
				}
				
				
				MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(retType), miNode, false);
				if(relObj.containsMI(mihForMi) == null) {
					relObj.getMiList().add(mihForMi);
					mihForMi.setKey(MethodInvocationHolder.MIH_LIBKEYGEN++);
				}			 
				
				//If the actual class containing the method-declaration is different,
				//then the current class is a child of the actual class
				IMethodBinding imbObj = miNode.resolveMethodBinding();
				String declaringClsName = imbObj.getDeclaringClass().getQualifiedName();				
				if(!refClassName.equals(declaringClsName) && !declaringClsName.equals("java.lang.Object")) {
					ExternalObject relPObj = raObj.getExternalObjects().get(declaringClsName);
					if(relPObj == null) {
						relPObj = new ExternalObject();
						relPObj.setClassName(declaringClsName);
						raObj.getExternalObjects().put(declaringClsName, relPObj);				
					}
					
					relPObj.getChildClasses().add(refClassName);
					
				}
			}
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		super.visit(miNode);
		return true;
	}		
	
	public boolean visit(FieldDeclaration fdObj)
	{
		addToExternalObjectsSet(fdObj.getType());
		return true;
	}
	
	public boolean visit(VariableDeclarationStatement vds)
	{
		addToExternalObjectsSet(vds.getType());
		return true;
	}
	
	public boolean visit(SingleVariableDeclaration svd)
	{
		addToExternalObjectsSet(svd.getType());
		return true;
	}
	
	private ExternalObject addToExternalObjectsSet(Type typeObj)
	{
		try {
			RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
				
			if(typeObj.isArrayType())
			{
				typeObj = ((ArrayType)typeObj).getComponentType();
			} else if(typeObj.isParameterizedType())
			{
				typeObj = ((ParameterizedType)typeObj).getType();
			}
			
			if(typeObj.isPrimitiveType())
				return null;

			if(typeObj.toString().endsWith("String"))
				return null;
			
			String typeName = typeObj.resolveBinding().getQualifiedName().toString();
			//Handling parameterized types
			int indexOfBrace;
			if((indexOfBrace = typeName.indexOf("<")) != -1)
			{
				typeName = typeName.substring(0, indexOfBrace);
				typeName = typeName.trim();
			}
			
			//If it is already in the current classes, ignore it.
			if(raObj.getLibClassMap().get(typeName) != null) {
				return null;
			}
			

			if(raObj.getExternalObjects().get(typeName) == null) {
				ExternalObject eoObj = new ExternalObject();
				eoObj.setClassName(typeName);
				raObj.getExternalObjects().put(typeName, eoObj);
				return eoObj;
			}
			
		} catch (RuntimeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		return null;
	}
}
