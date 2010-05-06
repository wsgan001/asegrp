package anamolydetector.popup.actions;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.ExternalObject;
import pw.code.analyzer.TypeHolder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.common.CommonConstants;

/**
 * This class accepts a list of APIs in a text file and finds the open source
 * code that have violations in that API.
 * 
 * Format of the External File:
 * <ClassName>#<API Name>#...
 * 
 * @author suresh_thummalapenta
 *
 */
public class APIAnalyzer {
	//private static Logger logger = Logger.getLogger("APIAnalyzer");
	
	public void loadExternalObjects(String fileName, String dirLocation, String bPackageFlag) 
	{
		String line = "";
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		try
		{
    		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS_FROM_CODESAMPLES;
    		RepositoryAnalyzer.resetMIIdGen();

			Scanner inpScan = new Scanner(new File(fileName));
			
			while(inpScan.hasNextLine())
			{
				line = inpScan.nextLine(); 
				String clsArr[] = line.split("#"); 
				//Filtering out primitive types
				int lastIndex = 0;
				if(clsArr.length != 0 && (lastIndex = clsArr[0].lastIndexOf(".")) != -1) {
					//Create an external object
					ExternalObject eoObj = new ExternalObject();
					eoObj.setClassName(clsArr[0]);
					raObj.getExternalObjects().put(clsArr[0], eoObj);
										
					//Add methods to the external objects
					for(int tcnt = 1; tcnt < clsArr.length; tcnt++) {
						MethodInvocationHolder mihForMi = MethodInvocationHolder.parseFromString(clsArr[tcnt], new TypeHolder(clsArr[0]));
						mihForMi.initializeCondVarMaps();
						eoObj.getMiList().add(mihForMi);
					}
					
					//Gather package names and ignore them while mining patterns and while 
					//identifying violations
					String packageName = clsArr[0].substring(0, lastIndex);
					raObj.getLibPackageList().add(packageName);				
				}				
			}			
			inpScan.close();			
			CommonConstants.bUsePackageNames = true;
			
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws IOException 
	{
		if(args.length == 0 || args.length != 7) {
			System.err.println("Usage: RepositoryCreater <APIList> <DirLocation> <PackageFlag> <OptionalLocalPackageInfoFile> <OperationMode> <PatternsFile> <DetectionMode>");
			System.err.println("OperationMode 0 : Mine Patterns");
			System.err.println("OperationMode 1 : Detect Defects");
			System.err.println("DetectionMode 0 : Ignore ImBalanced Patterns/Treat each pattern of MIH inidividually");
			System.err.println("DetectionMode 1 : Ignore ImBalanced Patterns/Treat all patterns of MIH together");
			System.err.println("DetectionMode 2 : Consider ImBalanced Patterns and Treat all patterns of MIH together");
			System.exit(1);
		}
		
		PatternLayout plOut = new PatternLayout();
		plOut.setConversionPattern("%d %5p (%F:%L) - %m%n");
		BasicConfigurator.configure(new RollingFileAppender(plOut, "AnamolyDetector.log", false));
		
		AnamolyDetector.localPackageDataFile = args[3];		
		APIAnalyzer apiObj = new APIAnalyzer();
		apiObj.loadExternalObjects(args[0], args[1], args[2]);
		
		//Main function that performs the rest
		int operationMode = Integer.parseInt(args[4]);
		
		if(operationMode == 0) {
			AnamolyDetector.startProcess(args[1]);
		} else {
			try {
				CommonConstants.BUG_DETECTION_MODE = Integer.parseInt(args[6]);
				AnamolyDetector.detectDefects(args[1], args[5]);
			} catch(Exception ex) {
				ex.printStackTrace();
			}
		}	
		
		System.out.println("Done...");
	}
}
