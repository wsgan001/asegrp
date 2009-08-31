/**
 * 
 */
package edu.ncsu.csc.ase.itutor.core.asm;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.eclipse.jdt.core.Signature;
import org.objectweb.asm.tree.MethodNode;

import edu.ncsu.csc.ase.itutor.core.asm.model.DecompiledClass;
import edu.ncsu.csc.ase.itutor.core.asm.model.MethodIdMapGenerator;
import edu.ncsu.csc.ase.itutor.core.asm.visitors.DecompilerClassVisitor;

/**
 * @author Yoonki Song
 * 
 */
public class ASMProxy {

	private static ASMProxy INSTANCE;

	public static String TRACECLASSNAME = "edu.ncsu.csc.ase.itutur.core.asm.Tracer";
	
	public DecompiledClass fDecompiledClass = null;

	private MethodIdMapGenerator identifierMapGenerator = null;

	private ASMProxy() {
	}

	public static synchronized ASMProxy getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ASMProxy();
		}
		return INSTANCE;
	}

	public void decompile() {
		System.out.println("Running...");
		decompileBytecode("d:/BinaryType.class");
	}
	
	public void decompile(String fileName) {
		System.out.println("Running...");
		decompileBytecode(fileName);
	}

	private void decompileBytecode(String fileName) {
		System.out.println("decompileBytecode: " + fileName);
		FileClassLoader cl = null;
		InputStream is = null;
		try {
			cl = new FileClassLoader(fileName);
			is = new FileInputStream(fileName);
			fDecompiledClass = DecompilerClassVisitor.getDecompiledClass(is, null, null, new BitSet(), cl);
		} catch (IOException e) {
			 e.printStackTrace();
		}
		
		toString();
	}
	
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Class Name: " + getName()).append("\n");
		builder.append("Super Name: " + getSuperName()).append("\n");
		builder.append("Interfaces: [");
		int i = 0;
		for (String intf : getInterfaces()) {
			if (i++ >= 1) {
				builder.append(", ");
			}
			builder.append(intf);
		}
		builder.append("]").append("\n");
		
		builder.append("Methods:").append("\n");
		for (String method : getMethods()) {
			builder.append("\t" + method + "\n");
		}
		
		System.out.println(builder.toString());
		return builder.toString();
	}
    
    public String getName() {
    	return fDecompiledClass.getClassNode().name;
    }
        
    public String getSuperName() {
    	return fDecompiledClass.getClassNode().superName;
    }
    
    public List<String> getInterfaces() {
    	List<String> intfs = new ArrayList<String>();
    	for (Object o : fDecompiledClass.getClassNode().interfaces) {
    		intfs.add(o.toString());
    	} 
    	return intfs;
    }
    
    public List<String> getMethods() {
    	List<String> methods = new ArrayList<String>();
    	for (Object o : fDecompiledClass.getClassNode().methods) {
    		if (o instanceof MethodNode) {
    			MethodNode m = (MethodNode)o;
    			methods.add(m.name.replace("/", ".") + " : " + Signature.toString(m.desc));
    		}
    	} 
    	return methods;
    }

}
