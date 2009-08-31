package edu.ncsu.csc.ase.itutor.core.asm.model;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.softevo.util.io.ObjectReaderWriter;


/**
 * <code>MethodIdMapGenerator</code>.
 * 
 * @author Yoonki Song
 * @version 3:36:29 AM, Oct 20, 2007 EST
 */
public class MethodIdMapGenerator {

	private HashMap<Integer, MethodId> fIdToIdentifierMap;

	private HashMap<MethodId, Integer> fIdentifierToIdMap;

	private int fIdCounter;

	public MethodIdMapGenerator() {
		fIdCounter = 0;
		fIdToIdentifierMap = new HashMap<Integer, MethodId>();
		fIdentifierToIdMap = new HashMap<MethodId, Integer>();
	}

	private MethodIdMapGenerator(HashMap<MethodId, Integer> identifierToIdMap) {
		fIdCounter = 0;
		fIdentifierToIdMap = identifierToIdMap;
		fIdToIdentifierMap = new HashMap<Integer, MethodId>();

		for (MethodId fi : fIdentifierToIdMap.keySet()) {
			Integer id = fIdentifierToIdMap.get(fi);
			fIdToIdentifierMap.put(id, fi);
		}
	}

	public MethodId getMethodIdentifier(int identifier) {
		return fIdToIdentifierMap.get(identifier);
	}

	public int getIdentifier(FieldId fieldIdentifier) {
		return getIdentifier(fieldIdentifier.getClassName(), fieldIdentifier.getFieldName(), fieldIdentifier.getFieldType(),
				fieldIdentifier.getAccess());
	}

	public int getIdentifier(String className, String fieldName, String signature, int access) {
		// System.out.println("== getIdentifier(...)");
		MethodId methodId = new MethodId(className, fieldName, signature, access);
		//MethodIdMapGenerator methodIdMapGenerator = this;
		// try {
		Integer id = fIdentifierToIdMap.get(methodId);
		if (id != null) {
			// System.out.println("=============================================> exist: " + id);
			return id;
		}
		id = fIdCounter++;
		// System.out.println("=============================================> new method id inserted: " + id);
		fIdToIdentifierMap.put(id, methodId);
		fIdentifierToIdMap.put(methodId, id);
		// } catch () {}
		return id;
	}
	
	public MethodId getMethodId(int i) {
		return fIdToIdentifierMap.get(i);
	}

	public synchronized void save(String fileName) throws IOException {
		//*/
		ObjectReaderWriter<HashMap<MethodId, Integer>> writer = new ObjectReaderWriter<HashMap<MethodId, Integer>>();
		writer.write(fIdentifierToIdMap, new File(fileName));
		/*/
		ObjectReaderWriter writer = new ObjReaderWriter();
		writer.write(fIdentifierToIdMap, new File(fileName));
		//*/
	}

	public static MethodIdMapGenerator load(String fileName) throws IOException {
		//*/
		ObjectReaderWriter<HashMap<MethodId, Integer>> reader = new ObjectReaderWriter<HashMap<MethodId, Integer>>();
		return new MethodIdMapGenerator(reader.read(new File(fileName)));
		/*/
		ObjReaderWriter  reader = new ObjReaderWriter();
		return new MethodIdMapGenerator((HashMap)reader.read(new File(fileName)));
		//*/
	}

}
