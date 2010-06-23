package imminer.migrator;

import java.util.HashMap;
import java.util.List;

import minebugs.core.RepositoryAnalyzer;
import minebugs.srclibhandlers.ExternalObject;

import org.apache.log4j.Logger;

import pw.code.analyzer.TypeHolder;
import pw.code.analyzer.holder.MethodInvocationHolder;
import pw.common.CommonConstants;
import imminer.core.IMMiner;
import imminer.core.MethodSpecificIds;
import imminer.core.MinerStorage;
import imminer.pattern.AndOnlyPattern;
import imminer.pattern.ComboPattern;
import imminer.pattern.MinedPattern;
import imminer.pattern.SinglePattern;

/**
 * Migrates patterns from the IMMiner project into AnamolyDetector project
 * @author suresh
 *
 */
public class PatternMigrator {

	static private Logger logger = Logger.getLogger("PatternMigrator");
	
	/**
	 * Function that invokes IMMiner project's entry point for each 
	 * item in parentDir and gathers back the entire data 
	 * and transforms into local patterns for all mined
	 * patterns
	 */
	public void startProcess()
	{
		try
		{
			long beginTime = System.currentTimeMillis();			
			IMMiner mm = new IMMiner();	
			
			for(String pdir : CommonConstants.inputPatternDirs)
			{
				MinerStorage ms = mm.startProcess(pdir);
				
				int methodsFound = 0;
				
				//browse through each API in the miner storage
				for(String methodId : ms.rootIDMapper.keySet())
				{
					String methodName = ms.rootIDMapper.get(methodId);
					
					//Locate external method invocationholder for this method,
					MethodInvocationHolder mih = getExternalMIH(methodName);
					if(mih == null)
					{
						logger.error("Ignoring patterns of method invocation: " + methodName + ", since this is not found in input library");
						continue;
					}
					
					if(ms.methodIdsSamePFormats.contains(methodId))
					{
						logger.error("Ignoring the patterns of \"" + methodName 
								+ "\" for defect detection, since they do not help in showing the benefits of alternative patterns");
						mih.hasSamePatternFormats = true;
					}
					
					migratePatternsOfMethod(ms, methodId, mih);
					methodsFound++;
				}
				
				logger.warn("Total number of methods with patterns: " + ms.rootIDMapper.size());
				logger.warn("Number of methods found in the current library (all remaining patterns are ignored): " + methodsFound);				
			}
			
			long endTime = System.currentTimeMillis();
			logger.warn("Time taken for mining patterns: " + (endTime - beginTime)/1000 + " sec ");			
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			logger.error("Error occurred while migrating patterns " + ex.getMessage());
		}
	}
	
	/**
	 * migrates all patterns AND, OR, XOR, Combo from IMMiner project
	 * @param ms
	 * @param methodId
	 * @param mih
	 */
	private void migratePatternsOfMethod(MinerStorage ms, String methodId, MethodInvocationHolder mih)
	{
		//Getting the method specific Ids
		MethodSpecificIds msi = ms.methodSpecificIDMapper.get(methodId);
		
		//migrating all patterns
		migrateComboPatterns(ms.SinglePatterns.get(methodId), mih.singlePatternList, msi);	
		migrateComboPatterns(ms.AndPatterns.get(methodId), mih.andPatternList, msi);
		migrateComboPatterns(ms.OrPatterns.get(methodId), mih.orPatternList, msi);
		migrateComboPatterns(ms.XorPatterns.get(methodId), mih.xorPatternList, msi);
		migrateComboPatterns(ms.ComboPatterns.get(methodId), mih.comboPatternList, msi);
	}
	
	/**
	 * Migrates all elements from MinedPattern to ADMinedPattern
	 * @param cplist
	 * @param adcplist
	 */
	private void migrateComboPatterns(List<MinedPattern> cplist, List<ADMinedPattern> adcplist, MethodSpecificIds msi)
	{
		if(cplist == null)
			return;
		
		for(MinedPattern mp : cplist)
		{
			if(mp instanceof SinglePattern)
			{
				SinglePattern sp = (SinglePattern) mp;			
				ADSinglePattern adsp = new ADSinglePattern(sp, msi);
				adcplist.add(adsp);
				continue;
			}
			
			if(mp instanceof AndOnlyPattern)
			{
				AndOnlyPattern ap = (AndOnlyPattern) mp;
				ADAndPattern aap = new ADAndPattern(ap, msi);
				adcplist.add(aap);
				continue;
			}
			
			if(mp instanceof ComboPattern)
			{
				ComboPattern cp = (ComboPattern) mp;
				ADComboPattern acp = ADComboPattern.initializeComboPattern(cp, msi);
				adcplist.add(acp);
				continue;
			}		
		}		
	}
	
	/**
	 * Gives external method invocation holder
	 * @param methodName
	 * @return
	 */
	private MethodInvocationHolder getExternalMIH(String methodName)
	{
		int firstColon = methodName.indexOf(',');
		String receiverObj = methodName.substring(0, firstColon);
		String argumentStr = methodName.substring(firstColon + 1);
		MethodInvocationHolder inpLibMIH = MethodInvocationHolder.parseFromString(argumentStr, new TypeHolder(receiverObj));
		
		//Read the entire pattern from the input file. Load the pattern into MethodinvocationHolder
		HashMap<String, ExternalObject> externalObjectSet = RepositoryAnalyzer.getInstance().getExternalObjects(); 
		ExternalObject eeObj = externalObjectSet.get(receiverObj);
		
		if(eeObj == null) {
			return null;
		}
		
		MethodInvocationHolder eeMIH = eeObj.containsMI(inpLibMIH);	
		if(eeMIH == null) {
			return null;
		}
		
		return eeMIH;
	}	
}
