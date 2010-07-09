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
				//general format: java.lang.StringBuilder.insert(int,char[],int,int)
				String line = sc.nextLine();
				
				String className = null, methodSignature = null;
				
				if(line.contains("<get>"))
				{
					//It could be a field. General format: java.lang.Float.<get>POSITIVE_INFINITY
					int lastDotIndex = line.lastIndexOf(".");
					if(lastDotIndex == -1)
					{
						System.err.println("Invalid field name: " + line);
						continue;
					}
					
					className = line.substring(0, lastDotIndex);
					methodSignature = line.substring(line.indexOf(">") + 1, line.length());
				}
				else
				{
					int openBIndex = line.indexOf("(");
					if(openBIndex == -1)
					{
						System.err.println("Invalid method name: " + line);
						continue;
					}
					
					String firstPart = line.substring(0, openBIndex);
					
					int lastDotIndex = firstPart.lastIndexOf(".");
					if(lastDotIndex == -1)
					{
						System.err.println("Invalid method name: " + line);
						continue;
					}
					
					className = firstPart.substring(0, lastDotIndex);
					String methodName = firstPart.substring(lastDotIndex + 1, firstPart.length());
					methodSignature = methodName + line.substring(openBIndex, line.length());
				}
				
				HashSet<String> methods = allowedMethods.get(className);
				if(methods == null)
				{
					methods = new HashSet<String>();
					allowedMethods.put(className, methods);
				}
				methods.add(methodSignature);
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
		
		if(!isFilterInfoLoaded) {
			String methodFilterFileName = System.getenv(APIMappingConstants.API_MAPPING_ENV_VAR);
		
			//If there is no filter, this method returns true for all the methods
			if(methodFilterFileName == null) {
				isEnvVariableSet = false;
				return true;
			}
		
			loadFilterInformation(methodFilterFileName);
			isFilterInfoLoaded = true;
		}
		
		HashSet<String> methods = allowedMethods.get(c.getName());		
		if(methods == null)
			return false;		
		
		return methods.contains(mname);
	}		
	
	public static boolean isAPIMappingEnvVariableSet()
	{
		String methodFilterFileName = System.getenv(APIMappingConstants.API_MAPPING_ENV_VAR);
		if(methodFilterFileName == null) {
			return false;
		}
		else
			return true;
	}
	
	public static boolean isGetPUTEnvVariableSet()
	{
		String methodFilterFileName = System.getenv(APIMappingConstants.GENERATE_PUTS);
		if(methodFilterFileName == null) {
			return false;
		}
		else
			return true;
	}
}
