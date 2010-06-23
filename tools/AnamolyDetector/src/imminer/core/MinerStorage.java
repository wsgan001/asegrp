package imminer.core;

import imminer.pattern.AndOnlyPattern;
import imminer.pattern.ComboPattern;
import imminer.pattern.MinedPattern;
import imminer.pattern.SinglePattern;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


/**
 * Central storage for the entire data held during the mining process
 * @author suresh
 *
 */
public class MinerStorage {
	private static Logger logger = Logger.getLogger("MinerStorage");	
	
	private static MinerStorage ms = null;	
	public HashMap<String,String> rootIDMapper = new HashMap<String,String>();
	 
	public HashMap<String, MethodSpecificIds> methodSpecificIDMapper = new HashMap<String, MethodSpecificIds>();
	public HashMap<String, List<MinedPattern>> SinglePatterns = new HashMap<String, List<MinedPattern>>();
	public HashMap<String, List<MinedPattern>> AndPatterns = new HashMap<String, List<MinedPattern>>();
	public HashMap<String, List<MinedPattern>> OrPatterns = new HashMap<String, List<MinedPattern>>();
	public HashMap<String, List<MinedPattern>> XorPatterns = new HashMap<String, List<MinedPattern>>();
	public HashMap<String, List<MinedPattern>> ComboPatterns = new HashMap<String, List<MinedPattern>>();
	
	//Stores all method Ids that have the same patterns across all pattern formats
	//Usually this happens with the methods that just have one item among all patterns
	//These patterns are not helpful for showing the benefits of alternative patterns
	public HashSet<String> methodIdsSamePFormats = new HashSet<String>();
	
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
	
	public void addToRootMapper(String methodId, String methodName)
	{
		this.rootIDMapper.put(methodId, methodName);
	}
	
	/**
	 * Get the associated method specific ID mapper
	 * @param key
	 * @return
	 */
	public MethodSpecificIds getMethodSpecificIdMapper(String key)
	{
		MethodSpecificIds msi = this.methodSpecificIDMapper.get(key);
		if(msi == null)
		{
			msi = new MethodSpecificIds();
			this.methodSpecificIDMapper.put(key, msi);
		}		
		return msi;
	}
	
	/**
	 * Stores a pattern in a central location
	 * @param methodId
	 * @param mp
	 * @param ptype
	 */
	public void storePattern(String methodId, MinedPattern mp, PatternType ptype)
	{
		if(ptype == PatternType.SINGLE_PATTERN)
		{
			List<MinedPattern> mplist = this.SinglePatterns.get(methodId);
			if(mplist == null)
			{
				mplist = new ArrayList<MinedPattern>();
				this.SinglePatterns.put(methodId, mplist);
			}
			mplist.add(mp);
			return;
		}
		
		if(ptype == PatternType.AND_PATTERN)
		{
			List<MinedPattern> mplist = this.AndPatterns.get(methodId);
			if(mplist == null)
			{
				mplist = new ArrayList<MinedPattern>();
				this.AndPatterns.put(methodId, mplist);
			}
			mplist.add(mp);			
			return;
		}
		
		if(ptype == PatternType.OR_PATTERN)
		{
			List<MinedPattern> mplist = this.OrPatterns.get(methodId);
			if(mplist == null)
			{
				mplist = new ArrayList<MinedPattern>();
				this.OrPatterns.put(methodId, mplist);
			}
			mplist.add(mp);			
			return;			
		}
		
		if(ptype == PatternType.XOR_PATTERN)
		{
			List<MinedPattern> mplist = this.XorPatterns.get(methodId);
			if(mplist == null)
			{
				mplist = new ArrayList<MinedPattern>();
				this.XorPatterns.put(methodId, mplist);
			}
			mplist.add(mp);			
			return;			
		}
		
		if(ptype == PatternType.COMBO_PATTERN)
		{
			List<MinedPattern> mplist = this.ComboPatterns.get(methodId);
			if(mplist == null)
			{
				mplist = new ArrayList<MinedPattern>();
				this.ComboPatterns.put(methodId, mplist);
			}
			mplist.add(mp);			
			return;			
		}
	}
	
	/**
	 * Prunes all methods that have just one item among patterns in all 
	 * four formats. Although these patterns are useful, they are not helpful
	 * in showing the advantages of alternative patterns
	 */
	public void pruneSingleItemMethods()	
	{
		//browse through all methods. If there are more one items
		//in any pattern format of the method that forward to next method
		for(String methodId : rootIDMapper.keySet())
		{
			Set<String> allUniqueItems = new HashSet<String>();
			
			List<MinedPattern> andPatterns = this.AndPatterns.get(methodId);
			for(MinedPattern mp : andPatterns)
			{
				this.getAllUniqueItems(mp, allUniqueItems);
			}
			
			if(allUniqueItems.size() != 1)
				continue;
			allUniqueItems.clear();
			List<MinedPattern> orPatterns = this.OrPatterns.get(methodId);
			for(MinedPattern mp : orPatterns)
			{
				this.getAllUniqueItems(mp, allUniqueItems);
			}
			
			if(allUniqueItems.size() != 1)
				continue;
			allUniqueItems.clear();
			List<MinedPattern> xorPatterns = this.XorPatterns.get(methodId);
			for(MinedPattern mp : xorPatterns)
			{
				this.getAllUniqueItems(mp, allUniqueItems);
			}
			
			if(allUniqueItems.size() != 1)
				continue;
			allUniqueItems.clear();
			List<MinedPattern> comboPatterns = this.ComboPatterns.get(methodId);
			for(MinedPattern mp : comboPatterns)
			{
				this.getAllUniqueItems(mp, allUniqueItems);
			}
			
			if(allUniqueItems.size() != 1)
				continue;
			
			this.methodIdsSamePFormats.add(methodId);
		}
	}
	
	/**
	 * Function that dumps all patterns. Also eliminates a pattern if it is subsumed by its parent pattern
	 * @param patternList
	 */
	public void dumpPatterns()
	{
		dumpPatternsOfType(this.AndPatterns, this.SinglePatterns, PatternType.AND_PATTERN);
		dumpPatternsOfType(this.OrPatterns, this.SinglePatterns, PatternType.OR_PATTERN);
		dumpPatternsOfType(this.XorPatterns, this.SinglePatterns, PatternType.XOR_PATTERN);
		dumpPatternsOfType(this.ComboPatterns, this.SinglePatterns, PatternType.COMBO_PATTERN);
	}
	
	private void dumpPatternsOfType(HashMap<String, List<MinedPattern>> allPatterns, HashMap<String, 
			List<MinedPattern>> singlepatterns, PatternType ptype)
	{
		try
		{
			bwConsolidated.write("******************************************************\n");
			bwConsolidated.write("PATTERNS OF TYPE: " + ptype.toString() + "\n");
			bwConsolidated.write("******************************************************\n");
			
			String fileName = ms.parentDirectory + AllConstants.FILE_SEP + ptype.toString() + "_Patterns.csv";			
						
			//Preparing the consolidated pattern list across all method ids for preparing
			//the final list of patterns.
			List<MinedPattern> consolidatedList = new ArrayList<MinedPattern>();
			
			//Traversing individual patterns for filtering out
			//those patterns that are subsumed by others
			for(String methodId : this.rootIDMapper.keySet())
			{
				List<MinedPattern> methodSpecificP = allPatterns.get(methodId);			
				consolidatedList.addAll(methodSpecificP);
			}
			
			//Dumping all patterns to respective output files			
			BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
			bw.write("ID, APIName, Pattern, Support, HasAndPatterns, UsedForDefectDetection\n");
			Collections.sort(consolidatedList);
			for(MinedPattern mp : consolidatedList)
			{
				StringBuffer sb = new StringBuffer();
				sb.append(mp.getMethodId() + ",");
				sb.append("\"" + this.rootIDMapper.get(mp.getMethodId()) + "\",");
				sb.append(mp.getKeyString() + ",");
				sb.append(mp.getSupportValue() + ",");				
				if(this.AndPatterns.get(mp.getMethodId()).size() > 0) {
					sb.append("1,");
				}
				else {
					sb.append("0,");
				}
				
				if(this.methodIdsSamePFormats.contains(mp.getMethodId()))
				{
					sb.append("0\n");
				}
				else
				{
					sb.append("1\n");
				}
				
				bw.write(sb.toString());				
				String rootMethodId = mp.getMethodId();
				bwConsolidated.write("\"" + this.rootIDMapper.get(rootMethodId) + "\", " +
						mp.getKeyString() + ", Support " + mp.getSupportValue() + "\n");				
				Set<String> allUniqueItems = new HashSet<String>();
				getAllUniqueItems(mp, allUniqueItems);
				MethodSpecificIds msi = this.methodSpecificIDMapper.get(rootMethodId);
				for(String item : allUniqueItems)
				{
					bwConsolidated.write("\t (" + item + ") " + msi.getConditionCheck(item) + "\n");
				}
			}			
			bw.close();
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("Error occurred while dumping patterns " + ex.getMessage());
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
		
		if(mp instanceof AndOnlyPattern)
		{
			allItems.addAll(mp.getCandidate().getItems());
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
	public void getAllOperators(MinedPattern mp, Set<PatternType> allOperators)
	{
		if(!(mp instanceof ComboPattern))
			return;
		
		ComboPattern cp = (ComboPattern) mp;
		allOperators.add(cp.getPtype());
		getAllOperators(cp.getLeft(), allOperators);
		getAllOperators(cp.getRight(), allOperators);				
	}
}
