package xweb.common;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashSet;
import java.util.Properties;

import apiusagemetrics.actions.APIUsageActions;

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
	
	public static boolean bStartAction = false;	
	public static final String FILE_SEP = "//";
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
	 * AWeb specific constants
	 */
		
	//A mode for deciding type of process to be conducted.
	static public final int MINE_PATTERNS_FROM_INPUTPROJECT = 0;	//Mining patterns from the input project itself
	static public final int MINE_PATTERNS_CODE_SAMPLES = 1;			//Mining patterns from code samples
	static public final int DETECT_BUGS_IN_INPUTPROJECT = 2;		//Detecting bugs using mined patterns
	
	static public int OPERATION_MODE = MINE_PATTERNS_FROM_INPUTPROJECT;	
	
	//Miner types
	static public final int FREQUENT_ITEMSET_MINER = 0;
	static public final int SUBSEQUENCE_MINER = 1;
	static public final int ASSOCIATION_MINER = 2;
	static public int miner_type = FREQUENT_ITEMSET_MINER;
	
	//Cut-off percentage for identifying the frequent patterns
	static public final double LOWER_THRESHOLD = 0.06; 
	static public final double UPPER_THRESHOLD = 0.90;
	
	static public final int AVERAGE_TO_HIGH_DIFF = 2;
	
	
	//A global variable for controlling the data for association rule miner
	static public final boolean bEnableAssocMiner = true;
	
	//A flag to turn off results with Jex exceptions. This is because,
	//sometimes Jex exceptions might not be available due to the problem of OUT OF MEMORY given by Jex
	//The results may be imprecise when this flag is turned on.
	static public final boolean bIgnoreJex = false;
			
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
					APIUsageActions.MODE_RUNNING =  Integer.parseInt(props.getProperty("OperationMode"));
				}
	    	}
    	}
    	catch(Exception ex)
    	{
    		
    	}
    }
    
    /**
     * This method re-loads a few important properties
     * during the begining of every run
     */
    public static void reloadProperties() {
    	try {
	    	File propertyfile = new File (baseDirectoryName + FILE_SEP + "XWeb.properties");
	    	if(propertyfile != null)
	    	{
				FileInputStream fIStream = new FileInputStream (propertyfile);
				System.getProperties().load(fIStream);
				Properties props = System.getProperties();
				if (props.containsKey("XWeb_User_Mode")) {
					APIUsageActions.MODE_RUNNING =  Integer.parseInt(props.getProperty("XWeb_User_Mode"));
				}
	    	}
    	} catch(Exception ex) {
    		
    	}
    }
    
    public static String getUniqueIDForUnknown() {
    	return unknownType + unknownIdGen++;
    }    
}
