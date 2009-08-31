package edu.ncsu.csc.ase.itutor.core.asm.model;

import java.io.Serializable;

public class FieldId implements Serializable, Comparable<FieldId> {
	
	protected String fFieldName;
	protected String fFieldType;
	protected String fClassName;
	protected int fAccess;

	/**
	 * 
	 */
	private static final long serialVersionUID = 896084365172345167L;

	public FieldId(String className, String fieldName, String signature, int access) {
		fFieldName = fieldName;
		fFieldType = signature;
		fClassName = className;
		fAccess = access;
	}
	
	public FieldId(String fieldIdentifier) {
		parse(fieldIdentifier);
	}
	
	protected FieldId() {
		fFieldName = null;
		fFieldType = null;
		fClassName = null;
		fAccess = -1;
	}
	
	private void parse(String fieldIdentifier) {
		fClassName = fieldIdentifier.substring(0, fieldIdentifier.lastIndexOf('.'));
		fFieldName = fieldIdentifier.substring(fieldIdentifier.lastIndexOf('.') + 1, fieldIdentifier.indexOf('('));
		fFieldType = fieldIdentifier.substring(fieldIdentifier.indexOf('('), fieldIdentifier.length());
	}
	
	public static FieldId parseFromIdentifier(String fieldIdentifier) {
		return new FieldId(fieldIdentifier);
	}

	/**
	 * @return the fFieldName
	 */
	public String getFieldName() {
		return fFieldName;
	}

	/**
	 * @param fieldName the fFieldName to set
	 */
	public void setFieldName(String fieldName) {
		fFieldName = fieldName;
	}

	/**
	 * @return the fSignature
	 */
	public String getFieldType() {
		return fFieldType;
	}

	/**
	 * @param signature the fSignature to set
	 */
	public void setFieldType(String fieldType) {
		fFieldType = fieldType;
	}

	/**
	 * @return the fClassName
	 */
	public String getClassName() {
		return fClassName;
	}

	/**
	 * @param className the fClassName to set
	 */
	public void setClassName(String className) {
		fClassName = className;
	}

	/**
	 * @return the fAccess
	 */
	public int getAccess() {
		return fAccess;
	}

	/**
	 * @param access the fAccess to set
	 */
	public void setAccess(int access) {
		fAccess = access;
	}

	@Override
	public int compareTo(FieldId other) {
		if (other.fClassName.compareTo(fClassName) > 0) {
			return 1;
		} else if (other.fClassName.compareTo(fClassName) < 0) {
			return -1;
		}
		
		if (other.fFieldType.compareTo(fFieldType) > 0) {
			return 1;
		} else if (other.fFieldType.compareTo(fFieldType) < 0) {
			return -1;
		}
		
		if (other.fFieldName.compareTo(fFieldName) > 0) {
			return 1;
		} else if (other.fFieldName.compareTo(fFieldName) < 0) {
			return -1;
		}
		
		if (other.fAccess > fAccess) {
			return 1;
		} else if (other.fAccess < fAccess) {
			return -1;
		}
		
		return 0;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + fAccess;
		result = prime * result + ((fClassName == null) ? 0 : fClassName.hashCode());
		result = prime * result + ((fFieldName == null) ? 0 : fFieldName.hashCode());
		result = prime * result + ((fFieldType == null) ? 0 : fFieldType.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		final FieldId other = (FieldId) obj;
		if (fAccess != other.fAccess)
			return false;
		if (fClassName == null) {
			if (other.fClassName != null)
				return false;
		} else if (!fClassName.equals(other.fClassName))
			return false;
		if (fFieldName == null) {
			if (other.fFieldName != null)
				return false;
		} else if (!fFieldName.equals(other.fFieldName))
			return false;
		if (fFieldType == null) {
			if (other.fFieldType != null)
				return false;
		} else if (!fFieldType.equals(other.fFieldType))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return (new StringBuilder(String.valueOf(fClassName))).append(".").append(fFieldName).append(fFieldType).toString();
	}

}
