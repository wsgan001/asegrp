package rpt.ncsu.transaction.analysis;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.apache.xerces.parsers.DOMParser;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import rpt.ncsu.transaction.util.FileOperators;
import rpt.ncsu.transaction.util.TempInfo;

public class TraceAnalyzer {
	private HashMap<String, MethodDef> method_Id_Def_Map = new HashMap<String, MethodDef>();
	
	public HashMap<String, ClassDef> getClass_Id_Def_Map() {
		return class_Id_Def_Map;
	}

	public void setTStartPoints(ArrayList<MethodEntry> startPoints) {
		tStartPoints = startPoints;
	}

	private HashMap<String, ClassDef> class_Id_Def_Map = new HashMap<String, ClassDef>();
	private ArrayList<MethodEntry> methodEntryList = new ArrayList<MethodEntry>();

	private ArrayList<MethodExit> methodExitList = new ArrayList<MethodExit>();
	private HashSet<String> signatureMethodIDSet = new HashSet<String>();
	private ArrayList<MethodEntry> tStartPoints = new ArrayList<MethodEntry>();
	private HashMap<String, String> threadID_Parent = new HashMap<String, String>();
	// Entry points of doGet or doPost
	private int pEntry = 0;
	private HashMap<String, String> fatherThreads = new HashMap<String, String>();

	// private HashSet<String> existingTransactions = new HashSet<String>();
	public TraceAnalyzer() {
	}

	public TraceAnalyzer(String[] xmlFiles) {
		try {
			for (int xmlIndex = 0; xmlIndex < xmlFiles.length; xmlIndex++) {
				DOMParser parser = new DOMParser();
				parser.parse(xmlFiles[xmlIndex]);
				Document doc = parser.getDocument();

				NodeList outputNodes = doc.getElementsByTagName("classDef");
				for (int i = 0; i < outputNodes.getLength(); i++) {
					NamedNodeMap attrs = outputNodes.item(i).getAttributes();
					String threadIdRef = attrs.getNamedItem("threadIdRef")
							.getNodeValue();
					String classId = attrs.getNamedItem("classId")
							.getNodeValue();
					String className = attrs.getNamedItem("name")
							.getNodeValue();
					String objIdRef = attrs.getNamedItem("objIdRef")
							.getNodeValue();
					String time = attrs.getNamedItem("time").getNodeValue();
					ClassDef classDef = new ClassDef(threadIdRef, className,
							classId, objIdRef, time);
					class_Id_Def_Map.put(classId, classDef);
					//System.err.println(new String(className.replace("/", ".")));
				}
				
				System.out.println("## classDefList lists are initialied by ##"
						+ xmlFiles[xmlIndex]);

				outputNodes = doc.getElementsByTagName("methodDef");
				for (int i = 0; i < outputNodes.getLength(); i++) {
					NamedNodeMap attrs = outputNodes.item(i).getAttributes();
					String classIdRef = attrs.getNamedItem("classIdRef")
							.getNodeValue();
					String methodId = attrs.getNamedItem("methodId")
							.getNodeValue();
					String methodName = attrs.getNamedItem("name")
							.getNodeValue();
					MethodDef methodDef = new MethodDef(classIdRef, methodId,
							methodName);
					method_Id_Def_Map.put(methodId, methodDef);

					if (methodName.equals("doGet")
							|| methodName.equals("doPost")) {							
								signatureMethodIDSet.add(methodDef
										.getClassIdRef()
										+ ":" + methodDef.getMethodId());							
					}
				}
				System.out
						.println("## methodDefList lists are initialied by ##"
								+ xmlFiles[xmlIndex]);

				outputNodes = doc.getElementsByTagName("methodEntry");
				for (int i = 0; i < outputNodes.getLength(); i++) {
					NamedNodeMap attrs = outputNodes.item(i).getAttributes();
					String threadIdRef = "";
					String time = "";
					String methodIdRef = "";
					String objIdRef = "";
					String classIdRef = "";
					String ticket = "";

					if (attrs.getNamedItem("threadIdRef") != null)
						threadIdRef = attrs.getNamedItem("threadIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("time") != null)
						time = attrs.getNamedItem("time").getNodeValue();

					if (attrs.getNamedItem("methodIdRef") != null)
						methodIdRef = attrs.getNamedItem("methodIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("objIdRef") != null)
						objIdRef = attrs.getNamedItem("objIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("classIdRef") != null)
						classIdRef = attrs.getNamedItem("classIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("ticket") != null)
						ticket = attrs.getNamedItem("ticket").getNodeValue();

					MethodEntry methodEntry = new MethodEntry(threadIdRef,
							Double.parseDouble(time), methodIdRef, objIdRef, classIdRef, ticket);
					methodEntryList.add(methodEntry);

					if (this.signatureMethodIDSet
							.contains(methodEntry.classIdRef + ":"
									+ methodEntry.methodIdRef)) {
						tStartPoints.add(methodEntry);
					}

				}
				System.out
						.println("## methodEntryList lists are initialied by ##"
								+ xmlFiles[xmlIndex]);

				outputNodes = doc.getElementsByTagName("methodExit");
				for (int i = 0; i < outputNodes.getLength(); i++) {
					NamedNodeMap attrs = outputNodes.item(i).getAttributes();
					String threadIdRef = "";
					String time = "";
					String methodIdRef = "";
					String objIdRef = "";
					String classIdRef = "";
					String overhead = "";
					String ticket = "";

					if (attrs.getNamedItem("threadIdRef") != null)
						threadIdRef = attrs.getNamedItem("threadIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("time") != null)
						time = attrs.getNamedItem("time").getNodeValue();

					if (attrs.getNamedItem("methodIdRef") != null)
						methodIdRef = attrs.getNamedItem("methodIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("objIdRef") != null)
						objIdRef = attrs.getNamedItem("objIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("classIdRef") != null)
						classIdRef = attrs.getNamedItem("classIdRef")
								.getNodeValue();

					if (attrs.getNamedItem("overhead") != null)
						overhead = attrs.getNamedItem("overhead")
								.getNodeValue();

					if (attrs.getNamedItem("ticket") != null)
						ticket = attrs.getNamedItem("ticket").getNodeValue();

					MethodExit methodExit = new MethodExit(threadIdRef, Double.parseDouble(time),
							methodIdRef, objIdRef, classIdRef, overhead, ticket);

					// if(threadIdRef.length()==0||time.length()==0||methodIdRef.length()==0||classIdRef.length()==0||objIdRef.length()==0)
					// System.out.print("an empty attr");

					methodExitList.add(methodExit);
				}
				System.out
						.println("## methodExitList lists are initialied by ##"
								+ xmlFiles[xmlIndex]);

				outputNodes = doc.getElementsByTagName("threadStart");
				for (int i = 0; i < outputNodes.getLength(); i++) {
					NamedNodeMap attrs = outputNodes.item(i).getAttributes();
					String threadId = attrs.getNamedItem("threadId")
							.getNodeValue();
					// String threadName = attrs.getNamedItem("threadName")
					// .getNodeValue();
					// String groupName = attrs.getNamedItem("threadName")
					// .getNodeValue();
					String parentName = attrs.getNamedItem("parentName")
							.getNodeValue();
					// ThreadStart threadStart = new ThreadStart(threadId,
					// threadName, groupName, parentName);
					this.threadID_Parent.put(threadId, parentName);

				}
				System.out.println("## threadStart lists are initialied by ##"
						+ xmlFiles[xmlIndex]);

			}

			System.out
					.println("------------------ All lists are initialied ----------------------");

		} catch (Exception ex) {
			System.out.println(ex);
		}
	}

	public ArrayList<Transaction> identifyTransactions(String outputTxt) {
		ArrayList<Transaction> transactionList = new ArrayList<Transaction>();
		ArrayList<String> transactionSig = new ArrayList<String>();
		int count = 0;

		for (int i = 0; i < this.tStartPoints.size(); i++) {
			Transaction newT = getATransaction(this.tStartPoints.get(i));
			if (transactionSig.contains(newT.getSignature())) {
				Transaction t = transactionList.get(transactionSig.indexOf(newT
						.getSignature()));
				t.setAveResponseTime((t.getAveResponseTime() + newT
						.getAveResponseTime())
						/ (t.getTotal() + 1));
				t.setTotal(t.getTotal() + 1);
				
				for (int j = 0; j < t.getMethodInvoke().size(); j++) {
					MethodInvocation mi = (MethodInvocation) t.getMethodInvoke().get(j)[1];
					mi.setAveResponseTime((mi.getAveResponseTime() + ((MethodInvocation)newT
							.getMethodInvoke().get(j)[1]).getAveResponseTime())
							/ mi.getTotal() + 1);
				}
								
			} else {
				transactionList.add(newT);
				transactionSig.add(newT.getSignature());
			}
			this.pEntry = 0;
			System.out.println("## Transaction " + (i + 1) + "/"
					+ this.tStartPoints.size() + " finished");
			if (((i + 1) % 100) == 0) {
				FileOperators.outputTransactionInText(transactionList,
						outputTxt.replace(".txt", "-" + i + ".txt"), count);
				transactionList.clear();
				count = i + 1;
			}
		}

		FileOperators.outputTransactionInText(transactionList, outputTxt
				.replace(".txt", "-last.txt"), count);

		// Group the same transactions launches by a performance test suite

		return transactionList;
	}

	// getAssociatedMethodSequences start from doGet or doPost
	private Transaction getATransaction(MethodEntry methodEntry) {
		
		Transaction tran = new Transaction();
		StringBuilder sig = new StringBuilder();
		String tranThreadId = methodEntry.threadIdRef;
		double lastExitTime =0;
		
		// Add the start point, i.e., the first method invocation, doGet or doPost
		MethodInvocation mi = new MethodInvocation();
		mi.setClassName(((ClassDef) this.class_Id_Def_Map
				.get(methodEntry.classIdRef)).name);// getClassNameById
		mi.setMethodName(((MethodDef) this.method_Id_Def_Map
				.get(methodEntry.methodIdRef)).getMethodName());// getMethodNameById
		sig = sig.append(((ClassDef) this.class_Id_Def_Map
				.get(methodEntry.classIdRef)).name
				+ ":"
				+ ((MethodDef) this.method_Id_Def_Map
						.get(methodEntry.methodIdRef)).getMethodName() + " ");
		mi.setTotal(1);		

		MethodExit methodExit = findTExit(methodEntry);// find exit for entry
		if (methodExit != null) {
			mi.setAveResponseTime(methodExit.time
					- methodEntry.time);			
			mi.setEndTime(methodExit.time);
		} else {
			System.out.println("No exit node for " + mi.getMethodName() + "@"
					+ methodEntry.time);
			mi.setAveResponseTime(-1);			
		}
		mi.setAveTimeWithoutInvocations(mi.getAveResponseTime());
		
		tran.addMethodCall(mi, methodEntry.time, 0);
				
		tran.setAveResponseTime(mi.getAveResponseTime());
		tran.setThreadId(methodEntry.threadIdRef);
		
		double tExitTime = 0;
		if (methodExit != null) {
			tExitTime = methodExit.time;
		} else {
			tran.setTotal(1);
			tran.setSignature(sig.toString());
			return tran;
		}
		lastExitTime = tExitTime;
			
		MethodEntry nextEntry = findNextEntry(methodEntry,tranThreadId);
		if (nextEntry == null) {
			System.out.println("No next Entry");
		}
		double entryTime = nextEntry.time;
		
		//Add the children of doGet/doPost
		while (entryTime < tExitTime) {
			MethodInvocation mc = new MethodInvocation();
			mc.setClassName(((ClassDef) this.class_Id_Def_Map
					.get(nextEntry.classIdRef)).name);// getClassNameById
			mc.setMethodName(((MethodDef) this.method_Id_Def_Map
					.get(nextEntry.methodIdRef)).getMethodName());// getMethodNameById
			sig = sig.append(((ClassDef) this.class_Id_Def_Map
					.get(methodEntry.classIdRef)).name
					+ ":"
					+ ((MethodDef) this.method_Id_Def_Map
							.get(methodEntry.methodIdRef)).getMethodName()
					+ " ");
			mc.setTotal(1);
			
			MethodExit exit = findExit(nextEntry);
			if (exit != null) {
				mc.setEndTime(exit.time);
				mc.setAveResponseTime(exit.time
						- nextEntry.time);
			} else {
				System.out.println("No exit node for " + mc.getMethodName()
						+ "@" + nextEntry.time);
				mc.setAveResponseTime(-1);
			}
			mc.setAveTimeWithoutInvocations(mc.getAveResponseTime());
			
			tran.addMethodCall(mc, entryTime, lastExitTime);
			lastExitTime = exit.time;
						
			nextEntry = findNextEntry(nextEntry, tranThreadId);
			if (nextEntry != null)
				entryTime = nextEntry.time;
			else
				break;
		}

		tran.setSignature(sig.toString());
		tran.setAveResponseTime(tran.getAveResponseTime());
		tran.setTotal(1);

		return tran;
	}

	private MethodExit findTExit(MethodEntry methodEntry) {
//		String objId = methodEntry.objIdRef;
//		String classId = methodEntry.classIdRef;
//		String methodId = methodEntry.methodIdRef;
//		String threadId = methodEntry.threadIdRef;
//		double time = methodEntry.time;

		for (int i = 0; i < this.methodExitList.size(); i++) {
			MethodExit me = this.methodExitList.get(i);
			if(me.ticket.equals(methodEntry.ticket))
//			if (me.classIdRef.equals(classId)
//					&& me.methodIdRef.equals(methodId)
//					&& me.objIdRef.equals(objId)
//					&& me.threadIdRef.equals(threadId)
//					&& me.time >= time) 
			{
				return me;
			}
		}

		return null;
	}

	
	private MethodEntry findNextEntry(MethodEntry methodEntry, String tranThreadId) {
		// TODO Auto-generated method stub

		double time = methodEntry.time;
		// String threadId = methodEntry.threadIdRef;

		for (int i = this.pEntry; i < this.methodEntryList.size(); i++) {
			MethodEntry me = this.methodEntryList.get(i);
			String threadId = me.threadIdRef;
			//String parent = this.threadID_Parent.get(threadId);

			if ((threadId.equals(tranThreadId))
					&& (me.time >= time)
					&& !(me.ticket.equals(methodEntry.ticket))) {
				this.pEntry = i + 1;
				//this.fatherThreads.put(threadId, parent);
				return me;
			}
		}
		return null;
	}

	private MethodExit findExit(MethodEntry methodEntry) {
		// TODO Auto-generated method stub
		// String objId = methodEntry.objIdRef;
		// String classId = methodEntry.classIdRef;
		// String methodId = methodEntry.methodIdRef;
		// String threadId = methodEntry.threadIdRef;
		double time = methodEntry.time;

		for (int i = 0; i < this.methodExitList.size(); i++) {

			MethodExit me = this.methodExitList.get(i);
			/*
			 * me.classIdRef.equals(classId) && me.methodIdRef.equals(methodId)
			 * && me.objIdRef.equals(objId) && me.threadIdRef.equals(threadId)
			 */
			if (me.ticket.equals(methodEntry.ticket)
					&& me.time >= time)
				return me;
		}
		return null;
	}

	public ArrayList<String> getEntryClassDef(File file) {
		ArrayList<String> methodDef = new ArrayList<String>();
		final String[] signatures = { "Servlet" };

		try {
			DOMParser parser = new DOMParser();
			parser.parse(file.getAbsolutePath());
			Document doc = parser.getDocument();

			NodeList outputNodes = doc.getElementsByTagName("classDef");
			outputNodes = doc.getElementsByTagName("classDef");

			for (int i = 0; i < outputNodes.getLength(); i++) {
				NamedNodeMap attrs = outputNodes.item(i).getAttributes();
				String name = attrs.getNamedItem("name").getNodeValue();
				for (int j = 0; j < signatures.length; j++)
					if (name.contains(signatures[j]))
						methodDef.add(name);
			}
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return methodDef;
	}


	private Transaction getATran_TimeLineBased(MethodEntry methodEntry,
			MethodEntry nextMethod) {
		// this.
		Transaction tran = new Transaction();
		StringBuilder sig = new StringBuilder();
		double lastExitTime =0;
		int level = 0;
		
		// Add the start point, doGet or doPost
		MethodInvocation mi = new MethodInvocation();
		mi.setClassName(((ClassDef) this.class_Id_Def_Map
				.get(methodEntry.classIdRef)).name);// getClassNameById
		mi.setMethodName(((MethodDef) this.method_Id_Def_Map
				.get(methodEntry.methodIdRef)).getMethodName());// getMethodNameById
		sig = sig.append(((ClassDef) this.class_Id_Def_Map
				.get(methodEntry.classIdRef)).name
				+ ":"
				+ ((MethodDef) this.method_Id_Def_Map
						.get(methodEntry.methodIdRef)).getMethodName() + " ");
		mi.setTotal(1);		
		// mi.setStartTime(methodEntry.time);
		
		MethodExit methodExit = findTExit(methodEntry);// find exit for entry
		if (methodExit != null) {
			mi.setAveResponseTime(methodExit.time
					- methodEntry.time);
		} else {
			System.out.println("No exit node for " + mi.getMethodName() + "@"
					+ methodEntry.time);
			mi.setAveResponseTime(-1);
		}

		tran.addMethodCall(mi, methodEntry.time, 0 );
		tran.setAveResponseTime(mi.getAveResponseTime());
		tran.setThreadId(methodEntry.threadIdRef);
		this.fatherThreads.put(methodEntry.threadIdRef, this.threadID_Parent
				.get(methodEntry.threadIdRef));// threadName

		double tEndTime = 0;
		if (nextMethod != null) {
			tEndTime = nextMethod.time;
		} else {
			tEndTime = this.methodExitList
					.get(this.methodExitList.size() - 1).time;
		}
		lastExitTime = tEndTime;
		
		MethodEntry nextEntry = findNextEntryBasedOnFatherThread(methodEntry);
		if (nextEntry == null) {
			System.out.println("No next Entry");
		}
		double entryTime = nextEntry.time;

		while (entryTime < tEndTime) {
			MethodInvocation mc = new MethodInvocation();
			mc.setClassName(((ClassDef) this.class_Id_Def_Map
					.get(nextEntry.classIdRef)).name);// getClassNameById
			mc.setMethodName(((MethodDef) this.method_Id_Def_Map
					.get(nextEntry.methodIdRef)).getMethodName());// getMethodNameById
			sig = sig.append(((ClassDef) this.class_Id_Def_Map
					.get(methodEntry.classIdRef)).name
					+ ":"
					+ ((MethodDef) this.method_Id_Def_Map
							.get(methodEntry.methodIdRef)).getMethodName()
					+ " ");
			mc.setTotal(1);

			MethodExit exit = findExit(nextEntry);
			if (exit != null) {
				mc.setAveResponseTime(exit.time
						- nextEntry.time);
			} else {
				System.out.println("No exit node for " + mc.getMethodName()
						+ "@" + nextEntry.time);
				mc.setAveResponseTime(-1);
			}

			tran.addMethodCall(mc, entryTime, lastExitTime);
			lastExitTime = exit.time;
				
			nextEntry = findNextEntryBasedOnFatherThread(nextEntry);
			if (nextEntry != null)
				entryTime = nextEntry.time;
			else
				break;
		}

		tran.setSignature(sig.toString());
		tran.setAveResponseTime(tran.getAveResponseTime());
		tran.setTotal(1);

		return tran;
	}

	private MethodEntry findNextEntryBasedOnFatherThread(MethodEntry methodEntry) {
		// TODO Auto-generated method stub

		double time = methodEntry.time;
		// String threadId = methodEntry.threadIdRef;

		for (int i = this.pEntry; i < this.methodEntryList.size(); i++) {
			MethodEntry me = this.methodEntryList.get(i);
			String threadId = me.threadIdRef;
			String parent = this.threadID_Parent.get(threadId);

			if ((fatherThreads.containsKey(threadId) || fatherThreads
					.containsValue(parent))
					&& (me.time >= time)
					&& !(me.ticket.equals(methodEntry.ticket))) {
				this.pEntry = i + 1;
				this.fatherThreads.put(threadId, parent);
				return me;
			}
		}
		return null;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		/*String xmlFile1 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-1formated.xml";
		String xmlFile2 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-2formated.xml";
		String xmlFile3 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-3formated.xml";
		String xmlFile4 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-4formated.xml";
		String xmlFile5 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-5formated.xml";
		String xmlFile6 = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\plants.9082.schedule2.doGet-6formated.xml";
		String outputTxt = "C:\\MyWorkspace\\RPT\\TPTP\\Mar20\\Traces\\plants.9082.schedule2.doGet\\splited\\transactions.txt";
		String[] xmlFiles = { xmlFile1, xmlFile2, xmlFile3, xmlFile4, xmlFile5,
				xmlFile6 };
		// FileOperators.formatXML(xmlFile);
		TraceAnalyzer tr = new TraceAnalyzer(xmlFiles);
		// tr.initializeLists(xmlFiles);
		tr.identifyTransactions(outputTxt);
		// FileOperators.outputTransactionInText(trans, outputTxt);
*/
	}
}
