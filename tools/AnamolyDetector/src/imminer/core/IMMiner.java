package imminer.core;

import java.io.File;
import java.util.Scanner;
import org.apache.log4j.Logger;


/**
 * Main class of IMMiner that starts the process
 * @author suresh
 */
public class IMMiner {
	
	private static Logger logger = Logger.getLogger("IMMiner");	
	public static void main(String args[])
	{
		if(args.length == 0)
		{
			System.err.println("Incorrect usage. Give the location of the directory");
			return;
		}	
						
		IMMiner imm = new IMMiner();
		imm.startProcess(args[0]);	
	}
	
	public MinerStorage minePatterns(String parentDir)
	{
		IMMiner imm = new IMMiner();
		return imm.startProcess(parentDir);	
	}
		
	public MinerStorage startProcess(String parentDir)
	{
		MinerStorage ms = MinerStorage.getInstance();
		ms.initialize(parentDir);
		
		try
		{
			String idfile = parentDir + AllConstants.FILE_SEP + AllConstants.ROOT_ID_FILENAME;
			Scanner sc = new Scanner(new File(idfile));			
			while(sc.hasNextLine())
			{
				String line = sc.nextLine();
				System.out.println("Processing method: " + line);
				String lineparts[] = line.split(":");
				
				if(lineparts.length != 2)
				{
					logger.warn("Incorrect entry in root ids file: " + line);
					continue;
				}
				
				String methodId = lineparts[0].trim();
				String methodName = lineparts[1].trim();
				ms.addToRootMapper(methodId, methodName);				
				MethodMiner mm = new MethodMiner(methodId, methodName);
				mm.generateLattice();
			}
			sc.close();
			ms.pruneSingleItemMethods();
			ms.dumpPatterns();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		ms.cleanUp();
		return ms;
	}
}
