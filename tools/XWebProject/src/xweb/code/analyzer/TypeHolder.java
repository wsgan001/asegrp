package xweb.code.analyzer;

import xweb.common.CommonConstants;

/**
 * This class holds an object type and its associated variable name
 * The variable name and object name are set to unknown if the tool is
 * not able to extract them.
 * @author suresh_thummalapenta
 *
 */
public class TypeHolder {	
	public String type = CommonConstants.getUniqueIDForUnknown();
	public String var = CommonConstants.getUniqueIDForUnknown();	
	
	public TypeHolder() {
		
	}
	
	public TypeHolder(String type) {
		this.type = type;
	}
	
	public String toString() {
		return type;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof TypeHolder)
		{
			TypeHolder otherTh = (TypeHolder) obj;
		
			if(this.type.startsWith(CommonConstants.unknownType) && otherTh.type.startsWith(CommonConstants.unknownType))
				return true;
			
			return this.type.equals(otherTh.type);
		}
		
		return false;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
}
