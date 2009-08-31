package edu.ncsu.csc.ase.itutor.core.asm.model;

import java.io.Serializable;
import java.util.Set;
import java.util.TreeSet;


public class MethodId implements Serializable {

	protected String fMethodName;

	protected String fSignature;

	protected String fClassName;
	
	protected Set<FieldId> fWrittenFields;
	
	protected Set<FieldId> fReadFields;

	protected int fAccess;

	/**
	 * 
	 */
	private static final long serialVersionUID = 896084365172345167L;

	public MethodId(String className, String fieldName, String signature, int access) {
		fMethodName = fieldName;
		fSignature = signature;
		fClassName = className;
		fAccess = access;
		fWrittenFields = new TreeSet<FieldId>();
		fReadFields = new TreeSet<FieldId>();
	}

	public MethodId(String fieldIdentifier) {
		parse(fieldIdentifier);
	}

	protected MethodId() {
		fMethodName = null;
		fSignature = null;
		fClassName = null;
		fAccess = -1;
		fWrittenFields = new TreeSet<FieldId>();
		fReadFields = new TreeSet<FieldId>();
	}

	private void parse(String fieldIdentifier) {
		fClassName = fieldIdentifier.substring(0, fieldIdentifier.lastIndexOf('.'));
		fMethodName = fieldIdentifier.substring(fieldIdentifier.lastIndexOf('.') + 1, fieldIdentifier.indexOf('('));
		fSignature = fieldIdentifier.substring(fieldIdentifier.indexOf('('), fieldIdentifier.length());
	}

	public static MethodId parseFromIdentifier(String fieldIdentifier) {
		return new MethodId(fieldIdentifier);
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		return toString().equals(obj.toString());
	}

	@Override
	public String toString() {
		return (new StringBuilder(String.valueOf(fClassName))).append(".").append(fMethodName).append(fSignature).toString();
	}

	/**
	 * @return the fMethodName
	 */
	public String getMethodName() {
		return fMethodName;
	}

	/**
	 * @param fieldName
	 *            the fFieldName to set
	 */
	public void setMethodName(String methodName) {
		fMethodName = methodName;
	}

	/**
	 * @return the fSignature
	 */
	public String getSignature() {
		return fSignature;
	}

	/**
	 * @param signature
	 *            the fSignature to set
	 */
	public void setSignature(String signature) {
		fSignature = signature;
	}

	/**
	 * @return the fClassName
	 */
	public String getClassName() {
		return fClassName;
	}

	/**
	 * @param className
	 *            the fClassName to set
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
	 * @param access
	 *            the fAccess to set
	 */
	public void setAccess(int access) {
		fAccess = access;
	}

	/**
	 * @return the fWrittenFields
	 */
	public Set<FieldId> getWrittenFields() {
		return fWrittenFields;
	}

	/**
	 * @param writtenFields the fWrittenFields to set
	 */
	public void setWrittenFields(Set<FieldId> writtenFields) {
		fWrittenFields = writtenFields;
	}

	/**
	 * @return the fReadFields
	 */
	public Set<FieldId> getReadFields() {
		return fReadFields;
	}

	/**
	 * @param readFields the fReadFields to set
	 */
	public void setReadFields(Set<FieldId> readFields) {
		fReadFields = readFields;
	}

}
