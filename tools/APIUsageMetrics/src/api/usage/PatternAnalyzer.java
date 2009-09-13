package api.usage;

import java.util.HashMap;
import java.util.Iterator;

/**
 *Class for identifying templates, hooks and meta-patterns from the data 
 */
public class PatternAnalyzer {

	private HashMap<String, LibClassHolder> libClsMap;
	
	public PatternAnalyzer(HashMap<String, LibClassHolder>libClsMap)
	{
		this.libClsMap = libClsMap;
	}
	
	
	public void identifyTemplateAndHookMethods()
	{
		for(Iterator iter = libClsMap.values().iterator(); iter.hasNext();)
		{
			//LibClassHolder lch = (LibClassHolder) iter.next();
			
			
			
			
		}
	}
	
	
	
	
	
}
