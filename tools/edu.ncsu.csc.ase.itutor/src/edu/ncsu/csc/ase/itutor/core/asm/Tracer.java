/*
 *
 */
package edu.ncsu.csc.ase.itutor.core.asm;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Methods in this class are called by injected code to write trace events. <BR>
 * This class continously writes a trace of events into file
 * <code>trace.out</code>. If a suffix file (a file named
 * <code>suffix_*</code> exists, then the suffix of the filename (e.g. file
 * name suffix_0 -> suffix = 0 -> file name = trace.out_0) is appended to the
 * name of the outputfile.
 * 
 * @author dallmeier
 */
public class Tracer {

	/**
	 * The stream used to write events. <BR>
	 */
	private static DataOutputStream dataOutputStream = null;

	/**
	 * The size for the write buffer. <BR>
	 */
	private static final int BUFFERSIZE = 16777216;

	/**
	 * The base name for the output file.
	 */
	public static final String OUTPUTFILENAME = "trace.out";

	/**
	 * Constant for object creation event.
	 */
	public static final short EV_OBJECTCREATION = 1;

	/**
	 * Constant for field write event.
	 */
	public static final short EV_FIELDWRITE = 2;

	/**
	 * Constant for static field write event.
	 */
	public static final short EV_STATICFIELDWRITE = 3;

	/**
	 * Constant for start of dynamic method event.
	 */
	public static final short EV_DYNAMICMETHODSTART = 4;

	/**
	 * Constant for start of static method event.
	 */
	public static final short EV_STATICMETHODSTART = 5;

	/**
	 * Constant for start of dynamic method event.
	 */
	public static final short EV_DYNAMICMETHODEND = 6;

	/**
	 * Constant for end of static method end event.
	 */
	public static final short EV_STATICMETHODEND = 7;

	/**
	 * Constant for array creation event.
	 */
	public static final short EV_ARRAYCREATED = 8;

	/**
	 * Constant for array modification event.
	 */
	public static final short EV_ARRAYMODIFIED = 9;

	/**
	 * Constant indicating access to a static variable or execution of a static
	 * method.
	 */
	public static final int STATIC = -1;

	/**
	 * The suffix appended to the name of the trace output file.
	 */
	private static String suffix = "";

	/**
	 * Mutex object that protects access to the <code>DataOutputStream</code>
	 */
	private static Object mutex = null;

	/*private static HashMap<Integer, Stack<Integer>> threadIdToActiveMethodStackMap = new HashMap<Integer, Stack<Integer>>();*/

	private static boolean debug = false; // false
	
	/**
	 * Static initializer creates the output file and adds a shutdown hook such
	 * that the class is notified when the vm exits.
	 */
	static {
		String fileName;

		// System.out.println("Using up 4 date version of Tracer.");
		mutex = new Object();
		try {
			readSuffix();
			if (suffix.length() > 0) {
				fileName = OUTPUTFILENAME + "_" + suffix;
			} else {
				fileName = OUTPUTFILENAME;
			}
			dataOutputStream = new DataOutputStream(new BufferedOutputStream(
					new FileOutputStream(fileName), BUFFERSIZE));
			Runtime.getRuntime().addShutdownHook(new Thread(new ShutdownThread()));
		} catch (IOException exception) {
			System.out.println("Unable to create trace outputStream.");
			exception.printStackTrace(System.out);
			System.exit(5);
		}
	}

	/**
	 * Tries to find a suffix file and reads the suffix for the trace file name if
	 * any.
	 */
	private static void readSuffix() {
		File[] children;
		String suffixFileName;

		children = new File("./").listFiles();
		for (int counter = 0; counter < children.length; counter++) {
			if (children[counter].getName().startsWith("suffix_")) {
				suffixFileName = children[counter].getName();
				suffix = suffixFileName.substring(7, suffixFileName.length());
			}
		}
	}

	/**
	 * This method is used to safely write events to the output stream. It is
	 * thread safe. If anything goes wrong writing to the stream, the vm is
	 * terminated with error code <code>5</code>.
	 * 
	 * @param eventId
	 *          the id of the event to be written
	 * @param value
	 *          the value of the event
	 */
	private static void writeEvent(short eventId, int value) {
		try {
			synchronized (mutex) {
				if (dataOutputStream != null) {
					dataOutputStream.writeShort(eventId);
					dataOutputStream.writeInt(System.identityHashCode(Thread
							.currentThread()));
					dataOutputStream.writeInt(value);
				}
			}
		} catch (IOException exception) {
			System.out.println("An exception occured writing to the trace.");
			exception.printStackTrace(System.out);
			System.exit(5);
		}
	}

	/**
	 * This method writes events that have 2 parameters.
	 * 
	 * @param eventId
	 *          the id of the event
	 * @param value1
	 *          the first value
	 * @param value2
	 *          the second value
	 */
	private static void writeEvent(short eventId, int value1, Object object) {
		try {
			synchronized (mutex) {
				if (dataOutputStream != null) {
					//System.out.println("[writeEvent]");
					dataOutputStream.writeShort(eventId);
					dataOutputStream.writeInt(System.identityHashCode(Thread
							.currentThread()));
					dataOutputStream.writeInt(value1);
					dataOutputStream.writeInt(System.identityHashCode(object));
					dataOutputStream.writeUTF(object.getClass().getName());
				}
			}
		} catch (IOException exception) {
			System.out.println("An exception occured writing to the trace.");
			exception.printStackTrace(System.out);
			System.exit(5);
		}
	}
	
	/**
	 * Called whenever a field is written in an object.
	 * 
	 * @param object
	 *          the modified object
	 */
	public static void fieldWritten(Object object) {
		if (debug) {
			System.out.println("Field written.");
		}
		writeEvent(EV_FIELDWRITE, System.identityHashCode(object));
	}

	/**
	 * Called whenever a new object is created. This method is called after the
	 * constructor call.
	 * 
	 * @param object
	 *          the object that is created
	 */
	public static void objectCreated(Object object) {
		if (debug) {
			System.out.println("Object created.");
		}
		writeEvent(EV_OBJECTCREATION, System.identityHashCode(object));
	}

	/**
	 * Called whenever a dynamic method is started.
	 * 
	 * @param methodId
	 *          the id of the method that is started
	 * @param thisObject
	 *          the object the method is invoked on
	 */
	public static void dynamicMethodStarted(int methodId, Object thisObject) {
		if (debug) {
			//System.out.println("Method started " + methodId);
		}
		writeEvent(EV_DYNAMICMETHODSTART, methodId, thisObject);
	}
	
	/**
	 * Called whenever a static method is started.
	 * 
	 * @param methodId
	 *          the id of the method that is started
	 */
	public static void staticMethodStarted(int methodId) {
		if (debug) {
			System.out.println("Static method started " + methodId);
		}
		writeEvent(EV_STATICMETHODSTART, methodId);
	}

	/**
	 * Called whenever a dynamic method has just finished execution.
	 * 
	 * @param methodId
	 *          the id of the method
	 */
	public static void dynamicMethodEnded(int methodId) {
		if (debug) {
			System.out.println("Dynamic method ended " + methodId);
		}
		writeEvent(EV_DYNAMICMETHODEND, methodId);
	}

	/**
	 * Called whenever a static method has just finished execution.
	 * 
	 * @param methodId
	 *          the id of the method
	 */
	public static void staticMethodEnded(int methodId) {
		if (debug) {
			System.out.println("Static method ended " + methodId);
		}
		writeEvent(EV_STATICMETHODEND, methodId);
	}

	/**
	 * Called whenever a static field was written.
	 */
	public static void staticFieldWritten() {
		if (debug) {
			System.out.println("Static field written.");
		}
		writeEvent(EV_STATICFIELDWRITE, STATIC);
	}

	/**
	 * Called whenever a new array was created.
	 * 
	 * @param object
	 *          the array created
	 */
	public static void arrayCreated(Object object) {
		if (debug) {
			System.out.println("Array created.");
		}
		writeEvent(EV_ARRAYCREATED, System.identityHashCode(object));
	}

	/**
	 * Called whenever an entry in an array is modified.
	 * 
	 * @param object
	 *          the array that was modified
	 */
	public static void arrayModified(Object object) {
		if (debug) {
			System.out.println("Array modified.");
		}
		writeEvent(EV_ARRAYMODIFIED, System.identityHashCode(object));
	}

	/**
	 * This method is called to record creation of multi dimensional arrays. This
	 * is neccessary because otherwise we don't know about the creation of all
	 * arrays.
	 * 
	 * @param array
	 *          the object array, possibly containing more object arrays
	 * @param depth
	 *          the recursion depth
	 */
	public static void addArraysRecursively(Object[] array, int depth) {
		for (int counter = 0; counter < array.length; counter++) {
			arrayCreated(array[counter]);
			if (depth > 0) {
				addArraysRecursively((Object[]) array[counter], depth - 1);
			}
		}
	}

	/**
	 * Gets the suffix used by the tracer.
	 */
	public static String getSuffix() {
		return suffix;
	}

	/**
	 * This class implements the shutdown hook needed to assure that all trace
	 * information gets written at vm shutdown time.
	 * 
	 * @author dallmeier
	 */
	private static class ShutdownThread implements Runnable {

		/**
		 * Flushes the stream and closes it.
		 */
		public void run() {
			// System.out.println("Running shutdown thread version 1");
			synchronized (mutex) {
				try {
					if (dataOutputStream != null) {
						dataOutputStream.flush();
						dataOutputStream.close();
						dataOutputStream = null;
					}
				} catch (IOException finalizeException) {
					System.out
							.println("An exception occured finalizing trace output stream.");
					finalizeException.printStackTrace(System.out);
				}
			}
		}
	}

}
