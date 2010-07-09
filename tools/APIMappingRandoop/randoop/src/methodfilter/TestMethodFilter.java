package methodfilter;

public class TestMethodFilter {

	public static void main(String args[])
	{
		String fileName = System.getenv(APIMappingConstants.API_MAPPING_ENV_VAR);
		MethodFilter.loadFilterInformation(fileName);		
	}	
}
