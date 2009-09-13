package api.srcparser;

import java.util.Set;
import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ITypeBinding;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import api.usage.LibClassHolder;
import api.usage.LibMethodHolder;
import api.usage.RepositoryAnalyzer;
import pw.code.analyzer.MethodInvocationHolder;

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
			
			MethodInvocationHolder mihObj = new MethodInvocationHolder(currentClsName, retType, mdNode, false);
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
			Set libClsCollection = RepositoryAnalyzer.getInstance().getLibClassMap().keySet();
			if(libClsCollection.contains(refClassName))
			{	
				MethodInvocationHolder mihForMi = new MethodInvocationHolder(refClassName, "", miNode, false);
				LibMethodHolder invokedLMH = RepositoryAnalyzer.getInstance().getEqviMethodDeclaration(refClassName, mihForMi);
				
				if(invokedLMH == null)
				{
					logger.warn("Failed to get equivalent method declaration for " + mihForMi + " for class: " + refClassName);
				}
				
				if(invokedLMH != null && currentLmh != null)
					currentLmh.addToInvokedMethodList(invokedLMH);
			}
		}	
		catch(Exception ex)
		{
			ex.printStackTrace();
		}

		super.visit(miNode);
		return true;
	}		
}
