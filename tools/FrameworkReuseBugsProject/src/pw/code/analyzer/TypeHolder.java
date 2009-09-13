package pw.code.analyzer;

import pw.common.CommonConstants;

/**
 * This class holds an object type and its associated variable name
 * Mainly used for holding instances of receiver types, argument types, and return types.
 * The variable name and object name are set to unknown if the tool is
 * not able to extract them.
 * @author suresh_thummalapenta
 *
 */
public class TypeHolder {	
	private String type = CommonConstants.getUniqueIDForUnknown();
	public String var = CommonConstants.getUniqueIDForUnknown();	
	
	public TypeHolder() {
		
	}
	
	public TypeHolder(String type) {
		
		setType(type);
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
		
		//As we are not handling array types and parameterized types they
		//have to be suppressed to normal types.
		if(type != null) {
			int indexOfLeft = -1;
			if((indexOfLeft = type.indexOf("[")) != -1) {
				type = type.substring(0, indexOfLeft);
			}
			type = type.trim();
			
			if((indexOfLeft = type.indexOf("<")) != -1) {
				type = type.substring(0, indexOfLeft);
			}
			type = type.trim();
		}
		
		this.type = type;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}
	
}
