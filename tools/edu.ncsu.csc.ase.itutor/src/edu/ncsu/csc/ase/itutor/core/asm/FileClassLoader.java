/**
 * 
 */
package edu.ncsu.csc.ase.itutor.core.asm;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author Yoonki
 *
 */
public class FileClassLoader extends ClassLoader {
	
	private String fFileName;
	
	public FileClassLoader(String fileName) {
		fFileName = fileName;
	}
	
	public InputStream getResourceAsStream() {
		return ClassLoader.getSystemResourceAsStream(fFileName);
	}
	
	protected Class<?> findClass(String name) throws ClassNotFoundException {
		byte[] buf = readBytes(new File(name));
		if (buf == null) {
			throw new ClassNotFoundException(name);
		}
		return defineClass(name, buf, 0, buf.length);
	}
	
	public static byte[] readBytes(File file) {
		if (file == null || !file.exists()) {
			return null;
		}
		
		InputStream stream = null;
		try {
			stream = new BufferedInputStream(new FileInputStream(file));
			int size = 0;
			byte[] buf = new byte[10];
			while (true) {
				int count = stream.read(buf, size, buf.length - size);
				if (count < 0) {
					break;
				}
				size += count;
				if (size < buf.length) {
					break;
				}
				byte[] newBuf = new byte[size + 10];
				System.arraycopy(buf, 0, newBuf, 0, size);
				buf = newBuf;
			}
			byte[] result = new byte[size];
			System.arraycopy(buf, 0, result, 0, size);
			return result;
		} catch (Exception e) {
			return null;
		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (IOException e) {
				return null;
			}
		}
	}
}
