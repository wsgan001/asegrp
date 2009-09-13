package cm.popup.actions;


import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;

import cm.common.CommonConstants;
import cm.inpapp.holder.ExternalObject;
import cm.samples.analyzer.TypeHolder;
import cm.samples.analyzer.holder.MethodInvocationHolder;

/**
 * Main class for using the tool in Command line mode
 * 
 * This class accepts a list of APIs in a text file and a directory
 * that includes a set of code examples that contain usage scenarios of these APIs. 
 *  
 * Format of the External File:
 * <ClassName>#<API Name>#...
 * 
 * Returns the traces extracted from these code examples w.r.t condition checks
 * 
 * @author suresh_thummalapenta
 *
 */
public class APIAnalyzer {
		
	public void loadExternalObjects(String fileName, String dirLocation) 
	{
		String line = "";
		RepositoryAnalyzer raObj = RepositoryAnalyzer.getInstance();
		try
		{
    		CommonConstants.OPERATION_MODE = CommonConstants.MINE_PATTERNS;
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
						eoObj.getMiList().add(mihForMi);
					}
					
					//Gather package names and ignore them while mining patterns and while identifying violations
					String packageName = clsArr[0].substring(0, lastIndex);
					raObj.getLibPackageList().add(packageName);				
				}				
			}			
			inpScan.close();				
		}
		catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public static void main(String args[]) throws IOException 
	{
		if(args.length == 0 || args.length != 2) {
			System.err.println("Usage: APIAnalyzer <APIList> <DirLocation>");
			System.exit(1);
		}
		
		//Setting the Log4j information
		PatternLayout plOut = new PatternLayout();
		plOut.setConversionPattern("%d %5p (%F:%L) - %m%n");
		BasicConfigurator.configure(new RollingFileAppender(plOut, "AnamolyDetector.log", false));
		
		//Loading the API information into local memory
		APIAnalyzer apiObj = new APIAnalyzer();
		apiObj.loadExternalObjects(args[0], args[1]);
		
		AnamolyDetector.startProcess(args[1]);
		System.out.println("Done...");
	}
}
