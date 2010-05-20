package imminer.core;



public class AllConstants {	
	
	public static String ROOT_ID_FILENAME = "AssocMethodIds.txt";
	public static String METHOD_SPECIFIC_ID_FOLDER = "AssocMiner_IDs";
	public static String METHOD_SPECIFIC_DATA_FOLDER = "AssocMiner_Data";
	public static final String FILE_SEP = "//";
	public static final String CONSOLIDATED_OUTPUT_FILENAME = "Alternative_ConsolidatedOutput.txt";
	public static final String DUMMY_MINING_ENTRY = "DUMMY_MINING_ENTRY";
	
	public static final double MIN_SUP = 0.4;
	public static double NEXT_LEVEL_SUPPORT = MIN_SUP / 4;  
	public static final double MAX_SUP = 0.8;
	
	public static boolean ENABLE_GREEDY_APPROACH = false;
		
}
