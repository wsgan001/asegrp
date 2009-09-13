package cm.inpapp.parser;

import java.util.Set;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import cm.common.CommonConstants;
import cm.inpapp.holder.ExternalObject;
import cm.inpapp.holder.LibClassHolder;
import cm.inpapp.holder.LibMethodHolder;
import cm.popup.actions.RepositoryAnalyzer;
import cm.samples.analyzer.TypeHolder;
import cm.samples.analyzer.holder.MethodInvocationHolder;


/**
 * A visitor for analyzing application under analysis
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
			
			TypeHolder receiverType = new TypeHolder();
			receiverType.setType(currentClsName);
			TypeHolder returnType = new TypeHolder();
			returnType.setType(retType);
			MethodInvocationHolder mihObj = new MethodInvocationHolder(receiverType, returnType, mdNode, false);
			currentLmh = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(currentClsName, mihObj);
		
			//If an equivalent LibMethodHolder is not found, it means it is a private method, which is of no interest.
			if(currentLmh == null)
			{
				logger.warn("Failed to get equivalent method declaration " + mihObj + " for class: " + currentClsName);
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
	
	public boolean visit(ClassInstanceCreation ccNode)
	{
		Type expr = ccNode.getType();
		String refClassName = "";
		if(expr == null)
		{
			refClassName = currentClsName;
		}
		else
		{	
			ITypeBinding refTypeBinding = ccNode.getType().resolveBinding();
			if(refTypeBinding != null)
			{
				refClassName = refTypeBinding.getQualifiedName();
			}
			else
			{
				logger.debug("ERROR : TO DEAL WITH THIS...");
			}
		}	
		
		//Adding constructor node to the external object
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance(); 
		Set libClsCollection = raObj.getLibClassMap().keySet();
		if(!libClsCollection.contains(refClassName))
		{			
			ExternalObject relObj = raObj.getExternalObjects().get(refClassName);
			if(relObj != null){
				MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(refClassName), ccNode);
				if(relObj.containsMI(mihForMi) == null)
					relObj.getMiList().add(mihForMi);
			} else {
				//logger.warn("Missing External Object for a given API: " + miNode);
			}		
		}	
		
		return super.visit(ccNode);
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
			if(libClsCollection.contains(refClassName))
			{	
				//AnamolyDetector : Commented this piece of code as this is not required for AnamolyDetector*/
				if(!CommonConstants.bAnamolyDetector){
					MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(), miNode, false);
					LibMethodHolder invokedLMH = raObj.getEqviMethodDeclaration(refClassName, mihForMi);
				
					if(invokedLMH == null)
					{
						logger.warn("Failed to get equivalent method declaration for " + mihForMi + " for class: " + refClassName);
					}
				
					if(invokedLMH != null && currentLmh != null)
						currentLmh.addToInvokedMethodList(invokedLMH);
				}	
			}
			else 
			{
				//This API belongs to external objects
				ExternalObject relObj = raObj.getExternalObjects().get(refClassName);
				if(relObj != null){
					MethodInvocationHolder mihForMi = new MethodInvocationHolder(new TypeHolder(refClassName), new TypeHolder(), miNode, false);
					if(relObj.containsMI(mihForMi) == null)
						relObj.getMiList().add(mihForMi);
				} else {
					//logger.warn("Missing External Object for a given API: " + miNode);
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
	
	private void addToExternalObjectsSet(Type typeObj)
	{
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
			
		if(typeObj.isArrayType())
		{
			typeObj = ((ArrayType)typeObj).getComponentType();
		} else if(typeObj.isParameterizedType())
		{
			typeObj = ((ParameterizedType)typeObj).getType();
		}
		
		if(typeObj.isPrimitiveType())
			return;
	
		if(typeObj.toString().endsWith("String"))
			return;
		
		String typeName = typeObj.resolveBinding().getQualifiedName().toString();
		//Handling parameterized types
		int indexOfBrace;
		if((indexOfBrace = typeName.indexOf("<")) != -1)
		{
			typeName = typeName.substring(0, indexOfBrace);
			typeName = typeName.trim();
		}
		
		//Get the package name. If it is contained in the list of packages
		//of current library, Ignore it.
		int indexOfDot;
		if((indexOfDot = typeName.lastIndexOf(".")) != -1)
		{
			String packageName = typeName.substring(0, indexOfDot);
			if(raObj.getLibPackageList().contains(packageName))
				return;
		}
		
		//TODO: THIS HAS TO BE REMOVED LATER
		//Adding filter on the external objects. This is a temporary for restricting the number of 
		//external objects.
		/*String filter = "bcel";
		if(typeName.indexOf(filter) == -1) 
		{			
			return;
		}*/
		
		
		if(raObj.getExternalObjects().get(typeName) == null) {
			ExternalObject eoObj = new ExternalObject();
			eoObj.setClassName(typeName);
			raObj.getExternalObjects().put(typeName, eoObj);
		}	
	}
}
