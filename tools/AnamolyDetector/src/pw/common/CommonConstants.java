package pw.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;
import java.util.logging.LogManager;

import org.apache.log4j.Logger;


public class CommonConstants {

	public static String sourceObject = "";
	public static String DestinationObject = "";
	
	/**
	 * Flag that controls the results returned by PARSEWeb
	 * TRUE: Always the pattern is continued till the root
	 * FALSE: Pattern search is stopped once it finds the root
	 */
	public static boolean bAlternateMethodArgumentSeq = true;
	
	public static String baseDirectoryName = System.getenv("ALATTIN_PATH");
	public static Logger logger = Logger.getLogger("CommonConstants");
	
	public static boolean bStartAction = false;	
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
	
	
	/**
	 * Variable that defines the precision level of treated different method sequences as same
	 * 0: Method sequences are matched exactly.
	 * n: Method sequences having 'n' method invocation differences are treated as same. 
	 * 		To avoid loss of information, higher one is included
	 * 		Eg: When n = 1 -> "1 4 5 7" and "1 4 7 9" are treated as same
	 * 			When n = 2 -> "1 4 6 7 8 9" and "1 4 21 22 8" are treated as same
	 */
	public static int methodSequenceClusterPrecision = 1;
	
	
	/**
	 * Variable for preventing the maximum number of sequences.
	 */
	public static int MAX_NUM_SEQUENCE_OUTPUT = 10;
	
	/**
	 * Parameters for Google Code Downloader
	 */
    //Parameter for tuning the number of threads. Modify this if more threads are required
    public static int MAX_THREAD_CNT = 1;
    public static int MAX_FILES_TO_DOWNLOAD = 5000;

    
    /**
     * Variables for PROXY Server
     */
	public static int bUseProxy = 0;
	public static String pHostname = "";
	public static int pPort;
	
	/**
	 * bUseMode2
	 */
	public static int bUseMode2 = 0;
	
	
	public static int uniqueMIHolderId = 0;
	public static int unknownIdGen = 0;
	
	/**
	 * All possible primitive types. These types cannot be used as destinations
	 */
	static public HashSet<String> primitiveTypes = new HashSet<String>();
	
	/**
	 * A Threshold value that pushes the search to mode 2 (To make this configurable)
	 */
	static public int thresholdValue = 1;
	
	
	/**
	 * Additional constants for API USage metrics project
	 */
	static public int API_Freq_Threshold = 10;
	static public int Method_Type_Threshold = 5;
	
	
	
	/**
	 * An additional setting for Google Code Downloader for getting more results
	 */
	static public boolean bUsePackageNames = true;
	
	/**
	 * Global variable denoting the parameterized data type
	 */
	static final public String PARAMETERIZED_TYPE = "<parameter>"; 
	
	
	static public boolean bExtendInterfacesToClasses = true;
	
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
	static public final int MINE_PATTERNS_FROM_CODESAMPLES = 0;
	static public final int MINE_PATTERNS_FROM_LIBRARY = 1;
	static public final int DETECT_BUGS_IN_CODESAMPLES = 2;
	static public final int DETECT_BUGS_IN_LIBRARY = 3;
	static public int OPERATION_MODE = MINE_PATTERNS_FROM_CODESAMPLES;
	static public int userConfiguredMode = 1;
	static public String inputPatternFile = "";
	
	//Threshold values
	static public double UPPER_THRESHOLD = 0.75;
	static public double LOWER_THRESHOLD = 0.1;
	static public double DIFF_THRESHOLD = 0.1;
	static public int AVERAGE_TO_HIGH_DIFF = 2; //A special parameter that moves avarage confidence rules to
												//high confidence rules. 
	
	//Rule types
	static public final int HIGH_CONFIDENCE = 1;
	static public final int AVERAGE_CONFIDENCE = 2;
	static public final int LOW_CONFIDENCE = 3;	
	static public final int NO_CONFIDENCE = 4;
	
	//Temporary workaround
	static public final int LIMIT_NUM_FILES = 1000;
	
	//Constants for sorting detected defects
	static public final double PENALTY_FOR_LOW_SAMPLES = 0.15;
	static public final int PENALTY_THRESHOLD = 5;
	
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
	static public int BUG_DETECTION_MODE = INDIVIDUAL_PATTERNS;
		
	//A classification 
	static public final int DFC_SINGLE_CHECK_PATTERNS = 0;
	static public final int DFC_BALANCED_PATTERNS = 1;
	static public final int DFC_IMBALANCED_PATTERNS = 2;
	
	static
    {
    	//User can optionally mention values for above parameters.
    	//In that case the default values will be overridden
    	try
    	{
	    	File propertyfile = new File (baseDirectoryName + FILE_SEP + "AlattinProperties.txt");
	    	if(propertyfile != null)
	    	{
				FileInputStream fIStream = new FileInputStream (propertyfile);
				System.getProperties().load(fIStream);
				Properties props = System.getProperties();
	
				if (props.containsKey("NumberOfThreads")) {
					MAX_THREAD_CNT = Integer.parseInt(props.getProperty("NumberOfThreads"));
				}

				if (props.containsKey("MaximumNumberOfFiles")) {
					MAX_FILES_TO_DOWNLOAD = Integer.parseInt(props.getProperty("MaximumNumberOfFiles"));
				}

				if (props.containsKey("AlternateMethodSequence")) {
					int bAlt = Integer.parseInt(props.getProperty("AlternateMethodSequence"));
					if(bAlt == 1)
					{
						bAlternateMethodArgumentSeq = true;
					}
					else
					{
						bAlternateMethodArgumentSeq = false;
					}
				}
				
				if (props.containsKey("MaximumOutputSequences")) {
					MAX_NUM_SEQUENCE_OUTPUT = Integer.parseInt(props.getProperty("MaximumOutputSequences"));
				}
				
				if (props.containsKey("ClusterPrecision")) {
					methodSequenceClusterPrecision = Integer.parseInt(props.getProperty("ClusterPrecision"));
				}
				
				if (props.containsKey("UseProxy")) {
					bUseProxy =  Integer.parseInt(props.getProperty("UseProxy"));
					
					if(bUseProxy == 1)
					{
						pHostname = props.getProperty("ProxyAddress");
						pPort = Integer.parseInt(props.getProperty("ProxyPort"));
					}
				}
				
				if (props.containsKey("Mode2")) {
					bUseMode2 =  Integer.parseInt(props.getProperty("Mode2"));
				}
				
				if (props.containsKey("OperationMode")) {
					userConfiguredMode =  Integer.parseInt(props.getProperty("OperationMode"));
				}
				
				if(props.containsKey("InputPatternFile")) {
					inputPatternFile = props.getProperty("InputPatternFile");
				}
				
				if(props.containsKey("BugDetectionMode")) {
					BUG_DETECTION_MODE = Integer.parseInt(props.getProperty("BugDetectionMode"));
				}
	    	}
	    	else
	    	{
	    		System.err.println("Properties file missing!!!");
	    		logger.error("Properties file AlattinProperties.txt missing!!!");
	    	}
    	}
    	catch(Exception ex)
    	{
    		ex.printStackTrace();
    	}
    }
    
    
    public static String getUniqueIDForUnknown() {
    	return unknownType + unknownIdGen++;
    }
    
}
