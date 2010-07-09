package methodfilter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Generates input class list for Randoop from API Mapping tests
 * @author suresh
 *
 */
public class InpClassGenerator {
	
	public static void main(String args[])
	{
		//Get the system environment variable
		String apiMappingOutput = System.getenv(APIMappingConstants.API_MAPPING_ENV_VAR);
		
		if(apiMappingOutput == null || apiMappingOutput.isEmpty())
		{
			System.err.println("The environment variable RANDOOP_METHOD_FILTER is not set. Set this variable" +
					"to API mapping Whitelist");
			return;
		}
		
		try
		{	
			//loading the filter information
			MethodFilter.loadFilterInformation(apiMappingOutput);		
			//outputtting back the information in the format required by Randoop
			BufferedWriter bw = new BufferedWriter(new FileWriter("InputClasses.txt"));
			for(String className : MethodFilter.allowedMethods.keySet())
			{
				bw.write(className);
				bw.newLine();
			}
			bw.close();
		}
		catch(IOException ex)
		{
			
		}
	}

}
