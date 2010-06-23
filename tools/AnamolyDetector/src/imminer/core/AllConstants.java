package imminer.core;



public class AllConstants {	
	
	public static String ROOT_ID_FILENAME = "AssocMethodIds.txt";
	public static String METHOD_SPECIFIC_ID_FOLDER = "AssocMiner_IDs";
	public static String METHOD_SPECIFIC_DATA_FOLDER = "AssocMiner_Data";
	public static final String FILE_SEP = "//";
	public static final String CONSOLIDATED_OUTPUT_FILENAME = "Alternative_ConsolidatedOutput.txt";
	public static final String DUMMY_MINING_ENTRY = "DUMMY_MINING_ENTRY";
	
	public static final double MIN_SUP = 0.4;
	
	/**
	 * Enabling the greedy approach helps to reduce the number of patterns choosen
	 * in the next level of the lattice. 
	 */
	public static boolean ENABLE_GREEDY_APPROACH = true;
	public static int NUMBER_OF_PATTERNS_TO_CHOOSE = 1;
		
}
