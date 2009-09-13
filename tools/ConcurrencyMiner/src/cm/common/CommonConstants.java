package cm.common;

import java.util.HashSet;

public class CommonConstants {

	public static String sourceObject = "";
	public static String DestinationObject = "";
	
	public static final String FILE_SEP = "/";
	//public static final String FILE_SEP = System.getProperty("file.separator");
	public static int numberOfSamplesFound = 0;	//An internal variable that automatically pushes the PARSEWeb to Mode 2, if they are less than 10
	
	
	/**
	 * String used to represent the UNKNOWN types.
	 */
	public static final String unknownType = "#UNKNOWN#";
	
	/**
	 * String used to represent to multiple sources as possible types
	 */
	public static final String multipleCurrTypes = "#MULTICURRTYPES#";
	
	/**
	 * String used for pre-fixing downcasts
	 */
	public static final String downcastPrefix = "DOWNCAST_";
	
	/**
	 * Variable that defines the precision level for treating different method invocations as same
	 * 0: Method invocation signatures are matched exactly as they are extracted.
	 * 1: Method invocation signatures are considered as same even if there is a difference in type of 
	 * 		one argument. Say "int a(int, int)" and "int a(int, string)" are treated as same
	 * 
	 */
	public static int methodInvocationClusterPrecision = 0;

	public static int uniqueMIHolderId = 0;
	public static int unknownIdGen = 0;
	
	/**
	 * All possible primitive types. These types cannot be used as destinations
	 */
	static public HashSet<String> primitiveTypes = new HashSet<String>();
	
	/**
	 * Global variable denoting the parameterized data type
	 */
	static final public String PARAMETERIZED_TYPE = "<parameter>"; 
	
	/**
	 * Anamoly Detector specific constants
	 */
	//Specific flag for Anamoly Detector
	static public final boolean bAnamolyDetector = true;
	
	//Types of patterns gathered and their related constants
	static public final int RECEIVER_PATTERNS = 1;
	static public final int ARGUMENT_PATTERNS = 2;
	static public final int PRECEDING_METHOD_CALL_PATTERS = 3;
	static public final int RETURN_PATTERNS = 4;
	static public final int SUCCEEDING_METHOD_CALL_PATTERNS = 5;
	static public final int SUCCEEDING_RECEIVER_PATTERNS = 6;	//A case where receiver variable is passed to a method invocation for conditional checks
	
	//A mode for deciding type of process to be conducted.
	static public final int MINE_PATTERNS = 0;
	static public final int DETECT_BUGS_IN_CODESAMPLES = 1;
	static public final int DETECT_BUGS_IN_LIBRARY = 2;
	static public int OPERATION_MODE = MINE_PATTERNS;
	static public int userConfiguredMode = 2;
		
	//Temporary workaround
	static public final int LIMIT_NUM_FILES = 1000;
	
	//Constants for traversal direction
	static public final int BACKWARD_TRAVERSAL = 1;
	static public final int FORWARD_TRAVERSAL = 0;
	
	
	static public final boolean ALL_METHODS_OF_CLASS = false;	//A flag that gives flexibility to avoid specifying methods for all APIs
	
	static public final boolean B_COLLECT_MINER_DATA = true;    //A flag that can be used to turn on/off the miner data into files
																//rather than directly colelcting and mining using simple statistical methods
	
	//Constants for bug detection mode types, mainly used in the evaluation
	static public final int INDIVIDUAL_PATTERNS = 0;	//Ignore Balanced Patterns/Treat each pattern of MIH inidividually
	static public final int COMBINED_PATTERNS = 1;		//Ignore Balanced Patterns/Treat all patterns of MIH together
	static public final int IMBALANCED_PATTERNS = 2;	//Consider Balanced Patterns and Treat all patterns of MIH together
			
	//A classification 
	static public final int DFC_SINGLE_CHECK_PATTERNS = 0;
	static public final int DFC_BALANCED_PATTERNS = 1;
	static public final int DFC_IMBALANCED_PATTERNS = 2;
    public static String getUniqueIDForUnknown() {
    	return unknownType + unknownIdGen++;
    }    
}
