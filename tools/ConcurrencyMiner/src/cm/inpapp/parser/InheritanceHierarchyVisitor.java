package cm.inpapp.parser;

import java.util.Iterator;
import java.util.List;


import org.apache.log4j.Logger;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import cm.common.CommonConstants;
import cm.inpapp.holder.LibClassHolder;
import cm.popup.actions.RepositoryAnalyzer;


/**
 * This visitor class justs loads only classes and packages names of the given
 * source library. All the loaded information is directly stored in 
 * RepositoryAnalyzer class. This loads
 * 1. All package names of the current library or framework
 * 2. All class -> full package names mapping.
 * 3. All fully qualified class names.	(Give preference to this always)
 **/
public class InheritanceHierarchyVisitor extends ASTVisitor {
	
	String currentPackage;
	public static Logger logger = Logger.getLogger("InheritanceHierarchyVisitor");
	
	@SuppressWarnings("unchecked")
	public boolean visit(TypeDeclaration td)
	{
		//AnamolyDetector: Whole Inheritance Visitor is disabled for Anamoly Detector
		if(CommonConstants.bAnamolyDetector)
			return false;
		
		String fullClsName = td.resolveBinding().getQualifiedName();

		//Incase of parameterized type, ignore the parameter part
		int indexOfBrace;
		if((indexOfBrace = fullClsName.indexOf("<")) != -1)
		{
			fullClsName = fullClsName.substring(0, indexOfBrace);
			fullClsName = fullClsName.trim();
		}
	

		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		
		try
		{
			LibClassHolder lch = raObj.getLibClassMap().get(fullClsName);
			if(lch == null)
			{
				logger.warn("Could not found the object for class " + fullClsName);
			}
			
			//Identify the parent and child classes
			if(td.getSuperclassType() != null)
			{
				Type superClsType = (Type)td.getSuperclassType();
				String fullNameOfSuperCls = null;
				fullNameOfSuperCls = superClsType.resolveBinding().getQualifiedName();
				
				//Incase of parameterized type, ignore the parameter part
				if((indexOfBrace = fullNameOfSuperCls.indexOf("<")) != -1)
				{
					fullNameOfSuperCls = fullNameOfSuperCls.substring(0, indexOfBrace);
					fullNameOfSuperCls = fullNameOfSuperCls.trim();
				}
				
				LibClassHolder lchSuper = raObj.getLibClassMap().get(fullNameOfSuperCls);
		
				if(lchSuper != null)
				{	
					lchSuper.getChildTypes().add(lch.getName());
					lch.setParentType(fullNameOfSuperCls);
				}
				else
				{
					logger.warn("Parent object is found as null for " + fullNameOfSuperCls);
				}				
			}
		
			List superInterfaces = td.superInterfaceTypes();
			if(superInterfaces != null)
			{
				for(Iterator tmp_iter = superInterfaces.iterator(); tmp_iter.hasNext(); )
				{
					Type superInterfaceType = (Type) tmp_iter.next();
	    			String fullNameOfSuperInterface = superInterfaceType.resolveBinding().getQualifiedName();
	    			
					//Incase of parameterized type, ignore the parameter part
					if((indexOfBrace = fullNameOfSuperInterface.indexOf("<")) != -1)
					{
						fullNameOfSuperInterface = fullNameOfSuperInterface.substring(0, indexOfBrace);
						fullNameOfSuperInterface = fullNameOfSuperInterface.trim();
					}
	    			
	    			LibClassHolder lchInterface = raObj.getLibClassMap().get(fullNameOfSuperInterface);
	    			
	    			if(lchInterface != null)
	    			{	
	    				lchInterface.getChildTypes().add(lch.getName());
	    				lch.getAllInterfaceTypes().add(fullNameOfSuperInterface);
	    			} else {
	    				logger.warn("Parent object is found as null for " + fullNameOfSuperInterface);
	    			}
	    			
				}
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		return true;
	}
}
