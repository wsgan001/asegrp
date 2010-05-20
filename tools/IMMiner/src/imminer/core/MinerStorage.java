package imminer.core;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;

import pattern.ComboPattern;
import pattern.ItemSet;
import pattern.MinedPattern;
import pattern.SinglePattern;


/**
 * Central storage for the entire data held during the mining process
 * @author suresh
 *
 */
public class MinerStorage {
	private static Logger logger = Logger.getLogger("MinerStorage");	
	
	private static MinerStorage ms = null;	
	public HashMap<String,String> rootIDMapper = new HashMap<String,String>();
	
	public String parentDirectory = "";
	BufferedWriter bwConsolidated;
	
	public void initialize(String parentDirectory)
	{		
		this.parentDirectory = parentDirectory;
		try
		{
			String outfilename = this.parentDirectory + AllConstants.FILE_SEP + AllConstants.CONSOLIDATED_OUTPUT_FILENAME;			
			bwConsolidated = new BufferedWriter(new FileWriter(outfilename));
		}
		catch(IOException ex)
		{
			ex.printStackTrace();
		}
	}
		
	public static MinerStorage getInstance()
	{
		if(ms == null)
			ms = new MinerStorage();			
		return ms;
	}
	
	public void cleanUp()
	{
		try
		{
			this.bwConsolidated.close();
		}
		catch(IOException ex)
		{
			
		}
	}
	
	public void dumpAllPatterns(MethodMiner mm, List<MinedPattern> patternList)
	{
		try
		{		
			bwConsolidated.write("******************\n");
			bwConsolidated.write("All Patterns of method: " + mm.methodName + "\n");
			for(MinedPattern mp : patternList)
			{
				bwConsolidated.write(mp.getKeyString() + ", Support " + mp.getSupportValue() + "\n");				
				Set<String> allUniqueItems = new HashSet<String>();
				getAllUniqueItems(mp, allUniqueItems);					
				for(String item : allUniqueItems)
				{
					bwConsolidated.write("\t (" + item + ") " + mm.decodeMethodId(item) + "\n");
				}				
			}
			
			//Dump all the support values for debugging purposes
			/*bwConsolidated.write("ALL SUPPORT VALUES **************\n");
			Map<String, MinedPattern> supportValues = mm.getSupportValues();
			for(String key : supportValues.keySet())
			{
				bwConsolidated.write(key + " : " + supportValues.get(key).getSupportValue() + "\n");
			}*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("Exception occurred while dumping all itemsets" + ex.getMessage());
		}
	}
	
	/**
	 * Returns all items in a combo pattern
	 * @param cp
	 */
	public void getAllUniqueItems(MinedPattern mp, Set<String> allItems)
	{
		if(mp instanceof SinglePattern)
		{
			allItems.add(((SinglePattern)mp).getCandidate().getItems().iterator().next());
			return;
		}
		
		if(mp instanceof ComboPattern)
		{
			ComboPattern cp = (ComboPattern) mp;
			getAllUniqueItems(cp.getLeft(), allItems);
			getAllUniqueItems(cp.getRight(), allItems);		
		}		
	}
	
	/**
	 * Returns all items in a combo pattern
	 * @param cp
	 */
	public void getAllOperators(MinedPattern mp, Set<PATTERN_TYPE> allOperators)
	{
		if(!(mp instanceof ComboPattern))
			return;
		
		ComboPattern cp = (ComboPattern) mp;
		allOperators.add(cp.getPtype());
		getAllOperators(cp.getLeft(), allOperators);
		getAllOperators(cp.getRight(), allOperators);				
	}
}
