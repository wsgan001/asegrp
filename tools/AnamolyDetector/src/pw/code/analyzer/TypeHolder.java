package pw.code.analyzer;

import com.google.gdata.data.Content.Type;

import pw.code.analyzer.holder.Holder;
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
	private int elemType = -1; 	//Possible values for this are receiver, arguments, or return.
	private Holder assocHolderObj = null; //Provides a reverse relationship of identifying what is the 
								   //associated holder object.
	
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

	public int getElemType() {
		return elemType;
	}

	public void setElemType(int elemType) {
		this.elemType = elemType;
	}

	public Holder getAssocHolderObj() {
		return assocHolderObj;
	}

	public void setAssocHolderObj(Holder assocHolderObj) {
		this.assocHolderObj = assocHolderObj;
	}
	
	public TypeHolder clone()
	{
		TypeHolder cloneObj = new TypeHolder();
		cloneObj.assocHolderObj = this.assocHolderObj;
		cloneObj.elemType = this.elemType;
		cloneObj.type = this.type;
		cloneObj.var = this.var;
		return cloneObj;
	}
	
}
