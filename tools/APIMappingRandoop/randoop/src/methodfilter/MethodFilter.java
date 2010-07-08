package methodfilter;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;

/**
 * Creates an additional filter on the Randoop to restrict the methods
 * used while generating sequences
 * @author Suresh
 */
public class MethodFilter {
	
	public static boolean isFilterInfoLoaded = false;
	public static boolean isEnvVariableSet = true;
	
	//Stores all allowed methods grouped by their declaring class name
	public static HashMap<String, HashSet<String>> allowedMethods = new HashMap<String, HashSet<String>>();
	
	
	public static String getType(Class<?> type)
	{
		if(type.isArray())
		{		
			int numDimensions = 1;
			type = type.getComponentType();
			while(type.isArray())
			{
				numDimensions++;
				type = type.getComponentType();
			}	
			StringBuffer sb = new StringBuffer(type.getName());
			for(int count = 0; count < numDimensions; count++)
			{
				sb.append("[]");
			}
			return sb.toString();
		}
		else
			return type.getName();		
	}	
	
	public static String getMethodInvocationSignature(Method method)
	{
		StringBuffer sb = new StringBuffer(method.getName());
		sb.append("(");		
		int numParameters = method.getParameterTypes().length;
		int count = 0;
		for(Class<?> ptype : method.getParameterTypes())
		{
			sb.append(getType(ptype));
			
			count++;
			if(count != numParameters)
				sb.append(",");
		}
		
		sb.append(")");
		return sb.toString();		
	}
	
	public static String getConstructorSignature(Constructor<?> constructor)
	{
		StringBuffer sb = new StringBuffer("<init>");
		sb.append("(");		
		int numParameters = constructor.getParameterTypes().length;
		int count = 0;
		for(Class<?> ptype : constructor.getParameterTypes())
		{
			sb.append(getType(ptype));
			
			count++;
			if(count != numParameters)
				sb.append(",");			
		}
		
		sb.append(")");
		return sb.toString();		
	}
	
	/**
	 * Loads the methods from the 
	 * @param fileName
	 */
	public static void loadFilterInformation(String fileName)
	{
		try
		{
			Scanner sc = new Scanner(new File(fileName));				
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				
				String[] tokens = line.split("#");
				if(tokens == null || tokens.length == 0)
					continue;
				
				String className = tokens[0];
				HashSet<String> methods = allowedMethods.get(className);
				if(methods == null)
				{
					methods = new HashSet<String>();
					allowedMethods.put(className, methods);
				}
				for(int count = 1; count < tokens.length; count++)
				{
					methods.add(tokens[count]);
				}
			}		
		}
		catch(FileNotFoundException ex)
		{
			System.err.println("Error occurred while reading contents of the file " + ex.getMessage());
		}
	}
	
	/**
	 * Checks whether this method is allowed by the filter
	 * @param className
	 * @param methodName
	 */
	public static boolean isMethodAllowed(Class<?> c, Method method)
	{	
		//Apply this condition check only when the declaring class of method is c
		if(method.getDeclaringClass() != c)
			return true;
			
		String mname = getMethodInvocationSignature(method);
		return isMethodOrConstructorAllowed(c, mname);
	}	
		
	/**
	 * Checks whether this method is allowed by the filter
	 * @param className
	 * @param methodName
	 */
	public static boolean isConstructorAllowed(Class<?> c, Constructor<?> constructor)
	{	
		//Always allow default constructors
		if(constructor.getParameterTypes().length == 0)
			return true;
		
		String mname = getConstructorSignature(constructor);
		return isMethodOrConstructorAllowed(c, mname);
	}	
	
	public static boolean isMethodOrConstructorAllowed(Class<?> c, String mname)
	{
		if(!isEnvVariableSet)
			return true;
		
		String methodFilterFileName = System.getenv("RANDOOP_METHOD_FILTER");
		
		//If there is no filter, this method returns true for all the methods
		if(methodFilterFileName == null) {
			isEnvVariableSet = false;
			return true;
		}
		
		if(!isFilterInfoLoaded) {
			isFilterInfoLoaded = true;
			loadFilterInformation(methodFilterFileName);
		}
		
		HashSet<String> methods = allowedMethods.get(c.getName());		
		if(methods == null)
			return false;		
		
		return methods.contains(mname);
	}		
}
